package emergency.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String host=null,database=null,username=null,password=null;
	int port=0;
	
	static String errorresponse=null;
	
    public void init() {       
        	host="localhost";
        	port=3306;
        	database="emergencydb";
        	username="user";
        	password="asdf";
        	DatabaseInteraction.connect(host, port, database, username, password);
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
		
		Enumeration<String> parameter_list=request.getParameterNames();
		String attributes[]=new String[4];
		String attribute_values[]=new String[4];
		int index;
		for(index=0;index<4;index++)
			attribute_values[index]="";
		for(index=0;parameter_list.hasMoreElements();index++){
			attributes[index]=parameter_list.nextElement().toString();
		}
		for(int i=0;i<index;i++){
			for(int j=0;j<index;j++){
				if(attributes[j].equals(new Integer(i).toString())){
					attribute_values[i]=request.getParameter(attributes[j]);
				}
			}
		}
		
		int number_of_rows;//=-1;
		switch(attribute_values[0]){
		case "personal":
			number_of_rows=DatabaseInteraction.update("insert into euser(aadhaarid,bloodgroup,userkey) values('"+attribute_values[1]+"','"+attribute_values[2]+"','"+attribute_values[3]+"')");
			break;
		case "insurance":
			number_of_rows=DatabaseInteraction.update("insert into insurance(insuranceid,firmid,aadhaarid) values('"+attribute_values[1]+"','"+attribute_values[2]+"','"+attribute_values[3]+"')");
			break;
		case "family":
			number_of_rows=DatabaseInteraction.update("insert into euser_euser(aadhaarid,familyaadhaarid,relation) values('"+attribute_values[1]+"','"+attribute_values[2]+"','"+attribute_values[3]+"')");
			break;
		default:
			number_of_rows=-2;
			break;
		}
		if(number_of_rows<1){
			System.out.println("Error in inserting: "+number_of_rows);
			if(errorresponse.equals("")){
				if(attribute_values[0].equalsIgnoreCase("personal")){
					errorresponse="Invalid Aadhaar ID";
				}
				else if(attribute_values[0].equalsIgnoreCase("family")){
					errorresponse="This person has not yet registered.";
				}
				/*else{
					out.print("Foreign Key Constraint on firm name");
				}*/
				out.print(errorresponse);
			}
			else if(errorresponse==null){
				out.print("Error inserting into the Database. Please try again later.");
			}
			else{
				if(attribute_values[0].equalsIgnoreCase("family")){
					if(errorresponse.equalsIgnoreCase("already registered")){
						out.print("Relation already exists");
					}
				}
				else{
					out.print(errorresponse);
				}
			}
			return; //exit from here
		}
		else{
			System.out.println("Data Inserted");
			out.print("success");
		}
	}
}