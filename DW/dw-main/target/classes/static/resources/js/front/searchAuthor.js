/**
 * Created by Lenovo on 2018/1/17.
 */
var $ ,
    form,
    laypage ,
    Hussar ,
    laytpl ,
    layer ,
    $ax ,
    element;
var orderFileType = $("#fileTypeValue").val();
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function(){
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;
    $('#loginout').click(function (event) {
        var operation = function () {
            window.location.href = Hussar.ctxPath + "/logout";
        };
        // Hussar.confirm("您确定要退出吗?", operation);
        layer.confirm('您确定要退出吗？', {skin: 'move-confirm'}, operation);
    });
    $(function() {
        var fileName_defult =  $("#headerSearchInputValue").val();
        $("#headerSearchInput").keypress(function (even) {
            if (even.which == 13) {
                searchByKeywords();
            }
        });
        $('#headerSearchBtn').click(function () {
            searchByKeywords();
        });
        function searchByKeywords(){
            var currentHref = location.href;
            var fileName = $("#headerSearchInput").val();
            //搜索自动过滤特殊字符，对特殊字符不进行搜索
            fileName = fileName.replace(/[\ |\~|\`|\￥|\!|\@|\#|\$|\%|\^|\&|\*|\(|\)|\-|\_|\=|\||\\|\[|\]|\{|\}|\;|\:|\"|\'|\,|\<|\.|\>|\/|\?|\+|\？]/g,"");
            if (fileName !== "") {
                fileName =fileName.substring(0,30);
                $("#headerSearchInputValue").val(fileName);
                var selectVal=$("#select").val();
                fileName = fileName.replace("#", escape("#")).replace("?", escape("?")).replace("？", escape("？")).replace("$", escape("$")).replace("￥", escape("￥"));
                var newUrl = "";
                if(selectVal === 8 || selectVal === "8"){
                    newUrl= Hussar.ctxPath+"/searchPic?keyWords=" + encodeURI(fileName) + "&fileType=" + selectVal;
                }else if(selectVal===15 || selectVal === "15"){
                    newUrl=Hussar.ctxPath+"/searchAuthor?keyWords=" + encodeURI(fileName)
                }else {
                    newUrl = Hussar.ctxPath+"/searchView?keyWords=" + encodeURI(fileName) + "&fileType=" + selectVal;
                }

                if(currentHref.indexOf("personalcenter")>-1){ // 处于个人中心页面
                    openWin(newUrl);
                }else{
                    location.href=newUrl;
                }
            } else {

            }
        }

        //IE兼容

        //积分系统控制
        /*$.ajax({
            url: Hussar.ctxPath+"/integral/addIntegral",
            async: true,
            data:{
                docId: null,
                ruleCode: 'search'
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
        //热搜获取/hotHistorySearch
        $.ajax({
            url:'/hotHistorySearch',
            data:{
                size: 10000,
                folderId:'7d9f267b319741ca90844efc7108db87'
            },
            async: true,
            success: function (data) {
                for(var i = 0;i<data.length;i++){
                    var html = "<span>"+data[i].keywords+"</span>";
                    $(".hot-search").append(html)
                }

                $(".hot-search span").click(function () {
                    var fileName = $(this).html();
                    var selectVal = $("#select").val().replace(/[\ |\~|\`|\￥|\!|\@|\#|\$|\%|\^|\&|\*|\(|\)|\-|\_|\=|\||\\|\[|\]|\{|\}|\;|\:|\"|\'|\,|\<|\.|\>|\/|\?|\+|\？]/g, "");;
                    var newUrl = "";
                    if(selectVal === 8 || selectVal === "8"){
                        newUrl= Hussar.ctxPath+"/searchPic?keyWords=" + encodeURI(fileName) + "&fileType=0";
                    }else if(selectVal===15 || selectVal === "15"){
                        newUrl=Hussar.ctxPath+"/searchAuthor?keyWords=" + encodeURI(fileName)
                    }else {
                        newUrl = Hussar.ctxPath+"/searchView?keyWords=" + encodeURI(fileName) + "&fileType=0";
                    }
                    window.location.href = newUrl
                });
            }
        });
        var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
            if (null != data && data != '' && data != undefined){
                if (data.integral != 0 && data.integral != null && data.integral != ''){
                    $("#num").html(data.msg);
                    $(".integral").show();
                    setTimeout(function () {
                        $(".integral").hide();
                    },2000)

                }
            }
        }, function(data) {

        });
        ajax.set("docId",null);
        ajax.set("ruleCode",'search');
        ajax.start();
        var count="";
        var gridView ={
            /*初始化页面*/
            initPage: function() {
                var that = this;
                var fileType = $("#fileTypeValue").val(); //文档类型
                var fileName = fileName_defult;
                //初始化表格
                that.initButtonEvent();
                that.initView(1,fileName,fileType,null);
            },
            initButtonEvent:function(){
                layui.use('form', function(){
                    var form = layui.form;
                    form.on('radio(fileType)', function (data) {
                        var fileName = fileName_defult;
                        var fileType = data.value;
                        orderFileType = fileType;
                        if(fileName!=""){
                            $("#orderTime").hide();
                            $("#orderTime1").hide();
                            gridView.initView(1,fileName,fileType,null);
                            layui.define(['jquery', 'form', 'layer', 'element', 'Hussar'], function(exports) {
                                var layer = layui.layer;
                                $(".doc-name").click(function () {
                                    var fileType = $(this).data("type");
                                    var id = $(this).data("id");
                                    if(fileType=="png"||fileType=="jpg"||fileType=="gif"||fileType=="bmp"||fileType=="jpeg"){
                                        openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
                                    }else if(fileType=="mp4"||fileType=="wmv"){
                                        openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
                                    } else if(fileType=="mp3"||fileType=="m4a"){
                                        openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
                                    }else if(fileType == 'docx'||fileType == 'doc'||fileType == 'dot'||fileType == 'xls'
                                        ||fileType == 'wps'||fileType == 'xlt'||fileType == 'et'
                                        ||fileType == 'ett'||fileType == 'ppts'||fileType == 'pot'
                                        ||fileType == 'dps'||fileType == 'dpt'
                                        || fileType == 'xlsx'||fileType == 'txt'||fileType == 'pdf'
                                        ||fileType == 'ceb' ||fileType == 'ppt'|| fileType == 'pptx'){
                                        openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id );
                                    }else {
                                        openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id );
                                    }
                                })
                            });
                        }
                    });
                });
                $('#orderByTime').on('click',function (){
                    var fileType = orderFileType; //文档类型
                    var fileName = fileName_defult;
                    if ($("#orderTime").css("display") != "none"){
                        $("#orderTime").hide();
                        $("#orderTime1").show();
                        gridView.initView(1,fileName,fileType,null);
                    }else {
                        $("#orderTime").show();
                        $("#orderTime1").hide();
                        gridView.initView(1,fileName,fileType,2);
                    }
                })
            },
            initView:function(page,fileName,fileType,order){
                /*$.ajax({
                    async: false,
                    type: "post",
                    url: Hussar.ctxPath+"/searchAuthorList",
                    dataType: 'json',
                    data: {keyword: fileName, fileType: fileType, page:page,size:10},
                    success: function (data) {
                        var json = eval(data);
                        if (json.total == 0) {
                            $("#articleItem").siblings().remove();
                            $("#articleItem").after('<div class="tips"><div class="tipPic"></div><div class="tipTxt">未找到相关内容~</div></div>');
                            $("#totalCount").html('0');
                            $("#footDiv").hide();
                            $("#laypageAre").hide();
                        } else {
                            count = json.total;
                            var list = json.items;
                            var num = list.length;
                            var adminFlag= $("#adminFlag").val();
                            json.adminFlag=adminFlag;
                            $("#totalCount").html(count);
                            $("#count").val(count);
                            layui.use(['jquery','laytpl'], function() {
                                var $ = layui.jquery;
                                var laytpl = layui.laytpl;
                                var getTpl = $("#articleItem").html();
                                laytpl(getTpl).render(json, function(html){
                                    $("#articleItem").siblings().remove();
                                    $("#articleItem").after(html);
                                });
                            });
                            $("#laypageAre").show();
                        }
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/searchAuthorList", function(data) {
                    var json = eval(data);
                    if (json.total == 0) {
                        $("#articleItem").siblings().remove();
                        $("#articleItem").after('<div class="tips"><div class="tipPic"></div><div class="tipTxt">未找到相关内容~</div></div>');
                        $("#totalCount").html('0');
                        $("#footDiv").hide();
                        $("#laypageAre").hide();
                    } else {
                        count = json.total;
                        var list = json.items;
                        var num = list.length;
                        for (var i = 0;i<num;i++){
                            var url = list[i].url;
                            if(url === ""||url===null){
                                list[i].url = "/static/resources/img/front/index/photo.png"
                            }else {
                                list[i].url = "/preview/list?fileId=" + url ;
                            }
                        }
                        var adminFlag= $("#adminFlag").val();
                        json.adminFlag=adminFlag;
                        $("#totalCount").html(count);
                        $("#count").val(count);
                        layui.use(['jquery','laytpl'], function() {
                            var $ = layui.jquery;
                            var laytpl = layui.laytpl;
                            var getTpl = $("#articleItem").html();
                            laytpl(getTpl).render(json, function(html){
                                $("#articleItem").siblings().remove();
                                $("#articleItem").after(html);
                            });
                        });
                        $("#laypageAre").show();
                    }
                }, function(data) {

                });
                ajax.set("keyword",fileName);
                ajax.set("fileType",fileType);
                ajax.set("page",page);
                ajax.set("size",10);
                ajax.set("order",order);
                ajax.start();

                //搜索历史
                var ajax = new $ax(Hussar.ctxPath +"/docHistorySearch",function (data) {
                    let inner = "";
                    for(let i =0; i < data.length;i++){
                        if(i>=7){break}
                        inner += "<div class='history-detail'>\n" +
                            "                    <div class='history-content'  title='"+ data[i].keywords +"'>"+data[i].keywords+"</div>\n" +
                            "                    <div class='history-time'>"+(data[i].createTime?data[i].createTime.substr(0,10):"未知")+"</div>\n" +
                            "                </div>"
                    }
                    $(".history-total").html("");
                    $(".history-total").html(inner);
                    $(".history-detail").on('click',function () {
                        var currentHref = location.href;
                        var content = $(this).find(".history-content").text();
                        var fileName =content.slice(0,content.length);
                        //搜索自动过滤特殊字符，对特殊字符不进行搜索
                        fileName = fileName.replace(/[\ |\~|\`|\￥|\!|\@|\#|\$|\%|\^|\&|\*|\(|\)|\-|\_|\=|\||\\|\[|\]|\{|\}|\;|\:|\"|\'|\,|\<|\.|\>|\/|\?|\+|\？]/g,"");
                        if (fileName !== "") {
                            fileName =fileName.substring(0,30);
                            $("#headerSearchInputValue").val(fileName);
                            var selectVal=$("#select").val();
                            fileName = fileName.replace("#", escape("#")).replace("?", escape("?")).replace("？", escape("？")).replace("$", escape("$")).replace("￥", escape("￥"));
                            var newUrl = "";
                            if(selectVal === 8 || selectVal === "8"){
                                newUrl= Hussar.ctxPath+"/searchPic?keyWords=" + encodeURI(fileName) + "&fileType=" + selectVal;
                            }else if(selectVal===15 || selectVal === "15"){
                                newUrl=Hussar.ctxPath+"/searchAuthor?keyWords=" + encodeURI(fileName)
                            }else {
                                newUrl = Hussar.ctxPath+"/searchView?keyWords=" + encodeURI(fileName) + "&fileType=" + selectVal;
                            }

                            if(currentHref.indexOf("personalcenter")>-1){ // 处于个人中心页面
                                openWin(newUrl);
                            }else{
                                location.href=newUrl;
                            }
                        } else {

                        }
                    })
                });
                ajax.start();

                //热搜文档
                var ajax = new $ax(Hussar.ctxPath + '/getOpTypeRank',function (data) {
                    let inner = "";
                    data = data.list;
                    if(data.length>1){
                        $(".hot").removeClass("hide");
                    }
                    for(let i =0; i < data.length;i++){
                        if(i>=6){break}
                        inner += "<div" +
                            " onclick=\"showHotDoc(\'" + data[i].DOCID + "\',\'" + data[i].DOCTYPE + "\')\" " +
                            " class=\"hot-detail  hot-detail-"+(i+1)+"\" title='"+data[i].TITLE+"'>"+data[i].TITLE+"</div>"
                    }
                    $(".hot-total").html("");
                    $(".hot-total").html(inner)
                });
                ajax.start();

                layui.use(['laypage','layer'], function(){
                    var laypage = layui.laypage,
                        layer = layui.layer;
                    var count=$("#count").val();
                    laypage.render({
                        elem: 'laypageAre'
                        ,count: count
                        ,limit: 10
                        ,layout: ['prev', 'page', 'next']
                        ,curr: page || 1
                        ,jump: function(obj, first){
                            //首次不执行
                            if(!first){
                                var name =  $("#headerSearchInputValue").val();
                                var type = $("#fileTypeValue").val(); //文档类型
                                // var type =    $("input[type=radio][name='fileType']:checked").val();
                                //
                                // var search=  $(".search-box");
                                // if(search.is(':hidden')){
                                //     type = $("#fileTypeValue").val();
                                // }
                                // if(!$("#word").is(':hidden')){
                                type= $(" #all input[type=radio][name='fileType']:checked").val();
                                // }
                                //如果在文档搜索中查询全部 ，仍然是选择 文档全部
                                // if($("#fileTypeValue").val()=="7"&&  $("input[type=radio][name='fileType']:checked").val()==0){
                                //     type=7;
                                // }
                                gridView.initView(obj.curr,name,type,null);
                            }
                        }
                    });
                });
            },
            // initPic:function(fileName,fileType){
            //     $.ajax({
            //         async: false,
            //         type: "post",
            //         url: "/searchAuthorList",
            //         dataType: 'json',
            //         data: {keyword: fileName, fileType: fileType, page:"1",size:"10"},
            //         success: function (data) {
            //             var json = eval(data);
            //             if (json.total == 0) {
            //                 $("#picItem").siblings().remove();
            //                 $("#number").html("0");
            //                 $("#picList").hide();
            //             } else {
            //                 count = json.total;
            //                 var list = json.items;
            //                 $("#picItem").html("");
            //                 layui.each(json.items, function(index, item){
            //
            //                     var param='<div class="swiper-slide"> <a class="slider-item" href="javascript:void (0)"onclick="showDoc(\''+item.docType+'\',\''+item.docId+'\')"> ' +
            //                         '<img src="/preview/list?fileId='+item.filePdfPath+'&&isThumbnails=0"> </a> </div>';
            //                     $("#picItem").append(param);
            //                 });
            //
            //                 var num = list.length;
            //                 var adminFlag= $("#adminFlag").val();
            //                 $("#number").html(count);
            //                 $("#release").html( $("#headerSearchInputValue").val())
            //                 json.adminFlag=adminFlag;
            //                 //swiper初始化添加
            //                 //mySwiper.init();
            //                 var mySwiper = new Swiper('.swiper-container-self', {
            //                     slidesPerView: 'auto',
            //                     spaceBetween: 5
            //                 });
            //
            //             }
            //         }
            //     })
            //
            // },
        }
        gridView.initPage();
        var searchRosd = $("#headerSearchInput").val();
        $("#resultName").html(searchRosd);
        setTimeout(function () {
            $(".swiper-slide img").each(function () {
                if($(this).width()<"100"){
                    $(this).width("200");
                    $(this).height("auto");
                    var mySwiper = new Swiper('.swiper-container-self', {
                        slidesPerView: 'auto',
                        spaceBetween: 5
                    });
                }
            })
        },2000);


    });
});
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showDoc(fileType,id) {
    var selectVal = $("#select").val();
    var keyWords = $("#headerSearchInputValue").val();
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        if(fileType=="png"||fileType=="jpg"||fileType=="gif"||fileType=="bmp"||fileType=="jpeg"){
            openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }else if(fileType=="mp4"||fileType=="wmv"){
            openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        } else if(fileType=="mp3"||fileType=="m4a"){
            openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }else if(fileType == 'docx'||fileType == 'doc'||fileType == 'dot'||fileType == 'xls'
            ||fileType == 'wps'||fileType == 'xlt'||fileType == 'et'
            ||fileType == 'ett'||fileType == 'ppts'||fileType == 'pot'
            ||fileType == 'dps'||fileType == 'dpt'
            || fileType == 'xlsx'||fileType == 'txt'||fileType == 'pdf'
            ||fileType == 'ceb' ||fileType == 'ppt'|| fileType == 'pptx'){
            openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }else {
            openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }
    });
}
function showPdf(id,flag,fileSuffixName) {
    var keyword =  $("#headerSearchInputValue").val();
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        if(fileSuffixName=="png"||fileSuffixName=="jpg"||fileSuffixName=="gif"||fileSuffixName=="bmp"||fileSuffixName=="ceb"||fileSuffixName=="jpeg"){
            openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
        }else if(fileSuffixName=="mp4"||fileSuffixName=="wmv"){
            openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
        } else if(fileSuffixName=="mp3"||fileSuffixName=="m4a"){
            openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
        }else {
            openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
        }
    });
}
function downloadFile(id,name){
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajaxFileUpload({
            url: Hussar.ctxPath+"/files/fileDownNew",
            type:"post",
            data:{
                docName:"", //name,
                fileIds:id
            }
        });
    });
}
// function searchPic() {
//     var fileName =  $("#headerSearchInputValue").val();
//     location.href="/searchPic?keyWords=" + encodeURI(fileName) + "&fileType=8";
// }
//收藏
function iconCollect(docId,status) {
    if(status=='0'){
        var layerOpen1 = layer.open({
            type: 2,
            id:"collections",
            area: [ '402px',  '432px'],
            fix: false, //不固定
            move: false,
            maxmin: false,
            shadeClose: true,
            moveOut: true,
            shade: 0.4,
            title: "<span id='upLevel' onclick='upLevel()'>收藏</span>",
            content: "/personalCollection/collectionToFolderView?docId="+docId,

        });
    }else {
        layer.confirm("确定取消收藏吗？", {
            title: ['取消收藏', 'background-color:#fff'],
            skin:'move-confirm'
        }, function () {
            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/cancelCollection", function(data) {
                if(data.success == "0"){
                    layer.msg('取消收藏成功',{icon: 1});
                    hideCancelCollection(docId);
                }else {
                    layer.msg('取消收藏失败',{anim:6,icon: 2})
                }
            }, function(data) {
                layer.msg('取消收藏异常!',{anim:6,icon: 2})

            });
            ajax.set("docIds",docId);
            ajax.start();
        })
    }
}
//分享
function iconShare(docId,fileSuffixName,fileName) {
    if(fileSuffixName.substr(0,1)=="."){
        openShare('', '/s/shareConfirm', 538, 390,docId,fileSuffixName,fileName);
    }else {
        openShare('', '/s/shareConfirm', 538, 390,docId,"."+fileSuffixName,fileName);
    }

}
/*打开分享链接*/
function openShare(title, url, w, h,docId,fileSuffixName,fileName) {
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;
        $ax = layui.HussarAjax;
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
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            move: false,
            maxmin: false,
            shadeClose: true,
            moveOut: true,
            shade: 0.4,
            title: title,
            content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURI(fileName)
        });
    })
}
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}
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
                    var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {icon: 3, title: '提示',offset: '40%'}, function (index) {
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

                } else {
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
                var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {icon: 3, title: '提示',offset: '40%',skin:'download-info'}, function (index) {
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

            } else {
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

//跳转到热搜文档预览
function showHotDoc(id, fileType,topicId,title) {
    if(fileType==="folder" ){
        toTopic(id,fileType,topicId,title)
    }else {
        var selectVal = '0';
        var keyWords = '';
        layui.use(['Hussar', 'HussarAjax'], function () {
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function (data) {
                if (data.code == 1) {
                    openWin(Hussar.ctxPath + "/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
                } else if (data.code == 2) {
                    openWin(Hussar.ctxPath + "/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
                } else if (data.code == 3) {
                    openWin(Hussar.ctxPath + "/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
                } else if (data.code == 4) {
                    openWin(Hussar.ctxPath + "/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
                } else {
                    openWin(Hussar.ctxPath + "/preview/toShowOthers?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
                }
            }, function (data) {

            });
            ajax.setAsync(true);
            ajax.set("suffix", fileType);
            ajax.start();
        });
    }
}
function showCancelCollection(docId) {
    var className = ".no_" + docId;
    $(className).hide();
    $(className).next('span').show()
}

function hideCancelCollection(docId) {
    var className = ".no_" + docId;
    $(className).show();
    $(className).next('span').hide()
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