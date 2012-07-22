package emergency.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetUserkey")
public class GetUserkey extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String host=null,database=null,username=null,password=null;
	int port=0;
	
    public void init() {       
        	host="localhost";
        	port=3306;
        	database="emergencydb";
        	username="user";
        	password="asdf";
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out=response.getWriter();
		if(DatabaseInteraction.connected==false || DatabaseInteraction.con==null || DatabaseInteraction.stat==null){
			DatabaseInteraction.connect(host, port, database, username, password); //trying to connect.
			if(DatabaseInteraction.connected==false){
				out.print("Error connecting to the Database. Please try again later.");
				return;
			}
		}
		
		String aadhaarid=request.getParameter("aadhaarid");
		ResultSet userkey=DatabaseInteraction.query("select userkey from euser where aadhaarid='"+aadhaarid+"'");
		try {
			if(userkey==null || userkey.isAfterLast()){
				out.print("Error in retrieving the userkey. Please try again later.");
				System.out.println("error in retrieving");
				return;
			}
			userkey.next();
			System.out.print(userkey.getString("userkey"));
			out.print(userkey.getString("userkey"));
		}
		catch(SQLException e){
			out.print("Error in retrieving the service types. Please try again later.");
			e.printStackTrace();
		}
	}
}
