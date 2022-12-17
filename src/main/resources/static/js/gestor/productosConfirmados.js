$(function () {

    let $checkbox = $("#confirmado1");
    console.log($checkbox.is(":checked"));
    $("#id\\.numerodocumento").val('').prop("disabled",!$checkbox.is(":checked"));
    $(".inputFile").prop("hidden",!$checkbox.is(":checked"));

    $("body").on('change','#confirmado1', function () {
        $("#id\\.numerodocumento").prop("disabled",!this.checked);
        $(".inputFile").prop("hidden",!this.checked);
    })
}).on("submit","#registraVentaForm", function () {
    if((document.getElementById('foto1').files[0].size*1.0004)>=2097152) {
        $("#foto1").next().remove().end().parent().append("<div class=\"text-danger\">Archivo mayor a 2MB</div>");
        return false;
    }
    return  true;
});