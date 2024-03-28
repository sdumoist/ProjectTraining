/**
 * Created by Lenovo on 2018/1/20.
 */
var docType="";
var scrollHeightAlert=0;
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var layerOpen1;
var object;
var obj;
var obj_scroll;
var isFullScreen = false;
var isOpen = false;
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
    };
    document.onkeydown = function () {
        if (window.event && window.event.keyCode == 123) {
            event.keyCode = 0;
            event.returnValue = false;
            return false;
        }
    };
    //苹果电脑禁止缩放
    window.onload=function () {
        document.addEventListener('touchstart',function (event) {
            if(event.touches.length>1){
                event.preventDefault();
            }
        });
        var lastTouchEnd=0;
        document.addEventListener('touchend',function (event) {
            var now=(new Date()).getTime();
            if(now-lastTouchEnd<=300){
                event.preventDefault();
            }
            lastTouchEnd=now;
        },false);
        document.addEventListener('gesturestart', function (event) {
            event.preventDefault();
        });
    }
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
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layui.config({
            base: Hussar.ctxPath+'/static/resources/weadmin/static/js/'
            , version: '101100'
        }).use('admin');
    });
    layui.use(['jquery', 'element'], function () {
        var $ = layui.$, element = layui.element;
    });

    $(document).ready(function() {
        gridView.initPage();
        // setTimeout(iframeScroll,1500)
    });


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
            var fileType;
            var parentDirectory = '';

            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

                var ajax = new $ax(Hussar.ctxPath + "/preview/getFoldPath", function(data) {
                    var path ="<span><a href=\""+Hussar.ctxPath+"\/\" target=\"_blank\">首页</a> <i class=\"layui-icon\">&#xe602;</i> </span>";
                    if(!!data){
                        for(var i=0;i<data.length;i++){
                            path +=" <span><a href='#' target='_blank' data-id='"+data[i].foldId+"'>"+data[i].foldName+"</a> <i class='layui-icon'>&#xe602;</i> </span>";
                        }
                        parentDirectory = data[data.length - 1].foldName;
                    }else{
                        path ="";
                    }
                    path +=" <span>"+fileName+"</span>";
                    var pathes = $(".pathes");

                    pathes.html(path);
                    $('.parent-directory').html(parentDirectory);
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
                            if(index === -1){
                                $(this).attr("href",Hussar.ctxPath+"/")
                            }else if(index ===0){
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
            });
        },
        init:function(){
            var that = this;
            var id= $("#docId").val();
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;


                var ajax = new $ax(Hussar.ctxPath + "/preview/fileDetail", function(data) {
                    document.title = data.title +'-'+ projectTitle;
                    authorId = data.authorId;
                    filePath = data.filePath;
                    fileName = data.title;
                    gridView.initPath();
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
                    }else {
                        $("#title").addClass("type-other");
                        // obj.style.cssText = "background:url(/static/resources/img/other.png)no-repeat left center;";
                    }
                    var author=data.author;
                    if(author==""||author==undefined){
                        author=data.userId;
                    }
                    // $("#owner").html(""+author+"<em>|</em>");
                    // $("#uploadTime").html(" "+data.createTime.slice(0,10) +"<em>|</em>");
                    // $("#fileSize").html(""+data.fileSize +"<em>|</em>");
                    // $("#downloadNum").html(""+data.downloadNum +"次下载"+"<em>|</em>");
                    // $("#previewNum").html(""+data.readNum +"次预览");
                    $("#owner").html("作者: "+author+"<em>|</em>");
                    $("#uploadTime").html("上传日期: "+data.createTime.slice(0,10) +"<em>|</em>");
                    $("#fileSize").html("文件大小: "+data.fileSize +"<em>|</em>");
                    $("#downloadNum").html("下载次数: "+"<span>"+data.downloadNum +"</span>"+"<em>|</em>");
                    $("#previewNum").html("预览次数: "+"<span>"+data.readNum + "</span>" );
                    $("#docAbstract").text(docAbstract);
                    if(authority=='1'||data.adminFlag==1||authority=='2'){
                        $("#dowLoadButton").show();
                    }
                    if(collection=='0'){
                        $("#collectionToFolder").show();
                        $("#cancelCollection").hide();
                    }else{
                        $("#collectionToFolder").hide();
                        $("#cancelCollection").show();
                    }
                    if (shareFlag == '1'){
                        $("#shareBtn").show();
                        $("#shareBtn").css('display','inline-block');
                    }

                    var username = $("#username").val();
                    var watermark_company_flag = $("#watermark_company_flag").val();
                    var companyValue = $("#companyValue").val();
                    var watermark_user_flag = $("#watermark_user_flag").val();
                    //相关推荐
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
                                }else if(items[i].docType === '.xls'||items[i].docType === '.xlsx' || items[i].docType === '.csv' || items[i].docType === '.et') {
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
                    if (uploadState && (fileSuffixName == "xlsx" || fileSuffixName == "xls" || fileSuffixName == "csv" || fileSuffixName == "et" )) {
                        var mask = document.getElementById("lucky-mask-demo");
                        mask.style.display = "flex";
                        var url = Hussar.ctxPath + "/preview/excel/" + id;
                        LuckyExcel.transformExcelToLuckyByUrl(url, name, function (exportJson, luckysheetfile) {
                            if (exportJson.sheets == null || exportJson.sheets.length == 0) {
                                alert("文件读取失败!");
                                return;
                            }
                            mask.style.display = "none";
                            window.luckysheet.destroy();

                            window.luckysheet.create({
                                container: 'luckysheet', //luckysheet is the container id
                                showinfobar: false, // 信息栏
                                showtoolbar: false, // 工具栏
                                showstatisticBar: false, // 底部计数栏
                                showstatisticBarConfig: {
                                    zoom: true,
                                },
                                sheetRightClickConfig: {
                                    delete: false, // 删除
                                    copy: false, // 复制
                                    rename: false, //重命名
                                    color: false, //更改颜色
                                    hide: false, //隐藏，取消隐藏
                                    move: false, //向左移，向右移
                                },
                                sheetFormulaBar: false, // 公式栏
                                enableAddRow: false,
                                enableAddBackTop: false,
                                showsheetbarConfig:{
                                    add: false,
                                    menu: true,
                                    sheet: true
                                },
                                lang: 'zh',
                                allowEdit: false,
                                showConfigWindowResize: true,
                                data: exportJson.sheets,
                                title: exportJson.info.name,
                                userInfo: exportJson.info.name.creator
                            });
                        });

                        $("#excelContent").removeClass("hide");
                        if (['.ppt', '.pptx', '.ppsx'].indexOf(docType) === -1) {
                            $(".div-top").show();
                            $('#openPage').show();
                        }
                        $(".open").show();
                    }else if (uploadState){
                        document.getElementById("pdfContainer").src = Hussar.ctxPath+"/static/resources/pdf/web/viewer.html?file="
                            + encodeURIComponent(Hussar.ctxPath+"/preview/list?fileId=" +encodeURIComponent(filePdfPath)+"&isView=0&range_size="+PDFJS.jqwk_rang_size)+"&username=" + encodeURIComponent(username)
                            + "&watermark_user_flag=" + encodeURIComponent(watermark_user_flag) + "&watermark_company_flag=" + encodeURIComponent(watermark_company_flag)
                            + "&companyValue= " + encodeURIComponent(companyValue);
                        $("#pdfContainer").removeClass("hide");
                        $(".div-top").hide();
                        if(['.ppt', '.pptx','.ppsx'].indexOf(docType) === -1){
                            $('#openPage').show();
                        }
                        $(".open").show();

                    } else {
                        var transFlag = $("#transFlag").val();
                        if (transFlag == "1") {
                            // 显示手动转换按钮
                            $("#manualConversion").show();
                            $("#openPage").hide();
                        } else {
                            //文档预览加上“加载中”遮罩
                            $("#loadingBox").removeClass("hide");
                            //循环获取状态，判断是否上传成功
                            var checkUploadState = setInterval(function (){

                                var ajax = new $ax(Hussar.ctxPath + '/preview/checkUploadState', function(data) {
                                    if (data){
                                        //关闭定时器
                                        clearInterval(checkUploadState);
                                        //重新加载
                                        gridView.init();
                                        //清除遮罩
                                        $("#pdfContainer").removeClass("hide");
                                        $("#loadingBox").addClass("hide");
                                    }
                                }, function(data) {

                                });
                                ajax.set("docId",id);
                                ajax.start();
                            },2000);
                        }
                    }
                }, function(data) {
                    $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
                });
                ajax.set("id",id);
                ajax.start();
            });

            $("#changePdf").click(function () {
                $("#transLoading").show();
                $("#manualConversion").hide();
                layui.use(['Hussar','HussarAjax'], function() {
                    var Hussar = layui.Hussar,
                        $ax = layui.HussarAjax;
                    var id = $("#docId").val();
                    var ajax = new $ax(Hussar.ctxPath + '/preview/handTransFile', function (data) {
                        location.reload();
                        $("#transLoading").hide();
                    }, function (data) {
                        location.reload();
                        $("#transLoading").hide();
                    });
                    ajax.set("docId", id);
                    ajax.setAsync(true);
                    ajax.start();
                })
            })
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

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
            });
        },
        initButtonEvent:function (){
            var that = this;
            $('#loginButton').click(function () {
                loginSubmit();
            });

            //鼠标在收藏夹弹出框上时阻止滚动
            var preventDefault = function (event) {
                event.preventDefault();
            };
            $(document).on('mouseenter',".collectionsContainer",function (e) {
                $(document).on('scroll',preventDefault)
            });
            $(document).on('mouseleave',".collectionsContainer",function (e) {
                $(document).off('scroll',preventDefault)
            });
            //var srcLink = "/files/fileDownNew?docName="+fileName+"&docIds="+docId;
            //var srcLink = "/files/fileDownNew?docName=" + "" + "&docIds=" + docId;
            var collectionLink ="/personalCollection/addCollection";
            var  cancelCollection="/personalCollection/cancelCollection";
            //$("#dowLoadButton").attr('href',srcLink);
            $("#collection").click(function () {
                layui.use(['Hussar','HussarAjax'], function(){
                    var Hussar = layui.Hussar,
                        $ax = layui.HussarAjax;

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
            });
            $("#cancelCollection").click(function () {
                layui.use(['HussarAjax','Hussar'], function(){
                    var Hussar = layui.Hussar,
                        $ax = layui.HussarAjax;

                    layer.confirm("确定取消收藏吗？", {
                        title: ['取消收藏', 'background-color:#fff'],
                        skin:'move-confirm'
                    }, function (index) {
                        var ajax = new $ax(Hussar.ctxPath + cancelCollection, function(data) {
                            if (data.success == "0") {
                                $("#collectionToFolder").show();
                                $("#cancelCollection").hide();
                                layer.close(index);
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
                layui.use(['Hussar','HussarAjax'], function(){
                    var Hussar = layui.Hussar,
                        $ax = layui.HussarAjax;
                    var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {
                        if (data.status == "1") {
                            var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                icon: 3,
                                title: '提示',
                                skin:'download-info'
                            }, function (index) {
                                layer.close(index2);
                                var valid = true;
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
                    moveOut: true,
                    shade: [0.5, 'rgb(0,0,0)'],
                    title: "<span id='upLevel' onclick='upLevel()'>收藏</span>",
                    content: "/personalCollection/collectionToFolderView?docId="+docId
                });
                $("#collections").parent().addClass("collectionsContainer")
            })
        },
        // 布局
        initBj:function () {
            var pdf_height = $("#top .viewContent").height() - $(".viewContent .viewTitle").outerHeight(true) - $(".viewContent .docView").outerHeight(true);
             $(".pdf-content").css("height",pdf_height);
        },

    }
})(this);
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

        var ajax = new $ax(Hussar.ctxPath + "/fsFile/cacheViewNum", function(data) {

        }, function(data) {

        });
        ajax.set("docId",docId);
        ajax.start();

    });
    $(".open").click(function () {
        $(".layui-col-sm2_5").hide(1000);
        isOpen = true;
        if(isOpen){
            $(".btn-div").addClass('btn-bottom-div1');
        }
        $(".layui-col-sm9_5").addClass("pdf-full-screen");
        $(".viewContent").addClass("none-background");
        $(".open").hide();
        $(".shrink").show();
        setTimeout(()=>{
            window.luckysheet.resize();
        },800)
    })
    $(".shrink").click(function () {
        $(".layui-col-sm9_5").removeClass("pdf-full-screen");
        $(".layui-col-sm2_5").show(1000);
        isOpen = false;
        if(!isOpen){
            $(".btn-div").removeClass('btn-bottom-div1');
        }
        $(".viewContent").removeClass("none-background");
        $("#shrink").removeClass("shrink");
        $(".open").show();
        $(".shrink").hide();
    })
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

function getIframe(id){
    return document.getElementById(id).contentWindow.document;
}
function launchFullscreen(element) {
    if (element.requestFullscreen) {
        element.requestFullscreen()
    } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen()
    } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen()
    } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullScreen()
    }
}
function iframeScroll() {
    object=$(window.frames["pdfContainer"].document);
    obj = object.find("#viewerContainer");
    var fullClass = object.find("#mainContainer");
    var customScaleOption = $("#pdfContainer").contents().find("#customScaleOption");//presentationMode
    $(window).resize(function () {
        if (document.fullscreenElement == null && document.msFullscreenElement == null && document.mozFullscreenElement == null && document.webkitFullscreenElement == null) {
            isFullScreen = false;
            if(['.ppt', '.pptx','.ppsx'].indexOf(docType) === -1){
                $('#openPage').show();
            }
            fullClass.removeClass("full-class");
            obj.removeClass('full-screen-scroller');
        }
        if(window.screen.width <= 1024){
            customScaleOption.remove();
        }
    });

    if(window.screen.width <= 1024){
        customScaleOption.remove();
    }
    obj_scroll = obj.scrollTop();
    var last_scroll = - obj_scroll;
    $("#openPage").click(function () {
        debugger;
        var ob = document.getElementById("pdf-contianer");
        launchFullscreen(ob);
        isFullScreen = true;
        $("#openPage").hide();
        fullClass.addClass("full-class");
        obj.addClass('full-screen-scroller');
    })
    obj.on("scroll",function () {
        if(!isFullScreen){
            var scroll_top;
            scroll_top = obj.scrollTop();
            if(scroll_top > 40){
                $(".layui-col-space15").removeClass('divTop');
                $("#container-pdf").addClass("goToTop");
                getIframe("pdfContainer").getElementById("toolbarOuterContainer").style.cssText = 'width:100%;position:absolute;bottom:-10px;background:#fff;height:60px;box-shadow: 0px -5px 7px 0px rgba(189,189,189,0.18);'
                getIframe("pdfContainer").getElementById("toolbarContainer").style.cssText = 'width:66%;background: #fff;position: relative;top: 13px;';
                getIframe("pdfContainer").getElementById("toolbarViewerMiddle").style.cssText = 'left: 266px;top: -0.5px;';
                getIframe("pdfContainer").getElementById("viewerContainer").style.cssText = 'background:#f5f5f5;height:calc(100% - 88px)';
                getIframe("pdfContainer").getElementById("mainContainer").style.cssText = 'border:0;padding-bottom:60px;';
                getIframe("pdfContainer").getElementById("sidebarToggle").style.cssText = 'margin-right:12px;';
                getIframe("pdfContainer").getElementById("splitToolbarButton-update").style.cssText = 'left:2%;';
                getIframe("pdfContainer").getElementById("toolbarViewerRight").style.cssText = 'position: absolute;left: 440px;';
                getIframe("pdfContainer").getElementById("sidebarContainer").style.cssText = 'top:37px;';
                getIframe("pdfContainer").getElementById("presentationMode").style.cssText = 'position:fixed;right:0';
                $('#title').addClass('suction-top');
                $(".viewContent").addClass('no-padding-top');
                $('.layui-col-sm2_5').addClass('no-padding-top');
                $(".btn-div").addClass('btn-bottom-div');
                if(isOpen){
                    $(".btn-div").addClass('btn-bottom-div1');
                }
               // $(".parent-directory").show();
            }else{
                $("#container-pdf").removeClass("goToTop");
                $(".layui-col-space15").addClass('divTop');
                getIframe("pdfContainer").getElementById("toolbarOuterContainer").style.cssText = '';
                getIframe("pdfContainer").getElementById("toolbarContainer").style.cssText = '';
                getIframe("pdfContainer").getElementById("toolbarViewerMiddle").style.cssText = '';
                getIframe("pdfContainer").getElementById("viewerContainer").style.cssText = '';
                getIframe("pdfContainer").getElementById("mainContainer").style.cssText = '';
                getIframe("pdfContainer").getElementById("sidebarToggle").style.cssText = '';
                getIframe("pdfContainer").getElementById("splitToolbarButton-update").style.cssText = '';
                getIframe("pdfContainer").getElementById("toolbarViewerRight").style.cssText = '';
                getIframe("pdfContainer").getElementById("sidebarContainer").style.cssText = '';
                getIframe("pdfContainer").getElementById("presentationMode").style.cssText = '';
                $('#title').removeClass('suction-top');
                $(".viewContent").removeClass('no-padding-top');
                $('.layui-col-sm2_5').removeClass('no-padding-top');
                $(".btn-div").removeClass("btn-bottom-div");
                if(!isOpen){
                    $(".btn-div").removeClass('btn-bottom-div1');
                }
            }
        }
    });
    $("#goToTop").click(function () {
        obj.scrollTop(0);
    });
    $("#outerContainer").on('mousewheel', function(event, delta) {
        //鼠标滚轮事件
        var dir = delta > 0 ? 'Up' : 'Down';
        var scroll_top = obj.scrollTop();
        if (dir == 'Up') {
            scroll_top = scroll_top - 40;
        } else {
            scroll_top = scroll_top + 40;
        }
        obj.scrollTop(scroll_top);
        return false;
    })
}
//定义一个变量进行判断，默认false 非全屏状态
var exitFullscreen = false
// 全屏事件
function handleFullScreen() {
    // var element = document.getElementById('excelContent');
    var element = document.getElementById('luckysheet');
    if (this.fullscreen) {
        if (document.exitFullscreen) {
            document.exitFullscreen();
        } else if (document.webkitCancelFullScreen) {
            document.webkitCancelFullScreen();
        } else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
        } else if (document.msExitFullscreen) {
            document.msExitFullscreen();
        }
    } else {
        if (element.requestFullscreen) {
            element.requestFullscreen();
        } else if (element.webkitRequestFullScreen) {
            element.webkitRequestFullScreen();
        } else if (element.mozRequestFullScreen) {
            element.mozRequestFullScreen();
        } else if (element.msRequestFullscreen) {
            // IE11
            element.msRequestFullscreen();
        }
    }
}