function loadMap(){
map = new L.Map('map', {center: new L.LatLng(61.15, -149.9), zoom: 11});
addBaseMap('dunaway87.hffcoej7');
}
var basemap;
function addBaseMap(mapboxID){
	if(basemap != null){
		map.removeLayer(basemap);
	}
		basemap = L.tileLayer('http://{s}.tiles.mapbox.com/v3/'+mapboxID+'/{z}/{x}/{y}.png', {
	    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
	});
	map.addLayer(basemap);
}

