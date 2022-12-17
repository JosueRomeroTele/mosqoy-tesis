/*const BASE = window.location.href.split("/")[0]+"//"+window.location.href.split("/")[2]+"/notification";
const unread = 'style="background:rgba(148, 244, 255, 0.8)"';
const maxHeight = 400;
$(function () {
    //Load notifications
    $.ajax({method:"GET", url:BASE+"/number"})
        .done(function (value) {
            (value>0) && $("#notification").find(" #number").text(value);

            $.ajax({method:"GET",url:BASE+"/seen"})
                .fail(function (err) {
                    console.log(err);
                })
        })
        .fail(function (err) {
            alert("Ocurrió un error al cargar la página");
        });
    $('.dropdownButton').on('click',function () {
        $("#notification").find(" #number").text('');
        !document.getElementById("notification").classList.contains("show") &&
            $.ajax({method:"GET", url:BASE+"/shortList"})
            .done(function (shortList) {
                let dropdown = $("#notificationBody");
                dropdown.empty();
                $.each( shortList.reverse(),function () {
                    dropdown.prepend(
                        '<a class="dropdown-item d-flex align-items-center" href="#">\n' +
                        '<div class="mr-3">\n' +
                        '<div class="icon-circle bg-gradient-dark"><i class="fas fa-file-alt text-white"></i></div>\n' +
                        '</div>\n' +
                        '<div>\n' +
                        '<div class="small text-dark">'+this.beautifiedDatetime+'</div>\n' +
                        '<span class="font-weight-bold">'+this.mensaje+'</span>\n' +
                        '</div>\n' +
                        '</a>')
                })
            })
            .fail(function (err) {
                alert("Ocurrió un error al cargar la página");
            });
    });
});*/