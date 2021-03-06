package emergency.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UserLocationUpdater")
public class UserLocationUpdater extends HttpServlet {
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
    	
    	String aadhaarid=request.getParameter("aadhaarid");
    	String latitude=request.getParameter("latitude");
    	String longitude=request.getParameter("longitude");
		
    	int number_of_rows=DatabaseInteraction.update("update euser set latitude='"+latitude+"',longitude='"+longitude+"' where aadhaarid='"+aadhaarid+"'");
    	
    	if(number_of_rows<1){
			System.out.println("Error in inserting: "+number_of_rows);
			out.print("Error inserting location details into the Database. Please try again later.");
			return; //exit from here
		}
		else{
			System.out.println("Table Updated.");
			out.print("success");
		}
	}
}