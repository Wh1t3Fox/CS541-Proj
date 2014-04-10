import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {
	
	private static Connection conn;
	
	public static void main(String args[]) throws SQLException, ClassNotFoundException, UnsupportedEncodingException, NoSuchAlgorithmException {
	
		int input_sid = 0;
		String input_passwd = "", user_type = "";
		Properties props;
		final String url = "jdbc:oracle:thin:@claros.cs.purdue.edu:1524:strep";
		PreparedStatement preState;
		
		Scanner sc = new Scanner( System.in );
	    
	    System.out.println("1.Student\n2.Teacher\nQ.Exit");
	    user_type = sc.next();
	    	    
	    switch(user_type){
	    	case "1":
	    		System.out.print("Enter student ID: ");
				input_sid = sc.nextInt();
				System.out.print("Enter password: ");
				input_passwd = sc.next();
	    		break;
	    		
	    	case "2":
	    		System.out.print("Enter teacher ID: ");
				input_sid = sc.nextInt();
				System.out.print("Enter password: ");
				input_passwd = sc.next();
	    		break;
	    		
	    	case "Q":
	    	case "q":
	    		System.exit(0);
	    		break;
	    		
	    	default:
	    		System.out.println("Invalid Choice");
	    		System.exit(0);
	    		break;
	    }
	    try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			props = new Properties();
		    props.setProperty("user", "west1");
		    props.setProperty("password", "iIXeLSkV");
		     
		    conn = DriverManager.getConnection(url,props);    	    
		    
		    //removeData();
		    insertData();
		    /*
		    String sql ="SELECT s_name FROM Students WHERE s_id = ? AND s_pwrd = ? AND ROWNUM < 2";
        	preState = conn.prepareStatement(sql);
        	preState.setInt(1, input_sid);
        	preState.setString(2, passHash(input_passwd));
        
        	ResultSet result = preState.executeQuery();       	
        	
        	if(result.next()){
        		System.out.println("Logged in!");
        	}
        	else{
        		System.out.println("Invalid Login!");
        	}
        	
        	result.close();
        	
        	preState.close();
        	*/
        	conn.close();
	    }catch(SQLException se){
	    	se.printStackTrace();
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	public static String passHash(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		byte[] hash_bytes = digest.digest(password.getBytes("UTF-8"));
		
		StringBuilder hash = new StringBuilder();
		
		for(int i=0; i<hash_bytes.length; i++){
			hash.append(Integer.toString((hash_bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		return hash.toString();
	}
	
	public static void insertData(){
		Statement stmt = null;
		try{
			String createStudent = "CREATE TABLE Students("
										+ "s_id VARCHAR(15), "
										+ "s_name VARCHAR(30), "
										+ "gpa DECIMAL(3,2), "
										+ "s_pwrd VARCHAR(64), "
										+ "integrity_value NUMBER (1), "
										+ "CONSTRAINT pk_students PRIMARY KEY(s_id), "
										+ "CONSTRAINT fk_students_integrity FOREIGN KEY(s_id) REFERENCES Integrity(identity))";
			
			
			String createTeacher = "CREATE TABLE Teachers("
										+ "t_id VARCHAR(15),"
										+ "t_name VARCHAR(30),"
										+ "office VARCHAR(12),"
										+ "t_pwrd VARCHAR(64),"
										+ "integrity_value NUMBER (1), "
										+ "CONSTRAINT pk_teachers PRIMARY KEY(t_id), "
										+ "CONSTRAINT fk_teachers_integrity FOREIGN KEY(t_id) REFERENCES Integrity(identity))";
			
			
			String createClasList = "CREATE TABLE Classes("
										+ "c_id VARCHAR(15),"
										+ "t_id VARCHAR(15),"
										+ "subject VARCHAR(30),"
										+ "CONSTRAINT pk_classid PRIMARY KEY(c_id),"
										+ "CONSTRAINT fk_classes FOREIGN KEY(t_id) REFERENCES Teachers(t_id))";
			
			
			String creatClasses = "CREATE TABLE ClassList("
										+ "c_id VARCHAR(15),"
										+ "s_id VARCHAR(15),"
										+ "CONSTRAINT pk_classlist PRIMARY KEY(c_id, s_id))";
			
			
			String createIntegrity = "CREATE TABLE Integrity("
										+ "identity VARCHAR(15),"
										+ "integrity_value NUMBER(1), "
										+ "CONSTRAINT pk_integrity PRIMARY KEY(identity))";
			
			
			String insert  = "insert into STUDENTS values (0418,'S.Jack',3.5,'"+passHash("jack")+"', 5)";
			String insert1 = "insert into STUDENTS values (0671,'A.Smith',2.9,'"+passHash("smith")+"', 5)";
			String insert2 = "insert into STUDENTS values (1234,'T.Banks',4.0,'"+passHash("banks")+"', 5)";
			String insert3 = "insert into STUDENTS values (3726,'M.Lee',3.2,'"+passHash("lee")+"', 5)";
			String insert4 = "insert into STUDENTS values (4829,'J.Bale',3.0,'"+passHash("bale")+"', 5)";

			String insert5 = "insert into TEACHERS values (101,'S.Layton','L1', '"+passHash("layton")+"', 4)";
			String insert6 = "insert into TEACHERS values (102,'B.Jungles','L2', '"+passHash("jungles")+"', 4)";
			String insert7 = "insert into TEACHERS values (103,'N.Guzaldo','L3', '"+passHash("guzaldo")+"', 4)";
			String insert8 = "insert into TEACHERS values (104,'S.Boling','L4', '"+passHash("boling")+"', 4)";
			String insert9 = "insert into TEACHERS values (105,'G.Mason','L5', '"+passHash("mason")+"', 4)";


			String insert10 = "insert into CLASSES values (100, 101,'Math')";
			String insert11 = "insert into CLASSES values (200, 102,'English')";
			String insert12 = "insert into CLASSES values (300, 103,'History')";
			String insert13 = "insert into CLASSES values (400, 104,'Spanish')";
			String insert14 = "insert into CLASSES values (500, 105,'Science')";


			stmt = conn.createStatement();

			String insert15 = "insert into ClassList values (100, 0418)";
			String insert16 = "insert into ClassList values (100, 1234)";
			String insert17 = "insert into ClassList values (102, 0418)";
			String insert18 = "insert into ClassList values (103, 1234)";
			String insert19 = "insert into ClassList values (104, 0671)";
			String insert20 = "insert into ClassList values (102, 1234)";
			String insert21 = "insert into ClassList values (101, 3726)";
			String insert22 = "insert into ClassList values (105, 4829)";

			
			

			stmt.execute(createIntegrity);
			stmt.execute(createStudent);
			stmt.execute(createTeacher);
			stmt.execute(createClasList);
			stmt.execute(creatClasses);
			
			stmt.executeQuery("insert into Integrity values ('ClassList', 4)");
			stmt.executeQuery("insert into Integrity values ('CLASSES', 5)");
			stmt.executeQuery("insert into Integrity values ('TEACHERS', 5)");
			stmt.executeQuery("insert into Integrity values ('STUDENTS', 4)");
			stmt.executeQuery("insert into Integrity values (0418, 5)");
			stmt.executeQuery("insert into Integrity values (0671, 5)");
			stmt.executeQuery("insert into Integrity values (1234, 5)");
			stmt.executeQuery("insert into Integrity values (3726, 5)");
			stmt.executeQuery("insert into Integrity values (4829, 5)");
			stmt.executeQuery("insert into Integrity values (101, 5)");
			stmt.executeQuery("insert into Integrity values (102, 5)");
			stmt.executeQuery("insert into Integrity values (103, 5)");
			stmt.executeQuery("insert into Integrity values (104, 5)");
			stmt.executeQuery("insert into Integrity values (105, 5)");
			

			stmt.executeQuery(insert);
			stmt.executeQuery(insert2);
			stmt.executeQuery(insert3);
			stmt.executeQuery(insert4);
			stmt.executeQuery(insert5);
			stmt.executeQuery(insert6);
			stmt.executeQuery(insert7);
			stmt.executeQuery(insert8);
			stmt.executeQuery(insert9);
			stmt.executeQuery(insert10);
			stmt.executeQuery(insert11);
			stmt.executeQuery(insert12);
			stmt.executeQuery(insert13);
			stmt.executeQuery(insert14);
			stmt.executeQuery(insert15);
			stmt.executeQuery(insert16);
			stmt.executeQuery(insert17);
			stmt.executeQuery(insert18);
			stmt.executeQuery(insert19);
			stmt.executeQuery(insert20);
			stmt.executeQuery(insert21);
			stmt.executeQuery(insert22);
		
		}catch(SQLException se){
	    	se.printStackTrace();
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	public static void removeData(){
		Statement stmt = null;
		
		try{
			String insert4 = "Drop table INTEGRITY cascade constraints";
			String insert = "Drop table STUDENTS cascade constraints";
			String insert1 = "Drop table TEACHERS cascade constraints";
			String insert2 = "Drop table CLASSES cascade constraints";
			String insert3 = "Drop table ClassList cascade constraints";
			
			stmt = conn.createStatement();
			stmt.executeQuery(insert4);
			stmt.executeQuery(insert);
			stmt.executeQuery(insert2);
			stmt.executeQuery(insert3);
		
		}catch(SQLException se){
	    	se.printStackTrace();
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
}


