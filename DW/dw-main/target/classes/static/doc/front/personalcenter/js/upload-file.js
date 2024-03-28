/**
 * Created by smt on 2018/6/30.
 */
var filesFlag = false; // 是否是多文件上传
var returnDocId = ''; // 重名文件跳转的文档id
layui.extend({
    admin: '{/}../../../static/resources/weadmin/static/js/admin'
});
/**
 * 初始化上传组件
 */
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
    var $ = layui.jquery,
        form = layui.form,
        jstree = layui.jstree,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;
    hussar = layui.Hussar;

    /**************** 添加代码开始 *****************/
    var renameObj = {}; // 重名的文件对象
    var uploadObj = {}; // 当前正在上传的文件
    /**************** 添加代码结束 *****************/

    var BreakpointUpload = {
        layerIndex: -1
    };
    BreakpointUpload.initUploader = function () {
        //初始变量定义
        chooseFile = [];
        var dropZone = document.getElementById("dndArea");
        var shadow = $(".shadow")[0];
        dropZone.addEventListener("dragover", function (e) {
            $(".popWinNew *").css("pointer-events","none");
            $(".popWinNew .delete").css("pointer-events","none");
            $(".fast-button").show();
            $(".shadow").show();
            $("#dndArea").css("opacity", "0");
            $("#dragArea").css("pointer-events","none");
        }, false);
        dropZone.addEventListener("drop", function (e) {
            $(".popWinNew .delete").css("pointer-events","auto");
            $("#thelist .item p").css("pointer-events","auto");
        }, false);
        dropZone.addEventListener("dragleave", function (e) {
            $(".fast-button").hide();
            $(".shadow").hide();
            $("#dndArea").css("opacity", "1");
            $("#dragArea").css("pointer-events","auto");
            $(".popWinNew .delete").css("pointer-events","auto");
        }, false);
        dropZone.addEventListener("dragend", function (e) {
            $("#thelist .item p").css("pointer-events","auto");
        }, false);
        // 监听分块上传过程中的三个时间点
        WebUploader.Uploader.register({
                "before-send-file": "beforeSendFile",//整个文件上传前
                "before-send": "beforeSend",  //每个分片上传前
                "after-send-file": "afterSendFile"  //分片上传完毕
            },
            {
                //时间点1：所有分块进行上传之前调用此函数
                beforeSendFile: function (file) {
                    uploadObj = file;
                    $("#dndArea").css("opacity", "1");
                    $(".shadow").hide();
                    $(".webuploader-pick").parent().show();
                    fileId = file.id;
                    if (categoryId == undefined) {
                        $(".popWinNew").css("display", "none");
                        return;
                    }
                    // if (noChildPower == 0 && adminFlag != 1) {
                    //     $(".popWin").css("display", "none")
                    //     return;
                    // }

                    $(".popWinNew").css("display", "block")
                    $("#continueUpload").show();
                    $(".dragArea").css("display", "none");
                    $(".upload-tip").css("display", "none");


                    var deferred = WebUploader.Deferred();
                    //1、计算文件的唯一标记fileMd5，用于断点续传  如果.md5File(file)方法里只写一个file参数则计算MD5值会很慢 所以加了后面的参数：10*1024*1024
                    (new WebUploader.Uploader()).md5File(file, 0, 1000 * 1024 * 1024).progress(function (percentage) {
                        percentageFlag = percentage;
                        $('#' + file.id).find('p.state').text('读取中...');
                        $('#' + file.id).find('.btnRemoveFile').removeClass("delete");

                    })
                        .then(function (val) {

                            $('#' + file.id).find("p.state").text("读取成功...");
                            fileMd5 = val;
                            fileName = file.name; //为自定义参数文件名赋值
                            var folderId=categoryId;
                            // if(file.source.source.isDir!='1'){
                            //     folderId=realcategoryId[isdir];
                            //     isdir= isdir+1
                            // }
                            if(file.source.source.folderId!=undefined){
                                folderId=file.source.source.folderId;
                            }
                            /*$.ajax({
                                    type: "post",
                                    url: Hussar.ctxPath+"/breakpointUpload/checkMd5Exist",
                                    data: {
                                        fileName: fileName,
                                        categoryId: folderId,
                                        visible: "0",
                                        downloadAble: "0",
                                        watermarkUser: "",
                                        watermarkCompany: "",
                                        fileMd5: fileMd5,
                                        group: "",
                                        person: "",
                                        shareable:shareable
                                    },
                                    contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                                    async: false,
                                    cache: false,
                                    dataType: "json",
                                    success: function (data) {
                                        if (data.code == '2') {

                                            fastFlag = 1;
                                        }
                                        if (data.code == '4') {

                                            fastFlag = 2;
                                            returnDocId = data.docId;
                                        }
                                        if (data.code == '6') {

                                            fastFlag = 6;
                                        }
                                        if (data.code == '7') {

                                            fastFlag = 7;
                                        }
                                        if (data.code == '8') {

                                            fastFlag = 8;
                                        }
                                        if (data.code == '5') {
                                            fastFlag = 0;
                                            chooseUploadFile.push(data.id);
                                            chooseUploadAuthor.push(data.authorId);
                                        }
                                    }
                                }

                            );*/
                            /***************** 修改代码开始 ******************/
                            if(!uploadObj.zdyFlag){
                                var ajax = new $ax(Hussar.ctxPath + "/breakpointUpload/checkMd5Exist", function(data) {
                                    if (data.code == '2') {

                                        fastFlag = 1;
                                    }
                                    if (data.code == '4') {

                                        fastFlag = 2;
                                        returnDocId = data.docId;
                                        renameObj[file.id] = data.docId;
                                    }
                                    if (data.code == '6') {

                                        fastFlag = 6;
                                    }
                                    if (data.code == '7') {

                                        fastFlag = 7;
                                    }
                                    if (data.code == '8') {

                                        fastFlag = 8;
                                    }
                                    if (data.code == '5') {
                                        fastFlag = 0;
                                        chooseUploadFile.push(data.id);
                                        chooseUploadAuthor.push(data.authorId);
                                    }
                                    if (data.code == '10') {
                                        fastFlag = 10;
                                        returnDocId = data.docId;
                                    }
                                }, function(data) {

                                });
                                ajax.set("fileName",fileName);
                                ajax.set("categoryId",folderId);
                                ajax.set("visible","0");
                                ajax.set("downloadAble","0");
                                ajax.set("watermarkUser","");
                                ajax.set("watermarkCompany","");
                                ajax.set("fileMd5",fileMd5);
                                ajax.set("group","");
                                ajax.set("person","");
                                ajax.set("shareable",shareable);
                                ajax.set("fileOpen",fileOpen);
                                ajax.start();
                            }else{
                                var ajax = new $ax(Hussar.ctxPath + "/breakpointUpload/checkVersionMd5Exist", function(data) {
                                    if (data.code == '2') {

                                        fastFlag = 1;
                                    }
                                    if (data.code == '4') {

                                        fastFlag = 2;
                                    }
                                    if (data.code == '6') {

                                        fastFlag = 6;
                                    }
                                    if (data.code == '7') {

                                        fastFlag = 7;
                                    }
                                    if (data.code == '8') {

                                        fastFlag = 8;
                                    }
                                    if (data.code == '9') {

                                        fastFlag = 9;
                                    }
                                    if (data.code == '5') {
                                        fastFlag = 0;
                                        chooseUploadFile.push(data.id);
                                        chooseUploadAuthor.push(data.authorId);
                                        newDocId = data.id;
                                    }
                                }, function(data) {

                                });
                                ajax.set("fileName",fileName);
                                ajax.set("categoryId",categoryId);
                                ajax.set("visible","0");
                                ajax.set("downloadAble","0");
                                ajax.set("watermarkUser","");
                                ajax.set("watermarkCompany","");
                                ajax.set("fileMd5",fileMd5);
                                ajax.set("group","");
                                ajax.set("person","");
                                ajax.set("shareable",'1');
                                ajax.set("isVersion","1");
                                ajax.set("oldDocId",renameObj[uploadObj.id]);
                                ajax.start();
                            }
                            /***************** 修改代码结束 ******************/
                            //获取文件信息后进入下一步
                            deferred.resolve();
                        });

                    return deferred.promise();

                },
                //时间点2：如果有分块上传，则每个分块上传之前调用此函数
                beforeSend: function (block) {
                    if (fastFlag == 0 || fastFlag == 1 || fastFlag == 2 || fastFlag == 6 || fastFlag == 7 || fastFlag == 8 || fastFlag == 10 || fastFlag == 9) {
                        return;
                    }
                    if (categoryId == undefined) {

                        return;
                    }
                    // if(noChildPower==0&&adminFlag!=1){
                    //     return;
                    // }
                    var deferred = WebUploader.Deferred();
                    //ajax验证每一个分片
                    /***************** 修改代码开始 ******************/
                    if(!uploadObj.zdyFlag){
                        var ajax = new $ax(Hussar.ctxPath+"/breakpointUpload/mergeOrCheckChunks", function (data) {
                                var res = eval('(' + data + ')');
                                if (res.ifExist) {
                                    //分块存在，跳过
                                    deferred.reject();
                                } else {
                                    //分块不存在或不完整，重新发送该分块内容
                                    deferred.resolve();
                                }
                            },
                            function (data) {
                            });
                        ajax.set("param", "checkChunk");
                        ajax.set("fileName", fileName);
                        ajax.set("jindutiao", $("#jindutiao").val());
                        ajax.set("fileMd5", fileMd5);//文件唯一标记
                        ajax.set("chunk", block.chunk);//当前分块下标
                        ajax.set("chunkSize", block.end - block.start);//当前分块大小
                        ajax.set("shareable",shareable);// 分享标识
                        ajax.set("fileOpen",fileOpen);
                        ajax.start();
                    }else{
                        var ajax = new $ax(Hussar.ctxPath+"/breakpointUpload/mergeOrCheckVersionChunks", function (data) {
                                var res = eval('(' + data + ')');
                                if (res.ifExist) {
                                    //分块存在，跳过
                                    deferred.reject();
                                } else {
                                    //分块不存在或不完整，重新发送该分块内容
                                    deferred.resolve();
                                }
                            },
                            function (data) {
                            });
                        ajax.set("param", "checkChunk");
                        ajax.set("fileName", fileName);
                        ajax.set("jindutiao", $("#jindutiao").val());
                        ajax.set("fileMd5", fileMd5);//文件唯一标记
                        ajax.set("chunk", block.chunk);//当前分块下标
                        ajax.set("chunkSize", block.end - block.start);//当前分块大小
                        ajax.set("shareable",'1');// 分享标识
                        ajax.set("oldDocId",renameObj[uploadObj.id]);// 旧版本文档的ID
                        ajax.start();
                    }
                    /***************** 修改代码结束 ******************/

                    this.owner.options.formData.fileMd5 = fileMd5;

                    deferred.resolve();

                    return deferred.promise();
                },
                //时间点3：所有分块上传成功后调用此函数
                afterSendFile: function (file) {
                    fileId = file.id;
                    if (categoryId == undefined) {

                        powerFlag = 1;
                        $('#' + fileId).remove();
                        uploader.removeFile(fileId, true);
                        count++
                        return;
                    }
                    // if (noChildPower == 0 && adminFlag != 1) {
                    //     powerFlag = 1;
                    //     uploader.removeFile(fileId, true);
                    //     $('#' + fileId).remove();
                    //     count++
                    //     return;
                    // }
                    powerFlag = 0;
                    if (fastFlag == 0 || fastFlag == 1 || fastFlag == 2 || fastFlag == 6 || fastFlag == 7 || fastFlag == 8 || fastFlag == 10 || fastFlag == 9) {

                        count++; //每上传完成一个文件 count+1
                        if (count > filesArr.length - 1) {

                        } else {
                            uploader.upload(filesArr[count].id);//上传文件列表中的下一个文件
                        }
                        return;
                    }

                    var code = 0;
                    $('#' + fileId).find('p.state').text('转化中')
                    //如果分块上传成功，则通知后台合并分块
                    /*********************** 修改代码开始 ***********************/
                    if(!uploadObj.zdyFlag){
                        var ajax = new $ax(Hussar.ctxPath+"/breakpointUpload/mergeOrCheckChunks",
                            function (data) {
                                count++; //每上传完成一个文件 count+1
                                if (data != "") {
                                    dataNew = eval('(' + data + ')')
                                    if (dataNew.code == 3) {
                                        code = 3;
                                    }
                                    if (dataNew.code == 2) {
                                        code = 2;
                                    }
                                    if (code == 0) {
                                        chooseUploadFile.push(dataNew.id);
                                        chooseUploadAuthor.push(dataNew.authorId);
                                    }
                                }
                                if (code != 3 && code != 2&& count <= filesArr.length - 1) {
                                    uploader.upload(filesArr[count].id);//上传文件列表中的下一个文件
                                } else {
                                    // 合并成功之后的操作

                                    if (code == 2) {
                                        flag = 4;


                                    }
                                    if (code == 3) {
                                        flag = 3;

                                    }
                                }
                            },
                            function (data) {
                            });

                        var folderId=categoryId;
                        // if(file.source.source.isDir!='1'){
                        //     folderId=realcategoryId[isdir];
                        //     isdir= isdir+1
                        // }
                        if(file.source.source.folderId!=undefined){
                            folderId=file.source.source.folderId;
                        }
                        ajax.set("categoryId", folderId);
                        ajax.set("visible", "0");
                        ajax.set("downloadAble", "0");
                        ajax.set("watermarkUser", "");
                        ajax.set("watermarkCompany", "");
                        ajax.set("group", "");
                        ajax.set("person", "");
                        ajax.set("param", "mergeChunks");
                        ajax.set("fileName", fileName);
                        ajax.set("fileMd5", fileMd5);
                        ajax.set("shareable",shareable);
                        ajax.set("fileOpen",fileOpen);
                        ajax.start();
                    }else{
                        var ajax = new $ax(Hussar.ctxPath+"/breakpointUpload/mergeOrCheckVersionChunks",
                            function (data) {
                                count++; //每上传完成一个文件 count+1
                                if (data != "") {
                                    dataNew = eval('(' + data + ')')
                                    if (dataNew.code == 3) {
                                        code = 3;
                                    }
                                    if (dataNew.code == 2) {
                                        code = 2;
                                    }
                                    if (code == 0) {
                                        chooseUploadFile.push(dataNew.id);
                                        chooseUploadAuthor.push(dataNew.authorId);
                                    }
                                }
                                if (code != 3 && count <= filesArr.length - 1) {
                                    uploader.upload(filesArr[count].id);//上传文件列表中的下一个文件
                                } else {
                                    // 合并成功之后的操作

                                    if (code == 2) {
                                        flag = 4;


                                    }
                                    if (code == 3) {
                                        flag = 3;

                                    }
                                }
                            },
                            function (data) {
                            });

                        ajax.set("categoryId", categoryId);
                        ajax.set("visible", "0");
                        ajax.set("downloadAble", "0");
                        ajax.set("watermarkUser", "");
                        ajax.set("watermarkCompany", "");
                        ajax.set("group", "");
                        ajax.set("person", "");
                        ajax.set("param", "mergeChunks");
                        ajax.set("fileName", fileName);
                        ajax.set("fileMd5", fileMd5);
                        ajax.set("shareable",'1');
                        ajax.set("oldDocId",renameObj[file.id]);// 旧版本文档的ID
                        ajax.start();
                    }
                    /*********************** 修改代码结束 ***********************/
                }
            });//监听结束

        uploader = WebUploader.create({
            auto: true, //是否自动上传
            pick: {
                id: '#picker',
                label: '选择文件',
                multiple: true
            },
            duplicate: false, //同一文件是否可重复选择
            prepareNextFile: false,
            // 不压缩image
            resize: false,
            accept: {
                title: 'Files',
                extensions: '*',
                mimeTypes: '*'

            },
            compress: null,//图片不压缩
            chunked: true, //分片
            chunkSize: 50 * 1024 * 1024, //每片10M
            chunkRetry: 3,//如果失败，则不重试
            threads: 1,//上传并发数。允许同时最大上传进程数。
            fileNumLimit: 1000,//验证文件总数量, 超出则不允许加入队列
            fileSizeLimit: 6 * 1024 * 1024 * 1024,//6G 验证文件总大小是否超出限制, 超出则不允许加入队列
            fileSingleSizeLimit: 6 * 1024 * 1024 * 1024,  //3G 验证单个文件大小是否超出限制, 超出则不允许加入队列
            // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
            disableGlobalDnd: true,
            dnd: "#dndArea",
            // swf文件路径
            swf: '${ctxPath}/static/assets/lib/webuploader0.1.5/Uploader.swf',
            // 文件接收服务端。
            server: Hussar.ctxPath+"/breakpointUpload/fileSave"
        });

        /**
         *  当有文件添加进来的时候
         */
        uploader.on('fileQueued', function (file) {
            // 上传文件过程 禁用完成按钮
            $("#newFolder").attr("disabled",true);
            $("#newFolder").addClass("disabled-button");

            $(".btns").css("pointer-events","auto");
            //限制单个文件的大小 超出了提示
            if (file.size > 6 * 1024 * 1024 * 1024) {
                alert("单个文件大小不能超过3G！");
                return false;
            }
            filesArr.push(file);
            // var fileType = "." + file.name.toString().split(".")[1];
            var fileType ="." + file.ext;
            var imgSrc;
            console.log(file);
            if(fileType === '.folder'){
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-folder15.png";
            }else if(fileType === '.doc'||fileType === '.docx'){
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-word15.png";
            }else if(fileType === '.txt'){
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-text15.png";
            }else if(fileType === '.ppt'||fileType === '.pptx'||fileType === '.ppsx'){
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ppt15.png";
            }else if(fileType === '.pdf'){
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-pdf15.png";
            }else if(fileType === '.ceb'){
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-ceb15.png";
            }else if(['.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(fileType)!=-1){
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-audio15.png";
            }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(fileType)!=-1){
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-video15.png";
            }else if(fileType === '.xls'||fileType === '.xlsx') {
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-excel15.png";
            }else if(['.png','.jpeg','.gif','.jpg'].indexOf(fileType)!=-1) {
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-img15.png";
            }else if(['.bmp'].indexOf(fileType)!=-1) {
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-bmp15.png";
            }else if(['.psd'].indexOf(fileType)!=-1) {
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-psd15.png";
            }else if(['.html'].indexOf(fileType)!=-1) {
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-html15.png";
            }else if(['.exe'].indexOf(fileType)!=-1) {
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-exe15.png";
            }else if(['.zip','.rar'].indexOf(fileType)!=-1) {
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-zip15.png";
            }else {
                imgSrc=Hussar.ctxPath+"/static/resources/img/front/file-icon/ic-other15.png";
            }
            success++;
            var ajax = new $ax(Hussar.ctxPath+"/breakpointUpload/selectProgressByFileName",
                function (data) {
                    var res = eval('(' + data + ')');
                    var name="";
                    if(file.source.source.folderName!=undefined){
                        name=file.source.source.folderName;
                    }
                    if(name==""){
                        name= $("#selectFolder .folderinner").html();
                    }
                    //上传过程
                    if (res.jindutiao > 0) {
                        //上传过的进度的百分比
                        oldJindu = res.jindutiao / 100;
                        //如果上传过 上传了多少


                        var jindutiaoStyle = "width:" + res.jindutiao + "%";
                        /******************* 删除代码开始 ******************/
                        // $list.append('<div id="' + file.id + '" class="item">' +
                        //     '<p class="info" style="width: 50%"  title="' + file.name + '">'+'<img title="' +fileType + '" src="' + imgSrc + '">' + file.name + '</p>' +
                        //     '<p class="info" style="width: 24%" title="' +name + '">' + name + '</p>' +
                        //     '<p class="state" style="width: 14%">已上传' + res.jindutiao + '%</p>' +
                        //     '<a href="javascript:void(0);"  style="float: left;width: 2% !important;pointer-events: auto;margin-left: 5%;" class=" delete btnRemoveFile"></a>' +
                        //     '</div>');
                        /******************* 删除代码结束 ******************/

                        /******************* 添加代码开始 ******************/
                        $list.append('<div id="' + file.id + '" class="item">' +
                            '<p class="info" style="width: 40%"  title="' + file.name + '">'+'<img title="' +fileType + '" src="' + imgSrc + '">' + file.name + '</p>' +
                            '<p class="info" style="width: 24%" title="' +name + '">' + name + '</p>' +
                            '<p class="state" style="width: 20%">已上传' + res.jindutiao + '%</p>' +
                            '<a href="javascript:void(0);"  style="float: left;width: 2% !important;pointer-events: auto;margin-left: 7%;" class=" delete btnRemoveFile"></a>' +
                            '</div>');
                        /******************* 添加代码结束 ******************/
                        //将上传过的进度存入map集合
                        map[file.id] = oldJindu;
                    } else {//没有上传过
                        /******************* 删除代码开始 ******************/
                        // $list.append('<div id="' + file.id + '" class="item">' +
                        //     '<p class="info" style="width: 50%"  title="' + file.name + '"><img title="' +fileType + '" src="' + imgSrc + '">' + file.name + '</p>' +
                        //     '<p class="info" style="width: 24%" title="' + name + '"> ' + name + '</p>' +
                        //     '<p class="state" style="width: 14%">等待上传...</p>' +
                        //     '<a style="float: left;width: 2% !important;pointer-events: auto;margin-left: 5%;" href="javascript:void(0);" class=" delete btnRemoveFile"></a>' +
                        //     '</div>');
                        /******************* 删除代码结束 ******************/

                        /******************* 添加代码开始 ******************/
                        $list.append('<div id="' + file.id + '" class="item">' +
                            '<p class="info" style="width: 40%"  title="' + file.name + '"><img title="' +fileType + '" src="' + imgSrc + '">' + file.name + '</p>' +
                            '<p class="info" style="width: 24%" title="' + name + '"> ' + name + '</p>' +
                            '<p class="state" style="width: 20%">等待上传...</p>' +
                            '<a style="float: left;width: 2% !important;pointer-events: auto;margin-left: 7%;" href="javascript:void(0);" class=" delete btnRemoveFile"></a>' +
                            '</div>');
                        /******************* 添加代码结束 ******************/
                    }
                },
                function (data) {

                });
            ajax.set("fileName", file.name);     //文件名
            ajax.start();

            uploader.stop(true);
            //删除队列中的文件
            $(".btnRemoveFile").bind("click", function () {
                var fileItem = $(this).parent();
                uploader.removeFile($(fileItem).attr("id"), true);
                $(fileItem).fadeOut(function () {
                    $(fileItem).remove();
                });


                //数组中的文件也要删除
                for (var i = 0; i < filesArr.length; i++) {
                    if (filesArr[i].id == $(fileItem).attr("id")) {
                        filesArr.splice(i, 1);//i是要删除的元素在数组中的下标，1代表从下标位置开始连续删除一个元素
                    }
                }
                //隐藏上传按钮

                if (count - success < 0) {
                    success--;
                }
            });
        });

        uploader.on('filesQueued', function (file) {
            $(".btns").css("pointer-events","auto");
            //限制单个文件的大小 超出了提示
            if (file.length > 1) {
                filesFlag = true;
            }else{
                filesFlag = false;
            }
        });
        //文件上传过程中创建进度条实时显示
        uploader.on('uploadProgress', function (file, percentage) {
            $(".btns").css("pointer-events","auto");
            var $li = $('#' + file.id);

            //避免重复创建

            //将实时进度存入隐藏域
            $("#jindutiao").val(Math.round(percentage * 100));
            //根据fielId获得当前要上传的文件的进度
            var oldJinduValue = map[file.id];

            if (percentage < oldJinduValue && oldJinduValue != 1) {
                $li.find('p.state').text('上传中' + Math.round(oldJinduValue * 100) + '%');

                if (oldJinduValue == 1) {
                    $li.find('p.state').text('转化中...');
                }
            } else {
                $li.find('p.state').text('上传中' + Math.round(percentage * 100) + '%');

                if (percentage == 1) {
                    $li.find('p.state').text('转化中...');
                }

            }
        });

        uploader.on('uploadFinished', function (file){
            // 上传文件完成 取消完成按钮禁用
            $("#newFolder").attr("disabled",false);
            $("#newFolder").removeClass("disabled-button");
            $(".btns").css("pointer-events","auto");
            // uploadAmount=0;
            // $("#continueUpload").show();
        })

        uploader.on('beforeFileQueued', function (file){
            $(".btns").css("pointer-events","auto");
            // uploadAmount++
            // if(uploadAmount>10){
            //
            //     layer.msg("一次最多选择10个文件", {anim:6,icon: 0});
            //     return false;
            // }
            if(categoryId==undefined){
                $(".shadow").hide();
                $(".uploadFile").show();
                $(".uploadFolder").show();
                $("#dndArea").css("opacity", "1");
                $("#setFileAuthority").addClass("hide");
                $("#setFileTip").addClass("hide");
                $("#setClassify").addClass("hide");
                layer.msg("请先选择目录", {anim:6,icon: 0});
                return false;
            }
            // $("#continueUpload").hide();
        })
        //上传成功后执行的方法
        uploader.on('uploadSuccess', function (file) {

            var ajax2 = new $ax(Hussar.ctxPath + "/breakpointUpload/delete", function(data) {

            }, function(data) {

            });
            ajax2.start();
            // 将上传路径存入cookie
            var path = $("#selectFolder .folderinner").text();
            var folderId = categoryId;
            document.cookie = "path=" + encodeURIComponent(path);
            document.cookie = "openFileId=" + folderId;
            $(".btns").css("pointer-events","auto");
            if (flag == 0) {
                $('#' + file.id).find('p.state').addClass("warn").text('文件已存在')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
            } else if (flag == 3) {
                $('#' + file.id).find('p.state').addClass("warn").text('文件上传失败')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
                fastFlag = null;

            }
            else if (flag == 4) {
                $('#' + file.id).find('p.state').addClass("warn").text('空间不足')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
            } else if (fastFlag == 2) {
                var sameNameNewVersion = $('#sameNameNewVersion').val();
                if(sameNameNewVersion == "true"){
                    /**************** 添加代码开始 *******************/
                    // 当文件重名时，提示是否将当前文档作为重名文档的新版本上传
                    $('#' + file.id).find('p.state').addClass("warn-info").text('存在同名文件,是否上传为新版本');
                    $('#' + file.id).find('a:last-child').removeClass();
                    var originStyle = $('#' + file.id).find('a:last-child').attr('style');
                    var newStyle = "float: left;width: 8% !important;pointer-events: auto;margin-left: 5%;";
                    $('#' + file.id).find('a:last-child').attr('style',newStyle);
                    $('#' + file.id).find('a:last-child').html("<span class='yes'>是</span><span class='no'>否</span>");
                    $('#' + file.id).find('a:last-child').find('span').css({
                        "display":"inline-block",
                        "width":"39%",
                        "text-align":"center",
                        "box-shadow":"0 0 1px #000",
                        "line-height":"2",
                        "margin-top":"10px",
                        "margin-right":"5%"
                    });
                    // 用户选择是，上传最新版本
                    $('#' + file.id).find('a:last-child').find('.yes').click(function () {
                        file.zdyFlag = "version";
                        uploader.upload(file);
                        $('#' + file.id).find('p.state').removeClass("warn-info");
                        showDelBtn();
                        event.stopPropagation();
                    });
                    // 用户选择否，显示删除按钮
                    $('#' + file.id).find('a:last-child').find('.no').click(function () {
                        showDelBtn();
                        event.stopPropagation();
                    });

                    function showDelBtn(){
                        $('#' + file.id).find('a:last-child').attr('style',originStyle);
                        $('#' + file.id).find('a:last-child').html('');
                        $('#' + file.id).find('a:last-child').addClass("delete btnRemoveFile");
                    }

                    fastFlag = null;
                    /**************** 添加代码结束 *******************/
                }else{
                    $('#' + file.id).find('p.state').addClass("warn").text('文件已存在')
                    $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                    if (!filesFlag){
                        //layer.confirm("检测到重名文件，是否进入版本管理？",{title:['版本管理','background-color:#fff']}, function () {
                        //    returnList(returnDocId);
                        //});
                    }
                    fastFlag = null;
                }
            } else if (fastFlag == 10) {
                $('#' + file.id).find('p.state').addClass("warn").text('存在同名待审核文件')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            }  else if (fastFlag == 1) {
                $('#' + file.id).find('p.state').addClass("warn").text('空间不足')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 6) {
                $('#' + file.id).find('p.state').addClass("warn").text('名称过长')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 7) {
                $('#' + file.id).find('p.state').addClass("warn").text('名称不合法')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 8) {
                $('#' + file.id).find('p.state').addClass("warn").text('格式不支持')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 0) {

                // 隐藏删除按钮
                $('#' + file.id).find('.btnRemoveFile').hide();
                // $('#' + file.id).find('.btnRemoveFile').removeClass("btnRemoveFile");
                //隐藏上传按钮
                success--;
                if (success == 0) {

                }
                $('#' + file.id).find('p.state').text('')
                $('#' + file.id).find('p.state').addClass("success").append('秒传');
                // refreshFile(openFileId)
                fastFlag = null;
                /*$.ajax({
                    url: Hussar.ctxPath+"/fsFolder/isOwn",
                    async: true,
                    data: {
                        categoryId: categoryId,
                    },
                    success: function (data) {
                        if(data=="1"){
                            $("#setFileAuthority").addClass("hide");
                            $("#setFileTip").addClass("hide");
                        }else{
                            $("#setFileAuthority").removeClass("hide");
                            $("#setFileTip").removeClass("hide");
                        }
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/fsFolder/isOwn", function(data) {
                    if(data=="1"){
                        $("#setFileAuthority").addClass("hide");
                        $("#setFileTip").addClass("hide");
                        $("#setClassify").addClass("hide");
                        $("#setFileOpen").addClass("hide");
                    }else{
                        $("#setFileAuthority").removeClass("hide");
                        $("#setFileTip").removeClass("hide");
                        $("#setClassify").removeClass("hide");
                        $("#setFileOpen").removeClass("hide");
                    }
                }, function(data) {

                });
                ajax.set("categoryId",categoryId);
                ajax.start();
            }else if(fastFlag == 9){
                $('#' + file.id).find('p.state').addClass("warn").text('与历史版本相同');
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
            }
            else {  //上传成功去掉进度条

                //隐藏删除按钮
                $('#' + file.id).find('.btnRemoveFile').hide();
                //$('#' + file.id).find('.btnRemoveFile').removeClass("btnRemoveFile");
                //上传成功，获得积分
                /*$.ajax({
                    url: Hussar.ctxPath+"/integral/addIntegral",
                    async: true,
                    data:{
                        docId:'',
                        ruleCode: 'upload'
                    },
                    success: function (data) {
                        if (data.integral != 0 && data.integral != null && data.integral != ''){
                            $("#num").html(data.msg)
                            if(data.msg=="积分不足"||data.msg=="已达上限"){
                                $(".integral .point").hide();
                                $(".integral .num") .css({"width":"81px","padding-top":"13px"})
                            }
                            $(".integral").show();
                            // 实时更新积分
                            $("#totalIntegral",parent.document).text(parent.getTotalIntegral());
                            setTimeout(function () {
                                $(".integral .point").show();
                                $(".integral .num") .css({"width":"40px","padding-top":"0"})
                                $(".integral").hide();

                            },2000)
                        }
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                    if (data.integral != 0 && data.integral != null && data.integral != ''){
                        $("#num").html(data.msg)
                        if(data.msg=="积分不足"||data.msg=="已达上限"){
                            $(".integral .point").hide();
                            $(".integral .num") .css({"width":"81px","padding-top":"13px"})
                        }
                        $(".integral").show();
                        // 实时更新积分
                        $("#totalIntegral",parent.document).text(parent.getTotalIntegral());
                        setTimeout(function () {
                            $(".integral .point").show();
                            $(".integral .num") .css({"width":"40px","padding-top":"0"})
                            $(".integral").hide();

                        },2000)
                    }
                }, function(data) {

                });
                ajax.set("docId",'');
                ajax.set("ruleCode",'upload');
                ajax.start();
                //隐藏上传按钮
                success--;
                if (success == 0) {

                }
                $('#' + file.id).find('p.state').text('')
                $('#' + file.id).find('p.state').addClass("success").append('上传成功')


                /*$.ajax({
                    url: Hussar.ctxPath+"/fsFolder/isOwn",
                    async: true,
                    data: {
                        categoryId: categoryId,
                    },
                    success: function (data) {
                        if(data=="1"){
                            $("#setFileAuthority").addClass("hide");
                            $("#setFileTip").addClass("hide");
                        }else{
                            $("#setFileAuthority").removeClass("hide");
                            $("#setFileTip").removeClass("hide");
                        }
                    }
                })*/
                var ajax = new $ax(Hussar.ctxPath + "/fsFolder/isOwn", function(data) {
                    if(data=="1"){
                        $("#setFileAuthority").addClass("hide");
                        $("#setFileTip").addClass("hide");
                        $("#setClassify").addClass("hide");
                    }else{
                        $("#setFileAuthority").removeClass("hide");
                        $("#setFileTip").removeClass("hide");
                        $("#setClassify").removeClass("hide");
                    }
                }, function(data) {

                });
                ajax.set("categoryId",categoryId);
                ajax.start();
            }
            if (count > filesArr.length - 1) {
                if (powerFlag == 1) {
                    chooseUploadFile = [];
                    chooseUploadAuthor = [];
                    count = 0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
                    success = 0;//上传成功的文件数
                    filesArr = new Array();//文件数组：每当有文件被添加进队列的时候 就push到数组中
                    map = {};//key存储文件id，value存储该文件上传过的进度
                    powerFlag = 0
                    if(categoryId==undefined){
                        $(".shadow").hide();
                        // $(".webuploader-pick").parent().show();
                        $(".uploadFile").show();
                        $(".uploadFolder").show();
                        $("#dndArea").css("opacity", "1");
                        $("#setFileAuthority").addClass("hide");
                        $("#setFileTip").addClass("hide");
                        $("#setClassify").addClass("hide");
                        // layer.msg("1", {anim:6,icon: 0});
                    }

                } else {
                    var amount = count - success;
                    if (amount < 0) {
                        amount = 0;
                    }
                    $(".success-msg").html("成功上传" + amount + "个文件！").show(500);
                }

            }


            // refreshFile(openFileId)
            fastFlag = null;

        });

        //上传出错后执行的方法
        uploader.on('uploadError', function (file) {
            $(".btns").css("pointer-events","auto");
            errorUpload = true;
            uploader.stop(true);
            $('#' + file.id).find('p.state').addClass("warn").text('上传出错，请检查网络连接');
        });

        //文件上传成功失败都会走这个方法
        uploader.on('uploadComplete', function (file) {
            $(".btns").css("pointer-events","auto");

        });

        uploader.on('all', function (type) {
            $(".btns").css("pointer-events","auto");
            if (type === 'startUpload') {
                state = 'uploading';
            } else if (type === 'stopUpload') {
                state = 'paused';
            } else if (type === 'uploadFinished') {
                state = 'done';
            }
        });

        /**
         验证文件格式以及文件大小
         */
        uploader.on("error", function (type) {
            $(".btns").css("pointer-events","auto");
            if (type == "Q_EXCEED_SIZE_LIMIT") {
                $(".shadow").hide();
                // $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("文件大小不能超过3G", {anim: 6, icon: 0});
            } else if (type == "Q_EXCEED_NUM_LIMIT") {
                $(".shadow").hide();
                // $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传列表中不得超过1000个文件", {anim: 6, icon: 0});
            }  else if (type == "Q_TYPE_DENIED") {
                $(".shadow").hide();
                // $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("不允许上传空文件", {anim: 6, icon: 0});
            } else if (type == "F_DUPLICATE") {
                $(".shadow").hide();
                // $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传列表中存在重复文件", {anim: 6, icon: 0});
            } else {
                $(".shadow").hide();
                // $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传出错！请检查后重新上传！错误代码" + type, {anim: 6, icon: 0});
            }
        });
    };

    /**
     *  初始化按钮事件
     */
    BreakpointUpload.initButtonEvents = function () {
        $("#searchName").click(function () {
            searchFlag = 1;
        })
    };
    setTimeout(function () {
        $(".webuploader-pick").on("click",function () {
            personParam = [];
            groupParam = [];
            editFlag = false;
            buttonType = 'upload';
            groupId = [];
            personId = [];
            // count=0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
            // success=0;//上传成功的文件数
            // filesArr=[];

            if(categoryId==undefined){
                $(".shadow").hide();
                $(".uploadFile").show();
                $(".uploadFolder").show();
                $("#dndArea").css("opacity", "1");
                layer.msg("请先选择目录", {anim:6,icon: 0});

                return;
            }
            $(this).next().find("label").click()
        });
    },300);
    function getLoginUser(){
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/files/getLoginUser",
            async:true,
            cache:false,
            dataType:"json",
            success:function(result){
                if(result){
                    userId = result.userId;
                    userName = result.userName;
                }
            }, error:function(data) {

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/files/getLoginUser", function(result) {
            if(result){
                userId = result.userId;
                userName = result.userName;
            }
        }, function(data) {

        });
        ajax.start();
    }
    function refreshFile(){
    }

    function folderList(){
        /*$.ajax({
            type: "POST",
            url: Hussar.ctxPath+"/frontUpload/folderList",
            data : {
                id:"#"
                /!*,description:categoryDesc,*!/
            },
            contentType:"application/x-www-form-urlencoded",
            dataType:"json",
            async: true,
            success:function(result) {
                var innerHtml="<ul>";
                var data=result;
                for(var i=0; i<data.length; i++){
                    innerHtml+= '<li onmouseover="expandFolder(\''+data[i].id+'\',\''+data[i].text+'\',\'1\')" ' +
                        'onclick="expandFolder2(\''+data[i].id+'\',\''+data[i].text+'\',\'1\')">'+data[i].folderName+'</li>';
                }
                innerHtml+="</ul>";
                $("#folderList").html(innerHtml);
                $(document).click(function(e){
                    if($(e.target)[0]!=$("#folderList")){
                        $(".folderList").hide();
                        $(".folderList ul").remove();
                    }

                });
            }

        });*/
        var ajax = new $ax(Hussar.ctxPath + "/frontUpload/folderList", function(result) {
            var innerHtml="<ul>";
            var data=result;
            for(var i=0; i<data.length; i++){
                innerHtml+= '<li onmouseover="expandFolder(\''+data[i].id+'\',\''+data[i].text+'\',\'1\')" ' +
                    'onclick="expandFolder2(\''+data[i].id+'\',\''+data[i].text+'\',\'1\')">'+data[i].folderName+'</li>';
            }
            innerHtml+="</ul>";
            $("#folderList").html(innerHtml);
            $(document).click(function(e){
                if($(e.target)[0]!=$("#folderList")){
                    $(".folderList").hide();
                    $(".folderList ul").remove();
                }

            });
        }, function(data) {

        });
        ajax.set("id","#");
        ajax.start();
    }
    /*修改*/
    $("#setFileTip").on('click',function(){
        chooseFile=[];
        chooseFile=chooseUploadFile;
        chooseFileAuthor=chooseUploadAuthor
        if(chooseFile.length==0){
            layer.msg("请先上传文档", {anim:6,icon: 0});
            return
        }
        editFlag = true;
        groupId = [];
        groupParam = [];
        personId = [];
        personParam= [];

        layer.open({
            type: 2,
            title: ['标签设置','background-color:#fff'],
            offset:scrollHeightTip,
            area: ['auto','250'], //宽高
            fix: false, //不固定
            maxmin: false,
            skin:'label-dialog',
            content: Hussar.ctxPath+'/fsFile/setTip',
            success:function(){
            }
        });


    });
    /*修改是否可分享*/
    $("#setFileShareable").on('click',function(){
        chooseFile=[];
        chooseFile=chooseUploadFile;
        chooseFileAuthor=chooseUploadAuthor
        if(chooseFile.length==0){
            $(this).find(".layui-form-checkbox").toggleClass("layui-form-checked");
            if ($(this).find(".layui-form-checkbox").hasClass("layui-form-checked")){
                shareable = 1;
            }else {
                shareable = 0;
            }
            return
        }
        if ($(this).find(".layui-form-checkbox").hasClass("layui-form-checked")){
            var index = layer.confirm("确认要将上传的文件设为不可分享吗？",
                {
                    title :['信息','background-color:#fff'],
                    offset:scrollHeightAlert,skin:'move-confirm'},
                function () {
                    shareable = 0;
                    editShareFlag();
                    $("#setFileShareable").find(".layui-form-checkbox").removeClass("layui-form-checked");
                    layer.close(index);
                });
        }else {
            shareable = 1;
            editShareFlag();
            $(this).find(".layui-form-checkbox").addClass("layui-form-checked");
        }
    });
    /*修改是否可分享*/
    $("#setFileOpen").on('click',function(){
        chooseFile=[];
        chooseFile=chooseUploadFile;
        chooseFileAuthor=chooseUploadAuthor
        if(chooseFile.length==0){
            $(this).find(".layui-form-checkbox").toggleClass("layui-form-checked");
            if ($(this).find(".layui-form-checkbox").hasClass("layui-form-checked")){
                fileOpen = 1;
            }else {
                fileOpen = 0;
            }
            return
        }
        if (!$(this).find(".layui-form-checkbox").hasClass("layui-form-checked")){
            var index = layer.confirm("确认要将上传的文件设为全员可见吗？",{title :['信息','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'}, function () {
                $(".layui-layer-content ").append("<i class='layui-icon layui-icon-loading layui-anim layui-anim-rotate layui-anim-loop'></i>   ");
                fileOpen = 1;
                editFileOpen();
                $("#setFileOpen").find(".layui-form-checkbox").addClass("layui-form-checked");
                layer.close(index);
            });
        }else {
            fileOpen = 0;
            $("body").append("<i class='layui-icon layui-icon-loading '></i>   ");
            editFileOpen();
            $(this).find(".layui-form-checkbox").removeClass("layui-form-checked");
            layer.msg('全员可见已取消', {icon: 1})
        }
    });
    /*修改*/
    $("#setFileAuthority").on('click',function(){
        chooseFile=[];
        chooseFile=chooseUploadFile;
        chooseFileAuthor=chooseUploadAuthor
        if(chooseFile.length==0){
            layer.msg("请先上传文档", {anim:6,icon: 0});
            return
        }
        editFlag = true;
        groupId = [];
        groupParam = [];
        personId = [];
        personParam= [];

        layer.open({
            type: 2,
            title: ['权限设置','background-color:#fff'],
            // area: ['686px', '510px'], //宽高
            offset:scrollHeightLong,
            skin:'permission-dialog-file',
            fix: false, //不固定
            maxmin: false,
            content: Hussar.ctxPath+'/fsFile/fileAuthority',
            success:function(){
            } ,end: function(){
                refreshFile(openFileId)
            }
        });


    });

    $("#selectFolder .folderinner").hover(function () {
        var scrollWidth = $(this)[0].scrollWidth;
        $(this).animate({scrollLeft:scrollWidth},25000,'linear');
    }, function () {
        $(this).stop(true,false);
        var scrollWidth = $(this)[0].scrollWidth;
        $(this).animate({scrollLeft:- scrollWidth},10,'linear');
    });

    /*指定分类*/
    $("#setClassify").on('click',function(){
        chooseFile=[];
        chooseFile=chooseUploadFile;
        chooseFileAuthor=chooseUploadAuthor
        if(chooseFile.length==0){
            layer.msg("请先上传文档", {anim:6,icon: 0});
            return
        }
        editFlag = true;
        groupId = [];
        groupParam = [];
        personId = [];
        personParam= [];

        layer.open({
            type: 2,
            title: ['指定分类','background-color:#fff'],
            offset:scrollHeightLong,
            area: ['400px','434px'],
            fix: false, //不固定
            maxmin: false,
            content: Hussar.ctxPath+'/fsClassify/setClassify',
            success:function(){
            } ,end: function(){
                refreshFile(openFileId)
            }
        });


    });

//页面初始化
    $(function () {
        getLoginUser();
        var getExplorer = (function () {
            var explorer = window.navigator.userAgent,
                compare = function (s) { return (explorer.indexOf(s) >= 0); },
                ie11 = (function () { return ("ActiveXObject" in window) })();
            if (compare("MSIE") || ie11) { return 'ie'; }
            else if (compare("Firefox") && !ie11) { return 'Firefox'; }
            else if (compare("Chrome") && !ie11) {
                if (explorer.indexOf("Edge") > -1) {
                    return 'Edge';
                } else {
                    return 'Chrome';
                }
            }
            else if (compare("Opera") && !ie11) { return 'Opera'; }
            else if (compare("Safari") && !ie11) { return 'Safari'; }

        })()

        if (getExplorer == 'ie') {
            $(".uploadFolder").hide();

        }
        if (getExplorer == 'Edge') {
        }
        // 当从文档管理页面的上传按钮跳转过来时，接收参数，并给成员变量赋值
        var path = $("#path").val();
        if(path=='我的文件夹'){
            $("#setFileOpen").addClass("hide");
        }
        var folderId = $("#openFileId").val();
        if (path != '' && folderId != ""){// 都不为空——从文档管理跳转过来
            $("#selectFolder .folderinner").text(path);
            categoryId = folderId;
            realcategoryId = folderId;
            $("#openFileId").val("");
            $("#path").val("");
        } else {
/*            if(document.cookie.length != 0) {
                console.log(document.cookie);
                var cookiePath = getCookie("path");
                var cookieFolderId = getCookie("openFileId");
                if (!(cookiePath == undefined || cookiePath == null || cookieFolderId == undefined || cookieFolderId == null)){
                    $("#selectFolder .folderinner").text(cookiePath);
                    categoryId = cookieFolderId;
                    realcategoryId = cookieFolderId;
                }
            }*/
        }

        BreakpointUpload.initUploader();
        BreakpointUpload.initButtonEvents();

        // //弹窗按钮事件添加
        // $(".controls-down").click(function () {
        //     $(".popWinNew").toggleClass("success");
        //     if($(".popWin").hasClass("success")){
        //         $(this).html("&#xe619;")
        //     }else {
        //         $(this).html("&#xe61a;")
        //     }
        //
        // });

        //
        // $(".controls-close").click(function () {
        //     $(".success-msg").html("成功上传"+(count-success)+"个文件！").hide(500);
        //     //最开始进入页面时清除原来的列表
        //     var fileList = $("#thelist").find(".item");
        //     for(var n = 0;n<fileList.length;n++){
        //         fileList.eq(n).remove();
        //         uploader.removeFile( fileList.eq(n).attr("id"),true);
        //     }
        //     chooseUploadFile=[];
        //     chooseUploadAuthor=[];
        //     count=0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
        //     success=0;//上传成功的文件数
        //     filesArr=new Array();//文件数组：每当有文件被添加进队列的时候 就push到数组中
        //     map={};//key存储文件id，value存储该文件上传过的进度
        //     $(".popWin").css("display","none")
        // })
    });
})

function  expandFolder(id,name,index,e){
    $("#folderList ul:gt("+(index-1)+") li,#folderList ul:eq("+(index-1)+") li").css("color","#333")
    // $("#folderList ul:gt("+(index-1)+") li span,#folderList ul:eq("+(index-1)+") li span").remove()
    $(e).css("color","#3C91FD");
    // $(e).append("")
    folderName+=name+">"

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "POST",
            url: Hussar.ctxPath+"/frontUpload/folderList",
            data : {
                id:id
                /!*,description:categoryDesc,*!/
            },
            contentType:"application/x-www-form-urlencoded",
            dataType:"json",
            async: true,
            success:function(result) {

                var length= $("#folderList ul").length;
                if(index!=length){
                    $("#folderList ul:gt("+(index-1)+")").remove();

                    var account=  findChar(folderName,'>',index - 1);
                    folderName=folderName.substr(0,account+1);
                    folderName=   folderName+name+">";

                }
                length= $("#folderList ul").length;
                var totalLength= 188-(130*(length));
                var innerHtml="<ul style='right: "+totalLength+"px'>";
                var data=result;
                if (data.length == 0){
                    innerHtml = "<ul style='display:none;right: "+totalLength+"px'>";
                }
                //if(data.length==0){
                //categoryId=id;
                //folderName=folderName.substring(0,folderName.length-1)
                //$("#selectFolder .folderinner").html(folderName);
                //$("#selectFolder .folderinner")[0].scrollLeft = 0;
                //$("#folderList").html("")
                //$("#selectFolder .downfont").show();
                //$("#selectFolder .upfont").hide();
                //innerHtml=""
                //return
                //}
                index=parseInt(index)+1;
                for(var i=0; i<data.length; i++){
                    var children ;
                    if(data[i].children==true){
                        children="<span class='iconfont'>&#xe602;</span>"
                    }else{
                        children=""
                    }
                    innerHtml+= '<li title="'+data[i].text+'"  onmouseover="expandFolder(\''+data[i].id+'\',\''+data[i].text+'\',\''+(index)+'\',this)" ' +
                        'onclick="expandFolder2(\''+data[i].id+'\',\''+data[i].text+'\',\'1\')">'+data[i].text+'' +children+

                        '</li>';
                }
                innerHtml+="</ul>";
                $("#folderList").append(innerHtml);

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/frontUpload/folderList", function(result) {
            var length= $("#folderList ul").length;
            if(index!=length){
                $("#folderList ul:gt("+(index-1)+")").remove();

                var account=  findChar(folderName,'>',index - 1);
                folderName=folderName.substr(0,account+1);
                folderName=   folderName+name+">";

            }
            length= $("#folderList ul").length;
            var totalLength= 188-(130*(length));
            var innerHtml="<ul style='right: "+totalLength+"px'>";
            var data=result;
            if (data.length == 0){
                innerHtml = "<ul style='display:none;right: "+totalLength+"px'>";
            }
            //if(data.length==0){
            //categoryId=id;
            //folderName=folderName.substring(0,folderName.length-1)
            //$("#selectFolder .folderinner").html(folderName);
            //$("#selectFolder .folderinner")[0].scrollLeft = 0;
            //$("#folderList").html("")
            //$("#selectFolder .downfont").show();
            //$("#selectFolder .upfont").hide();
            //innerHtml=""
            //return
            //}
            index=parseInt(index)+1;
            for(var i=0; i<data.length; i++){
                var children ;
                if(data[i].children==true){
                    children="<span class='iconfont'>&#xe602;</span>"
                }else{
                    children=""
                }
                innerHtml+= '<li title="'+data[i].text+'"  onmouseover="expandFolder(\''+data[i].id+'\',\''+data[i].text+'\',\''+(index)+'\',this)" ' +
                    'onclick="expandFolder2(\''+data[i].id+'\',\''+data[i].text+'\',\'1\')">'+data[i].text+'' +children+

                    '</li>';
            }
            innerHtml+="</ul>";
            $("#folderList").append(innerHtml);
        }, function(data) {

        });
        ajax.set("id",id);
        ajax.start();
    });
}
function  expandFolder2(id,name,index,e){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "POST",
            url: Hussar.ctxPath+"/frontUpload/changeFolder",
            data : {
                id:id
                /!*,description:categoryDesc,*!/
            },
            dataType:"json",
            async: true,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success:function(result) {

                if(result.noChildPower==0){

                    layer.msg("您没有权限", {anim:6,icon: 0});
                    return;
                }else{
                    categoryId=id;
                    realcategoryId=id;
                    folderName=folderName.substring(0,folderName.length-1)
                    $("#selectFolder .folderinner").html(folderName);
                    $("#selectFolder .folderinner")[0].scrollLeft = 0;
                    $("#folderList").html("")
                    $("#selectFolder .downfont").show();
                    $("#selectFolder .upfont").hide();
                    innerHtml=""
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/frontUpload/changeFolder", function(result) {
            if(result.noChildPower==0){

                layer.msg("您没有权限", {anim:6,icon: 0});
                return;
            }else{
                categoryId=id;
                realcategoryId=id;
                folderName=folderName.substring(0,folderName.length-1)
                $("#selectFolder .folderinner").html(folderName);
                $("#selectFolder .folderinner")[0].scrollLeft = 0;
                $("#folderList").html("")
                $("#selectFolder .downfont").show();
                $("#selectFolder .upfont").hide();
                innerHtml=""
                var ajax = new $ax(Hussar.ctxPath + "/fsFolder/isOwn", function(data) {
                    if(data=="1"){

                        $("#setFileOpen").addClass("hide");
                    }else{

                        $("#setFileOpen").removeClass("hide");
                    }
                }, function(data) {

                });
                ajax.set("categoryId",categoryId);
                ajax.start();
            }
        }, function(data) {

        });
        ajax.set("id",id);
        ajax.start();
    });

}
// $(document).click(function(e){
//     if($(e.target)[0]!=$("#folderList ")){
//         $("#folderList ").remove();
//     }
//
// });
function showFolderList(){
    $("div.upload-to").focus();
    $("#selectFolder .downfont").hide();
    $("#selectFolder .upfont").show();

    folderName="";
    $("#folderList").html("");
    $("#folderList").show();

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "POST",
            url: Hussar.ctxPath+"/frontUpload/folderList",
            data : {
                id:"2bb61cdb2b3c11e8aacf429ff4208431"
                /!*,description:categoryDesc,*!/
            },
            contentType:"application/x-www-form-urlencoded",
            dataType:"json",
            async: true,
            success:function(result) {
                var innerHtml="<ul>";
                var data=result;
                if(data.length==0){
                    layer.msg("您没有上传权限", {anim: 6, icon: 0});
                    $("div.upload-to").blur();
                    $("#selectFolder .downfont").show();
                    $("#selectFolder .upfont").hide();

                    folderName="";
                    $("#folderList").html("");
                    $("#folderList").hide();
                    return;
                }
                for(var i=0; i<data.length; i++){
                    var children;
                    if(data[i].children==true){
                        children="<span class='iconfont'>&#xe602;</span>"
                    }else{
                        children=""
                    }
                    innerHtml+= '<li title="'+data[i].text+'" onmouseover="expandFolder(\''+data[i].id+'\',\''+data[i].text+'\',\'1\',this)" ' +
                        'onclick="expandFolder2(\''+data[i].id+'\',\''+data[i].text+'\',\'1\')">'+data[i].text+'' +children+
                        '</li>';
                }
                innerHtml+="</ul>";
                $("#folderList").html(innerHtml);
                // $(document).click(function(e){
                //     if($(e.target)[0]==$("#folderList ul")){
                //       alert(1);
                //     }
                //
                // });
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/frontUpload/folderList", function(result) {
            var innerHtml="<ul>";
            var data=result;
            var adminFlag = $("#adminFlag").val();
            if(data.length==0){
                if(adminFlag!=1){
                    layer.msg("您没有上传权限", {anim: 6, icon: 0});
                }else{
                    layer.msg("请先创建目录", {anim: 6, icon: 0});
                }

                $("div.upload-to").blur();
                $("#selectFolder .downfont").show();
                $("#selectFolder .upfont").hide();

                folderName="";
                $("#folderList").html("");
                $("#folderList").hide();
                return;
            }
            for(var i=0; i<data.length; i++){
                var children;
                if(data[i].children==true){
                    children="<span class='iconfont'>&#xe602;</span>"
                }else{
                    children=""
                }
                innerHtml+= '<li title="'+data[i].text+'" onmouseover="expandFolder(\''+data[i].id+'\',\''+data[i].text+'\',\'1\',this)" ' +
                    'onclick="expandFolder2(\''+data[i].id+'\',\''+data[i].text+'\',\'1\')">'+data[i].text+'' +children+
                    '</li>';
            }
            innerHtml+="</ul>";
            $("#folderList").html(innerHtml);
            // $(document).click(function(e){
            //     if($(e.target)[0]==$("#folderList ul")){
            //       alert(1);
            //     }
            //
            // });
        }, function(data) {

        });
        ajax.set("id","2bb61cdb2b3c11e8aacf429ff4208431");
        ajax.start();
    });
}

function hideFolderList(){
    $("#selectFolder .downfont").show();
    $("#selectFolder .upfont").hide();
    //folderName="";
    $("#folderList").hide();
}
/* 设置文件的分享状态*/
function editShareFlag(){
    chooseFile=[];
    chooseFile=chooseUploadFile;
    for(var i=0;i<chooseFile.length;i++){
        //var categoryName = parent.chooseFileName[i];
        var fileId = chooseFile[i];
        // var authorId = parent.chooseFileAuthor[i];

        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

            /** 调用权限的更新方法 */
            /*$.ajax({
                type: "POST",
                url: Hussar.ctxPath+"/fsFile/setShareFlag",
                data : {
                    docId:fileId,
                    shareFlag:shareable
                },
                contentType:"application/x-www-form-urlencoded",
                dataType:"json",
                async: false,
                success:function(result) {

                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/setShareFlag", function(result) {

            }, function(data) {

            });
            ajax.set("docId",fileId);
            ajax.set("shareFlag",shareable);
            ajax.start();
        });

    }

}
function editFileOpen(){
    chooseFile=[];
    chooseFile=chooseUploadFile
    var docIds = chooseFile.join(",");
    //for(var i=0;i<chooseFile.length;i++){
    //var categoryName = parent.chooseFileName[i];
    //var fileId = chooseFile[i];
    // var authorId = parent.chooseFileAuthor[i];

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /** 调用权限的更新方法 */
        /*$.ajax({
            type: "POST",
            url: Hussar.ctxPath+"/fsFile/setShareFlag",
            data : {
                docId:fileId,
                shareFlag:shareable
            },
            contentType:"application/x-www-form-urlencoded",
            dataType:"json",
            async: false,
            success:function(result) {

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/setFileOpen", function(result) {

        }, function(data) {

        });
        ajax.setAsync(true);
        ajax.set("docIds",docIds);
        ajax.set("fileOpen",fileOpen);
        ajax.start();
    });
    $(".layui-icon-loading").remove();
    //}

}
function  returnList(docId) {
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        var path = $("#selectFolder .folderinner").text().toString();
        var url = "/frontUpload/upload?openFileId=" + categoryId + "&folderName=" + (encodeURI(path.substring(path.lastIndexOf(">") + 1)));
        if (docId != undefined && docId != ''){
            url += "&returnDocId=" + docId;
        }
        if(categoryId==undefined){
            window.parent.open(Hussar.ctxPath+"/frontUpload/upload","mainFrame")
        }else{
            window.parent.open(Hussar.ctxPath+url,"mainFrame")
        }
    });

}
function  continueUpload() {
    var fileList = $("#thelist").find(".item");
    for(var n = 0;n<fileList.length;n++){
        fileList.eq(n).remove();
        uploader.removeFile( fileList.eq(n).attr("id"),true);
    }
    chooseUploadFile=[];
    chooseUploadAuthor=[];
    count=0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
    success=0;//上传成功的文件数
    filesArr=new Array();//文件数组：每当有文件被添加进队列的时候 就push到数组中
    map={};//key存储文件id，value存储该文件上传过的进度
    $(".dragArea").show();
    $(".upload-tip").show();
    $(".popWinNew").hide();
    $("#continueUpload").hide();

}
// 修改继续上传方式
function continueUpload2(){
    uploadFloder=false;
    $(".webuploader-pick").triggerHandler("click");
}
function uploadFolder(){
    uploadFloder=true;
    $(".webuploader-pick").triggerHandler("click");
    // setTimeout(function () {
    //     uploadFloder=false;
    // },1000)
}
function uploadFile(){
    uploadFloder=false;
    $(".webuploader-pick").triggerHandler("click");
    // setTimeout(function () {
    //     uploadFloder=false;
    // },1000)
}
function refreshFile(){
}

/**
 * 查找字符串str中第num个char字符的位置
 * @param str   要被查询的字符串
 * @param cha   要查询的字符
 * @param num   第几个
 * @returns {*} （第0个，返回-1；第1个及以上，返回位置）
 * @author  zgr
 */
function findChar(str,cha,num){
    var x=str.indexOf(cha);
    if (num <= 0){
        return -1;
    }
    for(var i=1;i<num;i++){
        x=str.indexOf(cha,x+1);
    }
    return x;
}
$(function() {

    setInterval(function () {
        scrollHeight = parent.scrollHeight;
        var height = parseInt(scrollHeight);
        var screenHeight = parseInt(window.screen.availHeight);
        if (scrollHeight != 0) {
            scrollHeightAlert = parseInt(height - 130 + (screenHeight - 154) / 2.0) + "px";
            scrollHeightLong = parseInt(height - 130 + (screenHeight - 510) / 2.0) + "px";
            scrollHeightTip = parseInt(height - 130 + (screenHeight - 250) / 2.0) + "px";
            scrollHeightShare = parseInt(height - 130 + (screenHeight - 200) / 2.0) + "px";
            scrollHeightMsg = parseInt(height - 130 + (screenHeight - 64) / 2.0) + "px";
            //console.log(scrollHeightAlert + ' ' + scrollHeightLong)
        }
        //console.log(height + "//" + screenHeight + " " + layerHeight)
        //$(".layui-layer.layui-layer-iframe").css("top",height - 130 + (screenHeight - layerHeight) / 2.0 + "px");
    }, 300);
});
function browserVersion(){
    if(navigator.userAgent.indexOf("MSIE")>0) {                                 // MSIE内核
        return "MSIE";
    }
    if(navigator.userAgent.indexOf("Firefox")>0){                                 // Firefox内核
        return "Firefox";
    }
    if(navigator.userAgent.indexOf("Opera")>0){                                  // Opera内核
        return "Opera";
    }
    if(navigator.userAgent.indexOf("Safari")>0) {                                  // Safari内核
        return "Safari";
    }
    if(navigator.userAgent.indexOf("Camino")>0){                                  // Camino内核
        return "Camino";
    }
    if(navigator.userAgent.indexOf("Gecko")>0){                                    // Gecko内核
        return "Gecko";
    }
}
// 读取cookie方法
function getCookie(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");

    if(arr=document.cookie.match(reg)) {
        return decodeURIComponent(arr[2]);
    }else {
        return null;
    }
}function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").css("top",scrollHeightAlert);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}