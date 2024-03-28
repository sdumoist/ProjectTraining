var changePage=1;
var changeTotal=0;

layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function(){
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;

    $(function () {

        $(".user-wrapper2 .message ").hover(function () {
            $(this).find(".top img").attr("src",Hussar.ctxPath+"/static/resources/img/front/index/messagehover.png");
            $(this).find(".bottom").css("color","#6DADFF");
        },function () {
            $(this).find(".top img").attr("src",Hussar.ctxPath+"/static/resources/img/front/index/message.png");
            $(this).find(".bottom").css("color","#8796AB");
        })
        $(".user-wrapper-new").hover(function () {
            $(this).parent().find(".user-dropdown").show();
        },function () {
            $(this).parent().find(".user-dropdown").hide();
        })
        $(".user-dropdown").hover(function () {
            $(this).show();
        },function () {
            $(this).hide();
        })
        $(".swiper-cloud").hover(function () {
            $(this).html("查看更多")
        },function () {
            $(this).html("")
        })
        $(document).ready(function () {
            initPage();
        });
        /*
         * 退出
         */
        $('#loginout').click(function(event){
            var operation = function () {
                window.location.href = Hussar.ctxPath+"/logout";
            }
            // Hussar.confirm("您确定要退出吗?", operation);
            layer.confirm('您确定要退出吗？',{btn:['确定','关闭']},operation);
        });
        $('#uploadOpen').click(function(event){
            if($("#manager").val()=="1"){
                openWin(Hussar.ctxPath+"/jqwkmanager","_blank");
            }else{
                layui.define('layer', function() {
                    var layer = layui.layer;
                    layer.msg("暂无权限，即将开放");
                });
            }
        });


        //计数翻页效果添加，min为实际数据减4；max为实际数据；time是时间（毫秒）；len是数字长度
        var maxNum = $("#fileCount").val();
        while (maxNum.length < 4){
            maxNum="0"+maxNum;
        }
        if(!!window.ActiveXObject || "ActiveXObject" in window||maxNum<4){

            $('.dataStatistics').dataStatistics({min:maxNum,max:maxNum,time:1,len:5});
        }else{
            $('.dataStatistics').dataStatistics({min:maxNum-2,max:maxNum,time:1000,len:5});
        }
        $(".article-others").click(function () {
            layui.define('layer', function() {
                var layer = layui.layer;
                layer.msg("此文件类型不支持预览。");
            });
        });
        $('#search').click(function () {
            var fileName = $("#searchInput").val();
            //搜索自动过滤特殊字符，对特殊字符不进行搜索
            fileName = fileName.replace(/[\ |\~|\`|\￥|\!|\@|\#|\$|\%|\^|\&|\*|\(|\)|\-|\_|\=|\||\\|\[|\]|\{|\}|\;|\:|\"|\'|\,|\<|\.|\>|\/|\?|\+|\？]/g,"");
            var fileType = $('input:radio:checked').val();
            if (fileName != "" ) {
                fileName =fileName.substring(0,30);
                fileName = fileName.replace("#",escape("#")).replace("?",escape("?")).replace("？",escape("？")).replace("$",escape("$")).replace("￥",escape("￥"));
                if(fileType==8){
                    openWin(Hussar.ctxPath+"/searchPic?keyWords=" + encodeURI(fileName) + "&fileType=" + fileType );

                }if(fileType==15){
                    openWin(Hussar.ctxPath+"/searchAuthor?keyWords=" + encodeURI(fileName) );

                }else{
                    openWin(Hussar.ctxPath+"/searchView?keyWords=" + encodeURI(fileName) + "&fileType=" + fileType );
                }
            }else {
                $("#searchInput").val("");
                location.href= Hussar.ctxPath+'/';
            }
            $("#searchInput").focus();
        });
        // 关键词搜索框添加绑定回车函数
        $('#searchInput').bind('keypress', function(event) {
            if (event.keyCode == "13") {
                $("#search").click();
            }
        });

        var initPage = function () {
            //积分系统控制
            $.ajax({
                url: Hussar.ctxPath+"/integral/addIntegral",
                async: true,
                data:{
                    docId: null,
                    ruleCode: 'login'
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
            });
            $.ajax({
                url: Hussar.ctxPath+'/newMessgeFolder',
                data:{
                    size: 10,
                    folderId:'7d9f267b319741ca90844efc7108db87'
                },
                async: true,
                success: function (data) {
                    $("#plane").html("");
                    for(var i = 0;i<data.length;i++){
                        if(i<10){
                            // var j=parseInt(i)-1;
                            // var z=parseInt(i)-2;
                            // if(z<0||j<0){
                            //     continue;
                            // }
                            var imgsrc="";
                            // var imgsrc2="";
                            // var imgsrc3="";
                            if(data[i].docType === 'folder'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-folder15.png";
                            }else if(data[i].docType === '.doc'||data[i].docType === '.docx'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-word15.png";
                            }else if(data[i].docType === '.txt'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-text15.png";
                            }else if(data[i].docType === '.ppt'||data[i].docType === '.pptx'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ppt15.png";
                            }else if(data[i].docType === '.pdf'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-pdf15.png";
                            }else if(data[i].docType === '.ceb'){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ceb15.png";
                            }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(data[i].docType)!=-1){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-audio15.png";
                            }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(data[i].docType)!=-1){
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-video15.png";
                            }else if(data[i].docType === '.xls'||data[i].docType === '.xlsx') {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-excel15.png";
                            }else if(['.png','.jpeg','.gif','.jpg'].indexOf(data[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-img15.png";
                            }else if(['.bmp'].indexOf(data[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-bmp15plus.png";
                            }else if(['.psd'].indexOf(data[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-psd15.png";
                            }else if(['.html'].indexOf(data[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
                            }else if(['.exe'].indexOf(data[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-exe15.png";
                            }else if(['.zip','.rar'].indexOf(data[i].docType)!=-1) {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-zip15.png";
                            }else {
                                imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
                            }
                            // if(data[j].docType === 'folder'){
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-folder15.png";
                            // }else if(data[j].docType === '.doc'||data[j].docType === '.docx'){
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-word15.png";
                            // }else if(data[j].docType === '.txt'){
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-text15.png";
                            // }else if(data[j].docType === '.ppt'||data[j].docType === '.pptx'){
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-ppt15.png";
                            // }else if(data[j].docType === '.pdf'){
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-pdf15.png";
                            // }else if(data[j].docType === '.ceb'){
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-ceb15.png";
                            // }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(data[j].docType)!=-1){
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-audio15.png";
                            // }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(data[j].docType)!=-1){
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-video15.png";
                            // }else if(data[j].docType === '.xls'||data[j].docType === '.xlsx') {
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-excel15.png";
                            // }else if(['.png','.jpeg','.gif','.jpg'].indexOf(data[j].docType)!=-1) {
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-img15.png";
                            // }else if(['.bmp'].indexOf(data[j].docType)!=-1) {
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-bmp15plus.png";
                            // }else if(['.psd'].indexOf(data[j].docType)!=-1) {
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-psd15.png";
                            // }else if(['.html'].indexOf(data[j].docType)!=-1) {
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-html15.png";
                            // }else if(['.exe'].indexOf(data[j].docType)!=-1) {
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-exe15.png";
                            // }else if(['.zip','.rar'].indexOf(data[j].docType)!=-1) {
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-zip15.png";
                            // }else {
                            //     imgsrc2="/static/resources/img/front/file-icon/ic-html15.png";
                            // }
                            // if(data[z].docType === 'folder'){
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-folder15.png";
                            // }else if(data[z].docType === '.doc'||data[z].docType === '.docx'){
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-word15.png";
                            // }else if(data[z].docType === '.txt'){
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-text15.png";
                            // }else if(data[z].docType === '.ppt'||data[z].docType === '.pptx'){
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-ppt15.png";
                            // }else if(data[z].docType === '.pdf'){
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-pdf15.png";
                            // }else if(data[z].docType === '.ceb'){
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-ceb15.png";
                            // }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(data[z].docType)!=-1){
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-audio15.png";
                            // }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(data[z].docType)!=-1){
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-video15.png";
                            // }else if(data[z].docType === '.xls'||data[z].docType === '.xlsx') {
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-excel15.png";
                            // }else if(['.png','.jpeg','.gif','.jpg'].indexOf(data[z].docType)!=-1) {
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-img15.png";
                            // }else if(['.bmp'].indexOf(data[z].docType)!=-1) {
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-bmp15plus.png";
                            // }else if(['.psd'].indexOf(data[z].docType)!=-1) {
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-psd15.png";
                            // }else if(['.html'].indexOf(data[z].docType)!=-1) {
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-html15.png";
                            // }else if(['.exe'].indexOf(data[z].docType)!=-1) {
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-exe15.png";
                            // }else if(['.zip','.rar'].indexOf(data[z].docType)!=-1) {
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-zip15.png";
                            // }else {
                            //     imgsrc3="/static/resources/img/front/file-icon/ic-html15.png";
                            // }
//"+data[i].docId+","+data[i].docType+"'
                            var inner = '<div class="swiper-slide" onclick="showDoc(\''+data[i].docId+'\',\''+data[i].docType+'\')" >' +
                                "<span>" +
                                "<img src='"+imgsrc+"'>" + data[i].title +
                                "</span>" + "</div>";
                            $(".plane").append(inner);
                        }
                    }
                    var plane_width = 0;
                    for(var n = 0;n<$("#plane .swiper-slide").length;n++){
                        plane_width = plane_width + $("#plane .swiper-slide").eq(n).width() + 45
                    }
                    plane_width = plane_width
                    $(".plane").width(plane_width);
                    var Html = $(".plane-swiper").html();
                    $(".plane-swiper").append(Html);
                    $(".plane:nth-child(2)").css("left",plane_width+"px");

                    //最新消息轮播效果,使用定时器实现
                    var timer = setInterval(timeInterval,30)
                    $(".plane").hover(function(){
                        clearInterval(timer)
                    },function(){
                        timer = setInterval(timeInterval,30)
                    });
                    function timeInterval() {
                        var left_1  = parseInt($(".plane:nth-child(1)").css("left")) - 1;
                        var left_2  = parseInt($(".plane:nth-child(2)").css("left")) - 1;
                        if(left_2 ===0){
                            left_1 = plane_width;
                        }
                        if(left_1 ===0){
                            left_2 = plane_width;
                        }
                        $(".plane:nth-child(1)").css("left",left_1+"px");
                        $(".plane:nth-child(2)").css("left",left_2+"px");

                    }
                }
            });
            $.ajax({
                url: Hussar.ctxPath+'/banner/bannerList',
                data: {
                    page: 1,
                    limit: 5
                },
                async: true,
                success: function (result) {
                    /*var data = result.data;
                    var inner = '';
                    for (var i = 0; i < data.length; i++) {
                        inner += '  <div class="swiper-slide">';

                        inner += '<a href="'+Hussar.ctxPath+'' + data[i].bannerHref + '" target="_blank">' +
                            // '<li style="list-style: circle ;color:rgba(238,244,252,1)" class="little">&nbsp;</li>' +
                            '  <img src="'+Hussar.ctxPath+'/preview/list?fileId=' + data[i].bannerPath.replace(/\\/g, "/") + '" style="width: 593px;height: 337px"/>';

                        inner += '</a></div>';

                    }

                    $(".center-swiper .swiper-wrapper").html("");
                    $(".center-swiper .swiper-wrapper").html(inner);*/

                    var swiper = new Swiper('.center-swiper', {
                        direction: 'horizontal', // 垂直切换选项
                        loop: true, // 循环模式选项
                        autoplay: {
                            delay: 4000,
                            stopOnLastSlide: false,
                            disableOnInteraction: true
                        },
                        // 如果需要分页器
                        pagination: {
                            el: '.swiper-pagination'
                        },

                    });
                    var comtainer = document.getElementsByClassName('.center-swiper');
                    comtainer.onmouseenter = function () {
                        swiper.autoplay.stop();
                    };
                    comtainer.onmouseleave = function () {
                        swiper.autoplay.start();
                    }
                }
            });

            $.ajax({
                url: Hussar.ctxPath+'/newMessge',
                data:{
                    size: 9
                },
                async: true,
                success: function (data) {
                    var inner = '';
                    for(var i = 0;i<data.length;i++){
                        if(i<9){
                            inner+='  <div class="swiper-slide">';

                            var  time =data[i].showTime;

                            inner+=' <div class="message-info"  >' +
                                // '<li style="list-style: circle ;color:rgba(238,244,252,1)" class="little">&nbsp;</li>' +
                                '<div class="new-message-date" ><span>'+time+'</span><span class="author">'+data[i].authorName+'</span></div> ' +
                                '<div class="new-message-message" ><label class="greyCircle"></label><span title="'+data[i].title+'" onclick="showDoc(\''+data[i].docId+'\',\''+data[i].docType+'\')">上传&nbsp;&nbsp;《'+data[i].title+'》</span></div> </div>';

                            inner+='</div>';
                        }
                    }

                    $("#newList").html("");
                    $("#newList").html(inner);


                    var swiper=  new Swiper ('.newList', {
                        direction: 'vertical', // 垂直切换选项
                        loop: true, // 循环模式选项
                        loopAdditionalSlides : 4,
                        height:70,
                        autoplay: {
                            delay: 3000,
                            stopOnLastSlide: false,
                            disableOnInteraction: false
                        }
                    })
                    if(data.length>0){   $(".newList").height("280px");}

                    // $(" .newList .swiper-slide").height("70px")

                    var comtainer = document.getElementById('newList');
                    comtainer.onmouseenter = function () {
                        swiper.autoplay.stop();
                    };
                    comtainer.onmouseleave = function () {
                        swiper.autoplay.start();
                    }

                }
            });
            //文件目录获取
          /*  $.ajax({
                url: Hussar.ctxPath+'/getFolderData',
                async: true,
                success: function (data) {
                    $("#folders").html("");
                    for (var i = 0; i < data.length; i++){
                        var child = "";
                        child+='<li class="type-lv-1 clearfix">';
                        child+='<a href="'+Hussar.ctxPath+'/personalcenter?menu=11&folderId='+data[i].FOLDERID+'&folderName='+encodeURI(data[i].FOLDERNAME)+'"><div class="type-name">'+data[i].FOLDERNAME+'</div></a>';
                        var childs = data[i].childs;
                        if (childs.length > 0){
                            child+='<ul class="type-lv-2 clearfix">';
                            for (var j = 0; j < childs.length; j++){
                                // child+='<li><a href="/frontFile?fileId='+childs[j].FOLDERID+'&fileName='+childs[j].FOLDERNAME+'">'+childs[j].FOLDERNAME+'</a></li>'
                                child+='<li><a href="'+Hussar.ctxPath+'/personalcenter?menu=11&folderId='+childs[j].FOLDERID+'&folderName='+encodeURI(childs[j].FOLDERNAME)+'">'+childs[j].FOLDERNAME+'</a></li>'
                            }
                            child+='</ul>'
                        }
                        child+='<div class="more-list"><a href="'+Hussar.ctxPath+'/personalcenter?menu=11&folderId='+data[i].FOLDERID+'&folderName='+encodeURI(data[i].FOLDERNAME)+'"><i class="iconfont">&#xe602;</i></a></div>';
                        $("#folders").append(child);
                    }

                }
            });*/
            //上传排行数据获取
            $.ajax({
                url: Hussar.ctxPath+'/getUploadRank',
                data:{
                    size: 5
                },
                async: true,
                success: function (data) {
                    var inner = '<colgroup>'+
                        '<colwidth="40">'+
                        '<colwidth="75\">'+
                        '<colwidth="110">'+
                        '<col>'+
                        '</colgroup>';
                    for (var i = 0; i < data.length; i++){
                        if(i<5){
                            inner+='<tr><td>'+data[i].RANK+'</td>' +
                                '<td>'+data[i].USER_NAME+'</td>' +
                                '<td style="width: 156px;text-align: center">'+data[i].SHORT_NAME+'</td>' +
                                '<td><i class="iconfont">&#xe017;</i>'+data[i].FILENUM+'</td>' +
                                '</tr>';
                        }

                    }
                    $("#uploadRank").html("");
                    $("#uploadRank").html(inner);
                }
            });
            //热门下载数据获取
            /* $.ajax({
                 url:'getOpTypeRank',
                 data:{
                     opType: 4,
                     page: 1,
                     pageSize: 6
                 },
                 async: true,
                 success: function (data) {
                     var inner = '<colgroup>' +
                         '<col width="225">' +
                         '<col >' +
                         '</colgroup>';
                     var imgsrc="";
                     for (var i = 0; i < data.length; i++){
                           if(data[i].DOCTYPE === 'folder'){
                         imgsrc="/static/resources/img/front/file-icon/ic-folder15.png";
                               }else if(data[i].DOCTYPE === '.doc'||data[i].DOCTYPE === '.docx'){
                         imgsrc="/static/resources/img/front/file-icon/ic-word15.png";
                               }else if(data[i].DOCTYPE === '.txt'){
                         imgsrc="/static/resources/img/front/file-icon/ic-text15.png";
                               }else if(data[i].DOCTYPE === '.ppt'||data[i].DOCTYPE === '.pptx'){
                         imgsrc="/static/resources/img/front/file-icon/ic-ppt15.png";
                               }else if(data[i].DOCTYPE === '.pdf'){
                         imgsrc="/static/resources/img/front/file-icon/ic-pdf15.png";
                               }else if(data[i].DOCTYPE === '.ceb'){
                         imgsrc="/static/resources/img/front/file-icon/ic-ceb15.png";
                               }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(data[i].DOCTYPE)!=-1){
                         imgsrc="/static/resources/img/front/file-icon/ic-audio15.png";
                               }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(data[i].DOCTYPE)!=-1){
                         imgsrc="/static/resources/img/front/file-icon/ic-video15.png";
                               }else if(data[i].DOCTYPE === '.xls'||data[i].DOCTYPE === '.xlsx') {
                         imgsrc="/static/resources/img/front/file-icon/ic-excel15.png";
                               }else if(['.png','.jpeg','.gif','.jpg'].indexOf(data[i].DOCTYPE)!=-1) {
                         imgsrc="/static/resources/img/front/file-icon/ic-img15.png";
                               }else if(['.bmp'].indexOf(data[i].DOCTYPE)!=-1) {
                         imgsrc="/static/resources/img/front/file-icon/ic-bmp15.png";
                               }else if(['.psd'].indexOf(data[i].DOCTYPE)!=-1) {
                         imgsrc="/static/resources/img/front/file-icon/ic-psd15.png";
                               }else if(['.html'].indexOf(data[i].DOCTYPE)!=-1) {
                         imgsrc="/static/resources/img/front/file-icon/ic-html15.png";
                               }else if(['.exe'].indexOf(data[i].DOCTYPE)!=-1) {
                         imgsrc="/static/resources/img/front/file-icon/ic-exe15.png";
                               }else if(['.zip','.rar'].indexOf(data[i].DOCTYPE)!=-1) {
                         imgsrc="/static/resources/img/front/file-icon/ic-zip15.png";
                               }else {
                         imgsrc="/static/resources/img/front/file-icon/ic-html15.png";
                               }
                         inner+='<tr>' +
                             '<td>' +
                             '<img class="article-type" src="'+imgsrc+'">' +
                             '<a class="article-title" target="_blank" title="'+data[i].TITLE+'" href="javascript:void(0)" onclick="showDoc(\''+data[i].DOCID+'\',\''+data[i].DOCTYPE+'\');return false">' +
                             '<span>'+ data[i].TITLE +'</span>'+
                             '</a>' +
                             '</td>' +
                             '<td><i class="iconfont">&#xe02d;</i>'+data[i].YLCOUNT+'</td>' +
                             '</tr>'
                     };
                     $("#downloadRank").html("");
                     $("#downloadRank").html(inner);
                     var timer;
                     $(".article-title>span").hover(function () {
                         var _this = $(this);
                         var len = _this.width();
                         var width = _this.parent().width();
                         if(len >= width){
                             var num = 0;
                             timer = setInterval(function () {
                                 if (num <= -len) {
                                     num = 0;
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
             });*/
            //热门预览数据获取
            $.ajax({
                url: Hussar.ctxPath+'/getOpTypeRank',
                data:{
                    opType: 3,
                    pageNumber: changePage,
                    pageSize: 5
                },
                async: true,
                success: function (result) {
                    var    data=result.list;

                    var inner = '<colgroup>' +
                        '<col width="225">' +
                        '<col >' +
                        '</colgroup>';
                    var imgsrc="";
                    for (var i = 0; i < data.length; i++){
                        if(data[i].DOCTYPE === 'folder'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-folder15.png";
                        }else if(data[i].DOCTYPE === '.doc'||data[i].DOCTYPE === '.docx'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-word15.png";
                        }else if(data[i].DOCTYPE === '.txt'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-text15.png";
                        }else if(data[i].DOCTYPE === '.ppt'||data[i].DOCTYPE === '.pptx'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ppt15.png";
                        }else if(data[i].DOCTYPE === '.pdf'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-pdf15.png";
                        }else if(data[i].DOCTYPE === '.ceb'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ceb15.png";
                        }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(data[i].DOCTYPE)!=-1){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-audio15.png";
                        }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(data[i].DOCTYPE)!=-1){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-video15.png";
                        }else if(data[i].DOCTYPE === '.xls'||data[i].DOCTYPE === '.xlsx') {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-excel15.png";
                        }else if(['.png','.jpeg','.gif','.jpg'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-img15.png";
                        }else if(['.bmp'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-bmp15plus.png";
                        }else if(['.psd'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-psd15.png";
                        }else if(['.html'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
                        }else if(['.exe'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-exe15.png";
                        }else if(['.zip','.rar'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-zip15.png";
                        }else {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-other15.png";
                        }
                        inner+='<tr>' +
                            '<td>' +
                            '<img class="article-type" src="'+imgsrc+'">' +
                            '<a class="article-title" target="_blank" title="'+data[i].TITLE+'"  onclick="showDoc(\''+data[i].DOCID+'\',\''+data[i].DOCTYPE+'\');return false">' +
                            '<span>'+ data[i].TITLE +'</span>'+
                            '</a>' +
                            '</td>' +
                            '<td><i class="iconfont">&#xea16;</i>'+data[i].YLCOUNT+'</td>' +
                            '</tr>'
                    };
                    $("#previewRank").html("");
                    $("#previewRank").html(inner);
                    changePage=result.pageNumber;
                    changeTotal=result.total;
                    var timer;
                    $(".article-title>span").hover(function () {
                        var _this = $(this);
                        var len = _this.width();
                        var width = _this.parent().width();
                        if(len >= width){
                            var num = 0;
                            var change = 0;
                            timer = setInterval(function () {
                                if (navigator.userAgent.indexOf('iPad') != -1){
                                    change = 150-len;
                                }else{
                                    change = 190-len;
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
            });
            //专题数据获取
            $.ajax({
                url: Hussar.ctxPath+'/getTopicData',
                async: true,
                success: function (data) {
                    var inner = '';
                    for (var i = 0; i < data.length; i++){
                        inner+=' <div class="category-item">' +
                            '<a href="'+Hussar.ctxPath+'/frontTopic/topicDetail?topicId='+data[i].topicId+'&page=1&size=10" target="_blank"><div class="category-item-img layui-col-sm12">' +
                            // '<img src="/static/resources/img/front/index/item-4.png"/></div></a>' +
                            '<img src="'+Hussar.ctxPath+''+data[i].topicCover+'"/></a></div>' +
                            '<div class="category-item-list layui-col-sm12">' +
                            ' ';
                        var docList = data[i].docList;
                        if (docList.length > 0){
                            inner+='<ul class="article-list">';
                            for (var j = 0; j < docList.length; j++){
                                var imgsrc='';
                                var isNew ='';
                                if (docList[j].isNew) {
                                    isNew=' <img  style="cursor:auto" class="article-type article-new" src="'+Hussar.ctxPath+'/static/resources/img/front/file-icon/ic-new-15.png">'
                                }
                                if(docList[j].fileType === 'folder'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-folder15.png";
                                }else if(docList[j].fileType === '.doc'||docList[j].fileType === '.docx'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-word15.png";
                                }else if(docList[j].fileType === '.txt'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-text15.png";
                                }else if(docList[j].fileType === '.ppt'||docList[j].fileType === '.pptx'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ppt15.png";
                                }else if(docList[j].fileType === '.pdf'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-pdf15.png";
                                }else if(docList[j].fileType === '.ceb'){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ceb15.png";
                                }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(docList[j].fileType)!=-1){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-audio15.png";
                                }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(docList[j].fileType)!=-1){
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-video15.png";
                                }else if(docList[j].fileType === '.xls'||docList[j].fileType === '.xlsx') {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-excel15.png";
                                }else if(['.png','.jpeg','.gif','.jpg'].indexOf(docList[j].fileType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-img15.png";
                                }else if(['.bmp'].indexOf(docList[j].fileType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-bmp15plus.png";
                                }else if(['.psd'].indexOf(docList[j].fileType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-psd15.png";
                                }else if(['.html'].indexOf(docList[j].fileType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
                                }else if(['.exe'].indexOf(docList[j].fileType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-exe15.png";
                                }else if(['.zip','.rar'].indexOf(docList[j].fileType)!=-1) {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-zip15.png";
                                }else {
                                    imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-other15.png";
                                }
                                var time = docList[j].createTime;
                                time = time.substr(0,time.indexOf(" "));
                                inner+='<li  style="position: relative"><img class="article-type" src="'+imgsrc+'"><a class="article-title" target="_blank" title="'+docList[j].title+'" onclick="showDoc(\''+docList[j].doc_id+'\',\''+docList[j].fileType+'\')">'+docList[j].title+'</a> <span style="position: absolute ;right: 2px;    color: rgba(135,150,171,1);">'+time+'</span>'+isNew+'</li>';
                            }
                            inner+="</ul>";
                        }
                        inner+="</div></div>"

                    }
                    $("#topicRecommend").html("");
                    $("#topicRecommend").html(inner);
                }
            });
            // 分类推荐产品

        }
    });
});
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showDoc(id,fileType) {
    fileType = fileType.replace(".","");
    var selectVal = '0';
    var keyWords = '';
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
/**
 * 获取当前所有积分
 * @returns {number}
 */
$("#totalIntegral",document).text(getTotalIntegral());
function getTotalIntegral(){
    var integral = 0;
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajax({
            type:'post',
            url: Hussar.ctxPath+'/integral/getTotalIntegral',
            async:false,
            success: function (data) {
                integral = parseInt(data);
            }
        });
    });
    return integral;
}
function planeSwiper(fileName,fileId,type){

}
function  change() {
    if (changeTotal>changePage*5){
        changePage=changePage+1;
        if(changePage==5){
            changePage=1;
        }
        layui.use(['Hussar'], function(){
            var Hussar = layui.Hussar;

            $.ajax({
                url: Hussar.ctxPath+'/getOpTypeRank',
                data:{
                    opType: 3,
                    pageNumber: changePage,
                    pageSize: 5
                },
                async: true,
                success: function (result) {
                    var    data=result.list;

                    var inner = '<colgroup>' +
                        '<col width="225">' +
                        '<col >' +
                        '</colgroup>';
                    var imgsrc="";
                    for (var i = 0; i < data.length; i++){
                        if(data[i].DOCTYPE === 'folder'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-folder15.png";
                        }else if(data[i].DOCTYPE === '.doc'||data[i].DOCTYPE === '.docx'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-word15.png";
                        }else if(data[i].DOCTYPE === '.txt'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-text15.png";
                        }else if(data[i].DOCTYPE === '.ppt'||data[i].DOCTYPE === '.pptx'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ppt15.png";
                        }else if(data[i].DOCTYPE === '.pdf'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-pdf15.png";
                        }else if(data[i].DOCTYPE === '.ceb'){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ceb15.png";
                        }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(data[i].DOCTYPE)!=-1){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-audio15.png";
                        }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(data[i].DOCTYPE)!=-1){
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-video15.png";
                        }else if(data[i].DOCTYPE === '.xls'||data[i].DOCTYPE === '.xlsx') {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-excel15.png";
                        }else if(['.png','.jpeg','.gif','.jpg'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-img15.png";
                        }else if(['.bmp'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-bmp15plus.png";
                        }else if(['.psd'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-psd15.png";
                        }else if(['.html'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
                        }else if(['.exe'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-exe15.png";
                        }else if(['.zip','.rar'].indexOf(data[i].DOCTYPE)!=-1) {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-zip15.png";
                        }else {
                            imgsrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-other15.png";
                        }
                        inner+='<tr>' +
                            '<td>' +
                            '<img class="article-type" src="'+imgsrc+'">' +
                            '<a class="article-title" target="_blank" title="'+data[i].TITLE+'"  onclick="showDoc(\''+data[i].DOCID+'\',\''+data[i].DOCTYPE+'\');return false">' +
                            '<span>'+ data[i].TITLE +'</span>'+
                            '</a>' +
                            '</td>' +
                            '<td><i class="iconfont">&#xea16;</i>'+data[i].YLCOUNT+'</td>' +
                            '</tr>'
                    };
                    $("#previewRank").html("");
                    $("#previewRank").html(inner);
                    changePage=result.pageNumber;
                    changeTotal=result.total;
                    var timer;
                    $(".article-title>span").hover(function () {
                        var _this = $(this);
                        var len = _this.width();
                        var width = _this.parent().width();
                        if(len >= width){
                            var num = 0;
                            var change = 0;
                            timer = setInterval(function () {
                                if (navigator.userAgent.indexOf('iPad') != -1){
                                    change = 150-len;
                                }else{
                                    change = 190-len;
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
            });
        });
    }
}
