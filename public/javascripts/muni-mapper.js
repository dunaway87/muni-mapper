var map;
function loadMap(){
map = new L.Map('map', {center: new L.LatLng(61.15, -149.9), zoom: 11});
addBaseMap('dunaway87.hffcoej7');
$("#sataliteListItem").addClass("baseMapItemClicked");

}
var basemap;
function addBaseMap(mapboxID){
	if(basemap != null){
		map.removeLayer(basemap);
	}
		basemap = L.tileLayer('http://{s}.tiles.mapbox.com/v3/'+mapboxID+'/{z}/{x}/{y}.png', {
	    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
	});
	map.addLayer(basemap, true);
	basemap.bringToBack();
}


var layers = new Array();

function manageLayer(layername){
	removeGeoJson();
	removePopup();
	
	 console.log(layername);
		var hasLayer = false;
		for(var i =0; i < layers.length; i++){
			 if(layers[i].layerName == layername){
				$("#"+layername).css({
					"background-color": "#fff",
					"border": "1px solid #ddd"
				});
				removeLayer(layers[i], i);
				hasLayer = true;
				break;
			}

		}
		if(hasLayer == false){
			$("#"+layername).css({
				 "background-color":"#55555f",
		       "border-style":"outset",
		       "border-color":"#707373"});
			addLayerToArray(layername);
		}
	  };
	  function addLayerToArray(layername){
			var layerInfo = new Object();
			layerInfo.layer =  mapLayer(layername);
			layerInfo.layerName= layername;
			layers.push(layerInfo);
		}
	  
	  function removeLayer(layer, i){

		  console.log(layer);
		  console.log(layer.layerName);
		  map.removeLayer(layer.layer);
		layers.splice(i,1);
		}
	  function mapLayer(layerName){
		 	var layer = L.tileLayer.wms("http://geoblaster.info:8080/geoserver/cite/wms", {
		    layers: 'cite:'+layerName,
		    format: 'image/png',
		    transparent: true,
		});
		 	map.addLayer(layer);
		 	return layer;
};



