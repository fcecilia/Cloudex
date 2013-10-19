/**
 * Created with IntelliJ IDEA.
 * User: fred
 * Date: 13/10/13
 * Time: 13:00
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(function () {

    removeInit()

    editInit()

    $('.editLogin').editable('/admin/login'
        , {
            indicator: "Wait...",
            tooltip: "Click to edit...",
            //onblur : "ignore",
            style: "inherit"
        });


    $('.editPassword').editable('/admin/password'
        , {
            indicator: "Wait...",
            tooltip: "Click to edit...",
            style: "inherit",
            // onblur : "ignore"
        });


    $(".index_text").editable("/admin/index", {
        indicator: "Wait...",
        type: 'textarea',
        submitdata: { _method: "post" },
        select: true,
        submit: 'OK',
        cancel: 'cancel',
        cssclass: "editable",
        //onblur : "ignore"     ,
        style: "inherit",
        rows: "10"
    });


    $(".welcome_text").editable("/admin/welcome", {
        indicator: "Wait...",
        type: 'textarea',
        submitdata: { _method: "post" },
        select: true,
        submit: 'OK',
        cancel: 'cancel',
        cssclass: "editable",
        //onblur : "ignore"     ,
        style: "inherit",
        rows: "10"
    });


    $('.addRow').click(function () {
        $.ajax({
            url: "/admin/new/redirection",
            type: 'post',
            cache: false,

            data: JSON.stringify("{save:ok}"),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (json) {
                console.log(" reponse :" + json);
                var id_ = json.id
                var $pathId = "path_".concat(id_);
                var $editId = "edit_".concat(id_);
                //alert(pathId)
                $('#table').append('<tr id="' +
                    id_ +
                    '">' +

                    '<td id="' +
                    $pathId +
                    '" class="edit"> </td>' +
                    '<td id="' +
                    $editId +
                    '" class="edit"> </td>' +
                    '<td><button type="button" class="btn removeRow">-</button></td>' +
                    '</tr>');
                editInit()
                removeInit();

            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log("error :" + XMLHttpRequest.responseText);
            }


        });


    });


});

function editInit() {
    $('.edit').editable('/admin/redirection'
        , {
            indicator: "Wait...",
            tooltip: "Click to edit...",
            style: "inherit",
            //  onblur : "ignore"
        });
}


function removeInit() {
    $('.removeRow').click(function (e) {
        var tr = $(this).closest('tr');
        var id_ = tr.attr("id")

        var url_ = '/admin/redirection/'.concat(id_)
        // alert(tr.attr("id"))
        $.ajax({
            url: url_,
            type: 'DELETE',
            cache: false,

            //data:'value ='.concat(tr.attr("id")),
            data: '{value:toto}',
            success: function (json) {
                console.log(" reponse :" + json);

            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log("error :" + XMLHttpRequest.responseText);
            }


        });


        tr.css("background-color", "#FF3700");
        tr.fadeOut(400, function () {
            tr.remove();
        });

    });
}



