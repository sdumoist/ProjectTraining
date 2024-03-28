var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var dataObj;
var index
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
    var TopicBin = {
        tableId: "topicBinTable",	//表格id
        seItem: null,		//选中的条目
    };
    //初始化表格
    TopicBin.initTableView = function () {
        tableIns =table.render({
            elem: '#topicList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: Hussar.ctxPath+'/top/topList' //数据接口
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
                {field: 'topId', title: 'id', type: 'checkbox', width: '5%', align: "center"},
                {type: 'numbers', title: '序号', align: 'center', width: '5%'},
                {field: 'title', title: '文件名', width: '50%', align: "center",},
                {field: 'createTime', title: '创建时间', width: '40%', align: "center"},
            ]] //设置表头
        });
        //.监听选择，记录已选择项
        table.on('checkbox(topicList)', function (obj) {
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
                        key: v.topId, value: v
                    });
                } else {
                    //.删除
                    layui.data('checked', {
                        key: v.topId, remove: true
                    });
                }
            });
        });
    }
    /*打开专题维护*/
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
        , moveUp: function () {
            var dataArr = active.getCheckData();
            if(dataArr.length != 1){
                layer.alert('请选择一条要上移的置顶文件', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            //var index = dataArr[0].LAY_TABLE_INDEX;    //选中行的序号

            var data =  table.cache["topicList"];

            var $tr = $("tr[data-index = "+(index-1)+"]");     //选中的行
            var upNum = index-2;                //上一行的序号
            if ($tr.index() != 0) {
                /*$.ajax({                        //交换两行的showorder
                    type:"post",
                    url: Hussar.ctxPath+"/top/moveTop",
                    data:{
                        table:"doc_top_file",
                        idColumn:"top_id",
                        idOne:dataArr[0].topId,
                        idTwo:data[upNum].topId
                    },
                    async:false,
                    cache:false,
                    dataType:"json",
                    success:function(result){
                        if(result > 0){
                            tableIns.reload({
                                where:{
                                    //防止IE浏览器第一次请求后从缓存读取数据
                                    timestamp: (new Date()).valueOf()
                                },
                                done: function(res, curr, count){       //刷新列表后重新选中之前的行
                                    var td = $('#topicList').next().find("tr[data-index='"+upNum+"'] div.layui-form-checkbox");
                                    td.click();
                                }
                            });
                            layui.data('checked', null);
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/top/moveTop", function(result) {
                    if(result > 0){
                        tableIns.reload({
                            where:{
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            },
                            done: function(res, curr, count){       //刷新列表后重新选中之前的行
                                var td = $('#topicList').next().find("tr[data-index='"+upNum+"'] div.layui-form-checkbox");
                                td.click();
                            }
                        });
                        layui.data('checked', null);
                    }
                }, function(data) {

                });
                ajax.set("table","doc_top_file");
                ajax.set("idColumn","top_id");
                ajax.set("idOne",dataArr[0].topId);
                ajax.set("idTwo",data[upNum].topId);
                ajax.start();
                /*$tr.fadeOut().fadeIn();
                 $tr.prev().before($tr);*/
            }else{
                layer.alert('已经上移到最顶端', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
            }
        }, moveDown: function () {
            var dataArr = active.getCheckData();
            if(dataArr.length != 1){
                layer.alert('请选择一条要下移的置顶文件', {
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
            var data =  table.cache["topicList"];
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
                return;
            }
            if(next){
                /*$.ajax({
                    type:"post",
                    url: Hussar.ctxPath+"/top/moveTop",
                    data:{
                        table:"doc_top_file",
                        idColumn:"top_id",
                        idOne:dataArr[0].topId,
                        idTwo:data[downNum].topId
                    },
                    async:false,
                    cache:false,
                    dataType:"json",
                    success:function(result){
                        if(result > 0){
                            tableIns.reload({
                                where:{
                                    //防止IE浏览器第一次请求后从缓存读取数据
                                    timestamp: (new Date()).valueOf()
                                },
                                done: function(res, curr, count){
                                    var td = $('#topicList').next().find("tr[data-index='"+downNum+"'] div.layui-form-checkbox");
                                    td.click();
                                }
                            });
                            layui.data('checked', null);

                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/top/moveTop", function(result) {
                    if(result > 0){
                        tableIns.reload({
                            where:{
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            },
                            done: function(res, curr, count){
                                var td = $('#topicList').next().find("tr[data-index='"+downNum+"'] div.layui-form-checkbox");
                                td.click();
                            }
                        });
                        layui.data('checked', null);

                    }
                }, function(data) {

                });
                ajax.set("table","doc_top_file");
                ajax.set("idColumn","top_id");
                ajax.set("idOne",dataArr[0].topId);
                ajax.set("idTwo",data[downNum].topId);
                ajax.start();
                /*$tr.fadeOut().fadeIn();
                 $tr.next().after($tr);*/
            }
        }
        , delTopics: function () {
            var dataArr = active.getCheckData();
            if (dataArr.length == 0) {
                layer.alert('请先选择要取消置顶的文件', {
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
                    key: dataArr[i].topId, remove: true
                });
                if (i == 0) {
                    ids = dataArr[i].topId;
                } else {
                    ids += ',' + dataArr[i].topId;
                }
            }
            layer.confirm('确定要取消所选中的文件置顶吗？', function () {
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/top/delTops",
                    data: {
                        ids: ids
                    },
                    async: false,
                    cache: false,
                    success: function (data) {
                        if (data == dataArr.length) {
                            layer.alert('取消成功', {
                                icon: 1,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示',

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
                            layer.alert('取消失败', {
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
                var ajax = new $ax(Hussar.ctxPath + "/top/delTops", function(data) {
                    if (data == dataArr.length) {
                        layer.alert('取消成功', {
                            icon: 1,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示',

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
                        layer.alert('取消失败', {
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
        , publishTopic: function () {
            var dataArr = active.getCheckData();
            var topicShow = 1;
            var state = "发布";
            if (dataArr.length == 0) {
                layer.alert('请先选择要发布的专题', {
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
                if (i == 0) {
                    ids = dataArr[i].topicId;
                } else {
                    ids += ',' + dataArr[i].topicId;
                }
            }
            active.editPublicState(ids, topicShow, state, dataArr)
        }, stopTopic: function () {
            var dataArr = active.getCheckData();
            var topicShow = 0;
            var state = "撤回";
            if (dataArr.length == 0) {
                layer.alert('请先选择要下架的专题', {
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
                if (i == 0) {
                    ids = dataArr[i].topicId;
                } else {
                    ids += ',' + dataArr[i].topicId;
                }
            }
            active.editPublicState(ids, topicShow, state, dataArr)
        }, editPublicState: function (ids, topicShow, state, dataArr) {
            layer.confirm('确定要下架所选中的专题吗？', function () {
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/topic/publishTopic",
                    data: {
                        ids: ids,
                        topicShow: topicShow
                    },
                    async: false,
                    cache: false,
                    success: function (data) {
                        if (data == dataArr.length) {
                            layer.alert('下架成功', {
                                icon: 1,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示',
                                end: function () {
                                    tableIns.reload({
                                        where: {
                                            //防止IE浏览器第一次请求后从缓存读取数据
                                            timestamp: (new Date()).valueOf()
                                        }
                                    });
                                    var index = layer.alert();
                                    layer.close(index);
                                }
                            }, function () {
                                tableIns.reload({
                                    where: {
                                        //防止IE浏览器第一次请求后从缓存读取数据
                                        timestamp: (new Date()).valueOf()
                                    }
                                });
                                var index = layer.alert();
                                layer.close(index);
                            });
                        } else {
                            layer.alert(state + '失败', {
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
                var ajax = new $ax(Hussar.ctxPath + "/topic/publishTopic", function(data) {
                    if (data == dataArr.length) {
                        layer.alert('下架成功', {
                            icon: 1,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示',
                            end: function () {
                                tableIns.reload({
                                    where: {
                                        //防止IE浏览器第一次请求后从缓存读取数据
                                        timestamp: (new Date()).valueOf()
                                    }
                                });
                                var index = layer.alert();
                                layer.close(index);
                            }
                        }, function () {
                            tableIns.reload({
                                where: {
                                    //防止IE浏览器第一次请求后从缓存读取数据
                                    timestamp: (new Date()).valueOf()
                                }
                            });
                            var index = layer.alert();
                            layer.close(index);
                        });
                    } else {
                        layer.alert(state + '失败', {
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
                ajax.set("topicShow",topicShow);
                ajax.start();
            })
        }, sendToJQX: function () {
            var dataArr = active.getCheckData();
            var topicShow = 0;
            if (dataArr.length != 1) {
                layer.alert('请先选择要推送的一条专题', {
                    icon: 0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            layer.confirm('确定要推送所选中的专题吗？', function () {
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/topic/sentTopicToJQX",
                    data: {
                        topicId: dataArr[0].topicId,
                    },
                    async: false,
                    cache: false,
                    success: function (data) {
                        if (data == dataArr.length) {
                            layer.alert('推送成功', {
                                icon: 1,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示'
                            })
                        } else {
                            layer.alert('推送失败', {
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
                var ajax = new $ax(Hussar.ctxPath + "/topic/sentTopicToJQX", function(data) {
                    if (data == dataArr.length) {
                        layer.alert('推送成功', {
                            icon: 1,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示'
                        })
                    } else {
                        layer.alert('推送失败', {
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
                ajax.set("topicId",dataArr[0].topicId);
                ajax.start();
            })
        } ,

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
        TopicBin.initTableView();//初始化表格
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