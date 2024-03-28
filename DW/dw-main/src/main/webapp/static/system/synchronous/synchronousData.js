layui.use(['jquery', 'layer', 'Hussar'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        Hussar = layui.Hussar,
        $ax = layui.HussarAjax;

    // 同步用户
    $("#synchronousUser").on('click', function () {
        var index = layer.load(1, {
            shade: [0.1, '#fff'] //0.1透明度的白色背景
        });
        var ajax = new $ax(Hussar.ctxPath + "/synchronousData/getYyzcUser", function (data) {
            $("#synresult").val(data.msg);
        }, function (data) {

        });
        ajax.start();
        layer.close(index);
    });
    // 同步组织机构
    $("#synchronousOrganise").on('click', function () {
        var index = layer.load(1, {
            shade: [0.1, '#fff'] //0.1透明度的白色背景
        });
        var ajax = new $ax(Hussar.ctxPath + "/synchronousData/getYyzcOrganise", function (data) {
            $("#synresult").val(data.msg);
        }, function (index) {

        });
        ajax.start();
        layer.close(index);
    });
    // 同步用户头像
    $("#synchronousHeadPhoto").on('click', function () {
        var index = layer.load(1, {
            shade: [0.1, '#fff'] //0.1透明度的白色背景
        });
        var ajax = new $ax(Hussar.ctxPath + "/synchronousData/getYyzcHeadPhoto", function (data) {
            $("#synresult").val(data.msg);
        }, function (data) {

        });
        ajax.start();
        layer.close(index);
    });
});