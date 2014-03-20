package searches;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import play.Logger;
import utils.DatabaseConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Address {

	public static JsonObject search(String inputAddress) throws SQLException{
		inputAddress = inputAddress.trim();
		JsonArray addresses = new JsonArray();
		inputAddress = inputAddress.replace(" ", " & ");

		Connection conn = DatabaseConnection.getConnection();

		ResultSet results =conn.prepareStatement("Select ST_X(ST_TRANSFORM(St_SETSRID(my_geom, 3857), 4326)),ST_Y(ST_TRANSFORM(St_SETSRID(my_geom, 3857), 4326)), \"PARCEL_NUM\", \"full_address\" from \"Address\" where index_column @@ to_tsquery('"+inputAddress+"') limit 5").executeQuery(); 
		int count = 0;
		while(results.next() && count < 5){
			JsonObject obj = new JsonObject();
			obj.addProperty("lon", results.getDouble(1));
			obj.addProperty("lat", results.getDouble(2));
			obj.addProperty("parcelNumber", results.getString(3));
			obj.addProperty("address", results.getString(4));
			addresses.add(obj);
			count++;
		}

		JsonObject data = new JsonObject();
		data.add("addresses", addresses);
		return data;
	}





}