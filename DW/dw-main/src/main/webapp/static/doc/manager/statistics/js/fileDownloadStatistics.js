/**
 * 文件系统-文件管理初始化
 */
var Statistics = {
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    pageData : [],
    myChart:null
};
layui.use(['layer','form','table','bootstrap_table_edit','Hussar', 'HussarAjax'], function(){
	var layer = layui.layer
	    ,table = layui.table
	    ,form = layui.form
        ,Hussar = layui.Hussar
	    ,$ = layui.jquery
	    ,$ax = layui.HussarAjax;

/**
 * 初始化表格的列
 */
	Statistics.initEchar=function(url){
		var ajax = new $ax(Hussar.ctxPath + url, function(data){
			Statistics.myChart = echarts.init(document.getElementById('main'));
	        // 指定图表的配置项和数据

	        option = {
	            backgroundColor: '#fff',
	            title: {
	                text: '文件下载统计',
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
	                        var ret = "";//拼接加返回的名称
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
	                name:'上传文件下载数量',
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
	        Statistics.myChart.setOption(option);


	    },function(data){
	        Hussar.error("查询数量失败!");
	    });
	    ajax.start();
	}
	Statistics.init = function() {
		// 初始化表格
		Statistics.tableIns = table.render({
			elem : '#fileDataLogTable', // 指定原始表格元素选择器（推荐id选择器）
			height : $("body").height() - $(".toolBar").height() - 135, // 容器高度
			url : '/fileStatistics/getFileListData', // 数据接口
			id : 'fileDataLogTable',
			where:{
				opType:'4'
			},
			done : function(res) {
				// .假设你的表格指定的 id="topicList"，找到框架渲染的表格
				var tbl = $('#fileDataLogTable').next('.layui-table-view');
				// 记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
				Statistics.pageData = res.data;
				var len = Statistics.pageData.length;
				// .遍历当前页数据，对比已选中项中的 id
				for (var i = 0; i < len; i++) {
					if (layui.data('checked', Statistics.pageData[i]['userId'])) {
						// 选中它，目前版本没有任何与数据或表格 id 相关的标识，不太好搞，土办法选择它吧
						tbl.find('table>tbody>tr').eq(i).find('td').eq(0).find('input[type=checkbox]').prop('checked', true);
					}
				}
				form.render('checkbox');
				// .PS：table 中点击选择后会记录到 table.cache，没暴露出来，也不能
				// mytbl.renderForm('checkbox');
			},
			request : {
				pageName : 'pageNumber', // 页码的参数名称，默认：page
				limitName : 'pageSize' // 每页数据量的参数名，默认：limit
			},
			page : true, // 开启分页
			// id : 'groupListView',
			even : true,
			cols : [ [ {
				title : '序号',
				type : 'numbers',
				width : '50',
				align : "center"
			}, {
				title : '文件名',
				field : 'TITLE',
				align : 'left',
				halign : 'center',
				width : "30%"
			}, {
				title : '大小',
				field : 'FILE_SIZE',
				align : 'center',
				halign : 'center',
				width : "10%"
			}, {
				title : '上传人',
				field : 'AUTHOR_ID',
				align : 'center',
				halign : 'center',
				width : "10%"
			}, {
				title : '上传时间',
				field : 'CREATE_TIME',
				align : 'center',
				halign : 'center',
				width : "30%"
			}, {
				title : '下载次数',
				field : 'YLCOUNT',
				align : 'center',
				halign : 'center',
				width : "20%"
			} ] ]
		});
	}



$(function () {
    $(".switch>div").click(function () {
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
    });
    var url = "/fileStatistics/getDeptDownloadData";
	form.on('select(opType)', function(data){
		  if(data.value=="dept"){
			 url = "/fileStatistics/getDeptDownloadData";
			 $("#content").css("display","block");
			 $("#main").css("display","block" );
			 Statistics.initEchar(url);
			 Statistics.myChart.resize();
			 //隐藏table
			 $(".layui-table-box").parent().css("display", "none");
			 $("#fileDataLogTable").css("display","none" );
		  }else{
			  if(data.value=="user"){
				  url = "/fileStatistics/getUserDownloadData" ;
				  $("#content").css("display","block");
				  $("#main").css("display","block" );
				  Statistics.initEchar(url);
				  Statistics.myChart.resize();
				  //隐藏table
				  $(".layui-table-box").parent().css("display", "none");
				  $("#fileDataLogTable").css("display","none" );
			  } 
			  if(data.value=="file"){
				  $("#main").css("display","none" );
				  $("#content").css("display","none" );
            	  Statistics.init();
            	  $("#fileDataLogTable").css("display","block" );
              }
		  }
		});
	form.render();
	$("#content").css("display","block");
	$("#main").css("display","block" );
	Statistics.initEchar(url);
	Statistics.myChart.resize();
})

});