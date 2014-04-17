Drop table INTEGRITY cascade constraints;
Drop table STUDENTS cascade constraints;
Drop table TEACHERS cascade constraints;
Drop table CLASSES cascade constraints;
Drop table ClassList cascade constraints;

CREATE TABLE Integrity(
    identity VARCHAR(15),
	integrity_value NUMBER(1),
	CONSTRAINT pk_integrity PRIMARY KEY(identity));
				
CREATE TABLE Students(s_id VARCHAR(15),
	s_name VARCHAR(30),
	gpa DECIMAL(3,2),
	s_pwrd VARCHAR(64),
	integrity_value NUMBER (1),
	CONSTRAINT pk_students PRIMARY KEY(s_id),
	CONSTRAINT fk_students_integrity FOREIGN KEY(s_id) REFERENCES Integrity(identity));
			
			
CREATE TABLE Teachers(
    t_id VARCHAR(15),
	t_name VARCHAR(30),
	office VARCHAR(12),
	t_pwrd VARCHAR(64),
	integrity_value NUMBER (1), 
	CONSTRAINT pk_teachers PRIMARY KEY(t_id), 
	CONSTRAINT fk_teachers_integrity FOREIGN KEY(t_id) REFERENCES Integrity(identity));
			
CREATE TABLE Classes(
    c_id VARCHAR(15),
	t_id VARCHAR(15),
	subject VARCHAR(30),
	CONSTRAINT pk_classid PRIMARY KEY(c_id),
	CONSTRAINT fk_classes FOREIGN KEY(t_id) REFERENCES Teachers(t_id));
			
CREATE TABLE ClassList(
    c_id VARCHAR(15),
	s_id VARCHAR(15),
	CONSTRAINT pk_classlist PRIMARY KEY(c_id, s_id));
						
insert into Integrity values ('ClassList', 5);
insert into Integrity values ('CLASSES', 4);
insert into Integrity values ('TEACHERS', 5);
insert into Integrity values ('STUDENTS', 3);
insert into Integrity values ('0418', 3);
insert into Integrity values ('0671', 3);
insert into Integrity values ('1234', 3);
insert into Integrity values ('3726', 3);
insert into Integrity values ('4829', 3);
insert into Integrity values ('101', 5);
insert into Integrity values ('102', 5);
insert into Integrity values ('103', 5);
insert into Integrity values ('104', 5);
insert into Integrity values ('105', 5);
			
insert into STUDENTS values ('0418','S.Jack',3.5,'"+passHash("jack")+"', 3);
insert into STUDENTS values ('0671','A.Smith',2.9,'"+passHash("smith")+"', 3);
insert into STUDENTS values ('1234','T.Banks',4.0,'"+passHash("banks")+"', 3);
insert into STUDENTS values ('3726','M.Lee',3.2,'"+passHash("lee")+"', 3);
insert into STUDENTS values ('4829','J.Bale',3.0,'"+passHash("bale")+"', 3);

insert into TEACHERS values ('101','S.Layton','L1', '"+passHash("layton")+"', 5);
insert into TEACHERS values ('102','B.Jungles','L2', '"+passHash("jungles")+"', 5);
insert into TEACHERS values ('103','N.Guzaldo','L3', '"+passHash("guzaldo")+"', 5);
insert into TEACHERS values ('104','S.Boling','L4', '"+passHash("boling")+"', 5);
insert into TEACHERS values ('105','G.Mason','L5', '"+passHash("mason")+"', 5);

insert into CLASSES values ('M100', '101','Math');
insert into CLASSES values ('E200', '102','English');
insert into CLASSES values ('H300', '103','History');
insert into CLASSES values ('ES400', '104','Spanish');
insert into CLASSES values ('SC500', '105','Science');
			
insert into ClassList values ('M100', '0418');
insert into ClassList values ('M100', '1234');
insert into ClassList values ('E200', '0418');
insert into ClassList values ('H300', '1234');
insert into ClassList values ('H300', '0671');
insert into ClassList values ('E200', '1234');
insert into ClassList values ('SC500', '3726');
insert into ClassList values ('SC500', '4829');
