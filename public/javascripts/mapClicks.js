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
				
				
				}
				});
			} else {
				 $('#map').css({'cursor':'wait'})
                 $('#myNeighborhood').css({'cursor':'wait'});

                 $('#myNeighborhood').toggle();

                /* $.get("/myNeighborhood?layers= critical hospital, fire station, elementary school, middle school, high school, grocery store, trails, major trails, Municipal_Parks &bbox="+bbox+"&epsg="+epsg+"&layers="+layerNames+"&x="+x+"&y="+y+"&height="+height+"&width="+width+"&lat="+latlng.lat+"&lon="+latlng.lng,function(result){
                console.log(result);
           
                var allLayers = jQuery.parseJSON(result);
	               var distanceLayers = allLayers.distanceLayers;
	               var content = "";
	               for (var x=0; x<distanceLayers.length; x++){
	            	  
	            	   content +=distanceLayers[x].Value;
	            	   content += "   ";
	            	   content += distanceLayers[x].Distance;
	            	   content += "<br>";
	            	   content += "<br>";
	            	  
	               }
	               
	               $('#buildings').append(content);
	               $('#map').css({'cursor':'default'});
	               $('#myNeighborhood').css({'cursor':'default'});
	           
	                
	                
        });*/
                 
                 appendToMyNeighborhood('critical hospital, fire station, elementary school, middle school, high school, grocery store, trails, major trails, Municipal_Parks ', 'buildings',bbox, epsg, x, y, width, height, latlng);
                 
                 appendToMyNeighborhood('Chugach_National_Forest, Chugach_State_Park, Zoning, Subdivisions  ', 'landUse',bbox, epsg, x, y, width, height, latlng);
                 appendToMyNeighborhood('House_Districts, Senate_Districts, Assembly_Districs, Community_Councils, Zip_Codes, Community_Borders ', 'politcal',bbox, epsg, x, y, width, height, latlng);
                 appendToMyNeighborhood('Census_Race, Census_Home_Ownership, Census_Gender ', 'census',bbox, epsg, x, y, width, height, latlng);
                 appendToMyNeighborhood('Geology, Seismic, Wetlands, Avalanche ', 'environment',bbox, epsg, x, y, width, height, latlng);

                 clickNeighborhoodButton();
			}

	}
function appendToMyNeighborhood(layers,divToAppendTo,bbox, epsg, x, y, width, height, latlng){
	console.log("start");
	$.get("/myNeighborhood?layers="+layers+"&bbox="+bbox+"&epsg="+epsg+"&layers="+layerNames+"&x="+x+"&y="+y+"&height="+height+"&width="+width+"&lat="+latlng.lat+"&lon="+latlng.lng,function(result){
        console.log(result);
        
        var allLayers = jQuery.parseJSON(result);
           var distanceLayers = allLayers.distanceLayers;
           var content = "";
           for (var x=0; x<distanceLayers.length; x++){
        	  
        	   content +=distanceLayers[x].Value;
        	   content += "   ";
        	   content += distanceLayers[x].Distance;
        	   content += "<br>";
        	   content += "<br>";
        	  console.log("finish");
           }
           var coveragePercents = allLayers.coveragePercents;
           
           for (var x=0; x<coveragePercents.length; x++){
        	  
        	   content +=coveragePercents[x].Label;
        	   content += "   ";
        	   content += coveragePercents[x].Value;
        	   content += "<br>";
        	   content += "<br>";
           }
           var geoserverLayers = allLayers.geoserverLayers.array;

           	var geocontent = "";
           	if(geoserverLayers != undefined){
           	for(var i=0; i<geoserverLayers.length; i++){
           		geocontent+= geoserverLayers[i].layer;
           		geocontent+= " ";
           		geocontent+= objToHtml(geoserverLayers[i].properties);
           		geocontent+="<br>";
           		
           		}
           	}
           $('#'+divToAppendTo).append(content);
           $('#'+divToAppendTo).append(geocontent);
           $('#map').css({'cursor':'default'});
           $('#myNeighborhood').css({'cursor':'default'});
       
            
            
});
	
}

function objToHtml(obj){
	var htmlToReturn = "";
	for (var label in obj) {
		  var value = obj[label];
		htmlToReturn += label;
		htmlToReturn += '<br>';
		htmlToReturn += value;
		htmlToReturn += '<br>';
		htmlToReturn += '<br>';
		  
		}
	return htmlToReturn;
}

function removeGeoJson(){
	for(var i = 0; i < geoJsonLayers.length; i++){
		map.removeLayer(geoJsonLayers[i]);
	}

}
function removePopup(){
	map.removeLayer(popup);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}