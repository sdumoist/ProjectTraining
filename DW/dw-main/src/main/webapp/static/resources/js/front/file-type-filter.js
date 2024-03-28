/**
 * Created by Lenovo on 2018/1/17.
 */

layui.use(['jquery','form'], function () {

   var  form =  layui.form;

    $(function() {
        var fileType = $("#fileTypeValue").val(); //文档类型
        if (fileType==null||fileType==""||fileType==undefined){
            fileType=0;
        }
        $("#select").val(fileType);
        if(fileType=="7"){
            $("#all").hide();
            $("#word").show();
        }
        if(fileType=='8'){
            $(".search-box").hide()
        }
        $("input[type=radio][name='fileType']").eq(0).attr("checked","checked");

        $('input[type=radio][name=fileType]').change(function() {
            var fileType =    $("input[type=radio][name='fileType']:checked").val();
            $("#fileTypeValue").val(fileType);
        });    form.render();
    });
});
