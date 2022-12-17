var contextPath = window.location.href;

$(document).on( "click", ".asign-A-Tienda", function () {

    $("#AsignarProductoModal  input").val('');

    $("#AsignarProductoModal  #asignadosSedes\\.id\\.gestor").val($(this).data('id1'));
    $("#AsignarProductoModal  #asignadosSedes\\.id\\.sede").val($(this).data('id2'));
    $("#AsignarProductoModal  #asignadosSedes\\.id\\.productoinventario").val($(this).data('id3'));
    $("#AsignarProductoModal  #asignadosSedes\\.id\\.estadoasignacion").val($(this).data('id4'));
    $("#AsignarProductoModal  #asignadosSedes\\.id\\.precioventa").val($(this).data('id5'));

    $("#AsignarProductoModal  #fechaasignacion").val('');
    $("#AsignarProductoModal  #stock").val('');
    $("#AsignarProductoModal  #tienda").val('');
    $("#AsignarProductoModal  #cantAsign").text('');
    let url = contextPath + "/post";

    let data = {
        sede: parseInt($(this).data('id2')),
        productoinventario: $(this).data('id3'),
        estadoasignacion: parseInt($(this).data('id4')),
        precioventa: parseFloat($(this).data('id5'))
    };

    console.log(data);
    $.ajax({
        method:"POST",
        data: data,
        url:url
    }).done(function(asigntienda){
        if (asigntienda!=null){
            $("#AsignarProductoModal #cantAsign").text(asigntienda.cantAsign);
        }
        $("#AsignarProductoModal .modal-footer button").attr("disabled",false);
    }).fail(function (err) {
        console.log(err);
        $('#AsignarProductoModal').modal('hide');
        alert("Ocurrió un error");
    })

});

$(document).ready(function() {
    if ($("#msgAsign").text()==="ERROR"){
        $("#AsignarProductoModal").modal({show: true, backdrop: 'static', keyboard: false });
        $("#AsignarProductoModal .modal-footer button").attr("disabled",false);
    }
});

