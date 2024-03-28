var personData = [];
var pageData = [];
var tableIns;
var buttonType;//点击按钮的类型
var oldData=[];
var treeData;
if(undefined !=$('#groupType').val() && ''!= $('#groupType').val() && 'butAdd'==$('#groupType').val()){
    $(".layui-form").hide();
    $(".authority-top>div.content").css("height","100%");
}
layui.use(['layer','Hussar', 'HussarAjax','form','table','jquery','element','jstree'], function(){
    var layer = layui.layer
        ,table = layui.table
        ,element = layui.element
        ,Hussar = layui.Hussar
        ,$ = layui.jquery
        ,$ax = layui.HussarAjax,
        jstree=layui.jstree,
        form = layui.form;
    $(function () {
        initRitTable();   //初始化右侧表格
        initLeftTree();   //初始化左侧树
        initButtonEvent();
        getSort();
        $(window).resize(function () {
            initRitTable();   //初始化右侧表格
        })
    });
    //初始化左侧树
    function initLeftTree() {
        getRenData();
    }
    //初始化右侧表格
    function initRitTable() {
        if(undefined !=$('#groupId').val() && $('#groupId').val()!=''){
            buttonType='edit';
            initTable();
        }else{
            buttonType='treeAdd';
            if(!!oldData){
                initChooseTable(oldData);
            }else{
                initChooseTable([]);
            }

        }
        if(undefined !=$('#groupType').val() && $('#groupType').val()!=''){
            buttonType='butAdd';
        }
    }
    function initButtonEvent() {
        $("#batchDel").on('click',function(){
            var tableBak =  table.cache["personAddTable"];
            var checkStatus = table.checkStatus('personAddTable');
            checkStatus = checkStatus.data;
            if(checkStatus.length>0){
                for(var i = 0;i<checkStatus.length;i++){
                    for(var j = 0;j<tableBak.length;j++){
                        if(tableBak[j].personId == checkStatus[i].personId){
                            tableBak.splice(j,1);
                            $("#"+checkStatus[i].personId+"_anchor").parent().children("span.treeSpanActive").remove();
                        }
                    }
                }
                initChooseTable(tableBak);
            }else{
                layer.msg("请选择要删除的人员", {anim:6,icon: 0});
            }
        })
        /*关闭弹窗*/
        $("#cancel").on('click',function(){
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        });
    }

    // 获取人员树数据
    function getRenData() {
        var data;
        var ajax = new $ax(Hussar.ctxPath + "/orgTreeDemo/usersTree",function(result) {
            data = result;
        }, function(data) {
            Hussar.error("获取人员失败");
        });
        ajax.set("treeType", 2);
        ajax.start();
        initEmployeeTree(data);
    }
    // 加载人员树
    function initEmployeeTree(data){
        var $tree = $("#renTree");
        $tree.jstree({
            core: {
                data: data
            },
            plugins: ['types','search'],
            types:{
                "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/root.png"},
                "1":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/com.png"},
                "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/station.png"},
                "4":{'icon' : Hussar.ctxPath+"/static/resources/img/fsfile/group-blue.png"},
                "5":{'icon' : Hussar.ctxPath+"/static/resources/img/fsfile/group-blue.png"},
                "USER":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/user.png"}
            },
            search:treeSearch("renTree","personSearch")
        });

        $tree.bind('activate_node.jstree', function (obj,e){
            if(e.node.original.type != "USER"){
                layer.msg("请选择人员");
                return;
            }else{
                oldData =  table.cache["personAddTable"];
                var id = "#"+e.node.a_attr.id;
                if($(id).parent().children("span.treeSpanActive").length>0){
                    $(id).parent().children("span.treeSpanActive").remove();
                    var id = e.node.original.id;
                    for(var i = 0;i<oldData.length;i++){
                        if(oldData[i].personId == id){
                            oldData.splice(i,1);
                            break;
                            // personData[0].splice(i,1);
                        }
                    }
                }else {
                    $(id).parent().append("<span class='treeSpanActive'></span>")
                    var pNode = $('#renTree').jstree("get_node", e.node.parent);
                    var data1={"personId":e.node.original.id,"deptName":pNode.text,"personName":e.node.original.text,"type":1};
                    oldData.push(data1);
                }
                initChooseTable(oldData)
            }
        })
        $tree.bind("open_node.jstree", function (e,data) {
            if(personData.length>0){
                for(var i = 0;i<personData[0].length;i++){
                    var id = personData[0][i].personId+"_anchor";
                    //$('#'+id).next().remove();
                    $('#'+id).parent().append("<span class='treeSpanActive'></span>")
                }
            }
        });
    }
    function initTable() {
        var groupId = $('#groupId').val();
        tableIns = table.render({
            elem: '#personAddTable', //指定原始表格元素选择器（推荐id选择器）
            height: $(".rit-bot").height() - 20,//容器高度
            url: Hussar.ctxPath+'/group/getPersonList' ,//数据接口
            even: true,//开启隔行背景
            where: {
                groupId: groupId
            },
            done: function (res) {
                pageData = res.data;
                personData.push(res.data);
            },
            request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            },
            page: false, //开启分页
            limit: 1000, //每页默认显示的数量
            cols: [[
                {field: 'personId', title: 'id', type: 'checkbox', width: '5%', align: "center"},
                {
                    field: 'deptName',
                    title: '部门名称',
                    width: '45%',
                    align: "center",
                    templet:function (data) {
                        var title = data.deptName;
                        return "<div class='textOver' title='"+title+"'>"+title+"</div>"
                    }
                },
                {field: 'personName', title: '人员名称', width: '35%', align: "center",
                    templet:function (data) {
                        var title = data.personName;
                        return "<div class='textOver' title='"+title+"'>"+title+"</div>"
                    }},
                {title: '操作', toolbar: '#barDemo', width: '12%', align: "center" }
            ]] //设置表头
        });

        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'del'){
                var id = "#"+data.personId;
                $(id).find("span[class='treeSpanActive']").remove();
                for(var i = 0;i<personData[0].length;i++){
                    if(personData[0][i].personId == data.personId){
                        if( personData[0][i].length == 0){
                            personData[0][i].splice(i,1);
                        }
                    }
                }
                obj.del();
            }
        });
        //.监听选择，记录已选择项
        table.on('checkbox(personAddTable)', function (obj) {
            //.全选或单选数据集不一样
            var data = obj.type == 'one' ? [obj.data] : pageData;
            //.遍历数据
            $.each(data, function (k, v) {
                //.假设你数据中 id 是唯一关键字
                if (obj.checked) {
                    //.增加已选中项
                    layui.data('checked', {
                        key: v.personId, value: v
                    });
                } else {
                    //.删除
                    layui.data('checked', {
                        key: v.personId, remove: true
                    });
                }
            });
        });
    }
    // 加载表格
    function initChooseTable(data) {
        table.render({
            elem: '#personAddTable', //指定原始表格元素选择器（推荐id选择器）
            height: $(".rit-bot").height() - 20//容器高度
            ,cols: [[ //标题栏
                {field: 'personId', title: 'id', type: 'checkbox', width: '5%', align: "center"},
                {
                    field: 'deptName',
                    title: '部门名称',
                    width: '45%',
                    align: "center",
                    templet:function (data) {
                        var title = data.deptName;
                        return "<div class='textOver' title='"+title+"'>"+title+"</div>"
                    }
                },
                {field: 'personName', title: '人员名称', width: '35%', align: "center",
                    templet:function (data) {
                        var title = data.personName;
                        return "<div class='textOver' title='"+title+"'>"+title+"</div>"
                    }},
                {title: '操作', toolbar: '#barDemo', width: '12%', align: "center" }
            ]]
            ,data:data
            //,skin: 'line' //表格风格
            ,even: true
            ,page: false //是否显示分页
            //,limits: [5, 7, 10]
            ,limit: 1000 //每页默认显示的数量
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'del'){
                if(data.type == 1){
                    var id = "#"+data.personId+"_anchor";
                    $(id).parent().children("span.treeSpanActive").remove();
                }else{
                    $("#"+data.personId).find("span[class='treeSpanActive']").remove();
                }
                obj.del();
            }
        });
    }

    /*新增/编辑专题*/
    $("#saveBtn").on('click',function(){
        var groupName = $("#groupName").val().trim();//群组名称
        var pattern = new RegExp("^[^/\\\\:\\*\\?\\<\\>\\|\"]{0,255}$"); //特殊字符
        var tableBak = table.cache["personAddTable"];
        var userId ='';
        var sortId =  $("#sortId").val();
        if(groupName == ""){
            layer.msg("群组名称不能为空", {anim:6,icon: 0});
            return;
        }
        if (!pattern.test(groupName)) {
            layer.msg("输入的群组名称不合法", {anim:6,icon: 0});
            return;
        }
        if(groupName.trim().length > 15){
            layer.msg("群组不能超过15个字符", {anim:6,icon: 0});
            return;
        }
        if (tableBak != null && tableBak.length>0 ){
            var arr=new Array();
            for(var i= 0;i<tableBak.length;i++){
                if( tableBak[i].length!=0){
                    userId = tableBak[i].personId;
                    arr.push(userId);
                }
            }
            userId = arr.join(",");
        }else{
            layer.msg("请选择人员", {anim:6,icon: 0});
            return;
        }
        if(userId.length<=0){
            layer.msg("请选择人员", {anim:6,icon: 0});
            return;
        }
        var url;//请求地址
        var successMsg,errorMsg;//成功失败提示
        var groupId;
        if(buttonType=='edit'){
            url = "/group/editGroup";
            groupId=$('#groupId').val();
        }else if(buttonType=='butAdd'){
            url = "/group/addGroupUser";
            groupId=$('#groupId').val();
        }else{
            url = "/group/addGroup";
            groupId='';
        }
        successMsg = "保存成功";
        errorMsg = "保存失败";
        /*$.ajax({
            type:"get",
            url: Hussar.ctxPath+url,
            data:{
                groupId:groupId,
                groupName:groupName,
                userId:userId,
                groupType: buttonType,
                sortId:sortId
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                if(data.result == "0"){
                    layer.msg("该群组已存在", {anim: 6, icon: 0});
                }else if(data.result == "1"){
                    layer.msg('保存成功',{time:1 * 1000,icon: 1},function() {
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.window.$("#powerTree").jstree('deselect_all');
                        parent.window.$("#powerTree").jstree(true).select_node(groupId);
                        parent.window.$("#powerTree").jstree(true).refresh();
                        parent.layer.close(index);
                    })
                }else{
                    layer.msg("保存失败", {anim: 6, icon: 0});
                }
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + url, function(data) {
            if(data.result == "0"){
                layer.msg("该群组已存在", {anim: 6, icon: 0});
            }else if(data.result == "1"){
                layer.msg('保存成功',{time:1 * 1000,icon: 1},function() {
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.window.$("#powerTree").jstree('deselect_all');
                    parent.window.$("#powerTree").jstree(true).select_node(groupId);
                    parent.window.$("#powerTree").jstree(true).refresh();
                    parent.layer.close(index);
                })
            }else{
                layer.msg("保存失败", {anim: 6, icon: 0});
            }
        }, function(data) {

        });
        ajax.set("groupId",groupId);
        ajax.set("groupName",groupName);
        ajax.set("userId",userId);
        ajax.set("groupType",buttonType);
        ajax.set("sortId",sortId);
        ajax.start();
    });
    //点击所属群组后
    $("#parentSortName").click(function(){
        // 先让其他input失去焦点
        $("input").blur();
        var authName=$("#parentSortName").val().trim();
        layerView=layer.open({
            type: 1,
            area: ['350px','450px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: "所属分组",
            content: $("#employeeTreeDiv"),
            success:function(){
                initSortTree(treeData,authName);
            }
        });
    });
    /**
     * 人员树模糊查询
     */
    function treeSearch(treeId,searchId) {
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
                // $tree.jstree(true).search(v);
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
            $("#parentSortName").val(e.node.original.text);
            $("#sortId").val(e.node.original.id);
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
            }
            treeData = arrays;
        }, function(data) {
            Hussar.error("加载群组列表失败");
        });
        ajax.set("treeType","2");
        ajax.start();
    });
}