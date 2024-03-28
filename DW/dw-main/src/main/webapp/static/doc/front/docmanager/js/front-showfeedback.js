/**
 * Created by ZhongGuangrui on 2018/12/3.
 */
// 意见反馈 点击事件
window.show_feedback = function () {

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        var showFeedback = layer.open({
            type: 2,
            title: ['用户反馈','background-color: #ffffff;padding-left:45%;font-size:16px;font-weight:400'],
            area: ['600px', '600px'], //宽高
            fix: false, //不固定
            closeBtn: 2,
            //scrollbar: false,
            //maxmin: true,
            content: Hussar.ctxPath+'/feedback',
            end:function(){

            }
        });
    });
}