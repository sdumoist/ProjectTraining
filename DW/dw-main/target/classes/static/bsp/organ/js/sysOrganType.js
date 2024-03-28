/**
 * @Description: 定义组织机构类型脚本文件
 * @Author: chenxin
 * @Date: 2018/2/27.
 */
layui.use(['jquery','layer','Hussar','jstree','HussarAjax','form'], function(){

	var Hussar = layui.Hussar;
	var $ = layui.jquery;
	var layer=layui.layer;
	var $ax=layui.HussarAjax;
	var form=layui.form;
    var table1;
    var layerView;

var OrganType = {
	seItem : null, // 选中的条目
	isSystem : null
};
OrganType.initTable = function() {
	layui.use('table', function() {
		var table = layui.table;

        var orgCode = $('#orgCode').val();
        var orgName = $('#orgName').val();
		table.render({
			elem : '#typeTable',
			height:$("body").height() - $(".layui-form").outerHeight(true)-26,
			url : Hussar.ctxPath+'/orgType/list',
			cols : [
				[ {
				type : 'checkbox',
				width : 40
			}, {
				title : '序号',
				type : 'numbers',
				align : 'center',
				width : 50
			}, {
				field : 'organType',
				title : '组织类型代码',
				width : 200,
				align : 'center'
			}, {
				field : 'typeName',
				title : '组织类型名称',
				width : 200,
				align : 'center'
			}, {
				field : 'parentTypeName',
				title : '上级组织类型',
				templet : typeEvts,
				align : 'center'
			}] ],
			page : true,
			id : 'testReload',
			even: true,
			where: {
                code : orgCode,
                name : orgName
			},
			encryptEnable: true //layui table 传输加密
		});

		//上级组织机构类型formater
		function typeEvts(d) {
			if (d.parentType == '0') {
				return '无';
			}
			return d.parentTypeName;
		}

		//自定义校验
		form.verify({
			code : function(value, item){
				if(new RegExp("[\\u4E00-\\u9FFF]+","g").test(value)){
					return '组织类型代码不能有汉字！';
				}
			},
			imgUrl : function(value, item){
				if(value.substring(0,1)!="/"){
					return '图片url请以“/”开头！';
				}
			},
			orgCode : function(value, item){
				if(new RegExp("[`~!@#$^&*()=|{}':;,.<>/?~！@#￥……&*（）——|【】‘；：”“'。，、？%+ 　\"\\\\]").test(value)){
					return '组织类型代码不能有特殊字符！';
				}
			},
			codeLatter : function(value, item){
				if(new RegExp("[A-Za-z]+$").test(value)){
					return '组织类型代码不能有字母！';
				}
			}
		})
		table1 = table;
	});
}

/**
 * 获取上级组织机构类型option
 */
OrganType.queryOption = function() {
	var ajax = new $ax(Hussar.ctxPath + "/orgType/orgTypeOption",
			function(json) {
				$("select[name^='parentType']").html("");
				for (var i = 0; i < json.length; i++) {
					$("select[name^='parentType']").append(
							"<option value='" + json[i].organType + "'>"
									+ json[i].typeName + "</option>");
				}
			}, function(data) {
				Hussar.error(GET_SUPERIOR_ORGANIZATION_TYPE_FAIL);
			});
	ajax.setEncryptEnable(true);
	ajax.start();
};

/**
 * 按钮操作
 */
OrganType.initButton = function() {

	/**
	 * 查询
	 */
	$('#search').on('click', function() {
		var orgCode = $('#orgCode');
		var orgName = $('#orgName');
		// 执行重载
		table1.reload('testReload', {
			page : {
				curr : 1
			// 重新从第 1 页开始
			},
			where : {
				code : orgCode.val(),
				name : orgName.val()
			},
			encryptEnable: true //layui table 传输加密
		});
	});

	/**
	 * 添加
	 */
	$("#add").click(function() {
		$("#status").val("add");
		layerView = layer.open({
			type : 1,
			area : [ '400px', '360px' ],
			title : "组织类型新增",
			//shift : 6,
			maxmin : false,
			content : $("#addEvent"),
			shadeClose:false,
			success : function() {
				// 重置form
				$("[name=orgCode]").removeAttr("disabled");
				$("[name=parentType]").removeAttr("disabled");
				$("[name=imgUrl]").removeAttr("disabled");
				$("[name=imgUrl]").css("background","");
				// 获取上级组织机构类型option
				OrganType.queryOption();
		        $('#addTypeForm')[0].reset();
				form.render();
			}
		});
	});

	/**
	 * 修改
	 */
	$("#edit").click(function() {
		if (OrganType.check()) {
			if(OrganType.seItem.isSystem=="1"){
				Hussar.info(CANNOT_UPDATE_BASIC_TYPE);
				return;
			}
			$("#status").val("edit");
			layerView = layer.open({
				type : 1,
				area : [ '400px', '360px' ],
				title : "组织类型修改",
				maxmin : false,
				shadeClose:false,
				content : $("#addEvent"),
				success : function() {
					// 重置form
                    OrganType.queryOption();
                    var ajax = new $ax(Hussar.ctxPath + "/orgType/selectOrg", function(data) {
                    	$("[name=orgCode]").val(data.organType);
                        $("[name=orgName]").val(data.typeName);
                        $("[name=oldOrgName]").val(data.typeName);
                        $("[name=parentType]").val(data.parentType);
                        $("[name=imgUrl]").val(data.imgUrl);
                        $("[name=imgUrl]").css("background","#f5f5f5");
                        $("[name=orgCode]").attr("disabled", "disabled");
                        $("[name=parentType]").attr("disabled", "disabled");
                        $("[name=imgUrl]").attr("disabled", "disabled");
                        form.render();
        		    }, function(data) {
        		        Hussar.error(SELECT_ORGANIZATION_TYPE_INFO_FAIL);
        		    });

                	ajax.setEncryptEnable(true);
        		    ajax.set("parentTypeCode",OrganType.seItem.organType);
        		    ajax.start();
				}
			});
		}
	});

	/**
	 * 删除
	 */
	$("#del").click(function() {
		if (OrganType.checkIds()) {
			var l=OrganType.isSystem.split(",").length;
			for(var i=0;i<l;i++){
				if(OrganType.isSystem.split(",")[i]=="1"){
					Hussar.info(CANNOT_DELETE_BASIC_TYPE);
					return;
				}
			}
			var operation =function(){
				var ajax = new $ax(Hussar.ctxPath + "/orgType/delByIds",
						function(data) {
					        if(data.code == "200"){
					        	$(".layui-laypage-btn").click();
								//Hussar.success("删除成功！");
                                Hussar.success(data.message);
					        }
							else if (data.code == "500") {
								Hussar.error(data.message);
							}
							else {
								//Hussar.error("删除失败！");
                                Hussar.error(data.message);
							}
						}, function(data) {
								Hussar.error(DELETE_FAIL);
						});
						ajax.set("ids",OrganType.seItem.join());
						ajax.setEncryptEnable(true);
						ajax.start();
						};
			Hussar.confirm(DELETE_SURE, operation);
			}
		});

	/**
	 * 保存
	 */
	$("#btnSave").click(function(){
		form.on('submit(verify)', function(data){
			var type=$("#status").val();
			var code=$("#code").val();
			var orgName=$("[name=orgName]").val();
			var oldOrgName=$("[name=oldOrgName]").val();
			var parentType=$("[name=parentType]").val();
			var imgUrl=$("[name=imgUrl]").val();
			var url;
			if(type=="add"){
				url="/orgType/add";
			}else{
				url="/orgType/edit";
			}

			var ajax = new $ax(Hussar.ctxPath + url, function(data) {
				if (data.code == "200") {
					//Hussar.success("保存成功！");
					Hussar.success(data.message)
					layer.close(layerView);
					$('#search').click();
					$('#addTypeForm')[0].reset();
				} else if (data.code == "500") {
					Hussar.valid(data.message);
				} else {
					//Hussar.error("保存失败！");
                    Hussar.error(data.message);
				}
			}, function(data) {
				Hussar.error(SAVE_FAIL);
			});
			ajax.set("organType",code);
			ajax.set("typeName",orgName);
			ajax.set("oldOrgName",oldOrgName);
			ajax.set("parentType",parentType);
			ajax.set("imgUrl",imgUrl);
			ajax.setEncryptEnable(true);
			ajax.start();

		});
	});

	/**
	 * 关闭
	 */
	$("#btnClose").click(function(){
		layer.close(layerView);
	})
};

/**
 * 选中一条结果
 */
OrganType.check = function() {
	var checkStatus = table1.checkStatus('testReload'), data = checkStatus.data;
	if (data.length == 0 || data.length > 1) {
        Hussar.info(PLEASE_CHOOSE_ONE);
		return false;
	} else {
		OrganType.seItem = data[0];
		return true;
	}
};

/**
 * 选中多条结果
 */
OrganType.checkIds = function() {
	var checkStatus = table1.checkStatus('testReload'), data = checkStatus.data;
	var ids = [];
	var isSystems = [];
	if (data.length == 0) {
		Hussar.info(PLEASE_SELECT_AT_LEAST_ONE);
		return false;
	} else {
		for (var i = 0, l = data.length; i < l; i++) {
			var r = data[i];
			ids.push(r.organType);
			isSystems.push(r.isSystem);
		}
		var isSystem = isSystems.join(',');
		OrganType.seItem = ids;
		OrganType.isSystem = isSystem;
		return true;
	}
};


	/**
	 * 初始化
	 */
	$(function() {
		OrganType.initTable();
		OrganType.initButton();
	});

});
