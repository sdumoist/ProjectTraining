layui.use('Hussar', function () {
    var Hussar = layui.Hussar;
    layui.config({
        base: Hussar.ctxPath + '/static/resources/js/front/'
        , version: '101101'
    }).use('cropbox');
    layui.use(['jquery', 'cropbox'], function () {
        var $ = layui.$,
            cropbox = layui.cropbox,
            $ax = layui.HussarAjax,
            Hussar = layui.Hussar;

        var base64Img = '';
        var currentSrc = $('#curImgSrc').val() || Hussar.ctxPath+'/static/resources/img/front/index/photo.png';
        $('.resultImgWrap').find('img').attr('src', currentSrc);

        var options = {
            imageBox: '.imgWrap',
            thumbBox: '.imgBox',
            spinner: '.spinner',
            resultImgWrap: '.resultImgWrap',
            imgSrc: base64Img
        };
        var cropper = new cropbox(options);
        $('#zoomIn').on('click',function () {
            if(cropper.options.imgSrc !== ''){
                cropper.zoomIn();
                base64Img = cropper.getDataURL();
                $('.resultImgWrap').find('img').attr('src', base64Img);
            }
        });
        $('#zoomOut').on('click',function () {
            if(cropper.options.imgSrc !== ''){
                cropper.zoomOut();
                base64Img = cropper.getDataURL();
                $('.resultImgWrap').find('img').attr('src', base64Img);
            }
        });

        $('#upload').on('click',function () {
           $('#file').click();
        });
        $('#file').on('change', function(e){
            var reader = new FileReader();
            reader.onload = function(e) {
                options.imgSrc = e.target.result;
                cropper = new cropbox(options);
                //
                setTimeout(function () {
                    base64Img = cropper.getDataURL();
                    $('.resultImgWrap').find('img').attr('src', base64Img);
                },500)
            };
            reader.readAsDataURL(this.files[0]);
            this.files = null;
        });
        $('.imgWrap').on('mouseup',function () {
            if(cropper.options.imgSrc !== ''){
                base64Img = cropper.getDataURL();
                $('.resultImgWrap').find('img').attr('src', base64Img);
            }
        });
        $('.imgWrap').on('mousewheel DOMMouseScroll',function (e) {
            e.preventDefault();
            if(cropper.options.imgSrc !== ''){
                base64Img = cropper.getDataURL();
                $('.resultImgWrap').find('img').attr('src', base64Img);
            }
        });

        $('#confirm').on('click',function () {
            var ajax = new $ax(Hussar.ctxPath + "/changeHeadIcon", function (result) {
                Hussar.success('更改成功');
                if(base64Img !== ''){
                    parent.$('.changeIcon').find('img:first-child').attr('src',base64Img);
                }
                setTimeout(function () {
                    var mylay = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(mylay);
                },1000);
            }, function (data) {
                Hussar.error('更改失败');
            });
            ajax.set("base64Img", base64Img);
            ajax.start();
        });

        $(function () {
        })
    });
});