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
        }, 2000);

    });
    var gridView ={
        /*初始化页面*/
        initPage: function() {
            var that = this;
            //初始化表格
            that.init();
        },
        init:function(){
            var that = this;
            var hash= $("#hash").val();
            //判断是否为macOs pc
            if (window.screen.width >1024){
                $("body").addClass("macOsPc");
            }else {
                $("body").removeClass("macOsPc");
            }
            $.ajax({
                async:false,
                type:"post",
                url:"/sharefile/fileDetail",
                data:{hash:hash},
                success:function(data) {
                        authorId = data.authorId;
                        filePath = data.filePath;
                        fileName = data.title;
                        fileSuffixName = data.fileSuffixName.toLowerCase();
                        docAbstract = data.docAbstract;
                        authority=data.authority;
                        $("#title").text(data.title);
                        var docId = data.docId;
                        if(window.screen.width <= 1024){
                            if(fileSuffixName == "doc" || fileSuffixName == "docx"){
                                $("#pdfContainer").attr("style","transform:scale(1.4) translateY(14%);margin-left:0");
                            } else {
                                $("body#top .pdf-content").css("padding","10px");
                            }
                        }
                        var obj = document.getElementById("title");
                        if(fileSuffixName==="xlsx"||fileSuffixName==="xls"){  //文档名称前的图片
                            var mask = document.getElementById("lucky-mask-demo");
                            mask.style.display = "flex";
                            var url = "/sharefile/excel/" + docId;
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
                            $("#title").addClass("type-xls");
                        }else if(fileSuffixName==="pdf"){
                            $("#title").addClass("type-pdf");
                            $("#pdfContainer").removeClass("hide");
                        }else if(fileSuffixName==="ppt"||fileSuffixName==="pptx"||fileSuffixName==="ppsx"){
                            $("#title").addClass("type-ppt");
                            $("#pdfContainer").removeClass("hide");
                        }else if(fileSuffixName==="ceb"){
                            $("#pdfContainer").removeClass("hide");
                            $("#title").addClass("type-ceb");
                        }else if(fileSuffixName==="txt") {
                            $("#pdfContainer").removeClass("hide");
                            $("#title").addClass("type-txt");
                        }else if(fileSuffixName==="doc"||fileSuffixName==="docx") {
                            $("#pdfContainer").removeClass("hide");
                            $("#title").addClass("type-doc");
                        }else {
                            $("#pdfContainer").removeClass("hide");
                            $("#title").addClass("type-other");
                        }
                    var author=data.author;
                    if(author===""||author===undefined){
                        author=data.userId;
                    }
                    $("#owner").html("上传者: "+author+"<em>|</em>");
                        $("#uploadTime").html("上传时间: "+data.createTime +"<em>|</em>");
                        $("#fileSize").html("文件大小: "+data.fileSize +"<em>|</em>");
                        $("#downloadNum").html("下载次数: "+data.downloadNum +"<em>|</em>");
                        $("#previewNum").html("预览次数: "+data.readNum );
                        $("#docAbstract").text(docAbstract);
                    if(authority==='1'||data.adminFlag===1||authority==='2'){
                            $("#dowLoadButton").show();
                        }

                        var shareUser = $("#shareUser").val();
                        var watermark_user_flag = $("#watermark_user_flag").val();      //预览时是否添加用户水印标记
                    var watermark_company_flag = $("#watermark_company_flag").val();
                    var companyValue = $("#companyValue").val();
                    if(window.screen.width <= 1024){
                        document.getElementById("pdfContainer").src = "/static/resources/pdf/web/viewer_mobile.html?file="
                            + encodeURIComponent("/preview/listForShare?hash=" +hash+"&isView=0&range_size="+PDFJS.jqwk_rang_size)+"&username=" + encodeURIComponent(shareUser)
                            + "&watermark_user_flag=" + encodeURIComponent(watermark_user_flag) + "&watermark_company_flag=" + encodeURIComponent(watermark_company_flag)
                            + "&companyValue= " + encodeURIComponent(companyValue);
                    }else{
                        document.getElementById("pdfContainer").src = "/static/resources/pdf/web/viewer.html?file="
                            + encodeURIComponent("/preview/listForShare?hash=" +hash+"&isView=0&range_size="+PDFJS.jqwk_rang_size)+"&username=" + encodeURIComponent(shareUser)
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
