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
	private static String input_sid, user_type;
	private static int integrityValue = 0;
	private static final String BibaMode = "Strict";
	
	public static void main(String args[]) throws SQLException, ClassNotFoundException, UnsupportedEncodingException, NoSuchAlgorithmException {
	
		String input_passwd = "";
		Properties props;
		final String url = "jdbc:oracle:thin:@claros.cs.purdue.edu:1524:strep";
		PreparedStatement preState;
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner( System.in );
	    
	    System.out.println("1.Student\n2.Teacher\nQ.Exit");
	    user_type = sc.next();
	    	    
	    switch(user_type){
	    	case "1":
	    		System.out.print("Enter student ID: ");
				input_sid = sc.next();
				System.out.print("Enter password: ");
				input_passwd = sc.next();
	    		break;
	    		
	    	case "2":
	    		System.out.print("Enter teacher ID: ");
				input_sid = sc.next();
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
		    
		    //removeData();
		    //insertData();
		    
		    String sql = "";
		    if(user_type.equals("1")){
		    	sql ="SELECT s_name, s_pwrd, integrity_value FROM Students WHERE s_id = ?";
		    }
		    else{
		    	sql ="SELECT t_name,t_pwrd,integrity_value FROM Teachers WHERE t_id = ?";
		    }
		    
        	preState = conn.prepareStatement(sql);
        	preState.setString(1, input_sid);
        
        	ResultSet result = preState.executeQuery();       	
        	
        	if(result.next()){
        		String name, passwd;
        		
        		name = result.getString(1);
        		passwd = result.getString(2);
        		integrityValue = result.getInt(3);
        		
        		
        		        		
        		if(passwd.equals(passHash(input_passwd))){
        			System.out.println("Hello, " + name);
        			authenticatedUser();
        		}
        	
        		
        	}
        	else{
        		System.out.println("Invalid Login!");
        		System.exit(0);
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
	
	public static void authenticatedUser(){
		String command, tableName;
		Scanner sc = new Scanner( System.in );
		
		while(true){
			String query = "";
			
			query += actionMenu();
			if(query.contains("INVALID")){
				System.out.println("INVALID QUERY");
				authenticatedUser();
			}
			query += tableMenu();
			if(query.contains("INVALID")){
				System.out.println("INVALID QUERY");
				authenticatedUser();
			}
						
			if(query.contains("SELECT")){
				System.out.println("Current Query: " + query);
				System.out.println("From where? (NONE for no WHERE): ");
				String predicate = sc.nextLine();
				
				if(!predicate.contains("NONE")){
					query += " WHERE ";
					query += predicate;
				}
			}else if(query.contains("INSERT")){
				System.out.println("Current Query: " + query);
				System.out.println("Into where? (Ex. s_id,s_name NONE for no attributes): ");
				String attributes = sc.nextLine();
				
				if(!attributes.contains("NONE")){
					query += " (";
					query += attributes;
					query += ") VALUES (";
					
					System.out.println("their values? (Ex. s_id = '1': ");
					String values = sc.nextLine();
					query += values;
					query += ")";
					
				}
				
			}else if(query.contains("UPDATE")){
				query += " SET ";
				System.out.println("Current Query: " + query);
				System.out.println("Set What?: ");
				String predicate = sc.nextLine();
				query += predicate;
				query += " WHERE ";
				System.out.println("Where what?: ");
				String where = sc.nextLine();
				query += where;
			}
			
			System.out.println("Final Query: " + query);
			//Select Statements aka Reading
			if(query.contains("SELECT")){
				//If using Ring Policy
				if(BibaMode.equals("Ring")){
					//Allow Reading to all Data
				
				//Strict and Watermark Policy
				}else{
					//Only allow reading with integrity = or >
					
				}
			//Insert and Update Statements aka Writing
			}else{
				//Watermark Policy
				if(BibaMode.equals("Watermark")){
					//Write to all but lower integrity if writing up
					
				//Strict and Ring policy
				}else{
					//Only allow writing down
					
				}
			}
			
		}
			
		
	}
	
	
	public static String actionMenu(){
		String command = "", tableAttribute = "";
		Scanner sc = new Scanner( System.in );
		System.out.println("Choose Your Action:\n1. Select\n2. Insert\n3. Update");
		command = sc.next();
		
		switch(command){
			case "1":
				System.out.print("What do you want to select? (Ex: *, s_name, t_name): ");
				tableAttribute = sc.next();
				return "SELECT " + tableAttribute + " FROM ";
			case "2":
				return "INSERT INTO ";
			case "3":
				return "UPDATE ";
			default:
				return "INVALID";
		}
	}
	
	public static String tableMenu(){
		String command = "";
		Scanner sc = new Scanner( System.in );
		System.out.println("Choose Your Action:\n1. Teachers\n2. Students\n3. Classes\n4. Classlist");
		command = sc.next();
		
		switch(command){
			case "1":
				return " TEACHERS ";
			case "2":
				return " STUDENTS ";
			case "3":
				return " CLASSES ";
			case "4":
				return " CLASSLIST ";
			default:
				return " INVALID ";
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
		try{
			
			Statement stmt = conn.createStatement();
			stmt.execute("CREATE TABLE Integrity("
					+ "identity VARCHAR(15),"
					+ "integrity_value NUMBER(1), "
					+ "CONSTRAINT pk_integrity PRIMARY KEY(identity))");
			
			stmt.execute("CREATE TABLE Students("
							+ "s_id VARCHAR(15), "
							+ "s_name VARCHAR(30), "
							+ "gpa DECIMAL(3,2), "
							+ "s_pwrd VARCHAR(64), "
							+ "integrity_value NUMBER (1), "
							+ "CONSTRAINT pk_students PRIMARY KEY(s_id), "
							+ "CONSTRAINT fk_students_integrity FOREIGN KEY(s_id) REFERENCES Integrity(identity))");
			
			
			stmt.execute("CREATE TABLE Teachers("
							+ "t_id VARCHAR(15),"
							+ "t_name VARCHAR(30),"
							+ "office VARCHAR(12),"
							+ "t_pwrd VARCHAR(64),"
							+ "integrity_value NUMBER (1), "
							+ "CONSTRAINT pk_teachers PRIMARY KEY(t_id), "
							+ "CONSTRAINT fk_teachers_integrity FOREIGN KEY(t_id) REFERENCES Integrity(identity))");
			
			stmt.execute("CREATE TABLE Classes("
							+ "c_id VARCHAR(15),"
							+ "t_id VARCHAR(15),"
							+ "subject VARCHAR(30),"
							+ "CONSTRAINT pk_classid PRIMARY KEY(c_id),"
							+ "CONSTRAINT fk_classes FOREIGN KEY(t_id) REFERENCES Teachers(t_id))");
			
			stmt.execute("CREATE TABLE ClassList("
							+ "c_id VARCHAR(15),"
							+ "s_id VARCHAR(15),"
							+ "CONSTRAINT pk_classlist PRIMARY KEY(c_id, s_id))");
						
			stmt.execute("insert into Integrity values ('ClassList', 4)");
			stmt.execute("insert into Integrity values ('CLASSES', 5)");
			stmt.execute("insert into Integrity values ('TEACHERS', 5)");
			stmt.execute("insert into Integrity values ('STUDENTS', 4)");
			stmt.execute("insert into Integrity values ('0418', 5)");
			stmt.execute("insert into Integrity values ('0671', 5)");
			stmt.execute("insert into Integrity values ('1234', 5)");
			stmt.execute("insert into Integrity values ('3726', 5)");
			stmt.execute("insert into Integrity values ('4829', 5)");
			stmt.execute("insert into Integrity values ('101', 5)");
			stmt.execute("insert into Integrity values ('102', 5)");
			stmt.execute("insert into Integrity values ('103', 5)");
			stmt.execute("insert into Integrity values ('104', 5)");
			stmt.execute("insert into Integrity values ('105', 5)");
			
			
			stmt.execute("insert into STUDENTS values ('0418','S.Jack',3.5,'"+passHash("jack")+"', 5)");
			stmt.execute("insert into STUDENTS values ('0671','A.Smith',2.9,'"+passHash("smith")+"', 5)");
			stmt.execute("insert into STUDENTS values ('1234','T.Banks',4.0,'"+passHash("banks")+"', 5)");
			stmt.execute("insert into STUDENTS values ('3726','M.Lee',3.2,'"+passHash("lee")+"', 5)");
			stmt.execute("insert into STUDENTS values ('4829','J.Bale',3.0,'"+passHash("bale")+"', 5)");

			stmt.execute("insert into TEACHERS values ('101','S.Layton','L1', '"+passHash("layton")+"', 4)");
			stmt.execute("insert into TEACHERS values ('102','B.Jungles','L2', '"+passHash("jungles")+"', 4)");
			stmt.execute("insert into TEACHERS values ('103','N.Guzaldo','L3', '"+passHash("guzaldo")+"', 4)");
			stmt.execute("insert into TEACHERS values ('104','S.Boling','L4', '"+passHash("boling")+"', 4)");
			stmt.execute("insert into TEACHERS values ('105','G.Mason','L5', '"+passHash("mason")+"', 4)");


			stmt.execute("insert into CLASSES values ('M100', '101','Math')");
			stmt.execute("insert into CLASSES values ('E200', '102','English')");
			stmt.execute("insert into CLASSES values ('H300', '103','History')");
			stmt.execute("insert into CLASSES values ('ES400', '104','Spanish')");
			stmt.execute("insert into CLASSES values ('SC500', '105','Science')");
			
			stmt.execute("insert into ClassList values ('M100', '0418')");
			stmt.execute("insert into ClassList values ('M100', '1234')");
			stmt.execute("insert into ClassList values ('E200', '0418')");
			stmt.execute("insert into ClassList values ('H300', '1234')");
			stmt.execute("insert into ClassList values ('H300', '0671')");
			stmt.execute("insert into ClassList values ('E200', '1234')");
			stmt.execute("insert into ClassList values ('SC500', '3726')");
			stmt.execute("insert into ClassList values ('SC500', '4829')");
		
		}catch(Exception e){
	    	System.out.println("Data  already exists");
	    }
	}
	
	public static void removeData(){
		
		try{
			Statement stmt = conn.createStatement();
			
			stmt.executeQuery("Drop table INTEGRITY cascade constraints");
			stmt.executeQuery("Drop table STUDENTS cascade constraints");
			stmt.executeQuery("Drop table TEACHERS cascade constraints");
			stmt.executeQuery("Drop table CLASSES cascade constraints");
			stmt.executeQuery("Drop table ClassList cascade constraints");
		
		}catch(Exception e){
	    	System.out.println("No Data to Remove");
	    }
	}
}


