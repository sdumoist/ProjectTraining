
var ininer = ''

$("#s_company_info").text('Copyright ©2018-2019 金现代  鲁ICP备09033671  电话：0531-88872666')
$('.hot>.title').text('热搜词条')
$('.btn-rit>a').text('词条录入')
var entryadd="<a href='/personalcenter?menu=9' onclick='entryInput()'>词条录入</a>"
$('.btn-rit').html(entryadd)


layui.use(['form', 'Hussar', 'jquery', 'HussarAjax','laypage'], function () {
    var $ = layui.jquery;
    var form = layui.form;
    var Hussar = layui.Hussar;
    var $ax = layui.HussarAjax;
    var laypage = layui.laypage;

    $(function () {
        var filename = $('#headerSearchInput').val()
        var tagstring = ''

        getEntryList(1,filename)
        if (filename != '') {
            $('#searchName').text(filename)
        }
        getImglabel()         // 获得标签
        getHotEntry()         //获得热搜词条


        layui.use(['form'], function () {
            var form = layui.form;
            $('select[name="selectModule"]').next().find('.layui-select-title input').attr("onFocus","this.blur()");
        });
        // 获得全部词条
        function getEntryList(page,filename, order = 2, tagstring = '') {
            var ajax = new $ax(Hussar.ctxPath + "/searchEntry", function (data) {
                console.log('全部词条', data)
                var json = eval(data);
                var list = json.items;
                count = json.total;

                if (json.total == 0) {
                    $("#searchNumber").text(count);
                    $(".text-center").hide();
                    $("#searchINfo").html("");
                    $(".noDataTip").css('display','block')
                } else {
                    $(".noDataTip").css('display','none')
                    $(".text-center").show();
                    $("#searchINfo").html("");
                    $("#searchNumber").text(count);
                    layui.each(json.items, function (index, item) {
                        // item.imgUrl=item.imgUrl.replace(/\\/g,'/')
                        let itemTags=item.tag.replace(/,/g,'&nbsp&nbsp&nbsp&nbsp')
                        var itemImgUrl=''
                        if (item.imgUrl == undefined) {
                            itemImgUrl = Hussar.ctxPath + '/static/doc/front/entry/img/defaultse.png'
                        }else {
                            itemImgUrl='/preview/list?fileId='+item.imgUrl.replace(/\\/g,'/')
                        }
                        var param = '<div class="entryMoreMain">' +
                            '<div class="entryMoreMainTitle" onclick="showDoc(\'' + item.id + '\',\'' + 'entry' + '\')">' + item.name + '</div>' +
                            '<div class="entryMoreText">' +
                            '<img src="' + itemImgUrl + '" alt="" class="entryMoreImg" onerror="toFind();">' +
                            '<div class="entryMoreTextRight">' +
                            '<div>' + item.summaryText +
                            '</div>' +
                            '<div><span>' + itemTags + '</span></div>' +
                            '</div>' +
                            '</div>' +
                            '</div>'
                        $("#searchINfo").append(param);
                    });
                }
                var count = $("#searchNumber").html();
                laypage.render({
                    elem: 'laypageAre'
                    , count: count
                    , limit: 10
                    , layout: ['prev', 'page', 'next']
                    , curr:  page||1
                    , jump: function (obj, first) {
                        //首次不执行
                        if (!first) {
                            var name = $("#headerSearchInput").val();
                            // var type =    $("input[type=radio][name='fileType']:checked").val();
                            // var type = $("#fileTypeValue").val(); //文档类型
                            //
                            // if (type == '0') {
                            //     type = $(" #all input[type=radio][name='fileType']:checked").val();
                            // } else if (type == '7') {
                            //     type = $(" #word input[type=radio][name='fileType']:checked").val();
                            // } else {
                            //
                            // }
                            // }
                            getEntryList(obj.curr,name)

                        }
                    }
                });


                }, function (data) {

            });

            if(filename==''){
                ajax.set("tagString", tagstring);
                ajax.set("fileType", '14');
                ajax.set("page", page);
                ajax.set("size", "10");
                ajax.set("order", order);
                ajax.start();
            }else {

                ajax.set("tagString", tagstring);
                ajax.set("fileType", '14');
                ajax.set("keyword", filename);
                ajax.set("page", page);
                ajax.set("size", "10");
                ajax.set("order", order);
                ajax.start();
            }

        }

        // 获得标签
        function getImglabel() {
            var ajax = new $ax(Hussar.ctxPath + "/dicList", function (result) {
                console.log(result, 'resuit')
                $(".layui-input-block").append(' <input type="checkbox" name="industry_type" lay-filter="industry_type" title="全部" lay-skin="primary" checked   >');
                for (var i = 0; i < result.length; i++) {
                    var param = ' <input type="checkbox" name="industry_type"  lay-filter="industry_type" title="' + result[i].LABEL + '" lay-skin="primary" value="' + result[i].LABEL + '">'
                    $(".layui-input-block").append(param);
                }
                // $(".layui-input-block").append('<button type="button" class="layui-btn">一个标准的按钮</button>');
            }, function (data) {
            });
            ajax.set("dicType","tag_entry");
            ajax.start();
            form.render()
        }
        // 获得热搜词条
        function getHotEntry() {
            var ajax = new $ax(Hussar.ctxPath + "/getHotEntry", function (result) {
                console.log(result, 'resultresult', '获得热搜词条')
                for (let i = 1; i <= 6; i++) {
                    ininer += "<div " + "class=\"hot-detail hot-detail-" + (i) + "\" title='" + result[i].name + "' onclick=\"showDoc(\'" + result[i].id + "\',\'" + 'entry' + "\')\">" + result[i].name + "</div>"


                }

                $('.hot-total').html(ininer)
            }, function (data) {
            });
            ajax.setAsync(true);
            ajax.start();
        }

        //获得标签内容
        getLabel()

        function getLabel() {
            layui.use('form', function () {
                var form = layui.form;
                form.on('checkbox(industry_type)', function (data) {
                    var name = $("#headerSearchInputValue").val();
                    var type = "8";
                    //如果在文档搜索中查询全部 ，仍然是选择 文档全部

                    if (data.value == 'on') {
                        if ($("input[name='industry_type']").eq(0).prop("checked") == true) {
                            $("input[name='industry_type']").each(function (index) {
                                if (index > 0) {
                                    $(this).prop("checked", false)
                                }
                            })
                        } else {

                        }
                    } else {
                        $("input[name='industry_type']:checked").each(function (index) {
                            if (index > 0) {
                                $("input[name='industry_type']").eq(0).prop("checked", false);
                            }
                        })
                    }
                    form.render();
                    $("#entryorderPic").css('display','none')
                    $("#entryorderPic1").css('display','none')
                    var formChecked = []
                    for (let i = 0; i < $('.layui-form-checked').length; i++) {
                        formChecked.push($('.layui-form-checked')[i].previousSibling.title)
                    }
                    let formCheckedString=formChecked.toString()
                    if(formCheckedString=='全部'){
                        formCheckedString='';
                    }
                    getEntryList(1,filename,2,formCheckedString)// 获得全部词条
                })
            })

        }

        entryOrder(filename)
        //词条点击
        function entryOrder(filename){

            $('#entryOrderPicByTime').on('click', function () {
                var formChecked = []
                for (let i = 0; i < $('.layui-form-checked').length; i++) {
                    formChecked.push($('.layui-form-checked')[i].previousSibling.title)
                }
                tagstring=formChecked.toString()

                if(tagstring=='全部'){
                    tagstring=''
                }
                console.log(tagstring,'tagstring')
                if ($("#entryorderPic").css("display") != "none") {
                    $("#entryorderPic").hide();
                    $("#entryorderPic1").show();
                    getEntryList(1,filename, 2,tagstring)
                } else {
                    $("#entryorderPic").show();
                    $("#entryorderPic1").hide();
                    getEntryList(1,filename, 3,tagstring)
                }
            });

        }


    });

})


function showDoc(id) {
    openWin(Hussar.ctxPath + "/entry/entryPreview?id=" + id);
}

function openWin(url) {
    var a = document.createElement("a"); //创建a标签
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    document.body.appendChild(a);
    a.click(); //执行当前对象
}
function entryInput(){
    sessionStorage.setItem("from","pageA");
}
function toFind(){
    var img=event.target;
    img.src="/static/doc/front/entry/img/default.png";
    img.οnerrοr=null;
}


