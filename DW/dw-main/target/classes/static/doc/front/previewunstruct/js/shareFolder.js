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
var folderAmount;
var key='';
var authority ="";
var adminFlag;
var noChildPowerFolder=0;
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
var scrollHeight=0;
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
var currOrder = '';
var searchFlag=0;
var scrollHeightAlert=0;
var scrollHeightLong=0;
var scrollHeightShare = 0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var currPage = 1;
var oldDocId = '';
var newDocId = '';
layui.extend({
    admin: '{/}../../../static/resources/weadmin/static/js/admin'
});
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar','element'], function() {
    var $ = layui.jquery,
        form=layui.form,
        jstree=layui.jstree,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;
    hussar=layui.Hussar;
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
        if (chooseFileType[0]=="folder"){
            if (noChildPowerFolder == 0) {
                layer.msg("您没有删除目录权限", {anim: 6, icon: 0,offset:scrollHeightMsg});
                return;
            }

            var folderIdArrStr = chooseFile.join(",");
            /*检查目录下是否有文件（递归查）*/
            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/fsFolder/checkFolderType",
                data: {
                    ids: folderIdArrStr
                },
                async: false,
                cache: false,
                success: function (data) {
                    if (data == 'haveFile') {
                        layer.msg("请先删除目录下存放的文件", {anim: 6, icon: 0,offset:scrollHeightMsg});
                        return;
                    } else {
                        layer.confirm('确定要删除所选目录吗？',{title :['删除','background-color:#fff'],offset:scrollHeightAlert,}, function () {
                            var index = layer.load(1, {
                                shade: [0.1, '#fff'] //0.1透明度的白色背景
                            });
                            $.ajax({
                                type: "post",
                                url: Hussar.ctxPath+"/fsFolder/delete",
                                data: {
                                    fsFolderIds: folderIdArrStr,
                                },
                                async: false,
                                cache: false,
                                success: function (data) {
                                    if (data > 0) {
                                        layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                                    }
                                },
                                error: function () {

                                    layer.msg("删除失败", {icon: 2,offset:scrollHeightMsg});

                                }
                            })
                            for (var i = 0; i < chooseFile.length; i++) {
                                for (var j = 0; j < cutFile.length; j++) {
                                    if (cutFile[j] == chooseFile[i]) {
                                        cutFile = cutFile.del(j);
                                        break;
                                    }
                                }
                            }

                            refreshFile(openFileId);
                            emptyChoose();
                            layer.close(index);
                        })
                    }
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFolder/checkFolderType", function(data) {
                if (data == 'haveFile') {
                    layer.msg("请先删除目录下存放的文件", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    return;
                } else {
                    layer.confirm('确定要删除所选目录吗？',{title :['删除','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'}, function () {
                        var index = layer.load(1, {
                            shade: [0.1, '#fff'] //0.1透明度的白色背景
                        });
                        /*$.ajax({
                            type: "post",
                            url: Hussar.ctxPath+"/fsFolder/delete",
                            data: {
                                fsFolderIds: folderIdArrStr,
                            },
                            async: false,
                            cache: false,
                            success: function (data) {
                                if (data > 0) {
                                    layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                                }
                            },
                            error: function () {

                                layer.msg("删除失败", {icon: 2,offset:scrollHeightMsg});

                            }
                        })*/
                        var ajax = new $ax(Hussar.ctxPath + "/fsFolder/delete", function(data) {
                            if (data > 0) {
                                layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                            }
                        }, function(data) {
                            layer.msg("删除失败", {icon: 2,offset:scrollHeightMsg});
                        });
                        ajax.set("fsFolderIds",folderIdArrStr);
                        ajax.start();
                        for (var i = 0; i < chooseFile.length; i++) {
                            for (var j = 0; j < cutFile.length; j++) {
                                if (cutFile[j] == chooseFile[i]) {
                                    cutFile = cutFile.del(j);
                                    break;
                                }
                            }
                        }

                        refreshFile(openFileId);
                        emptyChoose();
                        layer.close(index);
                    })
                }
            }, function(data) {

            });
            ajax.set("ids",folderIdArrStr);
            ajax.start();

        }else{
            for(var i=0;i<chooseFile.length;i++){
                var power= $("#authority"+chooseFile[i]).html()
                if(power!='2'&&noChildPower!=2&&adminFlag!=1){
                    layer.msg("您没有权限删除文件", {anim:6,icon: 0,offset:scrollHeightMsg});
                    return;
                }

            }
            if(chooseFile.length==0){
                layer.msg("请选择要删除的文件", {anim:6,icon: 0,offset:scrollHeightMsg});
                return;
            }

            layer.confirm('确定要删除所选文件吗？',{title :['删除','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},function(){
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
                /*$.ajax({
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
                            layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                        }else {

                            layer.msg("删除异常", {icon: 2,offset:scrollHeightMsg});
                        }
                        btnState();
                        // refreshTree();
                        refreshFile(openFileId);
                        emptyChoose();
                        layer.close(index);
                    },
                    error:function () {
                        layer.msg("删除异常", {icon: 2,offset:scrollHeightMsg});
                        btnState();
                        //  refreshTree();
                        refreshFile(openFileId);
                        emptyChoose();
                        layer.close(index);
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/fsFile/deleteScope", function(data) {
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
                        layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                    }else {

                        layer.msg("删除异常", {icon: 2,offset:scrollHeightMsg});
                    }
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }, function(data) {
                    layer.msg("删除异常", {icon: 2,offset:scrollHeightMsg});
                    btnState();
                    //  refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                });
                ajax.set("fsFileIds",scopeId);
                ajax.start();
            })
        }

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
        if (chooseFileType[0]=="folder"){
            if (noChildPowerFolder == 0) {
                layer.msg("您没有移动目录权限", {anim: 6, icon: 0,offset:scrollHeightMsg});
                return;
            }

            var operation = function () {
                layerView = layer.open({
                    type: 1,
                    area: ['350px', '510px'],
                    //shift : 1,
                    shadeClose: false,
                    title: ['目录结构','background-color:#fff'],
                    maxmin: false,
                    skin: 'move-class',
                    offset:scrollHeightLong,
                    content: $("#folderTreeAuthority"),
                    success: function () {
                        initFolderTree();
                        layer.close(index);
                    },
                    end: function () {
                        layer.closeAll(index);
                    }
                });
            }
            var index = layer.confirm('确定要移动所选目录吗？',{title :['移动','background-color:#fff'], offset:scrollHeightAlert,skin:'move-confirm'}, operation);
            cutFile=[].concat(chooseFile);
            cutFileType=[].concat(chooseFileType);
            cutFileName=[].concat(chooseFileName);

        }else{
            for(var i=0;i<chooseFile.length;i++){
                var power= $("#authority"+chooseFile[i]).html()
                if(power!='2'&&noChildPower!=2&&adminFlag!=1){
                    layer.msg("您没有权限移动文件", {anim:6,icon: 0,offset:scrollHeightMsg});
                    return;
                }


            }

            var operation =function(){
                layerView=layer.open({
                    type : 1,
                    area: ['350px','510px'],
                    //shift : 1,
                    shadeClose: false,
                    title : ['目录结构','background-color:#fff'],
                    maxmin : false,
                    offset:scrollHeightLong,
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
            var index = layer.confirm('确定要移动所选文件吗？',{title :['移动','background-color:#fff'], offset:scrollHeightAlert,skin:'move-confirm'},operation);
            cutFile=[].concat(chooseFile);
            cutFileType=[].concat(chooseFileType);
            cutFileName=[].concat(chooseFileName);

        }


    });
    // /*粘贴*/
    // $("#pasteFile").on('click',function(){
    //
    //     if(isChild==false){
    //         layer.msg("请选择最小文件夹进行粘贴", {anim:6,icon: 0,offset:scrollHeightMsg});
    //         return;
    //     }
    //     if(cutFile.length <= 0){
    //         layer.close(index);
    //         layer.msg("请先选择要剪切的文件", {anim:6,icon: 0,offset:scrollHeightMsg});
    //         return;
    //     }
    //     for(var i=0;i<chooseFile.length;i++){
    //         var power= $("#authority"+chooseFile[i]).html()
    //         if(power!='2'&&noChildPower!=2&&adminFlag!=1){
    //             layer.msg("您没有权限粘贴文件", {anim:6,icon: 0,offset:scrollHeightMsg});
    //             return;
    //         }
    //
    //         if (chooseFileType[i]=="folder"){
    //             layer.msg(folderMessage, {anim:6,icon: 0,offset:scrollHeightMsg});
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
    //                 layer.msg("存在重名文件", {anim:6,icon: 0,offset:scrollHeightMsg});
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
    //                                 layer.msg("目标文件夹是剪切文件夹的子文件夹", {anim:6,icon: 0,offset:scrollHeightMsg});
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
                layer.msg("您没有权限设置标签", {anim:6,icon: 0,offset:scrollHeightMsg});
                return;
            }
            if (chooseFileType[i]=="folder"){
                layer.msg(folderMessage, {anim:6,icon: 0,offset:scrollHeightMsg});
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
            title: ['标签设置','background-color:#fff'],
            area: ['650px', '250px'], //宽高
            fix: false, //不固定
            maxmin: false,
            offset:scrollHeightTip,
            content: Hussar.ctxPath+'/fsFile/setTip',
            success:function(){
            }
        });


    });
    /* 批量设置分享*/
    $("#setShare").on('click', function () {
        for(var i=0;i<chooseFile.length;i++){
            var power= $("#authority"+chooseFile[i]).html()
            if(power!='2'&&noChildPower!=2&&adminFlag!=1){
                layer.msg("您没有权限设置分享", {anim:6,icon: 0,offset:scrollHeightMsg});
                return;
            }

        }
        if(chooseFile.length==0){
            layer.msg("请选择要设置分享的文件", {anim:6,icon: 0,offset:scrollHeightMsg});
            return;
        }
        var url="";
        if(chooseFile.length>1){
            url="/fsFile/shareFlagView?docIds="+chooseFile.toString();
        }else{
            url="/fsFile/shareFlagView?docId="+chooseFile.toString();
        }
        layer.open({
            type: 2,
            area: [300 + 'px', 200 + 'px'],
            fix: false, //不固定
            maxmin: false,
            offset:parseInt(scrollHeightTip) + 25 + "px",
            shadeClose: true,
            shade: 0.4,
            title: ['修改分享权限','background-color:#fff'],
            content: Hussar.ctxPath+url
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
            title: ['文件授权','background-color:#fff'],
            area: ['686px', '510px'], //宽高
            fix: false, //不固定
            maxmin: false,
            offset:scrollHeightLong,
            content: Hussar.ctxPath+'/fsFile/fileAuthority',
            success:function(){
            } ,end: function(){
                refreshFile(openFileId)
            }
        });


    });

    /*新增子目录*/
    $("#newFolder").on('click', function () {
        if (noChildPowerFolder == 0) {
            layer.msg("您没有创建目录权限", {anim: 6, icon: 0,offset:scrollHeightMsg});
            return;
        }
        editFlag = false;
        var folderAmountNum = parseInt(folderAmount)+1;
        if (pathId.length >= folderAmountNum) {
            layer.msg("目录最多为"+folderAmount+"级", {anim: 6, icon: 0,offset:scrollHeightMsg});
            return false;
        }
        groupId = [];
        groupParam = [];
        personId = [];
        personParam = [];
        groupIdPower = [];
        personIdPower = [];
        personParamPower = [];
        groupParamPower = [];
        $("#categoryName").val("");
        $('.name-list').empty();
        form.render();
        layer.open({
            type: 1,
            btn: ['保存', '取消'],
            area: ['60%', '300px'],
            skin: 'confirm-class',
            fix: false, //不固定
            maxmin: false,
            offset:parseInt(scrollHeightTip) - 25 + "px",
            shadeClose: false,
            shade: 0.4,
            title: ["创建目录",'background-color:#fff'],
            content: $('#addDiv'),
            btn1: function (index, layero) {
                var categoryName = $("#categoryName").val().trim();
                var isEdit = $("input[name='isEdit']:checked").val();
                if (isEdit != undefined && isEdit == 'on') {
                    isEdit = '0'
                } else {
                    isEdit = '';
                }
                var isChild = $("input[name='isChild']:checked").val();
                if (isChild != undefined && isChild == 'on') {
                    isEdit = isEdit + "1";
                } else {
                    isEdit = isEdit + "";
                }
                var visible = $("input[name='visible']:checked").val();

                if (categoryName.length <= 0) {
                    layer.msg("目录名称不能为空", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    return false;
                }
                if (categoryName.length > 200) {
                    layer.msg("目录名称不能超过200个字符", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    return false;
                }
                var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
                //特殊字符
                if (!pattern.test(categoryName)) {
                    layer.msg("输入的目录名称不合法", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    return;
                }

                if (groupId.length == 0 && personId.length == 0) {
                    visible = '0';
                } else {
                    visible = '1';
                }
                var groupStr = '';
                var personStr = '';
                var personOrganStr = '';
                var group = [];
                var person = [];
                var personOrgan = [];
                var authorTypeGroup = [];
                var authorTypePerson = [];
                var authorTypeStrGroup = '';
                var authorTypeStrPerson = '';
                var operateTypeGroup = [];
                var operateTypePerson = [];
                var operateTypeStrGroup = '';
                var operateTypeStrPerson = '';
                if (groupId != undefined) {
                    for (var i = 0; i < groupId.length; i++) {
                        group.push(groupId[i].id);
                        authorTypeGroup.push(groupId[i].type);
                        operateTypeGroup.push(groupId[i].operateType)
                    }
                    groupStr = group.join(",")
                    authorTypeStrGroup = authorTypeGroup.join(",");
                    operateTypeStrGroup = operateTypeGroup.join(",");
                }
                if (personId != undefined) {
                    for (var i = 0; i < personId.length; i++) {
                        person.push(personId[i].id);
                        personOrgan.push(personId[i].organId);
                        authorTypePerson.push(personId[i].type);
                        operateTypePerson.push(personId[i].operateType);
                    }
                    personStr = person.join(",")
                    personOrganStr = personOrgan.join(",")
                    authorTypeStrPerson = authorTypePerson.join(",")
                    operateTypeStrPerson = operateTypePerson.join(",");
                }
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/fsFolder/addCheck",
                    data: {
                        name: categoryName,
                        parentFolderId: openFileId,
                    },
                    async: false,
                    cache: false,
                    dataType: "json",
                    success: function (data) {
                        if (data == "false") {
                            layer.msg("“" + categoryName + "”目录已存在", {anim: 6, icon: 0,offset:scrollHeightMsg});
                            return;
                        } else {
                            $.ajax({
                                type: "POST",
                                url: Hussar.ctxPath+"/fsFolder/add",
                                data: {
                                    parentFolderId: openFileId,
                                    folderName: categoryName,
                                    visible: visible,
                                    isEdit: isEdit,
                                    group: groupStr,
                                    person: personStr,
                                    personOrgan: personOrganStr,
                                    authorTypeStrGroup: authorTypeStrGroup,
                                    authorTypeStrPerson: authorTypeStrPerson,
                                    operateTypeStrGroup: operateTypeStrGroup,
                                    operateTypeStrPerson: operateTypeStrPerson
                                },
                                contentType: "application/x-www-form-urlencoded",
                                dataType: "json",
                                async: false,
                                success: function (result) {
                                    refreshFile(openFileId);
                                    // refreshTree();
                                    layer.closeAll();
                                }
                            });
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/fsFolder/addCheck", function(data) {
                    if (data == "false") {
                        layer.msg("“" + categoryName + "”目录已存在", {anim: 6, icon: 0,offset:scrollHeightMsg});
                        return;
                    } else {
                        /*$.ajax({
                            type: "POST",
                            url: Hussar.ctxPath+"/fsFolder/add",
                            data: {
                                parentFolderId: openFileId,
                                folderName: categoryName,
                                visible: visible,
                                isEdit: isEdit,
                                group: groupStr,
                                person: personStr,
                                personOrgan: personOrganStr,
                                authorTypeStrGroup: authorTypeStrGroup,
                                authorTypeStrPerson: authorTypeStrPerson,
                                operateTypeStrGroup: operateTypeStrGroup,
                                operateTypeStrPerson: operateTypeStrPerson
                            },
                            contentType: "application/x-www-form-urlencoded",
                            dataType: "json",
                            async: false,
                            success: function (result) {
                                refreshFile(openFileId);
                                // refreshTree();
                                layer.closeAll();
                            }
                        });*/
                        var ajax = new $ax(Hussar.ctxPath + "/fsFolder/add", function(result) {
                            refreshFile(openFileId);
                            // refreshTree();
                            layer.closeAll();
                        }, function(data) {

                        });
                        ajax.set("parentFolderId",openFileId);
                        ajax.set("folderName",categoryName);
                        ajax.set("visible",visible);
                        ajax.set("isEdit",isEdit);
                        ajax.set("group",groupStr);
                        ajax.set("person",personStr);
                        ajax.set("personOrgan",personOrganStr);
                        ajax.set("authorTypeStrGroup",authorTypeStrPerson);
                        ajax.set("authorTypeStrPerson",authorTypeStrPerson);
                        ajax.set("operateTypeStrGroup",operateTypeStrGroup);
                        ajax.set("operateTypeStrPerson",operateTypeStrPerson);
                        ajax.start();
                    }
                }, function(data) {

                });
                ajax.set("name",categoryName);
                ajax.set("parentFolderId",openFileId);
                ajax.start();
            },
        });
    });
    /*修改*/
    $("#reName").on('click',function(){

        if (chooseFileType[0]=="folder"){
            if (noChildPowerFolder == 0) {
                layer.msg("您没有授权目录权限", {anim: 6, icon: 0,offset:scrollHeightMsg});
                return;
            }

            layer.open({
                type: 2,
                title: ['目录授权','background-color:#fff'],
                area: ['686px', '510px'], //宽高
                fix: false, //不固定
                offset:scrollHeightLong,
                //maxmin: false,
                content: Hussar.ctxPath + '/fsFolder/folderAuthority',
                success: function () {
                }
            });
        }else{
            for(var i=0;i<chooseFile.length;i++){
                var power= $("#authority"+chooseFile[i]).html()
                if(power!='2'&&noChildPower!=2&&adminFlag!=1){
                    layer.msg("您没有权限设置文件", {anim:6,icon: 0,offset:scrollHeightMsg});
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
                    title: ['文件授权','background-color:#fff'],
                    area: ['686px', '510px'], //宽高
                    fix: false, //不固定
                    //maxmin: false,
                    offset:scrollHeightLong,
                    content: Hussar.ctxPath+'/fsFile/fileAuthority',
                    success:function(){
                    }
                });

            }
        }

    });


    function editFolder(){
        /*$.ajax({
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
                    btn: ['取消','保存'],
                    area: ['360px','350px'],
                    skin: 'confirm-class',
                    fix: false, //不固定
                    maxmin: false,
                    offset:parseInt(scrollHeightTip) - 50 + "px",
                    shadeClose: false,
                    shade: 0.4,
                    title: ["修改目录",'background-color:#fff'],
                    content: $('#addDiv'),
                    btn2: function(index, layero){
                        var categoryName = $("#categoryName").val().trim();
                        var levelId = $("#levelId").val();
                        if(categoryName.length<=0){
                            layer.msg("目录名称不能为空", {anim:6,icon: 0,offset:scrollHeightMsg});
                            return false;
                        }
                        if(categoryName.length>30){
                            layer.msg("目录名称不能超过30", {anim:6,icon: 0,offset:scrollHeightMsg});
                            return false;
                        }
                        var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
                        //特殊字符
                        if(!pattern.test(categoryName)){
                            layer.msg("输入的文件名称不合法", {anim:6,icon: 0,offset:scrollHeightMsg});
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
                                    //    refreshTree();
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
                                        layer.msg("“"+categoryName+"”目录已存在", {anim:6,icon: 0,offset:scrollHeightMsg});
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
                                                //   refreshTree();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    },
                });
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getFsFolderDetail", function(data) {
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
                btn: ['取消','保存'],
                area: ['360px','350px'],
                skin: 'confirm-class',
                fix: false, //不固定
                maxmin: false,
                offset:parseInt(scrollHeightTip) - 50 + "px",
                shadeClose: false,
                shade: 0.4,
                title: ["修改目录",'background-color:#fff'],
                content: $('#addDiv'),
                btn2: function(index, layero){
                    var categoryName = $("#categoryName").val().trim();
                    var levelId = $("#levelId").val();
                    if(categoryName.length<=0){
                        layer.msg("目录名称不能为空", {anim:6,icon: 0,offset:scrollHeightMsg});
                        return false;
                    }
                    if(categoryName.length>130){
                        layer.msg("目录名称不能超过130", {anim:6,icon: 0,offset:scrollHeightMsg});
                        return false;
                    }
                    var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
                    //特殊字符
                    if(!pattern.test(categoryName)){
                        layer.msg("输入的文件名称不合法", {anim:6,icon: 0,offset:scrollHeightMsg});
                        return;
                    }
                    if(levelId == '1'){

                    }
                    if(oldName == categoryName){
                        /*$.ajax({
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
                                //    refreshTree();
                            }
                        });*/
                        var ajax = new $ax(Hussar.ctxPath + "/fsFile/add", function(result) {
                            refreshFile(openFileId);
                            //    refreshTree();
                        }, function(data) {

                        });
                        ajax.set("filePid",openFileId);
                        ajax.set("fileName",categoryName);
                        ajax.set("levelId",levelId);
                        ajax.set("fileId",fileId);
                        ajax.set("filePid",filePid);
                        ajax.set("fileType",fileType);
                        ajax.start();
                    }else{
                        /*$.ajax({
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
                                    layer.msg("“"+categoryName+"”目录已存在", {anim:6,icon: 0,offset:scrollHeightMsg});
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
                                            //   refreshTree();
                                        }
                                    });
                                }
                            }
                        });*/
                        var ajax = new $ax(Hussar.ctxPath + "/fsFile/addCheck", function(data) {
                            if(data == "false"){
                                layer.msg("“"+categoryName+"”目录已存在", {anim:6,icon: 0,offset:scrollHeightMsg});
                            }else{
                                /*$.ajax({
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
                                        //   refreshTree();
                                    }
                                });*/
                                var ajax = new $ax(Hussar.ctxPath + "/fsFile/add", function(result) {
                                    refreshFile(openFileId);
                                    //   refreshTree();
                                }, function(data) {

                                });
                                ajax.set("filePid",openFileId);
                                ajax.set("fileName",categoryName);
                                ajax.set("levelId",levelId);
                                ajax.set("fileId",fileId);
                                ajax.set("filePid",filePid);
                                ajax.set("fileType",fileType);
                                ajax.start();
                            }
                        }, function(data) {

                        });
                        ajax.set("name",categoryName);
                        ajax.set("filePid",openFileId);
                        ajax.start();
                    }
                },
            });
        }, function(data) {

        });
        ajax.set("fsFileId",chooseFile[0]);
        ajax.start();
    }


    $("#setEditAuthority").click(function(){
        layer.open({
            type: 2,
            title: '选择可见范围',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: false,
            offset:scrollHeightLong,
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
            btn: ['取消','保存'],
            area: ['360px','300px'],
            skin: 'confirm-class',
            fix: false, //不固定
            maxmin: false,
            offset:parseInt(scrollHeightTip) - 25 + "px",
            shadeClose: false,
            shade: 0.4,
            title: "创建目录",
            content: $('#addDiv'),
            btn2: function(index, layero){
                var categoryName = $("#categoryName").val().trim();
                var levelId = $("#levelId").val();
                if(categoryName.length<=0){
                    layer.msg("目录名称不能为空", {anim:6,icon: 0,offset:scrollHeightMsg});
                    return false;
                }
                if(categoryName.length>130){
                    layer.msg("目录名称不能超过130", {anim:6,icon: 0,offset:scrollHeightMsg});
                    return false;
                }
                var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
                //特殊字符
                if(!pattern.test(categoryName)){
                    layer.msg("输入的文件名称不合法", {anim:6,icon: 0,offset:scrollHeightMsg});
                    return;
                }
                if(levelId == '1'){
                    if(groupId.length==0&&personId.length==0){
                        layer.msg("请给保密文档设置权限", {anim:6,icon: 0,offset:scrollHeightMsg});
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
                /*$.ajax({
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
                            layer.msg("“"+categoryName+"”目录已存在", {anim:6,icon: 0,offset:scrollHeightMsg});
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
                                    /!*,description:categoryDesc,*!/
                                },
                                contentType:"application/x-www-form-urlencoded",
                                dataType:"json",
                                async: false,
                                success:function(result) {
                                    refreshFile(openFileId);
                                    //refreshTree();
                                }
                            });
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/fsFile/addCheck", function(data) {
                    if(data == "false"){
                        layer.msg("“"+categoryName+"”目录已存在", {anim:6,icon: 0,offset:scrollHeightMsg});
                        return;
                    }else {
                        /*$.ajax({
                            type: "POST",
                            url: Hussar.ctxPath+"/fsFile/add",
                            data : {
                                filePid:openFileId,
                                fileName:categoryName,
                                levelId:levelId,
                                group:groupStr,
                                person:personStr
                                /!*,description:categoryDesc,*!/
                            },
                            contentType:"application/x-www-form-urlencoded",
                            dataType:"json",
                            async: false,
                            success:function(result) {
                                refreshFile(openFileId);
                                //refreshTree();
                            }
                        });*/
                        var ajax = new $ax(Hussar.ctxPath + "/fsFile/add", function(result) {
                            refreshFile(openFileId);
                            //refreshTree();
                        }, function(data) {

                        });
                        ajax.set("filePid",openFileId);
                        ajax.set("fileName",categoryName);
                        ajax.set("levelId",levelId);
                        ajax.set("group",groupStr);
                        ajax.set("person",personStr);
                        ajax.start();
                    }
                }, function(data) {

                });
                ajax.set("name",categoryName);
                ajax.set("filePid",openFileId);
                ajax.start();
            },
        });
    });
    /*上传*/

    setTimeout(function () {
        $(".webuploader-pick").on("click",function () {
            personParam = [];
            groupParam = [];
            editFlag = false;
            buttonType = 'upload';
            groupId = [];
            personId = [];
            // count=0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
            // success=0;//上传成功的文件数
            // filesArr=[];

            if(categoryId==undefined){
                $(".shadow").hide();
                //$(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("请先选择目录", {anim:6,icon: 0,offset:scrollHeightMsg});

                return;
            }
            $(this).next().find("label").click()
        });
        $(".uploadBtn").on("click",function () {
            personParam = [];
            groupParam = [];
            editFlag = false;
            buttonType = 'upload';
            groupId = [];
            personId = [];
            // count=0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
            // success=0;//上传成功的文件数
            // filesArr=[];
            // if(isChild==false){
            //
            //     layer.msg("请选择最小文件夹进行上传", {anim:6,icon: 0,offset:scrollHeightMsg});
            //
            //     return;
            // }
            if(noChildPower==0&&adminFlag!=1){

                layer.msg("您没有上传文件权限", {anim:6,icon: 0,offset:scrollHeightMsg});
                return;
            }

            // setTimeout(function ( ) {  window.parent.initHeight();},300)

            // 传递参数
            var path = '';
            var elem = $("#path span");
            for (var i in elem){
                path += elem.eq(i).text();
            }
            path = path.replace(/\s/g,'');
            if(path.indexOf(">")==-1){
                layer.msg("请不要在根目录上传文件", {anim:6,icon: 0,offset:scrollHeightMsg});
                return;
            }
            path = path.substring(path.indexOf(">") + 1);
            window.parent.open("/frontUpload/uploadFile?openFileId=" + openFileId + "&path=" +  encodeURIComponent(encodeURIComponent(path)) +"&" + Math.random(),"mainFrame");

        });
    },300);


    /*多选下载*/
    $("#mulDownLoad, #manyMulDownLoad").on('click',function(){
        if(chooseFile.length==0){
            layer.msg("请选择要下载的文件", {anim:6,icon: 0,offset:scrollHeightMsg});
            return;
        }
        for (var i = 0; i < chooseFile.length; i++) {
            if (chooseFileType[i]==="folder"){
                layer.msg("无法下载目录文件", {anim:6,icon: 0,offset:scrollHeightMsg});
                return;
            }
            var power= $("#authority"+chooseFile[i]).html()
            if(power!='1'&&power!='2'&&noChildPower!=2&&adminFlag!=1){
                layer.msg("权限不足，无法下载", {anim:6,icon: 0,offset:scrollHeightMsg});
                return;
            }

        }
        var ids = chooseFile.join(",");
        /*$.ajax({
            url: Hussar.ctxPath+"/integral/downloadIntegral",
            async: true,
            data: {
                docId: ids,
                ruleCode: 'download'
            },
            success: function (data) {
                if (data.status == "1") {
                    var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                        icon: 3,
                        title: '提示',
                        offset: scrollHeightAlert
                    }, function (index) {
                        layer.close(index2);
                        var index = layer.load(1, {
                            shade: [0.1, '#fff'],//0.1透明度的白色背景
                            scrollbar: false,
                            time: 1000
                        });
                        var ids = chooseFile.join(",");
                        var name = chooseFileName.join("*");

                                        download(ids, name);


                    })

                } else {
                    var index = layer.load(1, {
                        shade: [0.1, '#fff'],//0.1透明度的白色背景
                        scrollbar: false,
                        time: 1000
                    });
                    var ids = chooseFile.join(",");
                    var name = chooseFileName.join("*");

                                    download(ids, name);




                }
            }
        });       // layer.close(index);*/
        var ajax = new $ax(Hussar.ctxPath + "/integral/downloadIntegral", function(data) {
            if (data.status == "1") {
                var index2 = layer.confirm('下载文件将扣除'+data.integral+'积分，是否确认下载？', {
                    icon: 3,
                    title: '提示',
                    offset: scrollHeightAlert,
                    skin:'download-info'
                }, function (index) {
                    layer.close(index2);
                    var index = layer.load(1, {
                        shade: [0.1, '#fff'],//0.1透明度的白色背景
                        scrollbar: false,
                        time: 1000
                    });
                    var ids = chooseFile.join(",");
                    var name = chooseFileName.join("*");

                    download(ids, name);


                })

            } else {
                var index = layer.load(1, {
                    shade: [0.1, '#fff'],//0.1透明度的白色背景
                    scrollbar: false,
                    time: 1000
                });
                var ids = chooseFile.join(",");
                var name = chooseFileName.join("*");

                download(ids, name);




            }
        }, function(data) {

        });
        ajax.set("docId",ids);
        ajax.set("ruleCode",'download');
        ajax.start();
    });
    /*加入专题*/
    $("#joinTopic").on('click',function(){
        if(chooseFile.length == 0){
            layer.msg("请选择要加入专题的目录或文件", {anim:6,icon: 0,offset:scrollHeightMsg});
            return;
        }else {
            var title = ["加入专题",'background-color:#fff'];
            var url = "/topicDoc/topicDocAdd?chooseFile="+chooseFile +"&chooseFileType="+chooseFileType ;
            docAddOpen=  layer.open({
                type: 2,
                area: [620 + 'px', 510 + 'px'],
                fix: false, //不固定
                maxmin: false,
                offset:scrollHeightLong,
                shadeClose: true,
                shade: 0.4,
                title: title,
                content: Hussar.ctxPath+url
            });
        }
    })

    $("#more").on('focus',function () {

    })


    //积分免除
    $("#exempt").on('click',function () {
        var scopeId = chooseFile.join(',')
        var type=chooseFileType.join(',')
        var fileName=chooseFileName.join(',')
        if (chooseFile.length == 0) {
            layer.msg("请选择文件或文件夹", {anim: 6, icon: 0, offset: scrollHeightMsg});
            return;
        }else {
            var ids=chooseFile.join(",");
            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+"/exempt/addCheck",
                data:{
                    ids:ids
                },
                async:false,
                cache:false,
                dataType:"json",
                success:function(data){
                    if(data == "false"){
                        if(chooseFileType[0]=="folder"){
                            layer.msg("选中文件夹中存在已免除积分文件夹", {anim:6,icon: 0,offset:scrollHeightMsg});
                            return;
                        }else{
                            layer.msg("选中文件中存在已免除积分文件", {anim:6,icon: 0,offset:scrollHeightMsg});
                            return;
                        }

                    }else {
                        $.ajax({
                            type: "post",
                            url: Hussar.ctxPath+"/exempt/exemptAdd",
                            data: {
                                fsFolderIds: scopeId,
                                type: type,
                                fileName:fileName,
                            },
                            async: true,
                            cache: false,
                            success:function(result) {
                                layer.msg("免除成功", {icon: 1,offset:scrollHeightMsg});
                                //refreshTree();
                            }
                            ,
                        })
                    }
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/exempt/addCheck", function(data) {
                // if(data == "false"){
                //     if(chooseFileType[0]=="folder"){
                //         layer.msg("文件夹已免除积分", {anim:6,icon: 0,offset:scrollHeightMsg});
                //         return;
                //     }else{
                //         layer.msg("部分文件已免除积分", {anim:6,icon: 0,offset:scrollHeightMsg});
                //         return;
                //     }
                //
                // }else {
                /*$.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/exempt/exemptAdd",
                    data: {
                        fsFolderIds: scopeId,
                        type: type,
                        fileName:fileName,
                    },
                    async: true,
                    cache: false,
                    success:function(result) {
                        layer.msg("免除成功", {icon: 1,offset:scrollHeightMsg});
                        //refreshTree();
                    }
                    ,
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/exempt/exemptAdd", function(result) {
                    layer.msg("免除成功", {icon: 1,offset:scrollHeightMsg});
                    //refreshTree();
                }, function(data) {

                });
                ajax.set("fsFolderIds",scopeId);
                ajax.set("type",type);
                ajax.set("fileName",fileName);
                ajax.start();
                // }
            }, function(data) {

            });
            ajax.set("ids",ids);
            ajax.start();
        }
    })


    $("#setTop").on('click',function(){

        if(chooseFile.length == 0){
            layer.msg("请选择要置顶的文件", {anim:6,icon: 0,offset:scrollHeightMsg});
            return;
        }else {
            var ids=chooseFile.join(",");
            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+"/top/addCheck",
                data:{
                    ids:ids
                },
                async:false,
                cache:false,
                dataType:"json",
                success:function(data){
                    if(data == "false"){
                        layer.msg("存在已经置顶的文件", {anim:6,icon: 0,offset:scrollHeightMsg});
                        return;
                    }else {
                        $.ajax({
                            type: "POST",
                            url: Hussar.ctxPath+"/top/add",
                            data : {
                                ids:ids
                                /!*,description:categoryDesc,*!/
                            },
                            contentType:"application/x-www-form-urlencoded",
                            dataType:"json",
                            async: false,
                            success:function(result) {
                                layer.msg("置顶成功", {icon: 1,offset:scrollHeightMsg});
                                //refreshTree();
                            }
                        });
                    }
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/top/addCheck", function(data) {
                if(data == "false"){
                    layer.msg("存在已经置顶的文件", {anim:6,icon: 0,offset:scrollHeightMsg});
                    return;
                }else {
                    /*$.ajax({
                        type: "POST",
                        url: Hussar.ctxPath+"/top/add",
                        data : {
                            ids:ids
                            /!*,description:categoryDesc,*!/
                        },
                        contentType:"application/x-www-form-urlencoded",
                        dataType:"json",
                        async: false,
                        success:function(result) {
                            layer.msg("置顶成功", {icon: 1,offset:scrollHeightMsg});
                            //refreshTree();
                        }
                    });*/
                    var ajax = new $ax(Hussar.ctxPath + "/top/add", function(result) {
                        layer.msg("置顶成功", {icon: 1,offset:scrollHeightMsg});
                        //refreshTree();
                    }, function(data) {

                    });
                    ajax.set("ids",ids);
                    ajax.start();
                }
            }, function(data) {

            });
            ajax.set("ids",ids);
            ajax.start();
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
        getUserTree();
        btnState()
    }
    $("#setAuthorityFolder").click(function () {
        layer.open({
            type: 2,
            title: ['目录授权','background-color:#fff'],
            area: ['686px', '510px'], //宽高
            fix: false, //不固定
            //maxmin: false,
            offset:scrollHeightLong,
            content: Hussar.ctxPath+'/fsFolder/authority',
            success: function () {

            }
        });
    });
    $("#setAuthority").click(function(){
        layer.open({
            type: 2,
            title: ['选择可见范围','background-color:#fff'],
            area: ['1000px', '510px'], //宽高
            fix: false, //不固定
            //maxmin: false,
            offset:scrollHeightAlert,
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
            maxmin: false,
            offset:scrollHeightAlert,
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
                "1":{'icon' : "/static/assets/img/treeContext/com.png"},
                "2":{'icon' : "/static/assets/img/treeContext/dept.png"},
                "3":{'icon' : "/static/assets/img/treeContext/station.png"},
                "9":{'icon' : "/static/assets/img/treeContext/empl.png"},
                "USER":{'icon' : "/static/assets/img/treeContext/user.png"}
            },
            search:treeSearch("showAuthorTree","authorTreeSearch",authorName)
        });
        $authortree.bind('activate_node.jstree', function (obj,e){
            if(e.node.original.type !='9' && e.node.original.type !='USER'){
                layer.msg("请选择人员", {anim:6,icon: 0,offset:scrollHeightMsg});
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
            maxmin: false,
            offset:scrollHeightAlert,
            shadeClose: false,
            shade: 0.4,
            closeBtn: 0,
            title: "作者",
            btn: ['确定', '关闭']
            ,btn1: function(index, layero){
                if(contactsIdSnap!=""){
                    $("#contactsName").val(contactsNameSnap);
                    $("#contactsId").val(contactsIdSnap);
                } else{
                    layer.msg("请选择作者", {anim:6,icon: 0,offset:scrollHeightMsg});
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
                "1":{'icon' : "/static/assets/img/treeContext/com.png"},
                "2":{'icon' : "/static/assets/img/treeContext/dept.png"},
                "3":{'icon' :"/static/assets/img/treeContext/station.png"},
                "9":{'icon' : "/static/assets/img/treeContext/empl.png"},
                "USER":{'icon' : "/static/assets/img/treeContext/user.png"}
            },
            /* search:treeSearch("showContactsTree","contactsTreeSearch",contactsName)*/
        });
        $authortree.bind('activate_node.jstree', function (obj,e){
            if(e.node.original.type !='9'&&e.node.original.type !='USER'){
                layer.msg("请选择人员", {anim:6,icon: 0,offset:scrollHeightMsg});
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
        /*$.ajax({
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
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/searchLevel", function(data) {
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
        }, function(data) {

        });
        ajax.start();
        form.render();
        $("dl").height(150);
    }
    /**
     * 加载目录树
     */
    function    initTree(){

        /*$.ajax({
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
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getRoot", function(result) {
            openFileId=result.root;
            categoryId = result.root;
            getChildren(result.root,result.rootName);
        }, function(data) {

        });
        ajax.start();
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
            maxmin: false,   offset:scrollHeightLong,

            shadeClose: true,
            shade: 0.4,
            title: title,
            content: Hussar.ctxPath+url
        });


    }
    function updateFolderPid(index, id) {
        var cutIds = cutFile.join(",");
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFolder/update",
            data: {
                ids: cutIds,
                parentFolderId: id,
            },
            async: true,
            cache: false,
            success: function (data) {
                if ("success" == data) {
                    emptyChoose();
                    refreshFile(openFileId);

                    layer.closeAll();
                }else{
                    layer.msg("目录最多为"+data+"级", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    layer.close(index);
                }


            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFolder/update", function(data) {
            if ("success" == data) {
                emptyChoose();
                refreshFile(openFileId);

                layer.closeAll();
            }else{
                layer.msg("目录最多为"+data+"级", {anim: 6, icon: 0,offset:scrollHeightMsg});
                layer.close(index);
            }
        }, function(data) {

        });
        ajax.set("ids",cutIds);
        ajax.set("parentFolderId",id);
        ajax.start();
    }
    function updatePid(index){
        var cutIds= cutFile.join(",");
        /*$.ajax({
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
                //     refreshTree();
                layer.close(index);
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/update", function(data) {
            if("success" == data) {
                emptyChoose();
            }
            refreshFile(openFileId);
            //     refreshTree();
            layer.close(index);
        }, function(data) {

        });
        ajax.set("ids",cutIds);
        ajax.set("filePid",openFileId);
        ajax.start();
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
            maxmin: false,
            offset:scrollHeightLong,
            content: Hussar.ctxPath+'/fsFile/authority',
            success: function(layero, index) {
            }
        });
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
        chooseFile = [];
        var dropZone = document.getElementById("dndArea");
        //dropZone.addEventListener("dragover", function (e) {
        //    $(".shadow").show();
        //    $(".webuploader-pick").parent().hide();
        //    $("#dndArea").css("opacity", "0.3");
        //    $(".btns").css("pointer-events","none");
        //}, false);
        //dropZone.addEventListener("dragleave", function (e) {
        //    $("#dndArea").css("opacity", "1");
        //    $(".shadow").hide();
        //    $(".webuploader-pick").parent().show();
        //    $(".btns").css("pointer-events","auto");
        //}, false);
        // 监听分块上传过程中的三个时间点
        WebUploader.Uploader.register({
                "before-send-file": "beforeSendFile",//整个文件上传前
                "before-send": "beforeSend",  //每个分片上传前
                "after-send-file": "afterSendFile"  //分片上传完毕
            },
            {
                //时间点1：所有分块进行上传之前调用此函数
                beforeSendFile: function (file) {
                    $("#dndArea").css("opacity", "1");
                    $(".shadow").hide();
                    //$(".webuploader-pick").parent().show();
                    fileId = file.id;
                    if (categoryId == undefined) {
                        //$(".popWin").css("display", "none");
                        //$("#layui-layer-shade1").remove();
                        return;
                    }
                    // if (noChildPower == 0 && adminFlag != 1) {
                    //     $(".popWin").css("display", "none")
                    //     return;
                    // }
                    //$(".popWin").css("top", parseInt(scrollHeightTip) - 25 + "px");
                    //$("body").append('<div class="layui-layer-shade" id="layui-layer-shade1" times="1" style="z-index: 19891014; background-color: rgb(0, 0, 0); opacity: 0.3;"></div>')
                    //$(".popWin").css("display", "block");
                    popWin = layer.open({
                        type: 1,
                        title: false,
                        area: ['850px', '226px'], //宽高
                        fix: false, //不固定
                        offset:parseInt(scrollHeightTip) + 12 + "px",
                        maxmin: false,
                        content: $('.popWin'),
                        success:function(layero, index){
                        },
                        end: function () {
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
                            refreshFile(openFileId);
                            setTimeout(function () {
                                try {
                                    if (newDocId != ''){
                                        changeBgColorOfTr($("#"+ newDocId)[0]);// 秒传
                                        newDocId == '';
                                    }else{
                                        changeBgColorOfTr($("#"+ dataNew.id)[0]);// 上传
                                    }
                                }catch (e){
                                    console.log(e);
                                    changeBgColorOfTr($("#"+ oldDocId)[0]);// 上传异常被阻止
                                    oldDocId = '';
                                }
                            },500);
                        }
                    });
                    //$("#continueUpload").show();
                    $(".dragArea").css("display", "none");
                    $(".upload-tip").css("display", "none");


                    var deferred = WebUploader.Deferred();
                    //1、计算文件的唯一标记fileMd5，用于断点续传  如果.md5File(file)方法里只写一个file参数则计算MD5值会很慢 所以加了后面的参数：10*1024*1024
                    (new WebUploader.Uploader()).md5File(file, 0, 10 * 1024 * 1024).progress(function (percentage) {

                        percentageFlag = percentage;
                        $('#' + file.id).find('p.state').text('读取中...');
                        $('#' + file.id).find('.btnRemoveFile').removeClass("delete");

                    })
                        .then(function (val) {

                            $('#' + file.id).find("p.state").text("读取成功...");
                            fileMd5 = val;
                            fileName = file.name; //为自定义参数文件名赋值
                            /*$.ajax({
                                    type: "post",
                                    url: Hussar.ctxPath+"/breakpointUpload/checkVersionMd5Exist",
                                    data: {
                                        fileName: fileName,
                                        categoryId: categoryId,
                                        visible: "0",
                                        downloadAble: "0",
                                        watermarkUser: "",
                                        watermarkCompany: "",
                                        fileMd5: fileMd5,
                                        group: "",
                                        person: "",
                                        shareable:'1',
                                        isVersion:"1",
                                        oldDocId:oldDocId
                                    },
                                    async: false,
                                    cache: false,
                                    dataType: "json",
                                    success: function (data) {
                                        if (data.code == '2') {

                                            fastFlag = 1;
                                        }
                                        if (data.code == '4') {

                                            fastFlag = 2;
                                        }
                                        if (data.code == '6') {

                                            fastFlag = 6;
                                        }
                                        if (data.code == '7') {

                                            fastFlag = 7;
                                        }
                                        if (data.code == '8') {

                                            fastFlag = 8;
                                        }
                                        if (data.code == '9') {

                                            fastFlag = 9;
                                        }
                                        if (data.code == '5') {
                                            fastFlag = 0;
                                            chooseUploadFile.push(data.id);
                                            chooseUploadAuthor.push(data.authorId);
                                            newDocId = data.id;
                                        }
                                    }
                                }
                            );*/
                            var ajax = new $ax(Hussar.ctxPath + "/breakpointUpload/checkVersionMd5Exist", function(data) {
                                if (data.code == '2') {

                                    fastFlag = 1;
                                }
                                if (data.code == '4') {

                                    fastFlag = 2;
                                }
                                if (data.code == '6') {

                                    fastFlag = 6;
                                }
                                if (data.code == '7') {

                                    fastFlag = 7;
                                }
                                if (data.code == '8') {

                                    fastFlag = 8;
                                }
                                if (data.code == '9') {

                                    fastFlag = 9;
                                }
                                if (data.code == '5') {
                                    fastFlag = 0;
                                    chooseUploadFile.push(data.id);
                                    chooseUploadAuthor.push(data.authorId);
                                    newDocId = data.id;
                                }
                            }, function(data) {

                            });
                            ajax.set("fileName",fileName);
                            ajax.set("categoryId",categoryId);
                            ajax.set("visible","0");
                            ajax.set("downloadAble","0");
                            ajax.set("watermarkUser","");
                            ajax.set("watermarkCompany","");
                            ajax.set("fileMd5",fileMd5);
                            ajax.set("group","");
                            ajax.set("person","");
                            ajax.set("shareable",'1');
                            ajax.set("isVersion","1");
                            ajax.set("oldDocId",oldDocId);
                            ajax.start();
                            //获取文件信息后进入下一步
                            deferred.resolve();

                        });

                    return deferred.promise();

                },
                //时间点2：如果有分块上传，则每个分块上传之前调用此函数
                beforeSend: function (block) {
                    if (fastFlag == 0 || fastFlag == 1 || fastFlag == 2 || fastFlag == 6 || fastFlag == 7 || fastFlag == 8 || fastFlag == 9) {
                        return;
                    }
                    if (categoryId == undefined) {

                        return;
                    }
                    // if(noChildPower==0&&adminFlag!=1){
                    //     return;
                    // }
                    var deferred = WebUploader.Deferred();
                    //ajax验证每一个分片
                    var ajax = new $ax(Hussar.ctxPath+"/breakpointUpload/mergeOrCheckVersionChunks", function (data) {
                            var res = eval('(' + data + ')');
                            if (res.ifExist) {
                                //分块存在，跳过
                                deferred.reject();
                            } else {
                                //分块不存在或不完整，重新发送该分块内容
                                deferred.resolve();
                            }
                        },
                        function (data) {
                        });
                    ajax.set("param", "checkChunk");
                    ajax.set("fileName", fileName);
                    ajax.set("jindutiao", $("#jindutiao").val());
                    ajax.set("fileMd5", fileMd5);//文件唯一标记
                    ajax.set("chunk", block.chunk);//当前分块下标
                    ajax.set("chunkSize", block.end - block.start);//当前分块大小
                    ajax.set("shareable",'1');// 分享标识
                    ajax.set("oldDocId",$("#oldDocId").val());// 旧版本文档的ID
                    ajax.start();

                    this.owner.options.formData.fileMd5 = fileMd5;

                    deferred.resolve();

                    return deferred.promise();
                },
                //时间点3：所有分块上传成功后调用此函数
                afterSendFile: function (file) {
                    fileId = file.id;
                    if (categoryId == undefined) {

                        powerFlag = 1;
                        $('#' + fileId).remove();
                        uploader.removeFile(fileId, true);
                        count++
                        return;
                    }
                    // if (noChildPower == 0 && adminFlag != 1) {
                    //     powerFlag = 1;
                    //     uploader.removeFile(fileId, true);
                    //     $('#' + fileId).remove();
                    //     count++
                    //     return;
                    // }
                    powerFlag = 0;
                    if (fastFlag == 0 || fastFlag == 1 || fastFlag == 2 || fastFlag == 6 || fastFlag == 7 || fastFlag == 8 || fastFlag == 9) {

                        count++; //每上传完成一个文件 count+1
                        if (count > filesArr.length - 1) {

                        } else {
                            uploader.upload(filesArr[count].id);//上传文件列表中的下一个文件
                        }
                        return;
                    }

                    var code = 0;
                    $('#' + fileId).find('p.state').text('转化中')
                    //如果分块上传成功，则通知后台合并分块
                    var ajax = new $ax(Hussar.ctxPath+"/breakpointUpload/mergeOrCheckVersionChunks",
                        function (data) {
                            count++; //每上传完成一个文件 count+1
                            if (data != "") {
                                dataNew = eval('(' + data + ')')
                                if (dataNew.code == 3) {
                                    code = 3;
                                }
                                if (dataNew.code == 2) {
                                    code = 2;
                                }
                                if (code == 0) {
                                    chooseUploadFile.push(dataNew.id);
                                    chooseUploadAuthor.push(dataNew.authorId);
                                }
                            }
                            if (code != 3 && count <= filesArr.length - 1) {
                                uploader.upload(filesArr[count].id);//上传文件列表中的下一个文件
                            } else {
                                // 合并成功之后的操作

                                if (code == 2) {
                                    flag = 4;


                                }
                                if (code == 3) {
                                    flag = 3;

                                }
                            }
                        },
                        function (data) {
                        });

                    ajax.set("categoryId", categoryId);
                    ajax.set("visible", "0");
                    ajax.set("downloadAble", "0");
                    ajax.set("watermarkUser", "");
                    ajax.set("watermarkCompany", "");
                    ajax.set("group", "");
                    ajax.set("person", "");
                    ajax.set("param", "mergeChunks");
                    ajax.set("fileName", fileName);
                    ajax.set("fileMd5", fileMd5);
                    ajax.set("shareable",'1');
                    ajax.set("oldDocId",oldDocId);// 旧版本文档的ID
                    ajax.start();

                }
            }
        );//监听结束

        uploader = WebUploader.create({
            auto: true, //是否自动上传
            pick: {
                id: '#picker',
                label: '选择文件',
                multiple: false
            },
            duplicate: false, //同一文件是否可重复选择
            prepareNextFile: false,
            // 不压缩image
            resize: false,
            accept: {
                title: 'Files',
                extensions: '*',
                mimeTypes: '*'

            },
            compress: null,//图片不压缩
            chunked: true, //分片
            chunkSize: 10 * 1024 * 1024, //每片10M
            chunkRetry: 3,//如果失败，则不重试
            threads: 1,//上传并发数。允许同时最大上传进程数。
            fileNumLimit: 1,//验证文件总数量, 超出则不允许加入队列
            fileSizeLimit: 6 * 1024 * 1024 * 1024,//6G 验证文件总大小是否超出限制, 超出则不允许加入队列
            fileSingleSizeLimit: 3 * 1024 * 1024 * 1024,  //3G 验证单个文件大小是否超出限制, 超出则不允许加入队列
            // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
            disableGlobalDnd: true,
            //dnd: "#dndArea",
            dnd:undefined,
            // swf文件路径
            swf: '${ctxPath}/static/assets/lib/webuploader0.1.5/Uploader.swf',
            // 文件接收服务端。
            server: Hussar.ctxPath+"/breakpointUpload/fileSave"
        });

        /**
         *  当有文件添加进来的时候
         */
        uploader.on('fileQueued', function (file) {
            $(".btns").css("pointer-events","auto");
            //限制单个文件的大小 超出了提示
            if (file.size > 3 * 1024 * 1024 * 1024) {
                alert("单个文件大小不能超过3G！");
                return false;
            }
            filesArr.push(file);
            success++;
            var ajax = new $ax(Hussar.ctxPath+"/breakpointUpload/selectProgressByFileName",
                function (data) {
                    var res = eval('(' + data + ')');
                    //上传过程
                    if (res.jindutiao > 0) {
                        //上传过的进度的百分比
                        oldJindu = res.jindutiao / 100;
                        //如果上传过 上传了多少
                        var jindutiaoStyle = "width:" + res.jindutiao + "%";
                        $list.append('<div id="' + file.id + '" class="item">' +
                            '<h4 class="info"  title="' + file.name + '">' + file.name + '</h4>' +
                            '<p class="state">已上传' + res.jindutiao + '%</p>' +
                            //'<a href="javascript:void(0);"  style="float:right;width: 150px" class=" delete btnRemoveFile"></a>' +
                            '</div>');
                        //将上传过的进度存入map集合
                        map[file.id] = oldJindu;
                    } else {//没有上传过
                        $list.append('<div id="' + file.id + '" class="item">' +
                            '<h4 class="info" title="' + file.name + '">' + file.name + '</h4>' +
                            '<p class="state">等待上传...</p>' +
                            //'<a style="float:right;width: 150px" href="javascript:void(0);" class=" delete btnRemoveFile"></a>' +
                            '</div>');
                    }
                },
                function (data) {

                });
            ajax.set("fileName", file.name);     //文件名
            ajax.start();

            uploader.stop(true);
            //删除队列中的文件
            $(".btnRemoveFile").bind("click", function () {
                var fileItem = $(this).parent();
                uploader.removeFile($(fileItem).attr("id"), true);
                $(fileItem).fadeOut(function () {
                    $(fileItem).remove();
                });


                //数组中的文件也要删除
                for (var i = 0; i < filesArr.length; i++) {
                    if (filesArr[i].id == $(fileItem).attr("id")) {
                        filesArr.splice(i, 1);//i是要删除的元素在数组中的下标，1代表从下标位置开始连续删除一个元素
                    }
                }
                //隐藏上传按钮

                if (count - success < 0) {
                    success--;
                }
            });
        });

        uploader.on('filesQueued', function (file) {
            $(".btns").css("pointer-events","auto");
            // 限制一次性上传的文件数量
            if (file.length > 1) {
                layer.msg("只能选择一个文件上传", {anim:6,icon: 0,offset:scrollHeightMsg});
                return false;
            }
        });

        //文件上传过程中创建进度条实时显示
        uploader.on('uploadProgress', function (file, percentage) {
            $(".btns").css("pointer-events","auto");
            var $li = $('#' + file.id);

            //避免重复创建

            //将实时进度存入隐藏域
            $("#jindutiao").val(Math.round(percentage * 100));
            //根据fielId获得当前要上传的文件的进度
            var oldJinduValue = map[file.id];

            if (percentage < oldJinduValue && oldJinduValue != 1) {
                $li.find('p.state').text('上传中' + Math.round(oldJinduValue * 100) + '%');

                if (oldJinduValue == 1) {
                    $li.find('p.state').text('转化中...');
                }
            } else {
                $li.find('p.state').text('上传中' + Math.round(percentage * 100) + '%');

                if (percentage == 1) {
                    $li.find('p.state').text('转化中...');
                }

            }
        });

        uploader.on('uploadFinished', function (file){
            $(".btns").css("pointer-events","auto");
            // uploadAmount=0;
            // $("#continueUpload").show();
        })
        //上传成功后执行的方法
        uploader.on('beforeFileQueued', function (file){
            $(".btns").css("pointer-events","auto");
            // uploadAmount++
            // if(uploadAmount>10){
            //
            //     layer.msg("一次最多选择10个文件", {anim:6,icon: 0});
            //     return false;
            // }
            if(categoryId==undefined){
                $(".shadow").hide();
                //$(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                $("#setFileAuthority").addClass("hide");
                $("#setFileTip").addClass("hide");
                layer.msg("请先选择目录", {anim:6,icon: 0,offset:scrollHeightMsg});
                return false;
            }
            // $("#continueUpload").hide();
        })
        uploader.on('uploadSuccess', function (file) {
            $(".btns").css("pointer-events","auto");
            //var dragZone = $("#dndArea")[0];
            //dragZone.removeEventListener("dragover",dragover);
            //dragZone.removeEventListener("dragleave",dragleave);
            if (flag == 0) {
                $('#' + file.id).find('p.state').text('文件已存在')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
            } else if (flag == 3) {
                $('#' + file.id).find('p.state').text('文件上传失败')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
                fastFlag = null;

            }
            else if (flag == 4) {
                $('#' + file.id).find('p.state').text('空间不足')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
            } else if (fastFlag == 2) {
                $('#' + file.id).find('p.state').text('文件已存在')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 1) {
                $('#' + file.id).find('p.state').text('空间不足')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 6) {
                $('#' + file.id).find('p.state').text('名称过长')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 7) {
                $('#' + file.id).find('p.state').text('名称不合法')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 8) {
                $('#' + file.id).find('p.state').text('格式不支持')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            }else if (fastFlag == 9){
                $('#' + file.id).find('p.state').text('与历史版本相同')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
            } else if (fastFlag == 0) {

                // 隐藏删除按钮
                //$('#' + file.id).find('.btnRemoveFile').removeClass("btnRemoveFile");
                //隐藏上传按钮
                success--;
                if (success == 0) {

                }
                $('#' + file.id).find('p.state').text('')
                $('#' + file.id).find('p.state').addClass("success").append('秒传');
                // refreshFile(openFileId)
                fastFlag = null;
                $("#setFileAuthority").removeClass("hide");
                $("#setFileTip").removeClass("hide");

            }
            else {  //上传成功去掉进度条

                //隐藏删除按钮
                //$('#' + file.id).find('.btnRemoveFile').removeClass("btnRemoveFile");
                //上传成功，获得积分
                //$.ajax({
                //    url: "/integral/addIntegral",
                //    async: true,
                //    data:{
                //        docId:'',
                //        ruleCode: 'upload'
                //    },
                //    success: function (data) {
                //        if (data.integral != 0 && data.integral != null && data.integral != ''){
                //            $("#num").html(data.msg)
                //            if(data.msg=="积分不足"||data.msg=="已达上限"){
                //                $(".integral .point").hide();
                //                $(".integral .num") .css({"width":"81px","padding-top":"43px"})
                //            }
                //            $(".integral").show();
                //            // 实时更新积分
                //            $("#totalIntegral",parent.document).text(parent.getTotalIntegral());
                //            setTimeout(function () {
                //                $(".integral .point").show();
                //                $(".integral .num") .css({"width":"40px","padding-top":"0"})
                //                $(".integral").hide();
                //
                //            },2000)
                //        }
                //    }
                //});
                //隐藏上传按钮
                success--;
                if (success == 0) {

                }
                $('#' + file.id).find('p.state').text('')
                $('#' + file.id).find('p.state').addClass("success").append('上传')

                $("#setFileAuthority").removeClass("hide");
                $("#setFileTip").removeClass("hide");
            }
            if (count > filesArr.length - 1) {
                if (powerFlag == 1) {
                    chooseUploadFile = [];
                    chooseUploadAuthor = [];
                    count = 0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
                    success = 0;//上传成功的文件数
                    filesArr = new Array();//文件数组：每当有文件被添加进队列的时候 就push到数组中
                    map = {};//key存储文件id，value存储该文件上传过的进度
                    powerFlag = 0
                    if(categoryId==undefined){
                        $(".shadow").hide();
                        //$(".webuploader-pick").parent().show();
                        $("#dndArea").css("opacity", "1");
                        $("#setFileAuthority").addClass("hide");
                        $("#setFileTip").addClass("hide");
                        // layer.msg("1", {anim:6,icon: 0});
                    }

                } else {
                    var amount = count - success;
                    if (amount < 0) {
                        amount = 0;
                    }
                    $(".success-msg").html("成功上传" + amount + "个文件！").show(500);
                }

            }


            // refreshFile(openFileId)
            fastFlag = null;

        });

        //上传出错后执行的方法
        uploader.on('uploadError', function (file) {
            $(".btns").css("pointer-events","auto");
            errorUpload = true;
            uploader.stop(true);
            $('#' + file.id).find('p.state').text('上传出错，请检查网络连接');
        });

        //文件上传成功失败都会走这个方法
        uploader.on('uploadComplete', function (file) {
            $(".btns").css("pointer-events","auto");

        });

        uploader.on('all', function (type) {
            $(".btns").css("pointer-events","auto");
            if (type === 'startUpload') {
                state = 'uploading';
            } else if (type === 'stopUpload') {
                state = 'paused';
            } else if (type === 'uploadFinished') {
                state = 'done';
            }
        });

        /**
         验证文件格式以及文件大小
         */
        uploader.on("error", function (type) {
            $(".btns").css("pointer-events","auto");
            if (type == "Q_TYPE_DENIED") {
                $(".shadow").hide();
                //$(".webuploader-pick").parent().show();

                $("#dndArea").css("opacity", "1");
                layer.msg("存在不支持上传的文档格式", {anim: 6, icon: 0,offset:scrollHeightMsg});
            } else if (type == "Q_EXCEED_SIZE_LIMIT") {
                $(".shadow").hide();
                //$(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("文件大小不能超过3G", {anim: 6, icon: 0,offset:scrollHeightMsg});
            } else if (type == "Q_EXCEED_NUM_LIMIT") {
                $(".shadow").hide();
                //$(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传列表中不得超过1个文件", {anim: 6, icon: 0,offset:scrollHeightMsg});
            } else if (type == "F_DUPLICATE") {
                $(".shadow").hide();
                //$(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传列表中存在重复文件", {anim: 6, icon: 0,offset:scrollHeightMsg});
            } else {
                $(".shadow").hide();
                //$(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传出错！请检查后重新上传！错误代码" + type, {anim: 6, icon: 0,offset:scrollHeightMsg});
            }
        });
    };

    /**
     *  初始化按钮事件
     */
    BreakpointUpload.initButtonEvents = function () {
        $("#searchName").click(function () {
            searchFlag = 1;
        })
    };

    //页面初始化
    $(function () {
        initTree();
        //$(".uploadBtn").hide();
        BreakpointUpload.initUploader();
        BreakpointUpload.initButtonEvents();

        // 获取转发参数 （author：zgr）
        var folderId = $("input#openFileId").val();
        var folderName = $("input#folderName").val();
        if(folderId != "" && folderName != ""){
            openFileId = folderId;
            // 获取转发文件夹所在路径
            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+"/sharefile/getFolderPathByFolder",
                contentType:"application/x-www-form-urlencoded",
                data:{folderId:folderId,
                    folderName:folderName},
                success: function (data) {

                    var list = data;
                    for (var i = 0; i < list.length; i++){
                        pathId.push(list[i].foldId);
                        pathName.push(list[i].foldName);
                    }
                    // 创建路径元素
                    createPath();
                    refreshFile(folderId);
                    $("input#openFileId").val("");
                    $("input#folderName").val("");
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/sharefile/getFolderPathByFolder", function(data) {
                var list = data;
                for (var i = 0; i < list.length; i++){
                    pathId.push(list[i].foldId);
                    pathName.push(list[i].foldName);
                }
                // 创建路径元素
                createPath();
                refreshFile(folderId);
                $("input#openFileId").val("");
                $("input#folderName").val("");
            }, function(data) {

            });
            ajax.set("folderId",folderId);
            ajax.set("folderName",folderName);
            ajax.start();
        }

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
            //$(".success-msg").html("成功上传"+(count-success)+"个文件！").hide(500);
            returnList();
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
    // $("#path").append("<span class='total'>");
    for(var i=0;i<pathId.length;i++){
        if(i==pathId.length-1){
            var param= '<span>'+pathName[i]+'</span>'
        }else {
            var param= '<span><a style="cursor: pointer;font-size: 14px; color: #3C91FD;" onclick="clickPath(\''+pathId[i]+'\')">'+pathName[i]+'</a>'+'  >  </span>'        }
        $("#path").append(param);


    }
    // $("#path").append("</span>");
    var timer;
    $(".message #path").hover(function () {
        var _this = $(this);
        var len = _this.width();

        var width = _this.parent().width();
        if(len >= width){
            var num = 0;
            var change = 0;
            timer = setInterval(function () {
                if (navigator.userAgent.indexOf('iPad') != -1){
                    change = 240-len;
                }else{
                    change = 290-len;
                }
                if (num <= change) {
                    clearInterval(timer);
                }
                num -= 1;
                _this.css("left",num);
            }, 25);
        }
    },function () {
        clearInterval(timer);
        $(this).css("left",0)
    })
}
function drawFile(param,showFlag) {
    authority = $("#authority").val();
    if(showStyle==1){
        layui.use('laytpl', function(){
            var laytpl = layui.laytpl;
            var data = { //数据
                "list":param,
                "authority":authority,
                "adminFlag":adminFlag
                ,"noChildPower":noChildPower
                ,"noChildPowerFolder":noChildPowerFolder
            }
            var getTpl = $("#demo").html()
                ,view = document.getElementById('view');
            laytpl(getTpl).render(data, function(html){
                view.innerHTML = html;
                if (param.length == 0){
                    setTimeout(function () {
                        $("div.noDataTip").show();
                        $("#laypageAre").hide();
                    },200);
                }else {
                    $("div.noDataTip").hide();
                    $("#laypageAre").show();
                }
            });
        });
    }
    else{
        layui.use('laytpl', function(){
            var laytpl = layui.laytpl;
            var data = { //数据
                "list":param,
                "authority":authority,
                "adminFlag":adminFlag
                ,"noChildPower":noChildPower
                ,"noChildPowerFolder":noChildPowerFolder
            }
            var getTpl = $("#demo1").html()
                ,view = document.getElementById('view');
            laytpl(getTpl).render(data, function(html){
                view.innerHTML = html;
                var inner = $("#view");
                var tableWidth =inner.width();
                //fixed-table-header
                $(".fixed-table-header").width(tableWidth);
                if (param.length == 0){
                    setTimeout(function () {
                        $("div.noDataTip").show();
                    },200);
                }else {
                    $("div.noDataTip").hide();
                }
            });
        });
    }
    // 当上传重名文件，确定进入版本管理后跳转过来时
    //var returnDocId = $("#returnDocId").val();
    //if (returnDocId != ''){
    //    setTimeout(function () {
    //        iconUploadVersion($("#" + returnDocId)[0],returnDocId);
    //    },1000);
    //    $("#returnDocId").val("");
    //};
}

function   drawPower(isAdmin){
    if(isAdmin!=1){
        // $("#addCategoryBtn").hide();
        $("#joinTopic").hide();
        $("#more").hide();
        $("#exempt").hide();
        $("#setTop").hide();
        $(".layui-nav").hide();

    }else{
        $("#joinTopic").show();
        $("#more").show();
        $("#exempt").show();
        $("#setTop").show();
        $(".layui-nav").show();
    }

}
function getChildren(id,name){

    // 判断是否是上传后跳转
    var folderId = $("input#openFileId").val();
    var folderName = $("input#folderName").val();
    if(folderId == "" || folderName == ""){
        createPath();
        refreshFile(id);
    }
}

function  refreshFile(id,num,size,nameFlag,order){
    var screenHeight = parseInt(window.screen.availHeight);
    //console.log(screenHeight);
    if (screenHeight > 728) {
        $("#marg").css("min-height","768");
    }
    var noOrder;
    if(order==null||order==undefined||order==''){
        noOrder=true;
        order="3";
    }
    currOrder = order;
    layui.use(['laypage','layer','table','flow'], function(){
        var flow = layui.flow;
        var laypage = layui.laypage,
            layer = layui.layer;
        var fileType = $("input[name='fileType']:checked").val();
        var name = $('#searchName').val();
        if(nameFlag!=""&&nameFlag!=undefined&&nameFlag!=null){
            var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if(!pattern.test(name)){
                layer.msg("输入的文件名称不合法", {anim:6,icon: 0,offset:scrollHeightMsg});
                return;
            }
        }

        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+"/sharefile/getChildren",
                data:{
                    id: id,
                    pageNumber:num,
                    pageSize:size,
                    type:fileType,
                    order:currOrder,
                    name:name,
                    nameFlag:nameFlag,
                    operateType:"1"
                },
                contentType:"application/x-www-form-urlencoded",
                async:true,
                cache:false,
                dataType:"json",
                success:function(data){
                    laypage.render({
                        elem: 'laypageAre'
                        ,count: data.total //数据总数，从服务端得到
                        ,limit: 60
                        ,layout: ['prev', 'page', 'next']
                        ,curr: num || 1 //当前页
                        ,jump: function(obj, first){
                            //obj包含了当前分页的所有参数，比如：
                            // obj.curr得到当前页，以便向服务端请求对应页的数据。
                            // obj.limit得到每页显示的条数
                            //首次不执行
                            currPage = obj.curr;
                            if(!first){
                                refreshFile(id,obj.curr,obj.limit,"0",currOrder)
                            }
                        }
                    });

                    adminFlag=data.isAdmin;
                    noChildPowerFolder=data.noChildPowerFolder;
                    noChildPower=data.noChildPower;
                    drawFile(data.rows);
                    drawPower(data.isAdmin);
                    openFileId = id;
                    categoryId = id;
                    userId=data.userId;
                    isChild=data.isChild;
                    emptyChoose();
                    btnState();
                    folderAmount = data.folderAmount;
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
                    //$("th").hover(function () {
                    if(noOrder==true){
                        //$(this).find("#orderName").hide();
                        //$(this).find("#orderName1").show();
                        $("#orderTime").hide();
                        $("#orderTime1").hide();
                    }else{
                        //if(order== "1"){
                        //    //$(this).find("#orderName").hide();
                        //    //$(this).find("#orderName1").show();
                        //    $(this).find("#orderTime").hide();
                        //    $(this).find("#orderTime1").show();
                        //}
                        //if(order== "0"){
                        //    //$(this).find("#orderName1").hide();
                        //    //$(this).find("#orderName").show();
                        //    $(this).find("#orderTime").hide();
                        //    $(this).find("#orderTime1").show();
                        //}
                        if(order== "2"){
                            //$(this).find("#orderName").hide();
                            //$(this).find("#orderName1").show();
                            $("#orderTime1").hide();
                            $("#orderTime").show();
                        }
                        if(order== "3" ){
                            $("#orderTime").hide();
                            $("#orderTime1").show();
                            //$(this).find("#orderName").hide();
                            //$(this).find("#orderName1").show();
                        }

                    }
                    //}, function () {
                    //    $(this).find("#orderTime").hide();
                    //    $(this).find("#orderTime1").hide();
                    //})
                    $(".layui-table tr").hover(function () {
                        //alert($(this).prev());
                        $(this).find("td").css("border-color","#DAEBFE");
                        $(this).prev().find("td").css("border-color","#DAEBFE");
                    }, function () {
                        $(this).find("td").css("border-color","rgba(242,246,253,1)");
                        $(this).prev().find("td").css("border-color","rgba(242,246,253,1)");
                    });
                    $(".layui-table tbody tr:first").hover(function () {
                        $(this).find("td").css("border-color","#DAEBFE");
                        $("thead").find("tr").css("border-bottom-color","#DAEBFE");
                    }, function () {
                        $(this).find("td").css("border-color","rgba(242,246,253,1)");
                        $("thead").find("tr").css("border-bottom-color","rgba(242,246,253,1)");
                    })

                    $("#amount").html("共"+data.total+"个");
                    $(".file-container-flatten").height($("body").height() - $(".toolBar").outerHeight(true) -
                        $("#pathDiv").outerHeight(true) - $(".orderSearch").outerHeight(true)  - $("#laypageAre").outerHeight(true)-35);

                    //失去焦点隐藏
                    $(".ishover").blur(function () {
                        $(this).parent().find(".nameTitpe").removeClass("hide");
                        $(this).addClass('hide')
                    });

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
                    $(".file").hover(function(){
                        $(this).find(".clickEventP").show();

                    },function(){
                        $(this).find(".clickEventP").hide();
                        $(this).find(".moreicon").hide();

                    });

                    $(".clickEventP").click(function () {
                        cancelBubble();
                        $(".moreicon").hide();

                        $(this).parent().find(".moreicon").show();
                    })
                    $(".clickEvent").click(function () {
                        cancelBubble();
                        var  index = $(this).next().val();
                        if(size==null||size==undefined){
                            size=60;
                        }

                        if(num==null||num==undefined){
                            num=1;
                        }
                        if((size*num)>data.total){
                            var lack = data.total%size;
                            if((lack-1==index||lack-2==index||lack-3==index||lack-4==index||lack-5==index)&&index>=6){
                                $(this).next().next().css("bottom","30px");
                            }
                        }

                        if(data.total>size*(num-1)){
                            if(index==(size-1)||index==(size-2)||index==(size-3)||index==(size-4)||index==(size-5)){
                                $(this).next().next().css("bottom","30px");
                            }
                        }

                        $(this).parent().find(".moreicon").show();
                    })


                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/sharefile/getChildren", function(data) {
                laypage.render({
                    elem: 'laypageAre'
                    ,count: data.total //数据总数，从服务端得到
                    ,limit: 60
                    ,layout: ['prev', 'page', 'next']
                    ,curr: num || 1 //当前页
                    ,jump: function(obj, first){
                        //obj包含了当前分页的所有参数，比如：
                        // obj.curr得到当前页，以便向服务端请求对应页的数据。
                        // obj.limit得到每页显示的条数
                        //首次不执行
                        currPage = obj.curr;
                        if(!first){
                            refreshFile(id,obj.curr,obj.limit,"0",currOrder)
                        }
                    }
                });

                adminFlag=data.isAdmin;
                noChildPowerFolder=data.noChildPowerFolder;
                noChildPower=data.noChildPower;
                drawFile(data.rows);
                drawPower(data.isAdmin);
                openFileId = id;
                categoryId = id;
                userId=data.userId;
                isChild=data.isChild;
                emptyChoose();
                btnState();
                folderAmount = data.folderAmount;
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
                //$("th").hover(function () {
                if(noOrder==true){
                    //$(this).find("#orderName").hide();
                    //$(this).find("#orderName1").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                }else{
                    //if(order== "1"){
                    //    //$(this).find("#orderName").hide();
                    //    //$(this).find("#orderName1").show();
                    //    $(this).find("#orderTime").hide();
                    //    $(this).find("#orderTime1").show();
                    //}
                    //if(order== "0"){
                    //    //$(this).find("#orderName1").hide();
                    //    //$(this).find("#orderName").show();
                    //    $(this).find("#orderTime").hide();
                    //    $(this).find("#orderTime1").show();
                    //}
                    if(order== "2"){
                        //$(this).find("#orderName").hide();
                        //$(this).find("#orderName1").show();
                        $("#orderTime1").hide();
                        $("#orderTime").show();
                    }
                    if(order== "3" ){
                        $("#orderTime").hide();
                        $("#orderTime1").show();
                        //$(this).find("#orderName").hide();
                        //$(this).find("#orderName1").show();
                    }

                }
                //}, function () {
                //    $(this).find("#orderTime").hide();
                //    $(this).find("#orderTime1").hide();
                //})
                $(".layui-table tr").hover(function () {
                    //alert($(this).prev());
                    $(this).find("td").css("border-color","#DAEBFE");
                    $(this).prev().find("td").css("border-color","#DAEBFE");
                }, function () {
                    $(this).find("td").css("border-color","rgba(242,246,253,1)");
                    $(this).prev().find("td").css("border-color","rgba(242,246,253,1)");
                });
                $(".layui-table tbody tr:first").hover(function () {
                    $(this).find("td").css("border-color","#DAEBFE");
                    $("thead").find("tr").css("border-bottom-color","#DAEBFE");
                }, function () {
                    $(this).find("td").css("border-color","rgba(242,246,253,1)");
                    $("thead").find("tr").css("border-bottom-color","rgba(242,246,253,1)");
                })

                $("#amount").html("共"+data.total+"个");
                $(".total").width($(".message").width() - 26*3 - $(".share-file-name").width() - $("#amount").width() )
                $(".file-container-flatten").height($("body").height() - $(".toolBar").outerHeight(true) -
                    $("#pathDiv").outerHeight(true) - $(".orderSearch").outerHeight(true)  - $("#laypageAre").outerHeight(true)-35);

                //失去焦点隐藏
                $(".ishover").blur(function () {
                    $(this).parent().find(".nameTitpe").removeClass("hide");
                    $(this).addClass('hide')
                });

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
                $(".file").hover(function(){
                    $(this).find(".clickEventP").show();

                },function(){
                    $(this).find(".clickEventP").hide();
                    $(this).find(".moreicon").hide();

                });

                $(".clickEventP").click(function () {
                    cancelBubble();
                    $(".moreicon").hide();

                    $(this).parent().find(".moreicon").show();
                })
                $(".clickEvent").click(function () {
                    cancelBubble();
                    var  index = $(this).next().val();
                    if(size==null||size==undefined){
                        size=60;
                    }

                    if(num==null||num==undefined){
                        num=1;
                    }
                    if((size*num)>data.total){
                        var lack = data.total%size;
                        if((lack-1==index||lack-2==index||lack-3==index||lack-4==index||lack-5==index)&&index>=6){
                            $(this).next().next().css("bottom","30px");
                        }
                    }

                    if(data.total>size*(num-1)){
                        if(index==(size-1)||index==(size-2)||index==(size-3)||index==(size-4)||index==(size-5)){
                            $(this).next().next().css("bottom","30px");
                        }
                    }

                    $(this).parent().find(".moreicon").show();
                })
            }, function(data) {

            });
            ajax.set("id",id);
            ajax.set("pageNumber",num);
            ajax.set("pageSize",size);
            ajax.set("type",fileType);
            ajax.set("order",currOrder);
            ajax.set("name",name);
            ajax.set("nameFlag",nameFlag);
            ajax.set("operateType","1");
            ajax.start();
        });

    });
}
/*打开分享链接*/
function share(e,docId,fileSuffixName,fileName) {
    cancelBubble();
    changeBgColorOfTr(e);
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                if(data.code==1){

                }else if(data.code==2){

                }else if(data.code==3){

                }else if(data.code==4){

                }else{
                    layer.msg("此文件类型不支持分享。",{anim:6,icon: 0,offset:scrollHeightMsg});
                    return;
                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){

            }else if(data.code==2){

            }else if(data.code==3){

            }else if(data.code==4){

            }else{
                layer.msg("此文件类型不支持分享。",{anim:6,icon: 0,offset:scrollHeightMsg});
                return;
            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/personalcenter/getInfo",
            data: {
                ids: docId
            },
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                if (data.result == "1") {
                    layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
                } else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else {
                    var title = '';
                    var url = "/s/shareConfirm";
                    var w =  538;
                    var h = 311;
                    if (title == null || title == '') {
                        title = false;
                    }
                    if (url == null || url == '') {
                        url = "404.jsp";
                    }
                    if (w == null || w == '') {
                        w = ($(window).width() * 0.9);
                    }
                    if (h == null || h == '') {
                        h = ($(window).height() - 50);
                    }
                    layer.open({
                        type: 2,
                        area: [w + 'px', h + 'px'],
                        fix: false, //不固定
                        maxmin: false,
                        shadeClose: true,
                        shade: 0.4,
                        title: title,
                        closeBtn:2,
                        offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                        content: url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                    });
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if (data.result == "1") {
                layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
            } else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }else {
                var title = '';
                var url = "/s/shareConfirm";
                var w =  538;
                var h = 311;
                if (title == null || title == '') {
                    title = false;
                }
                if (url == null || url == '') {
                    url = "404.jsp";
                }
                if (w == null || w == '') {
                    w = ($(window).width() * 0.9);
                }
                if (h == null || h == '') {
                    h = ($(window).height() - 50);
                }
                layer.open({
                    type: 2,
                    area: [w + 'px', h + 'px'],
                    fix: false, //不固定
                    maxmin: false,
                    shadeClose: true,
                    shade: 0.4,
                    title: title,
                    closeBtn:2,
                    offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                    content: url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                });
            }
        }, function(data) {

        });
        ajax.set("ids",docId);
        ajax.start();
    });
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
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showPdf(id,fileSuffixName,name) {
    var keyword =  name;
    dbclickover = true;
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/sharefile/getPreviewType",
            contentType:"application/x-www-form-urlencoded",
            data: {
                suffix: fileSuffixName
            },
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                if(data.code==1){
                    openWin(Hussar.ctxPath+"/sharefile/toShowPDFByFolder?id=" + id);
                }else if(data.code==2){
                    openWin(Hussar.ctxPath+"/sharefile/toShowIMGByFolder?id=" + id);
                }else if(data.code==3){
                    openWin(Hussar.ctxPath+"/sharefile/toShowVideoByFolder?id=" + id);
                }else if(data.code==4){
                    openWin(Hussar.ctxPath+"/sharefile/toShowVoiceByFolder?id=" + id);
                }else{
                    layer.msg("此文件类型不支持预览。",{anim:6,icon: 0});
                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/sharefile/getPreviewType", function(data) {
            if(data.code==1){
                openWin(Hussar.ctxPath+"/sharefile/toShowPDFByFolder?id=" + id);
            }else if(data.code==2){
                openWin(Hussar.ctxPath+"/sharefile/toShowIMGByFolder?id=" + id);
            }else if(data.code==3){
                openWin(Hussar.ctxPath+"/sharefile/toShowVideoByFolder?id=" + id);
            }else if(data.code==4){
                openWin(Hussar.ctxPath+"/sharefile/toShowVoiceByFolder?id=" + id);
            }else{
                layer.msg("此文件类型不支持预览。",{anim:6,icon: 0});
            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
    });

}



function download(id,name){
    //cancelBubble();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNewByShare?docIds="+id,
            type : "post",
            async:false,

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
            layer.msg("您没有权限重命名文件", {anim:6,icon: 0,offset:scrollHeightMsg});
            return;
        }
        if (chooseFileType[i]=="folder"){
            layer.msg(folderMessage, {anim:6,icon: 0,offset:scrollHeightMsg});
            return;
        }

    }

    if(chooseFile.length!=1){
        layer.msg("请选择一个要重命名的文件", {anim:6,icon: 0,offset:scrollHeightMsg});
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
function changeBgColorOfTr(e){
    var jq=$(e);
    //console.log(e.tagName.toLowerCase());
    if (e.tagName.toLowerCase() != "tr"){
        jq = jq.parents(".hoverEvent");
    }
    jq.parent().find("tr").css("background-color","#fff");

    jq.css("background-color","rgba(246, 250, 255, 1)");
}
function  clickCheck(e,id,type,name,index,author) {

    var jq=$(e);
    changeBgColorOfTr(e);
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
                chooseFile=chooseFile.del(chooseFile.indexOf(id));
            }
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

// $(document).keydown(function(e){
//     if(e.ctrlKey){
//         key=1;
//     }else if(e.shiftKey){
//         key=2;
//     }else if(e.keyCode == 13 && searchFlag == 1) {
//         var index = layer.load(1, {
//             shade: [0.1,'#fff'] //0.1透明度的白色背景
//         });
//         refreshFile(openFileId,null,null,"1");
//         layer.close(index);
//         searchFlag = 0;
//         $("#searchName").val("");
//         $("#searchName").blur();
//     }
//     //$("#bb").val("初始值:"+ibe+" key:"+key);
// }).keyup(function(){
//     key=0;
// });


function emptyChoose() {
    chooseFile = [];
    chooseFileType = [];
    chooseFileName = [];
    chooseFileAuthor=[]
}

function createAuthorityFolder(group, person) {
    if (editFlag) {
        $(".edit-name-list").empty();
    } else {
        $(".name-list").empty();
    }
    for (var i = 0; i < group.length; i++) {
        var operateType = '';
        if(group[i].operateType == 0){
            operateType = '（查看）';
        }else if(group[i].operateType == 1){
            operateType = '（上传）';
        }else{
            operateType = '（管理）';
        }
        var param = '';
        if(group[i].type == 2){
            param = '<div class="name-item org">' +
                '<p>' + group[i].name + operateType + '</p>' +
                '<span class="del_span" value="1"></span>' +
                '</div>'
        }else{
            param = '<div class="name-item qz">' +
                '<p>' + group[i].name + operateType + '</p>' +
                '<span class="del_span" value="1"></span>' +
                '</div>'
        }
        if (editFlag) {
            $(".edit-name-list").append(param);
        } else {
            $(".name-list").append(param);
        }
    }
    for (var i = 0; i < person.length; i++) {
        var param = '';
        var operateType = '';
        if(person[i].operateType == 0){
            operateType = '（查看）';
        }else if(person[i].operateType == 1){
            operateType = '（上传）';
        }else{
            operateType = '（管理）';
        }
        if (person[i].type == 2) {
            param = '<div class="name-item org">' +
                '<p>' + person[i].name+ operateType + '</p>' +
                '<span class="del_span" value="0"></span>' +
                '</div>'
        } else {
            param = '<div class="name-item people">' +
                '<p>' + person[i].name+ operateType + '</p>' +
                '<span class="del_span" value="0"></span>' +
                '</div>'
        }
        if (editFlag) {
            $(".edit-name-list").append(param);
        } else {
            $(".name-list").append(param);
        }
    }
    $(".del_span").click(function () {
        if ($(this).attr('value') == 0) {
            for (var i = 0; i < personId.length; i++) {
                if (personId[i].name == ($(this).prev().html()).substring(0,($(this).prev().html()).length-4)) {
                    personId.splice(i, 1);
                }
            }
        } else {
            for (var i = 0; i < groupId.length; i++) {
                if (groupId[i].name == ($(this).prev().html()).substring(0,($(this).prev().html()).length-4)) {
                    groupId.splice(i, 1);
                }
            }
        }
        $(this).parent(".name-item").remove();
    });
}
function createAuthorityPowerFolder(group, person) {
    layui.use(['form'], function () {
        var form = layui.form;
        if (editFlag) {
            $(".edit-name-list-power").empty();
            if (group.length != 0 || person.length != 0) {
                $('#isEditEdit').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
                $('#isEditChild').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
            } else {
                $('#isEditEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $('#isEditChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $("#isEditEdit").prop("checked", false);
                $("#isEditChild").prop("checked", false);
            }
        } else {
            $(".name-list-power").empty();
            if (group.length != 0 || person.length != 0) {
                $('#isEdit').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
                $('#isChild').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
            } else {
                $('#isEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $('#isChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $("#isEdit").prop("checked", false);
                $("#isChild").prop("checked", false);
            }
        }
        for (var i = 0; i < group.length; i++) {
            var param = '<div class="name-item qz">' +
                '<p>' + group[i].name + '</p>' +
                '<span class="del_span_power" value="1"></span>' +
                '</div>'
            if (editFlag) {
                $(".edit-name-list-power").append(param);
            } else {
                $(".name-list-power").append(param);
            }
        }
        for (var i = 0; i < person.length; i++) {
            var param = '<div class="name-item people">' +
                '<p>' + person[i].name + '</p>' +
                '<span class="del_span_power" value="0"></span>' +
                '</div>'
            if (editFlag) {
                $(".edit-name-list-power").append(param);
            } else {
                $(".name-list-power").append(param);
            }
        }

        form.render();
    });
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
    $(".moreicon").hide();
    if (noChildPowerFolder != 0) {
        $("#newFolder").show();
    }else{
        $("#newFolder").hide();
    }
    if(chooseFile.length==0){
        $('.clickBtn').hide()
        if(openFileId == '2bb61cdb2b3c11e8aacf429ff4208431' || openFileId == undefined){
            $(".uploadBtn").hide();
        }else if(noChildPower == 0&&adminFlag != 1){
            $(".uploadBtn").hide();
        }else{
            $(".uploadBtn").show();
        }
        // if(isChild==false){
        //     $(".uploadBtn").hide();
        // }else {
        //     $(".uploadBtn").show();
        // }
    }else {
        if(chooseFile.length>1){
            for(var i=0;i<chooseFileType.length;i++){
                if(chooseFileType[i+1]!=undefined){
                    if((chooseFileType[i]=="folder"&&chooseFileType[i+1]!="folder")
                        ||(chooseFileType[i]!="folder"&&chooseFileType[i+1]=="folder")){
                        $('.clickBtn').hide();
                        $(".uploadBtn").hide();
                        return;

                    }}
            }
        }
        var flag=0;
        var fileFlag = 0;
        for(var i=0;i<chooseFileType.length;i++){
            if(chooseFileType[i]=="folder"){
                flag=1;
                break;

            }
            var power= $("#authority"+chooseFile[i]).html()
            if(power!='2'&&noChildPower!=2&&adminFlag!=1){
                fileFlag = 1;
                break;
            }
        }

        if(flag=="1"){
            $('.clickBtn').hide()
            if (noChildPowerFolder == 0) {
                $("#reName").hide();
                $("#cutFile").hide();
                $("#delCategoryBtn").hide();
                $("#exempt").hide();
            }else {
                $("#reName").show();
                $("#cutFile").show();
                $("#delCategoryBtn").show();
                $("#exempt").show();
            }

        }else{
            if (fileFlag == 1){
                $('.clickBtn').hide()
            }else{
                $('.clickBtn').show()
                $('#manyMulDownLoad').hide();
                $('#setShare').show();
            }
            if(chooseFile.length>1 && fileFlag != 1){
                $('#updateName').hide();
                $('#mulDownLoad').hide();
                $('#manyMulDownLoad').show();
                $('#setShare').show();
            }
            if(adminFlag!=1){

                $("#joinTopic").hide();
                $("#more").hide();
                $("#exempt").hide();
                $("#setTop").hide();
                $(".layui-nav").hide();
            }else {
                $("#joinTopic").show();
                $("#more").show();
                $("#exempt").show();
                $("#setTop").show();
                $(".layui-nav").show();
            }

        }

    }
}

$(document).click(function(e){
    if($(e.target)[0]!=$(".moreicon")){
        $(".moreicon").hide();
    }

    if($(e.target)[0]==$('.file-container-flatten')[0] ||$(e.target)[0]==$('.content')[0]
        ||$(e.target)[0]==$('#marg')[0]||$(e.target)[0]==$('#view ul')[0]){
        if(reNameFlag==true){
            $('#name'+reNameIndex).removeClass("hide");
            $('#inputName'+reNameIndex).addClass("hide");
            reNameFlag=false;
            var inputname = $('#inputName'+reNameIndex).val().trim();
            if(inputname!=reNameParem){
                rename(inputname);
            }
        }
        if(showStyle==2){

        }else{
            $('.file').removeClass("active");
            // $("tbody").find(".checkbox").prop("checked",false);
            // $("tbody").find(".layui-form-checkbox").removeClass("layui-form-checked");
            $("input[name='checkboxname']").each(function () {
                $(this).prop("checked", false);
            });
            emptyChoose();
            btnState();
        }

    }

});
function  stopPop() {
    window.event? window.event.cancelBubble = true : e.stopPropagation();
}
function rename(inputname){

    if(chooseFileType[0]=='folder'){
        if (noChildPowerFolder == 0) {
            layer.msg("您没有重命名目录权限", {anim: 6, icon: 0,offset:scrollHeightMsg});
            return;
        }
        inputname = inputname.trim();
        if (inputname == '' || inputname == undefined) {
            layer.msg("目录名称不能为空", {anim: 6, icon: 0,offset:scrollHeightMsg});
            $('#inputName' + reNameIndex).val(reNameParem);
            return;
        }
        var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
        //特殊字符
        if (!pattern.test(inputname)) {
            layer.msg("输入的目录名称不合法", {anim: 6, icon: 0,offset:scrollHeightMsg});
            $('#inputName' + reNameIndex).val(reNameParem);
            return;
        }
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

            /*$.ajax({
                type: "post",
                url: Hussar.ctxPath+"/fsFolder/addCheck",
                data: {
                    name: inputname,
                    folderId:chooseFile[0],
                    parentFolderId: openFileId,
                },
                async: false,
                cache: false,
                dataType: "json",
                success: function (data) {
                    if (data == "false") {
                        layer.msg("“" + inputname + "”目录已存在", {anim: 6, icon: 0,offset:scrollHeightMsg});
                        $('#inputName' + reNameIndex).val(reNameParem);
                        return;
                    } else {
                        $.ajax({
                            type: "POST",
                            url: Hussar.ctxPath+"/fsFolder/update",
                            data: {
                                ids: chooseFile[0],
                                parentFolderId: openFileId,
                                folderName: inputname,
                            },
                            contentType: "application/x-www-form-urlencoded",
                            dataType: "json",
                            async: false,
                            success: function (result) {
                                refreshFile(openFileId);
                                if(showStyle==2){
                                    $("input[name='checkboxname']").each(function () {
                                        $(this).prop("checked", false);
                                    });
                                    emptyChoose();
                                    btnState();
                                }
                            }
                        });
                    }
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFolder/addCheck", function(data) {
                if (data == "false") {
                    layer.msg("“" + inputname + "”目录已存在", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    $('#inputName' + reNameIndex).val(reNameParem);
                    return;
                } else {
                    /*$.ajax({
                        type: "POST",
                        url: Hussar.ctxPath+"/fsFolder/update",
                        data: {
                            ids: chooseFile[0],
                            parentFolderId: openFileId,
                            folderName: inputname,
                        },
                        contentType: "application/x-www-form-urlencoded",
                        dataType: "json",
                        async: false,
                        success: function (result) {
                            refreshFile(openFileId);
                            if(showStyle==2){
                                $("input[name='checkboxname']").each(function () {
                                    $(this).prop("checked", false);
                                });
                                emptyChoose();
                                btnState();
                            }
                        }
                    });*/
                    var ajax = new $ax(Hussar.ctxPath + "/fsFolder/update", function(result) {
                        refreshFile(openFileId);
                        if(showStyle==2){
                            $("input[name='checkboxname']").each(function () {
                                $(this).prop("checked", false);
                            });
                            emptyChoose();
                            btnState();
                        }
                    }, function(data) {

                    });
                    ajax.set("ids",chooseFile[0]);
                    ajax.set("parentFolderId",openFileId);
                    ajax.set("folderName",inputname);
                    ajax.start();
                }
            }, function(data) {

            });
            ajax.set("name",inputname);
            ajax.set("folderId",chooseFile[0]);
            ajax.set("parentFolderId",openFileId);
            ajax.start();
        });
    }
    var power= $("#authority"+chooseFile[0]).html()
    if(power!='2'&&noChildPower!=2&&adminFlag!=1){
        layer.msg("您没有权限重命名文件", {anim:6,icon: 0,offset:scrollHeightMsg});
        return;
    }
    inputname = inputname.trim();

    if(inputname==''||inputname==undefined){
        layer.msg("目录名称或文件名称不能为空", {anim:6,icon: 0,offset:scrollHeightMsg});
        $('#inputName'+reNameIndex).val(reNameParem);
        return;
    }
    var pattern = new RegExp("^[^/\\\\:\\*\\'\\’\\?\\<\\>\\|\"]{0,255}$");
    //特殊字符
    if(!pattern.test(inputname)){
        layer.msg("输入的文件名称不合法", {anim:6,icon: 0,offset:scrollHeightMsg});
        $('#inputName'+reNameIndex).val(reNameParem);
        return;
    }
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
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
                    layer.msg("“"+inputname+"”文件已存在", {anim:6,icon: 0,offset:scrollHeightMsg});
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


                        }
                    });
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/addCheck", function(data) {
            if(data == "false"){
                layer.msg("“"+inputname+"”文件已存在", {anim:6,icon: 0,offset:scrollHeightMsg});
                $('#inputName'+reNameIndex).val(reNameParem);
                return;
            }else {
                /*$.ajax({
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


                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/fsFile/update", function(result) {

                }, function(data) {

                });
                ajax.set("ids",chooseFile[0]);
                ajax.set("fileName",inputname);
                ajax.set("type",chooseFileType[0]);
                ajax.start();
            }
        }, function(data) {

        });
        ajax.set("name",inputname);
        ajax.set("filePid",openFileId);
        ajax.start();
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
function orderByTime(){
    if ($("#orderTime").css("display") != "none"){
        getTimeOrder();
    }else {
        getTimeOrder1();
    }
}
function iconViewVersion(e,id,type,name,userName,time,size){
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.open({
            type: 2,
            title: false,
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            offset:scrollHeightLong,
            maxmin: false,
            content: Hussar.ctxPath+"/frontVersion/viewHistory?oldDocId=" + id + "&" + Math.random(),
            success:function(layero, index){
            }
        })
    });
}
function iconUploadVersion(e,id){
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
    oldDocId = id;
    $(".webuploader-pick").trigger("click");
}
function iconUploadVersionBefore(e,id){// 该方法暂时废弃
    cancelBubble();
    changeBgColorOfTr(e);
    // 传递参数
    var path = '';
    var elem = $("#path span");
    for (var i in elem){
        path += elem.eq(i).text();
    }
    path = path.replace(/\s/g,'');
    path = path.substring(path.indexOf(">") + 1);

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.open({
            type: 2,
            title: false,
            area: ['850px', '300px'], //宽高
            fix: false, //不固定
            offset:parseInt(scrollHeightTip) - 25 + "px",
            maxmin: false,
            content: Hussar.ctxPath+"/frontVersion/viewUpload?openFileId=" + openFileId + "&path=" + encodeURIComponent(path) +"&oldDocId=" + id + "&" + Math.random(),
            success:function(layero, index){
            }
        });
    });
}
function iconSetTip(e,id,type,name,author) {
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
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

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.open({
            type: 2,
            title: ['标签设置','background-color:#fff'],
            area: ['650px', '250px'], //宽高
            fix: false, //不固定
            offset:scrollHeightTip,
            maxmin: false,
            content: Hussar.ctxPath+'/fsFile/setTip',
            success:function(){
            }
        });
    });
}

function  iconDelete(e,id,name) {
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
    layer.confirm('确定要删除所选文件吗？',{title :['删除','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},function(){
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
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

            /*$.ajax({
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
                        layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                    }else {
                        layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                    }
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                },
                error:function () {
                    layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/deleteScope", function(data) {
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
                    layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                }else {
                    layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                }
                btnState();
                // refreshTree();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            }, function(data) {
                layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                btnState();
                // refreshTree();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("fsFileIds",scopeId);
            ajax.start();
        });
    })
}
function  iconDeleteFolder(e,id,name) {
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }


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
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

       /* $.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFolder/checkFolderType",
            data: {
                ids: scopeId
            },
            async: false,
            cache: false,
            success: function (data) {
                if (data == 'haveFile') {
                    layer.msg("请先删除目录下存放的文件", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    return;
                } else {
                    layer.confirm('确定要删除所选目录吗？',{title :['删除','background-color:#fff'],offset:scrollHeightAlert}, function () {
                        var index = layer.load(1, {
                            shade: [0.1, '#fff'] //0.1透明度的白色背景
                        });
                        $.ajax({
                            type: "post",
                            url: Hussar.ctxPath+"/fsFolder/delete",
                            data: {
                                fsFolderIds: scopeId,
                            },
                            async: false,
                            cache: false,
                            success: function (data) {
                                if (data > 0) {
                                    layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                                }
                            },
                            error: function () {
                                layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                            }
                        })

                        refreshFile(openFileId);
                        emptyChoose();
                        layer.close(index);
                    })
                }
            }

        })*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFolder/checkFolderType", function(data) {
            if (data == 'haveFile') {
                layer.msg("请先删除目录下存放的文件", {anim: 6, icon: 0,offset:scrollHeightMsg});
                return;
            } else {
                layer.confirm('确定要删除所选目录吗？',{title :['删除','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'}, function () {
                    var index = layer.load(1, {
                        shade: [0.1, '#fff'] //0.1透明度的白色背景
                    });
                    /*$.ajax({
                        type: "post",
                        url: Hussar.ctxPath+"/fsFolder/delete",
                        data: {
                            fsFolderIds: scopeId,
                        },
                        async: false,
                        cache: false,
                        success: function (data) {
                            if (data > 0) {
                                layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                            }
                        },
                        error: function () {
                            layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                        }
                    })*/
                    var ajax = new $ax(Hussar.ctxPath + "/fsFolder/delete", function(data) {
                        if (data > 0) {
                            layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                        }
                    }, function(data) {
                        layer.msg("删除成功", {icon: 1,offset:scrollHeightMsg});
                    });
                    ajax.set("fsFolderIds",scopeId);
                    ajax.start();

                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                })
            }
        }, function(data) {

        });
        ajax.set("ids",scopeId);
        ajax.start();
    });
}
function  iconUpdateName(e,id,type,name,author,index) {
    $(".hoverSpan").eq(index).hide()
    $(".moreicon").hide();
    $(".ishover").addClass("hide");
    $(".nameTitpe").removeClass("hide");
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
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
function  iconMove(e,id,type,name,author) {
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
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
            area: ['350px','510px'],
            //shift : 1,
            shadeClose: false,
            title : ['目录结构','background-color:#fff'],
            offset:scrollHeightLong,
            maxmin : false,
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
    var index = layer.confirm('确定要移动所选文件吗？',{title :['移动','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},operation);
    cutFile=[].concat(chooseFile);
    cutFileType=[].concat(chooseFileType);
    cutFileName=[].concat(chooseFileName);
}
function  iconDownLoad(e,id,name) {


    download(id, name);


}

function iconSetAuthority(e,id,type,name,author) {
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
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
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.open({
            type: 2,
            title: ['文件授权','background-color:#fff'],
            area: ['686px', '510px'], //宽高
            fix: false, //不固定
            offset:scrollHeightLong,
            //maxmin: false,
            content: Hussar.ctxPath+'/fsFile/fileAuthority',
            success:function(){
            }
        });
    });
}
function iconSetAuthorityFolder(e,id,type,name,author) {
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
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
    // parent.$('body').css('overflow','hidden');//浮层出现时窗口不能滚动设置

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.open({
            type: 2,
            title: ['目录授权','background-color:#fff'],
            area: ['686px', '510px'], //宽高
            fix: false, //不固定
            //maxmin: false,
            offset:scrollHeightLong,
            content:  Hussar.ctxPath+'/fsFolder/folderAuthority',
            success: function () {
            }
        });
    });
}
function  iconUpdateNameFolder(e,id,type,name,author,index) {
    $(".hoverSpan").eq(index).hide()
    $(".moreicon").hide();
    $(".ishover").addClass("hide");
    $(".nameTitpe").removeClass("hide");
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
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
function  iconMoveFolder(e,id,type,name,author) {
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor = [];
    chooseFile.push(id);
    chooseFileType.push(type);
    chooseFileName.push(name);
    chooseFileAuthor.push(author)
    var operation = function () {
        layerView = layer.open({
            type: 1,
            area: ['350px', '510px'],
            //shift : 1,
            shadeClose: false,
            skin: 'move-class',
            title: ['目录结构','background-color:#fff'],
            offset:scrollHeightLong,
            maxmin: false,
            content: $("#folderTreeAuthority"),
            success: function () {
                initFolderTree();
                layer.close(index);
            },
            end: function () {
                layer.closeAll(index);
            }
        });
    }
    var index = layer.confirm('确定要移动所选目录吗？',{title :['移动','background-color:#fff'], offset:scrollHeightAlert,skin:'move-confirm'}, operation);
    cutFile = [].concat(chooseFile);
    cutFileName = [].concat(chooseFileName);
    cutFileType=[].concat(chooseFileType);

}
function  returnList() {
    layer.close(popWin);
}
$(function(){
    setInterval(function () {
        scrollHeight=parent.scrollHeight;
        var height = parseInt(scrollHeight);
        var screenHeight = parseInt(window.screen.availHeight);
        if( scrollHeight!=0){
            scrollHeightAlert= parseInt(height - 130 + (screenHeight - 154) / 2.0) + "px";
            scrollHeightLong= parseInt(height - 130 + (screenHeight - 510) / 2.0) + "px";
            scrollHeightTip = parseInt(height - 130 + (screenHeight - 250) / 2.0) + "px";
            scrollHeightShare = parseInt(height - 130 + (screenHeight - 200) / 2.0) + "px";
            scrollHeightMsg = parseInt(height - 130 + (screenHeight - 64) / 2.0) + "px";
            //console.log(scrollHeightAlert + ' ' + scrollHeightLong)
        }
        //console.log(height + "//" + screenHeight + " " + layerHeight)
        //$(".layui-layer.layui-layer-iframe").css("top",height - 130 + (screenHeight - layerHeight) / 2.0 + "px");
    },300);
    $('#searchName').bind('keypress', function (event) {
        if (event.keyCode == "13") {
            $("#searchBtn").click();
        }
    });
})
function shareSetting(e,fileId) {
    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.open({
            type: 2,
            area: [300 + 'px', 200 + 'px'],
            fix: false, //不固定
            maxmin: false,
            offset:parseInt(scrollHeightTip) + 25 + "px",
            shadeClose: true,
            shade: 0.4,
            title: ['修改分享权限','background-color:#fff'],
            content: Hussar.ctxPath+"/fsFile/shareFlagView?docId=" + fileId
        });
    });
}
function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").css("top",scrollHeightAlert);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}