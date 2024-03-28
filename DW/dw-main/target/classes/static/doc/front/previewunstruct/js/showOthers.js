/**
 * @Author: xubin
 * @Date:2018-07-12
 */
$(document).ready(function(){
    var fileId = "group1/M00/00/04/wKgC4ltHN66Ab0bGAD3-i9p2EaU034.m4v";
    $("#jquery_jplayer_1").jPlayer({
        ready: function () {
            $(this).jPlayer("setMedia", {
                m4v: "/preview/list?fileId=" +fileId
            });
        },
        swfPath: "${ctxPath}/static/resources/lib/jplayer/jplayer",
        supplied: "webmv, ogv, m4v",
        size: {
            width: "640px",
            height: "360px",
            cssClass: "jp-video-360p"
        },
        useStateClassSkin: true,
        autoBlur: false,
        smoothPlayBar: true,
        keyEnabled: true,
        remainingDuration: true,
        toggleDuration: true
    });
});

$(document).ready(function(){
    var docId = $("#docId").val();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/fsFile/cacheViewNum",
            data:{
                docId:docId,
            },
        })*/

        var ajax = new $ax(Hussar.ctxPath + "/fsFile/cacheViewNum", function(data) {

        }, function(data) {

        });
        ajax.set("docId",docId);
        ajax.start();

        /*//积分系统控制
        $.ajax({
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


        /**
         * 加载猜你喜欢的文档
         */
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/preview/guessYouLike",
            dataType:'json',
            data:{currentId:id},
            success: function (data) {
                var items = data;
                var toLoad = "";
                if (items.length != 0) {
                    $("#guess").removeClass("hide");
                    for (var i = 0; i < 3 && i < items.length; i++) {//同上
                        var imgsrc='';
                        if(items[i].docType === '.folder'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-folder15.png";
                        }else if(items[i].docType === '.doc'||items[i].docType === '.docx'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-word15.png";
                        }else if(items[i].docType === '.txt'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-text15.png";
                        }else if(items[i].docType === '.ppt'||items[i].docType === '.pptx'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ppt15.png";
                        }else if(items[i].docType === '.pdf'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-pdf15.png";
                        }else if(items[i].docType === '.ceb'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ceb15.png";
                        }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(items[i].docType)!=-1){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-audio15.png";
                        }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(items[i].docType)!=-1){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-video15.png";
                        }else if(items[i].docType === '.xls'||items[i].docType === '.xlsx') {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-excel15.png";
                        }else if(['.png','.jpeg','.gif','.jpg'].indexOf(items[i].docType)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-img15.png";
                        }else if(['.bmp'].indexOf(items[i].docType)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-bmp15.png";
                        }else if(['.psd'].indexOf(items[i].docType)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-psd15.png";
                        }else if(['.html'].indexOf(items[i].docType)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
                        }else if(['.exe'].indexOf(items[i].docType)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-exe15.png";
                        }else if(['.zip','.rar'].indexOf(items[i].docType)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-zip15.png";
                        }else {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-other15.png";
                        }
                        var author=items[i].authorName;
                        if( author==undefined||author==""){
                            author=items[i].authorId;
                        }
                        toLoad += '<li class="message">';
                        toLoad += '<img class="article-type" src="'+imgsrc+'">';
                        toLoad += '<a href="javascript:void(0)" onclick=\'showDocBlank("' + items[i].docType + '","' + items[i].docId + '");return false;\' target="_blank" title="'+items[i].title+'">';
                        toLoad += items[i].title;
                        toLoad += '</a>';
                        toLoad += '<div class="clearfix">';
                        toLoad += '<div class="article-msg">'+ author+'</div>';
                        toLoad += '<div class="article-msg-date">'+ items[i].createTime+'</div>';
                        toLoad += '</div>';
                        toLoad += '</li>';
                    }
                    $("#guessYouLike").html("");
                    $("#guessYouLike").append(toLoad);
                }
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/preview/guessYouLike", function(data) {
            var items = data;
            var toLoad = "";
            if (items instanceof Array && items.length != 0) {
                $("#guess").removeClass("hide");
                for (var i = 0; i < 3 && i < items.length; i++) {//同上
                    var imgsrc='';
                    if(items[i].docType === '.folder'){
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-folder15.png";
                    }else if(items[i].docType === '.doc'||items[i].docType === '.docx'){
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-word15.png";
                    }else if(items[i].docType === '.txt'){
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-text15.png";
                    }else if(items[i].docType === '.ppt'||items[i].docType === '.pptx'){
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ppt15.png";
                    }else if(items[i].docType === '.pdf'){
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-pdf15.png";
                    }else if(items[i].docType === '.ceb'){
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ceb15.png";
                    }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(items[i].docType)!=-1){
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-audio15.png";
                    }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(items[i].docType)!=-1){
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-video15.png";
                    }else if(items[i].docType === '.xls'||items[i].docType === '.xlsx') {
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-excel15.png";
                    }else if(['.png','.jpeg','.gif','.jpg'].indexOf(items[i].docType)!=-1) {
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-img15.png";
                    }else if(['.bmp'].indexOf(items[i].docType)!=-1) {
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-bmp15.png";
                    }else if(['.psd'].indexOf(items[i].docType)!=-1) {
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-psd15.png";
                    }else if(['.html'].indexOf(items[i].docType)!=-1) {
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
                    }else if(['.exe'].indexOf(items[i].docType)!=-1) {
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-exe15.png";
                    }else if(['.zip','.rar'].indexOf(items[i].docType)!=-1) {
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-zip15.png";
                    }else {
                        imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-other15.png";
                    }
                    var author=items[i].authorName;
                    if( author==undefined||author==""){
                        author=items[i].authorId;
                    }
                    toLoad += '<li class="message">';
                    toLoad += '<img class="article-type" src="'+imgsrc+'">';
                    toLoad += '<a href="javascript:void(0)" onclick=\'showDocBlank("' + items[i].docType + '","' + items[i].docId + '");return false;\' target="_blank" title="'+items[i].title+'">';
                    toLoad += items[i].title;
                    toLoad += '</a>';
                    toLoad += '<div class="clearfix">';
                    toLoad += '<div class="article-msg">'+ author+'</div>';
                    toLoad += '<div class="article-msg-date">'+ items[i].createTime+'</div>';
                    toLoad += '</div>';
                    toLoad += '</li>';
                }
                $("#guessYouLike").html("");
                $("#guessYouLike").append(toLoad);
            }
        }, function(data) {

        });
        var id = $("#docId").val();
        ajax.set("currentId",id);
        ajax.start();
    });

    $("#shareBtn").click(function () {
        openShare('', '/s/shareConfirm', 538, 390,docId,fileSuffixName,fileName);
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
     /*       skin:'share-class',*/
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