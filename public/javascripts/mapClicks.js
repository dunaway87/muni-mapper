var displayedPolygon = null;
var parcelNumber = null;
var layerNames = new Array();
var latlng;
var geoJsonLayers = new Array();
var popup = L.popup();
var justDoubleClicked = false;
map.on('dblclick', function(){
	console.log("blabla");
	justDoubleClicked =true;
	myVar=setTimeout(function(){justDoubleClicked=false},1000);
});

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
	if(neighborhoodClicked==false){
		clickData = new Object();
		clickData = {bbox:bbox, epsg:epsg, x:x, y:y, width:width, height:height, latlng:latlng,zoom:map.getZoom()};
		console.log(clickData);
		$.get(


				"/onClick?bbox="+bbox+"&epsg="+epsg+"&layers="+layerNames+"&x="+x+"&y="+y+"&height="+height+"&width="+width,function(result){
					if(justDoubleClicked==false){
						result = JSON.parse(result);
						var popupHtml = result.html;
						console.log(result);


						if(popupHtml != "<div id='popupDiv'></div>"){
							var options = {'maxWidth':'500', 'minWidth':'300', 'maxHeight':'600'};
							popup = L.popup(options);
							popup.setLatLng(latlng).setContent('<div id="popupdiv"></div>').openOn(map);
							$('#popupdiv').append(popupHtml);


						}
						if (typeof(result.charts) !== 'undefined'){
							for(var i=0;i<result.charts.length;i++){
								var data = result.charts[i].data
								if(result.charts[i].type=="pie"){
									makePieChart(JSON.parse(data), 'popupDiv');
								}
								if(result.charts[i].type=="bar"){
									makeBarChart(JSON.parse(data), 'popupDiv');
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


					}
				});
	} else {
		$('#map').css({'cursor':'wait'})
		$('#myNeighborhood').css({'cursor':'wait'});

		$('#myNeighborhood').toggle();

		appendToMyNeighborhood('Zoning, Subdivisions, easements, parcels, military', 'landUse',bbox, epsg, x, y, width, height, latlng);
		appendToMyNeighborhood('Geology, Seismic, Wetlands, Avalanche, lakes, streams', 'environment',bbox, epsg, x, y, width, height, latlng);


		appendToMyNeighborhood('trails, major trails, Municipal_Parks, Chugach_State_Park, Chugach_National_Forest, lakes, streams','recreation',bbox, epsg, x, y, width, height, latlng);
		appendToMyNeighborhood('critical hospital, fire station, elementary school, middle school, high school, grocery store','buildings',bbox, epsg, x, y, width, height, latlng);

		 appendToMyNeighborhood('Community_Borders, Community_Councils,House_Districts, Senate_Districts, Assembly_Districts, Zip_Codes', 'politcal',bbox, epsg, x, y, width, height, latlng);
		appendToMyNeighborhood('Census_Home_Ownership, Census_Gender, Census_Race, Home_Vacancy', 'census',bbox, epsg, x, y, width, height, latlng);

		clickNeighborhoodButton();
	}

}

function appendToMyNeighborhood(layers,divToAppendTo,bbox, epsg, x, y, width, height, latlng){
	console.log("start");
	$.get("/myNeighborhood?layers="+layers+"&bbox="+bbox+"&epsg="+epsg+"&x="+x+"&y="+y+"&height="+height+"&width="+width+"&lat="+latlng.lat+"&lon="+latlng.lng,function(result){
		console.log(result);

		var allLayers = jQuery.parseJSON(result);
		var distanceLayers = allLayers.distanceLayers;
		var content = "<table>";
		var evenOdd =-1;
		for (var x=0; x<distanceLayers.length; x++){
			content += appendRowToModal(distanceLayers[x].Label, distanceLayers[x].Value, distanceLayers[x].Distance, getRowColor(evenOdd));
			evenOdd = evenOdd*-1;
		}
		var coveragePercents = allLayers.coveragePercents;

		for (var x=0; x<coveragePercents.length; x++){
			content += appendRowToModal(coveragePercents[x].Label, coveragePercents[x].Value, "", getRowColor(evenOdd));
			evenOdd = evenOdd*-1;
		}
		console.log(allLayers.charts);
		var geoserverLayers = allLayers.geoserverLayers.array;

		var geocontent = "";
		if(geoserverLayers != undefined){
			for(var i=0; i<geoserverLayers.length; i++){
				var count =0;
				for (var label in geoserverLayers[i].properties) {
					var value = geoserverLayers[i].properties[label];
					if(count ==0){
						content += appendRowToModal(geoserverLayers[i].layer,label, value, getRowColor(evenOdd));
					} else {
						content += appendRowToModal("",label, value, getRowColor(evenOdd));
					}
					count++;
				}
				evenOdd = evenOdd*-1;
			}
		}
	
		content += "</table>";

		$('#'+divToAppendTo).append(content);
		
		var myNeighborhoodCharts = allLayers.geoserverLayers.charts;
		if (typeof(myNeighborhoodCharts) != 'undefined'){
			
			for(var i=0;i<myNeighborhoodCharts.length;i++){
				
				var data = myNeighborhoodCharts[i].data
				console.log(myNeighborhoodCharts[i].type);
				if(myNeighborhoodCharts[i].type=="pie"){
					content += makePieChart(JSON.parse(data), divToAppendTo);
				}
				if(myNeighborhoodCharts[i].type=="bar"){
					content += makeBarChart(JSON.parse(data), divToAppendTo);
				}

			}
		}
		

		
		
		$('#map').css({'cursor':'default'});
		$('#myNeighborhood').css({'cursor':'default'});



	});

}


function appendRowToModal(label, value,distance, backgroundColor){
	var toReturn = "";
	toReturn += "<tr class='myNeighborhoodTable' style='background:"+backgroundColor+"'>"
		toReturn += "<th class='myNeighborhoodTable' style='width:200px; background:"+backgroundColor+"'>"+new String(label).replace("_", " ")+"</th>"
		toReturn += "<td class='myNeighborhoodTable' style='width:200px; background:"+backgroundColor+"'>"+new String(value).replace("_", " ")+"</td>";
		toReturn += "<td class='myNeighborhoodTable' style='width:200px; background:"+backgroundColor+"'>"+new String(distance).replace("_", " ")+"</td>";
	

	return toReturn;
}

function getRowColor(evenOdd){
	if(evenOdd == -1){
		return "#CCC";
	} else {
		return "#FFF";
	}
}

function removeGeoJson(){
	for(var i = 0; i < geoJsonLayers.length; i++){
		map.removeLayer(geoJsonLayers[i]);
	}

}
function removePopup(){
	map.removeLayer(popup);







	var clickData = new Object();








}