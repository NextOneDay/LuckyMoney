package com.nextonedaygg.hongbao.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nextonedaygg on 2017/12/31.
 * 这是sp工具类相关
 */

public class SpUtils {

    private static final String spfile= "status";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor edit;

    public static SharedPreferences.Editor getPreferences(Context context){
        sharedPreferences = context.getSharedPreferences(spfile,context.MODE_PRIVATE);
       return edit = sharedPreferences.edit();
    }

    public static void putString(Context context,String key, String value){
       if(edit==null){
           getPreferences(context);
       }

       edit.putString(key,value);
       edit.commit();

    }
    public static String getString(Context context , String key, String def){

        if(edit==null){
            getPreferences(context);
        }
        String string = sharedPreferences.getString(key, def);
        return string;

    }
    public static void putBoolean (Context context, String key, boolean value){
        if(edit==null){
            getPreferences(context);
        }
        edit.putBoolean(key,value);
        edit.commit();
    }
    public static boolean getBoolean (Context context, String key, boolean def){

        if(edit==null) {
            getPreferences(context);
        }

        return  sharedPreferences.getBoolean(key,def);
    }



}
