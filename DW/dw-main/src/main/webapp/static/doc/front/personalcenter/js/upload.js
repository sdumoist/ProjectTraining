/**
 * Created by smt on 2018/6/30.
 */
var hussar;
var openFileId;   //打开的文件夹的id
var chooseFile = [];    //选中的文件或目录的id
var chooseUploadFile = [];    //选中的文件或目录的id
var chooseUploadAuthor = [];    //选中的文件或目录的id
var chooseFileType = []; //选中的文件或目录的type
var chooseFileAuthor = []; //选中的文件或目录的type
var chooseFileName = []; //选中的文件或目录的name
var cutFile = [];          //剪切的文件或目录的id
var cutFileType = [];      //剪切的文件或目录的type
var cutFileName = [];      //剪切的文件或目录的name
var pathId = [];        //路径
var pathName = [];
var key='';
var adminFlag;
var userId;
var docAddOpen;
var categoryId;
var isChild;
var reNameFlag= false;      //重命名标志
var reNameParem='';
var reNameIndex='';
var clickFlag=false;
var treeData;
var authorIdSnap ="";//作者ID临时
var authorNameSnap ="";//作者名字临时
var contactsIdSnap ="";//联系人ID临时
var contactsNameSnap ="";//联系人名字临时
var groupId=[];
var personId=[];
var personParam = [];
var groupParam = [];
var folderMessage='请选择文件';
var editFlag = false;
var showStyle=2;
var noChildPower=0;
var layerView;
var dbclickover=true;
var tableIns;//表格
var files;//上传的批量文件
var oldData;//表格的缓存数据
var userName = "";
var downloadAble;//是否允许下载
var flag;
var fastFlag;
var percentageFlag;
var powerFlag=0;

var $list = $('#thelist'),//文件列表
    state = 'pending',//初始按钮状态
    uploader; //uploader对象
var fileMd5;  //文件唯一标识
var fileName;//文件名称
var oldJindu;//如果该文件之前上传过 已经上传的进度是多少
var count=0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
var success=0;//上传成功的文件数
var filesArr=new Array();//文件数组：每当有文件被添加进队列的时候 就push到数组中
var map={};//key存储文件id，value存储该文件上传过的进度
var fileId;
var currOrder = '';// 存放当前排序类型
var scrollHeightAlert=0;
var scrollHeightLong=0;
var scrollHeightShare = 0;
var scrollHeightTip = 0;
var scrollHeightMsg = 0;
var currPage = 1;
var htmlFlag = 'upload';
var oldDocId = '';
var newDocId = '';// 上传成功的docId
var fastDocId = '';// 秒传成功的文件id
layui.extend({
    admin: '{/}../../../static/resources/weadmin/static/js/admin'
});
layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function() {
    var $ = layui.jquery,
        form=layui.form,
        jstree=layui.jstree,
        laypage = layui.laypage,
        Hussar  = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;
    hussar = layui.Hussar;
    //初始化树

    start();

    /*搜索按钮*/
    $("#searchBtn").on('click',function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
            ,offset: scrollHeightAlert
        });
        refreshFile(null,null);
        layer.close(index);
    });
    $("#reName").on('click',function(){



        var editType = chooseFileType[0];
        editFlag = true;
        groupId = [];
        groupParam = [];
        personId = [];
        personParam= [];
        var type;
        if (editType == "folder"){

        }else {
            layer.open({
                type: 2,
                title: ['权限设置','background-color:#fff'],
                skin:'permission-dialog-file',
                fix: false, //不固定
                offset:scrollHeightLong,
                //maxmin: false,
                content: Hussar.ctxPath+'/fsFile/fileAuthority',
                success:function(){
                }
            });

        }
    });

    /*多选下载*/
    $("#mulDownLoad, #manyMulDownLoad").on('click',function(){
        if(chooseFile.length==0){
            layer.msg("请选择要下载的文件", {anim:6,icon: 0,offset:scrollHeightMsg});
            return;
        }
        var index = layer.load(1, {
            shade: [0.1,'#fff'] ,//0.1透明度的白色背景
            scrollbar: false,
            time:1000
            ,offset: scrollHeightAlert
        });
        var ids=chooseFile.join(",");
        var name=chooseFileName.join("*");
        download(ids,name);

        // layer.close(index);
    });
    /*加入专题*/


    function start() {
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");

        btnState()
    }


    $("#cancel").on('click',function(){
        var index = layer.getFrameIndex(window.name);
        layer.close(index);
    });

    function getLoginUser(){
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/files/getLoginUser",
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
                ;
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
    /**
     * 断点续传Demo的单例
     */
    var BreakpointUpload = {
        layerIndex: -1
    };
    /**
     * 初始化上传组件
     */
    BreakpointUpload.initUploader = function () {
        //初始变量定义
        chooseFile = [];
        var dropZone = document.getElementById("dndArea");
        //dropZone.addEventListener("dragover", function (e) {
        //    $(".shadow").show();
        //    $(".webuploader-pick").parent().hide();
        //    $("#dndArea").css("opacity", "0.3");
        //    $(".btns").css("pointer-events","none");
        //}, false);
        //dropZone.addEventListener("dragleave", function (e) {
        //    $("#dndArea").css("opacity", "1");
        //    $(".shadow").hide();
        //    $(".webuploader-pick").parent().show();
        //    $(".btns").css("pointer-events","auto");
        //}, false);
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
                    // $(".webuploader-pick").parent().show();
                    fileId = file.id;
                    if (categoryId == undefined) {
                        //$(".popWin").css("display", "none");
                        //$("#layui-layer-shade1").remove();
                        return;
                    }
                    // if (noChildPower == 0 && adminFlag != 1) {
                    //     $(".popWin").css("display", "none")
                    //     return;
                    // }
                    //$(".popWin").css("top", parseInt(scrollHeightTip) - 25 + "px");
                    //$("body").append('<div class="layui-layer-shade" id="layui-layer-shade1" times="1" style="z-index: 19891014; background-color: rgb(0, 0, 0); opacity: 0.3;"></div>')
                    //$(".popWin").css("display", "block");
                    popWin = layer.open({
                        type: 1,
                        title: false,
                        area: ['850px', '226px'], //宽高
                        fix: false, //不固定
                        offset:parseInt(scrollHeightTip) + 12 + "px",
                        maxmin: false,
                        content: $('.popWin'),
                        success:function(layero, index){
                        },
                        end: function () {
                            //最开始进入页面时清除原来的列表
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
                            refreshFile(currPage,60,currOrder);
                            setTimeout(function () {
                                try {
                                    if (fastDocId != ''){
                                        changeBgColorOfTr($("#"+ fastDocId)[0]);// 秒传
                                        fastDocId == '';
                                    }else{
                                        changeBgColorOfTr($("#"+ newDocId)[0]);// 上传
                                    }
                                }catch (e){
                                    console.log(e);
                                    changeBgColorOfTr($("#"+ oldDocId)[0]);// 上传异常被阻止
                                    oldDocId = '';
                                }
                            },1000);
                        }
                    });
                    //$("#continueUpload").show();
                    $(".dragArea").css("display", "none");
                    $(".upload-tip").css("display", "none");


                    var deferred = WebUploader.Deferred();
                    //1、计算文件的唯一标记fileMd5，用于断点续传  如果.md5File(file)方法里只写一个file参数则计算MD5值会很慢 所以加了后面的参数：10*1024*1024
                    (new WebUploader.Uploader()).md5File(file, 0, 10 * 1024 * 1024).progress(function (percentage) {

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
                                        shareable:'1',
                                        isVersion:"1",
                                        oldDocId:oldDocId
                                    },
                                    async: false,
                                    cache: false,
                                    dataType: "json",
                                    contentType : "application/x-www-form-urlencoded;charset=UTF-8",
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
                                            fastDocId = data.id;
                                        }
                                    }
                                });*/
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
                                    fastDocId = data.id;
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
                            ajax.set("oldDocId",oldDocId);
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
                    ajax.set("shareable",'1');// 分享标识
                    ajax.set("oldDocId",oldDocId);// 旧版本文档的ID
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
                                    newDocId = dataNew.id;
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
                    ajax.set("oldDocId",oldDocId);// 旧版本文档的ID
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
            //dnd: "#dndArea",
            dnd:undefined,
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
                            //'<a href="javascript:void(0);"  style="float:right;width: 150px" class=" delete btnRemoveFile"></a>' +
                            '</div>');
                        //将上传过的进度存入map集合
                        map[file.id] = oldJindu;
                    } else {//没有上传过
                        $list.append('<div id="' + file.id + '" class="item">' +
                            '<h4 class="info" title="' + file.name + '">' + file.name + '</h4>' +
                            '<p class="state">等待上传...</p>' +
                            //'<a style="float:right;width: 150px" href="javascript:void(0);" class=" delete btnRemoveFile"></a>' +
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
                layer.msg("只能选择一个文件上传", {anim:6,icon: 0,offset:scrollHeightMsg});
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
                // $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                $("#setFileAuthority").addClass("hide");
                $("#setFileTip").addClass("hide");
                layer.msg("请先选择目录", {anim:6,icon: 0,offset:scrollHeightMsg});
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
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
            } else if (flag == 3) {
                $('#' + file.id).find('p.state').text('文件上传失败')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
                fastFlag = null;

            }
            else if (flag == 4) {
                $('#' + file.id).find('p.state').text('空间不足')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                flag = 1;
            } else if (fastFlag == 2) {
                $('#' + file.id).find('p.state').text('文件已存在')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 1) {
                $('#' + file.id).find('p.state').text('空间不足')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 6) {
                $('#' + file.id).find('p.state').text('名称过长')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 7) {
                $('#' + file.id).find('p.state').text('名称不合法')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            } else if (fastFlag == 8) {
                $('#' + file.id).find('p.state').text('格式不支持')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag = null;
            }else if (fastFlag == 9){
                $('#' + file.id).find('p.state').text('与历史版本相同')
                //$('#' + file.id).find('.btnRemoveFile').addClass("delete");
            } else if (fastFlag == 0) {

                // 隐藏删除按钮
                //$('#' + file.id).find('.btnRemoveFile').removeClass("btnRemoveFile");
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
                //$('#' + file.id).find('.btnRemoveFile').removeClass("btnRemoveFile");
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
                        // $(".webuploader-pick").parent().show();
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
                // $(".webuploader-pick").parent().show();

                $("#dndArea").css("opacity", "1");
                layer.msg("存在不支持上传的文档格式", {anim: 6, icon: 0,offset:scrollHeightMsg});
            } else if (type == "Q_EXCEED_SIZE_LIMIT") {
                $(".shadow").hide();
                //  $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("文件大小不能超过3G", {anim: 6, icon: 0,offset:scrollHeightMsg});
            } else if (type == "Q_EXCEED_NUM_LIMIT") {
                $(".shadow").hide();
                //$(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传列表中不得超过1个文件", {anim: 6, icon: 0,offset:scrollHeightMsg});
            } else if (type == "F_DUPLICATE") {
                $(".shadow").hide();
                // $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传列表中存在重复文件", {anim: 6, icon: 0,offset:scrollHeightMsg});
            } else {
                $(".shadow").hide();
                // $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("上传出错！请检查后重新上传！错误代码" + type, {anim: 6, icon: 0,offset:scrollHeightMsg});
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
        $(".webuploader-pick").on("click", function () {
            personParam = [];
            groupParam = [];
            editFlag = false;
            buttonType = 'upload';
            groupId = [];
            personId = [];
            // count=0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
            // success=0;//上传成功的文件数
            // filesArr=[];

            //$.ajax({
            //    type:'post',
            //    url:'',
            //    data:{oldDocId:oldDocId},
            //    async:false,
            //    success: function (data) {
            //        if (){
            //
            //        }
            //    }
            //})
            if (categoryId == undefined) {
                $(".shadow").hide();
                // $(".webuploader-pick").parent().show();
                $("#dndArea").css("opacity", "1");
                layer.msg("请先选择目录", {anim: 6, icon: 0, offset: scrollHeightMsg});

                return;
            }
            $(this).next().find("label").click()
        });
    },300);

    //页面初始化
    $(function () {
        var load = new Loading();
        load.init({
            target: "#dndArea"
        });
        load.start();
        setTimeout(function() {
            load.stop();
        }, 800)
        BreakpointUpload.initUploader();
        BreakpointUpload.initButtonEvents();

        getLoginUser();
        refreshFile();
    });

    $(window).resize(function () {
        var inner = $("#view");
        var tableWidth =inner.width();
        //fixed-table-header
        $(".fixed-table-header").width(tableWidth)
    })
    initFileTree=function () {
        var $tree = $("#fileTree2");
        var openFolder = null;
        $tree.jstree("destroy");    //二次打开时要先销毁树
        $tree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": Hussar.ctxPath+"/fsFile/getMoveTreeDataLazy?lazy",
                    "data": function (node) {
                        return {"id": node.id,"type":"1"
                        };
                    }
                },
                themes:{
                    theme : "default",
                    dots:false,// 是否展示虚线
                    icons:true,// 是否展示图标
                }
            },
            types: {
                "closed" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/treeFile.png",
                },
                "default" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/treeFile.png",
                },
                "opened" : {
                    "icon" : Hussar.ctxPath+"/static/resources/img/fsfile/openFile.png",
                }
            },
            plugins: [ 'types']
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            openFolder = e;
        });
        $(".layui-layer-btn0").on('click',function(){
            if(openFolder === null){
                layer.msg("请选择目录",{anim:6,icon: 0,offset:scrollHeightMsg});
            }else{
                var operation =function(){
                    if(openFileId==openFolder.node.original.id){
                        layer.msg("不能移动到当前目录",{anim:6,icon: 0,offset:scrollHeightMsg});
                        return;
                    }
                    var ajax = new $ax(Hussar.ctxPath+"/fsFile/move", function(data) {
                        if(data.result == "0"){
                            layer.msg("文件已存在",{anim:6,icon: 2,offset:scrollHeightMsg});
                        }else if(data.result == "1") {
                            $(".layui-laypage-btn").click();
                            layer.close(layerView);
                            layer.msg("移动成功",{icon: 1,offset:scrollHeightMsg});
                            refreshFile(currPage,openFileId)
                        }else if(data.result == "4") {
                            layer.msg("文件必须在底层目录",{anim:6,icon: 0,offset:scrollHeightMsg});
                        }  else {
                            layer.msg("移动失败",{anim:6,icon: 2,offset:scrollHeightMsg});
                        }
                    }, function(data) {
                        layer.msg("系统出错，请联系管理员",{anim:6,icon: 2,offset:scrollHeightMsg});
                    });
                    ajax.set("fileId",cutFile.join(","));
                    ajax.set("folderId",openFolder.node.original.id);
                    ajax.set("fileName",cutFileName.join(","));
                    ajax.start();
                }
                layer.confirm('确定要移动到此目录下吗？',{title :['移动','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},operation);
            }
        })

    }
});

function drawFile(param,showFlag) {
    //console.log(param);
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
            "adminFlag":adminFlag
        }
        var getTpl = $("#demo1").html()
            ,view = document.getElementById('view');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view");
            var tableWidth =inner.width();
            //fixed-table-header
            $(".fixed-table-header").width(tableWidth);
            if (param.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                },200);
            }else {
                $("div.noDataTip").hide();
            }
        });
    });


}

function   drawPower(isAdmin){
    if(isAdmin!=1){
        // $("#addCategoryBtn").hide();
        $("#joinTopic").hide();

    }

}
function getChildren(id,name){
    pathId.push(id);
    pathName.push(name);
    createPath();
    refreshFile(id);
}
function refreshFile(num,size,order){
    if(num!=null&&num.length>10){
        num = null;
    }
    if(size!=null&&size.length>10){
        size = null;
    }
    var screenHeight = parseInt(window.screen.availHeight);
    //console.log(screenHeight);
    if (screenHeight > 728) {
        $("#marg").css("min-height","768");
    }
    var noOrder;
    /*if(order==null||order==undefined||order==''){
        noOrder=true;
        order = '';
    }*/
    currOrder = order;
    layui.use(['laypage','layer','table','Hussar','HussarAjax'], function(){
        var laypage = layui.laypage,
            layer = layui.layer;
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var name = $('#searchName').val();
        var ajax = new $ax(Hussar.ctxPath + "/personalUpload/getlist", function(data) {
            laypage.render({
                elem: 'laypageAre'
                ,count: data.total //数据总数，从服务端得到
                ,limit: 60
                ,layout: ['prev', 'page', 'next']
                ,curr: num || 1 //当前页
                ,jump: function(obj, first){
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    currPage = obj.curr;
                    if(!first){
                        refreshFile(obj.curr,obj.limit,currOrder)
                    }
                }
            });
            $("#amount").html("已全部加载" + data.total+"个")
            $(".total").width($(".message").width() - 26*2 - 65 - $("#amount").width() )
            drawFile(data.rows);
            drawPower(data.isAdmin);
            userId=data.userId;
            noChildPower=data.noChildPower;
            isChild=data.isChild;
            emptyChoose();
            btnState();
            dbclickover = true;
            var flag=false;
            var fileIds= [];
            $(".hoverEvent").hover(function(){
                var ishover= $(this).find(".ishover");
                if(ishover.is(':hidden')){
                    $(".moreicon").hide();
                    $(this).find("td  #hoverSpan").show();
                }else{
                    $(this).find("td  #hoverSpan").hide();
                }

            },function(){
                $(this).find("td  #hoverSpan").hide();
            });
            $(".clickEvent").click(function () {
                cancelBubble();
                var  index = $(this).next().val();
                if(size==null||size==undefined){
                    size=60;
                }

                if(num==null||num==undefined){
                    num=1;
                }
                if((size*num)>data.total){
                    var lack = data.total%size;
                    if((lack-1==index||lack-2==index||lack-3==index||lack-4==index||lack-5==index||lack-6==index)&&index>=6){
                        $(this).next().next().css("bottom","30px");
                    }
                }

                if(data.total>size*(num-1)){
                    if(index==(size-1)||index==(size-2)||index==(size-3)||index==(size-4)||index==(size-5)||index==(size-6)){
                        $(this).next().next().css("bottom","30px");
                    }
                }

                $(this).parent().find(".moreicon").show();
            })
            //$("th").hover(function () {
            if(noOrder==true){
                $("#orderName").hide();
                $("#orderName1").hide();
                $("#orderTime").hide();
                $("#orderTime1").hide();
                $("#orderSize").hide();
                $("#orderSize1").hide();
                $("#orderFileState").hide();
                $("#orderFileState1").hide();
                $("#orderApprovalUser").hide();
                $("#orderApprovalUser1").hide();
                $("#orderApprovalTime").hide();
                $("#orderApprovalTime1").hide();
            }else{
                if(order== "1"){
                    $("#orderName").hide();
                    $("#orderName1").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderFileState").hide();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").hide();
                }
                if(order== "0"){
                    $("#orderName1").hide();
                    $("#orderName").show();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderFileState").hide();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").hide();
                }
                if(order== "2"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime1").hide();
                    $("#orderTime").show();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderFileState").hide();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").hide();
                }
                if(order== "3" ){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").show();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderFileState").hide();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").hide();
                }
                if(order== "6"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderSize").show();
                    $("#orderSize1").hide();
                    $("#orderFileState").hide();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").hide();
                }
                if(order== "7"){
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").show();
                    $("#orderFileState").hide();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").hide();
                }
                if (order == "11") {
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderFileState").show();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").hide();
                }
                if (order == "12") {
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderFileState").hide();
                    $("#orderFileState1").show();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").hide();
                }
                if (order == "13") {
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderFileState").hide();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").show();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").hide();
                }
                if (order == "14") {
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderFileState").hide();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").show();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").hide();
                }
                if (order == "15") {
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderFileState").hide();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").show();
                    $("#orderApprovalTime1").hide();
                }
                if (order == "16") {
                    $("#orderName").hide();
                    $("#orderName1").hide();
                    $("#orderTime").hide();
                    $("#orderTime1").hide();
                    $("#orderSize").hide();
                    $("#orderSize1").hide();
                    $("#orderFileState").hide();
                    $("#orderFileState1").hide();
                    $("#orderApprovalUser").hide();
                    $("#orderApprovalUser1").hide();
                    $("#orderApprovalTime").hide();
                    $("#orderApprovalTime1").show();
                }
            }
            //}, function () {
            //    $(this).find("#orderTime").hide();
            //    $(this).find("#orderTime1").hide();
            //})
            $(".layui-table tr").hover(function () {
                //alert($(this).prev());
                $(this).find("td").css("border-color","#DAEBFE");
                $(this).prev().find("td").css("border-color","#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color","rgba(242,246,253,1)");
                $(this).prev().find("td").css("border-color","rgba(242,246,253,1)");
            });
            $(".layui-table tbody tr:first").hover(function () {
                $(this).find("td").css("border-color","#DAEBFE");
                $("thead").find("tr").css("border-bottom-color","#DAEBFE");
            }, function () {
                $(this).find("td").css("border-color","rgba(242,246,253,1)");
                $("thead").find("tr").css("border-bottom-color","rgba(242,246,253,1)");
            })

            $(".file-container-flatten").height($("body").height() - $(".toolBar").outerHeight(true) -
                $("#pathDiv").outerHeight(true) - $(".orderSearch").outerHeight(true)  - $("#laypageAre").outerHeight(true)-35);
            if(data.total==0){
                $("#laypageAre").hide();
            }else {
                $("#laypageAre").show();
            }
        }, function(data) {

        });
        ajax.set("pageNumber",num);
        ajax.set("pageSize",size);
        ajax.set("name",name);
        ajax.set("order",currOrder);
        ajax.start();
    });
}
function  returnList() {
    layer.close(popWin);
}
function getNameOrder() {
    refreshFile(null,null,1);
}
function getNameOrder1() {
    refreshFile(null,null,0);
}
function getTimeOrder() {
    refreshFile(null, null, 3);
}

function getTimeOrder1() {
    refreshFile(null, null, 2)
}
function getSizeOrder() {
    refreshFile(null, null, 7);
}
function getSizeOrder1() {
    refreshFile(null, null, 6)
}
function getFileStateOrder() {
    refreshFile(null, null, 12)
}

function getFileStateOrder1() {
    refreshFile(null, null, 11)
}

function getApprovalUserOrder() {
    refreshFile(null, null, 14)
}

function getApprovalUserOrder1() {
    refreshFile(null, null, 13)
}

function getApprovalTimeOrder() {
    refreshFile(null, null, 16)
}

function getApprovalTimeOrder1() {
    refreshFile(null, null, 15)
}
function orderByTime(){
    if ($("#orderTime").css("display") != "none"){
        getTimeOrder();
    }else {
        getTimeOrder1();
    }
}
function orderByName(){
    if ($("#orderName").css("display") != "none"){
        getNameOrder();
    }else {
        getNameOrder1();
    }
}
function orderBySize(){
    if ($("#orderSize").css("display") != "none"){
        getSizeOrder();
    }else {
        getSizeOrder1();
    }
}
function orderByFileState() {
    if ($("#orderFileState").css("display") != "none") {
        getFileStateOrder();
    } else {
        getFileStateOrder1();
    }
}

function orderByApprovalUser() {
    if ($("#orderApprovalUser").css("display") != "none") {
        getApprovalUserOrder();
    } else {
        getApprovalUserOrder1();
    }
}

function orderByApprovalTime() {
    if ($("#orderApprovalTime").css("display") != "none") {
        getApprovalTimeOrder();
    } else {
        getApprovalTimeOrder1();
    }
}
/*打开分享链接*/
function share(e,docId,fileSuffixName,fileName) {
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            async: false,
            cache: false,
            dataType: "json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success: function (data) {
                if(data.code==1){
                }else if(data.code==2){
                }else if(data.code==3){
                }else if(data.code==4){
                }else{

                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
            }else if(data.code==2){
            }else if(data.code==3){
            }else if(data.code==4){
            }else{

            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
        cancelBubble();
        changeBgColorOfTr(e);
        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/personalcenter/getInfo",
            data: {
                ids: docId
            },
            async: false,
            cache: false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType: "json",
            success: function (data) {
                if (data.result == "1") {
                    layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
                } else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else {
                    var title = '';
                    var url = "/s/shareConfirm";
                    var w =  538;
                    var h = 311;
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
                    layer.open({
                        type: 2,
                        // area: [w + 'px', h + 'px'],
                        fix: false, //不固定
                        maxmin: false,
                        shadeClose: true,
                        shade: 0.4,
                        title: title,
                        closeBtn:2,
                        skin:'share-class',
                        offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                        content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                    });
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if (data.result == "1") {
                layer.msg("该文件已被删除", {anim: 6, icon: 0, offset: scrollHeightMsg});
            } else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }else {
                var title = '';
                var url = "/s/shareConfirm";
                var w =  538;
                var h = 391;
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
                layer.open({
                    type: 2,
                     area: [w + 'px', h + 'px'],
                    fix: false, //不固定
                    maxmin: false,
                    shadeClose: true,
                    shade: 0.4,
                    title: title,
                    closeBtn:2,
                    offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",
                    content: Hussar.ctxPath+url + "?fileId=" + docId + "&fileType=" + fileSuffixName + "&fileName=" + encodeURIComponent(fileName) + "&" + Math.random()
                });
            }
        }, function(data) {

        });
        ajax.set("ids",docId);
        ajax.start();
    });
}
function iconViewVersion(e,id,type,name,userName,time,size){
    cancelBubble();
    changeBgColorOfTr(e);
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layer.open({
            type: 2,
            title: false,
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            offset:scrollHeightLong,
            maxmin: false,
            content: Hussar.ctxPath+"/frontVersion/viewHistory?oldDocId=" + id + "&" + Math.random(),
            success:function(layero, index){
            }
        })
    });
}
function iconUploadVersion(e,id,foldId){
    cancelBubble();
    changeBgColorOfTr(e);
    categoryId = foldId;
    openFileId = foldId;
    oldDocId = id;
    $(".webuploader-pick").trigger("click");
}
function iconSetTip(e,id,type,name,author) {
    cancelBubble();
    changeBgColorOfTr(e);
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor = [];
    chooseFile.push(id);
    chooseFileType.push(type);
    chooseFileName.push(name);
    chooseFileAuthor.push(author)
    editFlag = true;
    groupId = [];
    groupParam = [];
    personId = [];
    personParam= [];

    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layer.open({
            type: 2,
            title: ['标签设置','background-color:#fff'],
            area: ['auto', '250px'], //宽高
            fix: false, //不固定
            maxmin: false,
            offset:scrollHeightTip,
            skin:'label-dialog',
            content: Hussar.ctxPath+'/fsFile/setTip',
            success:function(){
            }
        });
    });
}

function  iconDelete(e,id,name) {
    cancelBubble();
    changeBgColorOfTr(e);
    layer.confirm('确定要删除所选文件吗？',{title :['删除','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},function(){
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
            ,offset: scrollHeightAlert
        });
        /* if(fileIdArr.length>0){
         $.ajax({
         type:"post",
         url:"/files/deleteFile",
         data:{
         ids:fileIdArrStr
         },
         async:false,
         cache:false,
         success:function(data){
         delFileFlag = true;
         },
         error:function () {
         delFileFlag = false;
         }
         })
         }*/

        var scopeId = id;
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;

            /*$.ajax({
                type:"get",
                url: Hussar.ctxPath+"/fsFile/deleteScope",
                data:{
                    fsFileIds:scopeId,
                },
                async:true,
                cache:false,
                success:function(data){
                    if(data> 0){
                        var fileList = $("#thelist").find(".item");
                        for(var n = 0;n<fileList.length;n++){
                            var name = fileList.eq(n).find(".info").html().split(".")[0];
                            for(var m =0 ;m<chooseFileName.length;m++){
                                if(name == chooseFileName[m]){
                                    fileList.eq(n).remove();
                                    uploader.removeFile( fileList.eq(n).attr("id"),true);
                                }
                            }
                        }
                        layer.msg('删除成功',{icon: 1,offset:scrollHeightMsg})
                    }else {
                        layer.msg('删除成功',{icon: 1,offset:scrollHeightMsg})
                    }
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                },
                error:function () {
                    layer.msg('删除成功',{icon: 1,offset:scrollHeightMsg})
                    btnState();
                    // refreshTree();
                    refreshFile(openFileId);
                    emptyChoose();
                    layer.close(index);
                }
            })*/
            var ajax = new $ax(Hussar.ctxPath + "/fsFile/deleteScope", function(data) {
                if(data> 0){
                    var fileList = $("#thelist").find(".item");
                    for(var n = 0;n<fileList.length;n++){
                        var name = fileList.eq(n).find(".info").html().split(".")[0];
                        for(var m =0 ;m<chooseFileName.length;m++){
                            if(name == chooseFileName[m]){
                                fileList.eq(n).remove();
                                uploader.removeFile( fileList.eq(n).attr("id"),true);
                            }
                        }
                    }
                    layer.msg('删除成功',{icon: 1,offset:scrollHeightMsg})
                }else {
                    layer.msg('删除成功',{icon: 1,offset:scrollHeightMsg})
                }
                btnState();
                // refreshTree();
                refreshFile();
                emptyChoose()
                layer.close(index);
            }, function(data) {
                layer.msg('删除成功',{icon: 1,offset:scrollHeightMsg})
                btnState();
                // refreshTree();
                refreshFile();
                emptyChoose();
                layer.close(index);
            });
            ajax.set("fsFileIds",scopeId);
            ajax.start();
        });
    })
}
function  iconUpdateName(e,id,type,name,author,index) {
    cancelBubble();
    if (e != '' && e != null) {
        changeBgColorOfTr(e);
    }

    layerView = layer.open({
        type: 1,
        btn: ['确定', '取消'],
        area: ['500px', '190'],
        shadeClose: false,
        title: ['文件重命名', 'background-color:#fff'],
        maxmin: false,
        content: $("#reNameDiv"),
        skin:'rename',
        success: function () {
            $('#reNameInput').val(name);
        },
        end: function () {
            layer.closeAll();
        },
        btn1: function (index, layero) {
            var reNameValue = $('#reNameInput').val().trim();

            if (reNameValue == '' || reNameValue == undefined || reNameValue == null) {
                layer.msg("名称不能为空", {anim: 6, icon: 0, offset: scrollHeightMsg});
                return;
            }

            if (reNameValue != name) {
                    var pattern = new RegExp("^[^/\\\\:\\*\\'\\’\\?\\<\\>\\|\"]{0,255}$");
                    //特殊字符
                    if (!pattern.test(reNameValue)) {
                        layer.msg("输入的文件名称不合法", {anim: 6, icon: 0, offset: scrollHeightMsg});
                        return;
                    }
                    layui.use(['Hussar', 'HussarAjax'], function () {
                        var Hussar = layui.Hussar,
                            $ax = layui.HussarAjax;
                        var ajax = new $ax(Hussar.ctxPath + "/fsFile/addCheck", function (data) {
                            if (data == "false") {
                                layer.msg("“" + reNameValue + "”文件已存在", {anim: 6, icon: 0, offset: scrollHeightMsg});
                                return;
                            } else {
                                var ajax = new $ax(Hussar.ctxPath + "/fsFile/update", function (result) {
                                    if ("success" == result) {
                                        emptyChoose();
                                    }
                                }, function (data) {

                                });
                                ajax.set("ids", id);
                                ajax.set("fileName", reNameValue);
                                ajax.set("type", type);
                                ajax.start();
                            }
                        }, function (data) {

                        });
                        ajax.set("name", reNameValue);
                        ajax.set("filePid", openFileId);
                        ajax.start();
                    });
                refreshFile(openFileId);
            }
            layer.closeAll();
        }
    });
}
function  iconMove(e,id,type,name,author) {
    cancelBubble();
    changeBgColorOfTr(e);
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor = [];
    chooseFile.push(id);
    chooseFileType.push(type);
    chooseFileName.push(name);
    chooseFileAuthor.push(author)
    var operation =function(){
        layerView=layer.open({
            type : 1,
            area: ['400px','434px'],
            //shift : 1,
            shadeClose: false,
            btn: ['确定','取消'],
            skin: 'move-class',
            offset:scrollHeightLong,
            title : ['目录结构','background-color:#fff'],
            maxmin : false,
            content : $("#filTree"),
            success : function() {
                initFileTree();
                layer.close(index);
            },
            end: function () {
                layer.closeAll(index);
            }
        });

    }
    var index = layer.confirm('确定要移动所选文件吗？',{title :['移动','background-color:#fff'],offset:scrollHeightAlert,skin:'move-confirm'},operation);
    cutFile=[].concat(chooseFile);
    cutFileType=[].concat(chooseFileType);
    cutFileName=[].concat(chooseFileName);
}
function  iconDownLoad(e,id,name) {
    cancelBubble();
    changeBgColorOfTr(e);
    var index = layer.load(1, {
        shade: [0.1,'#fff'] ,//0.1透明度的白色背景
        scrollbar: false,
        time:1000
        ,offset: scrollHeightAlert

    });

    download(id,name);
}

function iconSetAuthority(e,id,type,name,author) {
    cancelBubble();
    changeBgColorOfTr(e);
    chooseFile=[];
    chooseFileType=[];
    chooseFileName=[];
    chooseFileAuthor = [];
    chooseFile.push(id);
    chooseFileType.push(type);
    chooseFileName.push(name);
    chooseFileAuthor.push(author)
    editFlag = true;
    groupId = [];
    groupParam = [];
    personId = [];
    personParam= [];
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layer.open({
            type: 2,
            title: ['权限设置','background-color:#fff'],
            fix: false, //不固定
            //maxmin: false,
            skin:'permission-dialog-file',
            offset:scrollHeightLong,
            content: Hussar.ctxPath+'/fsFile/fileAuthority',
            success:function(){
            }
        });
    });
}
function refreshTree(){

}
function dbclick(id,type,name){
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/personalcenter/getInfo",
            data:{
                ids:id
            },
            async:false,
            cache:false,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            dataType:"json",
            success:function(data){
                if(data.result =="1"){
                    layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
                }
                else if(data.result =="2"){
                    layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
                }else if(data.result =="5"){
                    layer.msg("该文件不是最新版本", {anim:6,icon: 0});
                }else{
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
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalcenter/getInfo", function(data) {
            if(data.result =="1"){
                layer.msg("该文件已被删除", {anim:6,icon: 0,offset:scrollHeightMsg});
            }
            else if(data.result =="2"){
                layer.msg("您没有权限", {anim:6,icon: 0,offset:scrollHeightMsg});
            }else if(data.result =="5"){
                layer.msg("该文件不是最新版本", {anim:6,icon: 0});
            }else{
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
                        refreshFile();
                    } else {
                        showPdf(id, type, name)
                    }
                }
            }
        }, function(data) {

        });
        ajax.set("ids",id);
        ajax.start();
    });
}
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
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type: "post",
            url: Hussar.ctxPath+"/fsFile/getPreviewType",
            data: {
                suffix: fileSuffixName
            },
            async: false,
            cache: false,
            dataType: "json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success: function (data) {
                if(data.code==1){
                    openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
                }else if(data.code==2){
                    openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
                }else if(data.code==3){
                    openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
                }else if(data.code==4){
                    openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
                }else{
                    openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id);
                }

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/getPreviewType", function(data) {
            if(data.code==1){
                openWin(Hussar.ctxPath+"/preview/toShowPDF?id=" + id);
            }else if(data.code==2){
                openWin(Hussar.ctxPath+"/preview/toShowIMG?id=" + id);
            }else if(data.code==3){
                openWin(Hussar.ctxPath+"/preview/toShowVideo?id=" + id);
            }else if(data.code==4){
                openWin(Hussar.ctxPath+"/preview/toShowVoice?id=" + id);
            }else{
                openWin(Hussar.ctxPath+"/preview/toShowOthers?id=" + id);
            }
        }, function(data) {

        });
        ajax.set("suffix",fileSuffixName);
        ajax.start();
    });
}


function download(id,name){
    // 取消冒泡
    //var e=arguments.callee.caller.arguments[0]||event;
    //window.event? window.event.cancelBubble = true : e.stopPropagation();
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajaxFileUpload({
            url : Hussar.ctxPath+"/files/fileDownNew?docIds=" + id,
            type : "get",
            async:false
        });
    });
    /*
     * $.ajaxFileUpload({ url:"/files/fileDownNew", type:"post", data:{
     * docName:name, fileIds:id, } });
     */
}
function changeBgColorOfTr(e){
    var jq=$(e);
    //console.log(e.tagName.toLowerCase());
    if (e.tagName.toLowerCase() != "tr"){
        jq = jq.parents(".hoverEvent");
    }
    jq.parent().find("tr").css("background-color","#fff");

    jq.css("background-color","rgba(246, 250, 255, 1)");
}
function  clickCheck(e,id,type,name,index,author) {

    var jq=$(e);
    changeBgColorOfTr(e);
    /*if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile()
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }*/
    if(jq.find(".checkbox").prop("checked")==false){

        jq.find(".checkbox").prop("checked",true);
        jq.find(".layui-form-checkbox").addClass("layui-form-checked");

        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)


    }else{
        jq.find(".checkbox").prop("checked",false);
        jq.find(".layui-form-checkbox").removeClass("layui-form-checked");
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFile=chooseFile.del(chooseFile.indexOf(id));}
            chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
        }
    }

    btnState()

    cancelBubble()
}
function  clickIconCheck(e,id,type,name,index,author) {
    $(e).toggleClass('layui-form-checked');
    var checkbox=$(e).siblings('.checkbox');
   /* if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile()
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }*/
    if(checkbox.prop("checked")==false){

        checkbox.prop("checked",true);

        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)


    }else{
        checkbox.prop("checked",false);
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFile=chooseFile.del(chooseFile.indexOf(id));}
            chooseFileAuthor=chooseFileAuthor.del(chooseFile.indexOf(id))
        }
    }

    btnState()
    cancelBubble()
}
function checkAll(e) {
    var self = $(e).siblings('input');//
    $(e).toggleClass('layui-form-checked');
    if(self.prop("checked")==false){
        self.prop("checked",true);
    }else{
        self.prop("checked",false);
    }
    if (self.prop("checked")) { // 全选
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", true);
            $(this).siblings('.layui-form-checkbox').addClass("layui-form-checked");
            var checkFileId=  $(this).siblings(".checkFileId").val();
            var checkFileType=  $(this).siblings(".checkFileType").val()
            var checkFileName=  $(this).siblings(".checkFileName").val()
            var checkFileAuthor=  $(this).siblings(".chooseFileAuthor").val()
            chooseFile.push(checkFileId);
            chooseFileType.push(checkFileType);
            chooseFileName.push(checkFileName);
            chooseFileAuthor.push(checkFileAuthor)

        });

    }
    else { // 取消全选

        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", false);
            $(this).siblings('.layui-form-checkbox').removeClass("layui-form-checked");
        });
        chooseFileType=[];
        chooseFileName=[];
        chooseFile=[];
        chooseFileAuthor=[]
    }
    btnState();
}

Array.prototype.del=function(n) {
    if(n<0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0,n).concat(this.slice(n+1,this.length));
}

$(document).keydown(function(e){
    if(e.ctrlKey){
        key=1;
    }else if(e.shiftKey){
        key=2;
    }
    //$("#bb").val("初始值:"+ibe+" key:"+key);
}).keyup(function(){
    key=0;
});


function emptyChoose() {
    chooseFile = [];
    chooseFileType = [];
    chooseFileName = [];
    chooseFileAuthor=[]
}


function tryPop(th,id,type,name,index,author){
    /*if(chooseFile.indexOf(id)==-1) {
        if (reNameFlag == true) {
            $('#name' + reNameIndex).removeClass("hide");
            $('#inputName' + reNameIndex).addClass("hide");
            reNameFlag = false;

            var inputname = $('#inputName' + reNameIndex).val();
            if (inputname != reNameParem) {
                rename(inputname);
            }
            emptyChoose();
            refreshFile()
        } else {
            reNameIndex = index;
            reNameParem=name;
        }
    }*/
    if($(th).prop("checked")){
        chooseFile.push(id);
        chooseFileType.push(type);
        chooseFileName.push(name);
        chooseFileAuthor.push(author)
    }else{
        if(chooseFile.indexOf(id)!=-1){
            if(reNameFlag == false){
                chooseFileType=chooseFileType.del(chooseFile.indexOf(id));
                chooseFileName=chooseFileName.del(chooseFile.indexOf(id));
                chooseFile=chooseFile.del(chooseFile.indexOf(id));
                chooseFileAuthor=  chooseFileAuthor.del(chooseFile.indexOf(id))
            }
        }
    }
    btnState();
    cancelBubble()
}
function btnState() {
    if(chooseFile.length==0){
        $('.clickBtn').hide()
        // $(".webuploader-pick").show();
        // if(isChild==false||(noChildPower==0&&adminFlag!=1)){
        //     $(".webuploader-pick").hide();
        // }
    }else {
        var flag=0;
        for(var i=0;i<chooseFileType.length;i++){
            if(chooseFileType[i]=="folder"){
                flag=1;
                break;

            }
        }
        if(flag=="1"){
            $('.clickBtn').hide()

        }else{
            $('.clickBtn').show()
            $('#manyMulDownLoad').hide();
            // $(".webuploader-pick").hide()
            if(chooseFile.length>1){
                $('#updateName').hide();
                $('#mulDownLoad').hide();
                $('#manyMulDownLoad').show();

            }
            if(adminFlag!=1){
                $('#joinTopic').hide()
            }

        }

    }
}

$(document).click(function(e){
    if($(e.target)[0]==$('.file-container-flatten')[0] ||$(e.target)[0]==$('.content')[0]
        ||$(e.target)[0]==$('#marg')[0]||$(e.target)[0]==$('#view ul')[0]||$(e.target)[0]== $('.table-top')[0]||$(e.target)[0]==$('.message')[0]||$(e.target)[0]==$('#laypageAre')[0]){
        $('.file').removeClass("active");
        $("input[name='checkboxname']").each(function () {
            $(this).prop("checked", false);
        });
        emptyChoose();
        btnState();
        /*if(reNameFlag==true){
            $('#name'+reNameIndex).removeClass("hide");
            $('#inputName'+reNameIndex).addClass("hide");
            reNameFlag=false;
            var inputname = $('#inputName'+reNameIndex).val().trim();
            if(inputname!=reNameParem){
                rename(inputname);
            }
        }*/
    }else{
        reNameFlag=false;
    }


});
function  stopPop() {
    window.event? window.event.cancelBubble = true : e.stopPropagation();
}
function rename(inputname){
    if(chooseFileType[0]=='folder'){
        layer.msg("不能重命名目录", {anim:6,icon: 2,offset:scrollHeightMsg});
        return;
    }
    inputname = inputname.trim();

    if(inputname==''||inputname==undefined){
        layer.msg("目录名称或文件名称不能为空", {anim:6,icon: 2,offset:scrollHeightMsg});
        $('#inputName'+reNameIndex).val(reNameParem);
        return;
    }
    var pattern = new RegExp("^[^/\\\\:\\*\\'\\’\\?\\<\\>\\|\"]{0,255}$");
    //特殊字符
    if(!pattern.test(inputname)){
        layer.msg("输入的文件名称不合法", {anim:6,icon: 2,offset:scrollHeightMsg});
        $('#inputName'+reNameIndex).val(reNameParem);
        return;
    }
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        /*$.ajax({
            type:"get",
            url: Hussar.ctxPath+"/fsFile/addCheck",
            data:{
                name:inputname,
                filePid:openFileId,
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                if(data == "false"){
                    layer.msg("“"+inputname+"”文件已存在", {anim:6,icon: 0,offset:scrollHeightMsg});
                    $('#inputName'+reNameIndex).val(reNameParem);
                    return;
                }else {
                    $.ajax({
                        type: "POST",
                        url: Hussar.ctxPath+"/fsFile/update",
                        data : {
                            ids:chooseFile[0],
                            fileName:inputname,
                            type:chooseFileType[0]
                        },
                        contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                        dataType:"json",
                        async: false,
                        success:function(result) {

                            refreshTree();
                        }
                    });
                }
            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/fsFile/addCheck", function(data) {
            if(data == "false"){
                layer.msg("“"+inputname+"”文件已存在", {anim:6,icon: 0,offset:scrollHeightMsg});
                $('#inputName'+reNameIndex).val(reNameParem);
                return;
            }else {
                /*$.ajax({
                    type: "POST",
                    url: Hussar.ctxPath+"/fsFile/update",
                    data : {
                        ids:chooseFile[0],
                        fileName:inputname,
                        type:chooseFileType[0]
                    },
                    contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                    dataType:"json",
                    async: false,
                    success:function(result) {

                        refreshTree();
                    }
                });*/
                var ajax = new $ax(Hussar.ctxPath + "/fsFile/update", function(result) {
                    refreshTree();
                }, function(data) {

                });
                ajax.set("ids",chooseFile[0]);
                ajax.set("fileName",inputname);
                ajax.set("type",chooseFileType[0]);
                ajax.start();
            }
        }, function(data) {

        });
        ajax.set("name",inputname);
        ajax.set("filePid",openFileId);
        ajax.start();
    });
    refreshFile();
}

//得到事件
function getEvent(){
    if(window.event)    {return window.event;}
    func=getEvent.caller;
    while(func!=null){
        var arg0=func.arguments[0];
        if(arg0){
            if((arg0.constructor==Event || arg0.constructor ==MouseEvent
                || arg0.constructor==KeyboardEvent)
                ||(typeof(arg0)=="object" && arg0.preventDefault
                    && arg0.stopPropagation)){
                return arg0;
            }
        }
        func=func.caller;
    }
    return null;
}
//阻止冒泡
function cancelBubble()
{
    var e=getEvent();
    if(window.event){
        //e.returnValue=false;//阻止自身行为
        e.cancelBubble=true;//阻止冒泡
    }else if(e.preventDefault){
        //e.preventDefault();//阻止自身行为
        e.stopPropagation();//阻止冒泡
    }
}
$(function(){

    setInterval(function () {
        scrollHeight=parent.scrollHeight;
        var height = parseInt(scrollHeight);
        var screenHeight = parseInt(window.screen.availHeight);
        if( scrollHeight!=0){
            scrollHeightAlert= parseInt(height - 130 + (screenHeight - 154) / 2.0) + "px";
            scrollHeightLong= parseInt(height - 130 + (screenHeight - 510) / 2.0) + "px";
            scrollHeightTip = parseInt(height - 130 + (screenHeight - 250) / 2.0) + "px";
            scrollHeightShare = parseInt(height - 130 + (screenHeight - 200) / 2.0) + "px";
            scrollHeightMsg = parseInt(height - 130 + (screenHeight - 64) / 2.0) + "px";
            //console.log(scrollHeightAlert + ' ' + scrollHeightLong)
        }
        //console.log(height + "//" + screenHeight + " " + layerHeight)
        //$(".layui-layer.layui-layer-iframe").css("top",height - 130 + (screenHeight - layerHeight) / 2.0 + "px");
    },300);
    $('#searchName').bind('keypress', function (event) {
        if (event.keyCode == "13") {
            $("#searchBtn").click();
        }
    });
})
function shareSetting(e,fileId) {
    cancelBubble();
    changeBgColorOfTr(e);
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        layer.open({
            type: 2,
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            skin:'share-setting-dialog',
            offset:scrollHeightShare,
            shade: 0.4,
            title: ['修改分享权限','background-color:#fff'],
            content: Hussar.ctxPath+"/fsFile/shareFlagView?docId=" + fileId
        });
    });
}function showIntegral(msg) {
    $("#num").html(msg);
    $(".integral").css("top",scrollHeightAlert);
    $(".integral").show();
    setTimeout(function () {
        $(".integral").hide();
    },2000)
}
function iconEditDocYozo(e,id,type,name,userName,time,size){

    cancelBubble();
    if (e != '' && e != null){
        changeBgColorOfTr(e);
    }

    if(type==".doc" || type==".docx" || type==".xls" || type==".xlsx" || type==".ppt" || type==".pptx"){
        layui.use(['Hussar','HussarAjax','HussarSecurity'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax,
                Security = new layui.HussarSecurity();
            /**--------------------------------永中在线编辑开始----------------------------------**/
            // var ajax = new $ax(Hussar.ctxPath + "/yozoOnlineEdit/editFile", function(data) {
            //     if (data) {
            //         if (data.errorCode === "0") {
            //             rst = data.result;
            //             if (rst && rst.urls) {
            //                 openUrls(rst.urls)
            //             } else {
            //                 console.warn("ajax响应内容data.result有问题：" + rst);
            //             }
            //         } else {
            //             console.error(data.errorMessage);
            //             layer.msg("编辑文档失败",{anim:6,icon: 0,offset:scrollHeightMsg});
            //         }
            //     } else {
            //         console.warn("ajax响应内容为空!");
            //         layer.msg("编辑文档失败",{anim:6,icon: 0,offset:scrollHeightMsg});
            //     }
            // }, function(data) {
            //     console.log(data);
            // });
            // ajax.set("fileId", id);
            // ajax.set("fileName", name + type);
            // ajax.start();
            /**--------------------------------永中在线编辑结束----------------------------------**/

            /**-----------------------------onlyoffice在线编辑开始----------------------------------**/
            $.ajax({
                type:"post",
                url: Hussar.ctxPath+"/files/getServerAddress",
                data: {fileId:id},
                async:true,
                cache:false,
                dataType:"json",
                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                success:function(result){
                    if(result){
                        var uId = result.userId;
                        var uName = result.userName;
                        var serverAddress = result.serverAddress;
                        var time = result.sendTime;
                        var paramMap = {};
                        paramMap["fileId"] = id;
                        paramMap["uId"] = uId;
                        paramMap["uName"] = uName;
                        paramMap["time"] =  time;
                        var params = Security.encode(encodeURI(JSON.stringify(paramMap)));
                        window.open(serverAddress + "/EditorServlet?p=" + params);
                    }
                }, error:function(data) {
                }
            });
            /**-----------------------------onlyoffice在线编辑结束----------------------------------**/
        });
    }else {
        layer.msg("此文件类型不支持在线编辑。",{anim:6,icon: 0,offset:scrollHeightMsg});
    }
}