var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var dataObj;
layui.use(['jquery',  'tree', 'table', 'util', 'laytpl', 'form', 'layer','laydate','Hussar','HussarAjax'], function () {
    var $ = layui.jquery,
        table = layui.table,
        laytpl = layui.laytpl,
        form = layui.form,
        Hussar = layui.Hussar,
        layer = layui.layer;
    var laydate = layui.laydate,
        util = layui.util,
        $ax = layui.HussarAjax;
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
    var nowTime=new Date();
    var start=laydate.render({
        elem: '#start'
        ,type: 'date',
        theme:'molv',

        max:'nowTime',
        done:function(value,date){
            if (value !== '') {
                end.config.min = {
                    year: date.year,
                    month: date.month - 1,
                    date: date.date
                }
            }else {
                end.config.min.year = '';
                end.config.min.month = '';
                end.config.min.date = '';

            }
        }

    });
    var end=laydate.render({
        elem: '#end'
        ,type: 'date',
        theme:'molv',

        max:'nowTime',
        done:function(value,date){
            if (value !== '') {
                start.config.max = {
                    year: date.year,
                    month: date.month - 1,
                    date: date.date
                }
            }else {
                var y=nowTime.getFullYear();
                var month=nowTime.getMonth();
                var td=nowTime.getDate();
                start.config.max.year = y;
                start.config.max.month = month;
                start.config.max.date = td;

            }
        }
    });
    //初始化表格
    TopicBin.initTableView = function () {
        var deptName = $("#deptName").val();
        var title = $("#title").val();
        var caUserName = $("#caUserName").val();
        tableIns =table.render({
            elem: '#topicList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: Hussar.ctxPath+'/multiplex/multiplexListByDeptView?title=' + title + '&caUserName=' + caUserName + '&deptName=' + deptName
            , request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            , page: true //开启分页
            ,where: {
                //防止IE浏览器第一次请求后从缓存读取数据
                timestamp: (new Date()).valueOf()
            }
            ,done:function(res){
                tdTitle();
                tableRowSpanOfFixedCol(res)
            }
            ,even:false
            , cols: [[
                {field: 'componentId', title: 'id', type: 'checkbox', width: '5%', align: "center"},
                {field: 'componentName', title: '成果名称',width:'17%',  align: "left"},
                {field: 'caUserName', title: '提报人', width: '8%', align: "center"},
                {field: 'organAlias', title: '提报部门', width: '10%', align: "center"},
                {field: 'componentCount', title: '复用次数', align: 'center', width: '9%'},
                {field: 'projectName', title: '复用项目', width: '17%', align: "left",event: 'opeView', style: 'cursor: pointer;color:#00a4ff'},
                {field: 'projectDept', title: '登记部门', width: '14%', align: "center"},
                {field: 'userName', title: '登记人', width: '8%', align: "center"},
                {field: 'createTimeStr', title: '登记时间', width: '13%', align: "center"},
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
                        key: v.componentId, value: v
                    });

                } else {
                    //.删除
                    layui.data('checked', {
                        key: v.componentId, remove: true
                    });
                }
            });
        });
        table.on('tool(topicList)', function (obj) {

            if (obj.event === 'opeView') {
                if (obj.event === 'opeView') {
                    openView('复用登记查看','/multiplex/projectView?projectId='+obj.data.projectId, 760, 500, obj);

                }
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
        ,
        projectApply:function () {
            top.HussarTab.tabAdd("复用登记","/multiplex/multiplexApply","12345")
        },
        deleteComponentCognizance: function () {
            var dataArr = active.getCheckData();

            var idArr = [];
            for (var i = 0; i < dataArr.length; i++) {
                idArr.push(dataArr[i].projectId);
            }
            if (dataArr.length == 0) {
                layer.alert('请至少选择一条要删除的数据', {
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
            layer.confirm('确定要删除所选中的复用信息吗？', function (){
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/multiplex/deleteMultiplexSave",
                    data: {
                        projectId: ids,
                    },
                    async: false,
                    cache: false,
                    dataType: "json",
                    success: function (data) {
                        if (data.result == "1") {
                            layer.alert("删除成功", {
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
                            layer.alert("删除失败", {
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
                var ajax = new $ax(Hussar.ctxPath + "/multiplex/deleteMultiplexSave", function(data) {
                    if (data.result == "1") {
                        layer.alert("删除成功", {
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
                        layer.alert("删除失败", {
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
                ajax.set("projectId",ids);
                ajax.start();
            })


        },

        exportComponentCognizance:function () {
            var deptName = $("#deptName").val();
            var title = $("#title").val();
            var caUserName = $("#caUserName").val();
            window.location.href = Hussar.ctxPath + '/multiplex/export?title=' + title + '&caUserName=' + caUserName + '&deptName=' + deptName
        },

        componentPublish:function () {

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
                            layer.alert( +'发布失败', {
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
                        layer.alert( +'发布失败', {
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
        myComponent:function () {

            top.HussarTab.tabAdd("我的成果","/multiplex/myComponentView","123456")

        },
        getCheckData: function () { //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            var checkStatus = table.checkStatus('topicList'), mySelected = checkStatus.data;

//                $.each(layui.data('checked'), function (k, v) {
//                    mySelected.push(v);
//                });
//
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
    TopicBin.initButton = function() {
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
    top.reloadTab =  function(){
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




        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/multiplex/getDept",

            async: false,
            cache: false,
            success: function (data) {
                var dept = $("#deptName");
                dept.html(" <option value=''selected>请选择提报部门</option>");

                for(var i=0;i<data.length;i++){
                    dept.append("<option value='"+data[i].oragnAliasWhole+"'>"+data[i].oragnAliasWhole+"</option>");
                }
                form.render();
            }
        })*/
    var ajax = new $ax(Hussar.ctxPath + "/multiplex/getDept", function(data) {
        var dept = $("#deptName");
        dept.html(" <option value=''selected>请选择提报部门</option>");

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

/*获取专题是否公开*/
function getTopicShow(t) {
    if (t == 1) {
        return '是';
    } else {
        return '否';
    }
}
$("#multiplexRegister").on('click', function () {

});

var tabUrl = "/multiplex/multiplexApplyBackground"
var tabTitle="新增复用"
/*
 * @todo tab触发事件：增加、删除、切换
 */
var tabUtil = {

    tabAdd: function(tabTitle, tabUrl){
        var index = $('.layui-tab-item iframe').length;
        index = "div_"+(id);
        for(var i = 0; i < $('.weIframe').length; i++) {
            if($('.weIframe').eq(i).attr('src') == url) {
                tab.tabChange($('.weIframe').eq(i).attr('tab-id') );
                // event.stopPropagation();
                return;
            }
        }

}};

top.HussarTab = tabUtil;
function tableRowSpanOfFixedCol(res) {
    var data = res.data;
    var mergeIndex = 0;//定位需要添加合并属性的行数
    var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
    var columsName = ['projectId','numbers','componentName','projectDept','userName','createTimeStr'];//需要合并的列名称
    var columsIndex = [0,1,2,3,4];//需要合并的列索引值

    for (var k = 0; k < columsName.length; k++) { //这里循环所有要合并的列
        var trArr = $(".layui-table-body>.layui-table").find("tr");//所有行
        for (var i = 1; i < res.data.length; i++) { //这里循环表格当前的数据
            var tdCurArr = trArr.eq(i).find("td").eq(columsIndex[k]);//获取当前行的当前列
            var tdPreArr = trArr.eq(mergeIndex).find("td").eq(columsIndex[k]);//获取相同列的第一列

            if(columsName[k]=='componentId'||columsName[k]=='componentName'){
                if( columsName[k]=='numbers'){
                    data[i]['LAY_TABLE_INDEX']=1;
                }
                if (data[i][columsName[k]] === data[i-1][columsName[k]]) { //后一行的值与前一行的值做比较，相同就需要合并
                    mark += 1;
                    tdPreArr.each(function () {//相同列的第一列增加rowspan属性
                        $(this).attr("rowspan", mark);
                    });
                    tdCurArr.each(function () {//当前行隐藏
                        $(this).css("display", "none");
                    });
                }else {
                    mergeIndex = i;
                    mark = 1;//一旦前后两行的值不一样了，那么需要合并的格子数mark就需要重新计算
                }
            }else{
                if (data[i]['componentId'] === data[i-1]['componentId']) { //后一行的值与前一行的值做比较，相同就需要合并
                    mark += 1;
                    tdPreArr.each(function () {//相同列的第一列增加rowspan属性
                        $(this).attr("rowspan", mark);
                    });
                    tdCurArr.each(function () {//当前行隐藏
                        $(this).css("display", "none");
                    });
                }else {
                    mergeIndex = i;
                    mark = 1;//一旦前后两行的值不一样了，那么需要合并的格子数mark就需要重新计算
                }


            }

        }
        mergeIndex = 0;
        mark = 1;
    }
}