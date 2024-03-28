var personTreeData = [];
var groupTreeData = [];
var choose = [];
var personInit = [];
var groupInit = [];
var editFlag;
var openPersonId = [];
var openGroupId = [];
var operateTypeValue;
var personParamPower = [];
var groupParamPower = [];
var groupId = [];
var personId = [];
var chooseDeptId = "";
var chooseDeptAttrId = "";
var chooseDeptName = "";
var scrollHeightAlert=0;
var scrollHeightLong=0;
layui.use(['layer','Hussar', 'HussarAjax','form','table','jquery','element','jstree'], function(){
    var layer = layui.layer
        ,table = layui.table
        ,element = layui.element
        ,Hussar = layui.Hussar
        ,$ = layui.jquery
        ,$ax = layui.HussarAjax,
        jstree=layui.jstree,
        form = layui.form;

    $(function () {
        initLeftTree();   //初始化左侧树
        form.render();
        /*取消按钮*/
        $("#cancelBtn").on('click',function(){
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        });

        /*保存按钮*/
        $("#saveBtn").on('click',function(){
            if ($("#type").val()=="consulation"){
                $.ajax({
                    type: "post",
                    url: Hussar.ctxPath+"/consulationManager/updateDeptVisibleRange",
                    data: {
                        organId: chooseDeptId,
                        VisibleRange:"0"
                    },
                    contentType:"application/x-www-form-urlencoded",
                    async: false,
                    cache: false,
                    success: function (data) {
                        parent.tableIns.reload({
                            where: {
                                //防止IE浏览器第一次请求后从缓存读取数据
                                timestamp: (new Date()).valueOf()
                            }
                        });
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                    }
                })
            }else {
                parent.document.getElementById("deptId").value = chooseDeptId;
                parent.document.getElementById("deptName").value = chooseDeptName;
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
            }

        });

    //初始化左侧树
    function initLeftTree() {
        getRenData();
    }
    /*初始化的选中列表（区分是新增的还是编辑的）*/
    function initRitTable() {
        initAuthorityData();
        //判断修改还是新增
        personInit = personId;
        groupInit = groupId;
        if((personInit!=undefined&&personInit.length>0)||(groupInit!=undefined&&groupInit.length>0)){
            initChooseTable([]);
            table.reload('rit_table',{
                data : personInit.concat(groupInit)
            });
            choose = personInit.concat(groupInit);
        }else {
            initChooseTable([]);
        }
    }

    // 获取人员树数据
    function getRenData() {
        var ajax = new $ax(Hussar.ctxPath + "/middlegroundConsulation/deptTreeList",function(result) {
            personTreeData = result;
        }, function(data) {
            Hussar.error("获取部门失败");
        });
        ajax.set("treeType", 2);
        ajax.start();
        initEmployeeTree(personTreeData);
    }

    // 加载人员树
    function initEmployeeTree(data){
        var $tree = $("#renTree");
        $tree.jstree({
            core: {
                data: data
            },
            plugins: ['types','search'],
            types:{
                "isRoot":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/root.png"},
                "2":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/dept.png"},
                "3":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/station.png"},
                "4":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/deptOld.png"},
                "5":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/deptOld.png"},
                "9":{'icon' : Hussar.ctxPath+"/static/assets/img/treeContext/blue/user.png"},
            },
            search:treeSearch("renTree","personSearch")
        });
        $tree.bind('activate_node.jstree', function (obj,e){
            var ids;
            var type;
            var name;
            if(e.node.original.type != '2'&&e.node.original.type != '4'){
                layer.msg("请选择部门", {anim: 6, icon: 0});
                return;
            }

            if(e.node.original.type == "2"||e.node.original.type == "4"){
                type = 2;
                ids=e.node.original.id;
                name = e.node.original.text;
            }
            var id = "#"+e.node.a_attr.id;             //点击的节点的元素
            var oldId = "";
            if (chooseDeptAttrId != ""){
                oldId = "#"+chooseDeptAttrId;
            }

            if(e.node.original.text == '金现代公司' || e.node.original.text == '第一事业部群' || e.node.original.text == '第二事业部群' || e.node.original.text == '第三事业部群'|| e.node.original.text == '第四事业部群' || e.node.original.text == '第五事业部群'||e.node.original.text == '第六事业部群'){
                layer.msg("请选择部门", {anim: 6, icon: 0});
                return;
            }
            if($(id).parent().children("span.treeSpanActive").length>0){   //点击的节点后面有没有对号，判断点击节点的选中状态
                $(id).parent().children("span.treeSpanActive").remove();      //后面有对号的删除对号
                var id = e.node.original.id;
                var oId= e.node.original.organId;

                var id = "#"+e.node.a_attr.id;
                chooseDeptId = "";
                chooseDeptName = "";
            }else {
                $(id).parent().append("<span style='margin-right: 50px' class='treeSpanActive'></span>")     //没有对号的加上对号
                // if (chooseDeptId != ""){
                //     $(oldId).parent().children("span.treeSpanActive").remove();
                // }
                if (chooseDeptName==""){
                    chooseDeptId = ids;   //保存一下处于选中状态的数据
                    chooseDeptName = name;
                    chooseDeptAttrId = e.node.a_attr.id;
                }else {
                    chooseDeptId = chooseDeptId + "," +ids;   //保存一下处于选中状态的数据
                    chooseDeptName = chooseDeptName + "," +name;
                    chooseDeptAttrId = chooseDeptAttrId + "," +e.node.a_attr.id;
                }

            }
        })
        $tree.bind("open_node.jstree", function (e,data) {
            for(var i = 0;i<openPersonId.length;i++){
                var id = openPersonId[i];
                $(id).parent().append("<span class='treeSpanActive'></span>")
            }
        });
        $tree.bind("ready.jstree", function (e,data) {
            var deptId = parent.document.getElementById("deptId").val();
            $(deptId).parent().append("<span class='treeSpanActive'></span>")
            chooseDeptId = deptId;
        });
    }
    /*
     * 人员树的模糊查询
     */
    function treeSearch(treeId,searchId) {
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
                if(v==null){
                    v ='';
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

    /** 初始化数据 */
    function initAuthorityData(){
        if(parent.chooseFile.length == 1){
            var folderId = parent.chooseFile[0];
            $.ajax({
                type:"post",
                url: Hussar.ctxPath+"/fsFolder/getAuthority",
                data:{
                    folderId:folderId,
                },
                async:false,
                cache:false,
                dataType:"json",
                contentType : "application/x-www-form-urlencoded;charset=UTF-8",
                success:function(data){

                }
            });
        }
    }
    });
});

$(function(){


    setInterval(function () {
        scrollHeight=parent.scrollHeight;
        if( scrollHeight!=0){
            scrollHeightAlert=parseInt(scrollHeight.substring(0,scrollHeight.indexOf("px")))+150+"px";
            scrollHeightLong=parseInt(scrollHeight.substring(0,scrollHeight.indexOf("px")))-45+"px";

        }

    },300)
})
/*数组删除某一项调用的方法*/
Array.prototype.del=function(n) {
    if(n<0)//如果n<0，则不进行任何操作。
        return this;
    else
        return this.slice(0,n).concat(this.slice(n+1,this.length));
}