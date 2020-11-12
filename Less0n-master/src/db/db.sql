/*
* Version:
* Author: Zhijian Jiang <zhijian.jiang at columbia.edu>
* Description: DDL of user database of LessOn
* Reference: https://www.getdonedone.com/building-the-optimal-user-database-model-for-your-application/
 */


CREATE SCHEMA IF NOT EXISTS less0n;

----------------------------------------------------------------------
-- Create a table, users, to store the user''s basic info
CREATE TABLE IF NOT EXISTS less0n.users (
    id VARCHAR(40) PRIMARY KEY NOT NULL , -- Columbia Uni
    -- [What is a reasonable length limit on person “Name” fields?](https://stackoverflow.com/questions/30485/what-is-a-reasonable-length-limit-on-person-name-fields)
    avatar  VARCHAR(200),
    email   VARCHAR(40)  NOT NULL UNIQUE CHECK (email LIKE '%_@__%.__%'),
    name    VARCHAR(100) NOT NULL,
    tokens  TEXT
);

----------------------------------------------------------------------
-- Create a procedure to extract uni from email
CREATE OR REPLACE FUNCTION less0n.generate_user_id()
    RETURNS TRIGGER AS
$BODY$
/*
    % CREATE TRIGGER before_insert_on_users BEFORE INSERT ON lessOn_user.users
  FOR EACH ROW EXECUTE PROCEDURE generate_user_id();

Extract uni from new user's email and set the uni be new user's user_id.
*/
BEGIN
    NEW.id = substring(NEW.email FROM 1 FOR (position('@' IN NEW.email) - 1));
    RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100
;

----------------------------------------------------------------------
-- Create a trigger of users to generate user_id from email
CREATE TRIGGER before_insert_on_users
    BEFORE INSERT
    ON less0n.users
    FOR EACH ROW EXECUTE PROCEDURE less0n.generate_user_id();

----------------------------------------------------------------------
-- Create a table, role, store the role info
CREATE TABLE IF NOT EXISTS less0n.roles (
    id   SERIAL PRIMARY KEY NOT NULL ,
    name VARCHAR(40) NOT NULL UNIQUE
);

----------------------------------------------------------------------
-- Create a table, memberships, store the user''s relevant role and other info
CREATE TABLE IF NOT EXISTS less0n.memberships (
    id      SERIAL PRIMARY KEY NOT NULL ,
    user_id VARCHAR(40) NOT NULL,
    role_id INT         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES less0n.users (id),
    FOREIGN KEY (role_id) REFERENCES less0n.roles (id)
);

----------------------------------------------------------------------
-- Create a table, departments, store the department''s info
CREATE TABLE IF NOT EXISTS less0n.departments (
    id   VARCHAR(6) PRIMARY KEY NOT NULL ,
    name VARCHAR(100) NOT NULL,
    url  VARCHAR(200)
);

----------------------------------------------------------------------
-- Create a table, subjects, store the subject''s info
CREATE TABLE IF NOT EXISTS less0n.subjects (
    id   VARCHAR(6) PRIMARY KEY NOT NULL ,
    name VARCHAR(100) NOT NULL
);

----------------------------------------------------------------------
-- Create a table, courses, store the courses'' info
CREATE TABLE IF NOT EXISTS less0n.courses (
    id         VARCHAR(12) PRIMARY KEY NOT NULL ,
    subject    VARCHAR(6) NOT NULL,
    number     VARCHAR(6)   NOT NULL,
    name       VARCHAR(100) NOT NULL,
    department VARCHAR(6)   NOT NULL,
    FOREIGN KEY (subject) REFERENCES less0n.subjects (id),
    FOREIGN KEY (department) REFERENCES less0n.departments (id)
);

----------------------------------------------------------------------
-- Create a table, professors, store the professors'' info
CREATE TABLE IF NOT EXISTS less0n.professors (
    uni        VARCHAR(8) PRIMARY KEY NOT NULL ,
    name       VARCHAR(100) NOT NULL,
    department VARCHAR(6)   NOT NULL,
    avatar     VARCHAR(200),
    url        VARCHAR(200),
    FOREIGN KEY (department) REFERENCES less0n.departments (id)
);

----------------------------------------------------------------------
-- Create a table, teachings, store the teaching info
CREATE TABLE IF NOT EXISTS less0n.teachings (
    id        SERIAL PRIMARY KEY NOT NULL ,
    course    VARCHAR(12) NOT NULL,
    professor VARCHAR(8) NOT NULL,
    FOREIGN KEY (course) REFERENCES less0n.courses (id),
    FOREIGN KEY (professor) REFERENCES less0n.professors (uni)
);

----------------------------------------------------------------------
-- Create a table, terms, store the term info
CREATE TABLE IF NOT EXISTS less0n.terms (
    id VARCHAR(12) PRIMARY KEY NOT NULL
);

----------------------------------------------------------------------
-- Create a table, tags, store the tag info
CREATE TABLE IF NOT EXISTS less0n.tags (
    id   SERIAL PRIMARY KEY,
    text VARCHAR(40) NOT NULL
);

----------------------------------------------------------------------
-- Create a table, comments, store the comment info
CREATE TABLE IF NOT EXISTS less0n.comments (
    id        SERIAL PRIMARY KEY NOT NULL ,
    teaching  INT NOT NULL,
    title     VARCHAR(100),
    content   TEXT,
    term      VARCHAR(12) NOT NULL,
    rating    INT        NOT NULL,
    workload  INT        NOT NULL,
    grade     VARCHAR(2) NOT NULL,
    user_id      VARCHAR(40) NOT NULL,
    timestamp TIMESTAMP,
    tags      INT NOT NULL,
    FOREIGN KEY (teaching) REFERENCES less0n.teachings (id),
    FOREIGN KEY (term) REFERENCES less0n.terms (id),
    FOREIGN KEY (user_id) REFERENCES less0n.users (id),
    FOREIGN KEY (tags) REFERENCES less0n.tags (id)
);
