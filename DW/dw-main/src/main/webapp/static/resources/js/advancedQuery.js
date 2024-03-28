/**
 * Created by Lenovo on 2018/3/20.
 */

(function() {
     var categoryList;//分类列表
    var categoryCodeId; //高级搜索页面的传值标签
    layui.config({
        base: '${ctxPath}/static/resources/weadmin/static/js/'
        , version: '101100'
    }).use('admin');
    layui.use(['jquery', 'admin', 'element'], function () {
        var $ = layui.$, element = layui.element, admin = layui.admin;
    })
    $(document).ready(function() {
        var sortOrder =  $("#sortOrder").val(); //排序类型
        if(sortOrder==""){
            $("input[name='sortType']").eq(0).attr("checked","checked");
        }else{
            $("input[name='sortType']").eq(sortOrder).attr("checked","checked");
        }
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");
        var docType = $("#docType").val(); //文档类型
        $("input[name='fileType']").eq(docType).attr("checked","checked");
        var fileName =  $("#fileName").val();//关键词搜索
        var fileType =   $("input[name='fileType']:checked").val(); //文档类型

        categoryCodeId  = $('#categoryCode').val(); //分类编码
        $.ajax({
            type:"post",
            url:"/fileManage/categoryList",
            async:false,
            cache:false,
            success:function(data){
                categoryList = data;
                for(var i=0;i<categoryList.length;i++){
                    if(categoryList[i].level==0&&categoryList[i].pid=="root"){
                        var html ="<label class='categoryList' data-code='"+categoryList[i].code+"'>全部</label>";
                        $("#docCategory").append(html);
                    }else if(categoryList[i].level==1||categoryList[i].level==0){
                        var html ="<label class='categoryList' data-code='"+categoryList[i].code+"'>"+categoryList[i].name+"</label>";
                        $("#docCategory").append(html);
                    }
                }
                /*高级搜索页面的传过来的选中的分类标签放置文件分类内*/
                if(categoryCodeId!=null) {
                    var categoryArray = categoryCodeId.split(",");
                    for (var i = 0; i < categoryList.length; i++) {
                        for (var d = 0; d < categoryArray.length; d++) {
                            if (categoryArray[d] == categoryList[i].code && categoryList[i].code=="A") {
                                var html = "<span class='choseCategory' data-code='" + categoryArray[d] + "' >全部<button class='cancleLogo'></button></span>";
                                $("#selectedCate").append(html);
                            }else if (categoryArray[d] == categoryList[i].code && categoryList[i].code!="A") {
                                var html = "<span class='choseCategory' data-code='" + categoryArray[d] + "' >"+categoryList[i].name+"<button class='cancleLogo'></button></span>";
                                $("#selectedCate").append(html);
                            }
                        }
                        if(categoryList[i].code!="A") {
                            var seconedlist = categoryList[i].children;
                            if (seconedlist != null) {
                                for (var j = 0; j < seconedlist.length; j++) {
                                    for (var c = 0; c < categoryArray.length; c++) {
                                        if (categoryArray[c] == seconedlist[j].code) {
                                            var html = "<span class='choseCategory' data-code='" + categoryArray[c] + "' >" + seconedlist[j].name + "<button class='cancleLogo'></button></span>";
                                            $("#selectedCate").append(html);
                                        }
                                    }
                                    var threelist = seconedlist[j].children;
                                    if (threelist != null) {
                                        for (var k = 0; k < threelist.length; k++) {
                                            for (var d = 0; d < categoryArray.length; d++) {
                                                if (categoryArray[d] == threelist[k].code) {
                                                    var html = "<span class='choseCategory' data-code='" + categoryArray[d] + "' >" + threelist[k].name + "<button class='cancleLogo'></button></span>";
                                                    $("#selectedCate").append(html);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //删除
                    $(".cancleLogo").click(function(){
                        $(this).parent().remove();
                        var  code = $(this).parent()[0].dataset.code;
                        $('#'+code+'').removeClass("changColor");
                    });
                }
            },
            error:function(){
            }
        })
        gridView.initPage();
    });

    var count="";
    var gridView ={
        /*初始化页面*/
        initPage: function() {
            var that = this;
            //初始化表格
            that.initView(1);
            that.createPageContext();
            that.initButtonEvent();
        },
        //翻页后初始化页面
        aftTurnInit :function(spageno){
            var that = this;
            //初始化页面
            that.initView(spageno);
        },
        initButtonEvent:function(){
            var that=this;
            //排序事件
            $("#orderType li").click(function () {
                $("input[name='sortType']").parent().removeClass("sortType-checked");
                $("input[name='sortType']:checked").parent().addClass("sortType-checked");
                var fileName =  $("#fileName").val();
                var fileType =   $("input[name='fileType']:checked").val();
                var orderType =   $("input[name='sortType']:checked").val(); //排序类型
                categoryCodeId="";
                if(fileName!=""){
                    var category = $('#selectedCate')[0].children;
                    var code = "";
                    if(category!=""||category!="undefind"||category!=null) {
                        for (var i = 0; i < category.length; i++) {
                            if (i == category.length - 1) {
                                code += category[i].dataset.code;
                            } else {
                                code += category[i].dataset.code + ","
                            }
                        }
                    }
                    window.location.href = "/index/toAdancedQuery?keyWords=" + fileName + "&fileType=" + fileType + "&code=" + code + "&orderType="+orderType+"&page=" + 1;
                }else{
                    alert("请输入关键词，多个关键词以空格隔开");
                }
            })
            //搜索按钮点击事件
            $("#queryBtn").click(function () {
                var fileName =  $("#fileName").val();
                var fileType =   $("input[name='fileType']:checked").val();
                var orderType =   $("input[name='sortType']:checked").val(); //排序类型
                categoryCodeId="";
                if(fileName!=""){
                    var category = $('#selectedCate')[0].children;
                    var code = "";
                    if(category!=""||category!="undefind"||category!=null) {
                        for (var i = 0; i < category.length; i++) {
                            if (i == category.length - 1) {
                                code += category[i].dataset.code;
                            } else {
                                code += category[i].dataset.code + ","
                            }
                        }
                    }
                    window.location.href = "/index/toAdancedQuery?keyWords=" + fileName + "&fileType=" + fileType + "&code=" + code + "&orderType="+orderType+"&page=" + 1;
                }else{
                    alert("请输入关键词，多个关键词以空格隔开");
                }
            });
            //登录按钮点击事件
            $('#loginButton').click(function () {
                loginSubmit();
            });

            // 关键词搜索框添加绑定回车函数
            $('#fileName').bind('keypress', function(event) {
                if (event.keyCode == "13") {
                    $("#queryBtn").click();
                }
            });
            //点击标签事件
            /*点击三级分类时，文字变色，选中类型增加值*/
            $(document).on('click','.categoryDetail label',function(){
                /*文字变色*/
                $(this).addClass("changColor");
                /*选中类型增加值*/
                var content_txt = $(this).html();
                var content_code = $(this)[0].id;
                var category = $('#selectedCate')[0].children;
                var flag="1";
                if(category!=""&&category!="undefind"&&category!=null&&category.length>0) {
                    for (var i = 0; i < category.length; i++) {
                        if (category[i].dataset.code == content_code) {
                            flag = "0";
                        }
                    }
                    if (flag == "1") {
                        var html = "<span class='choseCategory' data-code='" + content_code + "' >" + content_txt + "<button class='cancleLogo'></button></span>";
                        $("#selectedCate").append(html);
                    }
                }else{
                    var html = "<span class='choseCategory' data-code='" + content_code + "' >" + content_txt + "<button class='cancleLogo'></button></span>";
                    $("#selectedCate").append(html);
                }
                //删除
                $(".cancleLogo").click(function(){
                    $(this).parent().remove();
                    var  code = $(this).parent()[0].dataset.code;
                    $('#'+code+'').removeClass("changColor");
                });

            });
            /*点击二级分类时，文字变色，选中类型增加值*/
            $(document).on('click','.rlzyStyle label',function(){
                /*文字变色*/
                $(this).addClass("changColor");
                /*选中类型增加值*/
                var content_txt = $(this).html();
                var content_code = $(this)[0].id;
                var category = $('#selectedCate')[0].children;
                var content_value = $(this)[0].dataset.value;
                var flag="1";
                //选中类型中是否已包含这个值，未包含则增加，已包含则不能增加（不能重复增加值）
                if(category!=""&&category!="undefind"&&category!=null&&category.length>0) {
                    for (var i = 0; i < category.length; i++) {
                        if (category[i].dataset.code == content_code) {
                            flag = "0";
                        }
                    }
                    if (flag == "1") {
                        if(content_txt=="全部") {//点击的目录是二级目录的“全部”时
                            var html = "<span class='choseCategory'  data-code='" + content_code + "'>" + content_value + "<button class='cancleLogo'></button></span>";
                            $("#selectedCate").append(html);
                        }else{
                            var html = "<span class='choseCategory'  data-code='" + content_code + "'>" + content_txt + "<button class='cancleLogo'></button></span>";
                            $("#selectedCate").append(html);
                        }
                    }
                }else{
                    if(content_txt=="全部") {//点击的目录是二级目录的“全部”时
                        var html = "<span class='choseCategory' data-code='" + content_code + "' >" + content_value + "<button class='cancleLogo'></button></span>";
                        $("#selectedCate").append(html);
                    }else{
                        var html = "<span class='choseCategory' data-code='" + content_code + "' >" + content_txt + "<button class='cancleLogo'></button></span>";
                        $("#selectedCate").append(html);
                    }
                }
                //删除
                $(".cancleLogo").click(function(){
                    $(this).parent().remove();
                    var  code = $(this).parent()[0].dataset.code;
                    $('#'+code+'').removeClass("changColor");
                });

            });
            /*点击一级分类时，文字变色，选中类型增加值*/
            $(".docCategory .categoryList").click(function(){
                $(".docCategory .categoryList").removeClass("changDeltaImg");
                $(this).addClass("changDeltaImg");
                var categoryCode = $(this)[0].dataset.code;
                $("#cateDiv").css("display","block");
                $("#cateDiv")[0].innerHTML="";
                for(var i=0;i<categoryList.length;i++){ //展开二级及三级菜单
                    if(categoryList[i].code==categoryCode&&categoryList[i].pid!="root") {
                        var seconedlist = categoryList[i].children;
                        var html =" <div class='allStyle' ><div class='rlzyStyle'><label class='removeBackground' id='" + categoryList[i].code + "' data-value='"+categoryList[i].name+"'>全部</label></div><div class='categoryDetail'></div></div>";
                        $("#cateDiv").append(html);
                        if(seconedlist!=null){
                            for (var j = 0; j < seconedlist.length; j++) {
                                var html ="";
                                html += "  <div class='allStyle' ><div class='rlzyStyle'><label id='" + seconedlist[j].code + "'' >" + seconedlist[j].name + "</label></div><div class='categoryDetail' >";
                                var threelist = seconedlist[j].children;
                                if(threelist!=null){
                                    for (var k = 0; k < threelist.length; k++) {
                                        html += "<label id='"+threelist[k].code+"'>"+threelist[k].name+"</label>";
                                    }
                                }
                                html +="</div></div>";
                                $("#cateDiv").append(html);
                            }
                        }
                    }else  if(categoryCode=="A"&&categoryList[i].pid=="root"){
                        var html="";
                        html ="<span class='choseCategory' data-code='"+categoryList[i].code+"'>全部<button class='cancleLogo'></button></span>";
                        $("#selectedCate").append(html);
                    }
                    //删除
                    $(".cancleLogo").click(function(){
                        $(this).parent().remove();
                        var  code = $(this).parent()[0].dataset.code;
                        $('#'+code+'').removeClass("changColor");
                    });
                }
                //
                if(categoryCodeId!=null) {
                    var categoryArray = categoryCodeId.split(",");
                    for (var c = 0; c < categoryArray.length; c++) {
                        $('#'+categoryArray[c]+'').addClass("changColor");

                    }
                }
                //
            });
        },
        initView:function(page){
            fileName =  encodeURI($("#fileName").val());
            fileType =   encodeURI($("input[name='fileType']:checked").val());
            orderType =   $("input[name='sortType']:checked").val(); //排序类型
            var category = $('#selectedCate')[0].children;
            var code = "";
            if(category!=""||category!="undefind"||category!=null) {
                for (var i = 0; i < category.length; i++) {
                    if (i == category.length - 1) {
                        code += category[i].dataset.code;
                    } else {
                        code += category[i].dataset.code + ","
                    }
                }
            }else{
                code = "A";
            }
            $.ajax({
                async: false,
                type: "post",
                url: "/queryFile/search",
                dataType: 'json',
                data: {keyword: fileName, fileType: fileType, page:page,categoryCode:code,orderType:orderType},
                success: function (data) {
                    var json = eval(data);
                    $(".docNum span").html(json.total).show();
                    if (json.total == 0) {
                        content='<div class="tips"><h3>没有符合您要求的文章!</h3></div>';
                        $(".docNum").hide();
                        $("#middleDiv").html(content);
                        $("#footDiv").hide();
                    } else {
                        count = json.total;
                        var list = json.items;
                        var num = list.length;
                        if (num > 0) {
                            var content = "";
                            for (var i = 0; i < num; i++) {
                                var fileSuffixName = list[i].fileSuffixName;
                                if (num > 0) {
                                    content += "<div class='docList' id='docList'><div class='docItem'><a href='javascript:void(0)' target='_blank' onclick=showPdf('" + list[i].id + "','" + list[i].allowPreview + "','"+list[i].fileSuffixName+"')>";
                                    if(fileSuffixName=="xlsx"){
                                        content +="<span class='docName' style='background:url(/static/resources/img/file-excle.png)no-repeat left center;' title='" + list[i].fileName + "'>" + list[i].fileName + "</span></a>";
                                    }else if(fileSuffixName=="pdf"){
                                        content +="<span class='docName' style='background:url(/static/resources/img/pdf.png)no-repeat left center;' title='" + list[i].fileName + "'>" + list[i].fileName + "</span></a>";
                                    }else if(fileSuffixName=="ceb"){
                                        content +="<span class='docName' style='background:url(/static/resources/img/file-ceb.png) no-repeat left center;' title='" + list[i].fileName + "'>" + list[i].fileName + "</span></a>";
                                    }else if(fileSuffixName=="ppt"||fileSuffixName=="pptx"||fileSuffixName=="ppsx"){
                                        content +="<span class='docName' style='background:url(/static/resources/img/ppt.png)no-repeat left center;' title='" + list[i].fileName + "'>" + list[i].fileName + "</span></a>";
                                    }else if(fileSuffixName=="txt") {
                                        content +="<span class='docName' style='background:url(/static/resources/img/file-txt.png)no-repeat left center;' title='" + list[i].title + "'>" + list[i].fileName + "</span></a>";
                                    }else{
                                        content +="<span class='docName' style='background:url(/static/resources/img/word.png)no-repeat left center;' title='" + list[i].title + "'>" + list[i].fileName + "</span></a>";
                                    }
                                    content +="<button type='button' class='btn-sm btn-downLoad' data-code='" + list[i].title + "' onclick=downloadFile('" + list[i].id + "','" + list[i].owner + "','1','" + list[i].category + "','" + list[i].filePath + "',this)>马上下载</button></div>";
                                    content +='<div class="docFileContent">' + list[i].content + '</div><div class="docDetail"><span>上传时间:' + list[i].optTs + '</span><span>下载次数:' + list[i].downloadNum + '</span><span>预览次数:' + list[i].previewNum + '</span>';
                                    content +='<span class="last">作者：' + list[i].author + '</span>';
                                    content +="</div></div>";
                                }
                            }
                            $("#middleDiv").html(content);
                        }
                    }
                }
            })
        },
        /**
         * 分页条
         */
        createPageContext : function(){
            $('#pagelist').extendPagination({
                totalCount: count,
                showPage: 10,
                limit: 10,
                callback: function (curr, limit, totalCount) {
                    gridView.aftTurnInit(curr);
                }
            })
        }
    }
    $(function () {
        $(".logout").click(function () {
            layer.confirm('确认退出？', {icon: 3, title:'提示'}, function(index){
                window.location.href = "/logout.do";
                layer.close(index);
            });
        })
    })
})(this);

var layer=document.createElement("div");
layer.id="layer";
/*预览*/
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function showPdf(id,flag,fileSuffixName) {
    if(fileSuffixName=="png"||fileSuffixName=="jpg"||fileSuffixName=="gif"||fileSuffixName=="bmp"||fileSuffixName=="ceb"||fileSuffixName=="jpeg"){
            openWin("/preview/toShowIMG?id=" + id);
        }else if(fileSuffixName=="mp4"||fileSuffixName=="wmv"||fileSuffixName=="wmv"||fileSuffixName=="wmv"){
            openWin("/preview/toShowVideo?id=" + id);
        }else {
            openWin("/preview/toShowPDF?id=" + id);
        }
}
/*下载*/
function downloadFile(docId,ownerId,points,category,filePath,obj){//文档ID，作者ID，下载消耗积分,文档名，分类,文件路径
 var fileName = obj.dataset.code;
    $.ajax({
        type:"post",
        url:"/file/changePoints",
        async:false,
        cache:false,
        data:{
            authorId:ownerId,
            docId:docId,
            points:points
        },
        success:function(data){
            if(data == "success"){
                $.ajaxFileUpload({
                    url:"/file/fileDownload",
                    type:"post",
                    data:{
                        fileName:fileName,
                        category:category,
                        filePath:filePath
                    }
                });
            }else {
                alert("下载出错");
            }
        },
        error:function(){
            alert("下载失败");
        }
    })
}
//注销
function cancleLogin(){
    layer.confirm('确认注销？', {icon: 3, title: '提示'}, function (index) {
        window.location.href = "/logout.do";
        layer.close(index);
    });
}



