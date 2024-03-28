/**
 * Created by Administrator on 2018/12/6.
 */
/**
 * Create By luzhanzhao
 * date 2018-11-19
 */
var chooseFile = [];    //选中的文件或目录的id
var chooseFileType = []; //选中的文件或目录的type
var chooseFileAuthor=[];
var chooseFileState=[];
var chooseFileName = [];
var clickFlag=false;
var currOrder = '';
var style= 0;

var scrollHeightAlert=0;
var reNameFlag= false;      //重命名标志
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var opType=$("#opType").val();
var pathId = [];        //路径
var pathName = [];

var treeData;
var openFileId;   //打开的文件夹的id
var cutFile = [];          //剪切的文件或目录的id
var cutFileType = [];      //剪切的文件或目录的type
var cutFileName = [];      //剪切的文件或目录的name
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;
    start();
    function getUsers(){
        /*$.ajax({
            type:"post",
            url:"/orgTreeDemo/usersTree",
            data:{
                treeType:"2"
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(result){
                // var arrays = [];
                // for(var i=0; i<result.length; i++){
                //     var arr = {
                //         id	:	result[i].ID,
                //         code:   result[i].CODE,
                //         text : result[i].TEXT,
                //         parent : result[i].PARENT,
                //         struLevel:result[i].STRULEVEL,
                //         struOrder:result[i].STRUORDER,
                //         struType:result[i].STRUTYPE,
                //         isLeaf:result[i].ISLEAF,
                //         type:result[i].TYPE,
                //         isEmployee:result[i].ISEMPLOYEE
                //     }
                //     arrays.push(arr);
                // }
                treeData = result;
            }, error:function(data) {
                Hussar.error("获取联系人失败");
            }
        });*/

        var ajax = new $ax(Hussar.ctxPath + "/orgTreeDemo/usersTree", function(result) {

            treeData = result;
        }, function(data) {
            Hussar.error("获取联系人失败");

        });
        ajax.set("treeType","2");
        ajax.start();
    }
    $(".changeTab li").on('click', function () {
        var index=   $(".changeTab li").index($(this));
        $("#searchName").val("");
        style=index;
        emptyChoose();
        if(style==0){
            $(".tab1").addClass('layui-this');
            $(".tab2").removeClass('layui-this');
            $(".message").show();
            $(".handover-info").show();
            $("#fileType").show();
            refreshFile(null,null)
        }if(style ==1){
            $(".tab2").addClass('layui-this');
            $(".tab1").removeClass('layui-this');
            $(".message").hide();
            $(".handover-info").hide();
            $("#fileType").hide();
            refreshFile2(null,null)
        }

    });
    form.on('checkbox(type)', function (data) {
        var value=data.value;
        if(value==0){
            if( $("input[name=type]:eq(0)").is(':checked')){
                $("input[name=type]:not(:first)").each(function () {
                    $(this).prop("checked",false)
                })
            }else{
                $("input[name=type]").each(function () {
                    $(this).prop("checked",false)
                })
            }

        }else{
            var flag=0;
            $("input[name=type]:not(:first)").each(function (index) {
                if(!$("input[name=type]:not(:first)").eq(index).is(':checked')){
                    $("input[name=type]:eq(0)") .prop("checked",false) ;
                    flag=1;
                    return false
                }
            })
            if(flag==0){
                // $("input[name=type]:eq(0)") .prop("checked",true) ;
            }
        }
        form.render();
        refreshFile(openFileId);
    })
    /*搜索按钮*/
    $("#searchBtn").on('click',function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
            ,offset: scrollHeightAlert
        });
        if(style==0){

            refreshFile(null,null)
        }if(style ==1){

            refreshFile2(null,null)
        }
        layer.close(index);
    });

    function start() {
        getUsers();
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");
        btnState()
    }


    $("#cancel").on('click',function(){
        var index = layer.getFrameIndex(window.name);
        layer.close(index);
    });


    //页面初始化
    $(function () {
        var load = new Loading();
        load.init({
            target: "#dndArea"
        });
        load.start();
        setTimeout(function() {
            load.stop();
        }, 800)
        pathId = ['abcde4a392934742915f89a586989292'];
        pathName = ['文件交接'];
        createPath()
        refreshFile();
        btnState()
    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth =inner.width();
        //fixed-table-header
        $(".fixed-table-header").width(tableWidth)
    })
    $("#pass").on('click', function () {
        if (chooseFile.length == 0) {
            layer.msg("请选择要通过的信息", {anim: 6, icon: 0, offset: scrollHeightMsg});
            return;
        }

        layer.confirm('确定要通过所选交接记录吗？', {title: ['审核', 'background-color:#fff'], offset: scrollHeightAlert,skin:'move-confirm'}, function () {
            var index = layer.load(1, {
                shade: [0.1, '#fff'] //0.1透明度的白色背景
            });
            if (chooseFile.length == 0) {
                layer.close(index);
                return;
            }
            var scopeId = chooseFile.join(',');

            var ajax = new $ax(Hussar.ctxPath + "/handover/pass", function(data) {
                if (data.code== 0) {

                    layer.msg("通过成功", {icon: 1, offset: scrollHeightMsg});

                } else {

                    layer.msg("通过异常", {icon: 2, offset: scrollHeightMsg});
                }

                // refreshTree();
                refreshFile2();
                emptyChoose();
                btnState();
                layer.close(index);
            }, function(data) {
                layer.msg("通过异常", {icon: 2, offset: scrollHeightMsg});
                btnState();
                //  refreshTree();
                //refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("idStr",chooseFile.join(","));
            ajax.start();
        })
    });
    $("#back").on('click', function () {
        if (chooseFile.length == 0) {
            layer.msg("请选择要驳回的信息", {anim: 6, icon: 0, offset: scrollHeightMsg});
            return;
        }

        layer.confirm('确定要驳回所选交接记录吗？', {title: ['驳回', 'background-color:#fff'], offset: scrollHeightAlert,skin:'move-confirm'}, function () {
            var index = layer.load(1, {
                shade: [0.1, '#fff'] //0.1透明度的白色背景
            });
            if (chooseFile.length == 0) {
                layer.close(index);
                return;
            }
            var scopeId = chooseFile.join(',');

            var ajax = new $ax(Hussar.ctxPath + "/handover/back", function(data) {
                if (data.code== 0) {

                    layer.msg("驳回成功", {icon: 1, offset: scrollHeightMsg});

                } else {

                    layer.msg("驳回异常", {icon: 2, offset: scrollHeightMsg});
                }
                btnState();
                // refreshTree();
                refreshFile2();
                emptyChoose();
                layer.close(index);
            }, function(data) {
                layer.msg("驳回异常", {icon: 2, offset: scrollHeightMsg});
                btnState();
                //  refreshTree();
                //refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("idStr",chooseFile.join(","));
            ajax.start();
        })
    });
    $("#authorName").click(function(){

        // 先让其他input失去焦点
        $("input").blur();
        var authName=$("#authorName").val().trim();
        layerView=layer.open({
            type: 1,
            area: ['399px','468px'],
            btn: ['确定','取消'],
            skin: 'move-class',
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: "选择接收人",
            content: $("#employeeTreeDiv"),
            success:function(){
                initEmployeeTree(treeData,authName);
            }
        });
    });
    /*打开分享链接*/
    function treeSearch(treeId,searchId,username){
        $("#"+searchId).val("");
        $(".jstree-search").remove();
        $(".search-results").html("");
        var $tree = $("#"+treeId);
        var to = false;
        //用户树查询
        $("#"+searchId).keyup(function () {
            if (to) { clearTimeout(to); }
            to = setTimeout(function () {
                var v = $("#"+searchId).val();
                if(v==null){
                    v ='';
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
        if(username!=null && username!=""){
            $("#"+searchId).val(username);
            var e = $.Event("keyup");//模拟一个键盘事件
            e.keyCode = 13;//keyCode=13是回车
            $("#"+searchId).trigger(e);//模拟页码框按下回车
        }
    }
    function initEmployeeTree(data,authName){
        var $tree = $("#showEmployeeTree");
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
            plugins: ['types','search'],
            types:{
                "1":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/com.png"},
                "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/station.png"},
                "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/empl.png"},
                "USER":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/user.png"}
            },
            search:treeSearch("showEmployeeTree","personSearch",authName)
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            if(e.node.original.struType != 9  && e.node.original.type !='USER'){
                layer.msg("请选择人员")
                return;
            }else{
                $("#authorName").val(e.node.original.text);
                $("#authorId").val(e.node.original.id);
                layer.close(layerView);
            }
            //layer.msg("你点击了："+e.node.original.text+"&nbsp;&nbsp;节点id："+e.node.original.id)
        })
    }
    /*剪切*/
    $("#cutFile").on('click',function(){
        if (chooseFileType[0]=="folder"){
            var operation = function () {
                layerView = layer.open({
                    type: 1,
                    area: ['400px','434px'],
                    //shift : 1,
                    shadeClose: false,
                    btn: ['确定','取消'],
                    skin: 'move-class',
                    title: ['目录结构','background-color:#fff'],
                    maxmin: false,
                    offset:scrollHeightLong,
                    content: $("#folderTreeAuthority"),
                    success: function () {
                        initFolderTree();
                        layer.close(index);
                    },
                    end: function () {
                        layer.closeAll(index);
                    }
                });
            }
            var index = layer.confirm('确定要移动所选目录吗？',{title :['移动','background-color:#fff'], offset:scrollHeightAlert,skin:'move-confirm'}, operation);
            cutFile=[].concat(chooseFile);
            cutFileType=[].concat(chooseFileType);
            cutFileName=[].concat(chooseFileName);

        }else{
            var operation =function(){
                layerView=layer.open({
                    type : 1,
                    area: ['400px','434px'],
                    btn: ['确定','取消'],
                    //shift : 1,
                    shadeClose: false,
                    skin: 'move-class',
                    title : ['目录结构','background-color:#fff'],
                    maxmin : false,
                    offset:scrollHeightLong,
                    content : $("#filTree"),
                    success : function() {
                        initFileTree();
                        layer.close(index);
                    },
                    end: function () {
                        layer.closeAll(index);
                    }
                });
            }
            var index = layer.confirm('确定要移动所选文件吗？',{title :['移动','background-color:#fff'], offset:scrollHeightAlert,skin:'move-confirm'},operation);
            cutFile=[].concat(chooseFile);
            cutFileType=[].concat(chooseFileType);
            cutFileName=[].concat(chooseFileName);
        }
    });
    initFileTree=function () {
        var $tree = $("#fileTree2");
        $tree.jstree("destroy");    //二次打开时要先销毁树
        $tree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": Hussar.ctxPath+"/personalCollection/getTreeDataLazy?lazy",
                    "data": function (node) {
                        return {"id": node.id
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
        var openFolder =null;
        $tree.bind('activate_node.jstree', function (obj,e){
            openFolder = e;
        });
        $(".layui-layer-btn0").on('click',function(){
            if(openFolder === null){
                layer.msg("请选择目录",{anim:6,icon: 0,offset:scrollHeightMsg});
            }else {
                var operation = function () {
                    if (openFileId == openFolder.node.original.id) {
                        layer.msg("不能移动到当前目录", {icon: 0, offset: scrollHeightMsg});
                        return;
                    }
                    var ajax = new $ax(Hussar.ctxPath + "/personalCollection/move", function (data) {
                        if (data.result == "0") {

                            layer.msg("文件已存在", {icon: 0, offset: scrollHeightMsg});
                        } else if (data.result == "1") {
                            $(".layui-laypage-btn").click();
                            layer.close(layerView);
                            layer.msg("移动成功", {icon: 1, offset: scrollHeightMsg});
                            refreshFile(openFileId)
                        } else {

                            layer.msg("移动失败", {icon: 2, offset: scrollHeightMsg});
                        }
                    }, function (data) {
                        layer.msg("系统出错，请联系管理员", {icon: 2, offset: scrollHeightMsg});
                    });
                    ajax.set("ids", cutFile.join(","));
                    ajax.set("parentFolderId", openFolder.node.original.id);
                    ajax.start();
                }
                layer.confirm('确定要移动到此目录下吗？', {
                    title: ['移动', 'background-color:#fff'],
                    offset: scrollHeightAlert,
                    skin:'move-confirm'
                }, operation);
            }
        });

    }
    //加载目录树
    initFolderTree = function () {
        var $tree = $("#folderTreeAuthority2");
        $tree.jstree("destroy");    //二次打开时要先销毁树
        $tree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": Hussar.ctxPath + "/personalCollection/getTreeDataLazy?lazy",
                    "data": function (node) {
                        return {
                            "id": node.id
                        };
                    }
                },
                themes: {
                    theme: "default",
                    dots: false,// 是否展示虚线
                    icons: true,// 是否展示图标
                }
            },
            types: {
                "closed": {
                    "icon": Hussar.ctxPath + "/static/resources/img/fsfile/treeFile.png",
                },
                "default": {
                    "icon": Hussar.ctxPath + "/static/resources/img/fsfile/treeFile.png",
                },
                "opened": {
                    "icon": Hussar.ctxPath + "/static/resources/img/fsfile/openFile.png",
                }
            },
            plugins: ['types']
        });
        var openFolder = null;
        $tree.bind('activate_node.jstree', function (obj, e) {
            openFolder = e;
        });
        $(".layui-layer-btn0").on('click', function () {
            if (openFolder === null) {
                layer.msg("请选择目录", {anim: 6, icon: 0, offset: scrollHeightMsg});
            } else {
                var operation = function () {
                    var index = layer.load(1, {
                        shade: [0.1, '#fff'] //0.1透明度的白色背景
                        , offset: scrollHeightAlert
                    });
                    if (cutFile.length <= 0) {
                        layer.close(index);
                        layer.msg("请先选择目标目录", {anim: 6, icon: 0, offset: scrollHeightMsg});
                        return;
                    }
                    var nameStr = cutFileName.join("*");
                    var ajax = new $ax(Hussar.ctxPath + "/personalCollection/addCheck", function (data) {
                        if (data != "true") {
                            layer.msg("存在重名目录", {anim: 6, icon: 0, offset: scrollHeightMsg});
                            layer.close(index);
                            return;
                        } else {
                            var folderIdStr = cutFile.join(",");
                            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/checkChild", function (data) {
                                if (data == "have") {
                                    layer.close(layerView);
                                    layer.msg("目标目录不能是移动目录的本身或子目录", {anim: 6, icon: 0, offset: scrollHeightMsg});
                                    layer.close(index);
                                    return;
                                } else {
                                    layer.close(layerView);
                                    layer.msg("移动成功", {icon: 1, offset: scrollHeightMsg});
                                    updateFolderPid(index, openFolder.node.id);
                                    cutFile = [];
                                    btnState();
                                }
                            }, function (data) {

                            });
                            ajax.set("fsFolderIds", folderIdStr);
                            ajax.set("id", openFolder.node.id);
                            ajax.start();
                            // layer.close(layerView);
                            // layer.msg("移动成功", {icon: 1, offset: scrollHeightMsg});
                            // updateFolderPid(index, openFolder.node.id);
                            // cutFile = [];
                            // btnState();
                        }
                    }, function (data) {

                    });
                    ajax.set("name", nameStr);
                    ajax.set("parentFolderId", openFolder.node.id);
                    ajax.start();
                };
                layer.confirm('确定要移动到此目录下吗？', {
                    title: ['移动', 'background-color:#fff'],
                    offset: scrollHeightAlert,
                    skin: 'move-confirm'
                }, operation);
            }

        })
    }
});
function updateFolderPid(index, id) {
    layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
        var $ = layui.jquery,
            Hussar = layui.Hussar,
            layer = layui.layer,
            $ax = layui.HussarAjax;
        var cutIds = cutFile.join(",");
        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/update", function (data) {
            if ("success" == data) {
                emptyChoose();
                refreshFile(openFileId);
                layer.closeAll();
            } else {
                layer.msg("移动失败", {anim: 6, icon: 0, offset: scrollHeightMsg});
                //layer.msg("目录最多为" + data + "级", {anim: 6, icon: 0, offset: scrollHeightMsg});
                layer.close(index);
            }
        }, function (data) {

        });
        ajax.set("ids", cutIds);
        ajax.set("parentFolderId", id);
        ajax.start();
    })
}
/*取消收藏*/
function cancelCollection(e,id,fileType){
    cancelBubble();
    changeBgColorOfTr(e);
    if (fileType=="folder"){
        layer.confirm('确定要删除吗？',{title :['删除','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'], //0.1透明度的白色背景
                fix:true
                ,offset: scrollHeightAlert
            });

            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;
                var ajax = new $ax(Hussar.ctxPath + "/personalCollection/deleteCollection", function(data) {
                    if(data> 0){
                        var fileList = $("#thelist").find(".item");
                        for(var n = 0;n<fileList.length;n++){
                            var name = fileList.eq(n).find(".info").html().split(".")[0];
                            for(var m =0 ;m<chooseFileName.length;m++){
                                if(name == chooseFileName[m]){
                                    fileList.eq(n).remove();
                                }
                            }
                        }
                        layer.msg('删除成功',{icon: 1,offset:scrollHeightMsg})
                    }else {
                        layer.msg('删除失败',{anim:6,icon: 2,offset:scrollHeightMsg})
                    }
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }, function(data) {
                    layer.msg('删除异常!',{anim:6,icon: 2,offset:scrollHeightMsg})
                    btnState();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                });
                ajax.set("ids",id);
                ajax.set("opType",opType);
                ajax.set("fileType",fileType);
                ajax.start();
            });
        })
    }else {
        layer.confirm('确定要取消收藏吗？',{title :['取消收藏','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'], //0.1透明度的白色背景
                fix:true
                ,offset: scrollHeightAlert
            });

            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;
                var ajax = new $ax(Hussar.ctxPath + "/personalCollection/deleteCollection", function(data) {
                    if(data> 0){
                        var fileList = $("#thelist").find(".item");
                        for(var n = 0;n<fileList.length;n++){
                            var name = fileList.eq(n).find(".info").html().split(".")[0];
                            for(var m =0 ;m<chooseFileName.length;m++){
                                if(name == chooseFileName[m]){
                                    fileList.eq(n).remove();
                                }
                            }
                        }
                        layer.msg('取消收藏成功',{icon: 1,offset:scrollHeightMsg})
                    }else {
                        layer.msg('取消收藏失败',{anim:6,icon: 2,offset:scrollHeightMsg})
                    }
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }, function(data) {
                    layer.msg('取消收藏异常!',{anim:6,icon: 2,offset:scrollHeightMsg})
                    btnState();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                });
                ajax.set("ids",id);
                ajax.set("opType",opType);
                ajax.set("fileType",fileType);
                ajax.start();
            });
        })
    }
};
function refreshFile(folderId,num,size,order) {
    if (folderId==null){
        folderId = 'abcde4a392934742915f89a586989292'
    }
    var screenHeight = parseInt(window.screen.availHeight);
    //console.log(screenHeight);
    if (screenHeight > 728) {
        // $("#marg").css("min-height","768");
    }
    var noOrder;
    // if(order==null||order==undefined||order==''){
    //     noOrder=true;
    //     order = '';
    // }
    currOrder = order;
    layui.use(['laypage', 'layer', 'table', 'Hussar', 'HussarAjax'], function () {
        var laypage = layui.laypage,
            Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var name = $('#searchName').val();
        var ajax = new $ax(Hussar.ctxPath + "/handover/getHandover", function (data) {
            laypage.render({
                elem: 'laypageAre'
                , count: data.count //数据总数，从服务端得到
                , limit: 60
                , layout: ['prev', 'page', 'next']
                , curr: num || 1 //当前页
                , jump: function (obj, first) {
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if (!first) {
                        refreshFile(folderId,obj.curr, obj.limit, currOrder)
                    }
                }
            });

            $("#amount").html("共" + data.count + "");
            openFileId = folderId;
            drawFile(data);
            emptyChoose();
            btnState();
            dbclickover = true;
            if(noOrder==true){
                $("#orderName").hide();
                $("#orderName1").show();
                $("#orderTime").hide();
                $("#orderTime1").hide();
                $("#orderUser").hide();
                $("#orderUser1").hide();
                $("#orderSize").hide();
                $("#orderState").hide();
                $("#orderState1").hide();
                $("#orderDept").hide();
                $("#orderDept1").hide();
                $("#orderType").hide();
                $("#orderType1").hide();
            }else{
                if(order== "1"){
                    $("#orderName").hide();
                    $("#orderName1").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "0"){
                    $("#orderName1").hide();
                    $("#orderName").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "2"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime1").hide();
                    $("#orderTime").show();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "3" ){
                    $("#orderTime").hide();
                    $("#orderTime1").show();
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "4"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").show();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "5"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").show();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "6"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").show();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "7"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").show();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "8"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").show();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                }
                if(order== "9"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").show();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "10"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").show();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "11"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").show();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                }
                if(order== "12"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").show();
                    $("#orderType1").hide();
                }
                if(order== "13"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").show();
                }


            }
            // 取消收藏按钮显示
            $(".hoverEvent").hover(function () {
                $(this).find("td>.hoverSpan").show();
            }, function () {
                $(this).find("td>.hoverSpan").hide();
            });
            $(".layui-table tr").hover(function () {
                //alert($(this).prev());
                $(this).find("td").css("border-color", "#DAEBFE");
                $(this).prev().find("td").css("border-color", "#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color", "rgba(242,246,253,1)");
                $(this).prev().find("td").css("border-color", "rgba(242,246,253,1)");
            });
            $(".layui-table tbody tr:first").hover(function () {
                $(this).find("td").css("border-color", "#DAEBFE");
                $("thead").find("tr").css("border-bottom-color", "#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color", "rgba(242,246,253,1)");
                $("thead").find("tr").css("border-bottom-color", "rgba(242,246,253,1)");
            })
            if (data.count == 0) {
                $("#laypageAre").hide();
            } else {
                $("#laypageAre").show();
            }
        }, function (data) {

        });
        var type = 0;
        $('input[name="type"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
           type =parseInt($(this).val())+type;
        });
        ajax.set("pageNumber", num);
        ajax.set("pageSize", size);
        ajax.set("name", name);
        ajax.set("type", type);
        ajax.set("order", currOrder);
        ajax.set("parentFolderId", folderId);
        ajax.start();
    });
}
function refreshFile2(folderId,num,size,order) {
    if (folderId==null){
        folderId = 'abcde4a392934742915f89a586989292'
    }
    var screenHeight = parseInt(window.screen.availHeight);
    //console.log(screenHeight);
    if (screenHeight > 728) {
        // $("#marg").css("min-height","768");
    }
    var noOrder;
    // if(order==null||order==undefined||order==''){
    //     noOrder=true;
    //     order = '';
    // }
    currOrder = order;
    layui.use(['laypage', 'layer', 'table', 'Hussar', 'HussarAjax'], function () {
        var laypage = layui.laypage,
            Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var name = $('#searchName').val();
        var ajax = new $ax(Hussar.ctxPath + "/handover/getExamine", function (data) {
            laypage.render({
                elem: 'laypageAre'
                , count: data.count //数据总数，从服务端得到
                , limit: 60
                , layout: ['prev', 'page', 'next']
                , curr: num || 1 //当前页
                , jump: function (obj, first) {
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if (!first) {
                        refreshFile2(folderId,obj.curr, obj.limit, currOrder)
                    }
                }
            });

            $("#amount").html("已全部加载" + data.count + "个");
            openFileId = folderId;
            drawFile(data);
            emptyChoose();
            btnState();
            dbclickover = true;
            if(noOrder==true){
                $("#orderName").hide();
                $("#orderName1").show();
                $("#orderTime").hide();
                $("#orderTime1").hide();
                $("#orderUser").hide();
                $("#orderUser1").hide();
                $("#orderSize").hide();
                $("#orderState").hide();
                $("#orderState1").hide();
                $("#orderDept").hide();
                $("#orderDept1").hide();
                $("#orderType").hide();
                $("#orderType1").hide();
                $("#orderNum").hide();
                $("#orderNum1").hide();
            }else{
                if(order== "1"){
                    $("#orderName").hide();
                    $("#orderName1").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "0"){
                    $("#orderName1").hide();
                    $("#orderName").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "2"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime1").hide();
                    $("#orderTime").show();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "3" ){
                    $("#orderTime").hide();
                    $("#orderTime1").show();
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "4"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").show();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "5"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").show();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "6"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").show();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "7"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").show();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "8"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").show();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "9"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").show();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "10"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").show();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "11"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").show();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "12"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").show();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "13"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").show();
                    $("#orderNum").hide();
                    $("#orderNum1").hide();
                }
                if(order== "14"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").show();
                    $("#orderNum1").hide();
                }
                if(order== "15"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderState").hide();
                    $("#orderState1").hide();
                    $("#orderDept").hide();
                    $("#orderDept1").hide();
                    $("#orderType").hide();
                    $("#orderType1").hide();
                    $("#orderNum").hide();
                    $("#orderNum1").show();
                }


            }
            // 取消收藏按钮显示
            $(".hoverEvent").hover(function () {
                $(this).find("td>.hoverSpan").show();
            }, function () {
                $(this).find("td>.hoverSpan").hide();
            });
            $(".layui-table tr").hover(function () {
                //alert($(this).prev());
                $(this).find("td").css("border-color", "#DAEBFE");
                $(this).prev().find("td").css("border-color", "#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color", "rgba(242,246,253,1)");
                $(this).prev().find("td").css("border-color", "rgba(242,246,253,1)");
            });
            $(".layui-table tbody tr:first").hover(function () {
                $(this).find("td").css("border-color", "#DAEBFE");
                $("thead").find("tr").css("border-bottom-color", "#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color", "rgba(242,246,253,1)");
                $("thead").find("tr").css("border-bottom-color", "rgba(242,246,253,1)");
            })
            if (data.count == 0) {
                $("#laypageAre").hide();
            } else {
                $("#laypageAre").show();
            }
        }, function (data) {

        });
        var type = 0;
        $('input[name="type"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            type =parseInt($(this).val())+type;
        });
        ajax.set("pageNumber", num);
        ajax.set("pageSize", size);
        ajax.set("handovername", name);
        ajax.set("type", type);
        ajax.set("order", currOrder);
        ajax.set("parentFolderId", folderId);
        ajax.start();
    });
}
/*新增子目录*/
$("#handover").on('click', function () {
$("#fileNum").html(chooseFile.length);
$("#authorName").val("");
var count = 0;
    $("input[name=type2]").each(function(index, el) {
        el.checked= false;})
    $("input[name=type2]").eq(0).prop("checked","true");
    layui.use(['Hussar','HussarAjax','form'], function() {
        var form = layui.form;
    form.render('radio');
        var flag = 0;
        if(chooseFile.length>0){
            for(var i=0;i<chooseFile.length;i++){
                if(chooseFileState[i]=='0'){
                    flag =1;
                }
            }
        }
        if(flag == 1){
            layer.msg("存在待审核的记录", {anim: 6, icon: 0,offset:scrollHeightMsg});
            return;
        }

    $("#authorId").val("");

        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        $("#categoryName").val("");

        layer.open({
            type: 1,
            btn: ['确定', '取消'],
            skin: 'confirm-class',
            area: ['502px', '303px'],
            fix: false, //不固定
            maxmin: false,
            offset: parseInt(scrollHeightTip) - 25 + "px",
            shadeClose: false,
            shade: 0.4,

            skin: 'creat-folder-dialog',
            title: ["文档交接", 'background-color:#fff'],
            content: $('#addDiv'),
            btn1: function (index, layero) {
                var authorId = $("#authorId").val();
                if (authorId == ""||authorId==undefined) {
                    layer.msg("接收人不能为空", {anim: 6, icon: 0});
                    return;
                }
                if (count == 0){
                    count++;


                    var ajax = new $ax(Hussar.ctxPath + "/handover/add", function (result) {


                        // refreshTree();
                        if(result.code =="0"){
                            layer.msg("提交成功", {icon: 1,offset:scrollHeightMsg});
                            btnState();
                            // refreshTree();
                            refreshFile(openFileId);
                            emptyChoose();
                            layer.close(index);
                        }else if(result.code =="2"){
                            layer.msg("存在不同的用户", {icon: 2, offset: scrollHeightMsg});
                        }else{
                            layer.msg("提交失败", {icon: 2, offset: scrollHeightMsg});
                        }

                    }, function (data) {

                    });
                    ajax.set("ids", chooseFile.join(","));
                    ajax.set("fileTypes", chooseFileType.join(","));
                    ajax.set("authors", chooseFileAuthor.join(","))
                    ajax.set("names", chooseFileName.join(","))
                    ajax.set("acceptId", authorId);
                    ajax.set("handOverType", $('input[name="type2"]:checked ').val());

                    ajax.start();
                } else{
                }


            },
        });
    })
});

function share(e,docId,fileSuffixName,fileName) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            async: false,
            cache: false,
            dataType: "json",
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
        cancelBubble();
        changeBgColorOfTr(e);
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/personalcenter/getInfo",
            data: {
                ids: docId
            },
            async: false,
            cache: false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType: "json",
            success: function (data) {
                if (data.result == "1") {
                    layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
                } else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else {
                    var title = '';
                    var url = "/s/shareConfirm";
                    var w =  538;
                    var h = 311;
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
                        skin:'share-dialog',
                        title: title,
                        closeBtn:2,
                        offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                        content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                    });
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if (data.result == "1") {
                layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
            } else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }else {
                var title = '';
                var url = "/s/shareConfirm";
                var w =  538;
                var h = 311;
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
                    // area: [w + 'px', h + 'px'],
                    fix: false, //不固定
                    maxmin: false,
                    shadeClose: true,
                    shade: 0.4,
                    skin:'share-class',
                    title: title,
                    closeBtn:2,
                    offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                    content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                });
            }
        }, function(data) {

        });
        ajax.set("ids",docId);
        ajax.start();
    });
}
function getNameOrder() {

    if(style==0){

        refreshFile(openFileId,null,null,1)
    }if(style ==1){

        refreshFile2(openFileId,null,null,1)
    }

}
function getNameOrder1() {
    if(style==0){

        refreshFile(openFileId,null,null,0)
    }if(style ==1){

        refreshFile2(openFileId,null,null,0)
    }




}
function getTimeOrder() {
    if(style==0){

        refreshFile(openFileId,null,null,3)
    }if(style ==1){

        refreshFile2(openFileId,null,null,3)
    }


}

function getDeptOrder() {
    if(style==0){

        refreshFile(openFileId,null,null,11)
    }if(style ==1){

        refreshFile2(openFileId,null,null,11)
    }


}
function getDeptOrder1() {
    if(style==0){

        refreshFile(openFileId,null,null,10)
    }if(style ==1){

        refreshFile2(openFileId,null,null,10)
    }


}
function getTimeOrder1() {
    if(style==0){

        refreshFile(openFileId,null,null,2)
    }if(style ==1){

        refreshFile2(openFileId,null,null,2)
    }


}
function getTypeOrder() {
    if(style==0){

        refreshFile(openFileId,null,null,13)
    }if(style ==1){

        refreshFile2(openFileId,null,null,13)
    }


}
function getTypeOrder1() {
    if(style==0){

        refreshFile(openFileId,null,null,12)
    }if(style ==1){

        refreshFile2(openFileId,null,null,12)
    }


}
function getUserOrder() {
    if(style==0){

        refreshFile(openFileId,null,null,5)
    }if(style ==1){

        refreshFile2(openFileId,null,null,5)
    }


}
function getUserOrder1() {
    if(style==0){

        refreshFile(openFileId,null,null,4)
    }if(style ==1){

        refreshFile2(openFileId,null,null,4)
    }


}
function getNumOrder() {
    if(style==0){

        refreshFile(openFileId,null,null,15)
    }if(style ==1){

        refreshFile2(openFileId,null,null,15)
    }


}
function getNumOrder1() {
    if(style==0){

        refreshFile(openFileId,null,null,14)
    }if(style ==1){

        refreshFile2(openFileId,null,null,14)
    }


}
function getStateOrder() {
    if(style==0){

        refreshFile(openFileId,null,null,9)
    }if(style ==1){

        refreshFile2(openFileId,null,null,9)
    }


}
function getStateOrder1() {
    if(style==0){

        refreshFile(openFileId,null,null,8)
    }if(style ==1){

        refreshFile2(openFileId,null,null,8)
    }


}
function orderByDept(){
    if ($("#orderDept").css("display") != "none"){
        getDeptOrder();
    }else {
        getDeptOrder1();
    }
}
function orderByTime(){
    if ($("#orderTime").css("display") != "none"){
        getTimeOrder();
    }else {
        getTimeOrder1();
    }
}
function orderByType(){
    if ($("#orderType").css("display") != "none"){
        getTypeOrder();
    }else {
        getTypeOrder1();
    }
}
function orderByName(){
    if ($("#orderName").css("display") != "none"){
        getNameOrder();
    }else {
        getNameOrder1();
    }
}
function orderByUser(){
    if ($("#orderUser").css("display") != "none"){
        getUserOrder();
    }else {
        getUserOrder1();
    }
}
function orderByState(){
    if ($("#orderState").css("display") != "none"){
        getStateOrder();
    }else {
        getStateOrder1();
    }
}
function orderByNum(){
    if ($("#orderNum").css("display") != "none"){
        getNumOrder();
    }else {
        getNumOrder1();
    }
}
function drawFile(param) {
    if(style ==0){
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param.rows,
            "adminFlag":param.adminFlag
        };
        var getTpl = $("#demo1").html()
            ,view = document.getElementById('view');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view");
            var tableWidth =inner.width();
            //fixed-table-header
            $(".fixed-table-header").width(tableWidth);
            if (param.rows.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                },200);
            }else {
                $("div.noDataTip").hide();
            }
        });
    });
    }else {
        layui.use('laytpl', function(){
            var laytpl = layui.laytpl;
            var data = { //数据
                "list":param.rows,
                "adminFlag":param.adminFlag
            };
            var getTpl = $("#demo2").html()
                ,view = document.getElementById('view');
            laytpl(getTpl).render(data, function(html){
                view.innerHTML = html;
                var inner = $("#view");
                var tableWidth =inner.width();
                //fixed-table-header
                $(".fixed-table-header").width(tableWidth);
                if (param.rows.length == 0){
                    setTimeout(function () {
                        $("div.noDataTip").show();
                    },200);
                }else {
                    $("div.noDataTip").hide();
                }
            });
        });
    }

}
function dbclick(id,type,name){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        if (type=='folder'){
            pathId.push(id);
            pathName.push(name);
            createPath();
            if(style==0){

                refreshFile()
            }if(style ==1){

                refreshFile2()
            }


        }else {
            var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
                if(data.result =="1"){
                    layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
                }
                else if(data.result =="2"){
                    layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
                }else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else{
                    if(dbclickover==true) {
                        if (clickFlag) {//取消上次延时未执行的方法
                            clickFlag = clearTimeout(clickFlag);
                        }
                        dbclickover=false;
                        reNameFlag = false;


                        showPdf(id, type, name)

                    }
                }
            }, function(data) {

            });
            ajax.set("ids",id);
            ajax.start();
        }

    });

}
function createPath(){
    $("#path").empty();
    // $("#path").append("<span class='total'>");
    for(var i=0;i<pathId.length;i++){
        if(i==pathId.length-1){
            var param= '<span>'+pathName[i]+'</span>'
        }else {
            var param= '<span><a style="cursor: pointer; color: #3C91FD;" onclick="clickPath(\''+pathId[i]+'\')">'+pathName[i]+'</a>'+'  >  </span>'        }
        $("#path").append(param);


    }
    var timer;
    // $("#path").append("</span>");
    $(".message #path").hover(function () {
        var _this = $(this);
        var len = _this.width();

        var width = _this.parent().width();
        if(len >= width){
            var num = 0;
            var change = 0;
            timer = setInterval(function () {
                if (navigator.userAgent.indexOf('iPad') != -1){
                    change = 240-len;
                }else{
                    change = 290-len;
                }
                if (num <= change) {
                    clearInterval(timer);
                }
                num -= 1;
                _this.css("left",num);
            }, 25);
        }
    },function () {
        clearInterval(timer);
        $(this).css("left",0)
    })
}
function clickPath(id) {
    while(pathId.indexOf(id)+1!=pathId.length){
        pathId.pop();
        pathName.pop();
    }
    createPath();
    refreshFile(id);
}
function download(id,name){
    //cancelBubble();
    layui.use(['Hussar','HussarAjax'], function() {
        var Hussar = layui.Hussar;
        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew?docIds="+id,
            type : "post",
            async:false
        });
    })
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}
function  iconDownLoad(e,id,name) {
    cancelBubble();
    changeBgColorOfTr(e);

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/personalcenter/getInfo",
            data:{
                ids:id
            },
            async:false,
            cache:false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType:"json",
            success:function(data){
                if(data.result =="1"){
                    layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
                }
                else if(data.result =="2"||data.result =="3"){
                    layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
                }else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else{
                    $.ajax({
                        url: Hussar.ctxPath+"/integral/downloadIntegral",
                        async: true,
                        data: {
                            docId: id,
                            ruleCode: 'download'
                        },
                        success: function (data) {
                            if(data.status=="1") {

                                var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                    icon: 3,
                                    title: '提示',
                                    offset: scrollHeightAlert
                                }, function (index) {
                                    layer.close(index2);
                                    var index = layer.load(1, {
                                        shade: [0.1, '#fff'],//0.1透明度的白色背景
                                        scrollbar: false,
                                        time: 1000
                                        ,offset: scrollHeightAlert
                                    });
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
                                                $(".integral").css("top", scrollHeightAlert);
                                                $(".integral").show();
                                                //alert($("#totalIntegral",parent.document).text());
                                                // 实时更新积分
                                                $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
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

                                });
                            }else {
                                var index = layer.load(1, {
                                    shade: [0.1, '#fff'],//0.1透明度的白色背景
                                    scrollbar: false,
                                    time: 1000
                                    ,offset: scrollHeightAlert
                                });
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
                                            $(".integral").css("top", scrollHeightAlert);
                                            $(".integral").show();
                                            //alert($("#totalIntegral",parent.document).text());
                                            // 实时更新积分
                                            $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
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
                            }
                        }})
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if(data.result =="1"){
                layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
            }
            else if(data.result =="2"||data.result =="3"){
                layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
            }else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }else{
                /*$.ajax({
                    url: Hussar.ctxPath+"/integral/downloadIntegral",
                    async: true,
                    data: {
                        docId: id,
                        ruleCode: 'download'
                    },
                    success: function (data) {
                        if(data.status=="1") {

                            var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                icon: 3,
                                title: '提示',
                                offset: scrollHeightAlert
                            }, function (index) {
                                layer.close(index2);
                                var index = layer.load(1, {
                                    shade: [0.1, '#fff'],//0.1透明度的白色背景
                                    scrollbar: false,
                                    time: 1000
                                    ,offset: scrollHeightAlert
                                });
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
                                            $(".integral").css("top", scrollHeightAlert);
                                            $(".integral").show();
                                            //alert($("#totalIntegral",parent.document).text());
                                            // 实时更新积分
                                            $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
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

                            });
                        }else {
                            var index = layer.load(1, {
                                shade: [0.1, '#fff'],//0.1透明度的白色背景
                                scrollbar: false,
                                time: 1000
                                ,offset: scrollHeightAlert
                            });
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
                                        $(".integral").css("top", scrollHeightAlert);
                                        $(".integral").show();
                                        //alert($("#totalIntegral",parent.document).text());
                                        // 实时更新积分
                                        $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
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
                        }
                    }})*/
                var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {
                    if(data.status=="1") {

                        var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                            icon: 3,
                            title: '提示',
                            offset: scrollHeightAlert,
                            skin:'download-info',
                        }, function (index) {
                            layer.close(index2);
                            var index = layer.load(1, {
                                shade: [0.1, '#fff'],//0.1透明度的白色背景
                                scrollbar: false,
                                time: 1000
                                ,offset: scrollHeightAlert
                            });
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
                                        $(".integral").css("top", scrollHeightAlert);
                                        $(".integral").show();
                                        //alert($("#totalIntegral",parent.document).text());
                                        // 实时更新积分
                                        $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
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
                                    $(".integral").css("top", scrollHeightAlert);
                                    $(".integral").show();
                                    //alert($("#totalIntegral",parent.document).text());
                                    // 实时更新积分
                                    $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
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
                        });
                    }else {
                        var index = layer.load(1, {
                            shade: [0.1, '#fff'],//0.1透明度的白色背景
                            scrollbar: false,
                            time: 1000
                            ,offset: scrollHeightAlert
                        });
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
                                    $(".integral").css("top", scrollHeightAlert);
                                    $(".integral").show();
                                    //alert($("#totalIntegral",parent.document).text());
                                    // 实时更新积分
                                    $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
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
                                $(".integral").css("top", scrollHeightAlert);
                                $(".integral").show();
                                //alert($("#totalIntegral",parent.document).text());
                                // 实时更新积分
                                $("#totalIntegral", parent.document).text(parent.getTotalIntegral());
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
                    }
                }, function(data) {

                });
                ajax.set("docId",id);
                ajax.set("ruleCode",'download');
                ajax.start();
            }
        }, function(data) {

        });
        ajax.set("ids",id);
        ajax.start();
    });

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
    dbclickover = true;
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
function isPDFShow(fileSuffixName){
    return fileSuffixName == ".pdf"
        || fileSuffixName == ".doc" || fileSuffixName == ".docx" || fileSuffixName == ".dot"
        || fileSuffixName == ".wps" || fileSuffixName == ".wpt"
        || fileSuffixName == ".xls" || fileSuffixName == ".xlsx" || fileSuffixName == ".xlt"
        || fileSuffixName == ".et" || fileSuffixName == ".ett"
        || fileSuffixName == ".ppt" || fileSuffixName == ".pptx" || fileSuffixName == ".ppts"
        || fileSuffixName == ".pot" || fileSuffixName == ".dps" || fileSuffixName == ".dpt"
        || fileSuffixName == ".txt"
        || fileSuffixName == ".ceb";
}
function btnState() {
    if(style ==0){
        $("#back").hide();
        $("#pass").hide();
    if(chooseFile.length==0){
        $('.clickBtn').hide()
    }
    if(chooseFile.length>0){
        $('.clickBtn').show()
    }
    }else{
        if(chooseFile.length==0){
            $("#back").hide();
            $("#pass").hide();
        }
        if(chooseFile.length>0){
            $("#back").show();
            $("#pass").show();
        }
    }

}
function changeBgColorOfTr(e){
    var jq=$(e);
    //console.log(e.tagName.toLowerCase());
    if (e.tagName.toLowerCase() != "tr"){
        jq = jq.parents(".hoverEvent");
    }
    jq.parent().find("tr").css("background-color","#fff");

    jq.css("background-color","rgba(246, 250, 255, 1)");
}
function  clickCheck(e,id,type,name,index,author,state) {
    var jq=$(e);
    changeBgColorOfTr(e);
    if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            if(style==0){

                refreshFile(openFileId,null,null,0)
            }if(style ==1){

                refreshFile2(openFileId,null,null,0)
            }


        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if(jq.find(".checkbox").prop("checked")==false){

        jq.find(".checkbox").prop("checked",true);
        jq.find(".layui-form-checkbox").addClass("layui-form-checked");

        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)
        chooseFileState.push(state)


    }else{
        jq.find(".checkbox").prop("checked",false);
        jq.find(".layui-form-checkbox").removeClass("layui-form-checked");
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
                chooseFileState=chooseFileState.del(chooseFile.indexOf(id))
                chooseFile=chooseFile.del(chooseFile.indexOf(id));
            }

        }
        if(chooseFile.length==1){
            var id = chooseFile[0];
            reNameParem=chooseFileName[0];
            var index=  $("#"+id+"").val();
            reNameIndex=index
        }

    }
    btnState();
    cancelBubble()
}
function  clickCheck2(e,id) {
    var jq=$(e);
    changeBgColorOfTr(e);
    if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile2(openFileId)
        } else {
            reNameIndex = index;
        }
    }
    if(jq.find(".checkbox").prop("checked")==false){

        jq.find(".checkbox").prop("checked",true);
        jq.find(".layui-form-checkbox").addClass("layui-form-checked");

        chooseFile.push(id);



    }else{
        jq.find(".checkbox").prop("checked",false);
        jq.find(".layui-form-checkbox").removeClass("layui-form-checked");
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){

                chooseFile=chooseFile.del(chooseFile.indexOf(id));
            }
        }
        if(chooseFile.length==1){
            var id = chooseFile[0];
            var index=  $("#"+id+"").val();
            reNameIndex=index
        }

    }
    btnState();
    cancelBubble()
}
$("#marg").on('click', function () {
    if(reNameFlag==true){
        $('#name'+reNameIndex).removeClass("hide");
        $('#inputName'+reNameIndex).addClass("hide");
        reNameFlag=false;
        var inputname = $('#inputName'+reNameIndex).val();
        if(inputname!=reNameParem){
            rename(inputname);
        }
    }
});
function checkAll(e) {
    var self = $(e).siblings('input');//
    $(e).toggleClass('layui-form-checked');
    if(self.prop("checked")==false){
        self.prop("checked",true);
    }else{
        self.prop("checked",false);
    }
    if (self.prop("checked")) { // 全选
        chooseFileType=[];
        chooseFileName=[];
        chooseFile=[];
        chooseFileAuthor=[]
        chooseFileState=[];
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", true);
            $(this).siblings('.layui-form-checkbox').addClass("layui-form-checked");
            var checkFileId=  $(this).siblings(".checkFileId").val();
            var checkFileType=  $(this).siblings(".checkFileType").val()
            var checkFileName=  $(this).siblings(".checkFileName").val()
            var checkFileAuthor=  $(this).siblings(".chooseFileAuthor").val()
            var checkFileState=  $(this).siblings(".chooseFileState").val()
            chooseFile.push(checkFileId);
            chooseFileType.push(checkFileType);
            chooseFileName.push(checkFileName);
            chooseFileAuthor.push(checkFileAuthor)
            chooseFileState.push(checkFileState)
        });
    }
    else { // 取消全选
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", false);
            $(this).siblings('.layui-form-checkbox').removeClass("layui-form-checked");
        });
        chooseFileType=[];
        chooseFileName=[];
        chooseFile=[];
        chooseFileAuthor=[];
        chooseFileState = []
    }
    btnState();
}
Array.prototype.del=function(n) {
    if(n<0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0,n).concat(this.slice(n+1,this.length));
}
function emptyChoose() {
    chooseFile = [];
    chooseFileType = [];
    chooseFileName = [];
    chooseFileAuthor=[]
    chooseFileState=[]
}
function  clickIconCheck(e,id,type,name,index,author,state) {
    $(e).toggleClass('layui-form-checked');
    var checkbox=$(e).siblings('.checkbox');
    if(checkbox.prop("checked")==false){
        checkbox.prop("checked",true);
        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)
        chooseFileState.push(state)
    }else{
        checkbox.prop("checked",false);
        $()
        if(chooseFile.indexOf(id)!=-1){
            chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
            chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
            chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
            chooseFileState=chooseFileState.del(chooseFile.indexOf(id))
            chooseFile=chooseFile.del(chooseFile.indexOf(id));

        }
    }
    btnState();
    cancelBubble()
}
function  clickIconCheck2(e,id) {
    $(e).toggleClass('layui-form-checked');
    var checkbox=$(e).siblings('.checkbox');
    if(checkbox.prop("checked")==false){
        checkbox.prop("checked",true);
        chooseFile.push(id);

    }else{
        checkbox.prop("checked",false);
        $()
        if(chooseFile.indexOf(id)!=-1){

            chooseFile=chooseFile.del(chooseFile.indexOf(id));
        }
    }
    btnState();
    cancelBubble()
}
function openView(id) {
    layui.use(['Hussar','HussarAjax'], function() {
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        layer.open({
            skin: 'handover-detail',
            type: 2,
            area: [646 + 'px', 587 + 'px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: true,
            shade: 0.4,
            btn: ['通过', '驳回'],
            title: ["交接详情", 'background-color:#fff'],
            content: "/handover/view?id=" + id,
            btn1: function (index, layero) {
                var index2 = layer.load(1, {
                    shade: [0.1, '#fff'] //0.1透明度的白色背景
                });

                var ajax = new $ax(Hussar.ctxPath + "/handover/pass", function (data) {
                    if (data.code == 0) {

                        layer.msg("通过成功", {icon: 1, offset: scrollHeightMsg});

                    } else {

                        layer.msg("通过异常", {icon: 2, offset: scrollHeightMsg});
                    }

                    btnState();
                    // refreshTree();
                    refreshFile2();
                    emptyChoose();
                    layer.close(index);
                    layer.close(index2);
                }, function (data) {
                    layer.msg("通过异常", {icon: 2, offset: scrollHeightMsg});
                    btnState();
                    emptyChoose();
                    layer.close(index);
                });
                ajax.set("idStr", id);
                ajax.start();
            },
            btn2: function (index, layero) {
                var index = layer.load(1, {
                    shade: [0.1, '#fff'] //0.1透明度的白色背景
                });
                var ajax = new $ax(Hussar.ctxPath + "/handover/back", function (data) {
                    if (data.code == 0) {

                        layer.msg("驳回成功", {icon: 1, offset: scrollHeightMsg});

                    } else {

                        layer.msg("驳回异常", {icon: 2, offset: scrollHeightMsg});
                    }
                    btnState();
                    // refreshTree();
                    refreshFile2();
                    emptyChoose();
                    layer.close(index);
                }, function (data) {
                    layer.msg("驳回异常", {icon: 2, offset: scrollHeightMsg});
                    btnState();

                    emptyChoose();
                    layer.close(index);
                });
                ajax.set("idStr", id);
                ajax.start();
            },

        });
    })
}
//得到事件
function getEvent(){
    if(window.event)    {return window.event;}
    func=getEvent.caller;
    while(func!=null){
        var arg0=func.arguments[0];
        if(arg0){
            if((arg0.constructor==Event || arg0.constructor ==MouseEvent
                || arg0.constructor==KeyboardEvent)
                ||(typeof(arg0)=="object" && arg0.preventDefault
                    && arg0.stopPropagation)){
                return arg0;
            }
        }
        func=func.caller;
    }
    return null;
}
//阻止冒泡
function cancelBubble()
{
    var e=getEvent();
    if(window.event){
        //e.returnValue=false;//阻止自身行为
        e.cancelBubble=true;//阻止冒泡
    }else if(e.preventDefault){
        //e.preventDefault();//阻止自身行为
        e.stopPropagation();//阻止冒泡
    }
}
function  updateNameCollectionFolder(e,id,type,name,author,index) {
    $(".hoverSpan").eq(index).hide()
    $(".moreicon").hide();
    $(".ishover").addClass("hide");
    $(".nameTitpe").removeClass("hide");
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor = [];
    chooseFileState =[]
    chooseFile.push(id);
    chooseFileType.push(type);
    chooseFileName.push(name);
    chooseFileAuthor.push(author)

    reNameIndex = index;
    reNameParem=name;
    $('#inputName'+index).val( chooseFileName[0]);
    $('#name'+index).addClass("hide");
    $('#inputName'+index).removeClass("hide");
    $('#inputName'+index).select();
    $('#inputName'+index).focus();
    reNameFlag=true;
}
$(function(){

    setInterval(function () {
        scrollHeight=parent.scrollHeight;
        var height = parseInt(scrollHeight);
        var screenHeight = parseInt(window.screen.availHeight);
        if( scrollHeight!=0){
            scrollHeightAlert= parseInt(height - 130 + (screenHeight - 154) / 2.0) + "px";
            scrollHeightLong= parseInt(height - 130 + (screenHeight - 510) / 2.0) + "px";
            scrollHeightTip = parseInt(height - 130 + (screenHeight - 250) / 2.0) + "px";
            scrollHeightShare = parseInt(height - 130 + (screenHeight - 200) / 2.0) + "px";
            scrollHeightMsg = parseInt(height - 130 + (screenHeight - 64) / 2.0) + "px";
            //console.log(scrollHeightAlert + ' ' + scrollHeightLong)
        }
        //console.log(height + "//" + screenHeight + " " + layerHeight)
        //$(".layui-layer.layui-layer-iframe").css("top",height - 130 + (screenHeight - layerHeight) / 2.0 + "px");
    },300);

    $('#searchName').bind('keypress', function (event) {
        if (event.keyCode == "13") {
            $("#searchBtn").click();
        }
    });
})
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").css("top",scrollHeightAlert);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}
function tryPop(th,id,type,name,index,author,state){
    if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile(openFileId)
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if($(th).prop("checked")){
        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)
        chooseFileState.push(state)
    }else{
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFileAuthor=  chooseFileAuthor.del(chooseFile.indexOf(id))
                chooseFileState=  chooseFileState.del(chooseFile.indexOf(id))
                chooseFile=chooseFile.del(chooseFile.indexOf(id));

            }
        }
        if(chooseFile.length==1){
            var id = chooseFile[0];
            reNameParem=chooseFileName[0];
            var index=  $("#"+id+"").val();
            reNameIndex=index
        }
    }
    btnState();
    cancelBubble()
}
function tryPop2(th,id){
    if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile2(openFileId)
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if($(th).prop("checked")){
        chooseFile.push(id);

    }else{
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFile=chooseFile.del(chooseFile.indexOf(id));

            }
        }
        if(chooseFile.length==1){
            var id = chooseFile[0];
           
            var index=  $("#"+id+"").val();
            reNameIndex=index
        }
    }
    btnState();
    cancelBubble()
}
function rename(inputname){
    if(chooseFileType[0]=='folder'){
        inputname = inputname.trim();
        if (inputname == '' || inputname == undefined) {
            layer.msg("目录名称不能为空", {anim: 6, icon: 0,offset:scrollHeightMsg});
            $('#inputName' + reNameIndex).val(reNameParem);
            return;
        }
        var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
        //特殊字符
        if (!pattern.test(inputname)) {
            layer.msg("输入的目录名称不合法", {anim: 6, icon: 0,offset:scrollHeightMsg});
            $('#inputName' + reNameIndex).val(reNameParem);
            return;
        }
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;
            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/addCheck", function(data) {
                if (data == "false") {
                    layer.msg("“" + inputname + "”目录已存在", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    $('#inputName' + reNameIndex).val(reNameParem);
                    return;
                } else {
                    var ajax = new $ax(Hussar.ctxPath + "/personalCollection/updateFolderName", function(result) {
                        refreshFile(openFileId);
                    }, function(data) {
                    });
                    ajax.set("collectionId",chooseFile[0]);
                    ajax.set("folderName",inputname);
                    ajax.start();
                }
            }, function(data) {

            });
            ajax.set("name",inputname);
            ajax.set("parentFolderId",openFileId);
            ajax.start();
        });
    }
    refreshFile(openFileId);
}