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

@WebServlet("/GetDetails")
public class GetDetails extends HttpServlet {
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
		
		String formname=request.getParameter("formname");
		String aadhaarid=request.getParameter("aadhaarid");
		ResultSet reply;
		JSONObject obj;
		switch(formname){
		case "personal":
			reply=DatabaseInteraction.query("select bloodgroup from euser where aadhaarid='"+aadhaarid+"'");
    		try {
				if(reply==null || reply.isAfterLast()){
					System.out.println("unable to get blood group from database.");
					out.print("Unable to get the bloodgroup from database. Please try again later.");
					return;
				}
				else{
					reply.next();
		    		out.print(reply.getString("bloodgroup"));
				}
			} catch (SQLException e1) {
				System.out.println("unable to get blood group from database.");
				out.print("Unable to get blood group from database. Please try again later.");
				e1.printStackTrace();
				return;
			}
			break;
		case "insurance":
			reply=DatabaseInteraction.query("select insuranceid, firmid from insurance where aadhaarid='"+aadhaarid+"'");
			try {
				if(reply==null || reply.isAfterLast()){
					System.out.println("unable to get insurance from database.");
					out.print("Unable to get the insurance from database. Please try again later.");
					return;
				}
				else{
					reply.next();
					obj=new JSONObject();
					obj.put("insuranceid", reply.getString("insuranceid"));
					obj.put("firmid", reply.getString("firmid"));
		    		out.print(obj);
				}
			} catch (SQLException e1) {
				System.out.println("unable to get insurance from database.");
				out.print("Unable to get insurance from database. Please try again later.");
				e1.printStackTrace();
				return;
			}
			break;
		/*case "family":
			reply=DatabaseInteraction.query("select familyaadhaarid, relation from euser_euser where aadhaarid='"+aadhaarid+"'");
			try {
				if(reply==null || reply.isAfterLast()){
					System.out.println("unable to get Acquaintance details from database.");
					out.print("Unable to get the Acquaintance details from database. Please try again later.");
					return;
				}
				else{
					obj=new JSONObject();
					while(reply.next()){
						obj.put(reply.getString("familyaadhaarid"), reply.getString("relation"));
					}
		    		out.print(obj);
				}
			} catch (SQLException e1) {
				System.out.println("unable to get family details from database.");
				out.print("Unable to get family details from database. Please try again later.");
				e1.printStackTrace();
				return;
			}
			break;
		default:
			break;*/
		}
	}
}
