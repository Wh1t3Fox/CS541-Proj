[Projct Handout](https://drive.google.com/file/d/0Bx2RUcQJmFxnZjJtc3VVOGxUYU0/edit?usp=sharing)
<br />
[Class Slides](https://drive.google.com/folderview?id=0Bx2RUcQJmFxnZzdFeVZreldEN1U&usp=sharing)
<br/>
[Google Drive](https://drive.google.com/folderview?id=0Bx2RUcQJmFxnUlktQ1dRajJkdXc&usp=sharing)
<br/>


Biba Integrity Model Application

I. CONNECTING TO A DATABASE
II. COMPILING THE CODE
III. SWITCHING BIBA POLICY MODES
IV. INSERTING AND REMOVING DATA INTO/FROM DATABASE


I. CONNECTING TO A DATABASE

The following should be done in the main.java code
1. Enter username
2. Enter password,
3. Enter the database url 

II. COMPILING THE CODE

IDE

1. Import project
2. Add the ojdbc14.jar file to the library

COMMAND LINE

1. Type the command javac -cp /path/to/jar/file Main.java
and replace /path/to/jar/ with the path to the ojdbc jar file 

III. SWITCHING BIBA POLICY MODES

The application has the ability to run in the following modes: RING, WATERMARK, and STRICT. To switch between modes, the following code in the main.java program should be changed to the designated mode before executing. 

private static final String BibaMode = "RING"; //RING, WATERMARK, STRICT


IV. INSERTING AND REMOVING DATA INTO/FROM DATABASE

The removeData() and insertData() methods should remain uncommented for inserting and removing data from the database. They can be commented out as needed.


Objects
- Tables
- Indexes
- Sequences
- Views

###Strict Policy
- No Write Up
- No Read Down

###Low-Watermark Object
Allow writing to all object but if the integrity level is higher, then lower the integrity level to match the users.

###Ring Policy
Subjects can read all objects but only write to one with lower integrity than themselves
