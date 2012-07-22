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


import net.sf.json.JSONObject;

@WebServlet("/ServiceTypeChoice")
public class ServiceTypeChoice extends HttpServlet {
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
				return; //exit from here
			}
		}
    	
    	ResultSet servicetypes=DatabaseInteraction.query("select * from sertype");
    	JSONObject reply=new JSONObject();
    	try {
			if(servicetypes==null || servicetypes.isAfterLast()){
				out.print("Error in retrieving the service types. Please try again later.");
				System.out.println("error in retrieving");
				return;
			}
			while(servicetypes.next()){
				reply.put(servicetypes.getString("typeid"), servicetypes.getString("typename"));
				System.out.print(servicetypes.getString("typeid"));
				System.out.print(":");
				System.out.println(servicetypes.getString("typename"));
			}
			out.print(reply.toString());
		}
		catch(SQLException e){
			out.print("Error in retrieving the service types. Please try again later.");
			e.printStackTrace();
		}
	}
}