layui.use(['form', 'laypage', 'jquery','layer','laytpl','Hussar'], function(){
    var $ = layui.jquery,
        form=layui.form,
        laypage = layui.laypage,
        Hussar = layui.Hussar,
        laytpl = layui.laytpl,
        layer = layui.layer,
        $ax = layui.HussarAjax,
        element = layui.element;

    $(function () {
        /*$.ajax({
            type:"post",
            url: Hussar.ctxPath+"/personalIntegral/rulesList",
            data:{

            },
            async:true,
            cache:false,
            dataType:"json",
            success:function(result){
                $(".integral-list").html("");
                var j=0;
                var data=result.list;
                var inner="";
                for(var  i=0;i<data.length;i++){
                    if((data[i].ruleCode)=="upload"){
                        var grader=data[i].integral;
                        if((grader+"").indexOf("-")==-1){
                            grader="+"+grader;
                        }
                        $("#uploadName").html(data[i].ruleName);
                        $("#uploadGrade").html(grader)
                        continue;
                    }
                    else if((data[i].ruleCode)=="dayLimit"){
                        $("#limit").html(data[i].integral);
                        continue;
                    }
                    else {
                        if((data[i].ruleCode)!="dayLimit"&&(data[i].ruleCode)!="defaultBonus"){
                            j=j+1;
                            var graderNew=data[i].integral;
                            if((""+graderNew).indexOf("-")==-1){
                                graderNew="+"+graderNew;
                            }else{
                                graderNew="<span style='color: #F86842'>"+graderNew+"</span>";
                            }

                            if(j%2==0){
                                inner+='<div class="integral-theme" ><div class="name">'+data[i].ruleName+'</div><div class="integral-grade">'+graderNew+'</div></div>';

                            }else{
                                inner+='<div class="integral-theme" style="margin-right: 12px"><div class="name">'+data[i].ruleName+'</div><div class="integral-grade">'+graderNew+'</div></div>';

                            }
                        }

                    }

                }



                $(".integral-list").html(inner);

            }
        });*/
        var ajax = new $ax(Hussar.ctxPath + "/personalIntegral/rulesList", function(result) {
            $(".integral-list").html("");
            var j=0;
            var data=result.list;
            var inner="";
            for(var  i=0;i<data.length;i++){
                if((data[i].ruleCode)=="upload"){
                    var grader=data[i].integral;
                    if((grader+"").indexOf("-")==-1){
                        grader="+"+grader;
                    }
                    $("#uploadName").html(data[i].ruleName);
                    $("#uploadGrade").html(grader)
                    continue;
                }
                else if((data[i].ruleCode)=="dayLimit"){
                    $("#limit").html(data[i].integral);
                    continue;
                }
                else {
                    if((data[i].ruleCode)!="dayLimit"&&(data[i].ruleCode)!="defaultBonus"){
                        j=j+1;
                        var graderNew=data[i].integral;
                        if((""+graderNew).indexOf("-")==-1){
                            graderNew="+"+graderNew;
                        }else{
                            graderNew="<span style='color: #F86842'>"+graderNew+"</span>";
                        }

                        if(j%2==0){
                            inner+='<div class="integral-theme" ><div class="name">'+data[i].ruleName+'</div><div class="integral-grade">'+graderNew+'</div></div>';

                        }else{
                            inner+='<div class="integral-theme" style="margin-right: 12px"><div class="name">'+data[i].ruleName+'</div><div class="integral-grade">'+graderNew+'</div></div>';

                        }
                    }

                }

            }



            $(".integral-list").html(inner);
        }, function(data) {

        });
        ajax.start();
    })
});