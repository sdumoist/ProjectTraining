/**
 * Created by Administrator on 2018/12/6.
 */
/**
 * Create By luzhanzhao
 * date 2018-11-19
 */
var chooseFile = [];    //选中的文件或目录的id
var chooseFileType = []; //选中的文件或目录的type
var clickFlag=false;
var currOrder = '';
var scrollHeightAlert=0;
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var opType=$("#opType").val();
var pathId = [];        //路径
var pathName = [];
var openFileId;   //打开的文件夹的id
var cutFile = [];          //剪切的文件或目录的id
var cutFileType = [];      //剪切的文件或目录的type
var cutFileName = [];      //剪切的文件或目录的name
var isCreatingCollectFolder = false; //是否正在创建收藏夹
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;
    start();
    // $("#upLevel").on('click',function(){
    //     refreshFile(openFileId);
    // });
    function openWin(url) {
        var a = document.createElement("a"); //创建a标签
        a.setAttribute("href", url);
        a.setAttribute("target", "_blank");
        document.body.appendChild(a);
        a.click(); //执行当前对象
    }
    /*新增子目录*/
    $("#newFolder").on('click', function () {
        if (isCreatingCollectFolder) {
            return;
        }
        isCreatingCollectFolder = true;
        // var folderAmountNum = parseInt(folderAmount)+1;
        // if (pathId.length >= folderAmountNum) {
        //     layer.msg("目录最多为"+folderAmount+"级", {anim: 6, icon: 0,offset:scrollHeightMsg});
        //     return false;
        // }
        $("#categoryName").val("");
        $('.name-list').empty();
        var table = $(".layui-table")[0];
        var tbody = document.createElement("tbody");
        var inner ="<tr class='hoverEvent'><td style=\"border-color: rgb(242, 246, 253);border-left: 0;border-right: 0\">" +
            "<span id=\"name1\" class=\"nameTitpe nofolder new-folder\" title=\"创建收藏夹\" style=\"cursor: default\"><a class=\"fileName\" style='padding-left: 32px'>" +
            "<input id='input-newFolder' placeholder='请输入' style='border: 1px solid #DBDBDB' >" +
            "</a></span></td>" +
            "<td style=\"border-color: rgb(242, 246, 253);border-left: 0;border-right: 0\">  <button id=\"noCollection\"\>确定</button>  </td></tr>";
        $(tbody).html(inner);
        $(table).append(tbody);
        $("#input-newFolder").focus();
        var newFolder =  function (openFileId,categoryName) {

            if (categoryName.length <= 0) {
                window.parent.layer.msg("目录名称不能为空", {icon: 0, offset: 'calc(30% - 170px)',id:"collectionError"});
                return false;
            }

            if (categoryName.length > 130) {
                window.parent.layer.msg("目录名称不能超过130个字符", {icon: 0, offset: 'calc(30% - 170px)',id:"collectionError"});

                 return false;
            }
            var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if (!pattern.test(categoryName)) {
                window.parent.layer.msg("输入的目录名称不合法", {icon: 0, offset: 'calc(30% - 170px)',id:"collectionError"});

                 return;
            }

            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/addCheck", function(data) {
                if (data == "false") {
                    parent. layer.msg("\"" + categoryName + "\"目录已存在", {icon: 2,offset:'35%'});
                     isCreatingCollectFolder = false;
                    return;
                } else {
                    var ajax = new $ax(Hussar.ctxPath + "/personalCollection/add", function(result) {
                        refreshFile(openFileId);
                        isCreatingCollectFolder = false;
                        // refreshTree();
                        layer.closeAll();
                    }, function(data) {
                    });
                    ajax.set("parentFolderId",openFileId);
                    ajax.set("resourceName",categoryName);
                    ajax.start();
                }
            }, function(data) {

            });
            ajax.set("name",categoryName);
            ajax.set("parentFolderId",openFileId);
            ajax.start();
        };
        $("#input-newFolder").on("blur",function () {
            var parent_name = $("#input-newFolder").parents("#name1").removeClass("nofolder")
            var categoryName = $("#input-newFolder").val().trim();
            if(categoryName===""){
                refreshFile(openFileId);
            }else {
                newFolder(openFileId,categoryName);
            }
            isCreatingCollectFolder = false;
        });

    });
    function start() {
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");
        btnState()
    }


    $("#cancel").on('click',function(){
        var index = layer.getFrameIndex(window.name);
        layer.close(index);
    });

    // 滚动时父层不滚动
    $(document).on('scroll',function () {
        $(window.parent.document).on('scroll',function (e) {
            e.preventDefault();
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
        pathId = ['root'];
        pathName = ['金现代'];
        createPath()
        refreshFile();
        btnState()
    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth =inner.width();
        //fixed-table-header
        $(".fixed-table-header").width(tableWidth)
    })
});
function createPath(){
    $("#path").empty();
    // $("#path").append("<span class='total'>");
    for(var i=0;i<pathId.length;i++){
        if(i==pathId.length-1){
            var param= '<span style="color:#222;cursor: auto;">'+pathName[i]+'</span>'
        }else {
            var param= '<span><a style="cursor: pointer; color: #3C91FD;" onclick="clickPath(\''+pathId[i]+'\')">'+pathName[i]+'</a>'+'  >  </span>'        }
        $("#path").append(param);


    }
    var timer;
    // $("#path").append("</span>");
    $(".message #path").hover(function () {
        var _this = $(this);
        var len = _this.width();

        var width = _this.parent().width();
        if(len >= width){
            var num = 0;
            var change = 0;
            timer = setInterval(function () {
                if (navigator.userAgent.indexOf('iPad') != -1){
                    change = 240-len;
                }else{
                    change = 290-len;
                }
                if (num <= change) {
                    clearInterval(timer);
                }
                num -= 1;
                _this.css("left",num);
            }, 25);
        }
    },function () {
        clearInterval(timer);
        $(this).css("left",0)
    })

}
function clickPath(id) {
    while(pathId.indexOf(id)+1!=pathId.length){
        pathId.pop();
        pathName.pop();
    }
    createPath();
    refreshFile(id);
}
function updateFolderPid(index, id) {
    layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
        var $ = layui.jquery,
            Hussar = layui.Hussar,
            layer = layui.layer,
            $ax = layui.HussarAjax;
        var cutIds = cutFile.join(",");
        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/update", function (data) {
            if ("success" == data) {
                emptyChoose();
                refreshFile(openFileId);
                layer.closeAll();
            } else {
                parent. layer.msg("移动失败", {icon: 2,offset:'35%'});
                 layer.close(index);
            }
        }, function (data) {

        });
        ajax.set("ids", cutIds);
        ajax.set("parentFolderId", id);
        ajax.start();
    })
}
/*取消收藏*/
function cancelCollection(e,parentFolderId){
    cancelBubble();
    changeBgColorOfTr(e);

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/cancelCollection", function(data) {
            if(data.success == "0"){
                parent.hideCancelCollection($("#docId").val());
            }else {
                parent. layer.msg("收藏失败", {icon: 2,offset:'35%'});

            }
            btnState();
            // refreshTree();
            refreshFile(openFileId);
            emptyChoose();
        }, function(data) {
            parent. layer.msg("取消收藏异常", {icon: 2,offset:'35%'});

            btnState();
            refreshFile(openFileId);
            emptyChoose();
        });
        ajax.set("docIds",$("#docId").val());
        // ajax.set("parentFolderId",parentFolderId);
        ajax.start();
    });
}
function refreshFile(folderId,num,size,order) {
    if (folderId==null){
        folderId = 'abcde4a392934742915f89a586989292'
    }
    var screenHeight = parseInt(window.screen.availHeight);
    var noOrder;
    // if(order==null||order==undefined||order==''){
    //     noOrder=true;
    //     order = '';
    // }
    if (folderId=='null'||folderId==undefined||folderId=='abcde4a392934742915f89a586989292'){
        parent.hideUpLevel()
    }else {
        parent.showUpLevel()
    }
    currOrder = order;
    layui.use(['laypage', 'layer', 'table', 'Hussar', 'HussarAjax'], function () {
        var laypage = layui.laypage,
            Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var name = $('#searchName').val();
        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/collectionToFolderList", function (data) {
            laypage.render({
                elem: 'laypageAre'
                , count: data.count //数据总数，从服务端得到
                , limit: 60
                , layout: ['prev', 'page', 'next']
                , curr: num || 1 //当前页
                , jump: function (obj, first) {
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if (!first) {
                        refreshFile(folderId,obj.curr, obj.limit, currOrder)
                    }
                }
            });
            openFileId = folderId;
            createPath();
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
            if (data.isCollectionToParentFolder){
                $("#collectionToNowFolder2").show()
                $("#collectionToNowFolder1").hide()
            }else {
                $("#collectionToNowFolder1").show()
                $("#collectionToNowFolder2").hide()
            }
        }, function (data) {

        });
        ajax.set("parentFolderId", folderId);
        ajax.set("docId",$("#docId").val());
        ajax.start();
    });
}
/*打开分享链接*/
function share(e,docId,fileSuffixName,fileName) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            async: false,
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
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType: "json",
            success: function (data) {
                if (data.result == "1") {
                    layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
                } else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else {
                    var title = '';
                    var url = "/s/shareConfirm";
                    var w =  538;
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
                        skin:'share-dialog',
                        title: title,
                        closeBtn:2,
                        offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                        content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                    });
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if (data.result == "1") {
                parent. layer.msg("该文件已被删除", {icon: 2,offset:'35%'});
            } else if(data.result =="5"){
                parent. layer.msg("该文件不是最新版本", {icon: 2,offset:'35%'});

            }else {
                var title = '';
                var url = "/s/shareConfirm";
                var w =  538;
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
                    closeBtn:2,
                    offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                    content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                });
            }
        }, function(data) {

        });
        ajax.set("ids",docId);
        ajax.start();
    });
}
function drawFile(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param.rows,
            "adminFlag":param.adminFlag
        };
        var getTpl = $("#demo1").html()
            ,view = document.getElementById('view');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view");
            var tableWidth =inner.width();
            //fixed-table-header
            $(".fixed-table-header").width(tableWidth);
            if (param.parentFolderId != 'abcde4a392934742915f89a586989292'){
                $("#newFolder").hide();

            }else {
                $("#newFolder").show();
            }
        });
    });

}
function dbclick(id,type,name){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        pathId.push(id);
        pathName.push(name);
        createPath()
        refreshFile(id);

    });
}

function download(id,name){
    //cancelBubble();
    layui.use(['Hussar','HussarAjax'], function() {
        var Hussar = layui.Hussar;
        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew?docIds="+id,
            type : "post",
            async:false
        });
    })
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}
function  iconDownLoad(e,id,name) {
    cancelBubble();
    changeBgColorOfTr(e);

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/personalcenter/getInfo",
            data:{
                ids:id
            },
            async:false,
            cache:false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType:"json",
            success:function(data){
                if(data.result =="1"){
                    layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
                }
                else if(data.result =="2"||data.result =="3"){
                    layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
                }else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else{
                    $.ajax({
                        url: Hussar.ctxPath+"/integral/downloadIntegral",
                        async: true,
                        data: {
                            docId: id,
                            ruleCode: 'download'
                        },
                        success: function (data) {
                            if(data.status=="1") {

                                var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                    icon: 3,
                                    title: '提示',
                                    offset: scrollHeightAlert
                                }, function (index) {
                                    layer.close(index2);
                                    var index = layer.load(1, {
                                        shade: [0.1, '#fff'],//0.1透明度的白色背景
                                        scrollbar: false,
                                        time: 1000
                                        ,offset: scrollHeightAlert
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
                            }else {
                                var index = layer.load(1, {
                                    shade: [0.1, '#fff'],//0.1透明度的白色背景
                                    scrollbar: false,
                                    time: 1000
                                    ,offset: scrollHeightAlert
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
                        }})
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if(data.result =="1"){
                parent. layer.msg("该文件已被删除", {icon: 2,offset:'35%'});
            }
            else if(data.result =="2"||data.result =="3"){
                parent. layer.msg("您没有权限", {icon: 2,offset:'35%'});
             }else if(data.result =="5"){
                parent. layer.msg("该文件不是最新版本", {icon: 2,offset:'35%'});
             }else{
                /*$.ajax({
                    url: Hussar.ctxPath+"/integral/downloadIntegral",
                    async: true,
                    data: {
                        docId: id,
                        ruleCode: 'download'
                    },
                    success: function (data) {
                        if(data.status=="1") {

                            var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                                icon: 3,
                                title: '提示',
                                offset: scrollHeightAlert
                            }, function (index) {
                                layer.close(index2);
                                var index = layer.load(1, {
                                    shade: [0.1, '#fff'],//0.1透明度的白色背景
                                    scrollbar: false,
                                    time: 1000
                                    ,offset: scrollHeightAlert
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
                        }else {
                            var index = layer.load(1, {
                                shade: [0.1, '#fff'],//0.1透明度的白色背景
                                scrollbar: false,
                                time: 1000
                                ,offset: scrollHeightAlert
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
                    }})*/
                var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {
                    if(data.status=="1") {

                        var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                            icon: 3,
                            title: '提示',
                            offset: scrollHeightAlert,
                            skin:'download-info',
                        }, function (index) {
                            layer.close(index2);
                            var index = layer.load(1, {
                                shade: [0.1, '#fff'],//0.1透明度的白色背景
                                scrollbar: false,
                                time: 1000
                                ,offset: scrollHeightAlert
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
                    }else {
                        var index = layer.load(1, {
                            shade: [0.1, '#fff'],//0.1透明度的白色背景
                            scrollbar: false,
                            time: 1000
                            ,offset: scrollHeightAlert
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
function showPdf(id,fileSuffixName,name) {
    var keyword =  name;
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
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
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
function btnState() {
    if(chooseFile.length==0){
        $('.clickBtn').hide()
    }
    if(chooseFile.length>0){
        if(chooseFile.length>1){
            for(var i=0;i<chooseFileType.length;i++){
                if(chooseFileType[i+1]!=undefined){
                    if((chooseFileType[i]=="folder"&&chooseFileType[i+1]!="folder")
                        ||(chooseFileType[i]!="folder"&&chooseFileType[i+1]=="folder")){
                        $('.clickBtn').hide();
                        $(".uploadBtn").hide();
                        return;

                    }}
            }
        }else {
            $('.clickBtn').show();
        }
    }
}
function changeBgColorOfTr(e){
    var jq=$(e);
    //console.log(e.tagName.toLowerCase());
    if (e.tagName.toLowerCase() != "tr"){
        jq = jq.parents(".hoverEvent");
    }
    jq.parent().find("tr").css("background-color","#fff");

    jq.css("background-color","rgba(246, 250, 255, 1)");
}
function  clickCheck(e,id) {
    var jq=$(e);
    changeBgColorOfTr(e);
    btnState();

    cancelBubble()
}
function checkAll(e) {
    var self = $(e).siblings('input');//
    $(e).toggleClass('layui-form-checked');
    if(self.prop("checked")==false){
        self.prop("checked",true);
    }else{
        self.prop("checked",false);
    }
    if (self.prop("checked")) { // 全选
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", true);
            $(this).siblings('.layui-form-checkbox').addClass("layui-form-checked");
            var checkFileId=  $(this).siblings(".checkFileId").val();
            var checkFileType=  $(this).siblings(".checkFileType").val()
            var checkFileName=  $(this).siblings(".checkFileName").val()
            var checkFileAuthor=  $(this).siblings(".chooseFileAuthor").val()
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
        chooseFileType=[];
        chooseFileName=[];
        chooseFile=[];
        chooseFileAuthor=[]
    }
    btnState();
}
Array.prototype.del=function(n) {
    if(n<0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0,n).concat(this.slice(n+1,this.length));
}
function emptyChoose() {
    chooseFile = [];
    chooseFileType = [];
    chooseFileName = [];
    chooseFileAuthor=[]
}
function  clickIconCheck(e,id,type,name,index,author) {
    // $(e).toggleClass('layui-form-checked');
    // var checkbox=$(e).siblings('.checkbox');
    // if(checkbox.prop("checked")==false){
    //     checkbox.prop("checked",true);
    //     chooseFile.push(id);
    // }else{
    //     checkbox.prop("checked",false);
    //     if(chooseFile.indexOf(id)!=-1){
    //         chooseFile=chooseFile.del(chooseFile.indexOf(id));
    //     }
    // }
    //
    // btnState();
    // cancelBubble()
    $(e).toggleClass('layui-form-checked');
    var checkbox=$(e).siblings('.checkbox');
    if(checkbox.prop("checked")==false){

        checkbox.prop("checked",true);

        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)


    }else{
        checkbox.prop("checked",false);
        if(chooseFile.indexOf(id)!=-1){
            chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
            chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
            chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
        }
    }

    btnState();
    cancelBubble()
}
//得到事件
function getEvent(){
    if(window.event)    {return window.event;}
    func=getEvent.caller;
    while(func!=null){
        var arg0=func.arguments[0];
        if(arg0){
            if((arg0.constructor==Event || arg0.constructor ==MouseEvent
                || arg0.constructor==KeyboardEvent)
                ||(typeof(arg0)=="object" && arg0.preventDefault
                    && arg0.stopPropagation)){
                return arg0;
            }
        }
        func=func.caller;
    }
    return null;
}
//阻止冒泡
function cancelBubble()
{
    var e=getEvent();
    if(window.event){
        //e.returnValue=false;//阻止自身行为
        e.cancelBubble=true;//阻止冒泡
    }else if(e.preventDefault){
        //e.preventDefault();//阻止自身行为
        e.stopPropagation();//阻止冒泡
    }
}
function tryPop(th,id,type,name,index,author){
    if(chooseFile.indexOf(id)==-1) {
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
            reNameParem=name;
        }
    }
    if($(th).prop("checked")){
        chooseFile.push(id);
    }else{
        if(chooseFile.indexOf(id)!=-1){
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
        }
    }
    btnState();
    cancelBubble()
}
$(function(){

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

    $('#searchName').bind('keypress', function (event) {
        if (event.keyCode == "13") {
            $("#searchBtn").click();
        }
    });
})
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").css("top",scrollHeightAlert);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}
function checkAll(e) {
    var self = $(e).siblings('input');//
    $(e).toggleClass('layui-form-checked');
    if(self.prop("checked")==false){
        self.prop("checked",true);
    }else{
        self.prop("checked",false);
    }
    if (self.prop("checked")) { // 全选
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", true);
            $(this).siblings('.layui-form-checkbox').addClass("layui-form-checked");
            var checkFileId=  $(this).siblings(".checkFileId").val();
            var checkFileType=  $(this).siblings(".checkFileType").val()
            var checkFileName=  $(this).siblings(".checkFileName").val()
            var checkFileAuthor=  $(this).siblings(".chooseFileAuthor").val()
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
        chooseFileType=[];
        chooseFileName=[];
        chooseFile=[];
        chooseFileAuthor=[]
    }
    btnState();
}

function tryPop(th,id,type,name,index,author){
    if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile(openFileId)
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if($(th).prop("checked")){
        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)
    }else{
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFile=chooseFile.del(chooseFile.indexOf(id));
                chooseFileAuthor=  chooseFileAuthor.del(chooseFile.indexOf(id))
            }
        }
        if(chooseFile.length==1){
            var id = chooseFile[0];
            reNameParem=chooseFileName[0];
            var index=  $("#"+id+"").val();
            reNameIndex=index
        }
    }
    btnState();
    cancelBubble()
}
function collectionToFolder(resourceId,parentFolderId) {
    layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
        var $ = layui.jquery,
            Hussar = layui.Hussar,
            layer = layui.layer,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/collectionToFolder", function (data) {
            if (data.result == "0") {
                parent. layer.msg("文件已收藏", {icon: 2,offset:'35%'});
            } else if (data.result == "1") {
                parent.showCancelCollection($("#docId").val())
            } else {
                parent. layer.msg("收藏失败", {icon: 2,offset:'35%'});
            }
        }, function (data) {
            parent. layer.msg("系统出错，请联系管理员", {icon: 2,offset:'35%'});

         });
        ajax.set("ids", $("#docId").val());
        ajax.set("parentFolderId", resourceId);
        ajax.start();
    })
    refreshFile(parentFolderId)
}

function collectionToNowFolder() {
    if (openFileId==undefined){
        openFileId = 'abcde4a392934742915f89a586989292'
    }
    layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
        var $ = layui.jquery,
            Hussar = layui.Hussar,
            layer = layui.layer,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/collectionToFolder", function (data) {
            if (data.result == "0") {
                parent. layer.msg("文件已收藏", {icon: 2,offset:'35%'});
            } else if (data.result == "1") {
                parent.showCancelCollection($("#docId").val());
                $("#collectionToNowFolder2").show()
                $("#collectionToNowFolder1").hide()
            } else {
                parent. layer.msg("收藏失败", {icon: 2,offset:'35%'});

            }
        }, function (data) {
            parent. layer.msg("系统出错，请联系管理员", {icon: 2,offset:'35%'});

         });
        ajax.set("ids", $("#docId").val());
        ajax.set("parentFolderId", openFileId);
        ajax.start();
    })
    refreshFile(openFileId)
}
function upLeveChild(){
    if(pathId.length==2){
        pathName.pop();
        pathId.pop();
        refreshFile();
        return;
    }
    var index = layer.load(1, {
        shade: [0.1,'#fff'] //0.1透明度的白色背景
        ,offset: scrollHeightAlert
    });
    createPath();
    refreshFile(pathId[pathId.length-2]);
    pathName.pop();
    pathId.pop();
    layer.close(index);
   // alert(22222)
};

/*取消收藏*/
function cancelCollectionFromNowFolder(){
    cancelBubble();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/personalCollection/cancelCollection", function(data) {
            if(data.success == "0"){
                parent.hideCancelCollection($("#docId").val());
                $("#collectionToNowFolder1").show();
                $("#collectionToNowFolder2").hide()
            }else {
                parent. layer.msg("收藏失败", {icon: 2,offset:'35%'});
            }
            btnState();
            // refreshTree();
            refreshFile(openFileId);
            emptyChoose();
        }, function(data) {
            parent. layer.msg("取消收藏异常", {icon: 2,offset:'35%'});

            btnState();
            refreshFile(openFileId);
            emptyChoose();
        });
        ajax.set("docIds",$("#docId").val());
        // ajax.set("parentFolderId",parentFolderId);
        ajax.start();
    });
}