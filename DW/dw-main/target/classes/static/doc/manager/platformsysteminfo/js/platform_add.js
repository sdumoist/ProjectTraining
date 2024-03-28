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
        var systemName = $("#systemName").val().trim();//系统名称
        
        if(systemName == ""||systemName == undefined || systemName == null){
            layer.msg("系统名称不能为空", {anim:6,icon: 0});
            return;
        }
        
        var url = "/platformSystemInfo/addPlatformSystemInfo";
        var ajax = new $ax(Hussar.ctxPath + url, function (data){
            if(data.result == "1"){
                layer.alert("新增成功", {
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
                layer.alert("新增失败", {
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
        ajax.set("systemName",systemName);
        ajax.start();
    });
});