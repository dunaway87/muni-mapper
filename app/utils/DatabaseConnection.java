package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	public static Connection getConnection() throws SQLException{
		//Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:63334/automated_shapefile_uploads?user=dunawayjenckes&password=MacsEdgar2155");
		Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/automated_shapefile_uploads?user=dunawayjenckes&password=MacsEdgar2155");
		//Connection conn = DriverManager.getConnection("jdbc:postgresql://geoblaster.info:5432/automated_shapefile_uploads?user=dunawayjenckes&password=MacsEdgar2155");
		
		//Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/automated_shapefile_uploads?user=dunawayjenckes&password=dunawayjenckes");

		//Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:63333/automated_shapefile_uploads?user=dunawayjenckes&password=dunawayjenckes");
		return conn;
	}
}
//Create Table Census_Race as Select gid, white, black, native, asian, pacisland as pacific_islander, hispanic, other+two_plus as other, geom from blockgroups