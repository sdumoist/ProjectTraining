var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var treedata;
var parentSortNames;
var parentSortIds;
layui.use(['jquery', 'layer', 'Hussar', 'jstree', 'table', 'HussarAjax', 'form', 'element','Hussar'], function () {
    var Hussar = layui.Hussar;
    var $ = layui.jquery;
    var layer = layui.layer;
    var $ax = layui.HussarAjax;
    var jstree = layui.jstree;
    var table = layui.table;
    var form = layui.form;
    var RoleManage = {};
    var element = layui.element;
    var Hussar = layui.Hussar;
    //.存储当前页数据集
    var pageData = [];
    //.存储已选择数据集，用普通变量存储也行
    layui.data('checked', null);
    /*初始化页面*/
    RoleManage.initPage = function () {
        this.initTree();
        this.init();
        this.initButtonEvent();
        start()
        //初始化数据
        initButtontype()
    }
    RoleManage.initTree = function () {
        //初始化数列表
        var $tree = $("#powerTree");
        $tree.data('jstree', false).empty();
        $tree.jstree({
            core: {
                "themes" : {
                    // "stripes" : true,//背景是否显示间纹。
                    "dots": false,//是否显示树连接线
                    // "icons": true,//是否显示节点的图标
                    // "ellipsis": true//节点名过长时是否显示省略号
                },
                "multiple": false,//单选
                check_callback: true,
                data: function (obj, callback) {
                    var data;
                    var ajax = new $ax(Hussar.ctxPath + "/group/sortAndGroupTree", function (result) {
                        var arrays = [];
                        if (result.length > 0) {
                            for (var i = 0; i < result.length; i++) {
                                var arr = {
                                    id: result[i].ID,
                                    code: result[i].CODE,
                                    text: result[i].TEXT,
                                    parent: result[i].PARENT,
                                    type: result[i].CODE,
                                    user: result[i].USERID,
                                    issort: result[i].ISSORT
                                }
                                arrays.push(arr);
                            }
                            $('#groupId').val(result[0].ID)
                            $('#groupName').val(result[0].TEXT)
                            $('#usersId').val(result[0].USERID)
                            $('#isSort').val(result[0].ISSORT)
                        }
                        data = arrays;
                        treedata = arrays;
                    }, function (data) {
                        Hussar.error("加载用户组列表失败");
                    });
                    ajax.set("treeType", 1);
                    ajax.set("flag",'1');
                    ajax.start();
                    callback.call(this, data);
                }
            },
            contextmenu: {
                select_node: true,
                show_at_node: true,
                'items': RoleManage.contextmenu
            },
            plugins: ['contextmenu', 'CODE','types', 'search'],
            types: {
                "GROUP": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/blue/roleGroup.png"},
                "ROLE": {'icon': Hussar.ctxPath + "/static/resources/img/fsfile/group-blue.png"},
                "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/root.png"},
                "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/station.png"},
                "4":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/deptOld.png"},
                "5":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/deptOld.png"},
                "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/user.png"}
            },
            search: RoleManage.search()

        }).on('loaded.jstree', function (e, data) {
            var inst = data.instance;
            if (undefined != e.target.firstChild.firstChild.lastChild.firstChild) {
                var obj = inst.get_node(e.target.firstChild.firstChild.lastChild.firstChild);
                inst.select_node(obj);
            }
        });
        $tree.bind('select_node.jstree', function (obj, e) {
            $('#groupName').val(e.node.text);
            $('#groupId').val(e.node.id);
            $('#usersId').val(e.node.original.user);
            $('#isSort').val(e.node.original.issort);
            if($('#loginId').val() == $('#usersId').val() || $('#loginId').val() == 'admin'){
                if(e.node.original.issort == 0){
                    $("#addSortBtn").hide();
                    $("#addBtn").hide();
                    $("#upBtn").show();
                    $("#delBtn").show();
                }else{
                    $("#addSortBtn").show();
                    $("#addBtn").show();
                    $("#upBtn").show();
                    $("#delBtn").show();
                }
            }else{
                if(e.node.original.issort == 0){
                    $("#addSortBtn").hide();
                    $("#addBtn").hide();
                    $("#upBtn").hide();
                    $("#delBtn").hide();
                }else{
                    $("#addSortBtn").show();
                    $("#addBtn").show();
                    $("#upBtn").hide();
                    $("#delBtn").hide();
                }
            }
            if(e.node.original.issort != '0'){
                $("#parentSortName").val(e.node.text);
                $("#parentSortId").val(e.node.id);
            }
            if(e.node.parent == '#'){ // 根节点
                $("#upBtn").hide();
                $("#delBtn").hide();
            }
            tableIns.reload({
                page: {
                    curr: 1
                },
                where: {
                    groupId: e.node.id
                }
            })
        })
    };
    RoleManage.init = function () {
        //初始化表格
        tableIns = table.render({
            elem: '#groupList', //指定原始表格元素选择器（推荐id选择器）
            height: $("body").height() - $(".toolBar").height() - 35, //容器高度
            url: Hussar.ctxPath+'/group/groupList', //数据接口
            id: 'groupList',
            where: {
                groupId: $('#groupId').val()
            },
            done: function (res) {
                //.假设你的表格指定的 id="topicList"，找到框架渲染的表格
                var tbl = $('#groupList').next('.layui-table-view');
                //记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
                pageData = res.data;
                var len = pageData.length;
                //.遍历当前页数据，对比已选中项中的 id
                for (var i = 0; i < len; i++) {
                    if (layui.data('checked', pageData[i]['userId'])) {
                        //选中它，目前版本没有任何与数据或表格 id 相关的标识，不太好搞，土办法选择它吧
                        tbl.find('table>tbody>tr').eq(i).find('td').eq(0).find('input[type=checkbox]').prop('checked', true);
                    }
                }
                form.render('checkbox');
                //.PS：table 中点击选择后会记录到 table.cache，没暴露出来，也不能 mytbl.renderForm('checkbox');
            }
            ,
            request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            ,
            page: true, //开启分页
            //id : 'groupListView',
            even: true,
            cols: [[
                {field: 'userId', title: 'id', type: 'checkbox', width: '0', align: "center"},
                {title: '序号', type: 'numbers', width: '40', align: "center"},
                {field: 'orgName', title: '部门名称', width: '38%', align: "center"},
                {field: 'userName', title: '人员名称', align: "center", width: '30%'},
                {field: 'userJob', title: '职务', align: "center", width: '30%'}
            ]] //设置表头
        });
        //.监听选择，记录已选择项
        table.on('checkbox(groupList)', function (obj) {
            //.全选或单选数据集不一样
            var data = obj.type == 'one' ? [obj.data] : pageData;
            //.遍历数据
            $.each(data, function (k, v) {
                //.假设你数据中 id 是唯一关键字
                if (obj.checked) {
                    //.增加已选中项
                    layui.data('checked', {
                        key: v.userId, value: v
                    });
                } else {
                    //.删除
                    layui.data('checked', {
                        key: v.userId, remove: true
                    });
                }
            });
        });
    }
    //格式化按钮
    RoleManage.initButtonEvent = function () {
        $("#searchBtn").on('click', function () {
            tableIns.reload({
                page: {
                    curr: 1
                },
                where: {
                    groupId: $('#groupId').val(),
                    uerName: $('#searchName').val()
                }
            })
        });
        //单击选项卡按钮
        $("#addBtn").on('click', function () {
            buttonType = 'addGroup';
            var isSort = $("#isSort").val();
            var groupName = $('#groupName').val();
            if (isSort == "0") {
                layer.msg("请选择分组", {anim: 6, icon: 0});
            } else {
                openTopic('新增用户组', '/group/groupAdd?flag=1&sortId=' + $("#groupId").val(), 840, 550);
            }
        });
        $("#delBtn").on('click', function () {
            var mas = "";
            var url = "";
            if ($("#loginId").val() == 'admin' || $('#loginId').val() == $('#usersId').val()) {
                if( $("#isSort").val() == '0'){
                    mas = "确定要删除当前用户组吗？";
                    url = '/group/delGroupById';
                }else{
                    mas = "确定要删除当前分组吗？";
                    url = '/group/deleteSort';
                }
                delebtn(mas,url);
            } else {
                layer.msg("您没有删除权限", {anim: 6, icon: 0});
            }
        });
        //修改
        $("#upBtn").on('click',function(){
            var id = $("#groupId").val();
            if ($("#loginId").val() == 'admin' ||  $('#loginId').val() == $('#usersId').val()) {
                if($("#isSort").val() == '0'){
                    buttonType = 'editGroup';
                    openEdit('修改用户组', '/group/groupEdit', id, 840, 550);
                }else{
                    buttonType = 'editGroup';
                    //openEdit('分组修改', '/personalGroup/sortEdit', id, 620, 160);
                    openSort('修改分组',id)
                }
            } else {
                layer.msg("您没有修改权限", {anim: 6, icon: 0});
            }
        });
        //单击新增分组按钮
        $("#addSortBtn").on('click', function () {
            buttonType = 'addSort';
            var isSort = $("#isSort").val();
            // 0 为群组 1 为分组
            if (isSort == "0") {
                layer.msg("请选择分组", {anim: 6, icon: 0});
            } else {
                //openTopic('新增分组', '/personalGroup/sortAdd?groupId=' + $("#groupId").val() + '&groupName=' + $("#groupName").val(), 620, 160);
                openSort('新增分组','');
            }
        });
    }
    /**
     * 树查询
     */
    RoleManage.search = function () {
        var to = false;
        $("#txtIndustryArea").keyup(function () {
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                var v = $("#txtIndustryArea").val();
                var temp = $("#powerTree").is(":hidden");
                if (temp == true) {
                    $("#powerTree").show();
                }
                $("#powerTree").jstree(true).search(v);
                //添加索引
                if (v != '') {
                    var n = $(".jstree-search").length, con_html;
                    if (n > 0) {
                        con_html = "<em>" + n + "</em>个匹配项";
                    } else {
                        con_html = "无匹配项";
                    }
                    $(".search-results").html(con_html);
                } else {
                    $(".search-results").html("");
                }
            }, 250);
        });
    }
    /**
     * 右键菜单
     */
    RoleManage.contextmenu = function (node) {
        var items = {
            'item2': {
                'label': '新增分组',
                'icon': Hussar.ctxPath + "/static/assets/img/treeContext/5011.png",
                'action': function () {
                    buttonType = 'addGroup';
                    //openTopic('新增分组', '/personalGroup/sortAdd?groupId=' + $("#groupId").val() + '&groupName=' + $("#groupName").val(), 620, 160);
                    openSort('新增分组',node.id);
                }
            },
            'item1': {
                'label': '新增用户组',
                'icon': Hussar.ctxPath + "/static/assets/img/treeContext/5011.png",
                'action': function () {
                    buttonType = 'addGroup';
                    openTopic('新增用户组', '/group/groupAdd?flag=1&sortId=' + $("#groupId").val(), 840, 550);
                }
            },
            'item3': {
                'label': '修改',
                'icon': Hussar.ctxPath + "/static/assets/img/treeContext/53.png",
                'action': function () {
                    if ($("#loginId").val() == 'admin' || $("#loginId").val() == node.original.user) {
                        if(node.original.issort == '0'){
                            buttonType = 'editGroup';
                            openEdit('修改用户组', '/group/groupEdit', node.id, 840, 550);
                        }else{
                            buttonType = 'editGroup';
                            //openEdit('分组修改', '/personalGroup/sortEdit', node.id, 620, 160);
                            openSort('修改分组',node.id);
                        }
                    } else {
                        layer.msg("您没有修改权限", {anim: 6, icon: 0});
                    }
                }
            },
            'item4': {
                'label': '删除',
                'icon': Hussar.ctxPath + "/static/assets/img/treeContext/57.png",
                'action': function () {
                    if ($("#loginId").val() == 'admin' || $("#loginId").val() == node.original.user) {
                        var mas = "";
                        var url = "";
                        if(node.original.issort == '0'){
                            mas = "确定要删除当前用户组吗？";
                            url = '/group/delGroupById';
                        }else{
                            mas = "确定要删除当前分组吗？";
                            url = '/group/deleteSort';
                        }
                        layer.confirm(mas, function () {
                            /*$.ajax({
                                type: "post",
                                url: Hussar.ctxPath+url,
                                data: {
                                    groupId: node.id
                                },
                                async: false,
                                cache: false,
                                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                                success: function (data) {
                                    if (data == 1) {
                                        layer.alert('删除成功', {
                                            icon: 1,
                                            shadeClose: false,
                                            skin: 'layui-layer-molv',
                                            shift: 5,
                                            area: ['300px', '180px'],
                                            title: '提示',
                                            end: function () {
                                                var index = layer.alert();
                                                layer.close(index);
                                                window.location.reload(true)
                                                /!* RoleManage.initTree();
                                                 tableIns.reload();*!/
                                            }
                                        }, function () {
                                            var index = layer.alert();
                                            layer.close(index);
                                            window.location.reload(true)
                                            /!* RoleManage.initTree();
                                             tableIns.reload();*!/
                                        });
                                    } else if(data == 2){
                                        layer.alert('请先删除子群组或分组', {
                                            icon: 0,
                                            shadeClose: false,
                                            skin: 'layui-layer-molv',
                                            shift: 5,
                                            area: ['300px', '180px'],
                                            title: '提示'
                                        });
                                    } else if(data == 6){
                                        layer.alert('该群组已被授权，无法删除', {
                                            icon: 0,
                                            shadeClose: false,
                                            skin: 'layui-layer-molv',
                                            shift: 5,
                                            area: ['300px', '180px'],
                                            title: '提示'
                                        });
                                    }else{
                                        layer.alert('删除失败', {
                                            icon: 2,
                                            shadeClose: false,
                                            skin: 'layui-layer-molv',
                                            shift: 5,
                                            area: ['300px', '180px'],
                                            title: '提示'
                                        });
                                    }
                                }
                            })*/
                            var ajax = new $ax(Hussar.ctxPath + url, function(data) {
                                if (data == 1) {
                                    // layer.alert('删除成功', {
                                    //     icon: 1,
                                    //     shadeClose: false,
                                    //     skin: 'layui-layer-molv',
                                    //     shift: 5,
                                    //     area: ['300px', 'auto'],
                                    //     title: '提示',
                                    //     end: function () {
                                    //         var index = layer.alert();
                                    //         layer.close(index);
                                    //         window.location.reload(true)
                                    //         /* RoleManage.initTree();
                                    //          tableIns.reload();*/
                                    //     }
                                    // }, function () {
                                    //     var index = layer.alert();
                                    //     layer.close(index);
                                    //     window.location.reload(true)
                                    //     /* RoleManage.initTree();
                                    //      tableIns.reload();*/
                                    // });
                                    layer.msg('删除成功',{time:1*1000,icon: 1},function() {
                                        var index = layer.alert();
                                        layer.close(index);
                                        window.location.reload(true)
                                        /* RoleManage.initTree();
                                         tableIns.reload();*/
                                    })
                                } else if(data == 2){
                                    layer.alert('请先删除子用户组或分组', {
                                        icon: 0,
                                        shadeClose: false,
                                        skin: 'layui-layer-molv',
                                        shift: 5,
                                        area: ['300px', 'auto'],
                                        title: '提示'
                                    });
                                } else if(data == 6){
                                    layer.alert('该用户组已被授权，无法删除', {
                                        icon: 0,
                                        shadeClose: false,
                                        skin: 'layui-layer-molv',
                                        shift: 5,
                                        area: ['300px', 'auto'],
                                        title: '提示'
                                    });
                                }else{
                                    layer.alert('删除失败', {
                                        icon: 2,
                                        shadeClose: false,
                                        skin: 'layui-layer-molv',
                                        shift: 5,
                                        area: ['300px', 'auto'],
                                        title: '提示'
                                    });
                                }
                            }, function(data) {

                            });
                            ajax.set("groupId",node.id);
                            ajax.start();
                        });
                    } else {
                        layer.msg("您没有删除权限", {anim: 6, icon: 0});
                    }
                }
            },
            'item5': {
                'label': '上移',
                'icon': Hussar.ctxPath + "/static/assets/img/treeContext/511101.png",
                'action': function (obj) {
                    var inst = $.jstree.reference(obj.reference);
                    var clickedNode = inst.get_node(obj.reference);
                    var prev = inst.get_prev_dom(obj.reference, true);
                    clickedNode.original.code
                    //选中ID
                    var id = clickedNode.id;
                    var prevId = prev[0].id;
                    RoleManage.singleMove(id, prevId,clickedNode.original.code);
                }
            },
            'item6': {
                'label': '下移',
                'icon': Hussar.ctxPath + "/static/assets/img/treeContext/511102.png",
                'action': function (obj) {
                    //获取点击的节点信息
                    var inst = $.jstree.reference(obj.reference);
                    var clickedNode = inst.get_node(obj.reference);
                    var next = inst.get_next_dom(obj.reference, true);
                    //选中ID
                    var id = clickedNode.id;
                    var nextId = next[0].id;
                    //RoleManage.singleMove(id, prevId,clickedNode.original.code);
                    RoleManage.singleMove(id, nextId,clickedNode.original.code);
                }
            },
        };
        if($('#loginId').val() == $('#usersId').val() || $('#loginId').val() == 'admin'){
            if(node.original.issort == 0){
                delete items.item1;
                delete items.item2;
            }
        }else{
            if(node.original.issort == 0){
                delete items.item1;  //删除节点 items
                delete items.item2;
                delete items.item3;
                delete items.item4;
                delete items.item5;
                delete items.item6;
            }else{
                delete items.item3;
                delete items.item4;
                delete items.item5;
                delete items.item6;
            }
        }
        var id = node.id;
        var n = $("#" + id);
        if (n.index() == 0) {//非第一个
            delete items.item5;  //删除节点 上移
        }
        if (n.next().length == 0) {//非最后一个
            delete items.item6;  //删除节点 上移
        }
        return items;
    };
    /**
     * 上移下移方法
     * @param id 组织机构id
     * @param direction 方向 'up' | 'down'
     */
    RoleManage.singleMove = function (id, nextId,code) {
        var table = '';
        var idcolumn = '';
        for( var i = 0 ; i < treedata.length ; i ++ ){
            if(treedata[i].id == nextId){
                var nextcode = treedata[i].code;
                if(nextcode == code && code == 'ROLE'){
                    table = 'personal_group';
                    idcolumn = 'group_id';
                }else if(nextcode == code && code == 'GROUP'){
                    table = 'personal_group_sort';
                    idcolumn = 'sort_id';
                }else if(nextcode == 'ROLE' && code == 'GROUP'){
                    layer.msg("已为第一个分组")
                    return;
                }else if(nextcode == 'GROUP' && code == 'ROLE'){
                    layer.msg("已为最后一个用户组")
                    return;
                }
            }
        }
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/topic/moveTopic",
            data: {
                table: table,
                idColumn: idcolumn,
                idOne: id,
                idTwo: nextId
            },
            async: true,
            cache: false,
            dataType: "json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success: function (result) {
                RoleManage.refreshTree();
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/topic/moveTopic", function(result) {
            RoleManage.refreshTree();
        }, function(data) {

        });
        ajax.set("table",table);
        ajax.set("idColumn",idcolumn);
        ajax.set("idOne",id);
        ajax.set("idTwo",nextId);
        ajax.start();
    };

    /*刷新组织机构树*/
    RoleManage.refreshTree = function () {
        $("#powerTree").jstree(true).refresh();
    }
    /*打开新增页面*/
    function openTopic(title, url, w, h) {
        if (title == null || title == '') {
            title = false;
        }
        ;
        if (url == null || url == '') {
            url = "404.jsp";
        }
        ;
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        }
        ;
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        }
        ;
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            shadeClose: false,
            skin:'confirm-class',
            shade: 0.3,
            title: title,
            content: Hussar.ctxPath+url

        });
    }
    /*打开编辑页面*/
    function openEdit(title, url, id, w, h) {
        layui.use(['Hussar'], function(){
            var Hussar = layui.Hussar;

            if (id.length == 0) {
                layer.alert('请先选择一条要编辑的用户组！', {
                    icon: 0,
                    shadeClose: false,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', 'auto'],
                    title: '提示'
                });
                return;
            }
            if (title == null || title == '') {
                title = false;
            }
            ;
            if (url == null || url == '') {
                url = "404.jsp";
            }
            ;
            if (w == null || w == '') {
                w = ($(window).width() * 0.9);
            }
            ;
            if (h == null || h == '') {
                h = ($(window).height() - 50);
            }
            ;
            layer.open({
                type: 2,
                area: [w + 'px', h + 'px'],
                fix: false, //不固定
                //maxmin: true,
                shadeClose: false,
                shade: 0.4,
                title: title,
                content: Hussar.ctxPath+url + "?flag=1&groupId=" + id
            });
        });
    };
    //分组新增
    function openSort(name,id) {
        layui.use(['Hussar'], function(){
            var Hussar = layui.Hussar;

            if(buttonType == 'editGroup'){
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+'/personalGroup/sortEdit',
                    data: {
                        groupId: id,
                    },
                    async: true,
                    cache: false,
                    dataType: "json",
                    contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                    success: function (data) {
                        $("#sortName").val(data.sortName);
                        $("#sortId").val(data.sortId);
                        $("#parentSortName").val(data.parentSortName);
                        $("#parentSortId").val(data.parentSortId);
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + '/group/sortEdit', function(data) {
                    $("#sortName").val(data.sortName);
                    $("#sortId").val(data.sortId);
                    $("#parentSortName").val(data.parentSortName);
                    $("#parentSortId").val(data.parentSortId);
                }, function(data) {

                });
                ajax.set("groupId",id);
                ajax.set("flag","1");
                ajax.start();
            }else{
                $("#sortName").val('');
                $("#sortId").val('');
                //$("#parentSortName").val('');
                //$("#parentSortId").val('');
            }
            layer.open({
                type: 1,
                btn: ['确定','取消'],
                area: ['350px', 'auto'],
                skin: 'confirm-class',
                fix: false, //不固定
                maxmin: true,
                shadeClose: false,
                shade: 0.4,
                title: name,
                content: $('#sortadd'),
                btn1: function (index, layero) {
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
                        layer.msg("分组名称不能超过15个字符", {anim: 6, icon: 0});
                        return;
                    }
                    if (parentSortId == "") {
                        layer.msg("所属用户组不能为空", {anim: 6, icon: 0});
                        return;
                    }
                    var url;//请求地址
                    var successMsg, errorMsg;//成功失败提示
                    var groupId;
                    if (buttonType == 'editGroup') {
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
                        contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                        success: function (data) {
                            if (data.result == "0") {
                                layer.msg("该分组已存在", {anim: 6, icon: 0});
                            } else if (data.result == "1") {
                                layer.msg('保存成功',{time:1*1000,icon: 1},function() {
                                    var index = parent.layer.getFrameIndex(window.name);
                                    $("#powerTree").jstree('deselect_all');
                                    $("#powerTree").jstree(true).select_node(sortId);
                                    $("#powerTree").jstree(true).refresh();
                                    layer.closeAll();
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
                                $("#powerTree").jstree('deselect_all');
                                $("#powerTree").jstree(true).select_node(sortId);
                                $("#powerTree").jstree(true).refresh();
                                layer.closeAll();
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
                    ajax.set("groupFlag","1");
                    ajax.start();
                },
            });
        });
    };
    /*选择删除*/
    function delGroupUser() {
        layui.use(['Hussar'], function(){
            var Hussar = layui.Hussar;

            var dataArr = getCheckData();
            if (dataArr.length == 0) {
                layer.msg("请选择要删除的人员", {anim: 6, icon: 0});
                return;
            }
            var ids;
            for (var i = 0; i < dataArr.length; i++) {
                layui.data('checked', {
                    key: dataArr[i].userId, remove: true
                });
                if (i == 0) {
                    ids = dataArr[i].userId;
                } else {
                    ids += ',' + dataArr[i].userId;
                }
            }
            layer.confirm('确定要删除所选中的用户组人员吗？', function () {
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/delGroupUserById",
                    data: {
                        groupId: $('#groupId').val(),
                        delIds: ids
                    },
                    async: false,
                    cache: false,
                    contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                    success: function (data) {
                        if (data > 0) {
                            layer.alert('删除成功', {
                                icon: 1,
                                shadeClose: false,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示',
                                end: function () {
                                    tableIns.reload({
                                        done: function (res, curr, count) {
                                            if (res.data.length == 0 && curr != 1) {
                                                tableIns.reload({
                                                    page: {
                                                        curr: curr - 1
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    var index = layer.alert();
                                    layer.close(index);
                                }
                            }, function () {
                                tableIns.reload({
                                    done: function (res, curr, count) {
                                        if (res.data.length == 0 && curr != 1) {
                                            tableIns.reload({
                                                page: {
                                                    curr: curr - 1
                                                }
                                            });
                                        }
                                    }
                                });
                                var index = layer.alert();
                                layer.close(index);
                            });
                        } else {
                            layer.alert('删除失败', {
                                icon: 2,
                                shadeClose: false,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示'
                            });
                        }
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/delGroupUserById", function(data) {
                    if (data > 0) {
                        layer.msg('删除成功',{time:1*1000,icon: 1},function() {
                            tableIns.reload({
                                done: function (res, curr, count) {
                                    if (res.data.length == 0 && curr != 1) {
                                        tableIns.reload({
                                            page: {
                                                curr: curr - 1
                                            }
                                        });
                                    }
                                }
                            });
                            var index = layer.alert();
                            layer.close(index);
                        })
                        // layer.alert('删除成功', {
                        //     icon: 1,
                        //     shadeClose: false,
                        //     skin: 'layui-layer-molv',
                        //     shift: 5,
                        //     area: ['300px', 'auto'],
                        //     title: '提示',
                        //     end: function () {
                        //         tableIns.reload({
                        //             done: function (res, curr, count) {
                        //                 if (res.data.length == 0 && curr != 1) {
                        //                     tableIns.reload({
                        //                         page: {
                        //                             curr: curr - 1
                        //                         }
                        //                     });
                        //                 }
                        //             }
                        //         });
                        //         var index = layer.alert();
                        //         layer.close(index);
                        //     }
                        // }, function () {
                        //     tableIns.reload({
                        //         done: function (res, curr, count) {
                        //             if (res.data.length == 0 && curr != 1) {
                        //                 tableIns.reload({
                        //                     page: {
                        //                         curr: curr - 1
                        //                     }
                        //                 });
                        //             }
                        //         }
                        //     });
                        //     var index = layer.alert();
                        //     layer.close(index);
                        // });
                    } else {
                        layer.alert('删除失败', {
                            icon: 2,
                            shadeClose: false,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', 'auto'],
                            title: '提示'
                        });
                    }
                }, function(data) {

                });
                ajax.set("groupId",$('#groupId').val());
                ajax.set("delIds",ids);
                ajax.start();
            })
        });
    };
    function getCheckData() { //获取选中数据
        //.看看已选中的所有数据
        var checkStatus = table.checkStatus('groupList'), mySelected = checkStatus.data;
        return mySelected;
    }

    RoleManage.initFrameHeight = function () {
        var h = $(window).height() - 60;
        var h2 = $(window).height() - 10;
        $("#roleIframe").css("height", h2 + "px");
        $("#roleInfo").css("height", h + "px");
    };
    /**
     * 初始化
     */
    $(function () {
        RoleManage.initPage();
        $(window).resize(function () {
            RoleManage.initFrameHeight();
            RoleManage.init();
        });
    });
    //列表上的删除按钮
    function delebtn(mas , url) {
        var id = $("#groupId").val();
        if ($("#loginId").val() == 'admin' || $("#loginId").val() ==  $('#usersId').val()) {
            layer.confirm(mas, function () {
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+url,
                    data: {
                        groupId: id
                    },
                    async: false,
                    cache: false,
                    contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                    success: function (data) {
                        if (data == 1) {
                            layer.alert('删除成功', {
                                icon: 1,
                                shadeClose: false,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示',
                                end: function () {
                                    var index = layer.alert();
                                    layer.close(index);
                                    window.location.reload(true)
                                    /!* RoleManage.initTree();
                                     tableIns.reload();*!/
                                }
                            }, function () {
                                var index = layer.alert();
                                layer.close(index);
                                window.location.reload(true)
                                /!* RoleManage.initTree();
                                 tableIns.reload();*!/
                            });
                        } else if(data == 2){
                            layer.alert('请先删除子群组或分组', {
                                icon: 0,
                                shadeClose: false,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示'
                            });
                        }else if(data == 6){
                            layer.alert('该群组已被授权，无法删除', {
                                icon: 0,
                                shadeClose: false,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示'
                            });
                        } else{
                            layer.alert('删除失败', {
                                icon: 2,
                                shadeClose: false,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示'
                            });
                        }
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + url, function(data) {
                    if (data == 1) {
                        // layer.alert('删除成功', {
                        //     icon: 1,
                        //     shadeClose: false,
                        //     skin: 'layui-layer-molv',
                        //     shift: 5,
                        //     area: ['300px', 'auto'],
                        //     title: '提示',
                        //     end: function () {
                        //         var index = layer.alert();
                        //         layer.close(index);
                        //         window.location.reload(true)
                        //         /* RoleManage.initTree();
                        //          tableIns.reload();*/
                        //     }
                        // }, function () {
                        //     var index = layer.alert();
                        //     layer.close(index);
                        //     window.location.reload(true)
                        //     /* RoleManage.initTree();
                        //      tableIns.reload();*/
                        // });
                        layer.msg('删除成功',{time:1*1000,icon: 1},function() {
                            var index = layer.alert();
                            layer.close(index);
                            window.location.reload(true)
                            /* RoleManage.initTree();
                             tableIns.reload();*/
                        })
                    } else if(data == 2){
                        layer.alert('请先删除子用户组或分组', {
                            icon: 0,
                            shadeClose: false,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', 'auto'],
                            title: '提示'
                        });
                    }else if(data == 6){
                        layer.alert('该用户组已被授权，无法删除', {
                            icon: 0,
                            shadeClose: false,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', 'auto'],
                            title: '提示'
                        });
                    } else{
                        layer.alert('删除失败', {
                            icon: 2,
                            shadeClose: false,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', 'auto'],
                            title: '提示'
                        });
                    }
                }, function(data) {

                });
                ajax.set("groupId",id);
                ajax.start();
            });
        } else {
            layer.msg("您没有删除权限", {anim: 6, icon: 0});
        }
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
            title: "所属分组",
            skin:'confirm-class',
            content: Hussar.ctxPath+'/group/sortTree?flag=1',
        });
    });
    //判断是新增打开窗口还是修改打开窗口
    function initButtontype() {
        if (undefined != $('#sortId').val() && $('#sortId').val() != '') {
            buttonType = 'edit';
        } else {
            buttonType = 'butAdd';
        }
    }

    $("#searchName").click(function () {
        searchFlag=1;
    });
    $(document).keydown(function (e) {
        if(e.keyCode == 13 && searchFlag == 1) {
            tableIns.reload({
                page: {
                    curr: 1
                },
                where: {
                    groupId: $('#groupId').val(),
                    uerName: $('#searchName').val()
                }
            })
            searchFlag = 0;
            $("#searchName").val("");
            $("#searchName").blur();
        }
    }).keyup(function () {
        key=0;
    })
});
//获取分组树数据
function getSort(){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/personalGroup/getSortTree",
            data:{
                treeType:"2"
            },
            async:true,
            cache:false,
            dataType:"json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
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
            Hussar.error("加载用户组列表失败");
        });
        ajax.set("treeType","2");
        ajax.set("flag",'1');
        ajax.start();
    });
}
function start() {
    getSort();
}
/*
$(document).ready(function () {
    $("#searchName").click(function () {
        searchFlag=1;
    })
})
$(document).keydown(function (e) {
    if(e.keyCode == 13 && searchFlag == 1) {
        tableIns.reload({
            page: {
                curr: 1
            },
            where: {
                groupId: $('#groupId').val(),
                uerName: $('#searchName').val()
            }
        })
        searchFlag = 0;
        $("#searchName").val("");
        $("#searchName").blur();
    }
}).keyup(function () {
    key=0;
})*/
