/**
 * @Author: xubin
 * @Date:2018-07-12
 */
$(document).ready(function(){
    var filePath;//文档路径
    var authorId;//作者ID
    var fileName;//文档名称
    var fileSuffixName;//文档后缀
    var filePdfPath;//文档预览路径
    var docAbstract;//文档描述
    var authority;//文档权限
    var collection;//是否收藏
    var hash= $("#hash").val();
    /*$.ajax({
        async:false,
        type:"post",
        url:"/sharefile/fileDetail",
        data:{hash:hash},
        success:function(data) {
            document.title = data.title+"-金企文库";
            authorId = data.authorId;
            filePath ="/preview/listForShare?hash=" + hash;
            fileName = data.title;
            fileSuffixName = data.fileSuffixName.toLowerCase();
            docAbstract = data.docAbstract;
            authority=data.authority;
            $("#docId").text(data.id);
            $("#title").text(data.title);

            var obj = document.getElementById("title");
            if(fileSuffixName=="mp3"){  //文档名称前的图片
                $("#title").addClass("type-mp3");
                // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
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
            if(collection=='0'){
                $("#collection").show();
                $("#cancelCollection").hide();
            }else{
                $("#collection").hide();
                $("#cancelCollection").show();
            }
            document.getElementById("s1").src=filePath;
            document.getElementById("audio").load();
        },
        error:function(data){
            $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
        }
    });*/
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        var ajax = new $ax(Hussar.ctxPath + "/sharefile/fileDetail", function(data) {
            document.title = data.title+"-"+projectTitle;
            authorId = data.authorId;
            filePath ="/preview/listForShare?hash=" + hash;
            fileName = data.title;
            fileSuffixName = data.fileSuffixName.toLowerCase();
            docAbstract = data.docAbstract;
            authority=data.authority;
            $("#docId").text(data.id);
            $("#title").text(data.title);

            var obj = document.getElementById("title");
            if(fileSuffixName=="mp3"){  //文档名称前的图片
                $("#title").addClass("type-mp3");
                // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
            }
            var author=data.author;
            if(author==""||author==undefined){
                author=data.userId;
            }
            $("#owner").html(""+author+"<em>|</em>");
            $("#uploadTime").html(""+data.createTime.slice(0,10) +"<em>|</em>");
            $("#fileSize").html(""+data.fileSize +"<em>|</em>");
            $("#downloadNum").html(""+data.downloadNum  +"次下载"+"<em>|</em>");
            $("#previewNum").html(""+data.readNum +"次预览");
            $("#docAbstract").text(docAbstract);
            if(authority=='1'||data.adminFlag==1||authority=='2'){
                $("#dowLoadButton").show();
            }
            if(collection=='0'){
                $("#collection").show();
                $("#cancelCollection").hide();
            }else{
                $("#collection").hide();
                $("#cancelCollection").show();
            }
            document.getElementById("s1").src=Hussar.ctxPath+filePath;
            document.getElementById("audio").load();
        }, function(data) {
            $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
        });
        ajax.set("hash",hash);
        ajax.start();
    });
});