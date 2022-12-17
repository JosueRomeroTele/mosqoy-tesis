const contextPath  = window.location.href;

$(function() {$("#msgSede").text()==="ERROR" && $('#formModal').modal({show: true, backdrop: 'static', keyboard: false });
}).on("click",".new-Sede", function(){
    $("#formModal").find("#formTitle").text('Nueva Sede');
    $("#formModal").find("input").empty();
    $("#formModal").find("#type").val('1');

}).on("click",".editar-sede",function (){
    $("#formModal").find("input").empty();
    $("#formModal").find("#formTitle").text('Editar Categoría');
    $("#formModal").find("#type").val('0');
    $.ajax({
        method: "get", url: contextPath + "/get?id="+$(this).data('id')
    }).done(function (sede) {
        if (sede!= null){
            $("#formModal #idsede").val(sede.idsede);
            $("#formModal #nombresede").val(sede.nombresede);
        }
    }).fail(function (err) {
        alert("ocurrio un error");
        $('#formModal').modal({show: false});
    })
}).on("click",".borrar-sede",function (){
    let id = $(this).data('id');

    $.ajax({
        method: "get", url: contextPath + "/productosAsignados?id=" + id
    }).done(function (data) {
        if (data==null || data.length===0){
            $("#deleteModal #idsede").val(id);
            $("#deleteModal #deleteModalBody #deleteModalP").text("¿Seguro que desea borrar esta Sede? Esta acción no se puede deshacer.")
            $("#deleteModal #buttonDelete").prop("disabled",false).prop("hidden",false);
        }else{
            $("#deleteModal #deleteModalBody #deleteModalP").text("La sede se encuentra asociada a producto(s)")
            $("#deleteModal #buttonDelete").prop("disabled",true).prop("hidden",true);
        }
    })
});