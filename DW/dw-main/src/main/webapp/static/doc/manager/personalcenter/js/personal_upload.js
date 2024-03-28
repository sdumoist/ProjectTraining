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
    /*删除目录*/
    $("#delCategoryBtn").on('click',function(){


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
                url:"/fsFile/deleteScope",
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

    /*搜索按钮*/
    $("#searchBtn").on('click',function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        refreshFile(null,null);
        layer.close(index);
    });
    $("#reName").on('click',function(){



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

    /*多选下载*/
    $("#mulDownLoad, #manyMulDownLoad").on('click',function(){
        if(chooseFile.length==0){
            layer.msg("请选择要下载的文件", {anim:6,icon: 0});
            return;
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


    function start() {
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");

        btnState()
    }


    $("#cancel").on('click',function(){
        var index = layer.getFrameIndex(window.name);
        layer.close(index);
    });

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
    //监听允许下载操作


    //页面初始化
    $(function () {
        getLoginUser();
        refreshFile();
    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth =inner.width();
        //fixed-table-header
        $(".fixed-table-header").width(tableWidth)
    })
});

function drawFile(param,showFlag) {

        layui.use('laytpl', function(){
            var laytpl = layui.laytpl;
            var data = { //数据
                "list":param,
                "adminFlag":adminFlag
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
function refreshFile(num,size){
    layui.use(['laypage','layer','table'], function(){
        var laypage = layui.laypage,
            layer = layui.layer;
        var name = $('#searchName').val();
        $.ajax({
            type:"post",
            url:"/personalUpload/getlist",
            data:{
                pageNumber:num,
                pageSize:size,
                name:name,
            },
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
                            refreshFile(obj.curr,obj.limit)
                        }
                    }
                });
                
                drawFile(data.rows);
                drawPower(data.isAdmin);
                userId=data.userId;
                noChildPower=data.noChildPower;
                isChild=data.isChild;
                emptyChoose();
                btnState();
                dbclickover = true;
                var flag=false;
                var fileIds= [];
           

            
                $(".file-container-flatten").height($("body").height() - $(".toolBar").outerHeight(true) -
                    $("#pathDiv").outerHeight(true) - $(".orderSearch").outerHeight(true)  - $("#laypageAre").outerHeight(true)-35);
            }
        });

    });
}

function refreshTree(){

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
    if(fileSuffixName==".png"||fileSuffixName==".jpg"||fileSuffixName==".gif"||fileSuffixName==".bmp"||fileSuffixName==".jpeg"){
        openWin("/preview/toShowIMG?id=" + id);
    }else if(fileSuffixName==".mp4"||fileSuffixName==".wmv"){
        openWin("/preview/toShowVideo?id=" + id);
    } else if(fileSuffixName==".mp3"||fileSuffixName==".m4a"){
        openWin("/preview/toShowVoice?id=" + id);
    }else if(isPDFShow(fileSuffixName)){
        openWin("/preview/toShowPDF?id=" + id );
    } else {
        openWin("/preview/toShowOthers?id=" + id );
    }
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
    var e=arguments.callee.caller.arguments[0]||event;
    window.event? window.event.cancelBubble = true : e.stopPropagation();
    $.ajaxFileUpload({
        url : "/files/fileDownNew",
        type : "post",
        async:false,
        data : {
            docName : "",//name,
            docIds : id
        }
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
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
    }
    btnState();
    cancelBubble()
}
function btnState() {
    if(chooseFile.length==0){
        $('.clickBtn').hide()
        $(".webuploader-pick").show();
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
}

$(document).click(function(e){
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
    $.ajax({
        type:"post",
        url:"/fsFile/addCheck",
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
                    url: "/fsFile/update",
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