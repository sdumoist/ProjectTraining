var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var dataObj;
var index;
layui.use(['jquery',  'tree', 'table', 'util', 'laytpl', 'form', 'layer','Hussar'], function () {
    var $ = layui.jquery,
        table = layui.table,
        laytpl = layui.laytpl,
        form = layui.form,
        layer = layui.layer;
    util = layui.util;
    var Hussar = layui.Hussar;
    //.存储当前页数据集
    var pageData = [];
    //.存储已选择数据集，用普通变量存储也行
    layui.data('checked', null);
    var resourceBin = {
        tableId: "resourceBinTable",	//表格id
        seItem: null,		//选中的条目
    };
    //初始化表格
    resourceBin.initTableView = function () {
        tableIns = table.render({
            elem: '#resourceList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: Hussar.ctxPath+'/resource/resourceList' //数据接口
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
            , cols: [[
                {field: 'id', title: 'id', type: 'checkbox', width: '5%', align: "center"},
                {type: 'numbers', title: '序号', align: 'center', width: '5%'},
                {field: 'fileName', title: '文档名称', width: '30%', align: "center"},
                {field: 'userName', title: '用户名称', width: '20%', align: "left"},
                {field: 'operateType', title: '操作类型', align: "center",templet:function (d) {
                        if (d.operateType == '0'){
                            return '上传'
                        }else if (d.operateType == '1'){
                            return '修改'
                        }else if (d.operateType == '2'){
                            return '删除'
                        }else if (d.operateType == '3'){
                            return '预览'
                        }else if (d.operateType == '4'){
                            return '下载'
                        }else if (d.operateType == '21'){
                            return '修改权限'
                        }else if (d.operateType == '22'){
                            return '删除权限'
                        }else if (d.operateType == '5'){
                            return '收藏'
                        }else if (d.operateType == '6'){
                            return '分享'
                        }else if (d.operateType == '8'){
                            return '重命名'
                        }else if (d.operateType == '9'){
                            return '取消收藏'
                        }else if (d.operateType == '11'){
                            return '登录'
                        }else if (d.operateType == '12'){
                            return '移动'
                        }else if (d.operateType == '14'){
                            return '添加日志'
                        }else if (d.operateType == '15'){
                            return '加入专题'
                        }else if (d.operateType == '21'){
                            return '修改权限'
                        }else if (d.operateType == '22'){
                            return '删除权限'
                        }else if (d.operateType == '23'){
                            return '新增分组'
                        }else if (d.operateType == '24'){
                            return '新增群组'
                        }else if (d.operateType == '25'){
                            return '修改分组'
                        }else if (d.operateType == '26'){
                            return '修改群组'
                        }else if (d.operateType == '27'){
                            return '删除分组'
                        }else if (d.operateType == '28'){
                            return '删除群组'
                        }else if (d.operateType == '29'){
                            return '查看专题列表'
                        }else if (d.operateType == '30'){
                            return '新增专题'
                        }else if (d.operateType == '31'){
                            return '修改专题'
                        }else if (d.operateType == '32'){
                            return '删除专题'
                        }else if (d.operateType == '33'){
                            return '查看专题下文件'
                        }else if (d.operateType == '34'){
                            return '删除专题下文件'
                        }else if (d.operateType == '35'){
                            return '移动专题'
                        }else if (d.operateType == '36'){
                            return '批量删除专题'
                        }else if (d.operateType == '37'){
                            return '新增广告位'
                        }else if (d.operateType == '38'){
                            return '修改广告位'
                        }else if (d.operateType == '39'){
                            return '删除广告位'
                        }else if (d.operateType == '40'){
                            return '移动广告位'
                        }else {
                            debugger;
                            return '其他'
                        }
//29查看专题列表,30新增专题,31修改专题,32删除专题,33查看专题下的文件,34删除专题下的文件,35移动专题）
                    }},
                {field: 'operateTime', title: '操作时间', align: "center"}
                , {field: 'addressIp', title: 'IP地址', align: "center"}
            ]] //设置表头
        });
    }
    window.initTableView = function () {
        resourceBin.initTableView();
    };
    active = {
        reload: function () {
            var resourceFileName = $("#resourceFileName").val().trim();
            //文件名
            var pattern = new RegExp("^[^/\\\\:\\*\\'\\‘\\?\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if (!pattern.test(resourceFileName)) {
                layer.alert('输入的文档名称不合法', {
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
                    resourceName: resourceFileName,
                    //防止IE浏览器第一次请求后从缓存读取数据
                    timestamp: (new Date()).valueOf()
                }
            })
        }
    }
    resourceBin.initButtonEvent = function () {
        $("#searchBtn").on('click', function () {
            tableIns.reload({
                page: {
                    curr: 1
                },
                where: {
                    groupId: $('#groupId').val(),
                    uerName: $('#searchName').val()
                }
            })
        });
    }
    $('.layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    $(function () {
        resourceBin.initTableView();//初始化表格
        resourceBin.initButtonEvent();
        $(window).resize(function() {
            resourceBin.initTableView();
        });
    });
})