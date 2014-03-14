package controllers;

import play.*;
import play.mvc.*;
import geoserver.Layers;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.*;

import models.*;

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
}