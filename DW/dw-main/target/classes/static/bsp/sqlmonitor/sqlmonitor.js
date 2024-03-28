layui.use(['jquery', 'layer', 'Hussar', 'HussarAjax'], function () {

    var Hussar = layui.Hussar;
    var $ = layui.jquery;
    var table1;
    var SQL = {
        seItem: null,
        isSystem: null
    };

    SQL.initTable = function () {
        var slowSqlMillis = parseInt($("#slowSqlMillis").val());
        layui.use('table', function () {
            var table = layui.table;
            table.render({
                elem: '#SQLTable',
                toolbar: '#barDemo',
                defaultToolbar:[],
                height: $("body").height() - $(".layui-form").outerHeight(true) - 26,
                url: Hussar.ctxPath + '/SqlMonitor/list',
                cols: [
                    [{
                        title: '序号',
                        type: 'numbers',
                        align: 'center'
                    }, {
                        field: 'SQL',
                        title: 'SQL文本',
                        sort:true,
                        align:'left'
                    }, {
                        field: 'ExecuteCount',
                        title: '执行次数',
                        align: 'center',
                        sort:true,
                        width: 150
                    }, {
                        field: 'TotalTime',
                        title: '累计执行时间(毫秒)',
                        align: 'center',
                        sort:true,
                        width: 200
                    }, {
                        field: 'MaxTimespan',
                        title: '最慢执行时间(毫秒)',
                        align: 'center',
                        sort:true,
                        width: 200,
                        templet: function(d){
                            if (parseInt(d.MaxTimespan) > slowSqlMillis) {
                                return '<span style="color:#f14946;" >' + d.MaxTimespan + '</span>'
                            }else {
                                return '<span >' + d.MaxTimespan + '</span>'
                            }
                        }
                    }
                    ]],
                page: false,
                id: 'testReload',
                even: true
            });
            //头工具栏事件
            table.on('toolbar(user)', function(obj){
                var checkStatus = table.checkStatus(obj.config.id);
                if( "refreshList" == obj.event ){
                    table.reload('testReload');
                }
            });

            table1 = table;
        });
    }

    /**
     * 初始化
     */
    $(function () {
        SQL.initTable();
    });

});
