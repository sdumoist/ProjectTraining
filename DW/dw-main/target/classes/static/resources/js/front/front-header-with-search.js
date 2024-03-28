// $(".global-search").click(function () {
var isHoverKeyword = false;
var nowKeyword = "";
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
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;
       form.render("select");

    $(function () {
        $("body").scroll(function () {
            if ($("body").scrollTop() > 0) {
                $(".header-with-search-2").addClass("fix")
            } else {
                $(".header-with-search-2").removeClass("fix")
            }
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

        if($('#headerSearchInput').val() != ''){
            $(".global-search-input").show();
            $(".global-search").hide();
        }
        $("#headerSearchInput").bind("input propertychange",function(event){
            var keyword = $("#headerSearchInput").val();
            nowKeyword = keyword;
            var ajax = new $ax(Hussar.ctxPath + "/suggestList", function (data) {
                if(keyword == nowKeyword){
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
            if(!isHoverKeyword){
                $("#keywordsAssociation").hide();
            }
        })
        $("#keywordsAssociation").bind("mouseover ",function () {
            isHoverKeyword = true;
        })
        $("#keywordsAssociation").bind("mouseout ",function () {
            isHoverKeyword = false;
        })
        if(!placeholderSupport()){   // 判断浏览器是否支持 placeholder
            $('[placeholder]').focus(function() {
                var input = $(this);
                if (input.val() == input.attr('placeholder')) {
                    input.val('');
                    input.removeClass('placeholder');
                }
            }).blur(function() {
                var input = $(this);
                if (input.val() == '' || input.val() == input.attr('placeholder')) {
                    input.addClass('placeholder');
                    input.val(input.attr('placeholder'));
                }
            }).blur();
        }
        function placeholderSupport() {
            return 'placeholder' in document.createElement('input');
        }
        // function searchEs() {
        //     var fileName = $("#headerSearchInput").val();
        //     //搜索自动过滤特殊字符，对特殊字符不进行搜索
        //     fileName = fileName.replace(/[\ |\~|\`|\￥|\!|\@|\#|\$|\%|\^|\&|\*|\(|\)|\-|\_|\=|\||\\|\[|\]|\{|\}|\;|\:|\"|\'|\,|\<|\.|\>|\/|\?|\+|\？]/g,"");
        //     if (fileName != "") {
        //         fileName =fileName.substring(0,30);
        //         $("#headerSearchInputValue").val(fileName);
        //         var selectVal=$("#select").val()
        //         fileName = fileName.replace("#", escape("#")).replace("?", escape("?")).replace("？", escape("？")).replace("$", escape("$")).replace("￥", escape("￥"));
        //         location.href="/searchView?keyWords=" + encodeURI(fileName) + "&fileType=" + selectVal;
        //     } else {
        //         location.href="/";
        //     }
        // }


        // 更换头像
        $('#changeHeadIcon').on('click',function () {
            layer.open({
                type: 2,
                area: [ '600px',  '500px'],
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


        $('#headerSearchBtn').click(function () {
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
                }else if(selectVal===14 || selectVal === "14"){
                    newUrl=Hussar.ctxPath+"/entry/allEntryList?keyWords=" + encodeURI(fileName)+ "&fileType=" + selectVal;
                }
                else {
                    newUrl = Hussar.ctxPath+"/searchView?keyWords=" + encodeURI(fileName) + "&fileType=" + selectVal;
                }

                if(currentHref.indexOf("personalcenter")>-1){ // 处于个人中心页面
                    window.open(newUrl);
                }else{
                    location.href=newUrl;
                }
            } else {

            }
        });
        // 关键词搜索框添加绑定回车函数
        $('#headerSearchInput').keydown(function (event) {
            if (event.keyCode === 13) {
                $("#headerSearchBtn").trigger("click");
                $(".global-search-input").removeClass("focus");
            }
        });

        $('#headerSearchInput').focus(function (event) {
            $(".global-search-input").addClass("focus");
        }).blur(function (event) {
            $(".global-search-input").removeClass("focus");
        });

        $('#loginout').click(function(event){
            layui.use('layer', function(){
                var layer = layui.layer;
                layer.confirm('您确定要退出吗?', {
                    btn: ['确定', '取消'] ,//可以无限个按钮
                    offset:'40%',
                    skin:'move-confirm',
                }, function(index, layero){
                    window.location.href = Hussar.ctxPath+"/logout";
                }, function(index){
                    layer.closeAll();
                });
            });
        });

        //获取菜单
        /*$.ajax({
            async:false,
            type:"post",
            url: Hussar.ctxPath+"/getNav",
            dataType:"json",
            success:function(data) {
                var ul_types = $(".ul-types");
                var html_in = "";
                if(data.length >5){
                    for(var i = 0 ;i<5;i++){
                        html_in = html_in +"<a class='fold-type' target='_blank' title='"+data[i].folderName+"' href='"+Hussar.ctxPath+"/personalcenter?menu=11&folderId="+data[i].folderId+"&folderName="+encodeURIComponent(data[i].folderName)+ "&" + Math.random() + "'>"+data[i].folderName+"</a>"
                    }
                    html_in = html_in + "<div class='fold-more'>" +
                        "            <span>更多<i class=\"iconfont\">&#xe61a;</i></span>" +
                        "            <div class='moreBox'>";
                    var kk = 9;
                    if (data.length < 9){
                        kk = data.length;
                    }

                    for (var n = 5 ;n<kk;n++) {
                        html_in = html_in +"<a class='fold-type' target='_blank' title='"+data[n].folderName+"' href='"+Hussar.ctxPath+"/personalcenter?menu=11&folderId="+data[n].folderId+"&folderName="+encodeURIComponent(data[n].folderName)+ "&" + Math.random()+"'>"+data[n].folderName+"</a>"
                    }
                    if (data.length >=9) {
                        html_in = html_in + " <a href='"+Hussar.ctxPath+"/personalcenter' class ='all' target='_blank' >全部>></a>"
                    }
                    html_in = html_in +"</div></div>"
                }else {
                    for(var m = 0 ;m<data.length;m++){
                        html_in = html_in +"<a class='fold-type' target='_blank' title='"+data[m].folderName+"' href='"+Hussar.ctxPath+"/personalcenter?menu=11&folderId="+data[m].folderId+"&folderName="+encodeURIComponent(data[m].folderName)+ "&" + Math.random()+"'>"+data[m].folderName+"</a>"
                    }
                }
                ul_types.html(html_in);
                $(".curtain").click(function () {
                    $(this).hide();
                    $(".user-dropdown").css("display","none");
                    $(".moreBox").css("display","none");
                });
                //ipad中点击出现菜单点击其他地方隐藏
                $(".user-wrapper").click(function (event) {
                    if(window.screen.width <= 1024){
                        $(".user-dropdown").css("display","block");
                        $(".curtain").css("display","block");
                        $(".moreBox").css("display","none");
                        event.stopPropagation()}
                });
                $(".fold-more").click(function (event) {
                    if(window.screen.width <= 1024){
                        $(".moreBox").css("display","block");
                        $(".curtain").css("display","block");
                        $(".user-dropdown").css("display","none");
                        event.stopPropagation()}
                });

            },
            error:function(data){
                $.showInfoDlg("提示","获取菜单失败",2);
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/getNav", function(data) {
            var ul_types = $(".ul-types");
            var html_in = "";
            if(data.length >5){
                for(var i = 0 ;i<5;i++){
                    html_in = html_in +"<a class='fold-type' target='_blank' title='"+data[i].folderName+"' href='"+Hussar.ctxPath+"/personalcenter?menu=11&folderId="+data[i].folderId+"&folderName="+encodeURIComponent(data[i].folderName)+ "&" + Math.random() + "'>"+data[i].folderName+"</a>"
                }
                html_in = html_in + "<div class='fold-more'>" +
                    "            <span>更多<i class=\"iconfont\">&#xe61a;</i></span>" +
                    "            <div class='moreBox'>";
                var kk = 9;
                if (data.length < 9){
                    kk = data.length;
                }

                for (var n = 5 ;n<kk;n++) {
                    html_in = html_in +"<a class='fold-type' target='_blank' title='"+data[n].folderName+"' href='"+Hussar.ctxPath+"/personalcenter?menu=11&folderId="+data[n].folderId+"&folderName="+encodeURIComponent(data[n].folderName)+ "&" + Math.random()+"'>"+data[n].folderName+"</a>"
                }
                if (data.length >=9) {
                    html_in = html_in + " <a href='"+Hussar.ctxPath+"/personalcenter' class ='all' target='_blank' >全部>></a>"
                }
                html_in = html_in +"</div></div>"
            }else {
                for(var m = 0 ;m<data.length;m++){
                    html_in = html_in +"<a class='fold-type' target='_blank' title='"+data[m].folderName+"' href='"+Hussar.ctxPath+"/personalcenter?menu=11&folderId="+data[m].folderId+"&folderName="+encodeURIComponent(data[m].folderName)+ "&" + Math.random()+"'>"+data[m].folderName+"</a>"
                }
            }
            ul_types.html(html_in);
            $(".curtain").click(function () {
                $(this).hide();
                $(".user-dropdown").css("display","none");
                $(".moreBox").css("display","none");
            });
            //ipad中点击出现菜单点击其他地方隐藏
            $(".user-wrapper").click(function (event) {
                if(window.screen.width <= 1024){
                    $(".user-dropdown").css("display","block");
                    $(".curtain").css("display","block");
                    $(".moreBox").css("display","none");
                    event.stopPropagation()}
            });
            $(".fold-more").click(function (event) {
                if(window.screen.width <= 1024){
                    $(".moreBox").css("display","block");
                    $(".curtain").css("display","block");
                    $(".user-dropdown").css("display","none");
                    event.stopPropagation()}
            });
        }, function(data) {
            $.showInfoDlg("提示","获取菜单失败",2);
        });
        ajax.start();
        layui.use(['form'], function () {
            var form = layui.form;
            $('select[name="selectModule"]').next().find('.layui-select-title input').attr("onFocus","this.blur()");
        });
        /**
         * 获取当前所有积分co
         * @returns {number}
         */
        $("#totalIntegral",document).text(getTotalIntegral());
        function getTotalIntegral(){
            var integral = 0;
            /*$.ajax({
                type:'post',
                url: Hussar.ctxPath+'/integral/getTotalIntegral',
                async:false,
                success: function (data) {
                    integral = parseInt(data);
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + '/integral/getTotalIntegral', function(data) {
                integral = parseInt(data);
            }, function(data) {

            });
            ajax.start();
            return integral;
        }
    });
});
