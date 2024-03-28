
var Statistics = {};
layui.use(['layer', 'table', 'HussarAjax', 'Hussar', 'form'], function() {
    var layer = layui.layer
        , table = layui.table
        , $ = layui.jquery
        , form = layui.form
        , $ax = layui.HussarAjax
        , Hussar = layui.Hussar;
    form.render();


    //初始化echarts图
    Statistics.initEchart = function () {

        var aarr,barr,carr = [];
        var ajax = new $ax(Hussar.ctxPath + "/statistical/getQueTableList", function(data) {
            aarr = data.majorName;
            barr = data.queAllNum;
            carr = data.timelyNum;
        }, function(data) {

        });
        ajax.start();



        var  firEchart = echarts.init(document.getElementById('echarts'));
        var option = {
            "color": [
                "#2980ff",
                "#F4B345"
            ],
            "grid": {
                left: 50,
                right: 50,
                top:50,
                bottom:80
            },
            "legend": {
                top: 5,
                itemWidth: 10,
                itemHeight: 10,
                textStyle: {
                    fontSize: 14,
                    color: '#333',
                    padding: [3, 8, 0, 2]
                },
                data:['数量', '回答及时率'],
            },

            xAxis: [
                {
                    type: "category",
                    data: aarr,
                    axisLine: {
                        lineStyle: {
                            color: '#d3dede'
                        }
                    },
                    axisTick: {
                        show: false,
                    },
                    splitLine: {
                        show: false,
                    },
                    axisLabel: {
                        color: "#929292",
                        interval: 0,
                        rotate: "45",
                        textStyle: {
                            fontSize: 12
                        },
                    },
                },
            ],
            "yAxis": [{
                name: '数量（个）',
                axisLine: {
                    show: false
                },
                type: "value",
                axisTick: {
                    show: false,
                },
                splitLine: {
                    show: true,
                    lineStyle: {
                        type: 'dashed',
                        color: '#d3dede'
                    }
                },
                axisLabel: {
                    color: "#888",
                    fontSize: "12",
                },
            },
                {
                    "type": "value",
                    "name": "回答及时率（%）",
                    "position": "right",
                    "axisLabel": {
                        "formatter": "{value} %",
                        color: '#888'
                    },
                    "max": 100,
                    "splitLine": {
                        "show": false
                    },
                    "axisPointer": {
                        "show": true
                    },
                    "axisTick": {
                        show: false
                    },
                    axisLine: {
                        show: false
                    },
                }
            ],
            "series": [{
                "type": "bar",
                "name": "数量",
                "barWidth": "20",
                "data": barr,
                itemStyle: {
                    normal: {
                        color:'#5aa0f9'
                    }
                }
            },
                {
                    "type": "line",
                    "name": "回答及时率",
                    "yAxisIndex": 1,
                    itemStyle: {
                        color: '#F4B345',
                    },
                    symbolSize: 8,
                    symbol: 'circle',
                    "data": carr
                }
            ]
        };
        firEchart.setOption(option);
        $(window).resize(firEchart.resize)
    };
    Statistics.initFirTable = function(){
        table.render({
            elem: '#firTable',
            url : Hussar.ctxPath+'/statistical/getTableData',
            /*where:{
                opType:'4'
            },*/
            height:'360'
            ,page: false //开启分页
            ,cols: [[ //表头
                {type: 'numbers', title: '序号', width:80,align: 'center'}
                ,{field: 'majorName', title: '问题专业',align: 'center'}
                ,{field: 'reviewer', title: '回答人',align: 'center'}
                ,{field: 'num', title: '问题数量',align: 'center'}
                ,{field: 'timely', title: '回答及时率',align: 'center'}
            ]]
        });
    };

    $(function () {
        Statistics.initEchart();
        Statistics.initFirTable();
    })

})