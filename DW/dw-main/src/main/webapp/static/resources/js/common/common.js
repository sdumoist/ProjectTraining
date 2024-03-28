
$(function(){
    /**
     * 创建一个下拉框列表
     * @param {Object} configObj 窗口参数
     */
    $.fn.createSingleDropDown = function(array){
        $(this).empty();
        //$(this).addClass("chosen-select");
        var sel = $(this);
        sel.append($("<option value=''></option>"));
        $.each(array, function(i, item){
            var taskName = item[1];
            if(item[1].length > 15){
                taskName = item[1].substring(0,14)+"...";
            }
            sel.append($("<option title='"+ item[1]+"' value='" + item[0] + "'>" + taskName + "</option>"));
        });
//		$(this).trigger("chosen:updated"); /* 试验可用 */
//		$(this).chosen({width : '100%'});
    }
    /**
     * 创建一个layer窗口
     * @param {Object} configObj 窗口参数
     * return layer实例
     */
    $.fn.createLayerWindow = function(configObj){
        var layerConfig = configObj;
        var contentHeight;
        if(configObj.full){
            layerConfig.full = function(layero, index){
                contentHeight = $(".layui-layer-content").height();
                $(".layui-layer-content").css("height" ,'100%');
                configObj.full();
            }
        }else{
            layerConfig.full = function(layero, index){
                contentHeight = $(".layui-layer-content").height();
                $(".layui-layer-content").css("height" ,'100%');
            }
        }
        if(configObj.restore){
            layerConfig.restore = function(layero, index){
                $(".layui-layer-content").css("height" ,contentHeight);
                configObj.restore();
            }
        }else{
            layerConfig.restore = function(layero, index){
                $(".layui-layer-content").css("height" ,contentHeight);
            }
        }
        layerConfig.content = $(this);
        return layer.open(layerConfig);
    }

    /**
     * 短暂提示框
     * msg: 消息
     * timeout: 提示时间
     * 回调：funciton
     */
    $.showMomentInforDlg = function(msg, timeout, callback){
        if(callback){
            layer.msg(msg,{
                time: timeout,
                icon: 1,
                area: ['300px', '180px']
            }, callback);
        }else{
            layer.msg(msg,{
                time: timeout,
                icon: 1,
                area: ['300px', '180px'],
            });
        }
    }

    /**
     * 提示框
     * title : 标题
     * msg : 信息
     * state :状态  0：警告提示框，1：成功提示框，2：错误提示框，3：确认提示框，4：拒绝操作提示，5：出错提示（不要用），6：成功提示（不要用）
     * 回调：funciton
     */
    $.showInfoDlg = function(title, msg, state, callback){
        if(callback){
            layer.alert(msg, {
                icon :  state,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: title
            }, callback);
        }else{
            layer.alert(msg, {
                icon :  state,
                shadeClose: true,
                skin: 'layui-layer-molv',
                shift: 5,
                area: ['300px', '180px'],
                title: title
            });
        }
    }


    /**
     * 重置表单
     * @param {Object} config 注意：成功返回请使用 $(this).resetForm(); // 提交后重置表单
     */
    $.fn.formReset = function(){
        $(this)[0].reset();
        //去除验证信息
        if($(this).data('validator')){
            $(this).data('validator').resetForm();
            $(this).children(".form-control .error").removeClass("error");
        }
        //chosen插件特殊处理
        $('.chosen-select').trigger("chosen:updated"); /* 试验可用 */
    }


    /**
     * 日期格式转换方法  支持 hh:mm
     * @param date
     * @param fmt
     * @returns {*}
     */
    $.dateFormatter = function (date,fmt) { //author: meizz
        var o = {
            "M+": date.month + 1, //月份
            "d+": date.date, //日
            "h+": date.hours, //小时
            "m+": date.minutes, //分
            "s+": date.seconds //秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, ((1900 + date.year) + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    /**
     * 数字加千位符，并保留小数点后两位
     * @param num
     * @param precision
     * @param separator
     * @returns {*}
     */
    $.numFormatter = function formatNumber(num, precision, separator) {
        var parts;
        // 判断是否为数字
        if (!isNaN(parseFloat(num)) && isFinite(num)) {
            // 把类似 .5, 5. 之类的数据转化成0.5, 5, 为数据精度处理做准, 至于为什么
            // 不在判断中直接写 if (!isNaN(num = parseFloat(num)) && isFinite(num))
            // 是因为parseFloat有一个奇怪的精度问题, 比如 parseFloat(12312312.1234567119)
            // 的值变成了 12312312.123456713
            num = Number(num);
            // 处理小数点位数
            num = (typeof precision !== 'undefined' ? num.toFixed(precision) : num).toString();
            // 分离数字的小数部分和整数部分
            parts = num.split('.');
            // 整数部分加[separator]分隔,
            parts[0] = parts[0].toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1' + (separator || ','));

            return parts.join('.');
        }
        return num;
    }

    $.fileSize = function (params){
        var fileSize= params;
        if(!isNaN(params)){
            if (params < 1024) {
                fileSize = parseInt(params).toFixed(2) + "B";
            } else if (params < 1048576) {
                fileSize = (parseInt(params)/ 1024).toFixed(2) + "KB";
            } else if (params < 1073741824) {
                fileSize = (parseInt(params)/ 1048576).toFixed(2) + "MB";
            } else {
                fileSize = (parseInt(params) / 1073741824).toFixed(2) + "G";
            }
        }
        return fileSize;
    }

    $.CurentTime = function ()
    {
        var now = new Date();

        var year = now.getFullYear();       //年
        var month = now.getMonth() + 1;     //月
        var day = now.getDate();            //日

        var hh = now.getHours();            //时
        var mm = now.getMinutes();          //分

        var clock = year + "-";

        if(month < 10)
            clock += "0";

        clock += month + "-";

        if(day < 10)
            clock += "0";

        clock += day + " ";
        return(clock);
    }
});