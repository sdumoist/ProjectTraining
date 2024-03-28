var tableIns;
var foldAuthManage = {};
layui.use(['jquery',  'tree', 'table', 'util', 'laytpl', 'form', 'layer','Hussar','jstree','HussarAjax',"element"], function () {
    var $ = layui.jquery,
        table = layui.table,
        element = layui.element ,
        laytpl = layui.laytpl,
        form = layui.form,
        layer = layui.layer,
        util = layui.util;
    var Hussar = layui.Hussar,
        $ax = layui.HussarAjax;
    var jstree = layui.jstree;

    //初始化表格
    initTableView = function () {
        tableIns = table.render({
            elem: '#folderList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: Hussar.ctxPath+'/folderExtranetManager/folderExtranetAuthList' //数据接口
            , request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            , limit: 20   //默认值
            , page: true //开启分页
            ,where: {
                //防止IE浏览器第一次请求后从缓存读取数据
                timestamp: (new Date()).valueOf()
            }
            ,even:true
            , cols: [[
                {field: 'folderId', title: 'folderId',  hide: true},
                {type: 'numbers', title: '序号', align: 'center', width: '5%'},
                {field: 'folderName', title: '目录名称', width: '27%', align: "left", templet:function(d){
                    return "<a  title=\""+d.folderName+"\" style = \"cursor: pointer;color:#00a4ff\" target=\"_blank\" href='/personalcenter?menu=11&folderId="+d.folderId+"&folderName="+encodeURI(d.folderName)+"'>"+d.folderName+"</a>";
                }},
                {field: 'folderPath', title: '目录路径', width: '35%', align: "left",templet:function(d){
                    return "<span  title=\""+d.folderPath+"\">"+d.folderPath+"</span>";
                }},
                {field: 'createUserName', title: '操作人',width: '10%', align: "center"},
                {field: 'createUserId', title: '操作人id', width: '10%',hide: true},
                {field: 'createTime', title: '操作时间', width: '15%', align: "center"},
                { fixed: 'right', title: '操作', width: '8%', toolbar: '#delFoldAuth', align: "center"}

            ]] //设置表头
        });

        //头工具栏事件
        table.on('tool(folderList)', function (obj) {
            var ids = obj.data.folderId;
            if(obj.event === 'delFold'){
                layer.confirm('确定要删除所选中的目录吗？', function () {
                    var ajax = new $ax(Hussar.ctxPath + "/folderExtranetManager/delFolderExtranetAuth", function(data) {
                        if (data == true) {
                            layer.msg("删除成功", {icon: 1});
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
                    }, function(data) {

                    });
                    ajax.set("ids",ids);
                    ajax.start();
                })
            }
        });
    },


    // 搜索
        $("#searchBtn").click(function () {
            var folderName = $("#folderNameSearch").val().trim();
            //文件名
            var pattern = new RegExp("^[^/\\\\:\\*\\'\\‘\\?\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if (!pattern.test(folderName)) {
                layer.alert('输入的目录名不合法', {
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
                    folderName: folderName,
                    //防止IE浏览器第一次请求后从缓存读取数据
                    timestamp: (new Date()).valueOf()
                }
            })
        });

    // 目录配置
    $("#manageFolder").click(function () {
       var layerView=layer.open({
            type: 1,
            area: ['30%', '85%'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            btn: ['确定', '取消'],
            shade: 0.4,
            title: ['目录外网访问权限配置','font-size: 14px; font-weight:bold;'],
            content: $("#folderTreeDiv"),
            success:function(){
                foldAuthManage.initFolderTrees();
            },
            btn1:function(index, layero){ // 点击确定按钮 获取选中的目录id
                var ref = $('#folderTree').jstree(true);//获得整个树
                var nodes = ref.get_selected(true);//获得所有选中节点，返回值为数组

                var allFoldData = []; // 所有选中的目录信息
                $.each(nodes, function(i, nd) {
                    allFoldData.push({"folderId": nd.original.id, "folderName": nd.original.text})
                });

                // 保存目录配置数据
                var ajax = new $ax(Hussar.ctxPath + "/folderExtranetManager/saveFolderExtranetAuth",function(result) {
                    if(result.code == 200){
                        Hussar.success("保存成功");
                        layer.close(layerView);

                        tableIns.reload({
                            page: {
                                curr: 1
                            },
                            where: {
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            }
                        })
                    }
                }, function(data) {
                    Hussar.error("保存失败");
                });
                ajax.set("param", JSON.stringify(allFoldData));
                ajax.start();
            },
            btn2:function(index, layero){
                layer.close(layer.index);
            }
        });
    });

    // 加载目录树
    foldAuthManage.initFolderTrees = function(){

        var foldTreeData = [];
        var ajax = new $ax(Hussar.ctxPath + "/folderExtranetManager/findFolderExtranetAuthTree",function(result) {
            foldTreeData = result;
        }, function(data) {
            Hussar.error("获取人员失败");
        });
        ajax.start();

        //初始化数列表
        var $tree = $("#folderTree");
        $tree.data('jstree', false).empty();
        $tree.jstree({
            core: {
                "themes" : {
                    // "stripes" : true,//背景是否显示间纹。
                    "dots": false,//是否显示树连接线
                    "icons": false,//是否显示节点的图标
                    "ellipsis": true //节点名过长时是否显示省略号
                },
                "multiple": true,//单选
                check_callback: true,
                data: foldTreeData
            },
            "checkbox": { // 设置复选框不级联
                "three_state": false,
                "keep_selected_style": false
            },
            plugins: ['search', 'checkbox'],
            search: foldAuthManage.search()

        }).on('loaded.jstree', function (e, data) {

        });
    };


    /**
     * 所有树的模糊查询
     */
    foldAuthManage.search = function(){
        $("#treeSearchName").val("");
        $(".jstree-search").remove();
        $(".search-results").html("");
        var $tree = $("#folderTree");
        var to = false;
        //用户树查询
        $("#treeSearchName").keyup(function () {
            if (to) { clearTimeout(to); }
            to = setTimeout(function () {
                var v = $("#treeSearchName").val();
                if(v==null){
                    v = "";
                }
                var temp = $tree.is(":hidden");
                if (temp == true) {
                    $tree.show();
                }
                //$tree.jstree(true).search(v);
                $tree.jstree('search', v).find('.jstree-search').focus();
                //添加索引
                if(v!=''){
                    var n = $(".jstree-search").length,con_html;
                    if(n>0){
                        con_html = "<em>"+ n +"</em>个匹配项";
                    }else{
                        con_html = "无匹配项";
                    }
                    $(".search-results").html(con_html);
                }else {
                    $(".search-results").html("");
                }
            }, 250);
        });
    }


    $(function () {
        initTableView();//初始化表格
        $(window).resize(function() {
            initTableView();
        });
    });
});

