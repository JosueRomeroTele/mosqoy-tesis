var contextPath  = window.location.href+"/get?id=";

$(document).on("click",".info-Sede", function(){
    $("#infoModal #id").val($(this).data('id'));//id del usuario

    $.ajax({
        method:"GET", url:contextPath +$(this).data('id')
    }).done(function(sede){
        if (sede!=null){
            $("#infoModal  #nombre").text(sede.nombre + ' ' + sede.apellido);
            $("#infoModal  #telefono").text(sede.telefono);
            $("#infoModal  #correo").text(sede.correo);
        }
    }).fail(function (err) {
        console.log(err);
        $('#infoModal').modal('hide');
        alert("Ocurrió un error");
    })
});

$(document).on("click",".confirmar", function(){
    $("#confirmarModal #id1").val($(this).data('id1'));
    $("#confirmarModal #id2").val($(this).data('id2'));
    $("#confirmarModal #id3").val($(this).data('id3'));
    $("#confirmarModal #id4").val($(this).data('id4'));
    $("#confirmarModal #id5").val($(this).data('id5'));
});

$(document).on("click",".rechazar", function(){
    $("#rechazarModal #dni").val($(this).data('id1'));
    $("#rechazarModal #codigo").val($(this).data('id2'));
    $("#rechazarModal #precio").val($(this).data('id3'));
    $("#rechazarModal #fecha").val($(this).data('id4'));
    $("#confirmarModal #encargado").val($(this).data('id5'));
});