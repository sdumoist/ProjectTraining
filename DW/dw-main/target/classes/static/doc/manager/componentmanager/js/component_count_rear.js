/**
 * Created by HP on 2019/7/17.
 */
/**
 * @Description:    统计分析 脚本文件
 * @Author:         liuX
 * @CreateDate:     2018/10/08 1
 * @Version:        1.0
 */

var  Statistics = {
    range_dic_type: "date_range",       //时间范围字典类型值
    date_range_value: "",            //当前选中的时间范围字典值
    chart: null,
    dateStr:"today",
    dateStartNum:"",
    dateEndNum:"",
    dateNum:"",
    state:"",
    syb:"0",
    dept:"dept",
    dept2:"dept"
};
layui.use(['layer', 'table', 'HussarAjax', 'Hussar', 'form','laydate'], function() {
    var layer = layui.layer
        , table = layui.table
        , $ = layui.jquery
        , form = layui.form
        , $ax = layui.HussarAjax
        , Hussar = layui.Hussar
        ,laydate = layui.laydate;;
var nowTime=new Date();
    var start=laydate.render({
        elem: '#start'
        ,type: 'date',
        theme:'molv',

        max:'nowTime',
        done:function(value,date){
            if (value !== '') {
                end.config.min = {
                    year: date.year,
                    month: date.month - 1,
                    date: date.date
                }
            }else {
                end.config.min.year = '';
                end.config.min.month = '';
                end.config.min.date = '';

            }
            Statistics.dateStartEnd();
        }

    });
   var end=laydate.render({
        elem: '#end'
        ,type: 'date',
        theme:'molv',

        max:'nowTime',
        done:function(value,date){
            if (value !== '') {
            start.config.max={
                year:date.year,
                month:date.month-1,
                date:date.date
            }
            }else {
                var y=nowTime.getFullYear();
                var month=nowTime.getMonth();
                var td=nowTime.getDate();
                start.config.max.year = y;
                start.config.max.month = month;
                start.config.max.date = td;

            }
            Statistics.dateStartEnd();
        }
    });
    var startMu=laydate.render({
        elem: '#startMu'
        ,type: 'date',
        theme:'molv',

        max:'nowTime',
        done:function(value,date){
            if (value !== '') {
                endMu.config.min = {
                    year: date.year,
                    month: date.month - 1,
                    date: date.date
                }
            }else {
                endMu.config.min.year = '';
                endMu.config.min.month = '';
                endMu.config.min.date = '';

            }
            Statistics.dateStartEndMu();
        }

    });
    var endMu=laydate.render({
        elem: '#endMu'
        ,type: 'date',
        theme:'molv',

        max:'nowTime',
        done:function(value,date){
            if (value !== '') {
                startMu.config.max = {
                    year: date.year,
                    month: date.month - 1,
                    date: date.date
                }
            }else {
                var y=nowTime.getFullYear();
                var month=nowTime.getMonth();
                var td=nowTime.getDate();
                startMu.config.max.year = y;
                startMu.config.max.month = month;
                startMu.config.max.date = td;

            }
            Statistics.dateStartEndMu();
        }
    });

    function openWin(url) {
        var a = document.createElement("a"); //创建a标签
        a.setAttribute("href", url);
        a.setAttribute("target", "_blank");
        document.body.appendChild(a);
        a.click(); //执行当前对象
    }

    //以下为模拟数据
    var legendData = ['登录量','下载量','预览量'];
    // var XData = ['第一事业部群','第一事业部群','第一事业部群','第一事业部群','第一事业部群','第一事业部群','财务','第一事业部群','第一事业部群','财务','第一事业部群','第一事业部群','第一事业部群','第一事业部群','财务','第一事业部群','第一事业部群','财务'];
    // var data1 = [123,210,300,153,200,154,116,217,216,231,300,153,200,154,116,217,216,231];
    // var data2 = [223,110,100,53,100,54,96,107,316,131,200,93,100,124,186,117,266,131];
    // var data3 = [213,180,90,103,220,254,96,117,206,201,100,253,130,104,106,117,227,201];

    /*
     * 初始化各个表格
     */
    //文件下载榜
    Statistics.initFirTable = function(){
        table.render({
            elem: '#firTable',
            url : Hussar.ctxPath+'/component/componentCount'
            ,page: false //开启分页
            ,cols: [[ //表头
                {type: 'numbers', title: '排名', align: 'center', width: 55}
                ,{field: 'userName', title: '提报人',align: 'center'}
                ,{field: 'deptName', title: '所属部门',align: 'center',width:160}
                ,{field: 'count', title: '提报数量',width:160,align: 'center'}
            ]]
        });
    };

    //文件预览榜
    Statistics.initSecTable = function(){
        table.render({
            elem: '#secTable',
            url : Hussar.ctxPath+'/component/componentDeptCount'
            ,page: false //开启分页
            ,cols: [[ //表头
                {type: 'numbers', title: '排名', align: 'center', width: 55}
                ,{field: 'userName', title: '成果提报人',align: 'center'}
                ,{field: 'deptName', title: '所属部门',align: 'center',width:160}
                ,{field: 'count', title: '复用数量',width:160,align: 'center'}
            ]]
        });
    };



    /**
     * 初始化成果统计Echarts图表
     */
    Statistics.initfirEchart = function () {
        var ajax = new $ax(Hussar.ctxPath + "/component/componentGraphCount", function(data) {
            var  firEchart = echarts.init(document.getElementById('firchart'));
            var option = {
                backgroundColor: '#fff',
                tooltip: {
                    trigger: 'axis',
                    axisPointer: { // 坐标轴指示器，坐标轴触发有效
                        type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
                    },
                    formatter:'{b}<br />'+ '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#018cff;"></span>成果数量：'+'{c}'
                },
                grid: {
                    left: 50,
                    right: 0
                },
                xAxis: {
                    type: 'category',
                    data: data.list,
                    axisLabel: {// 坐标轴刻度标签的相关设置。
                        interval: 0,
                        rotate: "45",
                        color: '#929292',
                        fontSize: 12,
                        formatter: function (value) {
                            var ret = "";// 拼接加返回的名称
                            var maxLength = 10;// 每项显示文字个数
                            var valLength = value.length;// X轴类目项的文字个数
                            if (valLength > 10)// 如果类目项的文字大于10,
                            {
                                ret = value.substring(0, maxLength) + "...";
                                return ret;
                            } else {
                                return value;
                            }
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#d3dede'
                        }
                    }
                },
                yAxis: {
                    type: 'value',
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        color: '#888',
                        fontSize: 12
                    },
                    splitLine: {
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            color: '#d3dede'
                        }
                    }
                },
                series: [{
                    name: '上传成果数量',
                    type: 'bar',
                    barWidth: 15,
                    itemStyle: {
                        normal: {
                            color: '#018cff'
                        }
                    },label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data:data.numList
                }]
            };
            firEchart.setOption(option);
            $(window).resize(firEchart.resize)

        },function(data){
            Hussar.error("查询数量失败!");
        });
        ajax.start();
    };

    /**
     * 初始化复用统计Echarts图表
     */
    Statistics.reuse = function () {
        var ajax = new $ax(Hussar.ctxPath + "/multiplex/multiplexGraphCount?", function(data) {
            var  firEchart = echarts.init(document.getElementById('reuse'));
            var option = {
                backgroundColor: '#fff',
                tooltip: {
                    trigger: 'axis',
                    axisPointer: { // 坐标轴指示器，坐标轴触发有效
                        type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
                    },
                    formatter:'{b}<br />'+ '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#018cff;"></span>复用数量：'+'{c}'
                },
                grid: {
                    left: 50,
                    right: 0
                },
                xAxis: {
                    type: 'category',
                    data: data.list,
                    axisLabel: {// 坐标轴刻度标签的相关设置。
                        interval: 0,
                        rotate: "45",
                        color: '#929292',
                        fontSize: 12,
                        formatter: function (value) {
                            var ret = "";// 拼接加返回的名称
                            var maxLength = 10;// 每项显示文字个数
                            var valLength = value.length;// X轴类目项的文字个数
                            if (valLength > 10)// 如果类目项的文字大于10,
                            {
                                ret = value.substring(0, maxLength) + "...";
                                return ret;
                            } else {
                                return value;
                            }
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#d3dede'
                        }
                    }
                },
                yAxis: {
                    type: 'value',
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        color: '#888',
                        fontSize: 12
                    },
                    splitLine: {
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            color: '#d3dede'
                        }
                    }
                },
                series: [{
                    name: '复用数量',
                    type: 'bar',
                    barWidth: 15,
                    itemStyle: {
                        normal: {
                            color: '#018cff'
                        }
                    },label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data:data.numList
                }]
            };
            firEchart.setOption(option);
            $(window).resize(firEchart.resize)

        },function(data){
            Hussar.error("查询数量失败!");
        });
        ajax.start();
    };

    /**
     *  初始化成果点击事件
     */
    Statistics.initClickEvent = function () {
        Statistics.dateStartEnd=(function () {

            //获取当前点击的时间范围值
            Statistics.dateStartNum = $("#start").val();
            Statistics.dateEndNum=$("#end").val();
            Statistics.refreshEchart();
        });
        $(".state>div").click(function () {
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
            //获取当前点击的时间范围值
            Statistics.state = $(this).attr("value");
            Statistics.refreshEchart();
        });
        $(".syb>div").click(function () {
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
            //获取当前点击的时间范围值
            Statistics.syb = $(this).attr("value");
            Statistics.refreshEchart();
        });
        $(".dept>div").click(function () {
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
            //获取当前点击的时间范围值
            Statistics.dept = $(this).attr("value");
            Statistics.refreshEchart();
        })
    };
    /**
     *  初始化复用点击事件
     */
    Statistics.initClickReuse = function () {
        Statistics.dateStartEndMu=(function () {

            //获取当前点击的时间范围值
            Statistics.dateStartNum = $("#startMu").val();
            Statistics.dateEndNum=$("#endMu").val();
            Statistics.flushReuse();
        });
        $(".dept2>div").click(function () {
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
            //获取当前点击的时间范围值
            Statistics.dept2 = $(this).attr("value");
            Statistics.flushReuse();
        });
        $(".bg>div").click(function () {
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
            //获取当前点击的时间范围值
            Statistics.syb = $(this).attr("value");
            Statistics.flushReuse();
        });
    };

    /**
     * 初始化统计分析顶部数据
     */
    Statistics.initFileNum = function(){
        var ajax = new $ax(Hussar.ctxPath + "/component/componentTopCount",
            function(data) {
                $("#analysis-doc .total .amount ").html(data[2]);
                $("#analysis-doc-today .total .amount ").html(data[3]);
                $("#analysis-visit .total .amount ").html(data[0]);
                $("#analysis-visit-today .total .amount ").html(data[1]);
                $("#analysis-prevew .total .amount ").html(data[4]);
                $("#analysis-download .total .amount ").html(data[5]);

            },
            function(data) {
                Hussar.error("获取List数据失败!");
            });
        ajax.start();
    }

    /**
     *  刷新Echart图表
     */
    Statistics.refreshEchart = function () {
        var ajax = new $ax(Hussar.ctxPath + "/component/componentGraphCount?dateStart="+Statistics.dateStartNum+"&dateEnd="+Statistics.dateEndNum+"&state="+Statistics.state+"&bu="+Statistics.syb+"&order="+Statistics.dept, function(data) {
            var  firEchart = echarts.init(document.getElementById('firchart'));
            var option = {
                backgroundColor: '#fff',
                tooltip: {
                    trigger: 'axis',
                    axisPointer: { // 坐标轴指示器，坐标轴触发有效
                        type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
                    },
                    formatter:'{b}<br />'+ '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#018cff;"></span>成果数量：'+'{c}'
                },
                grid: {
                    left: 50,
                    right: 0
                },
                xAxis: {
                    type: 'category',
                    data: data.list,
                    axisLabel: {// 坐标轴刻度标签的相关设置。
                        interval: 0,
                        rotate: "45",
                        color: '#929292',
                        fontSize: 12,
                        formatter: function (value) {
                            var ret = "";// 拼接加返回的名称
                            var maxLength = 10;// 每项显示文字个数
                            var valLength = value.length;// X轴类目项的文字个数
                            if (valLength > 10)// 如果类目项的文字大于10,
                            {
                                ret = value.substring(0, maxLength) + "...";
                                return ret;
                            } else {
                                return value;
                            }
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#d3dede'
                        }
                    }
                },
                yAxis: {
                    type: 'value',
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        color: '#888',
                        fontSize: 12
                    },
                    splitLine: {
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            color: '#d3dede'
                        }
                    }
                },
                series: [{
                    name: '上传成果数量',
                    type: 'bar',
                    barWidth: 15,
                    itemStyle: {
                        normal: {
                            color: '#018cff'
                        }
                    },label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data:data.numList
                }]
            };
            firEchart.setOption(option);
            $(window).resize(firEchart.resize)

        },function(data){
            Hussar.error("查询数量失败!");
        });
        ajax.start();
    }

    /**
     *  刷新复用Echart图表
     */
    Statistics.flushReuse = function () {
        var ajax = new $ax(Hussar.ctxPath + "/multiplex/multiplexGraphCount?dateStart="+Statistics.dateStartNum+"&dateEnd="+Statistics.dateEndNum+"&bu="+Statistics.syb+"&order="+ Statistics.dept2, function(data) {
            var  firEchart = echarts.init(document.getElementById('reuse'));
            var option = {
                backgroundColor: '#fff',
                tooltip: {
                    trigger: 'axis',
                    axisPointer: { // 坐标轴指示器，坐标轴触发有效
                        type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
                    },
                    formatter:'{b}<br />'+ '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#018cff;"></span>复用数量：'+'{c}'
                },
                grid: {
                    left: 50,
                    right: 0
                },
                xAxis: {
                    type: 'category',
                    data: data.list,
                    axisLabel: {// 坐标轴刻度标签的相关设置。
                        interval: 0,
                        rotate: "45",
                        color: '#929292',
                        fontSize: 12,
                        formatter: function (value) {
                            var ret = "";// 拼接加返回的名称
                            var maxLength = 10;// 每项显示文字个数
                            var valLength = value.length;// X轴类目项的文字个数
                            if (valLength > 10)// 如果类目项的文字大于10,
                            {
                                ret = value.substring(0, maxLength) + "...";
                                return ret;
                            } else {
                                return value;
                            }
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#d3dede'
                        }
                    }
                },
                yAxis: {
                    type: 'value',
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        color: '#888',
                        fontSize: 12
                    },
                    splitLine: {
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            color: '#d3dede'
                        }
                    }
                },
                series: [{
                    name: '复用数量',
                    type: 'bar',
                    barWidth: 15,
                    itemStyle: {
                        normal: {
                            color: '#018cff'
                        }
                    },label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data:data.numList
                }]
            };
            firEchart.setOption(option);
            $(window).resize(firEchart.resize)

        },function(data){
            Hussar.error("查询数量失败!");
        });
        ajax.start();
    }

    Statistics.initEvent = function(){
        table.on('tool(secTable)', function (obj) {
            if (obj.event === 'showPdf') {
                showPdf(obj.data.DOCID,obj.data.DOCTYPE);
            }
        });

        table.on('tool(firTable)', function (obj) {
            if (obj.event === 'showPdf') {
                showPdf(obj.data.DOCID,obj.data.DOCTYPE);
            }
        });
    }

    Statistics.initClick = function () {
        $("#html2canvas").click(function () {
            $("#html2canvasDiv").html("");
            $("#html2canvas").css("display","none")
            htmlToCanvas();
            layer.open({
                type:1,
                title:false,
                closeBtn:false,
                area:['1100px','400px'],
                offset: 100,
                content:$("#html2canvasDiv"),
                btn: ['保存', '取消'],
                btn1: function(){
                    canvasToImage();
                    layer.close();
                },
                btn2: function () {
                    layer.close()
                    $("#html2canvas").css("display","block")
                }
            })
        });
    };

    showPdf = function(id,fileSuffixName) {
        var keyword =  $("#headerSearchInputValue").val();
        if(fileSuffixName==".png"||fileSuffixName==".jpg"||fileSuffixName==".gif"||fileSuffixName==".bmp"||fileSuffixName==".ceb"||fileSuffixName==".jpeg"){
            openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
        }else if(fileSuffixName==".mp4"||fileSuffixName==".wmv"){
            openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
        } else if(fileSuffixName==".mp3"||fileSuffixName==".m4a"){
            openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
        }else if(fileSuffixName == '.docx'||fileSuffixName == '.doc'||fileSuffixName == '.xls'
            || fileSuffixName == '.xlsx'||fileSuffixName == '.txt'||fileSuffixName == '.pdf'
            ||fileSuffixName == '.ceb' ||fileSuffixName == '.ppt'|| fileSuffixName == '.pptx'){
            openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id );
        }else {
            layui.define('layer', function(exports) {
                var layer = layui.layer;
                layer.msg("此文件类型不支持预览。");
            })
        }
    }


    /**
     * 页面初始化
     */
    $(function () {
        //初始化头部数据
        Statistics.initFileNum();
        //初始化目录文件统计图表
        Statistics.initfirEchart();
        //文件下载排行
        Statistics.initFirTable();
        //文件预览排行
        Statistics.initSecTable();
        //部门共享榜
        Statistics.initClickEvent();
        //部门活跃度额分析
        // Statistics.initEchart(legendData,XData,data1,data2,data3);

        //部门活跃度分析点击事件
        Statistics.reuse();
        Statistics.initClickReuse();

        Statistics.initEvent();
        Statistics.initClick();
    });
    function htmlToCanvas(){
        html2canvas(document.getElementById('uploadList'), {
            onrendered: function(canvas) {
                canvas.setAttribute('id','thecanvas');	//添加属性
                $("#html2canvasDiv").append(canvas);
            },
            background: "#ffffff",		//canvas的背景颜色，如果没有设定默认透明
            logging: true,		//在console.log()中输出信息
             width: 1200,			//图片宽
             // height: 300,		//图片高*/
            useCORS: true, // 【重要】开启跨域配置
        });
    }

    function canvasToImage(){
        var oCanvas = document.getElementById("thecanvas");

        /*自动保存为png*/
        // 获取图片资源
        var img_data1 = Canvas2Image.saveAsPNG(oCanvas, true).getAttribute('src');
        saveFile(img_data1, 'Statistics.png');
    }


    // 保存文件函数
    var saveFile = function(data, filename){
        var save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a');
        save_link.href = data;
        save_link.download = filename;

        var event = document.createEvent('MouseEvents');
        event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
        save_link.dispatchEvent(event);
    };

});
