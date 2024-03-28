/**
 * @Description: 定义重置用户密码脚本文件
 * @Author: liangdong
 * @Date: 2018/3/15.
 */
layui.use(['jquery','layer','Hussar','jstree','HussarAjax','form','table','HussarSecurity'], function(){
	var Hussar = layui.Hussar;
	var $ = layui.jquery;
	var layer = layui.layer;
	var $ax = layui.HussarAjax;
	var form = layui.form;
	var table = layui.table;
	var Security = new layui.HussarSecurity();
	
	var ResetAllPwd = {		    
		    layerIndex: -1,
		    seItem: null	//选中的条目
		};
	
	/**
	 * 初始化按钮事件
	 */
	ResetAllPwd.initButtonEvent = function () {
		//保存按钮事件
		$("#btnSave").click(function(){
			form.on('submit(go)', function(data){
				ResetAllPwd.save();
			});				
	    });		
		//取消按钮事件
		$("#btnClose").click(function(){
			ResetAllPwd.closeWin();			
	    });	
		//	查询按钮事件
		$("#btnSearch").click(function(){
			var userAccount = $("#userAccount").val();
			var userName = $("#userName").val();
			table.reload('userTable', {where:{
                userAccount : userAccount,
				userName : userName
	        }, page: {
					curr: 1 //重新从第 1 页开始
				}});
		});
		//	重置按钮事件
		$("#btnReset").click(function(){
			if(ResetAllPwd.checkIds()){
				ResetAllPwd.layerIndex=layer.open({
			        type: 1,
			        title: '重置用户密码',
			        area: ['40%', '40%'], //宽高
			        fix: false, //不固定
			        maxmin: false,
			        shadeClose: false,
			        content: $('#resetEvent'),
			        success:function(){			            
			        	$('#pwdForm')[0].reset();	//重置form		            
			        }
			    });
			}
		});
	}
	
	/**
	 * 关闭弹窗
	 */
	ResetAllPwd.closeWin = function () {
	    layer.close(ResetAllPwd.layerIndex);
	}
	
	/**
	 * 重置密码
	 */
	ResetAllPwd.save = function () {//重置所有密码
		var data = $("#pwdForm").serialize();
		var params = Security.encode(data);//调用加密方法进行加密

		// 提交信息
		var ajax = new $ax(Hussar.ctxPath + "/user/resetAllPwd", function(data) {
			if (data.code == "200") {	
				layer.close(ResetAllPwd.layerIndex);	//关闭弹窗
				Hussar.success(data.message);					 
			} else if (data.code) {
				Hussar.error(data.message);
			} else {
				Hussar.error("密码重置失败!");
			}			
		}, function(data) {
			Hussar.error("密码重置失败!");
		});
		ajax.set("data",params);
        ajax.set("userIds",ResetAllPwd.seItem);
		ajax.start();			
	}
	
	/**
	 * 表单验证
	 */
	ResetAllPwd.initValidate = function () {
		form.verify({
			required : function(value, item){
				if(value==""){					
					return '必填项不能为空';
				}
			},
			pwd: [/^[\S]{6,12}$/ , '密码必须6到12位，且不能出现空格'] 
		});	
	}
	
	/**
	 * 初始化用户表
	 */
	ResetAllPwd.initTableView = function () {
        var userAccount = $("#userAccount").val();
        var userName = $("#userName").val();
		table.render({
			elem: '#userTable',				
			url:Hussar.ctxPath+'/user/userList',
			page: true,	
			id: 'userTable',
			even: true,
            height:$("body").height() - $(".layui-form").outerHeight(true)-26,	           
			cols: [[{type: 'checkbox',width:40},
			        {type: 'numbers', title: '序号', align: 'center',width:50},
			        {field: 'userAccount', title: '用户账号', align:'center'},
			        {field: 'userName', title: '用户名称', align:'center'},	
			        {field: 'mobile', title: '手机', align:'center'},	
			        {field: 'eMail', title: '邮箱', align:'center'},	
			]],
			where: {
                userAccount : userAccount,
				userName : userName
			}
		});	
	}
	
	/**
	 * 检查是否选中多条记录
	 */
	ResetAllPwd.checkIds = function() {
		var selected = table.checkStatus('userTable').data;
		var ids = [];
		if (selected.length == 0) {
			Hussar.info(PLEASE_SELECT_AT_LEAST_ONE);
			return false;
		} else {
			for (var i = 0, l = selected.length; i < l; i++) {
				var u = selected[i];
				ids.push(u.userId);
			}
			var eId = ids.join(',');
			ResetAllPwd.seItem = eId;
			return true;
		}
	};

	$(function () { 
		ResetAllPwd.initButtonEvent();	//初始化按钮事件
		ResetAllPwd.initTableView();	//初始化表格
		ResetAllPwd.initValidate();		//初始化表单验证
	});
	
});




