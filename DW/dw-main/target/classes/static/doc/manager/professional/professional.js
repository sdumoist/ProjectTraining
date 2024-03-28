var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var dataObj;
var index;
layui.use(['jquery',  'tree', 'table', 'util', 'laytpl', 'form', 'layer','Hussar','HussarAjax'], function () {
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
    var ProfessionalBin = {
        tableId: "ProfessionalBinTable",	//表格id
        seItem: null,		//选中的条目
    };
    //初始化表格
    ProfessionalBin.initTableView = function () {
        tableIns =table.render({
            elem: '#ProfessionalTable' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: Hussar.ctxPath+'/professional/list' //数据接口
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
            , cols: [[
                {field: 'id', title: 'id', type: 'checkbox', width: '5%', align: "center"},
                {type: 'numbers', title: '序号', align: 'center', width: '5%'},
                {field: 'major', title: '专业', width: '30%', align: "center", event: 'opeView', style: 'cursor: pointer;color:#00a4ff'},
                {field: 'userName', title: '专职', align: "left"}
            ]] //设置表头
        });
        //.监听选择，记录已选择项
        table.on('checkbox(ProfessionalTable)', function (obj) {
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
                        key: v.id, value: v
                    });
                } else {
                    //.删除
                    layui.data('checked', {
                        key: v.id, remove: true
                    });
                }
            });
        });
        table.on('tool(ProfessionalTable)', function (obj) {
            if (obj.event === 'opeView') {
                buttonType = 'viewTopic';
                openView('查看专业专职', '/professional/detail', 760, 440, obj);
            }
        });
    }
    /*打开专业专职维护*/
    function openTopic(title, url, w, h) {
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
            layer.alert('请先选择一条要修改的专业专职', {
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
        id = dataArr[0].id;
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: Hussar.ctxPath+url + "/" + id
        });
    }

    /*打开查看页面*/
    function openView(title, url, w, h, obj) {
        dataObj = obj;
        var dataArr = active.getThisData(obj);
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
            content: Hussar.ctxPath+url + "/" + obj.data.id
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
            content: Hussar.ctxPath+url + "?id=" + obj.data.id,
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
            var majorId = $("#majorId").val().trim();
            var userName = $("#userName").val().trim();
            tableIns.reload({
                page: {
                    curr: 1
                },
                where: {
                    majorId: majorId,
                    userName: userName,
                    //防止IE浏览器第一次请求后从缓存读取数据
                    timestamp: (new Date()).valueOf()
                }
            })
        }
        , addProfessional: function () {
            buttonType = 'addProfessional';
            openTopic('新增专业专职', '/professional/professional_add', 670, 440);//为ie兼容改为670
        }
        , editProfessional: function () {
            buttonType = 'editProfessional';
            openEdit('专业专职修改', '/professional/professional_update', 670, 440);
        }, moveUp: function () {
            if(active.moveUpAble) return;
            active.moveUpAble = true;
            var dataArr = active.getCheckData();
            if(dataArr.length != 1){
                layer.alert('请选择一条要上移的专业专职', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            // var index = dataArr[0].LAY_TABLE_INDEX;    //选中行的序号

            var data =  table.cache["ProfessionalTable"];

            var $tr = $("tr[data-index = "+(index-1)+"]");     //选中的行
            var upNum = index-2;                //上一行的序号
            if ($tr.index() != 0) {

                var ajax = new $ax(Hussar.ctxPath + "/topic/moveTopic", function(result) {
                    if(result > 0){
                        tableIns.reload({
                            where:{
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            },
                            done: function(res, curr, count){       //刷新列表后重新选中之前的行
                                var td = $('#ProfessionalTable').next().find("tr[data-index='"+upNum+"'] div.layui-form-checkbox");
                                td.click();
                                active.moveUpAble = false;
                            }
                        });
                        layui.data('checked', null);
                    }

                }, function(data) {
                    active.moveUpAble = false;

                });
                ajax.set("table","qa_professional");
                ajax.set("idColumn","ID");
                ajax.set("idOne",dataArr[0].id);
                ajax.set("idTwo",data[upNum].id);
                ajax.start();
            }else{
                layer.alert('已经上移到最顶端', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                active.moveUpAble = false;
            }
        }, moveDown: function () {
            if(active.moveDownAble) return;
            active.moveDownAble = true;
            var dataArr = active.getCheckData();
            if(dataArr.length != 1){
                layer.alert('请选择一条要下移的专业专职', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            //var index = dataArr[0].LAY_TABLE_INDEX;
            var downNum = index;
            var data =  table.cache["ProfessionalTable"];
            var $tr = $("tr[data-index = "+(index-1)+"]");
            var next = $tr.next();
            if(next.length == 0){
                layer.alert('已经下移到最底端', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                active.moveDownAble = false;
                return;
            }
            if(next){
                var ajax = new $ax(Hussar.ctxPath + "/topic/moveTopic", function(result) {
                    if(result > 0){
                        tableIns.reload({
                            where:{
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            },
                            done: function(res, curr, count){
                                var td = $('#ProfessionalTable').next().find("tr[data-index='"+downNum+"'] div.layui-form-checkbox");
                                td.click();
                                active.moveDownAble = false;
                            }
                        });
                        layui.data('checked', null);

                    }
                }, function(data) {
                    active.moveDownAble = false;
                });
                ajax.set("table","qa_professional");
                ajax.set("idColumn","ID");
                ajax.set("idOne",dataArr[0].id);
                ajax.set("idTwo",data[downNum].id);
                ajax.start();

                /*$tr.fadeOut().fadeIn();
                 $tr.next().after($tr);*/
            }
        }
        , delProfessional: function () {
            var dataArr = active.getCheckData();
            if (dataArr.length == 0) {
                layer.alert('请先选择要删除的专业专职', {
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
                    key: dataArr[i].id, remove: true
                });
                if (i == 0) {
                    ids = dataArr[i].id;
                } else {
                    ids += ',' + dataArr[i].id;
                }
            }
            layer.confirm('确定要删除所选中的专业专职吗？', function () {
                var ajax = new $ax(Hussar.ctxPath + "/professional/delete", function(data) {
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
        ,
        getCheckData: function () { //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            var checkStatus = table.checkStatus('ProfessionalTable'), mySelected = checkStatus.data;

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
        ProfessionalBin.initTableView();//初始化表格
        $(window).resize(function() {
            ProfessionalBin.initTableView();
        });
    });
});
