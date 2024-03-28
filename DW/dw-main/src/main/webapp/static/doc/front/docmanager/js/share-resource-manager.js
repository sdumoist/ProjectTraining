/**
 * Created by xxy on 2018/9/27.
 */
var chooseFile = [];
var chooseFileType = [];
var chooseFileName = [];
var cutFile = [];
var pathId = [];
var pathName = [];
var key = '';
var count = 0;
var adminFlag;
var dbclickover = true;
layui.use([ 'jquery', 'laytpl', 'layer', 'form' ], function() {
	var $ = layui.jquery, laytpl = layui.laytpl, layer = layui.layer, form = layui.form ;

	$(function() {
		$("input[name='sortType']:checked").parent().addClass("sortType-checked");
		// 初始化树
		initTree();
		initEvent();
		form.render();
	});
	// 从预览页面定位到目录
	function initLevel() {
		var nextId = getQueryString("nextId");
		var easyId = getQueryString("easyId");
		if (nextId != null && easyId != null) {
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

                /*$.ajax({
                    async : false,
                    type : "post",
                    url : Hussar.ctxPath+"/preview/getFoldPath",
                    data : {
                        docId : easyId
                    },
                    success : function(data) {
                        if (!!data) {
                            for (var i = 1; i < data.length; i++) {
                                if (data[i].foldId.length > nextId.length) {
                                    break;
                                }
                                openFileId = data[i].foldId;
                                categoryId = data[i].foldId;
                                pathId.push(data[i].foldId);
                                pathName.push(data[i].foldName);
                                refreshFile(data[i].foldId)
                            }
                            createPath();
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/preview/getFoldPath", function(data) {
                    if (!!data) {
                        for (var i = 1; i < data.length; i++) {
                            if (data[i].foldId.length > nextId.length) {
                                break;
                            }
                            openFileId = data[i].foldId;
                            categoryId = data[i].foldId;
                            pathId.push(data[i].foldId);
                            pathName.push(data[i].foldName);
                            refreshFile(data[i].foldId)
                        }
                        createPath();
                    }
                }, function(data) {

                });
                ajax.set("docId",easyId);
                ajax.start();
            });
		}
	}
	function initEvent() {
		// 类型查询
		$("#selectType").click(function() {
			refreshFile(openFileId);
		});
		// 排序查询
		$("#orderType li").change(function() {
			$("input[name='sortType']").parent().removeClass("sortType-checked");
			$("input[name='sortType']:checked").parent().addClass("sortType-checked");
			refreshFile(openFileId);
		})
		// 返回上级
		$("#upLevel").on('click', function() {
			if (pathId.length == 1) {
				return;
			}
			var index = layer.load(1, {
				shade : [ 0.1, '#fff' ]
			// 0.1透明度的白色背景
			});
			refreshFile(pathId[pathId.length - 2]);
			pathName.pop();
			pathId.pop();
			createPath();
			layer.close(index);
		});
		// 目录查找
		$("#searchInResultBtn").on('click', function() {
			var index = layer.load(1, {
				shade : [ 0.1, '#fff' ]
			// 0.1透明度的白色背景
			});
			refreshFile(openFileId, null, null, "1");
			layer.close(index);
		});

		/*
		 * $(".layui-unselect").click(function () { refreshFile(openFileId); });
		 */
	}
	/**
	 * 加载目录树
	 */
	function initTree() {
		var $tree = $("#fileTree");
		$(".pims_tree").height($(".content").height());
		var initId = $("#initId").val();
		var initName = $("#initName").val();
		$tree.jstree({
			core : {
				check_callback : true,
				data : {
					"url" : Hussar.ctxPath+"/frontFolder/getTreeDataLazy?lazy",
					"data" : function(node) {
						return {
							"id" : node.id,
							"type" : "0"
						}
					}
				}
			},

		/*	"contextmenu" : {
				items : {
					"分享" : {
						"label" : "分享",
						"icon" : "glyphicon glyphicon-share",
						"action" : function(data) {
							var inst = $.jstree.reference(data.reference), obj = inst.get_node(data.reference);
							var shareId = obj.original.id;
							var shareType = "folder";
							var sharePid = obj.original.pid;
							addShareInfo(shareType, shareId, sharePid);
						}
					}
				}
			},*/
			types : {
				"closed" : {
					"icon" : "/static/resources/img/fsfile/treeFile.png",
				},
				"default" : {
					"icon" : "/static/resources/img/fsfile/treeFile.png",
				},
				"opened" : {
					"icon" : "/static/resources/img/fsfile/openFile.png",
				},
			},
			plugins : [ 'state', 'types', "themes", "html_data"/*, "contextmenu"*/ ],
		});
		$tree.jstree().hide_dots();
		$tree.bind("activate_node.jstree", function(obj, e) {
			// 处理代码
			// 获取当前节点
			var currentNode = e.node;
			var parent = currentNode.parent;
			openFileId = currentNode.id;
			categoryId = currentNode.id;
			refreshFile(currentNode.id);

			emptyChoose();
			var paramId = [];
			var paramName = [];
			if (currentNode.parent == '#') {
				pathId = [];
				pathName = [];
				pathId.push(currentNode.id)
				pathName.push(currentNode.text)
				createPath();
				return;
			}
			$('#path').empty();
			pathId = [];
			pathName = [];
			paramId.push(currentNode.id);
			paramName.push(currentNode.text);
			do {// 2、判断循环条件;
				currentNode = $('#fileTree').jstree("get_node", currentNode.parent);
				paramId.push(currentNode.id);
				paramName.push(currentNode.text);
			} while (!!currentNode && currentNode.parent != '#')
			for (var i = 0; i < paramId.length; i++) {
				pathId.push(paramId[paramId.length - 1 - i]);
				pathName.push(paramName[paramId.length - 1 - i]);
			}
			createPath();
		});
		$tree.bind("open_node.jstree", function(e, data) {
			data.instance.set_type(data.node, 'opened');
		});
		$tree.bind("close_node.jstree", function(e, data) {
			data.instance.set_type(data.node, 'closed');
		});
		$tree.bind("loaded.jstree", function(event, data) {

			data.instance.clear_state(); // <<< 这句清除jstree保存的选中状态
		});

		if (initId != null && initId != undefined && initId != '') {
			$tree.bind("ready.jstree", function(e, data) {
				$tree.jstree('activate_node', initId);
				initLevel();
			});
		}
		if (initId != null && initId != undefined && initId != '') {
			openFileId = initId;
			categoryId = initId;
			getChildren(initId, initName);
		} else {
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

                /*$.ajax({
                    type : "POST",
                    url : Hussar.ctxPath+"/frontFile/getRoot",
                    contentType : "application/x-www-form-urlencoded",
                    dataType : "json",
                    async : false,
                    success : function(result) {
                        openFileId = result.root;
                        categoryId = result.root;
                        getChildren(result.root, result.rootName);
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/frontFile/getRoot", function(result) {
                    openFileId = result.root;
                    categoryId = result.root;
                    getChildren(result.root, result.rootName);
                }, function(data) {

                });
                ajax.start();
            });
		}

	}
	function refreshTree() {
		var $tree = $("#fileTree");
		$tree.jstree(true).refresh();
		$(".pims_tree").height($(".content").height());
	}
	
	/**
	 * 新增分享信息
	 * 
	 * @param shareType
	 *            分享类别
	 * @param shareId
	 *            分享ID
	 * @param sharePid
	 *            父级Id
	 * @returns
	 */
	function addShareInfo(shareType, shareId, sharePid) {
		$("#sharelink").val("http://192.168.21.73:8080/sharefile/fileView?id=pdf");
		layer.open({
			type : 1,
			area : [ '500px', '200px' ],
			fix : false, // 不固定
			maxmin : true,
			shadeClose : true,
			shade : 0.4,
			title : "分享链接",
			content : $("#sharelinkbody"),
			btn : [ '复制链接', '关闭' ],
			btn1 : function(index, layero) {
				var clipboard = new ClipboardJS('.layui-layer-btn0', {
					text : function() {
						return $("#sharelink").val();
					}
				});
				clipboard.on('success', function(e) {
					clipboard.destroy();
					layer.alert("复制链接成功！");
					layer.close(index);
				});

				clipboard.on('error', function(e) {
					layer.alert("复制链接失败！");
				});
			},
			btn2 : function(index, layero) {
				layer.close(index);
			}
		});

	}
});
function getQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	var reg_rewrite = new RegExp("(^|/)" + name + "/([^/]*)(/|$)", "i");
	var r = window.location.search.substr(1).match(reg);
	var q = window.location.pathname.substr(1).match(reg_rewrite);
	if (r != null) {
		return unescape(r[2]);
	} else if (q != null) {
		return unescape(q[2]);
	} else {
		return null;
	}
}
function createPageContext() {
	$('#pagelist').extendPagination({
		totalCount : count,
		showPage : 10,
		limit : 10,
		callback : function(curr, limit, totalCount) {
			refreshFile(id, obj.curr, obj.limit);
		}
	})
}
function clickPath(id) {
	while (pathId.indexOf(id) + 1 != pathId.length) {
		pathId.pop();
		pathName.pop();
	}
	refreshFile(id);
	createPath();
}
function createPath() {
	var innerlength = 0;
	$("#path").html("");
	$("span#path").css({
		"transform" : "translateX(0)"
	});
	$("span#path").width(innerlength);

	for (var i = 0; i < pathId.length; i++) {
		if (i == pathId.length - 1) {
			var param = '<span>' + pathName[i] + '</span>';
		} else {
			var param = '<span><a class="path-item" onclick="clickPath(\'' + pathId[i] + '\')">' + pathName[i] + '</a><i class="layui-icon">&#xe602;</i></span>';
		}
		$("#path").append(param);
	}
	setTimeout(function() {

		var list = $("#path>span");
		innerlength = 0;
		for (var m = 0; m < (list.length); m++) {
			innerlength = innerlength + Math.ceil(list.eq(m).width() + .5);
		}
		$("span#path").width(innerlength);
		var outWidth = $(".outer-nav").width();
		// 当目录长度超出显示范围，默认只显示可以显示的最后
		if (innerlength > outWidth) {
			$(".control-btn-l").show();
			$(".control-btn-r").hide();
			var subLength = innerlength - outWidth;
			$("span#path").css({
				"transform" : "translateX(-" + subLength + "px)"
			});
			// 获取当前偏移量

			$(".control-btn-l").click(function() {
				var subLength = $("span#path").width() - $(".outer-nav").width();
				var subLength_1 = -$("span#path").css("transform").replace(/[^0-9\-,]/g, '').split(',')[4];
				$(".control-btn-r").show();
				subLength_1 = subLength_1 - outWidth;
				if (subLength_1 > outWidth) {
					$("span#path").css({
						"transform" : "translateX(-" + subLength_1 + "px)"
					});
				} else {
					$("span#path").css({
						"transform" : "translateX(0)"
					});
					$(".control-btn-l").hide();
				}

			});
			$(".control-btn-r").click(function() {
				var subLength = $("span#path").width() - $(".outer-nav").width();
				$(".control-btn-l").show();
				var subLength_2 = -$("span#path").css("transform").replace(/[^0-9\-,]/g, '').split(',')[4];
				subLength_2 = subLength_2 + outWidth;
				if (subLength_2 > subLength) {
					$("span#path").css({
						"transform" : "translateX(-" + subLength + "px)"
					});
					$(".control-btn-r").hide();
				} else {
					$("span#path").css({
						"transform" : "translateX(-" + subLength_2 + "px)"
					});
				}
			})
		} else {
			$("span#path").css({
				"transform" : "translateX(0)"
			});
			$(".control-btn-l").hide();
			$(".control-btn-r").hide();
		}
	}, 100)
}
function drawFile(param) {
	layui.use('laytpl', function() {
		var laytpl = layui.laytpl;
		var data = { // 数据
			"list" : param,
			"adminFlag" : adminFlag
		}
		var getTpl = $("#demo").html(), view = document.getElementById('view');
		laytpl(getTpl).render(data, function(html) {

			if (html.indexOf("li") > -1) {
				$("#laypageAre").children().show();
			} else {
				$("#laypageAre").children().hide();
			}
			view.innerHTML = html;
		});
	});

}
function getChildren(id, name) {
	pathId.push(id);
	pathName.push(name);
	createPath();
	refreshFile(id);
}
function addOper(parent, node) {
	$("#fileTree").jstree("deselect_all", true);
	var ref = $('#fileTree').jstree(true);
	ref.open_node(node);
	var id = ref.get_node(node + '_anchor');
	if (id) {
		ref.select_node(id);
	} else {
		ref.select_node(id);
	}
}
function refreshFile(id, num, size, nameFlag) {
	layui.use([ 'laypage', 'layer' ], function() {
		var laypage = layui.laypage, layer = layui.layer;
		var form = layui.form;
		var fileType = $("input[name='fileType']:checked").val();
		form.on('radio(fileType)', function(data) {
			fileType = data.value;
		});
		var orderType = $("input[name='sortType']:checked").val(); // 排序类型
		var name = $('#searchInResult').val();
		addOper(openFileId, id);
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

            /*$.ajax({
                type : "post",
                url : Hussar.ctxPath+"/frontFile/getChildren",
                data : {
                    id : id,
                    pageNumber : num,
                    pageSize : size,
                    type : fileType,
                    order : orderType,
                    name : name,
                    nameFlag : nameFlag,
                    operateType : "0"
                },
                async : false,
                cache : false,
                dataType : "json",
                success : function(data) {
                    laypage.render({
                        elem : 'laypageAre',
                        count : data.total // 数据总数，从服务端得到
                        ,
                        limit : 20,
                        layout : [ 'prev', 'page', 'next' ],
                        curr : num || 1 // 当前页
                        ,
                        jump : function(obj, first) {
                            // obj包含了当前分页的所有参数，比如：
                            // obj.curr得到当前页，以便向服务端请求对应页的数据。
                            // obj.limit得到每页显示的条数
                            // 首次不执行
                            if (!first) {
                                refreshFile(id, obj.curr, obj.limit)
                            }
                        }
                    });
                    adminFlag = data.isAdmin;
                    count = data.total;
                    drawFile(data.rows);
                    openFileId = id;
                    categoryId = id;
                    emptyChoose();
                    var flag = false;
                    dbclickover = true;
                    var fileIds = [];
                    for (var i = 0; i < data.rows.length; i++) {
                        if (data.rows[i].fileType != 'folder') {
                            flag = true;
                            fileIds.push(data.rows[i].fileId)
                        }
                    }
                    $('#amount').html(data.amount);
                    var idStr = fileIds.join(",")
                    $.ajax({
                        type : "post",
                        url : Hussar.ctxPath+"/fsFile/getInfo",
                        data : {
                            ids : idStr
                        },
                        async : false,
                        cache : false,
                        dataType : "json",
                        success : function(data) {
                            for (var i = 0; i < data.length; i++) {
                                $('#downNum' + data[i].fileId).html(data[i].downNum);
                                $('#readNum' + data[i].fileId).html(data[i].readNum);
                                $('#person' + data[i].fileId).html(data[i].name);
                            }

                        }
                    });
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/frontFile/getChildren", function(data) {
                laypage.render({
                    elem : 'laypageAre',
                    count : data.total // 数据总数，从服务端得到
                    ,
                    limit : 20,
                    layout : [ 'prev', 'page', 'next' ],
                    curr : num || 1 // 当前页
                    ,
                    jump : function(obj, first) {
                        // obj包含了当前分页的所有参数，比如：
                        // obj.curr得到当前页，以便向服务端请求对应页的数据。
                        // obj.limit得到每页显示的条数
                        // 首次不执行
                        if (!first) {
                            refreshFile(id, obj.curr, obj.limit)
                        }
                    }
                });
                adminFlag = data.isAdmin;
                count = data.total;
                drawFile(data.rows);
                openFileId = id;
                categoryId = id;
                emptyChoose();
                var flag = false;
                dbclickover = true;
                var fileIds = [];
                for (var i = 0; i < data.rows.length; i++) {
                    if (data.rows[i].fileType != 'folder') {
                        flag = true;
                        fileIds.push(data.rows[i].fileId)
                    }
                }
                $('#amount').html(data.amount);
                var idStr = fileIds.join(",")
                /*$.ajax({
                    type : "post",
                    url : Hussar.ctxPath+"/fsFile/getInfo",
                    data : {
                        ids : idStr
                    },
                    async : false,
                    cache : false,
                    dataType : "json",
                    success : function(data) {
                        for (var i = 0; i < data.length; i++) {
                            $('#downNum' + data[i].fileId).html(data[i].downNum);
                            $('#readNum' + data[i].fileId).html(data[i].readNum);
                            $('#person' + data[i].fileId).html(data[i].name);
                        }

                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/fsFile/getInfo", function(data) {
                    for (var i = 0; i < data.length; i++) {
                        $('#downNum' + data[i].fileId).html(data[i].downNum);
                        $('#readNum' + data[i].fileId).html(data[i].readNum);
                        $('#person' + data[i].fileId).html(data[i].name);
                    }
                }, function(data) {

                });
                ajax.set("ids",idStr);
                ajax.start();
            }, function(data) {

            });
            ajax.set("id",id);
            ajax.set("pageNumber",num);
            ajax.set("pageSize",size);
            ajax.set("type",fileType);
            ajax.set("order",orderType);
            ajax.set("name",name);
            ajax.set("nameFlag",nameFlag);
            ajax.set("operateType","0");
            ajax.start();
        });
	});

}
function dbclick(id, type, name) {
	if (dbclickover == true) {
		dbclickover = false;
		if (type == "folder") {
			pathId.push(id);
			pathName.push(name);
			createPath();
			refreshFile(id);
		} else {
			showPdf(id, type, name);
		}
	}
}
function download(id, name) {
	/*
	 * $.ajaxFileUpload({ url: "/files/fileDownNew", type: "post", data: {
	 * docName: name, fileIds: id, }, success:function(){
	 * refreshFile(openFileId); } });
	 */
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew",
            type : "post",
            data : {
                docName : "",// name,
                docIds : id,
            }
        });
    });
}

function clickOneTime(e, id, type, name) {
	var jq = $(e);
	if (key == 1) {
		if (chooseFile.indexOf(id) != -1) {
			jq.removeClass("active");
			chooseFile = chooseFile.del(chooseFile.indexOf(id));
			chooseFileType = chooseFileType.del(chooseFile.indexOf(id));
			chooseFileName = chooseFileName.del(chooseFile.indexOf(id));
		} else {
			jq.addClass("active");
			chooseFile.push(id);
			chooseFileType.push(type);
			chooseFileName.push(name);
		}
	} else {
		$('.file').removeClass("active");
		// refreshFile(openFileId);
		emptyChoose();
		jq.addClass("active");
		chooseFile.push(id);
		chooseFileType.push(type);
		chooseFileName.push(name);
	}
}

Array.prototype.del = function(n) {
	if (n < 0)// 如果n<0，则不进行任何操作。
		return this;
	else
		return this.slice(0, n).concat(this.slice(n + 1, this.length));
}

$(document).keydown(function(e) {
	if (e.ctrlKey) {
		key = 1;
	} else if (e.shiftKey) {
		key = 2;
	}
}).keyup(function() {
	key = 0;
});

function emptyChoose() {
	chooseFile = [];
	chooseFileType = [];
	chooseFileName = [];
}
function openWin(url) {
	var a = document.createElement("a"); //创建a标签
	a.setAttribute("href", url);
	a.setAttribute("target", "_blank");
	document.body.appendChild(a);
	a.click(); //执行当前对象
}
function showPdf(id, fileSuffixName, name) {
	var keyword = name;
	dbclickover = true;
	if (fileSuffixName == ".png" || fileSuffixName == ".jpg" || fileSuffixName == ".gif" || fileSuffixName == ".bmp" || fileSuffixName == ".psd" || fileSuffixName == ".jpeg") {
		openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
	} else if (fileSuffixName == ".mp4" || fileSuffixName == ".wmv") {
		openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
	} else if (fileSuffixName == ".mp3" || fileSuffixName == ".m4a") {
		openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
	} else if (isPDFShow(fileSuffixName)) {
		openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
	} else {
        openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id);
	}
}
$('#searchInResult').bind('keypress', function(event) {
	if (event.keyCode == "13") {
		$("#searchInResultBtn").click();
	}
});
function isPDFShow(fileSuffixName) {
	return fileSuffixName == ".pdf" || fileSuffixName == ".doc" || fileSuffixName == ".docx" || fileSuffixName == ".dot" || fileSuffixName == ".wps" || fileSuffixName == ".wpt"
			|| fileSuffixName == ".xls" || fileSuffixName == ".xlsx" || fileSuffixName == ".xlt" || fileSuffixName == ".et" || fileSuffixName == ".ett" || fileSuffixName == ".ppt"
			|| fileSuffixName == ".pptx" || fileSuffixName == ".ppts" || fileSuffixName == ".pot" || fileSuffixName == ".dps" || fileSuffixName == ".dpt"
			|| fileSuffixName == ".txt" || fileSuffixName == ".ceb";
}
var openFlag = false;
// 控制目录显示
$(".mlBtn").click(function() {
	openFlag = !openFlag;
	if (openFlag) {
		$(this).text("收起目录");
	} else {
		$(this).text("展开目录");
	}
	$(".con-full-l").toggleClass("open");
});

$(document).click(function(event) {
	var _con = $('.con-full-l'); // 设置目标区域
	var _con2 = $('.mlBtn'); // 设置目标区域
	if (!_con.is(event.target) && _con.has(event.target).length === 0 && !_con2.is(event.target) && _con2.has(event.target).length === 0) { // Mark
		// 1

		$(".con-full-l").removeClass("open");
		_con2.text("展开目录");
		openFlag = !openFlag;
	}
});

/**
 * 文件新增分享
 * @returns
 */
function sharelink(fileId,fileType,fileName) {
	var shareLink ="";
	if (fileType == ".png" || fileType == ".jpg" || fileType == ".gif" || fileType == ".bmp" || fileType == ".psd" || fileType == ".jpeg") {
		layer.msg("此文件类型不支持预览。");
		return;
	} else if (fileType == ".mp4" || fileType == ".wmv") {
		shareLink = "http://192.168.21.73:8080/sharefile/fileView?type=mp4&id=" +fileId;
	} else if (fileType == ".mp3" || fileType == ".m4a") {
		layer.msg("此文件类型不支持分享。");
		return;
	} else if (isPDFShow(fileType)) {
		shareLink = "http://192.168.21.73:8080/sharefile/fileView?type=pdf&id=" +fileId;
	} else {
		layer.msg("此文件类型不支持分享。");
		return;
	}
	
	$("#sharelink").val(shareLink);
	layer.open({
		type : 1,
		area : [ '500px', '200px' ],
		fix : false, // 不固定
		maxmin : true,
		shadeClose : true,
		shade : 0.4,
		title : "分享链接",
		content : $("#sharelinkbody"),
		btn : [ '复制链接', '关闭' ],
		btn1 : function(index, layero) {
			var clipboard = new ClipboardJS('.layui-layer-btn0', {
				text : function() {
					return $("#sharelink").val();
				}
			});
			clipboard.on('success', function(e) {
				clipboard.destroy();
				layer.alert("复制链接成功！");
				layer.close(index);
			});

			clipboard.on('error', function(e) {
				layer.alert("复制链接失败！");
			});
		},
		btn2 : function(index, layero) {
			layer.close(index);
		}
	});
	
}