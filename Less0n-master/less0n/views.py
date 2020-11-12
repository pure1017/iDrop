# Reference:
# - http://bitwiser.in/2015/09/09/add-google-login-in-flask.html
# - https://stackoverflow.com/questions/34235590/how-do-you-restrict-google-login-oauth2-to-emails-from-a-specific-google-apps

from less0n import app, helpers, db
from less0n.models import *
import json
import logging
import re
from collections import defaultdict
from flask import url_for, redirect, render_template, session, request, flash, jsonify, abort
from flask_login import login_required, login_user, logout_user, current_user
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.sql.expression import ClauseElement
from requests_oauthlib import OAuth2Session
from requests.exceptions import HTTPError
from werkzeug.routing import BaseConverter
from config import Auth


class RegexConverter(BaseConverter):
    def __init__(self, url_map, *items):
        super(RegexConverter, self).__init__(url_map)
        self.regex = items[0]


app.url_map.converters['regex'] = RegexConverter


def get_google_auth(state=None, token=None):
    if token:
        return OAuth2Session(Auth.CLIENT_ID, token=token)
    if state:
        return OAuth2Session(Auth.CLIENT_ID, state=state, redirect_uri=app.config['GOOGLE_OAUTH_REDIRECT_URI'])
    oauth = OAuth2Session(Auth.CLIENT_ID, redirect_uri=app.config['GOOGLE_OAUTH_REDIRECT_URI'], scope=Auth.SCOPE)
    return oauth


def has_membership(user_id, role):
    membership = Membership.query.filter_by(user_id=user_id, role=role).first()
    return membership is not None


def is_valid_ratings(rating, workload, grade):
    return 0 < rating < 6 and 0 < workload < 6 and re.compile('^[A-DF]$|^[A-C][\+-]$').match(grade)


def get_or_create(model, defaults=None, **kwargs):
    instance = db.session.query(model).filter_by(**kwargs).first()
    if instance:
        return instance, False
    else:
        params = dict((k, v) for k, v in kwargs.items() if not isinstance(v, ClauseElement))
        params.update(defaults or {})
        instance = model(**params)
        db.session.add(instance)
        return instance, True


@app.context_processor
def injection():
    return {'now': datetime.utcnow()}


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/login')
def login():
    session['oauth_redirect'] = request.args.get('redirect') or url_for('index')
    if current_user.is_authenticated:
        return redirect(session['oauth_redirect'])

    google = get_google_auth()
    auth_url, state = google.authorization_url(
        Auth.AUTH_URI, access_type='offline', prompt='select_account')
    session['oauth_state'] = state
    return redirect(auth_url)


@app.route('/oauth')
def oauth2callback():
    # Redirect user to home page if already logged in.
    if current_user is not None and current_user.is_authenticated:
        return redirect(session.get('oauth_redirect', url_for('index')))
    if 'error' in request.args:
        if request.args.get('error') == 'access_denied':
            return 'You denied access.'
        return 'Error encountered.'
    if 'code' not in request.args and 'state' not in request.args:
        return redirect(url_for('login'))
    else:
        # Execution reaches here when user has
        # successfully authenticated our app.
        google = get_google_auth(state=session['oauth_state'])
        try:
            token = google.fetch_token(
                Auth.TOKEN_URI,
                client_secret=Auth.CLIENT_SECRET,
                authorization_response=request.url)
        except HTTPError:
            return 'HTTPError occurred.'
        google = get_google_auth(token=token)
        resp = google.get(Auth.USER_INFO)
        if resp.status_code == 200:
            user_data = resp.json()
            email = user_data['email']
            uni, hd = email.split('@')
            if (hd != 'columbia.edu' and hd != 'barnard.edu') or not re.compile(r'([a-z]{2,3}\d{1,4})').match(uni):
                flash('You cannot login using this email. Please use Lionmail instead.', 'danger')
                return redirect(session.get('oauth_redirect', url_for('index')))

            # Register user
            user = User.query.filter_by(email=email).first()
            if user is None:
                user = User()
                user.email = email
            user.id = uni
            user.name = user_data['name']
            print(token)
            user.tokens = json.dumps(token)
            user.avatar = user_data['picture']
            db.session.add(user)
            db.session.commit()

            # Register membership
            role_student = Role.query.filter_by(name='student').first()
            role_instructor = Role.query.filter_by(name='instructor').first()
            if not has_membership(user.id, role_instructor) and not has_membership(user.id, role_student):
                db.session.add(Membership(user=user, role=role_student))
                db.session.commit()

            login_user(user)
            return redirect(session.get('oauth_redirect', url_for('index')))
        return 'Could not fetch your information.'


@app.route('/logout')
@login_required
def logout():
    redirect_url = request.args.get('redirect') or url_for('index')
    logout_user()
    session.clear()
    return redirect(redirect_url)


@login_manager.unauthorized_handler
def unauthorized_callback():
    return redirect(url_for('login') + '?redirect=' + request.path)


@app.route('/department', methods=["GET", "POST"])
def department():
    """
    Render the template with all departments if it is the "GET" request.
    Render the template with departments having keywords if it is the "POST" request.
    :return: rendered template
    """
    if request.method == "GET":
        all_depts = Department.query.order_by(Department.id).all()
        context = {'depts': all_depts}
        return render_template('department.html', **context)

    elif request.method == "POST":
        dept_keyword = request.form['dept_keyword']
        depts = Department.query.filter(Department.name.contains(dept_keyword)).order_by(Department.id).all()
        context = {'depts': depts}
        return render_template('department.html', **context)


@app.route('/dept/<regex("[A-Za-z]{4}"):dept_arg>/')
def department_course(dept_arg):
    dept = Department.query.filter_by(id=dept_arg.upper()).first()
    if dept is None:
        return redirect(url_for('department'))

    statistics = {}
    all_courses = Course.query.filter_by(department=dept).order_by(Course.id).all()
    for c in all_courses:
        sum_rating = sum_workload = sum_grade = 0
        count_all_comments = count_nonempty_comments = 0
        statistics[c] = {}

        all_teachings = Teaching.query.filter_by(course=c).all()
        if len(all_teachings) == 0:  # If there is no teaching, return -1 for rating, workload and grade
            statistics[c]['rating'] = statistics[c]['workload'] = statistics[c]['grade'] = -1
            statistics[c]['comment'] = 0
        else:  # Otherwise, iterate each teaching
            for teaching in all_teachings:
                all_comments = teaching.comments
                for comment in all_comments:  # Iterate each comment
                    count_all_comments += 1
                    sum_rating += comment.rating
                    sum_workload += comment.workload
                    sum_grade += helpers.letter_grade_to_numeric(comment.grade)
                    if not (not comment.title.strip()) and not (not comment.content.strip()):
                        count_nonempty_comments += 1

            if count_all_comments == 0:  # If there is no comment, return -1 for rating, workload and grade
                statistics[c]['rating'] = statistics[c]['workload'] = statistics[c]['grade'] = -1
                statistics[c]['comment'] = 0
            else:
                statistics[c]['rating'] = sum_rating / count_all_comments
                statistics[c]['workload'] = sum_workload / count_all_comments
                statistics[c]['grade'] = sum_grade / count_all_comments
                statistics[c]['comment'] = count_nonempty_comments

    context = {
        'dept': dept,
        'courses': statistics,
    }
    return render_template('department-course.html', **context)


@app.route('/course/<regex("[A-Za-z]{4}[A-Za-z0-9]{4,5}"):course_arg>/')
def course(course_arg):
    c = Course.query.filter_by(id=course_arg.upper()).first()
    depts = Department.query.all()
    if c is None:
        return redirect(url_for('department'))
    context = {
        'course': c,
        'depts': depts,
    }
    return render_template('course-detail.html', **context)


@app.route('/course/<regex("[A-Za-z]{4}[A-Za-z0-9]{4,5}"):course_arg>/json/')
def course_json(course_arg):
    c = Course.query.filter_by(id=course_arg.upper()).first()
    if c is None:
        abort(404)

    all_teachings = Teaching.query.filter_by(course=c).all()
    all_statistics = {}

    # Statistics of all professors
    all_profs_sum_rating = all_profs_sum_workload = all_profs_sum_grade = 0
    all_profs_tags_count = {}
    all_profs_count_all_comments = 0
    all_profs_nonempty_comments = []

    for teaching in all_teachings:
        # Statistics of the current professor in the current teaching
        # Each professor in all_teachings is unique
        cur_prof = teaching.professor
        cur_prof_sum_rating = cur_prof_sum_workload = cur_prof_sum_grade = 0
        cur_prof_tags_count = {}
        cur_prof_count_all_comments = 0
        cur_prof_nonempty_comments = []

        # Accumulate each ratings
        all_comments = teaching.comments
        for comment in all_comments:
            cur_prof_sum_rating += comment.rating
            cur_prof_sum_workload += comment.workload
            cur_prof_sum_grade += helpers.letter_grade_to_numeric(comment.grade)
            cur_prof_count_all_comments += 1
            all_profs_sum_rating += comment.rating
            all_profs_sum_workload += comment.workload
            all_profs_sum_grade += helpers.letter_grade_to_numeric(comment.grade)
            all_profs_count_all_comments += 1

            # Count the frequency of all tags
            for tag in comment.tags:
                cur_prof_tags_count[tag] = cur_prof_tags_count.get(tag, 0) + 1
                all_profs_tags_count[tag] = all_profs_tags_count.get(tag, 0) + 1

            # Only show comments that have titles or contents
            if not (not comment.title.strip()) and not (not comment.content.strip()):
                json_comment = {
                    'professor_name': comment.teaching.professor.name,
                    'title': comment.title,
                    'content': comment.content,
                    'term': comment.term.id,
                    'rating': comment.rating,
                    'workload': comment.workload,
                    'grade': comment.grade,
                    'timestamp': comment.timestamp,
                }
                cur_prof_nonempty_comments.append(json_comment)
                all_profs_nonempty_comments.append(json_comment)

        # Store ratings of the current professor
        all_statistics[cur_prof] = {
            'sum_rating': cur_prof_sum_rating,
            'sum_workload': cur_prof_sum_workload,
            'sum_grade': cur_prof_sum_grade,
            'tags_count': cur_prof_tags_count,
            'count_all_comments': cur_prof_count_all_comments,
            'nonempty_comments': cur_prof_nonempty_comments,
        }

    # Return JSON
    ret = []
    # Return statistics of all professors
    all_profs_tags = [tag.text for tag in sorted(all_profs_tags_count, key=all_profs_tags_count.get, reverse=True)][:10]
    ret.append({
        'name': 'All Instructors',
        'uni': None,
        'avatar': '',
        'rating': -1 if all_profs_count_all_comments == 0 else all_profs_sum_rating / all_profs_count_all_comments,
        'workload': -1 if all_profs_count_all_comments == 0 else all_profs_sum_workload / all_profs_count_all_comments,
        'grade': -1 if all_profs_count_all_comments == 0 else all_profs_sum_grade / all_profs_count_all_comments,
        'tags': all_profs_tags,
        'comments': all_profs_nonempty_comments,
    })
    # Return statistics of each professor
    for cur_prof, cur_prof_statistics in all_statistics.items():
        # Sort and find the most frequent tags
        cur_prof_tags_count = cur_prof_statistics['tags_count']
        cur_prof_tags = [tag.text for tag in sorted(cur_prof_tags_count, key=cur_prof_tags_count.get, reverse=True)][:10]
        cur_prof_count_all_comments = cur_prof_statistics['count_all_comments']

        ret.append({
            'name': cur_prof.name,
            'uni': cur_prof.uni,
            'avatar': cur_prof.avatar,
            'rating': -1 if cur_prof_count_all_comments == 0 else cur_prof_statistics['sum_rating'] / cur_prof_count_all_comments,
            'workload': -1 if cur_prof_count_all_comments == 0 else cur_prof_statistics['sum_workload'] / cur_prof_count_all_comments,
            'grade': -1 if cur_prof_count_all_comments == 0 else cur_prof_statistics['sum_grade'] / cur_prof_count_all_comments,
            'tags': cur_prof_tags,
            'comments': cur_prof_statistics['nonempty_comments'],
        })
    return jsonify(ret)


@app.route('/course/new/', methods=['GET', 'POST'])
@login_required
def add_new_course():
    """
    Add course request

    Request form example:
    {
        'course_name': 'Computer Networks',
        'course_number': 'CSEE4119',
        'department_name': 'Computer Science',
        'subject_name': 'Computer Science and Electrical Engineering'
    }

    :return: json-str.
        "OK" if add successfully. "Fail" + error info if subject / department / professor not exists.
    """
    redirect_url = request.args.get('redirect') or url_for('index')
    if request.method == "GET":
        depts = Department.query.all()
        subjs = Subject.query.all()
        context = {
            'depts': depts,
            'subjs': subjs,
            'redirect_url': redirect_url,
        }
        return render_template('add-course.html', **context)

    elif request.method == "POST":
        # Pre check
        form_arguments = {'course-name', 'course-number', 'department', 'subject', 'semester', 'year'}
        for arg in form_arguments:
            if arg not in request.form:
                abort(404)

        # Retrieve request arguments
        course_name = request.form.get('course-name', type=str)
        course_number = request.form.get('course-number', type=str)
        department_id = request.form.get('department', type=str)
        subject_id = request.form.get('subject', type=str)
        course_id = subject_id + course_number
        term_id = request.form.get('semester', type=str) + ' ' + request.form.get('year', type=str)

        # Post check
        department = Department.query.filter_by(id=department_id).first()
        subject = Subject.query.filter_by(id=subject_id).first()
        for param in (course_id, course_name, course_number, department, subject, term_id):  # None or empty
            if not param:
                flash('The values you have input are invalid. Please check and submit again.', 'danger')
                return redirect(redirect_url)

        # Add request to database
        try:
            term, _ = get_or_create(Term, id=term_id)
            add_course_request = AddCourseRequest(
                course_id=course_id, course_name=course_name, course_number=course_number,
                department=department, subject=subject, term=term,
                user_id=current_user.id, approved=ApprovalType.PENDING)
            db.session.add(add_course_request)
            db.session.commit()
            flash('The adding course request is submitted.', 'success')
        except SQLAlchemyError:
            flash('An error occurred when submitting an adding course request.', 'danger')
        return redirect(redirect_url)


@app.route('/search/', methods=['GET'])
def search():
    """
    Search department, subject, professor and course
    :param
        request example
            {'dept': 'COMS', 'subj': 'COMS', 'prof': 'daniel', course: 'COMS4156', }
    :return: rendered template

    Examples:
        /search/?prof=daniel
    """
    context = {}
    context['count'] = 0

    # get keywords
    dept = request.args.get('dept')
    subj = request.args.get('subj')
    prof = request.args.get('prof')
    course = request.args.get('course')

    if dept is not None:
        dept = dept.strip()
        depts = re.split(',\s*|\s+', dept)  # split keywords by , |\s
        results = []
        for dept in depts:
            for result in Department.query.filter(
                    (Department.id.ilike("%%" + dept.upper() + "%")) |
                    (Department.name.ilike("%%" + dept + "%"))
            ).order_by(Department.id).all():
                results.append(result)
        context['depts'] = results
        context['count'] += len(results)

    if subj is not None:
        subj = subj.strip()
        subjs = re.split(',\s*|\s+', subj)
        results = []
        for subj in subjs:
            for result in Subject.query.filter(
                    (Subject.id.ilike("%%" + subj.upper() + "%")) |
                    (Subject.name.ilike("%%" + subj + "%"))
            ).order_by(Subject.id).all():
                results.append(result)
        context['subjs'] = results
        context['count'] += len(results)

    if prof is not None:
        prof = prof.strip()
        profs = re.split(',\s*|\s+', prof)
        results = []
        for prof in profs:
            for result in Professor.query.filter(
                    (Professor.uni.ilike("%%" + prof + "%")) |
                    (Professor.name.ilike("%%" + prof + "%"))
            ).order_by(Professor.name).all():
                results.append(result)
        context['profs'] = results
        context['count'] += len(results)

    if course is not None:
        course = course.strip()
        courses = re.split(',\s*|\s+', course)
        results = []
        for course in courses:
            for result in Course.query.filter(
                    (Course.id.ilike("%%" + course.upper() + "%")) |
                    (Course.name.ilike("%%" + course + "%"))
            ).order_by(Course.id).all():
                results.append(result)
        context['courses'] = results
        context['count'] += len(results)
    return render_template('search-result.html', **context)


@app.route('/prof/<regex("[A-Za-z]{2,3}[0-9]{1,4}"):prof_arg>/')
def prof(prof_arg):
    prof = Professor.query.filter_by(uni=prof_arg.lower()).first()
    if prof is None:
        abort(404)

    all_teachings = Teaching.query.filter_by(professor=prof).order_by(Teaching.course_id).all()
    statistics = {}  # Statistics of each course
    # Statistics of all courses
    all_courses_sum_rating = all_courses_sum_workload = all_courses_sum_grade = all_courses_count_all_comments = 0
    all_courses_tags_count = {}
    all_statistics = {}

    # Statistics of all courses (i.e. all teachings)
    for teaching in all_teachings:
        c = teaching.course
        cur_course_sum_rating = cur_course_sum_workload = cur_course_sum_grade = 0
        cur_course_count_all_comments = cur_course_count_nonempty_comments = 0
        statistics[c] = {}

        all_comments = teaching.comments
        for comment in all_comments:  # Iterate each comment
            cur_course_count_all_comments += 1
            cur_course_sum_rating += comment.rating
            cur_course_sum_workload += comment.workload
            cur_course_sum_grade += helpers.letter_grade_to_numeric(comment.grade)
            if not (not comment.title.strip()) and not (not comment.content.strip()):
                cur_course_count_nonempty_comments += 1

            all_courses_count_all_comments += 1
            all_courses_sum_rating += comment.rating
            all_courses_sum_workload += comment.workload
            all_courses_sum_grade += helpers.letter_grade_to_numeric(comment.grade)
            # Count the frequency of all tags
            for tag in comment.tags:
                all_courses_tags_count[tag] = all_courses_tags_count.get(tag, 0) + 1

        # Calculate ratings for the current course
        if cur_course_count_all_comments == 0:  # If there is no comment, return N/A for rating, workload and grade
            statistics[c]['rating'] = statistics[c]['workload'] = statistics[c]['grade'] = -1
            statistics[c]['comment'] = 0
        else:
            statistics[c]['rating'] = cur_course_sum_rating / cur_course_count_all_comments
            statistics[c]['workload'] = cur_course_sum_workload / cur_course_count_all_comments
            statistics[c]['grade'] = cur_course_sum_grade / cur_course_count_all_comments
            statistics[c]['comment'] = cur_course_count_nonempty_comments

    # Calculate ratings for the professor
    all_statistics['tags'] = [tag.text for tag in sorted(all_courses_tags_count, key=all_courses_tags_count.get, reverse=True)][:10]
    if all_courses_count_all_comments == 0:  # If there is no comment, return N/A for rating, workload and grade
        all_statistics['rating'] = all_statistics['workload'] = all_statistics['grade'] = -1
    else:
        all_statistics['rating'] = all_courses_sum_rating / all_courses_count_all_comments
        all_statistics['workload'] = all_courses_sum_workload / all_courses_count_all_comments
        all_statistics['grade'] = all_courses_sum_grade / all_courses_count_all_comments

    context = {
        'prof': prof,
        'courses': statistics,
        'prof_stats': all_statistics,
    }
    return render_template('faculty-page.html', **context)


@app.route('/prof/new/', methods=['POST'])
def add_new_prof():
    """
    Add professor request
    Request form example:
    {
        'name': 'Clifford Stein',
        'department_id': 'COMS',
        'term_id': 'Fall 2016'
    }
    :return: json-str.
        "OK" if add successfully. "Fail" + error info if subject / department / professor not exists.
    """
    redirect_url = request.args.get('redirect') or url_for('index')

    # Pre check
    form_arguments = {'name', 'department', 'course', 'semester', 'year'}
    for arg in form_arguments:
        if arg not in request.form:
            abort(404)

    # Retrieve request arguments
    name = request.form.get('name', type=str)
    department_id = request.form.get('department', type=str)
    course_id = request.form.get('course', type=str)
    term_id = request.form.get('semester', type=str) + ' ' + request.form.get('year', type=str)

    # Post check
    department = Department.query.filter_by(id=department_id).first()
    course = Course.query.filter_by(id=course_id).first()
    for param in (name, department, course, term_id):  # None or empty
        if not param:
            flash('The values you have input are invalid. Please check and submit again.', 'danger')
            return redirect(redirect_url)

    # Add request to database
    try:
        term, _ = get_or_create(Term, id=term_id)
        add_prof_request = AddProfRequest(
            name=name, department=department, course=course, term=term,
            user_id=current_user.id, approved=ApprovalType.PENDING)
        db.session.add(add_prof_request)
        db.session.commit()
        flash('The adding instructor request is submitted.', 'success')
        return redirect(redirect_url)
    except SQLAlchemyError:
        flash('An error occurred when submitting an adding instructor request.', 'danger')
        return redirect(redirect_url)


@app.route('/comment/', methods=["POST"])
@login_required
def comment():
    if request.method == "POST":
        redirect_url = request.args.get('redirect') or url_for('index')
        # Retrieve teaching
        prof_uni = request.form.get('prof', type=str)
        course_id = request.form.get('course', type=str)
        teaching = Teaching.query.filter_by(course_id=course_id.upper(), professor_uni=prof_uni.lower()).first()
        if teaching is None:
            abort(500)
        # Retrieve request arguments
        term_id = request.form.get('semester', type=str) + ' ' + request.form.get('year', type=str)
        title = request.form.get('title', type=str)
        content = request.form.get('message', type=str)
        rating = request.form.get('rating', type=int)
        workload = request.form.get('workload', type=int)
        grade = request.form.get('grade', type=str)
        tags_str = request.form.get('tags', type=str)
        # Post check
        if not is_valid_ratings(rating, workload, grade):
            flash('The values you have input are invalid. Please check and submit again.', 'danger')
            return redirect(redirect_url)
        tags_str_list = tags_str.split(',')
        # Insert
        try:
            tags = []
            for t in tags_str_list:
                if t:  # Neither None nor empty string
                    tag, _ = get_or_create(Tag, text=t.capitalize())
                    tags.append(tag)
            term, _ = get_or_create(Term, id=term_id)

            # Check if existed
            existed_comment = Comment.query.filter_by(user_id=current_user.id, teaching=teaching, term=term).first()
            if existed_comment is not None:
                flash('You have already published an evaluation before.', 'danger')
            else:
                comment = Comment(user_id=current_user.id, teaching=teaching, term=term,
                                  title=title, content=content, rating=rating, workload=workload, grade=grade, tags=tags)
                db.session.add(comment)
                db.session.commit()
                flash('The evaluation is published.', 'success')
            return redirect(redirect_url)
        except SQLAlchemyError:
            flash('An error occurred when publishing the evaluation.', 'danger')
            return redirect(redirect_url)


@app.route('/admin/', methods=['GET'])
@login_required
def admin():
    """Return the admin page, or redirect to index page if not authorized."""
    role_admin = Role.query.filter_by(name='admin').first()
    if current_user.is_authenticated and has_membership(current_user.id, role_admin):
        return render_template('admin.html')
    else:
        flash('You do not have the permission to view this page.', 'danger')
        return redirect(url_for('index'))


@app.route('/admin/course', methods=['GET'])
@login_required
def admin_list_course_request():
    """Return a JSON containing adding course requests, or raise 401 if not authorized."""
    role_admin = Role.query.filter_by(name='admin').first()
    if current_user.is_authenticated and has_membership(current_user.id, role_admin):
        list_approved = request.args.get('approved', type=int) or 0
        list_pending = request.args.get('pending', type=int) or 0
        list_declined = request.args.get('declined', type=int) or 0
        course_requests = []
        if list_approved:
            course_requests.extend(AddCourseRequest.query.filter_by(approved=ApprovalType.APPROVED).all())
        if list_pending:
            course_requests.extend(AddCourseRequest.query.filter_by(approved=ApprovalType.PENDING).all())
        if list_declined:
            course_requests.extend(AddCourseRequest.query.filter_by(approved=ApprovalType.DECLINED).all())
        ret = []
        for course_request in course_requests:
            ret.append({
                'id': course_request.id,
                'subject_id': course_request.subject.id,
                'course_number': course_request.course_number,
                'course_name': course_request.course_name,
                'department_id': course_request.department.id,
                'term_id': course_request.term.id,
                'approved': course_request.approved.value,
            })
        return jsonify(ret)
    else:
        abort(401)


@app.route('/admin/prof', methods=['GET'])
@login_required
def admin_list_prof_request():
    """Return a JSON containing adding instructor requests, or raise 401 if not authorized."""
    role_admin = Role.query.filter_by(name='admin').first()
    if current_user.is_authenticated and has_membership(current_user.id, role_admin):
        list_approved = request.args.get('approved', type=int) or 0
        list_pending = request.args.get('pending', type=int) or 0
        list_declined = request.args.get('declined', type=int) or 0
        prof_requests = []
        if list_approved:
            prof_requests.extend(AddProfRequest.query.filter_by(approved=ApprovalType.APPROVED).all())
        if list_pending:
            prof_requests.extend(AddProfRequest.query.filter_by(approved=ApprovalType.PENDING).all())
        if list_declined:
            prof_requests.extend(AddProfRequest.query.filter_by(approved=ApprovalType.DECLINED).all())
        ret = []
        for prof_request in prof_requests:
            ret.append({
                'id': prof_request.id,
                'name': prof_request.name,
                'department_id': prof_request.department.id,
                'course_id': prof_request.course.id,
                'term_id': prof_request.term.id,
                'approved': prof_request.approved.value,
            })
        return jsonify(ret)
    else:
        abort(401)


@app.route('/admin/course', methods=['POST'])
@login_required
def admin_approve_course_request():
    """
    Admin approves a course.
    - Add a new course to Course entity
    - Add a new teaching to Teaching entity

    :return: json-str.
        "OK" if add successfully. "Fail" + error info if subject / department / professor not exists.
    """
    # Get parameters
    role_admin = Role.query.filter_by(name='admin').first()
    if current_user.is_authenticated and has_membership(current_user.id, role_admin):
        req_id = request.form.get('request_id', type=int)  # 1
        subject_id = request.form.get('subject', type=str)  # COMS
        course_number = request.form.get('course_num', type=str)  # 4771
        course_name = request.form.get('course_name', type=str)  # Machine Learning
        department_id = request.form.get('department', type=str)  # COMS
        approved = True if request.form.get('decision', type=str).lower() == 'true' else False  # True

        approved_types = {True: ApprovalType.APPROVED, False: ApprovalType.DECLINED}

        try:
            if approved:
                # Check parameters
                for param in (req_id, subject_id, course_number, course_name, department_id):
                    if not param:  # None or empty string
                        return jsonify(error=500, text='failure')

                # Pre-process parameters
                course_id = subject_id + course_number

                # Get objects
                subject = Subject.query.filter_by(id=subject_id).first()
                department = Department.query.filter_by(id=department_id).first()

                # Check objects
                if subject is None or department is None:
                    return jsonify(error=500, text='failure')

                # Construct objects
                db.session.add(Course(id=course_id, subject=subject, number=course_number, department=department,
                                      name=course_name))
                db.session.commit()

            # Update add prof request
            if req_id != -1 and AddCourseRequest.query.filter_by(id=req_id).first() is not None:
                new_course_request = AddCourseRequest.query.filter_by(id=req_id).first()
                new_course_request.approved = approved_types[approved]
                db.session.commit()

            # Check if the new course is in the db
            if approved:
                new_course = Course.query.filter_by(id=course_id).first()
                if new_course is None:
                    return jsonify(error=500, text='failure')

            return jsonify('success')
        except SQLAlchemyError:
            return jsonify(error=500, text='failure')


@app.route('/admin/prof', methods=['POST'])
@login_required
def admin_approve_prof_request():
    """
    Admin approves a prof.

    Example:
    {
        "id": 1,
        "uni": "ab1234",
        "prof_name: "Alpha Beta",
        "department_id": "COMS",
        "term_id": "Fall 2017",
        "course_id": "COMS4115",
        "avatar": "",
        "url": "",
        "approved": False
    }

    :return: json-str.
        "OK" if add successfully. "Fail" + error info if subject / department / professor not exists.
    """
    role_admin = Role.query.filter_by(name='admin').first()
    if current_user.is_authenticated and has_membership(current_user.id, role_admin):
        # Get parameters
        req_id = request.form.get('request_id', type=int)
        uni = request.form.get('uni', type=str)
        prof_name = request.form.get('name', type=str)
        department_id = request.form.get('department', type=str)
        term_id = request.form.get('semester', type=str)
        avatar = request.form.get('avatar', type=str)
        url = request.form.get('url', type=str)
        course_id = request.form.get('course', type=str)
        approved = True if request.form.get('decision', type=str).lower() == 'true' else False

        approved_types = {True: ApprovalType.APPROVED, False: ApprovalType.DECLINED}

        try:
            if approved:
                # Check parameters
                for param in (req_id, uni, prof_name, department_id, term_id, course_id):
                    if not param:  # None or empty string
                        return jsonify(error=500, text='failure')

                # Check if this professor exists
                # Get objects
                department = Department.query.filter_by(id=department_id).first()
                course = Course.query.filter_by(id=course_id).first()

                # Check objects
                if department is None or course is None:
                    return jsonify(error=500, text='failure')

                # Update professor
                professor = Professor.query.filter_by(uni=uni).first()
                if professor is None:  # Create a new professor object if not exists
                    professor = Professor(uni=uni, name=prof_name, department=department)
                if avatar:  # Update avatar or URL if not empty
                    professor.avatar = avatar
                if url:
                    professor.url = url
                db.session.add(professor)
                db.session.commit()

                # Add a new teaching if not exists
                term, _ = get_or_create(Term, id=term_id)
                if Teaching.query.filter_by(course_id=course_id, professor_uni=uni).first() is None:
                    teaching = Teaching(course=course, professor=professor)
                    db.session.add(teaching)
                    db.session.commit()

            # Update the approved column
            if req_id != -1 and AddProfRequest.query.filter_by(id=req_id).first() is not None:  # If it is not added by admin
                new_prof_request = AddProfRequest.query.filter_by(id=req_id).first()
                new_prof_request.approved = approved_types[approved]
                db.session.commit()

            # Check if the professor is added to db
            if approved:
                new_prof = Professor.query.filter_by(uni=uni).first()
                new_teaching = Teaching.query.filter_by(course_id=course_id, professor_uni=uni).first()
                if new_prof is None or new_teaching is None:
                    return jsonify(error=500, text='failure')

            return jsonify('success')
        except SQLAlchemyError:
            return jsonify(error=500, text='failure')


@app.route('/student/', methods=['GET'])
@login_required
def student():
    """Return the student page, or redirect to index page if not authorized."""
    role_student = Role.query.filter_by(name='student').first()
    if current_user.is_authenticated and has_membership(current_user.id, role_student):
        return render_template('student.html')
    else:
        flash('You do not have the permission to view this page.', 'danger')
        return redirect(url_for('index'))


@app.route('/student/course', methods=['GET'])
@login_required
def student_list_course_request():
    """Return a JSON containing adding course requests, or raise 401 if not authorized."""
    role_student = Role.query.filter_by(name='student').first()
    if current_user.is_authenticated and has_membership(current_user.id, role_student):
        list_approved = request.args.get('approved', type=int) or 0
        list_pending = request.args.get('pending', type=int) or 0
        list_declined = request.args.get('declined', type=int) or 0
        course_requests = []
        if list_approved:
            course_requests.extend(AddCourseRequest.query.filter_by(
                user_id=current_user.id,
                approved=ApprovalType.APPROVED,
            ).all())
        if list_pending:
            course_requests.extend(AddCourseRequest.query.filter_by(
                user_id=current_user.id,
                approved=ApprovalType.PENDING,
            ).all())
        if list_declined:
            course_requests.extend(AddCourseRequest.query.filter_by(
                user_id=current_user.id,
                approved=ApprovalType.DECLINED
            ).all())
        ret = []
        for course_request in course_requests:
            ret.append({
                'id': course_request.id,
                'subject_id': course_request.subject.id,
                'course_number': course_request.course_number,
                'course_name': course_request.course_name,
                'department_id': course_request.department.id,
                'term_id': course_request.term.id,
                'approved': course_request.approved.value,
            })
        return jsonify(ret)
    else:
        abort(401)


@app.route('/student/prof', methods=['GET'])
@login_required
def student_list_prof_request():
    """Return a JSON containing adding instructor requests, or raise 401 if not authorized."""
    role_student = Role.query.filter_by(name='student').first()
    if current_user.is_authenticated and has_membership(current_user.id, role_student):
        list_approved = request.args.get('approved', type=int) or 0
        list_pending = request.args.get('pending', type=int) or 0
        list_declined = request.args.get('declined', type=int) or 0
        prof_requests = []
        if list_approved:
            prof_requests.extend(AddProfRequest.query.filter_by(
                user_id=current_user.id,
                approved=ApprovalType.APPROVED,
            ).all())
        if list_pending:
            prof_requests.extend(AddProfRequest.query.filter_by(
                user_id=current_user.id,
                approved=ApprovalType.PENDING,
            ).all())
        if list_declined:
            prof_requests.extend(AddProfRequest.query.filter_by(
                user_id=current_user.id,
                approved=ApprovalType.DECLINED,
            ).all())
        ret = []
        for prof_request in prof_requests:
            ret.append({
                'id': prof_request.id,
                'name': prof_request.name,
                'department_id': prof_request.department.id,
                'course_id': prof_request.course.id,
                'term_id': prof_request.term.id,
                'approved': prof_request.approved.value,
            })
        return jsonify(ret)
    else:
        abort(401)


@app.route('/student/comment/', methods=['GET'])
@login_required
def student_list_comment():
    """Return a JSON containing all comments, or raise 401 if not authorized."""
    role_student = Role.query.filter_by(name='student').first()
    if current_user.is_authenticated and has_membership(current_user.id, role_student):
        comments = Comment.query.filter_by(user_id=current_user.id).all()
        ret = []
        for comment in comments:
            tags = []
            for tag in comment.tags:
                tags.append(tag.text)
            ret.append({
                'id': comment.id,  # For reference only
                'teaching_id': comment.teaching_id,  # For reference only
                'subject_id': comment.teaching.course.subject_id,
                'course_number': comment.teaching.course.number,
                'course_name': comment.teaching.course.name,
                'professor_name': comment.teaching.professor.name,
                'title': comment.title,
                'content': comment.content,
                'term_id': comment.term.id,
                'rating': comment.rating,
                'workload': comment.workload,
                'grade': comment.grade,
                'tags': tags,
            })
        return jsonify(ret)
    else:
        abort(401)


@app.route('/student/comment/update/', methods=['POST'])
@login_required
def student_update_comment():
    redirect_url = url_for('student')
    # Retrieve request arguments
    id = request.form.get('comment-id', type=int)
    comment = Comment.query.filter_by(id=id).first()
    if comment is None:
        flash('An error occurred when updating the evaluation.', 'danger')
        return redirect(redirect_url)

    term_id = request.form.get('semester', type=str) + ' ' + request.form.get('year', type=str)
    title = request.form.get('title', type=str)
    content = request.form.get('message', type=str)
    rating = request.form.get('rating', type=int)
    workload = request.form.get('workload', type=int)
    grade = request.form.get('grade', type=str)
    tags_str = request.form.get('tags', type=str)
    # Post check
    if not is_valid_ratings(rating, workload, grade):
        flash('The values you have input are invalid. Please check and submit again.', 'danger')
        return redirect(redirect_url)
    tags_str_list = tags_str.split(',')
    # Update
    try:
        tags = []
        for t in tags_str_list:
            if t:  # Neither None nor empty string
                tag, _ = get_or_create(Tag, text=t.capitalize())
                tags.append(tag)
        term, _ = get_or_create(Term, id=term_id)

        # Update comment object
        comment.term = term
        comment.title = title
        comment.content = content
        comment.rating = rating
        comment.workload = workload
        comment.grade = grade
        comment.tags = tags

        db.session.add(comment)
        db.session.commit()
        flash('The evaluation is updated.', 'success')
        return redirect(redirect_url)
    except SQLAlchemyError:
        flash('An error occurred when updating the evaluation.', 'danger')
        return redirect(redirect_url)


@app.route('/student/comment/delete/', methods=['POST'])
@login_required
def student_delete_comment():
    redirect_url = url_for('student')
    # Retrieve request arguments
    id = request.form.get('comment-id', type=int)
    comment = Comment.query.filter_by(id=id).first()
    if comment is None:
        flash('An error occurred when deleting the evaluation.', 'danger')
        return redirect(redirect_url)

    # Delete
    try:
        db.session.delete(comment)
        db.session.commit()
        flash('The evaluation is deleted.', 'success')
        return redirect(redirect_url)
    except SQLAlchemyError:
        flash('An error occurred when deleting the evaluation.', 'danger')
        return redirect(redirect_url)


@app.errorhandler(400)
def bad_request_error(e):
    context = {
        'title': 'Bad Request',
        'content': 'the server could not understand the request.',
    }
    logging.exception(format(e))
    return render_template('404.html', **context), 400


@app.errorhandler(401)
def not_authorized_error(e):
    context = {
        'title': 'Authorization Required',
        'content': 'you are not authorized to access this page.',
    }
    logging.exception(format(e))
    return render_template('404.html', **context), 401


@app.errorhandler(404)
def page_not_found_error(e):
    context = {
        'title': 'Page Not Found',
        'content': 'the content was not found.',
    }
    logging.exception(format(e))
    return render_template('404.html', **context), 404


@app.errorhandler(500)
def server_error(e):
    context = {
        'title': 'Internal Server Error',
        'content': 'the server encountered an internal error.',
    }
    logging.exception(format(e))
    return render_template('404.html', **context), 500
