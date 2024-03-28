//金企文库基本信息页面脚本
$(document).ready(function() {
    $("#fileUpload").click(function(){
        window.location.href = "/file/goFileUpload";
    })
})
function logout(){
    if (confirm("确定要注销吗？")) {
        location.href = "/index/logout";
    }
}
function search(){
    var fileName = $("#searchInput").val();
    var fileType =   $('input:radio:checked').val();
    if(fileName!=""){
        window.location.href="/search?keyWords="+fileName+"&fileType="+fileType+"&page="+1;
    }else{
        $.showInfoDlg("提示","请输入关键词，多个关键词以空格隔开",2);
    }
}
function cancleLogin(){
    if (confirm("确定要注销吗？")) {
        location.href = "/index/logout";
    }
}
