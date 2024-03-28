var index = 0 ;
var idArr=[];
var nameArr=[];
var typeArr=[];
var projectId="";
var projectName= '';
var projectDept='';
var componentIds=[];
var componentNames=[];
var componentTypes=[];
var componentUsers=[];
layui.use(['upload','form','Hussar','HussarAjax'] ,function() {

    var form = layui.form;
    var $ = layui.jquery
        , upload = layui.upload;
    var Hussar = layui.Hussar,
        $ax = layui.HussarAjax;
    var componentId =$("#componentId").val();
    //普通图片上传

    $("#changeDoc").on('click',function(){
        layer.open({
            type: 2,
            area: [ '760px',  '450px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            moveOut: true,
            shade: 0.4,
            title: "选择项目",
            content: Hussar.ctxPath+"/multiplex/projectListViewBackground"
        });
    })

    $("#selectComponent").on('click',function(){
        layer.open({
            type: 2,
            area: [ '760px',  '450px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            moveOut: true,
            shade: 0.4,
            title: "选择成果",
            content: Hussar.ctxPath+"/multiplex/selectComponentViewBackground"
        });
    })
    /*新增/编辑专题*/
    $("#saveBtn").on('click',function(){

        var multiplexDesc = $("#multiplexDesc").val().trim();//作者名字
        var componentIdStr =  componentIds.join(",");//作者id
        var componentNameStr = componentNames.join(",");
        var componentTypeStr = componentTypes.join(",");
        var componentUserStr = componentUsers.join(",");
        if(projectName == ""){
            layer.msg("请先选择项目", {anim:6,icon: 0});
            return;
        }



        if(multiplexDesc.length > 1000){
            layer.msg("复用需求描述不能超过1000个字", {anim:6,icon: 0});
            return;
        }
        if(componentIds==""){
            layer.msg("请先选择成果", {anim:6,icon: 0});
            return;
        }
        var url;//请求地址
        var successMsg,errorMsg;//成功失败提示
        url = "/multiplex/multiplexSave";
        successMsg = "登记成功";
        errorMsg = "登记失败"
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+url,
            data:{
                projectId:projectId,
                projectName:projectName,
                multiplexDesc:multiplexDesc,
                projectDept:projectDept,
                componentIdStr:componentIdStr,
                componentUserStr:componentUserStr,
                componentNameStr:componentNameStr,
                componentUsers:componentUserStr
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                 if(data.result == "1"){
                    layer.alert(successMsg, {
                        icon :  1,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        end: function () {
                            window.parent.open(Hussar.ctxPath+"/frontComponent/myList?style=1","mainFrame");
                        }
                    },function(){
                        window.parent.open(Hussar.ctxPath+"/frontComponent/myList?style=1","mainFrame");
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
            if(data.result == "1"){
                layer.alert(successMsg, {
                    icon :  1,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    end: function () {
                        window.parent.open(Hussar.ctxPath+"/frontComponent/myList?style=1","mainFrame");
                    }
                },function(){
                    window.parent.open(Hussar.ctxPath+"/frontComponent/myList?style=1","mainFrame");
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
        ajax.set("projectId",projectId);
        ajax.set("projectName",projectName);
        ajax.set("multiplexDesc",multiplexDesc);
        ajax.set("projectDept",projectDept);
        ajax.set("componentIdStr",componentIdStr);
        ajax.set("componentUserStr",componentUserStr);
        ajax.set("componentNameStr",componentNameStr);
        ajax.set("componentUsers",componentUserStr);
        ajax.start();
    });
})
$("#cancel").on('click',function(){
    var url="/frontComponent/myList?style=1";
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        window.location.href=encodeURI(Hussar.ctxPath+url);
    });
})
function download(id){
    //cancelBubble();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew?docIds="+id,
            type : "post",
            async:false,

        });
    });


    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}
function deleteThis(e,i){
    var tr = $(e).parent();
    //cancelBubble();
 var index = $("#componentList tr").index(tr)
    componentIds.splice(index,1);
    componentNames.splice(index,1);
    componentTypes.splice(index,1);
    componentUsers.splice(index,1);
    var componentList= $(document.getElementById("componentList"));
    componentList.html("");
    for(var i=0; i<componentIds.length;i++){
        var type = "";
        if(componentTypes[i]==0){
            type="技术组件"
        }else{
            type="解决方案"
        }
        var innerHtml="  <tr>" +
            "<td>"+(i+1)+"</td>" +
            "<td>"+type+"</td>" +
            "<td style='text-align: left!important;padding-left: 10px'>"+componentNames[i]+"</td>" +
            "<td>"+componentUsers[i]+"</td>" +
            "<td  style='cursor: pointer;' onclick='deleteThis(this,"+i+")'><button type='button'style='background-color: #fff0f0;cursor: pointer;width: 40px;height: 24px;line-height:24px;border:1px solid #fac4c4;border-radius:2px;color: #f56b6b !important;font-size: 12px;'>" +
            " 删除</button></td>" +
            "</tr>"
        componentList.append(innerHtml)
    }
    $(function(){



    })
};