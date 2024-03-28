/**
 * Created by Lenovo on 2018/2/9.
 */
(function() {
    var count;
    var category//分类
    var docId;//文档ID
    var authorId;//作者ID
    var fileName;//文档名臣
    var filePath;//文档路径
    var collection;//是否收藏
    var point = 1;//下载所需积分`
    var  allowPage;
    $(document).ready(function() {
        gridView.initPage();
    });
    var gridView ={
        /*初始化页面*/
        initPage: function() {
            var that = this;
            //初始化表格
            that.init();
            that.initPath();
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
            $.ajax({
                async: false,
                type: "post",
                url: "/preview/getFoldPath",
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
                }
            });
        },
        init:function(){
            var that = this;
            var id= $("#id").val();
            $.ajax({
                async:false,
                type:"post",
                url:"/sharefile/fileDetailFolder",
                data:{id:id},
                success:function(data) {
                    document.title = data.title+"-"+projectTitle;
                    authorId = data.authorId;
                    filePath = data.filePath;
                    fileName = data.title;
                    collection =data.collection;
                    fileSuffixName = data.fileSuffixName.toLowerCase();
                    docAbstract = data.docAbstract;
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
                    }else if(fileSuffixName=="png"||fileSuffixName=="jpeg"||fileSuffixName=="gif"||fileSuffixName=="jpg"||fileSuffixName=="bmp") {
                        $("#title").addClass("type-pic");
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
                    var toLoad = "";
                    toLoad += '<img id="showImg"  oncontextmenu = "return false;"style="   -webkit-user-select: none; -moz-user-select: none; -ms-user-select: none;user-select: none;">';
                    $("#viewerForShare").html("");
                    $("#viewerForShare").append(toLoad);
                    document.getElementById("showImg").src = "/sharefile/list?fileId="+filePath;
                    //document.getElementById("showImg-big").src = "/preview/listForShare?fileId="+encodeURIComponent(data.filePath);

                    var viewer = new Viewer(document.getElementById('viewerForShare'),{title:false,  toolbar: {
                            zoomIn: 4,
                            zoomOut: 4,
                            oneToOne: 4,
                            prev: 4,
                            play: {
                                show: 4,
                                size: 'large',
                            },
                            next: 4,

                        }})



                    // that.getRatingRecords(id,1);
                    // $("#showImg").click(function () {
                    //
                    //     $(".showImg").css({"z-index":"1000","opacity":"1"});
                    //     //弹层出现时，页面禁止滚动
                    //     $("body").removeClass("img-body");
                    // });
                    // $(".showImg").click(function () {
                    //     $(".showImg").css({"z-index":"-1","opacity":"0"});
                    //     //弹层关闭时，页面恢复滚动
                    //     $("body").addClass("img-body");
                    //
                    // });


                },
                error:function(data){
                    $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
                }
            })
        },
        initButtonEvent:function (){
            var that = this;
            $("#collectionButton").click(function(){
                that.collectionFiles();
            });
            $('#loginButton').click(function () {
                loginSubmit();
            });

        },
        // getRatingRecords:function (id,pageNum){
        //     $.ajax({
        //         async:false,
        //         type:"post",
        //         url:"/preview/getRatingRecords",
        //         data:{id:id,page:pageNum},
        //         success:function(data) {
        //             var list = eval(data);
        //             var num = list.length-1;
        //             count = list[num];
        //             $("#num").html(count);
        //             var htmlTxet ="";
        //             if(num>0){
        //                 for(var i=0;i<num;i++){
        //                     htmlTxet += "<li><div class='userImg'></div><div class='recordCon'><div class='stars clearfix'><ul class='star-list fl'>";
        //                     for(var k=1;k<=5;k++){
        //                         if(k<=list[i].rate){
        //                             htmlTxet += "<li class='star light'></li>";
        //                         }else{
        //                             htmlTxet += "<li class='star'></li>";
        //                         }
        //                     }
        //                     htmlTxet += "</ul><span class='fl'>"+list[i].user_name+"</span><span class='date fr'>"+list[i].rateTime+"</span></div><p>"+list[i].comment+"</p></div></li>";
        //                 }
        //             }else {
        //                 htmlTxet += '<li class="none"><h5 class="tc">暂无评价!</h5></li>';
        //                 $("#footDiv").hide();
        //             }
        //             $("#ratingRecords").html(htmlTxet);
        //         },
        //         error:function(data){
        //             $.showInfoDlg("提示","没有评价!",2);
        //         }
        //     })
        // },
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
        // collectionFiles: function() {
        //     var id = $("#docId").val();
        //     $.ajax({
        //         async: false,
        //         type: "post",
        //         url: "/preview/colletFile",
        //         data: {id: id,authorId:authorId},
        //         success: function (data) {
        //             if(data=="success"){
        //                 $.showInfoDlg("提示", "收藏成功！", 2);
        //                 $("#collectionButton").disable(true);
        //             }else{
        //                 alert(data);
        //             }
        //         },
        //         error: function (data) {
        //             $.showInfoDlg("提示", "收藏失败！", 2);
        //         }
        //     })
        // },
        downloadFile:function(){//文档ID，作者ID，下载消耗积分,文档名，分类,文件路径
            var loginName = $("#loginName")[0].innerText;//获取登录用户的名称
            //判断用户是否登录
            if(loginName == "登录"){
                $("#login").modal();
                return;
            }
            $.ajax({
                type:"post",
                url:"/file/changePoints",
                async:false,
                cache:false,
                data:{
                    authorId:authorId,
                    docId:docId,
                    points:point
                },
                success:function(data){
                    if(data == "success"){
                        $.ajaxFileUpload({
                            url:"/file/fileDownload",
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

                },
                error:function(){
                    //alert("下载失败");
                    $.showInfoDlg("提示","下载失败",2);
                }
            })
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

})(this);
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
    $.ajax({
        url: "/index/loginCheck",
        type: "post",
        async: "false",
        dataType: "text",
        data:{name:name,password:password},
        success: function (data) {
            if(data=="false"){
                //alert("用户名或密码不正确！");
                $.showInfoDlg("提示","用户名或密码不正确！",2);
            }else{
                $("#loginName")[0].innerText = name;
                $("#login").modal("hide");
            }
        }
    });
}
function cancleLogin(){
    if (confirm("确定要注销吗？")) {
        location.href = "/index/logout";
    }
}
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showDoc(fileType,id) {
    var selectVal = $("#select").val();
    var keyWords = $("#headerSearchInputValue").val();
    if(fileType=="png"||fileType=="jpg"||fileType=="gif"||fileType=="bmp"||fileType=="jpeg"){
        openWin("/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_self");
    }else if(fileType=="mp4"||fileType=="wmv"){
        openWin("/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_self");
    } else if(fileType=="mp3"||fileType=="m4a"){
        openWin("/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_self");
    }else if(fileType == 'docx'||fileType == 'doc'||fileType == 'dot'||fileType == 'xls'
        ||fileType == 'wps'||fileType == 'xlt'||fileType == 'et'
        ||fileType == 'ett'||fileType == 'ppts'||fileType == 'pot'
        ||fileType == 'dps'||fileType == 'dpt'
        || fileType == 'xlsx'||fileType == 'txt'||fileType == 'pdf'
        ||fileType == 'ceb' ||fileType == 'ppt'|| fileType == 'pptx'){
        openWin("/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_self");
    }else {
        layer.msg("此文件类型不支持预览。");
    }
}
function showDocBlank(fileType,id) {
    var selectVal = $("#select").val();
    var keyWords = $("#headerSearchInputValue").val();
    if(fileType=="png"||fileType=="jpg"||fileType=="gif"||fileType=="bmp"||fileType=="jpeg"){
        openWin("/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
    }else if(fileType=="mp4"||fileType=="wmv"){
        openWin("/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
    } else if(fileType=="mp3"||fileType=="m4a"){
        openWin("/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
    }else if(fileType == 'docx'||fileType == 'doc'||fileType == 'dot'||fileType == 'xls'
        ||fileType == 'wps'||fileType == 'xlt'||fileType == 'et'
        ||fileType == 'ett'||fileType == 'ppts'||fileType == 'pot'
        ||fileType == 'dps'||fileType == 'dpt'
        || fileType == 'xlsx'||fileType == 'txt'||fileType == 'pdf'
        ||fileType == 'ceb' ||fileType == 'ppt'|| fileType == 'pptx'){
        openWin("/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
    }else {
        layer.msg("此文件类型不支持预览。");
    }
}
