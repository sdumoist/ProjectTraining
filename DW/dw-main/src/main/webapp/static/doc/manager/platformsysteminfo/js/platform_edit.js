var util;//工具
var layerView;
var systemId = null;//系统ID
layui.use(['form', 'jquery','util','layer','table','Hussar','HussarAjax','element','laydate'],function (){
    var form = layui.form,
        $ = layui.jquery,
        table = layui.table,
        layer = layui.layer,
        Hussar = layui.Hussar,
        $ax=layui.HussarAjax,
        element = layui.element,
        laydate = layui.laydate,
        util = layui.util;

    layui.data("childChecked",null);
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    /*新增/编辑专题*/
    $("#saveBtn").on('click',function(){
        var systemId = $("#systemId").val().trim();//系统id
        var systemName = $("#systemName").val().trim();//系统名称
        var systemKey = $("#systemKey").val().trim();//系统key密码
        var validFlag = $("#validFlag").val().trim();//是否有效
        var createTime = $("#createTime").val().trim();//创建时间
        var updateTime = $("#updateTime").val().trim();//更新时间
        var createUserId = $("#createUserId").val().trim();//当前操作者id
        
        if(systemName == ""||systemName == undefined || systemName == null){
            layer.msg("系统名称不能为空", {anim:6,icon: 0});
            return;
        }

        var url = "/platformSystemInfo/updatePlatformSystemInfo";
        var ajax = new $ax(Hussar.ctxPath + url, function (data){
            if(data.result == "1"){
                layer.alert("修改成功", {
                    icon :  1,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    end: function () {
                        parent.tableIns.reload({
                            where: {
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            }
                        });
                        layui.data('checked',null);
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                    }
                },function(){
                    parent.tableIns.reload({
                        where: {
                            //防止IE浏览器第一次请求后从缓存读取数据
                            timestamp: (new Date()).valueOf()
                        }
                    });
                    layui.data('checked',null);
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index);
                });
            }else{
                layer.alert("修改失败", {
                    icon :  2,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
            }
        }, function (data){

        });
        ajax.set("systemId",systemId);
        ajax.set("systemName",systemName);
        ajax.set("systemKey",systemKey);
        ajax.set("validFlag",validFlag);
        ajax.set("createTime",createTime);
        ajax.set("updateTime",updateTime);
        ajax.set("createUserId",createUserId);
        ajax.start();
    });
});