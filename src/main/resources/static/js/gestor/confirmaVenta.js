const contextPath  = window.location.href;

$(function() {
    if ($("#msgVenta").text()==="ERROR"){
        $('#formModal').modal({show: true, backdrop: 'static', keyboard: false });
    }
}).on("click",".confirmar-Venta", function(){
    $("#formModal  #idventas").val($(this).data('id'));

    let url = contextPath + "/get?id=" + $(this).data('id');
    console.log(url);
    $.ajax({
        method:"GET", url:url
    }).done(function(ven){
        if (ven!=null){
            $("#formModal #id\\.tipodocumento").val(ven.id.tipodocumento);
        }
    }).fail(function (err) {
        console.log(err);
        $('#formModal').modal('hide');
        alert("Ocurrió un error");
    })
}).on("click", ".denegar-Venta", function () {
    $("#cancelarModal  #idventas").val('').val($(this).data('id'));
}).on("submit","#formModal form", function () {
    if((document.getElementById('foto1').files[0].size*1.0004)>=2097152) {
        $("#foto1").next().remove().end().parent().append("<div class=\"text-danger\">Archivo mayor a 2MB</div>");
        return false;
    }
    return  true;
});
