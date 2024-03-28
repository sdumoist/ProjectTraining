/**
 * Created by Lenovo on 2018/3/20.
 */
var  categoryList;
(function() {
    layui.config({
        base: '${ctxPath}/static/resources/weadmin/static/js/'
        , version: '101100'
    }).use('admin');
    layui.use(['jquery', 'admin', 'element'], function () {
        var $ = layui.$, element = layui.element, admin = layui.admin;
    })
    $(document).ready(function() {
        var docType = $("#docType").val(); //文档类型
        $("input[name='fileType']").eq(docType).attr("checked","checked");
        var fileName =  $("#searchInput").val();//关键词搜索
        var fileType =   $('input:radio:checked').val(); //文档类型
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
                    } else if(categoryList[i].level==1||categoryList[i].level==0){
                        var html ="<label class='categoryList' data-code='"+categoryList[i].code+"'>"+categoryList[i].name+"</label>";
                        $("#docCategory").append(html);
                    }
                }
            },
            error:function(){

            }
        })
    });
    $(function () {
        $(".logout").click(function () {
            layer.confirm('确认退出？', {icon: 3, title:'提示'}, function(index){
                window.location.href = "/logout.do";
                layer.close(index);
            });
        })
    })
})(this);
$(function (){
    //点击标签事件
    $(document).on('click','.categoryDetail label',function(){
        $(this).addClass("changColor");
        var content_txt = $(this).html();
        var content_code = $(this)[0].id;
        var category = $('#selectedCate')[0].children;
        var flag="1";
        if(category!=""&&category!="undefind"&&category!=null&&category.length>0) {
            for (var i = 0; i < category.length; i++) {
                if(category[i].dataset.code==content_code){
                    flag="0";
                }
            }
            if(flag=="1"){
                var html ="<span class='choseCategory' data-code='"+content_code+"' >"+content_txt+"<button class='cancleLogo'></button></span>";
                $("#selectedCate").append(html);
            }
        }else{
            var html ="<span class='choseCategory' data-code='"+content_code+"' >"+content_txt+"<button class='cancleLogo'></button></span>";
            $("#selectedCate").append(html);
        }
        //删除
        $(".cancleLogo").click(function(){

            $(this).parent().remove();
            var  code = $(this).parent()[0].dataset.code;
            $('#'+code+'').removeClass("changColor");
        });
    });
    //点击标签事件
    $(document).on('click','.rlzyStyle label',function(){
        $(this).addClass("changColor");
        var content_txt = $(this).html();
        var content_code = $(this)[0].id;
        var content_value = $(this)[0].dataset.value;
        var category = $('#selectedCate')[0].children;
        var flag="1";//是否已经选中的标志
        if(category!=""&&category!="undefind"&&category!=null&&category.length>0) {   //选中类型有值时
            for (var i = 0; i < category.length; i++) {
               if(category[i].dataset.code==content_code){
                   flag="0";
               }
            }
            if(flag=="1"){
                if(content_txt=="全部"){//点击的目录是二级目录的“全部”时
                    var html ="<span class='choseCategory' data-code='"+content_code+"'>"+content_value+"<button class='cancleLogo'></button></span>";
                    $("#selectedCate").append(html);
                }else{//点击的目录不是二级目录的“全部”时
                   var html ="<span class='choseCategory' data-code='"+content_code+"'>"+content_txt+"<button class='cancleLogo'></button></span>";
                   $("#selectedCate").append(html);
                }
            }
        //选中类型没有值时
        }else{
            if(content_txt=="全部"){  //点击的目录是二级目录的“全部”时
                var html ="<span class='choseCategory' data-code='"+content_code+"'>"+content_value+"<button class='cancleLogo'></button></span>";
                $("#selectedCate").append(html);
            }else{  //点击的目录不是二级目录的“全部”时
                var html ="<span class='choseCategory' data-code='"+content_code+"'>"+content_txt+"<button class='cancleLogo'></button></span>";
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
    //点击文件类型事件
    $(".docCategory .categoryList").click(function(){
        $(".docCategory .categoryList").removeClass("changDeltaImg");
        $(this).addClass("changDeltaImg");
        var categoryCode = $(this)[0].dataset.code;
        $("#cateDiv").css("display","block");
        $("#cateDiv")[0].innerHTML="";
        for(var i=0;i<categoryList.length;i++){
            if(categoryList[i].code==categoryCode&&categoryList[i].pid!="root") {
                var seconedlist = categoryList[i].children;
                var html =" <div class='allStyle' ><div class='rlzyStyle'><label class='removeBackground' id='" + categoryList[i].code + "' data-value='"+categoryList[i].name+"'>全部</label></div><div class='categoryDetail'></div></div>";
                $("#cateDiv").append(html);
                if(seconedlist!=null){
                    for (var j = 0; j < seconedlist.length; j++) {
                        var html ="";
                        html += "  <div class='allStyle' ><div class='rlzyStyle'><label id='" + seconedlist[j].code + "'>" + seconedlist[j].name + "</label></div><div class='categoryDetail' >";
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
            }else if(categoryCode=="A"&&categoryList[i].pid=="root"){//为根节点时
                var category = $('#selectedCate')[0].children;
                var flag="1";
                if(category!=""&&category!="undefind"&&category!=null&&category.length>0) {
                    for (var i = 0; i < category.length; i++) {
                        if(category[i].dataset.code=="A"){
                            flag="0";
                        }
                    }
                    if(flag=="1") {
                        html = "";
                        html = "<span class='choseCategory' data-code='" + categoryList[i].code + "'>全部<button class='cancleLogo'></button></span>";
                        $("#selectedCate").append(html);
                    }
                }else{
                    html="";
                     html ="<span class='choseCategory' data-code='A'>全部<button class='cancleLogo'></button></span>";
                    $("#selectedCate").append(html);
                }
                //删除
                $(".cancleLogo").click(function(){
                    $(this).parent().remove();
                    var  code = $(this).parent()[0].dataset.code;
                    $('#'+code+'').removeClass("changColor");
                });
            }
        }
    });
});
/*点击高级搜索按钮*/
function toAdvancedQuery(){
    var fileName = $("#searchInput").val();
    var fileType = $("input[name='fileType']:checked").val();
    var category = $('#selectedCate')[0].children;
    var orderType = $("#sortOrder").val();
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
}
/*注销*/
function cancleLogin(){
    layer.confirm('确认注销？', {icon: 3, title: '提示'}, function (index) {
        window.location.href = "/logout.do";
        layer.close(index);
    });
}