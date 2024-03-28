/**
 * Created by Administrator on 2018/12/6.
 */
/**
 * Create By luzhanzhao
 * date 2018-11-19
 */
var chooseFile = [];    //选中的文件或目录的id
var chooseState = [];    //选中的文件或目录的id
var chooseOrg = [];
var clickFlag = false;
var currOrder = '';
var scrollHeightAlert = 0;
var scrollHeightLong = 0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var  scrollHeightList=0
var adminFlag;
var orgList=[];
var style=0;
var isOpen = 0;
var opType = $("#opType").val();
layui.use(['form', 'laypage', 'jquery', 'layer', 'laytpl', 'Hussar','element'], function () {
    var $ = layui.jquery,
        form = layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;

    start();
    form.on('checkbox(type)', function (data) {
        var value=data.value;
        if(value==""){
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
        refreshFile(null,null)
    })
    form.on('checkbox(origin)', function (data) {
        var value=data.value;
        if(value==""){
            if( $("input[name=origin]:eq(0)").is(':checked')){
                $("input[name=origin]:not(:first)").each(function () {
                    $(this).prop("checked",false)
                })
            }else {
                $("input[name=origin]:not(:first)").each(function () {
                    $(this).prop("checked",false)
                })
            }
        }else{
            var flag=0;
            $("input[name=origin]:not(:first)").each(function (index) {
                if(!$("input[name=origin]:not(:first)").eq(index).is(':checked')){
                    $("input[name=origin]:eq(0)") .prop("checked",false) ;
                    flag=1;
                    return false
                }
            })
            if(flag==0){
                // $("input[name=origin]:eq(0)") .prop("checked",true) ;
            }
        }
        form.render();
        refreshFile(null,null)
    })
    form.on('checkbox(state)', function (data) {
        var value=data.value;
        if(value==""){
            if( $("input[name=state]:eq(0)").is(':checked')){
                $("input[name=state]:not(:first)").each(function () {
                    $(this).prop("checked",false)
                })
            }else{
                $("input[name=state]").each(function () {
                    $(this).prop("checked",false)
                })
            }
        }else{
            var flag=0;
            $("input[name=state]:not(:first)").each(function (index) {
                if(!$("input[name=state]:not(:first)").eq(index).is(':checked')){
                    $("input[name=state]:eq(0)") .prop("checked",false) ;
                    flag=1;
                    return false
                }
            })
            if(flag==0){
                // $("input[name=state]:eq(0)") .prop("checked",true) ;
            }
        }
        form.render();
        refreshFile(null,null)
    })
    form.on('checkbox(type2)', function (data) {
        var value=data.value;
        if(value==""){
            if( $("input[name=type2]:eq(0)").is(':checked')){
                $("input[name=type2]:not(:first)").each(function () {
                    $(this).prop("checked",false)
                })
            }else{
                $("input[name=type2]").each(function () {
                    $(this).prop("checked",false)
                })
            }

        }else{
            var flag=0;
            $("input[name=type2]:not(:first)").each(function (index) {
                if(!$("input[name=type2]:not(:first)").eq(index).is(':checked')){
                    $("input[name=type2]:eq(0)") .prop("checked",false) ;
                    flag=1;
                    return false
                }
            })
            if(flag==0){
                // $("input[name=type]:eq(0)") .prop("checked",true) ;
            }
        }
        form.render();
        refreshFile(null,null)
    })
    form.on('checkbox(origin2)', function (data) {
        var value=data.value;
        if(value==""){
            if( $("input[name=origin2]:eq(0)").is(':checked')){
                $("input[name=origin2]:not(:first)").each(function () {
                    $(this).prop("checked",false)
                })
            }else {
                $("input[name=origin2]:not(:first)").each(function () {
                    $(this).prop("checked",false)
                })
            }
        }else{
            var flag=0;
            $("input[name=origin2]:not(:first)").each(function (index) {
                if(!$("input[name=origin2]:not(:first)").eq(index).is(':checked')){
                    $("input[name=origin2]:eq(0)") .prop("checked",false) ;
                    flag=1;
                    return false
                }
            })
            if(flag==0){
                // $("input[name=origin]:eq(0)") .prop("checked",true) ;
            }
        }
        form.render();
        refreshFile(null,null)
    })
    $(function () {
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/multiplex/getDept",
            contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            async: false,
            cache: false,
            success: function (data) {
                var dept = $("#dept2");

                for(var i=0;i<data.length;i++){
                    dept.append('<input name="dept2" title="'+data[i].oragnAlias+'" type="checkbox" value="'+data[i].organId+'" lay-skin="primary" lay-filter="dept2">');
                }
                form.render();
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/multiplex/getDept", function(data) {
            var dept = $("#dept2");

            for(var i=0;i<data.length;i++){
                dept.append('<input name="dept2" title="'+data[i].oragnAlias+'" type="checkbox" value="'+data[i].organId+'" lay-skin="primary" lay-filter="dept2">');
            }
            form.render();
        }, function(data) {

        });
        ajax.start();
    })
    form.on('checkbox(state2)', function (data) {
        var value=data.value;
        if(value==""){
            if( $("input[name=state2]:eq(0)").is(':checked')){
                $("input[name=state2]:not(:first)").each(function () {
                    $(this).prop("checked",false)
                })
            }else{
                $("input[name=state2]").each(function () {
                    $(this).prop("checked",false)
                })
            }
        }else{
            var flag=0;
            $("input[name=state2]:not(:first)").each(function (index) {
                if(!$("input[name=state2]:not(:first)").eq(index).is(':checked')){
                    $("input[name=state2]:eq(0)") .prop("checked",false) ;
                    flag=1;
                    return false
                }
            })
            if(flag==0){
                // $("input[name=state]:eq(0)") .prop("checked",true) ;
            }
        }
        form.render();
        refreshFile(null,null)
    })
    form.on('checkbox(dept2)', function (data) {
        var value=data.value;
        if(value==""){
            if( $("input[name=dept2]:eq(0)").is(':checked')){
                $("input[name=dept2]:not(:first)").each(function () {
                    $(this).prop("checked",false)
                })
            }else{
                $("input[name=dept2]").each(function () {
                    $(this).prop("checked",false)
                })
            }
        }else{
            var flag=0;
            $("input[name=dept2]:not(:first)").each(function (index) {
                if(!$("input[name=dept2]:not(:first)").eq(index).is(':checked')){
                    $("input[name=dept2]:eq(0)") .prop("checked",false) ;
                    flag=1;
                    return false
                }
            })
            if(flag==0){
                // $("input[name=state]:eq(0)") .prop("checked",true) ;
            }
        }
        form.render();
        refreshFile(null,null)
    })
    /*搜索按钮*/
    $("#searchBtn").on('click', function () {
        var index = layer.load(1, {
            shade: [0.1, '#fff'] //0.1透明度的白色背景
        });
        refreshFile(null, null);
        layer.close(index);
    });
    $(".changeTab li").on('click', function () {

        var index=   $(".changeTab li").index($(this));
        $("#searchName").val("");
        style=index;
        refreshFile(null,null)
    });

    function start() {
        $("input[name='sortType']:checked").parent().   addClass("sortType-checked");
        btnState()
    }


    $("#componentApply").on('click', function () {
        var type=[];
        var state=[];
        var origin=[];
        $('input[name="type"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            type.push($(this).val());
        });
        $('input[name="origin"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            origin.push($(this).val());
        });

        $('input[name="state"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            state.push($(this).val());
        });
        var stateStr=state.join(",");
        var typeStr="";
        var originStr="";
        if(type.length==3||type.length==2||type.length==0){
            typeStr=""
        }else{
            typeStr=type[0]
        }
        if(origin.length==3||origin.length==2||origin.length==0){
            originStr=""
        }else{
            originStr=origin[0]
        }
        location.href = Hussar.ctxPath+"/component/componentApply?style=1&type="+typeStr+"&origin="+originStr+"&state="+stateStr;
    });
    $("#componentApplyAll").on('click', function () {
        var type=[];
        var state=[];
        var origin=[];
        var dept=[];
        $('input[name="type2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            type.push($(this).val());
        });
        $('input[name="origin2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            origin.push($(this).val());
        });

        $('input[name="state2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            state.push($(this).val());
        });
        $('input[name="dept2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            dept.push($(this).val());
        });
        var stateStr=state.join(",");
        var typeStr="";
        var originStr="";
        var deptStr = dept.join(",")
        if(type.length==3||type.length==2||type.length==0){
            typeStr=""
        }else{
            typeStr=type[0]
        }
        if(origin.length==3||origin.length==2||origin.length==0){
            originStr=""
        }else{
            originStr=origin[0]
        }
        location.href = Hussar.ctxPath+"/component/componentApply?type="+typeStr+"&origin="+originStr+"&state="+stateStr+"&dept="+deptStr;
    });
    //修改
    $("#updateComponentApplyAll").on('click', function () {
        var type=[];
        var state=[];
        var origin=[];
        $('input[name="type"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            type.push($(this).val());
        });
        $('input[name="origin"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            origin.push($(this).val());
        });

        $('input[name="state"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            state.push($(this).val());
        });
        var stateStr=state.join(",");
        var typeStr="";
        var originStr="";
        if(type.length==3||type.length==2||type.length==0){
            typeStr=""
        }else{
            typeStr=type[0]
        }
        if(origin.length==3||origin.length==2||origin.length==0){
            originStr=""
        }else{
            originStr=origin[0]
        }
        if (chooseFile.length > 1) {
            layer.msg("请只选择一条数据", {anim: 6, icon: 0, offset: scrollHeightMsg});
            return;
        } else {
            location.href = Hussar.ctxPath+"/component/componentApplyUpdate?componentId=" + chooseFile[0]+"&type="+typeStr+"&origin="+originStr+"&state="+stateStr
        }

    });
    //成果修改
    $("#updateComponentApply").on('click', function () {
        var type=[];
        var state=[];
        var origin=[];
        $('input[name="type"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            type.push($(this).val());
        });
        $('input[name="origin"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            origin.push($(this).val());
        });

        $('input[name="state"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            state.push($(this).val());
        });
        var stateStr=state.join(",");
        var typeStr="";
        var originStr="";
        if(type.length==3||type.length==2||type.length==0){
            typeStr=""
        }else{
            typeStr=type[0]
        }
        if(origin.length==3||origin.length==2||origin.length==0){
            originStr=""
        }else{
            originStr=origin[0]
        }
        if (chooseFile.length > 1) {
            layer.msg("请只选择一条数据", {anim: 6, icon: 0, offset: scrollHeightMsg});
            return;
        } else {
            location.href = Hussar.ctxPath+"/component/componentApplyUpdate?componentId=" + chooseFile[0]+"&style=1&type="+typeStr+"&origin="+originStr+"&state="+stateStr
        }

    });
    //复用修改
    $("#updateMultiplex").on('click', function () {
        var type=[];
        var state=[];
        var origin=[];
        var dept=[];
        $('input[name="type2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            type.push($(this).val());
        });
        $('input[name="origin2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            origin.push($(this).val());
        });

        $('input[name="state2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            state.push($(this).val());
        });
        $('input[name="dept2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            dept.push($(this).val());
        });
        var stateStr=state.join(",");
        var typeStr="";
        var originStr="";
        if(type.length==3||type.length==2||type.length==0){
            typeStr=""
        }else{
            typeStr=type[0]
        }
        if(origin.length==3||origin.length==2||origin.length==0){
            originStr=""
        }else{
            originStr=origin[0]
        }
        var deptStr = dept.join(",")
        if (chooseFile.length > 1) {
            layer.msg("请只选择一条数据", {anim: 6, icon: 0, offset: scrollHeightMsg});
            return;
        } else {
            location.href = Hussar.ctxPath+"/multiplex/multiplexUpdateView?projectId=" + chooseFile[0];
        }

    });
    $("#multiplexList").on('click', function () {
        layer.open({
            type: 2,
            area: [ '760px',  '450px'],
            fix: false, //不固定
            offset:scrollHeightLong,
            maxmin: false,
            shadeClose: true,
            moveOut: false,
            shade: 0.4,
            title: "我的复用",
            content: Hussar.ctxPath+"/multiplex/myMultiplexView"
        });
    });
    $("#multiplexApply").on('click', function () {
        location.href = Hussar.ctxPath+"/multiplex/multiplexApply"
    });

    $("#cancel").on('click', function () {
        var index = layer.getFrameIndex(window.name);
        layer.close(index);
    });
    /*删除科研成果*/
    $("#delComponentBtn").on('click', function () {
        if (chooseFile.length == 0) {
            layer.msg("请选择要删除的成果", {anim: 6, icon: 0, offset: scrollHeightMsg});
            return;
        }

        layer.confirm('确定要删除所选成果吗？', {title: ['删除', 'background-color:#fff'], offset: scrollHeightAlert,skin:'move-confirm'}, function () {
            var index = layer.load(1, {
                shade: [0.1, '#fff'] //0.1透明度的白色背景
            });
            if (chooseFile.length == 0) {
                layer.close(index);
                return;
            }
            var scopeId = chooseFile.join(',');
            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/component/deleteScope"
                ,  contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                data: {
                    componentIds: scopeId,
                },
                async: true,
                cache: false,
                success: function (data) {
                    if (data > 0) {
                        var fileList = $("#thelist").find(".item");
                        for (var n = 0; n < fileList.length; n++) {
                            var name = fileList.eq(n).find(".info").html().split(".")[0];
                            for (var m = 0; m < chooseFileName.length; m++) {
                                if (name == chooseFileName[m]) {
                                    fileList.eq(n).remove();
                                    uploader.removeFile(fileList.eq(n).attr("id"), true);
                                }
                            }
                        }
                        layer.msg("删除成功", {icon: 1, offset: scrollHeightMsg});

                    } else {

                        layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                    }
                    btnState();
                    // refreshTree();
                    refreshFile();
                    emptyChoose();
                    layer.close(index);
                },
                error: function () {
                    layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                    btnState();
                    //  refreshTree();
                    //refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/component/deleteScope", function(data) {
                if (data > 0) {
                    var fileList = $("#thelist").find(".item");
                    for (var n = 0; n < fileList.length; n++) {
                        var name = fileList.eq(n).find(".info").html().split(".")[0];
                        for (var m = 0; m < chooseFileName.length; m++) {
                            if (name == chooseFileName[m]) {
                                fileList.eq(n).remove();
                                uploader.removeFile(fileList.eq(n).attr("id"), true);
                            }
                        }
                    }
                    layer.msg("删除成功", {icon: 1, offset: scrollHeightMsg});

                } else {

                    layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                }
                btnState();
                // refreshTree();
                refreshFile();
                emptyChoose();
                layer.close(index);
            }, function(data) {
                layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                btnState();
                //  refreshTree();
                //refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("componentIds",scopeId);
            ajax.start();
        })
    });
    /*删除科研成果*/
    $("#delMultiplex").on('click', function () {
        if (chooseFile.length == 0) {
            layer.msg("请选择要删除的复用登记", {anim: 6, icon: 0, offset: scrollHeightMsg});
            return;
        }

        layer.confirm('确定要删除所选复用登记吗？', {title: ['删除', 'background-color:#fff'], offset: scrollHeightAlert,skin:'move-confirm'}, function () {
            var index = layer.load(1, {
                shade: [0.1, '#fff'] //0.1透明度的白色背景
            });
            if (chooseFile.length == 0) {
                layer.close(index);
                return;
            }

            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/multiplex/deleteMultiplexSave",
                data: {
                    projectId: chooseFile[0],
                },
                async: true,
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                cache: false,
                success: function (data) {
                    if (data.result > 0) {
                        var fileList = $("#thelist").find(".item");
                        for (var n = 0; n < fileList.length; n++) {
                            var name = fileList.eq(n).find(".info").html().split(".")[0];
                            for (var m = 0; m < chooseFileName.length; m++) {
                                if (name == chooseFileName[m]) {
                                    fileList.eq(n).remove();
                                    uploader.removeFile(fileList.eq(n).attr("id"), true);
                                }
                            }
                        }
                        layer.msg("删除成功", {icon: 1, offset: scrollHeightMsg});

                    } else {

                        layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                    }
                    btnState();
                    // refreshTree();
                    refreshFile();
                    emptyChoose();
                    layer.close(index);
                },
                error: function () {
                    layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                    btnState();
                    //  refreshTree();
                    //refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/multiplex/deleteMultiplexSave", function(data) {
                if (data.result > 0) {
                    var fileList = $("#thelist").find(".item");
                    for (var n = 0; n < fileList.length; n++) {
                        var name = fileList.eq(n).find(".info").html().split(".")[0];
                        for (var m = 0; m < chooseFileName.length; m++) {
                            if (name == chooseFileName[m]) {
                                fileList.eq(n).remove();
                                uploader.removeFile(fileList.eq(n).attr("id"), true);
                            }
                        }
                    }
                    layer.msg("删除成功", {icon: 1, offset: scrollHeightMsg});

                } else {

                    layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                }
                btnState();
                // refreshTree();
                refreshFile();
                emptyChoose();
                layer.close(index);
            }, function(data) {
                layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                btnState();
                //  refreshTree();
                //refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("projectId",chooseFile[0]);
            ajax.start();
        })
    });
    /*删除科研成果*/
    $("#delComponentBtnAll").on('click', function () {
        if (chooseFile.length == 0) {
            layer.msg("请选择要删除的成果", {anim: 6, icon: 0, offset: scrollHeightMsg});
            return;
        }

        layer.confirm('确定要删除所选成果吗？', {title: ['删除', 'background-color:#fff'], offset: scrollHeightAlert,skin:'move-confirm'}, function () {
            var index = layer.load(1, {
                shade: [0.1, '#fff'] //0.1透明度的白色背景
            });
            if (chooseFile.length == 0) {
                layer.close(index);
                return;
            }
            var scopeId = chooseFile.join(',');
            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/component/deleteScope",
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                data: {
                    componentIds: scopeId,
                },
                async: true,
                cache: false,
                success: function (data) {
                    if (data > 0) {

                        layer.msg("删除成功", {icon: 1, offset: scrollHeightMsg});

                    } else {

                        layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                    }
                    btnState();
                    // refreshTree();
                    refreshFile();
                    emptyChoose();
                    layer.close(index);
                },
                error: function () {
                    layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                    btnState();
                    //  refreshTree();
                    //refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/component/deleteScope", function(data) {
                if (data > 0) {

                    layer.msg("删除成功", {icon: 1, offset: scrollHeightMsg});

                } else {

                    layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                }
                btnState();
                // refreshTree();
                refreshFile();
                emptyChoose();
                layer.close(index);
            }, function(data) {
                layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                btnState();
                //  refreshTree();
                //refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("componentIds",scopeId);
            ajax.start();
        })
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
        var styleThis=$("#style").val();
        if(styleThis==1){
            style=styleThis;
            $(".changeTab li").removeClass("layui-this")
            $(".changeTab li:eq(1)").addClass("layui-this")
            var type=$("#type").val();
            var origin=$("#origin").val();
            var state=$("#state").val();
            if(type!=""){
                $("input[name=type]") .prop("checked",false) ;
                $("input[name=type]").eq(type-0+1).prop("checked",true) ;
                form.render();
            }
            if(origin!=""){
                $("input[name=origin]") .prop("checked",false) ;
                $("input[name=origin]").eq(origin-0+1).prop("checked",true) ;
                form.render();
            }
            var stateStr = state.split(",");
            if(stateStr.length==1&&stateStr[0]==""){

            }else{
                $("input[name=state]") .prop("checked",false) ;
                for(var i=0;i<stateStr.length;i++){
                    $("input[name=state]").eq(stateStr[i]-0+1).prop("checked",true) ;

                }
            }


        }
         else if(styleThis==2){
            style=styleThis;
            $(".changeTab li:eq(0)").removeClass("layui-this")
            $(".changeTab li:eq(1)").removeClass("layui-this")
            $(".changeTab li:eq(2)").addClass("layui-this")
        }
        else {
            var type=$("#type").val();
            var origin=$("#origin").val();
            var state=$("#state").val();
            var dept=$("#dept").val();
            if(type!=""){
                $("input[name=type2]") .prop("checked",false) ;
                $("input[name=type2]").eq(type-0+1).prop("checked",true) ;
                form.render();
            }
            if(origin!=""){
                $("input[name=origin2]") .prop("checked",false) ;
                $("input[name=origin2]").eq(origin-0+1).prop("checked",true) ;
                form.render();
            }
            var stateStr = state.split(",");
            if(stateStr.length==1&&stateStr[0]==""){

            }else{
                $("input[name=state2]") .prop("checked",false) ;
                for(var i=0;i<stateStr.length;i++){
                    $("input[name=state2]").eq(stateStr[i]-0+1).prop("checked",true) ;

                }
            }
            var deptStr = dept.split(",");
            if(deptStr.length==1&&deptStr[0]==""){

            }else{
                $("input[name=dept2]") .prop("checked",false) ;
                for(var i=0;i<deptStr.length;i++){
                    $("input[name=dept2]").each(function (index) {
                        if($(this).val()==deptStr[i]){
                            $(this).prop("checked",true);
                        }
                    })


                }
            }
        }
        var check=$("#check").val();
        if(check==1){
            $("input[name=state2]") .prop("checked",false) ;
            $("input[name=state2]:eq(3)").prop("checked",true) ;

        }
        form.render();
        refreshFile();
    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth = inner.width();
        //fixed-table-header
        $(".fixed-table-header").width(tableWidth)
    })
});


/*取消收藏*/
function cancelCollection(e, id) {
    cancelBubble();
    changeBgColorOfTr(e);
    layer.confirm('确定要取消收藏吗？', {title: ['取消收藏', 'background-color:#fff'], offset: scrollHeightAlert,skin:'move-confirm'}, function () {
        var index = layer.load(1, {
            shade: [0.1, '#fff'], //0.1透明度的白色背景
            fix: true
        });

        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/personalCollection/deleteCollection",
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                data: {
                    ids: id,
                    opType: opType
                },
                async: true,
                cache: false,
                success: function (data) {
                    if (data > 0) {
                        var fileList = $("#thelist").find(".item");
                        for (var n = 0; n < fileList.length; n++) {
                            var name = fileList.eq(n).find(".info").html().split(".")[0];
                            for (var m = 0; m < chooseFileName.length; m++) {
                                if (name == chooseFileName[m]) {
                                    fileList.eq(n).remove();
                                }
                            }
                        }
                        layer.msg('取消收藏成功', {icon: 1, offset: scrollHeightMsg})
                    } else {
                        layer.msg('取消收藏失败', {anim: 6, icon: 2, offset: scrollHeightMsg})
                    }
                    btnState();
                    // refreshTree();
                    refreshFile();
                    emptyChoose();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('取消收藏异常!', {anim: 6, icon: 2, offset: scrollHeightMsg})
                    btnState();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/deleteCollection", function(data) {
                if (data > 0) {
                    var fileList = $("#thelist").find(".item");
                    for (var n = 0; n < fileList.length; n++) {
                        var name = fileList.eq(n).find(".info").html().split(".")[0];
                        for (var m = 0; m < chooseFileName.length; m++) {
                            if (name == chooseFileName[m]) {
                                fileList.eq(n).remove();
                            }
                        }
                    }
                    layer.msg('取消收藏成功', {icon: 1, offset: scrollHeightMsg})
                } else {
                    layer.msg('取消收藏失败', {anim: 6, icon: 2, offset: scrollHeightMsg})
                }
                btnState();
                // refreshTree();
                refreshFile();
                emptyChoose();
                layer.close(index);
            }, function(data) {
                layer.msg('取消收藏异常!', {anim: 6, icon: 2, offset: scrollHeightMsg})
                btnState();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("ids",id);
            ajax.set("opType",opType);
            ajax.start();
        });
    })
};
function refreshFile(num, size, order) {

    var screenHeight = parseInt(window.screen.availHeight);
    //console.log(screenHeight);
    if (screenHeight > 728) {
        $("#marg").css("min-height", "768");
    }
    var noOrder;
    if (order == null || order == undefined || order == '') {
        noOrder = true;
        order = '';
    }
    currOrder = order;
    if(style==1){
        $("#searchName").attr("placeholder","按名称查找成果");
        var type=[];
        var state=[];
        var origin=[];
        $('input[name="type"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            type.push($(this).val());
        });
        $('input[name="origin"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            origin.push($(this).val());
        });

        $('input[name="state"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            state.push($(this).val());
        });
        var stateStr=state.join(",");
        var typeStr="";
        var originStr="";
        if(type.length==3||type.length==2||type.length==0){
            typeStr=""
        }else{
            typeStr=type[0]
        }
        if(origin.length==3||origin.length==2||origin.length==0){
            originStr=""
        }else{
            originStr=origin[0]
        }
        $(".closes").trigger("click");
        $("#marg .file-container-flatten").css("margin-top","160px")
        $(".fileButtonGroup").hide();
        $(".fileButtonGroup3").hide();
        $(".fileButtonGroup2").show();
        $(".layui-tab-item").removeClass("layui-show");
        $(".layui-tab-item:eq(1)").addClass("layui-show");
        layui.use(['laypage', 'layer', 'table'], function () {
            var laypage = layui.laypage;
            var name = $('#searchName').val();
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/component/componentListByApply",
                    contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                    data: {
                        page: num,
                        limit: size,
                        componentName: name,
                        componentType:typeStr,
                        componentOrigin:originStr,
                        stateStr:stateStr
                    },
                    async: true,
                    cache: false,
                    dataType: "json",
                    success: function (data) {
                        laypage.render({
                            elem: 'laypageAre'
                            , count: data.count //数据总数，从服务端得到
                            , limit: 10
                            , layout: ['prev', 'page', 'next']
                            , curr: num || 1 //当前页
                            , jump: function (obj, first) {
                                //obj包含了当前分页的所有参数，比如：
                                // obj.curr得到当前页，以便向服务端请求对应页的数据。
                                // obj.limit得到每页显示的条数
                                //首次不执行
                                if (!first) {
                                    refreshFile(obj.curr, obj.limit, currOrder)
                                }
                            }
                        });

                        $("#amount").html("共" + data.count + "个");
                        drawFile(data);
                        emptyChoose();
                        btnState();
                        dbclickover = true;

                        // 取消收藏按钮显示
                        $(".hoverEvent").hover(function () {
                            $(this).find("td>.hoverSpan").show();
                        }, function () {
                            $(this).find("td>.hoverSpan").hide();
                        });

                        //$("#orderTime").parent().click(function () {
                        //    $(this).find(".layui-icon:visible").click();
                        //    cancelBubble();
                        //});
                        if (noOrder == true) {
                            $("#orderTime").hide();
                            $("#orderTime1").hide();
                        } else {
                            //if(order== "1"){
                            //    $(this).find("#orderName").hide();
                            //    $(this).find("#orderName1").show();
                            //    $(this).find("#orderTime").hide();
                            //    $(this).find("#orderTime1").show();
                            //}
                            //if(order== "0"){
                            //    $(this).find("#orderName1").hide();
                            //    $(this).find("#orderName").show();
                            //    $(this).find("#orderTime").hide();
                            //    $(this).find("#orderTime1").show();
                            //}
                            if (order == "2") {
                                $("#orderTime1").hide();
                                $("#orderTime").css("display", "inline-block");
                            }
                            if (order == "3") {
                                $("#orderTime").hide();
                                $("#orderTime1").show("display", "inline-block");
                            }
                        }
                        //}, function () {
                        //    $(this).find("#orderTime").hide();
                        //    $(this).find("#orderTime1").hide();
                        //})
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
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/component/componentListByApply", function(data) {
                    laypage.render({
                        elem: 'laypageAre'
                        , count: data.count //数据总数，从服务端得到
                        , limit: 10
                        , layout: ['prev', 'page', 'next']
                        , curr: num || 1 //当前页
                        , jump: function (obj, first) {
                            //obj包含了当前分页的所有参数，比如：
                            // obj.curr得到当前页，以便向服务端请求对应页的数据。
                            // obj.limit得到每页显示的条数
                            //首次不执行
                            if (!first) {
                                refreshFile(obj.curr, obj.limit, currOrder)
                            }
                        }
                    });

                    $("#amount").html("共" + data.count + "个");
                    drawFile(data);
                    emptyChoose();
                    btnState();
                    dbclickover = true;

                    // 取消收藏按钮显示
                    $(".hoverEvent").hover(function () {
                        $(this).find("td>.hoverSpan").show();
                    }, function () {
                        $(this).find("td>.hoverSpan").hide();
                    });

                    //$("#orderTime").parent().click(function () {
                    //    $(this).find(".layui-icon:visible").click();
                    //    cancelBubble();
                    //});
                    if (noOrder == true) {
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                    } else {
                        //if(order== "1"){
                        //    $(this).find("#orderName").hide();
                        //    $(this).find("#orderName1").show();
                        //    $(this).find("#orderTime").hide();
                        //    $(this).find("#orderTime1").show();
                        //}
                        //if(order== "0"){
                        //    $(this).find("#orderName1").hide();
                        //    $(this).find("#orderName").show();
                        //    $(this).find("#orderTime").hide();
                        //    $(this).find("#orderTime1").show();
                        //}
                        if (order == "2") {
                            $("#orderTime1").hide();
                            $("#orderTime").css("display", "inline-block");
                        }
                        if (order == "3") {
                            $("#orderTime").hide();
                            $("#orderTime1").show("display", "inline-block");
                        }
                    }
                    //}, function () {
                    //    $(this).find("#orderTime").hide();
                    //    $(this).find("#orderTime1").hide();
                    //})
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
                }, function(data) {

                });
                ajax.set("page",num);
                ajax.set("limit",size);
                ajax.set("componentName",name);
                ajax.set("componentType",typeStr);
                ajax.set("componentOrigin",originStr);
                ajax.set("stateStr",stateStr);
                ajax.start();
            });

        });
    }else if(style==0){
        $("#searchName").attr("placeholder","按名称查找成果");
        var type=[];
        var state=[];
        var origin=[];
        var dept=[]
        $('input[name="type2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            type.push($(this).val());
        });
        $('input[name="origin2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            origin.push($(this).val());
        });

        $('input[name="state2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            state.push($(this).val());
        });
        $('input[name="dept2"]:checked').each(function(){//遍历每一个名字为state的复选框，其中选中的执行函数
            dept.push($(this).val());
        });
        var stateStr=state.join(",");
        var typeStr="";
        var deptStr=dept.join(",");
        var originStr="";
        if(type.length==3||type.length==2||type.length==0){
            typeStr=""
        }else{
            typeStr=type[0]
        }
        if(origin.length==3||origin.length==2||origin.length==0){
            originStr=""
        }else{
            originStr=origin[0]
        }
        $("#marg .file-container-flatten").css("margin-top","210px")
        $(".opens").click(function () {
            isOpen=1;
            $(this).hide();
            $(".fileButtonGroup").addClass('fileButtonGroup-open');
            $(".noDataTip").css("top","65%");
            $(".closes").show()
            $("#marg .file-container-flatten").css("margin-top",$('#dndArea>.layui-tab').outerHeight());
            $(".item-last").css("overflow","initial");
            $("#componentApplyAll").css({"position":"static","bottom":"-170","right":"0"})
            $("#delComponentBtnAll").css({"position":"static","bottom":"-170","right":"88"})
            $("#updateComponentApplyAll").css({"position":"static","bottom":"-170","right":"186"})
        });
        $(".closes").click(function () {
            isOpen=0;
            $(".noDataTip").css("top","45%");
            $(".fileButtonGroup").removeClass('fileButtonGroup-open');
            $(this).hide();
            $(".opens").show()
            $("#marg .file-container-flatten").css("margin-top","210px");
            $(".item-last").css("overflow","hidden")
            $(".newFolder").css({"position":"static"})
        })
        if(isOpen==1){
            $(".opens").hide();
            $(".noDataTip").css("top","65%");
            $(".closes").show()
            $("#marg .file-container-flatten").css("margin-top",$('#dndArea>.layui-tab').outerHeight());
            $(".item-last").css("overflow","initial");
            $("#componentApplyAll").css({"position":"static","bottom":"-170","right":"0"})
            $("#delComponentBtnAll").css({"position":"static","bottom":"-170","right":"88"})
            $("#updateComponentApplyAll").css({"position":"static","bottom":"-170","right":"186"})
        }else{
            isOpen=0;
            $(".noDataTip").css("top","45%");
            $(".closes").hide();
            $(".opens").show()
            $("#marg .file-container-flatten").css("margin-top","210px");
            $(".item-last").css("overflow","hidden")
            $(".newFolder").css({"position":"static"})
        }

        $(".fileButtonGroup2").hide();
        $(".fileButtonGroup3").hide();
        $(".fileButtonGroup").show();
        $(".layui-tab-item").removeClass("layui-show");
        $(".layui-tab-item:eq(0)").addClass("layui-show");
        layui.use(['laypage', 'layer', 'table'], function () {
            var laypage = layui.laypage;
            var name = $('#searchName').val();
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/component/componentListFrontByAll",
                    contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                    data: {
                        page: num,
                        limit: size,
                        componentName: name,
                        componentType:typeStr,
                        componentOrigin:originStr,
                        stateStr:stateStr,
                        deptStr:deptStr
                    },
                    async: true,
                    cache: false,
                    dataType: "json",
                    success: function (data) {
                        laypage.render({
                            elem: 'laypageAre'
                            , count: data.count //数据总数，从服务端得到
                            , limit: 10
                            , layout: ['prev', 'page', 'next']
                            , curr: num || 1 //当前页
                            , jump: function (obj, first) {
                                //obj包含了当前分页的所有参数，比如：
                                // obj.curr得到当前页，以便向服务端请求对应页的数据。
                                // obj.limit得到每页显示的条数
                                //首次不执行
                                if (!first) {
                                    refreshFile(obj.curr, obj.limit, currOrder)
                                }
                            }
                        });
                        adminFlag=data.adminFlag;
                        orgList=[];
                        for(var i=0;i<data.orgId.length;i++){
                            orgList.push(data.orgId[i]);
                        }
                        $("#amount").html("共" + data.count + "个");
                        drawFile(data);
                        emptyChoose();
                        btnState();
                        dbclickover = true;

                        // 取消收藏按钮显示
                        $(".hoverEvent").hover(function () {
                            $(this).find("td>.hoverSpan").show();
                        }, function () {
                            $(this).find("td>.hoverSpan").hide();
                        });

                        //$("#orderTime").parent().click(function () {
                        //    $(this).find(".layui-icon:visible").click();
                        //    cancelBubble();
                        //});
                        if (noOrder == true) {
                            $("#orderTime").hide();
                            $("#orderTime1").hide();
                        } else {
                            //if(order== "1"){
                            //    $(this).find("#orderName").hide();
                            //    $(this).find("#orderName1").show();
                            //    $(this).find("#orderTime").hide();
                            //    $(this).find("#orderTime1").show();
                            //}
                            //if(order== "0"){
                            //    $(this).find("#orderName1").hide();
                            //    $(this).find("#orderName").show();
                            //    $(this).find("#orderTime").hide();
                            //    $(this).find("#orderTime1").show();
                            //}
                            if (order == "2") {
                                $("#orderTime1").hide();
                                $("#orderTime").css("display", "inline-block");
                            }
                            if (order == "3") {
                                $("#orderTime").hide();
                                $("#orderTime1").show("display", "inline-block");
                            }
                        }
                        //}, function () {
                        //    $(this).find("#orderTime").hide();
                        //    $(this).find("#orderTime1").hide();
                        //})
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
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath +"/component/componentListFrontByAll", function(data) {
                    laypage.render({
                        elem: 'laypageAre'
                        , count: data.count //数据总数，从服务端得到
                        , limit: 10
                        , layout: ['prev', 'page', 'next']
                        , curr: num || 1 //当前页
                        , jump: function (obj, first) {
                            //obj包含了当前分页的所有参数，比如：
                            // obj.curr得到当前页，以便向服务端请求对应页的数据。
                            // obj.limit得到每页显示的条数
                            //首次不执行
                            if (!first) {
                                refreshFile(obj.curr, obj.limit, currOrder)
                            }
                        }
                    });
                    adminFlag=data.adminFlag;
                    orgList=[];
                    for(var i=0;i<data.orgId.length;i++){
                        orgList.push(data.orgId[i]);
                    }
                    $("#amount").html("共" + data.count + "个");
                    drawFile(data);
                    emptyChoose();
                    btnState();
                    dbclickover = true;

                    // 取消收藏按钮显示
                    $(".hoverEvent").hover(function () {
                        $(this).find("td>.hoverSpan").show();
                    }, function () {
                        $(this).find("td>.hoverSpan").hide();
                    });

                    //$("#orderTime").parent().click(function () {
                    //    $(this).find(".layui-icon:visible").click();
                    //    cancelBubble();
                    //});
                    if (noOrder == true) {
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                    } else {
                        //if(order== "1"){
                        //    $(this).find("#orderName").hide();
                        //    $(this).find("#orderName1").show();
                        //    $(this).find("#orderTime").hide();
                        //    $(this).find("#orderTime1").show();
                        //}
                        //if(order== "0"){
                        //    $(this).find("#orderName1").hide();
                        //    $(this).find("#orderName").show();
                        //    $(this).find("#orderTime").hide();
                        //    $(this).find("#orderTime1").show();
                        //}
                        if (order == "2") {
                            $("#orderTime1").hide();
                            $("#orderTime").css("display", "inline-block");
                        }
                        if (order == "3") {
                            $("#orderTime").hide();
                            $("#orderTime1").show("display", "inline-block");
                        }
                    }
                    //}, function () {
                    //    $(this).find("#orderTime").hide();
                    //    $(this).find("#orderTime1").hide();
                    //})
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
                }, function(data) {

                });
                ajax.set("page",num);
                ajax.set("limit",size);
                ajax.set("componentName",name);
                ajax.set("componentType",typeStr);
                ajax.set("componentOrigin",originStr);
                ajax.set("stateStr",stateStr);
                ajax.set("deptStr",deptStr);
                ajax.start();
            });

        });
    }else{
        $(".newFolder").css({"position":"relative","margin-top":"-30px!important"})
        $("#searchName").attr("placeholder","按名称查找项目");
        $(".fileButtonGroup").hide();
        $(".fileButtonGroup2").hide();
        $(".fileButtonGroup3").show();
        $(".layui-tab-item").removeClass("layui-show");
        $(".layui-tab-item:eq(2)").addClass("layui-show");
        // $(".newFolder").css({"position":"relative","margin-top":"-30px !important"});

        $("#marg .file-container-flatten").css("margin-top","50px");
        layui.use(['laypage', 'layer', 'table'], function () {
            var laypage = layui.laypage;
            var name = $('#searchName').val();
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

               /* $.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/multiplex/myMultiplexList",
                    contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                    data: {
                        page: num,
                        limit: size,
                        title: name
                    },
                    async: true,
                    cache: false,
                    dataType: "json",
                    success: function (data) {
                        laypage.render({
                            elem: 'laypageAre'
                            , count: data.count //数据总数，从服务端得到
                            , limit: 10
                            , layout: ['prev', 'page', 'next']
                            , curr: num || 1 //当前页
                            , jump: function (obj, first) {
                                //obj包含了当前分页的所有参数，比如：
                                // obj.curr得到当前页，以便向服务端请求对应页的数据。
                                // obj.limit得到每页显示的条数
                                //首次不执行
                                if (!first) {
                                    refreshFile(obj.curr, obj.limit, currOrder)
                                }
                            }
                        });

                        $("#amount").html("共" + data.count + "个");
                        drawFile(data);
                        emptyChoose();
                        btnState();
                        dbclickover = true;

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
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/multiplex/myMultiplexList", function(data) {
                    laypage.render({
                        elem: 'laypageAre'
                        , count: data.count //数据总数，从服务端得到
                        , limit: 10
                        , layout: ['prev', 'page', 'next']
                        , curr: num || 1 //当前页
                        , jump: function (obj, first) {
                            //obj包含了当前分页的所有参数，比如：
                            // obj.curr得到当前页，以便向服务端请求对应页的数据。
                            // obj.limit得到每页显示的条数
                            //首次不执行
                            if (!first) {
                                refreshFile(obj.curr, obj.limit, currOrder)
                            }
                        }
                    });

                    $("#amount").html("共" + data.count + "个");
                    drawFile(data);
                    emptyChoose();
                    btnState();
                    dbclickover = true;

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
                }, function(data) {

                });
                ajax.set("page",num);
                ajax.set("limit",size);
                ajax.set("title",name);
                ajax.start();
            });

        });
    }

}
$(document).click(function(e){


    if($(e.target)[0]==$('.file-container-flatten')[0] ||$(e.target)[0]==$('.content')[0]
        ||$(e.target)[0]==$('#marg')[0]||$(e.target)[0]==$('#view ul')[0]||$(e.target)[0]==$('.layui-layer-dialog')[0]
        ||$(e.target)[0]==$('#updateComponentApply')[0]||$(e.target)[0]==$('#delComponentBtn')[0]||$(e.target)[0]==$('#updateComponentApplyAll')[0]||$(e.target)[0]==$('#delComponentBtnAll')[0]
        ||$(e.target)[0]==$('#delMultiplex')[0]||$(e.target)[0]==$('#updateMultiplex')[0]
    ){


    }else{
        if(style==1){

        }else{
            $('.hoverEvent').css("background-color","white");
            // $("tbody").find(".checkbox").prop("checked",false);
            // $("tbody").find(".layui-form-checkbox").removeClass("layui-form-checked");

            emptyChoose();
            btnState();
        }
    }

});
/*打开分享链接*/
function share(e, docId, fileSuffixName, fileName) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                if (data.code == 1) {
                } else if (data.code == 2) {
                } else if (data.code == 3) {
                } else if (data.code == 4) {
                } else {
                    layer.msg("此文件类型不支持分享。", {anim: 6, icon: 0, offset: scrollHeightMsg});
                    return;
                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if (data.code == 1) {
            } else if (data.code == 2) {
            } else if (data.code == 3) {
            } else if (data.code == 4) {
            } else {
                layer.msg("此文件类型不支持分享。", {anim: 6, icon: 0, offset: scrollHeightMsg});
                return;
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
            dataType: "json",
            success: function (data) {
                if (data.result == "1") {
                    layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
                } else if (data.result == "5") {
                    layer.msg("该文件不是最新版本", {anim: 6, icon: 0});
                } else {
                    var title = '';
                    var url = "/s/shareConfirm";
                    var w = 538;
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
                        title: title,
                        closeBtn: 2,
                        offset: parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                        content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                    });
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if (data.result == "1") {
                layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
            } else if (data.result == "5") {
                layer.msg("该文件不是最新版本", {anim: 6, icon: 0});
            } else {
                var title = '';
                var url = "/s/shareConfirm";
                var w = 538;
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
                    title: title,
                    closeBtn: 2,
                    offset: parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                    content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                });
            }
        }, function(data) {

        });
        ajax.set("ids",docId);
        ajax.start();
    });
}
function getTimeOrder() {

    refreshFile(null, null, 3);
}

function getTimeOrder1() {
    refreshFile(null, null, 2)
}
function orderByTime() {
    if ($("#orderTime").css("display") != "none") {
        getTimeOrder();
    } else {
        getTimeOrder1();
    }
}
function drawFile(param) {
    if(style==0){
        layui.use('laytpl', function () {
            var laytpl = layui.laytpl;
            var data = { //数据
                "list": param.data,
                "adminFlag": param.adminFlag
            };
            var getTpl = $("#demo1").html()
                , view = document.getElementById('view');
            laytpl(getTpl).render(data, function (html) {
                view.innerHTML = html;
                var inner = $("#view");
                var tableWidth = inner.width();
                //fixed-table-header
                $(".fixed-table-header").width(tableWidth);
                if (param.count == 0) {

                    setTimeout(function () {
                        $("div.noDataTip").show();
                        $("#laypageAre").hide();

                    }, 200);
                } else {
                    $("div.noDataTip").hide();
                    $("#laypageAre").show();
                }
            });
        });
    } else if(style==1){
        layui.use('laytpl', function () {
            var laytpl = layui.laytpl;
            var data = { //数据
                "list": param.data,
                "adminFlag": param.adminFlag,
                "style":style
            };
            var getTpl = $("#demo1").html()
                , view = document.getElementById('view');
            laytpl(getTpl).render(data, function (html) {
                view.innerHTML = html;
                var inner = $("#view");
                var tableWidth = inner.width();
                //fixed-table-header
                $(".fixed-table-header").width(tableWidth);
                if (param.count == 0) {
                    setTimeout(function () {
                        $("div.noDataTip").show();
                        $("#laypageAre").hide();
                    }, 200);
                } else {
                    $("div.noDataTip").hide();
                    $("#laypageAre").show();
                }
            });
        });
    }else {
        layui.use('laytpl', function () {
            var laytpl = layui.laytpl;
            var data = { //数据
                "list": param.data,
                "adminFlag": param.adminFlag
            };
            var getTpl = $("#demo2").html()
                , view = document.getElementById('view');
            laytpl(getTpl).render(data, function (html) {
                view.innerHTML = html;
                var inner = $("#view");
                var tableWidth = inner.width();
                //fixed-table-header
                $(".fixed-table-header").width(tableWidth);
                if (param.count == 0) {
                    setTimeout(function () {
                        $("div.noDataTip").show();
                        $("#laypageAre").hide();
                    }, 200);
                } else {
                    $("div.noDataTip").hide();
                    $("#laypageAre").show();
                }
            });
        });
    }

}
function dbclick(id, type, name,componentState) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        openWin(Hussar.ctxPath+"/toShowComponent/toShowPDF?id=" + id+"&componentState="+componentState);
    });

}
function download(id, name) {
    //cancelBubble();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        $.ajaxFileUpload({
            url: Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
            type: "post",
            async: false
        });
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}
function iconDownLoad(e, id, name) {
    cancelBubble();
    changeBgColorOfTr(e);

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/personalcenter/getInfo",
            data: {
                ids: id
            },
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                if (data.result == "1") {
                    layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
                }
                else if (data.result == "2" || data.result == "3") {
                    layer.msg("您没有权限", {anim: 6, icon: 0, offset: scrollHeightMsg});
                } else if (data.result == "5") {
                    layer.msg("该文件不是最新版本", {anim: 6, icon: 0});
                } else {
                    $.ajax({
                        url: Hussar.ctxPath+"/integral/downloadIntegral",
                        async: true,
                        data: {
                            docId: id,
                            ruleCode: 'download'
                        },
                        success: function (data) {
                            if (data.status == "1") {

                                var index2 = layer.confirm('下载文件将扣除' + data.integral + '积分，是否确认下载？', {
                                    icon: 3,
                                    title: '提示',
                                    offset: scrollHeightAlert
                                }, function (index) {
                                    layer.close(index2);
                                    var index = layer.load(1, {
                                        shade: [0.1, '#fff'],//0.1透明度的白色背景
                                        scrollbar: false,
                                        time: 1000
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
                            } else {
                                var index = layer.load(1, {
                                    shade: [0.1, '#fff'],//0.1透明度的白色背景
                                    scrollbar: false,
                                    time: 1000
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
                        }
                    })
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if (data.result == "1") {
                layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
            }
            else if (data.result == "2" || data.result == "3") {
                layer.msg("您没有权限", {anim: 6, icon: 0, offset: scrollHeightMsg});
            } else if (data.result == "5") {
                layer.msg("该文件不是最新版本", {anim: 6, icon: 0});
            } else {
                /*$.ajax({
                    url: Hussar.ctxPath+"/integral/downloadIntegral",
                    async: true,
                    data: {
                        docId: id,
                        ruleCode: 'download'
                    },
                    success: function (data) {
                        if (data.status == "1") {

                            var index2 = layer.confirm('下载文件将扣除' + data.integral + '积分，是否确认下载？', {
                                icon: 3,
                                title: '提示',
                                offset: scrollHeightAlert
                            }, function (index) {
                                layer.close(index2);
                                var index = layer.load(1, {
                                    shade: [0.1, '#fff'],//0.1透明度的白色背景
                                    scrollbar: false,
                                    time: 1000
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
                        } else {
                            var index = layer.load(1, {
                                shade: [0.1, '#fff'],//0.1透明度的白色背景
                                scrollbar: false,
                                time: 1000
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
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {
                    if (data.status == "1") {

                        var index2 = layer.confirm('下载文件将扣除' + data.integral + '积分，是否确认下载？', {
                            icon: 3,
                            title: '提示',
                            skin:'download-info',
                            offset: scrollHeightAlert
                        }, function (index) {
                            layer.close(index2);
                            var index = layer.load(1, {
                                shade: [0.1, '#fff'],//0.1透明度的白色背景
                                scrollbar: false,
                                time: 1000
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
                    } else {
                        var index = layer.load(1, {
                            shade: [0.1, '#fff'],//0.1透明度的白色背景
                            scrollbar: false,
                            time: 1000
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
function showPdf(id, fileSuffixName, name) {
    var keyword = name;
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
            dataType: "json",
            success: function (data) {
                if (data.code == 1) {
                    openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
                } else if (data.code == 2) {
                    openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
                } else if (data.code == 3) {
                    openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
                } else if (data.code == 4) {
                    openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
                } else {
                    layer.msg("此文件类型不支持预览。", {anim: 6, icon: 0, offset: scrollHeightMsg});
                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if (data.code == 1) {
                openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
            } else if (data.code == 2) {
                openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
            } else if (data.code == 3) {
                openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
            } else if (data.code == 4) {
                openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
            } else {
                layer.msg("此文件类型不支持预览。", {anim: 6, icon: 0, offset: scrollHeightMsg});
            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
    });
}
function isPDFShow(fileSuffixName) {
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
    if (chooseFile.length == 0) {
        $('.clickBtn').hide()
        $(".webuploader-pick").hide();
        $("#multiplexApply").show();
        // if(isChild==false||(noChildPower==0&&adminFlag!=1)){
        //     $(".webuploader-pick").hide();
        // }
    } else {
        var flag = 0;
        for (var i = 0; i < chooseFileType.length; i++) {
            if (chooseFileType[i] == "folder") {
                flag = 1;
                break;
            }
        }
        if (flag == "1") {
            $('.clickBtn').hide()
        } else {
            if(style!=2){
                $('.clickBtn').show()
            }else{
                $("#multiplexApply").show();
                $("#delMultiplex").show();
                $("#updateMultiplex").show();
            }
            if(style!=2&&adminFlag==3){
                $("#delComponentBtnAll").hide();
                $("#updateComponentApplyAll").hide();
                $("#delMultiplex").hide();
                $("#updateMultiplex").hide();
            }
            if(adminFlag==5){
                if(chooseState[0]!=0){
                    $("#delComponentBtnAll").hide();
                    $("#updateComponentApplyAll").hide();
                    $("#delMultiplex").hide();
                    $("#updateMultiplex").hide();

                }
             else{
                    var flag2=0;
                    for(var j=0;j<orgList.length;j++){
                        if(orgList[j]==chooseOrg[0]){
                            flag2=1;
                            break;
                        }
                    }
                    if(flag2==0){
                        $("#delComponentBtnAll").hide();
                        $("#updateComponentApplyAll").hide();
                        $("#delMultiplex").hide();
                        $("#updateMultiplex").hide();
                    }else{
                        $("#delComponentBtnAll").show();
                        $("#updateComponentApplyAll").show();
                        $("#delMultiplex").show();
                        $("#updateMultiplex").show();
                    }
                }

            }

            $('#manyMulDownLoad').hide();
            $(".webuploader-pick").hide()
            if (chooseFile.length > 1) {
                $('#updateName').hide();
                $('#mulDownLoad').hide();
                $('#manyMulDownLoad').show();

            }

        }
    }
}


function changeBgColorOfTr(e) {
    var jq = $(e);
    //console.log(e.tagName.toLowerCase());
    if (e.tagName.toLowerCase() != "tr") {
        jq = jq.parents(".hoverEvent");
    }
    jq.parent().find("tr").css("background-color", "#fff");

    jq.css("background-color", "rgba(246, 250, 255, 1)");
}
function clickCheck(e, id,state,orgId) {
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor=[];
    chooseState=[]
    chooseOrg=[]
    var jq = $(e);

    var checkFileId = $(this).siblings(".checkFileId").val();
    var checkFileType = $(this).siblings(".checkFileType").val()
    var checkFileName = $(this).siblings(".checkFileName").val()
    var checkFileAuthor = $(this).siblings(".chooseFileAuthor").val()
    chooseFile.push(id);
    chooseFileType.push(checkFileType);
    chooseFileName.push(checkFileName);
    chooseFileAuthor.push(checkFileAuthor)
    chooseState.push(state)
    chooseOrg.push(orgId)
    changeBgColorOfTr(e);
    btnState();
    cancelBubble()
}
function checkAll(e) {
    var self = $(e).siblings('input');//
    $(e).toggleClass('layui-form-checked');
    if (self.prop("checked") == false) {
        self.prop("checked", true);
    } else {
        self.prop("checked", false);
    }
    if (self.prop("checked")) { // 全选
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", true);
            $(this).siblings('.layui-form-checkbox').addClass("layui-form-checked");
            var checkFileId = $(this).siblings(".checkFileId").val();
            var checkFileType = $(this).siblings(".checkFileType").val()
            var checkFileName = $(this).siblings(".checkFileName").val()
            var checkFileAuthor = $(this).siblings(".chooseFileAuthor").val()
            chooseFile.push(checkFileId);
            chooseFileType.push(checkFileType);
            chooseFileName.push(checkFileName);
            chooseFileAuthor.push(checkFileAuthor)

        });

    }
    else { // 取消全选

        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", false);
            $(this).siblings('.layui-form-checkbox').removeClass("layui-form-checked");
        });
        chooseFileType = [];
        chooseFileName = [];
        chooseFile = [];
        chooseFileAuthor = []
    }
    btnState();
}
Array.prototype.del = function (n) {
    if (n < 0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0, n).concat(this.slice(n + 1, this.length));
}
function emptyChoose() {
    chooseFile = [];
    chooseFileType = [];
    chooseFileName = [];
    chooseFileAuthor = []
}
function clickIconCheck(e, id) {
    $(e).toggleClass('layui-form-checked');
    var checkbox = $(e).siblings('.checkbox');
    if (checkbox.prop("checked") == false) {
        checkbox.prop("checked", true);
        chooseFile.push(id);
    } else {
        checkbox.prop("checked", false);
        if (chooseFile.indexOf(id) != -1) {
            chooseFile = chooseFile.del(chooseFile.indexOf(id));
        }
    }

    btnState();
    cancelBubble()
}
//得到事件
function getEvent() {
    if (window.event) {
        return window.event;
    }
    func = getEvent.caller;
    while (func != null) {
        var arg0 = func.arguments[0];
        if (arg0) {
            if ((arg0.constructor == Event || arg0.constructor == MouseEvent
                || arg0.constructor == KeyboardEvent)
                || (typeof(arg0) == "object" && arg0.preventDefault
                    && arg0.stopPropagation)) {
                return arg0;
            }
        }
        func = func.caller;
    }
    return null;
}
//阻止冒泡
function cancelBubble() {
    var e = getEvent();
    if (window.event) {
        //e.returnValue=false;//阻止自身行为
        e.cancelBubble = true;//阻止冒泡
    } else if (e.preventDefault) {
        //e.preventDefault();//阻止自身行为
        e.stopPropagation();//阻止冒泡
    }
}
function tryPop(th, id, type, name, index, author) {
    if (chooseFile.indexOf(id) == -1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile()
        } else {
            reNameIndex = index;
            reNameParem = name;
        }
    }
    if ($(th).prop("checked")) {
        chooseFile.push(id);
    } else {
        if (chooseFile.indexOf(id) != -1) {
            chooseFile = chooseFile.del(chooseFile.indexOf(id));
        }
    }
    btnState();
    cancelBubble()
}
$(function () {

    setInterval(function () {
        scrollHeight = parent.scrollHeight;
        var height = parseInt(scrollHeight);
        var screenHeight = parseInt(window.screen.availHeight);
        if (scrollHeight != 0) {
            scrollHeightList= parseInt(height - 130 + (screenHeight - 154) / 2.0) + "px";
            scrollHeightAlert = parseInt(height - 130 + (screenHeight - 154) / 2.0) + "px";
            scrollHeightLong = parseInt(height - 130 + (screenHeight - 510) / 2.0) + "px";
            scrollHeightTip = parseInt(height - 130 + (screenHeight - 250) / 2.0) + "px";
            scrollHeightShare = parseInt(height - 130 + (screenHeight - 200) / 2.0) + "px";
            scrollHeightMsg = parseInt(height - 130 + (screenHeight - 64) / 2.0) + "px";
            //console.log(scrollHeightAlert + ' ' + scrollHeightLong)
        }
        //console.log(height + "//" + screenHeight + " " + layerHeight)
        //$(".layui-layer.layui-layer-iframe").css("top",height - 130 + (screenHeight - layerHeight) / 2.0 + "px");
    }, 300);

    $('#searchName').bind('keypress', function (event) {
        if (event.keyCode == "13") {
            $("#searchBtn").click();
        }
    });

})

function projectCheck(userName,componentName,style){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.open({
            type: 2,
            area: [ '920px',  '540px'],
            fix: true, //不固定
            shadeClose: true,

            offset:scrollHeightLong,
            moveOut: false,
            shade: 0.4,
            title: componentName+"的成果",
            content: Hussar.ctxPath+"/component/componentPersonage?userName=" + userName+"&style"+style
        });
    });
}
function viewReason(componentId){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.open({
            type: 2,
            area: [ '660px',  '355px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            offset:scrollHeightLong,
            moveOut: true,
            shade: 0.4,
            title: "驳回意见",
            content: Hussar.ctxPath+"/multiplex/viewReason?componentId=" + componentId
        });
    });
}
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").css("top", scrollHeightAlert);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    }, 2000)
}
/**
 * 获取成果复用情况
 */

