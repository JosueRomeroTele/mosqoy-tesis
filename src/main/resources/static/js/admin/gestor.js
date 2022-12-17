const contextPath  = window.location.href;
$(function() {($("#msgGestores").text()==="ERROR") && $('#formModal').modal({show: true, backdrop: 'static', keyboard: false });
}).on("click",".edit-Gestor", function(){
    $("#formModal input").val('');
    $("#formModal .text-danger").hide();
    $("#formModal  #type").val('0');
    $("#formModal  #formTitle").text('Editar Gestor');
    $.ajax({
        method:"GET", url:contextPath+"/get?id="+ $(this).data('id')
    }).done(function(gestor){
        if (gestor!=null){
            $("#formModal #idusuarios").val(gestor.idusuarios).prop("readonly", true);
            $("#formModal #nombre").val(gestor.nombre);
            $("#formModal #apellido").val(gestor.apellido);
            $("#formModal #correo").val(gestor.correo);
            $("#formModal #telefono").val(gestor.telefono);
            $("#formModal #foto").attr("src","/admin/gestor/fotoUser?id="+gestor.idusuarios);

        }
    }).fail(function (err) {alert("Ocurrió un error");$('#formModal').modal({show: false});
    })
}).on("click",".new-Gestor", function(){
    $("#formModal .text-danger").hide();
    $("#formModal input").val('').prop("readonly",false);
    $("#formModal #foto").attr("hidden",true);
    $("#formModal  #type").val('1');
    $("#formModal  #formTitle").text('Nuevo Gestor');

}).on("click",".delete-Gestor", function(){
    let id = $(this).data('id');
    $("#deleteModal #buttonDelete").prop("disabled",true).prop("hidden",true);
    $("#deleteModal #deleteModalBody #deleteModalBodyP").text("");
    $("#deleteModal #deleteModalBody table").prop("hidden",true);

    $.ajax({
        method:"GET", url:contextPath+"/has?id="  + id
    }).done(function(data){
        if (data==null || data[0].length === 0 && data[1].length === 0){
            $("#deleteModal #idusuarios").val(id);
            $("#deleteModal #deleteModalBody #deleteModalBodyP").text("¿Seguro que desea borrar este Gestor? Esta acción no se puede deshacer.");
            $("#deleteModal #buttonDelete").prop("disabled",false).prop("hidden",false);
        }
        else {
            $("#deleteModal #deleteModalBody #deleteModalBodyP").text("El gestor no se puede borrar, está asociada a los siguientes Ventas y/o Productos Asignados");
            let r = '';
            if(data[0].length!==0){
                for (let key=0, size=data[0].length; key<size; key++)
                    r+='<tr><td>'+data[0][key].inventario+'</td><td>'+data[0][key].rucdni+'</td><td>'+data[0][key].cliente+'</td></tr>';
                $("#deleteModal #deleteModalBody #tableModal1").prop("hidden",false);
                $("#deleteModal #tbody").html(r);
            }
            if(data[1].length!==0){
                r = '';
                for (let key=0, size=data[1].length; key<size; key++)
                    r+='<tr><td>'+data[1][key].inventario+'</td><td>'+data[1][key].stock+'</td><td>'+data[1][key].sede+'</td></tr>';
                $("#deleteModal #deleteModalBody #tableModal2").prop("hidden",false);
                $("#deleteModal #tbody2").html(r);
            }
        }
    }).fail(function (err) {alert("Ocurrió un error");$('#deleteModal').modal({show: false});
    });
}).on("submit","#formModal form", function () {
    if((document.getElementById('photo').files[0].size*1.0004)>=2097152) {
        $("#photo").next().remove().end().parent().append("<div class=\"text-danger\">Archivo mayor a 2MB</div>");
        return false;
    }
    return  true;
});