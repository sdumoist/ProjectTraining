/**
 * Created by Administrator on 2018/12/6.
 */
/**
 * Create By luzhanzhao
 * date 2018-11-19
 */
var chooseFile = [];    //选中的文件或目录的id
var chooseFileType = []; //选中的文件或目录的type
var clickFlag=false;
var currOrder = '';
var scrollHeightAlert=0;
var reNameFlag= false;      //重命名标志
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var opType=$("#opType").val();
var pathId = [];        //路径
var pathName = [];
var openFileId;   //打开的文件夹的id
var cutFile = [];          //剪切的文件或目录的id
var cutFileType = [];      //剪切的文件或目录的type
var cutFileName = [];      //剪切的文件或目录的name
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
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
        refreshFile(openFileId);
        layer.close(index);
    });
    /*上一级目录*/
    $("#upLevel").on('click',function(){
        if(pathId.length==1){
            return;
        }
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
            ,offset: scrollHeightAlert
        });
        refreshFile(pathId[pathId.length-2]);
        pathName.pop();
        pathId.pop();
        createPath();
        layer.close(index);
    });
    /*新增子目录*/
    $("#newFolder").on('click', function () {
        // var folderAmountNum = parseInt(folderAmount)+1;
        // if (pathId.length >= folderAmountNum) {
        //     layer.msg("目录最多为"+folderAmount+"级", {anim: 6, icon: 0,offset:scrollHeightMsg});
        //     return false;
        // }
        $("#categoryName").val("");
        $('.name-list').empty();
        form.render();
        layer.open({
            type: 1,
            btn: ['确定','取消'],
            fix: false, //不固定
            maxmin: false,
            offset:parseInt(scrollHeightTip) - 25 + "px",
            shadeClose: false,
            shade: 0.4,
            skin:'creat-folder-dialog',
            title: ["创建目录",'background-color:#fff'],
            content: $('#addDiv'),
            btn1: function (index, layero) {
                var categoryName = $("#categoryName").val().trim();

                if (categoryName.length <= 0) {
                    layer.msg("目录名称不能为空", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    return false;
                }
                if (categoryName.length > 130) {
                    layer.msg("目录名称不能超过130个字符", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    return false;
                }
                var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
                //特殊字符
                if (!pattern.test(categoryName)) {
                    layer.msg("输入的目录名称不合法", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    return;
                }

                var ajax = new $ax(Hussar.ctxPath + "/personalCollection/addCheck", function(data) {
                    if (data == "false") {
                        layer.msg("“" + categoryName + "”目录已存在", {anim: 6, icon: 0,offset:scrollHeightMsg});
                        return;
                    } else {
                        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/add", function(result) {
                            refreshFile(openFileId);
                            // refreshTree();
                            layer.closeAll();
                        }, function(data) {
                        });
                        ajax.set("parentFolderId",openFileId);
                        ajax.set("resourceName",categoryName);
                        ajax.start();
                    }
                }, function(data) {

                });
                ajax.set("name",categoryName);
                ajax.set("parentFolderId",openFileId);
                ajax.start();
            },
        });
    });
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
        var load = new Loading();
        load.init({
            target: "#dndArea"
        });
        load.start();
        setTimeout(function() {
            load.stop();
        }, 800)
        pathId = ['abcde4a392934742915f89a586989292'];
        pathName = ['我的收藏'];
        createPath()
        refreshFile();
        btnState()
    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth =inner.width();
        //fixed-table-header
        $(".fixed-table-header").width(tableWidth)
    })
    /*剪切*/
    $("#cutFile").on('click',function(){
        if (chooseFileType[0]=="folder"){
            var operation = function () {
                layerView = layer.open({
                    type: 1,
                    area: ['400px','434px'],
                    //shift : 1,
                    shadeClose: false,
                    btn: ['确定','取消'],
                    skin: 'move-class',
                    title: ['目录结构','background-color:#fff'],
                    maxmin: false,
                    offset:scrollHeightLong,
                    content: $("#folderTreeAuthority"),
                    success: function () {
                        initFolderTree();
                        layer.close(index);
                    },
                    end: function () {
                        layer.closeAll(index);
                    }
                });
            }
            var index = layer.confirm('确定要移动所选目录吗？',{title :['移动','background-color:#fff'], offset:scrollHeightAlert,skin:'move-confirm'}, operation);
            cutFile=[].concat(chooseFile);
            cutFileType=[].concat(chooseFileType);
            cutFileName=[].concat(chooseFileName);

        }else{
            var operation =function(){
                layerView=layer.open({
                    type : 1,
                    area: ['400px','434px'],
                    btn: ['确定','取消'],
                    //shift : 1,
                    shadeClose: false,
                    skin: 'move-class',
                    title : ['目录结构','background-color:#fff'],
                    maxmin : false,
                    offset:scrollHeightLong,
                    content : $("#filTree"),
                    success : function() {
                        initFileTree();
                        layer.close(index);
                    },
                    end: function () {
                        layer.closeAll(index);
                    }
                });
            }
            var index = layer.confirm('确定要移动所选文件吗？',{title :['移动','background-color:#fff'], offset:scrollHeightAlert,skin:'move-confirm'},operation);
            cutFile=[].concat(chooseFile);
            cutFileType=[].concat(chooseFileType);
            cutFileName=[].concat(chooseFileName);
        }
    });
    initFileTree=function () {
        var $tree = $("#fileTree2");
        $tree.jstree("destroy");    //二次打开时要先销毁树
        $tree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": Hussar.ctxPath+"/personalCollection/getTreeDataLazy?lazy",
                    "data": function (node) {
                        return {"id": node.id
                        };
                    }
                },
                themes:{
                    theme : "default",
                    dots:false,// 是否展示虚线
                    icons:true,// 是否展示图标
                }
            },
            types: {
                "closed" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/treeFile.png",
                },
                "default" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/treeFile.png",
                },
                "opened" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/openFile.png",
                }
            },
            plugins: [ 'types']
        });
        var openFolder =null;
        $tree.bind('activate_node.jstree', function (obj,e){
            openFolder = e;
        });
        $(".layui-layer-btn0").on('click',function(){
            if(openFolder === null){
                layer.msg("请选择目录",{anim:6,icon: 0,offset:scrollHeightMsg});
            }else {
                var operation = function () {
                    if (openFileId == openFolder.node.original.id) {
                        layer.msg("不能移动到当前目录", {icon: 0, offset: scrollHeightMsg});
                        return;
                    }
                    var ajax = new $ax(Hussar.ctxPath + "/personalCollection/move", function (data) {
                        if (data.result == "0") {

                            layer.msg("文件已存在", {icon: 0, offset: scrollHeightMsg});
                        } else if (data.result == "1") {
                            $(".layui-laypage-btn").click();
                            layer.close(layerView);
                            layer.msg("移动成功", {icon: 1, offset: scrollHeightMsg});
                            refreshFile(openFileId)
                        } else {

                            layer.msg("移动失败", {icon: 2, offset: scrollHeightMsg});
                        }
                    }, function (data) {
                        layer.msg("系统出错，请联系管理员", {icon: 2, offset: scrollHeightMsg});
                    });
                    ajax.set("ids", cutFile.join(","));
                    ajax.set("parentFolderId", openFolder.node.original.id);
                    ajax.start();
                }
                layer.confirm('确定要移动到此目录下吗？', {
                    title: ['移动', 'background-color:#fff'],
                    offset: scrollHeightAlert,
                    skin:'move-confirm'
                }, operation);
            }
        });

    }
    //加载目录树
    initFolderTree = function () {
        var $tree = $("#folderTreeAuthority2");
        $tree.jstree("destroy");    //二次打开时要先销毁树
        $tree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": Hussar.ctxPath + "/personalCollection/getTreeDataLazy?lazy",
                    "data": function (node) {
                        return {
                            "id": node.id
                        };
                    }
                },
                themes: {
                    theme: "default",
                    dots: false,// 是否展示虚线
                    icons: true,// 是否展示图标
                }
            },
            types: {
                "closed": {
                    "icon": Hussar.ctxPath + "/static/resources/img/fsfile/treeFile.png",
                },
                "default": {
                    "icon": Hussar.ctxPath + "/static/resources/img/fsfile/treeFile.png",
                },
                "opened": {
                    "icon": Hussar.ctxPath + "/static/resources/img/fsfile/openFile.png",
                }
            },
            plugins: ['types']
        });
        var openFolder = null;
        $tree.bind('activate_node.jstree', function (obj, e) {
            openFolder = e;
        });
        $(".layui-layer-btn0").on('click', function () {
            if (openFolder === null) {
                layer.msg("请选择目录", {anim: 6, icon: 0, offset: scrollHeightMsg});
            } else {
                var operation = function () {
                    var index = layer.load(1, {
                        shade: [0.1, '#fff'] //0.1透明度的白色背景
                        , offset: scrollHeightAlert
                    });
                    if (cutFile.length <= 0) {
                        layer.close(index);
                        layer.msg("请先选择目标目录", {anim: 6, icon: 0, offset: scrollHeightMsg});
                        return;
                    }
                    var nameStr = cutFileName.join("*");
                    var ajax = new $ax(Hussar.ctxPath + "/personalCollection/addCheck", function (data) {
                        if (data != "true") {
                            layer.msg("存在重名目录", {anim: 6, icon: 0, offset: scrollHeightMsg});
                            layer.close(index);
                            return;
                        } else {
                            var folderIdStr = cutFile.join(",");
                            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/checkChild", function (data) {
                                if (data == "have") {
                                    layer.close(layerView);
                                    layer.msg("目标目录不能是移动目录的本身或子目录", {anim: 6, icon: 0, offset: scrollHeightMsg});
                                    layer.close(index);
                                    return;
                                } else {
                                    layer.close(layerView);
                                    layer.msg("移动成功", {icon: 1, offset: scrollHeightMsg});
                                    updateFolderPid(index, openFolder.node.id);
                                    cutFile = [];
                                    btnState();
                                }
                            }, function (data) {

                            });
                            ajax.set("fsFolderIds", folderIdStr);
                            ajax.set("id", openFolder.node.id);
                            ajax.start();
                            // layer.close(layerView);
                            // layer.msg("移动成功", {icon: 1, offset: scrollHeightMsg});
                            // updateFolderPid(index, openFolder.node.id);
                            // cutFile = [];
                            // btnState();
                        }
                    }, function (data) {

                    });
                    ajax.set("name", nameStr);
                    ajax.set("parentFolderId", openFolder.node.id);
                    ajax.start();
                };
                layer.confirm('确定要移动到此目录下吗？', {
                    title: ['移动', 'background-color:#fff'],
                    offset: scrollHeightAlert,
                    skin: 'move-confirm'
                }, operation);
            }

        })
    }
});
function updateFolderPid(index, id) {
    layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
        var $ = layui.jquery,
            Hussar = layui.Hussar,
            layer = layui.layer,
            $ax = layui.HussarAjax;
        var cutIds = cutFile.join(",");
        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/update", function (data) {
            if ("success" == data) {
                emptyChoose();
                refreshFile(openFileId);
                layer.closeAll();
            } else {
                layer.msg("移动失败", {anim: 6, icon: 0, offset: scrollHeightMsg});
                //layer.msg("目录最多为" + data + "级", {anim: 6, icon: 0, offset: scrollHeightMsg});
                layer.close(index);
            }
        }, function (data) {

        });
        ajax.set("ids", cutIds);
        ajax.set("parentFolderId", id);
        ajax.start();
    })
}
/*取消收藏*/
function cancelCollection(e,id,fileType){
    cancelBubble();
    changeBgColorOfTr(e);
    if (fileType=="folder"){
        layer.confirm('确定要删除吗？',{title :['删除','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'], //0.1透明度的白色背景
                fix:true
                ,offset: scrollHeightAlert
            });

            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;
                var ajax = new $ax(Hussar.ctxPath + "/personalCollection/deleteCollection", function(data) {
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
                        layer.msg('删除成功',{icon: 1,offset:scrollHeightMsg})
                    }else {
                        layer.msg('删除失败',{anim:6,icon: 2,offset:scrollHeightMsg})
                    }
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }, function(data) {
                    layer.msg('删除异常!',{anim:6,icon: 2,offset:scrollHeightMsg})
                    btnState();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                });
                ajax.set("ids",id);
                ajax.set("opType",opType);
                ajax.set("fileType",fileType);
                ajax.start();
            });
        })
    }else {
        layer.confirm('确定要取消收藏吗？',{title :['取消收藏','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'], //0.1透明度的白色背景
                fix:true
                ,offset: scrollHeightAlert
            });

            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;
                var ajax = new $ax(Hussar.ctxPath + "/personalCollection/deleteCollection", function(data) {
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
                        layer.msg('取消收藏成功',{icon: 1,offset:scrollHeightMsg})
                    }else {
                        layer.msg('取消收藏失败',{anim:6,icon: 2,offset:scrollHeightMsg})
                    }
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }, function(data) {
                    layer.msg('取消收藏异常!',{anim:6,icon: 2,offset:scrollHeightMsg})
                    btnState();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                });
                ajax.set("ids",id);
                ajax.set("opType",opType);
                ajax.set("fileType",fileType);
                ajax.start();
            });
        })
    }
};
function refreshFile(folderId,num,size,order) {
    if (folderId==null){
        folderId = 'abcde4a392934742915f89a586989292'
    }
    var screenHeight = parseInt(window.screen.availHeight);
    //console.log(screenHeight);
    if (screenHeight > 728) {
        // $("#marg").css("min-height","768");
    }
    var noOrder;
    // if(order==null||order==undefined||order==''){
    //     noOrder=true;
    //     order = '';
    // }
    currOrder = order;
    layui.use(['laypage', 'layer', 'table', 'Hussar', 'HussarAjax'], function () {
        var laypage = layui.laypage,
            Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var name = $('#searchName').val();
        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/list", function (data) {
            laypage.render({
                elem: 'laypageAre'
                , count: data.count //数据总数，从服务端得到
                , limit: 60
                , layout: ['prev', 'page', 'next']
                , curr: num || 1 //当前页
                , jump: function (obj, first) {
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if (!first) {
                        refreshFile(folderId,obj.curr, obj.limit, currOrder)
                    }
                }
            });

            $("#amount").html("已全部加载" + data.count + "个");
            openFileId = folderId;
            createPath();
            drawFile(data);
            emptyChoose();
            btnState();
            dbclickover = true;
            if(noOrder==true){
                $("#orderName").hide();
                $("#orderName1").show();
                $("#orderTime").hide();
                $("#orderTime1").hide();
                $("#orderUser").hide();
                $("#orderUser1").hide();
                $("#orderSize").hide();
                $("#orderSize1").hide();
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
                }

            }
            // 取消收藏按钮显示
            $(".hoverEvent").hover(function () {
                $(this).find("td>.hoverSpan").show();
            }, function () {
                $(this).find("td>.hoverSpan").hide();
            });
            $(".layui-table tr").hover(function () {
                //alert($(this).prev());
                $(this).find("td").css("border-color", "#DAEBFE");
                $(this).prev().find("td").css("border-color", "#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color", "rgba(242,246,253,1)");
                $(this).prev().find("td").css("border-color", "rgba(242,246,253,1)");
            });
            $(".layui-table tbody tr:first").hover(function () {
                $(this).find("td").css("border-color", "#DAEBFE");
                $("thead").find("tr").css("border-bottom-color", "#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color", "rgba(242,246,253,1)");
                $("thead").find("tr").css("border-bottom-color", "rgba(242,246,253,1)");
            })
            if (data.count == 0) {
                $("#laypageAre").hide();
            } else {
                $("#laypageAre").show();
            }
        }, function (data) {

        });
        ajax.set("pageNumber", num);
        ajax.set("pageSize", size);
        ajax.set("name", name);
        ajax.set("order", currOrder);
        ajax.set("parentFolderId", folderId);
        ajax.start();
    });
}
/*打开分享链接*/
function share(e,docId,fileSuffixName,fileName) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                if(data.code==1){
                }else if(data.code==2){
                }else if(data.code==3){
                }else if(data.code==4){
                }else{

                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
            }else if(data.code==2){
            }else if(data.code==3){
            }else if(data.code==4){
            }else{

            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
        cancelBubble();
        changeBgColorOfTr(e);
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
                    layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
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
                        skin:'share-dialog',
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
                layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
            } else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }else {
                var title = '';
                var url = "/s/shareConfirm";
                var w =  538;
                var h = 390;
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
function getNameOrder() {
    refreshFile(openFileId,null,null,1);

}
function getNameOrder1() {

    refreshFile(openFileId,null,null,0);

}
function getTimeOrder() {

    refreshFile(openFileId,null, null, 3);
}

function getTimeOrder1() {
    refreshFile(openFileId,null, null, 2)
}
function getUserOrder() {
    refreshFile(openFileId,null,null,5);
}
function getUserOrder1() {
    refreshFile(openFileId,null, null, 4);
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
function drawFile(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param.rows,
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
            if (param.rows.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                },200);
            }else {
                $("div.noDataTip").hide();
            }
        });
    });

}
function dbclick(id,type,name){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        if (type=='folder'){
            pathId.push(id);
            pathName.push(name);
            createPath();
            refreshFile(id);
        }else {
            var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
                if(data.result =="1"){
                    layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
                }
                else if(data.result =="2"){
                    layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
                }else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else{
                    if(dbclickover==true) {
                        if (clickFlag) {//取消上次延时未执行的方法
                            clickFlag = clearTimeout(clickFlag);
                        }
                        dbclickover=false;
                        reNameFlag = false;


                        showPdf(id, type, name)

                    }
                }
            }, function(data) {

            });
            ajax.set("ids",id);
            ajax.start();
        }

    });

}
function createPath(){
    $("#path").empty();
    // $("#path").append("<span class='total'>");
    for(var i=0;i<pathId.length;i++){
        if(i==pathId.length-1){
            var param= '<span>'+pathName[i]+'</span>'
        }else {
            var param= '<span><a style="cursor: pointer; color: #3C91FD;" onclick="clickPath(\''+pathId[i]+'\')">'+pathName[i]+'</a>'+'  >  </span>'        }
        $("#path").append(param);


    }
    var timer;
    // $("#path").append("</span>");
    $(".message #path").hover(function () {
        var _this = $(this);
        var len = _this.width();

        var width = _this.parent().width();
        if(len >= width){
            var num = 0;
            var change = 0;
            timer = setInterval(function () {
                if (navigator.userAgent.indexOf('iPad') != -1){
                    change = 240-len;
                }else{
                    change = 290-len;
                }
                if (num <= change) {
                    clearInterval(timer);
                }
                num -= 1;
                _this.css("left",num);
            }, 25);
        }
    },function () {
        clearInterval(timer);
        $(this).css("left",0)
    })
}
function clickPath(id) {
    while(pathId.indexOf(id)+1!=pathId.length){
        pathId.pop();
        pathName.pop();
    }
    createPath();
    refreshFile(id);
}
function download(id,name){
    //cancelBubble();
    layui.use(['Hussar','HussarAjax'], function() {
        var Hussar = layui.Hussar;
        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew?docIds="+id,
            type : "post",
            async:false
        });
    })
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
                    layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
                }
                else if(data.result =="2"||data.result =="3"){
                    layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
                }else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else{
                    $.ajax({
                        url: Hussar.ctxPath+"/integral/downloadIntegral",
                        async: true,
                        data: {
                            docId: id,
                            ruleCode: 'download'
                        },
                        success: function (data) {
                            if(data.status=="1") {

                                var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                    icon: 3,
                                    title: '提示',
                                    offset: scrollHeightAlert
                                }, function (index) {
                                    layer.close(index2);
                                    var index = layer.load(1, {
                                        shade: [0.1, '#fff'],//0.1透明度的白色背景
                                        scrollbar: false,
                                        time: 1000
                                        ,offset: scrollHeightAlert
                                    });
                                    $.ajax({
                                        url: Hussar.ctxPath+"/integral/addIntegral",
                                        async: true,
                                        data: {
                                            docId: id,
                                            ruleCode: 'download'
                                        },
                                        success: function (data) {
                                            if (null == data) {
                                                download(id, name);
                                            } else {
                                                $("#num").html(data.msg)
                                                if (data.msg == "积分不足" || data.msg == "已达上限") {
                                                    $(".integral .point").hide();
                                                    $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                                }
                                                $(".integral").css("top", scrollHeightAlert);
                                                $(".integral").show();
                                                //alert($("#totalIntegral",parent.document).text());
                                                // 实时更新积分
                                                $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
                                                setTimeout(function () {
                                                    $(".integral .point").show();
                                                    $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                                    $(".integral").hide();

                                                }, 2000)
                                                if (data.integral != 0) {
                                                    download(id, name);
                                                }
                                            }
                                        }
                                    });

                                });
                            }else {
                                var index = layer.load(1, {
                                    shade: [0.1, '#fff'],//0.1透明度的白色背景
                                    scrollbar: false,
                                    time: 1000
                                    ,offset: scrollHeightAlert
                                });
                                $.ajax({
                                    url: Hussar.ctxPath+"/integral/addIntegral",
                                    async: true,
                                    data: {
                                        docId: id,
                                        ruleCode: 'download'
                                    },
                                    success: function (data) {
                                        if (null == data) {
                                            download(id, name);
                                        } else {
                                            $("#num").html(data.msg)
                                            if (data.msg == "积分不足" || data.msg == "已达上限") {
                                                $(".integral .point").hide();
                                                $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                            }
                                            $(".integral").css("top", scrollHeightAlert);
                                            $(".integral").show();
                                            //alert($("#totalIntegral",parent.document).text());
                                            // 实时更新积分
                                            $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
                                            setTimeout(function () {
                                                $(".integral .point").show();
                                                $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                                $(".integral").hide();

                                            }, 2000)
                                            if (data.integral != 0) {
                                                download(id, name);
                                            }
                                        }
                                    }
                                });
                            }
                        }})
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if(data.result =="1"){
                layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
            }
            else if(data.result =="2"||data.result =="3"){
                layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
            }else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }else{
                /*$.ajax({
                    url: Hussar.ctxPath+"/integral/downloadIntegral",
                    async: true,
                    data: {
                        docId: id,
                        ruleCode: 'download'
                    },
                    success: function (data) {
                        if(data.status=="1") {

                            var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                icon: 3,
                                title: '提示',
                                offset: scrollHeightAlert
                            }, function (index) {
                                layer.close(index2);
                                var index = layer.load(1, {
                                    shade: [0.1, '#fff'],//0.1透明度的白色背景
                                    scrollbar: false,
                                    time: 1000
                                    ,offset: scrollHeightAlert
                                });
                                $.ajax({
                                    url: Hussar.ctxPath+"/integral/addIntegral",
                                    async: true,
                                    data: {
                                        docId: id,
                                        ruleCode: 'download'
                                    },
                                    success: function (data) {
                                        if (null == data) {
                                            download(id, name);
                                        } else {
                                            $("#num").html(data.msg)
                                            if (data.msg == "积分不足" || data.msg == "已达上限") {
                                                $(".integral .point").hide();
                                                $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                            }
                                            $(".integral").css("top", scrollHeightAlert);
                                            $(".integral").show();
                                            //alert($("#totalIntegral",parent.document).text());
                                            // 实时更新积分
                                            $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
                                            setTimeout(function () {
                                                $(".integral .point").show();
                                                $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                                $(".integral").hide();

                                            }, 2000)
                                            if (data.integral != 0) {
                                                download(id, name);
                                            }
                                        }
                                    }
                                });

                            });
                        }else {
                            var index = layer.load(1, {
                                shade: [0.1, '#fff'],//0.1透明度的白色背景
                                scrollbar: false,
                                time: 1000
                                ,offset: scrollHeightAlert
                            });
                            $.ajax({
                                url: Hussar.ctxPath+"/integral/addIntegral",
                                async: true,
                                data: {
                                    docId: id,
                                    ruleCode: 'download'
                                },
                                success: function (data) {
                                    if (null == data) {
                                        download(id, name);
                                    } else {
                                        $("#num").html(data.msg)
                                        if (data.msg == "积分不足" || data.msg == "已达上限") {
                                            $(".integral .point").hide();
                                            $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                        }
                                        $(".integral").css("top", scrollHeightAlert);
                                        $(".integral").show();
                                        //alert($("#totalIntegral",parent.document).text());
                                        // 实时更新积分
                                        $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
                                        setTimeout(function () {
                                            $(".integral .point").show();
                                            $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                            $(".integral").hide();

                                        }, 2000)
                                        if (data.integral != 0) {
                                            download(id, name);
                                        }
                                    }
                                }
                            });
                        }
                    }})*/
                var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {
                    if(data.status=="1") {

                        var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                            icon: 3,
                            title: '提示',
                            offset: scrollHeightAlert,
                            skin:'download-info',
                        }, function (index) {
                            layer.close(index2);
                            var index = layer.load(1, {
                                shade: [0.1, '#fff'],//0.1透明度的白色背景
                                scrollbar: false,
                                time: 1000
                                ,offset: scrollHeightAlert
                            });
                            /*$.ajax({
                                url: Hussar.ctxPath+"/integral/addIntegral",
                                async: true,
                                data: {
                                    docId: id,
                                    ruleCode: 'download'
                                },
                                success: function (data) {
                                    if (null == data) {
                                        download(id, name);
                                    } else {
                                        $("#num").html(data.msg)
                                        if (data.msg == "积分不足" || data.msg == "已达上限") {
                                            $(".integral .point").hide();
                                            $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                        }
                                        $(".integral").css("top", scrollHeightAlert);
                                        $(".integral").show();
                                        //alert($("#totalIntegral",parent.document).text());
                                        // 实时更新积分
                                        $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
                                        setTimeout(function () {
                                            $(".integral .point").show();
                                            $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                            $(".integral").hide();

                                        }, 2000)
                                        if (data.integral != 0) {
                                            download(id, name);
                                        }
                                    }
                                }
                            });*/
                            var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                                if (null == data) {
                                    download(id, name);
                                } else {
                                    $("#num").html(data.msg)
                                    if (data.msg == "积分不足" || data.msg == "已达上限") {
                                        $(".integral .point").hide();
                                        $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                    }
                                    $(".integral").css("top", scrollHeightAlert);
                                    $(".integral").show();
                                    //alert($("#totalIntegral",parent.document).text());
                                    // 实时更新积分
                                    $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
                                    setTimeout(function () {
                                        $(".integral .point").show();
                                        $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                        $(".integral").hide();

                                    }, 2000)
                                    if (data.integral != 0) {
                                        download(id, name);
                                    }
                                }
                            }, function(data) {

                            });
                            ajax.set("docId",id);
                            ajax.set("ruleCode",'download');
                            ajax.start();
                        });
                    }else {
                        var index = layer.load(1, {
                            shade: [0.1, '#fff'],//0.1透明度的白色背景
                            scrollbar: false,
                            time: 1000
                            ,offset: scrollHeightAlert
                        });
                        /*$.ajax({
                            url: Hussar.ctxPath+"/integral/addIntegral",
                            async: true,
                            data: {
                                docId: id,
                                ruleCode: 'download'
                            },
                            success: function (data) {
                                if (null == data) {
                                    download(id, name);
                                } else {
                                    $("#num").html(data.msg)
                                    if (data.msg == "积分不足" || data.msg == "已达上限") {
                                        $(".integral .point").hide();
                                        $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                    }
                                    $(".integral").css("top", scrollHeightAlert);
                                    $(".integral").show();
                                    //alert($("#totalIntegral",parent.document).text());
                                    // 实时更新积分
                                    $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
                                    setTimeout(function () {
                                        $(".integral .point").show();
                                        $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                        $(".integral").hide();

                                    }, 2000)
                                    if (data.integral != 0) {
                                        download(id, name);
                                    }
                                }
                            }
                        });*/
                        var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                            if (null == data) {
                                download(id, name);
                            } else {
                                $("#num").html(data.msg)
                                if (data.msg == "积分不足" || data.msg == "已达上限") {
                                    $(".integral .point").hide();
                                    $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                }
                                $(".integral").css("top", scrollHeightAlert);
                                $(".integral").show();
                                //alert($("#totalIntegral",parent.document).text());
                                // 实时更新积分
                                $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
                                setTimeout(function () {
                                    $(".integral .point").show();
                                    $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                    $(".integral").hide();

                                }, 2000)
                                if (data.integral != 0) {
                                    download(id, name);
                                }
                            }
                        }, function(data) {

                        });
                        ajax.set("docId",id);
                        ajax.set("ruleCode",'download');
                        ajax.start();
                    }
                }, function(data) {

                });
                ajax.set("docId",id);
                ajax.set("ruleCode",'download');
                ajax.start();
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
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType: "json",
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
function isPDFShow(fileSuffixName){
    return fileSuffixName == ".pdf"
        || fileSuffixName == ".doc" || fileSuffixName == ".docx" || fileSuffixName == ".dot"
        || fileSuffixName == ".wps" || fileSuffixName == ".wpt"
        || fileSuffixName == ".xls" || fileSuffixName == ".xlsx" || fileSuffixName == ".xlt"
        || fileSuffixName == ".et" || fileSuffixName == ".ett"
        || fileSuffixName == ".ppt" || fileSuffixName == ".pptx" || fileSuffixName == ".ppts"
        || fileSuffixName == ".pot" || fileSuffixName == ".dps" || fileSuffixName == ".dpt"
        || fileSuffixName == ".txt"
        || fileSuffixName == ".ceb";
}
function btnState() {
    if(chooseFile.length==0){
        $('.clickBtn').hide()
    }
    if(chooseFile.length>0){
        if(chooseFile.length>1){
            for(var i=0;i<chooseFileType.length;i++){
                if(chooseFileType[i+1]!=undefined){
                    if((chooseFileType[i]=="folder"&&chooseFileType[i+1]!="folder")
                        ||(chooseFileType[i]!="folder"&&chooseFileType[i+1]=="folder")){
                        $('.clickBtn').hide();
                        $(".uploadBtn").hide();
                        return;
                    }else {
                        $('.clickBtn').show();
                    }
                }
            }
        }else {
            $('.clickBtn').show();
        }
    }
    if(openFileId!='abcde4a392934742915f89a586989292'){
        $("#newFolder").hide()
    }else {
        $("#newFolder").show()
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
function  clickCheck(e,id,type,name,index,author) {
    var jq=$(e);
    changeBgColorOfTr(e);
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
            refreshFile(openFileId)
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if(jq.find(".checkbox").prop("checked")==false){

        jq.find(".checkbox").prop("checked",true);
        jq.find(".layui-form-checkbox").addClass("layui-form-checked");

        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)


    }else{
        jq.find(".checkbox").prop("checked",false);
        jq.find(".layui-form-checkbox").removeClass("layui-form-checked");
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFile=chooseFile.del(chooseFile.indexOf(id));
            }
            chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
        }
        if(chooseFile.length==1){
            var id = chooseFile[0];
            reNameParem=chooseFileName[0];
            var index=  $("#"+id+"").val();
            reNameIndex=index
        }

    }
    btnState();
    cancelBubble()
}
$("#marg").on('click', function () {
    if(reNameFlag==true){
        $('#name'+reNameIndex).removeClass("hide");
        $('#inputName'+reNameIndex).addClass("hide");
        reNameFlag=false;
        var inputname = $('#inputName'+reNameIndex).val();
        if(inputname!=reNameParem){
            rename(inputname);
        }
    }
});
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
    btnState();
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
function  clickIconCheck(e,id,type,name,index,author) {
    $(e).toggleClass('layui-form-checked');
    var checkbox=$(e).siblings('.checkbox');
    if(checkbox.prop("checked")==false){
        checkbox.prop("checked",true);
        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)
    }else{
        checkbox.prop("checked",false);
        $()
        if(chooseFile.indexOf(id)!=-1){
            chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
            chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
            chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
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
function  updateNameCollectionFolder(e,id,type,name,author,index) {
    $(".hoverSpan").eq(index).hide()
    $(".moreicon").hide();
    $(".ishover").addClass("hide");
    $(".nameTitpe").removeClass("hide");
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor = [];
    chooseFile.push(id);
    chooseFileType.push(type);
    chooseFileName.push(name);
    chooseFileAuthor.push(author)
    reNameIndex = index;
    reNameParem=name;
    $('#inputName'+index).val( chooseFileName[0]);
    $('#name'+index).addClass("hide");
    $('#inputName'+index).removeClass("hide");
    $('#inputName'+index).select();
    $('#inputName'+index).focus();
    reNameFlag=true;
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
            refreshFile(openFileId)
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if($(th).prop("checked")){
        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)
    }else{
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFile=chooseFile.del(chooseFile.indexOf(id));
                chooseFileAuthor=  chooseFileAuthor.del(chooseFile.indexOf(id))
            }
        }
        if(chooseFile.length==1){
            var id = chooseFile[0];
            reNameParem=chooseFileName[0];
            var index=  $("#"+id+"").val();
            reNameIndex=index
        }
    }
    btnState();
    cancelBubble()
}
function rename(inputname){
    if(chooseFileType[0]=='folder'){
        inputname = inputname.trim();
        if (inputname == '' || inputname == undefined) {
            layer.msg("目录名称不能为空", {anim: 6, icon: 0,offset:scrollHeightMsg});
            $('#inputName' + reNameIndex).val(reNameParem);
            return;
        }
        var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
        //特殊字符
        if (!pattern.test(inputname)) {
            layer.msg("输入的目录名称不合法", {anim: 6, icon: 0,offset:scrollHeightMsg});
            $('#inputName' + reNameIndex).val(reNameParem);
            return;
        }
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;
            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/addCheck", function(data) {
                if (data == "false") {
                    layer.msg("“" + inputname + "”目录已存在", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    $('#inputName' + reNameIndex).val(reNameParem);
                    return;
                } else {
                    var ajax = new $ax(Hussar.ctxPath + "/personalCollection/updateFolderName", function(result) {
                        refreshFile(openFileId);
                    }, function(data) {
                    });
                    ajax.set("collectionId",chooseFile[0]);
                    ajax.set("folderName",inputname);
                    ajax.start();
                }
            }, function(data) {

            });
            ajax.set("name",inputname);
            ajax.set("parentFolderId",openFileId);
            ajax.start();
        });
    }
    refreshFile(openFileId);
}