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
    var TopicBin = {
        tableId: "topicBinTable",	//表格id
        seItem: null,		//选中的条目
    };
    //初始化表格
    TopicBin.initTableView = function () {
        tableIns =table.render({
            elem: '#topicList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: '/ThreadController/getThread' //数据接口
            , request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            , page: false //开启分页
            ,where: {
                //防止IE浏览器第一次请求后从缓存读取数据
                timestamp: (new Date()).valueOf()
            }
            ,even:true
            , cols: [[
                {type: 'numbers', title: '序号', align: 'center', width: '5%'},
                {field: 'threadName', title: '线程名称', width: '60%', align: "center"},
                {field: 'threadState', title: '线程状态', width: '35%', align: "center"},
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
                    //.增加已选中项
                    layui.data('checked', {
                        key: v.bannerId, value: v
                    });
                } else {
                    //.删除
                    layui.data('checked', {
                        key: v.bannerId, remove: true
                    });
                }
            });
        });
        table.on('tool(topicList)', function (obj) {

            if (obj.event === 'opeView') {
                buttonType = 'viewTopic';
                openView('查看banner', '/banner/bannerView', 760, 366, obj);
            }
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
            content: url
        });
    }

    /*打开编辑页面*/
    function openEdit(title, url, w, h) {
        var dataArr = active.getCheckData();
        if (dataArr.length != 1) {
            layer.alert('请先选择一条要修改的banner', {
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
        bannerId = dataArr[0].bannerId;
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: url + "?bannerId=" + bannerId
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
            h = ($(window).height() - 200);
        };
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: url + "?bannerId=" + obj.data.bannerId
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
            content: url + "?topicId=" + obj.data.topicId,
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
        reload: function () {
            var bannerName = $("#bannerName").val().trim();
            //文件名
            var pattern = new RegExp("^[^/\\\\:\\*\\'\\‘\\?\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if (!pattern.test(bannerName)) {
                layer.alert('输入的banner名不合法', {
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
                    bannerName: bannerName,
                    //防止IE浏览器第一次请求后从缓存读取数据
                    timestamp: (new Date()).valueOf()
                }
            })
        }
        , addTopic: function () {
            buttonType = 'addTopic';
            openTopic('新增banner', '/banner/bannerAdd', 670, 466);//为ie兼容改为670
        }
        , editTopic: function () {
            buttonType = 'editTopic';
            openEdit('banner修改', '/banner/bannerEdit', 670, 466);
        }, moveUp: function () {
            var dataArr = active.getMoveCheckData();
            if(dataArr.length != 1){
                layer.alert('请选择一条要上移的banner', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            var index = dataArr[0].LAY_TABLE_INDEX;    //选中行的序号

            var data =  table.cache["topicList"];

            var $tr = $("tr[data-index = "+index+"]");     //选中的行
            var upNum = index-1;                //上一行的序号
            if ($tr.index() != 0) {
                $.ajax({                        //交换两行的showorder
                    type:"post",
                    url:"/banner/moveBanner",
                    data:{
                        table:"doc_banner_file",
                        idColumn:"banner_id",
                        idOne:dataArr[0].bannerId,
                        idTwo:data[upNum].bannerId
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
                });
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
            var dataArr = active.getMoveCheckData();
            if(dataArr.length != 1){
                layer.alert('请选择一条要下移的banner', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            var index = dataArr[0].LAY_TABLE_INDEX;
            var downNum = index+1;
            var data =  table.cache["topicList"];
            var $tr = $("tr[data-index = "+index+"]");
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
                $.ajax({
                    type:"post",
                    url:"/banner/moveBanner",
                    data:{
                        table:"doc_banner_file",
                        idColumn:"banner_id",
                        idOne:dataArr[0].bannerId,
                        idTwo:data[downNum].bannerId
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
                });
                /*$tr.fadeOut().fadeIn();
                 $tr.next().after($tr);*/
            }
        }
        , delTopics: function () {
            var dataArr = active.getCheckData();
            if (dataArr.length == 0) {
                layer.alert('请先选择要删除的banner', {
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
                    key: dataArr[i].bannerId, remove: true
                });
                if (i == 0) {
                    ids = dataArr[i].bannerId;
                } else {
                    ids += ',' + dataArr[i].bannerId;
                }
            }
            layer.confirm('确定要删除所选中的banner吗？', function () {
                $.ajax({
                    type: "post",
                    url: "/banner/delBanners",
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
                    }
                })
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
                $.ajax({
                    type: "post",
                    url: "/topic/publishTopic",
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
                })
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
                $.ajax({
                    type: "post",
                    url: "/topic/sentTopicToJQX",
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
                })
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