const contextPath  = window.location.href;
$(function() {$("#msgCategoria").text()==="ERROR" && $('#formModal').modal({show: true, backdrop: 'static', keyboard: false });
}).on("click",".editar-Categoria", function(){
    let formModal =  $("#formModal");
    formModal.find(" input").val('');
    $("#formModal  #type").val('0');
    $("#formModal  #formTitle").text('Editar Categoría');
    $.ajax({method:"GET", url:contextPath+"/get?id="+ $(this).data('id')
    }).done(function(cat){
        if (cat!=null){
            $("#formModal  #codigo").val(cat.codigo).prop("readonly", true);
            $("#formModal #nombre").val(cat.nombre);
        }
    }).fail(function (err) {alert("Ocurrió un error"); $('#formModal').modal({show: false});
    })
}).on("click",".new-Categoria", function(){
    $("#formModal").find(" #formTitle").text('Nueva Categoría').end().find(" input").val('').prop("readonly",false).end()
    .find(" #type").val('1');
}).on("click",".delete-Categoria", function(){
    let id = $(this).data('id');
    $("#deleteModal #buttonDelete").prop("hidden",true);
    $("#deleteModal #deleteModalBody #deleteModalBodyP").text("");
    $("#deleteModal #deleteModalBody #tableModal").prop("hidden",true);

    $.ajax({method:"GET", url:contextPath+"/has?id="+id
    }).done(function(data){
        if (data==null || data.length === 0){
            $("#deleteModal #codigo").val(id);
            $("#deleteModal #deleteModalBody #deleteModalBodyP").text("¿Seguro que desea borrar esta Categoría? Esta acción no se puede deshacer.")
            $("#deleteModal #buttonDelete").prop("disabled",false).prop("hidden",false);
        }
        else {
            $("#deleteModal #deleteModalBody #deleteModalBodyP").text("La categoría no se puede borrar, esta asociada a lo siguiente:")
            $("#deleteModal #deleteModalBody #tableModal").prop("hidden",false);
            $("#deleteModal #buttonDelete").prop("disabled",true).prop("hidden",true);
            let r ='';
            for (let i=0,size=data.length; i<size; i++){
                r+='<tr><td>'+data[i].codigo+'</td><td>'+data[i].producto+'</td><td>'+data[i].cantidad+'</td></tr>';
            }
            $("#deleteModal #tbody").html(r);
        }
    }).fail(function (err) {alert("Ocurrió un error");$('#deleteModal').modal({show: false});
    })
});
