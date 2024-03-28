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
$(function () {
    var year= $("#curyear").val();
    var month= $("#curmonth").val();
    var date=new Date();
    if(year==null||year==undefined||year==""){


    }else{
        $("#year").val(year);
    }

    if(month==null||month==undefined||month==""){

    }else{
        $("#month").val(month);
    }

})

$("#searchBtn").on('click',function(){
    var year= $("#year").val();
    if(year=="请选择"){
        year="";
    }
    var month= $("#month").val();
    if(month=="请选择"){
        month="";
    }
    var name =$("#searchName").val();
    window.location.replace("/frontTopic/messageList?page="+1+"&size=10&year="+year+"&month="+month+"&name="+name);
});
$("#searchName").bind('keypress', function (event) {
    if (event.keyCode == "13") {
        $("#searchBtn").click();
    }
});
layui.use(['laypage','layer','form'], function(){
    var laypage = layui.laypage,
        layer = layui.layer;
    var  form =  layui.form;
    var count=$("#topicCount").val();
    var num = $("#curr").val();
    laypage.render({
        elem: 'laypageAre'
        ,pages:2
        ,count: count //数据总数，从服务端得到
        ,limit: 10
        ,layout: ['prev', 'page', 'next']
        ,curr: num || 1
        ,jump: function(obj, first){
            //首次不执行
            if(!first){
                var year= $("#year").val();
                var month= $("#month").val();
                var name =$("#searchName").val();
                if(year=="请选择"){
                    year="";
                }
                var month= $("#month").val();
                if(month=="请选择"){
                    month="";
                }
                window.location.replace("/frontTopic/messageList?page="+obj.curr+"&size=10&year="+year+"&month="+month+"&name="+name);
            }
        }
    });

    form.on('select(month)', function(data){
        var year= $("#year").val();
        if(year=="请选择"){
            year="";
        }
        var month= $("#month").val();
        if(month=="请选择"){
            month="";
        }
        var name =$("#searchName").val();
        form.render();
        window.location.replace("/frontTopic/messageList?page="+1+"&size=10&year="+year+"&month="+month+"&name="+name);

    });
    form.on('select(year)', function(data){
        var year= $("#year").val();
        if(year=="请选择"){
            year="";
        }
        var month= $("#month").val();
        if(month=="请选择"){
            month="";
        }
        var name =$("#searchName").val();
        form.render();
        window.location.replace("/frontTopic/messageList?page="+1+"&size=10&year="+year+"&month="+month+"&name="+name);

    });

});
