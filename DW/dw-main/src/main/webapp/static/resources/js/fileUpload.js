/**
 * Title:文件上传
 * author:zhangzhen
 * Date: 2018/1/12
 */
var category = [];//显示的分类内容
var categoryId = "";
var fileCounts = 0;
(function () {
    var allowedExtension = ['.doc','.docx','.xls','.xlsx','.ppt','.pptx','.pdf','.zip','.rar','.txt'];
    $(document).ready(function () {
        //$("#expectPointDiv").hide();//如果需要隐藏请自行隐藏
        var uploadResult = $("#uploadResult").val();//上传结果
        if(uploadResult != null && uploadResult != ""){
            alert(uploadResult);
        }
        initFileUpload();
        /**
         * 返回
         */
        $("#back").click(function(){
            window.location.href="/index/toInfo"
        });
        /**
         * 退出(注销)
         */
        $("#exit").click(function(){
            if (confirm("确定要注销吗？")) {
                location.href = "/index/logout";
            }
        });
        $("#allowPreview").click(function(){
            if($(this).is(":checked")){
                $("#previewPageNum").attr("disabled",false);
            }else{
                $("#previewPageNum").attr("disabled",true);
            }
        })
        $("#search").click(function () {
            fileName =  $("#searchInput").val();
            fileType =   $('input:radio:checked').val();
            if(fileName!=""){
                window.location.href="/search?keyWords="+fileName+"&fileType="+fileType+"&page="+1;
            }else{
                alert("请输入关键词，多个关键词以空格隔开");
            }
        });
        /**
         * 提交
         */
       $("#uploadSubmit").click(function(){
           var fileTitle = $("#fileName").val().trim();
           //标题
           if(fileTitle == ""){
               alert("请输入标题");
               return;
           }
           var fileName = $(".file-caption-name")[0].innerText;
           //上传的文件名
           if(fileName == ""){
               alert("请选择上传的文件");
               return;
           }
           var extension = fileName.substr(fileName.lastIndexOf("."));
           //文件的扩展名
           var extensionFlag = 0;
           //扩展名标志
           for(var i = 0;i<allowedExtension.length;i++){
               if(extension == allowedExtension[i]){
                   extensionFlag = 1;
                   break;
               }
           }
           //if(extensionFlag == 0){
           //    alert("上传文件的扩展名不符合要求");
           //    return;
           //}
           //var expectPoint = $("#expectPoint").find("option:selected").val();
           //期望分值
           var brief = $("#brief").val();
           //文档简介
           var previewFlag = $("#allowPreview").get(0).checked;
           //预览标志
           var downloadFlag = $("#allowDownload").get(0).checked;
           //下载标志
           var allowPreview = 1;
           //是否允许预览
           var allowDownload = 1;
           //是否允许下载
           var tags = $("#docTag").val();
           //标签
           var categoryContent = category.join(",");
           if(categoryContent == ""){
               alert("分类不能为空");
               return;
           }
           var reg = /^[1-9]\d*$/;
           if(!previewFlag){
               allowPreview = 0;
           }else{
               allowPreview = $("#previewPageNum").val();//允许预览的页数
               if(allowPreview == ""){
                   allowPreview = 1;
               }else{
                   if(!reg.test(allowPreview)){
                        alert("预览页数必须为正整数");
                       return;
                   }
               }
           }
           if(!downloadFlag){
               allowDownload = 0;
           }
           $("#uploadSubmit").attr("disabled",true);
           $("#file").fileinput('upload');
           //$("form").attr("action","/file/fileUpload?title="+fileTitle+"&expectPoint="+expectPoint+"&brief="+brief+"&allowPreview="+allowPreview+"&allowDownload="+allowDownload+"&categoryId="+categoryId+"&tags="+tags+"&categoryContent="+categoryContent);
       });
        var closeFlag = false;//判断input框是否关闭过
        var oldLevel = 0;//上次点击对象的等级
        //模拟下拉框
        $('#category').on('click',function(){
            if(closeFlag){
                category = [];
                $('#category').val("");
            }
            if($('#category1').is('.hide')){
                $.ajax({
                    type:"post",
                    url:"/file/categoryList",
                    data:{
                        id:'root'
                    },
                    cache:false,
                    async:false,
                    dataType:"json",
                    success:function(data){
                        var obj = "";
                        for(var i = 0;i<data.length;i++){
                            obj += "<p data-cid="+data[i].id+" data-level='1'>"+data[i].name+"</p>";
                        }
                        $("#category1").html(obj);
                    }
                });
                $('#category1').removeClass('hide');
            }else{
                $('#category1').addClass('hide');
                $('#category2').addClass('hide');
                $('#category3').addClass('hide');
                $('#category4').addClass('hide');
                closeFlag = true;
            }
        });
        $('.select ul li').on('click','p',function(){
            var id = $(this).data("cid");
            //点击对象的data-cid
            categoryId = id;
            var level = $(this).data("level");
            //点击对象的等级
            if(level <= oldLevel){
                for(var i = level+1;i <= 4;i++){
                    $('#category'+i).addClass('hide');
                }
                for(var i = category.length;i>=level;i--){
                    category.pop();
                }
            }
            /**
             * 根据等级拼接input框中内容
             */
            category.push($(this).html());
            /**
             * 查询点击对象的下一级分类
             */
            $.ajax({
                type:"post",
                url:"/file/categoryList",
                data:{
                    id:id
                },
                cache:false,
                async:false,
                dataType:"json",
                success:function(data){
                    if(data.length>0){
                        var obj = "";
                        for(var i = 0;i<data.length;i++){
                            obj += "<p data-cid="+data[i].id+" data-level="+(level+1)+">"+data[i].name+"</p>";
                        }
                        $("#category"+(level+1)).html(obj);
                        $("#category"+(level+1)).removeClass('hide');
                        $('#category').val(category.join(">"));
                        oldLevel = level;
                    }else{
                        $('#category').val(category.join(">"));
                        $('#category1').addClass('hide');
                        $('#category2').addClass('hide');
                        $('#category3').addClass('hide');
                        $('#category4').addClass('hide');
                        closeFlag = true;
                    }

                }
            });
//            $('.select input').val($(this).html());
//            $('.select .city').addClass('hide');
//            $('.select input').css('border-bottom','1px solid $d6d6d6');
        });
        $(document).mouseup(function(e){
            if($(e.target).parent(".select ul li").length == 0){
                $('#category1').addClass('hide');
                $('#category2').addClass('hide');
                $('#category3').addClass('hide');
                $('#category4').addClass('hide');
            }
        });
        $('.select ul li').on('mouseenter mouseleave','p',
            function(){
                $(this).toggleClass('p_enter');
            }
        );
        /**
         * 下载按钮的点击事件
         */
        $("#fileDownload").click(function(){
            $.ajax({
                type:"post",
                url:"/file/changePoints",
                async:false,
                cache:false,
                data:{
                    authorId:'admin',
                    docId:'4812870c-ffdd-11e7-8e6b-54e1ad611471',
                    points:5
                },
                success:function(data){
                    if(data == "success"){
                        window.location.href="/file/fileDownload";
                    }else {
                        $.showInfoDlg("提示","下载出错",2);
                    }

                },
                error:function(){
                    alert("下载失败");
                }
            })
        });
    });
})(this);
var xhrOnProgress=function(fun) {
    xhrOnProgress.onprogress = fun; //绑定监听
    //使用闭包实现监听绑
    return function() {
        //通过$.ajaxSettings.xhr();获得XMLHttpRequest对象
        var xhr = $.ajaxSettings.xhr();
        //判断监听函数是否为函数
        if (typeof xhrOnProgress.onprogress !== 'function')
            return xhr;
        //如果有监听函数并且xhr对象支持绑定时就把监听函数绑定上去
        if (xhrOnProgress.onprogress && xhr.upload) {
            xhr.upload.onprogress = xhrOnProgress.onprogress;
        }
        return xhr;
    }
}

function initFileUpload(){
    var num = 0;
    //清空上传文件
    $(".fileinput-remove-button").click();
    $("#file").fileinput({
        language: 'zh',                 //中文
        uploadUrl:'/file/fileUpload',
        allowedFileExtensions: ['doc','docx','xls','xlsx','ppt','pptx','pdf','zip','rar','mp4'],//接收的文件后缀
        allowedFileExtensions: ['doc','docx','xls','xlsx','ppt','pptx','pdf','zip','rar','txt'],//接收的文件后缀
        showPreview: true,              //展前预览
        showUpload: false,              //不显示上传按钮
        showCaption: true,             //显示文字表述
        uploadAsync:true,                             //采用异步上传
        removeFromPreviewOnError:false,                 //当文件不符合规则，就不显示预览
        maxFileCount: 100,
        maxFileSize: 1024*1024,                          //单位为kb，如果为0表示不限制文件大小
        autoReplace:true,
        overwriteInitial: false,
        /*不同文件图标配置*/
        previewFileIconSettings: {
            'docx': '<i class="fa fa-file-word-o text-primary" ></i>',
            'xlsx': '<i class="fa fa-file-excel-o text-success"></i>',
            'pptx': '<i class="fa fa-file-powerpoint-o text-danger"></i>',
            'jpg': '<i class="fa fa-file-photo-o text-warning"></i>',
            'pdf': '<i class="fa fa-file-pdf-o text-danger"></i>',
            'zip': '<i class="fa fa-file-archive-o text-muted"></i>',
            'doc': '<i class="fa fa-file-word-o text-primary"></i>',
            'xls': '<i class="fa fa-file-excel-o text-success"></i>',
            'ppt': '<i class="fa fa-file-powerpoint-o text-danger"></i>',
            'htm': '<i class="fa fa-file-code-o text-info"></i>',
            'txt': '<i class="fa fa-file-text-o text-info"></i>',
            'mov': '<i class="fa fa-file-movie-o text-warning"></i>',
            'mp3': '<i class="fa fa-file-audio-o text-warning"></i>',
            'gif': '<i class="fa fa-file-photo-o text-muted"></i>',
            'png': '<i class="fa fa-file-photo-o text-primary"></i>'
        },
        uploadExtraData: function(){  //传递的其他的参数
            var fileTitle = $("#fileName").val().trim();
            var expectPoint = $("#expectPoint").find("option:selected").val(); //期望分值
            var brief = $("#brief").val();//文档简介
            var previewFlag = $("#allowPreview").get(0).checked;//预览标志
            var downloadFlag = $("#allowDownload").get(0).checked;//下载标志
            var allowPreview = 1;
            //是否允许预览
            var allowDownload = 1;
            //是否允许下载
            var tags = $("#docTag").val();
            //标签
            var categoryContent = category.join(",");
            if(!previewFlag){
                allowPreview = 0;
            }else{
                var previewPages = $("#previewPageNum").val();//允许预览的页数
                if(previewPages != ""){
                    allowPreview = previewPages;
                }
            }
            if(!downloadFlag){
                allowDownload = 0;
            }
            var obj = {
                title:fileTitle,
                expectPoint:expectPoint,
                brief:brief,
                allowPreview:allowPreview,
                allowDownload:allowDownload,
                categoryId:categoryId,
                tags:tags,
                categoryContent:categoryContent
            };
            return obj;
        }
    }).on('fileuploaded', function(event, data, previewId, index) {
        var docArr = [];
        docArr = data.response.docId;
        if(data.response.result){
            $.ajax({
                type:"post",
                url:"/file/uploadAddPoints",
                data:{
                    "docId":docArr
                },
                async:false,
                cache:false,
                traditional:true,
                success:function(){
                    num++;
                    if(fileCounts == num){
                        endUpload();
                    }
                }
            });

        }else{
            $.showInfoDlg("提示","上传失败！", 0);
        }
    });
};

function endUpload(){
    layer.confirm("上传成功，是否继续上传",{icon:3,title:'提示'},function(){
        window.location.reload();
    },function(){
        window.location.href="/index/toInfo";
    });
    $("#uploadSubmit").attr("disabled",false);
}