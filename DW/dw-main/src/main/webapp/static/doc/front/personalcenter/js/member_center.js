/**
 * Created by Administrator on 2018/12/6.
 */
var scrollHeight=0;
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar','element'], function(){
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;

    $(function () {
        //空间
        var ajax = new $ax(Hussar.ctxPath + "/empStatistics/showSize", function(data) {
            if(data.limit=='0'){
                $("#used").html("不限");
                $("#total").html("不限");
                var withs="0%";
                element.progress('personal', withs);
            }else{
                $("#used").html(data.lack);
                $("#total").html(data.total);
                var withs=data.present+"%";
                element.progress('personal', withs);
            }
        }, function(data) {

        });
        ajax.start();

        var menu =$("#menu").val();
        if(menu!=null&&menu!=undefined&&menu!=""){
            $("#myNav .mItem").removeClass("active");

            if(menu!=6){
                if(menu ==11){
                    $("#myNav .mItem").eq(0).addClass("active")
                }else if(menu ==12){
                    $("#myNav .mItem").eq(8).addClass("active")
                }else{
                    $("#myNav .mItem").eq(menu-1).addClass("active")
                }

            }
        }
        $("#myNav .mItem").click(function () {
            $("#myNav .mItem").removeClass("active");
            $(this).addClass("active")
        })

        var oInfo = $(" .memberNav .nav");
        var oTop = $(".memberInfo").offset().top;
        var sTop = 0;
        $(window).scroll(function(){
            sTop = $(this).scrollTop();
            // oInfo.text(sTop + '-' + oTop);//这一句 只是为了看看数据 没有多大的用处
            if(sTop >= oTop){
                $(".memberNav").css({"position":"fixed","top":"149px"})
                $(".memberInfo").css({"position":"fixed","top":"-96px"})
                ;
                $(".btn-rit").show();
            }else{
                $(".memberNav").css({"position":"static"});
                $(".memberInfo").css({"position":"static"})
                $(".btn-rit").hide();
            }
        });
        $("#myNav").find(".active a").focus();
    })
    $(function(){
        var distance = $(document).scrollTop();

        setInterval(function () {
            var distance = $(document).scrollTop();
            scrollHeight=parseInt(distance)+50+"px";
        },300)
    })
});

function getTotalIntegral(){
    var integral = 0;
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type:'post',
            url: Hussar.ctxPath+'/integral/getTotalIntegral',
            async:false,
            success: function (data) {
                integral = parseInt(data);
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + '/integral/getTotalIntegral', function(data) {
            integral = parseInt(data);
        }, function(data) {

        });
        ajax.start();
        return integral;
    });
}