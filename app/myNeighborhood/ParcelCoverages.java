package myNeighborhood;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import play.Logger;
import play.vfs.VirtualFile;

import com.google.gson.JsonObject;

public class ParcelCoverages {

	public static JsonObject parcelCoverage(Connection conn, String geometry, String layerName, String layerLabel) throws SQLException{
		PreparedStatement pstmt  =conn.prepareStatement(VirtualFile.fromRelativePath("app/sql/ParcelCoverage.sql").contentAsString().replace("$layerName$", layerName));
		pstmt.setString(1, geometry);
		Logger.info(pstmt.toString());
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		JsonObject obj = new JsonObject();
		obj.addProperty("Label", layerLabel);

		if(rs == null || rs.getDouble(1) == 0){
			obj.addProperty("Value", 0+"%");
		} else {
			obj.addProperty("Value", rs.getDouble(1) * 100 +"%");
		}
		return obj;
	}
	
	public static String getParcelGeom(Connection conn, double lat, double lon) throws SQLException{
		PreparedStatement pstmt  =conn.prepareStatement(VirtualFile.fromRelativePath("app/sql/ParcelFromLatLon.sql").contentAsString());
		pstmt.setDouble(1, lon);
		pstmt.setDouble(2, lat);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()){
			return rs.getString(1);
		} else { 
			return null;
		}
	}
	
	
}
