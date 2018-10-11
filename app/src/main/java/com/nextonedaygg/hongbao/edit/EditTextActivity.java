package com.nextonedaygg.hongbao.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.lang.reflect.Method;


/**
 * Created by nextonedaygg on 2018/10/11.
 */

public class EditTextActivity extends AppCompatActivity {
    private static final String TAG = "EditTextActivity";
    private EditText mEditText;
    int count=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_edittext);
        mEditText = findViewById(R.id.edit);

        disableShowSoftInput(false);

        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(count==10){
                    Log.d(TAG, "onTouchEven"+count);

                    disableShowSoftInput(true);
                    count++;
                }
                return false;
            }
        });
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onTouchEven"+count);

            }
        });
    }


    /**
     * 禁止Edittext弹出软件盘，光标依然正常显示。
     */
    public void disableShowSoftInput(boolean disable) {

        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(mEditText, disable);
        } catch (Exception e) {
        }

        try {
            method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(mEditText, disable);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEven activity");

        return super.onTouchEvent(event);
    }

    /**
     * 禁止Edittext弹出软件盘，光标依然正常显示
     * call_roomNum 是EditText编辑框
     */
    public void disableShowSoftInputs() {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            mEditText.setInputType(InputType.TYPE_NULL);
        } else {//禁止Edittext弹出软件盘
            //Call是当前活动
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {//光标依然正常显示
                Class<EditText> cls = EditText.class;
                Method setSoftInputShownOnFocus;
                setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputShownOnFocus.setAccessible(true);
                setSoftInputShownOnFocus.invoke(mEditText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    // 能够拿到按键事件
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "ACTIVITY:" + event.getKeyCode());



        return super.dispatchKeyEvent(event);
    }


    public void show(View view){

        disableShowSoftInput(true);
    }
    public void hide(View view){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
