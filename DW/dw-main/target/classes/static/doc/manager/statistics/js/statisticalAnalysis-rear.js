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
    dateStrDept:"dept",
    dateStrOrigin:"today",
};
layui.use(['layer', 'table', 'HussarAjax', 'Hussar', 'form'], function() {
    var layer = layui.layer
        , table = layui.table
        , $ = layui.jquery
        , form = layui.form
        , $ax = layui.HussarAjax
        , Hussar = layui.Hussar;

    //以下为模拟数据
    var legendData = ['登录量','下载量','预览量'];
    var legendDataOrigin = ['PC端','手机端'];
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
            url : Hussar.ctxPath+'/fileStatistics/getFileListData',
            where:{
                opType:'4'
            }
            ,page: false //开启分页
            ,cols: [[ //表头
                {field: 'FILERANK', title: '排名', width:55,align: 'center'}
                ,{field: 'TITLE', title: '文件',align: 'left',event: 'showPdf', style: 'cursor: pointer;color:#00a4ff'}
                ,{field: 'USERNAME', title: '上传人',align: 'center',width:220}
                ,{field: 'YLCOUNT', title: '下载量',width:80,align: 'center'}
            ]]
        });
    };

    //文件预览榜
    Statistics.initSecTable = function(){
        table.render({
            elem: '#secTable',
            url : Hussar.ctxPath+'/fileStatistics/getFileListData',
            where:{
                opType:'3'
            }
            ,page: false //开启分页
            ,cols: [[ //表头
                {field: 'FILERANK', title: '排名', width:55,align: 'center'}
                ,{field: 'TITLE', title: '文件',align: 'left', event: 'showPdf', style: 'cursor: pointer;color:#00a4ff'}
                ,{field: 'USERNAME', title: '上传人',align: 'center',width:220}
                ,{field: 'YLCOUNT', title: '预览量',width:80,align: 'center'}
            ]]
        });
    };

    //部门贡献帮
    Statistics.initTirTable = function(){
        table.render({
            elem: '#tirTable'
            ,url : Hussar.ctxPath+'/fileStatistics/getDeptUploadData'
            ,page: false //开启分页
            ,cols: [[ //表头
                {field: 'FILERANK', title: '排名', width:55,align: 'center'}
                ,{field: 'SHORT_NAME', title: '部门',align: 'center'}
                ,{field: 'FILENUM', title: '上传量',width:80,align: 'center'}
            ]]
        });
    };

    //个人贡献榜
    Statistics.initLastTable = function(){
        table.render({
            elem: '#lastTable'
            ,url : Hussar.ctxPath+'/fileStatistics/getUserUploadData'
            ,page: false //开启分页
            ,cols: [[ //表头
                {field: 'FILERANK', title: '排名', width:55,align: 'center'}
                ,{field: 'USER_NAME', title: '姓名',align: 'center'}
                ,{field: 'SHORT_NAME', title: '部门',align: 'center'}
                ,{field: 'FILENUM', title: '上传量',width:80,align: 'center'}
            ]]
        });

    };

    /**
     * 初始化目录文件统计Echarts图表
     */
    Statistics.initfirEchart = function () {
        var ajax = new $ax(Hussar.ctxPath + "/statistics/list?order="+Statistics.dateStrDept, function(data) {
            var  firEchart = echarts.init(document.getElementById('firchart'));
            var option = {
                backgroundColor: '#fff',
                tooltip: {
                    trigger: 'axis',
                    axisPointer: { // 坐标轴指示器，坐标轴触发有效
                        type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
                    },
                    formatter:'{b}<br />'+ '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#5aa0f9;"></span>文件数量：'+'{c}'
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
                    name: '上传文件数量',
                    type: 'bar',
                    barWidth: 15,
                    itemStyle: {
                        normal: {
                            color: '#5aa0f9'
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
     * 初始化部门活跃度Echarts图表
     */
    Statistics.initEchart = function (legendData,XData,data1,data2,data3) {
        Statistics.chart = echarts.init(document.getElementById('chart'));
        var option = {
            backgroundColor: '#fff',
            color:['#5aa0f9','#26b7b1','#70cf6b'],
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            grid: {
                left:50,
                right:0
            },
            legend: {
                data:legendData,
                right:0,
                itemWidth:8,
                itemHeight:8
            },
            xAxis: {
                type : 'category',
                data : XData,
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
                }
            },
            series: [{
                name:'登录量',
                type:'bar',
                barWidth: 15,
                data:data1
            },{
                name:'下载量',
                type:'bar',
                barWidth: 15,
                data:data2
            },{
                name:'预览量',
                type:'bar',
                barWidth: 15,
                data:data3
            }]
        };
        Statistics.chart.setOption(option);
        $(window).resize(Statistics.chart.resize);
    };

    /**
     *  初始化点击事件
     */
    Statistics.initClickEventDept = function () {
        $(".folder-statistical>div").click(function () {
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
            //获取当前点击的时间范围值
            Statistics.dateStrDept = $(this).attr("value");
            Statistics.initfirEchart();
        });
    };
    /**
     *  初始化点击事件
     */
    Statistics.initClickEvent = function () {
        $(".user>div").click(function () {
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
            //获取当前点击的时间范围值
            Statistics.dateStr = $(this).attr("value");
            Statistics.refreshEchart();
        });
    };

    /**
     * 初始化统计分析顶部数据
     */
    Statistics.initFileNum = function(){
        var ajax = new $ax(Hussar.ctxPath + "/fileStatistics/getFileNum",
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
    /**
     *  初始化点击事件
     */
    Statistics.initClickEventOrigin = function () {
        $(".origin>div").click(function () {
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
            //获取当前点击的时间范围值
            Statistics.dateStrOrigin = $(this).attr("value");
            Statistics.refreshEchartOrigin();
        });
    };
    Statistics.refreshEchartOrigin = function () {
        var ajax = new $ax(Hussar.ctxPath + "/fileStatistics/getOrigin?dateStr="+Statistics.dateStrOrigin, function(data){
            var chart = echarts.init(document.getElementById('chartOrigin'));
            var option = {
                backgroundColor: '#fff',
                color:['#5aa0f9','#70cf6b'],
                tooltip : {
                    trigger: 'axis',
                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                grid: {
                    left:50,
                    right:0
                },
                legend: {
                    data:legendDataOrigin,
                    right:0,
                    itemWidth:8,
                    itemHeight:8
                },
                xAxis: {
                    type : 'category',
                    data : data.xdata,
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
                    }
                },
                series: [{
                    name:'PC端',
                    type:'bar',
                    data:data.ydata,
                    barWidth : 50,//柱图宽度
                }
                    ,{
                        name:'手机端',
                        type:'bar',
                        data:data.wdata,
                        barWidth : 50,//柱图宽度
                    }]
            };
            chart.setOption(option);
            $(window).resize(chart.resize);

        },function(data){
            Hussar.error("用户来源获取数据失败");
        });
        ajax.start();
    }
    Statistics.refreshEchart = function () {
        var ajax = new $ax(Hussar.ctxPath + "/fileStatistics/getDeptActive?dateStr="+Statistics.dateStr, function(data){
            var chart = echarts.init(document.getElementById('chart'));
            var option = {
                backgroundColor: '#fff',
                color:['#5aa0f9','#26b7b1','#70cf6b'],
                tooltip : {
                    trigger: 'axis',
                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                grid: {
                    left:50,
                    right:0
                },
                legend: {
                    data:legendData,
                    right:0,
                    itemWidth:8,
                    itemHeight:8
                },
                xAxis: {
                    type : 'category',
                    data : data.xdata,
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
                    }
                },
                series: [{
                    name:'登录量',
                    type:'bar',
                    data:data.ydata
                }
                    ,{
                        name:'下载量',
                        type:'bar',
                        data:data.zdata
                    },{
                        name:'预览量',
                        type:'bar',
                        data:data.wdata
                    }]
            };
            chart.setOption(option);
            $(window).resize(chart.resize);

        },function(data){
            Hussar.error("部门活跃度统计分析获取数据失败");
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
                area:['1100px','300px'],
                offset: '50px',
                content:$("#html2canvasDiv"),
                btn: ['保存', '取消'],
                btn1: function(){
                    canvasToImage();
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
            openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id );
        }
    }

    function openWin(url) {
        var a = document.createElement("a"); //创建a标签
        a.setAttribute("href", url);
        a.setAttribute("target", "_blank");
        document.body.appendChild(a);
        a.click(); //执行当前对象
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
        Statistics.initTirTable();
        //个人贡献榜
        Statistics.initLastTable();
        //部门活跃度额分析
        // Statistics.initEchart(legendData,XData,data1,data2,data3);
        Statistics.refreshEchart();
        Statistics.refreshEchartOrigin();
        //部门活跃度分析点击事件
        Statistics.initClickEvent();
        Statistics.initClickEventDept();
        Statistics.initClickEventOrigin();
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
            width: 1200,		//图片宽
            /* height: 300,		//图片高*/
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
