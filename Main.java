import java.io.IOException;
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
	private static final String BibaMode = "STRICT"; //RING, WATERMARK, STRICT
	private static final String username = "";
	private static final String password = "";
	
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
		    props.setProperty("user", username);
		    props.setProperty("password", password);
		     
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
	
	public static void authenticatedUser() throws IOException, NoSuchAlgorithmException{
		String command = "", tableName;
		int objectIntegrity = 0;
		PreparedStatement preState;
		Scanner sc = new Scanner( System.in );
		
		while(true){
			clearConsole();
			String query = "";
			
			query += actionMenu();
			if(query.contains("INVALID")){
				System.out.println("INVALID QUERY");
				authenticatedUser();
			}
			
			clearConsole();
			
			tableName = tableMenu();
			if(query.contains("INVALID")){
				System.out.println("INVALID QUERY");
				authenticatedUser();
			}
			query += tableName;
			clearConsole();
			
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
				query += " VALUES (";
				System.out.println("Values of attributes: (Ex. '1','b',3: ");
				String values = sc.nextLine();
				query += values;
				query += ")";
			}else if(query.contains("UPDATE")){
				query += " SET ";
				System.out.println("Current Query: " + query);
				System.out.println("Set What? (Ex. s_name = 'b'): ");
				String predicate = sc.nextLine();
				query += predicate;
				query += " WHERE ";
				System.out.println("Where what? (Ex. s_id = '1': ");
				String where = sc.nextLine();
				query += where;
			}
			clearConsole();
			System.out.println("Final Query: " + query);
			String[] aQuery = query.split("\\s+");
			try {
				String integrtiyQuery = "SELECT integrity_value FROM INTEGRITY WHERE identity = ?";
				PreparedStatement getInteg = conn.prepareStatement(integrtiyQuery);
				getInteg = conn.prepareStatement(integrtiyQuery);
				getInteg.setString(1, tableName.replaceAll("\\s+",""));
	        
	        	ResultSet integrityResult = getInteg.executeQuery();
				if(integrityResult.next()){
					objectIntegrity = integrityResult.getInt("integrity_value");
				}
				getInteg.close();
				
				
	        	//Select Statements aka Reading
				if(query.contains("SELECT")){
					//Strict and Watermark Policy
					if(!BibaMode.equalsIgnoreCase("RING")){
						//if we are in watermark or stric don't allow reading down
						if(objectIntegrity < integrityValue){
							System.out.println("You do not have permission to execute this.");
							authenticatedUser();
						}
					}
					//Ring can read anything
					
				//Insert and Update Statements aka Writing
				}else{
					
					//Watermark Policy
					if(BibaMode.equalsIgnoreCase("WATERMARK")){
						//Write to all but lower integrity if writing up
						if(objectIntegrity >= integrityValue){
							String updateIntegrityLevel = "UPDATE INTEGRITY SET integrity_value = ? WHERE identity = ?";
							PreparedStatement updateInteg = conn.prepareStatement(updateIntegrityLevel);
							updateInteg.setInt(1, integrityValue);
							updateInteg.setString(2, tableName.replaceAll("\\s+",""));
							updateInteg.executeUpdate();
							updateInteg.close();
						}
						
						//Strict and Ring policy
					}else{
						//if writing up don't allow and exit on strict and ring
						if(objectIntegrity > integrityValue){
							System.out.println("You do not have permission to execute this.");
							authenticatedUser();
						}
					}
					//Add entry to integrity table before inserting in students or teachers
					if(query.contains("INSERT") && (query.contains("STUDENTS") || tableName.contains("TEACHERS"))){
						String[] parse = query.split(",");
						String id = parse[0].split("\\s+")[4].replace("(", "").replace("\'", "");
						int integrity = Integer.parseInt(parse[parse.length-1].replace(")", "").replace(" ", ""));
												
						String insertIntegrity = "INSERT INTO Integrity VALUES (?,?)";
						PreparedStatement addInteg = conn.prepareStatement(insertIntegrity);
						addInteg.setString(1, id);
						addInteg.setInt(2, integrity);
						addInteg.executeUpdate();
						addInteg.close();
					}
				}
				clearConsole();
			
				
				
	        	if(query.contains("SELECT")){
	        		String[] attributes = new String[5];
	        		preState = conn.prepareStatement(query);
	        		ResultSet result = preState.executeQuery();
	        		if(!aQuery[1].contains("*")){
	        			attributes = aQuery[1].split(",");
	        		}else{
	        			if(query.contains("STUDENTS")){
	        				attributes = new String[]{"s_id","s_name","gpa","s_pwrd","integrity_value"};
	        			}else if(query.contains("TEACHERS")){
	        				attributes = new String[]{"t_id","t_name","office","t_pwrd","integrity_value"};
	        			}else if(query.contains("CLASSES")){
	        				attributes = new String[]{"c_id","t_id","subject"};
	        			}else if(query.contains("CLASSLIST")){
	        				attributes = new String[]{"c_id","s_id"};
	        			}
	        		}
	        		
	        		for(String a: attributes){
	        			System.out.print(a);
	        			System.out.print("\t\t");
	        		}
	        		System.out.print("\n");
	        		for(int i=0; i<attributes.length;i++){
	        			for(int j=0; j<attributes[i].length(); j++)
	        				System.out.print("-");
	        			System.out.print("\t\t");
	        		}
	        		System.out.print("\n");
	        		
	        		while(result.next()){
	        			for(int j=1;j<=attributes.length; j++){
	        				System.out.print(result.getString(j));
	        				System.out.print("\t\t");
	        			}
	        			System.out.print("\n");
	        				
	        		}
	        		preState.close();
	        	}else{
	        		if(query.contains("INSERT")){
	        			PreparedStatement insertStatement = null;
	        			String newQuery = "";
	        			
	        			String[] values = query.substring(query.indexOf("(")).replace("(", "").replace(")","").replace("\'", "").replace("\\s+", "").split(",");
	        			
	        			if(query.contains("STUDENTS")){
	        				newQuery = "INSERT INTO STUDENTS VALUES (?,?,?,?,?)";
	        				insertStatement= conn.prepareStatement(newQuery);
	        				insertStatement.setString(1, values[0]);
	        				insertStatement.setString(2, values[1]);
	        				insertStatement.setFloat(3, Float.valueOf(values[2]));
	        				insertStatement.setString(4, passHash(values[3]));
	        				insertStatement.setInt(5, Integer.parseInt(values[4]));
	        			}else if(query.contains("TEACHERS")){
	        				newQuery = "INSERT INTO TEACHERS VALUES (?,?,?,?,?)";
	        				insertStatement= conn.prepareStatement(newQuery);
	        				insertStatement.setString(1, values[0]);
	        				insertStatement.setString(2, values[1]);
	        				insertStatement.setString(3, values[2]);
	        				insertStatement.setString(4, passHash(values[3]));
	        				insertStatement.setInt(5, Integer.parseInt(values[4]));
	        			}else if(query.contains("CLASSES")){
	        				newQuery = "INSERT INTO CLASSES VALUES (?,?,?)";
	        				insertStatement= conn.prepareStatement(newQuery);
	        				insertStatement.setString(1, values[0]);
	        				insertStatement.setString(2, values[1]);
	        				insertStatement.setString(3, values[2]);
	        			}else if(query.contains("CLASSLIST")){
	        				newQuery = "INSERT INTO CLASSLIST VALUES (?,?)";
	        				insertStatement= conn.prepareStatement(newQuery);
	        				insertStatement.setString(1, values[0]);
	        				insertStatement.setString(2, values[1]);
	        			}
	        			
	        			insertStatement.executeUpdate();
	        		}else{
	        			PreparedStatement updateStatement = null;
	        			String nQuery = "UPDATE " + tableName.replaceAll("\\s+", "") + " SET ";
	        			String[] aTmp = query.substring(21).replaceAll("[\\s+|\"|\']", "").split("WHERE|,");
	        			String[] updateItems  = new String[aTmp.length];
	        			String[] updateValues = new String[aTmp.length];
	        			
	        			for(int i=0; i<aTmp.length; i++){
	        				String[] values = aTmp[i].split("=");
	        				updateItems[i] = values[0];
	        				updateValues[i] = values[1];
	        			}
	        			for(int i=0; i<aTmp.length-1; i++){
	        				
	        				String[] values = aTmp[i].split("=");

	        				nQuery += values[0] + " = ?, ";
	        				
	        			}
	        			nQuery = nQuery.substring(0, nQuery.length()-2);
	        			nQuery += " WHERE " + aTmp[aTmp.length-1].split("=")[0] + " = ?";
	        				        			
	        			updateStatement = conn.prepareStatement(nQuery);
	        			for(int j=1; j<=updateItems.length; j++){
	        				if(updateItems[j-1].equalsIgnoreCase("GPA")){
	        					updateStatement.setFloat(j, Float.valueOf(updateValues[j-1]));
	        				}else if(updateItems[j-1].equalsIgnoreCase("integrity_value")){
	        					updateStatement.setInt(j, Integer.parseInt(updateValues[j-1]));
	        				}else if(updateItems[j-1].contains("pwrd")){
	        					updateStatement.setString(j, passHash(updateValues[j-1]));
	        				}else{
	        					updateStatement.setString(j, updateValues[j-1]);
	        				}
	        			}
	        			updateStatement.executeUpdate();
	        			
	        		}
	        		System.out.println("SUCCESSFUL QUERY");
	           	}
	        	
			} catch (SQLException e) {
				System.out.println("INCORRECT QUERY");
				
				if(query.contains("INSERT")){
					String id = query.split(",")[0].split("\\s+")[4].replace("(", "").replace("\'", "");
					String insertIntegrity = "DELETE FROM Integrity WHERE identity = ?";
					PreparedStatement delInteg;
					try {
						delInteg = conn.prepareStatement(insertIntegrity);
						delInteg.setString(1, id);
						delInteg.executeUpdate();
						delInteg.close();
					
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}finally{
				System.out.println("Press any key to continue...");
				System.in.read();
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
						
			stmt.execute("insert into Integrity values ('ClassList', 5)");
			stmt.execute("insert into Integrity values ('CLASSES', 4)");
			stmt.execute("insert into Integrity values ('TEACHERS', 5)");
			stmt.execute("insert into Integrity values ('STUDENTS', 3)");
			stmt.execute("insert into Integrity values ('0418', 3)");
			stmt.execute("insert into Integrity values ('0671', 3)");
			stmt.execute("insert into Integrity values ('1234', 3)");
			stmt.execute("insert into Integrity values ('3726', 3)");
			stmt.execute("insert into Integrity values ('4829', 3)");
			stmt.execute("insert into Integrity values ('101', 5)");
			stmt.execute("insert into Integrity values ('102', 5)");
			stmt.execute("insert into Integrity values ('103', 5)");
			stmt.execute("insert into Integrity values ('104', 5)");
			stmt.execute("insert into Integrity values ('105', 5)");
			
			
			stmt.execute("insert into STUDENTS values ('0418','S.Jack',3.5,'"+passHash("jack")+"', 3)");
			stmt.execute("insert into STUDENTS values ('0671','A.Smith',2.9,'"+passHash("smith")+"', 3)");
			stmt.execute("insert into STUDENTS values ('1234','T.Banks',4.0,'"+passHash("banks")+"', 3)");
			stmt.execute("insert into STUDENTS values ('3726','M.Lee',3.2,'"+passHash("lee")+"', 3)");
			stmt.execute("insert into STUDENTS values ('4829','J.Bale',3.0,'"+passHash("bale")+"', 3)");

			stmt.execute("insert into TEACHERS values ('101','S.Layton','L1', '"+passHash("layton")+"', 5)");
			stmt.execute("insert into TEACHERS values ('102','B.Jungles','L2', '"+passHash("jungles")+"', 5)");
			stmt.execute("insert into TEACHERS values ('103','N.Guzaldo','L3', '"+passHash("guzaldo")+"', 5)");
			stmt.execute("insert into TEACHERS values ('104','S.Boling','L4', '"+passHash("boling")+"', 5)");
			stmt.execute("insert into TEACHERS values ('105','G.Mason','L5', '"+passHash("mason")+"', 5)");


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
			e.printStackTrace();
	    	//System.out.println("Data  already exists");
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
			e.printStackTrace();
	    	//System.out.println("No Data to Remove");
	    }
	}
	
	public final static void clearConsole()
	{
	    try
	    {
	        final String os = System.getProperty("os.name");

	        if (os.contains("Windows"))
	        {
	            Runtime.getRuntime().exec("cls");
	        }
	        else
	        {
	            Runtime.getRuntime().exec("clear");
	        }
	    }
	    catch (final Exception e)
	    {
	        //  Handle any exceptions.
	    }
	}
}


