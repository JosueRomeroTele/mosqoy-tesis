const contextPath  = window.location.href;
$(function() {($("#msgComunidad").text()==="ERROR") && $('#formModal').modal({show: true, backdrop: 'static', keyboard: false });
}).on("click",".editar-Comunidad", function(){
    $("#formModal  input").val('');
    $("#formModal  #type").val('0');
    $.ajax({method:"GET", url:contextPath + "/get?id=" + $(this).data('id')
    }).done(function(com){
        if (com!=null){
            $("#formModal  #codigo").val(com.codigo).prop("readonly", true);
            $("#formModal  #nombre").val(com.nombre);
            $("#formModal  #formTitle").text('Editar Comunidad');
            $("#formModal  #formSavebtn").text('Actualizar');
        }
    }).fail(function (err) {alert("Ocurrió un error");$('#formModal').modal({show: false});})
}).on("click",".new-Comunidad", function(){
    $("#formModal").find(" #formTitle").text('Nueva Comunidad').end().find(" input").val('').prop("readonly",false).end()
        .find(" #type").val('1').end().find("  #formSavebtn").text('Registrar');
}).on("click",".delete-Comunidad", function(){
    let id = $(this).data('id');
    $("#deleteModal #buttonDelete").prop("hidden",true);
    $("#deleteModal #deleteModalBody #deleteModalBodyP").text("");
    $("#deleteModal #deleteModalBody #tableModal").prop("hidden",true);
    $.ajax({method:"GET", url:contextPath+"/has?id="+id
    }).done(function(data){
        if (data==null || data.length === 0){
            $("#deleteModal #codigo").val(id);
            $("#deleteModal #deleteModalBody #deleteModalBodyP").text("¿Seguro que desea borrar esta Comunidad? Esta acción no se puede deshacer.")
            $("#deleteModal #buttonDelete").prop("disabled",false).prop("hidden",false);
        }
        else {
            $("#deleteModal #deleteModalBody #deleteModalBodyP").text("La comunidad no se puede borrar, está asociada a " + data.length + " artesanos:")
            $("#deleteModal #deleteModalBody #tableModal").prop("hidden",false);
            $("#deleteModal #buttonDelete").prop("disabled",true).prop("hidden",true);
            let r = '';
            for (let i=0,size=data.length; i<size; i++){
                r +='<tr><td>'+data[i].codigo+'</td><td>'+data[i].nombre+'</td><td>'+data[i].apellido+'</td></tr>';
            }
            $("#deleteModal #tbody").html(r);
        }
    }).fail(function (err) {alert("Ocurrió un error");$('#deleteModal').modal({show: false});
    })
});