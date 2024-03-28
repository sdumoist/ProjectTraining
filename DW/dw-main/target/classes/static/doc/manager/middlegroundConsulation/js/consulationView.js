var index = 0 ;
var idArr=[];
var nameArr=[];
var typeArr=[];
var docId=$("#docId").val();

layui.use(['upload','form','layer','Hussar','HussarAjax'] ,function() {

    var form = layui.form;
    var $ = layui.jquery
        , upload = layui.upload
        , layer = layui.layer;
    var Hussar = layui.Hussar,
        $ax = layui.HussarAjax;
    var componentId =$("#componentId").val();
    var componentState=$("#componentState").val();
    //普通图片上传

    $("#cancel").on('click',function(){
        var url="/component/componentListView";
        window.location.href=encodeURI(Hussar.ctxPath+url);
    });

    var componentId = $("#componentId").val();
    var adminFlag = $("#adminFlag").val();
    var orgId = $("#orgId").val();
    var deptId = $("#deptId").val();
    var state = $("#state").val();
    if(state==3){
        $("#audit").show();
    }
    if(adminFlag==4||adminFlag==6){
        $(".reject-btn").show();
        if(state==1){
            $(".pass-btn").show();
        }
    }
    if(adminFlag==5||adminFlag==6){
        if(state==0){
            var flag2=0;
            var reg = new RegExp('"',"g");
            orgId = orgId.replace(reg, "");

            orgId = orgId.substring(1,orgId.length);
            orgId = orgId.substring(0,orgId.length-1);
            var orgStr = orgId.split(",")
            for(var j=0;j<orgStr.length;j++){
                if(orgStr[j]==deptId){
                    flag2=1;
                    break;
                }
            }
            if(flag2==1){
                $(".reject-btn").show();
                $(".pass-btn").show();
            }
        }
    }
    if(state==3){
        $(".reject-btn").hide();
    }
    if(state!=2){
        $("#shareBtn").hide()
    }else{
        $("#shareBtn").show()
    }
    /*$.ajax({
        type:"post",
        url:Hussar.ctxPath+"/component/cacheViewNum",
        data:{
            componentId:componentId,
        },

    });*/
    // var ajax = new $ax(Hussar.ctxPath + "/component/cacheViewNum", function(data) {
    //
    // }, function(data) {
    //
    // });
    // ajax.set("componentId",componentId);
    // ajax.start();
    function download(id){
        //cancelBubble();
        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew?docIds="+id,
            type : "post",
            async:false,

        });
        /*
         * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
         * docName:name, fileIds:id, } });
         */
    }
    $("#shareBtn").click(function () {
        var name=$("#name").val();
        var componentId=$("#componentId").val()
        openShare('', '/s/shareConfirm', 538, 390,componentId,"component",name);
    });
    var isShare=$("#isShare").val();
    if(isShare==1){
        $("#shareBtn").hide()
        $(".reject-btn").hide();
        $(".pass-btn").hide();
        $("#audit").hide();
    }
    if(isShare==2){
        $("#shareBtn").hide()
        $(".docView").hide()
        $(".btn-div-btn").hide();
    }
    /*打开分享链接*/
    function openShare(title, url, w, h,docId,fileSuffixName,fileName) {
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
        parent.layer.open({
            type: 2,
            // area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            skin:'share-class',
            title: title,
            content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURI(fileName)
        });
    }



    //点击驳回显示弹窗
    $(".reject-btn").on('click',function () {

        parent.layer.open({
            type: 1
            ,title:'审核意见'
            ,skin: 'examineSkin'
            ,content:$("#examine", parent.document)
            ,btn: ['提交反馈' ]
            ,area: ['632px', '300px']
            ,yes: function(index, layero){

                var reason = $("#reason",window.parent.document).val().trim();//专题名称
                if(reason.length>500){
                    parent. layer.msg("驳回意见不能超过500个字", {anim:6,icon: 0});
                }else{

                    /*$.ajax({
                        type: "post",
                        url: Hussar.ctxPath+"/component/componentBack",
                        data: {
                            componentId: componentId,
                            componentState:componentState,
                            reason: reason
                        },
                        async: false,
                        cache: false,
                        dataType: "json",
                        success: function (data) {
                            if (data.result == "1") {
                                parent.layer.closeAll();
                                $("#reason",window.parent.document).val("")
                                parent.layer.alert("驳回成功", {
                                    icon: 1,
                                    shadeClose: true,
                                    skin: 'layui-layer-molv',
                                    shift: 5,
                                    area: ['300px', '180px'],
                                    title: '提示',
                                    end: function () {
                                        parent.layer.closeAll();
                                        location.reload();
                                        /!* var url="/component/componentListView";
                                         window.location.href=encodeURI(url);*!/
                                    }
                                }, function () {
                                    parent.layer.closeAll();
                                    location.reload();
                                    /!* var url="/component/componentListView";
                                     window.location.href=encodeURI(url);*!/
                                });
                            }else if (data.result == "3"){
                                $("#reason",window.parent.document).val("")
                                parent.layer.alert("成果已被驳回", {
                                    icon: 2,
                                    shadeClose: true,
                                    skin: 'layui-layer-molv',
                                    shift: 5,
                                    area: ['300px', '180px'],
                                    title: '提示',
                                    end: function () {
                                        parent.layer.closeAll();
                                        location.reload();
                                        /!* var url="/component/componentListView";
                                         window.location.href=encodeURI(url);*!/
                                    }
                                }, function () {
                                    parent.layer.closeAll();
                                    location.reload();
                                    /!* var url="/component/componentListView";
                                     window.location.href=encodeURI(url);*!/

                                });
                            } else {
                                $("#reason",window.parent.document).val("")
                                parent.layer.alert("驳回失败", {
                                    icon: 2,
                                    shadeClose: true,
                                    skin: 'layui-layer-molv',
                                    shift: 5,
                                    area: ['300px', '180px'],
                                    title: '提示',
                                    end: function () {
                                        parent.layer.closeAll();
                                        location.reload();
                                        /!* var url="/component/componentListView";
                                         window.location.href=encodeURI(url);*!/
                                    }
                                }, function () {
                                    parent.layer.closeAll();
                                    location.reload();
                                    /!* var url="/component/componentListView";
                                     window.location.href=encodeURI(url);*!/


                                });
                            }
                        }
                    });*/
                    var ajax = new $ax(Hussar.ctxPath + "/component/componentBack", function(data) {
                        if (data.result == "1") {
                            parent.layer.closeAll();
                            $("#reason",window.parent.document).val("")
                            parent.layer.alert("驳回成功", {
                                icon: 1,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示',
                                end: function () {
                                    parent.layer.closeAll();
                                    location.reload();
                                    /* var url="/component/componentListView";
                                     window.location.href=encodeURI(url);*/
                                }
                            }, function () {
                                parent.layer.closeAll();
                                location.reload();
                                /* var url="/component/componentListView";
                                 window.location.href=encodeURI(url);*/
                            });
                        }else if (data.result == "3"){
                            $("#reason",window.parent.document).val("")
                            parent.layer.alert("成果已被驳回", {
                                icon: 2,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示',
                                end: function () {
                                    parent.layer.closeAll();
                                    location.reload();
                                    /* var url="/component/componentListView";
                                     window.location.href=encodeURI(url);*/
                                }
                            }, function () {
                                parent.layer.closeAll();
                                location.reload();
                                /* var url="/component/componentListView";
                                 window.location.href=encodeURI(url);*/

                            });
                        } else {
                            $("#reason",window.parent.document).val("")
                            parent.layer.alert("驳回失败", {
                                icon: 2,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示',
                                end: function () {
                                    parent.layer.closeAll();
                                    location.reload();
                                    /* var url="/component/componentListView";
                                     window.location.href=encodeURI(url);*/
                                }
                            }, function () {
                                parent.layer.closeAll();
                                location.reload();
                                /* var url="/component/componentListView";
                                 window.location.href=encodeURI(url);*/


                            });
                        }
                    }, function(data) {

                    });
                    ajax.set("componentId",componentId);
                    ajax.set("componentState",componentState);
                    ajax.set("reason",reason);
                    ajax.start();
                }

                //按钮【按钮一】的回调

            }
            ,end: function () {//无论是确认还是取消，只要层被销毁了，end都会执行，不携带任何参数。layer.open关闭事件
                $("#reason",window.parent.document).val("")
            }
        });
    });
    //点击通过显示弹窗
    $(".pass-btn").on('click',function () {
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/component/componentPass",
            data: {
                componentId: componentId,
                componentState:componentState

            },
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                if (data.result == "1") {
                    parent.layer.alert("审核成功", {
                        icon: 1,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        end: function () {
                            parent.layer.closeAll();
                            location.reload();
                        }
                    }, function () {
                        parent.layer.closeAll();
                        location.reload();
                    });
                }
                else if (data.result == "3"){
                    parent.layer.alert("成果已被审核", {
                        icon: 2,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        end: function () {
                            parent.layer.closeAll();
                            location.reload(), function () {
                                parent.layer.closeAll();
                                location.reload();
                            }
                        }
                    });
                } else if (data.result == "4"){
                    parent.layer.alert("成果已被发布", {
                        icon: 2,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        end: function () {
                            parent.layer.closeAll();
                            location.reload(), function () {
                                parent.layer.closeAll();
                                location.reload();
                            }
                        }
                    });
                }
                else if (data.result == "5"){
                    parent.layer.alert("成果已被驳回", {
                        icon: 2,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        end: function () {
                            parent.layer.closeAll();
                            location.reload(), function () {
                                parent.layer.closeAll();
                                location.reload();
                            }
                        }
                    });
                }
                else {
                    parent.layer.alert("审核失败", {
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
        var ajax = new $ax(Hussar.ctxPath + "/component/componentPass", function(data) {
            if (data.result == "1") {
                parent.layer.alert("审核成功", {
                    icon: 1,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    end: function () {
                        parent.layer.closeAll();
                        location.reload();
                    }
                }, function () {
                    parent.layer.closeAll();
                    location.reload();
                });
            }
            else if (data.result == "3"){
                parent.layer.alert("成果已被审核", {
                    icon: 2,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    end: function () {
                        parent.layer.closeAll();
                        location.reload(), function () {
                            parent.layer.closeAll();
                            location.reload();
                        }
                    }
                });
            } else if (data.result == "4"){
                parent.layer.alert("成果已被发布", {
                    icon: 2,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    end: function () {
                        parent.layer.closeAll();
                        location.reload(), function () {
                            parent.layer.closeAll();
                            location.reload();
                        }
                    }
                });
            }
            else if (data.result == "5"){
                parent.layer.alert("成果已被驳回", {
                    icon: 2,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    end: function () {
                        parent.layer.closeAll();
                        location.reload(), function () {
                            parent.layer.closeAll();
                            location.reload();
                        }
                    }
                });
            }
            else {
                parent.layer.alert("审核失败", {
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
        ajax.set("componentState",componentState);
        ajax.start();
    });



})
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showPdfComponent(id,fileSuffixName,name){
    var isShare=$("#isShare").val();
    if(isShare==1||isShare==2){
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

            location.href= Hussar.ctxPath+"/login"
        });
        return
    }
    var index= fileSuffixName.lastIndexOf(".");
    var fileSuffixName = fileSuffixName.substring(index,fileSuffixName.length);
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
                if(data.code==1){
                    openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
                }else if(data.code==2){
                    openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
                }else if(data.code==3){
                    openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
                }else if(data.code==4){
                    openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
                }else{
                    layer.msg("此文件类型不支持预览。",{anim:6,icon: 0});
                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
                openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id + "&showType=consulation");
            }else if(data.code==2){
                openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id + "&showType=consulation");
            }else if(data.code==3){
                openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id + "&showType=consulation");
            }else if(data.code==4){
                openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id + "&showType=consulation");
            }else{
                parent.layer.msg("此文件类型不支持预览。",{anim:6,icon: 0});
            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
    });


};
function download(id) {
    var isShare=$("#isShare").val();
    if(isShare==1||isShare==2){
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

            location.href= Hussar.ctxPath+"/login"
        });
        return
    }
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            url: Hussar.ctxPath+"/integral/downloadIntegral",
            async: false,
            data: {
                docId: id,
                ruleCode: 'download'
            },
            success: function (data) {
                if (data.status == "1") {
                    var index2 = parent.layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                        icon: 3,
                        title: '提示'
                    }, function (index) {
                        parent.layer.close(index2);
                        var valid = true;
                        $.ajax({
                            url: Hussar.ctxPath+"/integral/addIntegral",
                            async: false,
                            data: {
                                docId: id,
                                ruleCode: 'download'
                            },
                            success: function (data) {
                                if (data != null) {

                                    $("#num").html(data.msg)
                                    if (data.msg == "积分不足") {
                                        $(".integral .point").hide();
                                        $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                    }
                                    $(".integral").show();
                                    setTimeout(function () {
                                        $(".integral .point").show();
                                        $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                        $(".integral").hide();

                                    }, 2000)

                                    if (data.integral == 0) {
                                        valid = false;
                                    }
                                }
                            }
                        });
                        if (valid) {
                            $.ajax({
                                type: "post",
                                url: Hussar.ctxPath+"/fsFile/getPreviewType",
                                data: {
                                    suffix: fileSuffixName
                                },
                                async: false,
                                cache: false,
                                dataType: "json",
                                success: function (data) {


                                    $.ajaxFileUpload({
                                        url: Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
                                        type: "post",
                                        async: false,
                                    });
                                    /!*
                                     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
                                     * docName:name, fileIds:id, } });
                                     *!/




                                }
                            });
                        }
                    })
                } else {
                    var valid = true;
                    $.ajax({
                        url: Hussar.ctxPath+"/integral/addIntegral",
                        async: false,
                        data: {
                            docId: id,
                            ruleCode: 'download'
                        },
                        success: function (data) {
                            if (data != null) {

                                $("#num").html(data.msg)
                                if (data.msg == "积分不足") {
                                    $(".integral .point").hide();
                                    $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                }
                                $(".integral").show();
                                setTimeout(function () {
                                    $(".integral .point").show();
                                    $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                    $(".integral").hide();

                                }, 2000)

                                if (data.integral == 0) {
                                    valid = false;
                                }
                            }
                        }
                    });
                    if (valid) {
                        $.ajax({
                            type: "post",
                            url: Hussar.ctxPath+"/fsFile/getPreviewType",
                            data: {
                                suffix: fileSuffixName
                            },
                            async: false,
                            cache: false,
                            dataType: "json",
                            success: function (data) {


                                $.ajaxFileUpload({
                                    url: Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
                                    type: "post",
                                    async: false,
                                });
                                /!*
                                 * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
                                 * docName:name, fileIds:id, } });
                                 *!/




                            }
                        });
                    }
                }
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {
            if (data.status == "1") {
                var index2 = parent.layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                    icon: 3,
                    title: '提示',
                    skin:'download-info'
                }, function (index) {
                    parent.layer.close(index2);
                    var valid = true;
                    /*$.ajax({
                        url: Hussar.ctxPath+"/integral/addIntegral",
                        async: false,
                        data: {
                            docId: id,
                            ruleCode: 'download'
                        },
                        success: function (data) {
                            if (data != null) {

                                $("#num").html(data.msg)
                                if (data.msg == "积分不足") {
                                    $(".integral .point").hide();
                                    $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                                }
                                $(".integral").show();
                                setTimeout(function () {
                                    $(".integral .point").show();
                                    $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                    $(".integral").hide();

                                }, 2000)

                                if (data.integral == 0) {
                                    valid = false;
                                }
                            }
                        }
                    });*/
                    var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                        if (data != null) {

                            $("#num").html(data.msg)
                            if (data.msg == "积分不足") {
                                $(".integral .point").hide();
                                $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                            }
                            $(".integral").show();
                            setTimeout(function () {
                                $(".integral .point").show();
                                $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                $(".integral").hide();

                            }, 2000)

                            if (data.integral == 0) {
                                valid = false;
                            }
                        }
                    }, function(data) {

                    });
                    ajax.set("docId",id);
                    ajax.set("ruleCode",'download');
                    ajax.start();
                    if (valid) {
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


                                $.ajaxFileUpload({
                                    url: Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
                                    type: "post",
                                    async: false,
                                });
                                /!*
                                 * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
                                 * docName:name, fileIds:id, } });
                                 *!/




                            }
                        });*/
                        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
                            $.ajaxFileUpload({
                                url: Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
                                type: "post",
                                async: false,
                            });
                            /*
                             * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
                             * docName:name, fileIds:id, } });
                             */
                        }, function(data) {

                        });
                        ajax.set("suffix",fileSuffixName);
                        ajax.start();
                    }
                })
            } else {
                var valid = true;
                /*$.ajax({
                    url: Hussar.ctxPath+"/integral/addIntegral",
                    async: false,
                    data: {
                        docId: id,
                        ruleCode: 'download'
                    },
                    success: function (data) {
                        if (data != null) {

                            $("#num").html(data.msg)
                            if (data.msg == "积分不足") {
                                $(".integral .point").hide();
                                $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                            }
                            $(".integral").show();
                            setTimeout(function () {
                                $(".integral .point").show();
                                $(".integral .num").css({"width": "36px", "padding-top": "0"})
                                $(".integral").hide();

                            }, 2000)

                            if (data.integral == 0) {
                                valid = false;
                            }
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                    if (data != null) {

                        $("#num").html(data.msg)
                        if (data.msg == "积分不足") {
                            $(".integral .point").hide();
                            $(".integral .num").css({"width": "81px", "padding-top": "13px"})
                        }
                        $(".integral").show();
                        setTimeout(function () {
                            $(".integral .point").show();
                            $(".integral .num").css({"width": "36px", "padding-top": "0"})
                            $(".integral").hide();

                        }, 2000)

                        if (data.integral == 0) {
                            valid = false;
                        }
                    }
                }, function(data) {

                });
                ajax.set("docId",id);
                ajax.set("ruleCode",'download');
                ajax.start();
                if (valid) {
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


                            $.ajaxFileUpload({
                                url: Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
                                type: "post",
                                async: false,
                            });
                            /!*
                             * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
                             * docName:name, fileIds:id, } });
                             *!/




                        }
                    });*/
                    var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
                        $.ajaxFileUpload({
                            url: Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
                            type: "post",
                            async: false,
                        });
                        /*
                         * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
                         * docName:name, fileIds:id, } });
                         */
                    }, function(data) {

                    });
                    ajax.set("suffix",fileSuffixName);
                    ajax.start();
                }
            }
        }, function(data) {

        });
        ajax.set("docId",id);
        ajax.set("ruleCode",'download');
        ajax.start();
    });

}
