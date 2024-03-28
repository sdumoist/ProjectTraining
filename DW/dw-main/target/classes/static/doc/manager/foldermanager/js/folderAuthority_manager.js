var personTreeData = [];
var groupTreeData = [];
var choose = [];
var personInit = [];
var groupInit = [];
var editFlag;
var openPersonId = [];
var openGroupId = [];
var operateTypeValue;
var personParamPower = [];
var groupParamPower = [];
var groupId = [];
var personId = [];
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
        initLeftTree();   //初始化左侧树
        initRitTable() ;   //初始化右侧表格
        checkAllUser();
        form.render();
        /*取消按钮*/
        $("#cancelBtn").on('click',function(){
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        });
        /*批量删除*/
        $("#delBtn").on('click',function(){
            var checkStatus = table.checkStatus('rit_table')
            checkStatus = checkStatus.data;
            if(checkStatus.length==0){
                layer.msg("请选择要删除的记录", {anim:6,icon: 0});
                return;
            }
            var oldData =  table.cache["rit_table"];
            for(var i = 0;i<checkStatus.length;i++){   //删掉表格中的数据
                for(var j = 0;j<oldData.length;j++){
                    if(oldData[j].id == checkStatus[i].id){
                        oldData = oldData.del(j);
                        break;
                    }
                }
            }
            table.reload('rit_table',{
                data : oldData
            });
            choose = oldData;
            var group = [];
            var person = [];
            for(var i = 0;i<checkStatus.length;i++){
                if(checkStatus[i].type == "0" || checkStatus[i].type == "2"){
                    person.push("#"+checkStatus[i].organId+"_anchor");
                }else {
                    group.push("#"+checkStatus[i].id+"_anchor")
                }
            }
            for(var i = 0;i<person.length;i++){
                $(person[i]).parent().children("span.treeSpanActive").remove();   //删掉人员树上对应的对号
                for(var j = 0;j<openPersonId.length;j++){
                    if(openPersonId[j] == person[i]){
                        openPersonId = openPersonId.del(j);
                        break;
                    }
                }
            }
            for(var i = 0;i<group.length;i++){
                if(group[i] == '#allpersonflag_anchor'){
                    $("#allUser").prop('checked', false);
                    form.render();
                }
                $(group[i]).parent().children("span.treeSpanActive").remove();      //删掉群组树上对应的对号
                for(var j = 0;j<openGroupId.length;j++){
                    if(openGroupId[j] == group[i]){
                        openGroupId = openGroupId.del(j);
                        break;
                    }
                }
            }

        });
        /*保存按钮*/
        $("#saveBtn").on('click',function(){
            var group = [];
            var person = [];
            for(var i = 0;i<choose.length;i++){
                if(choose[i].type == "0" || choose[i].type == "2"){
                    person.push(choose[i]);
                }else {
                    group.push(choose[i])
                }
            }
            var groupStrPower = '';
            var personStrPower ='';
            var personOrganStrPower ='';
            var groupPower = [];
            var personPower = [];
            var personOrganPower = [];
            var authorTypeGroup = [];
            var authorTypePerson = [];
            var authorTypeStrGroup = '';
            var authorTypeStrPerson = '';
            var operateTypeGroup = [];
            var operateTypePerson = [];
            var operateTypeStrGroup = '';
            var operateTypeStrPerson = '';
            if (group!=undefined){
                for (var i = 0; i < group.length; i++) {
                    groupPower.push(group[i].id);
                    authorTypeGroup.push(group[i].type);
                    operateTypeGroup.push(group[i].operateType);
                }
                var groupStrPower = groupPower.join(",")
                authorTypeStrGroup = authorTypeGroup.join(",");
                operateTypeStrGroup = operateTypeGroup.join(",");
            }
            if (person!=undefined){
                for (var i = 0; i < person.length; i++) {
                    personPower.push(person[i].id);
                    personOrganPower.push(person[i].organId);
                    authorTypePerson.push(person[i].type);
                    operateTypePerson.push(person[i].operateType)
                }
                personStrPower = personPower.join(",");
                personOrganStrPower = personOrganPower.join(",");
                authorTypeStrPerson = authorTypePerson.join(",");
                operateTypeStrPerson = operateTypePerson.join(",");
            }
            var categoryName = parent.chooseFolderName.join(",");
            var folderId = parent.chooseFolder.join(",");
            var visible = '';
            if(groupId.length==0&&personId.length==0){
                visible='0';
            }
            else{
                visible='1';
            }
            /** 调用权限的更新方法 */
            var operation = function(){
                $.ajax({
                    type: "POST",
                    url: "/fsFolder/editAuthority",
                    data : {
                        visibleRange:visible,
                        folderName:categoryName,
                        group:groupStrPower,
                        person:personStrPower,
                        personOrgan:personOrganStrPower,
                        folderId:folderId,
                        authorTypeStrGroup:authorTypeStrGroup,
                        authorTypeStrPerson:authorTypeStrPerson,
                        operateTypeStrGroup:operateTypeStrGroup,
                        operateTypeStrPerson:operateTypeStrPerson
                    },
                    contentType:"application/x-www-form-urlencoded",
                    dataType:"json",
                    async: false,
                    success:function(result) {
                        if(result){
                            parent.layer.msg("授权成功", {icon: 1});
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                            parent.editFlag = false;
                            parent.refreshFolder(parent.openFolderId);
                            parent.refreshTree();
                        }else{
                            parent.layer.msg("授权失败", {icon: 0});
                        }
                    }
                });
            }
            layer.confirm('确定要重新授权？', operation);

        });
        /*权限说明  鼠标滑过事件*/

        /*群组，人员tab的切换监听*/
        element.on('tab(navigate)', function(data) {
            if(this.innerHTML == "人员设置"){
                $("#searchGroupBtn").hide();
                $("#groupSearch").hide();
                $("#personSearch").show();
                $("#searchPersonBtn").show();
            }else {
                $("#searchGroupBtn").show();
                $("#groupSearch").show();
                $("#personSearch").hide();
                $("#searchPersonBtn").hide();
            }
        });

        //信息悬浮框显示
        $(".layui-form-label .green").hover(function () {
            $(".popWin").fadeIn(200);
        },function () {
            $(".popWin").hide();
        })
    });
    form.on('checkbox(allUser)', function(obj){
        var check = $("input[name='allUser']:checked").val();
        var oldData =  table.cache["rit_table"];
        if (check != undefined && check == 'on') {
            var data1={"id":"allpersonflag","name":"全体人员","type":"3","operateType":0};
            oldData.push(data1);     //右侧表格中添加数据
            openPersonId.push("allpersonflag");
        } else {
            for(var i = 0;i<oldData.length;i++){   //删掉表格中对应的数据
                if(oldData[i].id == "allpersonflag"){
                    oldData = oldData.del(i);
                    break;
                }
            }
        }
        choose = oldData;           //选中的数据存到全局变量中
        table.reload('rit_table',{   //重新加载表格
            data : oldData
        });
    });
    //监听是否继承上级目录权限
    form.on('switch(parentAuthority)', function (obj) {
        var parentFolderId = parent.openFolderId;
        var checkParent = this.checked;
        $.ajax({
            type:"post",
            url:"/fsFolder/getAuthority",
            data:{
                folderId:parentFolderId,
            },
            async:false,
            cache:false,
            dataType:"json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success:function(data){
                var oldData =  table.cache["rit_table"];
                if(checkParent){
                    for(var i = 0;i<data.length;i++){
                        //1、清除表格缓存中已存在的上级目录的权限
                        for(var j = 0;j<oldData.length;j++){
                            if(oldData[j].id == data[i].authorId){
                                oldData = oldData.del(j);
                                break;
                            }
                        }

                        for(var j = 0;j<openGroupId.length;j++){
                            var id = "#"+data[i].authorId + '_anchor';
                            if(openGroupId[j] == id){
                                openGroupId = openGroupId.del(j);
                                break;
                            }
                        }
                        for(var j = 0;j<openPersonId.length;j++){     //删掉记录的处于选中状态的节点
                            var id = "#"+data[i].organId + '_anchor';
                            if(openPersonId[j] == id){
                                openPersonId = openPersonId.del(j);
                                break
                            }
                        }
                        //1、将上级目录的权限加到表格缓存中
                        if(data[i].authorType == "0"){
                            var datajson={"id":data[i].authorId,"name":data[i].authorId,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                            oldData.push(datajson);     //右侧表格中添加数据
                            openPersonId.push("#" + data[i].organId + '_anchor');
                        }else if(data[i].authorType == "1"){
                            var datajson={"id":data[i].authorId,"name":data[i].groupName,"type":data[i].authorType,"operateType":data[i].operateType};
                            oldData.push(datajson);
                            openGroupId.push("#" + data[i].authorId + '_anchor');
                        }else if(data[i].authorType == "2"){
                            var datajson={"id":data[i].authorId,"name":data[i].authorId,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                            oldData.push(datajson);     //右侧表格中添加数据
                            openPersonId.push("#" +data[i].organId + '_anchor');
                        }else{
                            $("#allUser").prop('checked', true);
                            var datajson={"id":data[i].authorId,"name":"全体人员","type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                            oldData.push(datajson);     //右侧表格中添加数据
                            openPersonId.push("#" + data[i].authorId + '_anchor');
                        }
                        if(data[i].authorType == 1){
                            if($(id).parent().children("span.treeSpanActive").length == 0){
                                $("#" + data[i].authorId + '_anchor').parent().append("<span class='treeSpanActive'></span>")
                            }
                        }else{
                            if($(id).parent().children("span.treeSpanActive").length== 0) {
                                $("#" + data[i].organId + '_anchor').parent().append("<span class='treeSpanActive'></span>")
                            }
                        }

                    }
                }else{
                    //删除表格缓存中存在的上级目录权限
                    for(var i = 0;i<data.length;i++){
                        for(var j = 0;j<oldData.length;j++){
                            if(oldData[j].id == data[i].authorId){
                                oldData = oldData.del(j);
                                break;
                            }
                        }
                        if(data[i].authorType == '3'){
                            $("#allUser").prop('checked', false);
                        }
                        for(var j = 0;j<openGroupId.length;j++){
                            var id = "#"+data[i].authorId + '_anchor';
                            $(id).parent().children("span.treeSpanActive").remove();
                            if(openGroupId[j] == id){
                                openGroupId = openGroupId.del(j);
                                break;
                            }
                        }
                        for(var j = 0;j<openPersonId.length;j++){     //删掉记录的处于选中状态的节点
                            var id = "#"+data[i].organId + '_anchor';
                            $(id).parent().children("span.treeSpanActive").remove();
                            if(openPersonId[j] == id){
                                openPersonId = openPersonId.del(j);
                                break
                            }
                        }
                    }
                }
                choose = oldData;           //选中的数据存到全局变量中
                table.reload('rit_table',{   //重新加载表格
                    data : oldData
                });
            }
        })
    });
    //初始化左侧树
    function initLeftTree() {
        getRenData();
        getGroupData();
    }
    /*初始化的选中列表（区分是新增的还是编辑的）*/
    function initRitTable() {
        initAuthorityData();
        //判断修改还是新增
        personInit = personId;
        groupInit = groupId;
        if((personInit!=undefined&&personInit.length>0)||(groupInit!=undefined&&groupInit.length>0)){
            initChooseTable([]);
            table.reload('rit_table',{
                data : personInit.concat(groupInit)
            });
            choose = personInit.concat(groupInit);
        }else {
            initChooseTable([]);
        }
    }

    // 获取人员树数据
    function getRenData() {
        var ajax = new $ax(Hussar.ctxPath + "/orgTreeDemo/employeeTree",function(result) {
            personTreeData = result;
        }, function(data) {
            Hussar.error("获取人员失败");
        });
        ajax.set("treeType", 2);
        ajax.start();
        initEmployeeTree(personTreeData);
    }
    // 获取群组数树数据
    function getGroupData() {
        var ajax = new $ax(Hussar.ctxPath + "/group/sortAndGroupTree",function(result) {
            var arrays = [];
            for(var i=0; i<result.length; i++){
                var type = '';
                if(result[i].CODE == 'GROUP' &&  result[i].PARENT == '#'){
                    type = 'isRoot';
                }else{
                    type = result[i].CODE;
                }
                var arr = {
                    id	:	result[i].ID,
                    code:   result[i].CODE,
                    text : result[i].TEXT,
                    parent : result[i].PARENT,
                    type:type
                }
                arrays.push(arr);
            }
            groupTreeData = arrays;
        }, function(data) {
            Hussar.error("获取人员失败");
        });
        ajax.set("treeType", 2);
        ajax.start();
        initGroupTree(groupTreeData);
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
                "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/station.png"},
                "4":{'icon' : Hussar.ctxPath+"/static/resources/img/fsfile/group-blue.png"},
                "5":{'icon' : Hussar.ctxPath+"/static/resources/img/fsfile/group-blue.png"},
                "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/user.png"},
            },
            search:treeSearch("renTree","personSearch")
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            var type;
            if(e.node.original.struType != 9){     //点击的不是人员时提示
                type = 2;
            }else{
                type = 0;
            };
            var oldData =  table.cache["rit_table"];   //获取右侧表格的数据（如果要改成分页的表格，这样取出来的只是第一页。可以改为从choose全局变量里取）
            var id = "#"+e.node.a_attr.id;             //点击的节点的元素
            if(e.node.original.text == '系统用户' || e.node.original.text == '金现代公司' || e.node.original.text == '第一事业部群' || e.node.original.text == '第二事业部群'
                || e.node.original.text == '第三事业部群'|| e.node.original.text == '第四事业部'   || e.node.original.text == '第五事业部群'|| e.node.original.text == '第六事业部'){
                layer.msg("请选择部门或人员", {anim: 6, icon: 0});
                return;
            }
            if($(id).parent().children("span.treeSpanActive").length>0){   //点击的节点后面有没有对号，判断点击节点的选中状态
                $(id).parent().children("span.treeSpanActive").remove();      //后面有对号的删除对号
                var id = e.node.original.id;
                for(var i = 0;i<oldData.length;i++){   //删掉表格中对应的数据
                    if(oldData[i].organId == id){
                        oldData = oldData.del(i);
                        break;
                    }
                }
                var id = "#"+e.node.a_attr.id;
                for(var i = 0;i<openPersonId.length;i++){     //删掉记录的处于选中状态的节点
                    if(openPersonId[i] == id){
                        openPersonId = openPersonId.del(i);
                        break
                    }
                }
            }else {
                $(id).parent().append("<span class='treeSpanActive'></span>")     //没有对号的加上对号
                var data1={"id":e.node.original.text,"name":e.node.original.text,"type":type,"organId":e.node.original.id,"operateType":0};
                oldData.push(data1);     //右侧表格中添加数据
                openPersonId.push(id);   //保存一下处于选中状态的数据
            }
            choose = oldData;           //选中的数据存到全局变量中
            table.reload('rit_table',{   //重新加载表格
                data : oldData
            });
        })
        $tree.bind("open_node.jstree", function (e,data) {
            for(var i = 0;i<openPersonId.length;i++){
                var id = openPersonId[i];
                //$(id).next().remove();
                $(id).parent().append("<span class='treeSpanActive'></span>")
            }
        });
        $tree.bind("ready.jstree", function (e,data) {
            var id = ".one #11_anchor";
            $(id).hide();
            $(id).prev().hide();
            personInit = personId.concat(groupId);
            for(var i = 0;i<personInit.length;i++){
                var id = "#"+personInit[i].organId+"_anchor"
                if(personInit[i].type != 1){
                    $(id).parent().append("<span class='treeSpanActive'></span>")
                    openPersonId.push(id);
                }
            }
        });
        personInit = personId;
        for(var i = 0;i<personInit.length;i++){
            openPersonId.push("#"+personInit[i].organId+"_anchor");
        }
    }

    // 加载群组树
    function initGroupTree(data){
        var $tree = $("#qzTree");
        $tree.jstree({
            core: {
                data: data
            },
            plugins: ['types','search'],
            types: {
                "GROUP": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/roleGroup.png"},
                "ROLE": {'icon': Hussar.ctxPath + "/static/resources/img/fsfile/group-blue.png"},
                "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/root.png"},
            },
            search:treeSearch("qzTree","groupSearch")
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            if(e.node.original.type == 'isRoot' || e.node.original.code == 'GROUP'){
                layer.msg("请选择群组",{anim:6,icon:0,offset:scrollHeightAlert})
                return;
            }else{
                var oldData =  table.cache["rit_table"];
                var id = "#"+e.node.a_attr.id;
                if($(id).parent().children("span.treeSpanActive").length>0){
                    $(id).parent().children("span.treeSpanActive").remove();
                    var id = e.node.original.id;
                    for(var i = 0;i<oldData.length;i++){
                        if(oldData[i].id == id){
                            oldData = oldData.del(i);
                            break;
                        }
                    }
                    var id = "#"+e.node.a_attr.id;
                    for(var i = 0;i<openGroupId.length;i++){
                        if(openGroupId[i] == id){
                            openGroupId = openGroupId.del(i);
                            break;
                        }
                    }
                }else {
                    $(id).parent().append("<span class='treeSpanActive'></span>")
                    var data1={"id":e.node.original.id,"name":e.node.original.text,"type":1,"operateType":0};
                    oldData.push(data1);
                    openGroupId.push(id);
                }
                //choose = oldData;
                choose = oldData;
                table.reload('rit_table',{
                    data : oldData
                });
            }
        })
        $tree.bind("ready.jstree", function (e,data) {
            groupInit = groupId;
            for(var i = 0;i<groupInit.length;i++){
                var id = "#"+groupInit[i].id+"_anchor"
                if(groupInit[i].type == 1){
                    $(id).parent().append("<span class='treeSpanActive'></span>")
                    openGroupId.push(id);
                }
            }
        });
        $tree.bind("open_node.jstree", function (e,data) {
            for(var i = 0;i<openGroupId.length;i++){
                var id = openGroupId[i];
                $(id).next().remove();
                $(id).parent().append("<span class='treeSpanActive'></span>")
            }
        });
    }
    // 初始化右侧选中表格
    function initChooseTable(data) {
        table.render({
            elem: '#rit_table'
            ,height: $(".rit-bot").height()//容器高度
            ,cols: [[ //标题栏
                { type: 'checkbox', width: '5%', align: "center"},
                {
                    field: 'name',
                    title: '所选名称',
                    width: '57%',
                    align: "center",
                    toolbar: '#name'
                },
                {field:'operateType',title:'权限',width:'20%',sort:'true',event: 'operateType',templet:function(d){
                    var selectHtml = '<select name="operateType" lay-filter="operateType" >';
                    if(d.operateType == 0){
                        selectHtml = selectHtml + '<option value="0" selected>查看</option>' +
                            '<option value="1">上传</option>' +
                            '<option value="2" >管理</option>' +
                            '</select>';
                    }else if(d.operateType == 1){
                        selectHtml = selectHtml + '<option value="0">查看</option>' +
                            '<option value="1" selected>上传</option>' +
                            '<option value="2" >管理</option>' +
                            '</select>';
                    }else{
                        selectHtml = selectHtml + '<option value="0">查看</option>' +
                            '<option value="1">上传</option>' +
                            '<option value="2" selected>管理</option>' +
                            '</select>';
                    }
                    return selectHtml;
                },align:'center'},
                { title: '操作', width: '18%', align: "center", toolbar: '#barDemo'},
            ]]
            ,data:data
            //,skin: 'line' //表格风格
            ,even: true
            ,page: false //是否显示分页
            //,limits: [5, 7, 10]
            ,limit: 1000 //每页默认显示的数量,
            ,done : function(res,curr,count){
                layui.each($('select'),function(index,item){
                    var elem = $(item);
                    /* 下拉框选中事件 */
                    form.on('select(operateType)', function(data){
                        elem.val(data.value);
                        for(var i = 0;i<choose.length;i++){   //删掉表格中对应的数据
                            if(choose[i].id == operateTypeValue.data.id){
                                choose[i].operateType = parseInt(data.value);
                                break;
                            }
                        }
                        table.reload('rit_table',{
                            data : choose
                        });
                    });
                });
                form.render();
            }
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'del'){
                var oldData =  table.cache["rit_table"];
                if(data.type == 0 || data.type == 2){       //如果删除的是人员
                    var id = ".one #"+data.organId+"_anchor"
                    $(id).parent().children("span.treeSpanActive").remove();   //删掉树上的对号
                    var idOpen =  "#"+data.organId+"_anchor";
                    for(var i = 0;i<openPersonId.length;i++){
                        if(openPersonId[i] == idOpen){
                            openPersonId = openPersonId.del(i);
                            break;
                        }
                    }
                }else {      //如果删除的是群组
                    var id = ".two #"+data.id+"_anchor";
                    $(id).parent().children("span.treeSpanActive").remove();
                    var idOpen =  "#"+data.id+"_anchor";
                    for(var i = 0;i<openGroupId.length;i++){
                        if(openGroupId[i] == idOpen){
                            openGroupId = openGroupId.del(i);
                            break;
                        }
                    }
                }
                for(var j = 0;j<oldData.length;j++){     //删掉表格里的数据
                    //如果删除的为全体人员将全选人员得复选框置空
                   
                    if(oldData[j].id == data.id){
                        if(oldData[j].type == 3){
                            $("#allUser").prop('checked',false);
                        }
                        oldData = oldData.del(j);
                        break;
                    }
                }
                table.reload('rit_table',{
                    data : oldData
                });
                choose = oldData;
            }
            if (obj.event == 'operateType') {
                operateTypeValue = obj;
            }
        });
    }


    //$(window).resize(function () {
    //    initRitTable();
    //});
    /**
     * 群组人员树的模糊查询
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

    /** 初始化数据 */
    function initAuthorityData(){
        if(parent.chooseFolder.length == 1){
            var folderId = parent.chooseFolder[0];
            $.ajax({
                type:"post",
                url:"/fsFolder/getAuthority",
                data:{
                    folderId:folderId,
                },
                async:false,
                cache:false,
                dataType:"json",
                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                success:function(data){
                    for(var i = 0;i<data.length;i++){
                        if(data[i].authorType == "0"){
                            var datajson={"id":data[i].authorId,"name":data[i].authorId,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                            personParamPower.push(datajson);
                        }else if(data[i].authorType == "1"){
                            var datajson={"id":data[i].authorId,"name":data[i].groupName,"type":data[i].authorType,"operateType":data[i].operateType};
                            groupParamPower.push(datajson)
                        }else if(data[i].authorType == "2"){
                            var datajson={"id":data[i].authorId,"name":data[i].authorId,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                            groupParamPower.push(datajson);
                        }else{
                            var datajson={"id":data[i].authorId,"name":"全体人员","type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                            groupParamPower.push(datajson);
                        }
                    }
                    personId = personParamPower;
                    groupId = groupParamPower;
                }
            });
        }
    }

    /** 加载时判断是否有全体人员 */
    function checkAllUser(){
        groupInit = groupId;
        for(var i = 0;i<groupInit.length;i++){
            if(groupInit[i].id == "allpersonflag"){
                $('#allUser').attr('checked', true);
                break;
            }
        }
    }
});


/*数组删除某一项调用的方法*/
Array.prototype.del=function(n) {
    if(n<0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0,n).concat(this.slice(n+1,this.length));
}