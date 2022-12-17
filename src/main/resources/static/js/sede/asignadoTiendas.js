const contextPath  = window.location.href;

$(function() {

    let $checkbox = $("#confirmado1");
    //console.log($checkbox.is(":checked"));
    $("#id\\.numerodocumento").prop("disabled",!$checkbox.is(":checked"));
    $(".inputFile").prop("hidden",!$checkbox.is(":checked"));
    //$checkbox.val($checkbox.checked);

    //$("#id\\.numerodocumento").val('').prop("disabled",true);
    $("body").on('change','#confirmado1', function () {
        //console.log(this.checked);
        if(!this.checked){
            $("#id\\.numerodocumento").val('');
        }
        $("#id\\.numerodocumento").prop("disabled",!this.checked);
        $(".inputFile").prop("hidden",!this.checked);
    });

    if ($("#msgRegistrar").text()==="ERROR"){
        $('#registrarModal').modal({show: true, backdrop: 'static', keyboard: false });
    }else if($("#msgDevolucion").text()==="ERROR"){
        $('#devolucionModal').modal({show: true, backdrop: 'static', keyboard: false });
    }

}).on("click",".registar-Venta", function(){
    $("#registrarModal  input").val('');
    $("#registrarModal  #id1").val($(this).data(''));
    $("#registrarModal  .text-danger").hide();
    $("#id\\.numerodocumento").prop("disabled",true);
    $("#confirmado1").prop("checked",false);
    $(".inputFile").prop("hidden",true);

    $.ajax({
        method:"GET", url:contextPath +"/get?id1=" +$(this).data('id1')
    }).done(function(ventas){
        if (ventas!=null){
            $("#registrarModal  #fechaasignacion").text('Este producto se asignó el ' + ventas.fechaasignacion);
            $("#registrarModal  #cant").text(ventas.stock);
            $("#registrarModal  #id1").val(ventas.idtiendas);
            $("#registrarModal  #rucdni").val(ventas.tienda.ruc);
            $("#registrarModal  #nombrecliente").val(ventas.tienda.nombre);
            $("#registrarModal  #lugarventa").val(ventas.tienda.direccion);
            $("#registrarModal  #codigoinventario").val(ventas.asignadosSedes.id.productoinventario.codigoinventario);
            $("#registrarModal  #precioventa").val(ventas.asignadosSedes.id.precioventa);

        }
    }).fail(function (err) {
        console.log(err);
        $('#registrarModal').modal('hide');
        alert("Ocurrió un error");
    })
}).on("submit","#registrarModal form", function () {
    if((document.getElementById('foto1').files[0].size*1.0004)>=2097152) {
        $("#foto1").next().remove().end().parent().append("<div class=\"text-danger\">Archivo mayor a 2MB</div>");
        return false;
    }
    return  true;
});

$(document).on("click",".retornar-Producto", function(){
    $("#devolucionModal #id2").val($(this).data('id2'));
    //console.log($(this).data('id2'))
});

