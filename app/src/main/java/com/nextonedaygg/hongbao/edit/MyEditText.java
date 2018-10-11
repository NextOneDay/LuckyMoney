package com.nextonedaygg.hongbao.edit;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;

import java.lang.reflect.Method;

/**
 * Created by nextonedaygg on 2018/10/11.
 */

public class MyEditText extends AppCompatEditText {

    private static final String TAG = "MyEditText";

    public MyEditText(Context context) {
        super(context);
    }


//光标禁止，不弹软键盘，输入按键是，自己设置进去

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "edittext:" + keyCode);

        return super.onKeyDown(keyCode, event);

    }

    int count=0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(count==10){
            Log.d(TAG, "onTouchEven"+count);

            disableShowSoftInput();
            count++;
        }
        return super.onTouchEvent(event);
    }

    //activity 显示的时候，只要给edittext获取光标，然后禁止显示软件盘
//    然后在ontouch的时候就能够显示软键盘
    //在dispath的时候我禁止你弹出键盘，但是有光标可以输入，
//    但是点击的时候就要弹出键盘
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "edittext:" + event.getKeyCode());
disableShowSoftInput();
        return super.dispatchKeyEvent(event);
    }


    /**
     * 禁止Edittext弹出软件盘，光标依然正常显示。
     */
    public void disableShowSoftInput() {

        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(this, true);
        } catch (Exception e) {
        }

        try {
            method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
