import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class Main {
	
	private static int input_sid;
	private static String input_passwd, user_type;
	private static Scanner sc;
	private static Connection conn;
	private static Properties props;
	private static final String url = "jdbc:oracle:thin:@claros.cs.purdue.edu:1524:strep";;
	private static PreparedStatement preState;
	
	public static void main(String args[]) throws SQLException, ClassNotFoundException {
	
		sc = new Scanner( System.in );
	    
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
	    		break;
	    }
	    try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			props = new Properties();
		    props.setProperty("user", "USERNAME");
		    props.setProperty("password", "PASSWORD");
		     
		    conn = DriverManager.getConnection(url,props);
		    
		    /*
	        String insert = "INSERT INTO students (s_id, s_name, gpa, s_pwrd) VALUES (?,?,?,?)";
	        PreparedStatement preStatement = conn.prepareStatement(insert);
	        preStatement.setInt(1, 6);
	        preStatement.setString(2, "James");
	        preStatement.setFloat(3, (float) 2.34);
	        preStatement.setString(4, "abc");
	        preStatement.executeUpdate();
	       	*/	    
		    
	    	String sql ="SELECT * FROM Students WHERE s_id = ? AND s_pwrd = ?";
        	preState = conn.prepareStatement(sql);
        	preState.setInt(1, input_sid);
        	preState.setString(2, input_passwd);
        
        	ResultSet result = preState.executeQuery();
        	boolean success = false;       	
        	
        	while(result.next()){
        		//System.out.println(result.getFetchSize());
        		//System.out.println(result.getString("s_name") + " : " + result.getString("s_pwrd"));
        		if(result.getString("s_name") != null){
        			success = true;
        			System.out.println("Logged in!");
        		}
        	}
        	
        	if(!success){
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

}
