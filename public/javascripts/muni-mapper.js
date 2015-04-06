var map;
var layerNamesInit;
function loadMap(){
	
var startY = $('#zoomLat').val();
var startX = $('#zoomLon').val();
var zoomLevel = $('#zoomLevel').val();
layerNamesInit = JSON.parse($('#layerNames').val());

console.log(startX);
console.log(startY);
console.log(zoomLevel);
console.log(layerNames);

if(startX == 0){
	startX = -149.9;
} 
if(startY == 0){
	startY = 61.15; 
}
if(zoomLevel == 0){
	zoomLevel=11;
}

map = new L.Map('map', {center: new L.LatLng(startY, startX), zoom: zoomLevel});
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
	
	if($( '#'+layername+'Toggler' ).length && !$('#'+layername+'Toggler').is(":checked") && !dontRemoveLegend){
		$('#'+layername+'Legend').remove();
		return;
	}
	
	removeGeoJson();
	removePopup();
	
	 console.log(layername);
		var hasLayer = false;
		for(var i =0; i < layers.length; i++){
			 if(layers[i].layerName == layername){
				$("#"+layername).css({
					"background-color": "#ccc",
					"border": "1px solid #ddd",
					"color":"#333333",
					"font-weight":"normal"
				});
				if(!dontRemoveLegend){
					$('#'+layername+'Legend').remove();
				}
				removeLayer(layers[i], i);
				hasLayer = true;
				$
				break;
			}

		}
		if(hasLayer == false){
			$("#"+layername).css({
				 "background-color":"#999999",
		       "border-style":"outset",
		       "border-color":"#707373",
		    	"color":"#000000",
		    	"font-weight":"bold"
			});
			if(!dontRemoveLegend){
				$('#legends').append('<div id="'+layername+'Legend" class="legend"><b>'+layername.replace(/_/g," ")+'</b><span class="glyphicon glyphicon-remove-circle x-glyph" onClick="manageLayer(\''+layername+'\')"></span><span class ="toggle-checkbox" onClick="toggleLayer(\''+layername+'\')"><input id="'+layername+'Toggler" type = "checkbox" checked></span><br><img src="http://www.mountainhouseproject.com/muni/legend/'+layername+'" alt="some_text"><div>');
			}
			addLayerToArray(layername);
		}
		updateURL();
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
		    tiled: 'true',
		    transparent: true,
		});
		 	map.addLayer(layer);
		 	return layer;
};

$(window).resize(function(){
	changeWrapperMarginTop();
});
function changeWrapperMarginTop(){
	if(window.innerWidth < 1152 && window.innerWidth > 766){
		$('#wrapper').css('margin-top', '100px');
		$('#settingsList').css('margin-top', '100px');
	} else {
		$('#wrapper').css('margin-top', '51px');
		$('#settingsList').css('margin-top', '51px');
	}
}
