layui.use(['jquery', 'layer', 'laytpl', 'Hussar'], function () {
    var $ = layui.jquery,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        $ax = layui.HussarAjax,
        hussar = layui.Hussar;

    //页面初始化
    $(function () {
        document.title = $("#name").val()+"-"+projectTitle;
        // 加载词条详情
        var id = $("#entryId").val();
        console.log("词条的id",id)
        var ajax = new $ax(Hussar.ctxPath + "/entry/entryDetail", function (data) {
            console.log(data,'词条详情')
            var getTpl = $("#demo1").html()
                , view = document.getElementById('view1');
            var tag = $("#tag").val();
            data.tag = tag;
            laytpl(getTpl).render(data, function (html) {
                debugger
                console.log("这是获取的正文信息",html)
                view.innerHTML = html;
            });
        }, function (data) {

        });
        ajax.setAsync(true);
        ajax.set("id", id);
        ajax.start();
        var ajax = new $ax(Hussar.ctxPath + "/entry/getEntryImgs", function (data) {
            console.log('图册信息', data,data.length)
            var imgimg=''
            var img=''
            for (let i=0;i<data.length;i++){
                imgimg+='<img  src="/preview/list?fileId='+data[i].imgUrl+'">'
                img='/preview/list?fileId='+data[0].imgUrl+''
            }
            // var imgimg='<img src="/static/doc/front/entry/img/technology.jpeg" alt="">'
            $('#img').html(imgimg)
            $('.atlaNumber').html(data.length)
            if(data.length>0){
                $('#entryImg').attr('src',img)
                $("#tuce").css('display',"block")
            }
            console.log('长度', data.length)
        }, function (data) {

        });
        ajax.set("id", id);
        ajax.start();
        var viewer = new Viewer(document.getElementById('img'));
    });

    // 加载相关推荐
    var name = $("#name").val();
    var entryId = $("#entryId").val();
    var ajax = new $ax(Hussar.ctxPath + "/preview/recommendEntry", function (data) {

        if(data!=null && data.length>0){
            var ininer='';
            for(var i=0; i<data.length;i++){
                var entry = data[i];
                ininer+= '<div class="hot-detail hot-detail-'+(i+1)+'" title="'+entry.name+'" onclick="toEntryShoe(\''+entry.id+'\')">'+entry.name+'</div>'
            }
            $('.hot-total').html(ininer)
        }
    }, function (data) {

    });
    ajax.setAsync(true);
    ajax.set("currentId", entryId);
    ajax.set("keyword", name);
    ajax.set("pageNumber", 1);
    ajax.set("pageSize", 6);
    ajax.start();

    // 预览次数
    var ajax = new $ax(Hussar.ctxPath + "/entry/updateReadNum", function (data) {
    }, function (data) {

    });
    ajax.setAsync(true);
    ajax.set("id", $("#entryId").val());
    ajax.start();


})



// 词条预览
function toEntryShoe(id){
    window.open(Hussar.ctxPath + "/entry/entryPreview?id="+id);
}

function getEntryImgs(){
    layui.use(['laypage', 'layer', 'Hussar', 'HussarAjax'], function () {
        var laypage = layui.laypage;
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var id = $("#entryId").val();

    })
}
//加载发生错误时替换图片
function toFind(){
    var img=event.target;
    img.src="/static/doc/front/entry/img/default.png";
    img.οnerrοr=null;
}
