
rem CREATE TABLE Teachers
CREATE TABLE Teachers(
    t_id    NUMBER(5),
    t_name  VARCHAR(30),
    phone   CHAR(12),
    CONSTRAINT pk_teachers PRIMARY KEY(t_id)
);

rem CREATE TABLE Students
CREATE TABLE Students(
    s_id    NUMBER(5),
    s_name  VARCHAR(30),
    CONSTRAINT pk_students PRIMARY KEY(s_id)
);

rem CREATE TABLE Classes
CREATE TABLE Classes(
    c_id    NUMBER(5),
    t_id    NUMBER(5),
    subject VARCHAR(30),
    CONSTRAINT fk_classes FOREIGN KEY(t_id) REFERENCES Teachers(t_id)
);

rem CREATE TABLE ClassList
CREATE TABLE ClassList(
    c_id    NUMBER(5),
    s_id    NUMBER(5),
    CONSTRAINT pk_classlist PRIMARY KEY(c_id, s_id),
    CONSTRAINT fk_classlist_class FOREIGN KEY(c_id) REFERENCES Classes(c_id),
    CONSTRAINT fk_classlist_stud FOREIGN KEY(s_id) REFERENCES Students(s_id)
);
