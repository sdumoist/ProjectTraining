/**
 * Created by llm on 2017/12/4.
 */
$(document).ready(function(){
	//手机号码校验
    jQuery.validator.addMethod("phone", function(val, element) {
        var tel=/(^1[3|4|5|7|8]\d{9}$)|(^\d{3,4}-\d{7,8}$)|(^\d{7,8}$)|(^\d{3,4}-\d{7,8}-\d{1,4}$)|(^\d{7,8}-\d{1,4}$)/;
        return this.optional(element) || (tel.test(val));
    }, "请正确填写您的电话号码");
    //传真校验
    jQuery.validator.addMethod("fox", function(val, element) {
        var reg=/((^\d{11}$)|(^\d{12}$))|(^\d{3}-\d{8}$)|(^\d{4}-\d{7}$)|(^\d{4}-\d{8}$)/;
        return this.optional(element) || (reg.test(val));
    }, "请正确填写您的传真号码");
    //日期校验,包含年月日
    jQuery.validator.addMethod("date_ymd", function(val, element) {
        var reg=/ ^\d{4}-(0[1-9]|1[1-2])-(0[1-9]|2[0-9]|3[0-1])$/;
        return this.optional(element) || (reg.test(val));
    }, "请正确填写日期格式");
    //日期校验,例如 2017-12
    jQuery.validator.addMethod("date_ym", function(val, element) {
        var reg=/ ^\d{4}-(0[1-9]|1[1-2])$/;
        return this.optional(element) || (reg.test(val));
    }, "请正确填写日期格式");
    //过滤特殊字符
    jQuery.validator.addMethod("illegalchar", function(val, element) {
    	var pattern = new RegExp("[`~!@#$^&*()=|{}':;,.<>/?~！@#￥……&*（）——|【】‘；：”“'。，、？%+ 　\"\\\\]");  
        var specialStr = "";  
        for(var i=0;i<val.length;i++){  
             specialStr += val.substr(i, 1).replace(pattern, '');  
        }  
          
        if( specialStr == val){  
            return true;  
        }  
          
        return false; 
    }, "含有非法字符");
    //校验文本框长度
    jQuery.validator.addMethod("textlength", function(val, element) {        
        if(val.length==101){
        	return false;
        }
        return true;
    }, "字数已超过限制");
    
});