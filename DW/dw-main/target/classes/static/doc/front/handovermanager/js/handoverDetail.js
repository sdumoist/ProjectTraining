layui.use('table', function(){
    var table = layui.table;
    var id = $("#id").val();
    table.render({
        elem: '#detailTable'
        ,url:'/handover/getAttachmentList/'
        ,where: {
            //防止IE浏览器第一次请求后从缓存读取数据
            timestamp: (new Date()).valueOf(),
            id:id
        }    , request: {
            pageName: 'page', //页码的参数名称，默认：page
            limitName: 'limit' //每页数据量的参数名，默认：limit
        }
        ,page: true
        ,limit:5
        ,cols: [[
            // {field: 'fileId', title: 'id', type: 'checkbox', width: 40, align: "center"},
            {type: 'numbers', title: '序号', align: 'center', width: 50},
            {field:'fileName', title: '文件名称'},
            {field:'createTime', width:120, title: '上传时间',align: "center",templet: function(d){
                console.log(d);
                    return d.createTime.substring(0,10);
                }},
            {field:'createUserName', width:120, title: '上传人',align: "center"},
            {field:'deptName', title: '所在部门', width: 100,align: "center"}
        ]]

    });
});