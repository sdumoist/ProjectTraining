var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var dataObj;
var index;
layui.use(['jquery',  'tree', 'table', 'util', 'laytpl', 'form', 'layer','Hussar','HussarAjax'],function (){
    var $ = layui.jquery,
        table = layui.table,
        laytpl = layui.laytpl,
        form = layui.form,
        layer = layui.layer,
        util = layui.util;
    var Hussar = layui.Hussar,
        $ax = layui.HussarAjax;
    //.存储当前页数据集
    var pageData = [];
    //.存储已选择数据集，用普通变量存储也行
    layui.data('checked', null);
    var PlatformSystemInfoBin = {
        tableId: "platformSystemInfoBinTable",	//表格id
        seItem: null,		//选中的条目
    };
    //初始化表格
    PlatformSystemInfoBin.initTableView = function (){
        tableIns =table.render({
            elem: '#platformSystemInfoList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: Hussar.ctxPath+'/platformSystemInfo/getPlatformSystemInfoList' //数据接口
            , request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            , page: true //开启分页
            ,where: {
                //防止IE浏览器第一次请求后从缓存读取数据
                timestamp: (new Date()).valueOf()
            }
            ,even:true
            , cols:[[
                {type: 'checkbox', width: '5%', align: "center"},
                {type: 'numbers', title: '序号', align: 'center', width: '5%'},
                {field: 'systemId', title: '系统ID', width: '25%', align: "center"},
                {field: 'systemName', title: '系统名称', width: '20%', align: "center"},
                {field: 'systemKey', title: '系统key密码', width: '25%', align: "center"},
                {field: 'createUserName', title: '操作者名称', align: "center"}
            ]]//设置表头
        });
        //.监听选择，记录已选择项
        table.on('checkbox(platformSystemInfoList)',function (obj){
            if (obj.checked){
                index = $("tr").index(obj.tr);
            }
            //.全选或单选数据集不一样
            var data = obj.type == 'one' ? [obj.data] : pageData;
            //.遍历数据
            $.each(data, function (k, v) {
                //.假设你数据中 id 是唯一关键字
                if (obj.checked) {
                    //.增加已选中项
                    layui.data('checked', {
                        key: v.systemId, value: v
                    });
                } else {
                    //.删除
                    layui.data('checked', {
                        key: v.systemId, remove: true
                    });
                }
            });
        });
    }
    /*新增系统接入配置*/
    function openPlatformSystemInfo(title, url, w, h) {
        if (title == null || title == '') {
            title = false;
        };
        if (url == null || url == '') {
            url = "404.jsp";
        };
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        };
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        };
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: true,
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
            layer.alert('请先选择一条要修改的系统接入配置记录', {
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
        };
        if (url == null || url == '') {
            url = "404.jsp";
        };
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        };
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        };
        systemId = dataArr[0].systemId;
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: Hussar.ctxPath+url + "?systemId=" + systemId
        });
    }

    function openList(title, url, w, h, obj) {
        dataObj = obj;
        var dataArr = active.getThisData(obj);
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
            maxmin: true,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: Hussar.ctxPath+url + "?systemId=" + obj.data.systemId,
            end: function () {
                location.reload({
                    where: {
                        //防止IE浏览器第一次请求后从缓存读取数据
                        timestamp: (new Date()).valueOf()
                    }
                });
            }
        });
    }

    active = {
        moveUpAble: false,
        moveDownAble: false,
        reload: function () {
            var systemName = $("#systemName").val().trim();
            //系统名称
            var pattern = new RegExp("^[^/\\\\:\\*\\'\\‘\\?\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if (!pattern.test(systemName)) {
                layer.alert('输入的系统名称不合法', {
                    icon: 0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            tableIns.reload({
                page: {
                    curr: 1
                },
                where: {
                    systemName: systemName,
                    //防止IE浏览器第一次请求后从缓存读取数据
                    timestamp: (new Date()).valueOf()
                }
            })
        }
        , addPlatformSystemInfo: function () {
            buttonType = 'addPlatformSystemInfo';
            openPlatformSystemInfo('新增系统接入配置', '/platformSystemInfo/platformSystemInfoAdd', 500, 250);//为ie兼容改为670
        }
        , editPlatformSystemInfo: function () {
            buttonType = 'editPlatformSystemInfo';
            openEdit('系统接入配置修改', '/platformSystemInfo/platformSystemInfoEdit', 500, 250);
        }
        , delPlatformSystemInfo: function () {
            var dataArr = active.getCheckData();
            if (dataArr.length == 0) {
                layer.alert('请先选择要删除的系统接入配置', {
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
                    key: dataArr[i].systemId, remove: true
                });
                if (i == 0) {
                    ids = dataArr[i].systemId;
                } else {
                    ids += ',' + dataArr[i].systemId;
                }
            }
            layer.confirm('确定要删除所选中的系统接入配置吗？', function () {
                var ajax = new $ax(Hussar.ctxPath + "/platformSystemInfo/delPlatformSystemInfoByIds", function(data) {
                    if (data) {
                        layer.alert('删除成功', {
                            icon: 1,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示'
                        }, function () {
                            tableIns.reload({
                                where:{
                                    //防止IE浏览器第一次请求后从缓存读取数据
                                    timestamp: (new Date()).valueOf()
                                },
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
                            var index = layer.alert();
                            layer.close(index);
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
        }
        , getCheckData: function () { //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            var checkStatus = table.checkStatus('platformSystemInfoList'), mySelected = checkStatus.data;
            return mySelected;
        }, getThisData: function () {
            return dataObj;
        },getMoveCheckData: function(){ //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            $.each(layui.data('checked'), function(k, v) {
                mySelected.push(v);
            });
            return mySelected;
        }
    };
    //.渲染完成回调
    $('.layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    $(function () {
        PlatformSystemInfoBin.initTableView();//初始化表格
        $(window).resize(function() {
            PlatformSystemInfoBin.initTableView();
        });
    });
});