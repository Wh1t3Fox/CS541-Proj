
rem Create Table zipcodes
CREATE TABLE zipcodes(
	zip 	NUMBER(5),
	city	VARCHAR(30),
	CONSTRAINT pk_zipcode PRIMARY KEY(zip)
);

rem Create Table employees
CREATE TABLE employees(
	eno	 	NUMBER(4),
	ename	VARCHAR(30),
	zip 	NUMBER(5),
	hdate	DATE,
	CONSTRAINT pk_employee PRIMARY KEY(eno),
	CONSTRAINT fk_employee FOREIGN KEY(zip) REFERENCES zipcodes(zip)
);

rem Create Table parts
CREATE TABLE parts(
	pno 	NUMBER(5),
	pname	VARCHAR(30),
	qoh 	INT,
	price	NUMBER(6, 2) NOT NULL,
	olevel	INT,
	CONSTRAINT pk_parts PRIMARY KEY(pno),
	CONSTRAINT check_qoh CHECK (qoh >= 0),
	CONSTRAINT check_price CHECK (price >= 0)
);

rem Create Table customers
CREATE TABLE customers(
	cno 	NUMBER(5),
	cname	VARCHAR(30),
	street	VARCHAR(30),
	zip 	NUMBER(5),
	phone 	CHAR(12),
	CONSTRAINT pk_customers PRIMARY KEY(cno),
	CONSTRAINT fk_customer_zip FOREIGN KEY(zip) REFERENCES zipcodes(zip)
);

rem Create Table orders
CREATE TABLE orders(
	ono 	NUMBER(5),
	cno 	NUMBER(5),
	eno 	NUMBER(4),
	received	DATE,
	shipped		DATE,
	CONSTRAINT pk_orders PRIMARY KEY(ono),
	CONSTRAINT fk_orders_cno FOREIGN KEY(cno) REFERENCES customers(cno),
	CONSTRAINT fk_orders_eno FOREIGN KEY(eno) REFERENCES employees(eno)
);

rem Create Table odetails
CREATE TABLE odetails(
	ono 	INT,
	pno 	INT,
	qty		INT,
	CONSTRAINT pk_odetails PRIMARY KEY(ono, pno),
	CONSTRAINT check_qty CHECK (qty > 0)
);

rem Insert Data
@data.sql
