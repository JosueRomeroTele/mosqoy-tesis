var contextPath  = window.location.href+"/has?id1=";

$(document).on("click",".info-Venta", function(){
    $("#ventaModal #id1").val($(this).data('id1'));
    $.ajax({
        method:"GET", url:contextPath + $(this).data('id1')
    }).done(function(venta){
        if (venta!=null){
            $("#ventaModal  #nota").text(venta.nota);
            $("#ventaModal  #mensaje").text(venta.mensaje);
        }
    }).fail(function (err) {
        console.log(err);
        $('#ventaModal').modal('hide');
        alert("Ocurrió un error");
    })
});