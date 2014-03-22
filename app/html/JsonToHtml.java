package html;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonToHtml {
	public static String buildIt(JsonArray layers){
		String allHtml = "<div id='popupDiv'>";
		for(int i = 0; i < layers.size(); i++){

			JsonObject obj = layers.get(i).getAsJsonObject();
			String LayerName = obj.get("layer").getAsString();

			JsonObject properties = obj.get("properties").getAsJsonObject();
			Set<Entry<String, JsonElement>> layersSet = properties.entrySet();
			Iterator<Entry<String, JsonElement>> iterator = layersSet.iterator();
			Entry<String, JsonElement> entry;
			String html = "<fieldset><strong>"+LayerName.replace("_", " ")+"</strong>";
			while(iterator.hasNext()){
				entry = iterator.next();
				String key = entry.getKey();
				String value = entry.getValue().getAsString();

				html = addProperty(key, value, html);

			}
			html = html.concat("</fieldset>");
			allHtml = allHtml.concat(html);
		}
		allHtml= allHtml.concat("</div>");
		return allHtml;

	}
	public static String addProperty(String field, String value, String html){
		if(!field.equals("More_Info")){
		html = html.concat("<br><tr><td>"+field.replace("_", " ")+": </td><td>"+value+"</td></tr>");
		} else {
			html = html.concat("<br><tr><td>"+value+"</td></tr>");

		}
		return html;
	}
}