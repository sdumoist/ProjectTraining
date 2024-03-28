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
        initRitTable();    //初始化右侧表格
        form.render();
        /*取消按钮*/
        $("#cancelBtn").on('click',function(){
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        });
        /*批量删除*/
        $("#delBtn").on('click',function(){
            var checkStatus = table.checkStatus('rit_table');
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
            var person = [];
            for(var i = 0;i<checkStatus.length;i++){
                if(checkStatus[i].type == "0"){
                    person.push("#"+checkStatus[i].id+"_anchor")
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
        });

        /*保存按钮*/
        $("#saveBtn").on('click',function(){
            // 点击保存时，装入已选人员
            var person = [];
            for(var i = 0;i<choose.length;i++){
                if(choose[i].type == "0"){
                    person.push(choose[i]);
                }
            }
            var personId = [];
            var personName = [];
            if (person.length > 0) {
                for (var i = 0; i < person.length; i++) {
                    personId.push(person[i].id);
                    personName.push(person[i].name);
                }
            }
            var personIdStr = personId.join(",");
            var personNameStr = personName.join(",");
            if(!personIdStr){
                parent.layer.msg("请选择审批人", {icon: 0,offset:parent.scrollHeightMsg});
                return;
            }
            parent.auditorIds = personIdStr;
            parent.auditorNames = personNameStr;
            parent.$("#auditorName").val(personNameStr);
            parent.$("#auditorName1").val(personNameStr);
            $("#cancelBtn").click();
        });
    });
    //监听是否继承上级目录权限
    form.on('switch(parentAuthority)', function (obj) {
        var parentFolderId = parent.openFileId;
        var checkParent = this.checked;
        var ajax = new $ax(Hussar.ctxPath + "/fsFolder/getApprovalUser", function(data) {
            var oldData = table.cache["rit_table"];
            if (checkParent) {
                for (var i = 0; i < data.length; i++) {
                    //1、清除表格缓存中已存在的上级目录的权限
                    for (var j = 0; j < oldData.length; j++) {
                        if (oldData[j].id == data[i].auditUserId) {
                            oldData = oldData.del(j);
                            break;
                        }
                    }
                    for (var j = 0; j < openPersonId.length; j++) {     //删掉记录的处于选中状态的节点
                        var id = "#" + data[i].auditUserId + '_anchor';
                        if (openPersonId[j] == id) {
                            openPersonId = openPersonId.del(j);
                            break
                        }
                    }
                    //1、将上级目录的权限加到表格缓存中
                    var name =data[i].auditUserName;
                    if(name==undefined){
                        name=data[i].auditUserId;
                    }
                    var datajson = {
                        "id": data[i].auditUserId,
                        "name":name,
                        "type": 0
                    };
                    oldData.push(datajson);     //右侧表格中添加数据
                    openPersonId.push("#" + data[i].auditUserId + '_anchor');

                    if($(id).parent().children("span.treeSpanActive").length == 0){
                        $("#" + data[i].auditUserId + '_anchor').parent().append("<span class='treeSpanActive'></span>")
                    }

                }
            } else {
                //删除表格缓存中存在的上级目录权限
                for(var i = 0;i<data.length;i++){
                    for(var j = 0;j<oldData.length;j++){
                        if(oldData[j].id == data[i].auditUserId){
                            oldData = oldData.del(j);
                            break;
                        }
                    }
                    for(var j = 0;j<openPersonId.length;j++){     //删掉记录的处于选中状态的节点
                        var id=""
                        id = "#"+data[i].auditUserId + '_anchor';
                        $(id).parent().children("span.treeSpanActive").remove();
                        if(openPersonId[j] == id){
                            openPersonId = openPersonId.del(j);
                            break
                        }
                    }
                }

            }
            choose = oldData;           //选中的数据存到全局变量中
            table.reload('rit_table', {   //重新加载表格
                data: oldData
            });
        }, function(data) {

        });
        ajax.set("folderId",parentFolderId);
        ajax.start();
    });
    //初始化左侧树
    function initLeftTree() {
        getRenData();
    }
    /*初始化的选中列表（区分是新增的还是编辑的）*/
    function initRitTable() {
        initAuthorityData();
        //判断修改还是新增
        personInit = personId;
        if((personInit!=undefined&&personInit.length>0)){
            initChooseTable([]);
            table.reload('rit_table',{
                data : personInit
            });
            choose = personInit;
        }else {
            initChooseTable([]);
        }
    }

    // 获取人员树数据
    function getRenData() {
        var group = parent.groupId;
        var person = parent.personId;
        var idArr = [];
        var typeArr = [];
        var ids = '';
        var types = '';
        if (group != undefined) {
            for (var i = 0; i < group.length; i++) {
                if("2" == group[i].operateType){
                    idArr.push(group[i].id);
                    typeArr.push(group[i].type);
                }
            }
        }
        if (person != undefined) {
            for (var i = 0; i < person.length; i++) {
                if("2" == person[i].operateType){
                    if("2" == person[i].type){
                        idArr.push(person[i].organId);
                        typeArr.push(person[i].type);
                    } else {
                        idArr.push(person[i].id);
                        typeArr.push(person[i].type);
                    }
                }
            }
        }
        ids = idArr.join(",");
        types = typeArr.join(",");
        var ajax = new $ax(Hussar.ctxPath + "/fsFolder/usersTree",function(result) {
            personTreeData = result;
        }, function(data) {
            Hussar.error("获取人员失败");
        });
        ajax.set("folderId",$("#folderId").val());
        ajax.set("ids",ids);
        ajax.set("types",types);
        ajax.start();
        initEmployeeTree(personTreeData);
    }
    // 加载人员树
    function initEmployeeTree(data){
        //此处data为传入的树的data
        var $tree = $("#renTree");
        $tree.jstree({
            core: {
                data: data
            },
            plugins: ['types','search'],
            types:{
                "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/root.png"},
                "1":{'icon' : Hussar.ctxPath+"/static/resources/img/fsfile/authority/qz.png"},
                "2":{'icon' : Hussar.ctxPath+"/static/resources/img/fsfile/authority/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/resources/img/fsfile/authority/qz.png"},
                "10":{'icon' : Hussar.ctxPath+"/static/resources/img/fsfile/authority/qz.png"},
                "USER":{'icon' : Hussar.ctxPath+"/static/resources/img/fsfile/authority/people.png"}
            },
            search:treeSearch("renTree","personSearch")
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            var type;
            var ids;
            if(e.node.original.type != 'USER'){
                layer.msg("请选择人员", {anim: 6, icon: 0});
                return;
            }
            type = 0;
            ids=e.node.original.id;

            var oldData =  table.cache["rit_table"];   //获取右侧表格的数据（如果要改成分页的表格，这样取出来的只是第一页。可以改为从choose全局变量里取）
            var id = "#"+e.node.a_attr.id;             //点击的节点的元素
            if(e.node.original.text == '金现代公司' || e.node.original.text == '第一事业部群' || e.node.original.text == '第二事业部群' || e.node.original.text == '第三事业部群'|| e.node.original.text == '第四事业部群'|| e.node.original.text == '第六事业部群'|| e.node.original.text == '第五事业部群'){
                layer.msg("请选择人员", {anim: 6, icon: 0});
                return;
            }
            if($(id).parent().children("span.treeSpanActive").length>0){   //点击的节点后面有没有对号，判断点击节点的选中状态
                $(id).parent().children("span.treeSpanActive").remove();      //后面有对号的删除对号
                var id = e.node.original.id;
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
        });
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
        // personInit =parent.personParam;
        // for(var i = 0;i<personInit.length;i++){
        //     if(personInit[i].type == 0){
        //         openPersonId.push("#"+personInit[i].id+"_anchor");
        //     }else{
        //         openPersonId.push("#"+personInit[i].organId+"_anchor");
        //     }
        // }
    }

    // 初始化右侧选中表格
    function initChooseTable(data) {
        table.render({
            elem: '#rit_table'
            ,height: $(".rit-bot").height()//容器高度
            ,cols: [[ //标题栏
                { type: 'checkbox', width: '10%', align: "center"},
                {field: 'name', title: '所选名称', width: '45%', align: "center", toolbar: '#name'},
                { title: '操作', width: '45%', align: "center", toolbar: '#barDemo'}
            ]]
            ,data:data
            ,skin: 'nob' //表格风格
            ,even: true
            ,page: false //是否显示分页
            //,limits: [5, 7, 10]
            ,limit: 1000 //每页默认显示的数量

        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'del'){
                var oldData =  table.cache["rit_table"];
                var id="";
                var idOpen="";
                id = ".one #"+data.id+"_anchor";
                idOpen =  "#"+data.id+"_anchor";

                $(id).parent().children("span.treeSpanActive").remove();   //删掉树上的对
                for(var i = 0;i<openPersonId.length;i++){
                    if(openPersonId[i] == idOpen){
                        openPersonId = openPersonId.del(i);
                        break;
                    }
                }
                for(var j = 0;j<oldData.length;j++){     //删掉表格里的数据
                    //如果删除的为全体人员将全选人员得复选框置空
                    if(oldData[j].id == data.id){
                        oldData = oldData.del(j);
                        break;
                    }
                }
                table.reload('rit_table',{
                    data : oldData
                });
                choose = oldData;
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
    function initAuthorityData() {
        var personIdStr = parent.auditorIds;
        var personNameStr = parent.auditorNames;
        if("" != personIdStr && "" != personNameStr){
            var personIdArr = personIdStr.split(",");
            var personNameArr = personNameStr.split(",");
            if(personIdArr.length == personNameArr.length){
                for (var i = 0; i < personIdArr.length; i++) {
                    var datajson = {"id": personIdArr[i], "name": personNameArr[i], "type": 0};
                    personParamPower.push(datajson);
                }
                personId = personParamPower;
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