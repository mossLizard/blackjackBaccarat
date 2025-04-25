window.addEventListener('scroll', doParallax);
function doParallax(){
  var positionY = window.pageYOffset;
  var p2 = document.getElementsByClassName("altScrolling")[0];
  var p3 = document.getElementsByClassName("altScrolling")[1];
  document.body.style.backgroundPosition = "0 -" + (positionY/5) + "px";
  p2.style.backgroundPosition = "0 -" + ((positionY/2)) + "px";
  p2.style.backgroundPosition = "0 -" + ((positionY/3)) + "px";
}