map.on('zoomend', function(e){
	updateURL();

});

map.on('dragend', function(e){
	updateURL();
});

function updateURL(){
	var layerNames =  new String();
	for( var i = 0; i < layers.length;i++){
		if(i != 0){
			layerNames = layerNames+",";
		}
		layerNames = layerNames + layers[i].layerName;
	}
	console.log(layerNames);
	var startObj = {lat:map.getCenter().lat, lng:map.getCenter().lng, zoom:map.getZoom(), layers:layerNames};
	window.history.pushState(startObj, "", "?zoomLat="+startObj.lat+"&zoomLon="+startObj.lng+"&zoomLevel="+startObj.zoom+"&layers="+layerNames);
}