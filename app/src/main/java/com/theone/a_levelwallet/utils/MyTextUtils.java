package com.theone.a_levelwallet.utils;

import java.util.regex.Pattern;

/**
 * Created by liuyuan on 2015/10/31.
 */
public class MyTextUtils {

    /*
      * 判断是否为整数
      * @param str 传入的字符串
      * @return 是整数返回true,否则返回false
    */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

}
