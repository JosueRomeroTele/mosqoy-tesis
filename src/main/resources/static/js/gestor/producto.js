const contextPath  = window.location.href;
$(function() {($("#msgProducto").text()==="ERROR") && $('#formModal').modal({show: true, backdrop: 'static', keyboard: false });
}).on("click",".edit-Producto", function(){
    $("#formModal input").val('');
    $("#formModal  #type").val('0');
    $.ajax({method:"GET", url:contextPath +"/get?codigonom="+$(this).data('id1')+"&codigolinea="+$(this).data('id2')
    }).done(function(producto){
        if (producto!=null){
            $("#formModal #id\\.codigonom").val(producto.id.codigonom).prop("readonly", true);
            $("#formModal #nombre").val(producto.nombre);
            $("#formModal #codigodesc").val(producto.codigodesc);
            $("#formModal #descripcion").val(producto.descripcion);
            $("#formModal #id\\.codigolinea").val(producto.id.codigolinea);
            $("#formModal  #formTitle").text('Editar Producto');
        }
    }).fail(function (err) {alert("Ocurrió un error");$('#formModal').modal({show: false});
    })
}).on("click",".new-Producto", function(){
    $("#formModal").find(" #formTitle").text('Nuevo Producto').end().find(" input").val('').prop("readonly",false).end()
        .find(" #type").val('1');
}).on("click",".delete-Producto", function(){
    let id1 = $(this).data('id1');
    let id2 = $(this).data('id2');
    $("#deleteModal #buttonDelete").prop("hidden",true);
    $("#deleteModal #deleteModalBody #deleteModalBodyP").text("");
    $("#deleteModal #deleteModalBody #tableModal").prop("hidden",true);

    $.ajax({method:"GET", url:contextPath+"/has?codigonom="+id1+"&codigolinea="+id2
    }).done(function(data){
        if (data==null || data.length === 0){
            $("#deleteModal #id\\.codigonom").val(id1);
            $("#deleteModal #id\\.codigolinea").val(id2);
            $("#deleteModal #deleteModalBody #deleteModalBodyP").text("¿Seguro que desea borrar este Producto? Esta acción no se puede deshacer.");
            $("#deleteModal #buttonDelete").prop("disabled",false).prop("hidden",false);
        }
        else {
            $("#deleteModal #deleteModalBody #deleteModalBodyP").text("El producto no se puede borrar, está asociado a "+data.length+" elementos de inventario:");
            $("#deleteModal #deleteModalBody #tableModal").prop("hidden",false);
            $("#deleteModal #buttonDelete").prop("disabled",true).prop("hidden",true);
            let r='';
            for (let i=0, size=data.length; i<size; i++){
                r+='<tr><td>'+data[i].codigo+'</td><td>'+data[i].comunidad+'</td><td>'+data[i].categoria+'</td></tr>';
            }
            $("#deleteModal #tbody").html(r);
        }
    }).fail(function (err) {alert("Ocurrió un error");$('#deleteModal').modal({show: false});
    })
});










