package com.nextonedaygg.hongbao;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.nextonedaygg.hongbao.util.SpUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AccessibilityManager.AccessibilityStateChangeListener, View.OnClickListener {

    private TextView mStartSrerver;
    private AlertDialog.Builder mDialog;
    private AccessibilityManager mManager;
    private RelativeLayout mRlswitch;
    private ImageView mSwitch;
    private boolean serviceStatus = false;
    private Toolbar mToolbar;
    private LinearLayout mDelay;
    private LinearLayout mPingbi;
    private LinearLayout mAbout;
    private NotificationManager mNotifyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //沉浸式状态栏
//        SetStatus();
        initView();
        initEvent();
        //监听服务状态
        mManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        mManager.addAccessibilityStateChangeListener(this);

        //是否是第一次进来，如果是就弹出来，如果不是就关闭。
        boolean status = SpUtils.getBoolean(this, Constant.SERVICE_STATUS, false);
        if (!status) {
            showDialog();
        }
        updateServiceStatus(isServiceEnabled());
    }

    /**
     * 启动显示一个对话框，进行提示开启服务
     */
    private void showDialog() {

        mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle(R.string.dialog_title);
        mDialog.setMessage(R.string.dialog_content);
        mDialog.setPositiveButton(R.string.button_posit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //去开启打开服务权限
                startAccessibilyService();
            }
        });
        mDialog.setNegativeButton(R.string.button_negat, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDialog.create().dismiss(); //关闭dialog
                // 不作为
            }
        });
        mDialog.setCancelable(false);// 设置为不可取消
        mDialog.show();
        SpUtils.putBoolean(this, Constant.SERVICE_STATUS, true);
    }

    private void initEvent() {
        mRlswitch.setOnClickListener(this);
        mDelay.setOnClickListener(this);
        mPingbi.setOnClickListener(this);
        mAbout.setOnClickListener(this);

    }

    private void SetStatus() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
    }

    private void initView() {

        mStartSrerver = findViewById(R.id.btn_server);
        mRlswitch = findViewById(R.id.rl_switch_server);
        mSwitch = findViewById(R.id.switch_server);
        mToolbar = findViewById(R.id.toolbar);
        mDelay = findViewById(R.id.btn_delay);
        mPingbi = findViewById(R.id.pingbi);
        mAbout = findViewById(R.id.about);
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }


    private void updateServiceStatus(boolean serviceStatus) {
        if (serviceStatus) {
            mSwitch.setImageResource(R.drawable.open);
            mStartSrerver.setText(R.string.start_service);
        } else {
            mSwitch.setImageResource(R.drawable.close);
            mStartSrerver.setText(R.string.end_service);
        }
    }

    /**
     * 开启服务
     */
    private void startAccessibilyService() {
        Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(accessibleIntent);
    }


    @Override
    public void onAccessibilityStateChanged(boolean b) {
        // TODO: 2018/1/26 在这里发送一个状态栏通知状态，
        updateServiceStatus(b);
        Toast.makeText(this, "红包服务启动：" + b, Toast.LENGTH_SHORT).show();
        sendNotify(b);
    }
    private BroadcastReceiver receiver_onclick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    public static final String ONCLICK = "com.app.onclick";
    /**
    在这里开个通知栏
     * @param b
     */
    private void sendNotify(boolean b) {
        NotificationCompat.Builder notify = new NotificationCompat.Builder(this);
        notify.setContentTitle("服务开启通知");
        notify.setTicker("服务开启通知");
        notify.setSmallIcon(R.drawable.logo);
        if(b){
            notify.setContentText("红包服务开启");
//            RemoteViews view=new RemoteViews(getPackageName(), R.layout.notify_view);
//            view.setTextViewText(R.id.tv_title, "服务开启通知");
//            view.setTextViewText(R.id.tv_content, "服务开启通知");
//            view.setImageViewResource(R.id.img_icon, R.drawable.logo);

//            IntentFilter filter_click = new IntentFilter();
//            filter_click.addAction(ONCLICK);
//            registerReceiver(
// , filter_click);
//            Intent Intent_pre = new Intent(ONCLICK);
//            PendingIntent pendIntent_click = PendingIntent.getBroadcast(this, 0, Intent_pre, 0);
//            view.setOnClickPendingIntent(R.id.img_icon,pendIntent_click);

//            notify.setContent(view);
            notify.setOngoing(true);
            Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{accessibleIntent} ,0);
            notify.setContentIntent(pendingIntent);
            Notification build = notify.build();
            build.flags |= Notification.FLAG_NO_CLEAR;
            build.flags |= Notification.FLAG_ONGOING_EVENT;
            mNotifyManager.notify(1, build);
        }else {
            notify.setContentText("服务已经关闭");
            notify.setAutoCancel(true);
            mNotifyManager.notify(1, notify.build());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: 2018/1/26 在这里最后给关闭掉通知栏
        mNotifyManager.cancel(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus(isServiceEnabled());
    }

    /**
     * 获取 HongbaoService 是否启用状态
     *
     * @return
     */
    private boolean isServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices =
                mManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.server.HBAccessibilyService")) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_switch_server:
                startAccessibilyService();
                break;
            case R.id.btn_delay:
                showDelayDialog();
                break;
            case R.id.pingbi:
                showPingBiDialog();
                break;
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            default:
                break;
        }

    }

    private void showPingBiDialog() {

        PingBiDialog  pingbi = new PingBiDialog(this,R.style.MyDialog);
        pingbi.show();
    }

    private void showDelayDialog() {

       DelayDialog delay = new DelayDialog(this,R.style.MyDialog);
       delay.setTitle("延时拆开红包");
       delay.show();
    }
}
