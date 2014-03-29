package myNeighborhood;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import play.Logger;
import play.vfs.VirtualFile;

import com.google.gson.JsonObject;


public class DistanceFinder {

	
	public static JsonObject findNearest(Connection conn, double lat, double lon, String tableName, String tableLabel) throws SQLException{
		String sql = VirtualFile.fromRelativePath("app/sql/NearestPark.sql").contentAsString();
		PreparedStatement pstmt = conn.prepareStatement(sql.replace("$tableName$", tableName));
		pstmt.setDouble(1, lon);
		pstmt.setDouble(2, lat);
		
		Logger.info(pstmt.toString());
		ResultSet rs = pstmt.executeQuery();
		rs.next(); 
		JsonObject obj = new JsonObject();
		obj.addProperty("Label", tableLabel);
		obj.addProperty("Value", rs.getString(2));
		obj.addProperty("Distance", rs.getDouble(1)+ " miles");
		
		return obj;
		
	}
	
	
}
