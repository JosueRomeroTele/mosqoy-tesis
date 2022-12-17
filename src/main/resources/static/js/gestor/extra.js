$(document).ready(function () {
    $(".nav-item i").prop("hidden",false);
    let tableContainer = $(".table-responsive");
    let fakeContainer = $(".fakescroll");
    updatefakeScroll();
    fakeContainer.scroll(function () {
        tableContainer.scrollLeft(fakeContainer.scrollLeft());
    });
    tableContainer.scroll(function () {
        fakeContainer.scrollLeft(tableContainer.scrollLeft());
    });
    let tablawrapper = $("#dataTable_wrapper");
    tablawrapper.children().eq(0).appendTo("#newFilterLength");
    tablawrapper.children().eq(1).appendTo("#newtablefoot");
    $("#dataTable_filter label input").on('input', function () {
        updatefakeScroll();
    });
    $(".card-body").on('click', function () {
        updatefakeScroll();
    });
    refreshimg();
    $(document).on("click", ".show-foto", function () {
        let showfoto = $("#showFoto #fotoinv");
        showfoto.attr("src", "");
        $("#showFoto #fototitle").text($(this).data('id'));
        let id = '#' + $(this).data('id') + 'photo';
        let url = $(id).attr('src');
        showfoto.attr("src", url);
    });
    $(document).on("click", ".show-fotoU", function () {
        let showfoto = $("#showFotoU #fotoinv");
        showfoto.attr("src", "");
        $("#showFotoU #fototitle").text($('#' + $(this).data('id') + 'name').text() + " " + $('#' + $(this).data('id') + 'last').text());
        let id = '#' + $(this).data('id') + 'photo';
        let url = $(id).attr('src');
        showfoto.attr("src", url);
    });
    $(document).on("mouseover", function () {
        refreshimg();
    }).on("mouseout", function () {
        refreshimg();
    }).on("click", function () {
        refreshimg();
    });
});
$(window).resize(function () {
    updatefakeScroll();
});
function updatefakeScroll() {
    let fakeDiv = $(".fakescroll div");
    let table = $(".table-responsive table");
    let tableWidth = table.width();
    fakeDiv.width(tableWidth);
}
function refreshimg() {
    $('.show-fotoU').removeAttr('disabled');
    $('.show-foto').removeAttr('disabled');
    $('.show-foto').parent().addClass("tdfoto");
    $('.show-fotoU').parent().addClass("tdfoto");
    let imgTabla = $('.table-responsive img');
    imgTabla.removeClass();
    imgTabla.each(function () {
        if ($(this).attr('src') == 'https://storage-service.mosqoy-sw2.dns-cloud.net/profile/defaultProfilePicture.jpg') {
            $(this).parent().prop('disabled', true);
        }
    });
    imgTabla.addClass("fototabla");
    imgTabla.removeAttr('height');
    imgTabla.removeAttr('width');
    imgTabla.removeAttr('style');
    imgTabla.removeAttr('alt');
    imgTabla.attr('alt', "No disponible");
}
