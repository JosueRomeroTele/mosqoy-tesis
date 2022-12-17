var contextPath  = window.location.href+"/get?id=";

$(document).on("click",".editar-sede", function(){
    $("#formModal  #idusuarios").val(' ');$("#formModal  #nombre").val('');$("#formModal  #apellido").val('');$("#formModal  #correo").val('');$("#formModal  #telefono").val('');

    $.ajax({
        method:"GET", url:contextPath + $(this).data('id')
    }).done(function(sed){
        if (sed!=null){
            $("#formModal  #idusuarios").val(sed.idusuarios);
            $("#formModal #nombre").val(sed.nombre);
            $("#formModal #apellido").val(sed.apellido);
            $("#formModal #correo").val(sed.correo);
            $("#formModal #telefono").val(sed.telefono);

        }
    }).fail(function (err) {
        console.log(err);
        $('#formModal').modal('hide');
        alert("Ocurrió un error");
    })
});
$(document).on("click",".delete-sede", function(){
    $("#deleteModal #idusuarios").val($(this).data('id'));
});

$(document).ready(function() {
    if ($("#msgSede").text()==="ERROR"){
        $('#formModal').modal('show');
    }
});