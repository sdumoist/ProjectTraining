
layui.use(['jquery',  'tree', 'table', 'util', 'laytpl', 'form', 'layer'], function () {
    var table = layui.table;
    var WordsBin = {
        tableId: "WordsBinTable",	//表格id
        seItem: null		//选中的条目
    };

    //初始化表格
    WordsBin.initTableView = function () {
        var word = $("#wordName").val();
        tableIns =table.render({
            elem: '#wordsList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: '/SensitiveWords/list?name=' + word //数据接口
            , request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            , page: true //开启分页
            ,even:true
            , cols: [[
                {field: 'wordId', title: 'id', type: 'checkbox', width: '40', align: "center"},
                {type: 'numbers', title: '序号', align: 'center', width: '50'},
                {field: 'sensitiveWord', title: '敏感词', align: "left", width:'200'},
                {field: 'remark', title: '备注', align:'left',width: '80%'}
            ]] //设置表头
        });
    };

    active = {
        getCheckData: function () { //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            var checkStatus = table.checkStatus('wordsList'), mySelected = checkStatus.data;
            return mySelected;
        }
    };
    WordsBin.initButton = function() {
        $("#addBtn").click(function () {
            openWord('新增敏感词', '/SensitiveWords/wordAdd', 500, 260);
        });
        $("#editBtn").click(function () {
            openEdit('编辑敏感词', '/SensitiveWords/wordUpdate', 500, 260);
        });
        $("#delBtn").click(function () {
            var dataArr = active.getCheckData();
            if (dataArr.length == 0) {
                layer.alert('请先选择要删除的敏感词', {
                    icon: 0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            var ids;
            for (var i = 0; i < dataArr.length; i++) {
                layui.data('checked', {
                    key: dataArr[i].wordId, remove: true
                });
                if (i == 0) {
                    ids = dataArr[i].wordId;
                } else {
                    ids += ',' + dataArr[i].wordId;
                }
            }
            layer.confirm('确定要删除所选中的敏感词吗？', function () {
                $.ajax({
                    type: "post",
                    url: "/SensitiveWords/delete",
                    data: {
                        ids: ids
                    },
                    async: false,
                    cache: false,
                    success: function (data) {
                        if (data == dataArr.length) {
                            layer.alert('删除成功', {
                                icon: 1,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示',
                                end: function (index) {
                                    tableIns.reload({
                                        done: function(res, curr, count){
                                            if (res.data.length == 0&&curr!=1){
                                                tableIns.reload({
                                                    page: {
                                                        curr: curr-1
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    layer.close(index);
                                }
                            });
                        } else {
                            layer.alert('删除失败', {
                                icon: 2,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示'
                            });
                        }
                    }
                })
            })
        });
        $("#searchBtn").click(function () {
            WordsBin.initTableView();
        });
    };
    window.initTableView = function () {
        WordsBin.initTableView();
    }
    /*打开敏感词编辑*/
    function openWord(title, url, w, h) {
        if (title == null || title == '') {
            title = false;
        }
        if (url == null || url == '') {
            url = "404.jsp";
        }
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        }
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        }
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: url
        });
    }

    /*打开编辑页面*/
    function openEdit(title, url, w, h) {
        var dataArr = active.getCheckData();
        if (dataArr.length != 1) {
            layer.alert('请先选择一条要修改的敏感词', {
                icon: 0,
                maxmin: true,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
            return;
        }
        if (title == null || title == '') {
            title = false;
        }
        if (url == null || url == '') {
            url = "404.jsp";
        }
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        }
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        }
        wordId = dataArr[0].wordId;
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: url + "?wordId=" + wordId
        });
    }


    $(function () {
        WordsBin.initTableView();//初始化表格
        WordsBin.initButton();
        $(window).resize(function() {
            WordsBin.initTableView();
        });
    });
});
