package com.jxdinfo.doc.common.util;

/**
 * @ClassName StateUtil
 * @Description TODO
 * @Author yjs
 * @Date 2019/6/21 14:22
 * @Version 1.0
 */
public class StateUtil {
    public static Integer pass(Integer state) {
        if (state != null) {
            return (state + 1);
        }
        return null;
    }
}
