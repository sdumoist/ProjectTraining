var zTree_Menu = null;//ztree对象
var util;//工具
var layerView;
var topicId = null;//专题ID
var topicPic;
var treeData;
layui.use(['form', 'jquery','util','layer','table','Hussar','jstree','HussarAjax','element','laydate','upload'], function() {
    var form = layui.form,
        $ = layui.jquery,
        table = layui.table,
        layer = layui.layer,
        Hussar = layui.Hussar,
        jstree=layui.jstree,
        $ax=layui.HussarAjax,
        element = layui.element,
        laydate = layui.laydate,
        upload = layui.upload,
        util = layui.util;
    //日期
    start();
    $(function () {
        form.on('switch(topicShow)', function (data) {

            if(this.checked==true){
                $("#startTime").val(getNowFormatDate);

            }else{
                $("#startTime").val("");
            }

        })
    })
    laydate.render({
        elem: '#startTime'
        ,type: 'datetime'
    });
    laydate.render({
        elem: '#endTime'
        ,type: 'datetime'
    });
    //普通图片上传
    var uploadInst = upload.render({
        elem: '#choosePic'
        ,url: Hussar.ctxPath+'/topic/upload'
        ,before: function(obj){
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                $('#pic').attr('src', result).addClass("show_img");
                if(!($(".img-div").hasClass("imgs"))){
                    $(".img-div").addClass("imgs")
                }
                $("#saveBtn").prop("disabled",true).addClass('layui-btn-disabled');
            });
        }
        ,done: function(res, index, upload){
            //如果上传失败
            if(res.fName ==""||res.fName==undefined){
                topicPic="";
                return layer.msg('上传失败');
            } else {
                topicPic = res.fName;
                $("#saveBtn").prop("disabled",false).removeClass('layui-btn-disabled');
            }
        }
        ,accept: 'images'//允许上传的文件类型
    });
    var uploadInst = upload.render({
        elem: '#choosePic2'
        ,url: Hussar.ctxPath+'/topic/upload'
        ,before: function(obj){
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                $('#pic').attr('src', result).addClass("show_img");
                if(!($(".img-div").hasClass("imgs"))){
                    $(".img-div").addClass("imgs")
                }
                $("#saveBtn").prop("disabled",true).addClass('layui-btn-disabled');
            });
        }
        ,done: function(res, index, upload){
            //如果上传失败
            if(res.fName ==""||res.fName==undefined){
                topicPic="";
                return layer.msg('上传失败');
            } else {
                topicPic = res.fName;
                $("#saveBtn").prop("disabled",false).removeClass('layui-btn-disabled');
            }
        }
        ,accept: 'images'//允许上传的文件类型
    });
//        $("#startTime").val(getNowFormatDate);
    layui.data("childChecked",null);
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    $("#authorName").click(function(){

         // 先让其他input失去焦点
        $("input").blur();
        var authName=$("#authorName").val().trim();
        layerView=layer.open({
            type: 1,
            area: ['400px','350px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: "专题作者",
            content: $("#employeeTreeDiv"),
            success:function(){
                initEmployeeTree(treeData,authName);
            }
        });
    });
    /*新增/编辑专题*/
    $("#saveBtn").on('click',function(){
        var topicName = $("#topicName").val().trim();//专题名称
        var topicDesc = $("#topicDesc").val().trim();//专题描述
        var authorName = $("#authorName").val().trim();//作者名字
        var authorId = $("#authorId").val().trim();//作者id

        var topicShow = 0;
        if(topicPic == ""||topicPic == undefined || topicPic == null){
            layer.msg("专题封面不能为空", {anim:6,icon: 0});
            return;
        }
        if(topicName == ""){
            layer.msg("专题名称不能为空", {anim:6,icon: 0});
            return;
        }
        if(topicName.length > 8){
            layer.msg("专题名称不能超过8个字", {anim:6,icon: 0});
            return;
        }
        if(!new RegExp("^[^/\\\\:\\*\\?\\<\\>\\|\"]{1,255}$").test(topicName)){
            layer.msg("专题名称不能有特殊字符", {anim:6,icon: 0});
            return
        }
        if(authorName == ""){
            layer.msg("专题作者不能为空", {anim:6,icon: 0});
            return;
        }

        if(topicDesc.length > 200){
            layer.msg("专题描述不能超过200个字", {anim:6,icon: 0});
            return;
        }
        var docIds = '';//关联的文档ID拼接字符串
        $("tbody").children('tr').each(function(i){
            var a = $(this).children();//获取每一行
            if(i == 0){
                docIds += a[0].innerText;
            }else{
                docIds += ","+a[0].innerText;
            }
        });
        var url;//请求地址
        var successMsg,errorMsg;//成功失败提示
        url = "/topic/addTopic";
        successMsg = "新增成功";
        errorMsg = "新增失败"
        /*$.ajax({
            type:"post",
            url:url,
            data:{
                id:topicId,
                topicName:topicName,
                topicDesc:topicDesc,
                topicShow:topicShow,
                authorId:authorId,

                docIds:docIds,
                topicCover:topicPic
            },
            async:false,
            cache:false,
            dataType:"json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success:function(data){
                if(data.result == "0"){
                    layer.alert('该专题已存在', {
                        icon :  0,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    });
                }else if(data.result == "1"){
                    layer.alert(successMsg, {
                        icon :  1,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        end: function () {
                            parent.tableIns.reload({
                                where: {
                                    //防止IE浏览器第一次请求后从缓存读取数据
                                    timestamp: (new Date()).valueOf()
                                }
                            });
                            layui.data('checked',null);
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                        }
                    },function(){
                        parent.tableIns.reload({
                            where: {
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            }
                        });
                        layui.data('checked',null);
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                    });
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
            if(data.result == "0"){
                layer.alert('该专题已存在', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
            }else if(data.result == "1"){
                layer.alert(successMsg, {
                    icon :  1,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    end: function () {
                        parent.tableIns.reload({
                            where: {
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            }
                        });
                        layui.data('checked',null);
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                    }
                },function(){
                    parent.tableIns.reload({
                        where: {
                            //防止IE浏览器第一次请求后从缓存读取数据
                            timestamp: (new Date()).valueOf()
                        }
                    });
                    layui.data('checked',null);
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index);
                });
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
        ajax.set("id",topicId);
        ajax.set("topicName",topicName);
        ajax.set("topicDesc",topicDesc);
        ajax.set("topicShow",topicShow);
        ajax.set("authorId",authorId);
        ajax.set("docIds",docIds);
        ajax.set("topicCover",topicPic);
        ajax.start();


    });
    var active = {
        addDoc:function(){
            var nodes = zTree_Menu.getCheckedNodes(true);
            var idArr = '';
            for(var i = 0;i < nodes.length;i++){
                if(nodes[i].code == undefined){
                    if(idArr == ''){
                        idArr += nodes[i].id
                    }else{
                        idArr += (","+nodes[i].id)
                    }
                }
            }

        }
    };
    //.渲染完成回调
    $('.layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    function initEmployeeTree(data,authName){
        var $tree = $("#showEmployeeTree");
        //用户树查询
        var searchId = "personSearch";
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
                "1":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/com.png"},
                "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/station.png"},
                "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/empl.png"},
                "USER":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/user.png"}
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
        });
        $tree.on('ready.jstree', function(event, obj) {
            if(authName!=null && authName!=""){
                $("#"+searchId).val(authName);
                var e = $.Event("keyup");//模拟一个键盘事件
                e.keyCode = 13;//keyCode=13是回车
                $("#"+searchId).trigger(e);//模拟页码框按下回车
            }
        });
        $("#"+searchId).keyup(function () {
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
        });
    }


$(".laydata-input").on("focus",function () {
    $(this).blur()
})
/**
 * 所有树的模糊查询
 */
function treeSearch(treeId,searchId,username){
    $("#"+searchId).val("");
    $(".jstree-search").remove();
    $(".search-results").html("");
}

function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var min = date.getMinutes();
    var sec =date.getSeconds();
    if (min >= 1 && min <= 9) {
        min = "0" + min;
    }
    if (sec >= 1 && sec <= 9) {
        sec = "0" + sec;
    }
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
        + " " + date.getHours() + seperator2 + min
        + seperator2 + sec;
    return currentdate;
}

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
function start() {
    getUsers();
}});