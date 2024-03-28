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
    $("#saveBtn").on('click',function(){
        var word = $("#word").val().trim();//敏感词名称
        var remark = $("#remark").val().trim();
        if(word.length == 0){
            layer.msg("敏感词不能为空", {anim:6,icon: 0});
            return;
        }
        if(!new RegExp(/^[A-Za-z\u4e00-\u9fa5]+$/).test(word)){
            layer.msg("敏感词只能输入中文或字母", {anim:6,icon: 0});
            return
        }
        if(word.length > 8){
            layer.msg("敏感词不能超过8个字", {anim:6,icon: 0});
            return;
        }
        if(word == ""){
            layer.msg("敏感词不能为空", {anim:6,icon: 0});
            return;
        }
        if(remark.length > 100){
            layer.msg("备注不能超过100个字", {anim:6,icon: 0});
            return;
        }
        var url;//请求地址
        var successMsg,errorMsg;//成功失败提示
        url = "/SensitiveWords/add";
        successMsg = "新增成功";
        errorMsg = "新增失败";
        $.ajax({
            type:"post",
            url:url,
            data:{
                word: word,
                remark: remark
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                if(data == "0"){
                    layer.alert('该敏感词已存在', {
                        icon :  0,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    });
                }else if(data == "1"){
                    layer.alert(successMsg, {
                        icon :  1,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        end: function () {
                            parent.tableIns.reload();
                            layui.data('checked',null);
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                        }
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
