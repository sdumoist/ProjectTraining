var changePage = 1;
var changeTotal = 0;
var changeNewMessge = 0;
var personalPic = "/static/resources/img/front/index/photo.png ";//默认头像
var isHoverKeyword = false;
var nowKeyword = "";
function clickKeywords(keyword){
    keyword=keyword.replace("<em>","");
    keyword=keyword.replace("</em>","");
    $("#searchInput").val(keyword);
    $("#searchInput").focus();
    $("#keywordsAssociation").hide();
    isHoverKeyword = false;
    $("#search").click();
}
layui.use(['form', 'laypage', 'jquery', 'layer', 'laytpl', 'Hussar', 'HussarAjax'], function () {
    var $ = layui.jquery,
        Hussar = layui.Hussar,
        layer = layui.layer,
        form = layui.form,
        $ax = layui.HussarAjax;
    layer = layui.layer;

    $(function () {
        $(document).ready(function () {
            initPage();
            // 如果默认渲染未渲染成功，则渲染搜索分类下拉选
            if(!$('#select + div.layui-form-select').length) form.render('select','classify');
        });
        $('#changePwd').click(function(event){
            layer.open({
                type: 2,
                title: '修改密码',
                area: ['450px', '300px'], //宽高
                fix: false, //不固定
                maxmin: true,
                skin:'changePwd',
                content: Hussar.ctxPath+'/chpwd',
                success:function(){

                }
            });
        });
        /*
         * 退出
         */
        $('#loginout').click(function (event) {
            var operation = function () {
                window.location.href = Hussar.ctxPath + "/logout";
            };
            // Hussar.confirm("您确定要退出吗?", operation);
            layer.confirm('您确定要退出吗？', {skin: 'move-confirm',}, operation);
        });

        // 下载使用手册
        $('#downHandbook').click(function (event) {
            layui.use(['Hussar', 'HussarAjax'], function () {
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;
                $.ajaxFileUpload({
                    url: Hussar.ctxPath + "/files/downHandbook",
                    type: "post",
                    async: false
                });
            });
        });


        //计数翻页效果添加，min为实际数据减4；max为实际数据；time是时间（毫秒）；len是数字长度
        var maxNum = $("#fileCount").val();
        $(".dataStatistics-number span").html(maxNum);
        /*if (maxNum.length < 5) {
            /!*  maxNum="0"+maxNum;*!/
            $(".dataStatistics>:nth-child(1)").hide();
            $(".dataStatistics .digit_set").css({"margin": "0 2px"});
            $(".dataStatistics .set_second").css("margin", "0 4px 0 2px")
        }else {$(".dataStatistics>:nth-child(1)").show();}
        if (!!window.ActiveXObject || "ActiveXObject" in window || maxNum < 5) {

            $('.dataStatistics').dataStatistics({min: maxNum, max: maxNum, time: 1, len: 5});
        } else {
            $('.dataStatistics').dataStatistics({min: maxNum - 2, max: maxNum, time: 1000, len: 5});
        }*/
        $(".article-others").click(function () {
            layui.define('layer', function () {
                var layer = layui.layer;
                layer.msg("此文件类型不支持预览。");
            });
        });

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
                    if(data[i].keywords != undefined && data[i].keywords != null){
                        var html = "<span>"+data[i].keywords+"</span>";
                        $(".hot-search").append(html)
                    }
                }
                $(".hot-search span").click(function () {
                    var fileName = $(this).html();
                    var fileType = $("#select").val();
                    if (fileType === 8 || fileType === "8") {
                        openWin(Hussar.ctxPath + "/searchPic?keyWords=" + encodeURI(fileName) + "&fileType=" + fileType);

                    } else if (fileType === 15 || fileType === "15") {
                        openWin(Hussar.ctxPath + "/searchAuthor?keyWords=" + encodeURI(fileName));
                    } else if(fileType === 14 || fileType === "14"){
                        openWin(Hussar.ctxPath + "/entry/allEntryList?keyWords=" + encodeURI(fileName)+ "&fileType=" + fileType);
                    }
                    else {
                        openWin(Hussar.ctxPath + "/searchView?keyWords=" + encodeURI(fileName) + "&fileType=" + fileType);
                    }
                });
            }
        });

        $('#search').click(function () {
            var fileName = $("#searchInput").val();
            //搜索自动过滤特殊字符，对特殊字符不进行搜索
            fileName = fileName.replace(/[\ |\~|\`|\￥|\!|\@|\#|\$|\%|\^|\&|\*|\(|\)|\-|\_|\=|\||\\|\[|\]|\{|\}|\;|\:|\"|\'|\,|\<|\.|\>|\/|\?|\+|\？]/g, "");
            var fileType = $("#select").val();
            if (fileName != "") {
                fileName = fileName.substring(0, 30);
                fileName = fileName.replace("#", escape("#")).replace("?", escape("?")).replace("？", escape("？")).replace("$", escape("$")).replace("￥", escape("￥"));
                if (fileType === 8 || fileType === "8" ) {
                    openWin(Hussar.ctxPath + "/searchPic?keyWords=" + encodeURI(fileName) + "&fileType=" + fileType);

                } else if (fileType === 15 || fileType === "15") {
                    openWin(Hussar.ctxPath + "/searchAuthor?keyWords=" + encodeURI(fileName));
                } else if(fileType === 14 || fileType === "14"){
                    openWin(Hussar.ctxPath + "/entry/allEntryList?keyWords=" + encodeURI(fileName)+ "&fileType=" + fileType);
                }
                else {
                    openWin(Hussar.ctxPath + "/searchView?keyWords=" + encodeURI(fileName) + "&fileType=" + fileType);
                }
            } else {
                $("#searchInput").val("");
                //location.href='/';
            }
            $("#searchInput").focus();
        });
        // 关键词搜索框添加绑定回车函数
        $('#searchInput').bind('keypress', function (event) {
            if (event.keyCode == "13") {
                $("#search").click();
            }
        });


        // 更换头像
        $('#changeHeadIcon').on('click',function () {
            layer.open({
                type: 2,
                area: [ '600px',  '490px'],
                fix: false, //不固定
                maxmin: false,
                shadeClose: true,
                shade: 0.4,
                title:['更换头像','font-size:16px;font-weight:border'],
                closeBtn:2,
                offset: "30px",
                content: Hussar.ctxPath+"/changeHeadIconView"
            });
        });



        $("#searchInput").bind("input propertychange",function(event){
            var keyword = $("#searchInput").val();
            nowKeyword = keyword;
            var ajax = new $ax(Hussar.ctxPath + "/suggestList", function (data) {
                if(nowKeyword == keyword){
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
            ajax.setContentType("application/x-www-form-urlencoded");
            ajax.start();
        });
        $("#searchInput").bind("blur",function () {
            $("#searchInput").removeClass("input-hover");
            if(!isHoverKeyword){
                $("#keywordsAssociation").hide();
            }
        })
        $("#searchInput").bind("focus",function () {
            $("#searchInput").addClass("input-hover");
        })
        $("#keywordsAssociation").bind("mouseover ",function () {
            isHoverKeyword = true;
        })
        $("#keywordsAssociation").bind("mouseout ",function () {
            isHoverKeyword = false;
        })
        var initPage = function () {
            layui.use(['Hussar', 'HussarAjax','layer'], function () {
                var Hussar = layui.Hussar,
                    layer = layui.layer,
                    $ax = layui.HussarAjax;

                //文档目录菜单获取
                var ajax_menu = new $ax(Hussar.ctxPath + "/getFolderData", function (data) {
                    var floads = $("#folders");
                    var folders_other = $("#folders_other");
                    for(var i = 0;i<data.length;i++){
                        if (i<8){
                            //前八条直接显示#folders
                            var li = showLi(data[i]);
                            floads.append(li)

                        } else {
                            //其他隱藏顯示#folders_other
                            var li_hide = showLi(data[i]);
                            folders_other.append(li_hide)
                        }
                    }
                    if(data.length <= 8){
                        $(".all-top").hide();
                    }else{
                        $(".all-top").show();
                    }
                    $(".all-top").hover(function () {
                        folders_other.addClass("hover")
                    },function () {
                        folders_other.removeClass("hover")
                    })
                    $(".top-item-loading").hide();
                    floads.show();
                });
                ajax_menu.setAsync(true);
                ajax_menu.start();

                function showLi(obj) {
                    //傳入data[i]
                    var childs = obj.childs;
                    var show_span = "";
                    var hide_span = "";
                    for(var i = 0 ;i<childs.length;i++){
                        if(i<3){
                            show_span += '<span class="type-lv-2"><a  target="_blank" href="/personalcenter?menu=11&folderId='+childs[i].FOLDERID+'&folderName='+encodeURI(childs[i].FOLDERNAME)+'">'+childs[i].FOLDERNAME+'</a></span>'
                        }
                        hide_span += '<li class="folder-list">' +
                            '<a   target="_blank" href="/personalcenter?menu=11&folderId='+childs[i].FOLDERID+'&folderName='+encodeURI(childs[i].FOLDERNAME)+'">'+childs[i].FOLDERNAME+'</a>'+
                            '</li>'
                    }
                    var html = "<li class='type-lv-1 clearfix'>" +
                            "<div class='main-nav'>" +
                                "<a class='type-name' href='/personalcenter?menu=11&folderId="+obj.FOLDERID+"&folderName="+encodeURI(obj.FOLDERNAME)+"'>"+obj.FOLDERNAME+"</a>" +
                                ""+show_span+"" +
                                "<div class='more-list' id='more-list-"+obj.FOLDERID+"'>" +
                                    "<a  target='_blank' href='/personalcenter?menu=11&folderId="+obj.FOLDERID+"&folderName="+encodeURI(obj.FOLDERNAME)+"'><i class='iconfont'></i></a>" +
                                "</div>" +
                            "</div>" +
                        "   <div class='folder-detail'>" +
                        "       <div class='list-container'>" +
                        "           <div class='title'><a   target='_blank' href='/personalcenter?menu=11&folderId="+obj.FOLDERID+"&folderName="+encodeURI(obj.FOLDERNAME)+"'>"+obj.FOLDERNAME+"</a></div>" +
                        "           <ul>"+hide_span+"</ul>" +
                        "       </div>" +
                        "   </div>" +
                        "</li>";
                    return html;
                }


                //积分
                var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function (data) {
                    if (null != data && data != '' && data != undefined) {
                        if (data.integral != 0 && data.integral != null && data.integral != '') {
                            $("#num").html(data.msg);
                            $(".integral").show();
                            setTimeout(function () {
                                $(".integral").hide();
                            }, 2000)

                        }
                    }
                }, function (data) {

                });
                ajax.set("docId", null);
                ajax.set("ruleCode", 'login');
                ajax.setAsync(true);
                ajax.setContentType("application/x-www-form-urlencoded");
                ajax.start();

                //最新消息
                $.ajax({
                    url:'/newMessgeFolder',
                    data:{
                        size: 10000,
                        folderId:'7d9f267b319741ca90844efc7108db87'
                    },
                    async: true,
                    contentType: "application/x-www-form-urlencoded",
                    success: function (data) {
                        var inner = '';
                        var length=0;
                        inner+='    <div class=" swiper-slide" style="width: auto" >';
                        for(var i = 0;i<data.length;i++){
                            length+=data[i].title.length*15+36;
                            inner+=
                                // '<li style="list-style: circle ;color:rgba(238,244,252,1)" class="little">&nbsp;</li>' +

                                '<span  style=" margin-left:25px;cursor: pointer" onclick="showDoc(\''+data[i].docId+'\',\''+data[i].docType+'\')" >' +data[i].title+'</span>' ;



                        }
                        inner+='</div>';
                        $("#plane").html("");
                        $("#plane").html(inner + inner);
                        var width = $("#plane .swiper-slide").width() + 10;
                        $("#plane .swiper-slide").css({"position":"absolute","top":0,"width":width+ "px","left":'0'})
                        $("#plane .swiper-slide:nth-child(1)").css("transform","translateX(0px)");
                        $("#plane .swiper-slide:nth-child(2)").css("transform","translateX("+width+"px)");
                        var timer = setInterval(function () {
                           var x1=  $("#plane .swiper-slide:nth-child(1)").css("transform").replace(/[^0-9\-,]/g,'').split(',')[4] - 1;
                           var x2=  $("#plane .swiper-slide:nth-child(2)").css("transform").replace(/[^0-9\-,]/g,'').split(',')[4] - 1;
                           if(x1 < - width){
                               x1 = width
                           } if(x2 < - width){
                               x2 = width
                           }
                            $("#plane .swiper-slide:nth-child(1)").css("transform","translateX("+x1+"px)");
                            $("#plane .swiper-slide:nth-child(2)").css("transform","translateX("+x2+"px)");
                        },40);
                        $("#plane").hover(function () {
                            clearInterval(timer);
                        },function () {
                            timer = setInterval(function () {
                                var x1=  $("#plane .swiper-slide:nth-child(1)").css("transform").replace(/[^0-9\-,]/g,'').split(',')[4] - 1;
                                var x2=  $("#plane .swiper-slide:nth-child(2)").css("transform").replace(/[^0-9\-,]/g,'').split(',')[4] - 1;
                                if(x1 < - width){
                                    x1 = width
                                } if(x2 < - width){
                                    x2 = width
                                }
                                $("#plane .swiper-slide:nth-child(1)").css("transform","translateX("+x1+"px)");
                                $("#plane .swiper-slide:nth-child(2)").css("transform","translateX("+x2+"px)");
                            },40);
                        })
                    }
                });


                //banner滚动

                if($("#projectFlag").val()=="true"){
                    var ajax = new $ax(Hussar.ctxPath + '/banner/bannerList', function (data) {
                        var data = data.data;
                        var inner = '';
                        for (var i = 0; i < data.length; i++) {
                            inner += '  <div class="swiper-slide">';

                            inner += '<a href="'+Hussar.ctxPath+'' + data[i].bannerHref + '" target="_blank">' +
                                // '<li style="list-style: circle ;color:rgba(238,244,252,1)" class="little">&nbsp;</li>' +
                                '  <img src="'+Hussar.ctxPath+'/preview/list?fileId=' + data[i].bannerPath.replace(/\\/g, "/") + '" style=""/>';

                            inner += '</a></div>';

                        }

                        $(".center-swiper .swiper-wrapper").html("");
                        $(".center-swiper .swiper-wrapper").html(inner);

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


                    }, function (data) {

                    });
                    ajax.set("page",1);
                    ajax.set("limit",5);
                    ajax.setAsync(true);
                    ajax.setContentType("application/x-www-form-urlencoded");
                    ajax.start();
                }
                // 文档动态
                var swiper_wd
                var ajax = new $ax(Hussar.ctxPath + '/newMessge', function (data) {
                    var inner = '';
                    for (var i = 0; i < data.length; i++) {
                        if (i < 9) {
                            inner += '  <div class="swiper-slide">';
                            var time = data[i].showTime;
                            if(/^\d{4}-\d{2}-\d{2}$/.test(time)){time= time.substr(5)}
                            var authorName = data[i].authorName;
                            if (authorName == "" || authorName == undefined || authorName == null) {
                                authorName = data[i].authorId;
                            }
                            inner += ' <div class="message-info"  >' +
                                '<div class="new-message-date" ><span class="author">' + authorName + '</span><span class="time">' + time + '</span></div> ' +
                                '<div class="new-message-message" ><label class="greyCircle"></label><span title="' + data[i].title + '" onclick="showDoc(\'' + data[i].docId + '\',\'' + data[i].docType + '\')">' + data[i].title + '</span></div> </div>';

                            inner += '</div>';
                        }
                    }
                    $("#newList").html("");
                    $("#newList").html(inner);


                   swiper_wd = new Swiper('#swiper_container', {
                        direction: 'vertical', // 垂直切换选项
                        loop: true, // 循环模式选项
                        loopAdditionalSlides: 4,
                        height: 54,
                       observer:true,/*启动动态检查器，当改变swiper的样式（例如隐藏/显示）或者修改swiper的子元素时，自动初始化swiper。*/
                       observeParents:true,/*将observe应用于Swiper的父元素。当Swiper的父元素变化时，例如window.resize，Swiper更新。*/
                        autoplay: {
                            delay: 3000,
                            stopOnLastSlide: false,
                            disableOnInteraction: false
                        }
                    });
                    //
                    // var swiper = new Swiper('#swiper_container_second', {
                    //     direction: 'vertical', // 垂直切换选项
                    //     loop: true, // 循环模式选项
                    //     loopAdditionalSlides: 4,
                    //     height: 56,
                    //     autoplay: {
                    //         delay: 3000,
                    //         stopOnLastSlide: false,
                    //         disableOnInteraction: false
                    //     }
                    // });
                    //
                    swiper_wd.on('mouseover',function () {
                        swiper_wd.autoplay.stop();
                    });
                    swiper_wd.on('mouseout',function () {
                        swiper_wd.autoplay.start();
                    })


                }, function (data) {

                });
                ajax.set("size", 9);
                ajax.setAsync(true);
                ajax.setContentType("application/x-www-form-urlencoded");
                ajax.start();



                //科研成果
                var swiper_ky;
                var ajax = new $ax(Hussar.ctxPath + '/newComponentMessage', function (result) {
                    var data = result.list;
                    var count = result.count;
                    $("#count").html(count);
                    var inner = '';
                    if (!(data instanceof Array)) {
                        return
                    }
                    for (var i = 0; i < data.length; i++) {
                        if (i < 9) {
                            inner += '  <div class="swiper-slide">';
                            var time = data[i].showTime;
                            inner += ' <div class="message-info"  >' +
                                '<div class="new-message-date" ><span class=" author">' + data[i].userName + '</span><span class="time">' + time + '</span></div> ' +
                                '<div class="new-message-message" ><label class="greyCircle"></label><span title="' + data[i].componentName + '" onclick="dbclick(\'' + data[i].componentId + '\')">' + data[i].componentName + '</span></div> ' +
                                '</div>';

                            inner += '</div>';
                        }
                    }

                    $("#newComponent").html("");
                    $("#newComponent").html(inner);


                     swiper_ky = new Swiper('#achievements', {
                         direction: 'vertical', // 垂直切换选项
                         loop: true, // 循环模式选项
                         loopAdditionalSlides: 4,
                         height: 54,
                         observer:true,/*启动动态检查器，当改变swiper的样式（例如隐藏/显示）或者修改swiper的子元素时，自动初始化swiper。*/
                         observeParents:true,/*将observe应用于Swiper的父元素。当Swiper的父元素变化时，例如window.resize，Swiper更新。*/
                         autoplay: {
                             delay: 3000,
                             stopOnLastSlide: false,
                             disableOnInteraction: false
                         }
                    });

                    swiper_ky.on('mouseover',function () {

                        swiper_ky.autoplay.stop();
                    });
                    swiper_ky.on('mouseout',function () {
                        swiper_ky.autoplay.start();
                    })

                }, function (data) {

                });
                ajax.setAsync(true);
                ajax.set("size", 9);
                ajax.setContentType("application/x-www-form-urlencoded");
                ajax.start();

                //科研成果 与 文档信息 切换
                $(".right-tab span").click(function () {
                    var index = $(this).index();
                    $(this).siblings().removeClass("active");
                    $(this).addClass("active");
                    $(".newList").eq(index).siblings().removeClass("active");
                    $(".newList").eq(index).addClass("active");
                    if(index === 0){
                        swiper_wd = new Swiper('#swiper_container');
                    }else{
                        swiper_ky = new Swiper('#achievements');
                    }

                })


                //上传排行
                var ajax = new $ax(Hussar.ctxPath + '/getUploadRank', function (data) {
                    var inner = '';
                    var className = "",imgClass = "";
                    for (var i = 0; i < data.length; i++) {
                        if(i == 0){
                            className = "imp-num";
                            imgClass = "person-img-imp"
                        }else if(i == 1){
                            className = "imp-num2";
                            imgClass = "person-img-imp"
                        }else if(i == 2){
                            className = "imp-num3";
                            imgClass = "person-img-imp"
                        }else {
                            className = "";
                            imgClass = ""
                        }
                        if (i < 6) {
                            var img = "";
                            if(data[i].path == undefined||data[i].path==""||data[i].path==null){
                                img=personalPic
                            }else{
                                img = "/preview/list?fileId="+data[i].path+"&time="+(new Date().getTime());
                            }

                            var deptName = ""
                            var shortDeptName = "";
                            if( data[i].SHORT_NAME== undefined||data[i].SHORT_NAME==""||data[i].SHORT_NAME==null){
                                deptName = projectTitle;
                                shortDeptName = projectTitle;
                            }else{
                                deptName =data[i].SHORT_NAME;
                                shortDeptName = data[i].SHORT_NAME;
                            }
                            if(shortDeptName.length>7){
                                shortDeptName=shortDeptName.substring(0,7)+'...';
                            }


                            inner += '<div class="rank-item clearfix">' +
                                '<div class="number '+className+'"><span>'+(i+1)+'</span></div>' +
                                '<div class="person-img '+imgClass+'"><img class="user-photo" src=' + (img) + '>' + '</div>' +
                                '<div class="rank-info">' +
                                     "<div class='user-info'><span class='user'>" + data[i].USER_NAME + "</span><span class='depart' title='" + deptName + "' >" + shortDeptName + "</span></div>" +
                                    "<div class='file-num'>上传文档数量：" + "<span>" + data[i].FILENUM + "篇</span>" + "</div>" +
                                '</div>' +
                                '<div class= "rank-img rank-img-' + data[i].FILERANK + '">' + '</div>' +
                                '</div>';
                        }

                    }
                    $("#uploadRank").html("");
                    $("#uploadRank").html(inner);
                }, function (data) {
                });
                ajax.setAsync(true);
                ajax.set("size", 7);
                ajax.setContentType("application/x-www-form-urlencoded");
                ajax.start();

                //热门文档
                function getDoc() {
                    var ajax = new $ax(Hussar.ctxPath + '/getOpTypeRank', function (result) {
                        var data = result.list;
                        var inner = "";
                        var imgsrc = "";
                        var photo = personalPic;
                        var MAXDOC = 8;
                        if (!(data instanceof Array)) {
                            return
                        }
                        for (var i = 0; i < data.length; i++) {
                            var img = "";
                            if (i >= MAXDOC) {
                                i = 0;
                                break;
                            }
                            if(data[i].url == undefined||data[i].url==""||data[i].url==null){
                                img=personalPic;
                            }else{
                                img = "/preview/list?fileId="+data[i].url;
                            }
                            var userName = ""
                            if(data[i].userName== undefined||data[i].userName==""||data[i].userName==null){
                                userName="离职人员";
                            }else{
                                userName = data[i].userName;
                            }
                            var deptName = ""
                            var shortDeptName = "";
                            if( data[i].deptName== undefined||data[i].deptName==""||data[i].deptName==null){
                                deptName = projectTitle;
                                shortDeptName = projectTitle;
                            }else{
                                deptName =data[i].deptName;
                                shortDeptName = data[i].deptName;
                            }
                            if(shortDeptName.length>5){
                                shortDeptName=shortDeptName.substring(0,5)+'...';
                            }

                            //设置最多显示文档数
                            imgsrc = getFilePic(data[i].DOCTYPE);

                            inner += "  <div onclick=\"showDoc(\'" + data[i].DOCID + "\',\'" + data[i].DOCTYPE + "\')\" class='doc-detail doc-detail-1'>\n" +
                                "                        <div class='doc-detail-title' title='" + data[i].TITLE + "'><img  src=" + Hussar.ctxPath + imgsrc + ">" + data[i].TITLE + "  </div>\n" +
                                "                        <div class='date'><span>上传日期：" + data[i].createTime.slice(0, 10) + "</span>| <span>"+data[i].fileSize  +"</span></div>\n" +
                                "                        <ul>\n" +
                                "                            <li><img src='" + Hussar.ctxPath + (img) + "'></li>\n" +
                                "                            <li title='" + deptName + "'>" + shortDeptName + "</li>\n" +
                                "                            <li>" + userName + "</li>\n" +
                                "                            <li class='last'>" + " <div class='download'><span>" + data[i].YLCOUNT+ "</span>次浏览</div>" + "</li>\n" +
                                "                        </ul>\n" +
                                "                    </div>"
                        }

                        $("#doc-body").html("");
                        $("#doc-body").html(inner);
                        changeTotal = result.total;
                        if (changePage == Math.floor(changeTotal/8)+1){
                            changePage = 1;
                        }else {
                            changePage++;
                        }
                    }, function (data) {

                    });
                    ajax.set("opType", 3);
                    ajax.set("pageNumber", changePage);
                    ajax.set("pageSize", 8);
                    ajax.set("changeTotal",changeTotal);
                    ajax.setAsync(true);
                    ajax.setContentType("application/x-www-form-urlencoded");
                    ajax.start();
                }

                getDoc();
                //换一换
                $(".more-doc").click(function () {
                    getDoc();
                });
                //专题数据获取
                function getTopicDetail(id) {
                    var ajax = new $ax(Hussar.ctxPath + '/getTopicDetail', function (data) {
                        var inner_body = "";
                        inner_body += " <div class='topic-detail topic-detail-" + " description' onclick='toTopic('" + data.topicId + "')'>\n" +
                            "<a href='" + Hussar.ctxPath + "/frontTopic/topicDetail?topicId=" + data.topicId + "&page=1&size=10' target='_blank'>" +
                            "                        <div class='title'>" + data.topicName + "</div>\n" +
                            "                        <div class=\"subtitle\" title='" + data.topicDesc + "'>" + data.topicDesc + "</div>\n" +
                            "<button class='btn-all'>查看所有</button>" +
                            "          </a></div>";
                        var docList = data.docList || [];
                        for (var j = 0; j < docList.length; j++) {
                            var authorName = docList[j].authorName;
                            if (authorName != "" && authorName != undefined && authorName != null) {
                                // authorName = docList[j].authorId;
                            }else{
                                authorName = "管理员";
                            }
                            if (j < 8) {
                                var imgsrc = getFilePic(docList[j].fileType);
                                var img = "";
                                var isNew = "";
                                if(docList[j].isNew){
                                    isNew = "new";
                                }
                                if(docList[j].url == undefined||docList[j].url==""||docList[j].url==null){
                                    img=personalPic
                                }else{
                                    img = "/preview/list?fileId="+docList[j].url;
                                }

                                var deptName = ""
                                var shortDeptName = "";
                                if( docList[j].deptName == "" || docList[j].deptName == undefined || docList[j].deptName == null){
                                    deptName = projectTitle;
                                    shortDeptName = projectTitle;
                                }else{
                                    deptName =docList[j].deptName;
                                    shortDeptName = docList[j].deptName;
                                }
                                if(shortDeptName.length>8){
                                    shortDeptName=shortDeptName.substring(0,8)+'...';
                                }


                                if (docList[j].fileType=='folder'){
                                    inner_body += "<div  onclick=\"showDoc(\'" + docList[j].doc_id + "\',\'" + docList[j].fileType + "\',\'" + docList[j].topic_id + "\',\'" + docList[j].title + "\')\" class='topic-detail topic-detail-"  + "'>\n" +
                                        "                        <div  class='"+isNew+" topic-detail-title' title='" + docList[j].title + "'><img\n" +
                                        "                                src=" + Hussar.ctxPath + imgsrc + ">" + docList[j].title + "\n" +
                                        //"<img class='new_pic' src='"+Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-new.svg'>" +
                                        "                        </div>\n" +
                                        "                        <div class='date'><span>上传日期：" + docList[j].createTime.slice(0, 10) + "</span></div>\n" +
                                        "                        <ul>\n" +
                                        "                            <li><img src='" + Hussar.ctxPath + (img) + "'></li>\n" +
                                        "                            <li title='"+deptName+"'>" +shortDeptName + "</li>\n" +
                                        "                            <li>" + authorName + "</li>\n" +
                                        "                        </ul>\n" +
                                        "                    </div>"
                                }else {
                                    inner_body += "<div  onclick=\"showDoc(\'" + docList[j].doc_id + "\',\'" + docList[j].fileType + "\',\'" + docList[j].topic_id + "\',\'" + docList[j].title + "\')\" class='topic-detail topic-detail-" + "'>\n" +
                                        "                        <div class='"+isNew+" topic-detail-title' title='" + docList[j].title + "'><img\n" +
                                        "                                src=" + Hussar.ctxPath + imgsrc + ">" + docList[j].title + "\n" +
                                        //"<img class='new_pic' src='"+Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-new.svg'>" +
                                        "                        </div>\n" +
                                        "                        <div class='date'><span>上传日期：" + docList[j].createTime.slice(0, 10) + "</span>| <span>"+ docList[j].fileSize  +"</span></div>\n"+
                                        "                        <ul>\n" +
                                        "                            <li><img src='" + Hussar.ctxPath + (img) + "'></li>\n" +
                                        "                            <li title='"+deptName+"'>" +shortDeptName + "</li>\n" +
                                        "                            <li>" + authorName + "</li>\n" +
                                        "                            <li class='last'>" + "<div class='downLoad'><span>" +docList[j].readNum + "</span>次浏览</div>" + "</li>\n" +
                                        "                        </ul>\n" +
                                        "                    </div>"
                                }
                            }

                        }
                        $("#topic-body").html("");
                        $("#topic-body").html(inner_body);
                        $(".topic-loading").hide();
                        $("#topic-body").show();
                        setTimeout(function () {
                            $(".description").eq(0).siblings().removeClass('opShow');
                            $(".description").eq(0).addClass('opShow');
                        },100)
                        $("#moreTopic a").prop("href", "/frontTopic/topicDetail?topicId=" + $(".topic-title-list")[0].getAttribute("topicId") + "&page=1&size=10");
                    });
                    ajax.set("id", id );
                    ajax.setAsync(true);
                    ajax.setContentType("application/x-www-form-urlencoded");
                    ajax.start();

                }
                //分开  1.专题名称 2.专题列表
                var ajax = new $ax(Hussar.ctxPath + '/getTopicList', function (data) {
                    var inner_title = '';
                    var photo = personalPic;
                    var inner_body = '';
                    var one =1;
                    for (var i = 0; i < data.length; i++) {
                        var projectFlag = $("#projectFlag").val();
                        if(projectFlag!="true"){
                            if (data && !data.length > 0) {
                                if(one==(i+1)){
                                    one = parseInt(one)+1;
                                }
                                continue;
                            }
                        }
                        inner_title += "<li topicId='" + data[i].topicId + "' class='topic-title-list' id='topic-title-" + (i + 1) + "'>" + data[i].topicName + "</li>"
                    }
                    var firstTopic = data[0];
                    $(".topic-title").html("");
                    $(".topic-title").html(inner_title);
                    $(".topic-title2").hide();
                    $(".topic-title").show();
                    $("#topic-title-"+one).addClass("active");
                    if (firstTopic!=null && firstTopic!=undefined){
                       getTopicDetail(firstTopic.topicId);
                    }else{
                        $(".topic-loading").hide();
                    }
                    //topic类型切换
                    $(".topic-title-list").click(function () {
                        var index = $(this).index();
                        var id = this.id;
                        var topicId = this.getAttribute("topicId");
                        getTopicDetail(topicId);
                        $(".topic-title-list.active").removeClass("active");
                        $(".topic-detail.active").removeClass("active");
                        var className = id.replace('title', 'detail');
                        $("#" + id).addClass('active');
                        $("." + className).addClass('active');
                        $("#moreTopic a").prop("href", "/frontTopic/topicDetail?topicId=" + topicId + "&page=1&size=10");
                        setTimeout(function () {
                            $(".description").eq(index).siblings().removeClass('opShow');
                            $(".description").eq(index).addClass('opShow');
                        },100)
                    });
                });
                ajax.setAsync(true);
                ajax.setContentType("application/x-www-form-urlencoded");
                ajax.start();


// 热门词条
                var ajax = new $ax(Hussar.ctxPath + '/getHotEntry', function (data) {
                    console.log(data,'datadata')
                    // var data = result.list;
                    var inner = "";
                    // var imgsrc = "";
                    // var photo = personalPic;
                    // var MAXDOC = 8;
                    // if (!(data instanceof Array)) {
                    //     return
                    // }
                    for (var i = 0; i < data.length; i++) {
                        var img = "/static/resources/img/front/index/photo.png";
                        // if (i >= MAXDOC) {
                        //     i = 0;
                        //     break;
                        // }
                        // if(data[i].url == undefined||data[i].url==""||data[i].url==null){
                        //     img=personalPic;
                        // }else{
                        //     img = "/preview/list?fileId="+data[i].url;
                        // }
                        // var userName = ""
                        // if(data[i].createUserName== undefined||data[i].createUserName==""||data[i].userName==null){
                        //     userName="离职人员";
                        // }else{
                        //     userName = data[i].createUserName;
                        // }
                        var deptName = ""
                        var shortDeptName = "";
                        if( data[i].deptName== undefined||data[i].deptName==""||data[i].deptName==null){
                            deptName = projectTitle;
                            shortDeptName = projectTitle;
                        }else{
                            deptName =data[i].deptName;
                            shortDeptName = data[i].deptName;
                        }
                        if(shortDeptName.length>5){
                            shortDeptName=shortDeptName.substring(0,5)+'...';
                        }

                        //设置最多显示文档数
                        imgsrc = getFilePic('词条');

                        inner += "  <div onclick=\"showDoc(\'" + data[i].id + "\',\'" + 'entry' + "\')\" class='doc-detail doc-detail-1'>\n" +
                            "                        <div class='doc-detail-title' title='" + data[i].name + "'><img  src=" + Hussar.ctxPath + imgsrc + ">" + data[i].name + "  </div>\n" +
                            "                        <ul>\n" +
                            "                            <li><img src='" + Hussar.ctxPath + (img) + "'></li>\n" +
                            "                            <li title='" + deptName + "'>" + shortDeptName + "</li>\n" +
                            "                            <li>" +data[i].createUserName + "</li>\n" +
                            "                            <li class='last'>" + " <div class='download' style='top: 0px;'><span>" + data[i].readNum+ "</span>次浏览</div>" + "</li>\n" +
                            "                        </ul>\n" +
                            "                    </div>"
                    }

                    $("#entrybody").html("");
                    $("#entrybody").append(inner);
                    // changeTotal = result.total;
                    // if (changePage == Math.floor(changeTotal/8)+1){
                    //     changePage = 1;
                    // }else {
                    //     changePage++;
                    // }
                }, function (data) {
                });
                ajax.setAsync(true);
                ajax.start();

            });
        }
    });
});

//跳转专题
function toTopic(id, type, topicId, title) {
    layui.use(['laypage', 'layer', 'Hussar'], function () {
        var laypage = layui.laypage,
            layer = layui.layer;
        var Hussar = layui.Hussar;
        var num = $("#curr").val();
        laypage.render({
            elem: 'laypageAre'
            , pages: 2
            , limit: 10
            , layout: ['prev', 'page', 'next']
            , curr: num || 1
            , jump: function (obj, first) {
                openWin(Hussar.ctxPath + "/frontTopic/topicDetail?topicId="
                    + topicId + "&page=" + obj.curr + "&size=10&folderId=" + id + "&pathName=" + title + "&pathId=" + id);
            }
        });
    });

}

//科研成果跳转
function show(id, type, name) {
    layui.use(['Hussar', 'HussarAjax'], function () {
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        openWin(Hussar.ctxPath + "/personalcenter?menu=12");
    });

}

function dbclick(id, type, name) {
    layui.use(['Hussar', 'HussarAjax'], function () {
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        openWin(Hussar.ctxPath + "/toShowComponent/toShowPDF?id=" + id);
    });

}
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}

function showDoc(id, fileType,topicId,title) {
    var selectVal = '0';
    var keyWords = '';
    if(fileType==="folder" ){
        toTopic(id,fileType,topicId,title)
    }else if(fileType==='entry'){
        openWin(Hussar.ctxPath + "/entry/entryPreview?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
    }
    else {
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
            ajax.setContentType("application/x-www-form-urlencoded");
            ajax.start();
        });
    }
}

/**
 * 获取当前所有积分
 * @returns {number}
 */

var integral = 0;
layui.use(['Hussar', 'HussarAjax'], function () {
    var Hussar = layui.Hussar,
        $ax = layui.HussarAjax;

    var ajax = new $ax(Hussar.ctxPath + '/integral/getTotalIntegral', function (data) {
        integral = parseInt(data);
        $(".totalIntegral", document).text(integral);
        $("#totalIntegral", document).text(integral);
    }, function (data) {

    });

    ajax.setContentType("application/x-www-form-urlencoded");
    ajax.setAsync(true);
    ajax.start();
});

/**
 * 获取当前所有上传数量
 * @returns {number}
 */


var totalUpload = 0;
layui.use(['Hussar', 'HussarAjax'], function () {
    var Hussar = layui.Hussar,
        $ax = layui.HussarAjax;

    var ajax = new $ax(Hussar.ctxPath + '/personalUpload/getlist', function (data) {
        totalUpload = parseInt(data.total);
        $(".totalUpload", document).text(totalUpload);
    }, function (data) {

    });

    ajax.setContentType("application/x-www-form-urlencoded");
    ajax.setAsync(true);
    ajax.start();
});

/**
 * 根据文件类型获取文件图片数据
 */
function getFilePic(fileType) {
    var imgsrc = "";
    if (fileType === 'folder') {
        imgsrc = "/static/resources/img/front/file-icon/ic-folder26.png";
    } else if(fileType=='词条'){
        imgsrc = "/static/doc/front/entry/img/defaultse.png";
    }
    else if (fileType === '.doc' || fileType === '.docx') {
        imgsrc = "/static/resources/img/front/file-icon/ic-word15.png";
    } else if (fileType === '.txt') {
        imgsrc = "/static/resources/img/front/file-icon/ic-text15.png";
    } else if (fileType === '.ppt' || fileType === '.pptx'|| fileType === '.ppsx') {
        imgsrc = "/static/resources/img/front/file-icon/ic-ppt15.png";
    } else if (fileType === '.pdf') {
        imgsrc = "/static/resources/img/front/file-icon/ic-pdf15.png";
    } else if (fileType === '.ceb') {
        imgsrc = "/static/resources/img/front/file-icon/ic-ceb15.png";
    } else if (['.mp3', '.real', '.cd', '.ogg', '.asf', '.wav', '.ape', '.module', '.midi'].indexOf(fileType) != -1) {
        imgsrc = "/static/resources/img/front/file-icon/ic-audio15.png";
    } else if (['.mp4', '.avi', '.wma', '.rmvb', '.rm', '.flash'].indexOf(fileType) != -1) {
        imgsrc = "/static/resources/img/front/file-icon/ic-video15.png";
    } else if (fileType === '.xls' || fileType === '.xlsx') {
        imgsrc = "/static/resources/img/front/file-icon/ic-excel15.png";
    } else if (['.png', '.jpeg', '.gif', '.jpg'].indexOf(fileType) != -1) {
        imgsrc = "/static/resources/img/front/file-icon/ic-img15.png";
    } else if (['.bmp'].indexOf(fileType) != -1) {
        imgsrc = "/static/resources/img/front/file-icon/ic-bmp15plus.png";
    } else if (['.psd'].indexOf(fileType) != -1) {
        imgsrc = "/static/resources/img/front/file-icon/ic-psd15.png";
    } else if (['.html'].indexOf(fileType) != -1) {
        imgsrc = "/static/resources/img/front/file-icon/ic-html15.png";
    } else if (['.exe'].indexOf(fileType) != -1) {
        imgsrc = "/static/resources/img/front/file-icon/ic-exe15.png";
    } else if (['.zip', '.rar'].indexOf(fileType) != -1) {
        imgsrc = "/static/resources/img/front/file-icon/ic-zip15.png";
    } else {
        imgsrc = "/static/resources/img/front/file-icon/ic-other15.png";
    }
    return imgsrc;
}
