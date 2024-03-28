var zTree_Menu = null;//ztree对象
var util;//工具
var layerView;
var topicId = null;//专题ID
var topicPic;
var treeData;
layui.use(['form', 'jquery','util','layer','table','Hussar','jstree','HussarAjax','element','laydate','upload'], function() {
    var form = layui.form,
        $ = layui.jquery,
        table = layui.table,
        layer = layui.layer,
        Hussar = layui.Hussar,
        jstree=layui.jstree,
        $ax=layui.HussarAjax,
        element = layui.element,
        laydate = layui.laydate,
        upload = layui.upload,
        util = layui.util;

    //              <<<<<<<<<<<<<<<<<<<<<<<<<<<<
    var personArr= []; // 专职人员
    var ProfessiionalMgr = {
        personTree: $("#showEmployeeTree"),//人员树
        personIdArr:[], //专职人员ID

    };

    layui.data("childChecked",null);
    /*关闭弹窗*/
    $("#cancel").on('click',function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });

    /*新增/编辑专题*/
    $("#saveBtn").on('click',function(){

        var id = null;
        if($("#id").length > 0 ){
            id = $("#id").val();
        }
        var majorId = $("#majorId").val();//专业id
        var userName = $("#userName").val().trim();//专职
        var userId = $("#userId").val().trim();//专职 id
        var showOrder = $("#showOrder").val();//排序

        if(majorId == ""||majorId == undefined || majorId == null){
            layer.msg("专业不能为空", {anim:6,icon: 0});
            return;
        }
        var major =  $("#majorId option:selected").text(); //专业
        if(userName.length > 300){
            layer.msg("专职人员不能超过300个字", {anim:6,icon: 0});
            return;
        }

        var lock = true;
        var ajax = new $ax(Hussar.ctxPath + "/professional/operationJudge", function(data) {
            if(data != '0'){
                lock = false;
            }
        });
        ajax.set("majorId",majorId);
        ajax.set("id",id);
        ajax.start();

        if(!lock){
            layer.msg("专业不能重复", {anim:6,icon: 0});
            return;
        }
        var url;//请求地址
        var successMsg,errorMsg;//成功失败提示
        if(id == null){
            url = "/professional/add";
            successMsg = "新增成功";
            errorMsg = "新增失败"
        } else {
            url = "/professional/update";
            successMsg = "修改成功";
            errorMsg = "修改失败"
        }




        var ajax = new $ax(Hussar.ctxPath + url, function(data) {
            if(data.code == "200"){
                layer.alert(successMsg, {
                    icon :  1,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示',
                    end: function () {
                        parent.tableIns.reload({
                            where: {
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            }
                        });
                        layui.data('checked',null);
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                    }
                },function(){
                    parent.tableIns.reload({
                        where: {
                            //防止IE浏览器第一次请求后从缓存读取数据
                            timestamp: (new Date()).valueOf()
                        }
                    });
                    layui.data('checked',null);
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index);
                });
            }else{
                layer.alert(errorMsg, {
                    icon :  2,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                });
            }

        }, function(data) {
        });
        ajax.set("id",id);
        ajax.set("majorId",majorId);
        ajax.set("major",major);
        ajax.set("userId",userId);
        ajax.set("userName",userName);
        ajax.set("showOrder",showOrder);
        ajax.start();


    });
    var active = {
        addDoc:function(){
            var nodes = zTree_Menu.getCheckedNodes(true);
            var idArr = '';
            for(var i = 0;i < nodes.length;i++){
                if(nodes[i].code == undefined){
                    if(idArr == ''){
                        idArr += nodes[i].id
                    }else{
                        idArr += (","+nodes[i].id)
                    }
                }
            }

        }
    };
    //.渲染完成回调
    $('.layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    ceshisearch = function () {
        var to = false;

        //角色树查询
        $("#personSearch").keyup(function () {
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                var v = $("#personSearch").val();

                var temp1 = $personTree.is(":hidden");
                if (temp1 == true) {
                    $personTree.show();
                }
                $personTree.jstree(true).search(v);

                //定位到符合查询结果的树节点上

                var searchResult1 = $personTree.jstree('search', v);
                $(searchResult1).find('.jstree-search').focus();
            }, 250);
        });

    };

    $(".laydata-input").on("focus",function () {
        $(this).blur()
    })

    // 人员弹出树
    $('#userName').on('click', function () {
        $("#personSearch").val('');
        var index = layer.open({
            type: 1,
            title: '专职人员',
            area: ['400px','350px'],
            btn: ['确定', '取消'],
            content: $('#employeeTreeDiv'),
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            success: function () {

                // 用户
                $personTree = ProfessiionalMgr.personTree;
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
                        "1":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/com.png"},
                        "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/dept.png"},
                        "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/station.png"},
                        "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/empl.png"},
                        "USER":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/user.png"}
                    },
                    search: ceshisearch()
                }).on('loaded.jstree', function (e, data) {
                    $personTree.jstree().close_all();
                    var personIdArr = $('#userId').val();
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

            },
            yes: function (index) {
                var treeNodes = $('#showEmployeeTree').jstree(true).get_checked(true);//获取人员选中节点

                var treeId = '';
                var treeText = '';
                if (treeNodes.length == 0) {
                    layer.msg("请选择人员", {anim: 6, icon: 0});
                    return;
                }
                personArr = [];
                personIdArr = [];

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

                if (treeText.length > 0 && treeText.charAt(treeText.length - 1) == ",") {
                    treeText = treeText.substring(0, treeText.length - 1);
                }
                if (treeId.length > 0 && treeId.charAt(treeId.length - 1) == ",") {
                    treeId = treeId.substring(0, treeId.length - 1);
                }
                // 赋值到输入框中
                $('#userName').val(treeText);
                $('#userId').val(treeId);
                layer.close(index);
            },
            end:function () {
                $('#employeeTreeDiv').hide();
            }
        })
    })
});