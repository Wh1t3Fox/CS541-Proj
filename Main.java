import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {
	
	
	public static void main(String args[]) throws SQLException, ClassNotFoundException, UnsupportedEncodingException, NoSuchAlgorithmException {
	
		int input_sid = 0;
		String input_passwd = "", user_type = "";
		Connection conn;
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
		    props.setProperty("user", "");
		    props.setProperty("password", "");
		     
		    conn = DriverManager.getConnection(url,props);    	    
		    
		    
		    //insertData(conn);
		    
		    String sql ="SELECT s_name FROM Students WHERE s_id = ? AND s_pwrd = ?";
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
	
	public static void insertData(Connection conn){
		PreparedStatement preStatement;
		try{
			String createStudent = "CREATE TABLE Students("
										+ "s_id NUMBER(5), "
										+ "s_name VARCHAR(30), "
										+ "gpa DECIMAL(3,2), "
										+ "s_pwrd VARCHAR(64), "
										+ "integrity_value NUMBER (1), "
										+ "CONSTRAINT pk_students PRIMARY KEY(s_id), "
										+ "CONSTRAINT fk_students_integrity FOREIGN KEY(integrity_value) REFERENCES Integrity(integrity_value))";
			
			String createTeacher = "CREATE TABLE Teachers("
										+ "t_id NUMBER(5),"
										+ "t_name VARCHAR(30),"
										+ "office VARCHAR(12),"
										+ "t_pwrd VARCHAR(64),"
										+ "integrity_value NUMBER (1), "
										+ "CONSTRAINT pk_teachers PRIMARY KEY(t_id), "
										+ "CONSTRAINT fk_teachers_integrity FOREIGN KEY(integrity_value) REFERENCES Integrity(integrity_value))";
			
			String createClasList = "CREATE TABLE Classes("
										+ "c_id NUMBER(5),"
										+ "t_id NUMBER(5),"
										+ "subject VARCHAR(30),"
										+ "CONSTRAINT fk_classes FOREIGN KEY(t_id) REFERENCES Teachers(t_id))";
			
			String creatClasses = "CREATE TABLE ClassList("
										+ "c_id NUMBER(5),"
										+ "s_id NUMBER(5),"
										+ "CONSTRAINT pk_classlist PRIMARY KEY(c_id, s_id),"
										+ "CONSTRAINT fk_classlist_class FOREIGN KEY(c_id) REFERENCES Classes(c_id),"
										+ "CONSTRAINT fk_classlist_stud FOREIGN KEY(s_id) REFERENCES Students(s_id))";
			
			String createIntegrity = "CREATE TABLE Integrity("
										+ "table_name VARCHAR(10),"
										+ "integrity_value NUMBER(1), "
										+ "CONSTRAINT pk_integrity PRIMARY KEY(table_name))";
			
			String query = "INSERT INTO students "
							+ "(s_id, s_name, gpa, s_pwrd) "
							+ "VALUES (7,'Bill',3.50, '"+passHash("password")+"')";
					
			preStatement = conn.prepareStatement(query);
			preStatement.executeUpdate();
			
		}catch(SQLException se){
	    	se.printStackTrace();
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
}


