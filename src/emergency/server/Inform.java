package emergency.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Inform {

	public static String informService(double serviceid,String address,String familyaadhaarid,String bloodgroup,String insuranceid,String insurancefirm) {		
		// code to inform the service (SMS)		
		ResultSet service=DatabaseInteraction.query("select servicename,contactno from service where serviceid='"+serviceid+"'");
		try {
			if(service==null || service.isAfterLast()){
				System.out.println("error in retrieving the service name & contactno.");
				return "Error connecting to the database. Please try again later.";
			}
			else{
				service.next();
				String servicename=service.getString("servicename");
				String contactno=service.getString("contactno");
				String message="Emergency, require your immediate assistance." +
						"\nLocation= "+address+
						"\nAadhaarid= "+familyaadhaarid+
						"\nBloodgroup= "+bloodgroup+
						"\nInsurance Firm= "+insurancefirm+
						"\nInsurance ID= "+insuranceid;
				System.out.println(message);
				String encodedmessage=URLEncoder.encode(message, "UTF-8");
				System.out.println(encodedmessage);

				URL url=new URL("http://www.smsgatewaycenter.com/library/send_sms_2.php?UserName=ayushren&Password=renaissance&Type=Individual&To="+contactno+"&Mask=Idealabs&Message="+encodedmessage);					
				HttpURLConnection con =(HttpURLConnection) url.openConnection();
				System.out.println(con);
				if(con==null || con.getResponseCode()!=HttpURLConnection.HTTP_OK){
					return "Unable to inform the service. Please try again later.";
				}
				
				InputStream is=con.getInputStream();
				int i=0;
				StringBuilder s=new StringBuilder("");
				while((i=is.read()) != -1)
				{
					s.append((char)i);
				}
				String response=s.toString().trim();
				System.out.println(response);
				is.close();
				con.disconnect();
				if(!response.startsWith("SUCCESS")){
					return "Unable to inform the service. Please try again later.";
				}
				return servicename;
			}
		} catch (SQLException e) {
			System.out.println("error in retrieving the service name.");
			e.printStackTrace();
			return "Unable to inform the service. Please try again later.";
		} catch (UnsupportedEncodingException e) {
			System.out.println("error in informing.");
			e.printStackTrace();
			return "Unable to inform the service. Please try again later.";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "Unable to inform the service. Please try again later.";
		} catch (IOException e) {
			e.printStackTrace();
			return "Unable to inform the service. Please try again later.";
		}
	}
	
	public static /*String*/void informInsuranceDepartment(String contactnumber, String familyaadhaarid,String insuranceid) {
		try{
			String message="Your Client bearing Insurance ID= "+insuranceid
							+"\nAadhaar ID= "+familyaadhaarid
							+"\nis in Emergency, require your immediate assistance.";
			System.out.println(message);
			String encodedmessage;
			try{
			encodedmessage=URLEncoder.encode(message, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				encodedmessage=URLEncoder.encode(message);				
			}
			System.out.println(encodedmessage);
	
			URL url=new URL("http://www.smsgatewaycenter.com/library/send_sms_2.php?UserName=ayushren&Password=renaissance&Type=Individual&To="+contactnumber+"&Mask=Idealabs&Message="+encodedmessage);					
			HttpURLConnection con =(HttpURLConnection) url.openConnection();
			System.out.println(con);
			if(con==null || con.getResponseCode()!=HttpURLConnection.HTTP_OK){
				System.out.println("connection error");
	//			return "Unable to inform the service. Please try again later.";
			}
				
			InputStream is=con.getInputStream();
			int i=0;
			StringBuilder s=new StringBuilder("");
			while((i=is.read()) != -1)
			{
				s.append((char)i);
			}
			String response=s.toString().trim();
			System.out.println(response);
			is.close();
			con.disconnect();
			if(!response.startsWith("SUCCESS")){
				System.out.println("Unable to inform the insurancefirm. Error in sending SMS");
	//			return "Unable to inform the insurancefirm. Please try again later.";
			}
			else{
				System.out.println("Informed.");
	//			return firmname;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
	//		return "Unable to inform the insurancefirm. Please try again later.";
		} catch (IOException e) {
			e.printStackTrace();
	//		return "Unable to inform the insurancefirm. Please try again later.";
		}
	}
}