    var contextPath  = window.location.href;

$(document).on("click",".edit-Tienda", function(){
    $("#EditarTiendaModal  #idtienda").val('');
    $("#EditarTiendaModal  #nombre").val('');
    $("#EditarTiendaModal  #ruc").val('');
    $("#EditarTiendaModal  #direccion").val('');
    $("#EditarTiendaModal  #type").val('0');
    $("#EditarTiendaModal  #formTitle").text('Editar Tienda');
    $.ajax({
        method:"GET", url:contextPath+"/get?id=" +$(this).data('id')
    }).done(function(tienda){
        if (tienda!=null){
            $("#EditarTiendaModal  #idtienda").val(tienda.idtienda);
            $("#EditarTiendaModal  #nombre").val(tienda.nombre);
            $("#EditarTiendaModal  #ruc").val(tienda.ruc);
            $("#EditarTiendaModal  #direccion").val(tienda.direccion);
        }
    }).fail(function (err) {
        console.log(err);
        $('#EditarTiendaModal').modal('hide');
        alert("Ocurrió un error");
    })
});
$(document).on("click",".new-Tienda", function(){
    $("#EditarTiendaModal  #idtienda").val('0');
    $("#EditarTiendaModal  #nombre").val('');
    $("#EditarTiendaModal  #ruc").val('');
    $("#EditarTiendaModal  #direccion").val('');
    $("#EditarTiendaModal  #type").val('1');
    $("#EditarTiendaModal  #formTitle").text('Nueva Tienda');
});
$(document).on("click",".delete-Tienda", function(){
    let id = $(this).data('id');
    $("#deleteTiendaModal #buttonDelete").prop("hidden",true);
    $("#deleteTiendaModal #deleteModalBody #deleteModalBodyP").text("");
    $("#deleteTiendaModal #deleteModalBody #tableModal").prop("hidden",true);

    $.ajax({
        method:"GET", url:contextPath+"/has?id=" + id
    }).done(function(data){
        if (data==null || data.length === 0){
            $("#deleteTiendaModal #idtienda").val(id);
            $("#deleteTiendaModal #deleteModalBody #deleteModalBodyP").text("¿Seguro que desea borrar esta Tienda? Esta acción no se puede deshacer.");
            $("#deleteTiendaModal #buttonDelete").prop("disabled",false).prop("hidden",false);
        }
        else {
            $("#deleteTiendaModal #deleteModalBody #deleteModalBodyP").text("La Tienda no se puede borrar, esta asociada a lo siguiente:");
            $("#deleteTiendaModal #deleteModalBody #tableModal").prop("hidden",false);
            $("#deleteTiendaModal #buttonDelete").prop("disabled",true).prop("hidden",true);
            let r = [], j = -1;
            for (let key=0, size=data.length; key<size; key++){
                r[++j] ='<tr><td>';
                r[++j] = data[key].gestor;
                r[++j] = '</td><td>';
                r[++j] = data[key].codigo;
                r[++j] = '</td><td>';
                r[++j] = data[key].stock;
                r[++j] = '</td></tr>';
            }
            $("#deleteTiendaModal #tbody").html(r.join(''));
        }
    }).fail(function (err) {
        console.log(err);
        $('#editModal').modal('hide');
        alert("Ocurrió un error");
    })


});
$(document).ready(function() {
    console.log("Hi");
    if ($("#msgListaTiendas").text()==="ERROR"){
        $('#EditarTiendaModal').modal({show: true, backdrop: 'static', keyboard: false });
    }
});

