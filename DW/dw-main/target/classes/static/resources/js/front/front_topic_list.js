/*
/!**
 * Created by xb on 2018-07-10
 *!/
$(function() {
    var count=$("#topicCount").val();
    var gridView ={
        initPage: function() {
            var that = this;
            that.createPageContext();
        },
        createPageContext : function(){
            $('#pagelist').extendPagination({
                totalCount: count,
                showPage: 10,
                limit: 10,
                callback: function (curr, limit, totalCount) {
                    window.location.replace("/frontTopic/topicList?page="+curr+"&size=10");
                }
            })
        }
    }
    gridView.initPage();
});
*/
layui.use(['laypage','layer','Hussar'], function(){
    var laypage = layui.laypage,
        layer = layui.layer;
    var Hussar = layui.Hussar;
    var count=$("#topicCount").val();
    var num = $("#curr").val();
    laypage.render({
        elem: 'laypageAre'
        ,pages:2
        ,count: count //数据总数，从服务端得到
        ,limit: 12
        ,layout: ['prev', 'page', 'next']
        ,curr: num || 1
        ,jump: function(obj, first){
            //首次不执行
            if(!first){
                window.location.replace(Hussar.ctxPath+"/frontTopic/topicList?page="+obj.curr+"&size=12");
            }
        }
    });
    // $('#loginout').click(function (event) {
    //     var operation = function () {
    //         window.location.href = Hussar.ctxPath + "/logout";
    //     };
    //     // Hussar.confirm("您确定要退出吗?", operation);
    //     layer.confirm('您确定要退出吗？', {skin: 'move-confirm'}, operation);
    // });
});
var timer;
$(function () {
    $(".outContainer").scroll(function () {
        if ($(".outContainer").scrollTop() > 0) {
            $("#goToTop").show();
            $(".mainCon-inner").addClass("fix");
            $(".upload-fix").show(300)
        } else {
            $("#goToTop").hide();
            $(".mainCon-inner").removeClass("fix")
            $(".upload-fix").hide()
        }
    });
});

$("#goToTop").click(function () {
    $(".outContainer").scrollTop(0);
    cancelAnimationFrame(timer);
    timer = requestAnimationFrame(function fn() {

        var oTop = $(".outContainer").scrollTop();
        if (oTop > 0) {
            scrollTo(0, oTop - 50);
            timer = requestAnimationFrame(fn);
        } else {
            $("#goToTop").hide(1000);
            cancelAnimationFrame(timer);
        }
    });
})