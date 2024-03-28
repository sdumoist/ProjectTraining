var index = 0;
var idArr = [];
var nameArr = [];
var typeArr = [];
var load = ""
layui.use(['upload', 'layedit', 'HussarAjax','form', 'jquery','util','layer','table','Hussar','jstree','element','laydate'], function () {
    var form = layui.form,

        table = layui.table,
        layer = layui.layer,

        jstree=layui.jstree,

        element = layui.element,

        laydate = layui.laydate,
        util = layui.util;
    var Hussar = layui.Hussar;
    var $ax = layui.HussarAjax;

    var $ = layui.jquery
        , upload = layui.upload;
    var componentId = $("#componentId").val();
    var layedit = layui.layedit;
    var indexLay = layedit.build('componentDesc', {
        height: 210 //设置编辑器高度
    }); //建立编辑器
    //普通图片上传



    var ue = UE.getEditor('editor',{autoHeightEnabled: false,catchRemoteImageEnable:false})
    UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
    UE.Editor.prototype.getActionUrl = function(action) {
        if (action == 'uploadimage' || action == 'uploadscrawl' || action == 'uploadimage') {
            return '/ueditor/imageupload';//此处写自定义的图片上传路径
        } else if (action == 'uploadvideo') {
            return 'http://a.b.com/video.php';
        } else {
            return this._bkGetActionUrl.call(this, action);
        }
    }
    ue.addListener('beforepaste', myEditor_paste);
    function myEditor_paste(o, html) {
        html.html=html.html.replace(/<img[^>]*>/i,'');
    }
    $("#cancel").on('click', function () {
        var style=$("#style").val();
        var type=$("#type").val();
        var origin=$("#origin").val();
        var state=$("#state").val();
        var url =""
        var dept=$("#dept").val();

        if(style!=null&&style!=""){
            url=   "/frontComponent/myList?style="+style+"&type="+type+"&origin="+origin+"&state="+state
        }else{
            url=   "/frontComponent/myList?type="+type+"&origin="+origin+"&state="+state+"&dept="+dept
        }

        window.location.href = encodeURI(Hussar.ctxPath+url);
    })
    function download(id) {
        //cancelBubble();
        $.ajaxFileUpload({
            url: Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
            type: "post",
            async: false,

        });
        /*
         * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
         * docName:name, fileIds:id, } });
         */
    }

    // $("#selectAttachment").on('click', function () {
    //     var errorFlag = false
    //     $("#fileList tr").find("td:eq(1) select").each(function () {
    //         if ($(this).val() == "") {
    //             layer.msg("请先选择文件类型", {anim: 6, icon: 0});
    //             errorFlag = true;
    //         }
    //     });
    //     if (errorFlag == true) {
    //         return;
    //     }
    //     //    var type=$("#fileType").val();
    //     // var    typeText="";
    //     //    var    idText="";
    //     //    var deleteId=""
    //     var index = $("#fileList tr").length + 1;
    //     if (index > 5) {
    //         layer.msg("最多上传5个文件", {anim: 6, icon: 0});
    //         return
    //     }
    //
    //     var indexHtml = "<tr><td style='text-align: center'>" + index + "</td> <td style='text-align: center'>     <select style='margin: 0 10px;width: 110px;' class=\" fileType layui-select\"  name=\"fileType\" lay-filter=\"fileType\">\n" +
    //         "                            <option value=\"\">请选择文件类型</option>\n" +
    //         "                            <option value=\"0\">代码</option>\n" +
    //         "                            <option value=\"1\">配置说明</option>\n" +
    //         "                            <option value=\"2\">使用说明</option>\n" +
    //         "                            <option value=\"3\">demo</option>\n" +
    //         "                            <option value=\"4\">其他</option>\n" +
    //         "                        </select></td>  <td><span class='titleSpan'></span></td>  <td style='text-align: center'>未上传</td>" +
    //         " <td style='cursor: pointer;color: #3C91FD;text-align: center;'>" +
    //         "<button type='button' class='layui-btn component' style='background-color:  #ECF5FF;width: 40px;height: 24px;line-height:24px;border:1px solid #bcdcff;color: #66b1ff !important;cursor: pointer' id=''>上传</button>" +
    //         "  <button type='button' style='display: none;background-color:  #fff0f0;width: 40px;height: 24px;line-height:24px;border:1px solid #fac4c4;color: #f56b6b !important;cursor: pointer' class='layui-btn del componentDelete' onclick='deleteComponent()' id=''>删除" +
    //         " </button>" +
    //         " </td>" +
    //         "</tr>"
    //     $("#fileList").append(indexHtml)
    //     var indexArr = [];
    //     $("#fileList tr:not(':last')").find("td:eq(1)").each(function () {
    //         var index = $(this).html()
    //         indexArr.push(index);
    //
    //     });
    //     $("#fileList tr:last-child").find("td:eq(1) select option").each(function () {
    //         for (var i = 0; i < indexArr.length; i++) {
    //             if (indexArr[i] == $(this).html()) {
    //                 $(this.remove())
    //             }
    //         }
    //     });
    //     $(".componentDelete").click(function () {
    //         var thisNode = $(this);
    //         var fileName = thisNode.parent().parent().find("td:eq(2) span").html();
    //         var index = nameArr.indexOf(fileName);//要删除的文件所在数组的下标
    //
    //         var scopeId = idArr[index];
    //
    //
    //                     thisNode.parent().parent().find("td:eq(2) span").html("");
    //         thisNode.parent().parent().find("td:eq(2)").attr("title","");
    //                     thisNode.parent().parent().find("td:eq(3)").html("未上传");
    //                     thisNode.parent().find("button:eq(0)").show();
    //                     thisNode.hide();
    //
    //                 if (idArr.length != 0) {
    //                     var indexID = idArr.indexOf(scopeId);//要删除的文件所在数组的下标
    //                     if (indexID != -1) {
    //                         idArr.splice(indexID, 1);//删除文件
    //                         typeArr.splice(indexID, 1);//删除文件
    //                         nameArr.splice(indexID, 1);//删除文件
    //                     }
    //
    //                 }
    //
    //
    //
    //     })
    //
    //
    //     $(".fileType").change(function () {
    //
    //
    //
    //
    //         /*            btnState();
    //          // refreshTree();
    //          refreshFile(openFileId);
    //          emptyChoose();
    //          layer.close(index);*/
    //         var type = $(this).children('option:selected').val();
    //         var html = $(this).children('option:selected').html();
    //         var indexL = $(this).parent().parent().find("td:eq(0)").html();
    //
    //
    //         var idText = "";
    //         var deleteId = ""
    //         if (type == "") {
    //             $(this).parent().parent().find("td:eq(4)").find("button:eq(0)").attr("id", "")
    //             $(this).parent().parent().find("td:eq(4)").find("button:eq(1)").attr("id", "")
    //         }
    //         else {
    //
    //             $(this).parent().parent().find("td:eq(4)").find("button:eq(0)").attr("id", indexL+type)
    //             $(this).parent().parent().find("td:eq(4)").find("button:eq(1)").attr("id", indexL+type+"delete")
    //
    //             if(type!=""){
    //                 renderUpload(indexL+type,type)
    //                 $(this).parent().parent().find("td:eq(1)").html(html)
    //             }
    //
    //
    //
    //         }
    //
    //
    //     });
    // })



    $(function(){
        var list = ['10','21','32','43','54']
        for(var i=0;i<list.length;i++){
            var type=list[i].substring(1,list[i].length)
            renderUpload(list[i],type)
        }
    })
        $(".componentDelete").click(function () {
            var thisNode = $(this);
            var fileName = thisNode.parent().parent().find("td:eq(2) span").html();
            var index = nameArr.indexOf(fileName);//要删除的文件所在数组的下标

            var scopeId = idArr[index];


                        thisNode.parent().parent().find("td:eq(2) span").html("");
            thisNode.parent().parent().find("td:eq(2)").attr("title","");
                        thisNode.parent().parent().find("td:eq(3)").html("未上传");
                        thisNode.parent().find("button:eq(0)").show();
                        thisNode.hide();

                    if (idArr.length != 0) {
                        var indexID = idArr.indexOf(scopeId);//要删除的文件所在数组的下标
                        if (indexID != -1) {
                            idArr.splice(indexID, 1);//删除文件
                            typeArr.splice(indexID, 1);//删除文件
                            nameArr.splice(indexID, 1);//删除文件
                        }

                    }



        })

    /*新增/编辑专题*/
    $("#saveBtn").on('click', function () {
        var tip=  $("#tip").val();
        var  tipArray=tip.split(",");

        var componentId = $("#componentId").val().trim();//专题名称
        var componentName = $("#componentName").val().trim();//专题名称
        var componentDesc = ue.getContent();//作者名字
        var tags = $("#tip").val().trim();//专题名称

        var componentDescText = ue.getContentTxt().trim();//作者名字
        var componentType = "0";
        var componentOrigin = "0";//项目名称
        $("#componentType .layui-form-radio").each(function (index) {
            if ($(this).hasClass("layui-form-radioed")) {
                componentType = index;
            }
        })
        $("#componentOrigin .layui-form-radio").each(function (index) {
            if ($(this).hasClass("layui-form-radioed")) {
                componentOrigin = index;
            }
        })


        var componentRange = $("#componentRange").val();//应用场景

        var idArrStr = idArr.join(",");//作者id
        var nameArrStr = nameArr.join(",");
        var typeArrStr = typeArr.join(",");

        /*if(typeArrStr.indexOf("0")==-1){
         layer.msg("必须上传代码", {anim:6,icon: 0});
         return;
         }
         if(typeArrStr.indexOf("1")==-1&&typeArrStr.indexOf("2")==-1){
         layer.msg("配置文档和说明文档至少上传一个", {anim:6,icon: 0});
         return;
         }*/
        if (componentName == "") {
            layer.msg("成果名称不能为空", {anim: 6, icon: 0});
            $("#componentName").focus();
            return;
        }
        if (componentName.length > 30) {
            layer.msg("成果名称不能超过30个字", {anim: 6, icon: 0});
            $("#componentName").focus();
            return;
        }
        if (componentRange.length > 100) {
            layer.msg("应用场景不能超过100个字", {anim: 6, icon: 0});
            $("#componentRange").focus();
            return;
        }
        if (!new RegExp("^[^/\\\\:\\*\\?\\<\\>\\|\"]{1,255}$").test(componentName)) {
            layer.msg("成果名称不能有特殊字符", {anim: 6, icon: 0});
            $("#componentName").focus();
            return
        }


        if (componentRange == "") {
            layer.msg("应用场景不能为空", {anim: 6, icon: 0});
            $("#componentRange").focus();
            return;
        }
        if(tipArray.length>10){
            layer.msg("关键词不能超过10个", {anim:6,icon: 0});
            $("#tip").focus();
            return;
        }
        if(tip.length>100){
            layer.msg("关键词不能超过100字", {anim:6,icon: 0});
            $("#tip").focus();
            return;
        }

        if(tip!=""){
            if(tip.indexOf("!")!=-1||tip.indexOf(".")!=-1||tip.indexOf("。")!=-1||tip.indexOf("?")!=-1||tip.indexOf("@")!=-1||tip.indexOf("#")!=-1
                ||tip.indexOf("%")!=-1||tip.indexOf("^")!=-1||tip.indexOf("(")!=-1||tip.indexOf(")")!=-1||tip.indexOf("+")!=-1||tip.indexOf("-")!=-1
                ||tip.indexOf("‘")!=-1||tip.indexOf("'")!=-1||tip.indexOf("“")!=-1||tip.indexOf("/")!=-1||tip.indexOf("|")!=-1||tip.indexOf("*")!=-1
                ||tip.indexOf("，")!=-1||tip.indexOf(" ")!=-1){
                layer.msg("关键词以英文逗号进行分割且不能含特殊字符", {anim:6,icon: 0});
                $("#tip").focus();
                return;
            }
            for (i=0;i<tipArray.length ;i++ )
            {
                if(tipArray[i].length==0){
                    layer.msg("不允许存在空关键词", {anim:6,icon: 0});
                    $("#tip").focus();
                    return;
                }
                if(tipArray[i].length>10){
                    layer.msg("每个关键词不能超过10个字", {anim:6,icon: 0});
                    $("#tip").focus();
                    return;
                }

            }

           }

        var url;//请求地址
        var successMsg, errorMsg;//成功失败提示
        url = "/component/componentSave";
        successMsg = "提报成功";
        errorMsg = "提报失败";
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+url,
            data: {
                componentId: componentId,
                componentName: componentName,
                componentDesc: componentDesc,
                componentDescText: componentDescText,
                componentType: componentType,
                componentOrigin: componentOrigin,
                componentRange: componentRange,
                idArrStr: idArrStr,
                nameArrStr: nameArrStr,
                typeArrStr: typeArrStr,
                tags:tags
            },
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                if (data.result == "1") {
                    layer.alert(successMsg, {
                        icon: 1,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        end: function () {
                            var style=$("#style").val();
                            var type=$("#type").val();
                            var origin=$("#origin").val();
                            var state=$("#state").val();
                            var dept=$("#dept").val();
                            var url =""

                            if(style!=null&&style!=""){
                                url=   "/frontComponent/myList?style="+style+"&type="+type+"&origin="+origin+"&state="+state
                            }else{
                                url=   "/frontComponent/myList?type="+type+"&origin="+origin+"&state="+state+"&dept="+dept
                            }


                            window.parent.open(Hussar.ctxPath+url, "mainFrame");
                        }
                    }, function () {
                        var style=$("#style").val();
                        var type=$("#type").val();
                        var origin=$("#origin").val();
                        var state=$("#state").val();
                        var url =""
                        var dept=$("#dept").val();
                        if(style!=null&&style!=""){
                            url=   "/frontComponent/myList?style="+style+"&type="+type+"&origin="+origin+"&state="+state
                        }else{
                            url=   "/frontComponent/myList?type="+type+"&origin="+origin+"&state="+state+"&dept="+dept
                        }


                        window.parent.open(Hussar.ctxPath+url, "mainFrame");
                    });
                } else {
                    layer.alert(errorMsg, {
                        icon: 2,
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
            if (data.result == "1") {
                layer.alert(successMsg, {
                    icon: 1,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    end: function () {
                        var style=$("#style").val();
                        var type=$("#type").val();
                        var origin=$("#origin").val();
                        var state=$("#state").val();
                        var dept=$("#dept").val();
                        var url =""

                        if(style!=null&&style!=""){
                            url=   "/frontComponent/myList?style="+style+"&type="+type+"&origin="+origin+"&state="+state
                        }else{
                            url=   "/frontComponent/myList?type="+type+"&origin="+origin+"&state="+state+"&dept="+dept
                        }


                        window.parent.open(Hussar.ctxPath+url, "mainFrame");
                    }
                }, function () {
                    var style=$("#style").val();
                    var type=$("#type").val();
                    var origin=$("#origin").val();
                    var state=$("#state").val();
                    var url =""
                    var dept=$("#dept").val();
                    if(style!=null&&style!=""){
                        url=   "/frontComponent/myList?style="+style+"&type="+type+"&origin="+origin+"&state="+state
                    }else{
                        url=   "/frontComponent/myList?type="+type+"&origin="+origin+"&state="+state+"&dept="+dept
                    }


                    window.parent.open(Hussar.ctxPath+url, "mainFrame");
                });
            } else {
                layer.alert(errorMsg, {
                    icon: 2,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
            }
        }, function(data) {

        });
        ajax.set("componentId",componentId);
        ajax.set("componentName",componentName);
        ajax.set("componentDesc",componentDesc);
        ajax.set("componentDescText",componentDescText);
        ajax.set("componentType",componentType);
        ajax.set("componentOrigin",componentOrigin);
        ajax.set("componentRange",componentRange);
        ajax.set("idArrStr",idArrStr);
        ajax.set("nameArrStr",nameArrStr);
        ajax.set("typeArrStr",typeArrStr);
        ajax.set("tags",tags);
        ajax.start();
    });


    function renderUpload(index, type) {
        if (type == "") {

        } else {
            upload.render({
                elem: '#' + index
                , url: Hussar.ctxPath+'/component/componentApplySave?index=' + 0 + '&componentId=' + componentId
                , accept: 'file' //普通文件
                , before: function () {
                    load = layer.load(1, {
                        shade: [0.1, '#fff'],//0.1透明度的白色背景
                        scrollbar: false,
                    });
                }
                , done: function (res) {
                    layer.close(load);
                    if (res.code == 0) {
                        $("#" + index).parent().parent().find("td:eq(2) span").html(res.title);
                        $("#" + index).parent().parent().find("td:eq(2)").attr("title",res.title);
                        $("#" + index).parent().parent().find("td:eq(3)").html("已上传");
                        idArr.push(res.docId);
                        nameArr.push(res.title);
                        typeArr.push(type);
                    }
                    if (res.code == 1) {     $("#" + index).parent().parent().find("td:eq(2) span").html(res.title);
                        $("#" + index).parent().parent().find("td:eq(2)").attr("title",res.title);
                        $("#" + index).parent().parent().find("td:eq(3)").html("上传失败");

                    }
                    if (res.code == 2) {      $("#" + index).parent().parent().find("td:eq(2) span").html(res.title);
                        $("#" + index).parent().parent().find("td:eq(2)").attr("title",res.title);
                        $("#" + index).parent().parent().find("td:eq(3)").html("空间不足");

                    }
                    if (res.code == 6) {
                        $("#" + index).parent().parent().find("td:eq(2) span").html(res.title);
                        $("#" + index).parent().parent().find("td:eq(2)").attr("title",res.title);
                        $("#" + index).parent().parent().find("td:eq(3)").html("文件名过长");

                    }
                    if (res.code == 7) {
                        $("#" + index).parent().parent().find("td:eq(2) span").html(res.title);
                        $("#" + index).parent().parent().find("td:eq(2)").attr("title",res.title);
                        $("#" + index).parent().parent().find("td:eq(3)").html("文件名不合法");

                    }
                    if (res.code == 8) {
                        $("#" + index).parent().parent().find("td:eq(2) span").html(res.title);
                        $("#" + index).parent().parent().find("td:eq(2)").attr("title",res.title);
                        $("#" + index).parent().parent().find("td:eq(3)").html("格式不支持");

                    }
                    if (res.code == 5) {
                        $("#" + index).parent().parent().find("td:eq(2) span").html(res.title);
                        $("#" + index).parent().parent().find("td:eq(2)").attr("title",res.title);
                        $("#" + index).parent().parent().find("td:eq(3)").html("秒传成功");
                        idArr.push(res.docId);
                        nameArr.push(res.title);
                        typeArr.push(type);

                    }
                    if (res.code == 3) {
                        $("#" + index).parent().parent().find("td:eq(2) span").html(res.title);
                        $("#" + index).parent().parent().find("td:eq(2)").attr("title",res.title);
                        $("#" + index).parent().parent().find("td:eq(3)").html("上传失败");
                    }
                    $("#" + index).hide();
                    $("#" + index + "delete").show();

                }

            });

            form.render();
        }

    }

    /*新增/编辑专题*/
    /*$("#saveBtn").on('click',function(){

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
                    layer.msg("每个 不能超过10个字", {anim:6,icon: 0});
                    return;
                }

            }

            if(isRepeat(tipArray)){
                layer.msg("不允许存在重复标签", {anim:6,icon: 0});
                return
            }}




    });*/


});
function download(id) {
    //cancelBubble();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        $.ajaxFileUpload({
            url: Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
            type: "post",
            async: false,
        });
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}



