from less0n.models import *
from datetime import datetime


def init_db():
    # Create tables
    print('Creating all database tables...')
    db.create_all()

    # Create data
    print('Dumping all data...')

    # User
    users = {
        'bm2734': User(id='bm2734', avatar='', email='bm2734@columbia.edu', name='Bowen Mao', tokens=''),
        'yg2529': User(id='yg2529', avatar='', email='yg2529@columbia.edu', name='Yiming Guo', tokens=''),
        'yh2961': User(id='yh2961', avatar='', email='yh2961@columbia.edu', name='Yilan He', tokens=''),
        'zj2226': User(id='zj2226', avatar='', email='zj2226@columbia.edu', name='Zhijian Jiang', tokens=''),
        'ky2371': User(id='ky2371', avatar='', email='ky2371@columbia.edu', name='Kaimao Yang', tokens=''),
    }
    for _, user in users.items():
        db.session.add(user)
    db.session.commit()

    # Roles
    role_student = Role(name='student')
    db.session.add(role_student)
    role_instructor = Role(name='instructor')
    db.session.add(role_instructor)
    role_admin = Role(name='admin')
    db.session.add(role_admin)
    db.session.commit()

    # Membership
    memberships = [
        Membership(user=users['bm2734'], role=role_admin),
        Membership(user=users['yg2529'], role=role_admin),
        Membership(user=users['yh2961'], role=role_admin),
        Membership(user=users['zj2226'], role=role_admin),
        Membership(user=users['ky2371'], role=role_student),
        Membership(user=users['bm2734'], role=role_student),
    ]
    for membership in memberships:
        db.session.add(membership)
    db.session.commit()

    # Departments
    depts = {
        'AHAR': Department(id='AHAR', name='Art History and Archaeology',
                           url='http://www.columbia.edu/cu/arthistory/'),
        'COCI': Department(id='COCI', name='Contemporary Civilization and Literature Humanities'),
        'COMS': Department(id='COMS', name='Computer Science',
                           url='https://www.cs.columbia.edu/'),
        'EALC': Department(id='EALC', name='East Asian Languages and Cultures',
                           url='http://ealac.columbia.edu/'),
        'ECON': Department(id='ECON', name='Economics',
                           url='http://econ.columbia.edu/'),
        'ENCL': Department(id='ENCL', name='English and Comparative Literature',
                           url='http://english.columbia.edu/'),
        'HSTB': Department(id='HSTB', name='History @Barnard',
                           url='https://history.barnard.edu/'),
        'HUMC': Department(id='HUMC', name='Humanities (College)'),
        'IEOR': Department(id='IEOR', name='Industrial Engineering and Operations Research',
                           url='http://ieor.columbia.edu/'),
        'MATH': Department(id='MATH', name='Mathematics',
                           url='https://www.math.columbia.edu/'),
        'MUSI': Department(id='MUSI', name='Music',
                           url='https://music.columbia.edu/'),
        'STAT': Department(id='STAT', name='Statistics',
                           url='https://stat.columbia.edu/'),
    }
    for _, dept in depts.items():
        db.session.add(dept)
    db.session.commit()

    # Subjects
    subjs = {
        'AHUM': Subject(id='AHUM', name='Asian Humanities'),
        'ASCE': Subject(id='ASCE', name='Asian Civilization: East Asian'),
        'COCI': Subject(id='COCI', name='Contemporary Civilization'),
        'COMS': Subject(id='COMS', name='Computer Science'),
        'CPLT': Subject(id='CPLT', name='Comparative Literature'),
        'CSEE': Subject(id='CSEE', name='Computer Science and Electrical Engineering'),
        'CSOR': Subject(id='CSOR', name='Computer Science and Operations Research'),
        'ECON': Subject(id='ECON', name='Economics'),
        'ENGL': Subject(id='ENGL', name='English'),
        'HIST': Subject(id='HIST', name='History'),
        'HUMA': Subject(id='HUMA', name='Humanities'),
        'MATH': Subject(id='MATH', name='Mathematics'),
        'STAT': Subject(id='STAT', name='Statistics'),
    }
    for _, subj in subjs.items():
        db.session.add(subj)
    db.session.commit()

    # Courses
    courses = {
        'AHUM2604': Course(id='AHUM2604', subject=subjs['AHUM'], number='2604', department=depts['AHAR'],
                           name='Art In China, Japan, and Korea'),

        'ASCE1359': Course(id='ASCE1359', subject=subjs['ASCE'], number='1359', department=depts['EALC'],
                           name='Introduction to East Asian Civilizations: China'),
        'ASCE1361': Course(id='ASCE1361', subject=subjs['ASCE'], number='1361', department=depts['EALC'],
                           name='Introduction to East Asian Civilizations: Japan'),
        'ASCE1363': Course(id='ASCE1363', subject=subjs['ASCE'], number='1363', department=depts['EALC'],
                           name='Introduction to East Asian Civilizations: Korea'),

        'COCI1101': Course(id='COCI1101', subject=subjs['COCI'], number='1101', department=depts['COCI'],
                           name='Contemporary Western Civilization I'),
        'COCI1102': Course(id='COCI1102', subject=subjs['COCI'], number='1102', department=depts['COCI'],
                           name='Contemporary Western Civilization II'),

        'COMS1004': Course(id='COMS1004', subject=subjs['COMS'], number='1004', department=depts['COMS'],
                           name='Introduction to Computer Science and Programming in Java'),
        'COMS1007': Course(id='COMS1007', subject=subjs['COMS'], number='1007', department=depts['COMS'],
                           name='Honors Introduction to Computer Science'),
        'COMS3134': Course(id='COMS3134', subject=subjs['COMS'], number='3134', department=depts['COMS'],
                           name='Data Structures in Java'),
        'COMS3157': Course(id='COMS3157', subject=subjs['COMS'], number='3157', department=depts['COMS'],
                           name='Advanced Programming'),
        'COMS3203': Course(id='COMS3203', subject=subjs['COMS'], number='3203', department=depts['COMS'],
                           name='Discrete Mathematics: Introduction to Combinatorics and Graph Theory'),
        'COMS3261': Course(id='COMS3261', subject=subjs['COMS'], number='3261', department=depts['COMS'],
                           name='Computer Science Theory'),
        'COMS3998': Course(id='COMS3998', subject=subjs['COMS'], number='3998', department=depts['COMS'],
                           name='Undergraduate Projects in Computer Science'),
        'COMS4111': Course(id='COMS4111', subject=subjs['COMS'], number='4111', department=depts['COMS'],
                           name='Introduction to Databases'),
        'COMS4115': Course(id='COMS4115', subject=subjs['COMS'], number='4115', department=depts['COMS'],
                           name='Programming Languages and Translators'),
        'COMS4118': Course(id='COMS4118', subject=subjs['COMS'], number='4118', department=depts['COMS'],
                           name='Operating Systems I'),
        'COMS4156': Course(id='COMS4156', subject=subjs['COMS'], number='4156', department=depts['COMS'],
                           name='Advanced Software Engineering'),
        'COMS4701': Course(id='COMS4701', subject=subjs['COMS'], number='4701', department=depts['COMS'],
                           name='Artificial Intelligence'),
        'COMS4705': Course(id='COMS4705', subject=subjs['COMS'], number='4705', department=depts['COMS'],
                           name='Natural Language Processing'),
        'COMS4771': Course(id='COMS4771', subject=subjs['COMS'], number='4771', department=depts['COMS'],
                           name='Machine Learning'),
        'COMS6111': Course(id='COMS6111', subject=subjs['COMS'], number='6111', department=depts['COMS'],
                           name='Advanced Database Systems'),
        'COMS6232': Course(id='COMS6232', subject=subjs['COMS'], number='6232', department=depts['COMS'],
                           name='Analysis of Algorithms II'),
        'COMS6901': Course(id='COMS6901', subject=subjs['COMS'], number='6901', department=depts['COMS'],
                           name='Projects in Computer Science'),
        'CSEE3827': Course(id='CSEE3827', subject=subjs['CSEE'], number='3827', department=depts['COMS'],
                           name='Fundamentals of Computer Systems'),
        'CSOR4231': Course(id='CSOR4231', subject=subjs['CSOR'], number='4231', department=depts['COMS'],
                           name='Analysis of Algorithms I'),

        'CPLT3541': Course(id='CPLT3541', subject=subjs['CPLT'], number='3541', department=depts['ENCL'],
                           name='Contemporary Short Story'),

        'ECON1105': Course(id='ECON1105', subject=subjs['ECON'], number='1105', department=depts['ECON'],
                           name='Principles of Economics'),

        'ENGL1010': Course(id='ENGL1010', subject=subjs['ENGL'], number='1010', department=depts['ENCL'],
                           name='University Writing'),
        'ENGL3335': Course(id='ENGL3335', subject=subjs['ENGL'], number='3335', department=depts['ENCL'],
                           name='Shakespeare I'),
        'ENGL3336': Course(id='ENGL3336', subject=subjs['ENGL'], number='3336', department=depts['ENCL'],
                           name='Shakespeare II'),

        'HIST1101': Course(id='HIST1101', subject=subjs['HIST'], number='1101', department=depts['HSTB'],
                           name='European History 1500-1789'),
        'HIST1302': Course(id='HIST1302', subject=subjs['HIST'], number='1302', department=depts['HSTB'],
                           name='European History Since 1789'),

        'HUMA1001': Course(id='HUMA1001', subject=subjs['HUMA'], number='1001', department=depts['HUMC'],
                           name='Masterpieces of Western Literature and Philosophy I'),
        'HUMA1002': Course(id='HUMA1002', subject=subjs['HUMA'], number='1002', department=depts['HUMC'],
                           name='Masterpieces of Western Literature and Philosophy II'),
        'HUMA1121': Course(id='HUMA1121', subject=subjs['HUMA'], number='1121', department=depts['AHAR'],
                           name='Masterpieces of Western Art'),
        'HUMA1123': Course(id='HUMA1123', subject=subjs['HUMA'], number='1123', department=depts['MUSI'],
                           name='Masterpieces of Western Music'),

        'MATH1101': Course(id='MATH1101', subject=subjs['MATH'], number='1101', department=depts['MATH'],
                           name='Calculus I'),
        'MATH1102': Course(id='MATH1102', subject=subjs['MATH'], number='1102', department=depts['MATH'],
                           name='Calculus II'),
        'MATH1201': Course(id='MATH1201', subject=subjs['MATH'], number='1201', department=depts['MATH'],
                           name='Calculus III'),
        'MATH1202': Course(id='MATH1202', subject=subjs['MATH'], number='1202', department=depts['MATH'],
                           name='Calculus IV'),
        'MATH2010': Course(id='MATH2010', subject=subjs['MATH'], number='2010', department=depts['MATH'],
                           name='Linear Algebra'),

        'STAT1001': Course(id='STAT1001', subject=subjs['STAT'], number='1001', department=depts['STAT'],
                           name='Introduction to Statistical Reasoning'),
        'STAT1101': Course(id='STAT1101', subject=subjs['STAT'], number='1101', department=depts['STAT'],
                           name='Introduction to Statistical Reasoning'),
        'STAT1201': Course(id='STAT1201', subject=subjs['STAT'], number='1201', department=depts['STAT'],
                           name='Calculus-Based Introduction to Statistics'),
        'STAT4001': Course(id='STAT4001', subject=subjs['STAT'], number='4001', department=depts['STAT'],
                           name='Introduction to Probability and Statistics'),
    }
    for _, course in courses.items():
        db.session.add(course)
    db.session.commit()

    # Professors
    profs = {
        'cs2035': Professor(uni='cs2035', name='Clifford Seth Stein', department=depts['IEOR'],
                           url='http://www.columbia.edu/~cs2035/'),
        'dar27': Professor(uni='dar27', name='David Aaron Rios', department=depts['STAT'],
                           url='http://stat.columbia.edu/department-directory/name/david-rios/'),
        'db2711': Professor(uni='db2711', name='Daniel Bauer', department=depts['COMS'],
                            url='http://www.cs.columbia.edu/~bauer/',
                            avatar='https://www.cs.columbia.edu/wp-content/uploads/2017/02/cs_profile-225x225.jpg'),
        'etl2115': Professor(uni='etl2115', name='Ewan T. Lowe', department=depts['COMS'],
                             avatar='/static/img/etl2115.jpg', url=''),
        'jwl3': Professor(uni='jwl3', name='Jae Woo Lee', department=depts['COMS'],
                          url='http://www.cs.columbia.edu/~jae/',
                          avatar='http://www.cs.columbia.edu/~jae/images/jae-01.jpg'),
        'lt95': Professor(uni='lt95', name='Lisa S. Tiersten', department=depts['HSTB'],
                          url='https://barnard.edu/profiles/lisa-tiersten',
                          avatar='https://ma.europe.columbia.edu/sites/default/files/styles/cu_crop/public/content/images/Tiersten-Lisa.JPG'),
        'mak2191': Professor(uni='mak2191', name='Martha A. Kim', department=depts['COMS'],
                             url='http://www.cs.columbia.edu/~martha/',
                             avatar='http://engineering.columbia.edu/files/engineering/kim.jpg'),
        'mc3354': Professor(uni='mc3354', name='Michael John Collins', department=depts['COMS'],
                            url='http://www.cs.columbia.edu/~mcollins/',
                            avatar='http://engineering.columbia.edu/files/engineering/Collins.png'),
        'nv2274': Professor(uni='nv2274', name='Nakul Verma', department=depts['COMS'],
                            url='http://www.cs.columbia.edu/~verma/',
                            avatar='http://www.cs.columbia.edu/~verma/nakul_verma.png'),
        'rt2515': Professor(uni='rt2515', name='Richard Morse Townsend', department=depts['COMS'],
                            url='https://www.richardmtownsend.com/',
                            avatar='https://static.wixstatic.com/media/53f993_cc1ad3e6319d46ed929831fd9ffb98c0.jpg/v1/fill/w_414,h_496,al_c,q_80,usm_0.66_1.00_0.01/53f993_cc1ad3e6319d46ed929831fd9ffb98c0.jpg'),
        'tm2118': Professor(uni='tm2118', name='Tal G. Malkin', department=depts['COMS'],
                            url='http://www.cs.columbia.edu/~tal/',
                            avatar='https://industry.datascience.columbia.edu/sites/default/files/profiles/photos/Malkin_web.png'),
    }
    for _, prof in profs.items():
        db.session.add(prof)
    db.session.commit()

    # Teachings
    teachings = [
        Teaching(course=courses['CSEE3827'], professor=profs['mak2191']),
        Teaching(course=courses['COMS3157'], professor=profs['jwl3']),
        Teaching(course=courses['COMS3261'], professor=profs['tm2118']),
        Teaching(course=courses['STAT4001'], professor=profs['dar27']),
        Teaching(course=courses['COMS4701'], professor=profs['db2711']),
        Teaching(course=courses['COMS4705'], professor=profs['mc3354']),
        Teaching(course=courses['COMS4771'], professor=profs['nv2274']),
        Teaching(course=courses['COMS4115'], professor=profs['rt2515']),
        Teaching(course=courses['COMS4156'], professor=profs['etl2115']),
        Teaching(course=courses['HIST1302'], professor=profs['lt95']),
        Teaching(course=courses['COMS4156'], professor=profs['db2711']),
        Teaching(course=courses['COMS4111'], professor=profs['db2711']),
    ]
    for teaching in teachings:
        db.session.add(teaching)
    db.session.commit()

    # Terms
    terms = {
        'Fall 2016': Term(id='Fall 2016'),
        'Spring 2017': Term(id='Spring 2017'),
        'Summer 2017': Term(id='Summer 2017'),
        'Fall 2017': Term(id='Fall 2017'),
        'Spring 2018': Term(id='Spring 2018'),
    }
    for _, term in terms.items():
        db.session.add(term)
    db.session.commit()

    # Tags
    tags = [
        Tag(text="Interesting"),
        Tag(text="Hot"),
        Tag(text="Ace Professor"),
        Tag(text="Fabulous"),
        Tag(text="Great"),
        Tag(text="Heavy"),
        Tag(text="Can learn a lot"),
        Tag(text="Unforgettable"),
        Tag(text="Cool"),
        Tag(text="Average"),
    ]
    for tag in tags:
        db.session.add(tag)
    db.session.commit()

    # Comments
    comments = [
        Comment(teaching=teachings[8], term=terms['Spring 2018'],
                title='Great course',
                content='A very good teacher!',
                rating=5, workload=1, grade='A+', user=users['bm2734'], timestamp=datetime.now(),
                tags=[tags[0], tags[1]]),
        Comment(teaching=teachings[10], term=terms['Spring 2018'],
                title='Super excited',
                content='Excited!',
                rating=4, workload=2, grade='A', user=users['bm2734'], timestamp=datetime.now(),
                tags=[tags[0], tags[2]]),
        Comment(teaching=teachings[10], term=terms['Spring 2018'],
                title='Wow',
                content='Excellent!',
                rating=4, workload=1, grade='A+', user=users['zj2226'], timestamp=datetime.now(),
                tags=[tags[0]]),
        Comment(teaching=teachings[8], term=terms['Spring 2018'],
                title='Learned nothing',
                content='A waste of time and money :(',
                rating=1, workload=2, grade='B-', user=users['zj2226'], timestamp=datetime.now(),
                tags=[]),
        Comment(teaching=teachings[4], term=terms['Spring 2018'],
                title='Super good',
                content='A good teacher!',
                rating=3, workload=3, grade='B+', user=users['bm2734'], timestamp=datetime.now(),
                tags=[tags[0], tags[3], tags[4], tags[8]]),
        Comment(teaching=teachings[5], term=terms['Spring 2018'],
                title='Know a lot',
                content='Really learn a lot',
                rating=4, workload=3, grade='A-', user=users['bm2734'], timestamp=datetime.now(),
                tags=[tags[1], tags[2], tags[4], tags[7]]),
        Comment(teaching=teachings[6], term=terms['Spring 2018'],
                title='Kind of boring',
                content='Also kind of heavy.',
                rating=1, workload=5, grade='C-', user=users['yh2961'], timestamp=datetime.now(),
                tags=[tags[5]]),
        Comment(teaching=teachings[7], term=terms['Spring 2018'],
                title='Will recommend to others',
                content='Best course ever.',
                rating=5, workload=1, grade='A+', user=users['zj2226'], timestamp=datetime.now(),
                tags=[tags[7], tags[8]]),
        Comment(teaching=teachings[0], term=terms['Spring 2018'],
                title='Just so so',
                content='Average',
                rating=2, workload=2, grade='B+', user=users['bm2734'], timestamp=datetime.now(),
                tags=[tags[6], tags[9]]),
        Comment(teaching=teachings[0], term=terms['Spring 2018'],
                title='Good experience',
                content='Average',
                rating=2, workload=2, grade='B+', user=users['zj2226'], timestamp=datetime.now(),
                tags=[tags[0], tags[3], tags[4], tags[5], tags[7], tags[9]]),
        Comment(teaching=teachings[1], term=terms['Spring 2018'],
                title='Too heavy',
                content='The course is good but too heavy',
                rating=4, workload=5, grade='B', user=users['yh2961'], timestamp=datetime.now(),
                tags=[tags[1], tags[5], tags[7], tags[9]]),
        Comment(teaching=teachings[2], term=terms['Spring 2018'],
                title='Very good',
                content='It is good to take this course',
                rating=2, workload=2, grade='B+', user=users['bm2734'], timestamp=datetime.now(),
                tags=[tags[2], tags[3], tags[4], tags[9]]),
    ]
    for comment in comments:
        db.session.add(comment)
    db.session.commit()

    # Adding instructor requests
    add_prof_requests = [
        AddProfRequest(user=users['zj2226'], name='Alpha Beta', department=depts['COMS'],
                       course=courses['COMS4115'], term=terms['Fall 2017'], approved=ApprovalType.PENDING),
        AddProfRequest(user=users['bm2734'], name='James McInerney', department=depts['COMS'],
                       course=courses['COMS4771'], term=terms['Fall 2017'], approved=ApprovalType.PENDING),
        AddProfRequest(user=users['bm2734'], name='Alfred V. Aho', department=depts['COMS'],
                       course=courses['COMS3261'], term=terms['Fall 2016'], approved=ApprovalType.PENDING),
        AddProfRequest(user=users['bm2734'], name='Xi Chen', department=depts['COMS'],
                       course=courses['COMS3261'], term=terms['Fall 2016'], approved=ApprovalType.PENDING),
    ]
    for add_prof_request in add_prof_requests:
        db.session.add(add_prof_request)
    db.session.commit()

    # Adding course requests
    add_course_requests = [
        AddCourseRequest(user=users['bm2734'], course_id='COMS3137', course_number='3137',
                         course_name='Honors Data Structures and Algorithms',
                         department=depts['COMS'], subject=subjs['COMS'],
                         term=terms['Fall 2017'], approved=ApprovalType.PENDING),
        AddCourseRequest(user=users['bm2734'], course_id='COMS4172', course_number='4172',
                         course_name='3D User Interfaces and Augmented Reality',
                         department=depts['COMS'], subject=subjs['COMS'],
                         term=terms['Spring 2018'], approved=ApprovalType.PENDING),
        AddCourseRequest(user=users['bm2734'], course_id='COMS4170', course_number='4170',
                         course_name='User Interface Design',
                         department=depts['COMS'], subject=subjs['COMS'],
                         term=terms['Spring 2018'], approved=ApprovalType.PENDING),
        AddCourseRequest(user=users['bm2734'], course_id='MATH3020', course_number='3020',
                         course_name='Number Theory and Cryptography',
                         department=depts['MATH'], subject=subjs['MATH'],
                         term=terms['Spring 2018'], approved=ApprovalType.PENDING),
        AddCourseRequest(user=users['bm2734'], course_id='COMS4444', course_number='4444',
                         course_name='Programming and Problem Solving',
                         department=depts['COMS'], subject=subjs['COMS'],
                         term=terms['Fall 2017'], approved=ApprovalType.PENDING),
    ]
    for add_course_request in add_course_requests:
        db.session.add(add_course_request)
    db.session.commit()

    print('Done!')


def drop_db():
    db.session.remove()
    db.drop_all()


if __name__ == '__main__':
    drop_db()
    init_db()
