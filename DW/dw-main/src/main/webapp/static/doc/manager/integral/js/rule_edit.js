layui.use(['form', 'jquery','util','layer','table','Hussar','jstree','HussarAjax','element','laydate','upload'], function() {
    var form = layui.form,
        $ = layui.jquery,
        layer = layui.layer;
    var Hussar = layui.Hussar,
        $ax = layui.HussarAjax;
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    /*新增/编辑敏感词*/
    $("#saveBtn").on('click',function(){
        var ruleId = $("#ruleId").val();
        var ruleName = $("#ruleName").val().trim();
        var ruleCode = $("#ruleCode").val().trim();
        var ruleIntegral = $("#ruleIntegral").val().trim();
        var ruleDes = $("#ruleDes").val().trim();
        var remark = $("#remark").val().trim();
        var valid = $("#valid").val().trim();
        var maxTimes = $("#maxTimes").val().trim();
        if(!checkInput(ruleName,ruleCode,ruleIntegral,ruleDes,remark,maxTimes)){
            return;
        }
        var url;//请求地址
        var successMsg,errorMsg;//成功失败提示
        url = "/integralRule/update";
        successMsg = "修改成功";
        errorMsg = "修改失败";
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+url,
            data:{
                ruleId:ruleId,
                ruleName:ruleName,
                ruleCode:ruleCode,
                ruleIntegral:ruleIntegral,
                ruleDes:ruleDes,
                remark: remark,
                valid:valid,
                maxTimes:maxTimes
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                if(data == "0"){
                    layer.msg("存在重名规则", {anim: 6, icon: 0});
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
        })*/
        var ajax = new $ax(Hussar.ctxPath + url, function(data) {
            if(data == "0"){
                layer.msg("存在重名规则", {anim: 6, icon: 0});
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
        }, function(data) {

        });
        ajax.set("ruleId",ruleId);
        ajax.set("ruleName",ruleName);
        ajax.set("ruleCode",ruleCode);
        ajax.set("ruleIntegral",ruleIntegral);
        ajax.set("ruleDes",ruleDes);
        ajax.set("remark",remark);
        ajax.set("valid",valid);
        ajax.set("maxTimes",maxTimes);
        ajax.start();
    });

    $("#maxTimes").on('click',function () {
        $("#maxTimes").attr("type","number");
    })
});
function checkInput(ruleName,ruleCode,ruleIntegral,ruleDes,remark,maxTimes) {
    if(ruleName == ""){
        layer.msg("规则名称不能为空", {anim:6,icon: 0});
        return false;
    }
    if(!new RegExp(/^[A-Za-z0-9\u4e00-\u9fa5]+$/).test(ruleName)){
        layer.msg("规则名称不能有特殊字符", {anim:6,icon: 0});
        return false;
    }
    if(ruleName.length > 20){
        layer.msg("规则名称不能超过20个字", {anim:6,icon: 0});
        return;
    }
    if(ruleCode == ""){
        layer.msg("规则编码不能为空", {anim:6,icon: 0});
        return false;
    }
    if(!new RegExp(/^[A-Za-z0-9_]+$/).test(ruleCode)){
        layer.msg("规则编码只能输入英文或数字", {anim:6,icon: 0});
        return false;
    }
    if(ruleCode.length > 20){
        layer.msg("规则编码不能超过20个字", {anim:6,icon: 0});
        return false;
    }
    if(ruleIntegral == ""){
        layer.msg("规则积分不能为空", {anim:6,icon: 0});
        return false;
    }
    if(!new RegExp(/^(-|\+)?\d+$/).test(ruleIntegral)){
        layer.msg("规则积分只能输入整数", {anim:6,icon: 0});
        return false;
    }
    if(maxTimes != '' && maxTimes != '无上限' && !new RegExp(/^(\+)?\d+$/).test(maxTimes) && maxTimes != ''){
        layer.msg("每日上限次数只能输入非负整数或不输入", {anim:6,icon: 0});
        return false;
    }
    if(ruleDes.length > 100){
        layer.msg("规则描述不能超过100个字", {anim:6,icon: 0});
        return false;
    }
    if(remark.length > 100){
        layer.msg("备注不能超过100个字", {anim:6,icon: 0});
        return false;
    }
    return true;
}