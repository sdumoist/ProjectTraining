/**
 * Created by smt on 2018/6/30.
 */
var dragover;
var dragleave;
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
    var BreakpointUpload = {
        layerIndex: -1
    };
    BreakpointUpload.initUploader = function () {
        //初始变量定义
        chooseFile = [];
        var dropZone = document.getElementById("dndArea");
        var shadow = $(".shadow")[0];
        dropZone.addEventListener("dragover", dragover = function (e) {
            $(".shadow").show();
            $(".webuploader-pick").parent().hide();
            $("#dndArea").css("opacity", "0.3");
            $(".btns").css("pointer-events","none");
        }, false);
        dropZone.addEventListener("dragleave", dragleave = function (e) {
            $("#dndArea").css("opacity", "1");
            $(".shadow").hide();
            $(".webuploader-pick").parent().show();
            $(".btns").css("pointer-events","auto");
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
                    //$("#continueUpload").show();
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
                            /*$.ajax({
                                    type: "post",
                                    url: Hussar.ctxPath+"/breakpointUpload/checkVersionMd5Exist",
                                    data: {
                                        fileName: fileName,
                                        categoryId: categoryId,
                                        visible: "0",
                                        downloadAble: "0",
                                        watermarkUser: "",
                                        watermarkCompany: "",
                                        fileMd5: fileMd5,
                                        group: "",
                                        person: "",
                                        shareable:shareable,
                                        isVersion:"1",
                                        oldDocId:$("#oldDocId").val()
                                    },
                                    async: false,
                                    cache: false,
                                    contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                                    dataType: "json",
                                    success: function (data) {
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
                                        }
                                    }
                                }
                            );*/
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
                            ajax.set("shareable",shareable);
                            ajax.set("isVersion","1");
                            ajax.set("oldDocId",$("#oldDocId").val());
                            ajax.start();
                            //获取文件信息后进入下一步
                            deferred.resolve();

                        });

                    return deferred.promise();

                },
                //时间点2：如果有分块上传，则每个分块上传之前调用此函数
                beforeSend: function (block) {
                    if (fastFlag == 0 || fastFlag == 1 || fastFlag == 2 || fastFlag == 6 || fastFlag == 7 || fastFlag == 8 || fastFlag == 9) {
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
                    ajax.set("shareable",shareable);// 分享标识
                    ajax.set("oldDocId",$("#oldDocId").val());// 旧版本文档的ID
                    ajax.start();

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
                    if (fastFlag == 0 || fastFlag == 1 || fastFlag == 2 || fastFlag == 6 || fastFlag == 7 || fastFlag == 8 || fastFlag == 9) {

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
                    ajax.set("shareable",shareable);
                    ajax.set("oldDocId",$("#oldDocId").val());// 旧版本文档的ID
                    ajax.start();

                }
            }
        );//监听结束

        uploader = WebUploader.create({
            auto: true, //是否自动上传
            pick: {
                id: '#picker',
                label: '选择文件',
                multiple: false
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
            chunkSize: 10 * 1024 * 1024, //每片10M
            chunkRetry: 3,//如果失败，则不重试
            threads: 1,//上传并发数。允许同时最大上传进程数。
            fileNumLimit: 1,//验证文件总数量, 超出则不允许加入队列
            fileSizeLimit: 6 * 1024 * 1024 * 1024,//6G 验证文件总大小是否超出限制, 超出则不允许加入队列
            fileSingleSizeLimit: 3 * 1024 * 1024 * 1024,  //3G 验证单个文件大小是否超出限制, 超出则不允许加入队列
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
            $(".btns").css("pointer-events","auto");
            //限制单个文件的大小 超出了提示
            if (file.size > 3 * 1024 * 1024 * 1024) {
                alert("单个文件大小不能超过3G！");
                return false;
            }
            filesArr.push(file);
            success++;
            var ajax = new $ax(Hussar.ctxPath+"/breakpointUpload/selectProgressByFileName",
                function (data) {
                    var res = eval('(' + data + ')');
                    //上传过程
                    if (res.jindutiao > 0) {
                        //上传过的进度的百分比
                        oldJindu = res.jindutiao / 100;
                        //如果上传过 上传了多少
                        var jindutiaoStyle = "width:" + res.jindutiao + "%";
                        $list.append('<div id="' + file.id + '" class="item">' +
                            '<h4 class="info"  title="' + file.name + '">' + file.name + '</h4>' +
                            '<p class="state">已上传' + res.jindutiao + '%</p>' +
                            '<a href="javascript:void(0);"  style="float:right;width: 150px" class=" delete btnRemoveFile"></a>' +
                            '</div>');
                        //将上传过的进度存入map集合
                        map[file.id] = oldJindu;
                    } else {//没有上传过
                        $list.append('<div id="' + file.id + '" class="item">' +
                            '<h4 class="info" title="' + file.name + '">' + file.name + '</h4>' +
                            '<p class="state">等待上传...</p>' +
                            '<a style="float:right;width: 150px" href="javascript:void(0);" class=" delete btnRemoveFile"></a>' +
                            '</div>');
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
            // 限制一次性上传的文件数量
            if (file.length > 1) {
                layer.msg("只能选择一个文件上传", {anim:6,icon: 0});
                return false;
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
            $(".btns").css("pointer-events","auto");
            // uploadAmount=0;
            // $("#continueUpload").show();
        })
        //上传成功后执行的方法
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
                $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                $("#setFileAuthority").addClass("hide");
                $("#setFileTip").addClass("hide");
                layer.msg("请先选择目录", {anim:6,icon: 0});
                return false;
            }
            // $("#continueUpload").hide();
        })
        uploader.on('uploadSuccess', function (file) {
            $(".btns").css("pointer-events","auto");
            //var dragZone = $("#dndArea")[0];
            //dragZone.removeEventListener("dragover",dragover);
            //dragZone.removeEventListener("dragleave",dragleave);
            if (flag == 0) {
                $('#' + file.id).find('p.state').text('文件已存在')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
            } else if (flag == 3) {
                $('#' + file.id).find('p.state').text('文件上传失败')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
                fastFlag = null;

            }
            else if (flag == 4) {
                $('#' + file.id).find('p.state').text('空间不足')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
            } else if (fastFlag == 2) {
                $('#' + file.id).find('p.state').text('文件已存在')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 1) {
                $('#' + file.id).find('p.state').text('空间不足')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 6) {
                $('#' + file.id).find('p.state').text('名称过长')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 7) {
                $('#' + file.id).find('p.state').text('名称不合法')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 8) {
                $('#' + file.id).find('p.state').text('格式不支持')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            }else if (fastFlag == 9){
                $('#' + file.id).find('p.state').text('存在历史版本')
                $('#' + file.id).find('.btnRemoveFile').addClass("delete");
            } else if (fastFlag == 0) {

                // 隐藏删除按钮
                $('#' + file.id).find('.btnRemoveFile').removeClass("btnRemoveFile");
                //隐藏上传按钮
                success--;
                if (success == 0) {

                }
                $('#' + file.id).find('p.state').text('')
                $('#' + file.id).find('p.state').addClass("success").append('秒传');
                // refreshFile(openFileId)
                fastFlag = null;
                $("#setFileAuthority").removeClass("hide");
                $("#setFileTip").removeClass("hide");

            }
            else {  //上传成功去掉进度条

                //隐藏删除按钮
                $('#' + file.id).find('.btnRemoveFile').removeClass("btnRemoveFile");
                //上传成功，获得积分
                //$.ajax({
                //    url: "/integral/addIntegral",
                //    async: true,
                //    data:{
                //        docId:'',
                //        ruleCode: 'upload'
                //    },
                //    success: function (data) {
                //        if (data.integral != 0 && data.integral != null && data.integral != ''){
                //            $("#num").html(data.msg)
                //            if(data.msg=="积分不足"||data.msg=="已达上限"){
                //                $(".integral .point").hide();
                //                $(".integral .num") .css({"width":"81px","padding-top":"43px"})
                //            }
                //            $(".integral").show();
                //            // 实时更新积分
                //            $("#totalIntegral",parent.document).text(parent.getTotalIntegral());
                //            setTimeout(function () {
                //                $(".integral .point").show();
                //                $(".integral .num") .css({"width":"40px","padding-top":"0"})
                //                $(".integral").hide();
                //
                //            },2000)
                //        }
                //    }
                //});
                //隐藏上传按钮
                success--;
                if (success == 0) {

                }
                $('#' + file.id).find('p.state').text('')
                $('#' + file.id).find('p.state').addClass("success").append('上传')

                $("#setFileAuthority").removeClass("hide");
                $("#setFileTip").removeClass("hide");
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
                        $(".webuploader-pick").parent().show();
                        $("#dndArea").css("opacity", "1");
                        $("#setFileAuthority").addClass("hide");
                        $("#setFileTip").addClass("hide");
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
            $('#' + file.id).find('p.state').text('上传出错，请检查网络连接');
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
            if (type == "Q_TYPE_DENIED") {
                $(".shadow").hide();
                $(".webuploader-pick").parent().show();

                $("#dndArea").css("opacity", "1");
                layer.msg("存在不支持上传的文档格式", {anim: 6, icon: 0});
            } else if (type == "Q_EXCEED_SIZE_LIMIT") {
                $(".shadow").hide();
                $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("文件大小不能超过3G", {anim: 6, icon: 0});
            } else if (type == "Q_EXCEED_NUM_LIMIT") {
                $(".shadow").hide();
                $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传列表中不得超过1个文件", {anim: 6, icon: 0});
            } else if (type == "F_DUPLICATE") {
                $(".shadow").hide();
                $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传列表中存在重复文件", {anim: 6, icon: 0});
            } else {
                $(".shadow").hide();
                $(".webuploader-pick").parent().show();
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
                $(".webuploader-pick").parent().show();
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
            url:Hussar.ctxPath+"/files/getLoginUser",
            async:true,
            cache:false,
            dataType:"json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
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

    /*修改文件标签*/
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
            area: ['500px'], //宽高
            fix: false, //不固定
            maxmin: false,
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
            var index = layer.confirm("确认要将上传的文件设为不可分享吗？",{title :['信息','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'}, function () {
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
    /*修改文件权限*/
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
            area: ['686px', '510px'], //宽高
            offset:scrollHeightLong,
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
        $(this).animate({scrollLeft:scrollWidth},8000,'linear');
    }, function () {
        $(this).stop(true,false);
    });

//页面初始化
    $(function () {
        getLoginUser();
        // 当从我的上传页面的上传按钮跳转过来时，接收参数，并给成员变量赋值
        if ($("#path").val() != ''){
            $("#selectFolder .folderinner").text($("#path").val());
        }
        if ($("#openFileId").val() != ""){
            categoryId = $("#openFileId").val();
        }
        // folderList();
        BreakpointUpload.initUploader();
        BreakpointUpload.initButtonEvents();
    });
})
/* 设置文件的分享状态*/
function editShareFlag(){
    chooseFile=[];
    chooseFile=chooseUploadFile;
    for(var i=0;i<chooseFile.length;i++){
        //var categoryName = parent.chooseFileName[i];
        var fileId = chooseFile[i];
        // var authorId = parent.chooseFileAuthor[i];

        /** 调用权限的更新方法 */

        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

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
                    console.log(result);
                }
            });*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/setShareFlag", function(result) {
                console.log(result);
            }, function(data) {

            });
            ajax.set("docId",fileId);
            ajax.set("shareFlag",shareable);
            ajax.start();
        });

    }

}
function  returnList() {
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
    parent.refreshFile(parent.openFileId);
    //setTimeout(function () {
    //    parent.changeBgColorOfTr($("#"+ $("#oldDocId").val(),parent.document)[0]);
    //},1000);
}
// 修改继续上传方式
function continueUpload2(){
    $(".webuploader-pick").trigger("click");
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
}function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").css("top",scrollHeightAlert);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}