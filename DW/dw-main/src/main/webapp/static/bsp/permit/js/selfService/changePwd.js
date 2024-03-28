/**
 * @Description: 定义修改密码脚本文件
 * @Author: liangdong
 * @Date: 2018/3/14.
 */
layui.use(['jquery','layer','Hussar','jstree','HussarAjax','form','HussarSecurity'], function(){
	var Hussar = layui.Hussar;
	var $ = layui.jquery;
	var layer = layui.layer;
	var $ax = layui.HussarAjax;
	var form = layui.form;
	var Security =new layui.HussarSecurity();
	
	var ChangePwd = {		    
		    layerIndex: -1,
			regular: null,    //密码校验正则
			hint: null            //不满足规则时的提示信息
		};
	
	ChangePwd.initButtonEvent = function () {
		$("#btnSave").click(function(){
			form.on('submit(go)', function(data){
				ChangePwd.save();
			});			
	    });
		$("#btnClose").click(function(){
			closeWin();
	    });
	};
	
	closeWin = function () {//关闭弹出窗口
		var index = parent.layer.getFrameIndex(window.name);
		parent.layer.close(index);
	};
	
	ChangePwd.save = function () {//保存修改密码
		var old_pwd = $('#old_pwd').val();
		var new_pwd = $('#new_pwd').val();
		var c_pwd = $('#c_pwd').val();
		var params = "old_pwd="+old_pwd+"&new_pwd="+new_pwd+"&c_pwd="+c_pwd;
		var data = Security.encode(encodeURIComponent(params));//调用加密方法进行加密
		// var data = Security.encode($("#pwdForm").serialize());//调用加密方法进行加密
		// 提交信息
		var ajax = new $ax(Hussar.ctxPath + "/user/updatePwd", function(data) {
			if (data.code == "200") {					
				closeWin();
				parent.layui.Hussar.success("密码修改成功!");	
			} else if (data.code) {
				Hussar.error(data.message);
			} else {
				Hussar.error("密码修改失败!");
			}			
		}, function(data) {
			Hussar.error("密码修改失败!");
		});
		ajax.set("data",data);
		ajax.start();			
	};
	
	/**
	 * 表单验证
	 */
	ChangePwd.initValidate = function () {
		form.verify({
			required : function(value, item){
				if(value==""){					
					return '必填项不能为空';
				}
			},
			pwd: [ChangePwd.regular , ChangePwd.hint]
		});						
	};

	/**
	 * 加载密码校验规则
	 */
	ChangePwd.getPwdRegular = function () {
		var ajax = new $ax(Hussar.ctxPath + "/getBackPwd/regular", function(data) {
			ChangePwd.regular = eval(data.regular);
			ChangePwd.hint = data.hint;
			ChangePwd.initValidate();
		}, function(data) {
		});
		ajax.start();
	};

	$(function () { 
		ChangePwd.initButtonEvent();
		ChangePwd.getPwdRegular();
	});
	
});




