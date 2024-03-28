
var chooseFile = [];    //选中的文件或目录的id
var chooseFileType = []; //选中的文件或目录的type
var clickFlag=false;
var currOrder = '';
var scrollHeightAlert=0;
var reNameFlag= false;      //重命名标志
var scrollHeightLong=0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var opType=$("#opType").val();
var pathId = [];        //路径
var pathName = [];
var openFileId;   //打开的文件夹的id
var cutFile = [];          //剪切的文件或目录的id
var cutFileType = [];      //剪切的文件或目录的type
var cutFileName = [];      //剪切的文件或目录的name
var layerView;
var showStyle=2;
var chooseFileIndex = [];//选中的文件或者目录的index
var moveUpData;//表格数据-用于上移
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;

    //页面初始化
    $(function () {
        var load = new Loading();
        load.init({
            target: "#dndArea"
        });
        load.start();
        setTimeout(function() {
            load.stop();
        }, 800)
        pathId = ['abcde4a392934742915f89a586989292'];
        pathName = ['常用目录'];
        createPath()
        refreshFile();
        btnState()
    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth =inner.width();
        $(".fixed-table-header").width(tableWidth)
    })
});

/*移除常用目录*/
function removeCommonFolder(e,id,fileType){
    cancelBubble();
    changeBgColorOfTr(e);

    layer.confirm('确定要移除该目录吗？',{title :['移除常用目录','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'], //0.1透明度的白色背景
            fix:true
            ,offset: scrollHeightAlert
        });

        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;
            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/deleteCollection", function(data) {
                if(data> 0){
                    var fileList = $("#thelist").find(".item");
                    for(var n = 0;n<fileList.length;n++){
                        var name = fileList.eq(n).find(".info").html().split(".")[0];
                        for(var m =0 ;m<chooseFileName.length;m++){
                            if(name == chooseFileName[m]){
                                fileList.eq(n).remove();
                            }
                        }
                    }
                    layer.msg('移除成功',{icon: 1,offset:scrollHeightMsg})
                }else {
                    layer.msg('移除失败',{anim:6,icon: 2,offset:scrollHeightMsg})
                }
                btnState();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            }, function(data) {
                layer.msg('移除异常!',{anim:6,icon: 2,offset:scrollHeightMsg})
                btnState();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("ids",id);
            ajax.start();
        });
    })
};
function refreshFile(folderId,num,size,order) {
    if (folderId==null){
        folderId = 'abcde4a392934742915f89a586989292'
    }
    var noOrder;
    currOrder = order;
    layui.use(['laypage', 'layer', 'table', 'Hussar', 'HussarAjax'], function () {
        var laypage = layui.laypage,
            Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var name = $('#searchName').val();
        var ajax = new $ax(Hussar.ctxPath + "/fsCommonFolder/getCommonFolder", function (data) {
            laypage.render({
                elem: 'laypageAre'
                , count: data.count //数据总数，从服务端得到
                , limit: 60
                , layout: ['prev', 'page', 'next']
                , curr: num || 1 //当前页
                , jump: function (obj, first) {
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if (!first) {
                        refreshFile(folderId,obj.curr, obj.limit, currOrder)
                    }
                }
            });
            moveUpData = data;
            $("#amount").html("已全部加载" + data.count + "个");
            openFileId = folderId;
            createPath();
            drawFile(data);
            emptyChoose();
            btnState();
            dbclickover = true;
            if(noOrder==true){
                $("#orderName").hide();
                $("#orderName1").show();
                $("#orderTime").hide();
                $("#orderTime1").hide();
                $("#orderCfName").hide();
                $("#orderCfName1").hide();
            }else{
                if(order== "1"){
                    $("#orderName").show();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderCfName").hide();
                    $("#orderCfName1").hide();
                }
                if(order== "0"){
                    $("#orderName1").show();
                    $("#orderName").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderCfName").hide();
                    $("#orderCfName1").hide();
                }
                if(order== "2"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime1").hide();
                    $("#orderTime").hide();
                    $("#orderCfName").hide();
                    $("#orderCfName1").show();
                }
                if(order== "3" ){
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderCfName").show();
                    $("#orderCfName1").hide();
                }
                if(order== "4"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").show();
                    $("#orderTime1").hide();
                    $("#orderCfName").hide();
                    $("#orderCfName1").hide();
                }
                if(order== "5"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").show();
                    $("#orderCfName").hide();
                    $("#orderCfName1").hide();
                }
                if(order== "6"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderCfName").hide();
                    $("#orderCfName1").hide();
                }
                if(order== "7"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderCfName").hide();
                    $("#orderCfName1").hide();
                }

            }
            // 取消加入按钮显示
            $(".hoverEvent").hover(function () {
                $(this).find("td>.hoverSpan").show();
            }, function () {
                $(this).find("td>.hoverSpan").hide();
            });
            $(".layui-table tr").hover(function () {
                //alert($(this).prev());
                $(this).find("td").css("border-color", "#DAEBFE");
                $(this).prev().find("td").css("border-color", "#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color", "rgba(242,246,253,1)");
                $(this).prev().find("td").css("border-color", "rgba(242,246,253,1)");
            });
            $(".layui-table tbody tr:first").hover(function () {
                $(this).find("td").css("border-color", "#DAEBFE");
                $("thead").find("tr").css("border-bottom-color", "#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color", "rgba(242,246,253,1)");
                $("thead").find("tr").css("border-bottom-color", "rgba(242,246,253,1)");
            })
            if (data.count == 0) {
                $("#laypageAre").hide();
            } else {
                $("#laypageAre").show();
            }
        }, function (data) {

        });
        ajax.set("pageNumber", num);
        ajax.set("pageSize", size);
        ajax.set("name", name);
        ajax.set("order", currOrder);
        ajax.set("parentFolderId", folderId);
        ajax.start();
    });
}

function getNameOrder() {
    refreshFile(openFileId,null,null,0);
}
function getNameOrder1() {
    refreshFile(openFileId,null,null,1);
}
function getCfNameOrder() {
    refreshFile(openFileId,null, null, 2);
}
function getCfNameOrder1() {
    refreshFile(openFileId,null, null, 3)
}
function getTimeOrder() {
    refreshFile(openFileId,null,null,5);
}
function getTimeOrder1() {
    refreshFile(openFileId,null, null, 4);
}
function orderByCfName(){
    if ($("#orderCfName").css("display") != "none"){
        getCfNameOrder();
    }else {
        getCfNameOrder1();
    }
}
function orderByName(){
    if ($("#orderName").css("display") != "none"){
        getNameOrder();
    }else {
        getNameOrder1();
    }
}
function orderByTime(){
    if ($("#orderTime").css("display") != "none"){
        getTimeOrder();
    }else {
        getTimeOrder1();
    }
}
function drawFile(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param.rows,
            "adminFlag":param.adminFlag
        };
        var getTpl = $("#demo1").html()
            ,view = document.getElementById('view');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view");
            var tableWidth =inner.width();
            $(".fixed-table-header").width(tableWidth);
            if (param.rows.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                },200);
            }else {
                $("div.noDataTip").hide();
            }
        });
    });

}
function dbclick(id,name){
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;
        var url = "/frontUpload/upload?openFileId=" + id + "&folderName=" + encodeURI(name);
        if (id != undefined && id != ''){
            url += "&returnDocId=" + id;
        }
        window.parent.open(Hussar.ctxPath+url,"mainFrame")
    });
}
function createPath(){
    $("#path").empty();
    // $("#path").append("<span class='total'>");
    for(var i=0;i<pathId.length;i++){
        if(i==pathId.length-1){
            var param= '<span>'+pathName[i]+'</span>'
        }else {
            var param= '<span><a style="cursor: pointer; color: #3C91FD;" onclick="clickPath(\''+pathId[i]+'\')">'+pathName[i]+'</a>'+'  >  </span>'        }
        $("#path").append(param);


    }
    var timer;
    // $("#path").append("</span>");
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
function clickPath(id) {
    while(pathId.indexOf(id)+1!=pathId.length){
        pathId.pop();
        pathName.pop();
    }
    createPath();
    refreshFile(id);
}
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}

function btnState() {
    if(chooseFile.length==0){
        $('.clickBtn').hide()
    }
    if(chooseFile.length>0){
        if(chooseFile.length>1){
            for(var i=0;i<chooseFileType.length;i++){
                if(chooseFileType[i+1]!=undefined){
                    if((chooseFileType[i]=="folder"&&chooseFileType[i+1]!="folder")
                        ||(chooseFileType[i]!="folder"&&chooseFileType[i+1]=="folder")){
                        $('.clickBtn').hide();
                        $(".uploadBtn").hide();
                        return;
                    }else {
                        $('.clickBtn').show();
                    }
                }else{
                    $('.clickBtn').show();
                }
            }

        }else {
            $('.clickBtn').show();
        }
    }
    if(openFileId!='abcde4a392934742915f89a586989292'){
        $("#newFolder").hide()
    }else {
        $("#newFolder").show()
    }
    // if(moveUpData.rows.length!==0){
    //     if(moveUpData.rows.length===chooseFile.length){
    //         $('.allSelect').prev().addClass("layui-form-checked");
    //
    //     }else{
    //         $('.allSelect').prev().removeClass("layui-form-checked");
    //     }
    // }

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
    // if(jq.find(".checkbox").prop("checked")==false){
    //
    //     jq.find(".checkbox").prop("checked",true);
    //     jq.find(".layui-form-checkbox").addClass("layui-form-checked");
    //
    //     chooseFile.push(id);
    //     chooseFileType.push(type);
    //     chooseFileName.push(name);
    //     chooseFileAuthor.push(author)
    //
    //
    // }else{
    //     jq.find(".checkbox").prop("checked",false);
    //     jq.find(".layui-form-checkbox").removeClass("layui-form-checked");
    //     if(chooseFile.indexOf(id)!=-1){
    //         if(reNameFlag == false){
    //             chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
    //             chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
    //             chooseFile=chooseFile.del(chooseFile.indexOf(id));
    //         }
    //         chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
    //     }
    //     if(chooseFile.length==1){
    //         var id = chooseFile[0];
    //         reNameParem=chooseFileName[0];
    //         var index=  $("#"+id+"").val();
    //         reNameIndex=index
    //     }
    //
    // }
    btnState();
    cancelBubble()
}
$("#marg").on('click', function () {
    if(reNameFlag==true){
        $('#name'+reNameIndex).removeClass("hide");
        $('#inputName'+reNameIndex).addClass("hide");
        reNameFlag=false;
        var inputname = $('#inputName'+reNameIndex).val();
        if(inputname!=reNameParem){
            rename(inputname);
        }
    }
});
function checkAll(e) {
    var self = $(e).siblings('input');//
    $(e).toggleClass('layui-form-checked');
    if(self.prop("checked")==false){
        self.prop("checked",true);
    }else{
        self.prop("checked",false);
    }
    if (self.prop("checked")) { // 全选
        chooseFile=[]
        chooseFileType=[]
        chooseFileName=[]
        chooseFileAuthor=[]
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
function emptyChoose() {
    chooseFile = [];
    chooseFileType = [];
    chooseFileName = [];
    chooseFileAuthor=[]
}
function  clickIconCheck(e,id,type,name,index,author) {
    $(e).toggleClass('layui-form-checked');
    var checkbox=$(e).siblings('.checkbox');
    if(checkbox.prop("checked")==false){
        checkbox.prop("checked",true);
        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)
    }else{
        checkbox.prop("checked",false);
        $()
        if(chooseFile.indexOf(id)!=-1){
            chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
            chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
            chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
        }
    }

    btnState();
    cancelBubble()
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
function  updateNameCollectionFolder(e,id,type,name,author,index) {
    cancelBubble();
    layui.use(['layer', 'jquery', 'form'], function () {
        // 这个时候将挂载到layui中的layer放置到一个变量上面，有助于我们调用
        var layer = layui.layer
        var $ = layui.jquery
        var form = layui.form
        // 首先第一种调用方法就是(采用的是jquery的点击事件)
            // 调用的layer弹窗
        layerView = layer.open({
            type: 1,
            btn: ['确定', '取消'],
            area: ['500px', '190px'],
            shadeClose: false,
            title: ['别名修改', 'background-color:#fff'],
            maxmin: false,
            content: `<input class="aliasModification" value="${name}"/>`,
            skin:'rename',
            end: function () {
                layer.closeAll();
            },
            btn1: function (index, layero) {
                // debugger
                var reNameValue = $('.aliasModification').val().trim();

                if (reNameValue == '' || reNameValue == undefined || reNameValue == null) {
                    layer.msg("名称不能为空", {anim: 6, icon: 0, offset: scrollHeightMsg});
                    return;
                }

                if (reNameValue != name) {
                        // if (noChildPowerFolder == 0) {
                        //     layer.msg("您没有重命名目录权限", {anim: 6, icon: 0, offset: scrollHeightMsg});
                        //     return;
                        // }
                        var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
                        //特殊字符
                        if (!pattern.test(reNameValue)) {
                            layer.msg("输入的目录名称不合法", {anim: 6, icon: 0, offset: scrollHeightMsg});
                            return;
                        }
                        layui.use(['Hussar', 'HussarAjax'], function () {
                            var Hussar = layui.Hussar,
                                $ax = layui.HussarAjax;
                                    var ajax = new $ax(Hussar.ctxPath + "/fsCommonFolder/updateCommonFold", function (result) {
                                        refreshFile(openFileId);
                                        if (showStyle == 2) {
                                            $("input[name='checkboxname']").each(function () {
                                                $(this).prop("checked", false);
                                            });
                                            emptyChoose();
                                            btnState();
                                        }
                                    }, function (data) {

                                    });
                                    ajax.set("commonFolderId", id);
                                    ajax.set("commonFolderName", reNameValue);
                                    ajax.start();

                        });
                    }
                    refreshFile(openFileId);
                layer.closeAll();
            }
        });

    })

}
//批量删除
function batchRemove(){

    deleteFile()
}
//上移
function moveFileUp(){
    layui.use(['Hussar','HussarAjax'], function() {
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        getTableData();
        if (chooseFile.length != 1) {
            layer.alert('请选择一条要上移的目录', {
                icon: 0,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
            return;
        }
        var index = chooseFileIndex[0];
        if (index != 0) {
            var ajax = new $ax(Hussar.ctxPath + "/fsCommonFolder/moveFolder", function (result) {
                refreshFile(moveUpData.rows[index-1].commonFolderId);
                $('div.layui-form-checkbox').eq(index).removeClass('layui-form-checked')
                var moveuptd = $('div.layui-form-checkbox')[index]
                moveuptd.click();
            }, function (data) {

            });
            ajax.set("idOne", chooseFile[0]);
            ajax.set("idTwo", moveUpData.rows[index - 1].commonFolderId);
            ajax.start();
        } else {
            layer.alert('已经上移到最顶端', {
                icon: 0,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
        }
    })
}
//下移
function moveFileDown(){
    layui.use(['Hussar','HussarAjax'], function() {
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
            getTableData();
        if (chooseFile.length != 1) {
            layer.alert('请选择一条要下移的目录', {
                icon: 0,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
            return;
        }
        var index = chooseFileIndex[0];
        if (index != moveUpData.rows.length -1) {
            var ajax = new $ax(Hussar.ctxPath + "/fsCommonFolder/moveFolder", function (result) {
                refreshFile(moveUpData.rows[index].commonFolderId);
                $('div.layui-form-checkbox').eq(index).removeClass('layui-form-checked')
                var td = $('div.layui-form-checkbox')[index+1 ]
                td.click();
            }, function (data) {

            });
            ajax.set("idOne", chooseFile[0]);
            ajax.set("idTwo", moveUpData.rows[++index].commonFolderId);
            ajax.start();
        } else {
            layer.alert('已经下移到最底端', {
                icon: 0,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
        }
    })
}
//目录删除
function singleDeleteFile(e,id){
    cancelBubble()
    if(chooseFile.length>1){
        layui.use(['layer', 'jquery', ], function () {
            // 这个时候将挂载到layui中的layer放置到一个变量上面，有助于我们调用
            var layer = layui.layer
            var $ = layui.jquery
            // 首先第一种调用方法就是(采用的是jquery的点击事件)
                // 调用的layer弹窗
                layer.msg('请只选择一个')

        })
        return
    }
    deleteFile(id)

}
function deleteFile(id){
    layui.use([ 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
        var $ = layui.jquery,
            $ax = layui.HussarAjax;

        // for (var i = 0; i < chooseFile.length; i++) {
        //     var power = $("#authority" + chooseFile[i]).html()
        //     if (power != '2' && noChildPower != 2 && adminFlag != 1) {
        //         layer.msg("您没有权限删除文件", {anim: 6, icon: 0, offset: scrollHeightMsg});
        //         return;
        //     }
        //
        // }
        // if(chooseFile.length==0){
        //     layer.msg("请选择要删除的文件", {anim:6,icon: 0,offset:scrollHeightMsg});
        //     return;
        // }
        layer.confirm('确定要删除所选文件吗？', {
            title: ['删除', 'background-color:#fff'],
            offset: scrollHeightAlert,
            skin: 'move-confirm'
        }, function () {
            var index = layer.load(1, {
                shade: [0.1, '#fff'] //0.1透明度的白色背景
                , offset: scrollHeightAlert
            });

            for (var i = 0; i < chooseFile.length; i++) {
                for (var j = 0; j < cutFile.length; j++) {
                    if (cutFile[j] == chooseFile[i]) {
                        cutFile = cutFile.del(j);
                        break;
                    }
                }
            }

            var scopeId = ''
            if(id!=undefined ){
                 scopeId = id
            }else{
                 scopeId = chooseFile.join(',')
            }
            var ajax = new $ax(Hussar.ctxPath + "/fsCommonFolder/deleteCommonFold", function (data) {
                //不知道啥意思
                // if (data > 0) {
                //     var fileList = $("#thelist").find(".item");
                //     for (var n = 0; n < fileList.length; n++) {
                //         var name = fileList.eq(n).find(".info").html().split(".")[0];
                //         for (var m = 0; m < chooseFileName.length; m++) {
                //             if (name == chooseFileName[m]) {
                //                 fileList.eq(n).remove();
                //                 uploader.removeFile(fileList.eq(n).attr("id"), true);
                //             }
                //         }
                //     }
                //     layer.msg("删除成功", {icon: 1, offset: scrollHeightMsg});
                // } else {
                //
                //     layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                // }
                btnState();
                // refreshTree();
                refreshFile(openFileId);
                emptyChoose();
                layer.closeAll();
            }, function (data) {
                layer.msg("删除异常", {icon: 2, offset: scrollHeightMsg});
                btnState();
                //  refreshTree();
                refreshFile(openFileId);
                emptyChoose();
                layer.closeAll();
            });
            ajax.set("ids", scopeId);
            ajax.start();
        })
    })
}

function getTableData() {
    chooseFile = [];
    chooseFileName = [];
    chooseFileType = [];
    chooseFileIndex = [];
    var Check = $("#tableData input[type=checkbox]:checked");//在table中找input下类型为checkbox属性为选中状态的数据
    Check.each(function () {//遍历
        var row = $(this).parent("td").parent("tr");//获取选中行
        var id = row.find("[name='checkFileId']").val();//获取id值
        var name = row.find("[name='checkFileName']").val();//获取name值
        var type = row.find("[name='checkFileType']").val();//获取type值
        var index = row.find("[id='"+id+"']").val();
        chooseFile.push(id);
        chooseFileName.push(name);
        chooseFileType.push(type);
        chooseFileIndex.push(index);
    });
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
        }
    },300);
})

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
function rename(inputname){
    if(chooseFileType[0]=='folder'){
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
            var ajax = new $ax(Hussar.ctxPath + "/personalCollection/addCheck", function(data) {
                if (data == "false") {
                    layer.msg("“" + inputname + "”目录已存在", {anim: 6, icon: 0,offset:scrollHeightMsg});
                    $('#inputName' + reNameIndex).val(reNameParem);
                    return;
                } else {
                    var ajax = new $ax(Hussar.ctxPath + "/personalCollection/updateFolderName", function(result) {
                        refreshFile(openFileId);
                    }, function(data) {
                    });
                    ajax.set("collectionId",chooseFile[0]);
                    ajax.set("folderName",inputname);
                    ajax.start();
                }
            }, function(data) {

            });
            ajax.set("name",inputname);
            ajax.set("parentFolderId",openFileId);
            ajax.start();
        });
    }
    refreshFile(openFileId);
}