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
            $(".goToTop").click(function () {
                obj.scrollTop(0)
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
            debugger
            $.ajax({
                async:false,
                type:"post",
                url:"/sharefile/fileDetail",
                data:{hash:hash},
                success:function(data) {
                        document.title = data.title+"-"+projectTitle;
                        authorId = data.authorId;
                        filePath = data.filePath;
                        fileName = data.title;
                        fileSuffixName = data.fileSuffixName.toLowerCase();
                        docAbstract = data.docAbstract;
                        authority=data.authority;
                        uploadState = data.uploadState;
                        var docId = data.docId;

                    $("#title").text(data.title);

                        var obj = document.getElementById("title");
                        if(fileSuffixName=="xlsx"||fileSuffixName=="xls"){  //文档名称前的图片
                            $("#title").addClass("type-xls");
                            // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
                        }else if(fileSuffixName=="pdf"){
                            $("#title").addClass("type-pdf");
                            // obj.style.cssText = "background:url(/static/resources/img/pdf.png)no-repeat left center;"
                        }else if(fileSuffixName=="ppt"||fileSuffixName=="pptx" ||fileSuffixName=="ppsx"){
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
                    if (fileSuffixName == "xlsx" || fileSuffixName == "xls" || fileSuffixName == "csv" || fileSuffixName == "et" ) {
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
                    }else if (uploadState){
                        document.getElementById("pdfContainer").src = "/static/resources/pdf/web/viewer.html?file="
                            + encodeURIComponent("/preview/listForShare?hash=" +hash+"&isView=0&range_size="+PDFJS.jqwk_rang_size)+"&username=" + encodeURIComponent(shareUser)
                            + "&watermark_user_flag=" + encodeURIComponent(watermark_user_flag) + "&watermark_company_flag=" + encodeURIComponent(watermark_company_flag)
                            + "&companyValue= " + encodeURIComponent(companyValue);
                        $("#pdfContainer").removeClass("hide");
                    } else {
                        //文档预览加上“加载中”遮罩
                        $("#loadingBox").removeClass("hide");
                        //循环获取状态，判断是否上传成功
                        var checkUploadState = setInterval(function (){$.ajax({
                            url: '/sharefile/checkUploadState',
                            type: 'post',
                            data:{
                                hash:hash
                            },
                            success: function (data) {
                                if (data){
                                    //关闭定时器
                                    clearInterval(checkUploadState);
                                    //重新加载
                                    gridView.init();
                                    //清除遮罩
                                    $("#pdfContainer").removeClass("hide");
                                    $("#loadingBox").addClass("hide");


                                }
                            }
                        })},1000);

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
            // $(".pdf-content").css("height",pdf_height);
        },

    }
})(this);
