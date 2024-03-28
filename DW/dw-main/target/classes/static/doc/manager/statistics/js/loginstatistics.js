/**
* @Description:    用户登陆统计 脚本文件
* @Author:         LiangDong
* @CreateDate:     2018/8/13 10:55
* @UpdateUser:     LiangDong
* @UpdateDate:     2018/8/13 10:55
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
var LoginStatistics = {
    range_dic_type: "date_range",       //时间范围字典类型值
    date_range_value: "",            //当前选中的时间范围字典值
    chart: null
};

layui.use(['layer', 'table', 'HussarAjax', 'Hussar', 'form'], function() {
    var layer = layui.layer
        , table = layui.table
        , $ = layui.jquery
        , form = layui.form
        , $ax = layui.HussarAjax
        , Hussar = layui.Hussar;

    /**
     *  初始化下拉框
     */
    LoginStatistics.initSelectOption = function () {
        var ajax = new $ax(Hussar.ctxPath + "/loginStatistics/selectData",
            function(data) {
                for (var i = 0; i < data.length; i++) {
                    $("select[name='date_range']").append(
                        "<option value='" + data[i].VALUE + "'>"
                        + data[i].LABEL + "</option>");
                }
                form.render();
            },
            function(data) {
                Hussar.error("获取List数据失败!");
            });
        ajax.set("dicType",LoginStatistics.range_dic_type);
        ajax.start();

        //下拉框选择事件监听
        form.on('select(date_range)', function(data){
            //获取当前选择的时间范围
            LoginStatistics.date_range_value = data.value;
            LoginStatistics.refreshEchart();
        })
    }

    /**
     * 初始化Echarts图表
     */
    LoginStatistics.initEchart = function () {
        LoginStatistics.chart = echarts.init(document.getElementById('chart'));
        var option = {
            backgroundColor: '#fff',
            title: {
                text: '用户登录统计',
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
                bottom:'35%',
                y2: 140,
            },
            xAxis: {
                type : 'category',
                data : [],
                axisLabel : {//坐标轴刻度标签的相关设置。
                    interval:0,
                    rotate:"45",
                    color: '#929292',
                    fontSize: 12
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
                name:'登录用户',
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
                data:[]
            }]
        };
        LoginStatistics.chart.setOption(option);
    }

    /**
     *  刷新Echart图表
     */
    LoginStatistics.refreshEchart = function () {
        var ajax = new $ax(Hussar.ctxPath + "/loginStatistics/chartData", function(data){
            LoginStatistics.chart.setOption({
                xAxis: {
                    data: data.xdata
                },
                series: [{
                    data: data.ydata
                }]
            });

        },function(data){
            Hussar.error("用户登录统计失败");
        });
        ajax.set(LoginStatistics.range_dic_type, LoginStatistics.date_range_value);
        ajax.start();
        
    }

    /**
     *  初始化点击事件
     */
    LoginStatistics.initClickEvent = function () {
        $(".switch>div").click(function () {
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
            //获取当前点击的时间范围值
            LoginStatistics.date_range_value = $(this).attr("value");
            LoginStatistics.refreshEchart();
        });
    }



    /**
     * 页面初始化
     */
    $(function () {

        LoginStatistics.initEchart();
        LoginStatistics.initSelectOption();
        LoginStatistics.refreshEchart();
        LoginStatistics.initClickEvent();

    });

});