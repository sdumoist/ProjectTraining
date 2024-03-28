var tableIns;//表格
    var uploadData;//上传的内容
    var files;//上传的批量文件
    var oldData;//表格的缓存数据
    var classifyCode;//分类编码
    var treeData;
    var userId = "";
    var userName = "";
    var authorIdSnap ="";//作者ID临时
    var authorNameSnap ="";//作者名字临时
    var contactsIdSnap ="";//联系人ID临时
    var contactsNameSnap ="";//联系人名字临时
    var levelId;//保密等级
    var downloadAble;//是否允许下载
    var groupId=[];
    var personId=[];
    var personParam = [];
    var groupParam = [];
    var editFlag = false;
    var exts = "";
    layui.extend({
        admin: '{/}../../../static/resources/weadmin/static/js/admin'
    });
    layui.use(['form', 'jquery','Hussar','jstree','HussarAjax','util','admin', 'layer','upload','table'], function() {
        var form = layui.form,
        Hussar = layui.Hussar,
        jstree=layui.jstree,
            $ = layui.jquery,
            util = layui.util,
            admin = layui.admin,
            upload = layui.upload,
            table = layui.table,
            $ax=layui.HussarAjax,
            layer = layui.layer;
        getUserTree();
        getLoginUser();
        setOptionValues();
        tableIns = table.render({
            elem: '#demoList' //指定原始表格元素选择器（推荐id选择器）
            ,height:160
            ,done:function(res) {
                $("[data-field='id']").hide();
                //.假设你的表格指定的 id="docList"，找到框架渲染的表格
                var tbl = $('#demoList').next('.layui-table-view');
                //.记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
                pageData = res.data;
                var len = pageData.length;
                //.遍历当前页数据，对比已选中项中的 id
                for (var i = 0; i < len; i++) {
                    if (layui.data('checked', pageData[i]['id'])) {
                        //.选中它，目前版本没有任何与数据或表格 id 相关的标识，不太好搞，土办法选择它吧
                        tbl.find('table>tbody>tr').eq(i).find('td').eq(0).find('input[type=checkbox]').prop('checked', true);
                    }
                }
                table.render('checkbox');
                //.PS：table 中点击选择后会记录到 table.cache，没暴露出来，也不能 mytbl.renderForm('checkbox');
                // ;
            }
            ,request: {

            }
            ,page: false //关闭分页
            ,limit:1000
            ,cols: [[
                {field:'id', fixed: 'left'},
                // {title:'序号',type:'numbers',width:'5%',align:'center', fixed: 'left'},
                {field:'docName',title:'文件名',width:'40%',align:"left", fixed: 'left'},
                // {field:'status',title:'状态',width:'15%',align:"center"},
                // {field:'size',title:'大小',align:'center',width:'15%',style:'display:none;'},
                {field:'title',title:'标题',align:'left',width:'40%',event: 'setTitle'},
                //{field:'brief',title:'描述',align:'left',width:'35%',event:'setBrief'},

                // {field:'contacts',title:'联系人',align:'center',width:'15%',event:'setContacts',style:'display:none;'},
                {field: 'option', title: '操作', align: "center", toolbar: '#barDemo',width:'20%',padding:0},
                {field:'contactsId',style:'display:none;'},
                {field:'authorId',style:'display:none;'}
             //   {field:'allowDownload',title:'是否允许下载',align:'center',width:'15%',templet: '#checkboxTpl', unresize: true},
                
            ]] //设置表头
        });
        tableIns.reload({
            data : []
        });
        $(function () {
        	
        	$.ajax({
                 url:"/docconfig/getConfigValueByKey",
                 async:false,
                 cache:false,
                 data:{
                	 	configKey:"fileValidType"
                 },
                 dataType:"json",
                 success:function(data){
                	 exts = data.replace(/,/g,"|");
                 }
             });
        })
        /*关闭弹窗*/
        $("#cancel").on('click',function(){
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        });
        $("#testListAction").on('click',function(){
            oldData = table.cache["demoList"];//获取表格中的缓存
            if(oldData.length <=0){
                layer.msg("请先选择文件", {anim:6,icon: 0});
                return false;
            }
        });
        form.on('radio(visible)', function (data) {
            if (data.value == "0"){
                $('#setAuthority').addClass('layui-btn-disabled').attr('disabled',"true");
                $('.name-list').hide();
            }else {
                $('#setAuthority').removeClass('layui-btn-disabled').removeAttr('disabled',"false");
                $('.name-list').show();
            }
            form.render();
        });
        form.on('checkbox(watermark_company_isChecked)', function(obj){
            var check = $("input[name='watermark_company_isChecked']:checked").val()
            if (check!= undefined&&check=='on') {
                $('#watermark_company').removeClass('layui-disabled').removeAttr('disabled',"false");
            } else {
                $('#watermark_company').addClass('layui-disabled').attr('disabled',"true");
            }
            form.render();
        });
        //监听工具条
        table.on('tool(test)', function (obj) {
            var data = obj.data;
            if (obj.event == 'delete') {
                delete files[data.id]; //删除对应的文件
                uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                //var oldData = table.cache["demoList"];//获取表格中的缓存
                for(var i = 0;i < oldData.length;i++){
                    if(oldData[i].id == data.id){
                        oldData.splice(i,1)
                    }
                }
                tableIns.reload({
                    data : oldData
                });
            }else if(obj.event == 'setTitle'){
                layer.prompt({
                    formType: 2
                    ,title: '编辑标题'
                    ,value: data.title
                    ,maxlength: 30
                ,yes: function(index,layero){
                	var value = layero.find(".layui-layer-input").val();
                	if(value.trim().length == 0){
                        layer.msg("标题不能为空", {anim:6,icon: 0});
                        return;
                    }
                	if(value.trim().length > 40){
                        layer.msg("标题不能超过40个字符", {anim:6,icon: 0});
                        return;
                    }
                    if(!new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{1,255}$").test(value)){
                        layer.msg("标题不能有特殊字符", {anim:6,icon: 0});
                        return
                    }
                    layer.close(index);
                    //这里一般是发送修改的Ajax请求
                    //同步更新表格和缓存对应的值
                    obj.update({
                        title: value.trim()
                    });
                }});
            }else if(obj.event == 'setBrief'){
                layer.prompt({
                    formType: 2
                    ,title: '编辑描述'
                    ,value: data.brief
                    ,maxlength: 500
                    ,yes: function(index, layero){
                    var value = layero.find(".layui-layer-input").val();
                	if(value.trim().length == 0){
                        layer.msg("描述不能为空", {anim:6,icon: 0});
                        return;
                    }
                	if(value.trim().length > 500){
                        layer.msg("描述不能超过500个字符", {anim:6,icon: 0});
                        return;
                    }
                    layer.close(index);
                    //这里一般是发送修改的Ajax请求
                    //同步更新表格和缓存对应的值
                    obj.update({
                        brief: value.trim()
                    });
                }});
            }else if(obj.event == 'setAuthor'){
                layer.open({
                  type: 1,
                  area: ['350px','400px'],
                  fix: false, //不固定
                  maxmin: true,
                  shadeClose: false,
                  shade: 0.4,
                  title: "作者",
                  btn: [ '关闭','确定']
                  ,btn1: function(index, layero){
                	  if(authorIdSnap!=""){
                		  obj.update({
                              authorId: authorIdSnap,
                              author: authorNameSnap
                          });  
                	  } else{
                		  layer.msg("请选择作者", {anim:6,icon: 0});
                		  return;
                	  }
                	  layer.close(index); 
                  }
                 /* ,btn2: function(index, layero){
                	  obj.update({
                          authorId: "",
                          author: ""
                      });
                	  layer.close(layer.index); 
                  }*/
                  ,btn3: function(index, layero){
                	  layer.close(layer.index); 
                  },
                  content: $("#authorDiv"),
                  success:function(){
                      initAuthorTree(treeData,obj);
                  }
              });

            }else if(obj.event == 'setContacts'){
                layer.open({
                    type: 1,
                    area: ['350px','400px'],
                    fix: false, //不固定
                    maxmin: true,
                    shadeClose: false,
                    shade: 0.4,
                    title: "联系人",
                    btn: [ '关闭','确定']
	                ,btn1: function(index, layero){
                	if(contactsIdSnap!=""){
                 		  obj.update({
                               contacts: contactsNameSnap,
                           	 contactsId:contactsIdSnap
                           });  
  	               	  } else{
  	            		  layer.msg("请选择联系人", {anim:6,icon: 0});
  	            		  return;
  	            	  }
	              	  layer.close(index); 
	              	 
	                }
	                ,btn3: function(index, layero){
	              	  layer.close(layer.index); 
	                },
                    content: $("#contactsTreeDiv"),
                    success:function(){
                        initContactsTree(treeData,obj);
                    }
                });
            }
        });
       
        //监听允许下载操作
        form.on('checkbox(allowDownload)', function(obj){
            for(var i = 0;i < oldData.length;i++){
                if(oldData[i].id == obj.elem.dataset.id){
                    if(obj.elem.checked){
                        oldData[i].allowDownload = "1";
                        obj.elem.value = '1';
                    }else{
                        oldData[i].allowDownload = "0";
                        obj.elem.value = '0';
                    }

                }
            }
        });
        form.on('select(level)', function(data){
            var level = $("#levelId").val();
            if(level == "1"){
                $("input[name=downloadAble][value='1']")[0].checked = true;
                $("input[name=downloadAble][value='0']")[0].checked = false;
                $('#setAuthority').removeClass('layui-btn-disabled').removeAttr('disabled',"false");
            }else {
                $("input[name=downloadAble][value='1']")[0].checked = false;
                $("input[name=downloadAble][value='0']")[0].checked = true;
                $('#setAuthority').addClass('layui-btn-disabled').attr('disabled',"true");
            }
            form.render();
        });
        $("#setAuthority").click(function(){
            parent.layer.open({
                type: 2,
                title: '选择可见范围',
                area: ['850px', '510px'], //宽高
                fix: false, //不固定
                maxmin: true,
                content: Hussar.ctxPath+'/fsFile/authority',
                success: function(layero, index) {
                }
            });
        });

        function initAuthorTree(data,parentObj) {
        	authorIdSnap = "";
        	authorNameSnap = "";
            var $authortree = $("#showAuthorTree");
            if($authortree){
            	$authortree.jstree("destroy");
            }
            
            $authortree.jstree({
                core: {
                    data: data
                },
                plugins: ['types','search'],
                types:{
                    "1":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/com.png"},
                    "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/dept.png"},
                    "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/station.png"}, 
                    "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/empl.png"},
                    "USER":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/user.png"}
                },
                search:treeSearch("showAuthorTree","authorTreeSearch",parentObj.data.author)
            });
            $authortree.bind('activate_node.jstree', function (obj,e){
            	if(e.node.original.type !='9'&&e.node.original.type !='USER'){
            		layer.alert("请选择人员");
            		return;
            	}
            	authorIdSnap = e.node.original.id;
            	authorNameSnap = e.node.original.text;
            })
        };
        function initContactsTree(data,parentObj) {
        	contactsIdSnap = "";
        	contactsNameSnap = "";
            var $contactstree = $("#showContactsTree");
            if($contactstree){
            	$contactstree.jstree("destroy");
            }
            $contactstree.jstree({
                core: {
                    data: data
                },
                plugins: ['types','search'],
                types:{
                    "1":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/com.png"},
                    "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/dept.png"},
                    "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/station.png"}, 
                    "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/empl.png"},
                    "USER":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/user.png"}
                },
                search:treeSearch("showContactsTree","contactsTreeSearch",parentObj.data.contacts)
            });
            $contactstree.bind('activate_node.jstree', function (obj,e){
            	if(e.node.original.type !='9'&&e.node.original.type !='USER'){
            		layer.alert("请选择人员");
            		return;
            	}
            	contactsIdSnap = e.node.original.id;
            	contactsNameSnap = e.node.original.text;
            })
        }
        function setOptionValues(){
            $.ajax({
                url:"/fsFile/searchLevel",
                async:false,
                cache:false,
                dataType:"json",
                success:function(data){
                    var arr = data.data;
                    var optionContent = $("#levelId").html();
                    for(var i = 0;i < arr.length;i++){
                        optionContent += "<option value='"+arr[i].value+"'>"+arr[i].label+"</option>";
                    }
                    $("#levelId").html(optionContent);
                    $('#levelId').val('1');
                    form.render('select','level');
                }
            });
            
            form.render();
            $("dl").height(150);
            $.ajax({
                url:"/fsFile/downloadAble",
                async:false,
                cache:false,
                dataType:"json",
                success:function(data){
                    var arr = data.data;
                    var optionContent = $("#download").html();
                    for(var i = 0;i < arr.length;i++){
                        if(i==0){
                            optionContent += "&nbsp;&nbsp;&nbsp;&nbsp;<input type='radio' name='downloadAble' lay-filter='downLoadRadio' id='' checked value='"+arr[i].value+"' >"+arr[i].label+" ";
                        }else{
                            optionContent += "&nbsp;&nbsp;&nbsp;&nbsp;<input type='radio' name='downloadAble' lay-filter='downLoadRadio' value='"+arr[i].value+"' >"+arr[i].label+" ";

                        }
                    }
                    $("#download").html(optionContent);
                }
            });
            form.render();
        }
        function getUserTree(){

        }
        function getLoginUser(){
      	  $.ajax({
                type:"post",
                url:"/files/getLoginUser",
                async:true,
                cache:false,
                dataType:"json",
                success:function(result){
                	if(result){
                		userId = result.userId;
                		userName = result.userName;
                	}
                }, error:function(data) {
                      Hussar.error("获取登陆人失败");
                  }
            });
      }
    	/**
    	 * 所有树的模糊查询
    	 */
    	 function treeSearch(treeId,searchId,username) {
    		 $("#"+searchId).val("");
    		 $(".jstree-search").remove();
    		 $(".search-results").html("");
    		var $tree = $("#"+treeId);
    		var to = false;   
    		//用户树查询
    		$("#"+searchId).keyup(function () {   
    		    if (to) { clearTimeout(to); }   
    		    to = setTimeout(function () {   
    		        var v = $("#"+searchId).val();  
    		        if(v==null||v==""){
    		        	v =username;
    		        }
    		        var temp = $tree.is(":hidden");   
    		        if (temp == true) {   
    		        	$tree.show();   
    		        }   
    		        $tree.jstree(true).search(v);
    		        //添加索引
    				if(v!=''){
                        var n = $(".jstree-search").length,con_html;
                        if(n>0){
                            con_html = "<em>"+ n +"</em>个匹配项";
                        }else{
                            con_html = "无匹配项";
                        }
                        $(".search-results").html(con_html);
    				}else {
                        $(".search-results").html("");
    				}

    		    }, 250);   
    		}); 
    		if(username!=null&&username!=""){
        		var e = $.Event("keyup");//模拟一个键盘事件 
        		e.keyCode = 13;//keyCode=13是回车 
        		$("#"+searchId).trigger(e);//模拟页码框按下回车 
    		}
    	}
        uploadListIns = upload.render({
            elem: '#testList'
          //,url: '${ctxPath}/files/uploadOneTest'
            ,url: '/files/uploadData'
            ,method: "post"
            ,accept: 'file'
            ,exts: exts
            ,multiple: true
          //,size: 20 //最大允许上传的文件大小
            ,number:20//上传最大文件数
            ,auto: false
            ,bindAction: '#testListAction'
            ,before: function(obj){
            	ityzl_SHOW_LOAD_LAYER();
                oldData = table.cache["demoList"];//获取表格中的缓存
                for(var i = 0; i< oldData.length;i++){

                 	var nameDoc = oldData[i].docName.trim().lastIndexOf(".");
                    if(oldData[i].title.trim() == ''){
                    	oldData[i].option = '2';
                        layer.msg("标题不能为空", {anim:6,icon: 0});
                        return false;
                    }else if(oldData[i].docName.trim().substring(0,nameDoc).length >40){
                    	oldData[i].option = '2';
                        layer.msg("文件名不能超过40个字", {anim:6,icon: 0});
                        return false;
                    }
                }
                uploadData = JSON.stringify(oldData);

                var isDown = $("input[name='down']:checked").val();
                if(isDown!= undefined&&isDown=='on'){
                    isDown='1'
                }else {
                    isDown = '0';
                }
                var visible = $("input[name='visible']:checked").val();

                //是否添加水印
                var watermark_user = $("input[name='watermark_user']:checked").val();

                if(watermark_user!= undefined&&watermark_user=='on'){
                    watermark_user='1'
                }else {
                    watermark_user = '0';
                }

                var watermark_company = $("#watermark_company").val();      //公司水印内容
                var watermark_company_isChecked = $("input[name='watermark_company_isChecked']:checked").val();
                if (watermark_company_isChecked!= undefined&&watermark_company_isChecked=='on') {
                    if ( watermark_company.trim().length == 0 || watermark_company.trim().length > 5 ) {
                        layer.msg("为了不影响浏览效果，请输入五字以内的公司名称！", {anim:6,icon: 0});
                        return;
                    }
                } else {
                    if ( watermark_company.trim().length > 5 ) {
                        layer.msg("为了不影响浏览效果，请输入五字以内的公司名称！", {anim:6,icon: 0});
                        return;
                    }
                }
                var groupId=parent.groupId;
                var personId=parent.personId;
                if(visible == '1'){
                    if(groupId.length==0&&personId.length==0){
                        layer.msg("请给保密文档设置权限", {anim:6,icon: 0});
                        return;
                    }
                }
                if(visible == '1'){
                    var group = [];
                    var person = [];
                    var groupStr = '';
                    var personStr ='';

                    if (groupId!=undefined){
                        for (var i = 0; i < groupId.length; i++) {
                            group.push(groupId[i].id);
                        }
                        var groupStr = group.join(",")
                    }
                    if (personId!=undefined){
                        for (var i = 0; i < personId.length; i++) {
                            person.push(personId[i].id);
                        }
                        var personStr = person.join(",")
                    }
                }
                var flag = true;
                $.ajax({
                    type:"post",
                    url:/*Hussar.ctxPath+*/"/files/checkFileExist",
                    data:{
                        uploadData:uploadData,
                        pid:parent.categoryId
                    },
                    async:false,
                    cache:false,
                    dataType:"json",
                    success:function(data){
                        if(data.result.length > 0){
                            flag = false;
                            var nameStr = '';
                            for(var i = 0;i < data.result.length;i++){
                                if(i == 0){
                                    nameStr += data.result[i];
                                }else{
                                    nameStr += (","+data.result[i]);
                                }

                            }
                            layer.msg("”"+nameStr+"“文件已存在", {anim:6,icon: 0});
                        }
                    }
                });
                if(!flag){
                    return false;
                }
                this.data = {
                		
					                    uploadData:uploadData,
					                    foldId:parent.categoryId,
					                    //categoryCode:parent.categoryCode,
					                    //classifyCode:classifyCode,
					                    visible :visible,
					                    downloadAble:isDown,
					                    watermarkUser:watermark_user,
					                    watermarkCompany:watermark_company,
					                    group:groupStr,
					                    person:personStr
				                    
                };
            }
            ,choose: function(obj){
                uploadListIns.config.elem.next()[0].value = '';
                files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
                //读取本地文件
                obj.preview(function(index, file, result){
                    oldData = table.cache["demoList"];//获取表格中的缓存
                    if(oldData == undefined){
                        oldData = [];
                    }else{
                        for(var i = 0;i<oldData.length;i++){
                            if(oldData[i].docName == file.name){
                            	delete files[index];
                            	delete this.files[index];
                            	return;
                            }
                        }
                    }
                    var typeIndex = file.name.lastIndexOf(".");
                    var trData =
                        {"id":index,"docName":file.name,"size":(file.size/1024).toFixed(1)+"KB","status":"等待上传","title":file.name.substring(0,typeIndex),
                    		"brief":"","author":userName,
                    		"authorId":userId,"contacts":userName,"contactsId":userId/*,"allowDownload":"0"*/};//新增行的内容
                    oldData.push(trData);
                    tableIns.reload({
                        data : oldData
                    });
                });
            }
            ,done: function(res, index, upload){
            	//上传文件内容为空
            	if(res.code == 1){
            		for(var i = 0;i < oldData.length;i++){
                        if(oldData[i].id == index){
                        	 oldData[i].status = "不允许上传";
                             oldData[i].option = '2';
                             break;
                        }
                    }
        		    tableIns.reload({
        		    	data : oldData
                    });
        		    return false;
            	}
            	
            	//上传成功
                if(res.code == 0){ 
                    for(var i = 0;i < oldData.length;i++){
                        if(oldData[i].id == index){
                            oldData[i].status = "上传成功";
                            oldData[i].option = '1';
                            break;
                        }
                    }
                    tableIns.reload({
                        data : oldData
                    });
                    return delete files[index]; //删除文件队列已经上传成功的文件
                }
                
                //剩余空间不足
                if(res.code == 2){ 
                    tableIns.reload({
                        data : oldData
                    });
                    for(var i = 0;i < oldData.length;i++){
                        if(oldData[i].id == index){
                            oldData[i].status = "上传失败";
                            oldData[i].option = '4';
                            break;
                        }
                    }
                    tableIns.reload({
                        data : oldData
                    });
                    layer.msg("本部门可用存储空间不足", {anim:6,icon: 0});
                    return false
                }
                
                //上传失败
                if(res.code == 3){ 
                    for(var i = 0;i < oldData.length;i++){
                        if(oldData[i].id == index){
                            oldData[i].status = "上传失败";
                            oldData[i].option = '3';
                            break;
                        }
                    }
                    tableIns.reload({
                        data : oldData
                    });
                    return false;
                }

                //this.error(index, upload);
            }
            ,allDone:function(obj){
                for(var i = 0;i < oldData.length;i++){
                    if(oldData[i].option == "3"){
                    	layer.msg("上传失败", {anim:6,icon: 0});
                    	var loading = layer.getFrameIndex(window.name);
                        layer.close(loading);
                    	return false;
                    }
                    if(oldData[i].option == "2" || oldData[i].option == "4"){
                    	return false;
                    }
                }
                if(obj.total = obj.successful){
                    layer.alert('上传成功', {
                        icon :  1,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    },function(){
                        parent.refreshFile(parent.openFileId);
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                    });
                } else {
                	 var loading = layer.getFrameIndex(window.name);
                     layer.close(loading);
                }
            }
            ,error: function(index, upload){
                for(var i = 0;i < oldData.length;i++){
                    if(oldData[i].id == index){
                        oldData[i].status = "上传失败";
                        oldData[i].option = '3';
                    }
                }
                tableIns.reload({
                    data : oldData
                });
                var loading = layer.getFrameIndex(window.name);
                layer.close(loading);
            }
        });
        $(window).resize(function () {
            getUserTree();
            getLoginUser();
            setOptionValues();
            tableIns = table.render({
                elem: '#demoList' //指定原始表格元素选择器（推荐id选择器）
                ,height:160
                ,done:function(res) {
                    $("[data-field='id']").hide();
                    //.假设你的表格指定的 id="docList"，找到框架渲染的表格
                    var tbl = $('#demoList').next('.layui-table-view');
                    //.记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
                    pageData = res.data;
                    var len = pageData.length;
                    //.遍历当前页数据，对比已选中项中的 id
                    for (var i = 0; i < len; i++) {
                        if (layui.data('checked', pageData[i]['id'])) {
                            //.选中它，目前版本没有任何与数据或表格 id 相关的标识，不太好搞，土办法选择它吧
                            tbl.find('table>tbody>tr').eq(i).find('td').eq(0).find('input[type=checkbox]').prop('checked', true);
                        }
                    }
                    table.render('checkbox');
                    //.PS：table 中点击选择后会记录到 table.cache，没暴露出来，也不能 mytbl.renderForm('checkbox');
                    // ;
                }
                ,request: {

                }
                ,page: false //关闭分页
                ,limit:1000
                ,cols: [[
                    {field:'id', fixed: 'left'},
                    // {title:'序号',type:'numbers',width:'5%',align:'center', fixed: 'left'},
                    {field:'docName',title:'文件名',width:'40%',align:"left", fixed: 'left'},
                    // {field:'status',title:'状态',width:'15%',align:"center"},
                    // {field:'size',title:'大小',align:'center',width:'15%',style:'display:none;'},
                    {field:'title',title:'标题',align:'left',width:'40%',event: 'setTitle'},
                    //{field:'brief',title:'描述',align:'left',width:'35%',event:'setBrief'},

                    // {field:'contacts',title:'联系人',align:'center',width:'15%',event:'setContacts',style:'display:none;'},
                    {field: 'option', title: '操作', align: "center", toolbar: '#barDemo',width:'20%',padding:0},
                    {field:'contactsId',style:'display:none;'},
                    {field:'authorId',style:'display:none;'}
                    //   {field:'allowDownload',title:'是否允许下载',align:'center',width:'15%',templet: '#checkboxTpl', unresize: true},

                ]] //设置表头
            });
            tableIns.reload({
                data : []
            });
        });
        function ityzl_SHOW_LOAD_LAYER(){
	   		return layer.msg('上传中...', {icon: 16,shade: [0.5, '#f5f5f5'],scrollbar: false,offset: '0px', time:10000000}) ;
	   	}
    });
