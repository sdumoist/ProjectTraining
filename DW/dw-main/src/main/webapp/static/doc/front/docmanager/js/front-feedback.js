/**
 * Created by ZhongGuangrui on 2018/12/3.
 */
/**
 * 存放多张附件图片地址的数组
 * @type {Array}
 */
var attachmentUrls = [];
/**
 * 附件图索引（数量-1）
 * @type {number}
 */
var picNum = 0;

$(function () {
    // 反馈类型 单选框切换
    $("[name='feedbackType']").click(function () {
        $("[name='feedbackType']").parent().removeClass("radio-active");
        $(this).parent().addClass("radio-active");
    });


    //// 设置按钮禁用（未实现）
    //$("#addPic").on("click", function () {
    //    alert(picNum);
    //    if (picNum >= 5){
    //        $(this).attr("id","");
    //        alert($(this).attr("id"));
    //    }
    //})
});
// 撤销已上传的图片
function delPic(element){
    // 获取图片地址在数组中的位置
    var index = attachmentUrls.indexOf($(element).prev().attr("id"));
    // 删除该值
    attachmentUrls.splice(index,1);
    // 删除DOM元素
    $("#img-div")[0].removeChild(element.parentNode);
    // 附件索引前移
    picNum --;
}
// 单击图片时初始化viewer
function show_attachment(){
    var viewer = new Viewer(document.getElementById('img-div'),{
        title:false,    // 不显示标题
        toolbar: {      // 工具栏
            zoomIn: 4,
            zoomOut: 4,
            oneToOne: 4,
            prev: 4,
            play: {
                show: 4,
                size: 'large'
            },
            next: 4
        },
        hide: function () { // 隐藏时销毁对象
            viewer.destroy();
        }
    });
}
layui.use(['form','Hussar','HussarAjax','upload','layer'], function () {
    var form = layui.form
        ,Hussar = layui.Hussar
        ,upload = layui.upload
        ,layer = layui.layer
        ,$ax = layui.HussarAjax;

    // 自定义验证规则
    form.verify({
        // 手机号、QQ、邮箱
        multiple: function(value, item){ //value：表单的值、item：表单的DOM对象
            if(!/^1\d{10}$|^\w+@\w+(\.\w+){1,2}$|^\d{6,15}$/.test(value)){
                return '请输入符合规范的联系方式';
            }
        },
        // 反馈描述的字数限制
        numLimit: function (value, item) {
            if(value.length <= 0){
                return '问题描述不能为空';
            }
            if (value.length > 200){
                return '您输入的描述字数已超过限制（200字以下）'
            }
        },
        // 定制非空验证
        required: function (value, item) {
            var name = item.name;
            if (value == ''){
                switch (name){
                    case 'feedbackDescribe':
                        return '问题描述不能为空';
                    case 'contackWay':
                        return '联系方式不能为空';
                    default:
                        return '必填项不能为空';
                }
            }
        }
    });

    // 通过验证 异步提交表单
    form.on("submit(feedback_submit)",function(){
        $(".feedback_submit").prop("disabled",true);
        var ajax = new $ax(Hussar.ctxPath + "/feedback/add_feedback", function (data) {
            //当你在iframe页面关闭自身时
            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index); //再执行关闭
            parent.layer.msg(data + "",{icon:1,offset: 'auto'});
        }, function (data) {
            //当你在iframe页面关闭自身时
            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index); //再执行关闭
            parent.layer.msg("反馈失败" + data.responseJSON.message + "!",{icon:2,offset:'auto'});
            $(".feedback_submit").prop("disabled",false);
        });
        ajax.set({
            feedbackType:$("[name='feedbackType']:checked").val(),
            feedbackDescribe:$("[name='feedbackDescribe']").val(),
            contackWay:$("[name='contackWay']").val(),
            attachmentUrls:attachmentUrls.toString()
        });
        ajax.start();
    });

    // 上传图片
    var upload = upload.render({
        elem: '#addPic'
        , url: Hussar.ctxPath + '/feedback/upload_attachment'
        , size: 2048  // 最大允许上传2M的文件
        , before: function (obj) {
            if (picNum < 5) {   // 限制图片上传数量为5张
                // 为页面添加DOM元素（图片显示）
                $("#addPic").before(
                    "<div class='layui-inline'>" +
                        "<img class='layui-upload-img attach_img' onclick='show_attachment(this)'>" +
                        "<input type='button' class='btn-delPic' onclick='delPic(this)'>" +
                    "</div>");
                //预读本地文件示例，不支持ie8
                obj.preview(function (index, file, result) {
                    $('.attach_img').eq(picNum).attr('src', result);
                    picNum++;
                });
            } else {
                Hussar.error("最多上传5张图片");
                return;
            }
        }
        , done: function (res, index, upload) {
            //如果上传失败
            if (res.fName == "" || res.fName == undefined) {
                return layer.msg('上传失败');
            } else {    // 上传成功
                // 向数组中添加新上传的图片地址
                attachmentUrls.push(res.fName);
                // 记录该图片地址，方便在数组中删除
                $('.attach_img').eq(picNum - 1).attr('id', res.fName);
            }
        }
        , accept: 'images'//允许上传的文件类型
    });

});
