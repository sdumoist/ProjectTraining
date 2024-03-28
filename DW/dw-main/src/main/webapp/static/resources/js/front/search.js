/**
 * Created by Lenovo on 2018/1/17.
 */
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function(){
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;
    function openWin(url) {
        var a = document.createElement("a"); //创建a标签
        a.setAttribute("href", url);
        a.setAttribute("target", "_blank");
        document.body.appendChild(a);
        a.click(); //执行当前对象
    }
    $(function() {
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
        ajax.set("docId",null);
        ajax.set("ruleCode",'search');
        ajax.start();
        var count="";
        var gridView ={
            /*初始化页面*/
            initPage: function() {
                var that = this;
                var fileType = $("#fileTypeValue").val(); //文档类型
                var fileName =  $("#headerSearchInputValue").val();
                //初始化表格
                that.initButtonEvent();
                that.initView(1,fileName,fileType);
                var picCommond =  $("#picCommond").val();
                //初始化表格
                if(picCommond=="true"&&fileType=='0'){
                    $("#picList").show();
                    that.initPic(fileName,"8");

                }
            },
            initButtonEvent:function(){
                layui.use('form', function(){
                    var form = layui.form;
                    form.on('radio(fileType)', function (data) {
                        var fileName =  $("#headerSearchInputValue").val();
                        var fileType = data.value;
                        if(fileName!=""){
                            gridView.initView(1,fileName,fileType);
                            layui.define(['jquery', 'form', 'layer', 'element', 'Hussar'], function(exports) {
                                var layer = layui.layer;
                                $(".doc-name").click(function () {
                                    var fileType = $(this).data("type");
                                    var id = $(this).data("id");
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

                                        }
                                    });*/
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
                });
            },
            initView:function(page,fileName,fileType){
                /*$.ajax({
                    async: false,
                    type: "post",
                    url: Hussar.ctxPath+"/search",
                    dataType: 'json',
                    data: {keyword: fileName, fileType: fileType, page:page},
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
                ajax.setAsync(true);
                ajax.set("keyword",fileName);
                ajax.set("fileType",fileType);
                ajax.set("page",page);
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
                                var type =    $("input[type=radio][name='fileType']:checked").val();

                                var search=  $(".search-box");
                                if(search.is(':hidden')){
                                    type = $("#fileTypeValue").val();
                                }
                                if(!$("#word").is(':hidden')){
                                    type= $(" #word input[type=radio][name='fileType']:checked").val();
                                }
                                //如果在文档搜索中查询全部 ，仍然是选择 文档全部
                                // if($("#fileTypeValue").val()=="7"&&  $("input[type=radio][name='fileType']:checked").val()==0){
                                //     type=7;
                                // }

                                gridView.initView(obj.curr,name,type);
                            }
                        }
                    });
                });
            },
            initPic:function(fileName,fileType){
                /*$.ajax({
                    async: false,
                    type: "post",
                    url: Hussar.ctxPath+"/search",
                    dataType: 'json',
                    data: {keyword: fileName, fileType: fileType, page:"1",size:"10"},
                    success: function (data) {
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
                                    '<img src="'+Hussar.ctxPath+'/preview/list?fileId='+item.filePdfPath+'&&isThumbnails=0"> </a> </div>';
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
                                spaceBetween: 5
                            });

                        }
                    }
                })*/
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
                                '<img src="'+Hussar.ctxPath+'/preview/list?fileId='+item.filePdfPath+'&&isThumbnails=0"> </a> </div>';
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
                            spaceBetween: 5
                        });

                    }
                }, function(data) {

                });
                ajax.setAsync(true);
                ajax.set("keyword",fileName);
                ajax.set("fileType",fileType);
                ajax.set("page","1");
                ajax.set("size","10");
                ajax.start();
            },
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
function showDoc(fileType,id) {
    fileType="."+fileType;
    var selectVal = $("#select").val();
    var keyWords = $("#headerSearchInputValue").val();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileType
            },
            async: false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            cache: false,
            dataType: "json",
            success: function (data) {
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

            }
        });*/
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
    });
}
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
function searchPic() {
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        var fileName =  $("#headerSearchInputValue").val();
        location.href= Hussar.ctxPath+"/searchPic?keyWords=" + encodeURI(fileName) + "&fileType=8";
    });
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