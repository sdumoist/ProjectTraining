
// 所有的评论的追问追答   暂时没有，如果被恢复信心ID 需要确定到某一条追问追答获取要用
var allContinueQaList = {};

var index = null;
var onAll = '1';
layui.use(['layedit','layer','Hussar','HussarAjax','form'], function() {
    var layedit = layui.layedit
        ,layer = layui.layer
        , $ = layui.jquery,
        Hussar = layui.Hussar
        ,form = layui.form
        ,$ax = layui.HussarAjax;



    //点击我来答
    $("#response").on('click', function() {
        if( $("#uEditor").css("display")=='none' ){
            $("#uEditor").show();
        }else{
            $("#uEditor").hide();
        }
    })
    //点击提交回答
    $("#submitAnswers").on('click', function() {
        var queId = $("#queId").val();
        var ansContent = layedit.getContent(index); //获得编辑器的内容
        var ansContentText = layedit.getText(index); //获得编辑器的纯文本内容
        if (ansContent == '' || ansContent == null || ansContent == undefined){
            layer.msg('回答内容不能为空');
            return false;
        }
       /* var reg = new RegExp("src","g");//g,表示全部替换。
        ansContent = ansContent.replace(reg,"sr1c");*/
        var ajax = new $ax(Hussar.ctxPath + "/answer/add", function(data) {
            var allLen = $(".part").find("span").html().split('个回答')[0];
            var c = parseInt(allLen) + 1;
            if(c > 10){
                if($(".allOrOne").length <= 0){
                    var html = "<div class=\"allOrOne\">\n" +
                        "                <span onclick=\"openAll()\">展开所有</span>\n" +
                        "            </div>";
                    $("#view1").after(html);
                }
            }
            c = c+ '个回答';
            $(".part").find("span").html(c);
            // 清空富文本框数据
            $("#response").click();
            $(".layui-layedit").remove()
            index = layedit.build('LAY_demo1');
            Hussar.success("回答成功");
            form.render();
            refreshFile();
        }, function(data) {

        });
        ajax.set("queId",queId);
        ajax.set("ansContent",ansContent);
        ajax.set("ansContentText",ansContentText);
        ajax.start();
    })

    // 结束问题
    $("#endProblem").on('click',function () {
        var queId = $("#queId").val();
        var operation = function () {
            var ajax = new $ax(Hussar.ctxPath + "/question/endQuestion", function(data) {
                if(data == 'success'){
                    Hussar.success("结束成功");
                    refreshFile();
                }
            }, function(data) {

            });
            ajax.set("queId",queId);
            ajax.start();
        };
        Hussar.confirm("确定要结束问题吗？", operation);
    })

    //评论框字数显示及控制
    window.wordLeg = function (obj) {
        var currleg = $(obj).val().length;
        var btnObj = $(obj).parent().parent('.comment-input-container').children('.submit-comment-btn');
        var preObj = $(obj).parent().children('pre');
        if(currleg === 0){
            btnObj.attr('disabled',true);
        }else{
            btnObj.attr('disabled',false);
        }
        var length = $(obj).attr('maxlength');
        if (currleg > length) {
            layer.msg('字数请在' + length + '字以内');
        } else {
            $(obj).parent().children('.word').children('.text_count').text(currleg);
        }
        preObj[0].innerHTML = $(obj)[0].value;
    }

    // 输入框焦点
    window.textareaFoucs = function (obj) {
        var btnObj = $(obj).parent().parent('.comment-input-container').children('.submit-comment-btn');
        $(obj).css('width','980px');
        setTimeout(function () {
            btnObj.show();
        },200)
    }
    window.textareaFoucs1 = function (obj) {
        var btnObj = $(obj).parent().parent('.comment-input-container').children('.submit-comment-btn');
        $(obj).css('width','960px');
        $(obj).parent().children('pre').css('width','960px');
        setTimeout(function () {
            btnObj.show();
        },200)
    }
    // 失去焦点
    window.textareaBlur = function (obj) {
        var currleg = $(obj).val().length;
        if(currleg===0){
            var btnObj = $(obj).parent().parent('.comment-input-container').children('.submit-comment-btn');
            $(obj).css('width','1055px');
            btnObj.hide();
        }
    }


    //页面初始化
    $(function () {
        refreshFile();
        // 定义上传图片接口
        layedit.set({
            uploadImage: {
                url: Hussar.ctxPath+'/qaFile/uploadPic' //接口url

            }
        });
        //构建一个默认的编辑器
        index = layedit.build('LAY_demo1');
    });
})



function refreshFile(){

    layui.use(['laypage','layer','table','Hussar','HussarAjax'], function(){
        var laypage = layui.laypage;
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var queId = $("#queId").val();

        var ajax = new $ax(Hussar.ctxPath + "/question/getQuestionDetail", function(data) {
            // 保存追问追答数据
            var answer = data.answer;
            for(var i = 0;i<answer.length;i++){

                var continueQa = answer[i].continueQa;
                var cqaIdArr = [];
                for(var j =0;j<continueQa.length;j++){
                    var cqaId = continueQa[j].id;
                    cqaIdArr.push(cqaId);
                }
                allContinueQaList[answer[i].ansId] = cqaIdArr;
            }
            drawFile(data.answer);
        }, function(data) {

        });

        ajax.set("queId",queId);
        ajax.set("onAll",onAll);
        ajax.start();
    });
}

function drawFile(param) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
        };
        var getTpl = $("#demo1").html()
            ,view = document.getElementById('view1');
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#view1");
            if (param.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                },200);
            }else {
                $("div.noDataTip").hide();
            }
        });
    });
}
// 点赞评论
function giveALike(ele) {

    var ansId= $(ele).attr("value");

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var queId = $("#queId").val();
        var ajax = new $ax(Hussar.ctxPath + "/answer/agree", function(data) {
            refreshFile();
        });
        ajax.set("ansId",ansId);
        ajax.start();
    });

}
// 点赞回复
function giveALikeCommentPeply(ele) {
    var commentPeplyId= $(ele).attr("value");
    var isAgree = $(ele).attr("isAgree");
    var agreeNum = $(ele).children('span').text();

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var queId = $("#queId").val();
        var ajax = new $ax(Hussar.ctxPath + "/comment/agree", function(data) {
            if (isAgree == '1') {
                agreeNum--
                $(ele).css("color", "#7A8F9A");
                $(ele).children('span').text(agreeNum);
                $(ele).attr("isAgree", '0');
            } else {
                agreeNum++
                $(ele).css("color", "#FFCB3E");
                $(ele).children('span').html(agreeNum);
                $(ele).attr("isAgree", '1');
            }
        });
        ajax.set("commentPeplyId",commentPeplyId);
        ajax.set("isAgree", isAgree);
        ajax.start();
    });
}
// 点击展开全部
function onAllContent(ele) {
    var resObj = $(ele).parent().children('.answer-desc');
    resObj.removeClass('max-height')
    $(ele).hide();
}

// 点击评论
function myComments(ele) {
    var queId = $("#queId").val();
    var ctxPathStr = $("#ctxPathStr").val();
    var itemObj= $(ele).parent().parent().parent('.answer-item');
    var commentObj = itemObj.children('.comment-area');
    var objItem2 = $(this).parent().parent().parent().children('.comment-input-container2');
    if(objItem2.css("display")!='none' ){
        objItem2.hide();
    }
    if(commentObj.length > 0){
        if( commentObj.css("display")=='none' ){
            commentObj.css("display",'block');
        }else{
            commentObj.css("display",'none');
        }
    }else{
        var byReplyId= $(ele).attr("value");
        var ansId= $(ele).attr("ansId");
        var res = null;
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;
            var ajax = new $ax(Hussar.ctxPath + "/comment/getCommentDetail", function(data) {
                res = data;
            });
            ajax.set("byReplyId",ansId);
            ajax.start();
        });
        var html = '';
        if(res != null && res.length >0){ // 有子评论

            html = '<div class="comment-area">\n' +
                '                        <div class="comment-list-area" id="'+ansId+'">\n' +
                '                        </div>\n' +
                '                    </div>';
        } else {
            html = '';
        }
        itemObj.append(html);
        if(res != null && res.length >0){
            drawAnswer(res,ansId);
        }
    }
}
// 加载评论
function drawAnswer(param,divId) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
        };
        var getTpl = $("#demo2").html()
            ,view = document.getElementById(divId);
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#"+divId);
            if (param.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                },200);
            }else {
                $("div.noDataTip").hide();
            }
        });
    });
}
// 回复评论
function published(ele) {

    var queId = $(ele).attr("queId");
    var byReplyId= $(ele).attr("byReplyId");
    var ansId= $(ele).attr("ansId");

    var replyContent = $(ele).siblings().eq(1).children('textarea').val().trim();
    if (replyContent == '' || replyContent == null || replyContent == undefined){
        layer.msg('回答内容不能为空');
        return false;
    }
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/comment/add", function(data) {
            if(data == 'ansDelete'){
                Hussar.info("评论已被删除");
            } else {
                refreshFile();
            }
        });
        ajax.set("queId",queId);
        ajax.set("ansId",ansId);
        ajax.set("byReplyId",byReplyId);
        ajax.set("replyContent",replyContent);
        ajax.start();
    });
}

// 回复评论
function replyComment(ele) {
    var queId = $("#queId").val();
    var ctxPathStr = $("#ctxPathStr").val();

    var objItem = $(ele).parent().parent().parent().children('.comment-input-container1');
    if(objItem.length>0){
        if( objItem.css("display")=='none' ){
            objItem.show();
        }else{
            objItem.hide();
        }
    }else{
        var byReplyId= $(ele).attr("value");
        var ansId= $(ele).attr("ansId");
        var res = null;
        layui.use(['Hussar','HussarAjax'], function(){
            var Hussar = layui.Hussar,
                $ax = layui.HussarAjax;
            var ajax = new $ax(Hussar.ctxPath + "/comment/getCommentDetail", function(data) {
                res = data;
            });
            ajax.set("byReplyId",byReplyId);
            ajax.start();
        });
        var html = '<div class="comment-input-container comment-input-container1">\n' +
            '                                </div>';

        if(res != null && res.length >0) { // 有子回复
            html = html +
                '                        <div id="'+byReplyId+'">\n' +
                '                        </div>';
            $(ele).parent().parent().parent().children(".comment-item-content").after(html);
            drawComment(res,byReplyId);
        } else {
            $(ele).parent().parent().parent().children(".comment-item-content").after(html)

        }
    }
}

// 加载回复
function drawComment(param,divId) {
    layui.use('laytpl', function(){
        var laytpl = layui.laytpl;
        var data = { //数据
            "list":param,
        };
        var getTpl = $("#demo3").html()
            ,view = document.getElementById(divId);
        laytpl(getTpl).render(data, function(html){
            view.innerHTML = html;
            var inner = $("#"+divId);
            if (param.length == 0){
                setTimeout(function () {
                    $("div.noDataTip").show();
                },200);
            }else {
                $("div.noDataTip").hide();
            }
        });
    });
}
// 回复回复  和 回复评论基本一级 只有取值的时候的是0和1 的区别
// 因为 评论taxtarea框前有一个头像图片 所以 评论的0是图片
function published2(ele) {

    var queId = $(ele).attr("queId");
    var byReplyId= $(ele).attr("byReplyId");
    var ansId= $(ele).attr("ansId");

    var replyContent = $(ele).siblings().eq(0).children('textarea').val().trim();
    if (replyContent == '' || replyContent == null || replyContent == undefined){
        layer.msg('回答内容不能为空');
        return false;
    }
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/comment/add", function(data) {
            if(data == 'ansDelete'){
                Hussar.info("评论已被删除");
            } else {
                refreshFile();
            }
        });
        ajax.set("queId",queId);
        ajax.set("ansId",ansId);
        ajax.set("byReplyId",byReplyId);
        ajax.set("replyContent",replyContent);
        ajax.start();
    });
}

// 追问
function ask(ele) {

    var ansId = $(ele).attr("value");
    var ansUserId = $(ele).attr("ansUserId"); // 评论人ID
    var userId = $("#userId").val(); // 当前登陆人ID
    var queUserId = $("#queUserId").val(); // 问题提问人ID
    var type = '';
    if(userId != ansUserId && userId != queUserId){
        Hussar.info("当前用户不能操作此评论");
        return false;
    } else if(userId == ansUserId){
        type = '2';
    } else if(userId == queUserId){
        type = '1';
    }


    var ctxPathStr = $("#ctxPathStr").val();
    var objItem = $(ele).parent().parent().parent().children('.comment-input-container2');
    var objItem2 = $(ele).parent().parent().parent().children('.comment-area');
    if(objItem2.css("display")!='none' ){
        objItem2.hide();
    }
    if(objItem.length>0){
        if( objItem.css("display")=='none' ){
            objItem.show();
        }else{
            objItem.hide();
        }
    }else{
        var html = '<div class="comment-input-container comment-input-container2">\n' +
            '                            <!--用户头像或者是默认头像-->\n' +
            '                            <img src='+ctxPathStr+'"/static/resources/img/front/index/photo.png" />\n' +
            '                            <div class="textarea-container">\n' +
            '                                <pre></pre><textarea placeholder="请输入" onfocus="textareaFoucs(this)" onblur="textareaBlur(this)" oninput="wordLeg(this);" maxlength="200" onpropertychange="if(value.length>200) value=value.substr(0,200)" name="desc" class="layui-textarea"></textarea>\n' +
            '                                <div class="word"><span class="text_count">0</span>&nbsp;/&nbsp;<span class="num_count">200</span></div>\n' +
            '                            </div>\n' +
            '                            <button class="submit-comment-btn" ansId="'+ansId+'" onclick="askOrChased(this,'+type+')" disabled style="display: none">提交</button>\n' +
            '                        </div>';
        $(ele).parent().parent().after(html)
    }
}

// 追问追答
function askOrChased(ele,type) {

    var queId = $(ele).attr("queId");
    var ansId = $(ele).attr("ansId");

    var conQaList = [];
    for(var prop in allContinueQaList){
        if(allContinueQaList.hasOwnProperty(prop) && ansId == prop){
            conQaList = allContinueQaList[prop];
        }
    }

    var byReplyId = ansId;
    if(conQaList.length > 0){
        byReplyId = conQaList[conQaList.length-1]
    } else {
        if(type == '2'){
            Hussar.info("当前用户不能追答此评论");
            return false;
        }
    }


    var replyContent = $(ele).siblings().eq(1).children('textarea').val().trim();
    if (replyContent == '' || replyContent == null || replyContent == undefined){
        layer.msg('回答内容不能为空');
        return false;
    }
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/continueQa/add", function(data) {
            if(data == 'hasBast'){
                Hussar.info("已有最佳答案");
            } else {
                refreshFile();
            }
        });
        ajax.set("queId",queId);
        ajax.set("ansId",ansId);
        ajax.set("type",type);
        ajax.set("byReplyId",byReplyId);
        ajax.set("content",replyContent);

        ajax.start();
    });

}

// 设为最佳答案
function bestAnswer(ele) {

    var ansId = $(ele).attr("value");
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/answer/setBestAnswer", function(data) {
            if(data == 'hasBast'){
                Hussar.info("已有最佳答案");
            } else {
                refreshFile();
            }
        });
        ajax.set("ansId",ansId);
        ajax.start();
    });
}

// 删除回答
function delAnswer(ele) {

    var ansId = $(ele).attr("value");
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/answer/delete", function(data) {
            if(data == 'success'){
                refreshFile();
                Hussar.success("删除成功");
            }
        });
        ajax.set("ansId",ansId);
        ajax.start();
    });
}

// 删除 评论和回复  理论上 评论和回复是同一张表的数据， 差不多算是同样一等级
function delComment(ele) {
    var commentPeplyId = $(ele).attr("value");
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/comment/delete", function(data) {
            if(data == 'success'){
                refreshFile();
                Hussar.success("删除成功");
            } else if(data == 'hasChild') { //已有子回复
                Hussar.info("不能删除有回复的数据");
            }
        });
        ajax.set("commentPeplyId",commentPeplyId);
        ajax.start();
    });
}

// 关注/收藏问题
function focusOnQue(ele) {
    var queId = $("#queId").val();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/qaFollowInfo/add", function(data) {
            if(data == 'success'){
                $(ele).removeClass('question-collection')
                $(ele).attr('title','收藏成功');
                $(ele).addClass('question-collection-focus');
                $(ele).attr('onclick',"cancelFocus(this)");
                Hussar.success("收藏成功");
            }
        });
        ajax.set("queId",queId);
        ajax.start();
    });
}

function cancelFocus(ele) {
    var queId = $("#queId").val();
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;
        var ajax = new $ax(Hussar.ctxPath + "/qaFollowInfo/cancelFollow", function(data) {
            if(data == 'success'){
                Hussar.success("取消收藏");
                $(ele).removeClass('question-collection-focus')
                $(ele).removeAttr('title')
                $(ele).addClass('question-collection')
                $(ele).attr('onclick',"focusOnQue(this)");
            } else if(data == 'queDel'){
                Hussar.info("问题已删除");
            }
        });
        ajax.set("queId",queId);
        ajax.start();
    });
}
// 转入知识库
function intoKnowledgeBase(ele) {
    var queId = $("#queId").val();
    var title = $("#mainTitle").val();
    var label = $("#mainLabel").val();

    var ansId = $(ele).attr("value");

    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        var answerInfo = null;
        var ajax = new $ax(Hussar.ctxPath + "/answer/getAnswerById", function(data) {
            answerInfo = data;
        });
        ajax.set("ansId",ansId);
        ajax.start();

        var ajax = new $ax(Hussar.ctxPath + "/knowledge/add", function(data) {
            if(data == 'success'){
                Hussar.success("转入成功");
                $(".knowleggeButton").hide();
            } else if(data == 'esError'){
                Hussar.info("转入ES失败");
            }
        });
        ajax.set("title",title);
        ajax.set("label",label);
        ajax.set("content",answerInfo.ansContent);
        ajax.set("text",answerInfo.ansContentText);
        ajax.set("inputType","0");
        ajax.set("queId",queId);
        ajax.start();
    });

}

/*打开分享链接*/
// 功能暂时隐藏了，只是站过来一份，功能没实现
function share() {
    var queId = $("#queId").val();
    //var title = $("#mainTitle").val();
    var title = null;
    layui.use(['Hussar','HussarAjax'], function(){
        var Hussar = layui.Hussar,
            $ax = layui.HussarAjax;

        var url = "/q/shareConfirm";
        var w =  538;
        var h = 390;

        if (title == null || title == '') {
            title = false;
        }
        if (url == null || url == '') {
            url = "404.jsp";
        }
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        }
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        }
        layer.open({
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false, //不固定
            maxmin: false,
            shadeClose: true,
            shade: 0.4,
            title: title,
            /*offset:parseInt(scrollHeightShare) - (h - 200) / 2 + "px",*/
            content: Hussar.ctxPath+url + "?questionId=" + queId + "&title=" + title  + "&" + Math.random()
        });
    });
}

function openAll(ele) {
    onAll = "2"
    refreshFile();
    $(ele).attr('onclick',"closeAll(this)");
    $(ele).html("收起")
}

function closeAll(ele) {
    onAll = "1"
    refreshFile();
    $(ele).attr('onclick',"openAll(this)");
    $(ele).html("更多回答")
}