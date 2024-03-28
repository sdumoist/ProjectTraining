layui.use(['jquery','layer','Hussar'], function(){
    var $ = layui.jquery,
        layer = layui.layer,
        Hussar = layui.Hussar,
        $ax = layui.HussarAjax;

    $(function() {

        var ajax = new $ax(Hussar.ctxPath + "/docConfigure/getConfigureData", function(data) {
            if(data.length > 0){
                for( var i = 0 ; i < data.length ; i ++){
                    if(data[i].configKey == 'company_info'){
                        $("#s_company_info").text(data[i].configValue)
                        //$("#s_company_info").text("©"+year+" 金现代信息产业股份有限公司  电话：0531-88872666")
                    }
                }
            }else{
                layer.msg("加载数据失败", {anim:6,icon: 0});
            }

        }, function(data) {

        });
        ajax.start();

    })
});