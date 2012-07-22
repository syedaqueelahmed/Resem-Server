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

@WebServlet("/InsuranceChoice")
public class InsuranceChoice extends HttpServlet {
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
    	
    	ResultSet insurancefirms=DatabaseInteraction.query("select firmid, insurancefirm from insurancefirmdetails");
    	JSONObject reply=new JSONObject();
    	try {
			if(insurancefirms==null || insurancefirms.isAfterLast()){
				out.print("Error in retrieving the list of insurance firms. Please try again later.");
				System.out.println("error in retrieving");
				return;
			}
			for(int i=0;insurancefirms.next();i++){
				reply.put(insurancefirms.getString("firmid"), insurancefirms.getString("insurancefirm"));
				System.out.print(insurancefirms.getString("firmid"));
				System.out.print(":");
				System.out.println(insurancefirms.getString("insurancefirm"));
			}
			out.print(reply/*.toString()*/);
		}
		catch(SQLException e){
			out.print("Error in retrieving the list of insurance firms. Please try again later.");
			e.printStackTrace();
		}
	}
}