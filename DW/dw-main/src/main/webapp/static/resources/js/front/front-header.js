$(function () {
    $('#loginout').click(function(event){
        layui.use('layer', function(){
            var layer = layui.layer;
            layer.confirm('您确定要退出吗?', {
                btn: [ '关闭','确定'] //可以无限个按钮
            }, function(index, layero){
                window.location.href = "/logout";
            }, function(index){
                layer.closeAll();
            });
        });
    });
    $(".curtain").click(function () {
        $(this).hide();
        $(".user-dropdown").css("display","none");
        $(".moreBox").css("display","none");
    });

        //ipad中点击出现菜单点击其他地方隐藏
        $(".user-wrapper").click(function (event) {
            if(window.screen.width <= 1024){
            $(".user-dropdown").css("display","block");
            $(".curtain").css("display","block");
            $(".moreBox").css("display","none");
            event.stopPropagation()}
        });
        $(".fold-more").click(function (event) {
            if(window.screen.width <= 1024){
            $(".moreBox").css("display","block");
            $(".curtain").css("display","block");
            $(".user-dropdown").css("display","none");
            event.stopPropagation()}
        });



});