var pathId = [];
var pathName = [];
var topicName = "";
var scrollHeightAlert=0;
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var layerOpen1;
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
$(function () {
    layui.use(['Hussar','HussarAjax'], function() {
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/frontTopic/getCount", function (data) {
            $("#count").val(data);
            $("#fileCount").html(data)
        }, function (data) {

        });
        ajax.setAsync(true);
        ajax.set("topicId", $("#topicId").val());
        ajax.start();
    })
})


function showPdf(id,flag,fileSuffixName) {
    var keyword =  $("#headerSearchInputValue").val();

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
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
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
                    openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id);
                }

            }
        });*/

        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
                openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
            }else if(data.code==2){
                openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
            }else if(data.code==3){
                openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
            }else if(data.code==4){
                openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
            }else{
                openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id);
            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
    });
}
function dbclick(id,flag,fileSuffixName,name,aTopicName) {
    if (aTopicName=="aTopicName"){
        $("#path").empty();
        $("#pathName").val("");
        $("#pathId").val("");
    }
    var path = $("#pathName").val();
    var pathFolderId = $("#pathId").val();
    if (pathFolderId.search(id)!=-1&&aTopicName!="aTopicName"){
        path = path.split(name)[0];
        pathFolderId = pathFolderId.split(id)[0];
    }

    layui.use(['laypage','layer','Hussar'], function(){
        var laypage = layui.laypage,
            layer = layui.layer;
        var Hussar = layui.Hussar;
        var count=$("#docCount").val();
        var topicId=$("#topicId").val();
        var num = $("#curr").val();
        laypage.render({
            elem: 'laypageAre'
            ,pages:2
            ,count: count //数据总数，从服务端得到
            ,limit: 10
            ,layout: ['prev', 'page', 'next']
            ,curr: 1
            ,jump: function(obj, first){
                window.location.replace(Hussar.ctxPath+"/frontTopic/topicDetail?topicId="
                    +topicId+"&page="+obj.curr+"&size=10&folderId="+id+"&pathName="+path+name+"&pathId="+pathFolderId+id);
            }
        });
    });

}
function createPath() {
    var path = $("#pathName").val().substring(0,$("#pathName").val().length-1).split(",");
    var pathFolderId = $("#pathId").val().substring(0,$("#pathId").val().length-1).split(",");
    topicName = $("#topicName").val();
    for (var i=0;i<path.length;i++){
        if (pathFolderId[i]!=""){
            pathId.push(pathFolderId[i]);
            pathName.push(path[i]);
        }

    }
    if (pathId.length>=1&&pathId!=""){
        // $("#aTopicName").css("display","inline-block");
        // $("#aTopicName").css("height","30px");
        // $("#aTopicName").css("cursor","pointer");
        // $("#aTopicName").css("color","#8796AB");
        $("#aTopicName").append("  >  ");
    }
    $("#path").empty();
    for (var i = 0; i < pathId.length; i++) {
        if (pathId[i]!=""&&pathId[i]!="") {
            if (i == pathId.length - 1) {
                var param = '<span>' + pathName[i] + '</span>'
            } else {
                var param = '<span><a target="_blank"  onclick="dbclick('
                    + "'" + pathId[i] + "','','folder','" + pathName[i] + "'" + ')">' + pathName[i] + '</a>' + '  >  </span>'
            }
            $("#path").append(param);
        }
    }
}
$(document).ready(function(){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        var topicId = $("#topicId").val();
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/topic/cacheViewNum",
            data:{
                topicId:topicId,
            },
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/topic/cacheViewNum", function(data) {

        }, function(data) {

        });
        ajax.set("topicId",topicId);
        ajax.start();
        $("#shareBtn").click(function () {
            openShare('', '/s/shareConfirm', 538, 390,docId,fileSuffixName,fileName);
        });
    });
    // $("#breadCrumbs").hide();
    createPath()
    setInterval(function () {
        scrollHeight=parent.scrollHeight;
        var height = parseInt(scrollHeight);
        var screenHeight = parseInt(window.screen.availHeight);
        if( scrollHeight!=0){
            scrollHeightAlert= parseInt(height - 130 + (screenHeight - 154) / 2.0) + "px";
            scrollHeightLong= parseInt(height - 130 + (screenHeight - 510) / 2.0) + "px";
            scrollHeightTip = parseInt(height - 130 + (screenHeight - 250) / 2.0) + "px";
            scrollHeightShare = parseInt(height - 130 + (screenHeight - 200) / 2.0) + "px";
            scrollHeightMsg = parseInt(height - 130 + (screenHeight - 64) / 2.0) + "px";
            //console.log(scrollHeightAlert + ' ' + scrollHeightLong)
        }
        //console.log(height + "//" + screenHeight + " " + layerHeight)
        //$(".layui-layer.layui-layer-iframe").css("top",height - 130 + (screenHeight - layerHeight) / 2.0 + "px");
    },300);
});
layui.use(['laypage','layer','Hussar'], function(){
    var path = $("#pathName").val();
    var pathFolderId = $("#pathId").val();
    var laypage = layui.laypage,
        layer = layui.layer;
    var Hussar = layui.Hussar;
    var count=$("#docCount").val();
    var topicId=$("#topicId").val();
    var num = $("#curr").val();
    var id = $("#docId").val()
    laypage.render({
        elem: 'laypageAre'
        ,pages:2
        ,count: count //数据总数，从服务端得到
        ,limit: 10
        ,layout: ['prev', 'page', 'next']
        ,curr: num || 1
        ,jump: function(obj, first){
            if(!first) {
                window.location.replace(Hussar.ctxPath + "/frontTopic/topicDetail?topicId="
                    + topicId + "&page=" + obj.curr + "&size=10&folderId=" + id+"&pathName="+path+"&pathId="+pathFolderId);
            }
        }
    });
});
// layui.use(['laypage','layer','Hussar'], function(){
//     var laypage = layui.laypage,
//         layer = layui.layer;
//     var Hussar = layui.Hussar;
//     var count=$("#docCount").val();
//     var topicId=$("#topicId").val();
//     var num = $("#curr").val();
//     laypage.render({
//         elem: 'laypageAre'
//         ,pages:2
//         ,count: count //数据总数，从服务端得到
//         ,limit: 10
//         ,layout: ['prev', 'page', 'next']
//         ,curr: num || 1
//         ,jump: function(obj, first){
//             //首次不执行
//             if(!first){
//                 window.location.replace(Hussar.ctxPath+"/frontTopic/topicDetail?topicId="+topicId+"&page="+obj.curr+"&size=10");
//             }
//         }
//     });
// });
function  iconDownLoad(id,name) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            url: Hussar.ctxPath+"/integral/downloadIntegral",
            async: true,
            data: {
                docId: id,
                ruleCode: 'download'
            },
            success: function (data) {
                if (data.status == "1") {
                    var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {icon: 3, title: '提示'}, function (index) {
                        layer.close(index2);
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
                                    $(".integral").show();
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
                }else {
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
                                $(".integral").show();
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
                var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {icon: 3, title: '提示',skin:'download-info'}, function (index) {
                    layer.close(index2);
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
                                $(".integral").show();
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
                            $(".integral").show();
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
            }else {
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
                            $(".integral").show();
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
                        $(".integral").show();
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
    });
    // var index = layer.load(1, {
    //     shade: [0.1,'#fff'] ,//0.1透明度的白色背景
    //     scrollbar: false,
    //     time:1000
    // });

}
function download(id,name){
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajaxFileUpload({
            url: Hussar.ctxPath+"/files/fileDownNew",
            type:"post",
            data:{
                docIds:id,
            }
        });
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}

function collection(docId,status) {
    // layui.use(['Hussar','HussarAjax'], function(){
    //     var Hussar = layui.Hussar,
    //         $ax = layui.HussarAjax;
    //
    //     if (status == '1'){
    //         var cancelCollection="/personalCollection/cancelCollection";
    //         /*$.ajax({
    //             type:"post",
    //             url: Hussar.ctxPath+cancelCollection,
    //             async:false,
    //             data:{
    //                 docIds:docId
    //             },
    //             success:function(data) {
    //                 if(data.success=='0'){
    //                     $("#" + docId + "col").show();
    //                     $("#" + docId + "con").hide();
    //                 }else{
    //                     alert("取消收藏失败");
    //
    //                 }
    //             },
    //         });*/
    //         var ajax = new $ax(Hussar.ctxPath + cancelCollection, function(data) {
    //             if(data.success=='0'){
    //                 $("#" + docId + "col").show();
    //                 $("#" + docId + "con").hide();
    //             }else{
    //                 alert("取消收藏失败");
    //
    //             }
    //         }, function(data) {
    //
    //         });
    //         ajax.set("docIds",docId);
    //         ajax.start();
    //     } else {
    //         var collectionLink ="/personalCollection/addCollection";
    //         /*$.ajax({
    //             type:"post",
    //             url: Hussar.ctxPath+collectionLink,
    //             async:false,
    //             data:{
    //                 docIds:docId
    //             },
    //             success:function(data) {
    //                 if(data.success=='0'){
    //                     $("#" + docId + "col").hide();
    //                     $("#" + docId + "con").show();
    //                 }else{
    //                     alert("收藏失败");
    //                 }
    //             },
    //         });*/
    //         var ajax = new $ax(Hussar.ctxPath + collectionLink, function(data) {
    //             if(data.success=='0'){
    //                 $("#" + docId + "col").hide();
    //                 $("#" + docId + "con").show();
    //             }else{
    //                 alert("收藏失败");
    //             }
    //         }, function(data) {
    //
    //         });
    //         ajax.set("docIds",docId);
    //         ajax.start();
    //     }
    // });
    if(status=='0'){
        layerOpen1 = layer.open({
            type: 2,
            id:"collections",
            area: [ '402px',  '432px'],
            fix: false, //不固定
            move: false,
            offset:scrollHeightTip,
            maxmin: false,
            shadeClose: true,
            moveOut: true,
            shade: 0.4,
            title: "<span id='upLevel' onclick='upLevel()'>收藏</span>",
            content: "/personalCollection/collectionToFolderView?docId="+docId
        });
    }else {
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;
                layer.confirm("确定取消收藏吗？", {
                    title: ['取消收藏', 'background-color:#fff'],
                    skin:'move-confirm'
                }, function () {
                    var ajax = new $ax(Hussar.ctxPath + "/personalCollection/cancelCollection", function(data) {
                        if(data.success == "0"){
                            layer.msg('取消收藏成功',{icon: 1,offset:'40%'});
                            hideCancelCollection(docId);
                        }else {
                            layer.msg('取消收藏失败',{anim:6,icon: 2,offset:scrollHeightMsg})
                        }
                    }, function(data) {
                        layer.msg('取消收藏异常!',{anim:6,icon: 2,offset:scrollHeightMsg})

                    });
                    ajax.set("docIds",docId);
                    ajax.start();
                })
        });
    }

}
function share(docId,fileSuffixName,fileName) {
    if(fileSuffixName.substr(0,1)=="."){
        openShare('', '/s/shareConfirm', 538, 390,docId,fileSuffixName,fileName);
    }else {
        openShare('', '/s/shareConfirm', 538, 390,docId,"."+fileSuffixName,fileName);
    }

}
/*打开分享链接*/
function openShare(title, url, w, h,docId,fileSuffixName,fileName) {
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
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            cache: false,
            dataType: "json",
            success: function (data) {
                if(data.code==1){
                }else if(data.code==2){
                }else if(data.code==3){
                }else if(data.code==4){
                }else{
                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
            }else if(data.code==2){
            }else if(data.code==3){
            }else if(data.code==4){
            }else{
            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
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
            h = ($(window).height() - 200);
        }
        layer.open({
            type: 2,
            // area: [w + 'px', h + 'px'],
            fix: false, //不固定

            maxmin: false,
            shadeClose: true,
            skin:'share-class',
            shade: 0.4,
            title: title,
            offset:'45%',
            content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURI(fileName)
        });
    });
}
function isPDFShow(fileSuffixName){
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

function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}
function showCancelCollection(docId) {
    $("#"+docId+ "con").show();
    $("#"+docId+ "col").hide();
}

function hideCancelCollection(docId) {
    $("#"+docId+ "con").hide();
    $("#"+docId+ "col").show();
}
function showUpLevel() {
    $("#upLevel").html('返回');
}
function hideUpLevel() {
    $("#upLevel").html('收藏');
}
function upLevel(){
    var contentWindow = $("#layui-layer-iframe" + layerOpen1)[0].contentWindow;
    contentWindow.upLeveChild();
}
