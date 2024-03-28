var treeData;
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
    initSortTree(treeData);
    //加载分组树
    function initSortTree(data){
        var $tree = $("#sortTree");
        if($tree){
            $tree.jstree("destroy");
        }
        $tree.jstree({
            core: {
                data: data,
                themes:{
                    theme : "default",
                    dots:true,// 是否展示虚线
                    icons:true// 是否展示图标
                }
            },
            plugins: ['contextmenu', 'types', 'search'],
            types: {
                "GROUP": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/roleGroup.png"},
                "isRoot": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/root.png"}
            },
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            parent.document.getElementById('parentSortName').value=e.node.original.text;
            parent.document.getElementById('parentSortId').value=e.node.original.id;;
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
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
            url: Hussar.ctxPath+"/group/getSortTree",
            data:{
                treeType:"2"
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(result){
                var arrays = [];
                if (result.length > 0) {
                    for (var i = 0; i < result.length; i++) {
                        var type = '';
                        if(result[i].PARENT == '#'){
                            type = 'isRoot';
                        }else{
                            type = 'GROUP';
                        }
                        var arr = {
                            id: result[i].ID,
                            text: result[i].TEXT,
                            parent: result[i].PARENT,
                            type:type

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
                    var type = '';
                    if(result[i].PARENT == '#'){
                        type = 'isRoot';
                    }else{
                        type = 'GROUP';
                    }
                    var arr = {
                        id: result[i].ID,
                        text: result[i].TEXT,
                        parent: result[i].PARENT,
                        type:type

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