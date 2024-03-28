var dbclickover = false;
var clickFlag=false;
layui.use(['jquery',  'tree', 'table', 'util', 'laytpl', 'form', 'layer', 'element','Hussar','HussarAjax'], function () {
    var table = layui.table;
    var Hussar = layui.Hussar,
        $ax = layui.HussarAjax;
    var RuleBin = {
        tableId: "RuleBinTable",	//表格id
        seItem: null		//选中的条目
    };

    //初始化表格
    RuleBin.initTableView = function () {
        tableIns =table.render({
            elem: '#ruleList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: Hussar.ctxPath+'/integralRule/list'//数据接口
            , request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            , page: true //开启分页
            ,even:true
            , cols: [[
                {field: 'ruleId', title: 'id', type: 'checkbox', width: '4%', align: "center"},
                {type: 'numbers', title: '序号', align: 'center', width: '5%'},
                {field: 'ruleName', title: '规则名称', align:'left', width: '13%'},
                {field: 'ruleCode', title: '规则编码', align: "left", width:'13%'},
                {field: 'valid', title: '有效性', align: 'center', width: '10%', templet:'#valid'},
                {field: 'integral', title: '规则积分', align: 'center', width: '8%'},
                {field: 'maxTimes', title: '每日上限', align: 'center', width: '9%',templet:'#maxTimes'},
                {field: 'des', title: '描述', align: 'left', width: '25%'},
                {field: 'remark', title: '备注', align: 'left', width: '14%'}
            ]] //设置表头
        });
    };

    active = {
        getCheckData: function () { //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            var checkStatus = table.checkStatus('ruleList'), mySelected = checkStatus.data;
            return mySelected;
        }
    };
    RuleBin.initButton = function() {
        $("#addBtn").click(function () {
            openAdd('新增规则', '/integralRule/addView', 620, 450);
        });
        $("#editBtn").click(function () {
            openEdit('编辑规则', '/integralRule/editView', 620, 450);
        });
        $("#delBtn").click(function () {
            var dataArr = active.getCheckData();
            if (dataArr.length == 0) {
                layer.alert('请先选择要删除的积分规则', {
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
                    key: dataArr[i].ruleId, remove: true
                });
                if (i == 0) {
                    ids = dataArr[i].ruleId;
                } else {
                    ids += ',' + dataArr[i].ruleId;
                }
            }
            layer.confirm('确定要删除所选中的规则吗？', function () {
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/integralRule/delete",
                    data: {
                        ids: ids
                    },
                    async: false,
                    cache: false,
                    contentType : "application/x-www-form-urlencoded;charset=UTF-8",
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
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/integralRule/delete", function(data) {
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
                }, function(data) {

                });
                ajax.set("ids",ids);
                ajax.start();
            })
        });
    };
    window.initTableView = function () {
        RuleBin.initTableView();
    };
    /*打开敏感词编辑*/
    function openAdd(title, url, w, h) {
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
            content: Hussar.ctxPath+url
        });
    }

    /*打开编辑页面*/
    function openEdit(title, url, w, h) {
        var dataArr = active.getCheckData();
        if (dataArr.length != 1) {
            layer.alert('请先选择一条要修改的积分规则', {
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
        ruleId = dataArr[0].ruleId;
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: Hussar.ctxPath+url + "?ruleId=" + ruleId
        });
    }
    $(function () {
        RuleBin.initTableView();//初始化表格
        RuleBin.initButton();
        $(window).resize(function() {
            RuleBin.initTableView();
        });
    });
});