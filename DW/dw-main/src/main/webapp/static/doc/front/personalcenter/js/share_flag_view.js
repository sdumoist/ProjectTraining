layui.use(['form', 'jquery', 'util', 'layer', 'table', 'Hussar', 'jstree', 'HussarAjax', 'element', 'laydate', 'upload'], function () {
    var form = layui.form,
        $ = layui.jquery,
        layer = layui.layer,
        Hussar = layui.Hussar,
        $ax = layui.HussarAjax;
    var shareFlag = $("[name=shareFlag]").attr("checked") == "checked" ? '1' : '0';
    /*关闭弹窗*/
    $("#cancel").on('click', function () {
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    form.on('switch(shareFlag)', function (data) {
        var x = data.elem.checked;//判断开关状态
        if (x == true) {
            shareFlag = '1'
        } else {
            shareFlag = '0'
        }

    });
    /*新增/编辑敏感词*/
    $("#saveBtn").on('click', function () {
        var docId = $("#docId").val().trim();
        var docIds = $("#docIds").val().trim();
        if(docId != '-1') {
            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/fsFile/setShareFlag",
                async:false,
                data: {
                    docId: docId,
                    shareFlag: shareFlag
                },
                success: function (result) {
                    var msg = "";
                    var scrollHeightLong = 0;
                    var scrollHeight = parent.scrollHeight;
                    scrollHeightLong = parseInt(scrollHeight.substring(0, scrollHeight.indexOf("px"))) + 200 + "px";
                    if (result) {
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                        msg = "设置成功";
                        parent.layer.msg(msg, {icon: 1, offset: parent.scrollHeightMsg});
                        if (parent.htmlFlag == 'upload'){
                            parent.refreshFile(parent.currPage,parent.openFileId);
                        }else {
                            parent.refreshFile(parent.openFileId);
                        }
                    } else {
                        msg = "设置失败";
                        layer.msg(msg, {icon: 2});
                    }
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/setShareFlag", function(result) {
                var msg = "";
                var scrollHeightLong = 0;
                var scrollHeight = parent.scrollHeight;
                scrollHeightLong = parseInt(scrollHeight.substring(0, scrollHeight.indexOf("px"))) + 200 + "px";
                if (result) {
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index);
                    msg = "设置成功";
                    parent.layer.msg(msg, {icon: 1, offset: parent.scrollHeightMsg});
                    if (parent.htmlFlag == 'upload'){
                        parent.refreshFile(parent.currPage,parent.openFileId);
                    }else {
                        parent.refreshFile(parent.openFileId);
                    }
                } else {
                    msg = "设置失败";
                    layer.msg(msg, {icon: 2});
                }
            }, function(data) {

            });
            ajax.set("docId",docId);
            ajax.set("shareFlag",shareFlag);
            ajax.start();
        } else {
            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/fsFile/setShareFlags",
                async:false,
                data: {
                    docIds: docIds,
                    shareFlag: shareFlag
                },
                success: function (result) {
                    var msg = "";
                    if (result) {
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                        msg = "设置成功";
                        parent.layer.msg(msg, {icon: 1, offset: parent.scrollHeightMsg});
                    } else {
                        msg = "设置失败";
                        layer.msg(msg, {icon: 2});
                    }
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/setShareFlags", function(result) {
                var msg = "";
                if (result) {
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index);
                    msg = "设置成功";
                    parent.layer.msg(msg, {icon: 1, offset: parent.scrollHeightMsg});
                    if (parent.htmlFlag == 'upload'){
                        parent.refreshFile(parent.currPage,parent.openFileId);
                    }else {
                        parent.refreshFile(parent.openFileId);
                    }
                } else {
                    msg = "设置失败";
                    layer.msg(msg, {icon: 2});
                }
            }, function(data) {

            });
            ajax.set("docIds",docIds);
            ajax.set("shareFlag",shareFlag);
            ajax.start();
        }
    });
});
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}