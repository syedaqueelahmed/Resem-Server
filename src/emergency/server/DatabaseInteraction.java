package emergency.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInteraction {
	
	static Connection con=null;
	static Statement stat=null;
	static boolean connected=false;
	
	static void connect(String host, int port, String database, String username, String password) {
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con=DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database,username,password);
				System.out.println("Connected to Database");
			stat=con.createStatement();
				System.out.println("Statement Created");
			connected=true;
		}
		catch(InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e){
			e.printStackTrace();
			System.out.println("Error in connecting.");
			connected=false;
		}	
	}
	
	static ResultSet query(String statement){
		try {
			System.out.println("querying");
			return(stat.executeQuery(statement));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static int update(String statement){
		try {
			System.out.println("updating");
			return(stat.executeUpdate(statement));
		} catch (SQLException e) {
			e.printStackTrace();
			RegistrationServlet.errorresponse=null;
			if(e.getMessage().contains("a foreign key constraint fails")){
				RegistrationServlet.errorresponse="";
			}
			else if(e.getMessage().contains("Duplicate entry")){
				RegistrationServlet.errorresponse="Already Registered";
			}
			else{
				RegistrationServlet.errorresponse=null;
			}
			return -1;
		}
	}
}