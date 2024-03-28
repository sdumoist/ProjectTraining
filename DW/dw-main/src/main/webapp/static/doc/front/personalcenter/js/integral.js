/**
 * Create By luzhanzhao
 * date 2018-11-19
 */
var chooseFile = [];    // 选中的文件或目录的id
var clickFlag=false;
var opType=$("#opType").val();
var count = 0;  // 渲染次数
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
    var $ = layui.jquery,
        form=layui.form,
        jstree=layui.jstree,
        laypage = layui.laypage,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element,
        Hussar = layui.Hussar;
    start();

    /*搜索按钮*/
    $("#searchBtn").on('click',function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        refreshFile(null,null);
        layer.close(index);
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

    $("#play").click(function () {
        layer.open({
            type: 2,
            area: [ '649px',  '575px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title:['如何玩转积分','font-size:16px;font-weight:border'],
            closeBtn:2,
            offset: "30px",
            // offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
            content: Hussar.ctxPath+"/personalIntegral/ruleShow"
        });
    })

    /*删除记录*/
    $("#delHistoryBtn").on('click',function(){

        if(chooseFile.length==0){
            layer.msg("请选择要删除的记录", {anim:6,icon: 0});
            return;
        }

        layer.confirm('确定要删除所选记录吗？',function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'] //0.1透明度的白色背景
            });
            if(chooseFile.length==0){
                layer.close(index);
                return;
            }
            var histories = chooseFile.join(',')
            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+"/personalOperate/deleteHistory",
                data:{
                    histories: histories,
                    opType: opType
                },
                async:true,
                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                cache:false,
                success:function(data){
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

                    }else {

                    }
                    btnState();
                    // refreshTree();
                    refreshFile();
                    emptyChoose();
                    layer.close(index);
                },
                error:function () {

                    btnState();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/personalOperate/deleteHistory", function(data) {
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

                }else {

                }
                btnState();
                // refreshTree();
                refreshFile();
                emptyChoose();
                layer.close(index);
            }, function(data) {
                btnState();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("histories",histories);
            ajax.set("opType",opType);
            ajax.start();
        })
    });

    /*清空记录*/
    $("#clearHistoryBtn").on('click',function(){


        layer.confirm('确定要清空下载记录吗？',function(){
            var index = layer.load(1, {
                shade: [0.1,'#fff'] ,//0.1透明度的白色背景
                offset: scrollHeightAlert
            });
            /*$.ajax({
                type:"post",
                url: Hussar.ctxPath+"/personalOperate/clearHistory",
                data: {opType: opType},
                async:true,
                cache:false,
                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                success:function(data){
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

                    }else {

                    }
                    btnState();
                    // refreshTree();
                    refreshFile();
                    emptyChoose();
                    layer.close(index);
                },
                error:function () {

                    btnState();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/personalOperate/clearHistory", function(data) {
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

                }else {

                }
                btnState();
                // refreshTree();
                refreshFile();
                emptyChoose();
                layer.close(index);
            }, function(data) {
                btnState();
                refreshFile(openFileId);
                emptyChoose();
                layer.close(index);
            });
            ajax.set("opType",opType);
            ajax.start();
        })
    });


    function start() {
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");

        btnState()
    }


    $("#cancel").on('click',function(){
        var index = layer.getFrameIndex(window.name);
        layer.close(index);
    });


    //页面初始化
    $(function () {
        refreshFile();
    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth =inner.width();
        //fixed-table-header
        $(".fixed-table-header").width(tableWidth)
    })
});
function refreshFile(num,size,ruleCode){
    layui.use(['laypage','layer','table','Hussar','HussarAjax'], function(){
        var laypage = layui.laypage;
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var name = $('#searchName').val();
        ruleCode = (ruleCode != undefined && ruleCode != null) ? ruleCode.toString() : null;
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/personalIntegral/list",
            data:{
                pageNumber:num,
                pageSize:size,
                'ruleCodes': ruleCode
            },
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            async:true,
            cache:false,
            dataType:"json",
            success:function(data){
                laypage.render({
                    elem: 'laypageAre'
                    ,count: data.count //数据总数，从服务端得到
                    ,limit: 60
                    ,layout: ['prev', 'page', 'next']
                    ,curr: num || 1 //当前页
                    ,jump: function(obj, first){
                        //obj包含了当前分页的所有参数，比如：
                        // obj.curr得到当前页，以便向服务端请求对应页的数据。
                        // obj.limit得到每页显示的条数
                        //首次不执行
                        if(!first){
                            refreshFile(obj.curr,obj.limit,ruleCode)
                        }
                    }
                });
                $("#amount").html(data.count+"&nbsp;条记录")
                $("#present").html(data.present);
                drawFile(data.rows);
                if (count == 0){
                    drawIntegral(data.integrals);
                    count ++;
                }


                emptyChoose();
                btnState();
                dbclickover = true;


            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalIntegral/list", function(data) {
            laypage.render({
                elem: 'laypageAre'
                ,count: data.count //数据总数，从服务端得到
                ,limit: 60
                ,layout: ['prev', 'page', 'next']
                ,curr: num || 1 //当前页
                ,jump: function(obj, first){
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if(!first){
                        refreshFile(obj.curr,obj.limit,ruleCode)
                    }
                }
            });
            $("#amount").html(data.count+"&nbsp;条记录")
            $("#present").html(data.present);
            drawFile(data.rows);
            if (count == 0){
                drawIntegral(data.integrals);
                count ++;
            }


            emptyChoose();
            btnState();
            dbclickover = true;
        }, function(data) {

        });
        ajax.set("pageNumber",num);
        ajax.set("pageSize",size);
        ajax.set("ruleCodes",ruleCode);
        ajax.start();

        // $.ajax({
        //     type:"post",
        //     url:"/personalIntegral/ruleList",
        //     data:{
        //
        //     },
        //     async:true,
        //     cache:false,
        //     dataType:"json",
        //     success:function(result){
        //         $(".integral-info-info ").html("");
        //         var inner="<ul>";
        //         var data=result.list
        //         for(var  i=0;i<data.length;i++){
        //             inner+="<li>"+data[i]+"</li>";
        //         }
        //         inner+="<li style='color:#F86842;margin-top: 10px'>注："+result.limit+"</li>";
        //         inner+="</ul>"
        //
        //
        //         $(".integral-info-info ").html(inner);
        //
        //     }
        // });
    });
}

function drawFile(param) {

    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
        };
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
function drawIntegral(param) {

    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = param;
        var getTpl = $("#integral").html()
            ,view = document.getElementById('viewIntegral');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#viewIntegral");
            //var tableWidth =inner.width();
            //fixed-table-header
            //$(".fixed-table-header").width(tableWidth)
        });
    });


}
function dbclick(id,type,name){
    if(dbclickover==true) {
        if (clickFlag) {//取消上次延时未执行的方法
            clickFlag = clearTimeout(clickFlag);
        }
        dbclickover=false;
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
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
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
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
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
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
    });
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
        $(".webuploader-pick").show();
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
        }

    }
}
function  clickCheck(e,id) {

    var jq=$(e);
    if(jq.find(".checkbox").prop("checked")==false){

        jq.find(".checkbox").prop("checked",true);
        jq.find(".layui-form-checkbox").addClass("layui-form-checked");

        chooseFile.push(id);


    }else{
        jq.find(".checkbox").prop("checked",false);
        jq.find(".layui-form-checkbox").removeClass("layui-form-checked");
        if(chooseFile.indexOf(id)!=-1){
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
        }
    }

    btnState();

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
function emptyChoose() {
    chooseFile = [];
    chooseFileType = [];
    chooseFileName = [];
    chooseFileAuthor=[]
}
function  clickIconCheck(e,id) {
    $(e).toggleClass('layui-form-checked');
    var checkbox=$(e).siblings('.checkbox');
    if(checkbox.prop("checked")==false){
        checkbox.prop("checked",true);
        chooseFile.push(id);
    }else{
        checkbox.prop("checked",false);
        if(chooseFile.indexOf(id)!=-1){
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
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
            refreshFile()
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }
    if($(th).prop("checked")){
        chooseFile.push(id);
    }else{
        if(chooseFile.indexOf(id)!=-1){
            chooseFile=chooseFile.del(chooseFile.indexOf(id));
        }
    }
    btnState();
    cancelBubble()
}