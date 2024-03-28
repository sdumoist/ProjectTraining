

layui.use(['layedit','layer','Hussar','HussarAjax','form'], function() {
    var layedit = layui.layedit
        ,layer = layui.layer
        , $ = layui.jquery,
        Hussar = layui.Hussar
        ,form = layui.form
        ,$ax = layui.HussarAjax;




    //点击提交知识库 新增
    $("#knowlegeSubmit").on('click', function() {
        var title = $("#title").val();
        var label = $("#label").val();
        var content = layedit.getContent(index); //获得编辑器的内容
        var contentText = layedit.getText(index); //获得编辑器的纯文本内容

        // 特殊字符校验
        var reg = new RegExp(/<[^>]+>/gi);
        if(reg.test(title)){
            layer.msg('标题不能包含特殊字符!');
            $("#questionAdd").attr("disabled",false);
            return false;
        }
        if(reg.test(label)){
            layer.msg('标签不能包含特殊字符!');
            $("#questionAdd").attr("disabled",false);
            return false;
        }

        if (title == '' || title == null || title == undefined){
            layer.msg('标题不能为空');
            return false;
        }
        if (label == '' || label == null || label == undefined){
            layer.msg('标签不能为空');
            return false;
        }
        if (content == '' || content == null || content == undefined){
            layer.msg('内容不能为空');
            return false;
        }
        var ajax = new $ax(Hussar.ctxPath + "/knowledge/add", function(data) {
            // 清空富文本框数据
            $("#response").click();
            $(".layui-layedit").remove()
            index = layedit.build('LAY_demo1');
            Hussar.success("加入成功");
            form.render();
            setTimeout(function () {
                closeWindow()
            }, 1000)

        }, function(data) {

        });
        ajax.set("title",title);
        ajax.set("label",label);
        ajax.set("content",content);
        ajax.set("text",contentText);
        ajax.set("inputType",'1');
        ajax.set("queId",null);
        ajax.start();
    })


    //点击提交知识库 修改
    $("#knowlegeUpdate").on('click', function() {
        var knowId = $("#knowId").val();
        var title = $("#title").val();
        var label = $("#label").val();
        var content = layedit.getContent(index); //获得编辑器的内容
        var contentText = layedit.getText(index); //获得编辑器的纯文本内容

        // 特殊字符校验
        var reg = new RegExp(/<[^>]+>/gi);
        if(reg.test(title)){
            layer.msg('标题不能包含特殊字符!');
            $("#questionAdd").attr("disabled",false);
            return false;
        }
        if(reg.test(label)){
            layer.msg('标签不能包含特殊字符!');
            $("#questionAdd").attr("disabled",false);
            return false;
        }

        if (title == '' || title == null || title == undefined){
            layer.msg('标题不能为空');
            return false;
        }
        if (label == '' || label == null || label == undefined){
            layer.msg('标签不能为空');
            return false;
        }
        if (content == '' || content == null || content == undefined){
            layer.msg('内容不能为空');
            return false;
        }
        var ajax = new $ax(Hussar.ctxPath + "/knowledge/edit", function(data) {
            // 清空富文本框数据
            $("#response").click();
            $(".layui-layedit").remove()
            index = layedit.build('LAY_demo1');
            Hussar.success("修改成功");
            form.render();
            setTimeout(function () {
                closeWindow()
            }, 1000)
        }, function(data) {

        });
        ajax.set("knowId",knowId);
        ajax.set("title",title);
        ajax.set("label",label);
        ajax.set("content",content);
        ajax.set("text",contentText);
        ajax.start();
    })




    //页面初始化
    $(function () {
        //框字数显示及控制
        window.wordLeg = function (obj) {
            var currleg = $(obj).val().length;
            var length = $(obj).attr('maxlength');
            if (currleg > length) {
                layer.msg('字数请在' + length + '字以内');
            } else {
                $('.text_count').text(currleg);
            }
        }
        // 定义上传图片接口
        layedit.set({
            uploadImage: {
                url: Hussar.ctxPath+'/qaFile/uploadPic' //接口url

            }
        });
        //构建一个默认的编辑器
        index = layedit.build('LAY_demo1');
    });
})

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