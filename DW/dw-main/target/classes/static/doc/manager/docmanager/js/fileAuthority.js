var roleTreeData = [];
var personTreeData = [];
var groupTreeData = [];
var choose = [];
var roleInit = [];
var personInit = [];
var groupInit = [];
var editFlag;
var openRoleId = [];
var openPersonId = [];
var openGroupId = [];
var operateTypeValue;
var roleParamPower = [];
var personParamPower = [];
var groupParamPower = [];
var roleId = [];
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
        initRitTable()    //初始化右侧表格
        checkAllUser()
        form.render();
        /*取消按钮*/
        $("#cancelBtn").on('click',function(){
            var index = parent.layer.getFrameIndex(window.name);

            parent.layer.close(index);
            parent.refreshFile(parent.openFileId);
        });
        /*批量删除*/
        $("#delBtn").on('click',function(){
            var checkStatus = table.checkStatus('rit_table')
            checkStatus = checkStatus.data;
            if(checkStatus.length==0){
                layer.msg("请选择要删除的记录", {anim:6,icon: 0});
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
            var role = [];
            var group = [];
            var person = [];
            for(var i = 0;i<checkStatus.length;i++){
                if(checkStatus[i].type == "2"){
                    person.push("#"+checkStatus[i].id+"_anchor");
                }else {
                    if(checkStatus[i].type == "0"){
                        person.push("#"+checkStatus[i].id+"_anchor")
                    }else if(checkStatus[i].type == "1" || checkStatus[i].type == "3"){
                        group.push("#"+checkStatus[i].id+"_anchor")
                    }else {
                        role.push("#"+checkStatus[i].id+"_anchor")
                    }

                }
            }
            for(var i = 0;i<role.length;i++){
                $(role[i]).parent().children("span.treeSpanActive").remove();   //删掉角色树上对应的对号
                for(var j = 0;j<openRoleId.length;j++){
                    if(openRoleId[j] == role[i]){
                        openRoleId = openRoleId.del(j);
                        break;
                    }
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
        /** 加载时判断是否有全体人员 */
        function checkAllUser() {;
            for (var i = 0; i < groupId.length; i++) {
                if (groupId[i].id == "allpersonflag") {
                    $('#allUser').attr('checked', true);
                    break;
                }
            }
        }
        /*保存按钮*/
        $("#saveBtn").on('click',function(){
            var role = [];
            var group = [];
            var person = [];
            for(var i = 0;i<choose.length;i++){
                if(choose[i].type == "0"||choose[i].type == "2"){
                    person.push(choose[i]);
                }else  if(choose[i].type == "1" || choose[i].type == "3"){
                    group.push(choose[i])
                }else {
                    role.push(choose[i])
                }
            }
            var roleStrPower = '';
            var groupStrPower = '';
            var personStrPower ='';
            var personOrganStrPower ='';
            var rolePower = [];
            var groupPower = [];
            var personPower = [];
            var personOrganPower = [];
            var authorTypeRole = [];
            var authorTypeGroup = [];
            var authorTypePerson = [];
            var authorTypeStrRole = '';
            var authorTypeStrGroup = '';
            var authorTypeStrPerson = '';
            var operateTypeRole = [];
            var operateTypeGroup = [];
            var operateTypePerson = [];
            var operateTypeStrRole = '';
            var operateTypeStrGroup = '';
            var operateTypeStrPerson = '';
            var flag=0;
            if (role!=undefined){
                for (var i = 0; i < role.length; i++) {
                    rolePower.push(role[i].id);
                    authorTypeRole.push(role[i].type);
                    operateTypeRole.push(role[i].operateType);
                }
                roleStrPower = rolePower.join(",")
                authorTypeStrRole = authorTypeRole.join(",");
                operateTypeStrRole = operateTypeRole.join(",");
            }
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
            for(var i=0;i<parent.chooseFile.length;i++){
                var categoryName = parent.chooseFileName[i];
                var fileId = parent.chooseFile[i];
                var authorId = parent.chooseFileAuthor[i];


                var visible = '';
                if(roleId.length==0&&groupId.length==0&&personId.length==0){
                    visible='0';
                }
                else{
                    visible='1';
                }

                /** 调用权限的更新方法 */
                /*$.ajax({
                    type: "POST",
                    url: Hussar.ctxPath+"/fsFile/editAuthority",
                    data : {
                        visibleRange:visible,
                        folderName:categoryName,
                        group:groupStrPower,
                        person:personStrPower,
                        personOrgan:personOrganStrPower,
                        fileId:fileId,
                        authorId:authorId,
                        authorTypeGroup:authorTypeStrGroup,
                        authorTypePerson:authorTypeStrPerson,
                        operateTypeGroup:operateTypeStrGroup,
                        operateTypePerson:operateTypeStrPerson
                    },
                    contentType:"application/x-www-form-urlencoded",
                    dataType:"json",
                    async: false,
                    success:function(result) {
                        if(result){
                            flag += 1;
                        }else{
                            flag=0;

                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/fsFile/editAuthority", function(result) {
                    if(result){
                        flag += 1;
                    }else{
                        flag=0;

                    }
                }, function(data) {

                });
                ajax.set("visibleRange",visible);
                ajax.set("folderName",categoryName);
                ajax.set("role",roleStrPower);
                ajax.set("group",groupStrPower);
                ajax.set("person",personStrPower);
                ajax.set("personOrgan",personOrganStrPower);
                ajax.set("fileId",fileId);
                ajax.set("authorId",authorId);
                ajax.set("authorTypeRole",authorTypeStrRole);
                ajax.set("authorTypeGroup",authorTypeStrGroup);
                ajax.set("authorTypePerson",authorTypeStrPerson);
                ajax.set("operateTypeRole",operateTypeStrRole);
                ajax.set("operateTypeGroup",operateTypeStrGroup);
                ajax.set("operateTypePerson",operateTypeStrPerson);
                ajax.set("authType",$("#authType").val());
                ajax.start();
            }
            if(flag==parent.chooseFile.length){
                parent.layer.msg("授权成功", {icon: 1,offset:parent.scrollHeightMsg});
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
                parent.editFlag = false;
                //parent.refreshFile(parent.openFileId);
                //parent.refreshTree();
            }else {
                parent.layer.msg("授权失败", {icon: 0});
            }
        });


        /*群组，人员tab的切换监听*/
        element.on('tab(navigate)', function(data) {
            if(this.innerHTML == "组织架构"){
                $("#roleSearch").hide();
                $("#searchGroupBtn").hide();
                $("#groupSearch").hide();
                $("#personSearch").show();
                $("#searchPersonBtn").show();
            }else if(this.innerHTML == "群组"){
                $("#roleSearch").hide();
                $("#searchGroupBtn").show();
                $("#groupSearch").show();
                $("#personSearch").hide();
                $("#searchPersonBtn").hide();
            }else {
                $("#roleSearch").show();
                $("#searchGroupBtn").hide();
                $("#groupSearch").hide();
                $("#personSearch").hide();
                $("#searchPersonBtn").hide();
            }
        });
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
        var parentFolderId = parent.openFileId;
        var checkParent = this.checked;
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/fsFolder/getAuthority",
            data:{
                folderId:parentFolderId,
            },
            async:false,
            cache:false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType:"json",
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
                        var id = "#"+data[i].authorId;
                        for(var j = 0;j<openGroupId.length;j++){
                            if(openGroupId[j] == id){
                                openGroupId = openGroupId.del(j);
                                break;
                            }
                        }
                        for(var j = 0;j<openPersonId.length;j++){     //删掉记录的处于选中状态的节点
                            if(openPersonId[j] == id){
                                openPersonId = openPersonId.del(j);
                                break
                            }
                        }
                        //1、将上级目录的权限加到表格缓存中
                        if(data[i].authorType == "0"){
                            var datajson={"id":data[i].authorId,"name":data[i].authorId,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                            oldData.push(datajson);     //右侧表格中添加数据
                            openPersonId.push("#" + data[i].authorId);
                        }else if(data[i].authorType == "1"){
                            var datajson={"id":data[i].authorId,"name":data[i].groupName,"type":data[i].authorType,"operateType":data[i].operateType};
                            oldData.push(datajson);
                            openGroupId.push("#" + data[i].authorId);
                        }else if(data[i].authorType == "2"){
                            var datajson={"id":data[i].organId,"name":data[i].authorId,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                            oldData.push(datajson);     //右侧表格中添加数据
                            openPersonId.push("#" +data[i].authorId);
                        }else{
                            var datajson={"id":data[i].authorId,"name":"全体人员","type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                            oldData.push(datajson);     //右侧表格中添加数据
                            openPersonId.push("#" + data[i].authorId);
                        }
                        $("#" + data[i].authorId).parent().append("<span class='treeSpanActive'></span>")
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
                        var id = "#"+data[i].authorId;
                        for(var j = 0;j<openGroupId.length;j++){
                            if(openGroupId[j] == id){
                                openGroupId = openGroupId.del(j);
                                break;
                            }
                        }
                        for(var j = 0;j<openPersonId.length;j++){     //删掉记录的处于选中状态的节点
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
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFolder/getAuthority", function(data) {
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
                    var id = "#"+data[i].authorId;
                    for(var j = 0;j<openRoleId.length;j++){
                        if(openRoleId[j] == id){
                            openRoleId = openRoleId.del(j);
                            break;
                        }
                    }
                    for(var j = 0;j<openGroupId.length;j++){
                        if(openGroupId[j] == id){
                            openGroupId = openGroupId.del(j);
                            break;
                        }
                    }
                    for(var j = 0;j<openPersonId.length;j++){     //删掉记录的处于选中状态的节点
                        if(openPersonId[j] == id){
                            openPersonId = openPersonId.del(j);
                            break
                        }
                    }
                    //1、将上级目录的权限加到表格缓存中
                    if(data[i].authorType == "0"){
                        var datajson={"id":data[i].authorId,"name":data[i].authorId,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                        oldData.push(datajson);     //右侧表格中添加数据
                        openPersonId.push("#" + data[i].authorId);
                    }else if(data[i].authorType == "1"){
                        var datajson={"id":data[i].authorId,"name":data[i].groupName,"type":data[i].authorType,"operateType":data[i].operateType};
                        oldData.push(datajson);
                        openGroupId.push("#" + data[i].authorId);
                    }else if(data[i].authorType == "2"){
                        var datajson={"id":data[i].organId,"name":data[i].authorId,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                        oldData.push(datajson);     //右侧表格中添加数据
                        openPersonId.push("#" +data[i].authorId);
                    }else if(data[i].authorType == "3"){
                        var datajson={"id":data[i].authorId,"name":"全体人员","type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                        oldData.push(datajson);     //右侧表格中添加数据
                        openPersonId.push("#" + data[i].authorId);
                    }else {
                        var datajson={"id":data[i].authorId,"name":data[i].roleName,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].operateType};
                        oldData.push(datajson);     //右侧表格中添加数据
                        openRoleId.push("#" +data[i].authorId);
                    }
                    $("#" + data[i].authorId).parent().append("<span class='treeSpanActive'></span>")
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
                    var id = "#"+data[i].authorId;
                    for(var j = 0;j<openRoleId.length;j++){
                        if(openRoleId[j] == id){
                            openRoleId = openRoleId.del(j);
                            break;
                        }
                    }
                    for(var j = 0;j<openGroupId.length;j++){
                        if(openGroupId[j] == id){
                            openGroupId = openGroupId.del(j);
                            break;
                        }
                    }
                    for(var j = 0;j<openPersonId.length;j++){     //删掉记录的处于选中状态的节点
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
        }, function(data) {

        });
        ajax.set("folderId",parentFolderId);
        ajax.start();
    });
    //初始化左侧树
    function initLeftTree() {
        //getRoleData();
        getRenData();
        getGroupData();
    }
    /*初始化的选中列表（区分是新增的还是编辑的）*/
    function initRitTable() {
        initAuthorityData();
        //判断修改还是新增
        roleInit = roleId;
        personInit = personId;
        groupInit = groupId;
        if((roleInit!=undefined&&roleInit.length>0)||(personInit!=undefined&&personInit.length>0)||(groupInit!=undefined&&groupInit.length>0)){
            initChooseTable([]);
            table.reload('rit_table',{
                data : roleInit.concat(groupInit.concat(personInit))
            });
            choose = roleInit.concat(groupInit.concat(personInit));
        }else {
            initChooseTable([]);
        }
    }

    // 获取角色树数据
    function getRoleData() {
        var ajax = new $ax(Hussar.ctxPath + "/roleManager/roleTree",function(result) {
            roleTreeData = result;
        }, function(data) {
            Hussar.error("获取角色失败");
        });
        ajax.start();
        initRoleTree(roleTreeData);
    }
    // 获取人员树数据
    function getRenData() {
        var ajax = new $ax(Hussar.ctxPath + "/orgTreeDemo/usersTree",function(result) {
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
        var ajax = new $ax(Hussar.ctxPath + "/group/getGroupAndPergroupTree",function(result) {
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
    // 加载角色树
    function initRoleTree(data){
        var $tree = $("#roleTree");
        $tree.jstree({
            core: {
                data: data
            },
            plugins: ['types','search'],
            types: {
                "GROUP":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/roleGroup.png"},
                "ROLE":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/role.png"},
                "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/root.png"}
            },
            search:treeSearch("roleTree","roleSearch")
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            var ids;
            var type;
            if(e.node.original.type == 'isRoot' || e.node.original.type == 'GROUP'){
                layer.msg("请选择角色", {anim: 6, icon: 0});
                return;
            }
            if(e.node.original.id == 'superadmin_role' || e.node.original.id == '03b4cc9be3614ff4b5374e4d142f6bce'){
                layer.msg("超级管理员及知识库管理员无需分配权限", {anim: 6, icon: 0});
                return;
            }
            type = 4;
            ids=e.node.original.id;
            var oldData =  table.cache["rit_table"];   //获取右侧表格的数据（如果要改成分页的表格，这样取出来的只是第一页。可以改为从choose全局变量里取）
            var id = "#"+e.node.a_attr.id;             //点击的节点的元素
            if($(id).parent().children("span.treeSpanActive").length>0){   //点击的节点后面有没有对号，判断点击节点的选中状态
                $(id).parent().children("span.treeSpanActive").remove();      //后面有对号的删除对号
                var id = e.node.original.id;
                var oId= e.node.original.organId;
                for(var i = 0;i<oldData.length;i++){   //删掉表格中对应的数据
                    if(oldData[i].id == id){
                        oldData = oldData.del(i);
                        break;
                    }
                }
                var id = "#"+e.node.a_attr.id;
                for(var i = 0;i<openRoleId.length;i++){     //删掉记录的处于选中状态的节点
                    if(openRoleId[i] == id){
                        openRoleId = openRoleId.del(i);

                        break
                    }
                }
            }else {
                $(id).parent().append("<span class='treeSpanActive'></span>")     //没有对号的加上对号
                var data1={"id":ids,"name":e.node.original.text,"type":type,"organId":e.node.original.organId,"operateType":0};
                oldData.push(data1);     //右侧表格中添加数据
                openRoleId.push(id);   //保存一下处于选中状态的数据
            }
            choose = oldData;           //选中的数据存到全局变量中
            table.reload('rit_table',{   //重新加载表格
                data : oldData
            });
        })
        $tree.bind("open_node.jstree", function (e,data) {
            for(var i = 0;i<openRoleId.length;i++){
                var id = openRoleId[i];
                //$(id).next().remove();
                $(id).parent().append("<span class='treeSpanActive'></span>")
            }
        });
        $tree.bind("ready.jstree", function (e,data) {
            roleInit = roleId;
            for(var i = 0;i<roleInit.length;i++){
                var id = "#"+roleInit[i].id+"_anchor"
                if(roleInit[i].type == 4){
                    $(id).parent().append("<span class='treeSpanActive'></span>")
                    openRoleId.push(id);
                }
            }
        });
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
                "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/root.png"},
                "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/station.png"},
                "4":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/deptOld.png"},
                "5":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/deptOld.png"},
                "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/user.png"},
            },
            search:treeSearch("renTree","personSearch")
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            var type;
            var ids;
            if(e.node.original.type == '1'){
                layer.msg("请选择部门或人员", {anim: 6, icon: 0});
                return;
            }
            if(e.node.original.type !="USER"){     //点击的不是人员时提示
                type = 2;
                ids=e.node.original.id;
            }else{
                type = 0;
                ids=e.node.original.id;
            }
            var oldData =  table.cache["rit_table"];   //获取右侧表格的数据（如果要改成分页的表格，这样取出来的只是第一页。可以改为从choose全局变量里取）
            var id = "#"+e.node.a_attr.id;             //点击的节点的元素
            if(e.node.original.text == '金现代公司' || e.node.original.text == '第一事业部群' || e.node.original.text == '第二事业部群' || e.node.original.text == '第三事业部群'|| e.node.original.text == '第四事业部'){
                layer.msg("请选择部门或人员", {anim: 6, icon: 0});
                return;
            }
            if($(id).parent().children("span.treeSpanActive").length>0){   //点击的节点后面有没有对号，判断点击节点的选中状态
                $(id).parent().children("span.treeSpanActive").remove();      //后面有对号的删除对号
                var id = e.node.original.id;
                var oId= e.node.original.organId;
                for(var i = 0;i<oldData.length;i++){   //删掉表格中对应的数据
                    if(oldData[i].id == id){
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
                var data1={"id":ids,"name":e.node.original.text,"type":type,"organId":e.node.original.organId,"operateType":0};
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
                // $(id).next().remove();
                $(id).parent().append("<span class='treeSpanActive'></span>")
            }
        });
        $tree.bind("ready.jstree", function (e,data) {
            var id = ".two #11_anchor";
            $(id).hide();
            $(id).prev().hide();
            personInit = personId.concat(groupId);
            for(var i = 0;i<personInit.length;i++){
                var id="";
                if(personInit[i].type == 0){
                    id = "#"+personInit[i].id+"_anchor"
                }else{
                    id = "#"+personInit[i].organId+"_anchor"
                }

                if(personInit[i].type != 1){
                    $(id).parent().append("<span class='treeSpanActive'></span>")
                    openPersonId.push(id);
                }
            }
        });
        personInit =parent.personParam;
        for(var i = 0;i<personInit.length;i++){
            if(personInit[i].type == 0){
                openPersonId.push("#"+personInit[i].id+"_anchor");
            }else{
                openPersonId.push("#"+personInit[i].organId+"_anchor");
            }
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
                "GROUP": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/blue/roleGroup.png"},
                "ROLE": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/blue/deptOld.png"},
                "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/root.png"},
            },
            search:treeSearch("qzTree","groupSearch")
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            if(e.node.original.type == 'isRoot' || e.node.original.code == 'GROUP'){
                layer.msg("请选择群组",{anim:6,icon:0});
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
                { type: 'checkbox', width: '10%', align: "center"},
                {
                    field: 'name',
                    title: '所选名称',
                    width: '45%',
                    align: "center",
                    toolbar: '#name'
                },
                {field:'operateType',title:'权限',width:'27%',sort:'true',event: 'operateType',templet:function(d){
                        var selectHtml = '<select name="operateType" lay-filter="operateType" >';
                        if(d.operateType == 0){
                            selectHtml = selectHtml + '<option value="0" selected>查看</option>' +
                                '<option value="1">下载</option>' +
                                '<option value="2" >管理</option>' +
                                '</select>';
                        }else if(d.operateType == 1){
                            selectHtml = selectHtml + '<option value="0">查看</option>' +
                                '<option value="1" selected>下载</option>' +
                                '<option value="2" >管理</option>' +
                                '</select>';
                        }else{
                            selectHtml = selectHtml + '<option value="0">查看</option>' +
                                '<option value="1">下载</option>' +
                                '<option value="2" selected>管理</option>' +
                                '</select>';
                        }
                        return selectHtml;
                    },align:'center'},
                { title: '操作', width: '18%', align: "center", toolbar: '#barDemo'},
            ]]
            ,data:data
            ,skin: 'nob' //表格风格
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
                if(data.type == 0||data.type == 2){       //如果删除的是人员
                    var id="";
                    var idOpen="";
                    if(data.type == 0 ){
                        id = ".one #"+data.id+"_anchor"
                        idOpen =  "#"+data.id+"_anchor";
                    }else{
                        id = ".one #"+data.id+"_anchor"
                        idOpen =  "#"+data.id+"_anchor";
                    }
                    $(id).parent().children("span.treeSpanActive").remove();   //删掉树上的对
                    for(var i = 0;i<openPersonId.length;i++){
                        if(openPersonId[i] == idOpen){
                            openPersonId = openPersonId.del(i);
                            break;
                        }
                    }
                }else  if(data.type == 1 || data.type == 3){      //如果删除的是群组
                    var id = ".two #"+data.id+"_anchor";
                    $(id).parent().children("span.treeSpanActive").remove();
                    var idOpen =  "#"+data.id+"_anchor";
                    for(var i = 0;i<openGroupId.length;i++){
                        if(openGroupId[i] == idOpen){
                            openGroupId = openGroupId.del(i);
                            break;
                        }
                    }
                }else {      //如果删除的是角色
                    var id = ".three #"+data.id+"_anchor";
                    $(id).parent().children("span.treeSpanActive").remove();
                    var idOpen =  "#"+data.id+"_anchor";
                    for(var i = 0;i<openRoleId.length;i++){
                        if(openRoleId[i] == idOpen){
                            openRoleId = openRoleId.del(i);
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


    // $(window).resize(function () {
    //     initRitTable();
    // });
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
        if(parent.chooseFile.length>1){
            roleId = [];
            personId = [];
            groupId = [];
            return;
        }
        var fileId = parent.chooseFile[0];

        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/fsFile/getAuthority",
            data:{
                fileId:fileId,
            },
            async:false,
            cache:false,
            dataType:"json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success:function(data){
                for(var i = 0;i<data.length;i++){
                    if(data[i].authorType == "0"){
                        var name =data[i].userName;
                        if(name==undefined){
                            name=data[i].authorId;
                        }
                        var datajson={"id":data[i].authorId,"name":name,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].authority};
                        personParamPower.push(datajson);
                    }else if(data[i].authorType == "1"){
                        var datajson={"id":data[i].authorId,"name":data[i].groupName,"type":data[i].authorType,"operateType":data[i].authority};
                        groupParamPower.push(datajson)
                    }else if(data[i].authorType == "2"){
                        var datajson={"id":data[i].organId,"name":data[i].authorId,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].authority};
                        groupParamPower.push(datajson);
                    }else{
                        var datajson={"id":data[i].authorId,"name":"全体人员","type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].authority};
                        groupParamPower.push(datajson);
                    }
                }
                personId = personParamPower;
                groupId = groupParamPower;
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getAuthority", function(data) {
            for(var i = 0;i<data.length;i++){
                if(data[i].authorType == "0"){
                    var name =data[i].userName;
                    if(name==undefined){
                        name=data[i].authorId;
                    }
                    var datajson={"id":data[i].authorId,"name":name,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].authority};
                    personParamPower.push(datajson);
                }else if(data[i].authorType == "1"){
                    var datajson={"id":data[i].authorId,"name":data[i].groupName,"type":data[i].authorType,"operateType":data[i].authority};
                    groupParamPower.push(datajson)
                }else if(data[i].authorType == "2"){
                    var datajson={"id":data[i].organId,"name":data[i].authorId,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].authority};
                    groupParamPower.push(datajson);
                }else if(data[i].authorType == "3"){
                    var datajson={"id":data[i].authorId,"name":"全体人员","type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].authority};
                    groupParamPower.push(datajson);
                }else {
                    var datajson={"id":data[i].authorId,"name":data[i].roleName,"type":data[i].authorType,"organId":data[i].organId,"operateType":data[i].authority};
                    roleParamPower.push(datajson);
                }
            }
            roleId = roleParamPower;
            personId = personParamPower;
            groupId = groupParamPower;
        }, function(data) {

        });
        ajax.set("fileId",fileId);
        ajax.start();
    }
});


/*数组删除某一项调用的方法*/
Array.prototype.del=function(n) {
    if(n<0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0,n).concat(this.slice(n+1,this.length));
}