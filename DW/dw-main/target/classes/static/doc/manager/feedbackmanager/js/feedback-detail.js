/**
 * Created by ZhongGuangrui on 2018/12/6.
 */
layui.use('laytpl', function(){
    var laytpl = layui.laytpl;

    $(".feedback-item").eq(0).append($("#feedbackType").text() == '1'? '使用问题':'功能改进');
    var data = { //数据
        "list":document.getElementById('view').innerText
    };
    data = JSON.stringify(data).replace(/\\/g,"").replace('"[','[').replace(']"',']');
    data = JSON.parse(data);
    data.list = data.list.slice(0,5);
    var getTpl = $("#imgContainer").html()
        ,view = document.getElementById('viewImg');
    laytpl(getTpl).render(data, function(html){
        if (data.list.length != 0){
            view.innerHTML = html;
            $("#imgArea").css("display","block");
        }
    });
});