layui.use(['form', 'jquery','util','layer','table','Hussar','jstree','HussarAjax','element','laydate','upload'], function() {
    var form = layui.form,
        $ = layui.jquery,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        Hussar = layui.Hussar;
    var personArr = []; // 分享人员
    var ShareMgr = {
        personTree: $("#personTree"),//人员树
        personIdArr:[], // 分享人员ID
        roleTree: $("#roleTree"),//角色树
        roleIdArr: [], // 分享角色ID
        qzTree: $("#qzTree"),//用户组树
        qzIdArr:[] // 分享用户组Id
    };
    /*关闭弹窗*/
    $("#cancelBtn").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    $(".closeBtn").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    form.on('radio(range)', function (data) {
        if (data.value == 0) {
            $('#personDiv').hide();
            $("#personId").val("");
            $("#person").val("");
            $('#person').removeAttr("title");
            personArr = [];
        } else {
            $('#personDiv').show();
        }
    });
    /*分享确认*/
    $("#saveBtn").on('click',function(){
        var fileId = $("#fileId").val();
        var fileType = $("#fileType").val().trim();
        var pwdFlag = $("[name='pwd_flag']:checked").val();
        var validTime = $("[name='validTime']:checked").val();
        var authority = $("[name='authority']:checked").val();
        var url;//请求地址
        var fileName = $("#fileName").val().trim();
        var successMsg,errorMsg;//成功失败提示
        url = "/s/shareHref";
        successMsg = "分享成功";
        errorMsg = "分享失败";
        // 分享人员选择
        var shareUserRadio = "0"; // 默认全体人员
        var selectShareUsersId = ""; // 默认选中人员id为空
        var shareUserConfig = $("#shareUserConfig").val(); // 是否开启选择人员配置
        if (shareUserConfig == "true") {
            shareUserRadio = $("[name='shareUserRadio']:checked").val();
            if (shareUserRadio == "1") { //指定人员
                selectShareUsersId = $("#personId").val(); // 指定人员id
                if(selectShareUsersId == ""){
                    layer.msg("请选择指定人员");
                    return;
                }
            }
        }

        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+url,
            data:{
                fileId: fileId,
                fileType: fileType,
                pwdFlag: pwdFlag,
                validTime: validTime,
                authority:authority
            },
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                if (data.status == 1) {
                    if(fileType === 'folder'){
                        $(".fileType").html('<img class="folder-img" src="/static/resources/img/fsfile/folder_member1.png">');
                    }else if(fileType === '.doc'){
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/word.png">');
                    }else if(fileType === '.txt'){
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/txt.png">');
                    }else if(fileType === '.ppt'){
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/ppt.png">');
                    }else if(fileType === '.pdf'){
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/pdf.png">');
                    }else if(fileType === '.ceb'){
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/ceb.png">');
                    }else if(['.CD','.OGG','.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(fileType)!=-1){
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/music.png">');
                    }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(fileType)!=-1){
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/video.png">');
                    }else if(fileType === '.xls'||fileType === '.xlsx') {
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/excel.png">');
                    }else if(['.png','.jpeg','.gif','.jpg','.bmp'].indexOf(fileType)!=-1) {
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/pic.png">');
                    }else if(['.zip','.rar'].indexOf(fileType)!=-1) {
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/rar.png">');
                    }else if(['.exe'].indexOf(fileType)!=-1) {
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/exe.png">');
                    }else if(['.psd'].indexOf(fileType)!=-1) {
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/psd.png">');
                     }else if(['.html'].indexOf(fileType)!=-1) {
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/html.png">');
                    }else if(['.bmp'].indexOf(fileType)!=-1) {
                        $(".fileType").html('<img  src="/static/resources/img/front/file-iconNew/bmp.png">');
                    }else if(['component'].indexOf(fileType)!=-1) {
                        $(".fileType").html('<img  src="/static/resources/img/front/file-icon/ic-component02.png">');
                    }else {
                        $(".fileType").html('<img  src="/static/resources/img/front/file-icon/other.png">');
                    }
                    $(".fileName").html(fileName);
                    $("#share_result").html(data.msg);
                    $("#mapping_url").val(data.mapping_url);
                    if (data.valid_time != 36500){
                        $("#validTime").html("链接有效期" + data.valid_time + "天");
                    } else {
                        $("#validTime").html("链接永久有效");
                    }
                    if (data.pwd_flag == 1) {
                        $("#pwd").val(data.pwd);
                        $("#pwd_div").removeClass("hide");
                        $("#mapping_url").attr("style","width:298px!important");
                        $("#copyBtn").html("复制链接和提取码");
                    } else {
                        $("#copyBtn").html("复制链接");
                    }
                    $(".layui-form").addClass("hide");
                    $("#copyBtn").removeClass("hide");
                    $(".return").removeClass("hide");
                    $("#authority-selected").addClass("hide");
                    $(".share-warn").hide();
                    $('.header-title').hide();
                    $(".share-success-div").removeClass("hide");
                    $("#saveBtn").addClass("hide");
                    $("#cancel").html("关闭");
                    //积分系统控制
                    if(fileType!="folder"&&fileType!=".folder"&&fileType!=".component"){
                        $.ajax({
                            url: Hussar.ctxPath+"/integral/addIntegral",
                            async: true,
                            data:{
                                docId: fileId,
                                ruleCode: 'share'
                            },
                            success: function (data) {
                                if (null != data && data != '' && data != undefined){
                                    if (data.integral != 0 && data.integral != null && data.integral != ''){
                                        parent.showIntegral(data.msg);
                                    }
                                }
                            }
                        });
                    }
                } else if (data.status == 2) {
                    layer.msg(data.msg);
                    setTimeout(parent.layer.close(),1000);
                }
            }
        })*/

        var ajax = new $ax(Hussar.ctxPath + url, function(data) {
            if (data.status == 1) {
                if(fileType === 'folder'){
                    $(".fileType").html('<img class="folder-img" src="'+Hussar.ctxPath+'/static/resources/img/fsfile/folder_member1.png">');
                }else if(fileType === '.doc' || fileType === '.docx'){
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/word.png">');
                }else if(fileType === '.txt'){
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/txt.png">');
                }else if(fileType === '.ppt' || fileType === '.pptx' || fileType === '.ppsx'){
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/ppt.png">');
                }else if(fileType === '.pdf'){
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/pdf.png">');
                }else if(fileType === '.ceb'){
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/ceb.png">');
                }else if(['.CD','.OGG','.mp3','.real','.cd','.ogg','.asf','.wav','.ape','.module','.midi'].indexOf(fileType)!=-1){
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/music.png">');
                }else if(['.mp4','.avi', '.wma', '.rmvb','.rm', '.flash'].indexOf(fileType)!=-1){
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/video.png">');
                }else if(fileType === '.xls'||fileType === '.xlsx') {
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/excel.png">');
                }else if(['.png','.jpeg','.gif','.jpg','.bmp'].indexOf(fileType)!=-1) {
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/pic.png">');
                }else if(['.zip','.rar'].indexOf(fileType)!=-1) {
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/rar.png">');
                }else if(['.exe'].indexOf(fileType)!=-1) {
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/exe.png">');
                }else if(['.psd'].indexOf(fileType)!=-1) {
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/psd.png">');
                }else if(['.html'].indexOf(fileType)!=-1) {
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/html.png">');
                }else if(['.bmp'].indexOf(fileType)!=-1) {
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/bmp.png">');
                }else if(['component'].indexOf(fileType)!=-1) {
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-icon/ic-component02.png">');
                }else {
                    $(".fileType").html('<img  src="'+Hussar.ctxPath+'/static/resources/img/front/file-iconNew/other.png">');
                }
                $(".fileName").html(fileName);
                $("#share_result").html(data.msg);
                $("#mapping_url").val(data.mapping_url);
                if (data.valid_time != 36500){
                    $("#validTime").html("链接有效期" + data.valid_time + "天");
                } else {
                    $("#validTime").html("链接永久有效");
                }
                if (data.pwd_flag == 1) {
                    $("#pwd").val(data.pwd);
                    $("#pwd_div").removeClass("hide");
                    $("#mapping_url").attr("style","width:298px!important");
                    $("#copyBtn").html("复制链接和提取码");
                } else {
                    $("#pwd").val("");
                    $("#mapping_url").attr("style","width:425px!important");
                    $("#copyBtn").html("复制链接");
                    $("#pwd_div").addClass("hide");
                }
                $(".layui-form").addClass("hide");
                $("#copyBtn").removeClass("hide");
                $(".return").removeClass("hide");
                $("#authority-selected").addClass("hide");
                $(".share-warn").hide();
                $('.header-title').hide();
                $(".share-success-div").removeClass("hide");
                $("#saveBtn").addClass("hide");
                $("#cancel").html("关闭");
                //积分系统控制
                if(fileType!="folder"&&fileType!=".folder"&&fileType!=".component"&&fileType!="component"){
                    /*$.ajax({
                        url: Hussar.ctxPath+"/integral/addIntegral",
                        async: true,
                        data:{
                            docId: fileId,
                            ruleCode: 'share'
                        },
                        success: function (data) {
                            if (null != data && data != '' && data != undefined){
                                if (data.integral != 0 && data.integral != null && data.integral != ''){
                                    parent.showIntegral(data.msg);
                                }
                            }
                        }
                    });*/

                    var ajax = new $ax(Hussar.ctxPath + "/integral/addIntegral", function(data) {
                        if (null != data && data != '' && data != undefined){
                            if (data.integral != 0 && data.integral != null && data.integral != ''){
                                parent.showIntegral(data.msg);
                            }
                        }
                    }, function(data) {

                    });
                    ajax.set("docId",fileId);
                    ajax.set("ruleCode",'share');
                    ajax.start();
                }
            } else if (data.status == 2) {
                layer.msg(data.msg);
                setTimeout(parent.layer.close(),1000);
            }
        }, function(data) {

        });
        ajax.set("fileId",fileId);
        ajax.set("fileType",fileType);
        ajax.set("pwdFlag",pwdFlag);
        ajax.set("validTime",validTime);
        ajax.set("authority",authority);
        ajax.set("shareUserRadio",shareUserRadio);
        ajax.set("selectShareUsersId",selectShareUsersId);
        ajax.start();
    });

    $("#copyBtn").on('click',function () {
        var copyContent = "链接地址（" + $(".valid-time span").html()+ "）：" + $("#mapping_url").val();
        if ($("#pwd").val()!=''){
            copyContent += "\t提取码：" + $("#pwd").val();
        }
        copyContent += "      - " + projectTitle;
        $("#copyContent").val(copyContent);

    });
    $(function () {
        //复制兼容safari
        var clipboard = new ClipboardJS('#copyBtn');
        clipboard.on('success', function(e) {
            console.info('Action:', e.action);
            console.info('Text:', e.text);
            console.info('Trigger:', e.trigger);
            $("#copyright").html("复制成功")
            e.clearSelection();
        });
    })

    $(".return").on('click',function () {
        $(".return").addClass("hide");
        $(".share-success-div").addClass("hide");
        $(".layui-form").removeClass("hide");
        $("#saveBtn").removeClass("hide");
        $("#copyBtn").addClass("hide");
        $(".valid-time>span").html("");
        $("#authority-selected").removeClass("hide");
        $(".share-warn").show();
        $(".header-title").show();
    })

    // 人员弹出树
    $('#person').on('click', function () {
        var index = layer.open({
            type: 1,
            title: '人员选择',
            area: ['55%', '80%'],
            btn: ['确定', '取消'],
            content: $('#treeArea'),
            skin: 'tree-class',
            success: function () {

                // 角色
                $roleTree = ShareMgr.roleTree;
                $roleTree.jstree("destroy");    //二次打开时要先销毁树
                $roleTree.jstree({
                    core: {
                        check_callback: true,
                        data: {
                            "url": Hussar.ctxPath + "/roleManager/roleTree"
                        }
                    },
                    plugins: ['checkbox','types', 'search'],
                    checkbox: {
                        keep_selected_style: false,
                        three_state: true,
                        tie_selection: false
                    },
                    themes:{
                        theme : "default",
                        dots:false,// 是否展示虚线
                        icons:true// 是否展示图标
                    },
                    types: {
                        "GROUP":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/roleGroup.png"},
                        "ROLE":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/role.png"},
                        "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/root.png"}
                    },
                    search: ShareMgr.search()
                }).on('loaded.jstree', function (e, data) {
                    $roleTree.jstree().close_all();
                    var personIdArr = $('#personId').val();
                    var arr = [];
                    if(personIdArr != null || personIdArr != '' || personIdArr != undefined ){

                        if(personIdArr.indexOf(',') != -1){
                            arr = personIdArr.split(',');
                        } else {
                            arr.push(personIdArr);
                        }

                    }
                    $roleTree.jstree(true).select_node(arr);
                    $roleTree.jstree('check_node',arr);
                });

                // 用户
                $personTree = ShareMgr.personTree;
                $personTree.jstree("destroy");    //二次打开时要先销毁树
                $personTree.jstree({
                    core: {
                        check_callback: true,
                        data: {
                            "url": Hussar.ctxPath + "/orgTreeDemo/usersTree"
                        }
                    },
                    plugins: ['checkbox','types', 'search'],
                    checkbox: {
                        keep_selected_style: false,
                        three_state: true,
                        tie_selection: false
                    },
                    themes:{
                        theme : "default",
                        dots:false,// 是否展示虚线
                        icons:true// 是否展示图标
                    },
                    types: {
                        "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/root.png"},
                        "1":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/deptOld.png"},
                        "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/dept.png"},
                        "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/station.png"},
                        "4":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/deptOld.png"},
                        "5":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/deptOld.png"},
                        "USER":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/user.png"}
                    },
                    search: ShareMgr.search()
                }).on('loaded.jstree', function (e, data) {
                    $personTree.jstree().close_all();
                    var personIdArr = $('#personId').val();
                    var arr = [];
                    if(personIdArr != null || personIdArr != '' || personIdArr != undefined ){

                        if(personIdArr.indexOf(',') != -1){
                            arr = personIdArr.split(',');
                        } else {
                            arr.push(personIdArr);
                        }

                    }
                    $personTree.jstree(true).select_node(arr);
                    $personTree.jstree('check_node',arr);
                });

                // 用户组
                $qzTree = ShareMgr.qzTree;
                $qzTree.jstree("destroy");    //二次打开时要先销毁树
                $qzTree.jstree({
                    core: {
                        check_callback: true,
                        data: {
                            "url": Hussar.ctxPath + "/group/getGroupAndPergroupTreeForShare"
                        }
                    },
                    plugins: ['checkbox','types', 'search'],
                    checkbox: {
                        keep_selected_style: false,
                        three_state: true,
                        tie_selection: false
                    },
                    themes:{
                        theme : "default",
                        dots:false,// 是否展示虚线
                        icons:true// 是否展示图标
                    },
                    types: {
                        "GROUP": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/blue/roleGroup.png"},
                        "ROLE": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/blue/deptOld.png"},
                        "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/root.png"}
                    },
                    search: ShareMgr.search()
                }).on('loaded.jstree', function (e, data) {
                    $qzTree.jstree().close_all();
                    var personIdArr = $('#personId').val();
                    var arr = [];
                    if(personIdArr != null || personIdArr != '' || personIdArr != undefined ){

                        if(personIdArr.indexOf(',') != -1){
                            arr = personIdArr.split(',');
                        } else {
                            arr.push(personIdArr);
                        }

                    }
                    $qzTree.jstree(true).select_node(arr);
                    $qzTree.jstree('check_node',arr);
                });

            },
            yes: function (index) {
                var treeNodes = $('#personTree').jstree(true).get_checked(true);//获取人员选中节点
                var rolesNodes = $('#roleTree').jstree(true).get_checked(true);//获取角色选中节点
                var qzsNodes = $('#qzTree').jstree(true).get_checked(true);//获取用户组选中节点
                var treeId = '';
                var treeText = '';
                if (treeNodes.length == 0 && rolesNodes.length == 0 && qzsNodes.length == 0) {
                    layer.msg("请选择人员", {anim: 6, icon: 0});
                    return;
                }
                personArr = [];
                personIdArr = [];
                // 角色
                for (var i = 0; i < rolesNodes.length; i++) {
                    var type = rolesNodes[i].original.type;
                    var userId = rolesNodes[i].id;
                    var userName = rolesNodes[i].text;

                    if(type == 'isRoot' || type == 'GROUP'){
                        continue
                    }
                    if (i == rolesNodes.length - 1) {
                        // 拼接选中数据
                        treeText = treeText + userName + ',';
                        treeId = treeId + userId + ',';
                    } else {
                        treeText = treeText + userName + ',';
                        treeId = treeId + userId + ',';
                    }
                    personArr.push({userId: userId, userName: userName})

                }
                // 人员
                for (var i = 0; i < treeNodes.length; i++) {
                    var type = treeNodes[i].original.type;
                    var userId = treeNodes[i].id;
                    var userName = treeNodes[i].text;
                    if (type != "USER") { // 过滤根节点
                        continue;
                    }
                    if (i == treeNodes.length - 1) {
                        // 拼接选中数据
                        treeText = treeText + userName + ',';
                        treeId = treeId + userId + ',';
                    } else {
                        treeText = treeText + userName + ',';
                        treeId = treeId + userId + ',';
                    }
                    personArr.push({userId: userId, userName: userName})

                }
                // 用户组
                for (var i = 0; i < qzsNodes.length; i++) {
                    var type = qzsNodes[i].original.code;
                    var userId = qzsNodes[i].id;
                    var userName = qzsNodes[i].text;
                    if (type != "ROLE") { // 过滤根节点
                        continue;
                    }
                    if (i == qzsNodes.length - 1) {
                        // 拼接选中数据
                        treeText = treeText + userName;
                        treeId = treeId + userId;
                    } else {
                        treeText = treeText + userName + ',';
                        treeId = treeId + userId + ',';
                    }
                    personArr.push({userId: userId, userName: userName})

                }
                if (treeText.length > 0 && treeText.charAt(treeText.length - 1) == ",") {
                    treeText = treeText.substring(0, treeText.length - 1);
                }
                if (treeId.length > 0 && treeId.charAt(treeId.length - 1) == ",") {
                    treeId = treeId.substring(0, treeId.length - 1);
                }
                // 赋值到输入框中
                $('#person').val(treeText);
                $('#personId').val(treeId);
                $('#person').attr("title",treeText);
                layer.close(index);
            },
            end:function () {
                $('#treeArea').hide();
            }
        })
    })


    ShareMgr.search = function () {
        var to = false;

        //角色树查询
        $("#personTreeSearch").keyup(function () {
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                var v = $("#personTreeSearch").val();
                var temp = $roleTree.is(":hidden");
                if (temp == true) {
                    $roleTree.show();
                }
                var temp1 = $personTree.is(":hidden");
                if (temp1 == true) {
                    $personTree.show();
                }
                var temp2 = $qzTree.is(":hidden");
                if (temp2 == true) {
                    $qzTree.show();
                }
                $roleTree.jstree(true).search(v);
                $personTree.jstree(true).search(v);
                $qzTree.jstree(true).search(v);
                //定位到符合查询结果的树节点上
                var searchResult = $roleTree.jstree('search', v);
                $(searchResult).find('.jstree-search').focus();
                var searchResult1 = $personTree.jstree('search', v);
                $(searchResult1).find('.jstree-search').focus();
                var searchResult2 = $qzTree.jstree('search', v);
                $(searchResult2).find('.jstree-search').focus();

            }, 250);
        });

    };
});