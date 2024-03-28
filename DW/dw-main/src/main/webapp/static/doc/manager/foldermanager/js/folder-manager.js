var hussar;
var openFolderId;   //打开的文件夹的id
var chooseFolder = [];    //选中的目录的id
var chooseFolderName = []; //选中的目录的name
var cutFolder = [];          //剪切的目录的id
var cutFolderName = [];      //剪切的目录的name
var pathId = [];        //路径
var pathName = [];
var key = '';
var adminFlag;
var userId;
var categoryId;
var reNameFlag = false;      //重命名标志
var reNameParem = '';
var reNameIndex = '';
var clickFlag = false;
var groupId = [];
var personId = [];
var personParam = [];
var groupParam = [];
var groupIdPower = [];
var personIdPower = [];
var personParamPower = [];
var groupParamPower = [];
var editFlag = false;
var noChildPower = 1;
var dbclickover = true;
layui.use(['form', 'laypage', 'jquery', 'layer', 'Hussar', 'jstree', 'laytpl'], function () {
    var $ = layui.jquery,
        form = layui.form,
        jstree = layui.jstree,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer;
    hussar = Hussar.ctxPath;
    /*初始化*/
    start();
    /*删除目录*/
    $("#delCategoryBtn").on('click', function () {
        if (noChildPower == 0) {
            layer.msg("您没有删除目录权限", {anim: 6, icon: 0});
            return;
        }
        if (adminFlag == 3) {
            layer.msg("您的权限不允许删除目录", {anim: 6, icon: 0});
            return;
        }
        if (chooseFolder.length == 0) {
            layer.msg("请选择要删除的目录", {anim: 6, icon: 0});
            return;
        }
        var folderIdArrStr = chooseFolder.join(",");
        /*检查目录下是否有文件（递归查）*/
        $.ajax({
            type: "post",
            url: "/fsFolder/checkFolderType",
            data: {
                ids: folderIdArrStr
            },
            async: false,
            cache: false,
            success: function (data) {
                if (data == 'haveFile') {
                    layer.msg("请先删除目录下存放的文件", {anim: 6, icon: 0});
                    return;
                } else {
                    layer.confirm('确定要删除所选目录吗？', function () {
                        var index = layer.load(1, {
                            shade: [0.1, '#fff'] //0.1透明度的白色背景
                        });
                        $.ajax({
                            type: "post",
                            url: "/fsFolder/delete",
                            data: {
                                fsFolderIds: folderIdArrStr,
                            },
                            async: false,
                            cache: false,
                            success: function (data) {
                                if (data > 0) {
                                    Hussar.success('删除成功')
                                }
                            },
                            error: function () {
                                Hussar.error('删除失败')
                            }
                        })
                        for (var i = 0; i < chooseFolder.length; i++) {
                            for (var j = 0; j < cutFolder.length; j++) {
                                if (cutFolder[j] == chooseFolder[i]) {
                                    cutFolder = cutFolder.del(j);
                                    break;
                                }
                            }
                        }
                        refreshTree()
                        refreshFolder(openFolderId);
                        emptyChoose();
                        layer.close(index);
                    })
                }
            }
        })
    });
    /*返回上级按钮点击事件*/
    $("#upLevel").on('click', function () {
        if (pathId.length == 1) {
            return;
        }
        var index = layer.load(1, {
            shade: [0.1, '#fff'] //0.1透明度的白色背景
        });
        refreshFolder(pathId[pathId.length - 2]);
        pathName.pop();
        pathId.pop();
        createPath();
        layer.close(index);
    });
    /*搜索按钮*/
    $("#searchBtn").on('click', function () {
        var index = layer.load(1, {
            shade: [0.1, '#fff'] //0.1透明度的白色背景
        });
        refreshFolder(openFolderId, null, null, "1");
        layer.close(index);
    });
    /*剪切*/
    $("#cutFolder").on('click', function () {
        if (noChildPower == 0) {
            layer.msg("您没有移动目录权限", {anim: 6, icon: 0});
            return;
        }
        if (adminFlag == 3) {
            layer.msg("您的权限不允许移动目录", {anim: 6, icon: 0});
            return;
        }
        if (chooseFolder.length == 0) {
            layer.msg("请选择要移动的目录", {anim: 6, icon: 0});
            return;
        }
        var operation = function () {
            layerView = layer.open({
                type: 1,
                area: ['350px', '500px'],
                //shift : 1,
                shadeClose: false,
                title: '目录结构',
                maxmin: true,
                content: $("#folderTreeAuthority"),
                success: function () {
                    initFileTree();
                    layer.close(index);
                },
                end: function () {
                    layer.closeAll(index);
                }
            });
        }
        var index = layer.confirm('确定要移动所选目录吗？', operation);
        cutFolder = [].concat(chooseFolder);
        cutFolderName = [].concat(chooseFolderName);
    });
    //加载目录树
    initFileTree = function () {
        var $tree = $("#folderTreeAuthority2");
        $tree.jstree("destroy");    //二次打开时要先销毁树
        $tree.jstree({
            core: {
                check_callback: true,
                data: {
                    "url": Hussar.ctxPath + "/fsFolder/getTreeDataLazy?lazy",
                    "data": function (node) {
                        return {
                            "id": node.id, "type": "2"
                        };
                    }
                },
                themes: {
                    theme: "default",
                    dots: false,// 是否展示虚线
                    icons: true,// 是否展示图标
                }
            },
            types: {
                "closed": {
                    "icon": Hussar.ctxPath + "/static/resources/img/fsfile/treeFile.png",
                },
                "default": {
                    "icon": Hussar.ctxPath + "/static/resources/img/fsfile/treeFile.png",
                },
                "opened": {
                    "icon": Hussar.ctxPath + "/static/resources/img/fsfile/openFile.png",
                }
            },
            plugins: ['types']
        });
        $tree.bind('activate_node.jstree', function (obj, e) {
            var noChildPowers;
            var operation = function () {
                $.ajax({
                    type: "post",
                    url: "/fsFolder/checkIsEdit",
                    data: {
                        chooseFolder:  e.node.id
                    },
                    async: false,
                    cache: false,
                    dataType: "json",
                    success: function (data) {
                        noChildPowers = data;
                    }
                });
                if (!noChildPowers) {
                    layer.msg("您没有移动到此目录的权限", {anim: 6, icon: 0});
                    return;
                }
                var index = layer.load(1, {
                    shade: [0.1, '#fff'] //0.1透明度的白色背景
                });
                if (cutFolder.length <= 0) {
                    layer.close(index);
                    layer.msg("请先选择要目标目录", {anim: 6, icon: 0});
                    return;
                }
                if (adminFlag == 3) {
                    layer.msg("您的权限不允许移动到此目录", {anim: 6, icon: 0});
                    return;
                }
                var nameStr = cutFolderName.join("*");
                $.ajax({
                    type: "post",
                    url: "/fsFolder/checkName",
                    data: {
                        nameStr: nameStr,
                        folderPid: e.node.id,
                    },
                    async: false,
                    cache: false,
                    dataType: "json",
                    success: function (data) {
                        if (data != "success") {
                            layer.msg("存在重名目录", {anim: 6, icon: 0});
                            layer.close(index);
                            return;
                        } else {
                            var folderIdStr = cutFolder.join(",");
                            $.ajax({
                                type: "post",
                                url: "/fsFolder/checkChild",
                                data: {
                                    fsFolderIds: folderIdStr,
                                    id: e.node.id,
                                },
                                async: false,
                                cache: false,
                                dataType: "json",
                                success: function (data) {
                                    if (data == "have") {
                                        layer.msg("目标目录不能是移动目录的本身或子目录", {anim: 6, icon: 0});
                                        layer.close(index);
                                        return;
                                    } else {
                                        updatePid(index, e.node.id);
                                        cutFolder = [];
                                        btnState();
                                    }
                                }
                            });
                        }
                    }
                });
            }
            layer.confirm('确定要移动到此目录下吗？', operation);
        });
    }
    /*修改方法，（用户名，可见范围，是否可编辑）*/
    $("#reName").on('click', function () {
        if (noChildPower == 0) {
            layer.msg("您没有修改目录权限", {anim: 6, icon: 0});
            return;
        }
        editFlag = true;
        groupId = [];
        groupParam = [];
        personId = [];
        personParam = [];
        groupIdPower = [];
        personIdPower = [];
        personParamPower = [];
        groupParamPower = [];
        if (adminFlag == 3) {
            layer.msg("您的权限不允许修改目录", {anim: 6, icon: 0});
            return;
        }
        if (chooseFolder.length != 1) {
            layer.msg("请选择一个要修改的目录", {anim: 6, icon: 0});
            return;
        }
        $.ajax({
            type: "post",
            url: "/fsFolder/getAuthority",
            data: {
                folderId: chooseFolder[0],
            },
            async: false,
            cache: false,
            dataType: "json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success: function (data) {
                for (var i = 0; i < data.length; i++) {
                    if (data[i].authorType == "0") {
                        var datajson = {
                            "id": data[i].authorId,
                            "name": data[i].authorId,
                            "type": data[i].authorType,
                            "organId": data[i].organId,
                            "operateType": data[i].operateType
                        };
                        personParamPower.push(datajson);
                    } else if (data[i].authorType == "1") {
                        var datajson = {
                            "id": data[i].authorId,
                            "name": data[i].groupName,
                            "type": data[i].authorType,
                            "operateType": data[i].operateType
                        };
                        groupParamPower.push(datajson)
                    } else if (data[i].authorType == "2") {
                        var datajson = {
                            "id": data[i].authorId,
                            "name": data[i].authorId,
                            "type": data[i].authorType,
                            "organId": data[i].organId,
                            "operateType": data[i].operateType
                        };
                        groupParamPower.push(datajson);
                    } else {
                        var datajson = {
                            "id": data[i].authorId,
                            "name": "全体人员",
                            "type": data[i].authorType,
                            "organId": data[i].organId,
                            "operateType": data[i].operateType
                        };
                        groupParamPower.push(datajson);
                    }
                }
                personId = personParam;
                groupId = groupParam;
                personIdPower = personParamPower;
                groupIdPower = groupParamPower;
                createAuthorityPower(groupParamPower, personParamPower)
                $("#categoryEditName").val(chooseFolderName[0]);
                form.render();
            }
        });
        layer.open({
            type: 1,
            btn: ['确定','取消'],
            area: ['60%', '65%'],
            skin: 'confirm-class',
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: "修改目录",
            content: $('#editDiv'),
            btn1: function (index, layero) {
                var categoryName = $("#categoryEditName").val().trim();
                var isEdit = $("input[name='isEditEdit']:checked").val();
                if (isEdit != undefined && isEdit == 'on') {
                    isEdit = '0'
                } else {
                    isEdit = '';
                }
                var isEditChild = $("input[name='isEditChild']:checked").val();
                if (isEditChild != undefined && isEditChild == 'on') {
                    isEdit = isEdit + '1'
                } else {
                    isEdit = isEdit + ''
                }
                var visible = $("input[name='visibleEdit']:checked").val();

                if (categoryName.length <= 0) {
                    layer.msg("目录名称不能为空", {anim: 6, icon: 0});
                    return false;
                }
                if (categoryName.length > 30) {
                    layer.msg("目录名称不能超过30个字符", {anim: 6, icon: 0});
                    return false;
                }
                var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
                //特殊字符
                if (!pattern.test(categoryName)) {
                    layer.msg("输入的目录名称不合法", {anim: 6, icon: 0});
                    return;
                }

                if (groupId.length == 0 && personId.length == 0) {
                    visible = '0';
                }
                else {
                    visible = '1';
                }
                var groupStrPower = '';
                var personStrPower = '';
                var personOrganStrPower = '';
                var groupPower = [];
                var personPower = [];
                var personOrganPower = [];
                var authorTypeGroup = [];
                var authorTypePerson = [];
                var authorTypeStrGroup = '';
                var authorTypeStrPerson = '';
                var operateTypeGroup = [];
                var operateTypePerson = [];
                var operateTypeStrGroup = '';
                var operateTypeStrPerson = '';
                if (groupIdPower != undefined) {
                    for (var i = 0; i < groupIdPower.length; i++) {
                        groupPower.push(groupIdPower[i].id);
                        authorTypeGroup.push(groupIdPower[i].type);
                        operateTypeGroup.push(groupIdPower[i].operateType);
                    }
                    var groupStrPower = groupPower.join(",")
                    authorTypeStrGroup = authorTypeGroup.join(",");
                    operateTypeStrGroup = operateTypeGroup.join(",");
                }
                if (personIdPower != undefined) {
                    for (var i = 0; i < personIdPower.length; i++) {
                        personPower.push(personIdPower[i].id);
                        personOrganPower.push(personIdPower[i].organId);
                        authorTypePerson.push(personIdPower[i].type);
                        operateTypePerson.push(groupIdPower[i].operateType)
                    }
                    personStrPower = personPower.join(",");
                    personOrganStrPower = personOrganPower.join(",");
                    authorTypeStrPerson = authorTypePerson.join(",");
                    operateTypeStrPerson = operateTypePerson.join(",");
                }
                if (categoryName != chooseFolderName[0]) {
                    $.ajax({
                        type: "post",
                        url: "/fsFolder/addCheck",
                        data: {
                            name: categoryName,
                            parentFolderId: openFolderId,
                        },
                        async: false,
                        cache: false,
                        dataType: "json",
                        success: function (data) {
                            if (data == "false") {
                                layer.msg("“" + categoryName + "”目录已存在", {anim: 6, icon: 0});
                            } else {
                                $.ajax({
                                    type: "POST",
                                    url: "/fsFolder/editAuthority",
                                    data: {
                                        visibleRange: visible,
                                        folderName: categoryName,
                                        isEdit: isEdit,
                                        group: groupStrPower,
                                        person: personStrPower,
                                        personOrgan: personOrganStrPower,
                                        folderId: chooseFolder[0],
                                        authorTypeStrGroup: authorTypeStrGroup,
                                        operateTypeStrGroup: operateTypeStrGroup,
                                        authorTypeStrPerson: authorTypeStrPerson,
                                        operateTypeStrPerson: operateTypeStrPerson
                                    },
                                    contentType: "application/x-www-form-urlencoded",
                                    dataType: "json",
                                    async: false,
                                    success: function (result) {
                                        layer.closeAll();
                                        editFlag = false;
                                        refreshFolder(openFolderId);
                                        refreshTree();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    $.ajax({
                        type: "POST",
                        url: "/fsFolder/editAuthority",
                        data: {
                            visibleRange: visible,
                            folderName: categoryName,
                            isEdit: isEdit,
                            group: groupStrPower,
                            person: personStrPower,
                            personOrgan: personOrganStrPower,
                            folderId: chooseFolder[0],
                            authorTypeStrGroup: authorTypeStrGroup,
                            authorTypeStrPerson: authorTypeStrPerson,
                            operateTypeStrGroup: operateTypeStrGroup,
                            operateTypeStrPerson: operateTypeStrPerson
                        },
                        contentType: "application/x-www-form-urlencoded",
                        dataType: "json",
                        async: false,
                        success: function (result) {
                            layer.closeAll();
                            editFlag = false;
                            refreshFolder(openFolderId);
                            refreshTree();
                        }
                    });
                }
            },
        });
    });
    /*修改时设置权限*/
    $("#setEditAuthority").click(function () {
        layer.open({
            type: 2,
            title: '选择目录权限范围',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: false,
            content: Hussar.ctxPath + '/fsFolder/authority',
            success: function () {

            }
        });
    });
    $("#setEditAuthority1").click(function () {
        layer.open({
            type: 2,
            title: '选择后台可操作范围',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: false,
            content: Hussar.ctxPath + '/fsFolder/authorityPower',
            success: function () {

            }
        });
    });
    // 删除数据
    $(".del_span").click(function () {
        $(this).parent(".name-item").remove();
    });
    /*新增子目录*/
    $("#addCategoryBtn").on('click', function () {
        if (noChildPower == 0) {
            layer.msg("您没有新建目录权限", {anim: 6, icon: 0});
            return;
        }
        editFlag = false;
        if (pathId.length >= 5) {
            layer.msg("目录最多为4级", {anim: 6, icon: 0});
            return false;
        }
        groupId = [];
        groupParam = [];
        personId = [];
        personParam = [];
        groupIdPower = [];
        personIdPower = [];
        personParamPower = [];
        groupParamPower = [];


        $("#categoryName").val("");

        $('.name-list').empty();
        form.render();
        layer.open({
            type: 1,
            btn: ['确定','取消'],
            area: ['60%', '65%'],
            skin: 'confirm-class',
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: "新建目录",
            content: $('#addDiv'),
            btn1: function (index, layero) {
                var categoryName = $("#categoryName").val().trim();
                var isEdit = $("input[name='isEdit']:checked").val();
                if (isEdit != undefined && isEdit == 'on') {
                    isEdit = '0'
                } else {
                    isEdit = '';
                }
                var isChild = $("input[name='isChild']:checked").val();
                if (isChild != undefined && isChild == 'on') {
                    isEdit = isEdit + "1";
                } else {
                    isEdit = isEdit + "";
                }
                var visible = $("input[name='visible']:checked").val();

                if (categoryName.length <= 0) {
                    layer.msg("目录名称不能为空", {anim: 6, icon: 0});
                    return false;
                }
                if (categoryName.length > 30) {
                    layer.msg("目录名称不能超过30个字符", {anim: 6, icon: 0});
                    return false;
                }
                var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
                //特殊字符
                if (!pattern.test(categoryName)) {
                    layer.msg("输入的目录名称不合法", {anim: 6, icon: 0});
                    return;
                }

                if (groupId.length == 0 && personId.length == 0) {
                    visible = '0';
                } else {
                    visible = '1';
                }
                var groupStr = '';
                var personStr = '';
                var personOrganStr = '';
                var group = [];
                var person = [];
                var personOrgan = [];
                var authorTypeGroup = [];
                var authorTypePerson = [];
                var authorTypeStrGroup = '';
                var authorTypeStrPerson = '';
                var operateTypeGroup = [];
                var operateTypePerson = [];
                var operateTypeStrGroup = '';
                var operateTypeStrPerson = '';
                if (groupId != undefined) {
                    for (var i = 0; i < groupId.length; i++) {
                        group.push(groupId[i].id);
                        authorTypeGroup.push(groupId[i].type);
                        operateTypeGroup.push(groupId[i].operateType)
                    }
                    groupStr = group.join(",")
                    authorTypeStrGroup = authorTypeGroup.join(",");
                    operateTypeStrGroup = operateTypeGroup.join(",");
                }
                if (personId != undefined) {
                    for (var i = 0; i < personId.length; i++) {
                        person.push(personId[i].id);
                        personOrgan.push(personId[i].organId);
                        authorTypePerson.push(personId[i].type);
                        operateTypePerson.push(personId[i].operateType);
                    }
                    personStr = person.join(",")
                    personOrganStr = personOrgan.join(",")
                    authorTypeStrPerson = authorTypePerson.join(",")
                    operateTypeStrPerson = operateTypePerson.join(",");
                }
                $.ajax({
                    type: "post",
                    url: "/fsFolder/addCheck",
                    data: {
                        name: categoryName,
                        parentFolderId: openFolderId,
                    },
                    async: false,
                    cache: false,
                    dataType: "json",
                    success: function (data) {
                        if (data == "false") {
                            layer.msg("“" + categoryName + "”目录已存在", {anim: 6, icon: 0});
                            return;
                        } else {
                            $.ajax({
                                type: "POST",
                                url: "/fsFolder/add",
                                data: {
                                    parentFolderId: openFolderId,
                                    folderName: categoryName,
                                    visible: visible,
                                    isEdit: isEdit,
                                    group: groupStr,
                                    person: personStr,
                                    personOrgan: personOrganStr,
                                    authorTypeStrGroup: authorTypeStrGroup,
                                    authorTypeStrPerson: authorTypeStrPerson,
                                    operateTypeStrGroup: operateTypeStrGroup,
                                    operateTypeStrPerson: operateTypeStrPerson
                                },
                                contentType: "application/x-www-form-urlencoded",
                                dataType: "json",
                                async: false,
                                success: function (result) {
                                    refreshFolder(openFolderId);
                                    refreshTree();
                                    layer.closeAll();
                                }
                            });
                        }
                    }
                });

            },
        });
    });

    /* 目录授权*/
    $("#authorityBtn").on('click', function () {
        if (noChildPower == 0) {
            layer.msg("您没有授权目录权限", {anim: 6, icon: 0});
            return;
        }
        if (adminFlag == 3) {
            layer.msg("您的权限不允许修改目录", {anim: 6, icon: 0});
            return;
        }
        layer.open({
            type: 2,
            title: '目录授权',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath + '/fsFolder/folderAuthority_manager',
            success: function () {
            }
        });
    })
    $(".edit-name-list-power,.name-list-power").on("click", ".del_span_power", function () {
        if ($(this).attr('value') == 0) {
            for (var i = 0; i < personIdPower.length; i++) {
                if (personIdPower[i].name == $(this).prev().html()) {
                    personIdPower.splice(i, 1);
                }
            }
        } else {
            for (var i = 0; i < groupIdPower.length; i++) {
                if (groupIdPower[i].name == $(this).prev().html()) {
                    groupIdPower.splice(i, 1);
                }
            }
        }
        if (editFlag) {
            if (groupIdPower.length == 0 && personIdPower.length == 0) {
                $('#isEditEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $('#isEditChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $("#isEditEdit").prop("checked", false);
                $("#isEditChild").prop("checked", false);
            }
        }
        else {
            if (groupIdPower.length == 0 && personIdPower.length == 0) {
                $('#isEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $('#isChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $("#isEdit").prop("checked", false);
                $("#isChild").prop("checked", false);
            }
        }
        form.render();
        $(this).parent(".name-item").remove();

    });

    /*排序-----暂时不用了*/
    $("#orderType li").click(function () {
        $("input[name='sortType']").parent().removeClass("sortType-checked");
        $("input[name='sortType']:checked").parent().addClass("sortType-checked");
        var index = layer.load(1, {
            shade: [0.1, '#fff'] //0.1透明度的白色背景
        });
        refreshFolder(openFolderId);
        layer.close(index);
        // add
    })

    /* 重命名按钮*/
    $("#updateName").on('click', function () {
        if (noChildPower == 0) {
            layer.msg("您没有重命名目录权限", {anim: 6, icon: 0});
            return;
        }
        if (chooseFolder.length != 1) {
            layer.msg("请选择一个要重命名的文件", {anim: 6, icon: 0});
            return;
        }
        $('#inputName'+reNameIndex).val(chooseFolderName[0]);
        $('#name' + reNameIndex).addClass("hide");
        $('#inputName' + reNameIndex).removeClass("hide");
        $('#inputName' + reNameIndex).focus();
        reNameFlag = true;
    })


    /*新增 可见，部分可见 radio的监听*/
    form.on('radio(visible)', function (data) {
        if (data.value == "0") {

        } else {
        }
        form.render();
    });
    /*编辑 可见，部分可见 radio的监听*/
    form.on('radio(visibleEdit)', function (data) {
        if (data.value == "0") {

        } else {

        }
        form.render();
    });
    function start() {
        initTree();
        btnState();
    }

    $("#setAuthority").click(function () {
        layer.open({
            type: 2,
            title: '目录授权',
            area: ['850px', '510px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Hussar.ctxPath + '/fsFolder/authority',
            success: function () {

            }
        });
    });

    /**
     * 加载目录树
     */
    function initTree() {
        var $tree = $("#folderTree");
        $tree.jstree({
            core: {
                check_callback: true,
                multiple: false,//单选
                data: {
                    "url": "/fsFolder/getTreeDataLazy?lazy",
                    "data": function (node) {
                        return {
                            "id": node.id, "type": "2"
                        };
                    }
                },
            },
            types: {
                "closed": {
                    "icon": hussar + "/static/resources/img/fsfile/treeFile.png",
                },
                "default": {
                    "icon": hussar + "/static/resources/img/fsfile/treeFile.png",
                },
                "opened": {
                    "icon": hussar + "/static/resources/img/fsfile/openFile.png",
                },
            },
            contextmenu: {
                select_node: true,
                show_at_node: true,
                'items': function (node) {
                    var items = {
                        'item4': {
                            'label': '上移',
                            'icon': Hussar.ctxPath + "/static/assets/img/treeContext/511101.png",
                            'action': function (obj) {
                                var inst = $.jstree.reference(obj.reference);
                                var clickedNode = inst.get_node(obj.reference);  //点击的节点
                                var prev = inst.get_prev_dom(obj.reference, true);   //点击节点的上一个节点
                                //选中ID
                                var id = clickedNode.id;
                                var prevId =prev[0].id;
                                singleMove(id, prevId); //交换showorder
                            }
                        },
                        'item5': {
                            'label': '下移',
                            'icon': Hussar.ctxPath + "/static/assets/img/treeContext/511102.png",
                            'action': function (obj) {
                                //获取点击的节点信息
                                var inst = $.jstree.reference(obj.reference);
                                var clickedNode = inst.get_node(obj.reference);
                                var next = inst.get_next_dom(obj.reference, true);
                                //选中ID
                                var id = clickedNode.id;
                                var nextId =next[0].id
                                singleMove(id, nextId);
                            }
                        },
                    };
                    if (node.type === 'isRoot') { //根节点不显示上移下移
                        delete items.item4;
                        delete items.item5;
                    }
                    var id = node.id;
                    var n = $("#" + id);
                    if (n.index() == 0) {//非第一个
                        delete items.item4;  //删除 上移
                    }
                    if (n.next().length == 0) {//非最后一个
                        delete items.item5;  //删除 下移
                    }
                    return items;
                }
            },
            plugins: ['state', 'types', "themes", "html_data", 'contextmenu'],
        }).on('loaded.jstree', function (e, data) {
            var inst = data.instance;
            if (undefined != e.target.firstChild.firstChild.lastChild.firstChild) {
                var obj = inst.get_node(e.target.firstChild.firstChild.lastChild.firstChild);
                //inst.select_node(obj);
            }
        });

        $tree.jstree().hide_dots();

        $tree.bind("activate_node.jstree", function (obj, e) {
            // 处理代码
            // 获取当前节点
            var currentNode = e.node;
            openFolderId = currentNode.id;
            categoryId = currentNode.id;
            refreshFolder(currentNode.id);
            emptyChoose();
            var paramId = [];
            var paramName = [];
            if (currentNode.parent == '#') {    //如果点击的是根节点
                pathId = [];
                pathName = [];
                pathId.push(currentNode.id)
                pathName.push(currentNode.text)
                createPath();
                return;
            }
            $('#path').empty();
            pathId = [];
            pathName = [];
            /*生成路径*/
            paramId.push(currentNode.id);
            paramName.push(currentNode.text);
            do {
                currentNode = $('#folderTree').jstree("get_node", currentNode.parent);
                paramId.push(currentNode.id);
                paramName.push(currentNode.text);
            } while (currentNode.parent != '#')
            for (var i = 0; i < paramId.length; i++) {
                pathId.push(paramId[paramId.length - 1 - i]);
                pathName.push(paramName[paramId.length - 1 - i]);
            }
            createPath();
        });
        $tree.bind("open_node.jstree", function (e, data) {
            data.instance.set_type(data.node, 'opened');
        });
        $tree.bind("close_node.jstree", function (e, data) {
            data.instance.set_type(data.node, 'closed');
        });
        $tree.bind("loaded.jstree", function (event, data) {
            data.instance.clear_state(); // <<< 这句清除jstree保存的选中状态
        })
        $.ajax({
            type: "POST",
            url: "/fsFolder/getRoot",
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            async: false,
            success: function (result) {
                openFolderId = result.root;
                categoryId = result.root;
                getChildren(result.root, result.rootName);
            }
        });
    }

    /**
     *树节点右击的上移下移
     * @param id 点击节点id
     * @param nextId 需要交换节点的id
     */
    function singleMove(id, nextId) {
        $.ajax({
            type: "post",
            url: "/business/changeShowOrder",
            data: {
                table: "fs_folder",
                idColumn: "folder_id",
                idOne: id,
                idTwo: nextId
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            async: true,
            cache: false,
            success: function (result) {
                refreshTree();
                refreshFolder(openFolderId);
            }
        });
    };
    function updatePid(index, id) {
        var cutIds = cutFolder.join(",");
        $.ajax({
            type: "post",
            url: "/fsFolder/update",
            data: {
                ids: cutIds,
                parentFolderId: id,
            },
            async: true,
            cache: false,
            success: function (data) {
                if ("success" == data) {
                    emptyChoose();
                    refreshFolder(openFolderId);
                    refreshTree();
                    layer.closeAll();
                }else{
                    layer.msg("目录最多为"+data+"级", {anim: 6, icon: 0});
                    layer.close(index);
                }


            }
        })
    }

});
function clickPath(id) {
    while (pathId.indexOf(id) + 1 != pathId.length) {
        pathId.pop();
        pathName.pop();
    }
    createPath();
    refreshFolder(id);
}
function createPath() {
    $("#path").empty();
    $("#path").css({"transform": "translateX(0)"});
    for (var i = 0; i < pathId.length; i++) {
        if (i == pathId.length - 1) {
            var param = '<span>' + pathName[i] + '</span>'
        } else {
            var param = '<span><a style="cursor: pointer; color: #26B7B1;" onclick="clickPath(\'' + pathId[i] + '\')">' + pathName[i] + '</a>' + '  /  </span> '
        }
        $("#path").append(param);
        setTimeout(function () {
            var list = $("#path>span");
            var innerlength = 0;
            for (var m = 0; m < (list.length); m++) {
                innerlength = Math.ceil(innerlength + list.eq(m).width() + 5.4);
            }
            $("#path").width(innerlength);
            var outWidth = $(".outer-nav").width() - 5;
            //当目录长度超出显示范围，默认只显示可以显示的最后
            if (innerlength > outWidth) {
                $(".control-btn-l").show();
                $(".control-btn-r").hide();
                var subLength = innerlength - outWidth;
                $("#path").css({"transform": "translateX(-" + subLength + "px)"});
                //获取当前偏移量

                $(".control-btn-l").click(function () {
                    var subLength = $("#path").width() - $(".outer-nav").width();
                    var subLength_1 = -$("#path").css("transform").replace(/[^0-9\-,]/g, '').split(',')[4];
                    $(".control-btn-r").show();
                    subLength_1 = subLength_1 - outWidth;
                    if (subLength_1 > outWidth) {
                        $("#path").css({"transform": "translateX(-" + subLength_1 + "px)"});
                    } else {
                        $("#path").css({"transform": "translateX(0)"});
                        $(".control-btn-l").hide();
                    }

                });
                $(".control-btn-r").click(function () {
                    var subLength = $("#path").width() - $(".outer-nav").width();
                    $(".control-btn-l").show();
                    var subLength_2 = -$("#path").css("transform").replace(/[^0-9\-,]/g, '').split(',')[4];
                    subLength_2 = subLength_2 + outWidth;
                    if (subLength_2 > subLength) {
                        $("#path").css({"transform": "translateX(-" + subLength + "px)"});
                        $(".control-btn-r").hide();
                    } else {
                        $("#path").css({"transform": "translateX(-" + subLength_2 + "px)"});
                    }
                })
            } else {
                $("#path").css({"transform": "translateX(0)"});
                $(".control-btn-l").hide();
                $(".control-btn-r").hide();
            }
        }, 100)
    }
}
function drawFolder(param) {
    layui.use('laytpl', function () {
        var laytpl = layui.laytpl;
        var data = { //数据
            "list": param
        }
        var getTpl = $("#demo").html()
            , view = document.getElementById('view');
        laytpl(getTpl).render(data, function (html) {
            view.innerHTML = html;
        });
    });

}

function getChildren(id, name) {
    pathId.push(id);
    pathName.push(name);
    createPath();
    refreshFolder(id);
}
function addOper(parent, node) {
    $("#folderTree").jstree("deselect_all", true);
    var ref = $('#folderTree').jstree(true);
    ref.open_node(node);
    var id = ref.get_node(node + '_anchor');
    if (id) {
        ref.select_node(id);
    } else {
        ref.select_node(node.substr(0, node.length - 2));
    }
};
function refreshFolder(id, num, size, nameFlag) {
    layui.use(['laypage', 'layer'], function () {
        var laypage = layui.laypage,
            layer = layui.layer;
        var orderType = $("input[name='sortType']:checked").val(); //排序类型
        var name = $('#searchName').val();
        if (nameFlag != "" && nameFlag != undefined && nameFlag != null) {
            var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
            //特殊字符
            if (!pattern.test(name)) {
                layer.msg("输入的目录名称不合法", {anim: 6, icon: 0});
                return;
            }
        }
        addOper(openFolderId, id);
        $.ajax({
            type: "post",
            url: "/fsFolder/getChildren",
            data: {
                id: id,
                pageNumber: num,
                pageSize: size,
                order: "0",
                name: name,
                nameFlag: nameFlag,
                type: "2"
            },
            async: true,
            cache: false,
            dataType: "json",
            success: function (data) {
                if (data.total > 0) {
                    $('.hideBtn').show()
                } else {
                    $('.hideBtn').hide()
                }
                laypage.render({
                    elem: 'laypageAre'
                    , count: data.total //数据总数，从服务端得到
                    , limit: 300
                    , layout: ['prev', 'page', 'next']
                    , curr: num || 1 //当前页
                    , jump: function (obj, first) {
                        //obj包含了当前分页的所有参数，比如：
                        // obj.curr得到当前页，以便向服务端请求对应页的数据。
                        // obj.limit得到每页显示的条数
                        //首次不执行
                        if (!first) {
                            refreshFolder(id, obj.curr, obj.limit)
                        }
                    }
                });
                noChildPower = data.noChildPower;
                drawFolder(data.rows);
                openFolderId = id;
                categoryId = id;
                userId = data.userId;
                adminFlag = data.isAdmin;
                emptyChoose();
                $(".file-container-flatten").height($(".background").height() - $(".toolBar").outerHeight(true) - $("#laypageAre").outerHeight(true) - 10);
                btnState();
                dbclickover = true
            }
        });
    });
}

/*重载树*/
function refreshTree() {
    var $tree = $("#folderTree");
    $tree.jstree(true).refresh();
}
/*目录双击事件  为了不让区分，快速单击（打开）和缓慢单击（重命名）， 用了延时操作*/
function dbclick(id, name) {
    if (dbclickover == true) {
        if (clickFlag) {//取消上次延时未执行的方法
            clickFlag = clearTimeout(clickFlag);
        }
        dbclickover = false;
        /*开始重命名后又进行双击操作需要撤回之前的操作*/
        $('#name' + reNameIndex).removeClass("hide");
        $('#inputName' + reNameIndex).addClass("hide");
        $('#inputName' + reNameIndex).val(reNameParem);
        reNameFlag = false;
        pathId.push(id);
        pathName.push(name);
        refreshFolder(id);
        createPath();
    }
}

/*目录单击  为了不让区分，快速单击（打开）和缓慢单击（重命名）， 用了setTimeout延时操作*/
function clickOneTime(e, id, name, index) {
    if (clickFlag) {//取消上次延时未执行的方法
        clickFlag = clearTimeout(clickFlag);
    }
    clickFlag = setTimeout(function () {
        var jq = $(e);
        if (key == 1) {       //单击时是否按了Ctrl（按了为多选）
            if (chooseFolder.indexOf(id) != -1) {      // 单击的目录是否已选中
                jq.removeClass("active");
                chooseFolder = chooseFolder.del(chooseFolder.indexOf(id));
                chooseFolderName = chooseFolderName.del(chooseFolder.indexOf(id));
            } else {
                jq.addClass("active");
                chooseFolder.push(id);
                chooseFolderName.push(name);
            }
        } else {
            if (chooseFolder.indexOf(id) == -1) {
                if (reNameFlag == true) {    //判断单击的目录是否处在重命名状态
                    $('#name' + reNameIndex).removeClass("hide");
                    $('#inputName' + reNameIndex).addClass("hide");
                    reNameFlag = false;
                    var inputname = $('#inputName' + reNameIndex).val().trim();
                    if (inputname != reNameParem) {
                        rename(inputname);
                    }
                }
                reNameIndex=index;
                reNameParem=name;
                $('.file').removeClass("active");
                emptyChoose();
                jq.addClass("active");
                chooseFolder.push(id);
                chooseFolderName.push(name);
            } else {
/*                for (var i = 0; i < chooseFolder.length; i++){
                    var ind = chooseFolder[i];
                    var f = ind.substring(ind.length - 1);
                    $('#inputName' + f).addClass("hide");
                    $('#name' + f).removeClass("hide");
                }*/
                $("section input").addClass("hide");
                $("h1").removeClass("hide")
                $('#name' + index).addClass("hide");
                $('#inputName' + index).removeClass("hide");
                $('#inputName' + index).focus();
                reNameFlag = true;
                reNameParem = name;
                reNameIndex = index;
            }
        }
        btnState()
    }, 200);//延时300毫秒执行
}

/*数组删除某一项调用的方法*/
Array.prototype.del = function (n) {
    if (n < 0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0, n).concat(this.slice(n + 1, this.length));
}

/*键盘按键按下监听（多选时监听ctrl）*/
$(document).keydown(function (e) {
    if (e.ctrlKey) {
        key = 1;
    } else if (e.shiftKey) {
        key = 2;
    } else if(e.keyCode == 13) {
        renameFolder();
    }
    //$("#bb").val("初始值:"+ibe+" key:"+key);
}).keyup(function () {
    key = 0;
});

/*清空选中*/
function emptyChoose() {
    chooseFolder = [];
    chooseFolderName = [];
}

/*修改名字时，点击其他位置保存*/
$(document).click(function (e) {
    if ($(e.target)[0] == $('.file-container-flatten')[0] || $(e.target)[0] == $('.content')[0] || $(e.keyCode).val() == 13) {
        renameFolder();
    }
});
/*提交重命名*/
function renameFolder(){
    if (reNameFlag == true) {
        $('#name' + reNameIndex).removeClass("hide");
        $('#inputName' + reNameIndex).addClass("hide");
        reNameFlag = false;
        var inputname = $('#inputName' + reNameIndex).val().trim();
        if (inputname != reNameParem) {
            rename(inputname);
        }
    }
    $('.file').removeClass("active");
    emptyChoose();
    btnState();
}
/*控制btn 显示隐藏的方法*/
function btnState() {
    if (chooseFolder.length == 0) {
        $('.clickBtn').hide()
        if (cutFolder.length > 0) {
            $('#pasteFolder').hide()
        }
    } else {
        $('.clickBtn').show()
        if (cutFolder.length == 0) {
            $('#pasteFolder').hide()
        }
        //多选时，重命名不可用
        if(chooseFolder.length > 1){
            $("#updateName").css("display","none");
        }
    }
}
function createAuthorityFolder(group, person) {
    if (editFlag) {
        $(".edit-name-list").empty();
    } else {
        $(".name-list").empty();
    }
    for (var i = 0; i < group.length; i++) {
        var operateType = '';
        if(group[i].operateType == 0){
            operateType = '（查看）';
        }else if(group[i].operateType == 1){
            operateType = '（上传）';
        }else{
            operateType = '（管理）';
        }
        var param = '';
        if(group[i].type == 2){
            param = '<div class="name-item org">' +
                '<p>' + group[i].name + operateType + '</p>' +
                '<span class="del_span" value="1"></span>' +
                '</div>'
        }else{
            param = '<div class="name-item qz">' +
                '<p>' + group[i].name + operateType + '</p>' +
                '<span class="del_span" value="1"></span>' +
                '</div>'
        }
        if (editFlag) {
            $(".edit-name-list").append(param);
        } else {
            $(".name-list").append(param);
        }
    }
    for (var i = 0; i < person.length; i++) {
        var param = '';
        var operateType = '';
        if(person[i].operateType == 0){
            operateType = '（查看）';
        }else if(person[i].operateType == 1){
            operateType = '（上传）';
        }else{
            operateType = '（管理）';
        }
        if (person[i].type == 2) {
            param = '<div class="name-item org">' +
                '<p>' + person[i].name+ operateType + '</p>' +
                '<span class="del_span" value="0"></span>' +
                '</div>'
        } else {
            param = '<div class="name-item people">' +
                '<p>' + person[i].name+ operateType + '</p>' +
                '<span class="del_span" value="0"></span>' +
                '</div>'
        }
        if (editFlag) {
            $(".edit-name-list").append(param);
        } else {
            $(".name-list").append(param);
        }
    }
    $(".del_span").click(function () {
        if ($(this).attr('value') == 0) {
            for (var i = 0; i < personId.length; i++) {
                if (personId[i].name == ($(this).prev().html()).substring(0,($(this).prev().html()).length-4)) {
                    personId.splice(i, 1);
                }
            }
        } else {
            for (var i = 0; i < groupId.length; i++) {
                if (groupId[i].name == ($(this).prev().html()).substring(0,($(this).prev().html()).length-4)) {
                    groupId.splice(i, 1);
                }
            }
        }
        $(this).parent(".name-item").remove();
    });
}
function createAuthorityPowerFolder(group, person) {
    layui.use(['form'], function () {
        var form = layui.form;
        if (editFlag) {
            $(".edit-name-list-power").empty();
            if (group.length != 0 || person.length != 0) {
                $('#isEditEdit').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
                $('#isEditChild').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
            } else {
                $('#isEditEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $('#isEditChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $("#isEditEdit").prop("checked", false);
                $("#isEditChild").prop("checked", false);
            }
        } else {
            $(".name-list-power").empty();
            if (group.length != 0 || person.length != 0) {
                $('#isEdit').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
                $('#isChild').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
            } else {
                $('#isEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $('#isChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $("#isEdit").prop("checked", false);
                $("#isChild").prop("checked", false);
            }
        }
        for (var i = 0; i < group.length; i++) {
            var param = '<div class="name-item qz">' +
                '<p>' + group[i].name + '</p>' +
                '<span class="del_span_power" value="1"></span>' +
                '</div>'
            if (editFlag) {
                $(".edit-name-list-power").append(param);
            } else {
                $(".name-list-power").append(param);
            }
        }
        for (var i = 0; i < person.length; i++) {
            var param = '<div class="name-item people">' +
                '<p>' + person[i].name + '</p>' +
                '<span class="del_span_power" value="0"></span>' +
                '</div>'
            if (editFlag) {
                $(".edit-name-list-power").append(param);
            } else {
                $(".name-list-power").append(param);
            }
        }

        form.render();
    });
}
/*绘制已经选好的权限，（群组，人员）*/
function createAuthority(group, person) {
    if (editFlag) {
        $(".edit-name-list").empty();
    } else {
        $(".name-list").empty();
    }
    for (var i = 0; i < group.length; i++) {
        var operateType = '';
        if(group[i].operateType == 0){
            operateType = '（查看）';
        }else if(group[i].operateType == 1){
            operateType = '（上传）';
        }else{
            operateType = '（管理）';
        }
        var param = '';
        if(group[i].type == 2){
            param = '<div class="name-item org">' +
                '<p>' + group[i].name + operateType + '</p>' +
                '<span class="del_span" value="1"></span>' +
                '</div>'
        }else{
            param = '<div class="name-item qz">' +
                '<p>' + group[i].name + operateType + '</p>' +
                '<span class="del_span" value="1"></span>' +
                '</div>'
        }
        if (editFlag) {
            $(".edit-name-list").append(param);
        } else {
            $(".name-list").append(param);
        }
    }
    for (var i = 0; i < person.length; i++) {
        var param = '';
        var operateType = '';
        if(person[i].operateType == 0){
            operateType = '（查看）';
        }else if(person[i].operateType == 1){
            operateType = '（上传）';
        }else{
            operateType = '（管理）';
        }
        if (person[i].type == 2) {
            param = '<div class="name-item org">' +
                '<p>' + person[i].name+ operateType + '</p>' +
                '<span class="del_span" value="0"></span>' +
                '</div>'
        } else {
            param = '<div class="name-item people">' +
                '<p>' + person[i].name+ operateType + '</p>' +
                '<span class="del_span" value="0"></span>' +
                '</div>'
        }
        if (editFlag) {
            $(".edit-name-list").append(param);
        } else {
            $(".name-list").append(param);
        }
    }
    $(".del_span").click(function () {
        if ($(this).attr('value') == 0) {
            for (var i = 0; i < personId.length; i++) {
                if (personId[i].name == ($(this).prev().html()).substring(0,($(this).prev().html()).length-4)) {
                    personId.splice(i, 1);
                }
            }
        } else {
            for (var i = 0; i < groupId.length; i++) {
                if (groupId[i].name == ($(this).prev().html()).substring(0,($(this).prev().html()).length-4)) {
                    groupId.splice(i, 1);
                }
            }
        }
        $(this).parent(".name-item").remove();
    });
}
function createAuthorityPower(group, person) {
    layui.use(['form'], function () {
        var form = layui.form;
        if (editFlag) {
            $(".edit-name-list-power").empty();
            if (group.length != 0 || person.length != 0) {
                $('#isEditEdit').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
                $('#isEditChild').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
            } else {
                $('#isEditEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $('#isEditChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $("#isEditEdit").prop("checked", false);
                $("#isEditChild").prop("checked", false);
            }
        } else {
            $(".name-list-power").empty();
            if (group.length != 0 || person.length != 0) {
                $('#isEdit').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
                $('#isChild').removeClass('layui-checkbox-disbaled layui-disabled').removeAttr('disabled', "false");
            } else {
                $('#isEdit').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $('#isChild').addClass('layui-checkbox-disbaled layui-disabled').attr('disabled', "true");
                $("#isEdit").prop("checked", false);
                $("#isChild").prop("checked", false);
            }
        }
        for (var i = 0; i < group.length; i++) {
            var param = '<div class="name-item qz">' +
                '<p>' + group[i].name + '</p>' +
                '<span class="del_span_power" value="1"></span>' +
                '</div>'
            if (editFlag) {
                $(".edit-name-list-power").append(param);
            } else {
                $(".name-list-power").append(param);
            }
        }
        for (var i = 0; i < person.length; i++) {
            var param = '<div class="name-item people">' +
                '<p>' + person[i].name + '</p>' +
                '<span class="del_span_power" value="0"></span>' +
                '</div>'
            if (editFlag) {
                $(".edit-name-list-power").append(param);
            } else {
                $(".name-list-power").append(param);
            }
        }

        form.render();
    });
}
function rename(inputname) {
    if (noChildPower == 0) {
        layer.msg("您没有重命名目录权限", {anim: 6, icon: 0});
        return;
    }
    inputname = inputname.trim();
    if (inputname == '' || inputname == undefined) {
        layer.msg("目录名称不能为空", {anim: 6, icon: 0});
        $('#inputName' + reNameIndex).val(reNameParem);
        return;
    }
    var pattern = new RegExp("^[^/\\\\:\\*\\?\\'\\‘\\<\\>\\|\"]{0,255}$");
    //特殊字符
    if (!pattern.test(inputname)) {
        layer.msg("输入的目录名称不合法", {anim: 6, icon: 0});
        $('#inputName' + reNameIndex).val(reNameParem);
        return;
    }
    $.ajax({
        type: "post",
        url: "/fsFolder/addCheck",
        data: {
            name: inputname,
            folderId:chooseFolder[0],
            parentFolderId: openFolderId,
        },
        async: false,
        cache: false,
        dataType: "json",
        success: function (data) {
            if (data == "false") {
                layer.msg("“" + inputname + "”目录已存在", {anim: 6, icon: 0});
                $('#inputName' + reNameIndex).val(reNameParem);
                return;
            } else {
                $.ajax({
                    type: "POST",
                    url: "/fsFolder/update",
                    data: {
                        ids: chooseFolder[0],
                        parentFolderId: openFolderId,
                        folderName: inputname,
                    },
                    contentType: "application/x-www-form-urlencoded",
                    dataType: "json",
                    async: false,
                    success: function (result) {
                        refreshFolder(openFolderId);
                        refreshTree();
                    }
                });
            }
        }
    });
}