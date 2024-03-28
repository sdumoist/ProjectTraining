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
    var hash;//映射地址
    var filePath;//文档路径
    var authorId;//作者ID
    var fileName;//文档名称
    var fileSuffixName;//文档后缀
    var filePdfPath;//文档预览路径
    var docAbstract;//文档描述
    var authority;//文档权限
    var uploadState;//上传状态
    // layui.config({
    //     base: '/static/resources/weadmin/static/js/'
    //     , version: '101100'
    // }).use('admin');
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layui.config({
            base: Hussar.ctxPath+'/static/resources/weadmin/static/js/'
            , version: '101100'
        }).use('admin');
    });
    $(document).ready(function() {
        gridView.initPage();
        setTimeout(function(){
            //获取子页面的dom
            if(!window.frames["pdfContainer"]){return false}
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
            obj.on('scroll',function(){
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
            });
        }, 2000);

    });
    var gridView ={
        /*初始化页面*/
        initPage: function() {
            var that = this;
            //初始化表格
            that.init();
          //  that.initPath();
            that.initBj();
        },
        init:function(){
            var that = this;
            var hash= $("#hash").val();
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

                /*$.ajax({
                async:false,
                type:"post",
                url:"/sharefile/fileDetail",
                data:{hash:hash},
                success:function(data) {
                        document.title = data.title+"-金企文库";
                        authorId = data.authorId;
                        filePath = data.filePath;
                        fileName = data.title;
                        fileSuffixName = data.fileSuffixName.toLowerCase();
                        docAbstract = data.docAbstract;
                        authority=data.authority;
                        uploadState = data.uploadState;

                    $("#title").text(data.title);

                        var obj = document.getElementById("title");
                        if(fileSuffixName=="xlsx"||fileSuffixName=="xls"){  //文档名称前的图片
                            $("#title").addClass("type-xls");
                            // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
                        }else if(fileSuffixName=="pdf"){
                            $("#title").addClass("type-pdf");
                            // obj.style.cssText = "background:url(/static/resources/img/pdf.png)no-repeat left center;"
                        }else if(fileSuffixName=="ppt"||fileSuffixName=="pptx"){
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
                        }else if(fileSuffixName=="zip"||fileSuffixName=="rar") {
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
                    $("div.pdf-content").hide();
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
                },
                error:function(data){
                   $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
                }
            })*/
                var ajax = new $ax(Hussar.ctxPath + "/sharefile/fileDetail", function(data) {
                    document.title = data.title+"-"+projectTitle;
                    authorId = data.authorId;
                    filePath = data.filePath;
                    fileName = data.title;
                    fileSuffixName = data.fileSuffixName.toLowerCase();
                    docAbstract = data.docAbstract;
                    authority=data.authority;
                    uploadState = data.uploadState;

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
                    }else if(fileSuffixName=="zip"||fileSuffixName=="rar") {
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
                }, function(data) {
                    $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
                });
                ajax.set("hash",hash);
                ajax.start();
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
})(this);
