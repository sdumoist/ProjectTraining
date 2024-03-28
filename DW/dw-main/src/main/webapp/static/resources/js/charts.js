/**
 * Created by lx on 2018/2/1.
 */
    var datas = "";
    $(document).ready(function() {
        //设置日报标题
        $.ajax({
            url: "/docstatis/queryDocStatis",// 请求的URL
            type: "post",
            async : false,
            dataType: "json",
            success: function(data){
                datas = data;
            },
            error:function () {
                //$.showInforDlg("提示", "加載失敗！", 0);
            }
        });
        initBarChat1();
        initBarChat2();
        initBarChat3();
        initBarChat4();
        //实现日期选择联动
        var start = {
            format: 'YYYY-MM-DD',
            minDate: '2014-06-16 23:59:59', //设定最小日期为当前日期
            //festival:true,
            maxDate: $.nowDate({DD:0}), //最大日期
            choosefun: function(elem,datas){
                end.minDate = datas; //开始日选好后，重置结束日的最小日期
                endDates();
            },
            okfun:function (elem,datas) {
                alert(datas)
            }
        };
        var end = {
            format: 'YYYY-MM-DD',
            minDate: $.nowDate({DD:0}), //设定最小日期为当前日期
            //festival:true,
            maxDate: '2099-06-16 23:59:59', //最大日期
            choosefun: function(elem,datas){
                start.maxDate = datas; //将结束日的初始值设定为开始日的最大日期
            }
        };
        function endDates() {
            end.trigger = false;
            $("#inpend").jeDate(end);
        }
        $("#inpstart").jeDate(start);
        $("#inpend").jeDate(end);
    });

    function initBarChat1() {
        //var deptNamesArray=datas.deptNames.split(",");
        //var deptAvgsArray=datas.deptAvgs.split(",");
        var barChart1 = echarts.init(document.getElementById("deptDiv"));
        //var xAxisData = ['研发中心','信息技术中心','总经理办公司','能源中心','南方分公司','南京分公司','青岛分公司','财务处','上市办','轨道交通中心','产品中心','人资'];
        //var yAxisData = [154, 140, 137, 134, 130, 130, 120, 123, 120, 120, 110, 100];
        var xAxisData =  datas.deptNames.split(",");
        var yAxisData = datas.deptAvgs.split(",");
        var boxName = "部门排名";
        var option = {
            backgroundColor:"#fff",
            title: {
                text: boxName,
                left:10,
                top:10,
                textStyle:{
                    color:'#323232',
                    fontSize:14,
                    fontWeight:'normal'
                }
            },
            grid:{
                left:40,
                right:14,
                bottom:80
            },
            tooltip : {
                trigger: 'axis',
                
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            xAxis: {
                type : 'category',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: true,
                    lineStyle:{
                        color:'#e8ebeb',
                        width:1
                    }
                },
                axisLabel:{
                    interval :0,
                    rotate:-45,
                    textStyle: {
                        color: '#aaa',
                        fontSize:12
                    }
                },
                data: xAxisData
            },
            yAxis: {
                type : 'value',
                axisTick: {
                    show: false
                },
                splitLine:{
                    show: true,
                    lineStyle:{
                        color:'#f0f3f2',
                        width:1
                    }
                },
                axisLine: {
                    show: true,
                    lineStyle:{
                        color:'#e8ebeb',
                        width:1
                    }
                },
                axisLabel:{
                    interval :0,
                    textStyle: {
                        color: '#aaa',
                        fontSize:12
                    }
                }
            },
            series : {
                name:'部门积分',
                type:'bar',
                barWidth: '40%',
                itemStyle: {
                    normal: {
                        barBorderRadius: [15, 15, 0, 0],
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1,[
                                {offset: 0, color: '#0e9efe'},
                                {offset: 0.25, color: '#25a6df'},
                                {offset: 0.5, color: '#42b2b3'},
                                {offset: 0.75, color: '#66be81'},
                                {offset: 1, color: '#7cc75e'}
                            ]
                        )
                    }
                },
                data:yAxisData
            }
        };
        barChart1.setOption(option);
        $(window).resize(barChart1.resize);
    }
    function initBarChat2() {
        var barChart2 = echarts.init(document.getElementById("employDiv"));
       // var xAxisData = ['员工1','员工2','员工3','员工4','员工5','员工6','员工7','员工8','员工9','员工10','员工11','员工12'];
      //  var yAxisData = [159, 157, 150, 147, 145, 140, 130, 128, 120, 116, 115, 110];
        var xAxisData =datas.userNames.split(",");
        var yAxisData = datas.employSums.split(",");
        var boxName = "部门员工排名";
        var option = {
            backgroundColor:"#fff",
            title: {
                text: boxName,
                left:10,
                top:10,
                textStyle:{
                    color:'#323232',
                    fontSize:14,
                    fontWeight:'normal'
                }
            },
            grid:{
                left:40,
                right:14,
                bottom:80
            },
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            xAxis: {
                type : 'category',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: true,
                    lineStyle:{
                        color:'#e8ebeb',
                        width:1
                    }
                },
                axisLabel:{
                    interval :0,
                    rotate:-45,
                    textStyle: {
                        color: '#aaa',
                        fontSize:12
                    }
                },
                data: xAxisData
            },
            yAxis: {
                type : 'value',
                axisTick: {
                    show: false
                },
                splitLine:{
                    show: true,
                    lineStyle:{
                        color:'#f0f3f2',
                        width:1
                    }
                },
                axisLine: {
                    show: true,
                    lineStyle:{
                        color:'#e8ebeb',
                        width:1
                    }
                },
                axisLabel:{
                    interval :0,
                    textStyle: {
                        color:"#aaa",
                        fontSize:12
                    }
                }
            },
            series : {
                name:'部门员工积分',
                type:'bar',
                barWidth: '40%',
                itemStyle: {
                    normal: {
                        barBorderRadius: [15, 15, 0, 0],
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1,[
                                {offset: 0, color: '#0e9efe'},
                                {offset: 0.25, color: '#25a6df'},
                                {offset: 0.5, color: '#42b2b3'},
                                {offset: 0.75, color: '#66be81'},
                                {offset: 1, color: '#7cc75e'}
                            ]
                        )
                    }
                },
                data:yAxisData
            }
        };
        barChart2.setOption(option);
        $(window).resize(barChart2.resize);
    }
    function initBarChat3() {
        var barChart3 = echarts.init(document.getElementById("previewDiv"));
      //  var xAxisData = ['文档1','文档2','文档3','文档4','文档5','文档6','文档7','文档8','文档9','文档10','文档11','文档12'];
     //   var yAxisData = [690, 684, 680,680, 675, 671, 668, 665, 663, 660, 659, 653];
        var xAxisData =  datas.docPreTitles.split(",");
        var yAxisData = datas.previewCount.split(",");
        var boxName = "文章浏览次数排名";
        var option = {
            backgroundColor:"#fff",
            title: {
                text: boxName,
                left:10,
                top:10,
                textStyle:{
                    color:'#323232',
                    fontSize:14,
                    fontWeight:'normal'
                }
            },
            grid:{
                left:40,
                right:14,
                bottom:80
            },
            tooltip : {
                trigger: 'axis',
                
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            xAxis: {
                type : 'category',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: true,
                    lineStyle:{
                        color:'#e8ebeb',
                        width:1
                    }
                },
                axisLabel:{
                    interval :0,
                    rotate:-45,
                    textStyle: {
                        color: '#aaa',
                        fontSize:12
                    }
                },
                data: xAxisData
            },
            yAxis: {
                type : 'value',
                axisTick: {
                    show: false
                },
                splitLine:{
                    show: true,
                    lineStyle:{
                        color:'#f0f3f2',
                        width:1
                    }
                },
                axisLine: {
                    show: true,
                    lineStyle:{
                        color:'#e8ebeb',
                        width:1
                    }
                },
                axisLabel:{
                    interval :0,
                    textStyle: {
                        color:'#aaa',
                        fontSize:12
                    }
                }
            },
            series : {
                name:'文章浏览次数',
                type:'bar',
                barWidth: '40%',
                itemStyle: {
                    normal: {
                        barBorderRadius: [15, 15, 0, 0],
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1,[
                                {offset: 0, color: '#0e9efe'},
                                {offset: 0.25, color: '#25a6df'},
                                {offset: 0.5, color: '#42b2b3'},
                                {offset: 0.75, color: '#66be81'},
                                {offset: 1, color: '#7cc75e'}
                            ]
                        )
                    }
                },
                data:yAxisData
            }
        };
        barChart3.setOption(option);
        $(window).resize(barChart3.resize);
    }
    function initBarChat4() {
        var barChart4 = echarts.init(document.getElementById("downLoadDiv"));
        //var xAxisData = ['文档1','文档2','文档3','文档4','文档5','文档6','文档7','文档8','文档9','文档10','文档11','文档12'];
       // var yAxisData = [690, 684, 680,680, 675, 671, 668, 665, 663, 660, 659, 653];
        var xAxisData =  datas.docDownTitles.split(",");
        var yAxisData = datas.downLoadCount.split(",");
        var boxName = "文章下载次数排名";
        var option = {
            backgroundColor:"#fff",
            title: {
                text: boxName,
                left:10,
                top:10,
                textStyle:{
                    color:'#323232',
                    fontSize:14,
                    fontWeight:'normal'
                }
            },
            grid:{
                left:40,
                right:14,
                bottom:80
            },
            tooltip : {
                trigger: 'axis',
                
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            xAxis: {
                type : 'category',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: true,
                    lineStyle:{
                        color:'#e8ebeb',
                        width:1
                    }
                },
                axisLabel:{
                    interval :0,
                    rotate:-45,
                    textStyle: {
                        color: '#aaa',
                        fontSize:12
                    }
                },
                data: xAxisData
            },
            yAxis: {
                type : 'value',
                axisTick: {
                    show: false
                },
                splitLine:{
                    show: true,
                    lineStyle:{
                        color:'#f0f3f2',
                        width:1
                    }
                },
                axisLine: {
                    show: true,
                    lineStyle:{
                        color:'#e8ebeb',
                        width:1
                    }
                },
                axisLabel:{
                    interval :0,
                    textStyle: {
                        color:'#aaa',
                        fontSize:12
                    }
                }
            },
            series : {
                name:'文章下载次数',
                type:'bar',
                barWidth: '40%',
                itemStyle: {
                    normal: {
                        barBorderRadius: [15, 15, 0, 0],
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1,[
                                {offset: 0, color: '#0e9efe'},
                                {offset: 0.25, color: '#25a6df'},
                                {offset: 0.5, color: '#42b2b3'},
                                {offset: 0.75, color: '#66be81'},
                                {offset: 1, color: '#7cc75e'}
                            ]
                        )
                    }
                },
                data:yAxisData
            }
        };
        barChart4.setOption(option);
        $(window).resize(barChart4.resize);
    }
 function queryStatis(){

    var beginTime = $("#inpstart").val();
    var endTime = $("#inpend").val();
    $.ajax({
        url: "/docstatis/queryDocStatis",// 请求的URL
        type: "post",
        async : false,
        data:{"beginTime":beginTime,"endTime":endTime},
        dataType: "json",
        success: function(data){
            datas = data;
        },
        error:function () {
            //$.showInforDlg("提示", "加載失敗！", 0);
        }
    });
    initBarChat1();
    initBarChat2();
    initBarChat3();
    initBarChat4();
 }


