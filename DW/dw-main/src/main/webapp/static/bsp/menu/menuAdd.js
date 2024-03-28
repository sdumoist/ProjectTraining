/**
 * @Description: 菜单新增
 * @Author: sunZengXin
 * @Date: 2018/2/6.
 */
layui.use(['jquery', 'layer', 'Hussar', 'form', 'jstree', 'fontIconPicker', 'HussarAjax'], function () {
    var form = layui.form,
        laydate = layui.laydate,
        $ = layui.jquery,
        Hussar = layui.Hussar,
        $ax = layui.HussarAjax;
    var MenuAdd = {
        id: "menuAdd"
    }
    MenuAdd.initPage = function () {
        MenuAdd.initSourceTreeView();
        MenuAdd.initButtonEvent();
        $('#myselect-inverted').fontIconPicker({
            theme: 'fip-inverted',
            jsonUrl: Hussar.ctxPath + '/menu/allIcons',//访问json的url
            fontClass: 'customFont'//引入图标的类名
        });
    }
    MenuAdd.initButtonEvent = function () {
        //选择图标
        $("#iconSelect").click(function () {
            layer.open({
                type: 1,
                btn: ['确认', '关闭'],
                area: ['500px', '400px'],
                fix: false, //不固定
                maxmin: true,
                shadeClose: true,
                shade: 0.4,
                title: "选择资源",
                content: "",
                yes: function (index, layero) {
                },
                success: function () {
                }
            });
        });
        //选择资源
        $("#resourceName").click(function () {
            layer.open({
                type: 1,
                btn: ['取消', '保存'],
                skin: 'confirm-class',
                area: ['350px', '500px'],
                fix: false, //不固定
                maxmin: false,
                shadeClose: false,
                shade: 0.4,
                title: "选择资源",
                content: $("#sourceTreeDiv"),
                btn2: function (index, layero) {
                    //获取选中节点
                    var nodes = $("#sourceTree").jstree(true).get_checked();//使用get_checked方法
                    if (nodes.length == 0) {
                        Hussar.info(CHOOSE_RESOURCES);
                    } else {
                        var resourceName = $("#resourceNameHidden").val();
                        $("#resourceName").val(resourceName);
                        $("#resourceId").val(nodes[0]);
                        layer.close(index);
                    }

                },
                success: function () {
                    $(".layui-layer-btn0").prepend("<i class='iconfont'>&#x1006</i>");
                    $(".layui-layer-btn1").prepend("<i class='iconfont'>&#xe009</i>");
                    //资源名称
                    $("#resourceNameHidden").val("");
                    var ajax = new $ax(Hussar.ctxPath + "/resource/resTree",
                        function (result) {
                            $("#sourceTree").jstree(true).settings.core.data = result;
                            $("#sourceTree").jstree(true).refresh();
                        }, function (data) {
                            Hussar.error(LOAD_TREE_FAIL);
                        });
                    ajax.set("type", "menuRes");
                    ajax.start();
                }
            });
        })
        //开关监听
        form.on('switch(isLeaf)', function (data) {
            if (data.elem.checked == true) {
                $("#resourceDiv").attr("style", "display:block;");
                $("#openTypeDiv").attr("style", "display:block;");
                $("#resourceName").attr("lay-verify", "required");
                $("#resourceId").attr("lay-verify", "required");
                //清空资源
                $("#resourceName").val("");
                $("#resourceId").val("");
                $("#resourceNameHidden").val("");
            } else {
                $("#resourceDiv").attr("style", "display:none;");
                $("#openTypeDiv").attr("style", "display:none;");
                $("#resourceName").removeAttr("lay-verify");
                $("#resourceId").removeAttr("lay-verify");
                //清空资源
                $("#resourceName").val("");
                $("#resourceId").val("");
                $("#resourceNameHidden").val("");
            }
        });
        //监听提交
        form.on('submit(menuAdd)', function (data) {
            var loadIndex = layer.load(1,{shade:true});
            var formData = data.field;
            if (formData.icons != ""){
                var formIcons = data.field.icons.charCodeAt();
                formData.icons = "&#x" + formIcons.toString(16);
            }
            if (formData.isLeaf) {
                formData.isLeaf = 1;
            } else {
                formData.isLeaf = 0;
            }
            var ajax = new $ax(Hussar.ctxPath + "/menu/menuInfoSave",
                function (result) {
                    window.parent.layui.Hussar.success(SAVE_SUCCESS);
                    // 刷新菜单树
                    window.parent.MenuManage.refreshTree();
                }, function (data) {
                    layer.close(loadIndex);
                    Hussar.error(SAVE_FAIL);
                });
            ajax.setData(formData)
            ajax.start();
            return false;
        });
    }
    /*初始化资源树*/
    MenuAdd.initSourceTreeView = function () {
        //初始化数列表
        var $tree = $("#sourceTree");
        $tree.data('jstree', false).empty();
        $tree.jstree({
            plugins: ['state', 'types', 'checkbox', 'search'],
            types: {
                "isRoot": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/root.png"},
                "isModule": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/module.png"},
                "isFun": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/function.png"},
                "1": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/resource_menu.png"}, //菜单资源
                "2": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/resource_btn.png"},  //按钮资源
            },
            core: {
                check_callback: true,
                data: null,
                multiple: false//单选
            },
            sort: function (a, b) {
                return this.get_node(a).original.seq > this.get_node(b).original.seq ? 1 : -1;
            },
            checkbox: {
                keep_selected_style: false,
                three_state: false,
                tie_selection: false
            },
            search: MenuAdd.searchResource()
        });
        $tree.bind("activate_node.jstree", function (obj, e) {
            // 处理代码
            // 获取当前节点
            var currentNode = e.node;
        });
        $tree.on('check_node.jstree', function (event, obj) {
            var currentNode = obj.node;
            var isRes = obj.node.original.isRes;
            var menuName = obj.node.text;
            var ref = $tree.jstree(true);
            var nodes = ref.get_checked(); //使用get_checked方法
            var otherNode;
            //判断是否为资源
            if (isRes != 1) {
                ref.uncheck_node(obj.node.id);
                return;
            }
            //限制只能选一个
            if (nodes.length > 0) {
                $.each(nodes, function (i, nd) {
                    if (nd != obj.node.id) {
                        otherNode = nd;
                    }
                })
                ref.uncheck_node(otherNode);
                $("#resourceNameHidden").val(menuName)
            }
        });
    }

    /**
     * 资源树查询
     */
    MenuAdd.searchResource = function () {
        var to = false;
        $("#searchResource").keyup(function () {
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                var v = $("#searchResource").val();
                var temp = $("#sourceTree").is(":hidden");
                if (temp == true) {
                    $("#sourceTree").show();
                }
                $("#sourceTree").jstree(true).search(v);
                //添加索引
                if (v != '') {
                    //定位到符合查询结果的树节点上
                    var searchResult = $("#sourceTree").jstree('search', v);
                    $(searchResult).find('.jstree-search').focus();
                } else {
                }
            }, 250);
        });

    };


    $(function () {
        MenuAdd.initPage();
    });
});