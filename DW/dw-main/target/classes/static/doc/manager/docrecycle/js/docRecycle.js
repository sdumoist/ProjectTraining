/**
* @Description:    回收站管理 脚本文件
* @Author:         ChenXin
* @CreateDate:     2018/8/9 10:31
* @UpdateUser:     ChenXin
* @UpdateDate:     2018/8/9 10:31
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
layui.use(['jquery','layer','Hussar','HussarAjax','form','table','jstree'], function(){
    var Hussar = layui.Hussar;
    var $ = layui.jquery;
    var layer = layui.layer;
    var $ax = layui.HussarAjax;
    var form = layui.form;
    var table = layui.table;
    var jstree=layui.jstree;
    var layerView;

    var RecycleBin = {
        tableId: "recycleBinTable",	//表格id
        seItem: null,		//选中的条目
        layerIndex: -1,
        setId:null
    };

    /**
     *  初始化表格
     */
    RecycleBin.initTableView = function () {
        table.render({
            elem: "#"+RecycleBin.tableId,
            url: Hussar.ctxPath+'/docRecycle/docRecycleList',
            page: true,
            id: RecycleBin.tableId,
            height:$(".content").height() - $(".content .layui-form").outerHeight(true) - 10,
            cols: [[
                {type: 'checkbox',width:'40'},
                {field: 'title', title: '文件名', align:'left',width:'43%'},
                {field: 'fileSize', title: '文件大小', align:'center',width:'15%'},
                {field: 'deleteTime', title: '删除时间', align:'center',width:'25%'},
                {field: 'activeTime', title: '有效时间', align:'center',width:'15%'},
            ]],
            where: {
                fileName : $("#fileName").val(),
                //防止IE浏览器第一次请求后从缓存读取数据
                timestamp: (new Date()).valueOf()
            }
        });
        function nameEvts(d) {
            if(d.title!=","&&d.title!=""){
                var src=d.title.split(",")[0];
                var name=d.title.split(",")[1];
                return name;
              //  return "<img src='"+src+"' style='vertical-align: middle'>&nbsp;&nbsp;"+name;
            }else {
                return "-";
            }
        };
        function fileSize(d) {
            if(d.fileSize!=""&&d.fileSize!=undefined){
                return d.fileSize+"&nbsp;KB" ;
            }else {
                return "-";
            }
        };
    }

    /**
     *  初始化按钮事件
     */
    RecycleBin.initButtonEvent = function () {
        //查询按钮
        $("#btnSearch").click(function(){
            RecycleBin.search();
        });
        //还原按钮
        $("#btnRestore").click(function(){
            RecycleBin.restore();
        });
        //清空回收站按钮
        $("#btnEmpty").click(function(){
            RecycleBin.emptyRecycleBin();
        });
        
    }
    RecycleBin.checkIds = function() {
        var checkStatus = table.checkStatus('recycleBinTable'), data = checkStatus.data;
        var ids = [];
        var names=[];
        if (data.length == 0) {
            Hussar.info("请至少选择一条记录");
            return false;
        } else {
            for (var i = 0, l = data.length; i < l; i++) {
                var r = data[i];
                ids.push(r.fileId);
                names.push(r.title);
            }
            var eId = ids.join(',');
            var ename=names.join(',')
            RecycleBin.setId = eId;
            RecycleBin.setName=ename;
            return true;
        }
    };

    /**
     *  清空回收站
     */
    RecycleBin.emptyRecycleBin = function () {
        var queryData = {};
        queryData['fileName'] = $("#fileName").val();
        var tableBak =  table.cache["recycleBinTable"];
        if(tableBak.length>0){
            layer.confirm('确定要清空回收站吗？', function () {
                $.ajax({
                    type: "post",
                    url: "clear",
                    async: false,
                    cache: false,
                    success: function (data) {
                        if (data) {
                            Hussar.success("已全部清空");
                            table.reload("recycleBinTable",
                            {
                                where: queryData,
                                    //防止IE浏览器第一次请求后从缓存读取数据
                                    timestamp: (new Date()).valueOf()
                            });
                        } else {
                            layer.msg("清空失败", {anim:6,icon: 2});
                        }
                    },
                    error: function(){
                        layer.msg("系统出错，请联系管理员", {anim:6,icon: 0});
                    }
                })
            })
        }else{
            layer.msg("回收站已全部清空了", {anim:6,icon: 0});
        }
       /* var operation = function () {
            var ajax = new $ax(Hussar.ctxPath + "/docRecycle/clear", function(data) {
                if (data) {
                    table.reload("recycleBinTable");
                    Hussar.success("已全部清空！");
                }else {
                    Hussar.error("清空失败！");
                }
            }, function(data) {
                Hussar.error("系统出错，请联系管理员！");
            });
            ajax.start();
        }
        Hussar.confirm("确定要清空回收站吗？", operation);*/
    }

    /**
     *  文件还原
     */
    RecycleBin.restore = function () {
        if (RecycleBin.checkIds()) {
            var operation =function(){
                layerView=layer.open({
                    type : 1,
                    area: ['350px','500px'],
                    //shift : 1,
                    shadeClose: false,
                    title : '目录结构',
                    maxmin : true,
                    content : $("#filTree"),
                    success : function() {
                        RecycleBin.initFileTree();
                        layer.close(index);
                    },
                    end: function () {
                        layer.closeAll(index);
                    }
                });

            }
            var index = layer.confirm('确定要还原吗？',operation);        }
    }

    /**
     *  查询
     */
    RecycleBin.search = function () {
        var queryData = {};
        queryData['fileName'] = $("#fileName").val();
        table.reload(
            RecycleBin.tableId,
            {
                where: queryData,
                //防止IE浏览器第一次请求后从缓存读取数据
                    timestamp: (new Date()).valueOf()
            });
    }

    RecycleBin.initFileTree=function () {
        var $tree = $("#fileTree");
        $tree.jstree("destroy");    //二次打开时要先销毁树
        $tree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": Hussar.ctxPath+"/fsFile/getMoveTreeDataLazy?lazy",
                    "data": function (node) {
                        return {"id": node.id,"type":"1"
                        };
                    }
                },
                themes:{
                    theme : "default",
                    dots:false,// 是否展示虚线
                    icons:true,// 是否展示图标
                }
            },
            types: {
                "closed" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/treeFile.png",
                },
                "default" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/treeFile.png",
                },
                "opened" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/openFile.png",
                }
            },
            plugins: [ 'types']
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            var operation =function(){
                var ajax = new $ax(Hussar.ctxPath + "/docRecycle/restore", function(data) {
                    if(data.result == "0"){
                        Hussar.info("文件已存在");
                    }else if(data.result == "1") {
                        $(".layui-laypage-btn").click();
                        layer.close(layerView);
                        Hussar.success("还原成功");
                    }else if(data.result == "3") {
                        Hussar.info("此文件夹没有权限");
                    } else if(data.result == "5") {
                        Hussar.info("个人空间不足");
                    } else {
                        Hussar.error("还原失败");
                    }
                }, function(data) {
                    Hussar.error("系统出错，请联系管理员");
                });
                ajax.set("fileId",RecycleBin.setId);
                ajax.set("folderId",e.node.original.id);
                ajax.set("fileName",RecycleBin.setName);
                ajax.start();
            }
            layer.confirm('确定要还原此目录下吗？',operation);
        });

    }

    /**
     * 页面初始化
     */
    $(function () {
        RecycleBin.initTableView(); //初始化表格
        RecycleBin.initButtonEvent(); //初始化按钮事件
        $(window).resize(function() {
            RecycleBin.initTableView();
        });
    });

});




