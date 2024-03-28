var index = 0 ;
var idArr=[];
var nameArr=[];
var typeArr=[];
var projectId=$("#projectId").val();
var projectName= $("#projectName").val();
var projectDept=$("#mp").val();
var componentIds=[];
var componentNames=[];
var componentTypes=[];
var componentUsers=[];

var economizeStr=[];
layui.use(['upload','form','Hussar','HussarAjax'] ,function() {

    var form = layui.form;
    var $ = layui.jquery
        , upload = layui.upload;
    var Hussar = layui.Hussar,
        $ax = layui.HussarAjax;


    $(function () {


          /*
*/


        setInterval(function () {
            scrollHeight = parent.scrollHeight;
            var height = parseInt(scrollHeight);
            var screenHeight = parseInt(window.screen.availHeight);
            if (scrollHeight != 0) {

                scrollHeightLong = parseInt(height - 130 + (screenHeight - 510) / 2.0) + "px";
                if ((screen.width >= 1920) && (screen.height >= 1080)) {
                    scrollHeightTip = parseInt(height - 130 + (screenHeight - 650) / 2.0) + "px";
                } else if ((screen.width <= 1366) && (screen.height <= 768)) {
                    scrollHeightTip = parseInt(height - 130 + (screenHeight - 430) / 2.0) + "px";
                } else {
                    scrollHeightTip = parseInt(height - 130 + (screenHeight - 580) / 2.0) + "px";
                }
                scrollHeightShare = parseInt(height - 130 + (screenHeight - 200) / 2.0) + "px";
                scrollHeightMsg = parseInt(height - 130 + (screenHeight - 64) / 2.0) + "px";
                //console.log(scrollHeightAlert + ' ' + scrollHeightLong)
            }
            //$(".layui-layer.layui-layer-iframe").css("top",height - 130 + (screenHeight - layerHeight) / 2.0 + "px");
        }, 300);
        //console.log(height + "//" + screenHeight + " " + layerHeight)



    })



//普通图片上传
    $("#changeDoc").on('click',function(){
        layer.open({
            type: 2,
            area: [ '760px',  '450px'],
            fix: true, //不固定
            shadeClose: true,

            offset:scrollHeightTip,
            maxmin: false,
            shadeClose: true,
            moveOut: true,
            shade: 0.4,
            title: "选择项目",
            content: Hussar.ctxPath+"/multiplex/projectListView"
        });
    })

    $("#selectComponent").on('click',function(){
        var adminFlag=$("#adminFlag").val();
        layer.open({
            type: 2,
            area: [ '760px',  '450px'],
            fix: true, //不固定
            shadeClose: true,

            offset:scrollHeightTip,
            maxmin: false,
            shadeClose: true,
            moveOut: true,
            shade: 0.4,
            title: "选择成果",
            content: Hussar.ctxPath+"/multiplex/selectComponentView?adminFlag="+adminFlag
        });
    })
    /*新增/编辑专题*/
    $("#saveBtn").on('click',function(){
        var index = $("#componentList tr").length;
        for(var i=0;i<index;i++){
            var economize=$(".economize").eq(i).val();
            economizeStr.push(economize)
        }


        var multiplexDesc = $("#multiplexDesc").val().trim();
        var economizeStr2=economizeStr.join(",");
        var componentIdStr =  componentIds.join(",");//作者id
        var componentNameStr = componentNames.join(",");
        var componentTypeStr = componentTypes.join(",");
        var componentUserStr = componentUsers.join(",");
        if(projectName == ""){
            layer.msg("请先选择项目", {anim:6,icon: 0,offset:scrollHeightMsg, fix: true});
            $("#saveBtn").attr("disabled", false);
            return;
        }



        if(multiplexDesc.length > 1000){
            layer.msg("复用需求描述不能超过1000个字", {anim:6,icon: 0,offset:scrollHeightMsg, fix: true});
            $("#saveBtn").attr("disabled", false);
            return;
        }

        if(componentIds==""){
            layer.msg("请先选择成果", {anim:6,icon: 0,offset:scrollHeightMsg, fix: true});
            $("#saveBtn").attr("disabled", false);
            return;
        }
        $("#saveBtn").attr("disabled", true);
        var url;//请求地址
        var successMsg,errorMsg;//成功失败提示
        url = "/multiplex/multiplexSaveUpdate";
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
                componentUsers:componentUserStr,
                economizeStr:economizeStr2
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
                        offset:scrollHeightLong,
                        end: function () {

                            window.parent.open(Hussar.ctxPath+"/frontComponent/myList?style=2","mainFrame");
                        }
                    },function(){
                        window.parent.open(Hussar.ctxPath+"/frontComponent/myList?style=2","mainFrame");
                    });
                }else{
                     $("#saveBtn").attr("disabled", false);
                    layer.alert(errorMsg, {
                        icon :  2,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        offset:scrollHeightLong

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
                    // offset:scrollHeightLong,
                    end: function () {

                        window.parent.open(Hussar.ctxPath+"/frontComponent/myList?style=2","mainFrame");
                    }
                },function(){
                    window.parent.open(Hussar.ctxPath+"/frontComponent/myList?style=2","mainFrame");
                });
            }else{
                $("#saveBtn").attr("disabled", false);
                layer.alert(errorMsg, {
                    icon :  2,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    // offset:scrollHeightLong

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
        ajax.set("economizeStr",economizeStr2);
        ajax.start();
    });
})
$("#cancel").on('click',function(){
    var url="/frontComponent/myList?style=2";
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
    var adminFlag=$("#adminFlag").val();
    var index = $("#componentList tr").length;
    economizeStr=[];
    for(var i=0;i<index;i++){
        var economize=$(".economize").eq(i).val();
        economizeStr.push(economize)
    }
    var tr = $(e).parent();
    //cancelBubble();
 var index = $("#componentList tr").index(tr)
    componentIds.splice(index,1);
    componentNames.splice(index,1);
    componentTypes.splice(index,1);
    componentUsers.splice(index,1);
    economizeStr.splice(index,1);
    var componentList= $(document.getElementById("componentList"));
    componentList.html("");
    for(var i=0; i<componentIds.length;i++){
        var type = "";
        if(componentTypes[i]==0){
            type="技术组件"
        }else{
            type="解决方案"
        }
        var str;
        if(adminFlag==7){
            str="<td class='box'>" +
                "<div class='main'>"+
                "<div class='left' onclick='leftNum(this)'>" +'-'+"</div>"+
                "<div class='center' >"+
                "<input  type='text' class='economize' value='"+economizeStr[i]+"'  maxlength='3'>"+
                "</div>"+
                "<div class='right' onclick='rightNum(this)'>"+'+'+"</div>" +
                "</div>"+
                "</td>"
        }
        var innerHtml="  <tr>" +
            "<td>"+(i+1)+"</td>" +
            "<td>"+type+"</td>" +
            "<td style='text-align: left!important;padding-left: 10px'>"+componentNames[i]+"</td>" +
            "<td>"+componentUsers[i]+"</td>" +
            "<td  style='cursor: pointer;' onclick='deleteThis(this,"+i+")'><button type='button'style='background-color: #fff0f0;cursor: pointer;width: 40px;height: 24px;line-height:24px;border:1px solid #fac4c4;border-radius:2px;color: #f56b6b !important;font-size: 12px;'>" +
            " 删除</button></td>" +
            str

            "</tr>"
        componentList.append(innerHtml)
    }


}


function leftNum(e) {

    var index=$(".left").index(e)

    var center =$(".economize").eq(index).val();

    if (center > 0) {
        center--;       //自减

        $(".economize").eq(index).val(center);  //中间显示自减后的数值xx
    } else {

        layer.msg("不可小于0", {anim:6,icon: 0,offset:scrollHeightMsg, fix: true});
    }

}

function rightNum(e) {
    var index=$(".right").index(e)
    var center = $(".economize").eq(index).val();
    if (center <999) {
        center++  //自加
   $(".economize").eq(index).val(center);
    } else {
        layer.msg("不可大于999", {anim:6,icon: 0,offset:scrollHeightMsg, fix: true});

    }
}
$(function(){
    $(".listId").each(function(){
        var index = $(".listId").index($(this));
        var type=$(".listType").eq(index).val()
        var name = $(".listName").eq(index).val();
        var componentName = $(".componentName").eq(index).val();

        componentNames.push(componentName);
     componentTypes.push(type)
       componentUsers.push(name)

        componentIds.push($(this).val());


    });


});

