package com.nextonedaygg.hongbao;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nextonedaygg.hongbao.util.SpUtils;

/**
 * Created by nextonedaygg on 2018/1/8.
 */

public class DelayDialog extends Dialog implements SeekBar.OnSeekBarChangeListener {


    private final Context mContext;
    private SeekBar mSeekBar;
    private TextView mTextShow;

    public DelayDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.delay_view);
        mSeekBar = findViewById(R.id.seekbar);
        mTextShow =findViewById(R.id.text_show);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(50);
        setTitle("延迟拆开红包");
        String string = SpUtils.getString(mContext, Constant.DELAY_TIME, "");
        //重现
        if(string.length()>0){

            double dou= Double.parseDouble(string);
            mTextShow.setText("延迟"+(dou/10)+"秒拆开红包");
            mSeekBar.setProgress(Integer.parseInt(string));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        double time =i;
        double d=10;
        time = time/d;
        Log.d("Seekbar",i+"::;+"+time+"几秒");
        mTextShow.setText("延迟"+time+"秒拆开红包");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("onStartTrackingTouch","几秒");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
       //停止的时候
        Log.d("onStartTrackingTouch",seekBar.getProgress()+":几秒");

        SpUtils.putString(mContext, Constant.DELAY_TIME,seekBar.getProgress()+"");
    }
}
