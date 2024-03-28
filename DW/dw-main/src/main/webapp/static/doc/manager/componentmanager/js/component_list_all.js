var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var dataObj;
layui.use(['jquery',  'tree', 'table', 'util', 'laytpl', 'form', 'layer','HussarAjax', 'Hussar'], function () {
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
    function openWin(url) {
        var a = document.createElement("a"); //创建a标签
        a.setAttribute("href", url);
        a.setAttribute("target", "_blank");
        document.body.appendChild(a);
        a.click(); //执行当前对象
    }
    var publishFlag = '0';
    var collectFlag = '0';
    form.on('switch(publishFlag)', function (data) {
        var x = data.elem.checked;//判断开关状态
        if (x == true) {
            publishFlag = '1'
        } else {
            publishFlag = '0'
        }
    });
    form.on('switch(collectFlag)', function (data) {
        var x = data.elem.checked;//判断开关状态
        if (x == true) {
            collectFlag = '1'
        } else {
            collectFlag = '0'
        }
    });

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
        tableIns =table.render({
            elem: '#topicList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: Hussar.ctxPath+'/component/componentListByAll'
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
            }
            ,even:true
            , cols: [[
                {field: 'componentId', title: 'id', type: 'checkbox', width: '4%', align: "center"},
                {type: 'numbers', title: '序号', align: 'center', width: '4%'},
                {field: 'componentName', title: '成果名称', width: '20%', align: "left",event: 'opeView', style: 'cursor: pointer;color:#00a4ff'},
                // {field: 'componentDescText', title: '成果描述', width: '10%', align: "left"},
                {field: 'organAlias', title: '所属部门', width: '15%', align: "center"},
                {field: 'componentType', title: '成果分类', width: '9%', align: "center",templet: '#type'},
                {field: 'componentOrigin', title: '成果来源', width: '9%', align: "center",templet: '#origin'},
                {field: 'componentState', title: '状态', width: '9%', align: "center",templet: '#state'},
                {field: 'userName', title: '提报人', width: '10%', align: "center"},
                {field: 'createTime', title: '提报时间', width: '10%', align: "center",templet: '#createTime'},
                {field: 'awards', title: '奖项', width: '11%', align: "center",templet: '#awards'}
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
                    if(dataArr.length!=1){
                        $("#componentAudit").hide();
                        $("#componentCognizance").hide();
                        $("#componentPublish").hide();
                    }else{
                      var state=  dataArr[0].componentState;
                      if(state==0){
                          $("#componentAudit").show();
                          $("#componentCognizance").hide();
                          $("#componentPublish").hide();
                      }else if(state==1){
                          $("#componentAudit").hide();
                          $("#componentCognizance").show();
                          $("#componentPublish").hide();
                      }else if(state==2){
                          $("#componentAudit").hide();
                          $("#componentCognizance").hide();
                          $("#componentPublish").show();
                      }else{
                          $("#componentAudit").hide();
                          $("#componentCognizance").hide();
                          $("#componentPublish").hide();
                      }
                    }

                    //.增加已选中项
                    layui.data('checked', {
                        key: v.componentId, value: v
                    });

                } else {
                    var dataArr = active.getCheckData();
                    if(dataArr.length!=1){
                        $("#componentAudit").hide();
                        $("#componentCognizance").hide();
                        $("#componentPublish").hide();
                    }else{
                        var state=  dataArr[0].componentState;
                        if(state==0){
                            $("#componentAudit").show();
                            $("#componentCognizance").hide();
                            $("#componentPublish").hide();
                        }else if(state==1){
                            $("#componentAudit").hide();
                            $("#componentCognizance").show();
                            $("#componentPublish").hide();
                        }else if(state==2){
                            $("#componentAudit").hide();
                            $("#componentCognizance").hide();
                            $("#componentPublish").show();
                        }else{
                            $("#componentAudit").hide();
                            $("#componentCognizance").hide();
                            $("#componentPublish").hide();
                        }
                    }

                    //.删除
                    layui.data('checked', {
                        key: v.componentId, remove: true
                    });
                }
            });
        });
        table.on('tool(topicList)', function (obj) {

            if (obj.event === 'opeView') {
                buttonType = 'viewTopic';
                /*  var url="/component/componentView?componentId="+obj.data.componentId;
                  window.location.href=encodeURI(url);*/
                openWin(Hussar.ctxPath+"/toShowComponent/toShowPDF?id="+obj.data.componentId+"&date="+new Date())
            }
        });
    }
    /*打开查看页面*/

    /*打开专题维护*/

    active = {
        reload: function () {
            var title = $("#title").val().trim();
            var componentState = $("#componentState").val();
            var componentType = $("#componentType").val();
            var componentOrigin = $("#componentOrigin").val();
            var deptName = $("#deptName").val();
            var awardType = $("#awardType").val();
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
                    componentName: title,
                    componentState:componentState,
                    componentType:componentType,
                    componentOrigin:componentOrigin,
                    deptName:deptName,
                    awardType:awardType,
                    //防止IE浏览器第一次请求后从缓存读取数据
                    timestamp: (new Date()).valueOf()
                }
            })
        }
        ,
        componentApply:function () {
            top.HussarTab.tabAdd("成果提报","/component/componentApply","1");
        },
        componentAudit:function () {
            var dataArr = active.getCheckData();
            if (dataArr.length != 1) {
                layer.alert('请先选择一条要审核的数据', {
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
           /* var url="/component/componentAudit?componentId="+dataArr[0].componentId;
            window.location.href=encodeURI(url);*/
            top.HussarTab.tabAdd("成果预审","/component/componentAudit?componentId="+dataArr[0].componentId,"3");

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
        componentCognizance:function () {
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
            top.HussarTab.tabAdd("成果认定","/component/componentCognizance?componentId="+dataArr[0].componentId,"4");


        },
        setAwards: function () {
            var dataArr = active.getCheckData();
            if (dataArr.length == 0) {
                layer.alert('请先选择文件', {
                    icon: 0,
                    shadeClose: true,
                    skin: 'awards-info',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            var ids;
            for (var i = 0; i < dataArr.length; i++) {
                if (i == 0) {
                    ids = dataArr[i].componentId;
                } else {
                    ids += ',' + dataArr[i].componentId;
                }
            }
            publishFlag = '0';
            collectFlag = '0';
            if(dataArr.length == 1){
                publishFlag = dataArr[0].publishFlag;
                collectFlag = dataArr[0].collectFlag;
            }
            var radio1 = document.getElementsByName("publishFlag");
            for (var i = 0; i < radio1.length; i++) {
                if('1' == publishFlag){
                    radio1[i].checked = true;
                }else{
                    radio1[i].checked = false;
                }
            }
            var radio2 = document.getElementsByName("collectFlag");
            for (var i = 0; i < radio2.length; i++) {
                if('1' == collectFlag){
                    radio2[i].checked = true;
                }else{
                    radio2[i].checked = false;
                }
            }
            form.render();
            layer.open({
                type: 1,
                btn: ['确定', '取消'],
                fix: false, //不固定
                maxmin: false,
                shadeClose: false,
                shade: 0.4,
                skin: 'setAwards-dialog',
                title: ["奖项设置", 'background-color:#fff'],
                content: $('#awardsDiv'),
                btn1: function (index, layero) {
                    var ajax = new $ax(Hussar.ctxPath + "/component/setAwards", function(data) {
                        $(".layui-layer-btn1").click();
                        if (data) {
                            layer.alert('奖项设置成功', {
                                icon: 1,
                                shadeClose: true,
                                skin: 'awards-result',
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
                                layer.closeAll();
                            });
                        } else {
                            layer.alert('奖项设置失败', {
                                icon: 2,
                                shadeClose: true,
                                skin: 'awards-result',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示'
                            });
                        }
                    }, function(data) {

                    });
                    ajax.set("ids",ids);
                    ajax.set("publishFlag",publishFlag);
                    ajax.set("collectFlag",collectFlag);
                    ajax.start();
                }
            })
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

    top.reloadComponentAll =  function(){
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
        TopicBin.initTableView();
        $(window).resize(function() {
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
        ajax.start();

        dicType = 'component_state';
        ajax = new $ax(Hussar.ctxPath+"/dic/listData",
            function(data) {
                $('#listData').val(JSON.stringify(data));   //将数据展示到textarea中
                //dataDicdemo.initListSelect(data);   //加载list数据下拉框
                for (var i = 0; i < data.length; i++) {
                    $("#componentState").append(
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

        ajax = new $ax(Hussar.ctxPath + "/multiplex/getDept", function(data) {
            var dept = $("#deptName");
            dept.html(" <option value=''selected>请选择所属部门</option>");

            for(var i=0;i<data.length;i++){
                dept.append("<option value='"+data[i].oragnAliasWhole+"'>"+data[i].oragnAliasWhole+"</option>");
            }
            form.render();
        }, function(data) {

        });
        ajax.start();
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