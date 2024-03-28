$(function () {
    if(!($(".img-div").hasClass("imgs"))){
        $(".img-div").addClass("imgs")
    }
    $(".laydata-input").on("focus",function () {
        $(this).blur()
    })
});

var docId;
var zTree_Menu = null;//ztree对象
var util;//工具
var layerView;
var topicId =$("#id").val();
var topicPic;
var count;
var treeData;
var bannerHrefName;
layui.use(['form', 'jquery','util','layer','table','Hussar','jstree','HussarAjax','element','laydate','upload'], function() {
    var form = layui.form,
        $ = layui.jquery,
        table = layui.table,
        layer = layui.layer,
        Hussar = layui.Hussar,
        jstree=layui.jstree,
        $ax=layui.HussarAjax,
        element = layui.element,
        upload = layui.upload,
        laydate = layui.laydate,
        util = layui.util;
    start();
    //日期

    //普通图片上传
    var uploadInst = upload.render({
        elem: '#choosePic2'
        ,url: Hussar.ctxPath+'/banner/upload'
        ,before: function(obj){
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                $('#pic').attr('src', result);
                if(!($(".img-div").hasClass("imgs"))){
                    $(".img-div").addClass("imgs")
                }
                $("#saveBtn").prop("disabled",true).addClass('layui-btn-disabled');
            });
        }
        ,done: function(res, index, upload){
            //如果上传失败
            if(res.fName ==""||res.fName==undefined){
                topicPic="";
                return layer.msg('上传失败');
            } else {
                topicPic = res.fName;
                $("#saveBtn").prop("disabled",false).removeClass('layui-btn-disabled');
            }
        }
        ,accept: 'images'//允许上传的文件类型
    });
    //.存储当前页数据集
    var pageData = [];
    //.存储已选择数据集，用普通变量存储也行
    //layui.data('checked', null);
    layui.data("childChecked",null);
    //初始化表格
    var tableIns = table.render({
        elem: '#docList' //指定原始表格元素选择器（推荐id选择器）
        ,height: 300 //容器高度
        ,width:900
        ,url: Hussar.ctxPath+'/topicDoc/getDocListByIds' //数据接口
        ,done:function(res) {
            $("[data-field='id']").hide();
            count = res.count;
            //.假设你的表格指定的 id="docList"，找到框架渲染的表格
            var tbl = $('#docList').next('.layui-table-view');
            //.记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
            pageData = res.data;
            var len = pageData.length;
            //.遍历当前页数据，对比已选中项中的 id
            for (var i = 0; i < len; i++) {
                if (layui.data('childChecked', pageData[i]['id'])) {
                    //.选中它，目前版本没有任何与数据或表格 id 相关的标识，不太好搞，土办法选择它吧
                    tbl.find('table>tbody>tr').eq(i).find('td').eq(0).find('input[type=checkbox]').prop('checked', true);
                }
            }
            form.render('checkbox');
        }
        ,where: {
            topicId:topicId
        }
        ,page: false //开启分页
        ,cols: [[
            {field:'id'},
            {field:'',type:'checkbox',width:'9%',align:"center" },
            {field:'title',title:'文件名称',width:'47%',align:"left",event: 'openView', style: 'cursor: pointer;color:#00a4ff'},
            {field:'author',title:'作者',width:'15%',align:"center"},
            {field:'createTime',title:'上传时间',width:'18%',align:"center",templet:"#timeTpl"},
            {field: 'option', title: '操作', align: "center", toolbar: '#delDoc',width:'10%'}
        ]] //设置表头
    });
    //.监听选择，记录已选择项
    table.on('checkbox(docList)', function(obj) {
        //.全选或单选数据集不一样
        var data = obj.type == 'one' ? [obj.data] : pageData;
        //.遍历数据
        $.each(data, function(k, v) {
            //.假设你数据中 id 是唯一关键字
            if (obj.checked) {
                //.增加已选中项
                layui.data('childChecked', {
                    key: v.id, value: v
                });
            } else {
                //.删除
                layui.data('childChecked', {
                    key: v.id, remove: true
                });
            }
        });
    });
    //监听工具条
    table.on('tool(docList)', function (obj) {
        var data = obj.data;
        if (obj.event == 'delete') {
            var dataArr = parent.active.getCheckData();
            var docTopicId = data.docTopicId;
            if (docTopicId == null || docTopicId == "" || docTopicId == undefined){
                layer.confirm('确定要删除所选中的文件吗？', function(index){
                    obj.del();
                    layer.close(index);
                });
            }else {
                layer.confirm('确定要删除所选中的文件吗？',function(){
                    $.ajax({
                        type:"post",
                        url: Hussar.ctxPath+"/topic/delDoc",
                        data:{
                            id:docTopicId
                        },
                        async:false,
                        cache:false,
                        success:function(data){
                            if(data == dataArr.length){
                                layer.alert('删除成功', {
                                    icon :  1,
                                    shadeClose: true,
                                    skin: 'layui-layer-molv',
                                    shift: 5,
                                    area: ['300px', '180px'],
                                    title: '提示',
                                    end: function () {
                                        tableIns.reload({
                                            where: {
                                                //防止IE浏览器第一次请求后从缓存读取数据
                                                timestamp: (new Date()).valueOf()
                                            }
                                        });
                                        var index = layer.alert();
                                        layer.close(index);
                                    }
                                },function(){
                                    tableIns.reload({
                                        where: {
                                            //防止IE浏览器第一次请求后从缓存读取数据
                                            timestamp: (new Date()).valueOf()
                                        }
                                    });
                                    var index = layer.alert();
                                    layer.close(index);
                                });
                            }else{
                                layer.alert('删除失败', {
                                    icon :  2,
                                    shadeClose: true,
                                    skin: 'layui-layer-molv',
                                    shift: 5,
                                    area: ['300px', '180px'],
                                    title: '提示'
                                });
                            }
                        }
                    })
                })
            }
        }
        if(obj.event == 'openView'){
            openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + data.id);
        }
    });

    form.on('switch(topicShow)', function(data){
        if(this.checked==true){
            $("#startTime").val(getNowFormatDate);

        }else{
            $("#startTime").val("");
        }

    });
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    })
    /*新增/编辑专题*/

    $(document).on("click","#changeDoc",function(){
        layer.open({
            type: 2,
            area: [ '600px',  '400px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: true,
            moveOut: true,
            shade: 0.4,
            title: "选择文档",
            content: Hussar.ctxPath+"/banner/openDoc"
        });
    });

    form.on('select(adFilter)', function(data){
        var val = data.value;
        if (val === "0") { //文档
            $("#transformDiv").empty();
            $("#transformDiv").css({"width":"400px"});
            var html = "<input type=\"text\" id=\"bannerHref\" name=\"bannerHref\"   autocomplete=\"off\" class=\"layui-input\" style=\"width: 70%\" disabled>\n" +
                "                    <button  class=\"layui-btn\" id=\"changeDoc\" style=\"display: inline-block;position: absolute;right: 50px;top: 0px;\" > 请选择</button>"

            $("#transformDiv").html(html);
            form.render();
        } else if (val === "1") { //专题
            $("#transformDiv").empty();
            $("#transformDiv").css({"width":"280px"});
            var optionContent = "<select id=\"bannerHref\" class=\"layui-input\">";
            var ajax = new $ax(Hussar.ctxPath + "/topicDoc/searchTopic", function(data) {
                var arr = data.data;
                for(var i = 0;i < arr.length;i++){
                    optionContent += "<option value='"+arr[i].topicId+"'>"+arr[i].topicName+"</option>";
                }

            }, function(data) {
            });
            ajax.start();
            optionContent += "</select>";
            $("#transformDiv").html(optionContent);
            form.render();
        } else if (val === "2") { //自定义链接
            $("#transformDiv").empty();
            $("#transformDiv").css({"width":"400px"});
            var html = "<input type=\"text\" id=\"bannerHref\" name=\"bannerHref\"    autocomplete=\"off\" class=\"layui-input\"  style=\"width: 70%\">"
            docId = "无";
            $("#transformDiv").html(html);
            form.render();
        }
    });

    function encode(str){
// 对字符串进行编码
        var encode = encodeURI(str);
// 对编码的字符串转化base64
        var base64 = btoa(encode);
        return base64;
    }
    $("#saveBtn").on('click',function(){
        var advertClassfiy = $("#advertClassfiy").val();
        var bannerName = $("#bannerName").val().trim();//专题名称
        var bannerHref = $("#bannerHref").val().trim();//专题地址
        // 如果是专题，拼接链接
        if (advertClassfiy === "1") {
            docId = bannerHref;
            bannerHref = "/frontTopic/topicDetail?topicId="+bannerHref+"&page=1&size=10";
        }

        // if(topicPic == ""||topicPic == undefined || topicPic == null){
        //     layer.msg("banner图不能为空", {anim:6,icon: 0});
        //     return;
        // }

        if(bannerName == ""){
            layer.msg("banner名称不能为空", {anim:6,icon: 0});
            return;
        }
        if(bannerName.length > 8){
            layer.msg("banner名称不能超过8个字", {anim:6,icon: 0});
            return;
        }
        if(!new RegExp("^[^/\\\\:\\*\\?\\<\\>\\|\"]{1,255}$").test(bannerName)){
            layer.msg("banner名称不能有特殊字符", {anim:6,icon: 0});
            return
        }
        if(advertClassfiy == ""||advertClassfiy == undefined || advertClassfiy == null){
            layer.msg("广告分类不能为空", {anim:6,icon: 0});
            return;
        }
        var docIds = '';//关联的文档ID拼接字符串
        $("tbody").children('tr').each(function(i){
            var a = $(this).children();//获取每一行
            if(i == 0){
                docIds += a[0].innerText;
            }else{
                docIds += ","+a[0].innerText;
            }
        });
        var url;//请求地址
        var successMsg,errorMsg;//成功失败提示
        url = "/banner/editBanner";
        successMsg = "修改成功";
        errorMsg = "修改失败";
        if(docId==""||docId == undefined){
            docId = $("#docId").val();
        }

        $.ajax({
            type:"post",
            url: Hussar.ctxPath+url,
            data:{
                bannerId:topicId,
                bannerName:bannerName,
                docId:docId,
                bannerHref:encode(bannerHref),
                bannerPath:topicPic,
                classify: advertClassfiy
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                if(data.result == "0"){
                    layer.alert('该banner已存在', {
                        icon :  0,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    });
                }else if(data.result == "1"){
                    layer.alert(successMsg, {
                        icon :  1,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示',
                        end: function () {
                            parent.tableIns.reload({
                                where: {
                                    //防止IE浏览器第一次请求后从缓存读取数据
                                    timestamp: (new Date()).valueOf()
                                }
                            });
                            layui.data('checked',null);
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                        }
                    },function(){
                        parent.tableIns.reload({
                            where: {
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            }
                        });
                        layui.data('checked',null);
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                    });
                }else{
                    layer.alert(errorMsg, {
                        icon :  2,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    });
                }
            }
        })
    });
    var active = {
        addDoc:function(){
            var nodes = zTree_Menu.getCheckedNodes(true);
            var idArr = '';
            for(var i = 0;i < nodes.length;i++){
                if(nodes[i].code == undefined){
                    if(idArr == ''){
                        idArr += nodes[i].id
                    }else{
                        idArr += (","+nodes[i].id)
                    }
                }
            }
            tableIns.reload({
                page:{
                    curr:1
                },
                where:{
                    idArr:idArr
                }
            })
        }
        ,moveup:function(){
            var dataArr = active.getCheckData();
            if(dataArr.length != 1){
                layer.alert('请先选择一条要上移的文件', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            var index = dataArr[0].LAY_TABLE_INDEX;
            var $tr = $("tr[data-index = "+index+"]");
            if ($tr.index() != 0) {
                $tr.fadeOut().fadeIn();
                $tr.prev().before($tr);
            }else{
                layer.alert('已经上移到最顶端', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
            }
        }
        ,movedown:function(){
            var dataArr = active.getCheckData();
            if(dataArr.length != 1){
                layer.alert('请先选择一条要下移的文件', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            var index = dataArr[0].LAY_TABLE_INDEX;
            var $tr = $("tr[data-index = "+index+"]");
            var next = $tr.next();
            if(next.length == 0){
                layer.alert('已经下移到最底端', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            if(next){
                $tr.fadeOut().fadeIn();
                $tr.next().after($tr);
            }
        }
        ,del:function(){
            var dataArr = active.getCheckData();
            if(dataArr.length == 0){
                layer.alert('请先选择要删除的数据', {
                    icon :  0,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
                return;
            }
            var delIds = '';//删除的ID
            for(var i = 0;i<dataArr.length;i++){
                layui.data('childChecked', {
                    key: dataArr[i].id, remove: true
                });
                var index = dataArr[i].LAY_TABLE_INDEX;
                var $tr = $("tr[data-index = "+index+"]");
                $tr.remove();
                if(i == 0){
                    delIds += dataArr[i].id;
                }else{
                    delIds += ","+dataArr[i].id;
                }
            }
            $.ajax({
                type:"post",
                url: Hussar.ctxPath+"/topic/delDocById",
                data:{
                    delIds:delIds,
                    topicId:topicId
                },
                cache:false,
                success:function(data){
                    if(data != dataArr.length){
                        layer.alert('删除失败', {
                            icon :  0,
                            shadeClose: true,
                            skin: 'layui-layer-molv',
                            shift: 5,
                            area: ['300px', '180px'],
                            title: '提示'
                        });
                    }
                }
            })
        }
        ,getCheckData: function(){ //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            $.each(layui.data('childChecked'), function(k, v) {
                mySelected.push(v);
            });
            return mySelected;
        }
    };
    //.渲染完成回调
    $('.layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    function initEmployeeTree(data,authName){
        var $tree = $("#showEmployeeTree");
        if($tree){
            $tree.jstree("destroy");
        }
        $tree.jstree({
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
                "1":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/com.png"},
                "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/station.png"},
                "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/empl.png"},
                "USER":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/user.png"}
            },
            search:treeSearch("showEmployeeTree","personSearch",authName)
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            if(e.node.original.struType != 9){
                layer.msg("请选择人员")
                return;
            }else{
                $("#authorName").val(e.node.original.text);
                $("#authorId").val(e.node.original.id);
                layer.close(layerView);
            }
        })
    }

    function start() {
        var classify = $("#classify").val();
        $("#advertClassfiy").val(classify);
        form.render();
        if (classify === "1") {
            $("#transformDiv").css({"width":"280px"});
            // 专题id
            var topicId = $("#docId").val();
            var topicName = "";
            // 如果是专题，则加载数据
            var ajax = new $ax(Hussar.ctxPath + "/topicDoc/searchTopic", function(data) {
                var optionContent = "";
                var arr = data.data;
                for(var i = 0;i < arr.length;i++){
                    optionContent += "<option value='"+arr[i].topicId+"'>"+arr[i].topicName+"</option>";
                    if (arr[i].topicId == topicId) {
                        topicName = arr[i].topicName;
                    }
                }
                $("#bannerHref").html(optionContent);
                $("#bannerHref").val(topicId);
                form.render();
            }, function(data) {
            });
            ajax.start();
        }

        getUsers();
    }

});

function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
/**
 * 所有树的模糊查询
 */
function treeSearch(treeId,searchId,authName) {
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
                v = "";
            }
            var temp = $tree.is(":hidden");
            if (temp == true) {
                $tree.show();
            }
            //$tree.jstree(true).search(v);
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
    if(authName!=null&&authName!=""){
        $("#"+searchId).val(authName);
        var e = $.Event("keyup");//模拟一个键盘事件
        e.keyCode = 13;//keyCode=13是回车
        $("#"+searchId).trigger(e);//模拟页码框按下回车
    }
}

/**
 * 日期格式转换
 */
function time2date(t){
    return util.toDateString(t);
}
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var min = date.getMinutes();
    var sec =date.getSeconds();
    if (min >= 1 && min <= 9) {
        min = "0" + min;
    }
    if (sec >= 1 && sec <= 9) {
        sec = "0" + sec;
    }
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
        + " " + date.getHours() + seperator2 + min
        + seperator2 + sec;
    return currentdate;
}

init();
function init() {
    var show =$("#show").val();
    if(show== 0){
        $("#topicShow").attr("checked",false);
    }else{
        $("#topicShow").attr("checked",true);
    }
}

function getUsers(){
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajax({
            type:"post",
            url: Hussar.ctxPath+"/orgTreeDemo/employeeTree",
            data:{
                treeType:"2"
            },
            async:true,
            cache:false,
            dataType:"json",
            success:function(result){
                // var arrays = [];
                // for(var i=0; i<result.length; i++){
                //     var arr = {
                //         id	:	result[i].ID,
                //         code:   result[i].CODE,
                //         text : result[i].TEXT,
                //         parent : result[i].PARENT,
                //         struLevel:result[i].STRULEVEL,
                //         struOrder:result[i].STRUORDER,
                //         struType:result[i].STRUTYPE,
                //         isLeaf:result[i].ISLEAF,
                //         type:result[i].TYPE,
                //         isEmployee:result[i].ISEMPLOYEE
                //     }
                //     arrays.push(arr);
                // }
                treeData = result;
            }, error:function(data) {
                Hussar.error("获取联系人失败");
            }
        });
    });
}

function updateDoc() {
    if(docId!=null&&docId!=undefined){
        $("#bannerHref").val("/preview/toShowPDF?id="+docId)}
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajax({
            type:"post",
            url: Hussar.ctxPath+"/banner/bannerName",
            data:{
                docId:docId
            },
            async:false,
            cache:false,
            dataType:"text",
            success:function(str){
                bannerHrefName=str;

            }
        });
    });



    var topicShow = 0;

    var str = bannerHrefName; //要截取的字符串
    var index = str.indexOf(".");
    var result = str.substr(index ,str.length);
    if(result==".png"||result==".jpg"||result==".gif"||result==".bmp"||result==".jpeg"){

        $("#bannerHref").val("/preview/toShowIMG?id="+docId)
    }else if(result==".mp4"||result==".wmv"){

        $("#bannerHref").val("/preview/toShowVideo?id="+docId)
    } else if(result==".mp3"||result==".m4a"){

        $("#bannerHref").val("/preview/toShowVoice?id="+docId)
    }
    else if(isPDFShow(result)){
        $("#bannerHref").val("/preview/toShowPDF?id="+docId)
    }
    else {
        $("#bannerHref").val("/preview/toShowOthers?id="+docId)
        // layer.msg("此banner地址不支持。",{anim:6,icon: 0,});
        // $("#bannerHref").val("");
        // docId = "";
    }
}
function isPDFShow(result){
    return result == ".pdf"
        || result == ".doc" || result == ".docx" || result == ".dot"
        || result == ".wps" || result == ".wpt"
        || result == ".xls" || result == ".xlsx" || result == ".xlt"
        || result == ".et" || result == ".ett"
        || result == ".ppt" || result == ".pptx" || result == ".ppts"
        || result == ".pot" || result == ".dps" || result == ".dpt"
        || result == ".txt"
        || result == ".pps"
        || result == ".rtf"
        || result == ".tif"
        || result == ".ceb";
}