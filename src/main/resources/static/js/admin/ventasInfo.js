var contextPath  = window.location.href+"/get?id=";

$(document).on("click",".info-Gestor", function(){
    $("#infoModal #id").val($(this).data('id'));
    $.ajax({
        method:"GET", url:contextPath +$(this).data('id')
    }).done(function(gestor){
        if (gestor!=null){
            $("#infoModal  #nombre").text(gestor.nombre + ' ' + gestor.apellido);
            $("#infoModal  #telefono").text(gestor.telefono);
            $("#infoModal  #correo").text(gestor.correo);
        }
    }).fail(function (err) {
        console.log(err);
        $('#infoModal').modal('hide');
        alert("Ocurrió un error");
    })
});