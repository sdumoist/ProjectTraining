

var currOrder = '';

layui.use(['form','element','Hussar', 'laypage', 'jquery','layer','laytpl'], function(){
    var $ = layui.jquery
        ,form = layui.form
        ,laypage = layui.laypage
        ,laytpl = layui.laytpl
        ,Hussar = layui.Hussar
        ,layer = layui.layer
        ,$ax = layui.HussarAjax
        ,element = layui.element; //Tab的切换功能，切换事件监听等，需要依赖element模块

    /*搜索按钮*/
    $(".screening").on('click',function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        refreshFile();
        layer.close(index);
    });


    //页面初始化
    $(function () {
        refreshFile();
        form.render();
    });
});

// 所有的问题
function refreshFile(num,size,order){

    var noOrder;
    if(order==null||order==undefined){
        noOrder=true;
        order = '';
    }
    currOrder = order;
    layui.use(['laypage','layer','table','Hussar','HussarAjax'], function(){
        var laypage = layui.laypage;
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var title = $('#keyword').val();
        var label = $('#label').val();
        var state = $('#solve').val();

        var ajax = new $ax(Hussar.ctxPath + "/knowledge/getKnowledgeList", function(data) {
            laypage.render({
                elem: 'laypageAre'
                ,count: data.count //数据总数，从服务端得到
                ,limit: 20
                ,layout: ['prev', 'page', 'next']
                ,curr: num || 1 //当前页
                ,jump: function(obj, first){
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if(!first){
                        refreshFile(obj.curr,obj.limit,currOrder)
                    }
                }
            });
            drawFile(data.rows);

        }, function(data) {

        });

        ajax.set("title",title);
        ajax.set("label",label);
        ajax.set("state",state);
        ajax.set("order",currOrder);
        ajax.set("pageNumber",num);
        ajax.set("pageSize",size);
        ajax.start();
    });
}

function drawFile(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
        };
        var getTpl = $("#demo1").html()
            ,view = document.getElementById('view1');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view1");
            if (param.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                },200);
            }else {
                $("div.noDataTip").hide();
            }
        });
    });
}

// 逻辑删除知识库
function delKnowladge(knowId) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/knowledge/delete", function(data) {
            if(data == 'success'){
                refreshFile();
                Hussar.success("删除成功");
            } else {
                Hussar.success("删除失败");
            }
        });
        ajax.set("knowId",knowId);
        ajax.start();
    });
}


// 排序条件
function timeOrder(ele) {
    $(".screen-container .views").removeClass('up-active');
    $(".screen-container .views").removeClass('down-active');
    if($(ele)[0].className.indexOf('up-active')> -1 ){
        $(ele).removeClass('up-active');
        $(ele).addClass('down-active');
        //提问时间正序
        refreshFile(null, null, 2);
    }else if($(ele)[0].className.indexOf('down-active')> -1 ){
        $(ele).removeClass('down-active');
        refreshFile();
    }else{
        $(ele).addClass('up-active');
        //提问时间倒叙
        refreshFile(null, null, 3);
    }

}

function readNumOrder(ele) {

    $(".question-time").removeClass('up-active');
    $(".question-time").removeClass('down-active');
    if($(ele)[0].className.indexOf('up-active')> -1 ){
        $(ele).removeClass('up-active');
        $(ele).addClass('down-active');
        refreshFile(null, null, 4);
    }else if($(ele)[0].className.indexOf('down-active')> -1 ){
        $(ele).removeClass('down-active');
        refreshFile();
    }else{
        $(ele).addClass('up-active');
        refreshFile(null, null, 5);
    }

}