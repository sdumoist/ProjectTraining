/**
* @Description:    断点续传Demo脚本文件
* @Author:         LiangDong
* @CreateDate:     2018/9/4 9:59
* @UpdateUser:     LiangDong
* @UpdateDate:     2018/9/4 9:59
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
var tableIns;//表格
var files;//上传的批量文件
var oldData;//表格的缓存数据
var userId = "";
var userName = "";
var downloadAble;//是否允许下载
var groupId=[];
var personId=[];
var personParam = [];
var groupParam = [];
var flag;
var fastFlag;
var percentageFlag;
layui.extend({
    admin: '{/}../../../static/resources/weadmin/static/js/admin'
});
layui.use(['jquery','layer','Hussar','jstree','HussarAjax','form', 'element'], function() {
    var Hussar = layui.Hussar;
    var $ = layui.jquery;
    var layer = layui.layer;
    var $ax = layui.HussarAjax;
    var form = layui.form;
    var element = layui.element;

    $(".webuploader-pick").addClass("layui-btn");
    /**
     * 断点续传Demo的单例
     */
    var BreakpointUpload = {
        layerIndex: -1
    };
    $("#cancel").on('click',function(){
        var index = layer.getFrameIndex(window.name);
        layer.close(index);
    });
    form.on('radio(visible)', function (data) {
        if (data.value == "0"){
            $('#setAuthority').addClass('layui-btn-disabled').attr('disabled',"true");
            $('.name-list').hide();
        }else {
            $('#setAuthority').removeClass('layui-btn-disabled').removeAttr('disabled',"false");
            $('.name-list').show();
        }
        form.render();
    });
    form.on('checkbox(watermark_company_isChecked)', function(obj){
        var check = $("input[name='watermark_company_isChecked']:checked").val();
        if (check!= undefined&&check=='on') {
            $('#watermark_company').removeClass('layui-disabled').removeAttr('disabled',"false");
        } else {
            $('#watermark_company').addClass('layui-disabled').attr('disabled',"true");
        }
        form.render();
    });
    $("#setAuthority1").click(function(){
       layer.open({
            type: 2,
            title: '选择可见范围',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath+'/fsFile/authority',
            success: function(layero, index) {
            }
        });
    });
    function getLoginUser(){
        $.ajax({
            type:"post",
            url:"/files/getLoginUser",
            async:true,
            cache:false,
            dataType:"json",
            success:function(result){
                if(result){
                    userId = result.userId;
                    userName = result.userName;
                }
            }, error:function(data) {
                Hussar.error("获取登陆人失败");
            }
        });
    }
    //监听允许下载操作
    form.on('checkbox(allowDownload)', function(obj){
        for(var i = 0;i < oldData.length;i++){
            if(oldData[i].id == obj.elem.dataset.id){
                if(obj.elem.checked){
                    oldData[i].allowDownload = "1";
                    obj.elem.value = '1';
                }else{
                    oldData[i].allowDownload = "0";
                    obj.elem.value = '0';
                }

            }
        }
    });
    function treeSearch(treeId,searchId,username) {
        $("#"+searchId).val("");
        $(".jstree-search").remove();
        $(".search-results").html("");
        var $tree = $("#"+treeId);
        var to = false;
        //用户树查询
        $("#"+searchId).keyup(function () {
            if (to) { clearTimeout(to); }
            to = setTimeout(function () {
                var v = $("#"+searchId).val();
                if(v==null||v==""){
                    v =username;
                }
                var temp = $tree.is(":hidden");
                if (temp == true) {
                    $tree.show();
                }
                $tree.jstree(true).search(v);
                //添加索引
                if(v!=''){
                    var n = $(".jstree-search").length,con_html;
                    if(n>0){
                        con_html = "<em>"+ n +"</em>个匹配项";
                    }else{
                        con_html = "无匹配项";
                    }
                    $(".search-results").html(con_html);
                }else {
                    $(".search-results").html("");
                }

            }, 250);
        });
        if(username!=null&&username!=""){
            var e = $.Event("keyup");//模拟一个键盘事件
            e.keyCode = 13;//keyCode=13是回车
            $("#"+searchId).trigger(e);//模拟页码框按下回车
        }
    }
    /**
     * 初始化上传组件
     */
    BreakpointUpload.initUploader = function () {
        //初始变量定义

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
// 监听分块上传过程中的三个时间点
        WebUploader.Uploader.register({
                "before-send-file":"beforeSendFile",//整个文件上传前
                "before-send":"beforeSend",  //每个分片上传前
                "after-send-file":"afterSendFile"  //分片上传完毕
            },
            {
                //时间点1：所有分块进行上传之前调用此函数
                beforeSendFile:function(file){
                    $(".popWin").css("display","block").removeClass("success");
                    fileId=file.id;
                    var deferred = WebUploader.Deferred();
                    //1、计算文件的唯一标记fileMd5，用于断点续传  如果.md5File(file)方法里只写一个file参数则计算MD5值会很慢 所以加了后面的参数：10*1024*1024
                    (new WebUploader.Uploader()).md5File(file,0,10*1024*1024).progress(function(percentage){

                        percentageFlag=percentage;
                        $('#'+file.id ).find('p.state').text('正在读取文件信息...');

                    })
                        .then(function(val){

                            $('#'+file.id ).find("p.state").text("成功获取文件信息...");
                            fileMd5=val;
                            fileName=file.name; //为自定义参数文件名赋值

                            $.ajax({
                                    type:"post",
                                    url:/*Hussar.ctxPath+*/"/breakpointUpload/checkMd5Exist",
                                    data:{
                                        fileName:fileName,
                                        categoryId:categoryId,
                                        visible:"0",
                                        downloadAble:"0",
                                        watermarkUser:"",
                                        watermarkCompany:"",
                                        fileMd5:fileMd5,
                                        group:"",
                                        person:""

                                    },
                                    async:false,
                                    cache:false,
                                    dataType:"json",
                                    success:function(data){
                                        if(data.code=='2'){
                                            layer.msg("本部门可用存储空间不足", {anim:6,icon: 0});
                                            fastFlag=1;
                                        }
                                        if(data.code=='4'){
                                            layer.msg("”"+data.name+"“文件已存在", {anim:6,icon: 0});
                                            fastFlag=2;
                                        }
                                        if(data.code=='5'){
                                            fastFlag=0 ;
                                        }
                                    }
                                }
                            );
                            //获取文件信息后进入下一步
                            deferred.resolve();

                        });

                    return deferred.promise();

                },
                //时间点2：如果有分块上传，则每个分块上传之前调用此函数
                beforeSend:function(block){
                    if(fastFlag==0||fastFlag==1||fastFlag==2){
                        return;
                    }
                    var deferred = WebUploader.Deferred();
                    //ajax验证每一个分片
                    var ajax = new $ax(Hussar.ctxPath + "/breakpointUpload/mergeOrCheckChunks",
                        function(data) {
                            var res = eval('('+data+')');
                            if(res.ifExist){
                                //分块存在，跳过
                                deferred.reject();
                            }else{
                                //分块不存在或不完整，重新发送该分块内容
                                deferred.resolve();
                            }
                        },
                        function(data) {
                        });
                    ajax.set("param","checkChunk");
                    ajax.set("fileName",fileName);
                    ajax.set("jindutiao",$("#jindutiao").val());
                    ajax.set("fileMd5",fileMd5);//文件唯一标记
                    ajax.set("chunk",block.chunk);//当前分块下标
                    ajax.set("chunkSize",block.end-block.start);//当前分块大小
                    ajax.start();

                    this.owner.options.formData.fileMd5 = fileMd5;

                    deferred.resolve();

                    return deferred.promise();
                },
                //时间点3：所有分块上传成功后调用此函数
                afterSendFile:function(){
                    if(fastFlag==0||fastFlag==1||fastFlag==2){
                        if(fastFlag==0){
                            count++; //每上传完成一个文件 count+1
                            if(count>filesArr.length-1){
                                $(".success-msg").html("成功上传"+count+"个文件！").show(500);
                            }else {
                                uploader.upload(filesArr[count].id);//上传文件列表中的下一个文件
                            }
                        }
                        return ;
                    }

                    var code=0;
                    $('#'+fileId).find('p.state').text('转化中')
                    //如果分块上传成功，则通知后台合并分块
                    var ajax = new $ax(Hussar.ctxPath + "/breakpointUpload/mergeOrCheckChunks",
                        function(data) {
                            count++; //每上传完成一个文件 count+1
                            if(data!=""){
                                dataNew=eval('(' + data + ')')
                                if(dataNew.code==3){
                                    code=3;
                                }
                                if(dataNew.code==2){
                                    code=2;
                                }
                            }
                            if(code!=3&&count<=filesArr.length-1){
                                uploader.upload(filesArr[count].id);//上传文件列表中的下一个文件
                            }else{
                                // 合并成功之后的操作
                                if(code=="0"){
                                    $(".success-msg").html("成功上传"+count+"个文件！").show(500);
                                }
                                if(code==2){
                                    flag=4;
                                    layer.msg("本部门可用存储空间不足", {anim:6,icon: 0});

                                }
                                if(code==3){
                                    flag=3;
                                    Hussar.error("上传失败");
                                }
                            }
                        },
                        function(data) {
                        });

                    ajax.set("categoryId",categoryId);
                    ajax.set("visible","0");
                    ajax.set("downloadAble","0");
                    ajax.set("watermarkUser","");
                    ajax.set("watermarkCompany","");
                    ajax.set("group","");
                    ajax.set("person","");
                    ajax.set("param","mergeChunks");
                    ajax.set("fileName",fileName);
                    ajax.set("fileMd5",fileMd5);
                    ajax.start();

                }
            });//监听结束

        uploader = WebUploader.create({
            auto : true, //是否自动上传
            pick : {
                id : '#picker',
                label : '<i class="layui-icon">&#xea0c;</i>上传',
                multiple:true
            },
            duplicate : false, //同一文件是否可重复选择
            prepareNextFile: true,
            // 不压缩image
            resize: false,
            accept : {
                title: 'Files',
                extensions: 'gif,jpg,jpeg,bmp,png,pdf,doc,docx,txt,xls,xlsx,ppt,pptx,mp3,mp4',
                mimeTypes: 'image/*,text/*'
                //word
                +',application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document'
                //excel
                +',application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                //ppt
                +',application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation'
                +',application/pdf'

            },
            compress : null,//图片不压缩
            chunked : true, //分片
            chunkSize : 10 * 1024 * 1024, //每片10M
            chunkRetry : 3,//如果失败，则不重试
            threads : 1,//上传并发数。允许同时最大上传进程数。
            fileNumLimit : 10,//验证文件总数量, 超出则不允许加入队列
            fileSizeLimit:6*1024*1024*1024,//6G 验证文件总大小是否超出限制, 超出则不允许加入队列
            fileSingleSizeLimit:3*1024*1024*1024,  //3G 验证单个文件大小是否超出限制, 超出则不允许加入队列
            // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
            disableGlobalDnd : true,
            dnd:"#dndArea",
            // swf文件路径
            swf : '${ctxPath}/static/assets/lib/webuploader0.1.5/Uploader.swf',
            // 文件接收服务端。
            server : Hussar.ctxPath + "/breakpointUpload/fileSave"
        });

        /**
         *  当有文件添加进来的时候
         */
        uploader.on( 'fileQueued', function( file ) {
            //限制单个文件的大小 超出了提示
            if(file.size>3*1024*1024*1024){
                Hussar.info("单个文件大小不能超过3G！");
                return false;
            }
            filesArr.push(file);
            success++;
               var ajax = new $ax(Hussar.ctxPath + "/breakpointUpload/selectProgressByFileName",
                function(data) {
                    var res = eval('('+data+')');
                    //上传过程
                    if(res.jindutiao>0){
                        //上传过的进度的百分比
                        oldJindu=res.jindutiao/100;
                        //如果上传过 上传了多少
                        var jindutiaoStyle="width:"+res.jindutiao+"%";
                        $list.append( '<div id="' + file.id + '" class="item">' +
                            '<h4 class="info">' + file.name + '</h4>' +
                            '<p class="state">已上传'+res.jindutiao+'%</p>' +
                            '<a href="javascript:void(0);"  style="float:right;width: 150px" class="delete btnRemoveFile"></a>' +
                            '</div>' );
                        //将上传过的进度存入map集合
                        map[file.id]=oldJindu;
                    }else{//没有上传过
                        $list.append( '<div id="' + file.id + '" class="item">' +
                            '<h4 class="info">' + file.name + '</h4>' +
                            '<p class="state">等待上传...</p>' +
                            '<a style="float:right;width: 150px" href="javascript:void(0);" class="delete btnRemoveFile"></a>' +
                            '</div>' );
                    }
                },
                function(data) {

                });
            ajax.set("fileName",file.name);     //文件名
            ajax.start();

            uploader.stop(true);
            //删除队列中的文件
            $(".btnRemoveFile").bind("click", function() {
                var fileItem = $(this).parent();
                uploader.removeFile($(fileItem).attr("id"), true);
                $(fileItem).fadeOut(function() {
                    $(fileItem).remove();
                });

                //数组中的文件也要删除
                for(var i=0;i<filesArr.length;i++){
                    if(filesArr[i].id==$(fileItem).attr("id")){
                        filesArr.splice(i,1);//i是要删除的元素在数组中的下标，1代表从下标位置开始连续删除一个元素
                    }
                }
                //隐藏上传按钮
                success--;
                if(success == 0){
                }
            });
        });

        //文件上传过程中创建进度条实时显示
        uploader.on('uploadProgress', function(file, percentage) {
            var $li = $( '#'+file.id );

            //避免重复创建

            //将实时进度存入隐藏域
            $("#jindutiao").val(Math.round(percentage * 100));
            //根据fielId获得当前要上传的文件的进度
            var oldJinduValue = map[file.id];

            if(percentage<oldJinduValue && oldJinduValue!=1){
                $li.find('p.state').text('上传中'+Math.round(oldJinduValue * 100) + '%');

                if(oldJinduValue==1){
                    $li.find('p.state').text('转化中...');
                }
            }else{
                $li.find('p.state').text('上传中'+Math.round(percentage * 100) + '%');

                if(percentage==1){
                    $li.find('p.state').text('转化中...');
                }

            }
        });

        //上传成功后执行的方法
        uploader.on('uploadSuccess', function( file ) {
            if(flag==0){
                $('#'+file.id).find('p.state').text('文件已存在')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                flag=1;
            }else if(flag==3){
                $('#'+file.id).find('p.state').text('文件上传失败')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                flag=1;
            }
            else if(flag==4){
                $('#'+file.id).find('p.state').text('空间不足')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                flag=1;
            }else if(fastFlag==2){
                $('#'+file.id).find('p.state').text('文件已存在')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag=null;
            }else if(fastFlag==1){
                $('#'+file.id).find('p.state').text('空间不足')
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                fastFlag=null;
            }else if(fastFlag==0){
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                //隐藏删除按钮

                //隐藏上传按钮
                success--;
                if(success == 0){

                }
                $('#'+file.id).find('p.state').text('文件已秒传成功');
                refreshFile(openFileId)
                fastFlag=null;


            }
            else{  //上传成功去掉进度条
                $('#'+file.id).find('.btnRemoveFile').addClass("delete");
                //隐藏删除按钮

                //隐藏上传按钮
                success--;
                if(success == 0){

                }
                $('#'+file.id).find('p.state').text('文件已上传成功');}
                refreshFile(openFileId)
                fastFlag=null;

        });

        //上传出错后执行的方法
        uploader.on('uploadError', function( file ) {
            errorUpload=true;
            uploader.stop(true);
            $('#'+file.id).find('p.state').text('上传出错，请检查网络连接');
        });

        //文件上传成功失败都会走这个方法
        uploader.on('uploadComplete', function( file ) {

        });

        uploader.on('all', function(type){
            if (type === 'startUpload'){
                state = 'uploading';
            }else if(type === 'stopUpload'){
                state = 'paused';
            }else if(type === 'uploadFinished'){
                state = 'done';
            }
        });

    };

    /**
     *  初始化按钮事件
     */
    BreakpointUpload.initButtonEvents = function () {

    };

    //页面初始化
    $(function () {
        getLoginUser();
        BreakpointUpload.initUploader();
        BreakpointUpload.initButtonEvents();

        //弹窗按钮事件添加
        $(".controls-down").click(function () {
            $(".popWin").toggleClass("success");
            if($(".popWin").hasClass("success")){
                $(this).html("&#xe619;")
            }else {
                $(this).html("&#xe61a;")
            }

        });
        $(".controls-close").click(function () {
            $(".popWin").css("display","none")
        })
    });

});