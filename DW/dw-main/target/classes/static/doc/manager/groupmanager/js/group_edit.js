var pageFlag='add';
layui.use(['layer','Hussar', 'HussarAjax','form','table','jquery','element','jstree'], function(){
    var layer = layui.layer
        ,table = layui.table
        ,element = layui.element
        ,Hussar = layui.Hussar
        ,$ = layui.jquery
        ,$ax = layui.HussarAjax
        ,jstree=layui.jstree,
        form = layui.form;

    $(function () {
        init();
        if(undefined !=$('#groupId').val() && $('#groupId').val()!=''){
            initChooseTabled();
            pageFlag='edit';
        }else{
            initChooseTable();
        }
        $("#addAllPerson").on('click',function(){
            var tableBak = table.cache.personAddTable;
            var checkStatus = table.cache.personTable;
            var flag = false;
            if (tableBak != null && tableBak.length>0){
                var result = [].concat(tableBak);
                for(var i= 0;i<checkStatus.length;i++){
                    flag = false;
                    for(var j= 0;j<tableBak.length;j++){
                        if (tableBak[j].personId == checkStatus[i].personId){
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        result.push(checkStatus[i])
                    }
                }
                initChooseTable(result);
            }else {
                initChooseTable(checkStatus);
            }
        });
        $("#delAllPerson").on('click',function(){
            initChooseTable([]);
        });
        $("#searchBtn").on('click',function(){
            init();
        });
        /*关闭弹窗*/
        $("#cancel").on('click',function(){
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        });
        $("#authorName").click(function(){
            layerView=layer.open({
                type: 1,
                area: ['300px','450px'],
                fix: false, //不固定
                maxmin: true,
                shadeClose: false,
                shade: 0.5,
                title: "组织树",
                content: $("#employeeTreeDiv"),
                success:function(){
                    var data;
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
                        data = result;
                    }, function(data) {
                        Hussar.error("获取组织失败");
                    });
                    ajax.set("treeType", 1);
                    ajax.start();
                    initEmployeeTree(data);
                }
            });
        });
        $("#addPerson").on('click',function(){
            var tableBak = table.cache.personAddTable;
            var checkStatus = table.checkStatus('personTable')
            ,data = checkStatus.data;
            var flag = false;
            if (tableBak != null && tableBak.length>0){
                var result = [].concat(tableBak);
                for(var i= 0;i<data.length;i++){
                    flag = false;
                    for(var j= 0;j<tableBak.length;j++){
                        if (tableBak[j].personId == data[i].personId){
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        result.push(data[i])
                    }else if(flag&&data.length==1){
                        alert("已存在")
                    }
                }
                initChooseTable(result);
            }else {
                initChooseTable(data);
            }

        });
        /*新增/编辑专题*/
        $("#saveBtn").on('click',function(){
            var groupName = $("#groupName").val().trim();//群组名称
            var tableBak = table.cache.personAddTable;
            var userId ='';
            if (tableBak != null && tableBak.length>0){
                var arr=new Array();
                for(var i= 0;i<tableBak.length;i++){
                    userId = tableBak[i].personId;
                    arr.push(tableBak[i].personId);
                }
                userId = arr.join(",");
            }else{
                layer.alert('请先选择人员', {
                    icon: 0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            if(groupName == ""){
                layer.msg("群组名称不能为空", {anim:6,icon: 0});
                return;
            }
            if(groupName.length > 8){
                layer.msg("群组名称不能超过8位", {anim:6,icon: 0});
                return;
            }
            if(userId == ""){
                layer.msg("人员不能为空", {anim:6,icon: 0});
                return;
            }
            var url;//请求地址
            var successMsg,errorMsg;//成功失败提示
            var groupId;
            if(pageFlag=='add'){
                url = "/group/addGroup";
                groupId='';
            }else{
                url = "/group/editTopic";
                groupId=$('#groupId').val();
            }
            successMsg = "保存成功";
            errorMsg = "保存失败"
            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+url,
                data:{
                    groupId:groupId,
                    groupName:groupName,
                    userId:userId
                },
                async:false,
                cache:false,
                dataType:"json",
                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                success:function(data){
                    if(data.result == "0"){
                        layer.alert('该群组已存在', {
                            icon :  0,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示'
                        });
                    }else if(data.result == "1"){
                        layer.alert(successMsg, {
                            icon :  0,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示'
                        },function(){
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                            parent.tableIns.reload();
                        });
                    }else{
                        layer.alert(errorMsg, {
                            icon :  0,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示'
                        });
                    }
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + url, function(data) {
                if(data.result == "0"){
                    layer.alert('该群组已存在', {
                        icon :  0,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    });
                }else if(data.result == "1"){
                    layer.alert(successMsg, {
                        icon :  0,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    },function(){
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                        parent.tableIns.reload();
                    });
                }else{
                    layer.alert(errorMsg, {
                        icon :  0,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    });
                }
            }, function(data) {

            });
            ajax.set("groupId",groupId);
            ajax.set("groupName",groupName);
            ajax.set("userId",userId);
            ajax.start();
        });
        $("#delPerson").on('click',function(){
            var tableBak = table.cache.personAddTable;
            var checkStatus = table.checkStatus('personAddTable')
            ,data = checkStatus.data;
            var flag = false;
            if (tableBak != null && tableBak.length>0){
                var result = []
                for(var i= 0;i<tableBak.length;i++){
                    flag = false;
                    for(var j= 0;j<data.length;j++){
                        if (data[j].personId == tableBak[i].personId){
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        result.push(tableBak[i])
                    }
                }
                initChooseTable(result);
            }else {
                initChooseTable(data);
            }
        });
    });
    function init() {
        var name = $('#userName').val();
        var deptId = $('#authorId').val();
        tableIns = table.render({
            elem: '#personTable', //指定原始表格元素选择器（推荐id选择器）
            height: 350,//容器高度
            url: Hussar.ctxPath+'/fsFile/getPersonList' ,//数据接口
            request: {
                pageName: 'pageNumber', //页码的参数名称，默认：page
                limitName: 'pageSize' //每页数据量的参数名，默认：limit
            },
            page: true, //开启分页
            limit: 15, //每页默认显示的数量
            where: {
                name: name,
                deptId: deptId
            },
            cols: [[
                {field: 'personId', title: 'id', type: 'checkbox', width: '10%', align: "center"},
                {field: 'deptName', title: '部门名称', width: '40%', align: "center"},
                {field: 'personName', title: '人员名称', align: "center"}
            ]] //设置表头
        });
        //.监听选择，记录已选择项
        table.on('checkbox(personTable)', function (obj) {
            //.全选或单选数据集不一样
            var data = obj.type == 'one' ? [obj.data] : pageData;
            //.遍历数据
            $.each(data, function (k, v) {
                //.假设你数据中 id 是唯一关键字
                if (obj.checked) {
                    //.增加已选中项
                    layui.data('checked', {
                        key: v.id, value: v
                    });
                } else {
                    //.删除
                    layui.data('checked', {
                        key: v.id, remove: true
                    });
                }
            });
        });
    }
    function initChooseTable(data) {
        table.render({
            elem: '#personAddTable', //指定原始表格元素选择器（推荐id选择器）
            height: 350//容器高度
            ,cols: [[ //标题栏
                {field: 'id', title: 'id', type: 'checkbox', width: '10%', align: "center"},
                {field: 'deptName', title: '部门名称', width: '40%', align: "center"},
                {field: 'personName', title: '人员名称',  align: "center"}
            ]]
            ,data:data
            //,skin: 'line' //表格风格
            ,even: true
            ,page: true //是否显示分页
            //,limits: [5, 7, 10]
            ,limit: 15 //每页默认显示的数量
        });
    }
    function initChooseTabled(data) {
        var deptId = $('#groupId').val();
        table.render({
            elem: '#personAddTable', //指定原始表格元素选择器（推荐id选择器）
            height: 350,//容器高度
            width: 530,//弹窗内表格固定宽度
            url: Hussar.ctxPath+'/group/getPersonList' ,//数据接口
            request: {
                pageName: 'pageNumber', //页码的参数名称，默认：page
                limitName: 'pageSize' //每页数据量的参数名，默认：limit
            },
            where: {
                deptId: deptId
            }
            ,cols: [[ //标题栏
                {field: 'id', title: 'id', type: 'checkbox', width: '50', align: "center"},
                {field: 'deptName', title: '部门名称', width: '40%', align: "center"},
                {field: 'personName', title: '人员名称', align: "center"}
            ]]
            ,data:data
            //,skin: 'line' //表格风格
            ,even: true
            ,page: true //是否显示分页
            //,limits: [5, 7, 10]
            ,limit: 15 //每页默认显示的数量
        });
    }
    function initEmployeeTree(data){
        var $tree = $("#showEmployeeTree");
        $tree.jstree({
            core: {
                data: data
            },
            plugins: ['types']
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            if(e.node.children == '' ){
                $("#authorName").val(e.node.original.text);
                $("#authorId").val(e.node.original.id);
                layer.close(layerView);
            }else{
                layer.msg("请选择部门")
                return;
            }
            //layer.msg("你点击了："+e.node.original.text+"&nbsp;&nbsp;节点id："+e.node.original.id)
        })
    }
});


