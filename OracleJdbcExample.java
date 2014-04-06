import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Simple Java Program to connect Oracle database by using Oracle JDBC thin driver
 * Make sure you have Oracle JDBC thin driver in your classpath before running this program
 * @author
 */
public class OracleJdbcExample {

    public static void main(String args[]) throws SQLException, ClassNotFoundException {

        Class.forName("oracle.jdbc.driver.OracleDriver");

        //URL of Oracle database server
        String url = "jdbc:oracle:thin:@claros.cs.purdue.edu:1524:strep"; 
      
        //properties for creating connection to Oracle database
        Properties props = new Properties();
        //Put your username without the @csora
        props.setProperty("user", "USERNAME_HERE");
        props.setProperty("password", "PASSWORD_HERER");
      
        //creating connection to Oracle database using JDBC
        Connection conn = DriverManager.getConnection(url,props);

        String sql ="select sysdate as current_day from dual";

        //creating PreparedStatement object to execute query
        PreparedStatement preStatement = conn.prepareStatement(sql);
    
        ResultSet result = preStatement.executeQuery();
      
        while(result.next()){
            System.out.println("Current Date from Oracle : " +         result.getString("current_day"));
        }
        System.out.println("done");
      
    }
}
