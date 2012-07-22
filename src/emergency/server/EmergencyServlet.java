package emergency.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/EmergencyServlet")
public class EmergencyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String host=null,database=null,username=null,password=null;
	int port=0;
	
	double serviceid=0.0;
	String familyaadhaarid=null;
	String useraddress=null;
	static long temptime=0;
	long time=0;
	
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
    	
    	if((familyaadhaarid=request.getParameter("familyaadhaarid"))==null){
    	
    		String typeid=request.getParameter("typeid");
    		String aadhaarid=request.getParameter("aadhaarid");
    		double latitude=0.0,longitude=0.0,userlatitude=0.0,userlongitude=0.0;
    		
    		ResultSet userlatlong=DatabaseInteraction.query("select latitude,longitude from euser where aadhaarid='"+aadhaarid+"'");
    		try {
				if(userlatlong==null || userlatlong.isAfterLast()){
					System.out.println("unable to get user location from database.");
					out.print("Unable to get location details from database. Please try again later.");
					return;
				}
				else{
					userlatlong.next();
					String ulat=userlatlong.getString("latitude"),ulon=userlatlong.getString("longitude");
		    		if(ulat==null || ulon==null || ulat=="null" || ulon=="null"){
		    			System.out.println("null lat long");
		    			out.print("Please check your GPS. Please try again later.");
		    			return;
		    		}
	    			userlatitude=userlatlong.getDouble("latitude");
	    			userlongitude=userlatlong.getDouble("longitude");
				}
			} catch (SQLException e1) {
				System.out.println("unable to get user location from database.");
				out.print("Unable to get location details from database. Please try again later.");
				e1.printStackTrace();
				return;
			}
    		
    		System.out.println("before triangulateaddress");
    		useraddress=Location.triangulateAddress(userlatitude, userlongitude);
    		System.out.println("after triangulate address");
    		
    		ResultSet services=DatabaseInteraction.query("select serviceid,latitude,longitude from service where typeid='"+typeid+"'");
			try {
				if(services==null || services.isAfterLast()){
					System.out.println("error in retrieving the service id and location details");
					out.print("Unable to get service location details from database. Please try again later.");
					return;
				}
			} catch (SQLException e1) {
				System.out.println("error in retrieving the service id and location details");
				out.print("Unable to get service location details from database. Please try again later.");
				e1.printStackTrace();
				return;
			}
    	
    		double distance=0,tempdistance=0;
    		try{
    			services.next();
    			latitude=services.getDouble("latitude");
				longitude=services.getDouble("longitude");
//				distance=tempdistance=Location.findDrivingDistance(latitude, longitude, userlatitude, userlongitude);
				distance=tempdistance=Location.findStraightLineDistance(latitude, longitude, userlatitude, userlongitude);
					System.out.println("tempdistance: "+tempdistance);
				time=temptime;
				serviceid=services.getDouble("serviceid");
    			while(services.next()){
    				latitude=services.getDouble("latitude");
    				longitude=services.getDouble("longitude");
//    				tempdistance=Location.findDrivingDistance(latitude, longitude, userlatitude, userlongitude);
    				tempdistance=Location.findStraightLineDistance(latitude, longitude, userlatitude, userlongitude);
    				
    				System.out.println("tempdistance: "+tempdistance);
    				if(distance>tempdistance){
    					distance=tempdistance;
    					time=temptime;
    					serviceid=services.getDouble("serviceid");
    				}
    			}
    			System.out.println("nearest service id: "+serviceid);
    			out.print("success");
    		}
    		catch(SQLException e){
    			System.out.println("unable to get service location from database.");
    			out.print("Unable to zero-in on a particular service. Please try again later.");
    			e.printStackTrace();
    			return;
    		}
    	}
    	else{
    		ResultSet blood_group=DatabaseInteraction.query("select bloodgroup from euser where aadhaarid='"+familyaadhaarid+"'");
    		String bloodgroup = null,insuranceid = null,insurancefirm=null,contactnumber=null;
    		try {
				if(blood_group==null || blood_group.isAfterLast()){
					System.out.println("error in retrieving the blood group.");
					out.print("Error connecting to the database. Please try again later.");
					return;
				}
				else{
					blood_group.next();
	    			bloodgroup=blood_group.getString("bloodgroup");
				}
			} catch (SQLException e1) {
				System.out.println("error in retrieving the blood group.");
				out.print("Error connecting to the database. Please try again later.");
				e1.printStackTrace();
				return;
			}
    		
    		ResultSet insurancedetails=DatabaseInteraction.query("select insuranceid, firmid from insurance where aadhaarid='"+familyaadhaarid+"'");
    		try {
				if(insurancedetails==null || insurancedetails.isAfterLast()){
					System.out.println("error in retrieving the insurance details.");
					out.print("Error connecting to the database. Please try again later.");
					return;
				}
				else{
					insurancedetails.next();
	    			insuranceid=insurancedetails.getString("insuranceid");
	    			int firmid=insurancedetails.getInt("firmid");
	    			ResultSet firmdetails=DatabaseInteraction.query("select insurancefirm, contactnumber from insurancefirmdetails where firmid='"+firmid+"'");
    				if(firmdetails==null || firmdetails.isAfterLast()){
    					System.out.println("error in retrieving the firm details.");
    					out.print("Error connecting to the database. Please try again later.");
    					return;
    				}
    				else{
    					firmdetails.next();
    	    			insurancefirm=firmdetails.getString("insurancefirm");
    	    			contactnumber=firmdetails.getString("contactnumber");
    				}
				}
			} catch (SQLException e1) {
				System.out.println("error in retrieving the insurance details");
				out.print("Error connecting to the database. Please try again later.");
				e1.printStackTrace();
				return;
			}

    		String servicename=Inform.informService(serviceid, useraddress, familyaadhaarid, bloodgroup, insuranceid, insurancefirm);
    		if(servicename.endsWith("Please try again later.")){
    			out.print(servicename);
    			return;
    		}
    		System.out.println("informed");
    		
    		System.out.println(time);
    		long departuretime=5;
    		time+=(departuretime*60);
    		System.out.println(time);
    		
    		long hours=TimeUnit.SECONDS.toHours(time);    		
    		long minutes=TimeUnit.SECONDS.toMinutes(time)-TimeUnit.HOURS.toMinutes(hours);
    		System.out.println(hours+" Hours: "+minutes+" minutes");
    		String timestring;
    		if(hours!=0){
    			timestring=hours+" Hours: "+minutes+" minutes";
    		}
    		else{
    			timestring=minutes+" minutes";
    		}
    		System.out.println(timestring);
			
    		out.print("The Service \n\""+servicename+"\"\n will be reaching you soon.\n\nETA : "+timestring); //return acknowledgement;
    		System.out.print("The Service \n\""+servicename+"\"\n will be reaching you soon.\n\nETA : "+timestring);
    		
    		Inform.informInsuranceDepartment(contactnumber,familyaadhaarid,insuranceid);
    	}	
	}
}