package com.jxdinfo.doc.common.util;

/**
 * @author luzhanzhao
 * @description 设备查询相关工具
 */
public class DeviceUtil {
    /**
     * @author luzhanzhao
     * @date 2018-12-28
     * @description 判断是否手机访问
     * @param requestHeader
     * @return
     */
    public static boolean  isMobileDevice(String requestHeader){
        /*
         * android : 所有android设备
         * mac os : iphone ipad
         * windows phone:Nokia等windows系统的手机
         */
        String[] deviceArray = new String[]{"android","mac os","windows phone"};
        if(requestHeader == null)
            return false;
        requestHeader = requestHeader.toLowerCase();
        for(int i=0;i<deviceArray.length;i++){
            if(requestHeader.indexOf(deviceArray[i])>0){
                return true;
            }
        }
        return false;
    }
}
