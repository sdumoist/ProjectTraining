var index = 0;
var idArr = [];
var nameArr = [];
var typeArr = [];
layui.use(['upload', 'form','Hussar','HussarAjax'], function () {

    var form = layui.form;
    var $ = layui.jquery
        , upload = layui.upload;
    var Hussar = layui.Hussar,
        $ax = layui.HussarAjax;
    var componentId = $("#componentId").val();
    //普通图片上传

    $("#cancel").on('click', function () {
        var url = "/component/componentListView";
        window.location.href = encodeURI(Hussar.ctxPath+url);
        top.reloadComponent();
        top.HussarTab.tabDelete("3");
    })
    $("#pass").on('click', function () {
        var componentId = $("#componentId").val().trim();//专题名称

            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/component/componentPass",
                data: {
                    componentId: componentId

                },
                async: false,
                cache: false,
                dataType: "json",
                success: function (data) {
                    if (data.result == "1") {
                        layer.alert("认定成功", {
                            icon: 1,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示',
                            end: function () {
                                var index = parent.layer.getFrameIndex(window.name);
                                parent.layer.close(index);//关闭当前页
                                parent.layui.table.reload('topicList', {page: {curr: 1}});
                                /!*  var url="/component/componentListView";
                                 window.location.href=encodeURI(url);*!/
                            }
                        }, function () {
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);//关闭当前页
                            parent.layui.table.reload('topicList', {page: {curr: 1}});
                            /!*  var url="/component/componentListView";
                             window.location.href=encodeURI(url);*!/
                        });
                    } else {
                        layer.alert("认定失败", {
                            icon: 2,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示'
                        });
                    }
                }
            })*/
        var ajax = new $ax(Hussar.ctxPath + "/component/componentPass", function(data) {
            if (data.result == "1") {
                layer.alert("认定成功", {
                    icon: 1,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    end: function () {
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);//关闭当前页
                        parent.layui.table.reload('topicList', {page: {curr: 1}});
                        /*  var url="/component/componentListView";
                         window.location.href=encodeURI(url);*/
                    }
                }, function () {
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index);//关闭当前页
                    parent.layui.table.reload('topicList', {page: {curr: 1}});
                    /*  var url="/component/componentListView";
                     window.location.href=encodeURI(url);*/
                });
            } else {
                layer.alert("认定失败", {
                    icon: 2,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
            }
        }, function(data) {

        });
        ajax.set("componentId",componentId);
        ajax.start();
        })

    $("#back").on('click', function () {

        var componentId = $("#componentId").val().trim();//专题名称
        layer.open({
            type: 1,
            btn: ['驳回', '取消'],
            area: ['60%', '300px'],
            skin: 'confirm-class',
            fix: false, //不固定
            maxmin: false,
            shadeClose: false,
            shade: 0.4,
            title: ["驳回意见",'background-color:#fff'],
            content: $('#addDiv'),
            btn1: function (index, layero) {
                var reason = $("#reason").val().trim();//专题名称
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/component/componentBack",
                    data: {
                        componentId: componentId,
                        reason: reason
                    },
                    async: false,
                    cache: false,
                    dataType: "json",
                    success: function (data) {
                        if (data.result == "1") {
                            layer.alert("驳回成功", {
                                icon: 1,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示',
                                end: function () {
                                    var index = parent.layer.getFrameIndex(window.name);
                                    parent.layer.close(index);//关闭当前页
                                    parent.layui.table.reload('topicList',{page:{curr:1}});
                                    /!* var url="/component/componentListView";
                                     window.location.href=encodeURI(url);*!/
                                }
                            }, function () {
                                var index = parent.layer.getFrameIndex(window.name);
                                parent.layer.close(index);//关闭当前页
                                parent.layui.table.reload('topicList',{page:{curr:1}});
                                /!* var url="/component/componentListView";
                                 window.location.href=encodeURI(url);*!/
                            });
                        } else {
                            layer.alert("驳回失败", {
                                icon: 2,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示'
                            });
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/component/componentBack", function(data) {
                    if (data.result == "1") {
                        layer.alert("驳回成功", {
                            icon: 1,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示',
                            end: function () {
                                var index = parent.layer.getFrameIndex(window.name);
                                parent.layer.close(index);//关闭当前页
                                parent.layui.table.reload('topicList',{page:{curr:1}});
                                /* var url="/component/componentListView";
                                 window.location.href=encodeURI(url);*/
                            }
                        }, function () {
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);//关闭当前页
                            parent.layui.table.reload('topicList',{page:{curr:1}});
                            /* var url="/component/componentListView";
                             window.location.href=encodeURI(url);*/
                        });
                    } else {
                        layer.alert("驳回失败", {
                            icon: 2,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示'
                        });
                    }
                }, function(data) {

                });
                ajax.set("componentId",componentId);
                ajax.set("reason",reason);
                ajax.start();
            },
        });

    })

    /*新增/编辑专题*/

})
function download(id) {
    //cancelBubble();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        $.ajaxFileUpload({
            url: Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
            type: "post",
            async: false,

        });
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showPdf(id, name) {
    //cancelBubble();
    var dot = name.lastIndexOf(".");//获取"."的下标
    var fileSuffixName = name.substring(dot, name.length);//带 "."
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                if (data.code == 1) {
                    openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
                } else if (data.code == 2) {
                    openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
                } else if (data.code == 3) {
                    openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
                } else if (data.code == 4) {
                    openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
                } else {
                    layer.msg("此文件类型不支持预览。", {anim: 6, icon: 0, offset: scrollHeightMsg});
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if (data.code == 1) {
                openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
            } else if (data.code == 2) {
                openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
            } else if (data.code == 3) {
                openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
            } else if (data.code == 4) {
                openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
            } else {
                layer.msg("此文件类型不支持预览。", {anim: 6, icon: 0, offset: scrollHeightMsg});
            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
    });

}

