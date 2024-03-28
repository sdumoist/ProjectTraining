var companyid ;
var userid ;
var fileValidTypeid;
var amountId;
var previewTypeId;
var imgTypeId;
var videoTypeId;
var voiceTypeId;
var companyNameid;
var folderNameid;
var companyInfoid;
var projectTitleid;
var serverAddressid;
var clientShowid;
var contactShowid;
layui.use(['layer','Hussar', 'HussarAjax','form','table','jquery','element', 'upload'], function(){
    var layer = layui.layer
        ,table = layui.table
        ,element = layui.element
        ,Hussar = layui.Hussar
        ,$ = layui.jquery
        ,upload = layui.upload
        ,$ax = layui.HussarAjax,
        laytpl = layui.laytpl,
        form = layui.form;
    $(function(){
        //初始化数据
        initdata();
        initProjectLogo();
        initHandbook();

        //保存
        $("#saveBtn").on('click',function(){
            var filetype = $("#filetype").val();
            var imgType = $("#imgType").val();
            var videoType = $("#videoType").val();
            var voiceType = $("#voiceType").val();
            var previewType = $("#previewType").val();
            var company = $("#company").val();
            var amount =$("#amount").val();
            var folderName = $("#folderName").val();
            var companyInfo = $("#companyInfo").val()
            var projectTitle = $("#projectTitle").val()
            var serverAddress = $("#serverAddress").val()

            var pattern_Info = new RegExp("^[\\s\\：\\-\\©\\a-zA-Z\\d\u4E00-\u9FA5]{0,255}$");
            var pattern = new RegExp("^[\\a-zA-Z\\d\u4E00-\u9FA5]{0,255}$");
            if (filetype.length <= 0) {
                layer.msg("上传文件类型不能为空", {anim: 6, icon: 0});
                return false;
            }
            if (imgType.length <= 0) {
                layer.msg("预览的图片类型不能为空", {anim: 6, icon: 0});
                return false;
            }

            if (videoType.length <= 0) {
                layer.msg("预览的视频类型不能为空", {anim: 6, icon: 0});
                return false;
            }
            if (previewType.length <= 0) {
                layer.msg("预览的文件类型不能为空", {anim: 6, icon: 0});
                return false;
            }
            if (voiceType.length <= 0) {
                layer.msg("预览的音频类型不能为空", {anim: 6, icon: 0});
                return false;
            }
            if (amount.length <= 0) {
                layer.msg("目录层级不能为空", {anim: 6, icon: 0});
                return false;
            }
            if (amount < 4) {
                layer.msg("目录层级不能小于四级", {anim: 6, icon: 0});
                return false;
            }
            //特殊字符:公司名称及目录名称尽可为全汉字或者汉字数字或者汉字数字英文
            if (!pattern.test(company)) {
                layer.msg("输入公司名称不合法", {anim: 6, icon: 0});
                return;
            }
            if (company.length <= 0) {
                layer.msg("公司名称不能为空", {anim: 6, icon: 0});
                return;
            }
            if (!pattern.test(folderName)) {
                layer.msg("输入目录名称不合法", {anim: 6, icon: 0});
                return;
            }
            if (folderName.length <= 0) {
                layer.msg("目录名称不能为空", {anim: 6, icon: 0});
                return;
            }
            if (folderName.length > 50) {
                layer.msg("目录名称不能超过50位", {anim: 6, icon: 0});
                return;
            }
            if (!pattern.test(projectTitle)) {
                layer.msg("输入的项目标题不合法", {anim: 6, icon: 0});
                return;
            }
            if (projectTitle.length <= 0) {
                layer.msg("项目标题不能为空", {anim: 6, icon: 0});
                return;
            }
            if (projectTitle.length > 50) {
                layer.msg("项目标题不能超过50位", {anim: 6, icon: 0});
                return;
            }
            if (!pattern_Info.test(companyInfo)) {
                layer.msg("底部信息不可为特殊字符,仅可是用", {anim: 6, icon: 0});
                return;
            }
            if (serverAddress.length <= 0) {
                layer.msg("分享地址不能为空", {anim: 6, icon: 0});
                return;
            }
            if (companyInfo.length <= 0) {
                layer.msg("底部信息不能为空", {anim: 6, icon: 0});
                return;
            }
            var watermarkCompany = $("input[name='watermarkCompany']:checked").val();
            var user = $("input[name='user']:checked").val();
            var clientShow = $("input[name='clientShow']:checked").val();
            var contactShow = $("input[name='contactShow']:checked").val();

            var clientShowFlag = '0';
            if(clientShow == 'on'){
                clientShowFlag = '1';
            }
            var contactShowFlag = '0';
            if(contactShow == 'on'){
                contactShowFlag = '1';
            }


            var id = companyid + ',' + userid + ',' + fileValidTypeid+','+amountId+','
                +previewTypeId+','+imgTypeId+','+videoTypeId+','+voiceTypeId+','+folderNameid+','
                +companyInfoid+','+projectTitleid+','+serverAddressid+','+clientShowid+','+contactShowid;
            var configKey = "watermark_company,watermark_user,fileValidType,folder_amount,preview_type,img_type,video_type,voice_type" +
                ",folder_name,company_info,project_title,server_address,client_show,contact_show";
            var configValue = company + "@1@" + filetype+"@"+amount+"@"+previewType+"@"
                +imgType+"@"+videoType+"@"+voiceType+"@"+folderName+"@"+companyInfo+"@"
                +projectTitle+"@"+serverAddress+"@"+clientShowFlag+"@"+contactShowFlag;
            var watermark = '';
            var users = '';
            if(watermarkCompany == 'on'){
                watermark = '1';
            }else {
                watermark = '0';
            }
            if(user == 'on'){
                users = '1';
            }else{
                users = '0';
            }
            var configValidFlag = watermark + "," + users + "," +","+amount+",1,1,1,1,1,1,1,1,1,1,1";

            var ajax = new $ax(Hussar.ctxPath + "/docConfigure/save", function(data) {
                if (data){
                    layer.msg("保存成功", {icon: 1});
                }else{
                    layer.msg("保存失败", {anim:6,icon: 0});
                }
            }, function(data) {
                layer.msg("保存失败", {anim:6,icon: 0});
            });
            ajax.set("id",id);
            ajax.set("configKey",configKey);
            ajax.set("configValue",configValue);
            ajax.set("configValidFlag",configValidFlag);
            ajax.start();
        });
        //取消
        /*取消按钮*/
        $("#cancelBtn").on('click',function(){
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        });

        // 初始化数据
        function initdata(){
            var ajax = new $ax(Hussar.ctxPath + "/docConfigure/getConfigureData", function(data) {
                if(data.length > 0){
                    for( var i = 0 ; i < data.length ; i ++){
                        if (data[i].configKey == 'watermark_company'){
                            $("#company").val(data[i].configValue);
                            companyid = data[i].id;
                            var checked;
                            if(data[i].configValidFlag == 1){
                                checked = true;
                            }else{
                                checked = false;
                            }
                            $("input[name='watermarkCompany']:checked").val(true);
                            $('#watermarkCompany').attr("checked",true);
                            $("input[type='checkbox'][name='watermarkCompany']").attr("checked",checked);
                        } else if(data[i].configKey == 'fileValidType'){
                            $("#filetype").val(data[i].configValue);
                            fileValidTypeid = data[i].id;
                        }else if(data[i].configKey == 'preview_type'){
                            $("#previewType").val(data[i].configValue);
                            previewTypeId = data[i].id;
                        }else if(data[i].configKey == 'img_type'){
                            $("#imgType").val(data[i].configValue);
                            imgTypeId = data[i].id;
                        }else if(data[i].configKey == 'video_type'){
                            $("#videoType").val(data[i].configValue);
                            videoTypeId = data[i].id;
                        }else if(data[i].configKey == 'voice_type'){
                            $("#voiceType").val(data[i].configValue);
                            voiceTypeId = data[i].id;
                        }else if(data[i].configKey == 'folder_amount'){
                            $("#amount").val(data[i].configValue);
                            amountId = data[i].id;
                        }else if (data[i].configKey == 'watermark_user'){
                            var checked;
                            if(data[i].configValidFlag == 1){
                                checked = true;
                            }else{
                                checked = false;
                            }
                            $("input[name='user']:checked").val(checked);
                            $("input[type='checkbox'][name='user']").attr("checked",checked);
                            userid= data[i].id;
                        }else if(data[i].configKey == 'folder_name'){
                            $("#folderName").val(data[i].configValue);
                            folderNameid = data[i].id;
                        }else if(data[i].configKey == 'company_info'){
                            $("#companyInfo").val(data[i].configValue);
                            companyInfoid = data[i].id;
                        }else if(data[i].configKey == 'project_title'){
                            $("#projectTitle").val(data[i].configValue);
                            projectTitleid = data[i].id;
                        } else if (data[i].configKey == 'server_address') {
                            $("#serverAddress").val(data[i].configValue);
                            serverAddressid = data[i].id;
                        }else if (data[i].configKey == 'client_show'){ // 是否显示客户端
                            var checked;
                            if(data[i].configValue == 1){
                                checked = true;
                            }else{
                                checked = false;
                            }
                            $("input[name='clientShow']:checked").val(checked);
                            $("input[type='checkbox'][name='clientShow']").attr("checked",checked);
                            clientShowid= data[i].id;
                        }else if (data[i].configKey == 'contact_show'){ // 是否显示联系方式
                            var checked;
                            if(data[i].configValue == 1){
                                checked = true;
                            }else{
                                checked = false;
                            }
                            $("input[name='contactShow']:checked").val(checked);
                            $("input[type='checkbox'][name='contactShow']").attr("checked",checked);
                            contactShowid= data[i].id;
                        }
                        form.render();
                    }
                }else{
                    layer.msg("加载数据失败", {anim:6,icon: 0});
                }
            }, function(data) {

            });
            ajax.start();
        }

        // 上传logo
        function initProjectLogo(){
            upload.render({
                elem: '#uploadLogo'
                , url: Hussar.ctxPath + "/files/uploadProjectLogo"
                , multiple: true
                , accept: 'file'//允许上传的文件类型"
                , done: function (res, index, upload) {
                    if (res.code == '0'){
                        if(res.successAll){
                            layer.msg("上传成功", {icon: 1});
                        }else if(res.errorList.length>0){
                            var tips = "成功替换logo数量"+res.successCount+"个。 替换失败文件:";
                            res.errorList.forEach(function (item, index) {
                                tips += item;
                            });

                            layer.confirm(tips, function (index) {
                                layer.close(index);
                            });
                        }
                    }else{

                        layer.error("上传失败!", {icon: 1});
                    }
                }
            });
        }

        // 上传使用手册
        function initHandbook(){
            upload.render({
                elem: '#uploadHandbook'
                , url: Hussar.ctxPath + "/files/uploadHandbookRoot"
                , multiple: true
                , accept: 'file'//允许上传的文件类型"
                , done: function (res, index, upload) {
                    if (res.code == '0'){
                        layer.msg("上传成功", {icon: 1});
                    }else{
                        layer.error("上传失败!", {icon: 1});
                    }
                },
                error: function () {
                    layer.error("上传失败!", {icon: 1});
                }
            });
        }

        // 下载项目logo
        $("#download").on('click', function () {
            layui.use(['Hussar', 'HussarAjax'], function () {
                var Hussar = layui.Hussar,
                    $ax = layui.HussarAjax;
                $.ajaxFileUpload({
                    url: Hussar.ctxPath + "/files/downProjectLogo",
                    type: "post",
                    async: false
                });
            });
        });
    })
});
