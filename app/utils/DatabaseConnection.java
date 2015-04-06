package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	public static Connection getConnection() throws SQLException{
		//Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:63334/automated_shapefile_uploads?user=dunawayjenckes&password=MacsEdgar2155");
		//Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/automated_shapefile_uploads?user=dunawayjenckes&password=MacsEdgar2155");
		Connection conn = DriverManager.getConnection("jdbc:postgresql://geoblaster.info:5432/automated_shapefile_uploads?user=dunawayjenckes&password=MacsEdgar2155");
		
		//Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/automated_shapefile_uploads?user=dunawayjenckes&password=dunawayjenckes");

		//Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:63333/automated_shapefile_uploads?user=dunawayjenckes&password=dunawayjenckes");
		return conn;
	}
	
	
	public static void main(String[] args) throws SQLException, IOException{
		Connection conn = getConnection();
		FileReader fr = new FileReader(new File("/data/legend.csv"));
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		while((line = br.readLine()) != null ){
			String[] values = line.split(",");
			String sql ="INSERT into folders.legend values('"+values[0]+"',"+values[1]+",'"+values[2]+"','"+values[3]+"')";
			System.out.println(sql);
			conn.prepareStatement(sql).execute();
			
		}
		
		br.close();
		fr.close();
	}

}




//Create Table Census_Race as Select gid, white, black, native, asian, pacisland as pacific_islander, hispanic, other+two_plus as other, geom from blockgroups