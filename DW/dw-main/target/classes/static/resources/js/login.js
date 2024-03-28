//金企文库登录页面脚本

layui.extend({
    admin: '{/}/resources/weadmin/static/js/admin'
});

layui.use(['table', 'jquery', 'admin', 'layer'], function () {
    var table = layui.table,
        $ = layui.jquery,
        admin = layui.admin, layer = layui.layer;



    //默认焦点
    $("#name").focus();

    //登录按钮点击事件
    $('#loginBtnRow').click(function () {
        //loginSubmit();
        var name = $("#name").val();
        var password = $("#password").val();
        if (name == '' || password == '') {
            $("#loginResult").html("用户名或密码不能为空！");
            // layer.msg("用户名或密码不能为空！")
        } else {
            $("#loginForm").submit();
        }

    });
    document.getElementById("name").focus();
});