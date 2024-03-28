var dbclickover = false;
var clickFlag=false;
layui.use(['jquery',  'tree', 'table', 'util', 'laytpl', 'form', 'layer', 'element'], function () {
    var table = layui.table;
    var element = layui.element;
    var rState = "";
    var FileBin = {
        tableId: "FileBinTable",	//表格id
        seItem: null		//选中的条目
    };

    //初始化表格
    FileBin.initTableView = function () {
        var word = $("#wordName").val();
        tableIns =table.render({
            elem: '#fileList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: '/examineFile/list?name=' + word + "&rState=" + rState//数据接口
            , request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            , page: true //开启分页
            , even:true
            , cols: [[
                {field: 'examineId', title: 'id', type: 'checkbox', width: '4%', align: "center"},
                {type: 'numbers', title: '序号', align: 'center', width: '5%'},
                {field: 'title', title: '文件名', align:'left', width: '25%', templet: '#docName'},
                {field: 'sensitiveWord', title: '敏感词', align: "left", width:'17%'},
                {field: 'reviewState', title: '审核状态', align: 'center', width: '8%',templet: '#state'},
                {field: 'reviewUser', title: '审核人', align: 'center', width: '12%'},
                {field: 'reviewDescribe', title: '审核意见', align: 'left', width: '30%'}
            ]] //设置表头
        });
        dbclickover = true;
    };

    FileBin.initTableView_2 = function(){
        var word = $("#wordName").val();
        tableIns =table.render({
            elem: '#fileList' //指定原始表格元素选择器（推荐id选择器）
            , height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10 //容器高度
            , url: '/examineFile/list?name=' + word + "&rState=" + rState//数据接口
            , request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            , page: true //开启分页
            , even:true
            , cols: [[
                {type: 'numbers', title: '序号', align: 'center', width: '5%'},
                {field: 'title', title: '文件名', align:'left', width: '25%', templet: '#docName'},
                {field: 'sensitiveWord', title: '敏感词', align: "left", width:'17%'},
                {field: 'reviewState', title: '审核状态', align: 'center', width: '8%',templet: '#state'},
                {field: 'reviewUser', title: '审核人', align: 'center', width: '12%'},
                {field: 'reviewDescribe', title: '审核意见', align: 'left', width: '35%'}
            ]] //设置表头
        });
        dbclickover = true;
    };

    active = {
        getCheckData: function () { //获取选中数据
            //.看看已选中的所有数据
            var mySelected = [];
            var checkStatus = table.checkStatus('fileList'), mySelected = checkStatus.data;
            return mySelected;
        }
    };
    FileBin.initButton = function() {
        $("#passBtn").click(function () {
            openPass('通过', '/examineFile/passView', 500, 245);
        });
        $("#delBtn").click(function () {
            openDel('删除', '/examineFile/delView', 500, 245);
        });
        $("#searchBtn").click(function () {
            if (rState == 0){
                FileBin.initTableView();
            } else {
                FileBin.initTableView_2();
            }

        });
        //全部
        $('[name="rState"]').siblings("div.layui-form-select").find('dl dd[lay-value="-1"]').click(function () {
            $("#passBtn").addClass("hide");
            $("#delBtn").addClass("hide");
            rState = -1;
            FileBin.initTableView_2();
        });
        //未审核
        $('[name="rState"]').siblings("div.layui-form-select").find('dl dd[lay-value="0"]').click(function () {
            $("#passBtn").removeClass("hide");
            $("#delBtn").removeClass("hide");
            rState = 0;
            FileBin.initTableView();
        });
        //已恢复
        $('[name="rState"]').siblings("div.layui-form-select").find('dl dd[lay-value="1"]').click(function () {
            $("#passBtn").addClass("hide");
            $("#delBtn").addClass("hide");
            rState = 1;
            FileBin.initTableView_2();
        });
        //已删除
        $('[name="rState"]').siblings("div.layui-form-select").find('dl dd[lay-value="2"]').click(function () {
            $("#delBtn").addClass("hide");
            $("#passBtn").addClass("hide");
            rState = 2;
            FileBin.initTableView_2();
        });
    };
    window.initTableView = function () {
        FileBin.initTableView();
    };
    /*打开敏感词编辑*/
    function openPass(title, url, w, h) {
        var dataArr = active.getCheckData();
        if (dataArr.length < 1) {
            layer.alert('请先选择一条恢复的文档', {
                icon: 0,
                maxmin: true,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
            return;
        }
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
        examineId = new Array();
        for (var i = 0; i < dataArr.length; i++) {
            examineId.push(dataArr[i].id);
        }
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: url + "?examineId=" + examineId
        });
    }

    /*打开编辑页面*/
    function openDel(title, url, w, h) {
        var dataArr = active.getCheckData();
        if (dataArr.length < 1) {
            layer.alert('请先选择一条要删除的数据', {
                icon: 0,
                maxmin: true,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
            return;
        }
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
        examineId = new Array();
        for (var i = 0; i < dataArr.length; i++) {
            examineId.push(dataArr[i].id);
        }
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: url + "?examineId=" + examineId
        });
    }

    $(function () {
        FileBin.initTableView();//初始化表格
        FileBin.initButton();
        $(window).resize(function() {
            FileBin.initTableView();
        });
    });

    element.on('dbclick(fileList)', function (data) {
        console.log(data);
    })
});
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