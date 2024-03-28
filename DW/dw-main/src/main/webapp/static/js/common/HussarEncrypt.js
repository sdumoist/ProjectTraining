/**
 * @Description: 通用前端加密组件（国密算法）
 * @Author: 组件
 * @Date: 2019/8/7.
 */

layui.define([  'layer'],function(exports) {
    var layer = layui.layer;
    var $ = layui.jquery;
    var HussarEncrypt = function(){
        this.spr =  spr;//前台私钥
        this.puy =  puy;//后台公钥
        this.signOpen = signOpen;//是否开启签名验证
    };
    if(!String.prototype.trim){
        String.prototype.trim = function(){
            return this.replace(/^\s+|\s+$/g,'');
        }
    };
    HussarEncrypt.prototype = {
        /**
         * 生成SM4密钥
         */
        genSM4Key : function(){
            var keys = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%&*()';
            var keylen = keys.length;
            var key = '';
            for (var i = 0; i < 16; i++) {
                key += keys.charAt(Math.floor(Math.random() * keylen));
            }
            return key;
        },

        /**
         * 加密过程
         * 1、使用随机SM4密钥对数据加密
         * 2、使用SM3对加密后数据进行签名
         * 3、使用SM2公钥加密SM4密钥
         * 4、拼装成json {data:data,sign:sign,key:key}
         */
        encrypt : function(jsonStr){
            var sm4Key = this.genSM4Key();
            var data = encryptbySM4(jsonStr,sm4Key);//SM4加密明文
            var sign = "";
            if(this.signOpen==="true"){
                sign = encryptbySM3(data);//SM3对密文签名
            }
            var key = encryptbySM2(sm4Key,this.puy);//SM2公钥加密sm4Key

            return JSON.stringify( {data:data,sign:sign,key:key} );
        },

        /**
         * 解密过程
         * 1、SM3验证签名
         * 2、SM2私钥解密SM4密钥
         * 3、SM4密钥解密数据
         */
        decrypt : function(jsonData){
            var data = jsonData.data;
            var sign = jsonData.sign;
            if(this.signOpen==="true"){
                if( sign != encryptbySM3(data)){
                    return "";
                }
            }
            try{
                var sm4Key = decryptbySM2(jsonData.key,this.spr);
                var realData = decryptbySM4(data,sm4Key);
                realData = JSON.parse(realData);
            }catch(err){
                console.debug("数据异常")
            }
            return realData;
        }
    };

    window.HussarEncrypt = HussarEncrypt;
    exports('HussarEncrypt', HussarEncrypt );
});