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
var isHoverKeyword = false;
var nowKeyword = "";
var orderFileType = $("#fileTypeValue").val();
function clickKeywords(keyword){
    keyword=keyword.replace("<em>","");
    keyword=keyword.replace("</em>","");
    $("#headerSearchInput").val(keyword);
    $("#headerSearchInput").focus();
    $("#keywordsAssociation").hide();
    isHoverKeyword = false;
    $("#headerSearchBtn").click();
}
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function(){
        $ = layui.jquery,
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
        var fileName_defult =  $("#headerSearchInput").val();
        $('#headerSearchBtn').click(function () {
           searchByKeywords();
        });
        $("#headerSearchInput").keypress(function (even) {
            if (even.which == 13) {
                searchByKeywords();
            }
        });
        $("#headerSearchInput").bind("input propertychange",function(event){
            var keyword = $("#headerSearchInput").val();
            nowKeyword = keyword;
            var ajax = new $ax(Hussar.ctxPath + "/suggestList", function (data) {
                if(nowKeyword = keyword){
                    var keywordList = data;
                    var html;
                    if(keywordList.length > 0){
                        html = "";
                        $("#keywordsAssociation").show();
                        for(var i=0;i<keywordList.length;i++){
                            var keywordContent = keywordList[i];
                            var findText = keywordContent.split(keyword);
                            var emText = findText.join('<em>'+ keyword + '</em>');

                            html+= '<li onclick="clickKeywords(\'' + keywordList[i] + '\')">'+emText+'</li>';
                            // var findText = content.split(keyword);
                            // html+= findText.join('<em>'+ keyword + '</em>');
                        }
                        $("#keywordsAssociation ul").html(html);
                    }else{
                        $("#keywordsAssociation").hide();
                    }
                }
            });
            ajax.setAsync(true);
            ajax.set("keywords", keyword);
            ajax.set("size", 10);
            ajax.start();
        });
        $("#headerSearchInput").bind("blur",function () {
            $("#headerSearchInput").removeClass("input-hover");
            if(!isHoverKeyword){
                $("#keywordsAssociation").hide();
            }
        })
        $("#headerSearchInput").bind("focus",function () {
            $("#headerSearchInput").addClass("input-hover");
        })
        $("#keywordsAssociation").bind("mouseover ",function () {
            isHoverKeyword = true;
        })
        $("#keywordsAssociation").bind("mouseout ",function () {
            isHoverKeyword = false;
        })
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
                } else if (selectVal === 14 || selectVal === "14") {
                    newUrl=Hussar.ctxPath + "/entry/allEntryList?keyWords=" + encodeURI(fileName)+"&fileType=" + selectVal;
                } else if(selectVal===15 || selectVal === "15") {
                    newUrl=Hussar.ctxPath+"/searchAuthor?keyWords=" + encodeURI(fileName)
                } else {
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

        //热搜获取/hotHistorySearch
        $.ajax({
            url:'/hotHistorySearch',
            data:{
                size: 10000,
                folderId:'7d9f267b319741ca90844efc7108db87',
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
                    } else if (selectVal === 14 || selectVal === "14") {
                        openWin(Hussar.ctxPath + "/entry/allEntryList?keyWords=" + encodeURI(fileName)+"&fileType=14");
                    } else if(selectVal===15 || selectVal === "15"){
                        newUrl=Hussar.ctxPath+"/searchAuthor?keyWords=" + encodeURI(fileName)
                    }else {
                        newUrl = Hussar.ctxPath+"/searchView?keyWords=" + encodeURI(fileName) + "&fileType=0";
                    }
                    window.location.href = newUrl
                });
            }
        });



        //IE兼容
        var count="";
        var gridView ={
            /*初始化页面*/
            initPage: function() {
                var that = this;
                var fileType = $("#fileTypeValue").val(); //文档类型
                var fileName =  fileName_defult;
                var radios = $("[name=fileType]");
                for(var i =0; i < radios.length;i++){
                    var radio = $(radios[i]);
                    if(radio.val()===fileType){
                        radio.next().click();
                    }
                }
                //初始化表格
                that.initButtonEvent();
                that.initView(1,fileName,fileType,null);
                that.initEntry(fileName, "14", null);
                var picCommond =  $("#picCommond").val();
                //初始化表格
                if(picCommond=="true"&&fileType=='0'){
                    $("#picList").show();
                    that.initPic(fileName,"8");

                }
            },
            initButtonEvent:function(){
                form.on('radio(fileType)', function (data) {
                    var fileName =  fileName_defult;
                    var fileType = data.value;
                    orderFileType = fileType;
                    /*  $("#select").val(fileType);*/

                    if(fileName!=""){
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                        $("#orderPic").hide();
                        $("#orderPic1").hide();
                        gridView.initView(1,fileName,fileType,null);
                        layui.define(['jquery', 'form', 'layer', 'element', 'Hussar'], function(exports) {
                            $(".doc-name").click(function () {
                                var fileType = $(this).data("type");
                                var id = $(this).data("id");
                                var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
                                    if(data.code==1){
                                        openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
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
                            })
                        });
                    }
                });
                $('#orderByTime').on('click',function (){
                    var fileType = orderFileType; //文档类型
                    var fileName =  fileName_defult;
                    if ($("#orderTime").css("display") != "none"){
                        $("#orderTime").hide();
                        $("#orderTime1").show();
                        gridView.initView(1,fileName,fileType,2);
                    }else {
                        $("#orderTime").show();
                        $("#orderTime1").hide();
                        gridView.initView(1,fileName,fileType,3);
                    }
                });
                $('#orderPicByTime').on('click',function (){
                    var fileName =  fileName_defult;
                    if ($("#orderPic").css("display") != "none"){
                        $("#orderPic").hide();
                        $("#orderPic1").show();
                        gridView.initPic(fileName,"8",2);
                    }else {
                        $("#orderPic").show();
                        $("#orderPic1").hide();
                        gridView.initPic(fileName,"8",3);
                    }
                });
                //词条点击
                $('#entryOrderPicByTime').on('click', function () {
                    var fileName = fileName_defult;
                    if ($("#entryorderPic").css("display") != "none") {
                        $("#entryorderPic").hide();
                        $("#entryorderPic1").show();
                        gridView.initEntry(fileName, "14", 2);
                    } else {
                        $("#entryorderPic").show();
                        $("#entryorderPic1").hide();
                        gridView.initEntry(fileName, "14", 3);
                    }
                });
            },
            initView:function(page,fileName,fileType,order){
                //搜索结果
                var ajax = new $ax(Hussar.ctxPath + "/search", function(data) {
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
                        var getTpl = $("#articleItem").html();
                        laytpl(getTpl).render(json, function(html){
                            $("#articleItem").siblings().remove();
                            $("#articleItem").after(html);
                        });
                        $("#laypageAre").show();
                    }
                }, function(data) {

                });

                ajax.set("keyword",fileName);
                ajax.set("fileType",fileType);
                ajax.set("page",page);
                ajax.set("order",order);
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
                        inner += "<div " +
                            " onclick=\"showHotDoc(\'" + data[i].DOCID + "\',\'" + data[i].DOCTYPE + "\')\" " +
                            "class=\"hot-detail hot-detail-"+(i+1)+"\" title='"+data[i].TITLE+"'>"+data[i].TITLE+"</div>"
                    }
                    $(".hot-total").html("");
                    $(".hot-total").html(inner)
                });
                ajax.setAsync(true);
                ajax.start();
                //热搜词条\

                var ajax = new $ax(Hussar.ctxPath + "/getHotEntry", function (result) {
                    let ininer=''
                    for (let i = 0; i <6; i++) {
                        ininer += "<div " + "class=\"hot-detail hot-detail-" + (i+1) + "\" title='" + result[i].name + "' onclick=\"showDoc(\'" + '14' + "\',\'" + result[i].id + "\')\">" + result[i].name + "</div>"
                    }
                    $('.hot-entryTotal').html('')
                    $('.hot-entryTotal').html(ininer)

                }, function (data) {
                });
                ajax.setAsync(true);
                ajax.start();


                //搜索类型做出不同提示
                var fileType = $("#fileTypeValue").val();
                if(fileType === "9"){
                    $(".before").html("相关视频");
                    $(".after").html("个视频")
                }
                if(fileType === "10"){
                    $(".before").html("相关音频");
                    $(".after").html("个音频")
                }
                if(fileType === "12"){
                    $(".before").html("相关成果");
                    $(".after").html("个成果")
                }

                var count=$("#totalCount").html();
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
                            // var type =    $("input[type=radio][name='fileType']:checked").val();
                            var type = $("#fileTypeValue").val(); //文档类型

                            // var search=  $(".search-box");
                            // if(search.is(':hidden')){
                            //     type = $("#fileTypeValue").val();
                            // }
                            // if(!$("#word").is(':hidden')){
                            if(type == '0'){
                                type= $(" #all input[type=radio][name='fileType']:checked").val();
                            }else if(type == '7'){
                                type= $(" #word input[type=radio][name='fileType']:checked").val();
                            }else{

                            }
                            // }
                            gridView.initView(obj.curr,name,type,order);
                        }
                    }
                });
            },
            initPic:function(fileName,fileType,order){
                var ajax = new $ax(Hussar.ctxPath + "/search", function(data) {
                    var json = eval(data);
                    if (json.total == 0) {
                        $("#picItem").siblings().remove();
                        $("#number").html("0");
                        $("#picList").hide();
                    } else {
                        count = json.total;
                        var list = json.items;
                        $("#picItem").html("");
                        layui.each(json.items, function(index, item){

                            var param='<div class="swiper-slide"> <a class="slider-item" href="javascript:void (0)"onclick="showDoc(\''+item.docType+'\',\''+item.docId+'\')"> ' +
                                '<img src="'+Hussar.ctxPath+'/preview/list?fileId='+item.filePdfPath+'&&isThumbnails=0" onerror="javascript:this.src=de'+Hussar.ctxPath+'faults.png"> </a> </div>';
                            $("#picItem").append(param);
                        });

                        var num = list.length;
                        var adminFlag= $("#adminFlag").val();
                        $("#number").html(count);
                        $("#release").html( $("#headerSearchInputValue").val())
                        json.adminFlag=adminFlag;
                        //swiper初始化添加
                        //mySwiper.init();
                        var mySwiper = new Swiper('.swiper-container-self', {
                            slidesPerView: 'auto',
                            spaceBetween: 15
                        });

                    }
                }, function(data) {

                });
                ajax.setAsync(true);
                ajax.set("keyword",fileName);
                ajax.set("fileType",fileType);
                ajax.set("page","1");
                ajax.set("size","10");
                ajax.set("order",order);
                ajax.start();
            },
            initEntry: function (fileName, fileType, order, page, size) {
                var ajax = new $ax(Hussar.ctxPath + "/searchEntry", function (data) {
                    var json = eval(data);
                    console.log("词条检索121",json)
                    if (json.total == 0) {
                        $("#entrypicItem").siblings().remove();
                        $("#entryNumber").html("0");
                        $("#entryPicList").hide();
                    } else {
                        count = json.total;
                        var list = json.items;
                        $("#entrypicItem").html("");
                        layui.each(json.items, function (index, item) {
                            let itemTags=item.tag.replace(/,/g,'&nbsp&nbsp&nbsp&nbsp')
                            // item.imgUrl=item.imgUrl.replace(/\\/g,'/')
                            var itemImgUrl=''
                            if (item.imgUrl == undefined) {
                                itemImgUrl = Hussar.ctxPath + '/static/doc/front/entry/img/defaultse.png'
                            }else {
                                itemImgUrl='/preview/list?fileId='+item.imgUrl.replace(/\\/g,'/')
                            }
                            console.log("概述是",item)
                            var param = '<div class="entryMoreMain">' +
                                '<div class="entryMoreMainTitle" onclick="showDoc(\'' + 14 + '\',\'' + item.id + '\')">' + item.name + '</div>' +
                                '<div class="entryMoreText">' +
                                '<img src="' + itemImgUrl + '" alt="" class="entryMoreImg" onerror="toFind();">' +
                                '<div class="entryMoreTextRight">' +
                                '<div>' + item.summaryText +
                                '</div>' +
                                '<div><span>' + itemTags + '</span></div>' +
                                '</div>' +
                                '</div>' +
                                '</div>'
                            console.log("词条列表",param)
                            $("#entrypicItem").append(param)
                        });

                        var num = list.length;
                        var adminFlag = $("#adminFlag").val();
                        console.log(count)
                        $("#entryNumber").html(count);
                        $("#release").html($("#headerSearchInputValue").val())
                        json.adminFlag = adminFlag;
                        //swiper初始化添加
                        //mySwiper.init();
                        var mySwiper = new Swiper('.swiper-container-self', {
                            slidesPerView: 'auto',
                            spaceBetween: 15
                        });

                    }
                }, function (data) {

                });
                ajax.setAsync(true);
                ajax.set("keyword", fileName);
                ajax.set("fileType", fileType);
                ajax.set("page", "1");
                ajax.set("size", "5");
                ajax.set("order", order);
                ajax.start();
            },
        };
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
                        spaceBetween: 15
                    });
                }
            })
        },2000);
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
});
function search(keyWord) {
    window.location.href = Hussar.ctxPath + "/searchView?keyWords=" + keyWord + "&fileType=0"
}

function showDoc(fileType,id) {
    if (fileType == 14) {
        openWin(Hussar.ctxPath + "/entry/entryPreview?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
    }
    fileType="."+fileType;
    var selectVal = $("#fileTypeValue").val()||"";
    var keyWords = $("#headerSearchInput").val();
    var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
        if(data.code==1){
            openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }else if(data.code==2){
            openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }else if(data.code==3){
            openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }else if(data.code==4){
            openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }else if(data.code==10){
            var url=Hussar.ctxPath+"/toShowComponent/toShowPDF?id="+id;
            openWin(encodeURI(url))
        }else{
            openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }
    }, function(data) {

    });
    ajax.set("suffix",fileType);
    ajax.start();
}
function showPdf(id,flag,fileSuffixName) {
    var keyword =  $("#headerSearchInputValue").val();
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
}
function downloadFile(id,name){
    $.ajaxFileUpload({
        url: Hussar.ctxPath+"/files/fileDownNew",
        type:"post",
        data:{
            docName:"", //name,
            fileIds:id
        }
    });
}
function searchPic() {

    var fileName =  $("#headerSearchInputValue").val();
    location.href= Hussar.ctxPath+"/searchPic?keyWords=" + encodeURI(fileName) + "&fileType=8";
}


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
        content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURI(fileName),
    });
}
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}
//下载
function  iconDownLoad(id,name) {
    var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {

        if (data.status == "1") {
            var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {icon: 3, title: '提示',skin:'download-info'}, function (index) {
                layer.close(index2);

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
}
function download(id,name){
    $.ajaxFileUpload({
        url : Hussar.ctxPath+"/files/fileDownNew?docIds="+id,
        type : "post",
        async:false,

    });
}

//跳转到热搜文档预览
function showHotDoc(id, fileType,topicId,title) {
    if(fileType==="folder" ){
        toTopic(id,fileType,topicId,title)
    }else {
        var selectVal = '0';
        var keyWords = '';
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
    }
}
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
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
function toFind(){
    var img=event.target;
    img.src="/static/doc/front/entry/img/default.png";
    img.οnerrοr=null;
}