var workType="";
var industry_type=""
var color=""
var docId="";
var folderId="";
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar','HussarAjax'], function(){
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;

    $(function () {
        //显示截取的文字
        var timer;
        var tips;
        $(document).on('mouseover',".ellipse",function (e) {
            var _this = this;
            timer = setTimeout(function () {
                var text = $(_this).text();
                tips = layer.tips(text,_this,{
                    tips:1,
                    time:30000
                })
            },500)
        });
        $(document).on('mouseout',".ellipse",function (e) {
            clearTimeout(timer);
            layer.close(tips);
        });
        //
        getDic();
        //积分系统控制
        /*$.ajax({
            url: Hussar.ctxPath+"/integral/addIntegral",
            async: true,
            data:{
                docId: null,
                ruleCode: 'search'
            },
            success: function (data) {
                if (null != data && data != '' && data != undefined){
                    if (data.integral != 0 && data.integral != null && data.integral != ''){
                        $("#num").html(data.msg)
                        $(".integral").show();
                        setTimeout(function () {
                            $(".integral").hide();
                        },2000)

                    }
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
            if (null != data && data != '' && data != undefined){
                if (data.integral != 0 && data.integral != null && data.integral != ''){
                    $("#num").html(data.msg)
                    $(".integral").show();
                    setTimeout(function () {
                        $(".integral").hide();
                    },2000)

                }
            }
        }, function(data) {

        });
        ajax.set("docId",null);
        ajax.set("ruleCode",'search');
        ajax.start();

        $(".tab-item").click(function () {
            if( $(this).hasClass('active')){
                $(this).removeClass('active');
            }else{
                $(this).addClass('active');
            }
            //  $(this).siblings('.tab-item').removeClass('active')
        });
        layui.use('form', function(){
            var form = layui.form;
            form.render();
        });

        var count="";
        var gridView ={
            /*初始化页面*/
            initPage: function() {
                var that = this;
                var fileType = $("#fileTypeValue").val(); //文档类型
                var fileName =  $("#headerSearchInputValue").val();
                //初始化表格
                that.initButtonEvent();
                that.initView(1,fileName,fileType,null);

                //初始化表格
            },
            initButtonEvent:function(){
                layui.use('form', function(){
                    var form = layui.form;
                    form.on('checkbox(type)', function (data) {
                        var name =  $("#headerSearchInputValue").val();
                        var type =   "8";

                        //如果在文档搜索中查询全部 ，仍然是选择 文档全部
                        // if($("#fileTypeValue").val()=="7"&&  $("input[type=radio][name='fileType']:checked").val()==0){
                        //     type=7;
                        // }


                        if(data.value=='on'){
                            if( $("input[name='type']").eq(0).prop("checked")==true){
                                $("input[name='type']").each(function (index) {
                                    if(index>0){
                                        $(this).prop("checked", false)
                                    }
                                })
                            } else{

                            }
                        }else {
                            $("input[name='type']:checked").each(function (index) {
                                if(index>0){
                                    $("input[name='type']").eq(0).prop("checked",false);
                                }
                            })
                        }

                        form.render();

                        if(workType=="on"){
                            workType=""
                        }
                        var arr = new Array();
                        $("input:checkbox[name='type']:checked").each(function(i){
                            arr[i] = $(this).val();
                            if( arr[i]=="on"){
                                arr[i]="";
                            }

                        });
                        workType = arr.join(",")


                        gridView.initView(1,name,type,industry_type+"|"+workType+"|"+color);
                        var grid= $('.grid').masonry({
                            // options
                            itemSelector: '.grid-item'
                        });

                        grid.masonry('destroy');
                        grid.imagesLoaded( function() {
                            $(".photo-item  img").each(function () {
                                if($(this).height()<"100"){
                                    $(this).height("200");
                                    $(this).width("auto");
                                    $(this).css("max-width","290px")
                                    $(this).css("object-position","center")

                                }
                            });
                            grid.masonry();
                        });

                        $(".tips").css("position","unset")

                    });
                    form.on('checkbox(industry_type)', function (data) {
                        var name =  $("#headerSearchInputValue").val();
                        var type = "8";

                        //如果在文档搜索中查询全部 ，仍然是选择 文档全部
                        // if($("#fileTypeValue").val()=="7"&&  $("input[type=radio][name='fileType']:checked").val()==0){
                        //     type=7;
                        // }
                        if(industry_type=="on"){
                            industry_type=""
                        }
                        if(data.value=='on'){
                            if( $("input[name='industry_type']").eq(0).prop("checked")==true){
                                $("input[name='industry_type']").each(function (index) {
                                    if(index>0){
                                        $(this).prop("checked", false);
                                    }
                                })
                            } else{


                            }
                        }else{
                            $("input[name='industry_type']:checked").each(function (index) {
                                if(index>0){
                                    $("input[name='industry_type']").eq(0).prop("checked",false);
                                }
                            })
                        }
                        form.render();


                        var arr = new Array();
                        $("input:checkbox[name='industry_type']:checked").each(function(i){
                            arr[i] = $(this).val();
                            if( arr[i]=="on"){
                                arr[i]="";
                            }
                        });

                        industry_type = arr.join(",")

                        gridView.initView(1,name,type,industry_type+"|"+workType+"|"+color);
                        var grid= $('.grid').masonry({
                            // options
                            itemSelector: '.grid-item'
                        });
                        grid.masonry('destroy');



                        grid.imagesLoaded( function() {
                            $(".photo-item  img").each(function () {
                                if($(this).height()<"100"){
                                    $(this).height("200");
                                    $(this).width("auto");

                                }
                            })
                            grid.masonry();
                        });


                        $(".tips").css("position","unset")

                    });
                    $(".color-select").click(function () {
                        if( $(this).hasClass('color-selected')){
                            $(this).removeClass('color-selected');
                        }else{
                            $(this).addClass('color-selected');
                        }
                        if($(this).hasClass("all-color")){
                            if(  $('.all-color').eq(0).hasClass('color-selected')==true){

                                $('.color-select:gt(0)').each(function () {
                                    $(this).removeClass('color-selected');})
                            } else{

                            }
                        }else{
                            $(".color-selected").each(function (index) {
                                if(index>=0){
                                    $(".all-color").removeClass('color-selected');
                                }
                            })
                        }
                        form.render();

                        var name =  $("#headerSearchInputValue").val();
                        var type = "8";
                        var arr = new Array();
                        $('.color-selected').each(function(i){
                            arr[i] = $(this).data('value');
                            if( arr[i]=="on"){

                                arr[i]="";
                            }
                        });

                        color = arr.join(",");

                        gridView.initView(1,name,type,industry_type+"|"+workType+"|"+color);
                        var grid= $('.grid').masonry({
                            // options
                            itemSelector: '.grid-item'

                        });
                        grid.masonry('destroy');


                        grid.imagesLoaded( function() {
                            $(".photo-item  img").each(function () {
                                if($(this).height()<"100"){
                                    $(this).height("200");
                                    $(this).width("auto");
                                    $(this).css("max-width","290px")
                                    $(this).css("object-position","center")

                                }
                            })
                            grid.masonry();

                        });
                        $(".tips").css("position","unset")

                    });
                });
            },
            initView:function(page,fileName,fileType,tagString){
                docId = $("#docId").val();
                folderId = $("#folderId").val();
                /*$.ajax({
                    async: false,
                    type: "post",
                    url: Hussar.ctxPath+"/preview/folderIMG",
                    data: {docId: docId, folderId: folderId, page:page,size:30},
                    success: function (data) {
                        var json = eval(data);
                        var lis = [];
                        if (json.total == 0) {
                            $("#articleItem").html('<div class="tips" style="width: 100%"><div class="tipPic"></div><div class="tipTxt">未找到相关内容~</div></div>');
                            $("#totalCount").html('0');
                            $("#footDiv").hide();
                            $("#laypageAre").hide();
                        } else {
                            var json = eval(data);
                            $("#articleItem").html("");
                            $("#laypageAre").show();
                            layui.each(json.items, function(index, item){
                                var param='  <div class="grid-item layui-col-sm3"> ' +
                                    '<div class="photo-item "> ' +
                                    '<a href="javascript:void(0)" target="_blank" onclick="showDoc(\''+item.docType+'\',\''+item.docId+'\');return false;"> ' +
                                    '<img src="'+Hussar.ctxPath+'/preview/list?fileId='+item.filePdfPath+'&&isThumbnails=0"> ' +
                                    '<div class="photo-item-title"  title="'+item.title+'" >'+item.title+' ' +
                                    '</div> ' +
                                    '</a> ' +
                                    '</div> ' +
                                    '</div>';
                                $("#articleItem").append(param);
                                $('.grid').masonry('reload');
                            });
                            count = json.total;
                            var adminFlag= $("#adminFlag").val();
                            json.adminFlag=adminFlag;
                            $("#totalCount").html(count);
                            $("#count").val(count);

                        }
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/preview/folderIMG", function(data) {
                    var json = eval(data);
                    var lis = [];
                    if (json.total == 0) {
                        $("#articleItem").html('<div class="tips" style="width: 100%"><div class="tipPic"></div><div class="tipTxt">未找到相关内容~</div></div>');
                        $("#totalCount").html('0');
                        $("#footDiv").hide();
                        $("#laypageAre").hide();
                    } else {
                        var json = eval(data);
                        $("#articleItem").html("");
                        $("#laypageAre").show();
                        layui.each(json.items, function(index, item){
                            var param='  <div class="grid-item layui-col-sm3"> ' +
                                '<div class="photo-item "> ' +
                                '<a href="javascript:void(0)" target="_blank" onclick="showDoc(\''+item.docType+'\',\''+item.docId+'\');return false;"> ' +
                                '<img src="'+Hussar.ctxPath+'/preview/list?fileId='+item.filePdfPath+'&&isThumbnails=0" onerror="javascript:this.src=de'+Hussar.ctxPath+'fault.png> ' +
                                '<div class="photo-item-title"  title="'+item.title+'" >'+item.title+' ' +
                                '</div> ' +
                                '</a> ' +
                                '</div> ' +
                                '</div>';
                            $("#articleItem").append(param);
                            $('.grid').masonry('reload');
                        });
                        count = json.total;
                        var adminFlag= $("#adminFlag").val();
                        json.adminFlag=adminFlag;
                        $("#totalCount").html(count);
                        $("#count").val(count);

                    }
                }, function(data) {

                });
                ajax.set("docId",docId);
                ajax.set("folderId",folderId);
                ajax.set("page",page);
                ajax.set("size",30);
                ajax.start();
                layui.use(['laypage','layer'], function(){
                    var laypage = layui.laypage,
                        layer = layui.layer;
                    var count=$("#count").val();
                    laypage.render({
                        elem: 'laypageAre'
                        ,count: count
                        ,limit: 30
                        ,layout: ['prev', 'page', 'next']
                        ,curr: page || 1
                        ,jump: function(obj, first){
                            //首次不执行
                            if(!first){
                                var type =   "8";

                                //如果在文档搜索中查询全部 ，仍然是选择 文档全部
                                // if($("#fileTypeValue").val()=="7"&&  $("input[type=radio][name='fileType']:checked").val()==0){
                                //     type=7;
                                // }

                                gridView.initView(obj.curr,name,type,industry_type+"|"+workType+"|"+color);
                                var grid= $('.grid').masonry({
                                    // options
                                    itemSelector: '.grid-item'
                                });
                                grid.masonry('destroy');
                                grid.imagesLoaded( function() {
                                    $(".photo-item  img").each(function () {
                                        if($(this).height()<"100"){
                                            $(this).height("200");
                                            $(this).width("auto");
                                            $(this).css("max-width","290px")
                                            $(this).css("object-position","center")

                                        }
                                    });
                                    grid.masonry();
                                });
                            }
                        }
                    });
                });
            },
        };
        gridView.initPage();
        var grid= $('.grid').masonry({
            // options
            itemSelector: '.grid-item',
        });
        grid.masonry('destroy');
        grid.imagesLoaded( function() {
            $(".photo-item  img").each(function () {
                if($(this).height()<"100"){
                    $(this).height("200");
                    $(this).width("auto");
                    $(this).css("max-width","290px")
                    $(this).css("object-position","center")
                }
            });
            grid.masonry();
        });

    });
});
function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}

function showDoc(fileType,id) {
    var selectVal = $("#select").val();
    var keyWords = $("#headerSearchInputValue").val();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        if(fileType=="png"||fileType=="jpg"||fileType=="gif"||fileType=="bmp"||fileType=="jpeg"){
            openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }else if(fileType=="mp4"||fileType=="wmv"){
            openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        } else if(fileType=="mp3"||fileType=="m4a"){
            openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }else if(fileType == 'docx'||fileType == 'doc'||fileType == 'dot'||fileType == 'xls'
            ||fileType == 'wps'||fileType == 'xlt'||fileType == 'et'
            ||fileType == 'ett'||fileType == 'ppts'||fileType == 'pot'
            ||fileType == 'dps'||fileType == 'dpt'
            || fileType == 'xlsx'||fileType == 'txt'||fileType == 'pdf'
            ||fileType == 'ceb' ||fileType == 'ppt'|| fileType == 'pptx'){
            openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }else {
            openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords));
        }
    });
}
function showPdf(id,flag,fileSuffixName) {
    var keyword =  $("#headerSearchInputValue").val();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        if(fileSuffixName=="png"||fileSuffixName=="jpg"||fileSuffixName=="gif"||fileSuffixName=="bmp"||fileSuffixName=="ceb"||fileSuffixName=="jpeg"){
            openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
        }else if(fileSuffixName=="mp4"||fileSuffixName=="wmv"){
            openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
        } else if(fileSuffixName=="mp3"||fileSuffixName=="m4a"){
            openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
        }else {
            openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
        }
    });
}
function  getDic() {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            async: false,
            type: "post",
            url: Hussar.ctxPath+"/dicList",
            dataType: 'json',
            data: {dicType: "industry_type"},
            success: function (result) {
                $("#industry_type").append(' <input type="checkbox" name="industry_type" lay-filter="industry_type" title="全部" lay-skin="primary" checked   >');
                for (var i = 0; i < result.length; i++) {
                    var param = ' <input type="checkbox" name="industry_type"  lay-filter="industry_type" title="' +result[i].LABEL+ '" lay-skin="primary" value="' +result[i].LABEL+ '">'
                    $("#industry_type").append(param);
                }
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/dicList", function(result) {
            $("#industry_type").append(' <input type="checkbox" name="industry_type" lay-filter="industry_type" title="全部" lay-skin="primary" checked   >');
            for (var i = 0; i < result.length; i++) {
                var param = ' <input type="checkbox" name="industry_type"  lay-filter="industry_type" title="' +result[i].LABEL+ '" lay-skin="primary" value="' +result[i].LABEL+ '">'
                $("#industry_type").append(param);
            }
        }, function(data) {

        });
        ajax.set("dicType","industry_type");
        ajax.start();

        /*$.ajax({
            async: false,
            type: "post",
            url: Hussar.ctxPath+"/dicList",
            dataType: 'json',
            data: {dicType: "type"},
            success: function (result) {
                $("#type").append(' <input type="checkbox" name="type" title="全部"   lay-filter="type" lay-skin="primary"  checked   >');
                for (var i = 0; i < result.length; i++) {
                    var param = ' <input type="checkbox" name="type"  lay-filter="type"  title="' +result[i].LABEL+ '" lay-skin="primary" value="' +result[i].LABEL+ '">'
                    $("#type").append(param);
                }
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/dicList", function(result) {
            $("#type").append(' <input type="checkbox" name="type" title="全部"   lay-filter="type" lay-skin="primary"  checked   >');
            for (var i = 0; i < result.length; i++) {
                var param = ' <input type="checkbox" name="type"  lay-filter="type"  title="' +result[i].LABEL+ '" lay-skin="primary" value="' +result[i].LABEL+ '">'
                $("#type").append(param);
            }
        }, function(data) {

        });
        ajax.set("dicType","type");
        ajax.start();

        /*$.ajax({
            async: false,
            type: "post",
            url: Hussar.ctxPath+"/dicList",
            dataType: 'json',
            data: {dicType: "color"},
            success: function (result) {
                $("#color").append(' <span    class="all-color color-select color-selected "    data-value=""><i class="iconfont">&#xe605;</i></span>');
                for (var i = 0; i < result.length; i++) {
                    var param = ' <span class="color-select c'+result[i].VALUE+'-color" style="background: #'+result[i].VALUE+'"  data-value="' +result[i].LABEL+ '"><i class="iconfont">&#xe605;</i></span>'
                    $("#color").append(param);
                }
            }
        })*/
        var ajax = new $ax(Hussar.ctxPath + "/dicList", function(result) {
            $("#color").append(' <span    class="all-color color-select color-selected "    data-value=""><i class="iconfont">&#xe605;</i></span>');
            for (var i = 0; i < result.length; i++) {
                var param = ' <span class="color-select c'+result[i].VALUE+'-color" style="background: #'+result[i].VALUE+'"  data-value="' +result[i].LABEL+ '"><i class="iconfont">&#xe605;</i></span>'
                $("#color").append(param);
            }
        }, function(data) {

        });
        ajax.set("dicType","color");
        ajax.start();
    });

}
