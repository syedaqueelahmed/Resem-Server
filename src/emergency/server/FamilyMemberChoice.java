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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet("/FamilyMemberChoice")
public class FamilyMemberChoice extends HttpServlet {
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
    	if(aadhaarid==null){
    		System.out.println("aadhaarid is not passed");
    		out.print("Error in recieving your aadhaarid.");
    		return;
    	}

    	ResultSet familymembers=DatabaseInteraction.query("select familyaadhaarid,relation from euser_euser where aadhaarid='"+aadhaarid+"'");
    	JSONArray reply=new JSONArray();
    	JSONObject obj;
    	try {
    		if(familymembers==null || familymembers.isAfterLast()){
				out.print("Error in retrieving the family details. Please try again.");
				System.out.println("Query Incomplete. Error in retrieving.");
				return;
			}
    		System.out.println("Queried.");
			while(familymembers.next()){
				obj=new JSONObject();
				obj.put("familyaadhaarid", familymembers.getString("familyaadhaarid"));
				obj.put("relation", familymembers.getString("relation"));
				reply.add(obj);
				System.out.print(familymembers.getString("familyaadhaarid"));
				System.out.print(":");
				System.out.println(familymembers.getString("relation"));
			}
		} catch (SQLException e) {
			out.print("Error in retrieving the family details. Please try again.");
			e.printStackTrace();
		}
    	out.print(reply.toString());
	}
}