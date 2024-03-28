/**
 * Created by Lenovo on 2018/1/20.
 */
(function() {
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
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layui.config({
            base: Hussar.ctxPath+'/static/resources/weadmin/static/js/'
            , version: '101100'
        }).use('admin');
    });
    layui.use(['jquery', 'admin', 'element'], function () {
        var $ = layui.$, element = layui.element, admin = layui.admin;
    });
    $(document).ready(function() {
        gridView.initPage();
        setTimeout(function(){
            //获取子页面的dom
            var object=$(window.frames["pdfContainer"].document);
            var obj = object.find("#viewerContainer");
            //点击全屏按钮取消收缩效果
            var height_1 = $(".pdf-content").height() + 20;
            var fullBtn = $("#pdfContainer").contents().find("#presentationMode");//presentationMode
            var customScaleOption = $("#pdfContainer").contents().find("#customScaleOption");//presentationMode
            $(window).resize(function () {
                if(window.screen.width <= 1024){
                    customScaleOption.remove();
                }
            });
            if(window.screen.width <= 1024){
                customScaleOption.remove();
            }
            fullBtn.click(function () {
                $(".pdf-content").removeClass("goTop").height(height_1);
                $(".pdf-content #pdfContainer").height(height_1);
                $("body#top .container-new").removeClass("goToTop")
            });
            obj.on('scroll',function(){
                var scroll_top = obj.scrollTop();
                if(scroll_top>50){
                    $("body#top .container-new").removeClass("container-pdf");
                    var newHeight = $("body").height() - $(".header-bar").height() -20;
                    $(".pdf-view").addClass("goToTop");
                    $("body#top .container-new").addClass("goToTop");
                    $(".pdf-content").addClass("goTop").height(newHeight);
                    $(".pdf-content #pdfContainer").height(newHeight);
                    $(".layui-row").removeClass("divtop");
                    $(".goToTop").show();
                }else {
                    $(".pdf-content").removeClass("goTop").height(height_1);
                    $(".pdf-content #pdfContainer").height(height_1);
                    $(".pdf-view").removeClass("goToTop");
                    $("body#top .container-new").removeClass("goToTop");
                    $("body#top .container-new").addClass("container-pdf");
                    $(".layui-row").addClass("divtop");
                    $(".goToTop").hide();
                }
            });
            $(".goToTop").click(function () {
                obj.scrollTop(0)
            });
            $("body").bind('mousewheel', function(event, delta) {
                //鼠标滚轮事件
                var dir = delta > 0 ? 'Up' : 'Down';
                var scroll_top = obj.scrollTop();
                if (dir == 'Up') {
                    scroll_top = scroll_top - 40
                } else {
                    scroll_top = scroll_top + 40
                }
                obj.scrollTop(scroll_top)
                return false;

            })
        }, 2000);
        window.addEventListener("message",function (event) {
            var layer = event.data.layer;
            if(layer==="reject"){
                //关闭审核意见
                $(".layui-layer-close").on('click',function () {
                    $("#examine").hide()
                });
                $(".layui-layer-btn").on('click',function () {
                    $("#examine").hide()
                });
            }
        })
    });
    var gridView ={
        /*初始化页面*/
        initPage: function() {
            var that = this;
            //初始化表格
            that.init();
            that.initPath();
            that.initButtonEvent();
            that.initBj();
        },
        init:function(){
            var that = this;

            var id=$("#id").val();
            var url="/component/componentView?componentId="+id;
            layui.use(['Hussar'], function(){
                var Hussar = layui.Hussar;

                document.getElementById("pdfContainer").src = Hussar.ctxPath+url;
            });

            $("#pdfContainer").removeClass("hide");



            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

                /**
                 * 加载与当前文档名称相关的推荐文档
                 */
                /*$.ajax({
                async: false,
                type: "post",
                url: Hussar.ctxPath+"/toShowComponent/componentList",
                dataType: 'json',
                data: {},
                success: function (data) {
                    var items = data.data;
                    var toLoad = "";
                    if (items.length != 0) {
                        for (var i = 0; i < 5 && i < items.length; i++) {//同上

                            toLoad += '<li class="message">';
                            toLoad += '<img class="article-type" src="'+Hussar.ctxPath+'/static/resources/img/front/file-icon/ic-component01.png">';
                            toLoad += '<a href="'+Hussar.ctxPath+'/toShowComponent/toShowPDF?id='+items[i].componentId+'" target="_blank" title="'+items[i].componentName+'">';
                            toLoad += items[i].componentName;
                            toLoad += '</a>';
                            toLoad += '<div class="clearfix">';
                            toLoad += '<div class="article-msg">'+ items[i].userName+'</div>';
                            toLoad += '<div class="article-msg-date">'+ items[i].createTime+'</div>';
                            toLoad += '</div>';
                            toLoad += '</li>';
                        }
                        $("#showRecommendArticle").html("");
                        $("#showRecommendArticle").append(toLoad);
                    }
                }
            });*/
                var ajax = new $ax(Hussar.ctxPath + "/toShowComponent/componentList", function(data) {
                    var items = data.data;
                    var toLoad = "";
                    if (items.length != 0) {
                        for (var i = 0; i < 5 && i < items.length; i++) {//同上

                            toLoad += '<li class="message">';
                            toLoad += '<img class="article-type" src="'+Hussar.ctxPath+'/static/resources/img/front/file-icon/ic-component01.png">';
                            toLoad += '<a href="'+Hussar.ctxPath+'/toShowComponent/toShowPDF?id='+items[i].componentId+'" target="_blank" title="'+items[i].componentName+'">';
                            toLoad += items[i].componentName;
                            toLoad += '</a>';
                            toLoad += '<div class="clearfix">';
                            toLoad += '<div class="article-msg">'+ items[i].userName+'</div>';
                            toLoad += '<div class="article-msg-date">'+ items[i].createTime+'</div>';
                            toLoad += '</div>';
                            toLoad += '</li>';
                        }
                        $("#showRecommendArticle").html("");
                        $("#showRecommendArticle").append(toLoad);
                    }
                }, function(data) {

                });
                ajax.start();

                /**
                 * 加载猜你喜欢的文档
                 */
                /*$.ajax({
                    type:"post",
                    url: Hussar.ctxPath+"/preview/guessYouLike",
                    dataType:'json',
                    data:{currentId:id},
                    success: function (data) {
                        var items = data;
                        var toLoad = "";
                        if (items.length != 0) {
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
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/preview/guessYouLike", function(data) {
                    var items = data;
                    var toLoad = "";
                    if (items instanceof Array &&items.length != 0) {
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
                var id = $("#docId").val();
                ajax.set("currentId",id);
                ajax.start();

            });

        },
        initPath:function(){
            var that = this;
            var id= $("#id").val();
            var fileType

            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

                /*$.ajax({
                async: false,
                type: "post",
                url: Hussar.ctxPath+"/component/getComponent",
                data: {id: id},
                success: function (data) {
                    var path ="";
                    if(!!data){


                        path +=" <span><a href='#'target='_blank' data-id='"+data.componentApply.componentId+"'>"+data.componentApply.componentName+"</a> </span>";


                    }else{
                        path ="";
                    }
                    var pathes = $(".pathes");
                    pathes.html(path);

                }
            });*/
                var ajax = new $ax(Hussar.ctxPath + "/component/getComponent", function(data) {
                    var path ="";
                    if(!!data){


                        path +=" <span><a href='#'target='_blank' data-id='"+data.componentApply.componentId+"'>"+data.componentApply.componentName+"</a> </span>";


                    }else{
                        path ="";
                    }
                    var pathes = $(".pathes");
                    pathes.html(path);
                }, function(data) {

                });
                ajax.set("id",id);
                ajax.start();
            });
        },
        initButtonEvent:function (){
            var that = this;
            $('#loginButton').click(function () {
                loginSubmit();
            });

            $("#queryBtn").click(function () {
                var  docName =  $("#fileName").val();
                var fileType =   $('input:radio:checked').val();
                if(docName!=""){
                    layui.use(['Hussar'], function(){
                        var Hussar = layui.Hussar;

                        window.location.href = Hussar.ctxPath+"/searchView?keyWords=" + fileName + "&fileType=" + fileType;
                    });
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

        },
        // 布局
        initBj:function () {
            var pdf_height = $("#top .viewContent").height() - $(".viewContent .viewTitle").outerHeight(true) - $(".viewContent .docView").outerHeight(true) - $(".viewContent .doc-desc").outerHeight(true);
            $(".pdf-content").css("height",pdf_height);
        },

    }
})(this);
function cancleLogin(){
    layer.confirm('确认注销？', {icon: 3, title: '提示'}, function (index) {
        layui.use(['Hussar'], function(){
            var Hussar = layui.Hussar;

            window.location.href = Hussar.ctxPath+"/logout.do";
        });
        layer.close(index);
    });
}
$(document).ready(function(){



});


