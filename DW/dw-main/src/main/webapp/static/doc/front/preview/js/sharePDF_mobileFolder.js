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
    var hash;//映射地址
    var filePath;//文档路径
    var authorId;//作者ID
    var fileName;//文档名称
    var fileSuffixName;//文档后缀
    var filePdfPath;//文档预览路径
    var docAbstract;//文档描述
    var authority;//文档权限
    layui.config({
        base: '/static/resources/weadmin/static/js/'
        , version: '101100'
    }).use('admin');
    layui.use(['jquery', 'admin', 'element'], function () {
        var $ = layui.$, element = layui.element, admin = layui.admin;
    })
    $(document).ready(function() {
        gridView.initPage();
        setTimeout(function(){
            //获取子页面的dom
            var object=$(window.frames["pdfContainer"].document);
            var obj = object.find("#viewerContainer");
            //点击全屏按钮取消收缩效果
            var height_1 = $(".pdf-content").height();
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
                $("body#top .wrapper").removeClass("goToTop")
            });
/*            obj.on('scroll',function(){
                var scroll_top = obj.scrollTop();
                if(scroll_top>50){
                    var newHeight = $("body").height() - $(".header-bar").height() -40;
                    $(".pdf-content").addClass("goTop").height(newHeight);
                    $(".pdf-content #pdfContainer").height(newHeight);
                    $("body#top .wrapper").addClass("goToTop")
                }else {
                    $(".pdf-content").removeClass("goTop").height(height_1);
                    $(".pdf-content #pdfContainer").height(height_1);
                    $("body#top .wrapper").removeClass("goToTop")
                }
            });*/
        }, 2000);

    });
    var gridView ={
        /*初始化页面*/
        initPage: function() {
            var that = this;
            //初始化表格
            that.init();
          //  that.initPath();
          //   that.initBj();
            if (window.screen.width >1024){
                $("body").addClass("macOsPc");
            }else {
                $("body").removeClass("macOsPc");
            }
        },
/*        initPath:function(){
            var that = this;
            var id= $("#docId").val();

            $.ajax({
                async: false,
                type: "post",
                url: "/sharefile/getFoldPath",
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
                    path +=" <span>"+fileName+"</span>";
                    var pathes = $(".pathes");
                    pathes.html(path);
                    pathes.on("click","a",function(){
                        var parentId = $(".pathes a").first().data("id");
                        var curId = $(this).data("id");
                        var index =$(this).parent().index();
                        if(index == 0){
                            $(this).attr("href","/frontFile?fileId="+parentId)
                        }else{
                            $(this).attr("href","/frontFile?fileId="+parentId+"&nextId="+curId+"&easyId="+id);
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
            });
        },*/
        init:function(){
            var that = this;
            var id= $("#id").val();
            $.ajax({
                async:false,
                type:"post",
                url:"/sharefile/fileDetailFolder",
                data:{id:id},
                success:function(data) {
                        authorId = data.authorId;
                        filePath = data.filePath;
                        fileName = data.title;
                    filePdfPath = data.filePdfPath;
                        fileSuffixName = data.fileSuffixName.toLowerCase();
                        docAbstract = data.docAbstract;
                        authority=data.authority;
                        $("#title").text(data.title);
                        if(fileSuffixName == "doc" || fileSuffixName == "docx"){
                            $("#pdfContainer").attr("style","transform:scale(1.4) translateY(14%);margin-left:0");
                        } else {
                            $("body#top .pdf-content").css("padding","10px");
                        }
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
                    $("#owner").html("上传者: "+author+"<em>|</em>");
                        $("#uploadTime").html("上传时间: "+data.createTime +"<em>|</em>");
                        $("#fileSize").html("文件大小: "+data.fileSize +"<em>|</em>");
                        $("#downloadNum").html("下载次数: "+data.downloadNum +"<em>|</em>");
                        $("#previewNum").html("预览次数: "+data.readNum );
                        $("#docAbstract").text(docAbstract);
                    if(authority=='1'||data.adminFlag==1||authority=='2'){
                            $("#dowLoadButton").show();
                        }

                        var shareUser = $("#shareUser").val();
                        var watermark_user_flag = $("#watermark_user_flag").val();      //预览时是否添加用户水印标记
                    var watermark_company_flag = $("#watermark_company_flag").val();
                    var companyValue = $("#companyValue").val();
                    if(window.screen.width <= 1024){
                        document.getElementById("pdfContainer").src = "/static/resources/pdf/web/viewer_mobile.html?file="
                            + encodeURIComponent("/sharefile/list?fileId=" +filePdfPath+"&isView=0&range_size="+PDFJS.jqwk_rang_size)+"&username=" + encodeURIComponent(shareUser)
                            + "&watermark_user_flag=" + encodeURIComponent(watermark_user_flag) + "&watermark_company_flag=" + encodeURIComponent(watermark_company_flag)
                            + "&companyValue= " + encodeURIComponent(companyValue);
                    }else {
                        document.getElementById("pdfContainer").src = "/static/resources/pdf/web/viewer.html?file="
                            + encodeURIComponent("/sharefile/list?fileId=" +filePdfPath+"&isView=0&range_size="+PDFJS.jqwk_rang_size)+"&username=" + encodeURIComponent(shareUser)
                            + "&watermark_user_flag=" + encodeURIComponent(watermark_user_flag) + "&watermark_company_flag=" + encodeURIComponent(watermark_company_flag)
                            + "&companyValue= " + encodeURIComponent(companyValue);
                    }


                },
                error:function(data){
                   $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
                }
            })
        },
        // 布局
        initBj:function () {
            var pdf_height = $("#top .viewContent").innerHeight() - $(".viewContent .viewTitle").outerHeight(true) - $(".viewContent .docView").outerHeight(true) - $(".viewContent .doc-desc").outerHeight(true);
            $(".pdf-content").css("height",pdf_height);
        },

    }
})(this);
