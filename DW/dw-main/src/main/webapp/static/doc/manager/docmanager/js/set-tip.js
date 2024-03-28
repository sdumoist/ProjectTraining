
var zTree_Menu = null;//ztree对象
var util;//工具
var layerView;
var topicId =$("#id").val();
var topicPic;
var count;
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
        upload = layui.upload,
        laydate = layui.laydate,
        util = layui.util;
    //日期

    //.存储当前页数据集
    var pageData = [];
    //.存储已选择数据集，用普通变量存储也行
    //layui.data('checked', null);
    layui.data("childChecked",null);
    //初始化表格
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    /*新增/编辑专题*/
    $("#saveBtn").on('click',function(){

        var tip=  $("#tip").val();
        var  tipArray=tip.split(",");
        if(tipArray.length>10){
            layer.msg("标签不能超过10个", {anim:6,icon: 0});
            return;
        }
        if(tip.length>100){
            layer.msg("标签不能超过100字", {anim:6,icon: 0});
            return;
        }

        if(tip!=""){
            if(tip.indexOf("!")!=-1||tip.indexOf(".")!=-1||tip.indexOf("。")!=-1||tip.indexOf("?")!=-1||tip.indexOf("@")!=-1||tip.indexOf("#")!=-1
                ||tip.indexOf("%")!=-1||tip.indexOf("^")!=-1||tip.indexOf("(")!=-1||tip.indexOf(")")!=-1||tip.indexOf("+")!=-1||tip.indexOf("-")!=-1
                ||tip.indexOf("‘")!=-1||tip.indexOf("'")!=-1||tip.indexOf("“")!=-1||tip.indexOf("/")!=-1||tip.indexOf("|")!=-1||tip.indexOf("*")!=-1
                ||tip.indexOf("，")!=-1||tip.indexOf(" ")!=-1){
                layer.msg("标签请以英文逗号进行分割且不能含特殊字符", {anim:6,icon: 0});
                return;
            }
            for (i=0;i<tipArray.length ;i++ )
            {
                if(tipArray[i].length==0){
                    layer.msg("不允许存在空标签", {anim:6,icon: 0});
                    return;
                }
                if(tipArray[i].length>10){
                    layer.msg("每个标签不能超过10个字", {anim:6,icon: 0});
                    return;
                }

            }

            if(isRepeat(tipArray)){
                layer.msg("不允许存在重复标签", {anim:6,icon: 0});
                return
            }}


        var url;//请求地址
        var successMsg,errorMsg;//成功失败提示
        var addflag=0;
        for(var i=0;i<parent.chooseFile.length;i++){
            //var categoryName = parent.chooseFileName[i];
            var fileId = parent.chooseFile[i];
            // var authorId = parent.chooseFileAuthor[i];

            /** 调用权限的更新方法 */
            /*$.ajax({
                type: "POST",
                url: Hussar.ctxPath+"/fsFile/addtip",
                data : {
                    docId:fileId,
                    tip:tip
                },
                contentType:"application/x-www-form-urlencoded",
                dataType:"json",
                async: false,
                success:function(result) {
                    if(result){

                    }else{
                        addflag=1;
                    }
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/addtip", function(result) {
                if(result){

                }else{
                    addflag=1;
                }
            }, function(data) {

            });
            ajax.set("docId",fileId);
            ajax.set("tip",tip);
            ajax.start();
        }
        if(addflag==0){
            parent.layer.msg("设置标签成功", {icon: 1,offset:parent.scrollHeightMsg});
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
            parent.editFlag = false;
            parent.refreshFile(parent.openFileId);
            //parent.refreshTree();
        }else {
            parent.layer.msg("设置标签失败", {icon: 0});
        }


    });

    function isRepeat(arr) {

        var nary = arr.sort();
        for(var i = 0; i < nary.length - 1; i++)
        {
            if (nary[i] == nary[i+1])
            {
                return true;
            }
        }
        return false;
    }
    $(function () {
        getTip();
        getDic();
        $('.name-item').on('click', function(){
            var tip=$(this).text();
            var oldtip= $("#tip").val();
            var  tipArray=oldtip.split(",");
            if(tipArray.length>9){
                layer.msg("标签不能超过10个", {anim:6,icon: 0});
                return;
            }
            for (i=0;i<tipArray.length ;i++ )
            {
                if(tipArray[i]==tip){
                    layer.msg("不允许存在重复标签", {anim:6,icon: 0});
                    return;
                }
            }
            if(oldtip==""){
                $("#tip").val(tip);
            }
            else if(oldtip.substr(-1)==','){
                $("#tip").val(oldtip+""+tip);
            }else{
                $("#tip").val(oldtip+","+tip);
            }
        });
    });



    function  getDic() {
        var flag=0;
        for(var i=0;i<parent.chooseFile.length;i++){
            var fileType = parent.chooseFileType[i]
            if(fileType!=".png"&&fileType!=".jpg"&&fileType!=".gif"&&fileType!=".bmp"&&fileType!=".jpeg"){
                flag=1;
                break;
            }
        }
        if(flag==1){
            $("#color_inline").hide();
            $("#type_inline").hide();
        }
        var ajax = new $ax(Hussar.ctxPath+"/dicList",function(result) {

                for (var i = 0; i < result.length; i++) {
                    var param = '<div class="name-item" >' +
                        '<p>' +result[i].LABEL+ '</p>' +
                        '</div>'
                    $("#industry_type").append(param);
                }
                form.render();
            },
            function(data) {
                Hussar.error("加载行业类型失败！");
            });
        ajax.set("dicType","industry_type");
        ajax.start();
        var ajax = new $ax(Hussar.ctxPath+"/dicList",function(result) {

                for (var i = 0; i < result.length; i++) {
                    var param = '<div class="name-item" >' +
                        '<p>' +result[i].LABEL+ '</p>' +
                        '</div>'
                    $("#type").append(param);
                }
                form.render();
            },
            function(data) {
                Hussar.error("加载行业类型失败！");
            });
        ajax.set("dicType","tag_type");
        ajax.start();
        var ajax = new $ax(Hussar.ctxPath+"/dicList",function(result) {

                for (var i = 0; i < result.length; i++) {
                    var param = '<div class="name-item" >' +
                        '<p>' +result[i].LABEL+ '</p>' +
                        '</div>'
                    $("#color").append(param);
                }
                form.render();
            },
            function(data) {
                Hussar.error("加载行业类型失败！");
            });
        ajax.set("dicType","color");
        ajax.start();
    }


    function  getTip() {

        if( parent.chooseFile.length>1){
            return;
        }  else{
            var fileId=parent.chooseFile[0];
            /*$.ajax({
                type: "POST",
                url: Hussar.ctxPath+"/fsFile/gettip",
                data : {
                    docId:fileId,
                },
                contentType:"application/x-www-form-urlencoded",
                dataType:"json",
                async: false,
                success:function(result) {
                    if(result){
                        $("#tip").val(result.tip)
                    }else{
                    }
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/gettip", function(result) {
                if(result){
                    $("#tip").val(result.tip)
                }else{
                }
            }, function(data) {

            });
            ajax.set("docId",fileId);
            ajax.start();
        }
    }
});
