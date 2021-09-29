/**
 * <h1> Display Map </h1>
 *
 * Values and methods for the correct display of the severity level of a report in a map. Each
 * severity level belongs to a color.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

var radius = [0,0,0];
var color = ["green","orange","red"];
var icons = ["cloud-rain","cloud-showers-heavy","exclamation-triangle"];
var lat = document.getElementById("map-script").getAttribute("lat");
var lon = document.getElementById("map-script").getAttribute("lon");
var ip_address = document.getElementById("map-script").getAttribute("ip_address");
var currentLocation = [parseFloat(lat)+0.00007,parseFloat(lon)+0.00007];
var circle;
var marker;
var lim=50;

//Create a new map
var mymap = L.map('map').fitWorld();
mymap.setView(currentLocation,12);

//Create the tile layer with correct attribution
var osmUrl = 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
var osmAttrib = 'Map data © <a href="https://openstreetmap.org">OpenStreetMap</a> contributors';
var osm = new L.TileLayer(osmUrl, { attribution: osmAttrib});
mymap.addLayer(osm);

$.getJSON(ip_address+'reports/retrieve?limit='+lim,function(result){
    var index=0;
    $.each(result,function(i,data){
        if (data["severity"]=="Low"){
            index=0;
        }else if(data["severity"]=="Medium"){
            index=1;
        }else if(data["severity"]=="High"){
            index=2;
        }
        addMarker(data,icons[index],color[index],color[index], radius[index]);
        addHeatMarker(data);
    });
    addMarker(currentLocation,"flag","blue",null, null);
});

$.getJSON(ip_address+'raspi/retrieve?limit='+lim,function(result){
    globalData = null;
    if(result.length>0){
        globalData = result[0];
    }
    addMarker(globalData, "video",'lightgray','blue', 350);
});

function addMarker(data, iconName,markerColor,circleColor, radius){
    var loc= circleColor!=null?[data["location"].lat,data["location"].lon]:data;

    marker = L.marker(loc).addTo(mymap);
    var icon = L.AwesomeMarkers.icon({"extraClasses": "fa-rotate-0", "icon": iconName, "iconColor": "white", "markerColor": markerColor, "prefix": "fa"});
    marker.setIcon(icon);

    if(circleColor!=null){
        var severity=data["severity"];
        var date=data["date"];
        var imageName=data["imageName"];

        circle = L.circle(loc, {
            color: circleColor,
            fillColor: circleColor,
            fillOpacity: 0.2,
            radius: radius
        }).addTo(mymap);
        marker.on('click',function (e) {
            $("#id01").modal('show');
            //Set report data
            if(iconName == 'video'){
                $("#modalTitle").html("Data collected from sensor");
                $("#plot").show();
                $("#singleReport").hide();
            }else{
                $("#modalTitle").html("Report details");
                $("#plot").hide();
                $("#singleReport").show();
                $("#image").attr("src",ip_address + "images/download/"+imageName);
                $("#severityValue").show().html(severity);
                $("#dateValue").show().html(date);
            }
        });
    }
}

function addHeatMarker(data){
    L.heatLayer([[data["location"].lat,data["location"].lon,25]], {
        radius: 35,
        opacity: .05,
        gradient: {
            0.0: "rgb(0, 0, 252)",
            0.99: "rgb(0, 0, 252)",
            1.0: "rgb(0, 0, 252)"
        }
    }).addTo(mymap);
}