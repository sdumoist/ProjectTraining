var zTree_Menu = null;//ztree对象
var util;//工具
var layerView;
var topicId = null;//专题ID
var topicPic;
var treeData;
var docId;
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
        laydate = layui.laydate,
        upload = layui.upload,
        util = layui.util;
    //日期
    // $(function () {
    //     form.on('switch(topicShow)', function (data) {
    //
    //         if(this.checked==true){
    //             $("#startTime").val(getNowFormatDate);
    //
    //         }else{
    //             $("#startTime").val("");
    //         }
    //
    //     })
    // })
    //普通图片上传
    var uploadInst = upload.render({
        elem: '#choosePic'
        ,url: Hussar.ctxPath+'/banner/upload'
        ,before: function(obj){
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                $('#pic').attr('src', result).addClass("show_img");
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
    var uploadInst = upload.render({
        elem: '#choosePic2'
        ,url: Hussar.ctxPath+'/banner/upload'
        ,before: function(obj){
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                $('#pic').attr('src', result).addClass("show_img");
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
//        $("#startTime").val(getNowFormatDate);
    layui.data("childChecked",null);
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    /*新增/编辑专题*/
    function openDoc() {



    }
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
    function encode(str){
// 对字符串进行编码
        var encode = encodeURI(str);
// 对编码的字符串转化base64
        var base64 = btoa(encode);
        return base64;
    }
    $("#saveBtn").on('click',function(){
        var advertClassfiy = $("#advertClassfiy").val();
        if (advertClassfiy === "0") {
            updateDoc();
        }
        var bannerName = $("#bannerName").val().trim();//专题名称
        var bannerHref = $("#bannerHref").val().trim();//专题地址
        // 如果是专题，拼接链接
        if (advertClassfiy === "1") {
            docId = bannerHref;
            bannerHref = "/frontTopic/topicDetail?topicId="+bannerHref+"&page=1&size=10";
        }

        if(topicPic == ""||topicPic == undefined || topicPic == null){
            layer.msg("banner图不能为空", {anim:6,icon: 0});
            return;
        }
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
        if(docId == ""||docId == undefined || docId == null){
            layer.msg("链接不能为空", {anim:6,icon: 0});
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
        url = "/banner/addBanner";
        successMsg = "新增成功";
        errorMsg = "新增失败";

        $.ajax({
            type:"post",
            url: Hussar.ctxPath+url,
            data:{
                id:topicId,
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
            if(e.node.original.struType != 9  && e.node.original.type !='USER'){
                layer.msg("请选择人员")
                return;
            }else{
                $("#authorName").val(e.node.original.text);
                $("#authorId").val(e.node.original.id);
                layer.close(layerView);
            }
            //layer.msg("你点击了："+e.node.original.text+"&nbsp;&nbsp;节点id："+e.node.original.id)
        })
    }

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

});

$(".laydata-input").on("focus",function () {
    $(this).blur()
})
/**
 * 所有树的模糊查询
 */
function treeSearch(treeId,searchId,username){
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
    if(username!=null && username!=""){
        $("#"+searchId).val(username);
        var e = $.Event("keyup");//模拟一个键盘事件
        e.keyCode = 13;//keyCode=13是回车
        $("#"+searchId).trigger(e);//模拟页码框按下回车
    }
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
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        if(docId!=null&&docId!=undefined){
            $("#bannerHref").val("/preview/toShowPDF?id="+docId)

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
            } else if(isPDFShow(result)){
                $("#bannerHref").val("/preview/toShowPDF?id="+docId)
            }
            else {
                $("#bannerHref").val("/preview/toShowOthers?id="+docId)
            }
        };
    });
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
