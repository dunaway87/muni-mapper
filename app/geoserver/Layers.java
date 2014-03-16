package geoserver;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import play.Logger;
import play.Play;
import utils.DatabaseConnection;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Layers {

	public static final String RESTURL  =Play.configuration.getProperty("RESTURL");
	public static final String RESTUSER = Play.configuration.getProperty("RESTUSER");
	public static final String RESTPW   = Play.configuration.getProperty("RESTPW");
	
	public static String getLayersGeoserver() throws MalformedURLException {
	    GeoServerRESTReader reader = new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
	    RESTLayerList layers = reader.getLayers();
	    List<String> names = layers.getNames();
	    JsonArray array = new JsonArray();
	   String toReturn = "";
	    for(int i = 0; i < names.size(); i ++){
	    	Logger.info(names.get(i));
	    	array.add(new JsonPrimitive(names.get(i)));
	    	toReturn = toReturn.concat(names.get(i)+"*");
	    }
	    toReturn = toReturn.substring(0, toReturn.length()-1);
	    Logger.info(array.toString());
		return toReturn;
	}

	public static JsonObject getLayers() throws SQLException{
		JsonObject layers = getJson();
		JsonObject html = getHTML(layers);
		return layers;
	}
	private static JsonObject getHTML(JsonObject obj){
		Set<Entry<String, JsonElement>> entrySet = obj.entrySet();
		
		Iterator<Entry<String, JsonElement>> iterator = entrySet.iterator();
		JsonObject htmls = new JsonObject();
		String foldersHtml = "<div id=\"layerContainer\" class=\"layerContainer\">";
		for (Entry<String, JsonElement> entry : entrySet) {
			String folder = entry.getKey();
			foldersHtml += "<div id =\""+folder+"\" class=\"layer\" onClick=manageLayer(\""+folder+"\")><h2 class =\"h2\">"+folder.replace("_", " ")+"</h2></div>";
			JsonArray layers = entry.getValue().getAsJsonArray();
			String folderHtml = "<div id=\"layerContainer\" class=\"layerContainer\">";
			for (int i = 0; i < layers.size(); i++) {
				String name = layers.get(i).getAsString();
				String layerHTML = "<div id =\""+name+"\" class=\"layer\" onClick=manageLayer(\""+name+"\")><h2 class =\"h2\">"+name.replace("_", " ")+"</h2></div>";
				folderHtml += layerHTML;
			}
			folderHtml += "</div>";
			htmls.addProperty(folder, folderHtml);
		}
		foldersHtml += "</div>";
		htmls.addProperty("folders", foldersHtml);
		return htmls;
	}
	private static JsonObject getJson() throws SQLException{
		Connection conn = DatabaseConnection.getConnection();
		ResultSet results =conn.prepareStatement("Select folder, layer_name from folders.layer order by folder_id asc").executeQuery();
		JsonObject folders = new JsonObject();
		while(results.next()){
			String folderName = results.getString(1);
			String layerName = results.getString(2);
			if(!folders.has(folderName)){
				folders.add(folderName, new JsonArray());
			}
			folders.get(folderName).getAsJsonArray().add(new JsonPrimitive(layerName));
		}
		conn.close();
		return folders;
	}
}
