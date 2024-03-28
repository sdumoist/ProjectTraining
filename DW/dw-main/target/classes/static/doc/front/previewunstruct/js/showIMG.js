/**
 * Created by Lenovo on 2018/2/9.
 */
var pIndex=0;
var size=5;
var page=1;
var total;
var pdfItem={};
var totalItem;
var currentIndex = -1;
var maxIndex = -1;
var bIndex=0;
var viewer = null;
var scrollHeightAlert=0;
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var layerOpen1;
var onImgLoadedHandler=function () {
    if(!!viewer){
        viewer.update();
    }
};
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function(){
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;

    (function() {
        var count;
        var category//分类
        var docId;//文档ID
        var authorId;//作者ID
        var fileName;//文档名臣
        var filePath;//文档路径
        var collection;//是否收藏
        var shareFlag;//是否可分享
        var point = 1;//下载所需积分`
        var docType;

        var  allowPage;
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
        layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
            var $ = layui.jquery,
                Hussar = layui.Hussar,
                layer = layui.layer,
                $ax = layui.HussarAjax;
            initFileTree = function () {
                var $tree = $("#fileTree2");
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
                            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/collectionToFolder", function (data) {
                                if (data.result == "0") {

                                    layer.msg("文件已存在", {icon: 0, offset: scrollHeightMsg});
                                } else if (data.result == "1") {
                                    $(".layui-laypage-btn").click();
                                    layer.close(layerView);
                                    layer.msg("收藏成功", {icon: 1, offset: scrollHeightMsg});
                                } else {

                                    layer.msg("收藏失败", {icon: 2, offset: scrollHeightMsg});
                                }
                            }, function (data) {
                                layer.msg("系统出错，请联系管理员", {icon: 2, offset: scrollHeightMsg});
                            });
                            ajax.set("ids", docId);
                            ajax.set("parentFolderId", openFolder.node.original.id);
                            ajax.start();
                        }
                        layer.confirm('确定要收藏至此目录下吗？', {
                            title: ['收藏', 'background-color:#fff'],
                            offset: scrollHeightAlert,
                            skin: 'move-confirm'
                        }, operation);
                    }
                });
            }
        })
        var gridView ={
            /*初始化页面*/
            initPage: function() {
                var that = this;
                //初始化表格
                that.init();
                that.initButtonEvent();
                that.createPageContext();
            },
            aftTurnInit:function(spageno){
                var that = this;
                //初始化页面
                // that.getRatingRecords(docId,spageno);
            },
            initPath:function(){
                var that = this;
                var id= $("#docId").val();
                /*$.ajax({
                    async: false,
                    type: "post",
                    url: Hussar.ctxPath+"/preview/getFoldPath",
                    data: {docId: id},
                    success: function (data) {
                        var path ="";
                        if(!!data){
                            for(var i=0;i<data.length;i++){
                                path +=" <span><a href='#' target='_blank' data-id='"+data[i].foldId+"'>"+data[i].foldName+"</a> <i class='layui-icon'>&#xe602;</i> </span>";
                            }
                        }else{
                            path ="";
                        }
                        fileName = $("#title").html()
                        path +=" <span>"+fileName+"</span>";
                        var pathes = $(".pathes");
                        pathes.html(path);

                        pathes.on("click","a",function(){
                            if(data[0].type!="2"){
                            var parentId = $(".pathes a").first().data("id");
                            var curId = $(this).data("id");
                            var index =$(this).parent().index();
                            if(index == 0){
                                $(this).attr("href",Hussar.ctxPath+"/personalcenter?fileId="+parentId)
                            }else{
                                var folderName = data[index].foldName;
                                var folderId = data[index].foldId;
                                $(this).attr("href",Hussar.ctxPath+"/personalcenter?menu=11&folderId="+folderId+"&folderName="+folderName
                                );
                            }
                            }else {
                                var curId = $(this).data("id");
                                $(this).attr("href",Hussar.ctxPath+"/toShowComponent/toShowPDF?id="+curId);


                            }
                        });
                        //获取总长度与父级长度
                        var pathes_span = pathes.find("span"),box_length = 0;
                        var outlength = $(".breadCrumbs").width() - 70;
                        pathes_span.each(function () {
                            box_length = box_length + $(this).width();
                        });
                        pathes.width(box_length);
                        var innerlength,subLength = 0;
                        if(box_length>outlength){
                            for(var n = 0;n<pathes_span.length;n++){
                                subLength = subLength + pathes_span.eq(n).width();
                                innerlength = box_length - subLength;
                                if(innerlength <= outlength){
                                    $(".control-btn-l").show();
                                    $(".control-btn-r").hide();
                                    pathes.css({"transform":"translateX(-"+subLength+"px)"});
                                    break;
                                }
                            }
                        }
                        var subLength_l = subLength;
                        $(".control-btn-l").click(function () {
                            var subLength_l = subLength_l - outlength;
                            if(subLength_l > 0){
                                pathes.css({"transform":"translateX(-"+subLength_l+"px)"});
                            }else {
                                subLength_l = 0;
                                pathes.css({"transform":"translateX(-"+subLength_l+"px)"});
                                $(".control-btn-l").hide();
                                $(".control-btn-r").show();
                            }
                        });
                        var subLength_r = box_length;
                        var rLength = 0;
                        $(".control-btn-r").click(function () {
                            subLength_r = subLength_r - outlength;
                            if(subLength_r > outlength){
                                rLength = rLength +outlength;
                                pathes.css({"transform":"translateX(-"+rLength+"px)"});
                            }else {
                                $(".control-btn-l").show();
                                $(".control-btn-r").hide();
                                pathes.css({"transform":"translateX(-"+subLength+"px)"});
                            }
                        });
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/preview/getFoldPath", function(data) {
                    var path ="<span><a href=\""+Hussar.ctxPath+"\/\" target=\"_blank\" data-id=\"01\">首页</a> <i class=\"layui-icon\"></i> </span>";
                    if(!!data){
                        for(var i=0;i<data.length;i++){
                            path +=" <span><a href='#' target='_blank' data-id='"+data[i].foldId+"'>"+data[i].foldName+"</a> <i class='layui-icon'>&#xe602;</i> </span>";
                        }
                    }else{
                        path ="";
                    }
                    path +=" <span>"+fileName+"</span>";
                    var pathes = $(".pathes");
                    pathes.html(path);

                    pathes.on("click","a",function(){
                        if(data[0].type=="3"){
                            var curId = $(this).data("id");
                            $(this).attr("href","/toShowConsulation/toShowPDF?id="+curId);
                        }else if(data[0].type=="2"){
                            var curId = $(this).data("id");
                            $(this).attr("href","/toShowComponent/toShowPDF?id="+curId);
                        }else {
                            var parentId = $(".pathes a").first().data("id");
                            var curId = $(this).data("id");
                            var index =$(this).parent().index()-1;

                            if(index == 0){
                                $(this).attr("href",Hussar.ctxPath+"/personalcenter?fileId="+parentId)
                            }else{
                                var folderName = data[index].foldName;
                                var folderId = data[index].foldId;
                                $(this).attr("href",Hussar.ctxPath+"/personalcenter?menu=11&folderId="+folderId+"&folderName="+folderName
                                );
                            }
                        }
                    });
                    //获取总长度与父级长度
                    var pathes_span = pathes.find("span"),box_length = 0;
                    var outlength = $(".breadCrumbs").width() - 70;
                    pathes_span.each(function () {
                        box_length = box_length + $(this).width();
                    });
                    pathes.width(box_length);
                    var innerlength,subLength = 0;
                    if(box_length>outlength){
                        for(var n = 0;n<pathes_span.length;n++){
                            subLength = subLength + pathes_span.eq(n).width();
                            innerlength = box_length - subLength;
                            if(innerlength <= outlength){
                                $(".control-btn-l").show();
                                $(".control-btn-r").hide();
                                pathes.css({"transform":"translateX(-"+subLength+"px)"});
                                break;
                            }
                        }
                    }
                    var subLength_l = subLength;
                    $(".control-btn-l").click(function () {
                        var subLength_l = subLength_l - outlength;
                        if(subLength_l > 0){
                            pathes.css({"transform":"translateX(-"+subLength_l+"px)"});
                        }else {
                            subLength_l = 0;
                            pathes.css({"transform":"translateX(-"+subLength_l+"px)"});
                            $(".control-btn-l").hide();
                            $(".control-btn-r").show();
                        }
                    });
                    var subLength_r = box_length;
                    var rLength = 0;
                    $(".control-btn-r").click(function () {
                        subLength_r = subLength_r - outlength;
                        if(subLength_r > outlength){
                            rLength = rLength +outlength;
                            pathes.css({"transform":"translateX(-"+rLength+"px)"});
                        }else {
                            $(".control-btn-l").show();
                            $(".control-btn-r").hide();
                            pathes.css({"transform":"translateX(-"+subLength+"px)"});
                        }
                    });
                }, function(data) {

                });
                ajax.set("docId",id);
                ajax.set("showType",$("#showType").val());
                ajax.start();
            },
            init:function(){
                var that = this;
                var id= $("#docId").val();
                var folderId = $("#folderId").val();
                var all=""
                docId = id;
                var ajax = new $ax(Hussar.ctxPath + "/preview/fileDetail", function(data) {
                    // document.title = data.title+"-"+projectTitle;
                    document.title = data.title;
                    authorId = data.authorId;
                    filePath = data.filePath;
                    fileName = data.title;
                    // gridView.initPath();
                    collection =data.collection;
                    shareFlag = data.shareFlag;
                    fileSuffixName = data.fileSuffixName.toLowerCase();
                    docType = "." + fileSuffixName;
                    docAbstract = data.docAbstract;
                    filePdfPath = data.filePdfPath;
                    docId = data.id;
                    authority=data.authority;
                    $("#docId").text(data.id);
                    $("#title").text(data.title);

                    var obj = document.getElementById("title");
                    if(fileSuffixName=="xlsx"||fileSuffixName=="xls"){  //文档名称前的图片
                        $("#title").addClass("type-xls");
                        // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
                    }else if(fileSuffixName=="pdf"){
                        $("#title").addClass("type-pdf");
                        // obj.style.cssText = "background:url(/static/resources/img/pdf.png)no-repeat left center;"
                    }else if(fileSuffixName=="ppt"||fileSuffixName=="pptx"||fileSuffixName=="ppsx"){
                        $("#title").addClass("type-ppt");
                        // obj.style.cssText = "background:url(/static/resources/img/ppt.png)no-repeat left center;"
                    }else if(fileSuffixName=="ceb"){
                        $("#title").addClass("type-ceb");
                        // obj.style.cssText = "background:url(/static/resources/img/ppt.png)no-repeat left center;"
                    }else if(fileSuffixName=="txt") {
                        $("#title").addClass("type-txt");
                        // obj.style.cssText = "background:url(/static/resources/img/txt.png)no-repeat left center;";
                    }else if(fileSuffixName=="doc"||fileSuffixName=="docx") {
                        $("#title").addClass("type-doc");
                        // obj.style.cssText = "background:url(/static/resources/img/word.png)no-repeat left center;";
                    }else if(fileSuffixName=="png"||fileSuffixName=="jpeg"||fileSuffixName=="gif"||fileSuffixName=="jpg") {
                        $("#title").addClass("type-pic");
                        // obj.style.cssText = "background:url(/static/resources/img/word.png)no-repeat left center;";
                    }else if(fileSuffixName=="bmp") {
                        $("#title").addClass("type-bmp");
                    }else {
                        $("#title").addClass("type-other");
                        // obj.style.cssText = "background:url(/static/resources/img/other.png)no-repeat left center;";
                    }
                    var author=data.author;
                    if(author==""||author==undefined){
                        author=data.userId;
                    }
                    // $("#owner").html(""+author+"<em>|</em>");
                    // $("#uploadTime").html(""+data.createTime.slice(0,10) +"<em>|</em>");
                    // $("#fileSize").html(""+data.fileSize +"<em>|</em>")
                    // $("#downloadNum").html(""+data.downloadNum +"次下载"+"<em>|</em>");
                    // $("#previewNum").html(""+data.readNum+"次预览" );
                    $("#owner").html("上传者: "+author+"<em>|</em>");
                    $("#uploadTime").html("上传时间: "+data.createTime.slice(0,10) +"<em>|</em>");
                    $("#fileSize").html("文件大小: "+data.fileSize +"<em>|</em>");
                    $("#downloadNum").html("下载次数: "+"<span>"+data.downloadNum +"</span>"+"<em>|</em>");
                    $("#previewNum").html("预览次数: "+"<span>"+data.readNum+'</span>' );
                    if(authority=='1'||data.adminFlag==1||authority=='2'){
                        $("#dowLoadButton").show();
                    }
                    // if (shareFlag == '1'){
                    //     $("#shareBtn").show();
                    //     $("#shareBtn").css('display','inline-block');
                    // }

                    var ajax = new $ax(Hussar.ctxPath + "/preview/folderIMG", function(data) {
                        var items = data.items;

                        var toLoad = "";
                        var preAppend = "";
                        var afterAppend = "";
                        var toSwitch = "";
                        totalItem=items;
                        total=items.length;
                        if (items.length != 0) {
                            var selfIndex = 0;
                            maxIndex = items.length - 1;
                            for (var k = 0; k < items.length; k++) {//建议只显示4个，增加更多链接
                                if(items[k].isSelf){
                                    selfIndex = k;
                                    currentIndex = k;
                                }
                            }
                            var img_src =Hussar.ctxPath+"/preview/list?fileId=" + filePath;
                            if(CheckImgExists(img_src)){
                                // $(".img-container ").addClass("nofile")
                            }else {
                                $("#showImg").attr("data-id",selfIndex);
                                $("#showImg").attr("lay-src",img_src);
                            }
                            var min = 0;
                            var max = size * 2;
                            if (selfIndex - size < 0){
                                min = 0;
                                max = 2 * size;
                            } else {
                                min = selfIndex - size;
                                max = selfIndex + size;
                            }
                            for (var i = min; i < items.length && i < max; i++) {//建议只显示4个，增加更多链接


                                pdfItem[items[i].filePdfPath]=items[i].filePath;
                                if (i < selfIndex){
                                    if (null == items[i].thumbPath || items[i].thumbPath == '' || items[i].thumbPath == undefined){
                                        preAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '">';
                                    } else {
                                        preAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].thumbPath + '&isThumbnails=2">';
                                    }
                                }
                                if (i > selfIndex){
                                    if (null == items[i].thumbPath || items[i].thumbPath == '' || items[i].thumbPath == undefined){
                                        afterAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '">';
                                    } else {
                                        afterAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].thumbPath + '&isThumbnails=2">';
                                    }
                                }
                                if(items[i].isSelf){
                                } else {
                                    toLoad += '<img onload="onImgLoadedHandler"   style="display: none" src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePdfPath + '&isThumbnails=0">';
                                }
                            }
                            all=toLoad;
                            $("#viewer").prepend(preAppend);
                            $("#viewer").append(afterAppend);
                        }else{
                            var ImgSrc = "/preview/list?fileId=" + filePath;
                            if(CheckImgExists(ImgSrc)){
                                // $(".img-container ").addClass("nofile")
                            }else {
                                toLoad += '<img id="showImg"  onerror="javascript:this.src=de'+Hussar.ctxPath+'faults.png" src= '+Hussar.defaults.pngSrc+'  oncontextmenu = "return false;"style="   -webkit-user-select: none; -moz-user-select: none; -ms-user-select: none;user-select: none;">';
                                $("#showImg").attr("data-id",selfIndex);
                                $("#showImg").attr("lay-src","/preview/list?fileId=" + filePath);
                                $("#viewer").html("");
                                $("#viewer").append(toLoad);
                            }

                        }
                    }, function(data) {

                    });
                    ajax.set("folderId",folderId);
                    ajax.set("docId",docId);
                    ajax.set("page",1);
                    ajax.set("size",1000);
                    ajax.start();
                    if(  document.getElementById("showImg")==null){
                        var    spetoLoad = '<img id="showImg"  oncontextmenu = "return false;"style="   -webkit-user-select: none; -moz-user-select: none; -ms-user-select: none;user-select: none;">'
                        $("#viewer").html("");
                        $("#viewer").append(spetoLoad+all);
                    }
                    $("#collectionToFolder").hide();
                    $("#cancelCollection").hide();
                    // if(collection=='0'){
                    //     $("#collectionToFolder").show();
                    //     $("#cancelCollection").hide();
                    // }else{
                    //     $("#collectionToFolder").hide();
                    //     $("#cancelCollection").show();
                    // }
                    pdfItem[filePdfPath]=data.filePath;
                    //document.getElementById("showImg-big").src = "/preview/list?fileId="+encodeURIComponent(data.filePath);

                    viewer = new Viewer(document.getElementById('viewer'),{title:false, keyboard:false,navbar:false,interval:10000000000, toolbar: {
                            zoomIn: 4,
                            zoomOut: 4,
                            oneToOne: 4,
                            play: {
                                show: 4,
                                size: 'large',
                            },
                            rotateLeft: 4,
                            rotateRight: 4,
                            flipHorizontal: 4,
                            flipVertical: 4,




                        }});
                    // $("#showImg").click(function () {
                    //
                    // var pIndex=    $("#showImg").index(this)
                    // })

                    document.getElementById('viewer').addEventListener('view', function (e) {

                        var toLoad="";
                        bIndex=e.detail.index;
                    });

                }, function(data) {
                    $.showInfoDlg("提示","预览失败",2);
                });
                ajax.set("id",id);
                ajax.set("category",category);
                ajax.start();
                /**
                 * 加载当前项目中所包含的其他图片
                 */
                var items = totalItem;
                var toLoad = "";
                if (items.length != 0) {
                    $("#projectIMG").removeClass("hide");
                    var selfIndex = 0;
                    for (var i = 0; i < items.length; i++) {//建议只显示4个，增加更多链接
                        if(items[i].isSelf){
                            selfIndex = i;
                        }
                    }
                    if (items.length - selfIndex < 4){
                        selfIndex = items.length - 4;
                    }
                    if (selfIndex < 0) {
                        selfIndex = 0;
                    }
                    for (var j = selfIndex; j < items.length && j < selfIndex + 4; j++) {//建议只显示4个，增加更多链接
                        /*if(items[i].isSelf){
                            toLoad += '<li class="photo-item self">';
                        } else {
                            toLoad += '<li class="photo-item">';
                        }*/
                        toLoad += '<li class="photo-item">';
                        toLoad += '<a href="javascript:void(0)" onclick=\'showDoc("' + items[j].docType + '","' + items[j].docId + '");return false;\' target="_blank">';
                        toLoad += '<img src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[j].filePdfPath + '&&isThumbnails=0" alt="' + items[j].title + '" onerror="javascript:this.src=\''+Hussar.ctxPath+'/static/resources/img/img-default-thum.png\'">';
                        toLoad += '<div class="img-info" title="'+ items[j].title + '">' + items[j].title + '</div>';
                        toLoad += '</a>';
                        toLoad += '</li>';
                    }
                    $("#showFolderIMG").html("");
                    $("#showFolderIMG").append(toLoad);
                    $('.grid').masonry('reload');
                }
                /**
                 * 加载与当前图片名称相关的推荐图片
                 */
                /*$.ajax({
                    async: false,
                    type: "post",
                    url: Hussar.ctxPath+"/preview/recommendIMG",
                    dataType: 'json',
                    data: {keyword: fileName,imgId:docId, folderId: folderId, fileType: "8", page:"1", size: "50", tagString: $("#tags").val()},
                    success: function (data) {
                        var items = data;
                        var toLoad = "";
                        if (items.length != 0) {
                            $("#recommendIMG").removeClass("hide");
                            for (var i = 0; i < 4 && i < items.length; i++) {//同上
                                toLoad += '<li class="photo-item">';
                                toLoad += '<a href="javascript:void(0)" onclick=\'showDocBlank("' + items[i].docType + '","' + items[i].docId + '");return false;\' target="_blank">';
                                toLoad += '<img src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePdfPath + '&&isThumbnails=0" alt="' + items[i].title + '">';
                                toLoad += '<div class="img-info" title="' + items[i].title + '">' + items[i].title + '</div>';
                                toLoad += '</a>';
                                toLoad += '</li>';
                            }
                            $("#showRecommendIMG").html("");
                            $("#showRecommendIMG").append(toLoad);
                            $('.grid').masonry('reload');
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/preview/recommendIMG", function(data) {
                    var items = data;
                    var toLoad = "";
                    if (items.length != 0) {
                        $("#recommendIMG").removeClass("hide");
                        for (var i = 0; i < 4 && i < items.length; i++) {//同上
                            toLoad += '<li class="photo-item">';
                            toLoad += '<a href="javascript:void(0)" onclick=\'showDocBlank("' + items[i].docType + '","' + items[i].docId + '");return false;\' target="_blank">';
                            toLoad += '<img src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePdfPath + '&&isThumbnails=0" alt="' + items[i].title + '" onerror="javascript:this.src=\''+Hussar.ctxPath+'/static/resources/img/img-default-thum.png\'">';
                            toLoad += '<div class="img-info" title="' + items[i].title + '">' + items[i].title + '</div>';
                            toLoad += '</a>';
                            toLoad += '</li>';
                        }
                        $("#showRecommendIMG").html("");
                        $("#showRecommendIMG").append(toLoad);
                        $('.grid').masonry('reload');
                    }
                }, function(data) {

                });
                ajax.set("keyword",fileName);
                ajax.set("imgId",docId);
                ajax.set("folderId",folderId);
                ajax.set("fileType","8");
                ajax.set("page","1");
                ajax.set("size","50");
                ajax.set("tagString",$("#tags").val());
                ajax.start();

                var grid= $('.grid').masonry({
                    // options
                    itemSelector: '.photo-item'
                });
                grid.imagesLoaded().always( function() {
                    $(".photo-list .photo-item img").each(function () {
                        if($(this).height()<"30"){
                            $(this).height("120");
                            $(this).width("auto");

                        }
                    })
                    grid.masonry();
                });
                /**
                 * 初始化图片标签
                 */
                if($("#tags").val() != ""){
                    var tags = $("#tags").val().split(",");
                    $("#showImgTags").removeClass("hide");
                    layui.use(['jquery','laytpl'], function() {
                        var $ = layui.jquery;
                        var laytpl = layui.laytpl;
                        var getTpl = $("#imgTags").html();
                        laytpl(getTpl).render(tags, function(html){
                            $("#imgTags").siblings().remove();
                            $("#imgTags").after(html);
                        });
                    });
                };

                //积分系统控制
                /*$.ajax({
                    url: Hussar.ctxPath+"/integral/addIntegral",
                    async: true,
                    data:{
                        docId: id,
                        ruleCode: 'preview'
                    },
                    success: function (data) {
                        if (null != data && data != '' && data != undefined){
                            if (data.integral != 0 && data.integral != null && data.integral != ''){
                                $("#num").html(data.msg)
                                $(".integral").show();
                                setTimeout(function () {
                                    $(".integral").hide();
                                },2000)
                            }
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                    if (null != data && data != '' && data != undefined){
                        if (data.integral != 0 && data.integral != null && data.integral != ''){
                            $("#num").html(data.msg)
                            $(".integral").show();
                            setTimeout(function () {
                                $(".integral").hide();
                            },2000)
                        }
                    }
                }, function(data) {

                });
                ajax.set("docId",id);
                ajax.set("ruleCode",'preview');
                ajax.start();
            },
            initButtonEvent:function (){
                var that = this;
                $("#collectionButton").click(function(){
                    that.collectionFiles();
                });
                $('#loginButton').click(function () {
                    loginSubmit();
                });
                var collectionLink ="/personalCollection/addCollection";
                var  cancelCollection="/personalCollection/cancelCollection";
                $("#collection").click(function () {
                    /*$.ajax({
                        type:"post",
                        url: Hussar.ctxPath+collectionLink,
                        async:false,
                        data:{
                            docIds:docId
                        },
                        success:function(data) {
                            if(data.success=='0'){
                                //alert("收藏成功");
                                $("#collection").hide();
                                $("#cancelCollection").show();

                            }else{
                                alert("收藏失败");

                            }
                        },
                    });*/
                    var ajax = new $ax(Hussar.ctxPath + collectionLink, function(data) {
                        if(data.success=='0'){
                            //alert("收藏成功");
                            $("#collectionToFolder").hide();
                            $("#cancelCollection").show();

                        }else{
                            alert("收藏失败");

                        }
                    }, function(data) {

                    });
                    ajax.set("docIds",docId);
                    ajax.start();
                });
                $("#cancelCollection").click(function () {
                    layui.use(['HussarAjax','Hussar'], function(){
                        var Hussar = layui.Hussar,
                            $ax = layui.HussarAjax;

                        /*$.ajax({
                            type:"post",
                            url: Hussar.ctxPath+cancelCollection,
                            async:false,
                            data:{
                                docIds:docId
                            },
                            success:function(data) {
                                if(data.success=='0'){
                                    //alert("取消收藏成功");
                                    $("#collection").show();
                                    $("#cancelCollection").hide();
                                }else{
                                    alert("取消收藏失败");

                                }
                            },
                        });*/
                        layer.confirm("确定取消收藏吗？", {
                            title: ['取消收藏', 'background-color:#fff'],
                            skin:'move-confirm'
                        }, function (index) {
                            var ajax = new $ax(Hussar.ctxPath + cancelCollection, function(data) {
                                if (data.success == "0") {
                                    $("#collectionToFolder").show();
                                    $("#cancelCollection").hide();
                                    layer.close(index)
                                    layer.msg('取消收藏成功',{icon: 1,offset:'40%'})
                                } else if (data.code) {
                                    alert("取消收藏失败");
                                    layer.close(index)
                                } else {
                                    alert("取消收藏失败");
                                    layer.close(index)
                                }
                            }, function(data) {
                                alert("取消收藏失败");
                                layer.close(index)
                            });
                            ajax.set("docIds",docId);
                            ajax.start();
                        })
                    });
                });
                $("#dowLoadButton").on('click',function(){
                    /*$.ajax({
                        url: Hussar.ctxPath+"/integral/downloadIntegral",
                        async: true,
                        data: {
                            docId: docId,
                            ruleCode: 'download'
                        },
                        success: function (data) {
                            if (data.status == "1") {
                                var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                    icon: 3,
                                    title: '提示'
                                }, function (index) {
                                    layer.close(index2);
                                    var valid = true;
                                    $.ajax({
                                        url: Hussar.ctxPath+"/integral/addIntegral",
                                        async: false,
                                        data: {
                                            docId: docId,
                                            ruleCode: 'download'
                                        },
                                        success: function (data) {
                                            if (data != null) {

                                                $("#num").html(data.msg)
                                                if (data.msg == "积分不足") {
                                                    $(".integral .point").hide();
                                                    $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                                }
                                                $(".integral").show();
                                                setTimeout(function () {
                                                    $(".integral .point").show();
                                                    $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                                    $(".integral").hide();

                                                }, 2000)

                                                if (data.integral == 0) {
                                                    valid = false;
                                                }
                                            }
                                        }
                                    });
                                    if (valid) {
                                        $.ajaxFileUpload({
                                            url: Hussar.ctxPath+"/files/fileDownNew",
                                            type: "post",
                                            data: {
                                                docName: "",//fileName,
                                                docIds: docId
                                            }
                                        });
                                    }
                                })
                            } else {
                                var valid = true;
                                $.ajax({
                                    url: Hussar.ctxPath+"/integral/addIntegral",
                                    async: false,
                                    data: {
                                        docId: docId,
                                        ruleCode: 'download'
                                    },
                                    success: function (data) {
                                        if (data != null) {

                                            $("#num").html(data.msg)
                                            if (data.msg == "积分不足") {
                                                $(".integral .point").hide();
                                                $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                            }
                                            $(".integral").show();
                                            setTimeout(function () {
                                                $(".integral .point").show();
                                                $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                                $(".integral").hide();

                                            }, 2000)

                                            if (data.integral == 0) {
                                                valid = false;
                                            }
                                        }
                                    }
                                });
                                if (valid) {
                                    $.ajaxFileUpload({
                                        url: Hussar.ctxPath+"/files/fileDownNew",
                                        type: "post",
                                        data: {
                                            docName: "",//fileName,
                                            docIds: docId
                                        }
                                    });
                                }
                            }
                        }
                    })*/
                    var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {
                        if (data.status == "1") {
                            var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                icon: 3,
                                title: '提示',
                                skin:'download-info'
                            }, function (index) {
                                layer.close(index2);
                                var valid = true;
                                /*$.ajax({
                                    url: Hussar.ctxPath+"/integral/addIntegral",
                                    async: false,
                                    data: {
                                        docId: docId,
                                        ruleCode: 'download'
                                    },
                                    success: function (data) {
                                        if (data != null) {

                                            $("#num").html(data.msg)
                                            if (data.msg == "积分不足") {
                                                $(".integral .point").hide();
                                                $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                            }
                                            $(".integral").show();
                                            setTimeout(function () {
                                                $(".integral .point").show();
                                                $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                                $(".integral").hide();

                                            }, 2000)

                                            if (data.integral == 0) {
                                                valid = false;
                                            }
                                        }
                                    }
                                });*/
                                var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                                    if (data != null) {

                                        $("#num").html(data.msg)
                                        if (data.msg == "积分不足") {
                                            $(".integral .point").hide();
                                            $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                        }
                                        $(".integral").show();
                                        setTimeout(function () {
                                            $(".integral .point").show();
                                            $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                            $(".integral").hide();

                                        }, 2000)

                                        if (data.integral == 0) {
                                            valid = false;
                                        }
                                    }
                                }, function(data) {

                                });
                                ajax.set("docId",docId);
                                ajax.set("ruleCode",'download');
                                ajax.start();
                                if (valid) {
                                    $.ajaxFileUpload({
                                        url: Hussar.ctxPath+"/files/fileDownNew",
                                        type: "post",
                                        data: {
                                            docName: "",//fileName,
                                            docIds: docId
                                        }
                                    });
                                }
                            })
                        } else {
                            var valid = true;
                            /*$.ajax({
                                url: Hussar.ctxPath+"/integral/addIntegral",
                                async: false,
                                data: {
                                    docId: docId,
                                    ruleCode: 'download'
                                },
                                success: function (data) {
                                    if (data != null) {

                                        $("#num").html(data.msg)
                                        if (data.msg == "积分不足") {
                                            $(".integral .point").hide();
                                            $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                        }
                                        $(".integral").show();
                                        setTimeout(function () {
                                            $(".integral .point").show();
                                            $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                            $(".integral").hide();

                                        }, 2000)

                                        if (data.integral == 0) {
                                            valid = false;
                                        }
                                    }
                                }
                            });*/
                            var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                                if (data != null) {

                                    $("#num").html(data.msg)
                                    if (data.msg == "积分不足") {
                                        $(".integral .point").hide();
                                        $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                    }
                                    $(".integral").show();
                                    setTimeout(function () {
                                        $(".integral .point").show();
                                        $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                        $(".integral").hide();

                                    }, 2000)

                                    if (data.integral == 0) {
                                        valid = false;
                                    }
                                }
                            }, function(data) {

                            });
                            ajax.set("docId",docId);
                            ajax.set("ruleCode",'download');
                            ajax.start();
                            if (valid) {
                                $.ajaxFileUpload({
                                    url: Hussar.ctxPath+"/files/fileDownNew",
                                    type: "post",
                                    data: {
                                        docName: "",//fileName,
                                        docIds: docId
                                    }
                                });
                            }
                        }
                    }, function(data) {

                    });
                    ajax.set("docId",docId);
                    ajax.set("ruleCode",'download');
                    ajax.start();
                });
                // $("#collectionToFolder").on('click',function(){
                //     var operation =function(){
                //         layerView=layer.open({
                //             type : 1,
                //             area: ['400px','434px'],
                //             btn: ['确定','取消'],
                //             //shift : 1,
                //             shadeClose: false,
                //             skin: 'move-class',
                //             title : ['目录结构','background-color:#fff'],
                //             maxmin : false,
                //             offset:scrollHeightLong,
                //             content : $("#filTree"),
                //             success : function() {
                //                 initFileTree();
                //                 layer.close(index);
                //             },
                //             end: function () {
                //                 layer.closeAll(index);
                //             }
                //         });
                //     }
                //     var index = layer.confirm('确定要收藏所选文件吗？',{title :['收藏至','background-color:#fff'], offset:scrollHeightAlert,skin:'move-confirm'},operation);
                // });
                $("#collectionToFolder").on('click', function () {
                    layerOpen1 = layer.open({
                        type: 2,
                        id:"collections",
                        area: [ '402px',  '432px'],
                        fix: false, //不固定
                        move: false,
                        offset:scrollHeightTip,
                        maxmin: false,
                        shadeClose: true,
                        moveOut: true,
                        shade: 0.4,
                        title: "<span id='upLevel' onclick='upLevel()'>收藏</span>",
                        content: "/personalCollection/collectionToFolderView?docId="+docId
                    });
                    $("#collections").parent().addClass("collectionsContainer")
                });
                //鼠标在收藏夹弹出框上时阻止滚动
                var preventDefault = function (e) {
                    e.preventDefault();
                };
                $(document).on('mouseenter',".collectionsContainer",function (e) {
                    $(document).on('scroll',preventDefault(e))
                });
                $(document).on('mouseleave',".collectionsContainer",function (e) {
                    $(document).off('scroll',preventDefault(e))
                });
                $("#queryBtn").click(function () {
                    var  docName =  $("#fileName").val();
                    var fileType =   $('input:radio:checked').val();
                    if(docName!=""){
                        window.location.href=Hussar.ctxPath+"/search?keyWords="+docName+"&fileType="+fileType+"&page="+1;
                    }else{
                        //alert("请输入关键词，多个关键词以空格隔开");
                        $.showInfoDlg("提示","请输入关键词，多个关键词以空格隔开!",2);
                    }
                });
                // 关键词搜索框添加绑定回车函数
                $('#fileName').bind('keypress', function(event) {
                    if (event.keyCode == "13") {
                        $("#queryBtn").click();
                    }
                });

                $("#shareBtn").click(function () {
                    openShare('', '/s/shareConfirm', 538, 390,docId,docType,fileName);
                });
                $("#next").click(function () {
                    if (currentIndex == -1 || maxIndex == -1 || currentIndex == maxIndex){
                        if(currentIndex == maxIndex){
                            layer.msg("已经是最后一张");
                        }
                        return;
                    }
                    var current = $("[data-id='" + currentIndex + "']");
                    var nextIndex = parseInt(currentIndex) + 1;
                    var next = $("[data-id='" + nextIndex + "']");
                    $("#docId").val(next.attr("docId"));
                    docId = next.attr("docId");
                    next.attr("src",next.attr("thumb-src"));
                    current.addClass("hide");
                    next.removeClass("hide");
                    initFileMsg(next.attr("docId"),category);
                    currentIndex = nextIndex;
                    initFolderImg();
                    var lastchildIndex = $("#viewer img:last-child").attr("data-id");
                    var firstchildIndex = $("#viewer img:first-child").attr("data-id");
                    if (lastchildIndex - currentIndex < size){
                        var items = totalItem;
                        if (items.length != 0) {
                            var preAppend = "";
                            var afterAppend = "";
                            maxIndex = items.length - 1;
                            var min = 0;
                            var max = size * 2;
                            if (currentIndex - size < 0){
                                min = 0;
                                max = 2 * size;
                            } else {
                                min = nextIndex - size;
                                max = nextIndex + size;
                            }
                            for (var i = min; i < items.length && i < max; i++) {//建议只显示4个，增加更多链接


                                pdfItem[items[i].filePdfPath]=items[i].filePath;
                                if (i < firstchildIndex){
                                    if (null == items[i].thumbPath || items[i].thumbPath == '' || items[i].thumbPath == undefined){
                                        preAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '">';
                                    } else {
                                        preAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].thumbPath + '&isThumbnails=2">';
                                    }                            }
                                if (i > lastchildIndex){
                                    if (null == items[i].thumbPath || items[i].thumbPath == '' || items[i].thumbPath == undefined){
                                        afterAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '">';
                                    } else {
                                        afterAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].thumbPath + '&isThumbnails=2">';
                                    }
                                }
                            }
                            $("#viewer").prepend(preAppend);
                            $("#viewer").append(afterAppend);
                            if(!!viewer){
                                viewer.update();
                            }
                            $("#viewer img").off("click");
                            $("#viewer img").on("click",function () {
                                $(this).attr("src",$(this).attr("lay-src"));
                            });
                        }
                    }
                });
                document.onkeydown=jumpPage;
                function jumpPage() {
                    if (event.keyCode == "37") {
                        $("#last").click();
                    }
                    if (event.keyCode == "39") {
                        $("#next").click();
                    }
                }
                $("#last").click(function () {
                    if (currentIndex == -1 || maxIndex == -1 || currentIndex == 0){
                        if(currentIndex == 0){
                            layer.msg("已经是第一张");
                        }
                        return;
                    }
                    var current = $("[data-id='" + currentIndex + "']");
                    var lastIndex = parseInt(currentIndex) - 1;
                    var last = $("[data-id='" + lastIndex + "']");
                    $("#docId").val(last.attr("docId"));
                    docId = last.attr("docId");
                    last.attr("src",last.attr("thumb-src"));
                    current.addClass("hide");
                    last.removeClass("hide");
                    initFileMsg(last.attr("docId"),category);
                    currentIndex = lastIndex;
                    initFolderImg();
                    var lastchildIndex = $("#viewer img:last-child").attr("data-id");
                    var firstchildIndex = $("#viewer img:first-child").attr("data-id");
                    if (firstchildIndex - currentIndex < size){
                        var items = totalItem;
                        if (items.length != 0) {
                            var preAppend = "";
                            var afterAppend = "";
                            maxIndex = items.length - 1;
                            var min = 0;
                            var max = size * 2;
                            if (lastIndex - size < 0){
                                min = 0;
                                max = 2 * size;
                            } else {
                                min = lastIndex - size;
                                max = lastIndex + size;
                            }
                            for (var i = min; i < items.length && i < max; i++) {//建议只显示4个，增加更多链接


                                pdfItem[items[i].filePdfPath]=items[i].filePath;
                                if (i < firstchildIndex){
                                    if (null == items[i].thumbPath || items[i].thumbPath == '' || items[i].thumbPath == undefined){
                                        preAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '">';
                                    } else {
                                        preAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].thumbPath + '&isThumbnails=2">';
                                    }                            }
                                if (i > lastchildIndex){
                                    if (null == items[i].thumbPath || items[i].thumbPath == '' || items[i].thumbPath == undefined){
                                        afterAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '">';
                                    } else {
                                        afterAppend += '<img data-id="' + i + '" docId="' + items[i].docId + '" title="' + items[i].title + '" oncontextmenu = "return false;" lay-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePath + '" onload="onImgLoadedHandler"   class="hide" src="'+Hussar.ctxPath+'/static/assets/img/throbber.gif" thumb-src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].thumbPath + '&isThumbnails=2">';
                                    }
                                }
                            }
                            $("#viewer").prepend(preAppend);
                            $("#viewer").append(afterAppend);
                            if(!!viewer){
                                viewer.update();
                            }
                            $("#viewer img").off("click");
                            $("#viewer img").on("click",function () {
                                $(this).attr("src",$(this).attr("lay-src"));
                            });
                        }
                    }
                });
                $("#viewer img").on("click",function () {
                    $(this).attr("src",$(this).attr("lay-src"));
                });


            },

            /**
             * 分页条
             */
            createPageContext : function(){
                var that = this;
                $('#footDiv').extendPagination({
                    totalCount: count,
                    showPage: 10,
                    limit: 10,
                    callback: function (curr, limit, totalCount) {
                        // that.getRatingRecords(docId,curr);
                    }
                })
            },

            downloadFile:function(){//文档ID，作者ID，下载消耗积分,文档名，分类,文件路径
                var loginName = $("#loginName")[0].innerText;//获取登录用户的名称
                //判断用户是否登录
                if(loginName == "登录"){
                    $("#login").modal();
                    return;
                }

                var ajax = new $ax(Hussar.ctxPath + "/file/changePoints", function(data) {
                    if(data == "success"){
                        $.ajaxFileUpload({
                            url: Hussar.ctxPath+"/file/fileDownload",
                            type:"post",
                            data:{
                                fileName:fileName,
                                category:category,
                                filePath:filePath
                            }
                        });
                    }else {
                        $.showInfoDlg("提示","下载出错",2);
                    }
                }, function(data) {
                    //alert("下载失败");
                    $.showInfoDlg("提示","下载失败",2);
                });
                ajax.set("authorId",authorId);
                ajax.set("docId",docId);
                ajax.set("points",point);
                ajax.start();
            }
        }
        var fileType = $("#fileTypeValue").val(); //文档类型
        if (fileType==null||fileType==""||fileType==undefined){
            fileType=0;
        }
        $("#select").val(fileType);
        if(fileType=='6'||fileType=='8'||fileType=='9'||fileType=='10'){
            $(".search-box").hide()
        }
        $("input[type=radio][name='fileType']").eq(fileType).attr("checked","checked");

        $('input[type=radio][name=fileType]').change(function() {
            var fileType =    $("input[type=radio][name='fileType']:checked").val();
            $("#fileTypeValue").val(fileType);
        });

        $(document).ready(function() {
            gridView.initPage();
        });
    })(this);
});
function loginSubmit(){
    var name = $("#name").val();
    var password = $("#password").val();
    if(name==""){
        //alert("请输入账号！");
        $.showInfoDlg("提示","请输入账号！",2);
        return;
    }else if(password==""){
        //alert("请输入密码！");
        $.showInfoDlg("提示","请输入密码！",2);
        return ;
    }
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;


        var ajax = new $ax(Hussar.ctxPath + "/index/loginCheck", function(data) {
            if(data=="false"){
                //alert("用户名或密码不正确！");
                $.showInfoDlg("提示","用户名或密码不正确！",2);
            }else{
                $("#loginName")[0].innerText = name;
                $("#login").modal("hide");
            }
        }, function(data) {

        });
        ajax.set("name",name);
        ajax.set("password",password);
        ajax.start();
    });
}
function cancleLogin(){
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        if (confirm("确定要注销吗？")) {
            location.href = Hussar.ctxPath+"/index/logout";
        }
    });
}
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showDoc(fileType,id) {
    fileType="."+fileType;
    var selectVal = $("#select").val();
    var keyWords = $("#headerSearchInputValue").val();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
                openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
            }else if(data.code==2){
                openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
            }else if(data.code==3){
                openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
            }else if(data.code==4){
                openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
            }else if(data.code==10){
                var url=Hussar.ctxPath+"/toShowComponent/toShowPDF?id="+id;
                openWin(encodeURI(url))
            }else{
                openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
            }
        }, function(data) {

        });
        ajax.set("suffix",fileType);
        ajax.start();
    });
}
function showDocBlank(fileType,id) {
    fileType="."+fileType;
    var selectVal = $("#select").val();
    var keyWords = $("#headerSearchInputValue").val();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
                openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
            }else if(data.code==2){
                openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
            }else if(data.code==3){
                openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
            }else if(data.code==4){
                openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
            }else if(data.code==10){
                var url=Hussar.ctxPath+"/toShowComponent/toShowPDF?id="+id;
                openWin(encodeURI(url))
            }else{
                openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
            }
        }, function(data) {

        });
        ajax.set("suffix",fileType);
        ajax.start();
    });
}
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function(){
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;

    $(document).ready(function(){
        var docId = $("#docId").val();

        var ajax = new $ax(Hussar.ctxPath + "/fsFile/cacheViewNum", function(data) {

        }, function(data) {

        });
        ajax.set("docId",docId);
        ajax.start();

    });
});
/*打开分享链接*/
function openShare(title, url, w, h,docId,fileSuffixName,fileName) {
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
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layer.open({
            type: 2,
             area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,

            title: title,
            content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURI(fileName)
        });
    });
}
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}
function initFileMsg(id,category) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;


        var ajax = new $ax(Hussar.ctxPath + "/fsFile/cacheViewNum", function(data) {

        }, function(data) {

        });
        ajax.set("docId",id);
        ajax.start();
        history.replaceState("","title","?id=" + id + "&fileType=" + $("#fileTypeValue").val() + "&keyWords=" + $("#headerSearchInputValue").val());

        var ajax = new $ax(Hussar.ctxPath + "/preview/fileDetail", function(data) {
            // document.title = data.title+"-"+projectTitle;
            document.title = data.title;
            authorId = data.authorId;
            filePath = data.filePath;
            var fileName = data.title;
            collection =data.collection;
            var shareFlag = data.shareFlag;
            var fileSuffixName = data.fileSuffixName.toLowerCase();
            docAbstract = data.docAbstract;
            filePdfPath = data.filePdfPath;
            var docId = data.id;
            var folderId = $("#folderId").val();
            var authority=data.authority;
            $("#docId").text(data.id);
            $("#title").text(data.title);
            $(".pathes span:last-child").html(data.title);
            var obj = document.getElementById("title");
            if(fileSuffixName=="xlsx"||fileSuffixName=="xls"){  //文档名称前的图片
                $("#title").addClass("type-xls");
                // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
            }else if(fileSuffixName=="pdf"){
                $("#title").addClass("type-pdf");
                // obj.style.cssText = "background:url(/static/resources/img/pdf.png)no-repeat left center;"
            }else if(fileSuffixName=="ppt"||fileSuffixName=="pptx"||fileSuffixName=="ppsx"){
                $("#title").addClass("type-ppt");
                // obj.style.cssText = "background:url(/static/resources/img/ppt.png)no-repeat left center;"
            }else if(fileSuffixName=="ceb"){
                $("#title").addClass("type-ceb");
                // obj.style.cssText = "background:url(/static/resources/img/ppt.png)no-repeat left center;"
            }else if(fileSuffixName=="txt") {
                $("#title").addClass("type-txt");
                // obj.style.cssText = "background:url(/static/resources/img/txt.png)no-repeat left center;";
            }else if(fileSuffixName=="doc"||fileSuffixName=="docx") {
                $("#title").addClass("type-doc");
                // obj.style.cssText = "background:url(/static/resources/img/word.png)no-repeat left center;";
            }else if(fileSuffixName=="png"||fileSuffixName=="jpeg"||fileSuffixName=="gif"||fileSuffixName=="jpg") {
                $("#title").addClass("type-pic");
                // obj.style.cssText = "background:url(/static/resources/img/word.png)no-repeat left center;";
            }else if(fileSuffixName=="bmp") {
                $("#title").addClass("type-bmp");
            }else {
                $("#title").addClass("type-other");
                // obj.style.cssText = "background:url(/static/resources/img/other.png)no-repeat left center;";
            }
            var author=data.author;
            if(author==""||author==undefined){
                author="文库管理员"
            }
            $("#owner").html(""+author+"<em>|</em>");
            $("#uploadTime").html(""+data.createTime.slice(0,10) +"<em>|</em>");
            $("#fileSize").html(""+data.fileSize +"<em>|</em>")
            $("#downloadNum").html(""+data.downloadNum +"次下载"+"<em>|</em>");
            $("#previewNum").html(""+data.readNum+"次预览" );
            if(authority=='1'||data.adminFlag==1||authority=='2'){
                $("#dowLoadButton").show();
            } else {
                $("#dowLoadButton").hide();
            }
            // if (shareFlag == '1'){
            //     $("#shareBtn").show();
            //     $("#shareBtn").css('display','inline-block');
            // } else {
            //     $("#shareBtn").hide();
            // }
            $("#collectionToFolder").hide();
            $("#cancelCollection").hide();
            // if(data.collection=='0'){
            //     $("#collectionToFolder").show();
            //     $("#cancelCollection").hide();
            // }else{
            //     $("#collectionToFolder").hide();
            //     $("#cancelCollection").show();
            // }
            $("#tags").val(data.tags);
            /**
             * 初始化图片标签
             */
            if($("#tags").val() != ""){
                var tags = $("#tags").val().split(",");
                layui.use(['jquery','laytpl'], function() {
                    var $ = layui.jquery;
                    var laytpl = layui.laytpl;
                    var getTpl = $("#imgTags").html();
                    laytpl(getTpl).render(tags, function(html){
                        $("#imgTags").siblings().remove();
                        $("#imgTags").after(html);
                    });
                });
                $("#showImgTags").removeClass("hide");
            } else {
                $("#showImgTags").addClass("hide");
            }
            var ajax = new $ax(Hussar.ctxPath + "/preview/recommendIMG", function(data) {
                var items = data;
                var toLoad = "";
                if (items.length != 0) {
                    $("#recommendIMG").removeClass("hide");
                    for (var i = 0; i < 4 && i < items.length; i++) {//同上
                        toLoad += '<li class="photo-item">';
                        toLoad += '<a href="javascript:void(0)" onclick=\'showDocBlank("' + items[i].docType + '","' + items[i].docId + '");return false;\' target="_blank">';
                        toLoad += '<img src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[i].filePdfPath + '&&isThumbnails=0" alt="' + items[i].title + '">';
                        toLoad += '<div class="img-info" title="' + items[i].title + '">' + items[i].title + '</div>';
                        toLoad += '</a>';
                        toLoad += '</li>';
                    }
                    $("#showRecommendIMG").html("");
                    $("#showRecommendIMG").append(toLoad);
                    var grid= $('#showRecommendIMG').masonry({
                        // options
                        itemSelector: '.photo-item'
                    });
                    grid.masonry('destroy')
                    grid.imagesLoaded( function() {
                        grid.masonry();
                    });
                }
            }, function(data) {

            });
            ajax.set("keyword",fileName);
            ajax.set("imgId",docId);
            ajax.set("folderId",folderId);
            ajax.set("fileType","8");
            ajax.set("page","1");
            ajax.set("size","50");
            ajax.set("tagString",$("#tags").val());
            ajax.start();
        }, function(data) {
            $.showInfoDlg("提示","预览失败",2);
        });
        ajax.set("id",id);
        ajax.set("category",category);
        ajax.start();
    });

}
function initFolderImg() {
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        /**
         * 加载当前项目中所包含的其他图片
         */
        var items = totalItem;
        var toLoad = "";
        if (items.length != 0) {
            $("#projectIMG").removeClass("hide");
            var selfIndex = 0;
            if (currentIndex != -1) {
                selfIndex = currentIndex;
            } else {
                for (var i = 0; i < items.length; i++) {//建议只显示4个，增加更多链接
                    if(items[i].isSelf){
                        selfIndex = i;
                    }
                }
            }
            if (items.length - selfIndex < 4){
                selfIndex = items.length - 4;
            }
            if (selfIndex < 0) {
                selfIndex = 0;
            }
            for (var j = selfIndex; j < items.length && j < selfIndex + 4; j++) {//建议只显示4个，增加更多链接

                toLoad += '<li class="photo-item">';
                toLoad += '<a href="javascript:void(0)" onclick=\'showDoc("' + items[j].docType + '","' + items[j].docId + '");return false;\' target="_blank">';
                toLoad += '<img src="'+Hussar.ctxPath+'/preview/list?fileId=' + items[j].filePdfPath + '&&isThumbnails=0" alt="' + items[j].title + '">';
                toLoad += '<div class="img-info" title="'+ items[j].title + '">' + items[j].title + '</div>';
                toLoad += '</a>';
                toLoad += '</li>';
            }
            $("#showFolderIMG").html("");
            $("#showFolderIMG").append(toLoad);

            var grid= $('#showFolderIMG').masonry({
                // options
                itemSelector: '.photo-item'
            });
            grid.masonry('destroy')
            grid.imagesLoaded( function() {
                grid.masonry();
            });
        }
    });
}

function showCancelCollection() {
    $("#cancelCollection").show();
    $("#collectionToFolder").hide();
}

function hideCancelCollection() {
    $("#cancelCollection").hide();
    $("#collectionToFolder").show();
}
function showUpLevel() {
    $("#upLevel").html("<i class='layui-icon' style='cursor:pointer;'>&#xe65c;</i>");
}
function hideUpLevel() {
    $("#upLevel").html('收藏');
}
function upLevel(){
    var contentWindow = $("#layui-layer-iframe" + layerOpen1)[0].contentWindow;
    contentWindow.upLeveChild();
}

//判断图片是否损坏
function CheckImgExists(imgurl){
    var ImgObj = new Image(); //判断图片是否存在
    ImgObj.src = imgurl;
    //没有图片，则返回-1
    if(ImgObj.fileSize > 0 || (ImgObj.width > 0 && ImgObj.height > 0)) {
        return true;
    } else {
        return false;
    }
}
