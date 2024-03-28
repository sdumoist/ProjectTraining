var personData = [];
var pageData = [];
var tableIns;
var buttonType;//点击按钮的类型
var oldData = [];
var treeData;
var RoleManage = {};
var layerIndex;
var layerInitWidth;
var layerInitHeight;
layui.use(['layer', 'Hussar', 'HussarAjax', 'form', 'table', 'jquery', 'element', 'jstree'], function () {
    var layer = layui.layer
        , table = layui.table
        , element = layui.element
        , Hussar = layui.Hussar
        , $ = layui.jquery
        , $ax = layui.HussarAjax,
        jstree = layui.jstree,
        form = layui.form;
    start();
    $(function () {
        //初始化按钮
        initButtonEvent();
        //初始化数据
        initButtontype();
    });
    $(window).resize(function() {
        resizeLayer(layerIndex,layerInitWidth,layerInitHeight);
    });
    function resizeLayer(layerIndex,layerInitWidth,layerInitHeight) {
        var docWidth = $(document).width();
        var docHeight = $(document).height();
        var minWidth = layerInitWidth > docWidth ? docWidth : layerInitWidth;
        var minHeight = layerInitHeight > docHeight ? docHeight : layerInitHeight;
        console.log("doc:",docWidth,docHeight);
        console.log("lay:",layerInitWidth,layerInitHeight);
        console.log("min:",minWidth,minHeight);
        layer.style(layerIndex, {
            top:0,
            width: minWidth,
            height:minHeight
        });
    }
    //点击所属群组后
    $("#parentSortName").click(function(){
        // 先让其他input失去焦点
        $("input").blur();
        var authName=$("#parentSortName").val().trim();
        layerView=layer.open({
            type: 2,
            area: ['350px','450px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: "选择分组",
            content: Hussar.ctxPath+'/group/sortTree',
            success:function(layero, index){
                initSortTree(treeData,authName);
                //layerIndex      = index;
                //layerInitWidth  = $("#layui-layer"+layerIndex).width();
                //layerInitHeight = $("#layui-layer"+layerIndex).height();
                //resizeLayer(layerIndex,layerInitWidth,layerInitHeight);
                //form.render(null,'employeeTreeDiv');
            }
        });
    });
    /*新增/编辑专题*/
    $("#saveBtn").on('click', function () {
        var sortName = $("#sortName").val().trim();//群组名称
        var sortId = $("#sortId").val().trim();//群组ID
        var pattern = new RegExp("^[^/\\\\:\\*\\?\\<\\>\\|\"]{0,255}$"); //特殊字符
        var parentSortName = $("#parentSortName").val().trim();//s所属群组名称
        var parentSortId = $("#parentSortId").val().trim();//s所属群组名称
        var userId = '';
        if (sortName == "") {
            layer.msg("分组名称不能为空", {anim: 6, icon: 0});
            return;
        }
        if (!pattern.test(sortName)) {
            layer.msg("输入的分组名称不合法", {anim: 6, icon: 0});
            return;
        }
        if (sortName.trim().length > 15) {
            layer.msg("分组不能超过15个字符", {anim: 6, icon: 0});
            return;
        }
        if (parentSortId == "") {
            layer.msg("所属群组不能为空", {anim: 6, icon: 0});
            return;
        }
        var url;//请求地址
        var successMsg, errorMsg;//成功失败提示
        var groupId;
        if (buttonType == 'edit') {
            url = "/group/editSort";
            groupId = $('#sortId').val();
        } else {
            url = "/group/addSort";
            groupId = '';
        }
        successMsg = "保存成功";
        errorMsg = "保存失败";
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+url,
            data: {
                sortId: sortId,
                sortName: sortName,
                parentSortName: parentSortName,
                parentSortId: parentSortId
            },
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                if (data.result == "0") {
                    layer.msg("该分组已存在", {anim: 6, icon: 0});
                } else if (data.result == "1") {
                    layer.msg('保存成功',{time:1*1000,icon: 1},function() {
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.window.$("#powerTree").jstree('deselect_all');
                        parent.window.$("#powerTree").jstree(true).select_node(sortId);
                        parent.window.$("#powerTree").jstree(true).refresh();
                        parent.layer.close(index);
                    })
                } else {
                    layer.msg("保存失败", {anim: 6, icon: 0});
                }
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + url, function(data) {
            if (data.result == "0") {
                layer.msg("该分组已存在", {anim: 6, icon: 0});
            } else if (data.result == "1") {
                layer.msg('保存成功',{time:1*1000,icon: 1},function() {
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.window.$("#powerTree").jstree('deselect_all');
                    parent.window.$("#powerTree").jstree(true).select_node(sortId);
                    parent.window.$("#powerTree").jstree(true).refresh();
                    parent.layer.close(index);
                })
            } else {
                layer.msg("保存失败", {anim: 6, icon: 0});
            }
        }, function(data) {

        });
        ajax.set("sortId",sortId);
        ajax.set("sortName",sortName);
        ajax.set("parentSortName",parentSortName);
        ajax.set("parentSortId",parentSortId);
        ajax.start();
    });

    function initButtonEvent() {
        /*关闭弹窗*/
        $("#cancel").on('click', function () {
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        });
    }

    //判断是新增打开窗口还是修改打开窗口
    function initButtontype() {
        if (undefined != $('#sortId').val() && $('#sortId').val() != '') {
            buttonType = 'edit';
        } else {
            buttonType = 'butAdd';
        }
    }
    //加载分组树
    function initSortTree(data,authName){
        var $tree = $("#sortTree");
        if($tree){
            $tree.jstree("destroy");
        }
        $tree.jstree({
            core: {
                data: data,
                themes:{
                    theme : "default",
                    dots:false,// 是否展示虚线
                    icons:true,// 是否展示图标
                }
            },
            plugins: ['contextmenu', 'CODE', 'search'],
            types: {
                "GROUP": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/roleGroup.png"},
                "ROLE": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/role.png"},
                "isRoot": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/root.png"}
            },
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            $("#parentSortName").val(e.node.original.text);
            $("#parentSortId").val(e.node.original.id);
            layer.close(layerView);
            //layer.msg("你点击了："+e.node.original.text+"&nbsp;&nbsp;节点id："+e.node.original.id)
        })
    }
});
//获取分组树数据
function getSort(){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type:"post",
            url:Hussar.ctxPath+"/group/getSortTree",
            data:{
                treeType:"2"
            },
            async:true,
            cache:false,
            dataType:"json",
            success:function(result){
                var arrays = [];
                if (result.length > 0) {
                    for (var i = 0; i < result.length; i++) {
                        var arr = {
                            id: result[i].ID,
                            text: result[i].TEXT,
                            parent: result[i].PARENT
                        }
                        arrays.push(arr);
                    }
                    //$('#groupId').val(result[0].ID)
                    //$('#groupName').val(result[0].TEXT)
                }
                treeData = arrays;
            }, error:function(data) {
                Hussar.error("加载群组列表失败");
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/group/getSortTree", function(result) {
            var arrays = [];
            if (result.length > 0) {
                for (var i = 0; i < result.length; i++) {
                    var arr = {
                        id: result[i].ID,
                        text: result[i].TEXT,
                        parent: result[i].PARENT
                    }
                    arrays.push(arr);
                }
                //$('#groupId').val(result[0].ID)
                //$('#groupName').val(result[0].TEXT)
            }
            treeData = arrays;
        }, function(data) {
            Hussar.error("加载群组列表失败");
        });
        ajax.set("treeType","2");
        ajax.start();
    });
}
function start() {
    getSort();
}