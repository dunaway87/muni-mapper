package myNeighborhood;

import geoserver.Geoserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import play.Logger;
import play.Play;
import play.vfs.VirtualFile;
import utils.DatabaseConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MyNeighborhood {
	private static LinkedList<String> getBuildingTypes(String requestedLayers){
		Logger.info(requestedLayers);
		LinkedList<String> buildingTypesList = new LinkedList<String>();
		if(requestedLayers.contains("critical hospital")){
			buildingTypesList.add("critical hospital");
		}
		if(requestedLayers.contains("elementary school")){
			buildingTypesList.add("elementary school");
		}
		if(requestedLayers.contains("high school")){
			buildingTypesList.add("high school");
		}	
		if(requestedLayers.contains("middle school")){
			buildingTypesList.add("middle school");
		}	
		if(requestedLayers.contains("grocery store")){
			buildingTypesList.add("grocery store");
		}
		if(requestedLayers.contains("fire station")){
			buildingTypesList.add("fire station");
		}	
		return buildingTypesList;

	}
	public static JsonObject getIt(double lat, double lon, int epsg, String bbox, int x, int y, int height, int width,String layers) throws SQLException{
		Logger.info(layers);
		LinkedList<String> buildingTypesList = getBuildingTypes(layers);
		String baseSQL = VirtualFile.fromRelativePath("app/sql/NearestBuilding.sql").contentAsString();
		Connection conn = DatabaseConnection.getConnection(); 
		JsonArray array = new JsonArray();
		PreparedStatement pstmt = null;
		for(int i =0; i < buildingTypesList.size(); i++){
			pstmt = conn.prepareStatement(baseSQL);
			pstmt.setDouble(1, lon);
			pstmt.setDouble(2, lat);
			pstmt.setString(3, buildingTypesList.get(i));
			Logger.info(pstmt.toString());
			ResultSet results = pstmt.executeQuery();
			results.next();
			JsonObject obj = new JsonObject();
			obj.addProperty("Label", results.getString(2));
			obj.addProperty("Value", results.getString(1));
			obj.addProperty("Distance", results.getString(3)+ " miles");
			array.add(obj);
		}

		if(layers.contains("trails")){
			array.add(getTrail(lat, lon, conn));
		}
		if(layers.contains("major trails")){
			array.add(getMajorTrail(lat, lon, conn));
		}

		bbox = fixBBox(bbox);
		String[] geoserverLayers = getGeoserverLayers(layers);

		JsonObject geoserverStuff = new JsonObject();
		if(geoserverLayers != null && geoserverLayers.length != 0){
			geoserverStuff = Geoserver.getAllLayers(epsg, bbox, x, y, height, width, geoserverLayers, false);
			geoserverStuff.remove("geometries");
		}

		JsonObject toReturn = new JsonObject();

		if(layers.contains("Municipal_Parks")){
			array.add(DistanceFinder.findNearest(conn, lat, lon, "Municipal_Parks", "Municipal Park"));
		}
		if(layers.contains("Chugach_National_Forest")){
			array.add(DistanceFinder.findNearest(conn, lat, lon, "Chugach_National_Forest", "Chugach National Forest"));
		}
		if(layers.contains("Chugach_State_Park")){
			array.add(DistanceFinder.findNearest(conn, lat, lon, "Chugach_State_Park", "Chugach State Park"));
		}
		toReturn.add("distanceLayers", array);
		toReturn.add("geoserverLayers", geoserverStuff);

		JsonArray coverages = new JsonArray();

		if(layers.contains("Wetlands")){
			coverages.add(ParcelCoverages.parcelCoverage(conn, ParcelCoverages.getParcelNumber(conn, lat, lon), "Wetlands", "Wetlands"));
		}
		if(layers.contains("Avalanche")){
			coverages.add(ParcelCoverages.parcelCoverage(conn, ParcelCoverages.getParcelNumber(conn, lat, lon), "Avalanche", "Avalanche"));
		}
		toReturn.add("coveragePercents", coverages);
		conn.close();

		return toReturn;	
	}
	private static String[] getGeoserverLayers(String requestedLayers){
		LinkedList<String> list = new LinkedList<String>();
		if(requestedLayers.contains("Zoning")){
			list.add("Zoning");
		}
		if(requestedLayers.contains("Subdivisions")){
			list.add("Subdivisions");
		}
		if(requestedLayers.contains("Geology")){
			list.add("Geology");
		}
		if(requestedLayers.contains("Seismic")){
			list.add("Seismic");
		}
		if(requestedLayers.contains("House_Districts")){
			list.add("House_Districts");
		}
		if(requestedLayers.contains("Senate_Districts")){
			list.add("Senate_Districts");
		}
		if(requestedLayers.contains("Zip_Codes")){
			list.add("Zip_Codes");
		}
		if(requestedLayers.contains("Community_Borders")){
			list.add("Community_Borders");
		}
		if(requestedLayers.contains("Assembly_Districts")){
			list.add("Assembly_Districts");
		}		
		if(requestedLayers.contains("Community_Councils")){
			list.add("Community_Councils");
		}		
		if(requestedLayers.contains("Census_Race")){
			list.add("Census_Race");
		}		
		if(requestedLayers.contains("Community_Councils")){
			list.add("Community_Councils");
		}		
		if(requestedLayers.contains("Census_Home_Ownership")){
			list.add("Census_Home_Ownership");
		}		
		if(requestedLayers.contains("Census_Gender")){
			list.add("Census_Gender");
		}	
		if(list.size()>0){
			String[] toReturn  = new String[list.size()];
			for(int i = 0; i< list.size(); i++){
				toReturn[i]=list.get(i);
			}

			return toReturn;
		} else {
			return null;
		}
	}

	private static String fixBBox(String bbox) {
		String[] points = bbox.split(",");
		bbox = points[1]+","+points[0]+","+points[3]+","+points[2];
		return bbox;
	}	

	private static JsonObject getMajorTrail(double lat, double lon, Connection conn) throws SQLException{
		String baseSQL =  VirtualFile.fromRelativePath("app/sql/NearestMajorTrail.sql").contentAsString();
		PreparedStatement pstmt = conn.prepareStatement(baseSQL);
		pstmt.setDouble(1, lon);
		pstmt.setDouble(2, lat);
		ResultSet results = pstmt.executeQuery();
		results.next();
		JsonObject obj = new JsonObject();
		obj.addProperty("Label", "Major Bike Trail");
		String trailName = new String();
		if(results.getString(3).equals(results.getString(2))){
			trailName = results.getString(2);
		} else {
			trailName = results.getString(3) + " "+ results.getString(2);
		}
		obj.addProperty("Value", trailName);
		obj.addProperty("Distance", results.getString(1)+ " miles");
		return obj;

	}

	private static JsonObject getTrail(double lat, double lon, Connection conn) throws SQLException{
		String baseSQL =  VirtualFile.fromRelativePath("app/sql/NearestTrail.sql").contentAsString();
		PreparedStatement pstmt = conn.prepareStatement(baseSQL);
		pstmt.setDouble(1, lon);
		pstmt.setDouble(2, lat);
		ResultSet results = pstmt.executeQuery();
		results.next();
		JsonObject obj = new JsonObject();
		obj.addProperty("Label", "Trail");
		String trailName;
		if(results.getString(3).equals(results.getString(2))){
			trailName = results.getString(2);
		} else {
			trailName = results.getString(3) + " "+ results.getString(2);
		}
		obj.addProperty("Value", trailName);
		obj.addProperty("Distance", results.getString(1)+ " miles");
		return obj;

	}
}
