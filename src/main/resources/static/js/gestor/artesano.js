const contextPath  = window.location.href;
$(function(){ $("#msgArtesanos").text()==="ERROR" && $('#editModal').modal({show: true, backdrop: 'static', keyboard: false });
}).on("click",".edit-Artesano", function(){let editModal=$("#editModal");
    editModal.find(" #formTitle").text('Editar Artesano').end().find(" input").val('').end().find(" #type").val('0');
    $.ajax({ method:"GET", url:contextPath+"/get?id="+$(this).data('id')})
        .done(function(artesano){
            if (artesano!=null) editModal
                .find(" #nombre").val(artesano.nombre).end()
                .find(" #apellidopaterno").val(artesano.apellidopaterno).end()
                .find(" #apellidomaterno").val(artesano.apellidomaterno).end()
                .find(" #comunidades").val(artesano.comunidades.codigo).end()
                .find(" #codigo").val(artesano.codigo).prop("readonly", true);})
        .fail(function (err) {alert("Ocurrió un error");editModal.modal({show: false});})
}).on("click",".new-Artesano", function(){
    $("#editModal").find(" #formTitle").text('Nuevo Artesano').end().find(" input").val('').prop("readonly", false)
        .end().find(" #type").val('1');
}).on("click",".delete-Artesano", function(){let id=$(this).data('id'),dModal=$("#deleteModal");dModal
    .find(" #buttonDelete").prop("hidden",true).end()
    .find(" #deleteModalBody #deleteModalBodyP").text("").end()
    .find(" #deleteModalBody #tableModal").prop("hidden",true);
    $.ajax({method:"GET", url:contextPath+"/has?id="+id})
        .done(function(data){
            if (data==null || data.length === 0) dModal
                .find(" #codigo").val(id).end()
                .find(" #deleteModalBody #deleteModalBodyP").text("¿Seguro que desea borrar este Artesano? Esta acción no se puede deshacer.").end()
                .find(" #buttonDelete").prop("disabled",false).prop("hidden",false);
            else dModal
                .find(" #deleteModalBody #deleteModalBodyP").text("El/La Artesana(o) no se puede borrar, esta asociada(o) a estos productos de inventario:").end()
                .find(" #deleteModalBody #tableModal").prop("hidden",false).end()
                .find(" #tbody").html((function(d){let r='';$.each(d, function (i,l)
                {r+='<tr><td>'+l.codigo+'</td><td>'+l.producto+'</td><td>'+l.cantidad+'</td></tr>'});return r;})(data)).end()
                .find(" #buttonDelete").prop("disabled",true).prop("hidden",true);})
        .fail(function (err) {alert("Ocurrió un error");dModal.modal({show: false});})
});