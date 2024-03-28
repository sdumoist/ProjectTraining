layui.use(['jquery', 'layer', 'Hussar', 'upload'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        Hussar = layui.Hussar,
        upload = layui.upload,
        $ax = layui.HussarAjax;

    // 导入用户
    upload.render({
        elem: '#importUser'
        , url: Hussar.ctxPath + "/importOrgan/user"
        , multiple: false
        , accept: 'file'//允许上传的文件类型"
        , exts: 'xls|xlsx'
        , before: function (obj) {
            $("#importresult").val("");
        }
        , done: function (res, index, upload) {
            if (res.code === "0") {
                $("#importresult").val(res.msg)
            }else{
                if (res.exceptionList !== undefined && res.exceptionList != "") {
                    res.exceptionList.forEach(function (item, index) {
                        if (index + 1 < res.exceptionList.length) {
                            res.exceptionList[index] += ";   ";
                        }
                    });
                    res.exceptionList.unshift("导入失败!;   ");
                    var msg = res.exceptionList.join('');
                    $("#importresult").val(msg);
                } else {
                    $("#importresult").val(res.msg)
                }
            }
        }
    });

    // 导入组织机构
    upload.render({
        elem: '#importOrgan'
        , url: Hussar.ctxPath + "/importOrgan/organ"
        , multiple: false
        , accept: 'file'//允许上传的文件类型"
        , exts: 'xls|xlsx'
        , before: function (obj) {
            $("#importresult").val("");
        }
        , done: function (res, index, upload) {
            debugger
            if (res.code === "0") {
                $("#importresult").val(res.msg)
            }else{
                if (res.exceptionList !== undefined && res.exceptionList != "") {
                    res.exceptionList.forEach(function (item, index) {
                        if (index + 1 < res.exceptionList.length) {
                            res.exceptionList[index] += ";   ";
                        }
                    });
                    res.exceptionList.unshift("导入失败!;   ");
                    var msg = res.exceptionList.join('');
                    $("#importresult").val(msg);
                } else {
                    $("#importresult").val(res.msg)
                }
            }
        }
    });
});