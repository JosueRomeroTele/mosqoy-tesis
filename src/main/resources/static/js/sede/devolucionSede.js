var contextPath = window.location.href;

$(document).on("click", ".dev-Prod", function () {


    $("#devolucionModal  #idseded").val($(this).data('id13'));
    $("#devolucionModal  #idproductoinvd").val($(this).data('id33'));
    $("#devolucionModal  #idestadoasignd").val($(this).data('id43'));
    $("#devolucionModal  #idprecioventad").val($(this).data('id53'));
    $("#devolucionModal  #cantDevol").val('');

    let url = contextPath + "/postD";

    let data = {
        idsede: parseInt($(this).data('id13')),
        idproductoinv: $(this).data('id33'),
        idestadoasign: parseInt($(this).data('id43')),
        idprecioventa: parseFloat($(this).data('id53'))
    };

    console.log(data);
    $.ajax({
        method:"POST",
        data: data,
        url:url
    }).done(function(dev){
        if (dev!=null){
            $("#devolucionModal #cantAsignD").text(dev.cantAsignD);
        }
        $("#devolucionModal .modal-footer button").attr("disabled",false);
    }).fail(function (err) {
        console.log(err);
        $('#devolucionModal').modal('hide');
        alert("Ocurrió un error");
    })


//
});

$(document).ready(function() {
    if ($("#msgDevolucion").text()==="ERROR"){
        $("#devolucionModal").modal({show: true, backdrop: 'static', keyboard: false });
    }
});

