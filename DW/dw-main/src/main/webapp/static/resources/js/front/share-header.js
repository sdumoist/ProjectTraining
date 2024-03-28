toLogin=function () {
    function openWin(url) {
        var a = document.createElement("a"); //创建a标签
        a.setAttribute("href", url);
        a.setAttribute("target", "_blank");
        document.body.appendChild(a);
        a.click(); //执行当前对象
    }

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var sharePath = window.location.pathname;
        var ajax = new $ax(Hussar.ctxPath + "/shareLogin", function(data) {
            window.location.href = Hussar.ctxPath + "/";
        }, function(data) {
            alert("登录异常!")
        });
        ajax.set("sharePath", sharePath);
        ajax.start();
    });
};