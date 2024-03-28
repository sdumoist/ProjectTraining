/**
 * 文件系统-文件管理初始化
 */
var Statistics = {
    id: "StatisticsTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    pageSize:20,
    pageNumber:1
};
layui.use(['layer','bootstrap_table_edit','Hussar', 'HussarAjax'], function(){
	var layer = layui.layer
	    ,table = layui.table
        ,Hussar = layui.Hussar
	    ,$ = layui.jquery
	    ,$ax = layui.HussarAjax;

/**
 * 初始化表格的列
 */




/**
 * 查询文件系统-文件列表
 */
Statistics.search = function () {
    $('#StatisticsTable').bootstrapTable('refresh');
};

$(function () {

    $(".switch>div").click(function () {
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
    });


    var ajax = new $ax(Hussar.ctxPath + "/statistics/list", function(data){
        var myChart = echarts.init(document.getElementById('main'));
        // 指定图表的配置项和数据

        option = {
            backgroundColor: '#fff',
            title: {
                text: '文档数量:'+data.totalNum,
                left: 'center',
                top: 'top',
                textStyle: {
                    fontSize: 18,
                    color: 'rgba(0,0,0, 0.6)'
                }
            },
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            grid: {
                bottom: '35%',
            },
            xAxis: {
                type : 'category',
                data :data.list,
                /*axisTick: {
                 alignWithLabel: true
                 },*/
                axisLabel : {//坐标轴刻度标签的相关设置。
                    interval:0,
                    rotate:"45",
                    color: '#929292',
                    fontSize: 12,
                    formatter:function(value)
                    {
                        var ret = "";//拼接加返回的目录名称
                        var maxLength = 10;//每项显示文字个数
                        var valLength = value.length;//X轴类目项的文字个数
                        if (valLength > 10)//如果类目项的文字大于10,
                        {
                            ret = value.substring(0, maxLength) + "...";
                            return ret;
                        }
                        else {
                            return value;
                        }
                    }
                },
                axisTick: {
                    show:false
                },
                axisLine: {
                    lineStyle: {
                        color: '#d3dede'
                    }
                }
            },
            yAxis: {
                type : 'value',
                axisLine: {
                    show:false
                },
                axisTick: {
                    show:false
                },
                axisLabel: {
                    color: '#888',
                    fontSize: 12
                },
                splitLine: {
                    show:true,
                    lineStyle: {
                        type:'dashed',
                        color: '#d3dede'
                    }
                },
            },
            series: [{
                name:'文件数量',
                type:'bar',
                barWidth: 15,
                label: {
                    normal: {
                        show: true,
                        position: 'top'
                    }
                },
                itemStyle:{
                    normal:{
                        color:'#26b7b1'
                    }
                },
                data:data.numList
            }]
        };
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);


    },function(data){
        Hussar.error("查询数量失败!");
    });
    ajax.start();


})

});