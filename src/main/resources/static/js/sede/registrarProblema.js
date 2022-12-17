var contextPath = window.location.href;

$(document).on("click", ".problem-Produc", function () {

    $("#problemaModal  #idgestor12").val('');
    $("#problemaModal  #idsede22").val('');
    $("#problemaModal  #idproductoinv32").val('');
    $("#problemaModal  #idestadoasign42").val('');
    $("#problemaModal  #idprecioventa52").val('');



    $("#problemaModal  #idsede22").val($(this).data('id22'));
    $("#problemaModal  #idproductoinv32").val($(this).data('id32'));
    $("#problemaModal  #idestadoasign42").val($(this).data('id42'));
    $("#problemaModal  #idprecioventa52").val($(this).data('id52'));


});


