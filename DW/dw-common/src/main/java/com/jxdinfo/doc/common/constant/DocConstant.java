package com.jxdinfo.doc.common.constant;

/** 
 * 文库常量类 
 * @author wangning
 */
public class DocConstant {
	
	/** 数字  */
    public static enum NUMBER{
        ZERO("0",0),
        ONE("1",1),
        TWO("2",2),
        THREE("3",3),
        FOUR("4",4),
        FIVE("5",5),
        SIX("6",6),
        SEVEN("7",7),
        EIGHT("8",8),
        NINE("9",9),
        TEN("10",10);
    	
        private NUMBER(String name,int value){
            this.value = value;
            this.name = name;
        }
        private final int value;
        private final String name;
        
        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
    
    /** 特殊字符  */
    public static enum SPECIALCHAR{
        DOUBLESLASH("双斜杠","\\");
    	
        private SPECIALCHAR(String name,String value){
            this.value = value;
            this.name = name;
        }
        private final String value;
        private final String name;
        
        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
    
    /** 操作者类型 */
    public static enum AUTHORTYPE{
        USER("用户","0"),
        GROUP("群组","1");
    	
        private AUTHORTYPE(String name,String value){
            this.value = value;
            this.name = name;
        }
        private final String value;
        private final String name;
        
        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
    /** 操作者类型 */
    public static enum ROLEID{
        SUPERUSER("超级管理员","superadmin_role"),
        WKUSER("文库管理员","03b4cc9be3614ff4b5374e4d142f6bce"),
        WYH("委员会","4af8606b88f44f71b2c0dc5f1f7af79b"),
        FZR("部门负责人","fc82777d7b0a4070adafccd28b589767"),
        JJFZR("交接部门负责人","f0db8897bbba49129947ac6057a9d6ec"),
        ZTWYH("中台委员会","b20ec9faaa60492db0c5a102f55731ab");
        private ROLEID(String name,String value){
            this.value = value;
            this.name = name;
        }
        private final String value;
        private final String name;

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    /** 文档删除状态 */
    public static enum VALIDTYPE{
        INVALID("无效","0"),
        VALID("有效","1");

        private VALIDTYPE(String name,String value){
            this.value = value;
            this.name = name;
        }
        private final String value;
        private final String name;

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    /** 前后台标识 */
    public static enum OPERATETYPE{
        FRONT("前台","0"),
        MANAGER("后台","1");

        private OPERATETYPE(String name,String value){
            this.value = value;
            this.name = name;
        }

        private final String value;
        private final String name;

        public String getValue(){
            return value;
        }

        public String getName(){
            return name;
        }
    }
    
    /** 上传文件返回结果 */
    public static enum UPLOADRESULT{
        SUCCESS("上传成功","0"),
        EMPTY("上传文件内容为空","1"),
        NOSPACE("已分配存储空间不足","2"),
        FILEEXIST("文件已经存在","4"),
        NAMELONG("用户名过长","6"),
        NAMEERROR("用户名名称不合法","7"),
        ERRORTYPE("文档格式不支持","8"),
        FASTUPLOAD("秒传成功","5"),
        FAIL("上传失败","3"),
        HISTORYEXIST("存在相同的历史版本","9"),
        AUDITFILEEXIST("存在同名待审核文件","10"),
        WITHOUTAUTHORITY("无上传权限","11"),
        UPLOADFILEDAMAGE("上传时文件已损坏", "12");

        private UPLOADRESULT(String name,String value){
            this.value = value;
            this.name = name;
        }

        private final String value;
        private final String name;

        public String getValue(){
            return value;
        }

        public String getName(){
            return name;
        }
    }
    
    /** 文库角色标识 */
    public static enum ADMINFLAG{
    	WKADMIN("文库管理员",1),
        TEAMADMIN("团队管理员",2),
        COMMON("普通用户",3);

        private ADMINFLAG(String name,Integer value){
            this.value = value;
            this.name = name;
        }

        private final Integer value;
        private final String name;

        public Integer getValue(){
            return value;
        }

        public String getName(){
            return name;
        }
    }
}
