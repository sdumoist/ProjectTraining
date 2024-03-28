layui.use(['jquery', 'layer', 'laytpl', 'Hussar','form'], function () {
    var $ = layui.jquery,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        $ax = layui.HussarAjax,
        hussar = layui.Hussar;
    var form = layui.form;

    /*搜索按钮*/
    $("#screening").on('click', function () {
        var index = layer.load(1, {
            shade: [0.1, '#fff'] //0.1透明度的白色背景
        });
        refreshEntry(1, 20);
        layer.close(index);
    });

    // 搜索框回车搜索
    $('#name').bind('keypress', function (event) {
        if (event.keyCode == "13") {
            $("#screening").click();
            return false;
        }
    });

    //页面初始化
    $(function () {
        refreshEntry(1, 20);
    });

})

//新增词条
function addEntry() {
    location.href = '/entry/entryAdd';
}

//编辑词条
function editEntry(id) {
    location.href = '/entry/entryEdit/' + id;
}

// 词条预览
function toEntryShoe(id) {
    console.log("词条预览id",id)
    window.open(Hussar.ctxPath + "/entry/entryPreview?id="+id);
}

// 查询词条列表
function refreshEntry(num, limit) {
    layui.use(['laypage', 'layer', 'table', 'Hussar', 'HussarAjax'], function () {
        var laypage = layui.laypage;
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        var name = $("#name").val();
        var status = $("#status").val();

        var ajax = new $ax(Hussar.ctxPath + "/entry/searchEntryInfoList", function (data) {
            laypage.render({
                elem: 'laypageAre'
                , count: data.total //数据总数，从服务端得到
                , limit: limit
                , layout: ['prev', 'page', 'next']
                , curr: num || 1 //当前页
                , jump: function (obj, first) {
                    //obj包含了当前分页的所有参数，比如：
                    // obj.curr得到当前页，以便向服务端请求对应页的数据。
                    // obj.limit得到每页显示的条数
                    //首次不执行
                    if (!first) {
                        refreshEntry(obj.curr, obj.limit)
                    }
                }
            });
            $("#amount").html("已全部加载" + data.total + "个");
            drawFile(data.rows);

        }, function (data) {

        });

        ajax.set("name", name);
        ajax.set("status", status);
        ajax.set("pageNumber", num);
        ajax.set("pageSize", limit);
        ajax.start();
    });
}

// 渲染页面
function drawFile(param) {
    layui.use('laytpl', function () {
        var laytpl = layui.laytpl;
        var data = { //数据
            "list": param,
        };
        var getTpl = $("#demo1").html()
            , view = document.getElementById('view1');
        laytpl(getTpl).render(data, function (html) {
            view.innerHTML = html;
            var inner = $("#view1");
            if (param.length == 0) {
                setTimeout(function () {
                    $("div.noDataTip").show();
                }, 200);
            } else {
                $("div.noDataTip").hide();
            }
        });
    });
}

// 删除词条
function deleteEntry(id) {
    layui.use(['laypage', 'layer', 'Hussar', 'HussarAjax'], function () {
        var laypage = layui.laypage;
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        layer.confirm('确定要删除吗？', {title: ['提示', 'background-color:#fff'], skin: 'move-confirm'}, function () {
            var ajax = new $ax(Hussar.ctxPath + "/entry/deleteEntryInfo", function (data) {
                if (data.code == 1) {
                    refreshEntry("1", "20")
                    Hussar.success(data.msg);
                } else {
                    Hussar.error(data.msg);
                }
            }, function (data) {
                Hussar.error("删除出错");
            });
            ajax.set("id", id);
            ajax.start();
        })
    })
}

