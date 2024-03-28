/**
 * @Description: 用户管理脚本文件
 * @Author: liangdong
 * @Date: 2018/2/27.
 */
layui.use(['jquery', 'layer', 'Hussar', 'jstree', 'HussarAjax', 'element', 'form',  'upload', 'laydate'], function () {
    var Hussar = layui.Hussar;
    var $ = layui.jquery;
    var layer = layui.layer;
    var laydate = layui.laydate;
    var $ax = layui.HussarAjax;
    var form = layui.form;
    var element = layui.element;
    var upload = layui.upload;
    var layerView;

    var UserMgr = {
        userTree: $("#userTree"),  //用户树
    };

    /**
     * 初始化用户树
     */
    UserMgr.initUserTree = function () {
        $userTree = UserMgr.userTree;  //用户树
        $userTree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": Hussar.ctxPath + "/user/userTree",
                    "data": function (node) {
                        return {
                            "parentId": node.id,
                            "level" : node.parents.length,
                            "isExport": true
                        };
                    }
                }
            },
            checkbox: {
                "three_state": false,
                "keep_selected_style": false
            },
            contextmenu: {
                select_node: false,
                show_at_node: true
            },
            plugins: [ 'types', 'search','checkbox'],
            types: {
                "1": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/com.png"},
                "2": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/dept.png"},
                "3": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/station.png"},
                "9": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/empl.png"},
                "USER": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/user.png"},
                "isRoot": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/root.png"}
            },
            search: UserMgr.search()
        });

        $userTree.bind("activate_node.jstree", function (obj, e) {
            var node = e.node;
            if (node.original.code == 'USER') {
                $("#sData").hide();
                $("#eData").hide();
            } else {
                $('#addEvent').addClass('hide');
            }
        });
    };


    /**
     * 所有树的模糊查询
     */
    UserMgr.search = function () {
        var to = false;
        //用户树查询
        $("#userTreeSearch").keyup(function () {
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                var v = $("#userTreeSearch").val();
                var temp = $userTree.is(":hidden");
                if (temp == true) {
                    $userTree.show();
                }
                $userTree.jstree(true).search(v);
                //添加索引
                if (v != '') {
                    var n = $(".jstree-search").length, con_html;
                    if (n > 0) {
                        con_html = "<em>" + n + "</em>个匹配项";
                    } else {
                        con_html = "无匹配项";
                    }
                    $("#userTreeSearchResult").html(con_html);
                    //定位到符合查询结果的树节点上
                    var searchResult = $userTree.jstree('search', v);
                    $(searchResult).find('.jstree-search').focus();
                } else {
                    $("#userTreeSearchResult").html("");
                }
            }, 250);
        });

    };


    /**
     *  回收权限
     */
    UserMgr.initButtonEvent = function () {
        $("#recyclePermission").click(function () {
            // 获取选中的用户id
            var $tree = $("#userTree");
            var ref = $tree.jstree(true);
            var nodes = ref.get_checked(true);  //使用get_checked方法

            var recycleUserIds = [];
            var recycleUserName = [];
            $.each(nodes, function (i, nd) {
                recycleUserIds.push(nd.id);
                recycleUserName.push(nd.text);
            });

            if(recycleUserName.length == 0 ){
                Hussar.info("请选择回收权限的用户");
                return;
            }

            var msg = "确认回收用户【 "+recycleUserName.join(",")+" 】的权限吗，此操作会删除所选用户的所有目录和文件权限!!!";
            layer.confirm(msg, {
                title: ['回收权限', 'background-color:#fff'],
                skin: 'back-auth'
            }, function () {
                debugger
                var index = layer.load(1, {
                    shade: [0.1, '#fff'] //0.1透明度的白色背景
                });
                    var ajax = new $ax(Hussar.ctxPath + "/recyclePermission/recycle", function (data) {
                        if (data.code == "200") {
                            Hussar.success("回收用户权限成功");
                            UserMgr.refreshTree(); //刷新用户树
                            layer.close(index);
                        } else {
                            Hussar.error("回收用户权限失败!");
                            layer.close(index);
                        }
                    }, function (data) {
                        Hussar.error("回收用户权限失败!");
                        layer.close(index);
                    });
                    ajax.set("userIds", recycleUserIds.join(","));
                    ajax.start();
            })

        })
    },

    /* 刷新用户树 */
    UserMgr.refreshTree = function () {
        $("#userTree").jstree().deselect_all(true);
    },

    $(function () {
        UserMgr.initUserTree(); //初始化用户树
        UserMgr.initButtonEvent();	//初始化按钮事件
    });
});

