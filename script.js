console.log("Hello World");
function myFunc(){
    var element = document.getElementById("myId");
    if(element.innerHTML != "YAY!! The Javascript Works!")
        element.innerHTML="YAY!! The Javascript Works!";
    else
        element.innerHTML="button!";
}