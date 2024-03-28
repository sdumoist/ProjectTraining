/**
 * @Author: xubin
 * @Date:2018-07-12
 */
var scrollHeightAlert=0;
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var layerOpen1;
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function(){
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;
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
    $(document).ready(function(){
        var filePath;//文档路径
        var authorId;//作者ID
        var fileName;//文档名称
        var fileSuffixName;//文档后缀
        var filePdfPath;//文档预览路径
        var docAbstract;//文档描述
        var authority;//文档权限
        var collection;//是否收藏
        var shareFlag;
        var docType;
        var docId = $("#docId").val();
        initFileTree = function () {
            var $tree = $("#fileTree2");
            $tree.jstree("destroy");    //二次打开时要先销毁树
            $tree.jstree({
                core: {
                    check_callback: true,
                    data: {
                        "url": Hussar.ctxPath + "/personalCollection/getTreeDataLazy?lazy",
                        "data": function (node) {
                            return {
                                "id": node.id
                            };
                        }
                    },
                    themes: {
                        theme: "default",
                        dots: false,// 是否展示虚线
                        icons: true,// 是否展示图标
                    }
                },
                types: {
                    "closed": {
                        "icon": Hussar.ctxPath + "/static/resources/img/fsfile/treeFile.png",
                    },
                    "default": {
                        "icon": Hussar.ctxPath + "/static/resources/img/fsfile/treeFile.png",
                    },
                    "opened": {
                        "icon": Hussar.ctxPath + "/static/resources/img/fsfile/openFile.png",
                    }
                },
                plugins: ['types']
            });
            var openFolder = null;
            $tree.bind('activate_node.jstree', function (obj, e) {
                openFolder = e;
            });
            $(".layui-layer-btn0").on('click', function () {
                if (openFolder === null) {
                    layer.msg("请选择目录", {anim: 6, icon: 0, offset: scrollHeightMsg});
                } else {
                    var operation = function () {
                        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/collectionToFolder", function (data) {
                            if (data.result == "0") {

                                layer.msg("文件已存在", {icon: 0, offset: scrollHeightMsg});
                            } else if (data.result == "1") {
                                $(".layui-laypage-btn").click();
                                layer.close(layerView);
                                layer.msg("收藏成功", {icon: 1, offset: scrollHeightMsg});
                            } else {

                                layer.msg("收藏失败", {icon: 2, offset: scrollHeightMsg});
                            }
                        }, function (data) {
                            layer.msg("系统出错，请联系管理员", {icon: 2, offset: scrollHeightMsg});
                        });
                        ajax.set("ids", docId);
                        ajax.set("parentFolderId", openFolder.node.original.id);
                        ajax.start();
                    }
                    layer.confirm('确定要收藏至此目录下吗？', {
                        title: ['收藏', 'background-color:#fff'],
                        offset: scrollHeightAlert,
                        skin: 'move-confirm'
                    }, operation);
                }
            });
        };
        /*$.ajax({
            async:false,
            type:"post",
            url: Hussar.ctxPath+"/preview/fileDetail",
            data:{id:docId},
            success:function(data) {
                document.title = data.title+"-金企文库";
                authorId = data.authorId;
                filePath ="/preview/list?fileId=" + encodeURIComponent(data.filePath);
                fileName = data.title;
                fileSuffixName = data.fileSuffixName.toLowerCase();
                docAbstract = data.docAbstract;
                filePdfPath = data.filePdfPath;
                docId = data.id;
                authority=data.authority;
                collection =data.collection;
                shareFlag = data.shareFlag;
                $("#docId").text(data.id);
                $("#title").text(data.title);

                var obj = document.getElementById("title");
                if(fileSuffixName=="mp3"){  //文档名称前的图片
                    $("#title").addClass("type-mp3");
                    // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
                }
                var author=data.author;
                if(author==""||author==undefined){
                    author=data.userId;
                }
                $("#owner").html("上传者: "+author+"<em>|</em>");
                $("#uploadTime").html("上传时间: "+data.createTime +"<em>|</em>");
                $("#fileSize").html("文件大小: "+data.fileSize +"<em>|</em>");
                $("#downloadNum").html("下载次数: "+data.downloadNum +"<em>|</em>");
                $("#previewNum").html("预览次数: "+data.readNum );
                $("#docAbstract").text(docAbstract);
                if(authority=='1'||data.adminFlag==1||authority=='2'){
                    $("#dowLoadButton").show();
                }
                if (shareFlag == '1'){
                    $("#shareBtn").show();
                }
                if(collection=='0'){
                    $("#collection").show();
                    $("#cancelCollection").hide();
                }else{
                    $("#collection").hide();
                    $("#cancelCollection").show();
                }
                var srcLink = "/files/fileDownNew?docName=" + "" + "&docIds=" + docId;
                var collectionLink ="/personalCollection/addCollection";
                var  cancelCollection="/personalCollection/cancelCollection";
                $("#collection").click(function () {
                    $.ajax({
                        type:"post",
                        url: Hussar.ctxPath+collectionLink,
                        async:false,
                        data:{
                            docIds:docId
                        },
                        success:function(data) {
                            if(data.success=='0'){
                                //alert("收藏成功");
                                $("#collection").hide();
                                $("#cancelCollection").show();
                            }else{
                                alert("收藏失败");

                            }
                        },
                    });
                });
                $("#cancelCollection").click(function () {
                    $.ajax({
                        type:"post",
                        url: Hussar.ctxPath+cancelCollection,
                        async:false,
                        data:{
                            docIds:docId
                        },
                        success:function(data) {
                            if(data.success=='0'){
                                //alert("取消收藏成功");
                                $("#collection").show();
                                $("#cancelCollection").hide();
                            }else{
                                alert("取消收藏失败");

                            }
                        },
                    });
                });
                $("#dowLoadButton").on('click',function(){
                    $.ajax({
                        url: Hussar.ctxPath+"/integral/downloadIntegral",
                        async: true,
                        data: {
                            docId: docId,
                            ruleCode: 'download'
                        },
                        success: function (data) {
                            if (data.status == "1") {
                                var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                    icon: 3,
                                    title: '提示'
                                }, function (index) {
                                    layer.close(index2);
                                    var valid = true;
                                    $.ajax({
                                        url: Hussar.ctxPath+"/integral/addIntegral",
                                        async: false,
                                        data: {
                                            docId: docId,
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
                                        $.ajaxFileUpload({
                                            url: Hussar.ctxPath+"/files/fileDownNew",
                                            type: "post",
                                            data: {
                                                docName: "",//fileName,
                                                docIds: docId
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
                                        docId: docId,
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
                                    $.ajaxFileUpload({
                                        url: Hussar.ctxPath+"/files/fileDownNew",
                                        type: "post",
                                        data: {
                                            docName: "",//fileName,
                                            docIds: docId
                                        }
                                    });
                                }
                            }
                        }
                    })

                });
                $("#shareBtn").click(function () {
                    openShare('', '/s/shareConfirm', 538, 311,docId,fileSuffixName,fileName);
                });            //$("#dowLoadButton").attr('href',srcLink)
                var username = $("#username").val();
                document.getElementById("s1").src=Hussar.ctxPath+filePath;
                document.getElementById("audio").load();
            },
            error:function(data){
                $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/preview/fileDetail", function(data) {
            document.title = data.title+"-"+projectTitle;
            authorId = data.authorId;
            filePath ="/preview/list?fileId=" + encodeURIComponent(data.filePath);
            fileName = data.title;
            fileSuffixName = data.fileSuffixName.toLowerCase();
            docType = "." + fileSuffixName
            docAbstract = data.docAbstract;
            filePdfPath = data.filePdfPath;
            docId = data.id;
            authority=data.authority;
            collection =data.collection;
            shareFlag = data.shareFlag;
            $("#docId").text(data.id);
            $("#title").text(data.title);

            var obj = document.getElementById("title");
            if(fileSuffixName=="mp3"){  //文档名称前的图片
                $("#title").addClass("type-mp3");
                // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
            }
            if(fileSuffixName=="avi"){  //文档名称前的图片
                $("#title").addClass("type-avi");
                // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
            }
            if(fileSuffixName=="wmv"){  //文档名称前的图片
                $("#title").addClass("type-wmv");
                // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
            }
            if(fileSuffixName=="wav"){  //文档名称前的图片
                $("#title").addClass("type-wav");
                // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
            }
            var author=data.author;
            if(author==""||author==undefined){
                author=data.userId;
            }
            // $("#owner").html(""+author+"<em>|</em>");
            // $("#uploadTime").html(""+data.createTime.slice(0,10) +"<em>|</em>");
            // $("#fileSize").html(""+data.fileSize +"<em>|</em>");
            // $("#downloadNum").html(""+data.downloadNum +"次下载"+"<em>|</em>");
            // $("#previewNum").html(""+data.readNum+"次预览" );
            // $("#docAbstract").text(docAbstract);
            $("#owner").html("上传者: "+author+"<em>|</em>");
            $("#uploadTime").html("上传时间: "+data.createTime.slice(0,10) +"<em>|</em>");
            $("#fileSize").html("文件大小: "+data.fileSize +"<em>|</em>");
            $("#downloadNum").html("下载次数: "+"<span>"+data.downloadNum +"</span>"+"<em>|</em>");
            $("#previewNum").html("预览次数: "+"<span>"+data.readNum+"</span>" );
            if(authority=='1'||data.adminFlag==1||authority=='2'){
                $("#dowLoadButton").show();
            }
            if (shareFlag == '1'){
                $("#shareBtn").show();
                $("#shareBtn").css('display','inline-block');
            }
            if(collection=='0'){
                $("#collectionToFolder").show();
                $("#cancelCollection").hide();
            }else{
                $("#collectionToFolder").hide();
                $("#cancelCollection").show();
            }
            var srcLink = "/files/fileDownNew?docName=" + "" + "&docIds=" + docId;
            var collectionLink ="/personalCollection/addCollection";
            var  cancelCollection="/personalCollection/cancelCollection";
            $("#collection").click(function () {
                /*$.ajax({
                    type:"post",
                    url: Hussar.ctxPath+collectionLink,
                    async:false,
                    data:{
                        docIds:docId
                    },
                    success:function(data) {
                        if(data.success=='0'){
                            //alert("收藏成功");
                            $("#collection").hide();
                            $("#cancelCollection").show();
                        }else{
                            alert("收藏失败");

                        }
                    },
                });*/
                var ajax = new $ax(Hussar.ctxPath + collectionLink, function(data) {
                    if(data.success=='0'){
                        //alert("收藏成功");
                        $("#collectionToFolder").hide();
                        $("#cancelCollection").show();
                    }else{
                        alert("收藏失败");

                    }
                }, function(data) {

                });
                ajax.set("docIds",docId);
                ajax.start();
            });
            $("#cancelCollection").click(function () {
                layui.use(['HussarAjax','Hussar'], function(){
                    var Hussar = layui.Hussar,
                        $ax = layui.HussarAjax;

                    /*$.ajax({
                        type:"post",
                        url: Hussar.ctxPath+cancelCollection,
                        async:false,
                        data:{
                            docIds:docId
                        },
                        success:function(data) {
                            if(data.success=='0'){
                                //alert("取消收藏成功");
                                $("#collection").show();
                                $("#cancelCollection").hide();
                            }else{
                                alert("取消收藏失败");

                            }
                        },
                    });*/
                    layer.confirm("确定取消收藏吗？", {
                        title: ['取消收藏', 'background-color:#fff'],
                        skin:'move-confirm'
                    }, function (index) {
                        var ajax = new $ax(Hussar.ctxPath + cancelCollection, function(data) {
                            if (data.success == "0") {
                                $("#collectionToFolder").show();
                                $("#cancelCollection").hide();
                                layer.close(index);
                                layer.msg('取消收藏成功',{icon: 1,offset:'40%'})
                            } else if (data.code) {
                                alert("取消收藏失败");
                                layer.close(index)
                            } else {
                                alert("取消收藏失败");
                                layer.close(index)
                            }
                        }, function(data) {
                            alert("取消收藏失败");
                            layer.close(index)
                        });
                        ajax.set("docIds",docId);
                        ajax.start();
                    })
                });
            });
            // $("#collectionToFolder").on('click',function(){
            //     var operation =function(){
            //         layerView=layer.open({
            //             type : 1,
            //             area: ['400px','434px'],
            //             btn: ['确定','取消'],
            //             //shift : 1,
            //             shadeClose: false,
            //             skin: 'move-class',
            //             title : ['目录结构','background-color:#fff'],
            //             maxmin : false,
            //             offset:scrollHeightLong,
            //             content : $("#filTree"),
            //             success : function() {
            //                 initFileTree();
            //                 layer.close(index);
            //             },
            //             end: function () {
            //                 layer.closeAll(index);
            //             }
            //         });
            //     }
            //     var index = layer.confirm('确定要收藏所选文件吗？',{title :['收藏至','background-color:#fff'], offset:scrollHeightAlert,skin:'move-confirm'},operation);
            // });
            $("#collectionToFolder").on('click', function () {
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
                    content:  "/personalCollection/collectionToFolderView?docId="+docId
                });
                $("#collections").parent().addClass("collectionsContainer")
            })

            //鼠标在收藏夹弹出框上时阻止滚动
            var preventDefault = function (e) {
                e.preventDefault();
            };
            $(document).on('mouseenter',".collectionsContainer",function (e) {
                $(document).on('scroll',preventDefault(e))
            });
            $(document).on('mouseleave',".collectionsContainer",function (e) {
                $(document).off('scroll',preventDefault(e))
            });

            $("#dowLoadButton").on('click',function(){
                /*$.ajax({
                    url: Hussar.ctxPath+"/integral/downloadIntegral",
                    async: true,
                    data: {
                        docId: docId,
                        ruleCode: 'download'
                    },
                    success: function (data) {
                        if (data.status == "1") {
                            var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                icon: 3,
                                title: '提示'
                            }, function (index) {
                                layer.close(index2);
                                var valid = true;
                                $.ajax({
                                    url: Hussar.ctxPath+"/integral/addIntegral",
                                    async: false,
                                    data: {
                                        docId: docId,
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
                                    $.ajaxFileUpload({
                                        url: Hussar.ctxPath+"/files/fileDownNew",
                                        type: "post",
                                        data: {
                                            docName: "",//fileName,
                                            docIds: docId
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
                                    docId: docId,
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
                                $.ajaxFileUpload({
                                    url: Hussar.ctxPath+"/files/fileDownNew",
                                    type: "post",
                                    data: {
                                        docName: "",//fileName,
                                        docIds: docId
                                    }
                                });
                            }
                        }
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {
                    if (data.status == "1") {
                        var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                            icon: 3,
                            title: '提示',
                            skin:'download-info'
                        }, function (index) {
                            layer.close(index2);
                            var valid = true;
                            /*$.ajax({
                                url: Hussar.ctxPath+"/integral/addIntegral",
                                async: false,
                                data: {
                                    docId: docId,
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
                            ajax.set("docId",docId);
                            ajax.set("ruleCode",'download');
                            ajax.start();
                            if (valid) {
                                $.ajaxFileUpload({
                                    url: Hussar.ctxPath+"/files/fileDownNew",
                                    type: "post",
                                    data: {
                                        docName: "",//fileName,
                                        docIds: docId
                                    }
                                });
                            }
                        })
                    } else {
                        var valid = true;
                        /*$.ajax({
                            url: Hussar.ctxPath+"/integral/addIntegral",
                            async: false,
                            data: {
                                docId: docId,
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
                        ajax.set("docId",docId);
                        ajax.set("ruleCode",'download');
                        ajax.start();
                        if (valid) {
                            $.ajaxFileUpload({
                                url: Hussar.ctxPath+"/files/fileDownNew",
                                type: "post",
                                data: {
                                    docName: "",//fileName,
                                    docIds: docId
                                }
                            });
                        }
                    }
                }, function(data) {

                });
                ajax.set("docId",docId);
                ajax.set("ruleCode",'download');
                ajax.start();
            });
            $("#shareBtn").click(function () {
                openShare('', '/s/shareConfirm', 538, 390,docId,docType,fileName);
            });            //$("#dowLoadButton").attr('href',srcLink)
            var username = $("#username").val();
            document.getElementById("s1").src=Hussar.ctxPath+filePath;
            document.getElementById("audio").load();
        }, function(data) {
            $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
        });
        ajax.set("id",docId);
        ajax.start();

        /*$.ajax({
            async: false,
            type: "post",
            url: Hussar.ctxPath+"/preview/getFoldPath",
            data: {docId: docId},
            success: function (data) {
                var path ="";
                if(!!data){
                    for(var i=0;i<data.length;i++){
                        path +=" <span><a href='#' target='_blank' data-id='"+data[i].foldId+"'>"+data[i].foldName+"</a> <i class='layui-icon'>&#xe602;</i> </span>";
                    }
                }else{
                    path ="";
                }
                path +=" <span>"+fileName+"</span>";
                var pathes = $(".pathes");
                pathes.html(path);

                pathes.on("click","a",function(){
                    if(data[0].type!="2"){
                    var parentId = $(".pathes a").first().data("id");
                    var curId = $(this).data("id");
                    var index =$(this).parent().index();
                    if(index == 0){
                        $(this).attr("href",Hussar.ctxPath+"/personalcenter?fileId="+parentId)
                    }else{
                        var folderName = data[index].foldName;
                        var folderId = data[index].foldId;
                        $(this).attr("href",Hussar.ctxPath+"/personalcenter?menu=11&folderId="+folderId+"&folderName="+folderName
                        );
                    }
                    }else {
                        var curId = $(this).data("id");
                        $(this).attr("href",Hussar.ctxPath+"/toShowComponent/toShowPDF?id="+curId);


                    }
                });
                //获取总长度与父级长度
                var pathes_span = pathes.find("span"),box_length = 0;
                var outlength = $(".breadCrumbs").width() - 70;
                pathes_span.each(function () {
                    box_length = box_length + $(this).width();
                });
                pathes.width(box_length);
                var innerlength,subLength = 0;
                if(box_length>outlength){
                    for(var n = 0;n<pathes_span.length;n++){
                        subLength = subLength + pathes_span.eq(n).width();
                        innerlength = box_length - subLength;
                        if(innerlength <= outlength){
                            $(".control-btn-l").show();
                            $(".control-btn-r").hide();
                            pathes.css({"transform":"translateX(-"+subLength+"px)"});
                            break;
                        }
                    }
                }
                var subLength_l = subLength;
                $(".control-btn-l").click(function () {
                    var subLength_l = subLength_l - outlength;
                    if(subLength_l > 0){
                        pathes.css({"transform":"translateX(-"+subLength_l+"px)"});
                    }else {
                        subLength_l = 0;
                        pathes.css({"transform":"translateX(-"+subLength_l+"px)"});
                        $(".control-btn-l").hide();
                        $(".control-btn-r").show();
                    }
                });
                var subLength_r = box_length;
                var rLength = 0;
                $(".control-btn-r").click(function () {
                    subLength_r = subLength_r - outlength;
                    if(subLength_r > outlength){
                        rLength = rLength +outlength;
                        pathes.css({"transform":"translateX(-"+rLength+"px)"});
                    }else {
                        $(".control-btn-l").show();
                        $(".control-btn-r").hide();
                        pathes.css({"transform":"translateX(-"+subLength+"px)"});
                    }
                });
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/preview/getFoldPath", function(data) {
            var path ="<span><a href=\""+Hussar.ctxPath+"\/\" target=\"_blank\" data-id=\"01\">首页</a> <i class=\"layui-icon\"></i> </span>";

            if(!!data){
                for(var i=0;i<data.length;i++){
                    path +=" <span><a href='#' target='_blank' data-id='"+data[i].foldId+"'>"+data[i].foldName+"</a> <i class='layui-icon'>&#xe602;</i> </span>";
                }
            }else{
                path ="";
            }
            path +=" <span>"+fileName+"</span>";
            var pathes = $(".pathes");
            pathes.html(path);

            pathes.on("click","a",function(){
                if(data[0].type=="3"){
                    var curId = $(this).data("id");
                    $(this).attr("href","/toShowConsulation/toShowPDF?id="+curId);
                }else if(data[0].type=="2"){
                    var curId = $(this).data("id");
                    $(this).attr("href","/toShowComponent/toShowPDF?id="+curId);
                }else {
                    var parentId = $(".pathes a").first().data("id");
                    var curId = $(this).data("id");
                    var index =$(this).parent().index()-1;
                    if(index ==-1){$(this).attr("href",Hussar.ctxPath+"/")
                    }else if(index == 0){
                        $(this).attr("href",Hussar.ctxPath+"/personalcenter?fileId="+parentId)
                    }else{
                        var folderName = data[index].foldName;
                        var folderId = data[index].foldId;
                        $(this).attr("href",Hussar.ctxPath+"/personalcenter?menu=11&folderId="+folderId+"&folderName="+folderName
                        );
                    }
                }

            });
            //获取总长度与父级长度
            var pathes_span = pathes.find("span"),box_length = 0;
            var outlength = $(".breadCrumbs").width() - 70;
            pathes_span.each(function () {
                box_length = box_length + $(this).width();
            });
            pathes.width(box_length);
            var innerlength,subLength = 0;
            if(box_length>outlength){
                for(var n = 0;n<pathes_span.length;n++){
                    subLength = subLength + pathes_span.eq(n).width();
                    innerlength = box_length - subLength;
                    if(innerlength <= outlength){
                        $(".control-btn-l").show();
                        $(".control-btn-r").hide();
                        pathes.css({"transform":"translateX(-"+subLength+"px)"});
                        break;
                    }
                }
            }
            var subLength_l = subLength;
            $(".control-btn-l").click(function () {
                var subLength_l = subLength_l - outlength;
                if(subLength_l > 0){
                    pathes.css({"transform":"translateX(-"+subLength_l+"px)"});
                }else {
                    subLength_l = 0;
                    pathes.css({"transform":"translateX(-"+subLength_l+"px)"});
                    $(".control-btn-l").hide();
                    $(".control-btn-r").show();
                }
            });
            var subLength_r = box_length;
            var rLength = 0;
            $(".control-btn-r").click(function () {
                subLength_r = subLength_r - outlength;
                if(subLength_r > outlength){
                    rLength = rLength +outlength;
                    pathes.css({"transform":"translateX(-"+rLength+"px)"});
                }else {
                    $(".control-btn-l").show();
                    $(".control-btn-r").hide();
                    pathes.css({"transform":"translateX(-"+subLength+"px)"});
                }
            });
        }, function(data) {

        });
        ajax.set("docId",docId);
        ajax.set("showType",$("#showType").val());
        ajax.start();

        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/fsFile/cacheViewNum",
            data:{
                docId:docId,
            },
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/cacheViewNum", function(data) {

        }, function(data) {

        });
        ajax.set("docId",docId);
        ajax.start();

        //积分系统控制
        /*$.ajax({
            url: Hussar.ctxPath+"/integral/addIntegral",
            async: true,
            data:{
                docId: docId,
                ruleCode: 'preview'
            },
            success: function (data) {
                if (null != data && data != '' && data != undefined){
                    if (data.integral != 0 && data.integral != null && data.integral != ''){
                        $("#num").html(data.msg)
                        $(".integral").show();
                        setTimeout(function () {
                            $(".integral").hide();
                        },2000)
                    }
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
            if (null != data && data != '' && data != undefined){
                if (data.integral != 0 && data.integral != null && data.integral != ''){
                    $("#num").html(data.msg)
                    $(".integral").show();
                    setTimeout(function () {
                        $(".integral").hide();
                    },2000)
                }
            }
        }, function(data) {

        });
        ajax.set("docId",docId);
        ajax.set("ruleCode",'preview');
        ajax.start();
    });
});
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
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layer.open({
            type: 2,
             area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,

            shade: 0.4,
            title: title,
            content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURI(fileName)
        });
    });
}
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}

function showCancelCollection() {
    $("#cancelCollection").show();
    $("#collectionToFolder").hide();
}

function hideCancelCollection() {
    $("#cancelCollection").hide();
    $("#collectionToFolder").show();
}
function showUpLevel() {
    $("#upLevel").html("<i class='layui-icon' style='cursor:pointer;'>&#xe65c;</i>");
}
function hideUpLevel() {
    $("#upLevel").html('收藏');
}
function upLevel(){
    var contentWindow = $("#layui-layer-iframe" + layerOpen1)[0].contentWindow;
    contentWindow.upLeveChild();
}
