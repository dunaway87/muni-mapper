package searches;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import myNeighborhood.ParcelCoverages;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import play.Logger;
import utils.DatabaseConnection;

public class Owner {
	private final static String USER_AGENT = "Mozilla/5.0";
	public static JsonArray search(String searchName) throws Exception{
		JsonArray inArray = getOwnersFromMuni(searchName);
		JsonArray outArray = new JsonArray();
		Connection conn = DatabaseConnection.getConnection();
		for(int i =0; i < inArray.size(); i++){
			if(inArray.get(i).getAsJsonObject().has("parcelNumber")){
				outArray.add(getLatLon(inArray.get(i).getAsJsonObject(), conn));
			}
		}
		conn.close();
		return outArray;

	}

	private static JsonObject getLatLon(JsonObject inObj, Connection conn) {
		Logger.info("%s", inObj);
		String parcelNumber = inObj.get("parcelNumber").getAsString();
		ResultSet results;
		try {
			results = conn.prepareStatement("Select ST_X(ST_TRANSFORM(St_SETSRID(my_geom, 3857), 4326)),ST_Y(ST_TRANSFORM(St_SETSRID(my_geom, 3857), 4326)), \"full_address\" from \"Address\" where \"PARCEL_NUM\"='"+parcelNumber+"'").executeQuery();

			results.next();
			inObj.addProperty("lon", results.getDouble(1));
			inObj.addProperty("lat", results.getDouble(2));
			inObj.addProperty("address", results.getString(3));
		} catch (SQLException e) {
			Logger.error("%s", e);
		}
		return inObj;
	}


	private static JsonArray getOwnersFromMuni(String searchName) throws IOException{
		String url = "http://www.muni.org/pw/gsweb";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");

		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "Template=PUBNAME&Selectability=NAME&SelPgmid=GSWEBX&Parcel="+searchName+"&Length=00&Text=NO";

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		Logger.info("\nSending 'POST' request to URL : " + url);
		Logger.info("Post parameters : " + urlParameters);
		Logger.info("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		boolean addIT = false;
		JsonArray array = new JsonArray();
		JsonObject parcel = new JsonObject();
		int count = 0;
		while ((inputLine = in.readLine()) != null) {

			if(inputLine.contains("javascript:getParcel(")){
				addIT = true;
				count =0;
				array.add(parcel);
				parcel = new JsonObject();
				String parcelNumber = inputLine.substring(inputLine.indexOf("'")+1);

				parcelNumber = parcelNumber.substring(0,parcelNumber.indexOf("'"));

				parcel.addProperty("parcelNumber", parcelNumber.trim().replace("-", ""));

			}

			count++;

			if(count == 3 && addIT == true){
				Logger.error(inputLine);

				String ownerName = inputLine.substring(inputLine.indexOf(">")+1);
				Logger.info(ownerName);

				ownerName = ownerName.substring(0, ownerName.indexOf("</td"));
				Logger.info(ownerName);
				ownerName = ownerName.replace("<br>", " ");
				parcel.addProperty("owner", ownerName.trim());
			}

		}
		in.close();
		return array;
	}

	public static Map<String, String> getHeaders(){
		Map< String, String> map = new HashMap< String, String>();

		map.put("Accept", "*/*");
		map.put("Accept-Encoding", "gzip,deflate,sdch");
		map.put("Accept-Language", "en-US,en;q=0.8");
		map.put("Cache-Control", "no-cache");
		map.put("Connection", "keep-alive");
		map.put("Content-Length", "451");
		map.put("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryfxIpXRpFt99cWeYJ");
		map.put("Cookie", "BIGipServerpool_cicswpp_7081=503750666.43291.0000; BIGipServerpool_sharepoint_dmz_80=482414764.20480.0000; __utma=179633208.1325190981.1394917699.1396080704.1397151857.14; __utmb=179633208.2.10.1397151857; __utmc=179633208; __utmz=179633208.1397151857.14.14.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)");
		map.put("Host", "www.muni.org");
		map.put("User-Agent", "Mozilla/5.0");


		return map;	
	}



}
