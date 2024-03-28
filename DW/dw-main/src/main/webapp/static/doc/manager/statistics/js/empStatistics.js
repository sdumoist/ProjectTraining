var util;//全局变量，这样time2date就可以调用，否则报错
var buttonType;//点击按钮的类型
var active;
var tableIns;
var treedata;
var count;
var parentSortNames;
var parentSortIds;
layui.use(['jquery', 'layer', 'Hussar', 'jstree', 'table', 'HussarAjax', 'form', 'element'], function () {
    var Hussar = layui.Hussar;
    var $ = layui.jquery;
    var layer = layui.layer;
    var $ax = layui.HussarAjax;
    var jstree = layui.jstree;
    var table = layui.table;
    var form = layui.form;
    var RoleManage = {};
    var element = layui.element;
    //.存储当前页数据集
    var pageData = [];
    //.存储已选择数据集，用普通变量存储也行
    layui.data('checked', null);
    /*初始化页面*/
    RoleManage.initPage = function () {
        this.initTree();
        this.init();
        this.initButtonEvent();
        start()
    }
    RoleManage.initTree = function () {
        var $tree = $("#powerTree");
        $tree.data('jstree', false).empty();
        //$tree.jstree.defaults.core.themes.dots(false)
        $tree.jstree({

            core: {

                "themes" : {
                    // "stripes" : true,//背景是否显示间纹。
                    "dots": false,//是否显示树连接线
                    // "icons": true,//是否显示节点的图标
                    // "ellipsis": true//节点名过长时是否显示省略号
                },
                data: {
                    "url": Hussar.ctxPath + "/orgMain/orgTree",
                    "data": function (node) {
                        return {
                            "parentId": node.id,
                            "isEmployee": ""
                        };
                    }
                }
            },

            types: {
                "1": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/com.png"},
                "2": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/dept.png"},
                "3": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/station.png"},
                "9": {'icon': Hussar.ctxPath + "/static/assets/img/treeContext/" + theme + "/empl.png"}
            },
        });
        $tree.bind('select_node.jstree', function (obj, e) {
            $('#groupName').val(e.node.text);
            $('#groupId').val(e.node.id);

            if(e.node.original.issort != '0'){
                $("#parentSortName").val(e.node.text);
                $("#parentSortId").val(e.node.id);
            }
            tableIns.reload({
                page: {
                    curr: 1
                },
                where: {
                    groupId: e.node.id
                }
            })
        })
    };
    form.on('select(testSelect)', function (data) {
        var elem = $(data.elem);
        var trElem = elem.parents('tr');
        var tableData = table.cache['groupList'];
        // 更新到表格的缓存数据中，才能在获得选中行等等其他的方法中得到更新之后的值
        var NUM = tableData[trElem.data('index')]['unit']
        tableData[trElem.data('index')]['unit'] = data.value;
        // 其他的操作看需求 TODO
        var space= tableData[trElem.data('index')]['SpaceNum'];
        var id= tableData[trElem.data('index')]['USERID'];
        if (NUM != data.value) {
            if (data.value == 'GB') {
                space = space * 1024
            }

            $.ajax({
                type: "post",
                url: Hussar.ctxPath+"/empStatistics/updateSpace",
                data: {
                    "id": id,
                    "space": space
                },
                async: true,
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                success: function (data) {
                    layer.alert('修改成功', {
                        icon: 1,
                        shadeClose: true,
                        skin: 'layui-layer-molv',
                        shift: 5,
                        area: ['300px', '180px'],
                        title: '提示'
                    })
                    tableIns.reload({
                        page: {
                            curr: 1
                        },
                        where: {
                            groupId: $('#groupId').val(),
                            uerName: $('#searchName').val()
                        }
                    })
                }
            });
        }
    });
    RoleManage.init = function () {
        //初始化表格
        tableIns = table.render({
            elem: '#groupList', //指定原始表格元素选择器（推荐id选择器）
            height: $("body").height() - $(".toolBar").height() - 35, //容器高度
            url: Hussar.ctxPath+'/empStatistics/empStatisticsList', //数据接口
            id: 'groupList',
            where: {
                groupId: $('#groupId').val()
            },
            request: {
                pageName: 'page', //页码的参数名称，默认：page
                limitName: 'limit' //每页数据量的参数名，默认：limit
            }
            ,
            cols: [[
                {field: 'NAME', title: '姓名', align: 'center', width: '20%'},
                {field: 'orgName', title: '部门名称', width: '20%', align: "center"},
                {field: 'SpaceNum', title: '全部空间', width: '14.4%', align: 'center',edit:'text',style:'background-color:#FFFAFA'},
                {field:'unit',title: '单位', width: '12%', align: "center",templet: function (d) {
                    var unit = d.unit;
                    return '<select name="unit" class="selectUnit" lay-filter="testSelect" lay-verify="required" data-value="' + unit + '" >\n' +
                        '        <option value=""></option>\n' +
                        '        <option value="MB">MB</option>\n' +
                        '        <option value="GB">GB</option>\n' +
                        '      </select>';
                }
                },
                {field: 'NUM', title: '可用空间', width: '17%', align: "center"},
                {field: 'USERNUM', title: '已用空间', width: '17%', align: "center"},
            ]], //设置表头
            done: function (res) {
                //.假设你的表格指定的 id="topicList"，找到框架渲染的表格
                var tbl = $('#groupList').next('.layui-table-view');
                //记下当前页数据，Ajax 请求的数据集，对应你后端返回的数据字段
                pageData = res.data;
                var len = pageData.length;
                //.遍历当前页数据，对比已选中项中的 id
                for (var i = 0; i < len; i++) {
                    if (layui.data('checked', pageData[i]['userId'])) {
                        //选中它，目前版本没有任何与数据或表格 id 相关的标识，不太好搞，土办法选择它吧
                        tbl.find('table>tbody>tr').eq(i).find('td').eq(0).find('input[type=checkbox]').prop('checked', true);
                    }
                }
                count || this.elem.next('.layui-table-view').find('.layui-table-header').css('overflow', 'auto');
                layui.each($('.selectUnit'), function (index, item) {
                    var elem = $(item);
                    elem.val(elem.data('value')).parents('div.layui-table-cell').css('overflow', 'visible');
                });
                layui.each(pageData, function(i, item) {
                    if (item.adminFlag == "1") {
                        tbl.find('tr[data-index=' + i + ']').find('td').eq(2).removeAttr("style").data('edit', false)
                    }
                });
                form.render();
                //.PS：table 中点击选择后会记录到 table.cache，没暴露出来，也不能 mytbl.renderForm('checkbox');
            }
            ,
            page: true, //开启分页
            //id : 'groupListView',
            even: true,
        });
        //.监听选择，记录已选择项
        table.on('checkbox(groupList)', function (obj) {
            //.全选或单选数据集不一样
            var data = obj.type == 'one' ? [obj.data] : pageData;
            //.遍历数据
            $.each(data, function (k, v) {
                //.假设你数据中 id 是唯一关键字
                if (obj.checked) {
                    //.增加已选中项
                    layui.data('checked', {
                        key: v.userId, value: v
                    });
                } else {
                    //.删除
                    layui.data('checked', {
                        key: v.userId, remove: true
                    });
                }
            });
        });
    }
    table.on('edit(groupList)',function (obj) {
        var data = obj.data;
        var space=  data.SpaceNum;
        if(space.length<=0){
            layer.msg("个人空间不能为空 ",{anim:6,icon: 0});
            return
        }
        var pattern = /^\d{1,3}(\.\d{1,2})?$/;
        //特殊字符
        if(!pattern.test(space)){
            layer.msg("请输入1000以内的两位小数或整数", {anim:6,icon: 0});
            return;
        }
        if (data.unit=='GB'){
            space = space*1024
        }

        $.ajax({
            type: "post",
            url: Hussar.ctxPath+"/empStatistics/updateSpace",
            data: {
                "id": data.USERID,
                "space":space
            },
            async: true,
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success:function (data) {
                layer.alert('修改成功',{
                    icon: 1,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    shift: 5,
                    area: ['300px', '180px'],
                    title: '提示'
                })
                tableIns.reload({
                    page: {
                        curr: 1
                    },
                    where: {
                        groupId: $('#groupId').val(),
                        uerName: $('#searchName').val()
                    }
                })
            }
        });
    })

    //格式化按钮
    RoleManage.initButtonEvent = function () {
        $("#searchBtn").on('click', function () {
            tableIns.reload({
                page: {
                    curr: 1
                },
                where: {
                    groupId: $('#groupId').val(),
                    uerName: $('#searchName').val()
                }
            })
        });
    }



    /*刷新组织机构树*/
    RoleManage.refreshTree = function () {
        $("#powerTree").jstree(true).refresh();
    }



    RoleManage.initFrameHeight = function () {
        var h = $(window).height() - 60;
        var h2 = $(window).height() - 10;
        $("#roleIframe").css("height", h2 + "px");
        $("#roleInfo").css("height", h + "px");
    };
    /**
     * 初始化
     */
    $(function () {
        RoleManage.initPage();
        $(window).resize(function () {
            RoleManage.initFrameHeight();
            RoleManage.init();
        });
        $('#searchName').bind('keypress', function (event) {
            if (event.keyCode == "13") {
                $("#searchBtn").click();
            }
        });
    });
});
//获取分组树数据
function getSort(){
    layui.use(['Hussar'], function(){
        var Hussar = layui.Hussar;

        $.ajax({
            type:"post",
            url: Hussar.ctxPath+"/group/getSortTree",
            data:{
                treeType:"2"
            },
            async:true,
            cache:false,
            dataType:"json",
            contentType : "application/x-www-form-urlencoded;charset=UTF-8",
            success:function(result){
                var arrays = [];
                if (result.length > 0) {
                    for (var i = 0; i < result.length; i++) {
                        var arr = {
                            id: result[i].ID,
                            text: result[i].TEXT,
                            parent: result[i].PARENT
                        }
                        arrays.push(arr);
                    }
                    //$('#groupId').val(result[0].ID)
                    //$('#groupName').val(result[0].TEXT)
                }
                treeData = arrays;
            }, error:function(data) {
                Hussar.error("加载群组列表失败");
            }
        });
    });
}
function start() {
    getSort();
}
