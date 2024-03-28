
var pics = [];
var videos = [];

var picDataIds = []; // 图片的前台上传ID
var picIds = []; // 图片数据库ID
var videoDataIds = []; // 视频的前台上传ID
var videoIds = []; // 视频数据库ID
var uploadMg = {}
layui.use(['form','element','Hussar', 'laypage', 'jquery','layer','laytpl','upload'], function(){
    var $ = layui.jquery
        ,form = layui.form
        ,laypage = layui.laypage
        ,laytpl = layui.laytpl
        ,Hussar = layui.Hussar
        ,layer = layui.layer
        ,$ax = layui.HussarAjax
        ,upload = layui.upload
        ,element = layui.element; //Tab的切换功能，切换事件监听等，需要依赖element模块


    //评论框字数显示及控制
    window.wordLeg = function (obj) {
        var currleg = $(obj).val().length;
        var length = $(obj).attr('maxlength');
        if (currleg > length) {
            layer.msg('字数请在' + length + '字以内');
        } else {
            $('.text_count').text(currleg);
        }
    };
    window.autoHeight = function (obj) {
        var preObj = $(obj).parent().children('pre');
        preObj[0].innerHTML = $(obj)[0].value;
    };
    uploadMg.initPicUpload = function () {
        //普通图片上传
        upload.render({
            elem: '#picFile'
            ,url: Hussar.ctxPath+'/qaFile/uploadJson'
            ,accept : 'images'
            ,data : {
                type : 1,
                fileName : ''
            }
            ,before: function(obj){
                //预读本地文件示例，不支持ie8
                obj.preview(function(index, file, result){
                    darwPic(index, file, result);
                });
            }
            ,done: function(res, index, upload){

                //如果上传失败
                if(res.code !="0"){
                    return layer.msg('上传失败');
                } else {
                    picIds.push(res.data.fileId)
                }
            }
        });
    }

    uploadMg.initVideoUpload = function () {
        //普通图片上传
        upload.render({
            elem: '#videoFile'
            ,url: Hussar.ctxPath+'/qaFile/uploadJson'
            ,accept : 'video'
            ,data : {
                type : 1,
                fileName : ''
            }
            ,before: function(obj){
                //预读本地文件示例，不支持ie8
                obj.preview(function(index, file, result){
                    darwvideo(index, file, result);
                });
            }
            ,done: function(res, index, upload){

                //如果上传失败
                if(res.code !="0"){
                    return layer.msg('上传失败');
                } else {
                    videoIds.push(res.data.fileId)
                }
            }
        });
    }


    //点击提交回答
    $("#questionAdd").on('click', function() {
        $("#questionAdd").attr("disabled",true);
        var title = $("#title").val(); // 标题
        var supplement = $("#supplement").val(); // 问题补充
        var majorId = $("#majorId").val(); // 分类
        var label = $("#label").val(); // 标签
        var authority = $('input:radio[name="authority"]:checked').val(); // 可回复者

        var text = null // 问题补充纯文本
        if(supplement != '' && supplement != null && supplement != undefined){
            text = supplement;
            if(picDataIds.length > 0){
                for(var i = 0;i<picDataIds.length;i++){
                    var b = "[img" + picDataIds[i]  + "]";
                    if(supplement.indexOf(b) != -1){
                        supplement = supplement.split(b).join('[img'+picIds[i]+']');
                        text = text.split(b).join('');
                    }
                }
            }
            if(videoDataIds.length > 0){
                for(var i = 0;i<videoDataIds.length;i++){
                    var b = "[vid" + videoDataIds[i]  + "]";
                    if(supplement.indexOf(b) != -1){
                        supplement = supplement.split(b).join('[vid'+videoIds[i]+']');
                        text = text.split(b).join('');
                    }
                }
            }
        }
        // 特殊字符校验
        var reg = new RegExp(/<[^>]+>/gi);
        if(reg.test(title)){
            layer.msg('问题的标题不能包含特殊字符!');
            $("#questionAdd").attr("disabled",false);
            return false;
        }
        if(reg.test(label)){
            layer.msg('标签不能包含特殊字符!');
            $("#questionAdd").attr("disabled",false);
            return false;
        }

        if (title == '' || title == null || title == undefined){
            layer.msg('您的问题不能为空');
            $("#questionAdd").attr("disabled",false);
            return false;
        }
        if(authority == '2'){
            if (majorId == '' || majorId == null || majorId == undefined){
                layer.msg('分类不能为空');
                $("#questionAdd").attr("disabled",false);
                return false;
            }
        }
        var majorName = $("#majorId :selected").text();
        if (label == '' || label == null || label == undefined){
            layer.msg('标签不能为空');
            $("#questionAdd").attr("disabled",false);
            return false;
        }


        var ajax = new $ax(Hussar.ctxPath + "/question/add", function(data) {
            Hussar.success("提问成功");
            setTimeout(function () {
                $("#questionAdd").attr("disabled",false);
                closeWindow()
            }, 1000)
        }, function(data) {

        });
        ajax.set("title",title);
        ajax.set("supplement",supplement);
        ajax.set("text",text);
        ajax.set("rewardPoinits","0");
        ajax.set("label",label);
        ajax.set("answerFlag",authority);
        ajax.set("majorId",majorId);
        ajax.set("majorName",majorName);
        ajax.start();
    })

    /**
     * 点击单选框按钮 显隐分类必填
     * 2: 领域专家 1:全体人员
     */
    form.on('radio(objectType)', function(data){
        if(data.value == 2){
            $(".classification").show();
            $(".ceshi6").show();
        }else{
            $(".classification").hide();
            $(".ceshi6").hide();
        }
    });

    //页面初始化
    $(function () {
        uploadMg.initPicUpload();
        uploadMg.initVideoUpload();
        form.render();
    });
});



function darwPic(index, value, result) {
    //每次都只会遍历一个图片数据
    var div = document.createElement("div"),
        img = document.createElement("img"),
        div2 = document.createElement("div");
    var newContent = document.createTextNode("删除");
    div2.className = "file-delete";
    div2.appendChild(newContent);
    div.className = "file-box";
    var fr = new FileReader();
    fr.onload = function(){
        img.src=result;
        picDataIds.push(value.lastModified);
        var supplement = $("#supplement").val();
        supplement = supplement + "[img" + value.lastModified +"]";
        $("#supplement").val(supplement);

        img.setAttribute("data-name",value.name);
        img.setAttribute("data-id",value.lastModified);
        div.appendChild(img);
        div.appendChild(div2);
        $(".enclosure-container")[0].insertBefore(div,$(".upload-pic")[0]);
        pics.push({file:this,value:value});
        if(pics.length>=8){
            $(".upload-pic").hide();
        }
        // 删除按钮
        $(".file-delete").on('click',function () {
            console.log($(this).parent().children('img'));
            console.log(pics);
            for(var i=0;i<pics.length;i++){
                if(pics[i].value.lastModified === parseInt($(this).parent().children('img')[0].getAttribute('data-id'))){
                    // 同步删除问题补充内的文字
                    var a = $("#supplement").val();
                    var b = "[img" + pics[i].value.lastModified  + "]";
                    if(a.indexOf(b) != -1){
                        var c = a.split(b).join('');
                        $("#supplement").val(c);
                    }
                    pics.splice(i,1);
                    $(this).parent().remove();
                    if(pics.length<8){
                        $(".upload-pic").show();
                    }
                }
            }
        })
    }
    fr.readAsDataURL(value);

    var $input = $("#picFile");
    $input.removeAttr("id");
    //我们做个标记，再class中再添加一个类名就叫test
    var newInput = '<input class="uploadImg"  name="picFile" id="picFile">';
    $(".upload-pic").append($(newInput));
    var $input = $("#picFile");
    uploadMg.initPicUpload();
}

function darwvideo(index, value, result) {
    //每次都只会遍历一个图片数据
    var div = document.createElement("div"),
        video = document.createElement("video"),
        div2 = document.createElement("div");
    var newContent = document.createTextNode("删除");
    div2.className = "file-delete";
    div2.appendChild(newContent);
    div.className = "file-box";
    var fr = new FileReader();
    fr.onload = function(){
        video.src=result;

        videoDataIds.push(value.lastModified);
        var supplement = $("#supplement").val();
        supplement = supplement + "[vid" + value.lastModified +"]";
        $("#supplement").val(supplement);

        video.setAttribute("data-name",value.name);
        div.appendChild(video);
        div.appendChild(div2);
        $(".enclosure-container")[0].insertBefore(div,$(".upload-video")[0]);
        $(".upload-video").hide();
        videos.push({file:this,value:value});
        // 删除按钮
        $(".file-delete").on('click',function () {
            console.log($(this).parent().children('video'));
            $(this).parent().remove();
            $(".upload-video").show();
            videos = [];

            // 同步删除问题补充内的文字
            var a = $("#supplement").val();
            var b = "[vid" + videoDataIds[0]  + "]";
            if(a.indexOf(b) != -1){
                var c = a.split(b).join('');
                $("#supplement").val(c);
            }
            videoDataIds = [];
            videoIds = [];
        })
    }
    fr.readAsDataURL(value);
}

function closeWindow() {
    var userAgent = navigator.userAgent;
    if (userAgent.indexOf("Firefox") != -1 || userAgent.indexOf("Chrome") != -1) {
        location.href = "about:blank";
    } else {
        window.opener = null;
        window.open('', '_self');
    }
    window.close();
}