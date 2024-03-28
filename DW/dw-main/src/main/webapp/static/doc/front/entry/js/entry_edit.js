var  fileArray=[];
var entryInfo={};
var imgurl=[];
var entryImgsList=[];
var entryInfoBarList=[];
var entryBodyList=[];
var num = 0;
var nums = 0;
var numss = [];
var ue;
//正文的editor
var ueNum=[]

layui.use(['jquery', 'layer', 'laytpl', 'Hussar','form','upload'], function () {
    var $ = layui.jquery,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        $ax = layui.HussarAjax,
        hussar = layui.Hussar,
        upload = layui.upload;
    var form = layui.form;

    var uploadInst = upload.render({
        elem: "#test1",  // 指向容器选择器，与id进行绑定
        url: Hussar.ctxPath+'/topic/upload',
        multiple: true,//多文件上传
        auto: false,//选择文件后不自动上传
        bindAction: '#entryDetermine' //指向一个按钮触发上传
        ,choose: function(obj){
            //将每次选择的文件追加到文件队列
            var files = obj.pushFile();
            //预读本地文件，如果是多文件，则会遍历。(不支持ie8/9)
            obj.preview(function(index, file, result){
                 //重命名文件名，layui 2.3.0 开始新增
                // fileArray.push(file.name);
                // console.log(fileArray);
                let imgType="."+file.type.slice(file.type.indexOf('/')+1,file.type.length)
                obj.resetFile(index, file, index+imgType);
                /*var tr = '<div class="image-container" id="container'+index+'"><img class="ig" id="showImg'+index+'"src="' + result + '" alt="' + index + ''+imgType+'">' +
                    '<img id="upload_img_'+index+'"src=/static/doc/front/entry/img/deleteUpload.png></div>';*/
                var tr = '<div class="image-container" id="container'+index+'"><img id="showImg'+index+'"src="' + result + '" alt="' + index + ''+imgType+'" class="getImgName">' +
                    '<img id="upload_img_'+index+'"src=/static/doc/front/entry/img/deleteUpload.png></div>';
                //这里还可以做一些 append 文件列表 DOM 的操作
                //obj.upload(index, file); //对上传失败的单个文件重新上传，一般在某个事件中使用

                $('.upimgshow').append(tr);
                console.log(tr)
                $(".image-container").on('click',"#upload_img_"+index ,function () {
                    delete files[index];
                    $("#showImg"+index).attr('alt')
                    debugger
                    for(let j=0;j<imgurl.length;j++){
                        if($("#showImg"+index).attr('alt')==imgurl[j].fileName){
                            imgurl.splice(j, 1)
                        }
                    }
                    $("#container"+index).remove();

                });
                $("#entryDetermine").on('click' ,function () {
                    debugger
                    delete files[index];
                    // $("#setImg").parent().parent().css('display','none')
                    layer.close(layer.index);
                    var entryImgNumber=$('.image-container').length;
                    $('.atlaNumber').text(entryImgNumber);
                    if(entryImgNumber>0){
                        $('#entryImg').attr('src',$(".getImgName")[0].src)
                    }else {
                        $('#entryImg').attr('src','/static/doc/front/entry/img/default.png')
                    }
                });
            });
        }
        ,done: function(res, index, upload){
            debugger
            console.log("res是",res)
            console.log("index是",index)
            imgurl.push(res);
            //如果上传失败
        },
        allDone: function(obj){//当文件全部被提交后，才触发
            console.log("全部文件上传完成后",obj)
            console.log(obj.total); //得到总文件数
            console.log(obj.successful); //请求成功的文件数
            console.log(obj.aborted); //请求失败的文件数
            },
        accept: 'images'//允许上传的文件类型
    });

    $(".editUploadText").on('click', function () {
        // openAdd('新增规则', '/integralRule/addView', 620, 450);
        var index=layer.open({
            type: 1,
            area: [685 + 'px', 450 + 'px'],
            title: '添加图册',
            content: $('#setImg')
        });
        $('.layui-layer-btn1').attr('id','uploadImg');

    });


    $("#entryDetermine").on('click' ,function () {
        // $("#setImg").parent().parent().css('display','none')
        layer.close(layer.index);
        var entryImgNumber=$('.image-container').length;
        //console.log("第一个图片的地址是",$(".ig")[0].src)
        $('.atlaNumber').text(entryImgNumber);
    });

    $(document).on("mouseenter",'#addinformationBar .infoBarContain',function(){
        $(this).children('img').removeClass('qwqw');
    });
    $(document).on("mouseleave",'#addinformationBar .infoBarContain',function(){
        $(this).children('img').addClass('qwqw');
        // $(this).children('img').hide()
    });

    $("#img").on('click', function () {
        // openAdd('新增规则', '/integralRule/addView', 620, 450);
        var index=layer.open({
            type: 1,
            area: [685 + 'px', 450 + 'px'],
            title: '添加图册',
            content: $('#setImg')
        });
        $('.layui-layer-btn1').attr('id','uploadImg');
    });
    //移除信息框
    $(document).on("click",'.infoBarContain .imgshow',function(){
        $(this).parent().remove();
    });
    // 移除正文文本框
    $(document).on("click",'.infoBarContainTitle .inin',function(){
        debugger
        console.log("this包括",$(this).attr("id"))
        numss.push(Number.parseInt($(this).attr("id")))
        $(this).parent().parent().remove();
        for (var i = 0; i < num; i++) {
            console.log(ueNum[i].getContent())

        }
        nums--;
    });

    $("#entryCancel").on('click' ,function () {
        layer.close(layer.index);
    });


    // $(document).on("focus",' .entryInfoValue',function($event){
    //     if($event.target.previousElementSibling.value==''){
    //         layer.alert("标题不能为空");
    //         $event.target.setAttribute('disabled','disabled')
    //     }
    //     $event.target.removeAttribute('disabled')
    // });
    // $('.mainBody').on("focus",'.mainBodyTextInfo>.infoBarContainContent',function($event){
    //     if($event.target.previousSibling.childNodes[1].value==''){
    //         layer.alert("标题不能为空");
    //         $event.target.setAttribute('disabled','disabled')
    //     }
    //     $event.target.removeAttribute('disabled')
    // });


    //上传
    /*$('#saveBtn').on('click', function (){
        if ($('#entryName').val() == "") {
            layer.alert("词条名不能为空");
            return;
        }
        if (ue.getContentTxt().trim() == "") {
            layer.alert("概述不能为空");
            return;
        }
        //获得图片路径
        imgurl.forEach(function (value,index) {
            console.log(value,'item');
            entryImgsList.push({'imgUrl':value.fName,'imgTitle':"图册"+(index+1),'showOrder':index+1});
        })
        console.log(imgurl,'编辑imgurl')
        // return;
        //获得信息栏内容
        var entryInfoLabel=$('.entryInfoLabel');
        var entryInfoValuecontent=$('.entryInfoValue');
        for (var i=0;i<$('.entryInfoValue').length;i++){

            if(entryInfoLabel[i].value!=''&&entryInfoValuecontent[i].value==''){
                var closeInfoLabel =layer.confirm('信息栏须全部有内容', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm',btn: ['确定'] }, function () {
                    layer.close(closeInfoLabel)
                })
                imgurl=[]
                entryInfoBarList=[]
                return;
            }
            if(entryInfoLabel[i].value==''&&entryInfoValuecontent[i].value!=''){
                var closeInfoLabel =layer.confirm('信息栏须全部有内容', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm',btn: ['确定'] }, function () {
                    layer.close(closeInfoLabel)
                })
                imgurl=[]
                entryInfoBarList=[]
                return;
            }
            if(entryInfoLabel[i].value!=''&&entryInfoValuecontent[i].value!=''){
                entryInfoBarList.push({'label':entryInfoLabel[i].value,'value':entryInfoValuecontent[i].value,'showOrder':i+1});
            }
        }
        //获得正文内容
        var entryBodyListTitle=$('.entryBodyListTitle');
        var infoBarContainContent=$('.infoBarContainContent');
        for (var i=0;i<$('.infoBarContainContent').length;i++){
            if(entryBodyListTitle[i].value!=''&&infoBarContainContent[i].value==''){
                var closeMainContent =layer.confirm('正文须全部有内容', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm',btn: ['确定'] }, function () {
                    layer.close(closeMainContent)
                })
                imgurl=[]
                entryInfoBarList=[]
                entryBodyList=[]
                return;
            }
            if(entryBodyListTitle[i].value==''&&infoBarContainContent[i].value!=''){
                var closeMainContent =layer.confirm('正文须全部有内容', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm',btn: ['确定'] }, function () {
                    layer.close(closeMainContent)
                })
                imgurl=[]
                entryInfoBarList=[]
                entryBodyList=[]
                return;
            }

            if(entryBodyListTitle[i].value!=''&&infoBarContainContent[i].value!=''){
                entryBodyList.push({'title':entryBodyListTitle[i].value,'content':infoBarContainContent[i].value,'showOrder':i+1});
            }
        }
        //获得标签内容
        var tag=$('.layui-form-checked>span');
        var tagTextContent=[];
        for (var i=0;i<tag.length;i++){
            tagTextContent.push(tag[i].textContent);
        }

        var entryName={};
        entryInfo={'id':$('#entryId').val(),'name':$('#entryName').val(),'summary':ue.getContent(),'summaryText':ue.getContentTxt().trim(),'tag': tagTextContent.toString()};

        entryName.entryInfo=entryInfo;
        entryName.entryImgsList=entryImgsList;
        entryName.entryInfoBarList=entryInfoBarList;
        entryName.entryBodyList=entryBodyList;

        var entryNameJson= JSON.stringify(entryName);
        console.log('entryNameJson',entryNameJson);
        $.ajax({
            type: 'POST',
            url: Hussar.ctxPath + '/entry/updateEntryInfo',
            contentType:'application/json',
            dataType: 'json',
            data: entryNameJson,
            success: function (data) {
                if (data.code == "1") {
                    layer.alert(data.msg, {title: ['提示', 'background-color:#fff'], skin: 'move-confirm'}, function () {
                        returnList();
                    })
                } else {
                    layer.alert(data.msg, {title: ['提示', 'background-color:#fff'], skin: 'move-confirm'}, function () {
                        returnList();
                    })
                }
            },
            error: function (data) {
                layer.alert("新增词条出错", {title: ['提示', 'background-color:#fff'], skin: 'move-confirm'}, function () {
                    returnList();
                })
            }
        });
    });*/

    //上传
    $('#saveBtn').on('click', function (){
        if ($('#entryName').val() == "") {
            var closeEntryName =layer.confirm('词条名不能为空', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm',btn: ['确定'] }, function () {
                layer.close(closeEntryName)
            })
            return;
        }
        if (ue.getContentTxt().trim() == "") {
            var closeSummaryName =layer.confirm('概述不能为空', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm',btn: ['确定'] }, function () {
                layer.close(closeSummaryName)
            })
            return;
        }

        //获得图片路径
        imgurl.forEach(function (value,index) {
            console.log(value.fName,'item');
            entryImgsList.push({'imgUrl':value.fName,'imgTitle':"图册"+(index+1),'showOrder':index+1});
        })

        console.log(entryImgsList,'entryImgsList')
        //获得信息栏内容
        var entryInfoLabel=$('.entryInfoLabel');
        var entryInfoValuecontent=$('.entryInfoValue');
        for (var i=0;i<$('.entryInfoValue').length;i++){

            if(entryInfoLabel[i].value!=''&&entryInfoValuecontent[i].value==''){
                var closeInfoLabel =layer.confirm('信息栏须全部有内容', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm',btn: ['确定'] }, function () {
                    layer.close(closeInfoLabel)
                })
                imgurl=[]
                entryInfoBarList=[]
                return;
            }
            if(entryInfoLabel[i].value==''&&entryInfoValuecontent[i].value!=''){
                var closeInfoLabel =layer.confirm('信息栏须全部有内容', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm',btn: ['确定'] }, function () {
                    layer.close(closeInfoLabel)
                })
                imgurl=[]
                entryInfoBarList=[]
                return;
            }
            if(entryInfoLabel[i].value!=''&&entryInfoValuecontent[i].value!=''){
                entryInfoBarList.push({'label':entryInfoLabel[i].value,'value':entryInfoValuecontent[i].value,'showOrder':i+1});
            }
        }

        //获取正文
        var entryBodyListTitle = $('.entryBodyListTitle');
        for (var i=0;i<entryBodyListTitle.length;i++){
            entryBodyListTitle[i].id=i
        }
        for (var i=0;i<num;i++){
            console.log( numss.indexOf(i))
            var n = numss.indexOf(i)
            if(n != -1){
                continue;
            }
            if(ueNum[i].getContentTxt().trim()==''){
                var closeMainContent =layer.confirm('正文须全部有内容', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm',btn: ['确定'] }, function () {
                    layer.close(closeMainContent)
                })
                imgurl=[]
                entryInfoBarList=[]
                entryBodyList=[]
                return;
            }
            if(ueNum[i].getContentTxt().trim()!=''){
                var titleId = ueNum[i].key;
                var titleValue = $('#'+titleId).parent().children(':first').children('input').val();
                var showOrder = $('#'+titleId).parent().children(':first').children('input').attr('id');
                entryBodyList.push({'title':titleValue,'content':ueNum[i].getContent(),'showOrder':showOrder});
            }
        }
        var isTrue = true;
        for (var i = 0; i < entryBodyListTitle.length; i++) {
            if (entryBodyListTitle[i].value == ''){
                var closeMainContent =layer.confirm('正文须全部有内容', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm',btn: ['确定'] }, function () {
                    layer.close(closeMainContent)
                })
                imgurl=[]
                entryInfoBarList=[]
                entryBodyList=[]
                isTrue = false;
                return;
            }

        }

        //获得标签内容
        var tag=$('.layui-form-checked>span');
        var tagTextContent=[];
        for (var i=0;i<tag.length;i++){
            tagTextContent.push(tag[i].textContent);
        }

        var entryName={};
        entryInfo={'id':$('#entryId').val(),'name':$('#entryName').val(),'summary':ue.getContent(),'summaryText':ue.getContentTxt().trim(),'tag': tagTextContent.toString()};
        debugger
        entryName.entryInfo=entryInfo;
        entryName.entryImgsList=entryImgsList;
        entryName.entryInfoBarList=entryInfoBarList;
        entryName.entryBodyList=entryBodyList;

        var entryNameJson= JSON.stringify(entryName);
        console.log('entryNameJson',entryNameJson);
        $.ajax({
            type: 'POST',
            url: Hussar.ctxPath + '/entry/updateEntryInfo',
            contentType:'application/json',
            dataType: 'json',
            data: entryNameJson,
            success: function (data) {
                if (data.code == "1") {
                    layer.alert(data.msg, {title: ['提示', 'background-color:#fff'], skin: 'move-confirm'}, function () {
                        returnList();
                    })
                } else {
                    layer.alert(data.msg, {title: ['提示', 'background-color:#fff'], skin: 'move-confirm'}, function () {
                        returnList();
                    })
                }
            },
            error: function (data) {
                layer.alert("新增词条出错", {title: ['提示', 'background-color:#fff'], skin: 'move-confirm'}, function () {
                    returnList();
                })
            }
        });
    });
    //页面初始化
    $(function () {
        // editor的使用
        ue = UE.getEditor('editor',{textarea: 'editorValue'});
        //获取字典
        getDic();
        //页面赋值
        initData();
        form.render();
    });

})

//评论框字数显示及控制
function wordLeg(obj) {
    var currleg = $(obj).val().length;
    var btnObj = $(obj).parent().parent('.comment-input-container').children('.submit-comment-btn');
    var preObj = $(obj).parent().children('pre');
    if(currleg === 0){
        btnObj.attr('disabled',true);
    }else{
        btnObj.attr('disabled',false);
    }
    var length = $(obj).attr('maxlength');
    if (currleg > length) {
        layer.msg('字数请在' + length + '字以内');
    } else {
        $(obj).parent().children('.word').children('.text_count').text(currleg);
    }
    // preObj[0].innerHTML = $(obj)[0].value;
}



//增加信息
function addInfo(){
    var addinfomain = " <div class='infoBarContain'><input type='text'  placeholder='请输入' maxlength='50' " +
        " onpropertychange='if(value.length>50) value=value.substr(0,50)' name='desc' class='layui-input infoBarContainTitle entryInfoLabel' " +
        " autocomplete='off' > <input type='text' placeholder='请输入' maxlength='50'  " +
        " onpropertychange='if(value.length>50) value=value.substr(0,50)' name='desc' " +
        " class='layui-input infoBarContainFill entryInfoValue' autocomplete='off'> <img src='/static/doc/front/entry/img/delete.png'" +
        " alt='' class='qwqw imgshow'></div>";
    $('#addinformationBar').append(addinfomain);
}

// 增加正文
function addMainBody(){
    debugger
    num++;
    nums++;
    var addMainBodyinfo = "<div class='mainBodyTextInfo'><div class='infoBarContainTitle'>" +
        " <input type='text' placeholder='请输入' maxlength='50' onpropertychange='if(value.length>50) value=value.substr(0,50)'" +
        " name='desc' class='layui-input entryBodyListTitle' autocomplete='off'><img id='" + (num - 1) + "' src='/static/doc/front/entry/img/delete.png'" +
        " alt='' class='inin'><button type='button' class='layui-btn layui-btn-primary' onclick='addMainBody2($(this))'>添加信息项</button></div>" +
        "<script class='infoBarContainContent' id='" + "infoBarContainContent"+ (num - 1) + "' type='text/plain'></script></div> ";
    $('.infobutton').before(addMainBodyinfo);
    ueNum[(num-1)] = UE.getEditor('infoBarContainContent' + (num-1),{textarea: 'editorValue'});

}

function addMainBody2(thi){
    num++;
    nums++;
    var addMainBodyinfo = "<div class='mainBodyTextInfo'><div class='infoBarContainTitle'>" +
        " <input type='text' placeholder='请输入' maxlength='50' onpropertychange='if(value.length>50) value=value.substr(0,50)'" +
        " name='desc' class='layui-input entryBodyListTitle' autocomplete='off'><img id='" + (num - 1) + "' src='/static/doc/front/entry/img/delete.png'" +
        " alt='' class='inin'><button type='button' class='layui-btn layui-btn-primary' onclick='addMainBody2($(this))'>添加信息项</button></div>" +
        "<script class='infoBarContainContent' id='" + "infoBarContainContent"+ (num - 1) + "' type='text/plain'></script></div> ";
    console.log(thi.parent().next().attr("id"))
    //thi指的是当前点击事件的$(this)
    var ueId = thi.parent().next().attr("id")
    $('#' + ueId).parent().after(addMainBodyinfo)
    ueNum[(num-1)] = UE.getEditor('infoBarContainContent' + (num-1),{textarea: 'editorValue'});
}

//返回词条列表
function returnList(){
    location.href = '/entry/entryList';
    // document.getElementById("entry").href = "/entry/entryAdd";
}

//获取字典数据
function getDic(){
    layui.use(['Hussar','HussarAjax'], function() {
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/dicList", function(result) {
            for (var i = 0; i < result.length; i++) {
                var param = '<input name="state2" title="' +result[i].LABEL+ '" value="' +result[i].LABEL+ '" ' +
                    ' type="checkbox"  lay-skin="primary" lay-filter="state2">';
                $("#tagDiv").append(param);
            }
        }, function(data) {

        });
        ajax.set("dicType","tag_entry");
        ajax.start();
    });
}
//页面数据初始化
function initData() {
    layui.use(['Hussar','HussarAjax'], function() {
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/entry/getEntryInfoDetailById", function(data) {
            console.log("后台获取的数据",data)
            if (data.code == '1') {
                var entryInfo = data.data.entryInfo;//词条信息
                var entryImgsList = data.data.entryImgsList;//词条图册
                console.log(entryImgsList,'entryImgsList')
                var entryInfoBarList = data.data.entryInfoBarList;//词条信息栏
                var entryBodyList = data.data.entryBodyList;//词条正文

                $("#entryName").val(entryInfo.name);//词条名

                ue.ready(function(){
                    ue.setContent(entryInfo.summary);//概述
                })

                var tagArr = entryInfo.tag.split(",");//标签
                for(var i=0;i<tagArr.length;i++){
                    $("input[name='state2']").each(function () {
                        if($(this).val() == tagArr[i]) {
                            $(this).attr("checked","checked");
                        }
                    })
                }
                for (var i=0;i<entryInfoBarList.length;i++) {
                    var addinfomain = " <div class='infoBarContain'><input type='text'  placeholder='请输入' maxlength='50' " +
                        " onpropertychange='if(value.length>50) value=value.substr(0,50)' name='desc' class='layui-input infoBarContainTitle entryInfoLabel' " +
                        " autocomplete='off' value='" + entryInfoBarList[i].label + "' > <input type='text' placeholder='请输入' maxlength='50'  " +
                        " onpropertychange='if(value.length>50) value=value.substr(0,50)' name='desc' value='" + entryInfoBarList[i].value + "'" +
                        " class='layui-input infoBarContainFill entryInfoValue' autocomplete='off'> <img src='/static/doc/front/entry/img/delete.png'" +
                        " alt='' class='qwqw imgshow'></div>";
                    $('#addinformationBar').append(addinfomain);
                }

                for (var i=0;i<entryBodyList.length;i++) {
                    var addMainBodyinfo = "<div class='mainBodyTextInfo'><div class='infoBarContainTitle'>" +
                        " <input type='text' placeholder='请输入' maxlength='50' onpropertychange='if(value.length>50) value=value.substr(0,50)'" +
                        " name='desc' value='" + entryBodyList[i].title  + "' class='layui-input entryBodyListTitle' autocomplete='off'>" +
                        "<img id='" + i + "' src='/static/doc/front/entry/img/delete.png'" +
                        " alt='' class='inin'><button type='button' class='layui-btn layui-btn-primary' onclick='addMainBody2($(this))'>添加信息项</button></div>" +
                        "<script class='infoBarContainContent' id='" + "infoBarContainContent"+ i + "' type='text/plain'></script></div> ";
                    $('.infobutton').before(addMainBodyinfo);
                }

                num = entryBodyList.length;
                nums = entryBodyList.length;
                for (var i = 0; i < entryBodyList.length; i++) {
                    ueNum[i] = UE.getEditor('infoBarContainContent' + i,{textarea: 'editorValue'});
                    ueNum[i].ready(function() {
                        debugger
                        for (var j = 0; j < entryBodyList.length; j++) {
                            ueNum[j].setContent(entryBodyList[j].content)
                        }
                    })
                }

                //图册渲染
                for (var i=0;i<entryImgsList.length;i++) {
                    var tr = '<div class="image-container" id="container'+i+'"><img id="showImg'+i+'"src="/preview/list?fileId=' + entryImgsList[i].imgUrl.replace(/\\/g,'/') + '" alt="' + entryImgsList[i].id + '" class="getImgName">' +
                        '<img id="upload_img_'+i+'"src=/static/doc/front/entry/img/deleteUpload.png onclick="removeImg(event,'+i+')"></div>';
                    $('.upimgshow').append(tr);
                    imgurl.push({fName:entryImgsList[i].imgUrl,fileName:entryImgsList[i].id})
                }
                $('.atlaNumber').text(entryImgsList.length)
                var entryImgNumber=$('.image-container').length;
                if(entryImgNumber>0){
                    $('#entryImg').attr('src',$('.getImgName')[0].src)
                }else {
                    $('#entryImg').attr('src','/static/doc/front/entry/img/default.png')
                }

            } else {
               layer.msg(data.msg);
            }
        }, function(data) {
            layer.msg("获取词条详细信息失败")
        });
        ajax.set("id", $("#entryId").val());
        ajax.start();
    });

}
function removeImg(e,i){
    console.log(i,'i')
    $("#showImg"+i).attr('alt')
    for(let j=0;j<imgurl.length;j++){
        if($("#showImg"+i).attr('alt')==imgurl[j].fileName){
            imgurl.splice(j, 1)
        }
    }
    e.target.parentNode.parentNode.removeChild(e.target.parentNode)
}