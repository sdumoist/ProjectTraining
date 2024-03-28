var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var dataObj;
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
        var componentId=$("#componentId").val().trim();
        var projectName=$("#projectName").val().trim();
        tableIns =table.render({

            elem: '#topicList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度

            , url: Hussar.ctxPath+'/multiplex/selectComponentProjectView?componentId='+componentId+'&projectName='+projectName+''
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
                /*{field: 'projectId', title: 'id', type: 'checkbox', width: '5%', align: "center"},*/
                {type: 'numbers', title: '序号', align: 'center', width: '10%'},
                {field: 'projectName', title: '项目名称', align: "left"},
                {field: 'projectDept', title: '所属部门', width: '15%', align: "center" },
                {field: 'projectUser', title: '负责人', width: '15%', align: "center"},
            ]] //设置表头
        });
        //.监听选择，记录已选择项
       /* table.on('checkbox(topicList)', function (obj) {
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
        });*/

    }
    /*打开查看页面*/

    /*打开专题维护*/

   /* active = {
        reload: function () {
            var title = $("#title").val().trim();
            var projectName=$("#projectName").val().trim();
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
        selectComponent:function () {
            var dataArr = active.getCheckData();
            if (dataArr.length ==0 ) {
                layer.alert('请至少选择一个成果', {
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
            var componentList= $(parent.document.getElementById("componentList"));
            var length =componentList.find("tr").length;
            for(var i=0; i<dataArr.length;i++){
                var   componentIdsArr=    parent.componentIds.join(",");
                if(componentIdsArr.indexOf(dataArr[i].componentId)!=-1){
                    continue;
                }
                parent.componentIds.push(dataArr[i].componentId)
                parent.componentNames.push(dataArr[i].componentName)
                parent.componentTypes.push(dataArr[i].componentType)
                parent.componentUsers.push(dataArr[i].userName)

                var type = "";
                if(dataArr[i].componentType==0){
                    type="技术组件"
                }else{
                    type="解决方案"
                }
                var innerHtml="  <tr>" +
                    "<td>"+(length+i+1)+"</td>" +
                    "<td>"+type+"</td>" +
                    "<td>"+dataArr[i].componentName+"</td>" +
                    " <td>"+dataArr[i].userName+"</td>" +
                    "<td  onclick='deleteThis(this,"+i+")'><button type='button' style='background-color: #fff0f0;cursor: pointer;width: 40px;height: 24px;line-height:24px;border:1px solid #fac4c4;border-radius:2px;color: #f56b6b !important;font-size: 12px;'>" +
                    " 删除</button></td>" +
                    "</tr>"
                componentList.append(innerHtml)
            }

            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);

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
                $.ajax({
                    type: "post",
                    url: "/component/componentPublish",
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
                })
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
            var url="/component/componentCognizance?componentId="+dataArr[0].componentId;
            window.location.href=encodeURI(url);

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
    };*/
    TopicBin.initButton = function() {
        $("#passBtn").click(function () {
            // openPass('通过', '/examineFile/passView', 500, 245);
        });
        $("#delBtn").click(function () {
            // openDel('删除', '/examineFile/delView', 500, 245);
        });


    };
   /* //.渲染完成回调
    $('.layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
*/
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
        $(window).resize(function() {
            TopicBin.initTableView();
        });
    });
});