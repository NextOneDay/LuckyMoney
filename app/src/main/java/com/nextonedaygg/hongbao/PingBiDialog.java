package com.nextonedaygg.hongbao;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nextonedaygg.hongbao.util.SpUtils;

/**
 * Created by nextonedaygg on 2018/1/10.
 */

class PingBiDialog extends Dialog implements View.OnClickListener {

    private final Context mContext;
    private EditText mEtInput;


    public PingBiDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext =context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pingbi_view);
        mEtInput = findViewById(R.id.et_input);
        TextView cancel = findViewById(R.id.cancel);
        TextView confirm = findViewById(R.id.confirm);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.confirm:
                String trim = mEtInput.getText().toString().trim();
                if (null!=trim && trim.length()>0){
                    SpUtils.putString(mContext, Constant.TEXT_INPUT,trim);
                    SpUtils.putBoolean(mContext,Constant.IS_TEXT_INPUT,true);
                }
                dismiss();
                    break;
            default:
                break;
        }
    }
}
