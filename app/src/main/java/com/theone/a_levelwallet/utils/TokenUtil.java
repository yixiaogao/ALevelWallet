package com.theone.a_levelwallet.utils;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liuyuan on 2015/11/5.
 */
public class TokenUtil {
    public String token;

    public String getToken(String phoneNumber) {
        String queryString = "phoneNumber=" + phoneNumber;
        // 查询的URL
        String url = HttpUtil.URL + "getToken?" + queryString;
        System.out.println("token:" + url);
        //查询并返回结果
        token = HttpUtil.queryStringForPost(url);
        if (token==null){
            return "";
        }
        return token;
    }

    public void writetoken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("TOKEN", token);
        editor.commit();
    }
}
