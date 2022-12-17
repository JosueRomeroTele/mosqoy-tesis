var contextPath = window.location.href;
$(document).ready(function () {
    $("#addForm #invAdd").attr("disabled", false);
    $("#addForm #conDia").attr("disabled", false);
    $("#addForm #linea").attr("disabled", false);
    $("#addForm #codAdquisicion").attr("disabled", false);
    $("input[hidden='hidden']").removeAttr("required");
}).on("submit","form", function () {


    if ((document.getElementById('foto1').files[0])==null){
        return true
    }else{
        if((document.getElementById('foto1').files[0].size*1.0004)>=2097152 ) {
            $("#foto1").next().remove().end().parent().append("<div class=\"text-danger\">Archivo mayor a 2MB</div>");
            return false;
        }
    }
    return  true;
});
$("#addForm #linea").on('change', function () {
    let prodList = $("#addForm #productos");
    let option = this.value;
    if (option === "no") {
        prodList.empty();
        prodList.append("<option value=''>-Debe elegir una línea-</option>");
    } else {
        $.ajax({
            method: "GET", url: contextPath + "/getLinea?linea=" + option
        }).done(function (lista) {
            if (lista != null) {
                let len = lista.length;
                prodList.empty();
                if (lista.length !== 0) {
                    prodList.append("<option value=''>--- Seleccionar ---</option>");
                    for (let i = 0; i < len; i++) {
                        prodList.append("<option value='" + lista[i].codigonom + "'>" + lista[i].nombre + "</option>");
                    }
                } else {
                    prodList.append("<option value=''>--- No tiene productos ---</option>");
                }
            }
        }).fail(function (err) {
            console.log(err);
            alert("Ocurrió un error");
        })
    }
});

$("#addForm #codAdquisicion").on('change', function () {
    let cond = this.value === '0';
    $("#addForm #artesanoConsignacion").prop("hidden", cond).prop("disabled", cond);
    $("#addForm #vencimientoConsignacion").prop("hidden", cond).prop("disabled", cond);
    $("#addForm #vencimientoConsignacion input").prop("required", !cond);

    if (cond) {
        $("#addForm #artesanos").empty();
    } else {
        let option = $("#addForm #comunidades").val();
        updateArtesanos(option);
    }
});
$("#addForm #comunidades").on('change', function () {
    let option = this.value;
    let cond = ($("#addForm #codAdquisicion").val() === '1');
    if (cond && !$("#addForm #artesanoConsignacion").prop("hidden")) {
        updateArtesanos(option);
    }
});
$("#costotejedor").on('change', function () {
    let tej = (parseFloat(this.value) + 0.01).toFixed(2);
    $("#costomosqoy").attr("min",tej);
});


function updateArtesanos(option) {
    let artList = $("#addForm #artesanos");
    if (option === '') {
        artList.empty();
        artList.append("<option value=''>-Debe elegir una comunidad-</option>");
    } else {
        $.ajax({
            method: "GET", url: contextPath + "/getArtesanos?comunidad=" + option
        }).done(function (lista) {
            if (lista != null) {
                let len = lista.length;
                artList.empty();
                if (lista.length !== 0) {
                    artList.append("<option value=''>--- Seleccionar ---</option>");
                    for (let i = 0; i < len; i++) {
                        artList.append("<option value='" + lista[i].codigo + "'>" + lista[i].nombre + " " + lista[i].apellidopaterno + "</option>");
                    }
                } else {
                    artList.append("<option value=''>--- No tiene artesanos ---</option>");
                }
            }

        }).fail(function (err) {
            alert("Ocurrió un error");
        })
    }
}

$("#addForm #conDia").on('change', function () {

    $("#addForm #fechadia").prop("hidden", !this.checked).prop("disabled", !this.checked).prop("required", this.checked);
    $("#addForm #fechames").prop("hidden", this.checked).prop("disabled", this.checked).prop("required", !this.checked);

});
$("#addForm #fechadia").on('change', function () {
    let fecha = $("#addForm #fechadia").val().split("-");
    let fechasig = new Date(fecha[0], fecha[1] - 1, fecha[2]);
    fechasig.setDate(fechasig.getDate() + 1);
    setminfecha(fechasig);

});
$("#addForm #fechames").on('change', function () {

    let fecha = $("#addForm #fechames").val().split("-");
    let fechasig = new Date(fecha[0], fecha[1], 1);
    setminfecha(fechasig);
});
$("#addForm").on('submit', function () {
    if ($('#confirmModal').hasClass("show")) {
        return true;
    } else {
        $('#confirmModal').modal({show: true, backdrop: 'static', keyboard: false});
        return false;
    }
});

$("#confirmbtn").on('click', function () {
    $("#addForm").submit();
});

$(document).on("click", ".add-inventario", function () {
    $("#addModal #cant").val('1');
    $("#addModal #msgc").text('');
    $("#addModal #codinvAdd").val($(this).data('id'));
    $("#addModal #addTitle").text("Añadir " + $(this).data('id'));

});
$(document).on("click", ".edit-inventario", function () {
    $("#editModal #costotejedor").val('');
    $("#editModal #costomosqoy").val('');
    $("#editModal #facilitador").val('');
    $("#editModal #fechavencimientoconsignacion").val('');

    $("#editModal #codAdquisicion").val('');

    $("#editModal .errorInv").text('');
    $("#editModal .modal-title").text("Editar " + $(this).data('id'));
    $("#editModal #codigoinventario").val($(this).data('id'));
    $("#editModal #vencimientoConsignacion").prop("hidden", true);

    $.ajax({
        method: "GET", url: "/gestor/inventario/getInv?id=" + $(this).data('id')
    }).done(function (inv) {
        if (inv != null) {
            $("#editModal #codAdquisicion").val(inv.codAdquisicion);
            $("#editModal #costotejedor").val(inv.costotejedor);
            let tej = (parseFloat(inv.costotejedor) + 0.01).toFixed(2);
            $("#editModal #costomosqoy").val(inv.costomosqoy).prop("min",tej);
            $("#editModal #facilitador").val(inv.facilitador);
            if (inv.codAdquisicion === 1) {
                $("#editModal #vencimientoConsignacion").prop("hidden", false);
                $("#editModal #fechavencimientoconsignacion").val(inv.fechavencimientoconsignacion);
                let fechasig;
                if (inv.dia === 0) {
                    fechasig = new Date(inv.anho, inv.mes, 1);
                } else {
                    fechasig = new Date(inv.anho, inv.mes - 1, inv.dia);
                    fechasig.setDate(fechasig.getDate() + 1);
                }
                setminfecha(fechasig);
            }
        }
    }).fail(function (err) {
        alert("Ocurrió un error");
        $('#editModal').modal('hide');
    })
});

function setminfecha(fechasig) {
    let month = fechasig.getMonth() + 1;
    if (month < 10) {
        fechastr = fechasig.getFullYear() + "-0" + month;
    } else {
        fechastr = fechasig.getFullYear() + "-" + month;
    }
    if (fechasig.getDate() < 10) {
        fechastr = fechastr + "-0" + fechasig.getDate();
    } else {
        fechastr = fechastr + "-" + fechasig.getDate();
    }
    $("#fechavencimientoconsignacion").prop("min", fechastr);
}

$(document).ready(function () {
    if ($("#msgError").text() === "ERROR DE EDICION") {
        //$('#formModal').modal('show');
        $('#editModal').modal({show: true, backdrop: 'static', keyboard: false});
    }
    if ($("#msgError").text() === "ERROR DE CANTIDAD") {
        //$('#formModal').modal('show');
        $('#addModal').modal({show: true, backdrop: 'static', keyboard: false});
    }
    refreshbtn();
});
$(document).on("mouseover", function () {
    refreshbtn();
}).on("mouseout", function () {
    refreshbtn();
});
function refreshbtn(){
    $(".edit-inventario").attr("disabled", false);
    $(".delete-inventario").attr("disabled", false);
    $(".add-inventario").attr("disabled", false);
    $(".devolver-inventario").attr("disabled", false);
    $(".add-foto-inventario").attr("disabled", false);
}
$(document).on("click",".add-foto-inventario",function () {
    $("#addFotoInvModal #codigoinventario").val($(this).data('id'));
});
$(document).on("click",".devolver-inventario",function () {
    $("#devolverArtesanoModal #codigoinventario").val($(this).data('id'));

    $.ajax({
        method: "GET", url: "/gestor/inventario/getAsigSede?id=" + $(this).data('id')
    }).done(function (inv) {
        if ( inv==null || (inv[0].length === 0 && inv[1].length === 0)) {
            $("#devolverArtesanoModal #devolverArtesanoModalBody p").text("¿Estas seguro de devolver el producto al artesano?, esta opcion no se puede deshacer");

        }else{
            $("#devolverArtesanoModal #devolverArtesanoModalBody p").html("Aún hay productos que se encuentran en sedes<br> Revisar la tabla inferior de los detalles del inventario para saber dónde se encuentra");

            $("#devolverArtesanoModal #canceldevartesano").text("retroceder");
            $("#devolverArtesanoModal #devartesano").prop("disabled",true).prop("hidden",true);
        }
    }).fail(function (err) {
        alert("Ocurrió un error");
        $('#devolverArtesanoModal').modal('hide');
    })

});
$(document).on("click", ".delete-inventario", function () {
    $("#deleteModal .modal-title").text("Borrar " + $(this).data('id'));

    let id = $(this).data('id');
    $("#deleteModal #buttonDelete").prop("hidden",true).prop("disabled",true);
    $("#deleteModal #deleteModalBody #deleteModalBodyP").text("");
    $("#deleteModal #deleteModalBody #tableModal1").prop("hidden",true);
    $("#deleteModal #deleteModalBody #tableModal2").prop("hidden",true);
    $("#deleteModal #vent").prop("hidden",true);
    $("#deleteModal #asig").prop("hidden",true);
    $.ajax({
        method:"GET", url:"/gestor/inventario/has?id="  + id
    }).done(function(data){
        console.log(data);
        if (data==null || data[0].length === 0 && data[1].length === 0){
            $("#deleteModal #codigoinventario").val(id);
            $("#deleteModal #deleteModalBody #deleteModalBodyP").text("¿Seguro que desea borrar este registro del inventario? Esta acción no se puede deshacer.");
            $("#deleteModal #buttonDelete").prop("disabled",false).prop("hidden",false);
        }
        else {
            $("#deleteModal #deleteModalBody #deleteModalBodyP").text("Este registro no se puede borrar, ya que está asociado a las siguientes Ventas y/o Productos Asignados");
            let r = '';
            if(data[0].length!==0){
                for (let key=0, size=data[0].length; key<size; key++)
                    r+='<tr><td>'+data[0][key].sede+'</td><td>'+data[0][key].stock+'</td><td>'+data[0][key].envio+'</td><td>'+data[0][key].precio+'</td></tr>';
                $("#deleteModal #deleteModalBody #tableModal1").prop("hidden",false);
                $("#deleteModal #asig").prop("hidden",false);
                $("#deleteModal #tbody1").html(r);
            }
            if(data[1].length!==0){
                r = '';
                for (let key=0, size=data[1].length; key<size; key++)
                    r+='<tr><td>'+data[1][key].numdocumento+'</td><td>'+data[1][key].vendedor+'</td><td>'+data[1][key].fechaVenta+'</td></tr>';
                $("#deleteModal #deleteModalBody #tableModal2").prop("hidden",false);
                $("#deleteModal #vent").prop("hidden",false);
                $("#deleteModal #tbody2").html(r);
            }
        }
    }).fail(function (err) {alert("Ocurrió un error");$('#deleteModal').modal({show: false});
    });

});


