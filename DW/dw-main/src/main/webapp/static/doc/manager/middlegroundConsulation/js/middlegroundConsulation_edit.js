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
    var consulationId = $("#consulationId").val();
    var layedit = layui.layedit;
    var indexLay = layedit.build('componentDesc', {
        height: 210 //设置编辑器高度
    }); //建立编辑器
    initLaydate = function () {
        var dateDom = $(".dateType");
        $.each($(".dateType"), function (i, dom) {
            laydate.render({
                elem: dom,
                type: 'datetime',
                theme: '#2980FF'
            });
        });
    }
    var ue = UE.getEditor('content',{autoHeightEnabled: false,catchRemoteImageEnable:false})
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
    $(function () {
        initLaydate();
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
            //console.log(height + "//" + screenHeight + " " + layerHeight)
            //$(".layui-layer.layui-layer-iframe").css("top",height - 130 + (screenHeight - layerHeight) / 2.0 + "px");
        }, 300);



    })
//.存储当前页数据集
    var pageData = [];
    //.存储已选择数据集，用普通变量存储也行
    layui.data('checked', null);
    var TopicBin = {
        tableId: "topicBinTable",	//表格id
        seItem: null,		//选中的条目
    };
    //初始化表格
    TopicBin.initTableView = function () {
        var title = $("#title").val();
        tableIns =table.render({
            elem: '#topicList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: '/multiplex/projectList?title=' + title
            , request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            , page: true //开启分页
            ,where: {
                //防止IE浏览器第一次请求后从缓存读取数据
                timestamp: (new Date()).valueOf()
            }
            ,even:true
            , cols: [[
                {field: 'pORJECTID', title: 'id', type: 'radio', width: '5%', align: "left"},
                {type: 'numbers', title: '序号', align: 'center', width: '7%'},
                {field: 'pROJECTNAME', title: '项目名称', width: '53%', align: "left"},
                {field: 'pROJECTDEPT', title: '所属部门', width: '20%', align: "center"},
                {field: 'pROJECTUSER', title: '负责人', width: '15%', align: "center"},
            ]] //设置表头
        });
        table.on('toolbar(topicList)', function(obj){
            var checkStatus = table.checkStatus(obj.config.id); //获取选中行状态
            switch(obj.event){
                case 'getCheckData':
                    var data = checkStatus.data;  //获取选中行数据
                    layer.alert(JSON.stringify(data));
                    break;
            };
        });


    }


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
            url=   "/FrontMeetingRecord/meetingRecordList"
        }else{
            url=   "/FrontMeetingRecord/meetingRecordList"
        }

        window.location.href = encodeURI(url);
    })
    function download(id) {
        //cancelBubble();
        $.ajaxFileUpload({
            url: "/files/fileDownNew?docIds=" + id,
            type: "post",
            async: false,

        });

    }




    $(function(){
        var list = ['10','21','32','43','54']
        for(var i=0;i<list.length;i++){
            var type=list[i].substring(1,list[i].length)
            renderUpload(list[i],type)
        }
    })
    $(".listId").each(function(){
        var index = $(".listId").index($(this));
        var type=$(".listType").eq(index).val()
        var name = $(".listName").eq(index).val();
        $("#fileList tr:eq("+type+")").find("td:eq(2) span").html(name);
        $("#fileList tr:eq("+type+")").find("td:eq(3) ").html("已上传");
        $("#fileList tr:eq("+type+")").find("td:eq(4)").find("button:eq(0)").hide();
        $("#fileList tr:eq("+type+")").find("td:eq(4)").find("button:eq(1)").show();
        nameArr.push(name);
        typeArr.push(type);
        idArr.push($(this).val());
    });
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
    $("#projectName").on('click', function () {
        var con = $("#projectList");
        var title = "";
        layer.open({
            type: 2,
            area: [ '760px',  '450px'],
            fix: false, //不固定
            offset:scrollHeightTip,
            maxmin: false,
            shadeClose: true,
            moveOut: true,
            shade: 0.4,
            title: "选择项目",
            content: "/middlegroundConsulation/projectListView"

        });
    })

    $("#deptName").on('click', function () {
        var con = $("#projectList");
        var title = "";
        layer.open({
            type: 2,
            area: [ '300px',  '500px'],
            fix: false, //不固定
            offset:scrollHeightTip,
            maxmin: false,
            shadeClose: true,
            moveOut: true,
            shade: 0.4,
            title: "选择部门",
            content: "/middlegroundConsulation/deptListView"

        });
    })

    /*新增/编辑*/
    $("#saveBtn").on('click', function () {
        var deptName = $("#deptName").val().trim();
        var deptId = $("#deptId").val().trim();
        var time = $("#meetingTime").val().trim();
        //var projectName = $("#projectName").val().trim();
        var projectMsg = $("#projectMsg").val().trim();
        //var projectId = $("#projectId").val().trim();
        var participant = $("#participant").val().trim();
        var content = ue.getContent();
        var contentText = ue.getContentTxt().trim()
        if (deptName == "" || deptName == null) {
            layer.msg("部门不能为空", {anim: 6, icon: 0});
            return;
        }
        if (time == "" || time == null) {
            layer.msg("时间不能为空", {anim: 6, icon: 0});
            $("#meetingTime").focus();
            return;
        }
        // if (content.length > 500) {
        //     layer.msg("内容不能超过500个字", {anim: 6, icon: 0});
        //     $("#content").focus();
        //     return;
        // }

        // if (projectName == "") {
        //     layer.msg("项目不能为空", {anim: 6, icon: 0});
        //     $("#projectName").focus();
        //     return;
        // }

        if (projectMsg == "") {
            layer.msg("项目信息不能为空", {anim: 6, icon: 0})
            $("#projectMsg").focus();
            return;
        }

        if (participant == "") {
            layer.msg("参与人不能为空", {anim: 6, icon: 0});
            $("#participant").focus();
            return;
        }
        if (idArr!=null&&idArr!=""){
            idArr = idArr.join(",")
            nameArr = nameArr.join(",")
            typeArr = typeArr.join(",")
        }
        var url;//请求地址
        var successMsg, errorMsg;//成功失败提示
        url = "/middlegroundConsulation/editMiddlegroundConsulation";
        successMsg = "修改成功";
        errorMsg = "修改失败";
        $.ajax({
            type: "post",
            url: url,
            data: {
                consulationId: consulationId,
                deptName: deptName,
                deptId: deptId,
                time: time,
                // projectName: projectName,
                // projectId: projectId,
                projectDesc: projectMsg,
                participant: participant,
                content: content,
                idArr:idArr,
                nameArr:nameArr,
                typeArr:typeArr,
                contentText:contentText

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
                            var url =""
                            if(style!=null&&style!=""){
                                url=   "/FrontMeetingRecord/meetingRecordList"
                            }else{
                                url=   "/FrontMeetingRecord/meetingRecordList"
                            }
                            window.parent.open(url, "mainFrame");
                        }
                    }, function () {
                        var style=$("#style").val();
                        var url =""
                        if(style!=null&&style!=""){
                            url=   "/FrontMeetingRecord/meetingRecordList"
                        }else{
                            url=   "/FrontMeetingRecord/meetingRecordList"
                        }
                        window.parent.open(url, "mainFrame");
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
        })
    });


    function renderUpload(index, type) {
        if (type == "") {

        } else {
            upload.render({
                elem: '#' + index
                , url: '/Consulation/consulationApplySave?type=' + type  + '&consulationId=' + consulationId
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

});
function download(id) {
    //cancelBubble();
    $.ajaxFileUpload({
        url: "/files/fileDownNew?docIds=" + id,
        type: "post",
        async: false,
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}



