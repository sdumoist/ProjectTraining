/**
 * Created by smt on 2018/6/30.
 */
var hussar;
var openFolderId;   //打开的文件夹的id
var chooseFolder = [];    //选中的目录的id
var chooseFolderName = []; //选中的目录的name
var pathId = [];        //路径
var pathName = [];
var key='';
var adminFlag;
var userId;
var categoryId;
var reNameFlag= false;      //重命名标志
var reNameParem='';
var reNameIndex='';
var clickFlag=false;
var treeData;
var groupId=[];
var personId=[];
var organId = [];
var personParam = [];
var groupParam = [];
var organParam = [];
var groupIdPower=[];
var personIdPower=[];
var personParamPower = [];
var groupParamPower = [];
var noChildPower=1;
var dbclickover=true;
layui.use(['form', 'laypage', 'jquery','layer','Hussar','jstree','laytpl'], function() {
    var $ = layui.jquery,
        form=layui.form,
        jstree=layui.jstree,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer;
    //初始化树
    hussar=Hussar.ctxPath;
    start();

    /*上一级目录*/
    $("#upLevel").on('click',function(){
        if(pathId.length==1){
            return;
        }
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        refreshFolder(pathId[pathId.length-2]);
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
        refreshFolder(openFolderId,null,null,"1");
        layer.close(index);
    });
    form.on('radio(visible)', function (data) {
        if (data.value == "0"){
           //  $('#setAuthority').addClass('layui-btn-disabled').attr('disabled',"true");
           //  //$("#isEdit").attr("checked","checked");
           // // $('#isEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
           //  $('.name-list').hide()
        }else {
            // $('#setAuthority').removeClass('layui-btn-disabled').removeAttr('disabled',"false");
            // //$("#isEdit").remove("checked");
            // //$('#isEdit').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled',"false");
            // $('.name-list').show()
        }
        form.render();
    });
    /*权限设置*/
    $("#authorityBtn").on('click',function(){
        groupId = [];
        personId = [];
        personParam = []
        groupParam = [];
        groupIdPower=[];
        personIdPower=[];
        personParamPower = [];
        groupParamPower = [];
        //$("#categoryName").val("");
        /* $("#desc").val("");*/
        if(noChildPower==0){
            layer.msg("您没有授权目录权限", {anim:6,icon: 0});
            return;
        }
        if(chooseFolder.length!=1){
            layer.msg("请选择一个要设置权限的目录", {anim:6,icon: 0});
            return;
        }
        $.ajax({
            type:"post",
            url:"/fsFolder/getAuthority",
            data:{
                folderId:chooseFolder[0],
            },
            async:false,
            cache:false,
            dataType:"json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success:function(data){
                if(data.length>0){
                    editParam = data;
                    var editCheck= data[0].isEdit;

                    if(editCheck.indexOf("0")!=-1){
                        $("#isEditEdit").attr("checked","checked");
                    }else {
                        $('#isEditEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
                        $("#isEditEdit").prop("checked", false);
                    }
                    if(editCheck.indexOf("1")!=-1 ){
                        $("#isEditChild").attr("checked","checked");
                    }else {
                        $('#isEditChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
                        $("#isEditChild").prop("checked", false);
                    }
                    $("input[name=visible]")[1].checked = true;
                    $("input[name=visible]")[0].checked = false;
                }else {
                    $("input[name=visible]")[0].checked = true;
                    $("input[name=visible]")[1].checked = false;
                }

                for(var i = 0;i<data.length;i++){
                    if(data[i].authorType == "0"){
                        if(data[i].operateType==0){
                            var datajson={"id":data[i].authorId,"name":data[i].authorId,"type":0,"organId":data[i].organId};
                            personParam.push(datajson);
                        }else{
                            var datajson={"id":data[i].authorId,"name":data[i].authorId,"type":0,"organId":data[i].organId};
                            personParamPower.push(datajson);
                        }
                    }else {
                        if(data[i].operateType==0){
                            var datajson={"id":data[i].authorId,"name":data[i].groupName,"type":1};
                            groupParam.push(datajson)
                        }else{
                            var datajson={"id":data[i].authorId,"name":data[i].groupName,"type":1};
                            groupParamPower.push(datajson)
                        }
                    }
                }
                personId = personParam;
                groupId = groupParam;
                personIdPower = personParamPower;
                groupIdPower = groupParamPower;
                createAuthority(groupParam,personParam);
                createAuthorityPower(groupParamPower,personParamPower)
                $("#categoryName").val(chooseFolderName[0]);
                form.render();
            }
        });
        layer.open({
            type: 1,
            btn: ['确定','取消'],
            area: ['60%','65%'],
            skin: 'confirm-class',
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: "目录授权",
            content: $('#editDiv'),
            btn1: function(index, layero){
                var categoryName = $("#categoryName").val().trim();
                var isEdit = $("input[name='isEditEdit']:checked").val();
                if(isEdit!= undefined&&isEdit=='on'){
                    isEdit='0'
                }else {
                    isEdit = '';
                }
                var isEditChild = $("input[name='isEditChild']:checked").val();
                if(isEditChild!= undefined&&isEditChild=='on'){
                    isEdit=isEdit+'1'
                }else {
                    isEdit=isEdit+''
                }
                var visible = $("input[name='visible']:checked").val();

                if(categoryName.length<=0){
                    layer.msg("目录名称不能为空", {anim:6,icon: 0});
                    return false;
                }
                if(categoryName.length>30){
                    layer.msg("目录名称不能超过30", {anim:6,icon: 0});
                    return false;
                }
                var pattern = new RegExp("^[^/\\\\:\\*\\?\\<\\>\\|\"]{0,255}$");
                //特殊字符
                if(!pattern.test(categoryName)){
                    layer.msg("输入的目录名称不合法", {anim:6,icon: 0});
                    return;
                }
                if(groupId.length==0&&personId.length==0){
                    visible='0';
                }
                else{
                    visible='1';
                }

                var groupStr = '';
                var personStr ='';
                var personOrganStr ='';
                var group = [];
                var person = [];
                var personOrgan = [];
                if (groupId!=undefined){
                    for (var i = 0; i < groupId.length; i++) {
                        group.push(groupId[i].id);
                    }
                    var groupStr = group.join(",")
                }
                if (personId!=undefined){
                    for (var i = 0; i < personId.length; i++) {
                        person.push(personId[i].id);
                        personOrgan.push(personId[i].organId);
                    }
                    personStr = person.join(",")
                    personOrganStr = personOrgan.join(",")
                }
                var groupStrPower = '';
                var personStrPower ='';
                var personOrganStrPower ='';
                var groupPower = [];
                var personPower = [];
                var personOrganPower = [];
                if (groupIdPower!=undefined){
                    for (var i = 0; i < groupIdPower.length; i++) {
                        groupPower.push(groupIdPower[i].id);
                    }
                    var groupStrPower = groupPower.join(",")
                }
                if (personIdPower!=undefined){
                    for (var i = 0; i < personIdPower.length; i++) {
                        personPower.push(personIdPower[i].id);
                        personOrganPower.push(personIdPower[i].organId);
                    }
                    personStrPower = personPower.join(",")
                    personOrganStrPower = personOrganPower.join(",")
                }

                $.ajax({
                    type: "POST",
                    url: "/fsFolder/editAuthority",
                    data : {
                        visibleRange:visible,
                        isEdit:isEdit,
                        group:groupStr,
                        person:personStr,
                        personOrgan:personOrganStr,
                        groupPower:groupStrPower,
                        personPower:personStrPower,
                        personOrganPower:personOrganStrPower,
                        folderId:chooseFolder[0]
                    },
                    contentType:"application/x-www-form-urlencoded",
                    dataType:"json",
                    async: false,
                    success:function(result) {
                        layer.closeAll();
                        refreshFolder(openFolderId);
                        refreshTree();
                        layer.alert('授权成功', {
                            icon :  1,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示'
                        });
                    }
                });
            },
        });
    });


    function start() {
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");
        initTree();
        getUserTree();
    }
    $("#setAuthority").click(function(){
        layer.open({
            type: 2,
            title: '权限设置',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath+'/fsFolder/authority',
            success:function(){
            }
        });
    });
    $("#setAuthority1").click(function(){
        layer.open({
            type: 2,
            title: '权限设置',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath+'/fsFolder/authorityPower',
            success:function(){
            }
        });
    });
    function getUserTree(){
        $.ajax({
            type:"post",
            url:"/orgTreeDemo/userTree",
            data:{
                treeType:"3"
            },
            async:true,
            cache:false,
            dataType:"json",
            success:function(result){
                // var arrays = [];
                // for(var i=0; i<result.length; i++){
                //     var arr = {
                //         id   :   result[i].ID,
                //         parent : result[i].PARENT,
                //         text : result[i].TEXT,
                //         code : result[i].CODE,
                //         organId : result[i].ORGANID,
                //         struLevel : result[i].STRULEVEL,
                //         struType : result[i].STRUTYPE,
                //         struOrder : result[i].STRU_ORDER,
                //         type : result[i].TYPE
                //     }
                //     arrays.push(arr);
                // }
                treeData = result;
            }, error:function(data) {
                Hussar.error("获取联系人失败");
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


    /**
     * 加载树
     */
    function initTree(){
        var $tree = $("#folderTree");
        $tree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": "/fsFolder/getTreeDataLazy?lazy",
                    "data": function (node) {
                        return {"id": node.id ,  "type" :"2"
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
                    "icon" : hussar+"/static/resources/img/fsfile/treeFile.png",
                },
                "default" : {
                    "icon" : hussar+"/static/resources/img/fsfile/treeFile.png",
                },
                "opened" : {
                    "icon" : hussar+"/static/resources/img/fsfile/openFile.png",
                },
            },
            plugins: ['state', 'types',"themes", "html_data"],
        });
        $tree.jstree().hide_dots();

        $tree.bind("activate_node.jstree", function (obj, e) {
            // 处理代码
            // 获取当前节点
            var currentNode = e.node;
            openFolderId = currentNode.id;
            categoryId = currentNode.id;
            refreshFolder(currentNode.id);

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
                currentNode = $('#folderTree').jstree("get_node", currentNode.parent);
                paramId.push(currentNode.id);
                paramName.push(currentNode.text);
            } while (currentNode.parent!='#')
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
        })
        $.ajax({
            type: "POST",
            url: "/fsFolder/getRoot",
            contentType:"application/x-www-form-urlencoded",
            dataType:"json",
            async: false,
            success:function(result) {
                openFolderId=result.root;
                categoryId = result.root;
                getChildren(result.root,result.rootName);
            }
        });
    }
    // 删除数据
    $(".del_span").click(function () {
        $(this).parent(".name-item").remove();
    });

    $(".edit-name-list-power,.name-list-power").on("click",".del_span_power",function () {
        if($(this).attr('value')==0){
            for(var i=0;i<personIdPower.length;i++){
                if(personIdPower[i].name==$(this).prev().html()){
                    personIdPower.splice(i,1);
                }
            }
        }else {
            for(var i=0;i<groupIdPower.length;i++){
                if(groupIdPower[i].name==$(this).prev().html()){
                    groupIdPower.splice(i,1);
                }
            }
        }

        if(groupIdPower.length==0&&personIdPower.length==0){
            $('#isEditEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
            $('#isEditChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
            $("#isEditEdit").prop("checked", false);
            $("#isEditChild").prop("checked", false);
        }
        form.render();
        $(this).parent(".name-item").remove();
    });
});
function clickPath(id) {
    while(pathId.indexOf(id)+1!=pathId.length){
        pathId.pop();
        pathName.pop();
    }
    createPath();
    refreshFolder(id);
}
function createPath(){
    $("#path").empty();
    $("#path").css({"transform":"translateX(0)"});
    for(var i=0;i<pathId.length;i++){
        if(i==pathId.length-1){
            var param= '<span>'+pathName[i]+'</span>'
        }else {
            var param= '<span><a style="cursor: pointer; color: #26B7B1;" onclick="clickPath(\''+pathId[i]+'\')">'+pathName[i]+'</a>'+'  /  </span> '        }
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
                    var subLength_1 = - ($("#path").css("transform").replace(/[^0-9\-,]/g,'').split(',')[4]);
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
function drawFolder(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param
        }
        var getTpl = $("#demo").html()
            ,view = document.getElementById('view');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
        });
    });

}

function getChildren(id,name){
    pathId.push(id);
    pathName.push(name);
    createPath();
    refreshFolder(id);
}
function addOper(parent,node) {
    $("#folderTree").jstree("deselect_all",true);
    var ref = $('#folderTree').jstree(true);
    ref.open_node(node);
    var id = ref.get_node(node+'_anchor');
    if(id){
        ref.select_node(id);
    }else{
        ref.select_node(node.substr(0,node.length-2));
    }
};
function refreshFolder(id,num,size,nameFlag){
    layui.use(['laypage','layer'], function(){
        var laypage = layui.laypage,
            layer = layui.layer;
        var orderType =$("input[name='sortType']:checked").val(); //排序类型
        var name = $('#searchName').val();
        if(nameFlag!=""&&nameFlag!=undefined&&nameFlag!=null){
            var pattern = new RegExp("^[^/\\\\:\\*\\?\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if(!pattern.test(name)){
                layer.msg("输入的目录名称不合法", {anim:6,icon: 0});
                return;
            }
        }
        addOper(openFolderId,id);
        $.ajax({
            type:"post",
            url:"/fsFolder/getChildren",
            data:{
                id: id,
                pageNumber:num,
                pageSize:size,
                order:"0",
                type:"2",
                name:name,
                nameFlag:nameFlag,
            },
            async:true,
            cache:false,
            dataType:"json",
            success:function(data){
                laypage.render({
                    elem: 'laypageAre'
                    ,count: data.total //数据总数，从服务端得到
                    ,limit: 300
                    ,layout: ['prev', 'page', 'next']
                    ,curr: num || 1 //当前页
                    ,jump: function(obj, first){
                        //obj包含了当前分页的所有参数，比如：
                        // obj.curr得到当前页，以便向服务端请求对应页的数据。
                        // obj.limit得到每页显示的条数
                        //首次不执行
                        if(!first){
                            refreshFolder(id,obj.curr,obj.limit)
                        }
                    }
                });
                noChildPower=data.noChildPower;
                drawFolder(data.rows);
                openFolderId = id;
                categoryId = id;
                userId=data.userId;
                adminFlag=data.isAdmin;
                emptyChoose();
                dbclickover=true;
                $(".file-container-flatten").height($(".background").height() - $(".toolBar").outerHeight(true) - $("#laypageAre").outerHeight(true) - 10);
            }
        });
    });
}
function refreshTree(){
    var $tree = $("#folderTree");
    $tree.jstree(true).refresh();
}
function dbclick(id,name){
    if(dbclickover==true){
        dbclickover=false;
        pathId.push(id);
        pathName.push(name);
        createPath();
        refreshFolder(id);
    }
}


function clickOneTime(e,id,name,index){
    var jq=$(e);
    if(key==1){
        if(chooseFolder.indexOf(id)!=-1){
            jq.removeClass("active");
            chooseFolder=chooseFolder.del(chooseFolder.indexOf(id));
            chooseFolderName=chooseFolderName.del(chooseFolder.indexOf(id));
        }else {
            jq.addClass("active");
            chooseFolder.push(id);
            chooseFolderName.push(name);
        }
    }else{
        $('.file').removeClass("active");
        emptyChoose();
        jq.addClass("active");
        chooseFolder.push(id);
        chooseFolderName.push(name);
    }
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
    chooseFolder = [];
    chooseFolderName = [];
}

$(document).click(function(e){
    if($(e.target)[0]==$('.file-container-flatten')[0]||$(e.target)[0]==$('.content')[0]){
        $('.file').removeClass("active");
        emptyChoose();
    }
});
function createAuthority(group,person){
    $(".name-list").empty();
    for(var i=0;i<group.length;i++){
        var param= '<div class="name-item qz">'+
            '<p>'+group[i].name+'</p>'+
            '<span class="del_span" value="1" ></span>'+
            '</div>'
        $(".name-list").append(param);
    }
    for(var i=0;i<person.length;i++){
        var param= '<div class="name-item people">'+
            '<p>'+person[i].name+'</p>'+
            '<span class="del_span" value="0"></span>'+
            '</div>'
        $(".name-list").append(param);
    }
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
}
function createAuthorityPower(group,person){
    layui.use(['form'], function() {
        var form = layui.form;
        $(".edit-name-list-power").empty();
        if(group.length!=0||person.length!=0){
            $('#isEditEdit').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled',"false");
            $('#isEditChild').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled',"false");
        }else {
            $('#isEditEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
            $('#isEditChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled',"true");
            $("#isEditEdit").prop("checked", false);
            $("#isEditChild").prop("checked", false);
        }
        for(var i=0;i<group.length;i++){
            var param= '<div class="name-item qz">'+
                '<p>'+group[i].name+'</p>'+
                '<span class="del_span_power" value="1"></span>'+
                '</div>'
                $(".edit-name-list-power").append(param);
        }
        for(var i=0;i<person.length;i++){
            var param= '<div class="name-item people">'+
                '<p>'+person[i].name+'</p>'+
                '<span class="del_span_power" value="0"></span>'+
                '</div>'

                $(".edit-name-list-power").append(param);
        }

        form.render();
    });
}
