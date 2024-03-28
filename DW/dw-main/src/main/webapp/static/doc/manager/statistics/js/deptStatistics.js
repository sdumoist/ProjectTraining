/**
 * Created by Administrator on 2018/8/28.
 */
var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var dataObj;
layui.use(['jquery',  'tree', 'table', 'util', 'laytpl', 'form', 'layer'], function () {
    var $ = layui.jquery,
        table = layui.table,
        laytpl = layui.laytpl,
        form = layui.form,
        layer = layui.layer;
    util = layui.util;
    //.存储当前页数据集
    var pageData = [];
    //.存储已选择数据集，用普通变量存储也行
    layui.data('checked', null);
    var deptStatistics = {
        tableId: "deptStatistics",	//表格id
        seItem: null	//选中的条目
    };
    //初始化表格
    deptStatistics.initTableView = function () {
        tableIns =table.render({
            elem: '#deptStatistics' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: '/deptStatistics/list' //数据接口
            , cols: [[
                {field: 'NAME', title: '部门名称', align: 'center', width: '25%'},
                {field: 'INIT_SPACE', title: '总共空间', width: '25%', align: "center",event: 'editSpace'},
                {field: 'NUM', title: '可用空间', width: '25%', align: "center"},
                {field: 'USERNUM', title: '已用空间', width: '25%', align: "center"},

            ]] //设置表头
        });
        //.监听选择，记录已选择项
        table.on('checkbox(deptStatistics)', function (obj) {
            //.全选或单选数据集不一样
            var data = obj.type == 'one' ? [obj.data] : pageData;
            //.遍历数据
            $.each(data, function (k, v) {
                //.假设你数据中 id 是唯一关键字
                if (obj.checked) {
                    //.增加已选中项
                    layui.data('checked', {
                        key: v.topicId, value: v
                    });
                } else {
                    //.删除
                    layui.data('checked', {
                        key: v.topicId, remove: true
                    });
                }
            });
        });

    }
    table.on('tool(deptStatistics)', function (obj) {
        var data = obj.data;

        if (obj.event === 'editSpace') {
          var space=  data.INIT_SPACE;
        space=    space.substring(0,space.length-2);
            layer.prompt({
                formType:0,
                value: space,
                title: '请输入修改的容量大小（单位GB）'
            }, function(value, index, elem){
                layer.close(index);
                if(value.length<=0){
                    layer.msg("部门容量不能为空 ",{anim:6,icon: 0});
                    return ;
                }

                // if(value.length>5){
                //     layer.msg("部门容量不能超过5位！", {anim:6,icon: 0});
                //     return ;
                // }
                var pattern = /^\d{1,3}(\.\d{1,2})?$/;
                //特殊字符
                if(!pattern.test(value)){
                    layer.msg("请输入1000以内的两位小数或整数", {anim:6,icon: 0});
                    return;
                }
//layer.alert(value);
                $.ajax({
                    type: "post",
                    url: "/deptStatistics/updateSpace",
                    data: {
                        "id": data.ORGAN_ID,
                        "space":value
                    },
                    async: true,

                });
                layer.alert('修改成功',{
                    icon: 1,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                },function () {
                    location=location;
                });
               
            });
        }

    });
    active = {
        reload: function () {
            var deptName = $("#deptName").val().trim();
            //文件名
            var pattern = new RegExp("^[^/\\\\:\\*\\?\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if (!pattern.test(topicName)) {
                layer.alert('输入的部门名不合法', {
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
                    deptName: deptName
                }
            })
        }
        , delTopics: function () {
            var dataArr = active.getCheckData();
            if (dataArr.length == 0) {
                layer.alert('请先选择要删除的专题', {
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
                    key: dataArr[i].topicId, remove: true
                });
                if (i == 0) {
                    ids = dataArr[i].topicId;
                } else {
                    ids += ',' + dataArr[i].topicId;
                }
            }
            layer.confirm('确定要删除所选中的专题吗？', function () {
                $.ajax({
                    type: "post",
                    url: "${ctxPath}/topic/delTopics",
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
                                title: '提示'
                            }, function () {
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
                    }
                })
            })
        }
       ,
        getCheckData: function () { //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            var checkStatus = table.checkStatus('deptStatistics'), mySelected = checkStatus.data;

//                $.each(layui.data('checked'), function (k, v) {
//                    mySelected.push(v);
//                });
//                debugger;
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

    /* $(window).resize(function() {
     setTimeout(function () {
     table.render(tableOption);
     },300)
     });*/

    $(function () {
        deptStatistics.initTableView();//初始化表格
        $(window).resize(function() {
            deptStatistics.initTableView();
        });
    });
});




