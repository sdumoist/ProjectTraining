/**
 * Created by Lenovo on 2018/2/9.
 */
(function() {
    var count;
    var category;//分类
    var docId;//文档ID
    var authorId;//作者ID
    var fileName;//文档名臣
    var filePath;//文档路径
    var collection;//是否收藏
    var point = 1;//下载所需积分`
    var page;
    var  allowPage;
    var imgCount;
    $(document).ready(function() {
        gridView.initPage();
        if (window.screen.width >1024){
            $("body").addClass("macOsPc");
        }else {
            $("body").removeClass("macOsPc");
        }
    });
    var gridView ={
        /*初始化页面*/
        initPage: function() {
            var that = this;
            //初始化表格
            that.init();
            // that.initPath();
            that.initButtonEvent();
            that.createPageContext();
            that.initPhotoSwipeFromDOM('.my-gallery');
        },
        aftTurnInit:function(spageno){
            var that = this;
            //初始化页面
            // that.getRatingRecords(docId,spageno);
        },
        initPath:function(){
            var that = this;
            var id= $("#docId").val();
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

                var ajax = new $ax(Hussar.ctxPath + "/preview/getFoldPath", function(data) {
                    var path ="";
                    if(!!data){
                        for(var i=0;i<data.length;i++){
                            path +=" <span><a href='#' target='_blank' data-id='"+data[i].foldId+"'>"+data[i].foldName+"</a> <i class='layui-icon'>&#xe602;</i> </span>";
                        }
                    }else{
                        path ="";
                    }
                    fileName = $("#title").html()
                }, function(data) {

                });
                ajax.set("docId",id);
                ajax.start();
            });
        },
        init:function(){
            var that = this;
            var hash= $("#hash").val();
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;
                var ajax = new $ax(Hussar.ctxPath + "/sharefile/fileDetail", function(data) {
                    document.title = data.title+"-金企文库";
                    authorId = data.authorId;
                    filePath = data.filePath;
                    fileName = data.title;
                    collection =data.collection;
                    fileSuffixName = data.fileSuffixName.toLowerCase();
                    docAbstract = data.docAbstract;
                    docId = data.id;
                    authority=data.authority;
                    $("#docId").text(data.id);
                    $("#title").text(data.title);

                    var obj = document.getElementById("title");
                    if(fileSuffixName=="xlsx"||fileSuffixName=="xls"){  //文档名称前的图片
                        $("#title").addClass("type-xls");
                        // obj.style.cssText = "background:url(/static/resources/img/excel.png)no-repeat left center;"
                    }else if(fileSuffixName=="pdf"){
                        $("#title").addClass("type-pdf");
                        // obj.style.cssText = "background:url(/static/resources/img/pdf.png)no-repeat left center;"
                    }else if(fileSuffixName=="ppt"||fileSuffixName=="pptx"||fileSuffixName=="ppsx"){
                        $("#title").addClass("type-ppt");
                        // obj.style.cssText = "background:url(/static/resources/img/ppt.png)no-repeat left center;"
                    }else if(fileSuffixName=="ceb"){
                        $("#title").addClass("type-ceb");
                        // obj.style.cssText = "background:url(/static/resources/img/ppt.png)no-repeat left center;"
                    }else if(fileSuffixName=="txt") {
                        $("#title").addClass("type-txt");
                        // obj.style.cssText = "background:url(/static/resources/img/txt.png)no-repeat left center;";
                    }else if(fileSuffixName=="doc"||fileSuffixName=="docx") {
                        $("#title").addClass("type-doc");
                        // obj.style.cssText = "background:url(/static/resources/img/word.png)no-repeat left center;";
                    }else if(fileSuffixName=="png"||fileSuffixName=="jpeg"||fileSuffixName=="gif"||fileSuffixName=="jpg"||fileSuffixName=="bmp") {
                        $("#title").addClass("type-pic");
                        // obj.style.cssText = "background:url(/static/resources/img/word.png)no-repeat left center;";
                    }else {
                        $("#title").addClass("type-other");
                        // obj.style.cssText = "background:url(/static/resources/img/other.png)no-repeat left center;";
                    }
                    var author=data.author;
                    if(author==""||author==undefined){
                        author=data.userId;
                    }
                    $("#owner").html(""+author+"<em>|</em>");
                    $("#uploadTime").html(""+data.createTime +"<em>|</em>");
                    $("#fileSize").html(""+data.fileSize +"<em>|</em>");
                    $("#downloadNum").html(""+data.downloadNum +"次下载"+"<em>|</em>");
                    $("#previewNum").html(""+data.readNum +"次预览");
                    $(".my-gallery").append('<figure><div class="img-dv"><a href="/preview/listForShare?hash=' + hash + '" data-size="1920x1080"><img class="img-main" width="100%" src="/preview/listForShare?hash=' + hash + '"></a></div><figcaption style="display:none;">' + fileName + '</figcaption></figure>');

                }, function(data) {
                    $.showInfoDlg("提示","文件暂时无法预览，请稍后再试。",2);
                });
                ajax.set("hash",hash);
                ajax.start();

                var ajax = new $ax(Hussar.ctxPath + "/sharefile/folderIMG", function(data) {
                    var items = data.items;
                    var toLoad = "";
                    if (items.length != 0) {
                        for (var i = 0; i < items.length; i++) {//建议只显示4个，增加更多链接
                            if(items[i].isSelf){
                            } else {
                                toLoad += '<figure><div class="img-dv others"><a href="/preview/listForShare?fileId=' + items[i].filePath + '" data-size="1920x1080"><img height="80px" src="/preview/listForShare?fileId=' + items[i].filePdfPath + '&&isThumbnails=0"></a></div><figcaption style="display:none;">'+ items[i].title + '</figcaption></figure>';
                            }
                        }
                        $(".my-gallery").append(toLoad);
                    }
                    imgCount = data.count;
                }, function(data) {

                });
                ajax.set("page",'1');
                ajax.set("limit",'8');
                ajax.set("docId",docId);
                ajax.start();
            });
        },
        initButtonEvent:function (){
            var that = this;
            $("#collectionButton").click(function(){
                that.collectionFiles();
            });
            $('#loginButton').click(function () {
                loginSubmit();
            });

        },

        /**
         * 分页条
         */
        createPageContext : function(){
            var that = this;
            $('#footDiv').extendPagination({
                totalCount: count,
                showPage: 10,
                limit: 10,
                callback: function (curr, limit, totalCount) {
                    // that.getRatingRecords(docId,curr);
                }
            })
        },
        downloadFile:function(){//文档ID，作者ID，下载消耗积分,文档名，分类,文件路径
            var loginName = $("#loginName")[0].innerText;//获取登录用户的名称
            //判断用户是否登录
            if(loginName == "登录"){
                $("#login").modal();
                return;
            }
            layui.use(['Hussar','HussarAjax'], function(){
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;

                var ajax = new $ax(Hussar.ctxPath + "/file/changePoints", function(data) {
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
                        $.showInfoDlg("提示","下载出错",2);
                    }
                }, function(data) {
                    //alert("下载失败");
                    $.showInfoDlg("提示","下载失败",2);
                });
                ajax.set("authorId",authorId);
                ajax.set("docId",docId);
                ajax.set("points",point);
                ajax.start();
            });
        },

        initPhotoSwipeFromDOM:function(gallerySelector) {
            // 解析来自DOM元素幻灯片数据（URL，标题，大小...）
            var parseThumbnailElements = function(el) {
                var thumbElements = el.childNodes,
                    numNodes = thumbElements.length,
                    items = [],
                    figureEl,
                    linkEl,
                    size,
                    item,
                    divEl;
                for(var i = 0; i < numNodes; i++) {
                    figureEl = thumbElements[i]; // <figure> element
                    // 仅包括元素节点
                    if(figureEl.nodeType !== 1) {
                        continue;
                    }
                    divEl = figureEl.children[0];
                    linkEl = divEl.children[0]; // <a> element
                    size = linkEl.getAttribute('data-size').split('x');
                    // 创建幻灯片对象
                    item = {
                        src: linkEl.getAttribute('href'),
                        w: parseInt(size[0], 10),
                        h: parseInt(size[1], 10)
                    };
                    if(figureEl.children.length > 1) {
                        item.title = figureEl.children[1].innerHTML;
                    }
                    if(linkEl.children.length > 0) {
                        // <img> 缩略图节点, 检索缩略图网址
                        item.msrc = linkEl.children[0].getAttribute('src');
                    }
                    item.el = figureEl; // 保存链接元素 for getThumbBoundsFn
                    items.push(item);
                }
                return items;
            };

            // 查找最近的父节点
            var closest = function closest(el, fn) {
                return el && ( fn(el) ? el : closest(el.parentNode, fn) );
            };

            // 当用户点击缩略图触发
            var onThumbnailsClick = function(e) {
                e = e || window.event;
                e.preventDefault ? e.preventDefault() : e.returnValue = false;
                var eTarget = e.target || e.srcElement;
                var clickedListItem = closest(eTarget, function(el) {
                    return (el.tagName && el.tagName.toUpperCase() === 'FIGURE');
                });
                if(!clickedListItem) {
                    return;
                }
                var clickedGallery = clickedListItem.parentNode,
                    childNodes = clickedListItem.parentNode.childNodes,
                    numChildNodes = childNodes.length,
                    nodeIndex = 0,
                    index;
                for (var i = 0; i < numChildNodes; i++) {
                    if(childNodes[i].nodeType !== 1) {
                        continue;
                    }
                    if(childNodes[i] === clickedListItem) {
                        index = nodeIndex;
                        break;
                    }
                    nodeIndex++;
                }
                if(index >= 0) {
                    openPhotoSwipe( index, clickedGallery );
                }
                return false;
            };

            var photoswipeParseHash = function() {
                var hash = window.location.hash.substring(1),
                    params = {};
                if(hash.length < 5) {
                    return params;
                }
                var vars = hash.split('&');
                for (var i = 0; i < vars.length; i++) {
                    if(!vars[i]) {
                        continue;
                    }
                    var pair = vars[i].split('=');
                    if(pair.length < 2) {
                        continue;
                    }
                    params[pair[0]] = pair[1];
                }
                if(params.gid) {
                    params.gid = parseInt(params.gid, 10);
                }
                return params;
            };

            var openPhotoSwipe = function(index, galleryElement, disableAnimation, fromURL) {
                var pswpElement = document.querySelectorAll('.pswp')[0],
                    gallery,
                    options,
                    items;
                items = parseThumbnailElements(galleryElement);
                // 这里可以定义参数
                options = {
                    barsSize: {
                        top: 100,
                        bottom: 100
                    },
                    fullscreenEl : false,
                    shareButtons: [
                        {id:'wechat', label:'分享微信', url:'#'},
                        {id:'weibo', label:'新浪微博', url:'#'},
                        {id:'download', label:'保存图片', url:'{{raw_image_url}}', download:true}
                    ],
                    galleryUID: galleryElement.getAttribute('data-pswp-uid'),
                    getThumbBoundsFn: function(index) {
                        var thumbnail = items[index].el.getElementsByTagName('img')[0], // find thumbnail
                            pageYScroll = window.pageYOffset || document.documentElement.scrollTop,
                            rect = thumbnail.getBoundingClientRect();
                        return {x:rect.left, y:rect.top + pageYScroll, w:rect.width};
                    }
                };
                if(fromURL) {
                    if(options.galleryPIDs) {
                        for(var j = 0; j < items.length; j++) {
                            if(items[j].pid == index) {
                                options.index = j;
                                break;
                            }
                        }
                    } else {
                        options.index = parseInt(index, 10) - 1;
                    }
                } else {
                    options.index = parseInt(index, 10);
                }
                if( isNaN(options.index) ) {
                    return;
                }
                if(disableAnimation) {
                    options.showAnimationDuration = 0;
                }
                gallery = new PhotoSwipe( pswpElement, PhotoSwipeUI_Default, items, options);
                gallery.init();
            };

            var galleryElements = document.querySelectorAll( gallerySelector );
            for(var i = 0, l = galleryElements.length; i < l; i++) {
                galleryElements[i].setAttribute('data-pswp-uid', i+1);
                galleryElements[i].onclick = onThumbnailsClick;
            }
            var hashData = photoswipeParseHash();
            if(hashData.pid && hashData.gid) {
                openPhotoSwipe( hashData.pid ,  galleryElements[ hashData.gid - 1 ], true, true );
            }
        }
    }
    var fileType = $("#fileTypeValue").val(); //文档类型
    if (fileType==null||fileType==""||fileType==undefined){
        fileType=0;
    }
    $("#select").val(fileType);
    if(fileType=='6'||fileType=='8'||fileType=='9'||fileType=='10'){
        $(".search-box").hide()
    }
    $("input[type=radio][name='fileType']").eq(fileType).attr("checked","checked");

    $('input[type=radio][name=fileType]').change(function() {
        var fileType =    $("input[type=radio][name='fileType']:checked").val();
        $("#fileTypeValue").val(fileType);
    });

})(this);
function loginSubmit(){
    var name = $("#name").val();
    var password = $("#password").val();
    if(name==""){
        //alert("请输入账号！");
        $.showInfoDlg("提示","请输入账号！",2);
        return;
    }else if(password==""){
        //alert("请输入密码！");
        $.showInfoDlg("提示","请输入密码！",2);
        return ;
    }
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        var ajax = new $ax(Hussar.ctxPath + "/index/loginCheck", function(data) {
            if(data=="false"){
                //alert("用户名或密码不正确！");
                $.showInfoDlg("提示","用户名或密码不正确！",2);
            }else{
                $("#loginName")[0].innerText = name;
                $("#login").modal("hide");
            }
        }, function(data) {

        });
        ajax.set("name",name);
        ajax.set("password",password);
        ajax.start();
    });
}
function cancleLogin(){
    if (confirm("确定要注销吗？")) {
        location.href = "/index/logout";
    }
}
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
    if(fileType=="png"||fileType=="jpg"||fileType=="gif"||fileType=="bmp"||fileType=="jpeg"){
        openWin("/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_self");
    }else if(fileType=="mp4"||fileType=="wmv"){
        openWin("/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_self");
    } else if(fileType=="mp3"||fileType=="m4a"){
        openWin("/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_self");
    }else if(fileType == 'docx'||fileType == 'doc'||fileType == 'dot'||fileType == 'xls'
        ||fileType == 'wps'||fileType == 'xlt'||fileType == 'et'
        ||fileType == 'ett'||fileType == 'ppts'||fileType == 'pot'
        ||fileType == 'dps'||fileType == 'dpt'
        || fileType == 'xlsx'||fileType == 'txt'||fileType == 'pdf'
        ||fileType == 'ceb' ||fileType == 'ppt'|| fileType == 'pptx'){
        openWin("/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_self");
    }else {
        openWin("/preview/toShowOthers?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_self");
    }
}
function showDocBlank(fileType,id) {
    var selectVal = $("#select").val();
    var keyWords = $("#headerSearchInputValue").val();
    if(fileType=="png"||fileType=="jpg"||fileType=="gif"||fileType=="bmp"||fileType=="jpeg"){
        openWin("/preview/toShowIMG?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
    }else if(fileType=="mp4"||fileType=="wmv"){
        openWin("/preview/toShowVideo?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
    } else if(fileType=="mp3"||fileType=="m4a"){
        openWin("/preview/toShowVoice?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
    }else if(fileType == 'docx'||fileType == 'doc'||fileType == 'dot'||fileType == 'xls'
        ||fileType == 'wps'||fileType == 'xlt'||fileType == 'et'
        ||fileType == 'ett'||fileType == 'ppts'||fileType == 'pot'
        ||fileType == 'dps'||fileType == 'dpt'
        || fileType == 'xlsx'||fileType == 'txt'||fileType == 'pdf'
        ||fileType == 'ceb' ||fileType == 'ppt'|| fileType == 'pptx'){
        openWin("/preview/toShowPDF?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
    }else {
        openWin("/preview/toShowOthers?id=" + id + "&fileType=" + selectVal + "&keyWords=" + encodeURI(keyWords), "_blank");
    }
}
