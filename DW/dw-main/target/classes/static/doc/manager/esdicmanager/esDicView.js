/**
 * @Description: 附件典管理Demo脚本文件
 * @Author: chenxin
 * @Date: 2018/05/30.
 */

layui.use(['upload','element','Hussar'], function(){
  var $ = layui.jquery
  ,upload = layui.upload,element = layui.element
  ,Hussar = layui.Hussar;
 
  
  //普通图片上传
  var uploadInst = upload.render({
    elem: '#test1'
    ,url: Hussar.ctxPath+'/ESDicController/upload'

    ,done: function(res, index, upload){

      //上传成功
      layer.msg("上传成功！");

    }

    ,accept: 'file'//允许上传的文件类型
  });
  $("#download1").click(function () {
      $.ajaxFileUpload({
          url: Hussar.ctxPath+"/ESDicController/download",
          type: "post",
          data: {

          }
      });
  })

    //普通图片上传
    var uploadInst2 = upload.render({
        elem: '#test2'
        ,url: Hussar.ctxPath+'/ESDicController/uploadStop'

        ,done: function(res, index, upload){

            //上传成功
            layer.msg("上传成功！");

        }

        ,accept: 'file'//允许上传的文件类型
    });
    $("#download2").click(function () {
        $.ajaxFileUpload({
            url: Hussar.ctxPath+"/ESDicController/downloadStop",
            type: "post",
            data: {

            }
        });
    })

    //普通图片上传
    var uploadInst = upload.render({
        elem: '#test3'
        ,url: Hussar.ctxPath+'/ESDicController/uploadSynonymous'

        ,done: function(res, index, upload){

            //上传成功
            layer.msg("上传成功！");

        }

        ,accept: 'file'//允许上传的文件类型
    });
    $("#download3").click(function () {
        $.ajaxFileUpload({
            url: Hussar.ctxPath+"/ESDicController/downloadSynonymous",
            type: "post",
            data: {

            }
        });
    })


});