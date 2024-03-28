var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var personTreeData = [];
var personId = [];
var openPersonId = [];
var tableIns;
var treedata;
var parentSortNames;
var parentSortIds;
var oldData = [];
var cacheData = [];
var pageData = [];
layui.use(['jquery', 'layer', 'Hussar', 'jstree', 'table', 'HussarAjax', 'form', 'element','Hussar'], function () {
    var Hussar = layui.Hussar;
    var $ = layui.jquery,
        table = layui.table,
        element = layui.element ;
    var layer = layui.layer;
    var $ax = layui.HussarAjax;
    var jstree = layui.jstree;
    var table = layui.table;
    var form = layui.form;
    var personManage = {};
    var element = layui.element;
    var Hussar = layui.Hussar;
    //.存储当前页数据集
    var pageData = [];
    //.存储已选择数据集，用普通变量存储也行
    layui.data('checked', null);
    /*初始化页面*/
    personManage.initPage = function () {
        this.init();
        //初始化数据
    }
    personManage.init = function () {
        //初始化表格
        tableIns = table.render({
            elem: '#accessList', //指定原始表格元素选择器（推荐id选择器）
            height: $(".content").height() - $(".content .layui-form").outerHeight(true) - 10, //容器高度
            url: Hussar.ctxPath+'/access/accessList', //数据接口
            id: 'accessList',
            done: function (res) {
                //.假设你的表格指定的 id="topicList"，找到框架渲染的表格
                var tbl = $('#accessList').next('.layui-table-view');
                //记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
                pageData = res.data;
                var len = pageData.length;
                //.遍历当前页数据，对比已选中项中的 id

                form.render('checkbox');
                //.PS：table 中点击选择后会记录到 table.cache，没暴露出来，也不能 mytbl.renderForm('checkbox');
            }
            ,
            request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            ,
            page: true, //开启分页
            where: {
            //防止IE浏览器第一次请求后从缓存读取数据
                timestamp: (new Date()).valueOf()
            },
            //id : 'groupListView',
            even: true,
            cols: [[
                {field: 'id', title: 'id',hide:true},
                {title: '序号', type: 'numbers', width: '42', align: "center"},
                {field: 'department', title: '部门名称', width: '24%', align: "center"},
                {field: 'userName', title: '人员名称', align: "center", width: '18%'},
                {field: 'createTime', title: '操作时间', align: "center", width: '23%'},
                {field: 'createUserName', title: '操作人', align: "center", width: '24%'},
                { fixed: 'right', title: '操作', width: '8%', toolbar: '#delPersonAuth', align: "center"}
            ]] //设置表头
        });
    }

//头工具栏事件
        table.on('tool(accessList)', function (obj) {
            var ids = obj.data.userId;
            if(obj.event === 'delPerson'){
                layer.confirm('确定要删除所选中的人员吗？', function () {
                    var ajax = new $ax(Hussar.ctxPath + "/access/delPersonExtranetAuth", function(data) {
                        if (data == true) {
                            layer.msg("删除成功", {icon: 1});
                            tableIns.reload({
                                where:{
                                    //防止IE浏览器第一次请求后从缓存读取数据
                                    timestamp: (new Date()).valueOf()
                                },
                                done: function(res, curr, count){
                                    if (res.data.length == 0&&curr!=1){
                                        tableIns.reload({
                                            page: {
                                                curr: curr-1
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            layer.alert('删除失败', {
                                icon: 2,
                                shadeClose: true,
                                skin: 'layui-layer-molv',
                                shift: 5,
                                area: ['300px', '180px'],
                                title: '提示'
                            });
                        }
                    }, function(data) {

                    });
                    ajax.set("ids",ids);
                    ajax.start();
                })
            }
        })


    /*刷新组织机构树*/
    personManage.refreshTree = function () {
        $("#powerTree").jstree(true).refresh();
    }




    /**
     * 初始化
     */
    $(function () {
        personManage.initPage();
        $(window).resize(function () {
            personManage.init();
        });
    });
    $("#managePerson").click(function () {
        var layerView=layer.open({
            type: 1,
            area: ['30%', '85%'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: false,
            btn: ['确定', '取消'],
            shade: 0.4,
            title: ['人员外网访问权限配置','font-size: 14px; font-weight:bold;'],
            content: $("#personTreeDiv"),
            success:function(){
                personManage.initEmployeeTree();
            },
            btn1:function(index, layero){ // 点击确定按钮 获取选中的目录id
                var ref = $('#powerTree').jstree(true);//获得整个树
                var nodes = ref.get_selected(true);//获得所有选中节点，返回值为数组

                var allPersonData = []; // 所有选中的目录信息
                $.each(nodes, function(i, nd) {
                    if (nd.original.type=="USER"){
                        allPersonData.push({"userId": nd.original.id, "userName": nd.original.text,"department":nd.original.parent})
                    }else if(nd){

                    }else {
                        Hussar.error("请选择用户");
                    }
                });

                // 保存目录配置数据
                var ajax = new $ax(Hussar.ctxPath + "/access/savePersonExtranetAccess",function(result) {
                    if(result.code == 200){
                        Hussar.success("保存成功");
                        layer.close(layerView);

                        tableIns.reload({
                            page: {
                                curr: 1
                            },
                            where: {
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            }
                        })
                    }
                }, function(data) {
                    Hussar.error("保存失败");
                });
                ajax.set("param", JSON.stringify(allPersonData));
                ajax.start();
            },
            btn2:function(index, layero){
                layer.close(layer.index);
            }
        });
    });


    personManage.initEmployeeTree = function () {
        var personTreeData = [];
        var ajax = new $ax(Hussar.ctxPath + "/access/userTree",function(result) {
            personTreeData = result;
        }, function(data) {
            Hussar.error("获取人员失败");
        });
        ajax.start();

        //初始化数列表
        var $tree = $("#powerTree");
        $tree.data('jstree', false).empty();
        $tree.jstree({
            core: {
                "themes" : {
                    // "stripes" : true,//背景是否显示间纹。
                    "dots": true,//是否显示树连接线
                    "icons": true,//是否显示节点的图标
                    "ellipsis": true //节点名过长时是否显示省略号
                },
                "multiple": true,//单选
                check_callback: true,
                data: personTreeData
            },
            "checkbox": { // 设置复选框不级联
                "three_state": true,
                "cascade": 'undetermined',
                "tie_selection": true,
                "keep_selected_style": false
            },
            plugins: ['search', 'checkbox'],
            search: personManage.search()

        }).on('loaded.jstree', function (e, data) {

        });
    }

    /**
     * 所有树的模糊查询
     */
    personManage.search = function(){
        $("#treeSearchName").val("");
        $(".jstree-search").remove();
        $(".search-results").html("");
        var $tree = $("#powerTree");
        var to = false;
        //用户树查询
        $("#treeSearchName").keyup(function () {
            if (to) { clearTimeout(to); }
            to = setTimeout(function () {
                var v = $("#treeSearchName").val();
                if(v==null){
                    v = "";
                }
                var temp = $tree.is(":hidden");
                if (temp == true) {
                    $tree.show();
                }
                //$tree.jstree(true).search(v);
                $tree.jstree('search', v).find('.jstree-search').focus();
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
    }


    // 搜索
    $("#searchBtn").click(function () {
        var PersonName = $("#PersonNameSearch").val().trim();
        //文件名
        var pattern = new RegExp("^[^/\\\\:\\*\\'\\‘\\?\\<\\>\\|\"]{0,255}$");
        //特殊字符
        if (!pattern.test(PersonName)) {
            layer.alert('输入的名字不合法', {
                icon: 0,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: '提示'
            });
            return;
        }
        tableIns.reload({
            page: {
                curr: 1
            },
            where: {
                personName: PersonName,
                //防止IE浏览器第一次请求后从缓存读取数据
                timestamp: (new Date()).valueOf()
            }
        })
    });

});


