verify = function () {
    var hash = $("#hash").val();
    var pwd = $("#pwd").val();
    var flag = false;
    $.ajax({
        type: "post",
        url: "/s/verify",
        async: false,
        data: {
            hash: hash,
            pwd: pwd
        },
        success: function (data) {
            if (data == '1') {
                flag = true;
            } else {
                $("#errorMsg").html("");
                $("#errorMsg").html(data);
                $("#errorMsg").removeClass("hide");
                flag = false;
            }
        }
    });
    return flag;
};
$(function () {
    $("#pwd").focus();

});