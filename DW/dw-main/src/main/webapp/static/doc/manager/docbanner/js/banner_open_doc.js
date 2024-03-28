/**
 * Created by smt on 2018/6/30.
 */
var hussar;
var openFileId;   //打开的文件夹的id
var chooseFile = [];    //选中的文件或目录的id
var chooseUploadFile = [];    //选中的文件或目录的id
var chooseUploadAuthor = [];    //选中的文件或目录的id
var chooseFileType = []; //选中的文件或目录的type
var chooseFileAuthor = []; //选中的文件或目录的type
var chooseFileName = []; //选中的文件或目录的name
var cutFile = [];          //剪切的文件或目录的id
var cutFileType = [];      //剪切的文件或目录的type
var cutFileName = [];      //剪切的文件或目录的name
var pathId = [];        //路径
var pathName = [];
var key='';
var adminFlag;
var userId;
var docAddOpen;
var categoryId;
var isChild;
var reNameFlag= false;      //重命名标志
var reNameParem='';
var reNameIndex='';
var clickFlag=false;
var treeData;
var authorIdSnap ="";//作者ID临时
var authorNameSnap ="";//作者名字临时
var contactsIdSnap ="";//联系人ID临时
var contactsNameSnap ="";//联系人名字临时
var groupId=[];
var personId=[];
var personParam = [];
var groupParam = [];
var folderMessage='请选择文件';
var editFlag = false;
var showStyle=2;
var noChildPower=0;
var layerView;
var dbclickover=true;
var tableIns;//表格
var files;//上传的批量文件
var oldData;//表格的缓存数据
var userName = "";
var downloadAble;//是否允许下载
var flag;
var fastFlag;
var percentageFlag;
var powerFlag=0;

var $list = $('#thelist'),//文件列表
    state = 'pending',//初始按钮状态
    uploader; //uploader对象
var fileMd5;  //文件唯一标识
var fileName;//文件名称
var oldJindu;//如果该文件之前上传过 已经上传的进度是多少
var count=0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
var success=0;//上传成功的文件数
var filesArr=new Array();//文件数组：每当有文件被添加进队列的时候 就push到数组中
var map={};//key存储文件id，value存储该文件上传过的进度
var fileId;
var searchFlag=0;
layui.extend({
    admin: '{/}../../../static/resources/weadmin/static/js/admin'
});
layui.use(['form', 'laypage', 'jquery','layer','Hussar','jstree','laytpl'], function() {
    var $ = layui.jquery,
        form=layui.form,
        jstree=layui.jstree,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;
    $(".rit-menus").click(function () {
        var index = $(this).index();
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
        if(1 == index ){
            showStyle=2;
            refreshFile(openFileId)
        }else {
            showStyle=1;
            refreshFile(openFileId)
        }
    });

    //初始化树
    hussar=Hussar.ctxPath;
    start();
    /*form.on('radio(visible)', function (data) {
        if (data.value == "0"){
            $('#setAuthority').addClass('layui-btn-disabled').attr('disabled',"true");
            $('.edit-name-list').hide();
        }else {
            $('#setAuthority').removeClass('layui-btn-disabled').removeAttr('disabled',"false");
            $('.edit-name-list').show();
        }
        form.render();
    });*/
    form.on('radio(visibleEdit)', function (data) {
        if (data.value == "0"){
            $('#setEditAuthority').addClass('layui-btn-disabled').attr('disabled',"true");
            $('.edit-name-list').hide();
            // $('#isEditEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
            // $('#isPe').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
            // $('#isCollect').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
            // $('#isShare').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
            // $('#isPrint').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
        }else {
            $('#setEditAuthority').removeClass('layui-btn-disabled').removeAttr('disabled',"false");
            $('.edit-name-list').show();
            // $('#isEditEdit').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled',"false");
            // $('#isPe').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled',"false");
            // $('#isCollect').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled',"false");
            // $('#isShare').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled',"false");
            // $('#isPrint').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled',"false");
        }
        form.render();
    });






    /*删除目录*/
    $("#delCategoryBtn").on('click',function(){

        for(var i=0;i<chooseFile.length;i++){
            var power= $("#authority"+chooseFile[i]).html()
            if(power!='2'&&noChildPower!=2&&adminFlag!=1){
                layer.msg("您没有权限删除文件", {anim:6,icon: 0});
                return;
            }
            if (chooseFileType[i]=="folder"){
                layer.msg(folderMessage, {anim:6,icon: 0});
                return;
            }
        }
        if(chooseFile.length==0){
            layer.msg("请选择要删除的文件", {anim:6,icon: 0});
            return;
        }

        layer.confirm('确定要删除所选文件吗？',function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'] //0.1透明度的白色背景
            });
            /* if(fileIdArr.length>0){
                 $.ajax({
                     type:"post",
                     url:"/files/deleteFile",
                     data:{
                         ids:fileIdArrStr
                     },
                     async:false,
                     cache:false,
                     success:function(data){
                         delFileFlag = true;
                     },
                     error:function () {
                         delFileFlag = false;
                     }
                 })
             }*/

            for(var i = 0;i<chooseFile.length;i++){
                for(var j = 0;j<cutFile.length;j++){
                    if(cutFile[j] == chooseFile[i]){
                        cutFile = cutFile.del(j);
                        break;
                    }
                }
            }
            if(chooseFile.length==0){
                layer.close(index);
                return;
            }
            var scopeId = chooseFile.join(',')
            $.ajax({
                type:"post",
                url: Hussar.ctxPath+"/fsFile/deleteScope",
                data:{
                    fsFileIds:scopeId,
                },
                async:true,
                cache:false,
                success:function(data){
                    if(data> 0){
                        var fileList = $("#thelist").find(".item");
                        for(var n = 0;n<fileList.length;n++){
                            var name = fileList.eq(n).find(".info").html().split(".")[0];
                            for(var m =0 ;m<chooseFileName.length;m++){
                                if(name == chooseFileName[m]){
                                    fileList.eq(n).remove();
                                    uploader.removeFile( fileList.eq(n).attr("id"),true);
                                }
                            }
                        }
                        Hussar.success('删除成功')
                    }else {
                        Hussar.error('删除异常')
                    }
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                },
                error:function () {
                    Hussar.error('删除异常!')
                    btnState();
                    refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })
        })
    });
    /*上一级目录*/
    $("#upLevel").on('click',function(){
        if(pathId.length==1){
            return;
        }
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        refreshFile(pathId[pathId.length-2]);
        pathName.pop();
        pathId.pop();
        createPath();
        layer.close(index);
    });
    /*搜索按钮*/
    $("#searchBtn").on('click',function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        refreshFile(openFileId,null,null,"1");
        layer.close(index);
    });
    /*剪切*/
    $("#cutFile").on('click',function(){
        for(var i=0;i<chooseFile.length;i++){
            var power= $("#authority"+chooseFile[i]).html()
            if(power!='2'&&noChildPower!=2&&adminFlag!=1){
                layer.msg("您没有权限移动文件", {anim:6,icon: 0});
                return;
            }
            if (chooseFileType[i]=="folder"){
                layer.msg(folderMessage, {anim:6,icon: 0});
                return;
            }

        }

        if(chooseFile.length == 0){
            layer.msg("请选择要移动的目录或文件", {anim:6,icon: 0});
            return;
        }
        var operation =function(){
            layerView=layer.open({
                type : 1,
                area: ['350px','500px'],
                //shift : 1,
                shadeClose: false,
                title : '目录结构',
                maxmin : true,
                content : $("#filTree"),
                success : function() {
                    initFileTree();
                    layer.close(index);
                },
                end: function () {
                    layer.closeAll(index);
                }
            });

        }
        var index = layer.confirm('确定要移动所选文件吗？',operation);
        cutFile=[].concat(chooseFile);
        cutFileType=[].concat(chooseFileType);
        cutFileName=[].concat(chooseFileName);

    });

    initFileTree=function () {
        var $tree = $("#fileTree2");
        $tree.jstree("destroy");    //二次打开时要先销毁树
        $tree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": Hussar.ctxPath+"/fsFile/getMoveTreeDataLazy?lazy",
                    "data": function (node) {
                        return {"id": node.id,"type":"1"
                        };
                    }
                },
                themes:{
                    theme : "default",
                    dots:false,// 是否展示虚线
                    icons:true,// 是否展示图标
                }
            },
            types: {
                "closed" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/treeFile.png",
                },
                "default" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/treeFile.png",
                },
                "opened" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/openFile.png",
                }
            },
            plugins: [ 'types']
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            var operation =function(){
                if(openFileId==e.node.original.id){
                    Hussar.info("不能移动到当前目录");
                    return;
                }
                var ajax = new $ax(Hussar.ctxPath + "/fsFile/move", function(data) {
                    if(data.result == "0"){
                        Hussar.info("文件已存在");
                    }else if(data.result == "1") {
                        $(".layui-laypage-btn").click();
                        layer.close(layerView);
                        Hussar.success("移动成功");
                        refreshFile(openFileId)
                    }else if(data.result == "4") {
                        Hussar.info("文件必须在底层目录");
                    }  else {
                        Hussar.error("移动失败");
                    }
                }, function(data) {
                    Hussar.error("系统出错，请联系管理员");
                });
                ajax.set("fileId",cutFile.join(","));
                ajax.set("folderId",e.node.original.id);
                ajax.set("fileName",cutFileName.join(","));
                ajax.start();
            }
            layer.confirm('确定要移动到此目录下吗？',operation);
        });

    }
    // /*粘贴*/
    // $("#pasteFile").on('click',function(){
    //
    //     if(isChild==false){
    //         layer.msg("请选择最小文件夹进行粘贴", {anim:6,icon: 0});
    //         return;
    //     }
    //     if(cutFile.length <= 0){
    //         layer.close(index);
    //         layer.msg("请先选择要剪切的文件", {anim:6,icon: 0});
    //         return;
    //     }
    //     for(var i=0;i<chooseFile.length;i++){
    //         var power= $("#authority"+chooseFile[i]).html()
    //         if(power!='2'&&noChildPower!=2&&adminFlag!=1){
    //             layer.msg("您没有权限粘贴文件", {anim:6,icon: 0});
    //             return;
    //         }
    //
    //         if (chooseFileType[i]=="folder"){
    //             layer.msg(folderMessage, {anim:6,icon: 0});
    //             return;
    //         }
    //     }
    //     var index = layer.load(1, {
    //         shade: [0.1,'#fff'] //0.1透明度的白色背景
    //     });
    //
    //
    //
    //     var typeStr=  cutFileType.join(",");
    //     var nameStr=cutFileName.join("*");
    //
    //     $.ajax({
    //         type:"post",
    //         url:"/fsFile/checkName",
    //         data:{
    //             typeStr:typeStr,
    //             nameStr:nameStr,
    //             filePid:openFileId,
    //         },
    //         async:false,
    //         cache:false,
    //         dataType:"json",
    //         success:function(data){
    //             if(data != "success"){
    //                 layer.msg("存在重名文件", {anim:6,icon: 0});
    //                 layer.close(index);
    //                 return ;
    //             }else {
    //                 var folderIdArr = [];
    //                 for (var i = 0; i < cutFile.length; i++) {
    //                     if (cutFileType[i]=="folder"){
    //                         folderIdArr.push(cutFile[i]);
    //                     }
    //                 }
    //                 if(folderIdArr.length>0){
    //                     var folderIdStr=folderIdArr.join(",");
    //                     $.ajax({
    //                         type:"post",
    //                         url:"/fsFile/checkChild",
    //                         data:{
    //                             fsFileIds:folderIdStr,
    //                             id:openFileId,
    //                         },
    //                         async:false,
    //                         cache:false,
    //                         dataType:"json",
    //                         success:function(data){
    //                             if(data == "have"){
    //                                 layer.msg("目标文件夹是剪切文件夹的子文件夹", {anim:6,icon: 0});
    //                                 layer.close(index);
    //                                 return;
    //                             }else {
    //                                 updatePid(index);
    //                                 btnState()
    //                             }
    //                         }
    //                     });
    //                 }else {
    //                     updatePid(index);
    //                     btnState()
    //                 }
    //             }
    //         }
    //     });
    // });


    /*修改*/
    $("#setTip").on('click',function(){

        for(var i=0;i<chooseFile.length;i++){
            var power= $("#authority"+chooseFile[i]).html()
            if(power!='2'&&noChildPower!=2&&adminFlag!=1){
                layer.msg("您没有权限设置标签", {anim:6,icon: 0});
                return;
            }
            if (chooseFileType[i]=="folder"){
                layer.msg(folderMessage, {anim:6,icon: 0});
                return;
            }
        }
        editFlag = true;
        groupId = [];
        groupParam = [];
        personId = [];
        personParam= [];

        layer.open({
            type: 2,
            title: '标签设置',
            area: ['500px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath+'/fsFile/setTip',
            success:function(){
            }
        });


    });
    /*修改*/
    $("#setFileAuthority").on('click',function(){
        chooseFile=[];
        chooseFile=chooseUploadFile;
        chooseFileAuthor=chooseUploadAuthor
        editFlag = true;
        groupId = [];
        groupParam = [];
        personId = [];
        personParam= [];

        layer.open({
            type: 2,
            title: '文件授权',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath+'/fsFile/fileAuthority',
            success:function(){
            } ,end: function(){
                refreshFile(openFileId)
            }
        });


    });


    /*修改*/
    $("#reName").on('click',function(){

        for(var i=0;i<chooseFile.length;i++){
            var power= $("#authority"+chooseFile[i]).html()
            if(power!='2'&&noChildPower!=2&&adminFlag!=1){
                layer.msg("您没有权限设置文件", {anim:6,icon: 0});
                return;
            }
            if (chooseFileType[i]=="folder"){
                layer.msg(folderMessage, {anim:6,icon: 0});
                return;
            }
        }

        var editType = chooseFileType[0];
        editFlag = true;
        groupId = [];
        groupParam = [];
        personId = [];
        personParam= [];
        var type;
        if (editType == "folder"){

        }else {
            layer.open({
                type: 2,
                title: '文件授权',
                area: ['850px', '510px'], //宽高
                fix: false, //不固定
                maxmin: true,
                content: Hussar.ctxPath+'/fsFile/fileAuthority',
                success:function(){
                }
            });

        }
    });


    function editFolder(){
        $.ajax({
            type:"post",
            url: Hussar.ctxPath+"/fsFile/getFsFolderDetail",
            data:{
                fsFileId:chooseFile[0],
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                $("#levelId").empty();
                $("#level").empty();
                setOptionValues();
                var fileId = data.data["0"].fileId;
                $("#fileId").val(fileId);
                var oldName =data.data["0"].fileName;
                var filePid =data.data["0"].filePid;
                var fileType =data.data["0"].fileType;
                $("#categoryName").val(data.data["0"].fileName);
                $("#levelId").val(data.data["0"].levelId);
                form.render('select');
                layer.open({
                    type: 1,
                    btn: ['确定','取消'],
                    skin: 'confirm-class',
                    fix: false, //不固定
                    maxmin: true,
                    shadeClose: false,
                    shade: 0.4,
                    title: "修改目录",
                    content: $('#addDiv'),
                    btn2: function(index, layero){
                        var categoryName = $("#categoryName").val().trim();
                        var levelId = $("#levelId").val();
                        if(categoryName.length<=0){
                            layer.msg("目录名称不能为空", {anim:6,icon: 0});
                            return false;
                        }
                        if(categoryName.length>130){
                            layer.msg("目录名称不能超过130", {anim:6,icon: 0});
                            return false;
                        }
                        var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
                        //特殊字符
                        if(!pattern.test(categoryName)){
                            layer.msg("输入的文件名称不合法", {anim:6,icon: 0});
                            return;
                        }
                        if(levelId == '1'){

                        }
                        if(oldName == categoryName){
                            $.ajax({
                                type: "POST",
                                url: Hussar.ctxPath+"/fsFile/add",
                                data : {
                                    filePid:openFileId,
                                    fileName:categoryName,
                                    levelId:levelId,
                                    fileId:fileId,
                                    filePid:filePid,
                                    fileType:fileType
                                },
                                contentType:"application/x-www-form-urlencoded",
                                dataType:"json",
                                async: false,
                                success:function(result) {
                                    refreshFile(openFileId);
                                    refreshTree();
                                }
                            });
                        }else{
                            $.ajax({
                                type:"post",
                                url: Hussar.ctxPath+"/fsFile/addCheck",
                                data:{
                                    name:categoryName,
                                    filePid:openFileId,
                                },
                                async:false,
                                cache:false,
                                dataType:"json",
                                success:function(data){
                                    if(data == "false"){
                                        layer.msg("“"+categoryName+"”目录已存在", {anim:6,icon: 0});
                                    }else{
                                        $.ajax({
                                            type: "POST",
                                            url: Hussar.ctxPath+"/fsFile/add",
                                            data : {
                                                filePid:openFileId,
                                                fileName:categoryName,
                                                levelId:levelId,
                                                fileId:fileId,
                                                filePid:filePid,
                                                fileType:fileType
                                            },
                                            contentType:"application/x-www-form-urlencoded",
                                            dataType:"json",
                                            async: false,
                                            success:function(result) {
                                                refreshFile(openFileId);
                                                refreshTree();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    },
                });
            }
        });
    }


    $("#setEditAuthority").click(function(){
        layer.open({
            type: 2,
            title: '选择可见范围',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: false,
            content: Hussar.ctxPath+'/fsFolder/authority',
            success:function(){

            }
        });
    });
    /*新增子目录*/
    $("#addCategoryBtn").on('click',function(){
        groupId = [];
        personId = [];
        $("#levelId").empty();
        setOptionValues();
        $("#categoryName").val("");
        /* $("#desc").val("");*/
        layer.open({
            type: 1,
            btn: ['确定','取消'],
            skin: 'confirm-class',
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: "新建目录",
            content: $('#addDiv'),
            btn2: function(index, layero){
                var categoryName = $("#categoryName").val().trim();
                var levelId = $("#levelId").val();
                if(categoryName.length<=0){
                    layer.msg("目录名称不能为空", {anim:6,icon: 0});
                    return false;
                }
                if(categoryName.length>130){
                    layer.msg("目录名称不能超过130", {anim:6,icon: 0});
                    return false;
                }
                var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
                //特殊字符
                if(!pattern.test(categoryName)){
                    layer.msg("输入的文件名称不合法", {anim:6,icon: 0});
                    return;
                }
                if(levelId == '1'){
                    if(groupId.length==0&&personId.length==0){
                        layer.msg("请给保密文档设置权限", {anim:6,icon: 0});
                        return;
                    }
                }

                var groupStr = '';
                var personStr ='';
                var group = [];
                var person = [];
                if (groupId!=undefined){
                    for (var i = 0; i < groupId.length; i++) {
                        group.push(groupId[i].groupId);
                    }
                    var groupStr = group.join(",")
                }

                if (personId!=undefined){
                    for (var i = 0; i < personId.length; i++) {
                        person.push(personId[i].personId);
                    }
                    var personStr = person.join(",")
                }
                $.ajax({
                    type:"post",
                    url: Hussar.ctxPath+"/fsFile/addCheck",
                    data:{
                        name:categoryName,
                        filePid:openFileId,
                    },
                    async:false,
                    cache:false,
                    dataType:"json",
                    success:function(data){
                        if(data == "false"){
                            layer.msg("“"+categoryName+"”目录已存在", {anim:6,icon: 0});
                            return;
                        }else {
                            $.ajax({
                                type: "POST",
                                url: Hussar.ctxPath+"/fsFile/add",
                                data : {
                                    filePid:openFileId,
                                    fileName:categoryName,
                                    levelId:levelId,
                                    group:groupStr,
                                    person:personStr
                                    /*,description:categoryDesc,*/
                                },
                                contentType:"application/x-www-form-urlencoded",
                                dataType:"json",
                                async: false,
                                success:function(result) {
                                    refreshFile(openFileId);
                                    refreshTree();
                                }
                            });
                        }
                    }
                });
            },
        });
    });
    /*上传*/

    setTimeout(function () {
        $(".webuploader-pick").on("click",function () {
            if(chooseFile.length>1||chooseFile.length==0){
                layer.msg("请选择一条信息进行保存", {anim:6,icon: 0});
                return;
            }
            if(chooseFileType[0]!="folder"){
                parent.docId=chooseFile[0];
                parent.updateDoc();
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
            }else{

                layer.msg("请选择文件进行保存", {anim:6,icon: 0});
                return;
            }

        });
    },300);


    /*多选下载*/
    $("#mulDownLoad, #manyMulDownLoad").on('click',function(){
        if(chooseFile.length==0){
            layer.msg("请选择要下载的文件", {anim:6,icon: 0});
            return;
        }
        for (var i = 0; i < chooseFile.length; i++) {
            if (chooseFileType[i]==="folder"){
                layer.msg("无法下载目录文件", {anim:6,icon: 0});
                return;
            }
            var power= $("#authority"+chooseFile[i]).html()
            if(power!='1'&&power!='2'&&noChildPower!=2&&adminFlag!=1){
                layer.msg("权限不足，无法下载", {anim:6,icon: 0});
                return;
            }

        }
        var index = layer.load(1, {
            shade: [0.1,'#fff'] ,//0.1透明度的白色背景
            scrollbar: false,
            time:1000
        });
        var ids=chooseFile.join(",");
        var name=chooseFileName.join("*");
        download(ids,name);

        // layer.close(index);
    });
    /*加入专题*/
    $("#joinTopic").on('click',function(){
        if(chooseFile.length == 0){
            layer.msg("请选择要加入专题的目录或文件", {anim:6,icon: 0});
            return;
        }else {
            var title = "加入专题";
            var url = "/topicDoc/topicDocAdd?chooseFile="+chooseFile +"&chooseFileType="+chooseFileType ;
            docAddOpen=  layer.open({
                type: 2,
                area: [620 + 'px', 530 + 'px'],
                fix: false, //不固定
                maxmin: true,
                shadeClose: true,
                shade: 0.4,
                title: title,
                content: Hussar.ctxPath+url
            });
        }
    })

    $("#orderType li").click(function () {
        $("input[name='sortType']").parent().removeClass("sortType-checked");
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        refreshFile(openFileId);
        layer.close(index);
    })
    $('input[type=radio][name=fileType]').change(function() {
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        refreshFile(openFileId);
        layer.close(index);
    })
    form.on('select(level)', function(data){
        var level = $("#levelId").val();
        if(level == "1"){
            $('#setAuthority').removeClass('layui-btn-disabled').removeAttr('disabled',"false");
        }else {
            $('#setAuthority').addClass('layui-btn-disabled').attr('disabled',"true");
        }
        form.render('select','level');
    });

    function start() {
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");
        initTree();
        getUserTree();
        btnState()
    }
    $("#setAuthority").click(function(){
        layer.open({
            type: 2,
            title: '选择可见范围',
            area: ['1000px', '550px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath+'/fsFile/authority',
            success:function(){

            }
        });
    });
    function getUserTree(){

    }
    $("#authorName").click(function(){
        $("<link>")
            .attr({ rel: "stylesheet",
                type: "text/css",
                id:"aothorTree",
                href: Hussar.ctxPath+"/static/resources/css/jstree.css"
            })
            .appendTo("head");
        var authName=$("#authorName").val().trim();
        layerView=layer.open({
            type: 1,
            area: ['350px','400px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: "作者",
            content: $("#authorDiv"),
            success:function(){
                initAuthorTree(treeData,authName);
            }
        });
    });
    function initAuthorTree(data,authorName) {
        authorIdSnap = "";
        authorNameSnap = "";
        var $authortree = $("#showAuthorTree");
        if($authortree){
            $authortree.jstree("destroy");
        }
        $authortree.jstree({
            core: {
                data: data,
                themes:{
                    theme : "default",
                    dots:false,// 是否展示虚线
                    icons:true,// 是否展示图标
                }
            },
            plugins: ['types','search'],
            types:{
                "1":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/com.png"},
                "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/station.png"},
                "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/empl.png"},
                "USER":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/user.png"}
            },
            search:treeSearch("showAuthorTree","authorTreeSearch",authorName)
        });
        $authortree.bind('activate_node.jstree', function (obj,e){
            if(e.node.original.type !='9' && e.node.original.type !='USER'){
                layer.msg("请选择人员")
                return;
            }else{
                contactsIdSnap = e.node.original.id;
                contactsNameSnap = e.node.original.text;
                $("#authorName").val(e.node.original.text);
                $("#authorId").val(e.node.original.id);
                layer.close(layerView);
            }
        })
    };
    $("#contactsName").click(function(){
        layer.open({
            type: 1,
            area: ['350px','400px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            closeBtn: 0,
            title: "作者",
            btn: [ '关闭','确定']
            ,btn1: function(index, layero){
                if(contactsIdSnap!=""){
                    $("#contactsName").val(contactsNameSnap);
                    $("#contactsId").val(contactsIdSnap);
                } else{
                    layer.msg("请选择作者", {anim:6,icon: 0});
                    return;
                }
                layer.close(index);
            },btn2: function(index, layero){
                layer.close(layer.index);
            },
            content: $("#contactsTreeDiv"),
            success:function(){
                initContactsTree(treeData,$("#contactsName").val());
            }
        });
    });
    function initContactsTree(data,contactsName) {
        contactsIdSnap = "";
        contactsNameSnap = "";
        var $authortree = $("#showContactsTree");
        if($authortree){
            $authortree.jstree("destroy");
        }
        $authortree.jstree({
            core: {
                data: data
            },
            plugins: ['types'],
            types:{
                "1":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/com.png"},
                "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/station.png"},
                "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/empl.png"},
                "USER":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/user.png"}
            },
            /* search:treeSearch("showContactsTree","contactsTreeSearch",contactsName)*/
        });
        $authortree.bind('activate_node.jstree', function (obj,e){
            if(e.node.original.type !='9'&&e.node.original.type !='USER'){
                layer.msg("请选择人员")
                return;
            }else{
                contactsIdSnap = e.node.original.id;
                contactsNameSnap = e.node.original.text;
                $("#contactsName").val(e.node.original.text);
                $("#contactsId").val(e.node.original.id);
                layer.close(layerView);
            }
        })
    };
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
                if(v==null){
                    v ='';
                }
                var temp = $tree.is(":hidden");
                if (temp == true) {
                    $tree.show();
                }
                // $tree.jstree(true).search(v);
                $tree.jstree('search', v).find('.jstree-search').focus();
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
        if(username!=null && username!=""){
            $("#"+searchId).val(username);
            var e = $.Event("keyup");//模拟一个键盘事件
            e.keyCode = 13;//keyCode=13是回车
            $("#"+searchId).trigger(e);//模拟页码框按下回车
        }
    }

    function setOptionValues(){
        $.ajax({
            url: Hussar.ctxPath+"/fsFile/searchLevel",
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                var arr = data.data;
                var optionContent = $("#levelId").html();
                var optionContents = $("#level").html();
                for(var i = 0;i < arr.length;i++){
                    optionContent += "<option value='"+arr[i].value+"'>"+arr[i].label+"</option>";
                    optionContents += "<option value='"+arr[i].value+"'>"+arr[i].label+"</option>";
                }
                $("#levelId").html(optionContent);
                $("#level").html(optionContents);
                $('#levelId').val('1');
                $('#level').val('1');
                form.render('select','level');
            }
        });
        form.render();
        $("dl").height(150);
    }
    /**
     * 加载目录树
     */
    function    initTree(){
        var $tree = $("#fileTree");
        $tree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": Hussar.ctxPath+"/fsFile/getTreeDataLazy?lazy",
                    "data": function (node) {
                        return {"id": node.id,"type" :"1"
                        };
                    }
                },
                /* themes:{
                 theme : "default",
                 dots:false,// 是否展示虚线
                 icons:true,// 是否展示图标

                 },*/
            },
            types: {
                "closed" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/treeFile.png",
                },
                "default" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/treeFile.png",
                },
                "opened" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/openFile.png",
                },
            },
            plugins: ['state', 'types',"themes", "html_data"],
        });
        $tree.jstree().hide_dots();
        $tree.bind("activate_node.jstree", function (obj, e) {
            // 处理代码
            // 获取当前节点
            var currentNode = e.node;
            openFileId = currentNode.id;
            categoryId = currentNode.id;
            refreshFile(currentNode.id);

            emptyChoose();
            var paramId=[];
            var paramName=[];
            if(currentNode.parent=='#'){
                pathId=[];
                pathName=[];
                pathId.push(currentNode.id)
                pathName.push(currentNode.text)
                createPath();
                return;
            }
            $('#path').empty();
            pathId=[];
            pathName=[];
            paramId.push(currentNode.id);
            paramName.push(currentNode.text);
            do{//2、判断循环条件;
                currentNode = $('#fileTree').jstree("get_node", currentNode.parent);
                paramId.push(currentNode.id);
                paramName.push(currentNode.text);
            } while (!!currentNode && currentNode.parent!='#')
            for (var i = 0;i<paramId.length;i++){
                pathId.push(paramId[paramId.length-1-i]);
                pathName.push(paramName[paramId.length-1-i]);
            }
            createPath();
        });
        $tree.bind("open_node.jstree", function (e,data) {
            data.instance.set_type(data.node, 'opened');
        });
        $tree.bind("close_node.jstree", function (e,data) {
            data.instance.set_type(data.node, 'closed');
        });
        $tree.bind("loaded.jstree", function(event, data) {
            data.instance.clear_state(); // <<< 这句清除jstree保存的选中状态
        });

        $.ajax({
            type: "POST",
            url: Hussar.ctxPath+"/fsFile/getRoot",
            contentType:"application/x-www-form-urlencoded",
            dataType:"json",
            async: false,
            success:function(result) {
                openFileId=result.root;
                categoryId = result.root;
                getChildren(result.root,result.rootName);
            }
        });
    }

    function openUpload(title, url, w, h,categoryId,msg){

        if(categoryId == ''||categoryId == undefined || categoryId == null){
            layer.alert(msg, {
                id:"test0811",
                icon :  0,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
            return;
        }
        if(title == null || title == '') {
            title = false;
        };
        if(url == null || url == '') {
            url = "404.jsp";
        };
        if(w == null || w == '') {
            w = ($(window).width() * 0.9);
        };
        if(h == null || h == '') {
            h = ($(window).height() - 50);
        };
        layer.open({
            id:"0811",
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: Hussar.ctxPath+url
        });


    }
    function updatePid(index){
        var cutIds= cutFile.join(",");
        $.ajax({
            type:"post",
            url: Hussar.ctxPath+"/fsFile/update",
            data:{
                ids:cutIds,
                filePid:openFileId,
            },
            async:true,
            cache:false,
            success:function(data){
                if("success" == data) {
                    emptyChoose();
                }
                refreshFile(openFileId);
                refreshTree();
                layer.close(index);
            }
        })
    }

    /**
     * 断点续传Demo的单例
     */
    var BreakpointUpload = {
        layerIndex: -1
    };
    $("#cancel").on('click',function(){
        var index = layer.getFrameIndex(window.name);
        layer.close(index);
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
        var check = $("input[name='watermark_company_isChecked']:checked").val();
        if (check!= undefined&&check=='on') {
            $('#watermark_company').removeClass('layui-disabled').removeAttr('disabled',"false");
        } else {
            $('#watermark_company').addClass('layui-disabled').attr('disabled',"true");
        }
        form.render();
    });
    $("#setAuthority1").click(function(){
        layer.open({
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
    function getLoginUser(){
        $.ajax({
            type:"post",
            url: Hussar.ctxPath+"/files/getLoginUser",
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
    /**
     * 初始化上传组件
     */
    BreakpointUpload.initUploader = function () {
        //初始变量定义
        chooseFile=[];
        var dropZone = document.getElementById("dndArea");
        dropZone.addEventListener("dragover", function (e) {
            $(".shadow").show();
            $("#dndArea").css("opacity",".3");
        }, false);
        dropZone.addEventListener("dragleave", function (e) {

            $("#dndArea").css("opacity","1");
            $(".shadow").hide();
        }, false);
        // 监听分块上传过程中的三个时间点
        WebUploader.Uploader.register({
                "before-send-file":"beforeSendFile",//整个文件上传前
                "before-send":"beforeSend",  //每个分片上传前
                "after-send-file":"afterSendFile"  //分片上传完毕
            },
            {
                //时间点1：所有分块进行上传之前调用此函数
                beforeSendFile:function(file){
                    $("#dndArea").css("opacity","1");
                    $(".shadow").hide();
                    fileId=file.id;
                    if(isChild==false){
                        $(".popWin").css("display","none")
                        return;
                    }
                    if(noChildPower==0&&adminFlag!=1){
                        $(".popWin").css("display","none")
                        return;
                    }
                    $(".popWin").css("display","block").removeClass("success");

                    var deferred = WebUploader.Deferred();
                    //1、计算文件的唯一标记fileMd5，用于断点续传  如果.md5File(file)方法里只写一个file参数则计算MD5值会很慢 所以加了后面的参数：10*1024*1024
                    (new WebUploader.Uploader()).md5File(file,0,10*1024*1024).progress(function(percentage){

                        percentageFlag=percentage;
                        $('#'+file.id ).find('p.state').text('读取中...');
                        $('#'+file.id).find('.btnRemoveFile').removeClass("delete");

                    })
                        .then(function(val){

                            $('#'+file.id ).find("p.state").text("读取成功...");
                            fileMd5=val;
                            fileName=file.name; //为自定义参数文件名赋值
                            $.ajax({
                                    type:"post",
                                    url: Hussar.ctxPath+"/breakpointUpload/checkMd5Exist",
                                    data:{
                                        fileName:fileName,
                                        categoryId:categoryId,
                                        visible:"0",
                                        downloadAble:"0",
                                        watermarkUser:"",
                                        watermarkCompany:"",
                                        fileMd5:fileMd5,
                                        group:"",
                                        person:""

                                    },
                                    async:false,
                                    cache:false,
                                    dataType:"json",
                                    success:function(data){
                                        if(data.code=='2'){

                                            fastFlag=1;
                                        }
                                        if(data.code=='4'){

                                            fastFlag=2;
                                        }
                                        if(data.code=='6'){

                                            fastFlag=6;
                                        }
                                        if(data.code=='7'){

                                            fastFlag=7;
                                        }
                                        if(data.code=='8'){

                                            fastFlag=8;
                                        }
                                        if(data.code=='5'){
                                            fastFlag=0 ;
                                            chooseUploadFile.push(data.id);
                                            chooseUploadAuthor.push(data.authorId);
                                        }
                                    }
                                }
                            );
                            //获取文件信息后进入下一步
                            deferred.resolve();

                        });

                    return deferred.promise();

                },
                //时间点2：如果有分块上传，则每个分块上传之前调用此函数
                beforeSend:function(block){
                    if(fastFlag==0||fastFlag==1||fastFlag==2||fastFlag==6||fastFlag==7||fastFlag==8){
                        return;
                    }
                    // if(isChild==false){
                    //
                    //     return;
                    // }
                    // if(noChildPower==0&&adminFlag!=1){
                    //     return;
                    // }
                    var deferred = WebUploader.Deferred();
                    //ajax验证每一个分片
                    var ajax = new $ax(Hussar.ctxPath + "/breakpointUpload/mergeOrCheckChunks",
                        function(data) {
                            var res = eval('('+data+')');
                            if(res.ifExist){
                                //分块存在，跳过
                                deferred.reject();
                            }else{
                                //分块不存在或不完整，重新发送该分块内容
                                deferred.resolve();
                            }
                        },
                        function(data) {
                        });
                    ajax.set("param","checkChunk");
                    ajax.set("fileName",fileName);
                    ajax.set("jindutiao",$("#jindutiao").val());
                    ajax.set("fileMd5",fileMd5);//文件唯一标记
                    ajax.set("chunk",block.chunk);//当前分块下标
                    ajax.set("chunkSize",block.end-block.start);//当前分块大小
                    ajax.start();

                    this.owner.options.formData.fileMd5 = fileMd5;

                    deferred.resolve();

                    return deferred.promise();
                },
                //时间点3：所有分块上传成功后调用此函数
                afterSendFile:function(file){
                    fileId=file.id;
                    if(isChild==false){

                        powerFlag=1;
                        $('#'+fileId ).remove();
                        uploader.removeFile( fileId,true);
                        count++
                        return;
                    }
                    if(noChildPower==0&&adminFlag!=1){
                        powerFlag=1;
                        uploader.removeFile( fileId,true);
                        $('#'+fileId ).remove();
                        count++
                        return;
                    }
                    powerFlag=0;
                    if(fastFlag==0||fastFlag==1||fastFlag==2||fastFlag==6||fastFlag==7||fastFlag==8){

                        count++; //每上传完成一个文件 count+1
                        if(count>filesArr.length-1){

                        }else {
                            uploader.upload(filesArr[count].id);//上传文件列表中的下一个文件
                        }
                        return ;
                    }

                    var code=0;
                    $('#'+fileId).find('p.state').text('转化中')
                    //如果分块上传成功，则通知后台合并分块
                    var ajax = new $ax(Hussar.ctxPath + "/breakpointUpload/mergeOrCheckChunks",
                        function(data) {
                            count++; //每上传完成一个文件 count+1
                            if(data!=""){
                                dataNew=eval('(' + data + ')')
                                if(dataNew.code==3){
                                    code=3;
                                }
                                if(dataNew.code==2){
                                    code=2;
                                }
                                if(code==0){
                                    chooseUploadFile.push(dataNew.id);
                                    chooseUploadAuthor.push(dataNew.authorId);
                                }
                            }
                            if(code!=3&&count<=filesArr.length-1){
                                uploader.upload(filesArr[count].id);//上传文件列表中的下一个文件
                            }else{
                                // 合并成功之后的操作

                                if(code==2){
                                    flag=4;


                                }
                                if(code==3){
                                    flag=3;

                                }
                            }
                        },
                        function(data) {
                        });

                    ajax.set("categoryId",categoryId);
                    ajax.set("visible","0");
                    ajax.set("downloadAble","0");
                    ajax.set("watermarkUser","");
                    ajax.set("watermarkCompany","");
                    ajax.set("group","");
                    ajax.set("person","");
                    ajax.set("param","mergeChunks");
                    ajax.set("fileName",fileName);
                    ajax.set("fileMd5",fileMd5);
                    ajax.start();

                }
            });//监听结束

        uploader = WebUploader.create({
            auto : true, //是否自动上传
            pick : {
                id : '#picker',
                label : '保存',
                multiple:true
            },
            duplicate : false, //同一文件是否可重复选择
            prepareNextFile: false,
            // 不压缩image
            resize: false,
            accept : {
                title: 'Files',
                extensions: '*',
                mimeTypes: '*'

            },
            compress : null,//图片不压缩
            chunked : true, //分片
            chunkSize : 10 * 1024 * 1024, //每片10M
            chunkRetry : 3,//如果失败，则不重试
            threads : 1,//上传并发数。允许同时最大上传进程数。
            fileNumLimit : 10,//验证文件总数量, 超出则不允许加入队列
            fileSizeLimit:6*1024*1024*1024,//6G 验证文件总大小是否超出限制, 超出则不允许加入队列
            fileSingleSizeLimit:3*1024*1024*1024,  //3G 验证单个文件大小是否超出限制, 超出则不允许加入队列
            // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
            disableGlobalDnd : true,
            dnd:"#dndArea",
            // swf文件路径
            swf : '${ctxPath}/static/assets/lib/webuploader0.1.5/Uploader.swf',
            // 文件接收服务端。
            server : Hussar.ctxPath + "/breakpointUpload/fileSave"
        });

        /**
         *  当有文件添加进来的时候
         */
        uploader.on( 'fileQueued', function( file ) {
            //限制单个文件的大小 超出了提示
            if(file.size>3*1024*1024*1024){
                Hussar.info("单个文件大小不能超过3G！");
                return false;
            }
            filesArr.push(file);
            success++;
            var ajax = new $ax(Hussar.ctxPath + "/breakpointUpload/selectProgressByFileName",
                function(data) {
                    var res = eval('('+data+')');
                    //上传过程
                    if(res.jindutiao>0){
                        //上传过的进度的百分比
                        oldJindu=res.jindutiao/100;
                        //如果上传过 上传了多少
                        var jindutiaoStyle="width:"+res.jindutiao+"%";
                        $list.append( '<div id="' + file.id + '" class="item">' +
                            '<h4 class="info"  title="'+ file.name + '">' + file.name + '</h4>' +
                            '<p class="state">已上传'+res.jindutiao+'%</p>' +
                            '<a href="javascript:void(0);"  style="float:right;width: 150px" class=" delete btnRemoveFile"></a>' +
                            '</div>' );
                        //将上传过的进度存入map集合
                        map[file.id]=oldJindu;
                    }else{//没有上传过
                        $list.append( '<div id="' + file.id + '" class="item">' +
                            '<h4 class="info" title="'+ file.name + '">' + file.name + '</h4>' +
                            '<p class="state">等待上传...</p>' +
                            '<a style="float:right;width: 150px" href="javascript:void(0);" class=" delete btnRemoveFile"></a>' +
                            '</div>' );
                    }
                },
                function(data) {

                });
            ajax.set("fileName",file.name);     //文件名
            ajax.start();

            uploader.stop(true);
            //删除队列中的文件
            $(".btnRemoveFile").bind("click", function() {
                var fileItem = $(this).parent();
                uploader.removeFile($(fileItem).attr("id"), true);
                $(fileItem).fadeOut(function() {
                    $(fileItem).remove();
                });


                //数组中的文件也要删除
                for(var i=0;i<filesArr.length;i++){
                    if(filesArr[i].id==$(fileItem).attr("id")){
                        filesArr.splice(i,1);//i是要删除的元素在数组中的下标，1代表从下标位置开始连续删除一个元素
                    }
                }
                //隐藏上传按钮

                if(count-success <0){
                    success--;
                }
            });
        });

        //文件上传过程中创建进度条实时显示
        uploader.on('uploadProgress', function(file, percentage) {
            var $li = $( '#'+file.id );

            //避免重复创建

            //将实时进度存入隐藏域
            $("#jindutiao").val(Math.round(percentage * 100));
            //根据fielId获得当前要上传的文件的进度
            var oldJinduValue = map[file.id];

            if(percentage<oldJinduValue && oldJinduValue!=1){
                $li.find('p.state').text('上传中'+Math.round(oldJinduValue * 100) + '%');

                if(oldJinduValue==1){
                    $li.find('p.state').text('转化中...');
                }
            }else{
                $li.find('p.state').text('上传中'+Math.round(percentage * 100) + '%');

                if(percentage==1){
                    $li.find('p.state').text('转化中...');
                }

            }
        });

        //上传成功后执行的方法
        uploader.on('uploadSuccess', function( file ) {

            if(flag==0){
                $('#'+file.id).find('p.state').text('文件已存在')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                flag=1;
            }else if(flag==3){
                $('#'+file.id).find('p.state').text('文件上传失败')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                flag=1;
                fastFlag=null;

            }
            else if(flag==4){
                $('#'+file.id).find('p.state').text('空间不足')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                flag=1;
            }else if(fastFlag==2){
                $('#'+file.id).find('p.state').text('文件已存在')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag=null;
            }else if(fastFlag==1){
                $('#'+file.id).find('p.state').text('空间不足')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag=null;
            }else if(fastFlag==6){
                $('#'+file.id).find('p.state').text('名称过长')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag=null;
            }else if(fastFlag==7){
                $('#'+file.id).find('p.state').text('名称不合法')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag=null;
            }else if(fastFlag==8){
                $('#'+file.id).find('p.state').text('格式不支持')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag=null;
            }else if(fastFlag==0){

                //隐藏删除按钮

                //隐藏上传按钮
                success--;
                if(success == 0){

                }
                $('#'+file.id).find('p.state').text('')
                $('#'+file.id).find('p.state').addClass("success").append('秒传');
                refreshFile(openFileId)
                fastFlag=null;


            }
            else{  //上传成功去掉进度条

                //隐藏删除按钮

                //隐藏上传按钮
                $.ajax({
                    url: Hussar.ctxPath+"/integral/addIntegral",
                    async: true,
                    data:{
                        docId: file.id,
                        ruleCode: 'upload'
                    },
                    success: function (data) {
                        if (data.integral != 0){
                            alert(data.msg);
                        }
                    }
                });
                success--;
                if(success == 0){

                }
                $('#'+file.id).find('p.state').text('')
                $('#'+file.id).find('p.state').addClass("success").append('上传')
            }
            if(count>filesArr.length-1){
                if(powerFlag==1){
                    chooseUploadFile=[];
                    chooseUploadAuthor=[];
                    count=0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
                    success=0;//上传成功的文件数
                    filesArr=new Array();//文件数组：每当有文件被添加进队列的时候 就push到数组中
                    map={};//key存储文件id，value存储该文件上传过的进度
                    powerFlag=0
                    if(isChild==false){
                        $(".shadow").hide();
                        layer.msg("请选择最小文件夹进行上传", {anim:6,icon: 0});
                    }
                    if(noChildPower==0&&adminFlag!=1){
                        $(".shadow").hide();
                        layer.msg("您没有上传文件权限", {anim:6,icon: 0});
                    }
                }else{
                    var amount=count-success;
                    if(amount<0) {
                        amount=0;
                    }
                    $(".success-msg").html("成功上传"+amount+"个文件！").show(500);
                }

            }




            refreshFile(openFileId)
            fastFlag=null;

        });

        //上传出错后执行的方法
        uploader.on('uploadError', function( file ) {
            errorUpload=true;
            uploader.stop(true);
            $('#'+file.id).find('p.state').text('上传出错，请检查网络连接');
        });

        //文件上传成功失败都会走这个方法
        uploader.on('uploadComplete', function( file ) {

        });

        uploader.on('all', function(type){

            if (type === 'startUpload'){
                state = 'uploading';
            }else if(type === 'stopUpload'){
                state = 'paused';
            }else if(type === 'uploadFinished'){
                state = 'done';
            }
        });

        /**
         验证文件格式以及文件大小
         */
        uploader.on("error", function (type) {
            if (type == "Q_TYPE_DENIED") {
                $(".shadow").hide();
                layer.msg("存在不支持上传的文档格式", {anim:6,icon: 0});
            } else if (type == "Q_EXCEED_SIZE_LIMIT") {
                $(".shadow").hide();
                layer.msg("文件大小不能超过3G", {anim:6,icon: 0});
            }else if (type == "Q_EXCEED_NUM_LIMIT") {
                $(".shadow").hide();
                layer.msg("上传列表中不得超过10个文件", {anim:6,icon: 0});
            } else if (type == "F_DUPLICATE") {
                $(".shadow").hide();
                layer.msg("上传列表中存在重复文件", {anim:6,icon: 0});
            }else {
                $(".shadow").hide();
                layer.msg("上传出错！请检查后重新上传！错误代码"+type, {anim:6,icon: 0});
            }
        });
    };

    /**
     *  初始化按钮事件
     */
    BreakpointUpload.initButtonEvents = function () {
        $("#searchName").click(function () {
            searchFlag=1;
        })
    };

    //页面初始化
    $(function () {
        getLoginUser();
        BreakpointUpload.initUploader();
        BreakpointUpload.initButtonEvents();

        //弹窗按钮事件添加
        $(".controls-down").click(function () {
            $(".popWin").toggleClass("success");
            if($(".popWin").hasClass("success")){
                $(this).html("&#xe619;")
            }else {
                $(this).html("&#xe61a;")
            }

        });


        $(".controls-close").click(function () {
            $(".success-msg").html("成功上传"+(count-success)+"个文件！").hide(500);
            //最开始进入页面时清除原来的列表
            var fileList = $("#thelist").find(".item");
            for(var n = 0;n<fileList.length;n++){
                fileList.eq(n).remove();
                uploader.removeFile( fileList.eq(n).attr("id"),true);
            }
            chooseUploadFile=[];
            chooseUploadAuthor=[];
            count=0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
            success=0;//上传成功的文件数
            filesArr=new Array();//文件数组：每当有文件被添加进队列的时候 就push到数组中
            map={};//key存储文件id，value存储该文件上传过的进度
            $(".popWin").css("display","none")
        })
    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth =inner.width();
        //fixed-table-header
        $(".fixed-table-header").width(tableWidth)
    })
});
function clickPath(id) {
    while(pathId.indexOf(id)+1!=pathId.length){
        pathId.pop();
        pathName.pop();
    }
    createPath();
    refreshFile(id);
}
function createPath(){
    $("#path").empty();
    $("#path").css({"transform":"translateX(0)"});
    for(var i=0;i<pathId.length;i++){
        if(i==pathId.length-1){
            var param= '<span>'+pathName[i]+'</span>'
        }else {
            var param= '<span><a style="cursor: pointer; color: #00a4ff;" onclick="clickPath(\''+pathId[i]+'\')">'+pathName[i]+'</a>'+'  /  </span>'        }
        $("#path").append(param);
        setTimeout(function () {

            var list =  $("#path>span");
            var innerlength  = 0;
            for(var m = 0 ;m < (list.length) ;m++){
                innerlength = Math.ceil(innerlength + list.eq(m).width() + 5.4);
            }
            $("#path").width(innerlength);
            var outWidth = $(".outer-nav").width() - 5;
            //当目录长度超出显示范围，默认只显示可以显示的最后
            if(innerlength>outWidth){
                $(".control-btn-l").show();
                $(".control-btn-r").hide();
                var  subLength = innerlength - outWidth;
                $("#path").css({"transform":"translateX(-"+subLength+"px)"});
                //获取当前偏移量

                $(".control-btn-l").click(function () {
                    var  subLength = $("#path").width() - $(".outer-nav").width();
                    var subLength_1 = -$("#path").css("transform").replace(/[^0-9\-,]/g,'').split(',')[4];
                    $(".control-btn-r").show();
                    subLength_1 = subLength_1 - outWidth;
                    if(subLength_1 > outWidth){
                        $("#path").css({"transform":"translateX(-"+subLength_1+"px)"});
                    }else {
                        $("#path").css({"transform":"translateX(0)"});
                        $(".control-btn-l").hide();
                    }

                });
                $(".control-btn-r").click(function () {
                    var  subLength = $("#path").width() - $(".outer-nav").width();
                    $(".control-btn-l").show();
                    var subLength_2 = -$("#path").css("transform").replace(/[^0-9\-,]/g,'').split(',')[4];
                    subLength_2 = subLength_2 + outWidth;
                    if(subLength_2 > subLength){
                        $("#path").css({"transform":"translateX(-"+subLength+"px)"});
                        $(".control-btn-r").hide();
                    }else {
                        $("#path").css({"transform":"translateX(-"+subLength_2+"px)"});
                    }
                })
            }else {
                $("#path").css({"transform":"translateX(0)"});
                $(".control-btn-l").hide();
                $(".control-btn-r").hide();
            }
        },100)

    }
}
function drawFile(param,showFlag) {
    if(showStyle==1){
        layui.use('laytpl', function(){
            var laytpl = layui.laytpl;
            var data = { //数据
                "list":param,
                "adminFlag":adminFlag
            }
            var getTpl = $("#demo").html()
                ,view = document.getElementById('view');
            laytpl(getTpl).render(data, function(html){
                view.innerHTML = html;
            });
        });
    }
    else{
        layui.use('laytpl', function(){
            var laytpl = layui.laytpl;
            var data = { //数据
                "list":param,
                "adminFlag":adminFlag
                ,"noChildPower":noChildPower
            }
            var getTpl = $("#demo1").html()
                ,view = document.getElementById('view');
            laytpl(getTpl).render(data, function(html){
                view.innerHTML = html;
                var inner = $("#view");
                var tableWidth =inner.width();
                //fixed-table-header
                $(".fixed-table-header").width(tableWidth)
            });
        });
    }

}

function   drawPower(isAdmin){
    if(isAdmin!=1){
        // $("#addCategoryBtn").hide();
        $("#joinTopic").hide();

    }

}
function getChildren(id,name){
    pathId.push(id);
    pathName.push(name);
    createPath();
    refreshFile(id);
}
function addOper(parent,node) {
    $("#fileTree").jstree("deselect_all",true);
    var ref = $('#fileTree').jstree(true);
    ref.open_node(node);
    var id = ref.get_node(node+'_anchor');
    if(id){
        ref.select_node(id);
    }else{
        ref.select_node(node.substr(0,node.length-2));
    }
}
function refreshFile(id,num,size,nameFlag,order){
    var noOrder;
    if(order==null||order==undefined){
        noOrder=true;

    }
    layui.use(['laypage','layer','table','flow','Hussar'], function(){
        var flow = layui.flow;
        var laypage = layui.laypage,
            layer = layui.layer;
        var Hussar = layui.Hussar;
        var fileType = $("input[name='fileType']:checked").val();
        var name = $('#searchName').val();
        if(nameFlag!=""&&nameFlag!=undefined&&nameFlag!=null){
            var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if(!pattern.test(name)){
                layer.msg("输入的文件名称不合法", {anim:6,icon: 0});
                return;
            }
        }
        addOper(openFileId,id);
        $.ajax({
            type:"post",
            url: Hussar.ctxPath+"/fsFile/getChildren",
            data:{
                id: id,
                pageNumber:num,
                pageSize:size,
                type:fileType,
                order:order,
                name:name,
                nameFlag:nameFlag,
                operateType:"1"
            },
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            async:true,
            cache:false,
            dataType:"json",
            success:function(data){
                laypage.render({
                    elem: 'laypageAre'
                    ,count: data.total //数据总数，从服务端得到
                    ,limit: 20
                    ,layout: ['prev', 'page', 'next']
                    ,curr: num || 1 //当前页
                    ,jump: function(obj, first){
                        //obj包含了当前分页的所有参数，比如：
                        // obj.curr得到当前页，以便向服务端请求对应页的数据。
                        // obj.limit得到每页显示的条数
                        //首次不执行
                        if(!first){
                            refreshFile(id,obj.curr,obj.limit)
                        }
                    }
                });

                adminFlag=data.isAdmin;
                noChildPower=data.noChildPower;
                drawFile(data.rows);
                drawPower(data.isAdmin);
                openFileId = id;
                categoryId = id;
                userId=data.userId;
                isChild=data.isChild;
                emptyChoose();
                btnState();
                dbclickover = true;
                var flag=false;
                var fileIds= [];

                for(var i=0;i<data.rows.length;i++){
                    if(data.rows[i].fileType != 'folder'){
                        flag = true;
                        fileIds.push(data.rows[i].fileId)
                    }
                }
                $('#amount').html(fileIds.length);
                var idStr=fileIds.join(",")
                $.ajax({
                    type:"post",
                    url: Hussar.ctxPath+"/fsFile/getInfo",
                    data:{
                        ids:idStr
                    },
                    async:true,
                    cache:false,
                    dataType:"json",
                    success:function(data){

                        for(var i=0 ; i<data.length;i++){
                            /*var enId=data[i].fileId.replace(/\//g,'');
                             var enId=enId.replace('.','');*/
                            $('#downNum'+data[i].fileId).html(data[i].downNum);
                            $('#readNum'+data[i].fileId).html(data[i].readNum);
                            $('#person'+data[i].fileId).html(data[i].name);
                            $('#authority'+data[i].fileId).html(data[i].authority);
                        }
                    }
                });
                if(noOrder==true){
                    $("#orderName").hide();
                    $("#orderName1").show();
                    $("#orderTime").hide();
                    $("#orderTime1").show();
                }else{
                    if(order== "1"){
                        $("#orderName").hide();
                        $("#orderName1").show();
                        $("#orderTime").hide();
                        $("#orderTime1").show();
                    }
                    if(order== "0"){
                        $("#orderName1").hide();
                        $("#orderName").show();
                        $("#orderTime").hide();
                        $("#orderTime1").show();
                    }
                    if(order== "2"){
                        $("#orderName").hide();
                        $("#orderName1").show();
                        $("#orderTime1").hide();
                        $("#orderTime").show();
                    }
                    if(order== "3" ){
                        $("#orderTime").hide();
                        $("#orderTime1").show();
                        $("#orderName").hide();
                        $("#orderName1").show();
                    }

                }
                $("#amount").html(data.amount);
                $(".file-container-flatten").height($("body").height() - $(".toolBar").outerHeight(true) -
                    $("#pathDiv").outerHeight(true) - $(".orderSearch").outerHeight(true)  - $("#laypageAre").outerHeight(true)-35);
                $(".hoverEvent").hover(function(){
                    var ishover= $(this).find(".ishover");
                    if(ishover.is(':hidden')){
                        $(".moreicon").hide();
                        $(this).find("td  #hoverSpan").show();
                    }else{
                        $(this).find("td  #hoverSpan").hide();
                    }

                },function(){
                    $(this).find("td  #hoverSpan").hide();
                });
                $(".clickEvent").click(function () {
                    cancelBubble();
                    var  index = $(this).next().val();
                    if(size==null||size==undefined){
                        size=20;
                    }

                    if(num==null||num==undefined){
                        num=1;
                    }
                    if((size*num)>data.amount){
                        var lack = data.amount%size;
                        if((lack-1==index||lack-2==index||lack-3==index)&&index>=4){
                            $(this).next().next().css("bottom","30px");
                        }
                    }

                    if(data.amount>size*(num-1)){
                        if(index==(size-1)||index==(size-2)||index==(size-3)){
                            $(this).next().next().css("bottom","30px");
                        }
                    }

                    $(this).parent().find(".moreicon").show();
                })


            }
        });

    });
}
function refreshTree(){
    var $tree = $("#fileTree");
    $tree.jstree(true).refresh();
}
function dbclick(id,type,name){
    if(dbclickover==true) {
        if (clickFlag) {//取消上次延时未执行的方法
            clickFlag = clearTimeout(clickFlag);
        }
        dbclickover=false;
        /*开始重命名后又进行双击操作需要撤回之前的操作*/
        $('#name' + reNameIndex).removeClass("hide");
        $('#inputName' + reNameIndex).addClass("hide");
        $('#inputName' + reNameIndex).val(reNameParem);
        reNameFlag = false;

        if (type == "folder") {
            pathId.push(id);
            pathName.push(name);
            createPath();
            refreshFile(id);
        } else {
            showPdf(id, type, name)
        }
    }
}
function showPdf(id,fileSuffixName,name) {
    var keyword =  name;
    dbclickover = true;

    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            async: false,
            cache: false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType: "json",
            success: function (data) {
                if(data.code==1){
                    openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
                }else if(data.code==2){
                    openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
                }else if(data.code==3){
                    openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
                }else if(data.code==4){
                    openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
                }else{
                    openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id);
                }

            }
        });
    });
}
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function isPDFShow(fileSuffixName){
    return fileSuffixName == ".pdf"
        || fileSuffixName == ".doc" || fileSuffixName == ".docx" || fileSuffixName == ".dot"
        || fileSuffixName == ".wps" || fileSuffixName == ".wpt"
        || fileSuffixName == ".xls" || fileSuffixName == ".xlsx" || fileSuffixName == ".xlt"
        || fileSuffixName == ".et" || fileSuffixName == ".ett"
        || fileSuffixName == ".ppt" || fileSuffixName == ".pptx" || fileSuffixName == ".ppts"
        || fileSuffixName == ".pot" || fileSuffixName == ".dps" || fileSuffixName == ".dpt"
        || fileSuffixName == ".txt"
        || fileSuffixName == ".ceb";
}

function download(id,name){
    cancelBubble();
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew",
            type : "post",
            async:false,
            data : {
                docName : "",//name,
                docIds : id
            }
        });
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}

function clickOneTime(e,id,type,name,index,author){

    if(clickFlag) {//取消上次延时未执行的方法
        clickFlag = clearTimeout(clickFlag);
    }

    clickFlag = setTimeout(function() {

        var jq=$(e);
        if(key==1){
            if(chooseFile.indexOf(id)!=-1){
                jq.removeClass("active");
                chooseFile=chooseFile.del(chooseFile.indexOf(id));
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
            }else {
                jq.addClass("active");
                chooseFile.push(id);
                chooseFileType.push(type);
                chooseFileName.push(name);
                chooseFileAuthor.push(author)
            }
        }else{
            if(chooseFile.indexOf(id)==-1){
                if(reNameFlag==true){
                    $('#name'+reNameIndex).removeClass("hide");
                    $('#inputName'+reNameIndex).addClass("hide");
                    reNameFlag=false;
                    var inputname = $('#inputName'+reNameIndex).val();
                    if(inputname!=reNameParem){
                        rename(inputname);
                    }
                }
                reNameIndex=index;
                reNameParem=name;
                $('.file').removeClass("active");
                //refreshFile(openFileId);
                emptyChoose();
                jq.addClass("active");
                chooseFile.push(id);
                chooseFileType.push(type);
                chooseFileName.push(name);
                chooseFileAuthor.push(author)

            }else/* if(type=="folder")*/{
                $('#name'+index).addClass("hide");
                $('#inputName'+index).removeClass("hide");
                $('#inputName'+index).focus();
                reNameFlag=true;
                reNameParem=name;
                reNameIndex=index;
            }
        }
        btnState()

    }, 100);//延时300毫秒执行
}

/*修改*/
$("#updateName").on('click',function(){

    for(var i=0;i<chooseFile.length;i++){
        var power= $("#authority"+chooseFile[i]).html()
        if(power!='2'&&noChildPower!=2&&adminFlag!=1){
            layer.msg("您没有权限重命名文件", {anim:6,icon: 0});
            return;
        }
        if (chooseFileType[i]=="folder"){
            layer.msg(folderMessage, {anim:6,icon: 0});
            return;
        }

    }

    if(chooseFile.length!=1){
        layer.msg("请选择一个要重命名的文件", {anim:6,icon: 0});
        return;
    }
    $('#inputName'+reNameIndex).val( chooseFileName[0]);
    $('#name'+reNameIndex).addClass("hide");
    $('#inputName'+reNameIndex).removeClass("hide");
    $('#inputName'+reNameIndex).focus();
    reNameFlag=true;


})
function clickOneTime2(e,id,type,name,index,author){

    if(clickFlag) {//取消上次延时未执行的方法
        clickFlag = clearTimeout(clickFlag);
    }

    clickFlag = setTimeout(function() {

        var jq=$(e);
        if(key==1){
            if(chooseFile.indexOf(id)!=-1){
                jq.removeClass("active");
                chooseFile=chooseFile.del(chooseFile.indexOf(id));
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))

            }else {
                jq.addClass("active");
                chooseFile.push(id);
                chooseFileType.push(type);
                chooseFileName.push(name);
                chooseFileAuthor.push(author)
            }
        }else{
            if(chooseFile.indexOf(id)==-1){
                if(reNameFlag==true){
                    $('#name'+reNameIndex).removeClass("hide");
                    $('#inputName'+reNameIndex).addClass("hide");
                    reNameFlag=false;
                    var inputname = $('#inputName'+reNameIndex).val();
                    if(inputname!=reNameParem){
                        rename(inputname);
                    }
                }
                reNameIndex=index;
                $('.layui-table tr').removeClass("active");
                //refreshFile(openFileId);
                emptyChoose();
                jq.addClass("active");
                chooseFile.push(id);
                chooseFileType.push(type);
                chooseFileName.push(name);
                chooseFileAuthor.push(author)

            }else/* if(type=="folder")*/{
                $('#name'+index).addClass("hide");
                $('#inputName'+index).removeClass("hide");
                $('#inputName'+index).focus();
                reNameFlag=true;
                reNameParem=name;
                reNameIndex=index;
            }
        }  btnState()

    }, 100);//延时300毫秒执行
}
function  clickCheck(e,id,type,name,index,author) {

    var jq=$(e);
    if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile(openFileId)
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if(jq.find(".checkbox").prop("checked")==false){

        jq.find(".checkbox").prop("checked",true);
        jq.find(".layui-form-checkbox").addClass("layui-form-checked");

        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)


    }else{
        jq.find(".checkbox").prop("checked",false);
        jq.find(".layui-form-checkbox").removeClass("layui-form-checked");
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFile=chooseFile.del(chooseFile.indexOf(id));}
            chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
        }
        if(chooseFile.length==1){
            var id = chooseFile[0];
            reNameParem=chooseFileName[0];
            var index=  $("#"+id+"").val();
            reNameIndex=index
        }

    }

    btnState()

    cancelBubble()
}
function  clickIconCheck(e,id,type,name,index,author) {

    $(e).toggleClass('layui-form-checked');
    var checkbox=$(e).siblings('.checkbox');
    if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile(openFileId)
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if(checkbox.prop("checked")==false){

        checkbox.prop("checked",true);

        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)


    }else{
        checkbox.prop("checked",false);
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFile=chooseFile.del(chooseFile.indexOf(id));}
            chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
        }
    }

    btnState()
    cancelBubble()
}
function checkAll(e) {
    var self = $(e).siblings('input');//
    $(e).toggleClass('layui-form-checked');
    if(self.prop("checked")==false){
        self.prop("checked",true);
    }else{
        self.prop("checked",false);
    }
    if (self.prop("checked")) { // 全选
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", true);
            $(this).siblings('.layui-form-checkbox').addClass("layui-form-checked");
            var checkFileId=  $(this).siblings(".checkFileId").val();
            var checkFileType=  $(this).siblings(".checkFileType").val()
            var checkFileName=  $(this).siblings(".checkFileName").val()
            var checkFileAuthor=  $(this).siblings(".chooseFileAuthor").val()
            chooseFile.push(checkFileId);
            chooseFileType.push(checkFileType);
            chooseFileName.push(checkFileName);
            chooseFileAuthor.push(checkFileAuthor)

        });

    }
    else { // 取消全选

        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", false);
            $(this).siblings('.layui-form-checkbox').removeClass("layui-form-checked");
        });
        chooseFileType=[];
        chooseFileName=[];
        chooseFile=[];
        chooseFileAuthor=[]
    }
    btnState();
}

Array.prototype.del=function(n) {
    if(n<0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0,n).concat(this.slice(n+1,this.length));
}

$(document).keydown(function(e){
    if(e.ctrlKey){
        key=1;
    }else if(e.shiftKey){
        key=2;
    }else if(e.keyCode == 13 && searchFlag == 1) {
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        refreshFile(openFileId,null,null,"1");
        layer.close(index);
        searchFlag = 0;
        $("#searchName").val("");
        $("#searchName").blur();
    }
    //$("#bb").val("初始值:"+ibe+" key:"+key);
}).keyup(function(){
    key=0;
});


function emptyChoose() {
    chooseFile = [];
    chooseFileType = [];
    chooseFileName = [];
    chooseFileAuthor=[]
}

function createAuthority(group,person){
    if(editFlag){          //上传还是编辑
        $(".edit-name-list").empty();
    }else {
        $(document.getElementById("0811").firstChild.contentWindow.document.getElementById("nametab")).empty();    //清空子页面的
    }
    for(var i=0;i<group.length;i++){
        var param= '<div class="name-item qz">'+
            '<p>'+group[i].name+'</p>'+
            '<span class="del_span" value="1"></span>'+
            '</div>'
        if(editFlag){
            $(".edit-name-list").append(param);
        }else {
            $(document.getElementById("0811").firstChild.contentWindow.document.getElementById("nametab")).append(param);
        }
    }
    for(var i=0;i<person.length;i++){
        var param= '<div class="name-item people">'+
            '<p>'+person[i].name+'</p>'+
            '<span class="del_span" value="0"></span>'+
            '</div>'
        if(editFlag){
            $(".edit-name-list").append(param);
        }else {
            $(document.getElementById("0811").firstChild.contentWindow.document.getElementById("nametab")).append(param);
        }
    }
    if(editFlag){
        $(".del_span").click(function () {
            if($(this).attr('value')==0){
                for(var i=0;i<personId.length;i++){
                    if(personId[i].name==$(this).prev().html()){
                        personId.splice(i,1);
                    }
                }
            }else {
                for(var i=0;i<groupId.length;i++){
                    if(groupId[i].name==$(this).prev().html()){
                        groupId.splice(i,1);
                    }
                }
            }
            $(this).parent(".name-item").remove();
        });
    }else {
        $(document.getElementById("0811").firstChild.contentWindow.document.getElementById("nametab")).find(".del_span").click(function () {
            if($(this).attr('value')==0){
                for(var i=0;i<personId.length;i++){
                    if(personId[i].name==$(this).prev().html()){
                        personId.splice(i,1);
                    }
                }
            }else {
                for(var i=0;i<groupId.length;i++){
                    if(groupId[i].name==$(this).prev().html()){
                        groupId.splice(i,1);
                    }
                }
            }
            $(this).parent(".name-item").remove();
        });
    }

}

function tryPop(th,id,type,name,index,author){
    if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile(openFileId)
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if($(th).prop("checked")){
        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)
    }else{
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFile=chooseFile.del(chooseFile.indexOf(id));
                chooseFileAuthor=  chooseFileAuthor.del(chooseFile.indexOf(id))
            }
        }
        if(chooseFile.length==1){
            var id = chooseFile[0];
            reNameParem=chooseFileName[0];
            var index=  $("#"+id+"").val();
            reNameIndex=index
        }
    }
    btnState();
    cancelBubble()
}
function btnState() {
    if(chooseFile.length==0){
        $('.clickBtn').hide()
        $(".webuploader-pick").hide();
        // if(isChild==false||(noChildPower==0&&adminFlag!=1)){
        //     $(".webuploader-pick").hide();
        // }
    }else {
        var flag=0;
        for(var i=0;i<chooseFileType.length;i++){
            if(chooseFileType[i]=="folder"){
                flag=1;
                break;

            }
        }
        if(flag=="1"){
            $('.clickBtn').hide()

        }else{
            $('.clickBtn').show()
            $('#manyMulDownLoad').hide();
            $(".webuploader-pick").hide()
            if(chooseFile.length>1){
                $('#updateName').hide();
                $('#mulDownLoad').hide();
                $('#manyMulDownLoad').show();

            }
            if(adminFlag!=1){
                $('#joinTopic').hide()
            }

        }

    }

    if(chooseFile.length==1&&chooseFileType[0]!='folder'){
        $(".webuploader-pick").show();
    }else{
        $(".webuploader-pick").hide();
    }
}

$(document).click(function(e){
    if($(e.target)[0]!=$(".moreicon")){
        $(".moreicon").hide();
    }

    if($(e.target)[0]==$('.file-container-flatten')[0] ||$(e.target)[0]==$('.content')[0]){
        if(reNameFlag==true){
            $('#name'+reNameIndex).removeClass("hide");
            $('#inputName'+reNameIndex).addClass("hide");
            reNameFlag=false;
            var inputname = $('#inputName'+reNameIndex).val().trim();
            if(inputname!=reNameParem){
                rename(inputname);
            }
        }
        $('.file').removeClass("active");
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", false);
        });
        emptyChoose();
        btnState();
    }

});
function  stopPop() {
    window.event? window.event.cancelBubble = true : e.stopPropagation();
}
function rename(inputname){
    if(chooseFileType[0]=='folder'){
        layer.msg("不能重命名目录", {anim:6,icon: 0});
        return;
    }
    var power= $("#authority"+chooseFile[0]).html()
    if(power!='2'&&noChildPower!=2&&adminFlag!=1){
        layer.msg("您没有权限重命名文件", {anim:6,icon: 0});
        return;
    }
    inputname = inputname.trim();

    if(inputname==''||inputname==undefined){
        layer.msg("目录名称或文件名称不能为空", {anim:6,icon: 0});
        $('#inputName'+reNameIndex).val(reNameParem);
        return;
    }
    var pattern = new RegExp("^[^/\\\\:\\*\\'\\’\\?\\<\\>\\|\"]{0,255}$");
    //特殊字符
    if(!pattern.test(inputname)){
        layer.msg("输入的文件名称不合法", {anim:6,icon: 0});
        $('#inputName'+reNameIndex).val(reNameParem);
        return;
    }
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajax({
            type:"post",
            url: Hussar.ctxPath+"/fsFile/addCheck",
            data:{
                name:inputname,
                filePid:openFileId,
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                if(data == "false"){
                    layer.msg("“"+inputname+"”文件已存在", {anim:6,icon: 0});
                    $('#inputName'+reNameIndex).val(reNameParem);
                    return;
                }else {
                    $.ajax({
                        type: "POST",
                        url: Hussar.ctxPath+"/fsFile/update",
                        data : {
                            ids:chooseFile[0],
                            fileName:inputname,
                            type:chooseFileType[0]
                        },
                        contentType:"application/x-www-form-urlencoded",
                        dataType:"json",
                        async: false,
                        success:function(result) {

                            refreshTree();
                        }
                    });
                }
            }
        });
    });
    refreshFile(openFileId);
}

//得到事件
function getEvent(){
    if(window.event)    {return window.event;}
    func=getEvent.caller;
    while(func!=null){
        var arg0=func.arguments[0];
        if(arg0){
            if((arg0.constructor==Event || arg0.constructor ==MouseEvent
                || arg0.constructor==KeyboardEvent)
                ||(typeof(arg0)=="object" && arg0.preventDefault
                    && arg0.stopPropagation)){
                return arg0;
            }
        }
        func=func.caller;
    }
    return null;
}
//阻止冒泡
function cancelBubble()
{
    var e=getEvent();
    if(window.event){
        //e.returnValue=false;//阻止自身行为
        e.cancelBubble=true;//阻止冒泡
    }else if(e.preventDefault){
        //e.preventDefault();//阻止自身行为
        e.stopPropagation();//阻止冒泡
    }
}
function getNameOrder() {
    refreshFile(openFileId,null,null,null,1);

}
function getNameOrder1() {

    refreshFile(openFileId,null,null,null,0);

}
function getTimeOrder() {

    refreshFile(openFileId, null, null, null, 3);
}

function getTimeOrder1() {
    refreshFile(openFileId, null, null, null, 2)
}
function iconSetTip(id,type,name,author) {
    cancelBubble()
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor = [];
    chooseFile.push(id);
    chooseFileType.push(type);
    chooseFileName.push(name);
    chooseFileAuthor.push(author)
    editFlag = true;
    groupId = [];
    groupParam = [];
    personId = [];
    personParam= [];

    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layer.open({
            type: 2,
            title: '标签设置',
            area: ['500px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath+'/fsFile/setTip',
            success:function(){
            }
        });
    });
}

function  iconDelete(id,name) {
    cancelBubble()
    layer.confirm('确定要删除所选文件吗？',function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        /* if(fileIdArr.length>0){
         $.ajax({
         type:"post",
         url:"/files/deleteFile",
         data:{
         ids:fileIdArrStr
         },
         async:false,
         cache:false,
         success:function(data){
         delFileFlag = true;
         },
         error:function () {
         delFileFlag = false;
         }
         })
         }*/

        var scopeId = id;
        layui.use(['Hussar'], function(){
            var Hussar = layui.Hussar;

            $.ajax({
                type:"post",
                url: Hussar.ctxPath+"/fsFile/deleteScope",
                data:{
                    fsFileIds:scopeId,
                },
                async:true,
                cache:false,
                success:function(data){
                    if(data> 0){
                        var fileList = $("#thelist").find(".item");
                        for(var n = 0;n<fileList.length;n++){
                            var name = fileList.eq(n).find(".info").html().split(".")[0];
                            for(var m =0 ;m<chooseFileName.length;m++){
                                if(name == chooseFileName[m]){
                                    fileList.eq(n).remove();
                                    uploader.removeFile( fileList.eq(n).attr("id"),true);
                                }
                            }
                        }
                        layui.Hussar.success('删除成功')
                    }else {
                        layui.Hussar.error('删除异常')
                    }
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                },
                error:function () {
                    layui.Hussar.error('删除异常!')
                    btnState();
                    refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })
        });
    })
}

function  iconUpdateName(id,type,name,author,index) {
    $(".hoverSpan").eq(index).hide()
    $(".moreicon").hide();
    $(".ishover").addClass("hide");
    $(".nameTitpe").removeClass("hide");
    cancelBubble();
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor = [];
    chooseFile.push(id);
    chooseFileType.push(type);
    chooseFileName.push(name);
    chooseFileAuthor.push(author)
    reNameIndex = index;
    reNameParem=name;
    $('#inputName'+index).val( chooseFileName[0]);
    $('#name'+index).addClass("hide");
    $('#inputName'+index).removeClass("hide");
    $('#inputName'+index).focus();
    reNameFlag=true;
}
function  iconMove(id,type,name,author) {
    cancelBubble()
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor = [];
    chooseFile.push(id);
    chooseFileType.push(type);
    chooseFileName.push(name);
    chooseFileAuthor.push(author)
    var operation =function(){
        layerView=layer.open({
            type : 1,
            area: ['350px','500px'],
            //shift : 1,
            shadeClose: false,
            title : '目录结构',
            maxmin : true,
            content : $("#filTree"),
            success : function() {
                initFileTree();
                layer.close(index);
            },
            end: function () {
                layer.closeAll(index);
            }
        });

    }
    var index = layer.confirm('确定要移动所选文件吗？',operation);
    cutFile=[].concat(chooseFile);
    cutFileType=[].concat(chooseFileType);
    cutFileName=[].concat(chooseFileName);
}
function  iconDownLoad(id,name) {
    cancelBubble()
    var index = layer.load(1, {
        shade: [0.1,'#fff'] ,//0.1透明度的白色背景
        scrollbar: false,
        time:1000
    });

    download(id,name);
}

function iconSetAuthority(id,type,name,author) {
    cancelBubble();
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor = [];
    chooseFile.push(id);
    chooseFileType.push(type);
    chooseFileName.push(name);
    chooseFileAuthor.push(author)
    editFlag = true;
    groupId = [];
    groupParam = [];
    personId = [];
    personParam= [];
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layer.open({
            type: 2,
            title: '文件授权',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath+'/fsFile/fileAuthority',
            success:function(){
            }
        });
    });
}