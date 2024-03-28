/**
 * Create By luzhanzhao
 * date 2018-11-19
 */
var chooseFile = [];    //选中的文件或目录的id
var chooseFileValidFlag = [];   // 选中的文件的有效值
var clickFlag=false;
var currOrder = '';
var scrollHeightAlert=0;
var scrollHeightLong=0;
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
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
    /*多选下载*/
    $("#mulDownLoad, #manyMulDownLoad").on('click',function(){
        if(chooseFile.length==0){
            layer.msg("请选择要下载的文件", {anim:6,icon: 0});
            return;
        }
        var index = layer.load(1, {
            shade: [0.1,'#fff'] ,//0.1透明度的白色背景
            scrollbar: false,
            time:1000 ,
            offset: scrollHeightAlert
        });
        var ids=chooseFile.join(",");
        var name=chooseFileName.join("*");
        download(ids,name);

        // layer.close(index);
    });

    /*删除记录*/
    $("#delete-version").on('click',function(){
        if(chooseFile.length==0){
            layer.msg("请选择要删除的文档", {anim:6,icon: 0});
            return;
        }
        for (var i in chooseFileValidFlag){
            if (chooseFileValidFlag[i] == 1){
                layer.msg("无法删除最新版本文档", {anim:6,icon: 0});
                return;
            }
        }
        layer.confirm('确定要删除所选历史版本吗？',{title :['删除','background-color:#fff'],fixed:true,skin:'move-confirm'},function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'] //0.1透明度的白色背景
                ,offset: scrollHeightAlert
            });
            if(chooseFile.length==0){
                layer.close(index);
                return;
            }
            var histories = chooseFile.join(',')
            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+"/frontVersion/deleteVersion",
                data:{
                    histories: histories,
                },
                async:true,
                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                cache:false,
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

                        layer.msg('删除成功', {icon: 1})
                    }else {
                        layer.msg('删除异常', {anim:6,icon: 0})
                    }
                    btnState();
                    // refreshTree();
                    refreshFile();
                    emptyChoose();
                    layer.close(index);
                },
                error:function () {

                    btnState();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/frontVersion/deleteVersion", function(data) {
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

                    layer.msg('删除成功', {icon: 1})
                }else {
                    layer.msg('删除异常', {anim:6,icon: 0})
                }
                btnState();
                // refreshTree();
                refreshFile();
                emptyChoose();
                layer.close(index);
            }, function(data) {
                btnState();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("histories",histories);
            ajax.start();
        })
    });

    // 设为最新版本
    $("#new-version").on('click', function () {
        if(chooseFile.length==0){
            layer.msg("请选择要操作的文档", {anim:6,icon: 0});
            return;
        } else if (chooseFile.length >= 2){
            layer.msg("请选择一个文档", {anim:6,icon: 0});
            return;
        }else if (chooseFileValidFlag[0] == 1){
            layer.msg("该文档已经是最新版本", {anim:6,icon: 0});
            return;
        }
        layer.confirm('确定要将所选文档设为最新版本吗？',{title :['设为最新版本','background-color:#fff'],fixed:true,skin:'move-confirm',resize:false},function() {
            var index = layer.load(1, {
                shade: [0.1, '#fff'] //0.1透明度的白色背景
                ,offset: scrollHeightAlert
            });
            if (chooseFile.length == 0) {
                layer.close(index);
                return;
            }
            var docId = chooseFile[0];
            var oldDocId = $("#oldDocId").val();
            /*$.ajax({
                type:'post',
                url: Hussar.ctxPath+'/frontVersion/setVersion',
                async:false,
                data:{
                    docId:docId,
                    oldDocId:oldDocId
                },
                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                success: function (data) {
                    if (data == 'true'){
                        $("#oldDocId").val(docId);
                        layer.msg('设置成功', {icon: 1});
                    }else {
                        layer.msg('设置失败', {anim:6,icon: 1});
                    }
                    layer.close(index);
                    btnState();
                    refreshFile();
                    if (parent.htmlFlag == 'upload'){
                        parent.refreshFile(parent.currPage,60,parent.currOrder);
                    }else{
                        parent.refreshFile(parent.openFileId);
                    }
                    setTimeout(function () {
                        parent.changeBgColorOfTr($("#"+ docId,parent.document)[0]);
                    },2000);
                    emptyChoose();
                },
                error:function () {

                    btnState();
                    refreshFile();
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + '/frontVersion/setVersion', function(data) {
                if (data == 'true'){
                    $("#oldDocId").val(docId);
                    layer.msg('设置成功', {icon: 1});
                }else {
                    layer.msg('设置失败', {anim:6,icon: 1});
                }
                layer.close(index);
                btnState();
                refreshFile();
                if (parent.htmlFlag == 'upload'){
                    parent.refreshFile(parent.currPage,60,parent.currOrder);
                }else{
                    parent.refreshFile(parent.openFileId);
                }
                setTimeout(function () {
                    parent.changeBgColorOfTr($("#"+ docId,parent.document)[0]);
                },2000);
                emptyChoose();
            }, function(data) {
                btnState();
                refreshFile();
                emptyChoose();
                layer.close(index);
            });
            ajax.set("docId",docId);
            ajax.set("oldDocId",oldDocId);
            ajax.start();
        });
    })

    function start() {
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");

        btnState()
    }


    $("#cancel").on('click',function(){
        var index = layer.getFrameIndex(window.name);
        layer.close(index);
    });


    //页面初始化
    $(function () {
        refreshFile();
    });
});
function download(id,name){
    cancelBubble();
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew?docIds="+id,
            type : "post",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            async:false
        });
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}
function  iconDownLoad(e,id,name) {
    cancelBubble();
    changeBgColorOfTr(e);
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/personalcenter/getInfo",
            data:{
                ids:id
            },
            async:false,
            cache:false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType:"json",
            success:function(data){
                if(data.result =="1"){
                    layer.msg("该文件已被删除", {anim:6,icon: 0});
                }
                else if(data.result =="2"||data.result =="3"){
                    layer.msg("您没有权限", {anim:6,icon: 0});
                } else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else{
                    var index = layer.load(1, {
                        shade: [0.1,'#fff'] ,//0.1透明度的白色背景
                        scrollbar: false,
                        time:1000
                        ,offset: scrollHeightAlert
                    });
                    download(id,name);
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if(data.result =="1"){
                layer.msg("该文件已被删除", {anim:6,icon: 0});
            }
            else if(data.result =="2"||data.result =="3"){
                layer.msg("您没有权限", {anim:6,icon: 0});
            } else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }else{
                var index = layer.load(1, {
                    shade: [0.1,'#fff'] ,//0.1透明度的白色背景
                    scrollbar: false,
                    time:1000
                    ,offset: scrollHeightAlert
                });
                download(id,name);
            }
        }, function(data) {

        });
        ajax.set("ids",id);
        ajax.start();
    });

}
function refreshFile(num,size,order){
    var screenHeight = parseInt(window.screen.availHeight);
    //console.log(screenHeight);
    if (screenHeight > 728) {
        // $("#marg").css("min-height","768");
    }
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
        var name = $('#searchName').val();
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/frontVersion/list",
            data:{
                pageNumber:num,
                pageSize:size,
                name: name,
                order:currOrder,
                oldDocId:$("#oldDocId").val()
            },
            async:true,
            cache:false,
            dataType:"json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success:function(data){
                laypage.render({
                    elem: 'laypageAre'
                    ,count: data.count //数据总数，从服务端得到
                    ,limit: 7
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
                //$("#amount").html("共" + data.count+"个")
                drawFile(data.rows);
                emptyChoose();
                btnState();
                dbclickover = true;

                // 下载相关
                //$(".hoverEvent").hover(function () {
                //    $(this).find("td>.hoverSpan").show();
                //}, function () {
                //    $(this).find("td>.hoverSpan").hide();
                //});
                //$("th").hover(function () {
                if(noOrder==true){
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                }else{
                    if(order== "2"){
                        $("#orderTime1").hide();
                        $("#orderTime").show();
                    }
                    if(order== "3" ){
                        $("#orderTime").hide();
                        $("#orderTime1").show();
                    }

                }
                //}, function () {
                //    $(this).find("#orderTime").hide();
                //    $(this).find("#orderTime1").hide();
                //})
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
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/frontVersion/list", function(data) {
            laypage.render({
                elem: 'laypageAre'
                ,count: data.count //数据总数，从服务端得到
                ,limit: 7
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
            //$("#amount").html("共" + data.count+"个")
            drawFile(data.rows);
            emptyChoose();
            btnState();
            dbclickover = true;

            // 下载相关
            //$(".hoverEvent").hover(function () {
            //    $(this).find("td>.hoverSpan").show();
            //}, function () {
            //    $(this).find("td>.hoverSpan").hide();
            //});
            //$("th").hover(function () {
            if(noOrder==true){
                $("#orderTime").hide();
                $("#orderTime1").hide();
            }else{
                if(order== "2"){
                    $("#orderTime1").hide();
                    $("#orderTime").show();
                }
                if(order== "3" ){
                    $("#orderTime").hide();
                    $("#orderTime1").show();
                }

            }
            //}, function () {
            //    $(this).find("#orderTime").hide();
            //    $(this).find("#orderTime1").hide();
            //})
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
        }, function(data) {

        });
        ajax.set("pageNumber",num);
        ajax.set("pageSize",size);
        ajax.set("name",name);
        ajax.set("order",currOrder);
        ajax.set("oldDocId",$("#oldDocId").val());
        ajax.start();
    });
}
/*打开分享链接*/
function share(e,docId,fileSuffixName,fileName) {
    cancelBubble();
    changeBgColorOfTr(e);
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/personalcenter/getInfo",
            data: {
                ids: docId
            },
            async: false,
            cache: false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType: "json",
            success: function (data) {
                if (data.result == "1") {
                    layer.msg("该文件已被删除", {anim: 6, icon: 0});
                } else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else {
                    var title = '';
                    var url = "/s/shareConfirm";
                    var w =  538;
                    var h = 311;
                    if (title == null || title == '') {
                        title = false;
                    }
                    if (url == null || url == '') {
                        url = "404.jsp";
                    }
                    if (w == null || w == '') {
                        w = ($(window).width() * 0.9);
                    }
                    if (h == null || h == '') {
                        h = ($(window).height() - 50);
                    }
                    layer.open({
                        type: 2,
                        area: [w + 'px', h + 'px'],
                        fix: false, //不固定
                        maxmin: false,
                        shadeClose: true,
                        shade: 0.4,
                        title: title,
                        closeBtn:2,
                        offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                        content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                    });
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if (data.result == "1") {
                layer.msg("该文件已被删除", {anim: 6, icon: 0});
            } else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }else {
                var title = '';
                var url = "/s/shareConfirm";
                var w =  538;
                var h = 311;
                if (title == null || title == '') {
                    title = false;
                }
                if (url == null || url == '') {
                    url = "404.jsp";
                }
                if (w == null || w == '') {
                    w = ($(window).width() * 0.9);
                }
                if (h == null || h == '') {
                    h = ($(window).height() - 50);
                }
                layer.open({
                    type: 2,
                    area: [w + 'px', h + 'px'],
                    fix: false, //不固定
                    maxmin: false,
                    shadeClose: true,
                    shade: 0.4,
                    title: title,
                    closeBtn:2,
                    offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                    content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                });
            }
        }, function(data) {

        });
        ajax.set("ids",docId);
        ajax.start();
    });
}
//function getNameOrder() {
//    refreshFile(null,null,1);
//
//}
//function getNameOrder1() {
//
//    refreshFile(null,null,0);
//
//}
function getTimeOrder() {

    refreshFile(null, null, 3);
}

function getTimeOrder1() {
    refreshFile(null, null, 2)
}
function orderByTime(){
    if ($("#orderTime").css("display") != "none"){
        getTimeOrder();
    }else {
        getTimeOrder1();
    }
}
function drawFile(param) {
    //console.log(param);
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
        };
        var getTpl = $("#demo1").html()
            ,view = document.getElementById('view');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view");
            var tableWidth =inner.width();
            //fixed-table-header
            $(".fixed-table-header").width(tableWidth);
            if (param.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                },200);
            }else {
                $("div.noDataTip").hide();
            }
        });
    });
    var aWidth = parseInt($(".new-version-tip").prev().find("a").css("width"));
    var spanWidth = parseInt($(".new-version-tip").prev().css("width"));
    var tipLeft = aWidth > spanWidth ? (spanWidth - 8) : aWidth;
    $(".new-version-tip").css("margin-left",tipLeft + 62 + "px").css("display","block");

}
function dbclick(id,type,name){
    cancelBubble();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/personalcenter/getInfo",
            data:{
                ids:id
            },
            async:false,
            cache:false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType:"json",
            success:function(data){
                if(data.result =="2"){
                    layer.msg("您没有权限", {anim:6,icon: 0});
                }/!*else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }*!/else{
                    if(dbclickover==true) {
                        if (clickFlag) {//取消上次延时未执行的方法
                            clickFlag = clearTimeout(clickFlag);
                        }
                        dbclickover=false;
                        reNameFlag = false;

                        if (type == "folder") {
                            pathId.push(id);
                            pathName.push(name);
                            createPath();
                            refreshFile(id);
                        } else {
                            showPdf(id, type, name)
                        }
                    }
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if(data.result =="2"){
                layer.msg("您没有权限", {anim:6,icon: 0});
            }/*else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }*/else{
                if(dbclickover==true) {
                    if (clickFlag) {//取消上次延时未执行的方法
                        clickFlag = clearTimeout(clickFlag);
                    }
                    dbclickover=false;
                    reNameFlag = false;

                    if (type == "folder") {
                        pathId.push(id);
                        pathName.push(name);
                        createPath();
                        refreshFile(id);
                    } else {
                        showPdf(id, type, name)
                    }
                }
            }
        }, function(data) {

        });
        ajax.set("ids",id);
        ajax.start();
    });
}
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showPdf(id,fileSuffixName,name) {
    var keyword =  name;
    dbclickover = true;
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            async: false,
            cache: false,
            dataType: "json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success: function (data) {
                if(data.code==1){
                    openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
                }else if(data.code==2){
                    openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
                }else if(data.code==3){
                    openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
                }else if(data.code==4){
                    openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
                }else{
                    openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id);
                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
                openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
            }else if(data.code==2){
                openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
            }else if(data.code==3){
                openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
            }else if(data.code==4){
                openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
            }else{
                openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id);
            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
    });
}
function btnState() {
    if(chooseFile.length==0){
        $('.clickBtn').hide()
        $(".webuploader-pick").show();
    }else {
        var flag=0;
        for(var i=0;i<chooseFileType.length;i++){
            if(chooseFileType[i]=="folder"){
                flag=1;
                break;

            }
        }
        if(flag=="1"){
            $('.clickBtn').hide()

        }else{
            $('.clickBtn').show()
            $('#manyMulDownLoad').hide();
            $(".webuploader-pick").hide()
            if(chooseFile.length>1){
                $('#updateName').hide();
                $('#mulDownLoad').hide();
                $('#manyMulDownLoad').show();

            }
        }

    }
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
function  clickCheck(e,id,type,name,validFlag,author) {

    var jq=$(e);
    changeBgColorOfTr(e);
    if(jq.find(".checkbox").prop("checked")==false){
        jq.find(".checkbox").prop("checked",true);
        jq.find(".layui-form-checkbox").addClass("layui-form-checked");

        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author);
        chooseFileValidFlag.push(validFlag);


    }else{
        jq.find(".checkbox").prop("checked",false);
        jq.find(".layui-form-checkbox").removeClass("layui-form-checked");
        if(chooseFile.indexOf(id)!=-1){
            chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
            chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
            chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id));
            chooseFileValidFlag=chooseFileValidFlag.del(chooseFile.indexOf(id));
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
        }
    }
    if (jq.parents("tbody").find(".checkbox:checked").length == jq.parents("tbody").find(".checkbox").length){
        $(".checkAll").prop("checked",true);
        $(".checkAll").next().addClass("layui-form-checked");
    }else {
        $(".checkAll").prop("checked",false);
        $(".checkAll").next().removeClass("layui-form-checked");
    }
    btnState();

    cancelBubble()
    console.log(chooseFile);
}
function checkAll(e) {
    var self = $(e).siblings('input');//
    if(self.prop("checked")==false){
        self.prop("checked",true);
        $(e).addClass('layui-form-checked');
    }else{
        self.prop("checked",false);
        $(e).removeClass('layui-form-checked');
    }
    if (self.prop("checked")) { // 全选
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", true);
            $(this).siblings('.layui-form-checkbox').addClass("layui-form-checked");
            var checkFileId=  $(this).siblings(".checkFileId").val();
            var checkFileType=  $(this).siblings(".checkFileType").val()
            var checkFileName=  $(this).siblings(".checkFileName").val()
            var checkFileAuthor=  $(this).siblings(".chooseFileAuthor").val()
            var checkFileValidFlag=  $(this).siblings(".chooseFileValidFlag").val()
            chooseFile.push(checkFileId);
            chooseFileType.push(checkFileType);
            chooseFileName.push(checkFileName);
            chooseFileAuthor.push(checkFileAuthor);
            chooseFileValidFlag.push(checkFileValidFlag);

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
        chooseFileAuthor=[];
        chooseFileValidFlag=[];
    }
    btnState();
    console.log(chooseFile);
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
    chooseFileAuthor=[];
    chooseFileValidFlag=[];
}
function  clickIconCheck(e,id,validFlag) {
    $(e).toggleClass('layui-form-checked');
    var checkbox=$(e).siblings('.checkbox');
    if(checkbox.prop("checked")==false){
        checkbox.prop("checked",true);
        chooseFile.push(id);
        chooseFileValidFlag.push(validFlag);
    }else{
        checkbox.prop("checked",false);
        if(chooseFile.indexOf(id)!=-1){
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
            chooseFileValidFlag=chooseFileValidFlag.del(chooseFile.indexOf(id));
        }
    }

    btnState();
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
function tryPop(th,id,type,name,index,author,validFlag){
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
        chooseFileValidFlag.push(validFlag);
    }else{
        if(chooseFile.indexOf(id)!=-1){
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
            chooseFileValidFlag=chooseFileValidFlag.del(chooseFile.indexOf(id));
        }
    }
    btnState();
    cancelBubble()
}
$(function(){
    $('#searchName').bind('keypress', function (event) {
        if (event.keyCode == "13") {
            $("#searchBtn").click();
        }
    });
})
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").css("top",scrollHeightAlert);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}