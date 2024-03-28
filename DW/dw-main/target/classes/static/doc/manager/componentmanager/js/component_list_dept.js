var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var dataObj;
layui.use(['jquery', 'tree', 'table', 'util', 'laytpl', 'form', 'layer','HussarAjax', 'Hussar'], function () {

    var $ = layui.jquery,
        table = layui.table,
        laytpl = layui.laytpl,
        form = layui.form,
        layer = layui.layer;
    var Hussar = layui.Hussar;
    var $ax = layui.HussarAjax;
    util = layui.util;
    //.存储当前页数据集
    var pageData = [];
    //.存储已选择数据集，用普通变量存储也行
    layui.data('checked', null);
    var TopicBin = {
        tableId: "topicBinTable",	//表格id
        seItem: null,		//选中的条目
    };
    //添加title
    function tdTitle(){
        $('th').each(function(index,element){
            $(element).attr('title',$(element).text());
        });
        $('td').each(function(index,element){
            $(element).attr('title',$(element).text());
        });
    };
    //初始化表格
    TopicBin.initTableView = function () {
        var componentType = $("#componentType").val();
        var componentOrigin = $("#componentOrigin").val();
        var title = $("#title").val();
        var deptName = $("#deptName").val();
        tableIns = table.render({
            elem: '#topicList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: Hussar.ctxPath+'/component/componentListByDept?componentName=' + title + '&componentType=' + componentType
            + '&componentOrigin=' + componentOrigin+'&deptName='+deptName
            , request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            , page: true //开启分页
            , where: {
                //防止IE浏览器第一次请求后从缓存读取数据
                timestamp: (new Date()).valueOf()
            }
            , even: true
            ,done:function(res){
                tdTitle();
            }
            , cols: [[
                {field: 'componentId', title: 'id', type: 'checkbox', width: '4%', align: "center"},
                {type: 'numbers', title: '序号', align: 'center', width: '4%'},
                {field: 'componentName', title: '成果名称', width: '15%', align: "left", event: 'openView', style: 'cursor: pointer;color:#00a4ff'},
                {field: 'componentDescText', title: '成果描述', width: '26%', align: "left"},
                {field: 'organAlias', title: '所属部门', width: '10%', align: "center"},
                {field: 'componentType', title: '成果分类', width: '10%', align: "center", templet: '#type'},
                {field: 'componentOrigin', title: '成果来源', width: '10%', align: "center",templet: '#origin'},
                {field: 'userName', title: '提报人', width: '10%', align: "center"},
                {field: 'createTime', title: '提报时间', width: '12%', align: "center"}
            ]] //设置表头
        });
        //.监听选择，记录已选择项
        table.on('checkbox(topicList)', function (obj) {
            //.全选或单选数据集不一样
            var data = obj.type == 'one' ? [obj.data] : pageData;
            //.遍历数据
            $.each(data, function (k, v) {
                //.假设你数据中 id 是唯一关键字
                if (obj.checked) {
                    var dataArr = active.getCheckData();
                    //.增加已选中项
                    layui.data('checked', {
                        key: v.componentId, value: v
                    });
                } else {
                    var dataArr = active.getCheckData();
                    //.删除
                    layui.data('checked', {
                        key: v.componentId, remove: true
                    });
                }
            });
        });
        table.on('tool(topicList)', function (obj) {
            if (obj.event === 'openView') {
                buttonType = 'viewTopic';
                /*  var url="/component/componentView?componentId="+obj.data.componentId;
                 window.location.href=encodeURI(url);*/
                /*top.HussarTab.tabAdd("组件详情","/component/componentView?componentId="+obj.data.componentId,"2");*/
                /*top.HussarTab.tabAdd("成果预审","/component/componentAudit?componentId="+obj.data.componentId,"3");*/
                openView('成果预审', '/component/componentAudit?componentId=' + obj.data.componentId, 760, 500, obj);
            }
        });
    }
    /*打开查看页面*/

    /*打开专题维护*/
    function openView(title, url, w, h, obj) {
        dataObj = obj;
        var dataArr = active.getThisData(obj);
        if (title == null || title == '') {
            title = false;
        }
        ;
        if (url == null || url == '') {
            url = "404.jsp";
        }
        ;
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        }
        ;
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        }
        ;

        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: Hussar.ctxPath+url,
        });
    }

    active = {
        reload: function () {
            var title = $("#title").val().trim();
            //文件名
            var pattern = new RegExp("^[^/\\\\:\\*\\'\\‘\\?\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if (!pattern.test(title)) {
                layer.alert('输入的文件名不合法', {
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
                    title: title,
                    //防止IE浏览器第一次请求后从缓存读取数据
                    timestamp: (new Date()).valueOf()
                }
            })
        }
        ,
        componentApply: function () {
            top.HussarTab.tabAdd("成果提报", "/component/componentApply", "1");
        },
        componentAudit: function () {
            var dataArr = active.getCheckData();

            var idArr = [];
            for (var i = 0; i < dataArr.length; i++) {
                idArr.push(dataArr[i].componentId);
            }
            if (dataArr.length == 0) {
                layer.alert('请至少选择一条要审核的数据', {
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

            var ids = idArr.join(",")
            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/component/componentPassPlus",
                data: {
                    componentId: ids,
                    type:"1"
                },
                async: false,
                cache: false,
                dataType: "json",
                success: function (data) {
                    if (data.result == "1") {
                        layer.alert("审核成功", {
                            icon: 1,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示',
                            end: function () {

                            }
                        }, function () {
                            var index = layer.alert();
                            layer.close(index);
                            TopicBin.initTableView();

                        });
                    } else {
                        layer.alert("审核失败", {
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
            var ajax = new $ax(Hussar.ctxPath + "/component/componentPassPlus", function(data) {
                if (data.result == "1") {
                    layer.alert("审核成功", {
                        icon: 1,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        end: function () {

                        }
                    }, function () {
                        var index = layer.alert();
                        layer.close(index);
                        TopicBin.initTableView();

                    });
                } else {
                    layer.alert("审核失败", {
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
            ajax.set("componentId",ids);
            ajax.set("type","1");
            ajax.start();
        },

        componentPublish: function () {

            var dataArr = active.getCheckData();
            if (dataArr.length != 1) {
                layer.alert('请先选择一条要发布的数据', {
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
            layer.confirm('确定要发布所选中的成果吗？', function () {
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/component/componentPublish",
                    data: {
                        componentId: dataArr[0].componentId
                    },
                    async: false,
                    cache: false,
                    success: function (data) {
                        if (data.result == "1") {
                            layer.alert('发布成功', {
                                icon: 1,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示',
                                end: function () {

                                }
                            }, function () {
                                var index = layer.alert();
                                layer.close(index);
                                TopicBin.initTableView();
                            });
                        } else {
                            layer.alert(+'发布失败', {
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
                var ajax = new $ax(Hussar.ctxPath + "/component/componentPublish", function(data) {
                    if (data.result == "1") {
                        layer.alert('发布成功', {
                            icon: 1,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示',
                            end: function () {

                            }
                        }, function () {
                            var index = layer.alert();
                            layer.close(index);
                            TopicBin.initTableView();
                        });
                    } else {
                        layer.alert(+'发布失败', {
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
                ajax.set("componentId",dataArr[0].componentId);
                ajax.start();
            })


        }
        ,
        componentCognizance: function () {
            var dataArr = active.getCheckData();
            if (dataArr.length != 1) {
                layer.alert('请先选择一条要认定的数据', {
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
            /* var url="/component/componentCognizance?componentId="+dataArr[0].componentId;
             window.location.href=encodeURI(url);*/
            top.HussarTab.tabAdd("成果认定", "/component/componentCognizance?componentId=" + dataArr[0].componentId, "4");


        },
        getCheckData: function () { //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            var checkStatus = table.checkStatus('topicList'), mySelected = checkStatus.data;

//                $.each(layui.data('checked'), function (k, v) {
//                    mySelected.push(v);
//                });
//                debugger;
            return mySelected;
        }, getThisData: function () {
            return dataObj;
        }, getMoveCheckData: function () { //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            $.each(layui.data('checked'), function (k, v) {
                mySelected.push(v);
            });
            return mySelected;
        }
    };


    TopicBin.initButton = function () {
        $("#passBtn").click(function () {
            // openPass('通过', '/examineFile/passView', 500, 245);
        });
        $("#delBtn").click(function () {
            // openDel('删除', '/examineFile/delView', 500, 245);
        });


    };
    //.渲染完成回调
    $('.layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    top.reloadComponentDept = function () {
        tableIns.reload({
            page: {
                curr: 1
            }
        })
    }

    /* $(window).resize(function() {
     setTimeout(function () {
     table.render(tableOption);
     },300)
     });*/


    $(function () {
        TopicBin.initTableView();//初始化表格
        $("#searchBtn").click(function () {
            TopicBin.initTableView();
        });
        $(window).resize(function () {
            TopicBin.initTableView();
        });

        var dicType = 'component_type';
        var ajax = new $ax(Hussar.ctxPath+"/dic/listData",
            function(data) {
                $('#listData').val(JSON.stringify(data));   //将数据展示到textarea中
                //dataDicdemo.initListSelect(data);   //加载list数据下拉框
                for (var i = 0; i < data.length; i++) {
                    $("#componentType").append(
                        "<option value='" + data[i].VALUE + "'>"
                        + data[i].LABEL + "</option>");
                }
                form.render();
            },
            function(data) {
                Hussar.error("获取List数据失败!");
            });
        ajax.set("dicType",dicType);
        ajax.start();

        dicType = 'component_origin';
         ajax = new $ax(Hussar.ctxPath+"/dic/listData",
            function(data) {
                $('#listData').val(JSON.stringify(data));   //将数据展示到textarea中
                //dataDicdemo.initListSelect(data);   //加载list数据下拉框
                for (var i = 0; i < data.length; i++) {
                    $("#componentOrigin").append(
                        "<option value='" + data[i].VALUE + "'>"
                        + data[i].LABEL + "</option>");
                }
                form.render();
            },
            function(data) {
                Hussar.error("获取List数据失败!");
            });
        ajax.set("dicType",dicType);
        ajax.start()
    });
    $(function () {
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/multiplex/getDept",

            async: false,
            cache: false,
            success: function (data) {
                var dept = $("#deptName");
                dept.html(" <option value=''selected>请选择所属部门</option>");

                for(var i=0;i<data.length;i++){
                    dept.append("<option value='"+data[i].oragnAliasWhole+"'>"+data[i].oragnAliasWhole+"</option>");
                }
                form.render();
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/multiplex/getDept", function(data) {
            var dept = $("#deptName");
            dept.html(" <option value=''selected>请选择所属部门</option>");

            for(var i=0;i<data.length;i++){
                dept.append("<option value='"+data[i].oragnAliasWhole+"'>"+data[i].oragnAliasWhole+"</option>");
            }
            form.render();
        }, function(data) {

        });
        ajax.start();

        TopicBin.initTableView();//初始化表格

        $("#searchBtn").click(function () {
            TopicBin.initTableView();
        });
        $(window).resize(function() {
            TopicBin.initTableView();
        });
    });
});

/*获取专题是否公开*/
function getTopicShow(t) {
    if (t == 1) {
        return '是';
    } else {
        return '否';
    }
}