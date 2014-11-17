package jobs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import models.Crime;
import play.Logger;
import play.libs.WS;
import play.libs.WS.WSRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DailyCrime {

	public static Connection getConnection() throws SQLException{
		Connection conn = DriverManager.getConnection("jdbc:postgresql://geoblaster.info:5432/automated_shapefile_uploads?user=dunawayjenckes&password=MacsEdgar2155");
		return conn;
	}


	public static String updateCrimeTable(){
		Crime.deleteAll();
		Logger.info("deleted crime");
		JsonArray yesterday = getYesterday().get("d").getAsJsonArray();
		for(int i = 0; i < yesterday.size();i++){
			Crime crime = new Crime();
			Logger.info("crime "+ i);
			JsonObject obj = yesterday.get(i).getAsJsonObject();
			crime.Crime_Type = obj.get("m_crimeType").getAsString();
			crime.Time = obj.get("m_reportTimeString").getAsString();
			
			crime.lat = obj.get("m_latitude").getAsDouble();
			crime.lon = obj.get("m_longitude").getAsDouble();
			crime.Location = obj.get("m_reportTimeString").getAsString();
			
			crime.save();
			
		}
		
		Connection conn;
		try {
			conn = getConnection();
			Logger.info("adding geom");
		conn.prepareStatement("Update crime set geom = ST_POINT(lon, lat)").execute();
		
		} catch (SQLException e) {
			Logger.error("error in sql %s", e);
		}
		
		Logger.info("done");
		return null;
	}
	
	
	
	
	public static String getDate(){
		WSRequest wsr = WS.url("http://crimemap.muni.org/CrimeService.svc/LastReportDate");
		wsr.setHeader("Accept", "*/*");
		wsr.setHeader("Accept-Encoding", "gzip,deflate,sdch");
		wsr.setHeader("Accept-Language", "en-US,en;q=0.8");
		wsr.setHeader("Connection", "keep-alive");
		wsr.setHeader("Content-Length", "0");
		wsr.setHeader("Origin", "http://crimemap.muni.org");
		wsr.setHeader("Content-Type", "application/json; charset=UTF-8");
		wsr.setHeader("Cookie","utma=179633208.1325190981.1394917699.1400870406.1401474684.21; __utmc=179633208; __utmz=179633208.1401474684.21.20.utmcsr=localhost:9000|utmccn=(referral)|utmcmd=referral|utmcct=/; ASP.NET_SessionId=t42u1vyov245gf55ozbzllfm");
		wsr.setHeader("Host", "crimemap.muni.org");
		wsr.setHeader("Referer", "http://crimemap.muni.org");
		wsr.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		wsr.setHeader("Cache-Control", "max-age=0");
		
		String date = wsr.post().getJson().getAsJsonObject().get("d").getAsString();
		/*date = date.substring(date.indexOf("(")+1);
		date = date.substring(0, date.indexOf(")"));*/
		return date;
	}
	
	
	public static JsonObject getYesterday(){
		String date = getDate();
	

		WSRequest wsr = WS.url("http://crimemap.muni.org/CrimeService.svc/GetCrimeData");
		wsr.setHeader("Accept", "*/*");
		wsr.setHeader("Accept-Encoding", "gzip,deflate,sdch");
		wsr.setHeader("Accept-Language", "en-US,en;q=0.8");
		wsr.setHeader("Connection", "keep-alive");
		wsr.setHeader("Content-Length", "210");
		wsr.setHeader("Origin", "http://crimemap.muni.org");
		wsr.setHeader("Content-Type", "application/json; charset=UTF-8");
		wsr.setHeader("Cookie","__utma=179633208.1325190981.1394917699.1395730748.1396080704.13; __utmc=179633208; __utmz=179633208.1396080704.13.13.utmcsr=localhost:9000|utmccn=(referral)|utmcmd=referral|utmcct=/; ASP.NET_SessionId=vgtbuvqmr1avfs55n4ukqnra");
		wsr.setHeader("Host", "crimemap.muni.org");
		wsr.setHeader("Referer", "http://crimemap.muni.org");
		wsr.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");

		JsonObject obj = new JsonObject();

		obj.addProperty("maxx", -147.69050183447268);
		obj.addProperty("maxy", 61.754556992889);
		obj.addProperty("minx",  -150.1081616552737);
		obj.addProperty("miny", 60.5083554643706);
		obj.addProperty("p_enddate", date);
		obj.addProperty("p_startdate", date);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("AOO"));
		array.add(new JsonPrimitive("AR"));
		array.add(new JsonPrimitive("AS"));
		array.add(new JsonPrimitive("BUR"));
		array.add(new JsonPrimitive("DC"));
		array.add(new JsonPrimitive("DST"));
		array.add(new JsonPrimitive("DG"));
		array.add(new JsonPrimitive("DK"));

		array.add(new JsonPrimitive("DUI"));
		array.add(new JsonPrimitive("FF"));
		array.add(new JsonPrimitive("HM"));
		array.add(new JsonPrimitive("LQ"));
		array.add(new JsonPrimitive("PS"));
		array.add(new JsonPrimitive("RB"));
		array.add(new JsonPrimitive("THF"));
		array.add(new JsonPrimitive("VD"));
		array.add(new JsonPrimitive("VTH"));
		array.add(new JsonPrimitive("WO"));
		array.add(new JsonPrimitive("SA"));
		array.add(new JsonPrimitive("DV"));

		obj.add("p_crimecodes", array);
		wsr.body=obj;
		JsonObject returnObj = wsr.post().getJson().getAsJsonObject();



		return returnObj;
	}




}
