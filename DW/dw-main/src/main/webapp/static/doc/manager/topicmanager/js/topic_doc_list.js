var active;
var index;
layui.use(['form', 'jquery','util','layer','table','Hussar','HussarAjax'], function() {
    var form = layui.form,
        $ = layui.jquery,
        table = layui.table,
        Hussar = layui.Hussar,
        layer = layui.layer,
        $ax=layui.HussarAjax;

    layui.data("childChecked",null);
    //.存储当前页数据集
    var pageData = [];
    var topicId=$("#topicId").val();

    //初始化表格
    var tableIns = table.render({
        elem: '#docList' //指定原始表格元素选择器（推荐id选择器）
        ,height: $(".table-list").height() //容器高度
        ,type:"post",
        dataType:"json",
        url: '/topic/topicShowDoc' ,//数据接口
        where:{
            topicId:topicId
        } ,
        done:function(res) {
            $("[data-field='docId']").hide();
            //.假设你的表格指定的 id="docList"，找到框架渲染的表格
            var tbl = $('#docList').next('.layui-table-view');
            //.记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
        },
        async:true,
        cache:false,

        request: {
            pageName: 'page', //页码的参数名称，默认：page
            limitName: 'limit' //每页数据量的参数名，默认：limit
        }

        ,page: true //关闭分页
        ,cols: [[
            {field: 'doc_id', title: 'id', type: 'checkbox', width: '35', align: "center"},
            {title: '序号', type: 'numbers',width:'40', align: "center"},
            {field:'title',
                title:'文件名称',
                width:'33%',
                align:"left",
                templet:function (data) {
                    var title = data.title;
                    return "<div class='textOver' title='"+title+"'>"+title+"</div>"
                }
            },
            {field:'fileType',title:'文件类型',width:'12%',align:"center",
                templet:function (data) {
                    var title = data.fileType.substring(1);
                    return "<div class='textOver'>"+title+"</div>"
                }
            },
            {field:'fileSize',title:'文件大小',width:'14%',align:"center"},
            {field:'authorName',title:'上传人',width:'13%',align:"center"},
            {field:'createTime',title:'上传时间',width:'20%',align:"center"}
        ]] //设置表头
    });

    //.监听选择，记录已选择项
    table.on('checkbox(docList)', function(obj) {
        if (obj.checked){
            index = $("tr").index(obj.tr);
        }
        //.全选或单选数据集不一样
        var data = obj.type == 'one' ? [obj.data] : pageData;
        //.遍历数据
        $.each(data, function(k, v) {
            //.假设你数据中 id 是唯一关键字
            if (obj.checked) {
                //.增加已选中项
                layui.data('childChecked', {
                    key: v.doc_id, value: v
                });
            } else {
                //.删除
                layui.data('childChecked', {
                    key: v.doc_id, remove: true
                });
            }
        });
    });
    tableIns.reload({
        page: {
            curr: 1
        },
        where: {
            topicId:topicId,
            //防止IE浏览器第一次请求后从缓存读取数据
            timestamp: (new Date()).valueOf()
        }
    })
    //监听工具条
    table.on('tool(docList)', function (obj) {
        var data = obj.data;
        if (obj.event == 'delete') {
            layer.confirm('确定要删除所选中的文件吗？', function(index){
                obj.del();
                layer.close(index);
            });
        }
    });
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);

        parent.layer.close(index);
        window.parent.location.reload({
            where: {
                //防止IE浏览器第一次请求后从缓存读取数据
                timestamp: (new Date()).valueOf()
            }
        });
    });
    /*新增/编辑专题*/
    $("#delBtn").on('click',function(){
        var dataArr = getCheckData();
        var topicId=$("#topicId").val();
        if (dataArr.length == 0) {
            layer.alert('请先选择要删除的文档', {
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
            layui.data('childChecked', {
                key: dataArr[i].doc_id, remove: true
            });
            if (i == 0) {
                ids = dataArr[i].doc_id;
            } else {
                ids += ',' + dataArr[i].doc_id;
            }
        }
        layer.confirm('确定要删除所选中的文档吗？', function () {
            /*$.ajax({
                type: "post",
                url: "/topic/delDocs",
                data: {
                    ids: ids,
                    topicId:topicId
                },
                async: false,
                cache: false,
                success: function (data) {
                    if (data == dataArr.length) {
                        Hussar.success("删除成功")
                        tableIns.reload({
                            where: {
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf(),
                                topicId:topicId
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
                    } else {
                        Hussar.error("删除失败");
                        /!*layer.alert('删除失败', {
                         icon: 0,
                         shadeClose: true,
                         skin: 'layui-layer-molv',
                         shift: 5,
                         area: ['300px', '180px'],
                         title: '提示'
                         });*!/
                    }
                }
            })*/

            var ajax = new $ax(Hussar.ctxPath + "/topic/delDocs", function(data) {
                if (data == dataArr.length) {
                    Hussar.success("删除成功")
                    tableIns.reload({
                        where: {
                            //防止IE浏览器第一次请求后从缓存读取数据
                            timestamp: (new Date()).valueOf(),
                            topicId:topicId
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
                } else {
                    Hussar.error("删除失败");
                    /*layer.alert('删除失败', {
                     icon: 0,
                     shadeClose: true,
                     skin: 'layui-layer-molv',
                     shift: 5,
                     area: ['300px', '180px'],
                     title: '提示'
                     });*/
                }

            }, function(data) {


            });
            ajax.set("ids",ids);
            ajax.set("topicId",topicId);
            ajax.start();

        })
    });

    $("#moveUp").on('click',function(){
        var dataArr = getCheckData();
        if (dataArr.length != 1) {
            layer.alert('请选择一条要上移的文件', {
                icon: 0,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
            return;
        }
       // var index = dataArr[0].LAY_TABLE_INDEX;
        var data = table.cache["docList"];

        var $tr = $("tr[data-index = " + (index-1) + "]");
        var upNum = index - 2;
        if ($tr.index() != 0) {
            /*$.ajax({
                type: "post",
                url: "/topic/moveTopic",
                data: {
                    table:"doc_special_topic_files",
                    idColumn:"topic_file_id",
                    idOne: dataArr[0].topicFileId,
                    idTwo: data[upNum].topicFileId
                },
                async: false,
                cache: false,
                dataType: "json",
                success: function (result) {
                    if (result > 0) {
                        tableIns.reload({
                            where:{
                                topicId:topicId,
                                //防止IE浏览器第一次请求后从缓存读取数据
                                dates: (new Date()).valueOf()
                            },
                            done: function (res, curr, count) {
                                var td = $('#docList').next().find("tr[data-index='" + upNum + "'] div.layui-form-checkbox");
                                td.click();
                            }
                        });
                        layui.data('childChecked', null);
                    }
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/topic/moveTopic", function(result) {
                if (result > 0) {
                    tableIns.reload({
                        where:{
                            topicId:topicId,
                            //防止IE浏览器第一次请求后从缓存读取数据
                            dates: (new Date()).valueOf()
                        },
                        done: function (res, curr, count) {
                            var td = $('#docList').next().find("tr[data-index='" + upNum + "'] div.layui-form-checkbox");
                            td.click();
                        }
                    });
                    layui.data('childChecked', null);
                }

            }, function(data) {


            });
            ajax.set("table","doc_special_topic_files");
            ajax.set("idColumn","topic_file_id");
            ajax.set("idOne",dataArr[0].topicFileId);
            ajax.set("idTwo",data[upNum].topicFileId);
            ajax.start();
            /*$tr.fadeOut().fadeIn();
             $tr.prev().before($tr);*/
        } else {
            layer.alert('已经上移到最顶端', {
                icon: 0,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
        }
    });
    $("#moveDown").on('click',function(){
        var dataArr = getCheckData();
        if (dataArr.length != 1) {
            layer.alert('请选择一条要下移的文件', {
                icon: 0,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
            return;
        }
       // var index = dataArr[0].LAY_TABLE_INDEX;
        var downNum = index;
        var data = table.cache["docList"];
        var $tr = $("tr[data-index = " + (index-1) + "]");
        var next = $tr.next();
        if (next.length == 0) {
            layer.alert('已经下移到最底端', {
                icon: 0,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
            return;
        }
        if (next) {
            /*$.ajax({
                type: "post",
                url: "/topic/moveTopic",
                data: {
                    table:"doc_special_topic_files",
                    idColumn:"topic_file_id",
                    idOne: dataArr[0].topicFileId,
                    idTwo: data[downNum].topicFileId
                },
                async: false,
                cache: false,
                dataType: "json",
                success: function (result) {
                    if (result > 0) {
                        tableIns.reload({
                            where:{
                                topicId:topicId,
                                //防止IE浏览器第一次请求后从缓存读取数据
                                dates: (new Date()).valueOf()
                            },
                            done: function (res, curr, count) {
                                var td = $('#docList').next().find("tr[data-index='" + downNum + "'] div.layui-form-checkbox");
                                td.click();
                            }
                        });
                        layui.data('childChecked', null);
                    }
                }
            });*/

            var ajax = new $ax(Hussar.ctxPath + "/topic/moveTopic", function(result) {
                if (result > 0) {
                    tableIns.reload({
                        where:{
                            topicId:topicId,
                            //防止IE浏览器第一次请求后从缓存读取数据
                            dates: (new Date()).valueOf()
                        },
                        done: function (res, curr, count) {
                            var td = $('#docList').next().find("tr[data-index='" + downNum + "'] div.layui-form-checkbox");
                            td.click();
                        }
                    });
                    layui.data('childChecked', null);
                }

            }, function(data) {


            });
            ajax.set("table","doc_special_topic_files");
            ajax.set("idColumn","topic_file_id");
            ajax.set("idOne",dataArr[0].topicFileId);
            ajax.set("idTwo",data[downNum].topicFileId);
            ajax.start();
            /*$tr.fadeOut().fadeIn();
             $tr.next().after($tr);*/
        }
    });

    function  getCheckData() {   var mySelected = [];
        //.看看已选中的所有数据
        var checkStatus = table.checkStatus('docList'), mySelected = checkStatus.data;

//                $.each(layui.data('checked'), function (k, v) {
//                    mySelected.push(v);
//                });
//                debugger;
        return mySelected;

    }


    function getMoveCheckData(){ //获取选中数据
        //.看看已选中的所有数据
        var mySelected = [];
        $.each(layui.data('childChecked'), function(k, v) {
            mySelected.push(v);
        });
        return mySelected;
    }

    $(window).resize(function () {
        table.render({
            elem: '#docList' //指定原始表格元素选择器（推荐id选择器）
            ,height: $(".table-list").height() //容器高度
            ,type:"post",
            dataType:"json",
            url: '/topic/topicShowDoc' ,//数据接口
            where:{
                topicId:topicId
            } ,
            done:function(res) {
                $("[data-field='docId']").hide();
                //.假设你的表格指定的 id="docList"，找到框架渲染的表格
                var tbl = $('#docList').next('.layui-table-view');
                //.记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
            },
            async:true,
            cache:false,

            request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }

            ,page: true //关闭分页
            ,cols: [[
                {field: 'doc_id', title: 'id', type: 'checkbox', width: '35', align: "center"},
                {title: '序号', type: 'numbers',width:'40', align: "center"},
                {field:'title',
                    title:'文件名称',
                    width:'33%',
                    align:"left",
                    templet:function (data) {
                        var title = data.title;
                        return "<div class='textOver' title='"+title+"'>"+title+"</div>"
                    }
                },
                {field:'fileType',title:'文件类型',width:'12%',align:"center",
                    templet:function (data) {
                        var title = data.fileType.substring(1);
                        return "<div class='textOver'>"+title+"</div>"
                    }
                },
                {field:'fileSize',title:'文件大小',width:'14%',align:"center"},
                {field:'authorName',title:'上传人',width:'13%',align:"center"},
                {field:'createTime',title:'上传时间',align:"center"}
            ]] //设置表头
        })
    })
});
