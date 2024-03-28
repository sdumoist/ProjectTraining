var chooseFile = [];    //选中的文件或目录的id
var clickFlag=false;
var opType="3";
var currOrder = '';
var scrollHeightAlert=0;
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var layerView;
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar','jstree'], function() {
    var $ = layui.jquery,
        form = layui.form,
        jstree = layui.jstree,
        laypage = layui.laypage,
        laytpl = layui.laytpl,
        Hussar = layui.Hussar,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;

    /*搜索按钮*/
    $("#searchBtn").on('click',function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
            ,offset: scrollHeightAlert
        });
        refreshFile(null,null);
        layer.close(index);
    });

    $(function () {
        refreshFile();

    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth =inner.width();
        //fixed-table-header
        $(".fixed-table-header").width(tableWidth)
    })

})

function refreshFile(num,size,order){
    var screenHeight = parseInt(window.screen.availHeight);
    //console.log(screenHeight);
    if (screenHeight > 728) {
        $("#marg").css("min-height","768");
    }
    var noOrder;
    /* if(order==null||order==undefined||order==''){
         noOrder=true;
         order = '';
     }*/
    currOrder = order;
    layui.use(['laypage','layer','table','Hussar','HussarAjax'], function(){
        var laypage = layui.laypage;
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var name = $('#searchName').val();
        var ajax = new $ax(Hussar.ctxPath + "/personalShare/list", function(data) {
            laypage.render({
                elem: 'laypageAre'
                ,count: data.count //数据总数，从服务端得到
                ,limit: 60
                ,layout: ['prev', 'page', 'next']
                ,curr: num || 1 //当前页
                ,jump: function(obj, first){
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if(!first){
                        refreshFile(obj.curr,obj.limit,currOrder)
                    }
                }
            });
            $("#amount").html("已全部加载" + data.count+"个")
            $(".total").width($(".message").width() - 26*2 - 65 - $("#amount").width() )
            drawFile(data);
            dbclickover = true;
            //$("th").hover(function () {
            $(".clickEvent").click(function () {
                cancelBubble();
                var  index = $(this).next().val();
                if(size==null||size==undefined){
                    size=60;
                }

                if(num==null||num==undefined){
                    num=1;
                }
                if((size*num)>data.total){
                    var lack = data.total%size;
                    if((lack-1==index||lack-2==index||lack-3==index||lack-4==index||lack-5==index||lack-6==index)&&index>=6){
                        $(this).next().next().css("bottom","30px");
                    }
                }

                if(data.total>size*(num-1)){
                    if(index==(size-1)||index==(size-2)||index==(size-3)||index==(size-4)||index==(size-5)||index==(size-6)){
                        $(this).next().next().css("bottom","30px");
                    }
                }

                $(this).parent().find(".moreicon").show();
            })
            if(noOrder==true){
                $("#orderName").hide();
                $("#orderName1").hide();
                $("#orderTime").hide();
                $("#orderTime1").hide();
                $("#orderUser").hide();
                $("#orderUser1").hide();
                $("#orderSize").hide();
                $("#orderSize1").hide();
                $("#orderValidTime").hide();
                $("#orderValidTime1").hide();
            }else{
                if(order== "1"){
                    $("#orderName").hide();
                    $("#orderName1").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderValidTime").hide();
                    $("#orderValidTime1").hide();
                }
                if(order== "0"){
                    $("#orderName1").hide();
                    $("#orderName").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderValidTime").hide();
                    $("#orderValidTime1").hide();
                }
                if(order== "2"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime1").hide();
                    $("#orderTime").show();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderValidTime").hide();
                    $("#orderValidTime1").hide();
                }
                if(order== "3" ){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").show();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderValidTime").hide();
                    $("#orderValidTime1").hide();
                }
                if(order== "4"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").show();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderValidTime").hide();
                    $("#orderValidTime1").hide();
                }
                if(order== "5"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").show();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderValidTime").hide();
                    $("#orderValidTime1").hide();
                }
                if(order== "6"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").show();
                    $("#orderSize1").hide();
                    $("#orderValidTime").hide();
                    $("#orderValidTime1").hide();
                }
                if(order== "7"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").show();
                    $("#orderValidTime").hide();
                    $("#orderValidTime1").hide();
                }
                if(order== "9"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderValidTime").show();
                    $("#orderValidTime1").hide();
                }
                if(order== "8"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderUser").hide();
                    $("#orderUser1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderValidTime").hide();
                    $("#orderValidTime1").show();
                }
            }
            //}, function () {
            //    $(this).find("#orderTime").hide();
            //    $(this).find("#orderTime1").hide();
            //})
            $(".hoverEvent").hover(function(){
                var ishover= $(this).find(".ishover");
                if(ishover.is(':hidden')){
                    $(".moreicon").hide();
                    $(this).find("td  #hoverSpan").show();
                }else{
                    $(this).find("td  #hoverSpan").hide();
                }

            },function(){
                $(this).find("td  #hoverSpan").hide();
            });
            $(".layui-table tr").hover(function () {
                //alert($(this).prev());
                $(this).find("td").css("border-color","#DAEBFE");
                $(this).prev().find("td").css("border-color","#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color","rgba(242,246,253,1)");
                $(this).prev().find("td").css("border-color","rgba(242,246,253,1)");
            });
            $(".layui-table tbody tr:first").hover(function () {
                $(this).find("td").css("border-color","#DAEBFE");
                $("thead").find("tr").css("border-bottom-color","#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color","rgba(242,246,253,1)");
                $("thead").find("tr").css("border-bottom-color","rgba(242,246,253,1)");
            })
            if(data.count==0){
                $("#laypageAre").hide();
            }else {
                $("#laypageAre").show();
            }
        }, function(data) {

        });
        ajax.set("pageNumber",num);
        ajax.set("pageSize",size);
        ajax.set("name",name);
        ajax.set("order",currOrder);
        ajax.start();
    });
}

function dbclick(id,type,name){
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
            dataType:"json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success:function(data){
                if(data.result =="1"){
                    layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
                }
                else if(data.result =="2"){
                    layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
                }else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0,offset:scrollHeightMsg});
                }else{
                    if(type!="folder"){
                    if(dbclickover==true) {
                        if (clickFlag) {//取消上次延时未执行的方法
                            clickFlag = clearTimeout(clickFlag);
                        }
                        dbclickover=false;
                        reNameFlag = false;

                            showPdf(id, type, name)

                    }}
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if(data.result =="1"){
                layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
            }
            else if(data.result =="2"){
                layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
            }else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0,offset:scrollHeightMsg});
            }else{
                if(type!="folder"){
                    if(dbclickover==true) {
                        if (clickFlag) {//取消上次延时未执行的方法
                            clickFlag = clearTimeout(clickFlag);
                        }
                        dbclickover=false;
                        reNameFlag = false;

                        showPdf(id, type, name)

                    }}
            }
        }, function(data) {

        });
        ajax.set("ids",id);
        ajax.start();
    });
}
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
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

function drawFile(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param.rows.data,
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
            if (param.rows.data.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                    $("#laypageAre").hide();
                },300);
            }else {
                $("div.noDataTip").hide();
                $("#laypageAre").show();
            }
        });
    });
}
function  cancelShare(id) {
    layer.confirm('确定要取消分享吗？',{title :['取消分享','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},function(){
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

            /*$.ajax({
                url: Hussar.ctxPath+"/personalShare/cancelShare",
                async: true,
                data: {
                    docId: id,
                },
                success: function (data) {
                    if(data.isSuccess==true){
                        layer.msg('取消分享成功',{icon: 1,offset:scrollHeightMsg})
                    }else{
                        layer.msg('取消分享失败',{icon: 1,offset:scrollHeightMsg})
                    }
                    // refreshTree();
                    refreshFile();
                    layer.close();
                },
                error:function () {
                    layer.msg('取消分享异常',{icon: 1,offset:scrollHeightMsg});
                    refreshFile(openFileId);
                    layer.close();
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/personalShare/cancelShare", function(data) {
                if(data.isSuccess==true){
                    layer.msg('取消分享成功',{icon: 1,offset:scrollHeightMsg})
                }else{
                    layer.msg('取消分享失败',{icon: 1,offset:scrollHeightMsg})
                }
                // refreshTree();
                refreshFile();
                layer.close();
            }, function(data) {
                layer.msg('取消分享异常',{icon: 1,offset:scrollHeightMsg});
                refreshFile(openFileId);
                layer.close();
            });
            ajax.set("docId",id);
            ajax.start();
        });
    })

}

function  copyShare(url,pwdFlag,password,createTime,validTime) {
    /*
        layer.confirm('确定要复制吗？',{title :['复制链接','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},function(){
    */
    layui.use(['Hussar','HussarAjax','jquery', 'laytpl', 'layer', 'form'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax, $ = layui.jquery, laytpl = layui.laytpl, layer = layui.layer, form = layui.form ;
        var ajax = new $ax(Hussar.ctxPath + "/personalShare/getServerAddress", function(data) {
            if (data != "" && data != undefined) {
                //$("#copyContent").val(data+url);
                var cArr = createTime.split(" ");
                var vArr = validTime.split(" ");
                var sArr = vArr[0].split("-");
                var eArr = cArr[0].split("-");
                var sRDate = new Date(sArr[0], sArr[1], sArr[2]);
                var eRDate = new Date(eArr[0], eArr[1], eArr[2]);
                var days = (sRDate-eRDate)/(24*60*60*1000);
                var content = "";
                if (days > 7) {
                    content = '链接永久有效';
                }else {
                    content = "链接有效期"+ days + "天";
                }
                var copyContent = "链接地址（" + content+ "）：" + data+url;
                if (pwdFlag =='1'){
                    copyContent += "  \t提取码：" + password;
                }
                copyContent += "      - 金企文库";

                var clipboard = new ClipboardJS('#copyContent', {
                    text : function() {
                        return copyContent;
                    }
                });
                clipboard.on('success', function(e) {
                    clipboard.destroy();
                    layer.msg('复制分享链接成功!',{icon: 1,offset:scrollHeightMsg})
                });

                clipboard.on('error', function(e) {
                    layer.msg('复制分享链接失败!',{icon: 1,offset:scrollHeightMsg})
                });
                $("#copyContent").click();
            }else {
                layer.msg('复制分享链接异常!',{icon: 1,offset:scrollHeightMsg})
            }
        }, function(data) {
            layer.msg('复制分享链接异常！',{icon: 1,offset:scrollHeightMsg});
            //refreshFile(openFileId);
            //layer.close();
        });
        //ajax.set("docId",id);
        ajax.start();
    });
    /*})*/

}

function  iconDownLoad(id,name) {
    //cancelBubble()
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
                        }
                    })
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if(data.result =="1"){
                layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
            }
            else if(data.result =="2"||data.result =="3"){
                layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
            }else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
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
                    }
                })*/
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

function download(id,name){
    //cancelBubble();
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew?docIds="+id,
            type : "post",
            async:false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8"
        });
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}

function orderByTime(){
    if ($("#orderTime").css("display") != "none"){
        getTimeOrder();
    }else {
        getTimeOrder1();
    }
}
function orderByName(){
    if ($("#orderName").css("display") != "none"){
        getNameOrder();
    }else {
        getNameOrder1();
    }
}
function orderByUser(){
    if ($("#orderUser").css("display") != "none"){
        getUserOrder();
    }else {
        getUserOrder1();
    }
}
function orderBySize(){
    if ($("#orderSize").css("display") != "none"){
        getSizeOrder();
    }else {
        getSizeOrder1();
    }
}
function orderByValidTime(){
    if ($("#orderValidTime").css("display") != "none"){
        getValidTimOrder();
    }else {
        getValidTimOrder1();
    }
}
function getValidTimOrder() {
    refreshFile(null, null, 8);
}
function getValidTimOrder1() {
    refreshFile(null, null, 9)
}
function getTimeOrder() {
    refreshFile(null, null, 3);
}

function getTimeOrder1() {
    refreshFile(null, null, 2)
}
function getNameOrder() {
    refreshFile(null,null,1);
}
function getNameOrder1() {
    refreshFile(null,null,0);
}
function getUserOrder() {
    refreshFile(null,null,5);
}
function getUserOrder1() {
    refreshFile(null, null, 4);
}
function getSizeOrder() {
    refreshFile(null, null, 7);
}
function getSizeOrder1() {
    refreshFile(null, null, 6)
}
function  clickCheck(e,id) {

    var jq=$(e);
    changeBgColorOfTr(e);

    cancelBubble()
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

$(function(){
    var load = new Loading();
    load.init({
        target: "#dndArea"
    });
    load.start();
    setTimeout(function() {
        load.stop();
    }, 800)
    setTimeout(function () {
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
});
