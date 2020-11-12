import sys
import os.path
import unittest
from unittest import mock
import re
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), os.path.pardir)))
from less0n import app, database
from less0n.models import *
from config import Auth


class MainTest(unittest.TestCase):
    """This class uses the Flask tests app to run an integration test against a
    local instance of the server."""

    def setUp(self):
        self.app = app.test_client()
        database.drop_db()  # drop previous db
        database.init_db()

    def tearDown(self):
        database.drop_db()
        database.init_db()

    def testIndex(self):
        rv = self.app.get('/')
        data = rv.data.decode("utf-8")
        assert("Welcome to Less0n" in data)

    def testLogin(self):
        rv = self.app.get('/login')
        assert(rv._status_code == 302)
        assert(Auth.AUTH_URI in rv.location)

    @mock.patch('flask_login.utils._get_user')
    def testLogout(self, current_user):
        """
        test /logout
        """
        current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
        rv = self.app.get('/logout')
        assert rv.status_code == 302

    # Test /department/

    def testDepartment(self):
        """
        Test if department() with GET return rendered department.html
        """
        rv = self.app.get('/department')
        assert rv._status_code == 200
        assert rv.content_type == 'text/html; charset=utf-8'
        assert 'department' in rv.data.decode('utf-8').lower()  # "department.html" must have Department

    def testDepartmentSearchWithValidInput(self):
        """
        Test if department() with POST return department.html with correct department name

        Test cases:
        --------------------------------------------------
        dept_keyword Input                Expected Output
        computer                          computer in html
        """
        rv = self.app.post('/department', data=dict(
            dept_keyword="computer"
        ))
        assert rv._status_code == 200
        assert rv.content_type == 'text/html; charset=utf-8'
        data = rv.data.decode('utf-8').lower()  # convert data to lower case
        assert 'department' in data
        # assert 'computer' in data

    def testDepartmentSearchWithInvalidInput(self):
        """
        Test if department() with POST return department.html with correct department name

        Test cases:
        --------------------------------------------------
        dept_keyword Input                Expected Output
        zhongwenxi                        No word between <section id="sort-alphabetical" data-filter-group="dept"> and </section>
        """
        rv = self.app.post('/department', data=dict(
            dept_keyword="zhongwenxi"
        ))
        assert rv._status_code == 200
        assert rv.content_type == 'text/html; charset=utf-8'
        data = rv.data.decode('utf-8').lower()  # convert data to lower case
        assert 'department' in data

        # search <div class="row" id="alphabetical-card"> \n \n .. </div>
        pattern = re.compile('<div class="row" id="alphabetical-card">(\s*\n\s*)+</div>')
        assert pattern.search(data) is not None

    def testDepartmentSearchWithEmptyInput(self):
        """
        Test if department() with POST return department.html with correct department name

        Test cases:
        --------------------------------------------------
        dept_keyword Input                Expected Output
        empty string                      'computer science' in department.html
        """
        rv = self.app.post('/department', data=dict(
            dept_keyword=""
        ))
        assert rv._status_code == 200
        assert rv.content_type == 'text/html; charset=utf-8'
        data = rv.data.decode('utf-8').lower()  # convert data to lower case
        assert 'department' in data
        assert 'computer science' in data

    # Test /dept/DEPT/

    def testDepartmentCourseWithValidArg(self):
        """
        Test if department_course() return department-course.html with valid department name
        Test cases:
        --------------------------------------------------
        Input                             Expected Output
        /dept/COMS/                       COMS in html
        """
        test_cases = ('/dept/COMS/', '/dept/EALC/')
        for test_case in test_cases:
            rv = self.app.get(test_case)
            assert rv.status_code == 200
            assert rv.content_type == 'text/html; charset=utf-8'
            data = rv.data.decode('utf-8').lower()  # convert data to lower case
            assert 'department-course' in data
            assert test_case[6: 10].lower() in data

    def testDepartmentCourseWithInvalidArg(self):
        """
        Test if department_course() return department-course.html with unvalid department name
        Test cases:
        --------------------------------------------------
        Input                             Expected Output
        /dept/AAAA/                       302, department in html
        """
        rv = self.app.get('/dept/AAAA/')
        assert rv._status_code == 302
        assert rv.content_type == 'text/html; charset=utf-8'
        data = rv.data.decode('utf-8').lower()  # convert data to lower case
        assert 'department' in data

    # Test /course/COURSE/

    def testCourseWithValidArg(self):
        """
        Test if course() return course-detail.html with valid argument
        Test case:
        --------------------------------------------------
        Input                             Expected Output
        /course/COMS3157/                       department in html
        """
        rv = self.app.get('/course/COMS3157/')
        assert rv._status_code == 200
        assert rv.content_type == 'text/html; charset=utf-8'
        data = rv.data.decode('utf-8').lower()  # convert data to lower case
        assert 'course-detail' in data
        assert 'coms3157' in data

    def testCourseWithInvalidArg(self):
        """
        Test if course() return course-detail.html with valid argument
        Test case:
        --------------------------------------------------
        Input                             Expected Output
        /course/COMS2222/                 department in html
        /course/ABCDEFG/                  404
        """
        rv = self.app.get('/course/COMS2222/')
        assert rv._status_code == 302
        assert rv.content_type == 'text/html; charset=utf-8'
        data = rv.data.decode('utf-8').lower()  # convert data to lower case
        assert 'department' in data

        rv = self.app.get('/course/ABCDEFG/')
        assert rv._status_code == 404

    # Test /course/COURSE/json

    def testCourseJsonWithValidArg(self):
        """
        Test if course() return course-detail.html with valid argument
        Test case:
        --------------------------------------------------
        Input                             Expected Output
        /course/COMS3157/json             JSON data
        """
        for test_case in '/course/COMS3157/json/', '/course/COMS4156/json/':
            rv = self.app.get(test_case)
            assert rv._status_code == 200
            assert rv.content_type == 'application/json'

    def testCourseJsonWithInvalidArg(self):
        """
        Test if course() return course-detail.html with valid argument
        Test case:
        --------------------------------------------------
        Input                             Expected Output
        /course/COMS1234/json             404
        /course/ABCDEFG/json              404
        """
        rv = self.app.get('/course/COMS1234/json/')
        assert rv._status_code == 404

        rv = self.app.get('/course/ABCDEFG/json/')
        assert rv._status_code == 404

    def testSearchWithValidArg(self):
        """
        Test if search() return search-result.html with valid arguments
        Test case:
        --------------------------------------------------
        Input                             Expected Output
        /search/?dept=COMS,ECON&subj=COMS,
        CSEE&prof=daniel&course=COMS      coms, econ, electrical, daniel, asce in search-result.html
        """
        rv = self.app.get('/search/?dept=COMS,ECON&subj=COMS+CSEE&prof=daniel&course=ASCE')
        assert rv._status_code == 200
        assert rv.content_type == 'text/html; charset=utf-8'
        data = rv.data.decode('utf-8').lower()  # convert data to lower case
        assert 'coms' in data
        assert 'econ' in data
        assert 'electrical engineering' in data
        # assert 'daniel' in data
        assert 'asce' in data

    def testSearchWithInvalidArg(self):
        """
        Test if search() return search-result.html with valid arguments
        Test case:
        --------------------------------------------------
        Input                             Expected Output
        /search/?dept=zhongwen            empty in the dept/course/prof/subject part of search-result.html
        """
        rv = self.app.get('/search/?dept=zhongwen')
        assert rv._status_code == 200
        assert rv.content_type == 'text/html; charset=utf-8'
        data = rv.data.decode('utf-8').lower()  # convert data to lower case

        # verify if the content part is empty
        assert re.search(r'<div class="row" id="department-card">(\n\s+)+</div>', data) != None
        assert re.search(r'<div class="row" id="subject-card">(\n\s+)+</div>', data) != None
        assert re.search(r'<div class="row" id="professor-card">(\n\s+)+</div>', data) != None
        assert re.search(r'<div class="row" id="course-card">(\n\s+)+</div>', data) != None

    def testSearchWithEmptyArg(self):
        """
        Test if search() return search-result.html with valid arguments
        Test case:
        --------------------------------------------------
        Input                             Expected Output
        /search/                          empty in the dept/course/prof/subject part of search-result.html
        """
        rv = self.app.get('/search/?dept=zhongwen')
        assert rv._status_code == 200
        assert rv.content_type == 'text/html; charset=utf-8'
        data = rv.data.decode('utf-8').lower()  # convert data to lower case

        # verify if the content part is empty
        assert re.search(r'<div class="row" id="department-card">(\n\s+)+</div>', data) != None
        assert re.search(r'<div class="row" id="subject-card">(\n\s+)+</div>', data) != None
        assert re.search(r'<div class="row" id="professor-card">(\n\s+)+</div>', data) != None
        assert re.search(r'<div class="row" id="course-card">(\n\s+)+</div>', data) != None

    @mock.patch('flask_login.utils._get_user')
    def testAddProfToRequestDbWithValidArg(self, current_user):
        """
        Test if add_new_prof() return
        Test case:
        --------------------------------------------------
        Input                                              Expected Output
        {
        'name': 'Clifford Stein',
        'department': 'COMS',
        'semester': 'Fall',
        'year': '2016'
        }
        """
        test_cases = (
            {'name': 'Clifford Stein', 'department': 'COMS', 'course': 'COMS4701', 'semester': 'Fall', 'year': '2016'},
        )

        current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
        for test_case in test_cases:
            rv = self.app.post('/prof/new/', data=dict(
                name=test_case['name'],
                department=test_case['department'],
                course=test_case['course'],
                semester=test_case['semester'],
                year=test_case['year'],
            ))
            assert rv._status_code == 302
            assert rv.content_type == 'text/html; charset=utf-8'
            profs = AddProfRequest.query.filter_by(name=test_case['name'],
                                                   department_id=test_case['department'], course_id=test_case['course'],
                                                   term_id=test_case['semester'] + ' ' + test_case['year']).all()
            assert profs is not None
            assert len(profs) > 0

            # delete records
            for prof in profs:
                db.session.delete(prof)
            db.session.commit()

    @mock.patch('flask_login.utils._get_user')
    def testAddProfToRequestDbWithInvalidArg(self, current_user):
        """
        Test if add_new_prof() return
        Test case:
        --------------------------------------------------
        Input                                              Expected Output
        {
        'name': 'Clifford Stein',
        'department_id': 'COMS'
        }
        """
        test_cases = (
            {'name': 'Clifford Stein', 'department': 'COMS', 'course': '', 'semester': ''},
            {'name': 'Clifford Stein', 'department': 'COMS'},
            {'name': 'Alpha Beta', 'department': 'AAAA', 'course': 'No Course', 'semester': 'Fall 2017', 'year': 2017}
        )

        current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
        for test_case in test_cases:
            rv = self.app.post('/prof/new/', data=test_case)
            assert rv.status == '404 NOT FOUND' or rv.status_code == 302

    @mock.patch('flask_login.utils._get_user')
    def testApproveNewProfWithValidArg(self, current_user):
        """
        Test if admin_approve_prof_request() return
        Test case:
        --------------------------------------------------
        Input                                              Expected Output
        {                                                   200 OK
        'id': 1
        'uni': 'ab1234',
        'name': 'Alpha Beta',
        'department_id': 'COMS',
        'term_id': 'Fall 2017',
        'avatar': '',
        'url': '',
        'approved': 'Approved'
        }
        """
        test_cases = (
            {
                'id': 1,
                'uni': 'ab1234',
                'name': 'Alpha Beta',
                'department': 'COMS',
                'semester': 'Fall 2017',
                'avatar': 'avatar',
                'url': 'url',
                'decision': True,
                'course': 'COMS4115'
            },
            {
                'id': 1,
                'uni': 'ab1234',
                'name': 'Alpha Beta',
                'department': 'COMS',
                'semester': 'Fall 2017',
                'avatar': '',
                'url': '',
                'decision': False,
                'course': 'COMS4115'
            },
            {
                'id': -1,
                'uni': 'ab1234',
                'name': 'Alpha Beta',
                'department': 'COMS',
                'semester': 'Fall 2017',
                'avatar': '',
                'url': '',
                'decision': True,
                'course': 'COMS4115'
            }
        )

        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
            rv = self.app.post('/admin/prof', data=dict(
                request_id=test_case['id'],
                uni=test_case['uni'],
                name=test_case['name'],
                department=test_case['department'],
                semester=test_case['semester'],
                avatar=test_case['avatar'],
                url=test_case['url'],
                course=test_case['course'],
                decision=test_case['decision']
            ))
            assert rv._status_code == 200
            assert 'success' in rv.data.decode('utf-8').lower()
            assert rv.content_type == 'application/json'

            if test_case['decision']:
                prof = Professor.query.filter_by(uni=test_case['uni']).first()
                assert prof is not None

                teachings = Teaching.query.filter_by(course_id=test_case['course'],
                                                     professor_uni=test_case['uni']).all()

                assert teachings is not None
                assert len(teachings) > 0

                # check request
                if not test_case['id'] == -1:
                    add_prof_request = AddProfRequest.query.filter_by(id=test_case['id']).first()
                    assert add_prof_request is not None
                    assert add_prof_request.approved == ApprovalType.APPROVED

                # delete records
                db.session.delete(prof)

                for teaching in teachings:
                    db.session.delete(teaching)

                db.session.commit()

            elif not test_case['decision']:
                # check request
                add_prof_request = AddProfRequest.query.filter_by(id=test_case['id']).first()
                assert add_prof_request is not None
                assert add_prof_request.approved == ApprovalType.DECLINED

    @mock.patch('flask_login.utils._get_user')
    def testApproveNewProfWithInvalidArg(self, current_user):
        """
        Test if admin_approve_prof_request() return
        Test case:
        --------------------------------------------------
        Input                                              Expected Output
        """
        test_cases = (
            {
                'id': 1,
                'user_id': 'zj2226',
                'uni': 'ab1234',
                'name': 'Alpha Beta',
                'department_id': 'AAAA',
                'term_id': 'Fall 2017',
                'avatar': '',
                'url': '',
                'approved': True,
                'course_id': 'COMS4115'
            },
            {
                'id': 1,
                'user_id': 'zj2226',
                'uni': 'ab1234',
                'name': 'Alpha Beta',
                'department_id': 'COMS',
                'term_id': '',
                'avatar': '',
                'url': '',
                'approved': True,
                'course_id': 'COMS4115'
            }
        )

        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
            rv = self.app.post('/admin/prof', data=dict(
                request_id=test_case['id'],
                uni=test_case['uni'],
                name=test_case['name'],
                department=test_case['department_id'],
                semester=test_case['term_id'],
                avatar=test_case['avatar'],
                url=test_case['url'],
                course=test_case['course_id'],
                decision=test_case['approved']
            ))
            assert rv._status_code == 200
            assert 'failure' in rv.data.decode('utf-8').lower()
            assert rv.content_type == 'application/json'


    @mock.patch('flask_login.utils._get_user')
    def testApproveNewCourseWithValidArg(self, current_user):
        """
        Test if admin_approve_prof_request() return
        Test case:
        --------------------------------------------------
        Input                                              Expected Output
        {                                                   200 OK
        'id': 1,
        'subject': 'COMS',
        'course_num': '3137',
        'course_name': 'Honors Data Structures and Algorithms',
        'department': 'COMS',
        'semester': 'Fall 2017',
        'decision': True
        }
        """
        test_cases = (
            {
                'id': 1,
                'subject': 'COMS',
                'course_num': '3137',
                'course_name': 'Honors Data Structures and Algorithms',
                'department': 'COMS',
                'semester': 'Fall 2017',
                'decision': True
            },
        )

        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
            rv = self.app.post('/admin/course', data=dict(
                request_id=test_case['id'],
                subject=test_case['subject'],
                course_num=test_case['course_num'],
                course_name=test_case['course_name'],
                department=test_case['department'],
                semester=test_case['semester'],
                decision=test_case['decision']
            ))
            assert rv._status_code == 200
            assert 'success' in rv.data.decode('utf-8').lower()
            assert rv.content_type == 'application/json'

            if test_case['decision']:
                course = Course.query.filter_by(id=(test_case['subject'] + test_case['course_num'])).first()
                assert course is not None

                # check request
                if not test_case['id'] == -1:
                    add_course_request = AddCourseRequest.query.filter_by(id=test_case['id']).first()
                    assert add_course_request is not None
                    assert add_course_request.approved == ApprovalType.APPROVED

                # delete records
                db.session.delete(course)

                db.session.commit()

            elif not test_case['decision']:
                # check request
                add_prof_request = AddProfRequest.query.filter_by(id=test_case['id']).first()
                assert add_prof_request is not None
                assert add_prof_request.approved == ApprovalType.DECLINED

    @mock.patch('flask_login.utils._get_user')
    def testApproveNewCourseWithInvalidArg(self, current_user):
        """
        Test if admin_approve_prof_request() return
        Test case:
        --------------------------------------------------
        Input                                              Expected Output
        {                                                   404
        'id': 1,
        'subject': 'COMS',
        'course_num': '3137',
        'course_name': 'Honors Data Structures and Algorithms',
        'department': '',
        'semester': 'Fall 2017',
        'decision': True
        }
        """
        test_cases = (
            {
                'request_id': 1,
                'subject': 'COMS',
                'course_num': '3137',
                'course_name': 'Honors Data Structures and Algorithms',
                'department': '',
                'decision': True
            },
            {
                'request_id': 1,
                'subject': 'AAAA',
                'course_num': '3137',
                'course_name': 'Honors Data Structures and Algorithms',
                'department': 'COMS',
                'decision': True
            },
        )

        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
            rv = self.app.post('/admin/course', data=test_case)
            assert 'failure' in rv.data.decode('utf-8').lower()
            assert rv.content_type == 'application/json'

    @mock.patch('flask_login.utils._get_user')
    def testApproveNewCourseWithInvalidUser(self, current_user):
        current_user.return_value = User.query.filter_by(id='ky2371').first()
        rv = self.app.get('/admin/course')
        assert not rv.status_code == 200

    def testProfWithValidArg(self):
        """
        test /prof/<regex("[A-Za-z]{2,3}[0-9]{1,4}"):prof_arg>/
        :return:
        """
        test_cases = ('/prof/etl2115/', '/prof/rt2515/', '/prof/db2711/', '/prof/cs2035/')
        for test_case in test_cases:
            rv = self.app.get(test_case)
            assert rv.status_code == 200
            if test_case == '/prof/etl2115/':
                assert 'Ewan' in rv.data.decode('utf-8')

    def testProfWithInvalidArg(self):
        """
        test /prof/<regex("[A-Za-z]{2,3}[0-9]{1,4}"):prof_arg>/
        :return:
        """
        test_cases = ('/prof/ss1111/', )
        for test_case in test_cases:
            rv = self.app.get(test_case)
            assert rv.status_code == 404

    @mock.patch('flask_login.utils._get_user')
    def testApproveNewProfWithInvalidUser(self, current_user):
        current_user.return_value = User.query.filter_by(id='ky2371').first()
        rv = self.app.get('/admin/prof')
        assert not rv.status_code == 200

    @mock.patch('flask_login.utils._get_user')
    def testCommentWithValidArg(self, current_user):
        """
        test /comment/
        """
        test_cases = (
            {'prof': 'etl2115', 'course': 'COMS4156', 'semester': 'Spring', 'year': '2018', 'title': 'Title', 'content': 'Content',
             'rating': 5, 'workload': 5, 'grade': 'A+', 'tags_str': 'tag1,tag2'},
        )
        for test_case in test_cases:
            for user_uni in ('zj2226', 'yg2529'):
                current_user.return_value = User.query.filter_by(id=user_uni).first()  # Mocking current_user
                rv = self.app.post('/comment/', data=dict(
                    prof=test_case['prof'],
                    course=test_case['course'],
                    semester=test_case['semester'],
                    year=test_case['year'],
                    title=test_case['title'],
                    content=test_case['content'],
                    rating=test_case['rating'],
                    workload=test_case['workload'],
                    grade=test_case['grade'],
                    tags=test_case['tags_str']
                ))
                # clean records
                teaching = Teaching.query.filter_by(course_id=test_case['course'].upper(),
                                                    professor_uni=test_case['prof'].lower()).first()
                assert teaching is not None
                term = Term.query.filter_by(id=test_case['semester'] + ' ' + test_case['year']).first()
                assert term is not None
                comment = Comment.query.filter_by(user_id = user_uni, term_id=test_case['semester'] + ' ' + test_case['year'],
                                                  teaching_id=teaching.id).first()
                assert comment is not None

    @mock.patch('flask_login.utils._get_user')
    def testCommentWithInvalidArg(self, current_user):
        """
        test /comment/
        """
        test_cases = (
            ({'prof': 'etl2115', 'course': 'COMS4111', 'semester': 'Spring', 'year': '2018', 'title': 'Title',
              'content': 'Content', 'rating': 5, 'workload': 5, 'grade': 'A+', 'tags': 'tag1,tag2'}, 500),
            ({'prof': 'etl2115', 'course': 'COMS4156', 'semester': 'Spring', 'year': '2018', 'title': 'Title',
              'content': 'Content', 'rating': 100, 'workload': 5, 'grade': 'A+', 'tags': 'tag1,tag2'}, 302),
        )
        for test_case in test_cases:
            data, status_code = test_case
            current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
            rv = self.app.post('/comment/', data=data)
            assert rv.status_code == status_code

    @mock.patch('flask_login.utils._get_user')
    def testAdminListCourseRequest(self, current_user):
        test_cases = ('/admin/course?approved=1&pending=1&declined=1',)
        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
            rv = self.app.get(test_case)
            assert rv.status_code == 200
            assert rv.data is not None
            data = rv.data.decode('utf-8')
            assert 'approved' in data

    @mock.patch('flask_login.utils._get_user')
    def testAdminListProfRequest(self, current_user):
        test_cases = ('/admin/prof?approved=1&pending=1&declined=1',)
        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
            rv = self.app.get(test_case)
            assert rv.status_code == 200
            assert rv.data is not None
            data = rv.data.decode('utf-8')
            assert 'approved' in data

    @mock.patch('flask_login.utils._get_user')
    def testAddNewCourseGet(self, current_user):
        """
        test /course/new/ get
        """
        current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
        rv = self.app.get('/course/new/')
        assert rv.status_code == 200
        assert rv.content_type == 'text/html; charset=utf-8'
        assert 'Less0n - Add Course' in rv.data.decode('utf-8')

    @mock.patch('flask_login.utils._get_user')
    def testAddNewCoursePostWithValidArgs(self, current_user):
        test_cases = (
            {'course-name': "Test course",
             'course-number': '1111',
             'department': 'COMS',
             'subject': 'COMS',
             'semester': 'Fall',
             'year': '2017'},
        )
        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
            rv = self.app.post('/course/new/', data=test_case)
            assert rv is not None

    @mock.patch('flask_login.utils._get_user')
    def testAddNewCoursePostWithUnValidArgs(self, current_user):
        test_cases = (
            ({
                'course-name': "Test course",
                 'course-number': '1111',
                 'department': 'COMS',
                 'subject': 'COMS',
                 'semester': 'Fall'
            }, 404),
            ({
                'course-name': "Test course",
                'course-number': '1111',
                'department': 'AAAA',
                'subject': 'COMS',
                'semester': 'Fall',
                'year': '2017'
            }, 302)
        )

        for test_case in test_cases:
            data, status_code = test_case
            current_user.return_value = User.query.filter_by(id='zj2226').first()  # Mocking current_user
            rv = self.app.post('/course/new/', data=data)
            assert rv.status_code == status_code

    @mock.patch('flask_login.utils._get_user')
    def testAdminWithValidArg(self, current_user):
        """
        test /admin/
        """
        for user in ('zj2226', ):
            current_user.return_value = User.query.filter_by(id=user).first()
            rv = self.app.get('/admin/')
            assert rv.status_code == 200
            assert 'admin' in rv.data.decode('utf-8')

    @mock.patch('flask_login.utils._get_user')
    def testAdminWithInvalidArg(self, current_user):
        """
        test /admin/
        """
        for user in ('ky2371', ):
            current_user.return_value = User.query.filter_by(id=user).first()
            rv = self.app.get('/admin/')
            assert not rv.status_code == 200

    @mock.patch('flask_login.utils._get_user')
    def testStudentWithValidArg(self, current_user):
        """
        test /admin/
        """
        for user in ('ky2371', ):
            current_user.return_value = User.query.filter_by(id=user).first()
            rv = self.app.get('/student/')
            assert rv.status_code == 200
            assert 'student' in rv.data.decode('utf-8')

    @mock.patch('flask_login.utils._get_user')
    def testStudentWithInvalidArg(self, current_user):
        """
        test /student/
        """
        for user in ('zj2226', ):
            current_user.return_value = User.query.filter_by(id=user).first()
            rv = self.app.get('/student/')
            assert not rv.status_code == 200


    @mock.patch('flask_login.utils._get_user')
    def testStudentListProfRequestWithValidUser(self, current_user):
        """
        test /student/prof
        """
        for user in ('bm2734',):
            current_user.return_value = User.query.filter_by(id=user).first()
            rv = self.app.get('/student/prof?approved=1&pending=1&declined=1')
            assert rv.status_code == 200

    @mock.patch('flask_login.utils._get_user')
    def testStudentListProfRequestWittInvalidUser(self, current_user):
        """
        test /student/prof
        """
        for user in ('zj2226',):
            current_user.return_value = User.query.filter_by(id=user).first()
            rv = self.app.get('/student/prof?approved=1&pending=1&declined=1')
            assert rv.status_code == 401

    @mock.patch('flask_login.utils._get_user')
    def testStudentListCommentWithValidUser(self, current_user):
        """
        test /student/comment/
        """
        current_user.return_value = User.query.filter_by(id='bm2734').first()
        rv = self.app.get('/student/comment/')
        assert rv.status_code == 200

    @mock.patch('flask_login.utils._get_user')
    def testStudentListCommentWithInvalidUser(self, current_user):
        """
        test /student/comment/
        """
        current_user.return_value = User.query.filter_by(id='yh2961').first()
        rv = self.app.get('/student/comment/')
        assert rv.status_code == 401

    @mock.patch('flask_login.utils._get_user')
    def testStudentUpdateCommentWithValidArg(self, current_user):
        """
        test /student/comment/update
        """
        test_cases = (
            {'comment-id': 1, 'semester': 'Spring', 'year': 2018, 'message': 'Message',
             'rating': 4, 'workload': 4, 'grade': 'A', 'tags': 'str1,str2'},
        )
        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='bm2734').first()
            rv = self.app.post('/student/comment/update/', data=test_case)
            assert rv.status_code == 302

    @mock.patch('flask_login.utils._get_user')
    def testStudentUpdateCommentWithInvalidArg(self, current_user):
        """
        test /student/comment/update
        """
        test_cases = (
            {'comment-id': 1, 'semester': 'Spring', 'year': 2018, 'message': 'Message',
             'rating': 10, 'workload': 4, 'grade': 'A', 'tags': 'str1,str2'},
            {'comment-id': -1, 'semester': 'Spring', 'year': 2018, 'message': 'Message',
             'rating': 4, 'workload': 4, 'grade': 'A', 'tags': 'str1,str2'},
        )
        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='bm2734').first()
            rv = self.app.post('/student/comment/update/', data=test_case)
            assert not rv.status_code == 200

    @mock.patch('flask_login.utils._get_user')
    def testStudentDeleteCommentWithValidArg(self, current_user):
        """
        test /student/comment/delete
        """
        test_cases = (
            {'comment-id': 1},
        )
        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='bm2734').first()
            rv = self.app.post('/student/comment/delete/', data=test_case)
            assert rv.status_code == 302

    @mock.patch('flask_login.utils._get_user')
    def testStudentDeleteCommentWithInvalidArg(self, current_user):
        """
        test /student/comment/delete
        """
        test_cases = (
            {'comment-id': -1},
        )
        for test_case in test_cases:
            current_user.return_value = User.query.filter_by(id='bm2734').first()
            rv = self.app.post('/student/comment/delete/', data=test_case)
            assert rv.status_code == 302

    @mock.patch('flask_login.utils._get_user')
    def testStudentListCourseRequestWithValidUser(self, current_user):
        """
        test /student/course
        """
        for user in ('bm2734',):
            current_user.return_value = User.query.filter_by(id=user).first()
            rv = self.app.get('/student/course?approved=1&pending=1&declined=1')
            assert rv.status_code == 200

    @mock.patch('flask_login.utils._get_user')
    def testStudentListCourseRequestWittInvalidUser(self, current_user):
        """
        test /student/course
        """
        for user in ('zj2226',):
            current_user.return_value = User.query.filter_by(id=user).first()
            rv = self.app.get('/student/course?approved=1&pending=1&declined=1')
            assert rv.status_code == 401

if __name__ == '__main__':
    unittest.main()
