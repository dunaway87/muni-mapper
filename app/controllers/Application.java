package controllers;

import play.*;
import play.libs.WS;
import play.mvc.*;
import searches.Address;
import utils.DatabaseConnection;
import geoserver.Geoserver;
import geoserver.Layers;
import html.JsonToHtml;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class Application extends Controller {

	
    public static void map() {
        render();
    }
	public static void getLayers() throws SQLException{
		renderText(Layers.getLayers());
	}
	
	public static void getLayersGeoserver() throws MalformedURLException, SQLException{
	    String layers = Layers.getLayersGeoserver();
		Logger.info(layers.toString());
		renderText(layers);
	}

	public static void onClick(int epsg, String bbox, int x, int y, int height, int width, String layers, boolean getGeom){
		bbox = fixBBox(bbox);
		Logger.info("bbox %s", bbox);
		JsonObject arrayWithGeom = Geoserver.getAllLayers(epsg, bbox, x, y, height, width, layers.split(","), getGeom);
		JsonArray array = arrayWithGeom.get("array").getAsJsonArray();
		JsonArray geometries = arrayWithGeom.get("geometries").getAsJsonArray();
		
		String html = JsonToHtml.buildIt(array);
		
		JsonObject toReturn = new JsonObject();
		toReturn.add("geometries", geometries);
		toReturn.addProperty("html", html);
		toReturn.add("charts", arrayWithGeom.get("charts"));
		renderText(toReturn);
	}

	public static void addressSearch(String address) throws SQLException{
		if(address.equals("") || address == null){
			renderJSON("");
		}
		address = address.toLowerCase();
		renderJSON(Address.search(address.replace("road", "rd").replace("lp", "loop").replace("drive", "dr").replace("highway", "HWY").replace("circle", "cir").replace("avenue", "ave").replace("boulevard", "blvd")).toString());
	}
	
	public static void legend(String layerName) throws SQLException{
		Connection conn = DatabaseConnection.getConnection();
		ResultSet result = conn.prepareStatement("Select source from folders.legend where layer_name = '"+layerName+"'").executeQuery();
		result.next();
		InputStream is = WS.url(result.getString(1)).get().getStream();
		conn.close();
		renderBinary(is);
	}	
	
	
	private static String fixBBox(String bbox) {
		String[] points = bbox.split(",");
		bbox = points[1]+","+points[0]+","+points[3]+","+points[2];
		return bbox;
	}
	
}