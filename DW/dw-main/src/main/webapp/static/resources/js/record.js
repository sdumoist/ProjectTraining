//用户积分记录

(function () {
    $(document).ready(function () {
        gridView.initPage();
        /**
         * 菜单点击事件
         */
        $(".item").click(function () {
            $(".item").removeClass("active");
            $(this).addClass("active");
        });
        $("#queryBtn").click(function () {
            fileName =  $("#searchInput").val();
            fileType =   $('input:radio:checked').val();
            if(fileName!=""){
                window.location.href="/search?keyWords="+fileName+"&fileType="+fileType+"&page="+1;
            }else{
                alert("请输入关键词，多个关键词以空格隔开");
            }
        });

        /**
         * 评论提交点击事件
         */
        $("#reviewSubmitButton").unbind('click');
        $("#reviewSubmitButton").click(function () {
            var userId = $("#reviewUserId").val();
            var docId = $("#reviewDocId").val();
            var score = $("#reviewScore").val();
            var comment = $("#reviewContent").val();
            if (score == '0') {
                $.showInfoDlg("提示","分数不可为空",2);
                // alert("分数不可为空");
                return;
            }
            if (comment == '') {
                $.showInfoDlg("提示","评论不可为空",2);
                // alert("评论不可为空");
                return;
            }
            $.ajax({
                traditional: true,// 这使json格式的字符不会被转码
                type: "post",
                async: false,
                url: "/recordInfo/userReview",
                data: {
                    userId: userId,
                    docId: docId,
                    score: score,
                    comment: comment
                },
                dataType: "text",
                success: function (data) {
                    $("#myModal").modal("hide");
                    if (data == "success") {
                        $.showInfoDlg("提示","评分成功",1);
                        // alert("评分成功");
                    } else {
                        $.showInfoDlg("提示","评分失败",2);
                        // alert("评分失败");
                    }
                    initRecordTable($("#userIdArea").val(), 'download');
                }
            })

        });

        /**
         * 查询按钮点击事件
         */
        $("#queryBtn").click(function () {
            var fileName = $("#searchInput").val();
            var fileType = $('input:radio:checked').val();
            if (fileName != "") {
                window.location.href = "/search?keyWords=" + fileName + "&fileType=" + fileType + "&page=" + 1;
            } else {
                $.showInfoDlg("提示","请输入关键词，多个关键词以空格隔开",2);
                // alert("请输入关键词，多个关键词以空格隔开");
            }
        });
    });

    var gridView = {
        // 页面初始化
        initPage: function () {
            var that = this;
            // 初始化浏览记录
            that.initUserPreviewFileList();
            // 初始化表格
            initRecordTable($("#userIdArea").val(), 'upload');
        },

        /**
         * 用户最近浏览记录
         */
        initUserPreviewFileList: function () {
            $.ajax({
                traditional: true,// 这使json格式的字符不会被转码
                type: "post",
                async: false,
                url: "/recordInfo/userPreviewFile",
                data: {
                    userId: $("#userIdArea").val(),
                    pageSize: 4,
                    pageIndex: 1
                },
                dataType: "json",
                success: function (data) {
                    var str = '';
                    if (data.length == 0) {
                        str += "暂无记录";
                    } else {
                        for (var i = 0; i < data.length; i++) {
                            str += "<li ><a href='/preview/toShowPDF?id=" + data[i].doc_id + "' target='_blank' class='col-sm-8'> " + data[i].title + " </a><span class='col-sm-4'>" + data[i].preview_time + "</span></li>";
                        }
                    }
                    $("#userPreviewFileList").html(str);
                }
            });
        },

    }

})(this);
/**
 * 记录表数据加载
 * @param userId
 * @param recordType
 */
//总页数
var pageCount = 1;
//当前页数
var nowPage = 1;
//每页条数
var pageSize = 5;
//表格类型
var tableType = "upload";

function initRecordTable(userId, recordType) {
    if (recordType != tableType) {
        nowPage = 1;
    }
    tableType = recordType;
    //获得数据总页数
    $.ajax({
        traditional: true,// 这使json格式的字符不会被转码
        type: "post",
        async: false,
        url: "/recordInfo/getRecordDataCount",
        data: {
            userId: userId,
            recordType: tableType
        },
        dataType: "text",
        success: function (data) {
            var dataCount = parseInt(data);
            pageCount = Math.ceil(dataCount / pageSize);
        }
    })
    //获得表格数据
    $.ajax({
        traditional: true,// 这使json格式的字符不会被转码
        type: "post",
        async: false,
        url: "/recordInfo/getRecordData",
        data: {
            userId: userId,
            recordType: tableType,
            pageSize: pageSize,
            pageIndex: nowPage
        },
        dataType: "json",
        success: function (data) {
            var str = '';
            if (recordType == "upload") {
                str += "<div class='info-head'>上传记录</div> <div class='table-box'> <table class='table'> <tbody> <tr> <th>名称</th> <th>综合评分</th> <th>上传用户</th> <th>积分</th>  <th>上传时间</th><th>转换情况</th> </tr>";
                if (data.length == 0) {
                    str += "<tr><td>暂无数据</td><td></td><td></td><td></td><td></td><td></td></tr>";
                } else {
                    for (var i = 0; i < data.length; i++) {
                        if(data[i].transferStatus == '转换中'||data[i].transferStatus == '转换失败'){
                            str += "<tr> <td><div class='col-sm-8'>" + data[i].title + "</div></td> <td>" + data[i].avg_rate + "</td> <td>" + data[i].real_name + "</td> <td>" + data[i].score_change + "</td> <td>" + data[i].record_time + "</td><td>"+data[i].transferStatus+"</td> </tr>";
                        }else{
                            str += "<tr> <td><a href='/preview/toShowPDF?id=" + data[i].doc_id + "' target='_blank' class='col-sm-8'>" + data[i].title + "</a></td> <td>" + data[i].avg_rate + "</td> <td>" + data[i].real_name + "</td> <td>" + data[i].score_change + "</td> <td>" + data[i].record_time + "</td><td>"+data[i].transferStatus+"</td> </tr>";
                        }
                    }
                }
            } else if (recordType == "download") {
                str += "<div class='info-head'>下载记录</div> <div class='table-box'> <table class='table'> <tbody> <tr> <th>名称</th>  <th>上传用户</th> <th>积分</th>  <th>下载时间</th> <th>评价</th> </tr>";
                if (data.length == 0) {
                    str += "<tr><td>暂无数据</td><td></td><td></td><td></td><td></td></tr>";
                } else {
                    for (var i = 0; i < data.length; i++) {
                        str += "<tr> <td><a href='/preview/toShowPDF?id=" + data[i].doc_id + "' target='_blank' class='col-sm-8'>" + data[i].title + "</a></td> <td>" + data[i].real_name + "</td> <td>" + data[i].score_change + "</td> <td>" + data[i].record_time + "</td>";
                        if (data[i].rate == 0) {
                            str += " <td><span class='no' data-userId='" + data[i].user_id + "' data-docId='" + data[i].doc_id + "'>未评价</span></td></tr>"
                        } else {
                            str += "  <td><ul class='star-list'>";
                            for (var j = 0; j < data[i].rate; j++) {
                                str += "<li class='star light'></li>";
                            }
                            for (var k = 0; k < 5 - data[i].rate; k++) {
                                str += "<li class='star'></li>";
                            }
                            str += "</ul> </td> </tr>";
                        }
                    }
                }
            } else if (recordType == "preview") {
                str += "<div class='info-head'>浏览记录</div> <div class='table-box'> <table class='table'> <tbody> <tr> <th>名称</th> <th>综合评分</th> <th>上传用户</th>  <th>浏览时间</th> </tr>";
                if (data.length == 0) {
                    str += "<tr><td>暂无数据</td><td></td><td></td><td></td></tr>";
                } else {
                    for (var i = 0; i < data.length; i++) {
                        str += "<tr> <td><a href='/preview/toShowPDF?id=" + data[i].doc_id + "' target='_blank' class='col-sm-8'>" + data[i].title + "</a></td> <td>" + data[i].avg_rate + "</td> <td>" + data[i].real_name + "</td>  <td>" + data[i].preview_time + "</td> </tr>";
                    }
                }
            } else if (recordType == "collect") {
                str += "<div class='info-head'>我的收藏</div> <div class='table-box'> <table class='table'> <tbody> <tr> <th>名称</th> <th>综合评分</th> <th>上传用户</th>  <th>上传时间</th> </tr>";
                if (data.length == 0) {
                    str += "<tr><td>暂无数据</td><td></td><td></td><td></td></tr>";
                } else {
                    for (var i = 0; i < data.length; i++) {
                        str += "<tr> <td><a href='/preview/toShowPDF?id=" + data[i].doc_id + "' target='_blank' class='col-sm-8'>" + data[i].title + "</a></td> <td>" + data[i].avg_rate + "</td> <td>" + data[i].real_name + "</td> <td>" + data[i].collection_time + "</td> </tr>";
                    }
                }
            }
            if (data.length != 0) {
                str += "</tbody> </table><div class='pageSize'> <span>共" + pageCount + "页，当前第 " + nowPage + "/" + pageCount + " 页</span> <a class='btn-page' data-page='1' href='javascript:void (0)'>首页</a> ";
                if (nowPage == 1) {
                    str += "<a class='btn-page' data-page='1' href='javascript:void (0)'>上一页</a>  ";
                } else {
                    str += "<a class='btn-page' data-page='" + (nowPage - 1) + "' href='javascript:void (0)'>上一页</a>  ";
                }
                if(pageCount < 5){
                    for (var i = 0; i < pageCount; i++) {
                        if ((i + 1) == nowPage) {
                            str += "<a class='btn-page active' data-page='" + (i + 1) + "' href='javascript:void (0)'>" + (i + 1) + "</a> ";
                        } else {
                            str += "<a class='btn-page' data-page='" + (i + 1) + "' href='javascript:void (0)'>" + (i + 1) + "</a> ";
                        }
                    }
                }else{
                    var firstIndex = 0;
                    if(nowPage>3&&(pageCount-nowPage>=3)){
                        firstIndex = nowPage - 3;
                    }else if(nowPage>3&&(pageCount-nowPage<3)){
                        firstIndex = pageCount-5;
                    }else{
                        firstIndex = 0;
                    }
                    for (var i = firstIndex; i < firstIndex+5; i++) {
                        if ((i + 1) == nowPage) {
                            str += "<a class='btn-page active' data-page='" + (i + 1) + "' href='javascript:void (0)'>" + (i + 1) + "</a> ";
                        } else {
                            str += "<a class='btn-page' data-page='" + (i + 1) + "' href='javascript:void (0)'>" + (i + 1) + "</a> ";
                        }
                    }
                }

                if (nowPage == pageCount) {
                    str += "<a class='btn-page' data-page='" + pageCount + "' href='javascript:void (0)'>下一页</a> ";
                } else {
                    str += "<a class='btn-page' data-page='" + (nowPage + 1) + "' href='javascript:void (0)'>下一页</a> ";
                }
                str += "<a class='btn-page' data-page='" + pageCount + "' href='javascript:void (0)'>尾页</a> </div> </div>";
            } else {
                str += "</tbody> </table>";
            }
            $("#recordTableArea").html(str);
        }
    });
    /**
     * 评论相关事件
     */
    var star = $(".modal-self .star");

    $(".no").click(function () {
        var userId = $(this).attr("data-userId");
        var docId = $(this).attr("data-docId");
        $("#reviewUserId").val(userId);
        $("#reviewDocId").val(docId);
        $("#myModal").modal('show');
        star.removeClass("light");
        $("#reviewContent").val("");
        $("#reviewScore").val("0");
    });

// star.hover(function () {
//     star.removeClass("light");
//     var index = star.index(this) + 1;
//     for (var i = 0; i < index; i++) {
//         star.eq(i).addClass("light-1");
//     }
// }, function () {
//     star.removeClass("light-1")
// });
    star.click(function () {
        star.removeClass("light");
        var index = star.index(this) + 1;
        for (var i = 0; i < index; i++) {
            star.eq(i).addClass("light");
        }
        $("#reviewScore").val(index);
    });

    /**
     * 分页点击事件
     */
    $(".btn-page").click(function () {
        var pageNumber = $(this).attr("data-page");
        nowPage = parseInt(pageNumber);
        initRecordTable($("#userIdArea").val(), tableType);
    });
}


/**
 * 注销
 */
function cancleLogin() {
    if (confirm("确定要注销吗？")) {
        location.href = "/index/logout";
    }
}