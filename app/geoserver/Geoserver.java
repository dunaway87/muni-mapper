package geoserver;

import org.stringtemplate.v4.ST;

import play.Logger;
import play.libs.WS;




import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Geoserver {
	public static JsonObject getAllLayers(int epsg, String bbox, int x, int y, int height, int width, String[] layers, boolean getGeom){
		JsonArray geometries = new JsonArray();
		JsonArray array = new JsonArray();
		for(int i =0; i<layers.length; i++){
			JsonObject obj =getLayerInfo(epsg, bbox, x, y, height, width, layers[i], getGeom);
			if(!(obj == null)){
				geometries.add(obj.get("geometry"));
				obj.remove("geometry");
				array.add(obj);
			}
		}
		JsonObject arrayWithGeom = new JsonObject();
		arrayWithGeom.add("array", array);
		arrayWithGeom.add("geometries", geometries);
		return arrayWithGeom;
	}
	
	
	public static String GEOSERVER_URL = "http://geoblaster.info:8080/geoserver/wms/wms?SERVICE=WMS&CRS=EPSG:$epsg$&VERSION=1.3.0&REQUEST=GetFeatureInfo&EXCEPTIONS=JSON&BBOX=$bbox$&x=$x$&y=$y$&INFO_FORMAT=application/json&LAYERS=$layers$&QUERY_LAYERS=$layers$&WIDTH=$width$&HEIGHT=$height$";

	public static JsonObject getLayerInfo(int epsg, String bbox, int x, int y, int height, int width, String layer, boolean getGeom) {

		ST template = new ST(GEOSERVER_URL, '$', '$');

		template.add("epsg", epsg);
		template.add("bbox", bbox);
		template.add("x", x);
		template.add("y", y);
		template.add("height", height);
		template.add("width", width);
		template.add("layers", layer);
		
		Logger.info("%s",template.render());
		JsonObject obj = WS.url(template.render()).get().getJson().getAsJsonObject();
		
		Logger.info("%s",obj);
		JsonObject toReturn = new JsonObject();
		JsonArray features = obj.get("features").getAsJsonArray();
		if(features.size()==0){
			return null;
		}
		JsonObject properties = features.get(0).getAsJsonObject().get("properties").getAsJsonObject();
		properties.remove("fid");
		properties.remove("bbox");
		toReturn.addProperty("layer",layer);
		toReturn.add("properties", properties);
		toReturn.add("geometry", features.get(0).getAsJsonObject().get("geometry").getAsJsonObject());
		
		Logger.error("%s", features);
		
		
		
		return toReturn;
	}

	

}