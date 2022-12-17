const contextPath  = window.location.href;

const meses = [
    {numero:1, nombre:"Enero"},{numero:2, nombre:"Febrero"},{numero:3, nombre:"Marzo"},{numero:4, nombre:"Abril"},
    {numero:5, nombre:"Mayo"},{numero:6, nombre:"Junio"},{numero:7, nombre:"Julio"},{numero:8, nombre:"Agosto"},
    {numero:9, nombre:"Setiembre"},{numero:10, nombre:"Octubre"},{numero:11, nombre:"Noviembre"},{numero:12, nombre:"Diciembre"}
];

const trimestres = [
    {numero:1, nombre:"En-Feb-Mar"},{numero:2, nombre:"Abr-May-Jun"},
    {numero:3, nombre:"Jul-Ago-Set"},{numero:4, nombre:"Oct-Nov-Dic"}
];

const tipos = {'2':trimestres,'3':meses};

$(function () {
    let $ventaModal = $("#reporteVentaModal");

    $.ajax({method:"GET",url:contextPath+"/years"})
        .done(function (years) {
            let r = '';
            $.each(years, function () {
                r+=`<option value="${this}">${this}</option>`;
            });
            $ventaModal.find(" #years").append(r);
        })
        .fail(function(err){
            alert("Ocurrio un error: "+ err);
        });

    $("body").on('click','.reporte-Ventas',function () {
        $ventaModal.find(" #fields1 select").val(0);
        $ventaModal.find(" #fields2").prop("hidden",true).find(" #tipoSelectDiv").prop("hidden",true);
        }
    ).on('submit','#reporteForm', function () {
        if((parseInt($ventaModal.find(" #fields1 #ordenar").children("option:selected").val())===0)
            || (parseInt($ventaModal.find(" #fields1 #tipo").children("option:selected").val())===0)
            || (parseInt($ventaModal.find(" #fields1 #nombreselect").children("option:selected").val())===0)
            || (parseInt($ventaModal.find(" #fields2 #years").children("option:selected").val())===0)
        ){
            alert("Debe seleccionar todos los campos para generar un reporte");
            return false;
        }
    });
    
    $ventaModal.find(" #tipo").on('change',function () {
        let val = this.value;
        if(val!=='0'){
            $ventaModal.find(" #fields2").prop("hidden",false).find(" #tipoSelectDiv").prop("hidden",val==='1');
            if(val!=='1'){
                let nombre;
                switch (val) {
                    case '2':
                        nombre='Trimestre';break;
                    case '3':
                        nombre='Mes';break;
                }
                let r ='';
                $.each(tipos[val],function () {
                    r+=makeOption(this);
                });
                $ventaModal.find(" #tipoSelectDiv").find(" label").html(nombre).end()
                    .find(" #tipoSelect").empty().append(r);
            }
        }
    })
    
});

$("#reporteForm #ordenar").on('change',function () {
    let nombreList = $("#reporteForm #nombreselect");
    let data = this.value;
    if (data == 0){
        nombreList.empty();
        nombreList.append("<option value = '0' >-Debe Selecicionar un tipo-</option>")
    }else{

            if (data==1){
                nombreList.empty();
                nombreList.append("<option value='1'> </option>");
                $('#nombredivSelect').prop("hidden",true);
            }
            if (data == 2){

                $('#nombredivSelect').prop("hidden",false);

                $.ajax({
                    method: "get", url: contextPath + "/sedeVentas"
                }).done(function (list) {
                    if (list!=null){
                        let len = list.length;
                        nombreList.empty();
                        if (list.length !==0){
                            nombreList.append("<option value='0'> --- Seleccionar una sede ---</option>");
                            for (let i = 0; i<len;i++){
                                nombreList.append("<option value='"+ list[i].idsede+"'>"+ list[i].nombresede +"</option>");
                            }
                        }else{
                            nombreList.append("<option value='0'>--- No hay ventas en sedes ---</option>");
                        }
                    }
                }).fail(function (err) {
                    console.log(err);
                    alert("Ocurrio un error");
                })
            }

            if (data==3){
                $('#nombredivSelect').prop("hidden",false);
                $.ajax({
                    method: "get", url: contextPath + "/productosVentas"
                }).done(function (list) {
                    if (list!=null){
                        let len = list.length;
                        nombreList.empty();
                        if (list.length !==0){
                            nombreList.append("<option value='0'> --- Seleccionar el producto ---</option>");
                            for (let i = 0; i<len;i++){
                                nombreList.append("<option value='"+ list[i].codigonom+list[i].codigolinea+"'>"+ list[i].nombre +"</option>");
                            }
                        }else{
                            nombreList.append("<option value='0'>--- No hay productos vendidos ---</option>");
                        }
                    }
                }).fail(function (err) {
                    console.log(err);
                    alert("Ocurrio un error");
                })
            }
            if (data==4){
                $('#nombredivSelect').prop("hidden",false);
                $.ajax({
                    method: "get", url: contextPath + "/comunidadesVentas"
                }).done(function (list) {
                    if (list!=null){
                        let len = list.length;
                        nombreList.empty();
                        if (list.length !==0){
                            nombreList.append("<option value='0'> --- Seleccionar la comunidad ---</option>");
                            for (let i = 0; i<len;i++){
                                nombreList.append("<option value='"+ list[i].codigo+"'>"+ list[i].nombre +"</option>");
                            }
                        }else{
                            nombreList.append("<option value='0'>--- No hay comunidades con ventas ---</option>");
                        }
                    }
                }).fail(function (err) {
                    console.log(err);
                    alert("Ocurrio un error");
                })
            }

            if (data==5){
                $('#nombredivSelect').prop("hidden",false);
                $.ajax({
                    method: "get", url: contextPath + "/clientesVentas"
                }).done(function (list) {
                    if (list!=null){
                        let len = list.length;
                        nombreList.empty();
                        if (list.length !==0){
                            nombreList.append("<option value='0'> --- Seleccionar el cliente ---</option>");
                            for (let i = 0; i<len;i++){
                                nombreList.append("<option value='"+ list[i].idventas+"'>"+ list[i].nombrecliente +"</option>");
                            }
                        }else{
                            nombreList.append("<option value='0'>--- No hay clientes con ventas ---</option>");
                        }
                    }
                }).fail(function (err) {
                    console.log(err);
                    alert("Ocurrio un error");
                })
            }





    }

})



function makeOption(x) {
    return '<option value="'+x.numero +'" >'+x.nombre + '</option> ';
}