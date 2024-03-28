/**
 * @Description: 首页面脚本文件
 * @Author: sunZengXin
 * @Date: 2018/3/8.
 */

///**
// * 浏览器关闭事件
// * liangdong
// */
//window.onunload = function() { 			
//		$.ajax({
//    		type:"GET",
//    		contentType:"application/x-www-form-urlencoded",
//    		url:"/logout",
//    		async:false
//    	});
//	};

layui.define(['jquery', 'form', 'layer', 'element', 'Hussar'], function(exports) {
    var $ = layui.jquery,
        form = layui.form,
        layer = layui.layer,
        element = layui.element,
        Hussar = layui.Hussar;
    var menu = [];
    var curMenu;
    var userId=$("#userId").val();
    /*
     * @todo 初始化加载完成执行方法
     * 打开或刷新后执行
     */
    $(function() {
        $(".layui-nav-item>a").attr("title","收起菜单");
        // 展开收缩菜单点击事件
        $(".layui-nav-item>a").click(function () {
            if(0 == $(this).find(".layui-icon-spread-left").length){
                $(this).attr("title","展开菜单")
            }else {
                $(this).attr("title","收起菜单")
            }

        })

        /*
         * @todo 读取本地存储中记录的已打开的tab项
         * 刷新后，读取记录，打开原来已打开的tab项
         */

        /*
         * @todo table事件
         */
        layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar','element'], function(){
            var $ = layui.jquery,
                form=layui.form, $ax = layui.HussarAjax;

        var ajax = new $ax(Hussar.ctxPath + "/empStatistics/showSize", function(data) {
            if(data.limit=='0'){
                $("#used").html("不限");
                $("#total").html("不限");
                var withs="0%";
                $(".size-lack").width(withs)
            }else{
                $("#used").html(data.lack);
                $("#total").html(data.total);
                var withs=data.present+"%";
                $(".size-lack").width(withs)
            }

        }, function(data) {

        });
        ajax.start();
        });
        tableCheck = {
            init: function() {
                $(".layui-form-checkbox").click(function(event) {
                    if($(this).hasClass('layui-form-checked')) {
                        $(this).removeClass('layui-form-checked');
                        if($(this).hasClass('header')) {
                            $(".layui-form-checkbox").removeClass('layui-form-checked');
                        }
                    } else {
                        $(this).addClass('layui-form-checked');
                        if($(this).hasClass('header')) {
                            $(".layui-form-checkbox").addClass('layui-form-checked');
                        }
                    }
                });
            },
            getData: function() {
                var obj = $(".layui-form-checked").not('.header');
                var arr = [];
                obj.each(function(index, el) {
                    arr.push(obj.eq(index).attr('data-id'));
                });
                return arr;
            }
        }
        //开启表格多选
        tableCheck.init();
        //延时加载
        setTimeout(function() {
            //不加载缓存菜单
            return false;
            if(sessionStorage.getItem(userId+"menu")) {
                menu = JSON.parse(sessionStorage.getItem(userId+"menu"));
                for(var i = 0; i < menu.length; i++) {
                    tab.tabAdd(menu[i].title, menu[i].url, menu[i].id);
                }
            } else {
                return false;
            }
            if(sessionStorage.getItem(userId+'curMenu')) {
                $('.layui-tab-title').find('layui-this').removeClass('layui-class');
                curMenu = JSON.parse(sessionStorage.getItem(userId+'curMenu'));
                id = curMenu.id;
                if(id) { //因为默认桌面首页不存在lay-id,所以要对此判断
                    $('.layui-tab-title li[lay-id="' + id + '"]').addClass('layui-this');
                    tab.tabChange(id);
                } else {
                    $(".layui-tab-title li").eq(0).addClass('layui-this'); //未生效
                    $('.layui-tab-content iframe').eq(0).parent().addClass('layui-show');
                }
            } else {
                $(".layui-tab-title li").eq(0).addClass('layui-this'); //未生效
                $('.layui-tab-content iframe').eq(0).parent().addClass('layui-show');

            }
        }, 100);
        //初始化加载结束

        /**
         * 	判断是否需要设置密保和修改密码
         */
        var firstLogin = $('#firstLogin').val();
        var ischangePwd = $('#ischangePwd').val();
        if (firstLogin == 'YES' && ischangePwd == 'YES') {
            //既要设置密保又要修改密码
            layer.open({
                type: 2,
                title: '设置找回密码的问题和答案',
                area: ['450px', '250px'], //宽高
                fix: false, //不固定
                maxmin: true,
                closeBtn: 0, //去掉关闭按钮
                content: Hussar.ctxPath + '/user/setgbpwd',
                end:function(){
                    layer.open({
                        type: 2,
                        title: '修改密码',
                        area: ['450px', '300px'], //宽高
                        fix: false, //不固定
                        maxmin: true,
                        closeBtn: 0, //去掉关闭按钮
                        content: Hussar.ctxPath + '/user/chpwd',
                    });
                }
            });
        } else if (firstLogin == 'YES' && ischangePwd == 'NO') {
            //只需要设置密保
            layer.open({
                type: 2,
                title: '设置找回密码的问题和答案',
                area: ['450px', '250px'], //宽高
                fix: false, //不固定
                maxmin: true,
                closeBtn: 0, //去掉关闭按钮
                content: Hussar.ctxPath + '/user/setgbpwd',
            });
        } else if (firstLogin == 'NO' && ischangePwd == 'YES') {
            //只需要修改密码
            layer.open({
                type: 2,
                title: '修改密码',
                area: ['450px', '300px'], //宽高
                fix: false, //不固定
                maxmin: true,
                closeBtn: 0, //去掉关闭按钮
                content: Hussar.ctxPath + '/user/chpwd',
                success:function(){
                }
            });
        }

    });


    /*
     * 退出
     */
    $('#loginout').click(function(event){
        var operation = function () {
            window.location.href = Hussar.ctxPath+"/logout";
        }
        // Hussar.confirm("您确定要退出吗?", operation);
        layer.confirm('您确定要退出吗？',operation);
    });

    /*
     * 用户修改密码
     */
    $('#changePwd').click(function(event){
        layer.open({
            type: 2,
            title: '修改密码',
            area: ['450px', '300px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath+'/user/chpwd',
            success:function(){

            }
        });
    });

    /*
     * 个人信息
     */
    $('#selfInfo').click(function(event){
        layer.open({
            type: 2,
            title: '个人信息',
            area: ['480px', '580px'], //宽高
            fix: false, //不固定
            maxmin: true,
            resize: false,
            content: Hussar.ctxPath+'/user/pageUserInfo',
            success:function(){

            }
        });
    });

    /*
     * @todo 左侧导航菜单的显示和隐藏
     */
    $('.left_open').click(function(event) {
        $(this).toggleClass("close");
        $('.left-nav').toggleClass("sm");
        $('.size').toggleClass("hidden");
        $('.page-content').toggleClass("lg");

        $(this).children().children().toggleClass("layui-icon-shrink-right").toggleClass("layui-icon-spread-left");
        /**
         *收缩菜单悬浮事件
         */

        $(".sm>#side-nav>#nav>li").hover(function () {
            if($(".sm>#side-nav>#nav>li").length>0){
                $(this).siblings().removeClass("active");
                $(this).siblings().removeClass("open");
                $(this).addClass("active").addClass("open");
                if( $(this).children('.sub-menu').length) {
                    $(this).children('a').find('.nav_right').html('&#xe625;');
                    $(this).siblings().children('a').find('.nav_right').html('&#xe623;');
                    $(this).siblings().children('.sub-menu').stop(true, true).removeClass("subopen");
                    $(this).children('.sub-menu').stop(true, true).addClass("subopen");
                }
                var index = $(".sm #nav>li").index(this);
                var top = 41*index  + 51;
                $(this).children("ul.sub-menu").css("top",top+"px");
            }

        },function () {

        });

    });
    //点击遮罩背景，左侧菜单隐藏
    $('.page-content-bg').click(function(event) {
        $('.left-nav').animate({
            left: '-221px'
        }, 100);
        $('.page-content').animate({
            left: '0px'
        }, 100);
        $(this).hide();
    });

    /*
     * @todo 左侧菜单事件
     * 如果有子级就展开，没有就打开frame
     */
    $('.left-nav #nav li').click(function(event) {
        var th = $(this);
        $('#nav').find('.active').removeClass("active");

        /*$('#nav').find('.open').removeClass("open");
        $('#nav').find('.subopen').removeClass("subopen");
        $('#nav').find('.nav_right').html('&#xe623;');*/
        th.addClass("active");
        if(th.children('.sub-menu').length) {
            if(th.hasClass('open')) {
                th.removeClass('open');
                th.find('.nav_right').html('&#xe623;');
                th.children('.sub-menu').stop(true, true).removeClass("subopen");
                th.siblings().children('.sub-menu').removeClass("subopen");
            } else {
                if($('#nav').find('.open').length>0 && !(th.parents("li").hasClass("open"))){
                    $('#nav').find('.open').removeClass("open");
                    $('#nav').find('.subopen').removeClass("subopen");
                    $('#nav').find('.nav_right').html('&#xe623;');
                }
                th.addClass('open');
                th.children('a').find('.nav_right').html('&#xe625;');
                th.children('.sub-menu').stop(true, true).addClass("subopen");
                th.siblings().children('.sub-menu').stop(true, true).removeClass("subopen");
                th.siblings().find('.nav_right').html('&#xe623;');
                th.siblings().removeClass('open');
            }
        } else {
            if($('#nav').find('.open').length>0 && !(th.parents("li").hasClass("open"))){
                $('#nav').find('.open').removeClass("open");
                $('#nav').find('.subopen').removeClass("subopen");
                $('#nav').find('.nav_right').html('&#xe623;');
            }
            var url = th.children('a').attr('_href');
            var title = th.find('cite').html();
            var index = $('.left-nav #nav li').index(th);

            for(var i = 0; i < $('.weIframe').length; i++) {
                if($('.weIframe').eq(i).attr('tab-id') == index + 1) {
                    tab.tabChange(index + 1);
                    event.stopPropagation();
                    return;
                }
            }

            tab.tabAdd(title, url, index + 1);
            tab.tabChange(index + 1);
        }

        event.stopPropagation(); //不触发任何前辈元素上的事件处理函数
    });

    /*
     * @todo tab触发事件：增加、删除、切换
     */
    var tab = {
        tabAdd: function(title, url, id) {
            //判断当前id的元素是否存在于tab中
            var li = $("#WeTabTip li[lay-id=" + id + "]").length;
            //console.log(li);
            if(li > 0) {
                //tab已经存在，直接切换到指定Tab项
                //console.log(">0");
                element.tabChange('wenav_tab', id); //切换到：用户管理
            } else {
                //该id不存在，新增一个Tab项
                //console.log("<0");
                element.tabAdd('wenav_tab', {
                    title: title,
                    content: '<iframe tab-id="' + id + '" frameborder="0" src="' + url + '" scrolling="yes" class="weIframe"></iframe>',
                    id: id
                });
                //当前窗口内容
                setStorageMenu(title, url, id);

            }
            CustomRightClick(id); //绑定右键菜单
            FrameWH(); //计算框架高度

        },
        tabDelete: function(id) {
            element.tabDelete("wenav_tab", id); //删除
            removeStorageMenu(id);

        },
        tabChange: function(id) {
            //切换到指定Tab项
            element.tabChange('wenav_tab', id);
            //切换到指定tab页并刷新tab页
            var othis = $('.layui-tab-title').find('>li[lay-id="' + id + '"]'),
                index = othis.parent().children('li').index(othis),
                parents = othis.parents('.layui-tab').eq(0),
                item = parents.children('.layui-tab-content').children('.layui-tab-item'),
                src = item.eq(index).find('iframe').attr("src");
            item.eq(index).find('iframe').attr("src", src);
        },
        tabDeleteAll: function(ids) { //删除所有
            $.each(ids, function(i, item) {
                element.tabDelete("wenav_tab", item);
            })
            sessionStorage.removeItem(userId+"menu");
        }
    };

    /*
     * @todo 监听右键事件,绑定右键菜单
     * 先取消默认的右键事件，再绑定菜单，触发不同的点击事件
     */
    function CustomRightClick(id) {
        //取消右键
        $('.layui-tab-title li').on('contextmenu', function() {
            return false;
        })
        $('.layui-tab-title,.layui-tab-title li').on('click', function() {
            $('.rightMenu').hide();
        });
        //桌面点击右击
        $('.layui-tab-title li').on('contextmenu', function(e) {
            var index = $('.layui-tab-title li').index(this);
            if(index>0){
                var aid = $(this).attr("lay-id"); //获取右键时li的lay-id属性
                var popupmenu = $(".rightMenu");
                popupmenu.find("li").attr("data-id", aid);
                //console.log("popopmenuId:" + popupmenu.find("li").attr("data-id"));
                l = ($(document).width() - e.clientX) < popupmenu.width() ? (e.clientX - popupmenu.width()) : e.clientX;
                t = ($(document).height() - e.clientY) < popupmenu.height() ? (e.clientY - popupmenu.height()) : e.clientY;
                popupmenu.css({
                    left: l,
                    top: t
                }).show();
                //alert("右键菜单")
            }else {
                $('.rightMenu').hide();
            }

            return false;
        });
    }


    $("#rightMenu li").click(function() {
        var type = $(this).attr("data-type");
        var layId = $(this).attr("data-id")
        if(type == "current") {
            //console.log("close this:" + layId);
            tab.tabDelete(layId);
        } else if(type == "all") {
            //console.log("closeAll");
            var tabtitle = $(".layui-tab-title li");
            var ids = new Array();
            $.each(tabtitle, function(i) {
                ids[i] = $(this).attr("lay-id");
            })
            tab.tabDeleteAll(ids);
        } else if(type == "fresh") {
            //console.log("fresh:" + layId);
            tab.tabChange($(this).attr("data-id"));
            var othis = $('.layui-tab-title').find('>li[lay-id="' + layId + '"]'),
                index = othis.parent().children('li').index(othis),
                parents = othis.parents('.layui-tab').eq(0),
                item = parents.children('.layui-tab-content').children('.layui-tab-item'),
                src = item.eq(index).find('iframe').attr("src");
            item.eq(index).find('iframe').attr("src", src);
        } else if(type == "other") {
            var thisId = layId;
            $('.layui-tab-title').find('li').each(function(i, o) {
                var layId = $(o).attr('lay-id');
                if(layId != thisId && layId != 0) {
                    tab.tabDelete(layId);
                }
            });
        }
        $('.rightMenu').hide();
    });

    /*
     * @todo 重新计算iframe高度
     */
    function FrameWH() {
        var h = $(window).height() - 84;
        $("iframe").css("height", h + "px");
    }
    $(window).resize(function() {
        FrameWH();
    });

    /*
     * @todo 弹出层，弹窗方法
     * layui.use 加载layui.define 定义的模块，当外部 js 或 onclick调用 use 内部函数时，需要在 use 中定义 window 函数供外部引用
     * http://blog.csdn.net/xcmonline/article/details/75647144
     */
    /*
        参数解释：
        title   标题
        url     请求的url
        id      需要操作的数据id
        w       弹出层宽度（缺省调默认值）
        h       弹出层高度（缺省调默认值）
    */
    /*window.HussarShow = function(title, url, w, h) {
        if(title == null || title == '') {
            title = false;
        };
        if(url == null || url == '') {
            url = "404.html";
        };
        if(w == null || w == '') {
            w = ($(window).width() * 0.9);
        };
        if(h == null || h == '') {
            h = ($(window).height() - 50);
        };
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: url
        });
    }*/
    /*弹出层+传递ID参数*/
    window.WeAdminEdit = function(title, url, id, w, h) {
        if(title == null || title == '') {
            title = false;
        };
        if(url == null || url == '') {
            url = "404.html";
        };
        if(w == null || w == '') {
            w = ($(window).width() * 0.9);
        };
        if(h == null || h == '') {
            h = ($(window).height() - 50);
        };
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: true,
            maxmin: true,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: url,
            success: function(layero, index) {
                //向iframe页的id=house的元素传值  // 参考 https://yq.aliyun.com/ziliao/133150
                var body = layer.getChildFrame('body', index);
                body.contents().find("#dataId").val(id);
                console.log(id);
            },
            error: function(layero, index) {
                alert("aaa");
            }
        });
    }

    /**
     *@todo tab监听：点击tab项对应的关闭按钮事件
     */
    $('.layui-tab-close').click(function(event) {
        $('.layui-tab-title li').eq(0).find('i').remove();
    });
    /**
     *@todo tab切换监听
     * tab切换监听不能写字初始化加载$(function())方法内，否则不执行
     */
    element.on('tab(wenav_tab)', function(data) {
        //console.log(this); //当前Tab标题所在的原始DOM元素
        setStorageCurMenu();
    });
    /*
     * @todo 监听layui Tab项的关闭按钮，改变本地存储
     */
    element.on('tabDelete(wenav_tab)', function(data) {
        var layId = $(this).parent('li').attr('lay-id');
        //console.log(layId);
        removeStorageMenu(layId);
    });
    /**
     *@todo 本地存储 localStorage
     * 为了保持统一，将sessionStorage更换为存储周期更长的localStorage
     */
    //本地存储记录所有打开的窗口
    function setStorageMenu(title, url, id) {
        var menu = JSON.parse(sessionStorage.getItem(userId+"menu"));
        if(menu) {
            var deep = false;
            for(var i = 0; i < menu.length; i++) {
                if(menu[i].id == id) {
                    deep = true;
                    menu[i].title = title;
                    menu[i].url = url;
                    menu[i].id = id;
                }
            }
            if(!deep) {
                menu.push({
                    title: title,
                    url: url,
                    id: id
                })
            }
        } else {
            var menu = [{
                title: title,
                url: url,
                id: id
            }]
        }
        sessionStorage.setItem(userId+"menu", JSON.stringify(menu));
    }
    //本地存储记录当前打开窗口
    function setStorageCurMenu() {
        var curMenu = sessionStorage.getItem(userId+'curMenu');
        var text = $('.layui-tab-title').find('.layui-this').text();
        text = text.split('ဆ')[0];
        var url = $('.layui-tab-content').find('.layui-show').find('.weIframe').attr('src');
        var id = $('.layui-tab-title').find('.layui-this').attr('lay-id');
        //console.log(text);
        curMenu = {
            title: text,
            url: url,
            id: id
        }
        sessionStorage.setItem(userId+'curMenu', JSON.stringify(curMenu));
    }
    //本地存储中移除删除的元素
    function removeStorageMenu(id) {
        var menu = JSON.parse(sessionStorage.getItem(userId+"menu"));
        //var curMenu = JSON.parse(localStorage.getItem(userId+'curMenu'));
        if(menu) {
            var deep = false;
            for(var i = 0; i < menu.length; i++) {
                if(menu[i].id == id) {
                    deep = true;
                    menu.splice(i, 1);
                }
            }
        } else {
            return false;
        }
        sessionStorage.setItem(userId+"menu", JSON.stringify(menu));
    }

    /**
     *@todo Frame内部的按钮点击打开其他frame的tab
     */

    exports('admin', {});


});