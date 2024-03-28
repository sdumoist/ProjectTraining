layui.use(['form', 'jquery','util','layer','table','Hussar','jstree','HussarAjax','element','laydate','upload'], function() {
    var form = layui.form,
        $ = layui.jquery,
        layer = layui.layer;
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    /*新增/编辑敏感词*/
    $("#confirmBtn").on('click',function(){
        var describe = $("#describe").val().trim();
        var examineId = $("#examineId").val();
        var url;//请求地址
        var successMsg,errorMsg;//成功失败提示
        if(describe.length > 100){
            layer.msg("审核意见不能超过100个字", {anim:6,icon: 0});
            return;
        }
        url = "/examineFile/update";
        successMsg = "审核成功";
        errorMsg = "审核失败";
        $.ajax({
            type:"post",
            url:url,
            data:{
                examineId: examineId,
                rState: "删除",
                describe: describe
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                if(data == "0"){
                    layer.alert('审核状态未改变', {
                        icon :  0,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    });
                }else if(data >= "1"){
                    layer.alert(successMsg, {
                        icon :  1,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    },function(){
                        parent.tableIns.reload();
                        layui.data('checked',null);
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                    });
                }else{
                    layer.alert(errorMsg, {
                        icon :  2,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    });
                }
            }
        })
    });
});
