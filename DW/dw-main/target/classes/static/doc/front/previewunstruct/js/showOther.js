/**
 * Created by Lenovo on 2018/1/20.
 */
var scrollHeightAlert=0;
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var layerOpen1;
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
        document.oncontextmenu = function () {
            return false;
        }
        document.onkeydown = function () {
            if (window.event && window.event.keyCode == 123) {
                event.keyCode = 0;
                event.returnValue = false;
                return false;
            }
        };
        var docId;//文档ID
        var filePath;//文档路径
        var authorId;//作者ID
        var fileName;//文档名称
        var fileSuffixName;//文档后缀
        var filePdfPath;//文档预览路径
        var docAbstract;//文档描述
        var authority;//文档权限
        var collection;//是否收藏
        var shareFlag;//是否可分享
        var uploadState;//上传状态
        var docType;
        layui.use(['Hussar'], function(){
            var Hussar = layui.Hussar;

            layui.config({
                base: Hussar.ctxPath+'/static/resources/weadmin/static/js/'
                , version: '101100'
            }).use('admin');
        });
        layui.use(['jquery', 'admin', 'element'], function () {
            var $ = layui.$, element = layui.element, admin = layui.admin;
        })
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
        var gridView ={
            /*初始化页面*/
            initPage: function() {
                var that = this;
                //初始化表格
                that.init();
                that.initButtonEvent();
                that.initBj();
            },
            initPath:function(){
                var that = this;
                var id= $("#docId").val();
                var fileType

                var ajax = new $ax(Hussar.ctxPath + "/preview/getFoldPath", function(data) {
                    var path ="<span><a href=\""+Hussar.ctxPath+"\/\" target=\"_blank\" data-id=\"01\">首页</a> <i class=\"layui-icon\"></i> </span>";
                    if(!!data){
                        for(var i=0;i<data.length;i++){

                            path +=" <span><a href='#'target='_blank' data-id='"+data[i].foldId+"'>"+data[i].foldName+"</a> <i class='layui-icon'>&#xe602;</i> </span>";

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
                            if(index ==-1){$(this).attr("href",Hussar.ctxPath+"/")
                            }else if(index === 0){
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
                var ajax = new $ax(Hussar.ctxPath + "/preview/fileDetail", function(data) {
                    // document.title = data.title+"-"+projectTitle;
                    document.title = data.title;
                    authorId = data.authorId;
                    filePath = data.filePath;
                    fileName = data.title;
                    // gridView.initPath();
                    fileSuffixName = data.fileSuffixName.toLowerCase();
                    docType = "." + fileSuffixName;
                    docAbstract = data.docAbstract;
                    filePdfPath = data.filePdfPath;
                    collection =data.collection;
                    docId = data.id;
                    authority=data.authority;
                    shareFlag = data.shareFlag;
                    uploadState = data.uploadState;
                    $("#docId").text(data.id);
                    $("#title").text(data.title);

                    var obj = document.getElementById("title");
                    if(fileSuffixName=="xlsx"||fileSuffixName=="xls"){  //文档名称前的图片
                        $("#title").addClass("type-xls");
                        // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
                    }else if(['mp4','avi', 'wma', 'rmvb','rm', 'flash'].indexOf(fileSuffixName)!=-1){
                        $("#title").addClass("type-mp4");
                        // obj.style.cssText = "background:url(/static/resources/img/ppt.png)no-repeat left center;"
                    }else if( ['CD','OGG','mp3','real','cd','ogg','asf','wav','ape','module','midi'].indexOf(fileSuffixName)!=-1) {
                        $("#title").addClass("type-mp3");
                        // obj.style.cssText = "background:url(/static/resources/img/txt.png)no-repeat left center;";
                    }else if(['png','jpeg','gif','jpg'].indexOf(fileSuffixName)!=-1) {
                        $("#title").addClass("type-pic");
                        // obj.style.cssText = "background:url(/static/resources/img/txt.png)no-repeat left center;";
                    }else if(fileSuffixName=="html"){
                        $("#title").addClass("type-html");
                        // obj.style.cssText = "background:url(/static/resources/img/ppt.png)no-repeat left center;"
                    }else if(fileSuffixName=="bmp") {
                        $("#title").addClass("type-bmp");
                        // obj.style.cssText = "background:url(/static/resources/img/txt.png)no-repeat left center;";
                    }else if(fileSuffixName=="exe"){
                        $("#title").addClass("type-exe");
                        // obj.style.cssText = "background:url(/static/resources/img/ppt.png)no-repeat left center;"
                    }else if(fileSuffixName=="psd") {
                        $("#title").addClass("type-psd");
                        // obj.style.cssText = "background:url(/static/resources/img/txt.png)no-repeat left center;";
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
                    } else if(fileSuffixName=="zip"||fileSuffixName=="rar") {
                        $("#title").addClass("type-zip");
                        // obj.style.cssText = "background:url(/static/resources/img/word.png)no-repeat left center;";
                    }else {
                        $("#title").addClass("type-other");
                        // obj.style.cssText = "background:url(/static/resources/img/other.png)no-repeat left center;";
                    }
                    var author=data.author;
                    if(author==""||author==undefined){
                        author=data.userId;
                    }
                    // $("div.pdf-content").hide();
                    // $("#owner").html(" "+author+"<em>|</em>");
                    // $("#uploadTime").html(""+data.createTime.substr(0,10) +"<em>|</em>");
                    // $("#fileSize").html(""+data.fileSize +"<em>|</em>");
                    // $("#downloadNum").html(""+data.downloadNum +"次下载"+"<em>|</em>");
                    // $("#previewNum").html(""+data.readNum+"次预览" );
                    // $("#docAbstract").text(docAbstract);
                    $("#owner").html("上传者: "+author+"<em>|</em>");
                    $("#uploadTime").html("上传时间: "+data.createTime.slice(0,10) +"<em>|</em>");
                    $("#fileSize").html("文件大小: "+data.fileSize +"<em>|</em>");
                    $("#downloadNum").html("下载次数: "+"<span>"+data.downloadNum +"</span>"+"<em>|</em>");
                    $("#previewNum").html("预览次数: "+"<span>"+data.readNum+"</span>" );
                    if(authority=='1'||data.adminFlag==1||authority=='2'){
                        $("#dowLoadButton").show();
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
                    // if (shareFlag == '1'){
                    //     $("#shareBtn").show();
                    //     $("#shareBtn").css('display','inline-block');
                    // }
                }, function(data) {
                    $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
                });
                ajax.set("id",id);
                ajax.start();

                /**
                 * 加载与当前文档名称相关的推荐文档
                 */
                /*$.ajax({
                    async: false,
                    type: "post",
                    url: Hussar.ctxPath+"/preview/recommendArticle",
                    dataType: 'json',
                    data: {currentId: id,keyword: fileName, fileType: "8", pageNumber:"1", pageSize: "20"},
                    success: function (data) {
                        var items = data;
                        var toLoad = "";
                        if (items.length != 0) {
                            for (var i = 0; i < 5 && i < items.length; i++) {//同上
                                var imgsrc='';
                                if(items[i].docType === '.folder'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-folder15.png";
                                }else if(items[i].docType === '.doc'||items[i].docType === '.docx'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-word15.png";
                                }else if(items[i].docType === '.txt'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-text15.png";
                                }else if(items[i].docType === '.ppt'||items[i].docType === '.pptx'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ppt15.png";
                                }else if(items[i].docType === '.pdf'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-pdf15.png";
                                }else if(items[i].docType === '.ceb'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ceb15.png";
                                }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(items[i].docType)!=-1){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-audio15.png";
                                }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(items[i].docType)!=-1){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-video15.png";
                                }else if(items[i].fileType === '.xls'||items[i].docType === '.xlsx') {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-excel15.png";
                                }else if(['.png','.jpeg','.gif','.jpg'].indexOf(items[i].docType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-img15.png";
                                }else if(['.bmp'].indexOf(items[i].docType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-bmp15.png";
                                }else if(['.psd'].indexOf(items[i].docType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-psd15.png";
                                }else if(['.html'].indexOf(items[i].docType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
                                }else if(['.exe'].indexOf(items[i].docType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-exe15.png";
                                }else if(['.zip','.rar'].indexOf(items[i].docType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-zip15.png";
                                }else {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-other15.png";
                                }
                                var author=items[i].authorName;
                                if( author==undefined||author==""){
                                    author=items[i].authorId;
                                }
                                toLoad += '<li class="message">';
                                toLoad += '<img class="article-type" src="'+imgsrc+'">';
                                toLoad += '<a href="javascript:void(0)" onclick=\'showDocBlank("' + items[i].docType + '","' + items[i].docId + '");return false;\' target="_blank" title="'+items[i].title+'">';
                                toLoad += items[i].title;
                                toLoad += '</a>';
                                toLoad += '<div class="clearfix">';
                                toLoad += '<div class="article-msg">'+ author+'</div>';
                                toLoad += '<div class="article-msg-date">'+ items[i].createTime+'</div>';
                                toLoad += '</div>';
                                toLoad += '</li>';
                            }
                            $("#showRecommendArticle").html("");
                            $("#showRecommendArticle").append(toLoad);
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/preview/recommendArticle", function(data) {
                    var items = data;
                    var toLoad = "";
                    if (items.length != 0) {
                        for (var i = 0; i < 5 && i < items.length; i++) {//同上
                            var imgsrc='';
                            if(items[i].docType === '.folder'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-folder15.png";
                            }else if(items[i].docType === '.doc'||items[i].docType === '.docx'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-word15.png";
                            }else if(items[i].docType === '.txt'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-text15.png";
                            }else if(items[i].docType === '.ppt'||items[i].docType === '.pptx'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ppt15.png";
                            }else if(items[i].docType === '.pdf'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-pdf15.png";
                            }else if(items[i].docType === '.ceb'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ceb15.png";
                            }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(items[i].docType)!=-1){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-audio15.png";
                            }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(items[i].docType)!=-1){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-video15.png";
                            }else if(items[i].fileType === '.xls'||items[i].docType === '.xlsx') {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-excel15.png";
                            }else if(['.png','.jpeg','.gif','.jpg'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-img15.png";
                            }else if(['.bmp'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-bmp15.png";
                            }else if(['.psd'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-psd15.png";
                            }else if(['.html'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
                            }else if(['.exe'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-exe15.png";
                            }else if(['.zip','.rar'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-zip15.png";
                            }else {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-other15.png";
                            }
                            var author=items[i].authorName;
                            if( author==undefined||author==""){
                                author=items[i].authorId;
                            }
                            toLoad += '<li class="message">';
                            toLoad += '<img class="article-type" src="'+imgsrc+'">';
                            toLoad += '<a href="javascript:void(0)" onclick=\'showDocBlank("' + items[i].docType + '","' + items[i].docId + '");return false;\' target="_blank" title="'+items[i].title+'">';
                            toLoad += items[i].title;
                            toLoad += '</a>';
                            toLoad += '<div class="clearfix">';
                            toLoad += '<div class="article-msg">'+ author+'</div>';
                            toLoad += '<div class="article-msg-date">'+ items[i].createTime+'</div>';
                            toLoad += '</div>';
                            toLoad += '</li>';
                        }
                        $("#showRecommendArticle").html("");
                        $("#showRecommendArticle").append(toLoad);
                    }
                }, function(data) {

                });
                ajax.set("currentId",id);
                ajax.set("keyword",fileName);
                ajax.set("fileType","8");
                ajax.set("pageNumber","1");
                ajax.set("pageSize","20");
                ajax.setAsync(true);
                ajax.start();

                /**
                 * 加载猜你喜欢的文档
                 */
                var ajax = new $ax(Hussar.ctxPath + "/preview/guessYouLike", function(data) {
                    var items = data;
                    var toLoad = "";
                    if (items instanceof Array && items.length != 0) {
                        $("#guess").removeClass("hide");
                        for (var i = 0; i < 3 && i < items.length; i++) {//同上
                            var imgsrc='';
                            if(items[i].docType === '.folder'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-folder15.png";
                            }else if(items[i].docType === '.doc'||items[i].docType === '.docx'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-word15.png";
                            }else if(items[i].docType === '.txt'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-text15.png";
                            }else if(items[i].docType === '.ppt'||items[i].docType === '.pptx'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ppt15.png";
                            }else if(items[i].docType === '.pdf'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-pdf15.png";
                            }else if(items[i].docType === '.ceb'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ceb15.png";
                            }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(items[i].docType)!=-1){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-audio15.png";
                            }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(items[i].docType)!=-1){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-video15.png";
                            }else if(items[i].docType === '.xls'||items[i].docType === '.xlsx') {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-excel15.png";
                            }else if(['.png','.jpeg','.gif','.jpg'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-img15.png";
                            }else if(['.bmp'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-bmp15.png";
                            }else if(['.psd'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-psd15.png";
                            }else if(['.html'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
                            }else if(['.exe'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-exe15.png";
                            }else if(['.zip','.rar'].indexOf(items[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-zip15.png";
                            }else {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-other15.png";
                            }
                            var author=items[i].authorName;
                            if( author==undefined||author==""){
                                author=items[i].authorId;
                            }
                            toLoad += '<li class="message">';
                            toLoad += '<img class="article-type" src="'+imgsrc+'">';
                            toLoad += '<a href="javascript:void(0)" onclick=\'showDocBlank("' + items[i].docType + '","' + items[i].docId + '");return false;\' target="_blank" title="'+items[i].title+'">';
                            toLoad += items[i].title;
                            toLoad += '</a>';
                            toLoad += '<div class="clearfix">';
                            toLoad += '<div class="article-msg">'+ author+'</div>';
                            toLoad += '<div class="article-msg-date">'+ items[i].createTime+'</div>';
                            toLoad += '</div>';
                            toLoad += '</li>';
                        }
                        $("#guessYouLike").html("");
                        $("#guessYouLike").append(toLoad);
                    }
                }, function(data) {

                });
                ajax.set("currentId",id);
                ajax.start();

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
                $('#loginButton').click(function () {
                    loginSubmit();
                });
                //var srcLink = "/files/fileDownNew?docName="+fileName+"&docIds="+docId;
                //var srcLink = "/files/fileDownNew?docName=" + "" + "&docIds=" + docId;
                var collectionLink ="/personalCollection/addCollection";
                var  cancelCollection="/personalCollection/cancelCollection";
                //$("#dowLoadButton").attr('href',srcLink);
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
                        shadeClose: false,
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
                        window.location.href = Hussar.ctxPath+"/searchView?keyWords=" + fileName + "&fileType=" + fileType;
                    }else{
                        //alert("请输入关键词，多个关键词以空格隔开");
                        $.showInfoDlg("提示","请输入关键词，多个关键词以空格隔开",2);
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
            },
            // 布局
            initBj:function () {
                var pdf_height = $("#top .viewContent").innerHeight() - $(".viewContent .viewTitle").outerHeight(true) - $(".viewContent .docView").outerHeight(true) - $(".viewContent .doc-desc").outerHeight(true);
                $(".pdf-content").css("height",pdf_height);
                setTimeout(function () {
                    $("div.pdf-content").show();
                },200);
            },

        }

        $(document).ready(function() {
            gridView.initPage();
        });
    })(this);
});
function cancleLogin(){
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layer.confirm('确认注销？', {icon: 3, title: '提示'}, function (index) {
            window.location.href = Hussar.ctxPath+"/logout.do";
            layer.close(index);
        });
    });
}
$(document).ready(function(){
    var docId = $("#docId").val();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;


        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/fsFile/cacheViewNum",
            data:{
                docId:docId,
            },

        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/cacheViewNum", function(data) {

        }, function(data) {

        });
        ajax.set("docId",docId);
        ajax.start();
    });


});
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showDocBlank(fileType,id) {
    var selectVal = $("#select").val();
    var keyWords = $("#headerSearchInputValue").val();
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        if(fileType==".png"||fileType==".jpg"||fileType==".gif"||fileType==".bmp"||fileType==".jpeg"){
            openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
        }else if(fileType==".mp4"||fileType==".wmv"){
            openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
        } else if(fileType==".mp3"||fileType==".m4a"){
            openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
        }else if(fileType == '.docx'||fileType == '.doc'||fileType == '.dot'||fileType == '.xls'
            ||fileType == '.wps'||fileType == '.xlt'||fileType == '.et'
            ||fileType == '.ett'||fileType == '.ppts'||fileType == '.pot'
            ||fileType == '.dps'||fileType == '.dpt'
            || fileType == '.xlsx'||fileType == '.txt'||fileType == '.pdf'
            ||fileType == '.ceb' ||fileType == '.ppt'|| fileType == '.pptx'){
            openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
        }else {
            openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
        }
    });
}

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

