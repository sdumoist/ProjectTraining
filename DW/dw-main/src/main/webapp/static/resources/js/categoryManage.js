/**
 * Title:分类维护
 * author:zhangzhen
 * Date: 2018/1/19
 */
(function () {
    var zTree_Menu = null;
    var unitcode = "";
    var unitname1 = "";
    var unitBelongCode = "";
    $(document).ready(function () {
        gridView.initPage();
    });
    /**
     * 列表页面实现
     */
    var gridView = {
        gridColumns: [
            {
                field : 'checked',
                title: '1',
                checkbox : true
            },{
                field: 'id',
                title: 'id',
                visible: false
            },{
                field: 'number',
                title: '序号',
                align:'center',
                width:'50px',
                formatter: function (value, row, index) {
                    var page = $('#categoryTable').bootstrapTable("getPage");
                    return page.pageSize * (page.pageNumber - 1) + index + 1;
                }
            },{
                field: 'name',
                title: '分类名称',
                align:'center',
                sortable:true
            },{
                field: 'pName',
                title: '所属分类名称',
                align:'center',
                sortable:true
            },{
                field: 'pId',
                title: '所属分类ID',
                visible:false
            },{
                field:'showOrder',
                title:'展示顺序',
                align:'center'
            }
        ],
        $table: $('#categoryTable'),
        treeObj :  $("#treeDemo"),

        /*初始化页面*/
        initPage: function() {
            var that = this;
            //初始化表格
            that.initTableView();
            //初始化按钮事件
            that.initButtonEvent();
            //初始化树
            that.initTree();
        },

        /*初始化按钮*/
        initButtonEvent:function(){
            var that = this;
            /*查询按钮点击事件*/
            $("#btnSearch").click(function() {
                var name = $("#categoryName");
                $('#categoryTable').bootstrapTable('destroy');
                that.initTableView();
            });

            /*重置按钮*/
            $('#btnReset').click(function(){
                $('#categoryName').val('');
                $('#categoryTable').bootstrapTable('destroy');
                that.initTableView();
            });

            /*新增按钮*/
            $('#addCategory').click(function(){
                //var selectnodes = zTree_Menu.getSelectedNodes();
                //if(selectnodes.length == 0){
                //    $.showInfoDlg("新增提示","请选择组织机构树节点！", 0);
                //}else{
                    that.addFiles();
                //}

            });

            ///*修改按钮*/
            //$('#btnEdit').click(function(){
            //    that.editFiles();
            //});
            //
            /*新增保存按钮*/
            $('#btnSave').click(function(){
                that.saveFile();
            });
            //
            /*新增取消按钮*/
            $("#btnClose").click(function () {
                that.closeWindow();
                $('#categoryTable').bootstrapTable('destroy');
                that.initTableView();
            });

            /*新增页面，弹出树*/
            $("#categoryBelongName").click(function(){
                that.treeSelectForm();
            });
            /*删除结点及其子节点*/
            $("#delCategory").click(function(){

            })

            ///*修改页面，弹出树*/
            //$("#unitBelongName2").click(function(){
            //    that.treeSelectForm();
            //});
            //
            ///*修改保存按钮*/
            //$('#btnSave2').click(function(){
            //    that.editFile();
            //});
            //
            ///*修改取消按钮*/
            //$("#btnClose2").click(function () {
            //    layer.close(infoWin);
            //    $('#loadQueryTable').bootstrapTable('destroy');
            //    that.initTableView();
            //});
            //
            ///*删除按钮*/
            //$("#btnDelete").click(function(){
            //    var rows= $("#loadQueryTable").bootstrapTable('getAllSelections');
            //    if(rows.length<=0){
            //        $.showInforDlg("操作提示","请选择要删除的数据！", 0);
            //    }else {
            //        layer.confirm('确定要删除选中的数据吗？', {icon: 3, title: '删除提示'}, function (index) {
            //            that.delete();
            //            layer.close(index);
            //        });
            //    }
            //});
        },

        /**
         * 新增方法
         */
        addFiles:function () {
            //设定弹出页大小
            if ($(window).width()<1024){//平板范围
                winWidth = "43%";
                winHeight = "35%";
            }else {//电脑范围
                winWidth = "640px";
                winHeight = "180px";
            }

            //加载弹出页信息
            infoWin = $("#addCategoryDiv").createLayerWindow({
                type: 1,
                title: "新增分类目录",
                shadeClose: false,
                shade: true,
                maxmin: false, //开启最大化最小化按钮
                area: [winWidth, winHeight],
                success: function () {

                }
            });
        },

        /**
         * 弹出修改页面
         */
        editFiles:function () {
            //设定弹出页大小
            if ($(window).width()<1024){//平板范围
                winWidth = "43%";
                winHeight = "35%";
            }else {//电脑范围
                winWidth = "640px";
                winHeight = "238px";
            }
            var rows= $("#loadQueryTable").bootstrapTable('getAllSelections');
            if(rows.length != 1){
                $.showInforDlg("操作提示","请选择一条需要修改的数据！", 0);
            }else {
                var row = rows[0];
                //加载弹出页信息
                infoWin = $("#organizationEdit").createLayerWindow({
                    type: 1,
                    title: "修改组织结构",
                    shadeClose: false,
                    shade: true,
                    maxmin: false, //开启最大化最小化按钮
                    area: [winWidth, winHeight],
                    success: function () {
                        $('#unitId2').val(row.UNITID);//单位ID
                        $('#unitName2').val(row.UNITNAME);//单位名称
                        $('#unitCode2').val(row.UNITCODE);//单位编号
                        $('#unitBelongName2').val(row.UNITNAME2);//所属单位名称
                        $('#unitBelong2').val(row.UNITBELONG);//所属单位编号
                        if(row.UNITTYPEID =="1"){
                            $('#unitTypeId2').val("duan");//单位类别
                        }else if(row.UNITTYPEID=="2"){
                            $('#unitTypeId2').val("zu");//单位类别
                        }else if(row.UNITTYPEID=="3"){
                            $('#unitTypeId2').val("ban");//单位类别
                        }else if(row.UNITTYPEID=="4"){
                            $('#unitTypeId2').val("chejian");//单位类别
                        }else if(row.UNITTYPEID=="5"){
                            $('#unitTypeId2').val("banzu");//单位类别
                        }else if(row.UNITTYPEID=="6"){
                            $('#unitTypeId2').val("ke");//单位类别
                        }else if(row.UNITTYPEID=="7"){
                            $('#unitTypeId2').val("suo");//单位类别
                        }else if(row.UNITTYPEID=="8"){
                            $('#unitTypeId2').val("qita");//单位类别
                        }
                        $('#valid2').val(row.VALID);//有效标识
                        $('#memo2').val(row.MEMO);//备注说明
                    }
                });
            }
        },

        /*删除数据*/
        delete:function () {
            var ids="";
            var self = this;
            var rows= $("#loadQueryTable").bootstrapTable('getAllSelections');
            for (var i = 0, l = rows.length; i < l; i++) {
                ids = ids + rows[i].UNITID+","
            }
            var url="/org/deleteData";
            $.ajax({
                traditional:true,//这使json格式的字符不会被转码
                data: {id:ids},
                async : false,
                type: "post",
                url: url,
                success: function(data){
                    if(data.code!= -1){
                        $.showInfoDlg("操作提示","删除成功！", 0);
                        $('#loadQueryTable').bootstrapTable('destroy');
                        self.initTableView();
                    }
                },
                error:function(data){
                    $.showInforDlg("操作提示","删除失败！", 0);
                }
            });
        },

        /**
         * 新增确定保存方法
         */
        saveFile:function(){
            var self = this;
            /*填写信息验证*/
            var categoryTitle = $("#categoryTitle").val();//分类名称
            if (categoryTitle == ""){
                $.showInfoDlg("保存提示","请填写分类目录名称！", 0);
                return;
            }
            var categoryBelongName = $("#categoryBelongName").val();//所属分类目录名称
            if(categoryBelongName == ""){
                $.showInfoDlg("保存提示","请选择所属分类目录名称！", 0);
                return;
            }
            var categoryBelong = $("#categoryBelong").val();//所属上级分类目录ID
            var showOrder = $("#showOrder").val();//展示顺序

            $.ajax({
                type: "post",
                url: "/category/saveCategory",
                async: false,
                data:{
                    title: categoryTitle,
                    pid:categoryBelong,
                    showOrder:showOrder
                },
                success: function(data){
                    if (data == 1) {
                        $.showInfoDlg("操作提示", "保存成功！", 0);
                        self.closeWindow();
                        $('#categoryTable').bootstrapTable('destroy');
                        self.initTableView();
                    }
                    if (data == -1) {
                        $.showInfoDlg("操作提示", "保存失败！", 0);
                        self.closeWindow();
                        $('#categoryTable').bootstrapTable('destroy');
                        self.initTableView();
                    }
                },
                error: function (data) {
                    $.showInfoDlg("操作提示", "保存失败！", 0);
                    self.closeWindow();
                }
            });
        },

        //关闭新增弹出框
        closeWindow: function() {
            layer.close(infoWin);
            $("#categoryTitle").val("");
            $("#categoryBelong").val("");
            $("#showOrder").val("");
            $("#categoryBelongName").val("");
        },

        //初始化加载table表格
        initTableView: function() {
            var that = this;
            that.$table.bootstrapTable({
                url: "/category/categoryList",        //请求后台的URL（*）
                dataType: "json",
                method: 'post',                      //请求方式（*）
                contentType:"application/x-www-form-urlencoded; charset=UTF-8",
                striped: true,                      //是否显示行间隔色
                cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                pagination: true,                   //是否显示分页（*）
                sortable: true,                     //是否启用排序
                sortOrder: "desc",                   //排序方式
                queryParams: that.queryParams,//传递参数（*）
                sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
                pageNumber:1,                       //初始化加载第一页，默认第一页
                pageSize: 15,                       //每页的记录行数（*）
                pageList: [5,10,15,20,50,100],        //可供选择的每页的行数（*）
                search: false,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                strictSearch: false,
                showColumns: false,                  //是否显示所有的列
                showRefresh: false,                  //是否显示刷新按钮
                minimumCountColumns: 2,             //最少允许的列数
                clickToSelect: true,                //是否启用点击选中行
                //height: $(window).height()-130,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                /*有多行查询条件，按下面的写法；如果只有单行查询条件，则把jsp页面中的查询条件div的class换成single-search ，下面的写法为 $(".frame-body").height() - $(".single-search").outerHeight() */
                //height: $(".frame-right").height() - $(".search-box").outerHeight(true)-35,      //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "id",                     //每一行的唯一标识，一般为主键列
                showToggle:false,                    //是否显示详细视图和列表视图的切换按钮
                cardView: false,                    //是否显示详细视图
                detailView: false,                   //是否显示父子表,
                columns: that.gridColumns
            });
        },

        queryParams : function (params) {
            var categoryName = $("#categoryName").val();
            var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
                sortName:this.sortName,//排序字段
                sortOrder:this.sortOrder,//排序方式
                pageSize: this.pageSize,   //页面大小
                pageNumber: this.pageNumber, //页码
                unitName : categoryName.toString().trim(),
            };
            return temp;
        },

        onlyinitTable:function() {
            var that = this;
            //初始化表格
            that.initTableView();
        },

        setting : {
            view: {
                showLine: false,
                showIcon: true,
                selectedMulti: false,
                dblClickExpand: false,
                addDiyDom: function(treeId, treeNode) {
                    var spaceWidth = 5;
                    var switchObj = $("#" + treeNode.tId + "_switch"),
                        icoObj = $("#" + treeNode.tId + "_ico");
                    switchObj.remove();
                    icoObj.before(switchObj);
                    if (treeNode.level > 1) {
                        var spaceStr = "<span style='display: inline-block;width:" + (spaceWidth * treeNode.level)+ "px'></span>";
                        switchObj.before(spaceStr);
                    }
                }
            },
            data: {
                simpleData: {
                    enable: true
                }
            },
            callback: {
                beforeClick: function(treeId, treeNode) {
                    var zTree = $.fn.zTree.getZTreeObj("treeDemo");
                    zTree.expandNode(treeNode);
                    return true;
                },
                onClick: function(event, treeId, treeNode) {
                    //点击树时，查询右侧列表
                    unitcode = treeNode.id;
                    unitname1 = treeNode.title;
                    unitBelongCode = treeNode.pId;
                    //$('#loadQueryTable').bootstrapTable('destroy');
                    //gridView.onlyinitTable();
                }
            }
        },

        initTree :function(){
            var self = this;
            var zNodes = "";
            //获取tree节点数据
            $.ajax({
                type: "post",
                url: "/category/loadCategoryTree",
                async : false,
                dataType: "json",
                success: function(data){
                    zNodes = data;
                }
            });
            $.fn.zTree.init(self.treeObj, self.setting, zNodes);
            zTree_Menu = $.fn.zTree.getZTreeObj("treeDemo");

            // 默认选中并展开第一个节点
            var nodesExpand = zTree_Menu.getNodes();
            if (nodesExpand.length>0) {
                zTree_Menu.selectNode(nodesExpand[0]); //默认选中第一个节点
                zTree_Menu.expandNode(nodesExpand[0], true);
                // zTree_Menu.expandNode(nodesExpand[0], true,true); //此方法可展开所有节点
            }

            self.treeObj.hover(function () {
                if (!self.treeObj.hasClass("showIcon")) {
                    self.treeObj.addClass("showIcon");
                }
            }, function() {
                self.treeObj.removeClass("showIcon");
            });
        },

        /**
         * 新增修改页面弹出树
         */
        treeSelectForm : function (){
            var that = this;
            infoWinTree = $("#menuContent").createLayerWindow({
                type: 1,
                title : "分类目录树",
                shadeClose: false,
                shade: true,
                maxmin: false, //开启最大化最小化按钮
                area: ['350px', '220px'],
                success:function(){
                    that.selectTreeInit();
                }
            });
        },

        selectTreeInit : function(){
            var zNodes = "";
            //获取tree节点数据
            $.ajax({
                type: "post",
                url: "/category/loadCategoryTree",
                async : false,
                dataType: "json",
                success: function(data){
                    zNodes = data;
                }
            });

            $.fn.zTree.init($("#selectTree"), selSetting, zNodes);
        }
    };
    var selSetting = {
        view: {
            dblClickExpand: false
        },
        data: {
            key:{
              name:"name"
            },
            simpleData: {
                pIdKey:'pId',
                enable: true
            }
        },
        callback: {
            onClick: onClick
        }
    };
    function onClick(e, treeId, treeNode) {
        var zTree = $.fn.zTree.getZTreeObj("selectTree"),
            nodes = zTree.getSelectedNodes(),
            v = "";
        nodes.sort(function compare(a,b){return a.id-b.id;});
        for (var i=0, l=nodes.length; i<l; i++) {
            v += nodes[i].name + ",";
        }
        if (v.length > 0 ) v = v.substring(0, v.length-1);
        $("#categoryBelongName").val(v);
        $("#categoryBelong").val(nodes[0].id);
        layer.close(infoWinTree);
    }
})(this);