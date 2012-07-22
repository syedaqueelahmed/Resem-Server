package emergency.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class Location {
	static double findStraightLineDistance(double latitude, double longitude, double userlatitude, double userlongitude){

		//Simplelatlng api code for finding the straight line distance.
		System.out.println("LatLng inside findDistance: "+latitude+" "+longitude+" "+userlatitude+" "+userlongitude);
		LatLng userlatlng=new LatLng(userlatitude, userlongitude);
		LatLng servicelatlng=new LatLng(latitude, longitude);
		return LatLngTool.distance(userlatlng, servicelatlng, LengthUnit.METER);
	}
	
	static double findDrivingDistance(double latitude, double longitude, double userlatitude, double userlongitude){
		//maps api code for finding the driving distance
		URL url;
		String response;
		try {
			url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin="+latitude+","+longitude+"&destination="+userlatitude+","+userlongitude+"&region=in&sensor=false");
			HttpURLConnection connection =(HttpURLConnection) url.openConnection();
			System.out.println(connection);
			if(connection==null){
				return findStraightLineDistance(latitude, longitude, userlatitude, userlongitude);
			}
			StringBuilder sb=new StringBuilder("");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()), 10000);
				String strLine = null;
				while ((strLine = input.readLine()) != null) {
					sb.append(strLine);
				}
				input.close();
			}
			response = sb.toString();
			connection.disconnect();
			System.out.println(response);
			
			JSONObject obj=JSONObject.fromObject(response);
			JSONArray array = obj.getJSONArray("routes");
			JSONObject obje = array.getJSONObject(0);
			JSONArray legs= obje.getJSONArray("legs");
			JSONObject object=legs.getJSONObject(0);
			
			JSONObject distance=object.getJSONObject("distance");
			double dist=distance.getDouble("value");
			System.out.println(dist);
			
			JSONObject duration=object.getJSONObject("duration");
			EmergencyServlet.temptime=duration.getLong("value");
			System.out.println(EmergencyServlet.temptime);
			
			return dist;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return findStraightLineDistance(latitude, longitude, userlatitude, userlongitude);
		} catch (UnknownHostException e){
			e.printStackTrace();
			return findStraightLineDistance(latitude, longitude, userlatitude, userlongitude);
		} catch (IOException e) {
			e.printStackTrace();
			return findStraightLineDistance(latitude, longitude, userlatitude, userlongitude);
		}
	}
	
	static String triangulateAddress(double userlatitude, double userlongitude){
		//maps api code
		URL url;
		String response;
		try {
			url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng="+userlatitude+","+userlongitude+"&region=in&sensor=false");
			HttpURLConnection connection =(HttpURLConnection) url.openConnection();
			System.out.println(connection);
			if(connection==null){
				return "unsuccessful";
			}
			StringBuilder sb=new StringBuilder("");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()), 10000);
				String strLine = null;
				while ((strLine = input.readLine()) != null) {
					sb.append(strLine);
				}
				input.close();
			}
			response = sb.toString();
			connection.disconnect();
			System.out.println(response);
			JSONObject obj=JSONObject.fromObject(response);
			JSONArray array = obj.getJSONArray("results");
			JSONObject address = array.getJSONObject(0);
			String addr=address.getString("formatted_address");
			System.out.println(addr);
			return addr;			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "unsuccessful";
		} catch (UnknownHostException e){
			e.printStackTrace();
			return "unsuccessful";
		} catch (IOException e) {
			e.printStackTrace();
			return "unsuccessful";
		}
	}
}