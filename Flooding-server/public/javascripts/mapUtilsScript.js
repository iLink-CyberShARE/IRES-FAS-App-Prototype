//Get the modal
var modal = document.getElementById('id01');
var map=document.getElementById("map");

//When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
        map.style.display="flex";
    }
}

$("#ex").click(function(){
    $("#id01").css("display","none");
    $("#map").css("display","flex");
});