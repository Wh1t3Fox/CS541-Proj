Drop table STUDENTS cascade constraints;
Drop table TEACHERS cascade constraints;
Drop table CLASSES cascade constraints;
Drop table ClassList cascade constraints;

CREATE TABLE Students(s_id NUMBER(5), s_name VARCHAR(30), gpa DECIMAL(3,2), s_pwrd VARCHAR(10), integrity_value NUMBER (1), CONSTRAINT pk_students PRIMARY KEY(s_id), CONSTRAINT fk_students_integrity FOREIGN KEY(integrity_value) REFERENCES Integrity(integrity_value));

CREATE TABLE Teachers(t_id NUMBER(5),t_name VARCHAR(30),office CHAR(12),t_pwrd VARCHAR(10),integrity_value NUMBER (1), CONSTRAINT pk_teachers PRIMARY KEY(t_id), CONSTRAINT fk_teachers_integrity FOREIGN KEY(integrity_value) REFERENCES Integrity(integrity_value));

CREATE TABLE Classes(c_id NUMBER(5),t_id NUMBER(5),subject VARCHAR(30),CONSTRAINT fk_classes FOREIGN KEY(t_id) REFERENCES Teachers(t_id));

CREATE TABLE ClassList(c_id NUMBER(5),s_id NUMBER(5),CONSTRAINT pk_classlist PRIMARY KEY(c_id, s_id),CONSTRAINT fk_classlist_class FOREIGN KEY(c_id) REFERENCES Classes(c_id),CONSTRAINT fk_classlist_stud FOREIGN KEY(s_id) REFERENCES Students(s_id));

CREATE TABLE Integrity(table_name VARCHAR(10),integrity_value NUMBER(1), CONSTRAINT pk_integrity PRIMARY KEY(table_name));

insert into STUDENTS values (0418,'S.Jack',3.5,'jack', 5);
insert into STUDENTS values (0671,'A.Smith',2.9,'smith', 5);
insert into STUDENTS values (1234,'T.Banks',4.0,'banks', 5);
insert into STUDENTS values (3726,'M.Lee',3.2,'lee', 5);
insert into STUDENTS values (4829,'J.Bale',3.0,'bale', 5);

insert into TEACHERS values (101,'S.Layton','L1', 'layton', 4);
insert into TEACHERS values (102,'B.Jungles','L2', 'jungles', 4);
insert into TEACHERS values (103,'N.Guzaldo','L3', 'guzaldo', 4);
insert into TEACHERS values (104,'S.Boling','L4', 'boling', 4);
insert into TEACHERS values (105,'G.Mason','L5', 'mason', 4);


insert into CLASSES values (100, 101,'Math');
insert into CLASSES values (200, 102,'English');
insert into CLASSES values (300, 103,'History');
insert into CLASSES values (400, 104,'Spanish');
insert into CLASSES values (500, 105,'Science');


insert into ClassList values (100, 0418);
insert into ClassList values (100, 1234);
insert into ClassList values (102, 0418);
insert into ClassList values (103, 1234);
insert into ClassList values (104, 0671);
insert into ClassList values (102, 1234);
insert into ClassList values (101, 3726);
insert into ClassList values (105, 4829);

insert into Integrity values (ClassList, 4);
insert into Integrity values (CLASSES, 5);
insert into Integrity values (TEACHERS, 5);
insert into Integrity values (STUDENTS, 4);

