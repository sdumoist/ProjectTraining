
/**
 * @Description: 定义登陆页面脚本文件
 * @Author: liangdong
 * @Date: 2018/3/14.
 */

layui.use(['jquery', 'layer', 'Hussar', 'HussarAjax', 'form', 'HussarSecurity', 'HussarEncrypt'], function () {
    var Hussar = layui.Hussar;
    var $ = layui.jquery;
    var layer = layui.layer;
    var $ax = layui.HussarAjax;
    var form = layui.form;
    var Security = new layui.HussarSecurity(),
        HussarEncrypt = new layui.HussarEncrypt();

    var Login = {
        layerIndex: -1
    };

    /* 动画效果 */
    var items = document.getElementsByClassName("animation-img");// 移动div的名字
    document.addEventListener('mousemove', function (evt) {
        var x = evt.clientX;
        var y = evt.clientY;

        var winWidth = window.innerWidth;
        var winHeight = window.innerHeight;
        var halfWidth = winWidth / 2;
        var halfHeight = winHeight / 2;
        var rx = x - halfWidth;
        var ry = halfHeight - y;
        var length = items.length;
        var max = 30;
        for (var i = 0; i < length; i++) {
            var dx = (items[i].getBoundingClientRect().width / max) * (rx / halfWidth);
            var dy = (items[i].getBoundingClientRect().height / max) * (ry / -halfHeight);
            items[i].style['transform'] = items[i].style['-webkit-transform'] = 'translate(' + dx + 'px,' + dy + 'px)';
        }
    }, false);

    Login.initButtonEvent = function () {

        $("#kaptcha").on('click', function () {
            $("#kaptcha").attr('src', Hussar.ctxPath + '/kaptcha?' + Math.floor(Math.random() * 100)).fadeIn();
        });

        $("#login").click(function () {
            var kaptchaOnOff = $("#kaptchaOnOff").val();
            var totpOnOff = $("#totpOnOff").val();
            var username = $("input[name='username']").val().trim();
            var cipher = $("#password").val().trim();
            if (username === "" && cipher != "") {
                $(".error1 .error").html("用户名不能为空");
                $(".ani-label").addClass('ani');
                $(".error2 .error").html("");
                $("input[name='username']").addClass("errorTip");
                return false
            }
            if (cipher === "" && username != "") {
                $(".error2 .error").html("密码不能为空");
                $(".error1 .error").html("");
                $(".ani-label").removeClass('ani');
                $("#password").addClass("errorTip");
                return false
            }
            if (cipher === "" && username === "") {
                $(".error1 .error").html("用户名不能为空");
                $(".error2 .error").html("密码不能为空");
                $(".ani-label").addClass('ani');
                $("#password").addClass("errorTip");
                $("input[name='username']").addClass("errorTip");
                return false
            }
            if (kaptchaOnOff === "true") {
                var kaptcha = $("input[name='kaptcha']").val().trim();
                if (kaptcha === "") {
                    $(".error").html("验证码不能为空");
                    $("input[name='kaptcha']").addClass("errorTip");
                    return false
                }
            }
            if (totpOnOff === "true") {
                var totp = $("input[name='totp']").val().trim();
                if (totp === "") {
                    $(".error").html("动态密码不能为空");
                    $("input[name='totp']").addClass("errorTip");
                    return false
                }
            }
            $(".login-form input").removeClass("errorTip");
            Login.login();
        });

        $(".login-form input").blur(function () {
            var loginVal = $(this).val().trim();

            if (loginVal != "") {
                $(this).removeClass("errorTip");
            } else {
                $(this).addClass("errorTip");
            }
        });
    };

    Login.login = function () {
        //判断是否是分享链接
        var localHref = window.location.href;
        if(localHref.indexOf("/s/")!= -1){
            localStorage.setItem("shareHref",localHref);
        }
        // 使用loading，防止多次提交
        var index = layer.load(2);
        var cipher = encodeURIComponent($("#password").val());
        var params = Security.encode(cipher);// 调用加密方法进行加密
        $("#password").val("");
        $("#login").attr("disabled",true);
        var data = $.param({'encrypted': params}) + '&' + $("#tokenForm").serialize();
        var ajax = new $ax(Hussar.ctxPath + "/login", function (data) {
            if (data.code == 200) {
                var shareHref = localStorage.getItem("shareHref");
                if (shareHref != undefined && shareHref != null && shareHref != "") {
                    localStorage.removeItem("shareHref");
                    window.location.href = shareHref;
                } else {
                    if (window.location.origin == undefined) {
                        var location = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
                        window.location.href = location + "/" + Hussar.ctxPath;
                    }else {
                        window.location.href = window.location.origin + Hussar.ctxPath;
                    }
                }

            }
            layer.close(index);	// 关闭loading
        }, function (data) {
            $("#login").attr("disabled",false);
            $(".error1 .error").html("");
            $(".error2 .error").html(data.result);
            $("#password").addClass("errorTip");
            $("input[name='username']").addClass("errorTip");
            $("#kaptcha").attr("src", Hussar.ctxPath + "/kaptcha");
            layer.close(index);	// 关闭loading
        });
        ajax.setEncryptEnable(true);
        ajax.setData(data);
        ajax.start();

    };

    Login.checkLicense = function () {
        var ajax = new $ax(Hussar.ctxPath + "/license/check", function (data) {
            if (data.code == 500) {
                var json = JSON.parse(data.message);
                var startDate = json.startDate;
                var endDate = json.endDate;
                if (startDate && endDate) {
                    $(".login-box").append("<div id='div_debug'>&copy;轻骑兵V8试用版<span><font color='#26b7b0'>(" + startDate + "~" + endDate + ")</font></span>电话：0531-88872666<div>");
                }
            }
        });
        ajax.setEncryptEnable(true);
        ajax.start();
    };

    $(function () {
        if (tips != '') {
            $(".error").html(tips);
        }
        Login.initButtonEvent(); // 初始化按钮事件
        // Enter触发登录按钮事件
        $(document).keyup(function (event) {
            if (event.keyCode == 13) {
                $("#login").click();
            }
        });

        Login.checkLicense();// 校验授权

        /**
         * 密码是否可见控制
         */
        $(".see").click(function () {
            var see = $(".see");
            var ifsee = see[0].ifsee;
            var demoInput = document.getElementById("password");

            if(ifsee === "see"){
                demoInput.type = "password";
                see[0].ifsee = "unsee";
                see.css("background-image","url("+Hussar.ctxPath +"/static/resources/js/img/unsee.png)")
            }else {
                demoInput.type = "text";
                see[0].ifsee = "see";
                see.css("background-image","url("+Hussar.ctxPath +"/static/resources/js/img/see.png)")
            }
        });
    });

});
