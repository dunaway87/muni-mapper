var displayedPolygon = null;
var parcelNumber = null;
var layerNames = new Array();
var latlng;
var geoJsonLayers = new Array();
var popup = L.popup();

map.on('click', function(e){
	layerNames = new Array();
	for(var i =0; i < layers.length; i++){
		layerNames.push(layers[i].layerName);

	}	
	var bbox = map.getBounds().toBBoxString();
	var epsg = 4326;
	var x = e.containerPoint.x
	var y = e.containerPoint.y
	var width = map.getSize().x
	var height = map.getSize().y;
	console.log(e.latlng);
	latlng = e.latlng;
	
	addPopUp(bbox, epsg, x, y, width, height, latlng);
	
});



var jsToRun;

function addPopUp(bbox, epsg, x, y, width, height, latlng){
	$.get(
			"/onClick?bbox="+bbox+"&epsg="+epsg+"&layers="+layerNames+"&x="+x+"&y="+y+"&height="+height+"&width="+width,function(result){
				result = JSON.parse(result);
				var popupHtml = result.html;
				console.log(result);

				
				if(popupHtml != "<div></div>"){
					var options = {'maxWidth':'500', 'minWidth':'300', 'maxHeight':'600'};
					popup = L.popup(options);
				//	popup.setLatLng(latlng).setContent(popupHtml).openOn(map);

					popup.setLatLng(latlng).setContent('<div id="popupdiv"></div>').openOn(map);
					$('#popupdiv').append(popupHtml);
					
					
				}
				if (typeof(result.charts) !== 'undefined'){
					for(var i=0;i<result.charts.length;i++){
						var data = result.charts[i].data
						if(result.charts[i].type=="pie"){
							makePieChart(JSON.parse(data));
						}
						if(result.charts[i].type=="bar"){
							makeBarChart(JSON.parse(data));
						}
						
					}
				}
				
				
				var geometries = result.geometries;
				
				removeGeoJson();

				geoJsonLayers = new Array();
				
				
				var myStyle = {
					    "color": "#000000",
					    "weight": 5,
					    "fillOpacity": .35,
						"opacity": 1
					    
					    
					};
				
				for(var i = 0; i < geometries.length; i++){
					geoJsonLayers.push(L.geoJson(geometries[i], {
					    style: myStyle
					}));
					geoJsonLayers[i].addTo(map);
					
					
				}
				


			});


	}
function removeGeoJson(){
	for(var i = 0; i < geoJsonLayers.length; i++){
		map.removeLayer(geoJsonLayers[i]);
	}

}
function removePopup(){
	map.removeLayer(popup);
}