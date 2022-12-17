const contextPath  = window.location.href;

$(document).ready(function() {
    $(".nav-item i").prop("hidden",false);
    if ($("#msgGestor").text()==="ERROR"){
        $('#editprofile').modal({show: true, backdrop: 'static', keyboard: false });
    }
}).on("submit","#editprofile form", function () {
    if((document.getElementById('photo').files[0].size*1.0004)>=2097152) {
        $("#photo").next().remove().end().parent().append("<div class=\"text-danger\">Archivo mayor a 2MB</div>");
        return false;
    }
    return  true;
});