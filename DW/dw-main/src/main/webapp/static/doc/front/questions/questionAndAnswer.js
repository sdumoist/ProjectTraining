
var currOrder = '';
var tabNum = '0';
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
        if(tabNum == '1'){
            refreshFile2();
        } else if(tabNum == '2'){
            refreshFile3();
        } else if(tabNum == '3'){
            refreshFile4();
        } else if(tabNum == '4'){
            refreshFile5();
        } else {
            refreshFile();
        }
        layer.close(index);
    });

    /* tab页切换监听 */
    element.on('tab(docDemoTabBrief)', function(data){
        currOrder = '';
        tabNum = data.index;
        if(data.index == '1'){
            refreshFile2();
        } else if(data.index == '2'){
            refreshFile3();
        } else if(data.index == '3'){
            refreshFile4();
        } else if(data.index == '4'){
            refreshFile5();
        } else {
            refreshFile();
        }
        /*console.log(this); //当前Tab标题所在的原始DOM元素
        console.log(data.index); //得到当前Tab的所在下标
        console.log(data.elem); //得到当前的Tab大容器*/
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
        var title = $('#keyword1').val();
        var label = $('#label1').val();
        var state = $('#solve1').val();

        var ajax = new $ax(Hussar.ctxPath + "/question/getQueTableList", function(data) {
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

// 我的提问
function refreshFile2(num,size,order){

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
        var title = $('#keyword2').val();
        var label = $('#label2').val();
        var state = $('#solve2').val();

        var ajax = new $ax(Hussar.ctxPath + "/question/getMyQuestionList", function(data) {
            laypage.render({
                elem: 'laypageAre2'
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
                        refreshFile2(obj.curr,obj.limit,currOrder)
                    }
                }
            });
            drawFile2(data.rows);

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

function drawFile2(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
        };
        var getTpl = $("#demo2").html()
            ,view = document.getElementById('view2');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view2");
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

// 我的回答
function refreshFile3(num,size,order){

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
        var title = $('#keyword3').val();
        var label = $('#label3').val();
        var state = $('#solve3').val();

        var ajax = new $ax(Hussar.ctxPath + "/question/getMyAnswerList", function(data) {
            laypage.render({
                elem: 'laypageAre3'
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
                        refreshFile3(obj.curr,obj.limit,currOrder)
                    }
                }
            });
            drawFile3(data.rows);

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
function drawFile3(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
        };
        var getTpl = $("#demo3").html()
            ,view = document.getElementById('view3');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view3");
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

// 我的收藏
function refreshFile4(num,size,order){

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
        var title = $('#keyword4').val();
        var label = $('#label4').val();
        var state = $('#solve4').val();

        var ajax = new $ax(Hussar.ctxPath + "/question/getMyFollowQuestionList", function(data) {
            laypage.render({
                elem: 'laypageAre4'
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
                        refreshFile4(obj.curr,obj.limit,currOrder)
                    }
                }
            });
            drawFile4(data.rows);

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
function drawFile4(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
        };
        var getTpl = $("#demo4").html()
            ,view = document.getElementById('view4');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view4");
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

// 邀我回答
function refreshFile5(num,size,order){

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
        var title = $('#keyword5').val();
        var label = $('#label5').val();
        var state = $('#solve5').val();

        var ajax = new $ax(Hussar.ctxPath + "/question/getInviteMeAnswerList", function(data) {
            laypage.render({
                elem: 'laypageAre5'
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
                        refreshFile5(obj.curr,obj.limit,currOrder)
                    }
                }
            });
            drawFile5(data.rows);

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
function drawFile5(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
        };
        var getTpl = $("#demo5").html()
            ,view = document.getElementById('view5');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view5");
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

// 排序条件
function timeOrder(ele) {
    $(".screen-container .views").removeClass('up-active');
    $(".screen-container .views").removeClass('down-active');
    if($(ele)[0].className.indexOf('up-active')> -1 ){
        $(ele).removeClass('up-active');
        $(ele).addClass('down-active');
        //提问时间正序
        if(tabNum == '1'){
            refreshFile2(null, null, 2);
        } else if(tabNum == '2'){
            refreshFile3(null, null, 2);
        } else if(tabNum == '3'){
            refreshFile4(null, null, 2);
        } else if(tabNum == '4'){
            refreshFile5(null, null, 2);
        } else {
            refreshFile(null, null, 2);
        }
    }else if($(ele)[0].className.indexOf('down-active')> -1 ){
        $(ele).removeClass('down-active');
        if(tabNum == '1'){
            refreshFile2();
        } else if(tabNum == '2'){
            refreshFile3();
        } else if(tabNum == '3'){
            refreshFile4();
        } else if(tabNum == '4'){
            refreshFile5();
        } else {
            refreshFile();
        }
    }else{
        $(ele).addClass('up-active');
        //提问时间倒叙
        if(tabNum == '1'){
            refreshFile2(null, null, 3);
        } else if(tabNum == '2'){
            refreshFile3(null, null, 3);
        } else if(tabNum == '3'){
            refreshFile4(null, null, 3);
        } else if(tabNum == '4'){
            refreshFile5(null, null, 3);
        } else {
            refreshFile(null, null, 3);
        }
    }

}

function readNumOrder(ele) {

    $(".question-time").removeClass('up-active');
    $(".question-time").removeClass('down-active');
    if($(ele)[0].className.indexOf('up-active')> -1 ){
        $(ele).removeClass('up-active');
        $(ele).addClass('down-active');
        if(tabNum == '1'){
            refreshFile2(null, null, 4);
        } else if(tabNum == '2'){
            refreshFile3(null, null, 4);
        } else if(tabNum == '3'){
            refreshFile4(null, null, 4);
        } else if(tabNum == '4'){
            refreshFile5(null, null, 4);
        } else {
            refreshFile(null, null, 4);
        }
    }else if($(ele)[0].className.indexOf('down-active')> -1 ){
        $(ele).removeClass('down-active');
        if(tabNum == '1'){
            refreshFile2();
        } else if(tabNum == '2'){
            refreshFile3();
        } else if(tabNum == '3'){
            refreshFile4();
        } else if(tabNum == '4'){
            refreshFile5();
        } else {
            refreshFile();
        }
    }else{
        $(ele).addClass('up-active');
        if(tabNum == '1'){
            refreshFile2(null, null, 5);
        } else if(tabNum == '2'){
            refreshFile3(null, null, 5);
        } else if(tabNum == '3'){
            refreshFile4(null, null, 5);
        } else if(tabNum == '4'){
            refreshFile5(null, null, 5);
        } else {
            refreshFile(null, null, 5);
        }
    }

}

function collectionQuestion(queId) { // 收藏
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/qaFollowInfo/add", function(result) {
            if(result == 'success'){
                refreshFile();
            } else if(result == 'queDel'){
                Hussar.info("问题已被删除");
            }
        }, function(data) {
            Hussar.error("收藏失败");
        });
        ajax.set("queId",queId);
        ajax.start();
    });

}


// 删除问题
function delQuestion(queId) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var operation = function () {
            var ajax = new $ax(Hussar.ctxPath + "/question/delete", function(result) {
                if(result == 'success'){
                    Hussar.success("删除成功")
                    refreshFile2();
                    refreshFile3();
                } else if(result == 'esError'){
                    Hussar.info("删除失败");
                }
            }, function(data) {
                Hussar.error("删除失败");
            });
            ajax.set("queId",queId);
            ajax.start();
        }
        Hussar.confirm("确定要删除问题吗？", operation);

    });

}
// 取消收藏
function cancelFocus(queId) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/qaFollowInfo/cancelFollow", function(data) {
            if(data == 'success'){
                Hussar.success("取消收藏");
                refreshFile4();
            }else if(data == 'queDel'){
                Hussar.info("问题已删除");
            }
        });
        ajax.set("queId",queId);
        ajax.start();
    });
}