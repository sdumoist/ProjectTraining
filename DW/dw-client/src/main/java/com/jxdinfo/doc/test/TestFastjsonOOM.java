package com.jxdinfo.doc.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

/**
 * @author xubin
 * @version 1.0
 * @since 2019/12/11 13:37
 * <p>
 * TestFastjsonOOM
 * </p>
 */
public class TestFastjsonOOM {
    public static void main(String[] args) {
        String str = "{\"a\":\"\\x";
        try {
            Object obj = JSON.parse(str);
            System.out.println(obj);
        } catch (JSONException e) {
            System.out.println(e);
        }
    }
}
