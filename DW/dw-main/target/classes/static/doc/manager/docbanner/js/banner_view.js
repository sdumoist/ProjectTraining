var topicId =$("#id").val();
var topicPic;
layui.extend({
    admin: '/static/resources/weadmin/static/js/admin'
});
layui.use(['form', 'jquery','util','admin','layer','table','Hussar','jstree','HussarAjax','element','laydate','upload'], function() {
    var form = layui.form,
        $ = layui.jquery,
        table = layui.table,
        layer = layui.layer,
        Hussar = layui.Hussar,
        upload = layui.upload;
    function openWin(url) {
        var a = document.createElement("a"); //创建a标签
        a.setAttribute("href", url);
        a.setAttribute("target", "_blank");
        document.body.appendChild(a);
        a.click(); //执行当前对象
    }
    //.存储当前页数据集
    var pageData = [];
    //普通图片上传
    var uploadInst = upload.render({
        elem: '#choosePic'
        ,url: Hussar.ctxPath+'/attachmentDemo/uploadfilewithdrag'
        ,before: function(obj){
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                $('#topicPic').attr('src', result);
                topicPic = result;
            });
        }
        ,done: function(res, index, upload){
            //如果上传失败
            if(res.code > 0){
                return layer.msg('上传失败');
            }
        }
        ,accept: 'images'//允许上传的文件类型
    });
    //.存储已选择数据集，用普通变量存储也行
    layui.data("childChecked",null);

    //初始化表格
    var tableIns = table.render({
        elem: '#docList' //指定原始表格元素选择器（推荐id选择器）
        ,height: 300 //容器高度
        ,width:900
        ,url: '/topicDoc/getDocListByIds' //数据接口
        ,done:function(res) {
            $("[data-field='id']").hide();
            //.假设你的表格指定的 id="docList"，找到框架渲染的表格
            var tbl = $('#docList').next('.layui-table-view');
            //.记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
            pageData = res.data;
            var len = pageData.length;
            //.遍历当前页数据，对比已选中项中的 id
            for (var i = 0; i < len; i++) {
                if (layui.data('childChecked', pageData[i]['id'])) {
                    //.选中它，目前版本没有任何与数据或表格 id 相关的标识，不太好搞，土办法选择它吧
                    tbl.find('table>tbody>tr').eq(i).find('td').eq(0).find('input[type=checkbox]').prop('checked', true);
                }
            }
            form.render('checkbox');
        }
        ,where: {
            topicId:topicId
        }
        ,page: false //开启分页
        ,cols: [[
            {field:'id'},
            {field:'',type:'checkbox',width:'9%',align:"center"},
            {field:'title',title:'文件名称',width:'50%',align:"left",event: 'openView', style: 'cursor: pointer;color:#00a4ff'},
            {field:'author',title:'作者',width:'15%',align:"center"},
            {field:'createTime',title:'上传时间',width:'25%',align:"center",templet:"#timeTpl"},
        ]] //设置表头
    });
    //监听工具条
    table.on('tool(docList)', function (obj) {
        var data = obj.data;
        if(obj.event == 'openView'){
            openWin("/preview/toShowPDF?id=" + data.id);
        }
    });
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    //.渲染完成回调
    $('.layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
});

init();
function init() {

    var show =$("#show").val();
    if(show== 0){
        $("#topicShow").attr("checked",false);
    }else{
        $("#topicShow").attr("checked",true);
    };
}
