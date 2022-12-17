var contextPath  = window.location.href;

$(document).on("click", ".confirmar", function () {
    $("#confirmarModal  #id2").val('');

    $("#confirmarModal  #id2").val($(this).data('id2'));
});

$(document).on("click", ".rechazar", function () {
    $("#rechazarModal  #id3").val('');

    $("#rechazarModal  #id3").val($(this).data('id3'));
});

