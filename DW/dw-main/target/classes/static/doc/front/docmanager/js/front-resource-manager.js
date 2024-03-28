/**
 * Created by smt on 2018/6/30.
 */
var chooseFile = [];
var chooseFileType = [];
var chooseFileName = [];
var cutFile = [];
var pathId = [];
var pathName = [];
var noChildPower=0;
var key='';
var count = 0;
var adminFlag;
var showStyle=2;
var dbclickover=true;
var pathFlag=0
var levelArry=[];

layui.use(['jquery', 'laytpl','layer','form','Hussar','HussarAjax'], function () {
    var $ = layui.jquery,
        laytpl = layui.laytpl,
        layer = layui.layer,
        form =  layui.form,
        Hussar = layui.Hussar,
        $ax = layui.HussarAjax;

    $(function () {
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");
        //初始化树
        initTree();
        initEvent();

        form.render();
    });
    $(".rit-menus").click(function () {
        var index = $(this).index();
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
        if(1 == index ){
            showStyle=2;
            refreshFile(openFileId)
        }else {
            showStyle=1;
            refreshFile(openFileId)
        }
    });
    // 从预览页面定位到目录
    function initLevel(){
        var nextId  = getQueryString("nextId");
        var easyId  = getQueryString("easyId");
        if(nextId !=null ){
            if(easyId==null||easyId==""||easyId==undefined){
                easyId = nextId;
            }
            /*$.ajax({
                async: false,
                type: "post",
                url: Hussar.ctxPath+"/preview/getFoldPath",
                data: {docId: easyId},
                success: function (data) {
                    if (!!data) {
                        for (var i = 1; i < data.length; i++) {
                            if(data[i].foldId == nextId){

                                categoryId = data[i].foldId;
                                pathId.push(data[i].foldId);
                                pathName.push(data[i].foldName);
                                refreshFileLevel(data[i].foldId)
                                break;
                            }
                            openFileId = data[i].foldId;
                            categoryId = data[i].foldId;
                            pathId.push(data[i].foldId);
                            pathName.push(data[i].foldName);
                            addOperLevel(data[i].foldId,categoryId)

                        }
                        createPath();
                    }
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/preview/getFoldPath", function(data) {
                if (!!data) {
                    for (var i = 1; i < data.length; i++) {
                        if(data[i].foldId == nextId){

                            categoryId = data[i].foldId;
                            pathId.push(data[i].foldId);
                            pathName.push(data[i].foldName);
                            refreshFileLevel(data[i].foldId)
                            break;
                        }
                        openFileId = data[i].foldId;
                        categoryId = data[i].foldId;
                        pathId.push(data[i].foldId);
                        pathName.push(data[i].foldName);
                        addOperLevel(data[i].foldId,categoryId)

                    }
                    createPath();
                }
            }, function(data) {

            });
            ajax.set("docId",easyId);
            ajax.start();
        }
    }
    function initEvent() {
        //类型查询
        $("#selectType").click(function () {
            refreshFile(openFileId);
        });
        //排序查询
        $("#orderType li").change(function() {
            $("input[name='sortType']").parent().removeClass("sortType-checked");
            $("input[name='sortType']:checked").parent().addClass("sortType-checked");
            refreshFile(openFileId);
        })
        //返回上级
        $("#upLevel").on('click',function(){
            if(pathId.length==1){
                return;
            }
            var index = layer.load(1, {
                shade: [0.1,'#fff'] //0.1透明度的白色背景
            });
            refreshFile(pathId[pathId.length-2]);
            pathName.pop();
            pathId.pop();
            createPath();
            layer.close(index);
        });
        //目录查找
        $("#searchInResultBtn").on('click',function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'] //0.1透明度的白色背景
            });
            refreshFile(openFileId,null,null,"1");
            layer.close(index);
        });

        /* $(".layui-unselect").click(function () {
             refreshFile(openFileId);
         });*/
    }
    /**
     * 加载目录树
     */
    function initTree() {
        var $tree = $("#fileTree");
        $(".pims_tree").height($(".content").height()/* - $(".weadmin-nav").height()*/);
        var initId = $("#initId").val();
        var initName = $("#initName").val();
        $tree.jstree({
            core: {
                check_callback: true,
                data: {

                    "url": "/frontFolder/getTreeDataLazy?lazy",
                    "data": function (node) {
                        return {"id": node.id,
                            "type" :"0"
                        }
                    }
                }
            },
            types: {
                "closed" : {
                    "icon" : "/static/resources/img/fsfile/treeFile.png",
                },
                "default" : {
                    "icon" : "/static/resources/img/fsfile/treeFile.png",
                },
                "opened" : {
                    "icon" : "/static/resources/img/fsfile/openFile.png",
                },
            },
            plugins: ['state', 'types',"themes", "html_data"],
        });
        $tree.jstree().hide_dots();
        $tree.bind("activate_node.jstree", function (obj, e) {
            // 处理代码
            // 获取当前节点
            var currentNode = e.node;
            var parent = currentNode.parent;
            openFileId = currentNode.id;
            categoryId = currentNode.id;
            refreshFile(currentNode.id);

            emptyChoose();
            var paramId = [];
            var paramName = [];
            if (currentNode.parent == '#') {
                pathId=[];
                pathName=[];
                pathId.push(currentNode.id)
                pathName.push(currentNode.text)
                createPath();
                return;
            }
            $('#path').empty();
            pathId = [];
            pathName = [];
            paramId.push(currentNode.id);
            paramName.push(currentNode.text);
            do {//2、判断循环条件;
                currentNode = $('#fileTree').jstree("get_node", currentNode.parent);
                paramId.push(currentNode.id);
                paramName.push(currentNode.text);
            } while (!!currentNode && currentNode.parent != '#')
            for (var i = 0; i < paramId.length; i++) {
                pathId.push(paramId[paramId.length - 1 - i]);
                pathName.push(paramName[paramId.length - 1 - i]);
            }
            createPath();
        });
        $tree.bind("open_node.jstree", function (e,data) {
            data.instance.open_node(data.node)


        });



        $tree.bind("close_node.jstree", function (e,data) {
            data.instance.set_type(data.node, 'closed');
        });
        $tree.bind("loaded.jstree", function(event, data) {

            data.instance.clear_state(); // <<< 这句清除jstree保存的选中状态
        });

        if (initId!=null&&initId!=undefined&&initId!=''){
            $tree.bind("ready.jstree", function (e,data) {
                $tree.jstree('activate_node',initId);
                initLevel();
            });
        }
        if (initId!=null&&initId!=undefined&&initId!=''){
            openFileId = initId;
            categoryId = initId;
            getChildren(initId, initName);
        }else {
            /*$.ajax({
                type: "POST",
                url: Hussar.ctxPath+"/frontFile/getRoot",
                contentType: "application/x-www-form-urlencoded",
                dataType: "json",
                async: false,
                success: function (result) {
                    openFileId = result.root;
                    categoryId = result.root;
                    getChildren(result.root, result.rootName);
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/frontFile/getRoot", function(result) {
                openFileId = result.root;
                categoryId = result.root;
                getChildren(result.root, result.rootName);
            }, function(data) {

            });
            ajax.start();
        }

    }
    function refreshTree() {
        var $tree = $("#fileTree");
        $tree.jstree(true).refresh();
        $(".pims_tree").height($(".content").height()/* - $(".weadmin-nav").height()*/);
    }
});
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var reg_rewrite = new RegExp("(^|/)" + name + "/([^/]*)(/|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    var q = window.location.pathname.substr(1).match(reg_rewrite);
    if(r != null){
        return unescape(r[2]);
    }else if(q != null){
        return unescape(q[2]);
    }else{
        return null;
    }
}
function createPageContext(){
    $('#pagelist').extendPagination({
        totalCount: count,
        showPage: 10,
        limit: 10,
        callback: function (curr, limit, totalCount) {
            refreshFile(id, obj.curr, obj.limit);
        }
    })
}
function clickPath(id) {
    while (pathId.indexOf(id) + 1 != pathId.length) {
        pathId.pop();
        pathName.pop();
    }
    refreshFileLevel(id);
    createPath();
}
function createPath() {
    var innerlength = 0;
    $("#path").html("");
    $("span#path").css({"transform":"translateX(0)"});
    $("span#path").width(innerlength);

    for (var i = 0; i < pathId.length; i++) {
        if (i == pathId.length - 1) {
            var param = '<span>' + pathName[i] + '</span>';
        } else {
            var param = '<span><a class="path-item" onclick="clickPath(\'' + pathId[i] + '\')">' + pathName[i] + '</a><i class="layui-icon">&#xe602;</i></span>';
        }
        $("#path").append(param);
    }
    setTimeout(function () {

        var list =  $("#path>span");
        innerlength  = 0;
        for(var m = 0 ;m < (list.length) ;m++){
            innerlength = innerlength + list.eq(m).width()+12;
            //   innerlength = innerlength + Math.ceil(list.eq(m).width() + .5);
        }

        $("span#path").width(innerlength);
        var outWidth = $(".outer-nav").width();
        //当目录长度超出显示范围，默认只显示可以显示的最后
        if(innerlength>outWidth){
            $(".control-btn-l").show();
            $(".control-btn-r").hide();
            var  subLength = innerlength - outWidth-55;
            $("span#path").css({"transform":"translateX(-"+subLength+"px)"});
            //获取当前偏移量
            $(".control-btn-l").click(function () {
                var  subLength = $("span#path").width() - $(".outer-nav").width();
                var subLength_1 = -$("span#path").css("transform").replace(/[^0-9\-,]/g,'').split(',')[4];
                $(".control-btn-r").show();
                subLength_1 = subLength_1 - outWidth;
                if(subLength_1 > outWidth){
                    $("span#path").css({"transform":"translateX(-"+subLength_1+"px)"});
                }else {
                    $("span#path").css({"transform":"translateX(0)"});
                    $(".control-btn-l").hide();
                }

            });
            $(".control-btn-r").click(function () {
                var  subLength = $("span#path").width() - $(".outer-nav").width();
                $(".control-btn-l").show();
                var subLength_2 = -$("span#path").css("transform").replace(/[^0-9\-,]/g,'').split(',')[4];
                subLength_2 = subLength_2 + outWidth;
                if(subLength_2 > subLength){
                    $("span#path").css({"transform":"translateX(-"+subLength+"px)"});
                    $(".control-btn-r").hide();
                }else {
                    $("span#path").css({"transform":"translateX(-"+subLength_2+"px)"});
                }
            })
        }else {
            $("span#path").css({"transform":"translateX(0)"});
            $(".control-btn-l").hide();
            $(".control-btn-r").hide();
        }
    },100)
}
function drawFile(param) {

    if(showStyle==1){
        layui.use('laytpl', function(){
            var laytpl = layui.laytpl;
            var data = { //数据
                "list":param,
                "adminFlag":adminFlag
                ,"noChildPower":noChildPower
            }
            var getTpl = $("#demo").html()
                ,view = document.getElementById('view');
            laytpl(getTpl).render(data, function (html) {

                if(html.indexOf("li")>-1){
                    $("#laypageAre").children().show();
                }else{
                    $("#laypageAre").children().hide();
                }
                view.innerHTML = html;
            });
        });
    }
    else{
        layui.use('laytpl', function(){
            var laytpl = layui.laytpl;
            var data = { //数据
                "list":param,
                "adminFlag":adminFlag
                ,"noChildPower":noChildPower
            }
            var getTpl = $("#demo1").html()
                ,view = document.getElementById('view');
            laytpl(getTpl).render(data, function(html){
                view.innerHTML = html;
                var inner = $("#view");
                // var tableWidth =inner.width()-5;
                var tableWidth=890;
                //fixed-table-header
                $(".fixed-table-header").width(tableWidth)
            });
        });
    }


}
function getChildren(id, name) {
    pathId.push(id);
    pathName.push(name);
    createPath();
    refreshFile(id);
}
function addOperLevel(parent,node) {
    levelArry.push(node);
}
function runLevel(){
    openNode(levelArry,0);
}
function openNode(levelArry,i){
    $("#fileTree").jstree("deselect_all", true);
    var ref = $('#fileTree').jstree(true);
    $('#fileTree').jstree("open_node", levelArry[i], function ( ) {
        var id = ref.get_node(levelArry[i] + '_anchor');
        if (id) {
            ref.deselect_node(levelArry[i]);
            ref.select_node(id);
            i=i+1;
            if(i>=levelArry.length){
                return;
            }else{
                openNode(levelArry,i);
            }

        } else {
            ref.select_node(parent);
        }

    });
}
function addOper(parent,node) {

    $("#fileTree").jstree("deselect_all", true);
    var ref = $('#fileTree').jstree(true);
    $('#fileTree').jstree("open_node", parent, function ( parent,node) {
        var id = ref.get_node(node + '_anchor')
        if (id) {
            ref.deselect_node(parent);
            ref.select_node(id);
            ref.open_node(id);
        } else {
            ref.select_node(parent);
        }

    });


}
function refreshFile(id, num, size,nameFlag) {
    layui.use(['laypage','layer','Hussar','HussarAjax'], function(){
        var laypage = layui.laypage,
            layer = layui.layer;
        var Hussar = layui.Hussar;
        var form = layui.form,
            $ax = layui.HussarAjax;
        var fileType = $("input[name='fileType']:checked").val();
        form.on('radio(fileType)', function (data) {
            fileType = data.value;
        });
        var orderType = $("input[name='sortType']:checked").val(); //排序类型
        var name = $('#searchInResult').val();
        addOper(openFileId,id);
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/frontFile/getChildren",
            data: {
                id: id,
                pageNumber: num,
                pageSize: size,
                type: fileType,
                order: orderType,
                name: name,
                nameFlag:nameFlag,
                operateType:"0"
            },
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                laypage.render({
                    elem: 'laypageAre'
                    ,count: data.total //数据总数，从服务端得到
                    ,limit: 20
                    ,layout: ['prev', 'page', 'next']
                    ,curr: num || 1 //当前页
                    ,jump: function(obj, first){
                        //obj包含了当前分页的所有参数，比如：
                        // obj.curr得到当前页，以便向服务端请求对应页的数据。
                        // obj.limit得到每页显示的条数
                        //首次不执行
                        if(!first){
                            refreshFile(id,obj.curr,obj.limit)
                        }
                    }
                });
                adminFlag=data.isAdmin;
                count = data.total;
                noChildPower=data.noChildPower;
                drawFile(data.rows);
                openFileId = id;
                categoryId = id;
                emptyChoose();
                var flag = false;
                dbclickover=true;
                var fileIds = [];
                for (var i = 0; i < data.rows.length; i++) {
                    if (data.rows[i].fileType != 'folder') {
                        flag = true;
                        fileIds.push(data.rows[i].fileId)
                    }
                }
                $('#amount').html(data.total);
                var idStr = fileIds.join(",")
                $.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/fsFile/getInfo",
                    data: {
                        ids: idStr
                    },
                    async: false,
                    cache: false,
                    dataType: "json",
                    success: function (data) {

                        for (var i = 0; i < data.length; i++) {
                            if(data[i].name==undefined){
                                $('#person'+data[i].fileId).html(data[i].authorId);
                            }else{
                                $('#person'+data[i].fileId).html(data[i].name);
                            }
                            $('#downNum'+data[i].fileId).html(data[i].downNum);
                            $('#readNum'+data[i].fileId).html(data[i].readNum);
                        }

                    }
                });
                $(".hoverEvent").hover(function(){


                    $(this).find("td  #hoverSpan").show();


                },function(){
                    $(this).find("td  #hoverSpan").hide();
                });
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/frontFile/getChildren", function(data) {
            laypage.render({
                elem: 'laypageAre'
                ,count: data.total //数据总数，从服务端得到
                ,limit: 20
                ,layout: ['prev', 'page', 'next']
                ,curr: num || 1 //当前页
                ,jump: function(obj, first){
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if(!first){
                        refreshFile(id,obj.curr,obj.limit)
                    }
                }
            });
            adminFlag=data.isAdmin;
            count = data.total;
            noChildPower=data.noChildPower;
            drawFile(data.rows);
            openFileId = id;
            categoryId = id;
            emptyChoose();
            var flag = false;
            dbclickover=true;
            var fileIds = [];
            for (var i = 0; i < data.rows.length; i++) {
                if (data.rows[i].fileType != 'folder') {
                    flag = true;
                    fileIds.push(data.rows[i].fileId)
                }
            }
            $('#amount').html(data.total);
            var idStr = fileIds.join(",")
            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/fsFile/getInfo",
                data: {
                    ids: idStr
                },
                async: false,
                cache: false,
                dataType: "json",
                success: function (data) {

                    for (var i = 0; i < data.length; i++) {
                        if(data[i].name==undefined){
                            $('#person'+data[i].fileId).html(data[i].authorId);
                        }else{
                            $('#person'+data[i].fileId).html(data[i].name);
                        }
                        $('#downNum'+data[i].fileId).html(data[i].downNum);
                        $('#readNum'+data[i].fileId).html(data[i].readNum);
                    }

                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/getInfo", function(data) {
                for (var i = 0; i < data.length; i++) {
                    if(data[i].name==undefined){
                        $('#person'+data[i].fileId).html(data[i].authorId);
                    }else{
                        $('#person'+data[i].fileId).html(data[i].name);
                    }
                    $('#downNum'+data[i].fileId).html(data[i].downNum);
                    $('#readNum'+data[i].fileId).html(data[i].readNum);
                }
            }, function(data) {

            });
            ajax.set("ids",idStr);
            ajax.start();
            $(".hoverEvent").hover(function(){


                $(this).find("td  #hoverSpan").show();


            },function(){
                $(this).find("td  #hoverSpan").hide();
            });
        }, function(data) {

        });
        ajax.set("id",id);
        ajax.set("pageNumber",num);
        ajax.set("pageSize",size);
        ajax.set("type",fileType);
        ajax.set("order",orderType);
        ajax.set("name",name);
        ajax.set("nameFlag",nameFlag);
        ajax.set("operateType","0");
        ajax.start();
    });


}
function refreshFileLevel(id, num, size,nameFlag) {
    layui.use(['laypage','layer','Hussar','HussarAjax'], function(){
        var laypage = layui.laypage,
            layer = layui.layer;
        var Hussar = layui.Hussar;
        var form = layui.form,
            $ax = layui.HussarAjax;
        var fileType = $("input[name='fileType']:checked").val();
        form.on('radio(fileType)', function (data) {
            fileType = data.value;
        });
        var orderType = $("input[name='sortType']:checked").val(); //排序类型
        var name = $('#searchInResult').val();
        addOperLevel(openFileId,id);
        runLevel();
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/frontFile/getChildren",
            data: {
                id: id,
                pageNumber: num,
                pageSize: size,
                type: fileType,
                order: orderType,
                name: name,
                nameFlag:nameFlag,
                operateType:"0"
            },
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                laypage.render({
                    elem: 'laypageAre'
                    ,count: data.total //数据总数，从服务端得到
                    ,limit: 20
                    ,layout: ['prev', 'page', 'next']
                    ,curr: num || 1 //当前页
                    ,jump: function(obj, first){
                        //obj包含了当前分页的所有参数，比如：
                        // obj.curr得到当前页，以便向服务端请求对应页的数据。
                        // obj.limit得到每页显示的条数
                        //首次不执行
                        if(!first){
                            refreshFile(id,obj.curr,obj.limit)
                        }
                    }
                });
                adminFlag=data.isAdmin;
                count = data.total;
                noChildPower=data.noChildPower;
                drawFile(data.rows);
                openFileId = id;
                categoryId = id;
                emptyChoose();
                var flag = false;
                dbclickover=true;
                var fileIds = [];
                for (var i = 0; i < data.rows.length; i++) {
                    if (data.rows[i].fileType != 'folder') {
                        flag = true;
                        fileIds.push(data.rows[i].fileId)
                    }
                }
                $('#amount').html(data.total);
                var idStr = fileIds.join(",")
                $.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/fsFile/getInfo",
                    data: {
                        ids: idStr
                    },
                    async: false,
                    cache: false,
                    dataType: "json",
                    success: function (data) {

                        for (var i = 0; i < data.length; i++) {
                            if(data[i].name==undefined){
                                $('#person'+data[i].fileId).html(data[i].authorId);
                            }else{
                                $('#person'+data[i].fileId).html(data[i].name);
                            }
                            $('#downNum'+data[i].fileId).html(data[i].downNum);
                            $('#readNum'+data[i].fileId).html(data[i].readNum);
                        }

                    }
                });
                $(".hoverEvent").hover(function(){


                    $(this).find("td  #hoverSpan").show();


                },function(){
                    $(this).find("td  #hoverSpan").hide();
                });
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/frontFile/getChildren", function(data) {
            laypage.render({
                elem: 'laypageAre'
                ,count: data.total //数据总数，从服务端得到
                ,limit: 20
                ,layout: ['prev', 'page', 'next']
                ,curr: num || 1 //当前页
                ,jump: function(obj, first){
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if(!first){
                        refreshFile(id,obj.curr,obj.limit)
                    }
                }
            });
            adminFlag=data.isAdmin;
            count = data.total;
            noChildPower=data.noChildPower;
            drawFile(data.rows);
            openFileId = id;
            categoryId = id;
            emptyChoose();
            var flag = false;
            dbclickover=true;
            var fileIds = [];
            for (var i = 0; i < data.rows.length; i++) {
                if (data.rows[i].fileType != 'folder') {
                    flag = true;
                    fileIds.push(data.rows[i].fileId)
                }
            }
            $('#amount').html(data.total);
            var idStr = fileIds.join(",")
            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/fsFile/getInfo",
                data: {
                    ids: idStr
                },
                async: false,
                cache: false,
                dataType: "json",
                success: function (data) {

                    for (var i = 0; i < data.length; i++) {
                        if(data[i].name==undefined){
                            $('#person'+data[i].fileId).html(data[i].authorId);
                        }else{
                            $('#person'+data[i].fileId).html(data[i].name);
                        }
                        $('#downNum'+data[i].fileId).html(data[i].downNum);
                        $('#readNum'+data[i].fileId).html(data[i].readNum);
                    }

                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/getInfo", function(data) {
                for (var i = 0; i < data.length; i++) {
                    if(data[i].name==undefined){
                        $('#person'+data[i].fileId).html(data[i].authorId);
                    }else{
                        $('#person'+data[i].fileId).html(data[i].name);
                    }
                    $('#downNum'+data[i].fileId).html(data[i].downNum);
                    $('#readNum'+data[i].fileId).html(data[i].readNum);
                }
            }, function(data) {

            });
            ajax.set("ids",idStr);
            ajax.start();
            $(".hoverEvent").hover(function(){


                $(this).find("td  #hoverSpan").show();


            },function(){
                $(this).find("td  #hoverSpan").hide();
            });
        }, function(data) {

        });
        ajax.set("id",id);
        ajax.set("pageNumber",num);
        ajax.set("pageSize",size);
        ajax.set("type",fileType);
        ajax.set("order",orderType);
        ajax.set("name",name);
        ajax.set("nameFlag",nameFlag);
        ajax.set("operateType","0");
        ajax.start();
    });


}
function dbclick(id, type, name) {
    if(dbclickover==true) {
        dbclickover=false;
        if (type == "folder") {
            pathId.push(id);
            pathName.push(name);
            createPath();
            refreshFileLevel(id);
        } else {
            showPdf(id,type,name);
        }
    }
}
function download(id, name) {
    /* $.ajaxFileUpload({
         url: "/files/fileDownNew",
         type: "post",
         data: {
             docName: name,
             fileIds: id,
         },
         success:function(){
             refreshFile(openFileId);
         }
     });*/

    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew",
            type : "post",
            data : {
                docName : "",//name,
                docIds : id,
            }
        });
    });
}

function clickOneTime(e, id, type, name) {
    var jq = $(e);
    if (key == 1) {
        if (chooseFile.indexOf(id) != -1) {
            jq.removeClass("active");
            chooseFile = chooseFile.del(chooseFile.indexOf(id));
            chooseFileType = chooseFileType.del(chooseFile.indexOf(id));
            chooseFileName = chooseFileName.del(chooseFile.indexOf(id));
        } else {
            jq.addClass("active");
            chooseFile.push(id);
            chooseFileType.push(type);
            chooseFileName.push(name);
        }
    } else {
        $('.file').removeClass("active");
        //refreshFile(openFileId);
        emptyChoose();
        jq.addClass("active");
        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
    }
}

Array.prototype.del = function (n) {
    if (n < 0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0, n).concat(this.slice(n + 1, this.length));
}

$(document).keydown(function (e) {
    if (e.ctrlKey) {
        key = 1;
    } else if (e.shiftKey) {
        key = 2;
    }
}).keyup(function () {
    key = 0;
});


function emptyChoose() {
    chooseFile = [];
    chooseFileType = [];
    chooseFileName = [];
}
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showPdf(id,fileSuffixName,name) {
    var keyword =  name;
    dbclickover=true;
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            async: false,
            cache: false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType: "json",
            success: function (data) {
                if(data.code==1){
                    openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
                }else if(data.code==2){
                    openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
                }else if(data.code==3){
                    openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
                }else if(data.code==4){
                    openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
                }else{
                    openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id);
                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
                openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
            }else if(data.code==2){
                openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
            }else if(data.code==3){
                openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
            }else if(data.code==4){
                openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
            }else{
                openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id);
            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
    });
}
$('#searchInResult').bind('keypress', function (event) {
    if (event.keyCode == "13") {
        $("#searchInResultBtn").click();
    }
});
function isPDFShow(fileSuffixName){
    return fileSuffixName == ".pdf"
        || fileSuffixName == ".doc" || fileSuffixName == ".docx" || fileSuffixName == ".dot"
        || fileSuffixName == ".wps" || fileSuffixName == ".wpt"
        || fileSuffixName == ".xls" || fileSuffixName == ".xlsx" || fileSuffixName == ".xlt"
        || fileSuffixName == ".et" || fileSuffixName == ".ett"
        || fileSuffixName == ".ppt" || fileSuffixName == ".pptx" || fileSuffixName == ".ppts"
        || fileSuffixName == ".pot" || fileSuffixName == ".dps" || fileSuffixName == ".dpt"
        || fileSuffixName == ".txt"
        || fileSuffixName == ".ceb"
        ;
}
var openFlag = false;
// 控制目录显示
$(".mlBtn").click(function(){
    openFlag = !openFlag;
    if(openFlag){
        $(this).text("收起目录");
    }else{
        $(this).text("展开目录");
    }
    $(".con-full-l").toggleClass("open");
});

$(document).click(function(event){
    var _con = $('.con-full-l');  // 设置目标区域
    var _con2 = $('.mlBtn');  // 设置目标区域
    if(!_con.is(event.target)
        && _con.has(event.target).length === 0
        &&!_con2.is(event.target)
        && _con2.has(event.target).length === 0){ // Mark 1

        $(".con-full-l").removeClass("open");
        _con2.text("展开目录");
        openFlag = !openFlag;
    }
});
function cancelCollection(e,id){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.confirm('确定要取消收藏吗？',{title :['取消收藏','background-color:#fff']},function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'], //0.1透明度的白色背景
                fix:true
            });

            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+"/personalCollection/deleteCollection",
                data:{
                    ids: id,
                    opType: '5'
                },
                async:true,
                cache:false,
                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                success:function(data){
                    if(data> 0){
                        layer.msg('取消收藏成功',{icon: 1})
                    }else {
                        layer.msg('取消收藏失败',{anim:6,icon: 2})
                    }
                    // btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                },
                error:function () {
                    layer.msg('取消收藏异常!',{anim:6,icon: 2})
                    // btnState();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/deleteCollection", function(data) {
                if(data> 0){
                    layer.msg('取消收藏成功',{icon: 1})
                }else {
                    layer.msg('取消收藏失败',{anim:6,icon: 2})
                }
                // btnState();
                // refreshTree();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            }, function(data) {
                layer.msg('取消收藏异常!',{anim:6,icon: 2})
                // btnState();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("ids",id);
            ajax.set("opType",'5');
            ajax.start();
        })
    });
};
function collection(e,id){

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.confirm('确定要收藏吗？',{title :['收藏','background-color:#fff']},function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'], //0.1透明度的白色背景
                fix:true
            });

            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+"/personalCollection/addCollection",
                data:{
                    docIds:id
                },
                async:true,
                cache:false,
                success:function(data){
                    if(data.success=='0'){
                        layer.msg('收藏成功',{icon: 1})
                    }else {
                        layer.msg('收藏失败',{anim:6,icon: 2})
                    }
                    // btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                },
                error:function () {
                    layer.msg('取消收藏异常!',{anim:6,icon: 2})
                    // btnState();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/addCollection", function(data) {
                if(data.success=='0'){
                    layer.msg('收藏成功',{icon: 1})
                }else {
                    layer.msg('收藏失败',{anim:6,icon: 2})
                }
                // btnState();
                // refreshTree();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            }, function(data) {
                layer.msg('取消收藏异常!',{anim:6,icon: 2})
                // btnState();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("docIds",id);
            ajax.start();
        })
    });
};
function  iconDownLoad(id,name) {

    var index = layer.load(1, {
        shade: [0.1,'#fff'] ,//0.1透明度的白色背景
        scrollbar: false,
        time:1000
    });

    download(id,name);
}
/*打开分享链接*/
function share(docId,fileSuffixName,fileName) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        var title = '';
        var url = "/s/shareConfirm";
        var w =  538;
        var h = 311;
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            async: false,
            cache: false,
            dataType: "json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success: function (data) {
                if(data.code==1){
                }else if(data.code==2){
                }else if(data.code==3){
                }else if(data.code==4){
                }else{

                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
            }else if(data.code==2){
            }else if(data.code==3){
            }else if(data.code==4){
            }else{

            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
        if (title == null || title == '') {
            title = false;
        }
        if (url == null || url == '') {
            url = "404.jsp";
        }
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        }
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        }
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
        });
    });
}

function  iconDownLoad(id,name) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            url: Hussar.ctxPath+"/integral/downloadIntegral",
            async: true,
            data: {
                docId: id,
                ruleCode: 'download'
            },
            success: function (data) {
                if (data.status == "1") {
                    var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {icon: 3, title: '提示'}, function (index) {
                        layer.close(index2);
                        $.ajax({
                            url: Hussar.ctxPath+"/integral/addIntegral",
                            async: true,
                            data: {
                                docId: id,
                                ruleCode: 'download'
                            },
                            success: function (data) {
                                if (null == data) {
                                    download(id, name);
                                } else {
                                    $("#num").html(data.msg)
                                    if (data.msg == "积分不足" || data.msg == "已达上限") {
                                        $(".integral .point").hide();
                                        $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                    }
                                    $(".integral").show();
                                    setTimeout(function () {
                                        $(".integral .point").show();
                                        $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                        $(".integral").hide();

                                    }, 2000)
                                    if (data.integral != 0) {
                                        download(id, name);
                                    }
                                }
                            }
                        });
                        var index = layer.load(1, {
                            shade: [0.1, '#fff'],//0.1透明度的白色背景
                            scrollbar: false,
                            time: 1000
                        });
                    })
                } else {
                    $.ajax({
                        url: Hussar.ctxPath+"/integral/addIntegral",
                        async: true,
                        data: {
                            docId: id,
                            ruleCode: 'download'
                        },
                        success: function (data) {
                            if (null == data) {
                                download(id, name);
                            } else {
                                $("#num").html(data.msg)
                                if (data.msg == "积分不足" || data.msg == "已达上限") {
                                    $(".integral .point").hide();
                                    $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                }
                                $(".integral").show();
                                setTimeout(function () {
                                    $(".integral .point").show();
                                    $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                    $(".integral").hide();

                                }, 2000)
                                if (data.integral != 0) {
                                    download(id, name);
                                }
                            }
                        }
                    });
                    var index = layer.load(1, {
                        shade: [0.1, '#fff'],//0.1透明度的白色背景
                        scrollbar: false,
                        time: 1000
                    });
                }
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {
            if (data.status == "1") {
                var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {icon: 3, title: '提示',skin:'download-info'}, function (index) {
                    layer.close(index2);
                    /*$.ajax({
                        url: Hussar.ctxPath+"/integral/addIntegral",
                        async: true,
                        data: {
                            docId: id,
                            ruleCode: 'download'
                        },
                        success: function (data) {
                            if (null == data) {
                                download(id, name);
                            } else {
                                $("#num").html(data.msg)
                                if (data.msg == "积分不足" || data.msg == "已达上限") {
                                    $(".integral .point").hide();
                                    $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                }
                                $(".integral").show();
                                setTimeout(function () {
                                    $(".integral .point").show();
                                    $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                    $(".integral").hide();

                                }, 2000)
                                if (data.integral != 0) {
                                    download(id, name);
                                }
                            }
                        }
                    });*/
                    var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                        if (null == data) {
                            download(id, name);
                        } else {
                            $("#num").html(data.msg)
                            if (data.msg == "积分不足" || data.msg == "已达上限") {
                                $(".integral .point").hide();
                                $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                            }
                            $(".integral").show();
                            setTimeout(function () {
                                $(".integral .point").show();
                                $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                $(".integral").hide();

                            }, 2000)
                            if (data.integral != 0) {
                                download(id, name);
                            }
                        }
                    }, function(data) {

                    });
                    ajax.set("docId",id);
                    ajax.set("ruleCode",'download');
                    ajax.start();
                    var index = layer.load(1, {
                        shade: [0.1, '#fff'],//0.1透明度的白色背景
                        scrollbar: false,
                        time: 1000
                    });
                })
            } else {
                /*$.ajax({
                    url: Hussar.ctxPath+"/integral/addIntegral",
                    async: true,
                    data: {
                        docId: id,
                        ruleCode: 'download'
                    },
                    success: function (data) {
                        if (null == data) {
                            download(id, name);
                        } else {
                            $("#num").html(data.msg)
                            if (data.msg == "积分不足" || data.msg == "已达上限") {
                                $(".integral .point").hide();
                                $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                            }
                            $(".integral").show();
                            setTimeout(function () {
                                $(".integral .point").show();
                                $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                $(".integral").hide();

                            }, 2000)
                            if (data.integral != 0) {
                                download(id, name);
                            }
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                    if (null == data) {
                        download(id, name);
                    } else {
                        $("#num").html(data.msg)
                        if (data.msg == "积分不足" || data.msg == "已达上限") {
                            $(".integral .point").hide();
                            $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                        }
                        $(".integral").show();
                        setTimeout(function () {
                            $(".integral .point").show();
                            $(".integral .num").css({"width": "36px", "padding-top": "0"})
                            $(".integral").hide();

                        }, 2000)
                        if (data.integral != 0) {
                            download(id, name);
                        }
                    }
                }, function(data) {

                });
                ajax.set("docId",id);
                ajax.set("ruleCode",'download');
                ajax.start();
                var index = layer.load(1, {
                    shade: [0.1, '#fff'],//0.1透明度的白色背景
                    scrollbar: false,
                    time: 1000
                });
            }
        }, function(data) {

        });
        ajax.set("docId",id);
        ajax.set("ruleCode",'download');
        ajax.start();
    });
}
function download(id,name){
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew?docIds="+id,
            type : "post",
            async:false,

        });
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}
