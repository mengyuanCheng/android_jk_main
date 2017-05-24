package com.grgbanking.ct.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author ：     cmy
 * @version :     2017/5/9.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class LoginUtil {

    private String USER_INFO = "userInfo";
    private Context context;
    public static final String SAVE_PASSWORD = "savePassword";
    public static final String USER_NAME = "userName";
    public static final String USER_PASSWORD = "userPassword";

    public LoginUtil() {
    }


    public LoginUtil(Context context) {
        this.context = context;
    }

    // 存放字符串型的值

    public void setUserInfo(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putString(key, value);
        editor.commit();

    }

    // 存放整形的值

    public void setUserInfo(String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putInt(key, value);
        editor.commit();
    }

    // 获得用户信息中某项字符串型的值
    public String getStringInfo(String key) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    // 存放长整形值

    public void setUserInfo(String key, Long value) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putLong(key, value);
        editor.commit();

    }

    // 存放布尔型值

    public void setUserInfo(String key, Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putBoolean(key, value);
        editor.commit();
    }


    // 清空记录
    public void clear() {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    // 获得用户信息中某项布尔型参数的值
    public boolean getBooleanInfo(String key) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

}
