/**
 * Create By luzhanzhao
 * date 2018-11-19
 * edit by ZhongGuangrui on 2019/01/21
 */
var chooseFile = [];    //选中的文件或目录的id
var clickFlag=false;
var opType="3";
var currOrder = '';
var scrollHeightAlert=0;
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var layerView;
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar','jstree'], function() {
    var $ = layui.jquery,
        form=layui.form,
        jstree=layui.jstree,
        laypage = layui.laypage,
        laytpl = layui.laytpl,
        Hussar = layui.Hussar,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;

    start();

    /*搜索按钮*/
    $("#searchBtn").on('click',function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
            ,offset: scrollHeightAlert
        });
        refreshFile(null,null);
        layer.close(index);
    });

    /*删除记录*/
    $("#delHistoryBtn").on('click',function(){

        if(chooseFile.length==0){
            layer.msg("请选择要删除的记录", {anim:6,icon: 0,offset:scrollHeightMsg});
            return;
        }

        layer.confirm('确定要删除所选记录吗？',function(){
            var index = layer.load(1, {
                title :'删除',
                shade: [0.1,'#fff'] //0.1透明度的白色背景
                ,offset: scrollHeightAlert,
                skin:'move-confirm'
            });
            if(chooseFile.length==0){
                layer.close(index);
                return;
            }
            var histories = chooseFile.join(',')
            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+"/personalOperate/deleteHistory",
                data:{
                    histories: histories,
                    opType: opType
                },
                async:true,
                cache:false,
                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                success:function(data){
                    if(data> 0){
                        var fileList = $("#thelist").find(".item");
                        for(var n = 0;n<fileList.length;n++){
                            var name = fileList.eq(n).find(".info").html().split(".")[0];
                            for(var m =0 ;m<chooseFileName.length;m++){
                                if(name == chooseFileName[m]){
                                    fileList.eq(n).remove();
                                }
                            }
                        }

                        Hussar.success('删除成功')
                    }else {
                        Hussar.error('删除异常')
                    }
                    // refreshTree();
                    refreshFile();
                    emptyChoose();
                    layer.close(index);
                },
                error:function () {
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/personalOperate/deleteHistory", function(data) {
                if(data> 0){
                    var fileList = $("#thelist").find(".item");
                    for(var n = 0;n<fileList.length;n++){
                        var name = fileList.eq(n).find(".info").html().split(".")[0];
                        for(var m =0 ;m<chooseFileName.length;m++){
                            if(name == chooseFileName[m]){
                                fileList.eq(n).remove();
                            }
                        }
                    }

                    Hussar.success('删除成功')
                }else {
                    Hussar.error('删除异常')
                }
                // refreshTree();
                refreshFile();
                emptyChoose();
                layer.close(index);
            }, function(data) {
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("histories",histories);
            ajax.set("opType",opType);
            ajax.start();
        })
    });

    /*清空记录*/
    $("#clearHistoryBtn").on('click',function(){
        if ($(".table_list>tbody").find("tr").length != 0) {
            layer.confirm('确定要清空回收站吗？', {
                title: ['清空回收站', 'background-color:#fff'],
                offset: scrollHeightAlert,
                skin:'move-confirm'
            }, function () {
                var index = layer.load(1, {
                    shade: [0.1, '#fff'], //0.1透明度的白色背景
                    offset: scrollHeightAlert,
                });
               /* $.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/personalRecycle/clear",
                    data: "",
                    async: true,
                    cache: false,
                    success: function (data) {
                        if (data == true) {
                            layer.msg('清空成功', {icon: 1, offset: scrollHeightMsg})
                        } else {
                            layer.msg('清空异常', {anim: 6, icon: 2, offset: scrollHeightMsg})
                        }
                        refreshFile();
                        emptyChoose();
                        layer.close(index);
                    },
                    error: function () {
                        refreshFile(openFileId);
                        emptyChoose();
                        layer.close(index);
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/personalRecycle/clear", function(data) {
                    if (data == true) {
                        layer.msg('清空成功', {icon: 1, offset: scrollHeightMsg})
                    } else {
                        layer.msg('清空异常', {anim: 6, icon: 2, offset: scrollHeightMsg})
                    }
                    refreshFile();
                    emptyChoose();
                    layer.close(index);
                }, function(data) {
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                });
                ajax.start();
            })
        }else {
            layer.msg('回收站为空', {anim:6,icon: 0, offset: scrollHeightMsg})
        }
    });

    function start() {
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");
    }

    $("#cancel").on('click',function(){
        var index = layer.getFrameIndex(window.name);
        layer.close(index);
    });

    //页面初始化
    $(function () {
        var load = new Loading();
        load.init({
            target: "#dndArea"
        });
        load.start();
        setTimeout(function() {
            load.stop();
        }, 800)
        refreshFile();
    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth =inner.width();
        //fixed-table-header
        $(".fixed-table-header").width(tableWidth)
    })
});
function refreshFile(num,size,order){
    var screenHeight = parseInt(window.screen.availHeight);
    //console.log(screenHeight);
    if (screenHeight > 728) {
        $("#marg").css("min-height","768");
    }
    var noOrder;
    /* if(order==null||order==undefined||order==''){
         noOrder=true;
         order = '';
     }*/
    currOrder = order;
    layui.use(['laypage','layer','table','Hussar','HussarAjax'], function(){
        var laypage = layui.laypage;
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var name = $('#searchName').val();
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/personalRecycle/list",
            data:{
                pageNumber:num,
                pageSize:size,
                name: name,
                order:currOrder
            },
            async:true,
            cache:false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType:"json",
            success:function(data){
                laypage.render({
                    elem: 'laypageAre'
                    ,count: data.count //数据总数，从服务端得到
                    ,limit: 60
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
                $("#amount").html("已全部加载" + data.count+"个")
                drawFile(data);
                emptyChoose();
                dbclickover = true;
                //$("th").hover(function () {
                if(noOrder==true){
                    $("#orderName").hide();
                    $("#orderName1").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderEffectiveTime").hide();
                    $("#orderEffectiveTime1").hide();
                }else{
                    if(order== "1"){
                        $("#orderName").hide();
                        $("#orderName1").show();
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                        $("#orderUser").hide();
                        $("#orderUser1").hide();
                        $("#orderSize").hide();
                        $("#orderSize1").hide();
                        $("#orderEffectiveTime").hide();
                        $("#orderEffectiveTime1").hide();
                    }
                    if(order== "0"){
                        $("#orderName1").hide();
                        $("#orderName").show();
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                        $("#orderUser").hide();
                        $("#orderUser1").hide();
                        $("#orderSize").hide();
                        $("#orderSize1").hide();
                        $("#orderEffectiveTime").hide();
                        $("#orderEffectiveTime1").hide();
                    }
                    if(order== "2"){
                        $("#orderName").hide();
                        $("#orderName1").hide();
                        $("#orderTime1").hide();
                        $("#orderTime").show();
                        $("#orderUser").hide();
                        $("#orderUser1").hide();
                        $("#orderSize").hide();
                        $("#orderSize1").hide();
                        $("#orderEffectiveTime").hide();
                        $("#orderEffectiveTime1").hide();
                    }
                    if(order== "3" ){
                        $("#orderTime").hide();
                        $("#orderTime1").show();
                        $("#orderName").hide();
                        $("#orderName1").hide();
                        $("#orderUser").hide();
                        $("#orderUser1").hide();
                        $("#orderSize").hide();
                        $("#orderSize1").hide();
                        $("#orderEffectiveTime").hide();
                        $("#orderEffectiveTime1").hide();
                    }
                    if(order== "4"){
                        $("#orderName").hide();
                        $("#orderName1").hide();
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                        $("#orderUser").show();
                        $("#orderUser1").hide();
                        $("#orderSize").hide();
                        $("#orderSize1").hide();
                        $("#orderEffectiveTime").hide();
                        $("#orderEffectiveTime1").hide();
                    }
                    if(order== "5"){
                        $("#orderName").hide();
                        $("#orderName1").hide();
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                        $("#orderUser").hide();
                        $("#orderUser1").show();
                        $("#orderSize").hide();
                        $("#orderSize1").hide();
                        $("#orderEffectiveTime").hide();
                        $("#orderEffectiveTime1").hide();
                    }
                    if(order== "6"){
                        $("#orderName").hide();
                        $("#orderName1").hide();
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                        $("#orderUser").hide();
                        $("#orderUser1").hide();
                        $("#orderSize").show();
                        $("#orderSize1").hide();
                        $("#orderEffectiveTime").hide();
                        $("#orderEffectiveTime1").hide();
                    }
                    if(order== "7"){
                        $("#orderName").hide();
                        $("#orderName1").hide();
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                        $("#orderUser").hide();
                        $("#orderUser1").hide();
                        $("#orderSize").hide();
                        $("#orderSize1").show();
                        $("#orderEffectiveTime").hide();
                        $("#orderEffectiveTime1").hide();
                    }
                    if(order== "8"){
                        $("#orderName").hide();
                        $("#orderName1").hide();
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                        $("#orderUser").hide();
                        $("#orderUser1").hide();
                        $("#orderSize").hide();
                        $("#orderSize1").hide();
                        $("#orderEffectiveTime").show();
                        $("#orderEffectiveTime1").hide();
                    }
                    if(order== "9"){
                        $("#orderName").hide();
                        $("#orderName1").hide();
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                        $("#orderUser").hide();
                        $("#orderUser1").hide();
                        $("#orderSize").hide();
                        $("#orderSize1").hide();
                        $("#orderEffectiveTime").hide();
                        $("#orderEffectiveTime1").show();
                    }

                }
                //}, function () {
                //    $(this).find("#orderTime").hide();
                //    $(this).find("#orderTime1").hide();
                //})
                $(".hoverEvent").hover(function(){
                    var ishover= $(this).find(".ishover");
                    if(ishover.is(':hidden')){
                        $(".moreicon").hide();
                        $(this).find("td  #hoverSpan").show();
                    }else{
                        $(this).find("td  #hoverSpan").hide();
                    }

                },function(){
                    $(this).find("td  #hoverSpan").hide();
                });
                $(".layui-table tr").hover(function () {
                    //alert($(this).prev());
                    $(this).find("td").css("border-color","#DAEBFE");
                    $(this).prev().find("td").css("border-color","#DAEBFE");
                }, function () {
                    $(this).find("td").css("border-color","rgba(242,246,253,1)");
                    $(this).prev().find("td").css("border-color","rgba(242,246,253,1)");
                });
                $(".layui-table tbody tr:first").hover(function () {
                    $(this).find("td").css("border-color","#DAEBFE");
                    $("thead").find("tr").css("border-bottom-color","#DAEBFE");
                }, function () {
                    $(this).find("td").css("border-color","rgba(242,246,253,1)");
                    $("thead").find("tr").css("border-bottom-color","rgba(242,246,253,1)");
                })
                if(data.count==0){
                    $("#laypageAre").hide();
                }else {
                    $("#laypageAre").show();
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalRecycle/list", function(data) {
            laypage.render({
                elem: 'laypageAre'
                ,count: data.count //数据总数，从服务端得到
                ,limit: 60
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
            $("#amount").html("已全部加载" + data.count+"个")
            drawFile(data);
            emptyChoose();
            dbclickover = true;
            //$("th").hover(function () {
            if(noOrder==true){
                $("#orderName").hide();
                $("#orderName1").show();
                $("#orderTime").hide();
                $("#orderTime1").hide();
                $("#orderUser").hide();
                $("#orderUser1").hide();
                $("#orderSize").hide();
                $("#orderSize1").hide();
                $("#orderEffectiveTime").hide();
                $("#orderEffectiveTime1").hide();
            }else{
                if(order== "1"){
                    $("#orderName").hide();
                    $("#orderName1").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderEffectiveTime").hide();
                    $("#orderEffectiveTime1").hide();
                }
                if(order== "0"){
                    $("#orderName1").hide();
                    $("#orderName").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderEffectiveTime").hide();
                    $("#orderEffectiveTime1").hide();
                }
                if(order== "2"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime1").hide();
                    $("#orderTime").show();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderEffectiveTime").hide();
                    $("#orderEffectiveTime1").hide();
                }
                if(order== "3" ){
                    $("#orderTime").hide();
                    $("#orderTime1").show();
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderEffectiveTime").hide();
                    $("#orderEffectiveTime1").hide();
                }
                if(order== "4"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").show();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderEffectiveTime").hide();
                    $("#orderEffectiveTime1").hide();
                }
                if(order== "5"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").show();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderEffectiveTime").hide();
                    $("#orderEffectiveTime1").hide();
                }
                if(order== "6"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").show();
                    $("#orderSize1").hide();
                    $("#orderEffectiveTime").hide();
                    $("#orderEffectiveTime1").hide();
                }
                if(order== "7"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").show();
                    $("#orderEffectiveTime").hide();
                    $("#orderEffectiveTime1").hide();
                }
                if(order== "8"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderEffectiveTime").show();
                    $("#orderEffectiveTime1").hide();
                }
                if(order== "9"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderEffectiveTime").hide();
                    $("#orderEffectiveTime1").show();
                }

            }
            //}, function () {
            //    $(this).find("#orderTime").hide();
            //    $(this).find("#orderTime1").hide();
            //})
            $(".hoverEvent").hover(function(){
                var ishover= $(this).find(".ishover");
                if(ishover.is(':hidden')){
                    $(".moreicon").hide();
                    $(this).find("td  #hoverSpan").show();
                }else{
                    $(this).find("td  #hoverSpan").hide();
                }

            },function(){
                $(this).find("td  #hoverSpan").hide();
            });
            $(".layui-table tr").hover(function () {
                //alert($(this).prev());
                $(this).find("td").css("border-color","#DAEBFE");
                $(this).prev().find("td").css("border-color","#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color","rgba(242,246,253,1)");
                $(this).prev().find("td").css("border-color","rgba(242,246,253,1)");
            });
            $(".layui-table tbody tr:first").hover(function () {
                $(this).find("td").css("border-color","#DAEBFE");
                $("thead").find("tr").css("border-bottom-color","#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color","rgba(242,246,253,1)");
                $("thead").find("tr").css("border-bottom-color","rgba(242,246,253,1)");
            })
            if(data.count==0){
                $("#laypageAre").hide();
            }else {
                $("#laypageAre").show();
            }
        }, function(data) {

        });
        ajax.set("pageNumber",num);
        ajax.set("pageSize",size);
        ajax.set("name",name);
        ajax.set("order",currOrder);
        ajax.start();
    });
}
function getNameOrder() {
    refreshFile(null,null,1);

}
function getNameOrder1() {

    refreshFile(null,null,0);

}
function getTimeOrder() {

    refreshFile(null, null, 3);
}

function getTimeOrder1() {
    refreshFile(null, null, 2)
}
function getSizeOrder() {

    refreshFile(null, null, 7);
}
function getSizeOrder1() {
    refreshFile(null, null, 6)
}
function getEffectiveTimeOrder() {
    refreshFile(null, null, 9)
}
function getEffectiveTimeOrder1() {
    refreshFile(null, null, 8)
}
function orderByTime(){
    if ($("#orderTime").css("display") != "none"){
        getTimeOrder();
    }else {
        getTimeOrder1();
    }
}

function orderByTime(){
    if ($("#orderTime").css("display") != "none"){
        getTimeOrder();
    }else {
        getTimeOrder1();
    }
}
function orderByName(){
    if ($("#orderName").css("display") != "none"){
        getNameOrder();
    }else {
        getNameOrder1();
    }
}
function orderByUser(){
    if ($("#orderUser").css("display") != "none"){
        getUserOrder();
    }else {
        getUserOrder1();
    }
}
function orderBySize(){
    if ($("#orderSize").css("display") != "none"){
        getSizeOrder();
    }else {
        getSizeOrder1();
    }
}

function orderByEffectiveTime() {
    if ($("#orderEffectiveTime").css("display") != "none"){
        getEffectiveTimeOrder();
    }else {
        getEffectiveTimeOrder1();
    }
}
function drawFile(param) {
    console.log(param.rows.data);
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param.rows.data,
            "adminFlag":param.adminFlag
        };
        var getTpl = $("#demo1").html()
            ,view = document.getElementById('view');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view");
            var tableWidth =inner.width();
            //fixed-table-header
            $(".fixed-table-header").width(tableWidth);
            if (param.rows.data.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                },100);
            }
        });
    });


}

function iconRestore(e, id, name) {
    cancelBubble();
    changeBgColorOfTr(e);
    var operation = function () {
        layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar','jstree'], function() {
            var $ = layui.jquery,
                form=layui.form,
                jstree=layui.jstree,
                laypage = layui.laypage,
                laytpl = layui.laytpl,
                Hussar = layui.Hussar,
                layer = layui.layer,
                $ax = layui.HussarAjax,
                element = layui.element;

            var ajax = new $ax(Hussar.ctxPath + "/personalRecycle/restoreOldFolder", function (data) {
                if (data.result == "0") {
                    layer.msg("文件已存在", {anim: 6, icon: 0, offset: scrollHeightMsg});
                } else if (data.result == "1") {
                    $(".layui-laypage-btn").click();
                    layer.close(layerView);
                    layer.msg("还原成功", {icon: 1, offset: scrollHeightMsg});
                    refreshFile();
                } else if (data.result == "3") {
                    layer.msg("您没有还原到此目录的权限", {anim: 6, icon: 0, offset: scrollHeightMsg});
                } else if (data.result == "5") {
                    layer.msg("个人空间不足", {anim: 6, icon: 0, offset: scrollHeightMsg});
                }  else if (data.result == "6") {
                    layer.msg("原目录不存在，无法还原", {anim: 6, icon: 0, offset: scrollHeightMsg});
                } else {
                    layer.msg("无法还原到根目录", {anim: 6, icon: 0, offset: scrollHeightMsg});
                }
            }, function (data) {
                layer.msg("系统出错", {anim: 6, icon: 2, offset: scrollHeightMsg});
            });
            ajax.set("fileId", id);
            ajax.start();})
    }
    var index = layer.confirm('确定要还原吗？', {
        title: ['还原', 'background-color:#fff'],
        offset: scrollHeightAlert,
        skin:'move-confirm'
    }, operation);
}

function changeBgColorOfTr(e){
    var jq=$(e);
    //console.log(e.tagName.toLowerCase());
    if (e.tagName.toLowerCase() != "tr"){
        jq = jq.parents(".hoverEvent");
    }
    jq.parent().find("tr").css("background-color","#fff");

    jq.css("background-color","rgba(246, 250, 255, 1)");
}
function  clickCheck(e,id) {

    var jq=$(e);
    changeBgColorOfTr(e);

    cancelBubble()
}
function checkAll(e) {
    var self = $(e).siblings('input');//
    $(e).toggleClass('layui-form-checked');
    if(self.prop("checked")==false){
        self.prop("checked",true);
    }else{
        self.prop("checked",false);
    }
    if (self.prop("checked")) { // 全选
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", true);
            $(this).siblings('.layui-form-checkbox').addClass("layui-form-checked");
            var checkFileId=  $(this).siblings(".checkFileId").val();
            var checkFileType=  $(this).siblings(".checkFileType").val()
            var checkFileName=  $(this).siblings(".checkFileName").val()
            var checkFileAuthor=  $(this).siblings(".chooseFileAuthor").val()
            chooseFile.push(checkFileId);
            chooseFileType.push(checkFileType);
            chooseFileName.push(checkFileName);
            chooseFileAuthor.push(checkFileAuthor)

        });

    }
    else { // 取消全选

        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", false);
            $(this).siblings('.layui-form-checkbox').removeClass("layui-form-checked");
        });
        chooseFileType=[];
        chooseFileName=[];
        chooseFile=[];
        chooseFileAuthor=[]
    }
}
Array.prototype.del=function(n) {
    if(n<0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0,n).concat(this.slice(n+1,this.length));
}
function emptyChoose() {
    chooseFile = [];
    chooseFileType = [];
    chooseFileName = [];
    chooseFileAuthor=[]
}
function  clickIconCheck(e,id) {
    $(e).toggleClass('layui-form-checked');
    var checkbox=$(e).siblings('.checkbox');
    if(checkbox.prop("checked")==false){
        checkbox.prop("checked",true);
        chooseFile.push(id);
    }else{
        checkbox.prop("checked",false);
        if(chooseFile.indexOf(id)!=-1){
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
        }
    }

    cancelBubble()
}
//得到事件
function getEvent(){
    if(window.event)    {return window.event;}
    func=getEvent.caller;
    while(func!=null){
        var arg0=func.arguments[0];
        if(arg0){
            if((arg0.constructor==Event || arg0.constructor ==MouseEvent
                || arg0.constructor==KeyboardEvent)
                ||(typeof(arg0)=="object" && arg0.preventDefault
                    && arg0.stopPropagation)){
                return arg0;
            }
        }
        func=func.caller;
    }
    return null;
}
//阻止冒泡
function cancelBubble()
{
    var e=getEvent();
    if(window.event){
        //e.returnValue=false;//阻止自身行为
        e.cancelBubble=true;//阻止冒泡
    }else if(e.preventDefault){
        //e.preventDefault();//阻止自身行为
        e.stopPropagation();//阻止冒泡
    }
}
function tryPop(th,id,type,name,index,author){
    if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile()
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if($(th).prop("checked")){
        chooseFile.push(id);
    }else{
        if(chooseFile.indexOf(id)!=-1){
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
        }
    }
    cancelBubble()
}
$(function(){

    setInterval(function () {
        scrollHeight=parent.scrollHeight;
        var height = parseInt(scrollHeight);
        var screenHeight = parseInt(window.screen.availHeight);
        if( scrollHeight!=0){
            scrollHeightAlert= parseInt(height - 130 + (screenHeight - 154) / 2.0) + "px";
            scrollHeightLong= parseInt(height - 130 + (screenHeight - 510) / 2.0) + "px";
            scrollHeightTip = parseInt(height - 130 + (screenHeight - 250) / 2.0) + "px";
            scrollHeightShare = parseInt(height - 130 + (screenHeight - 200) / 2.0) + "px";
            scrollHeightMsg = parseInt(height - 130 + (screenHeight - 64) / 2.0) + "px";
            //console.log(scrollHeightAlert + ' ' + scrollHeightLong)
        }
        //console.log(height + "//" + screenHeight + " " + layerHeight)
        //$(".layui-layer.layui-layer-iframe").css("top",height - 130 + (screenHeight - layerHeight) / 2.0 + "px");
    },300);
});
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").css("top",scrollHeightAlert);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}