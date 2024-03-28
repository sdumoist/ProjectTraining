/*  layui.extend({
 admin: '/static/resources/weadmin/static/js/admin'
 });*/
layui.use(['form', 'jquery','util','layer','table','Hussar','HussarAjax'], function() {
    var form = layui.form,
        $ = layui.jquery,
        table = layui.table,
        layer = layui.layer;
    var Hussar = layui.Hussar,
        $ax=layui.HussarAjax;
    setOptionValues();

    function setOptionValues(){
        /*$.ajax({
            url: Hussar.ctxPath+"/topicDoc/searchTopic",
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                var arr = data.data;
                var optionContent = $("#topicId").html();
                for(var i = 0;i < arr.length;i++){
                    optionContent += "<option value='"+arr[i].topicId+"'>"+arr[i].topicName+"</option>";
                }
                $("#topicId").html(optionContent);
            }
        });*/

        var ajax = new $ax(Hussar.ctxPath + "/topicDoc/searchTopic", function(data) {

            var arr = data.data;
            var optionContent = $("#topicId").html();
            for(var i = 0;i < arr.length;i++){
                optionContent += "<option value='"+arr[i].topicId+"'>"+arr[i].topicName+"</option>";
            }
            $("#topicId").html(optionContent);
        }, function(data) {


        });

        ajax.start();
        form.render();
        $("dl").height(150);
    }
    //.存储当前页数据集
    var pageData = [];
    //初始化表格
    var tableIns = table.render({
        elem: '#docList' //指定原始表格元素选择器（推荐id选择器）
        ,height: 370 //容器高度
        ,data:{
            chooseFileType:$("#chooseFileType").val(),
            fsFiles:$("#fsFiles").val()
        }
        ,url: Hussar.ctxPath+'/topicDoc/getDocByFsFile' //数据接口
        ,done:function(res) {
            $("[data-field='docId']").hide();
            //.假设你的表格指定的 id="docList"，找到框架渲染的表格
            var tbl = $('#docList').next('.layui-table-view');
            //.记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
        }
        ,where: {
            fsFiles:$("#fsFiles").val()
        }
        ,page: false //关闭分页
        ,cols: [[
            {field:'docId'},
            {field:'title',title:'文件或目录名称',width:'50%',align:"left"},
            {field:'createTime',title:'上传时间',width:'30%',align:"center"},
            {field: 'option', title: '操作', width:'21%',align: "center", toolbar: '#delDoc'},
        ]] //设置表头
    });
    //.监听选择，记录已选择项
    table.on('checkbox(docList)', function(obj) {
        //.全选或单选数据集不一样
        var data = obj.type == 'one' ? [obj.data] : pageData;
        //.遍历数据
        $.each(data, function(k, v) {
            //.假设你数据中 id 是唯一关键字
            if (obj.checked) {
                //.增加已选中项
                layui.data('childChecked', {
                    key: v.id, value: v
                });
            } else {
                //.删除
                layui.data('childChecked', {
                    key: v.id, remove: true
                });
            }
        });
    });
    //监听工具条
    table.on('tool(docList)', function (obj) {
        var data = obj.data;
        if (obj.event == 'delete') {
            layer.confirm('确定要删除所选中的文件吗？', function(index){
                obj.del();
                layer.close(index);
            });
        }
    });
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    /*新增/编辑专题*/
    $("#saveBtn").on('click',function(){
        var docList = table.cache.docList;
        var docData = [];

        //因为layui table的obj.del();方法删除一行数据存在问题，所以在此处使用循环删除掉空数据
        for(var i=0;i<docList.length;i++){
            var obj=docList[i];
            if(!!obj["docId"]){
                docData.push(obj);
            }
        }
        var topicId = $("#topicId").val().trim();
        var url = "/topicDoc/saveTopicDoc";
        var successMsg,errorMsg;
        successMsg = "添加成功";
        errorMsg = "添加失败";
        if(topicId==null||topicId==''){
            layer.alert("专题名称不能为空");
            return false;
        }
        if(docData==null||docData.length==0){
            layer.alert("加入专题的文件不能为空");
            return false;
        }
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+url,
            data:{
                topicId:topicId,
                docData:JSON.stringify(docData)
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                if(data.result == 1){
                    layer.msg(successMsg, {
                        icon :  1,
                    },function () {
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                    });

                }
                else if(data.result == 2){
                    layer.msg("该专题存在同名的文件", {anim:6,icon: 0});
                }else{
                    layer.alert(errorMsg, {
                        icon :  2,
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
            if(data.result == 1){
                layer.msg(successMsg, {
                    icon :  1,
                },function () {
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index);
                });

            }
            else if(data.result == 2){
                layer.msg("该专题存在同名的文件", {anim:6,icon: 0});
            }else{
                layer.alert(errorMsg, {
                    icon :  2,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
            }

        }, function(data) {


        });
        ajax.set("topicId",topicId);
        ajax.set("docData",JSON.stringify(docData));
        ajax.start();
    });
});
