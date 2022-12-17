const contextPath  = window.location.href;

$(document).on("click",".editar-Venta", function(){
    $.ajax({
        method:"GET", url:contextPath + "/get?id=" + $(this).data('id')
    }).done(function(ven){
        if (ven!=null){
            $("#formModal #idventas").val(ven.idventas);
            $("#formModal #rucdni").val(ven.rucdni);
            $("#formModal #nombrecliente").val(ven.nombrecliente);
        }
    }).fail(function (err) {
        console.log(err);
        $('#formModal').modal('hide');
        alert("Ocurrió un error");
    })
});

$(document).on("click",".cancelar-Venta", function(){
    $.ajax({
        method:"GET", url:contextPath + "/get?id=" + $(this).data('id')
    }).done(function(ven){
        if (ven!=null){
            $("#cancelarModal #idventas").val(ven.idventas);
            $("#cancelarModal #nota").val(ven.nota);
            $("#cancelarModal #mensaje").val(ven.mensaje);
        }
    }).fail(function (err) {
        console.log(err);
        $('#cancelarModal').modal('hide');
        alert("Ocurrió un error");
    })
});

$(document).on("click",".delete-Venta", function(){
    $("#deleteModal #id1").val($(this).data('id1'));
    $("#deleteModal #id2").val($(this).data('id2'));
});
$(document).ready(function() {
    if ($("#msgVenta").text()==="ERROR"){
        $('#formModal').modal({show: true, backdrop: 'static', keyboard: false });
    }
    else if ($("#msgVenta").text() === "ERROR_cancelar"){
        $('#cancelarModal').modal({show: true, backdrop: 'static', keyboard: false });
    }
});