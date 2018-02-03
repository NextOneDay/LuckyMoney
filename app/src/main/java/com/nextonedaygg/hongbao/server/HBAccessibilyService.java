package com.nextonedaygg.hongbao.server;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.nextonedaygg.hongbao.Constant;
import com.nextonedaygg.hongbao.util.SpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nextonedaygg on 2017/12/31.
 */

public class HBAccessibilyService extends AccessibilityService {
    private static final String TAG = "HBAccessibilyService";
    private AccessibilityNodeInfo lastInfo;// 已经开过的红包，就不要了
    private String WIEXIN_TEXT = Constant.PACKET_NOTIFY;
    private ArrayList<AccessibilityNodeInfo> mRoot;
    private Handler mHandler;
    private boolean isReceivingHongbao = true; // 是否有红包
    private ArrayList<Integer> mList;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        String pkgName = accessibilityEvent.getPackageName().toString();
//        if ("xxx.xxx.xxx".equals(pkgName)) // 在这里做包名过滤
        int eventType = event.getEventType();
        Log.d(TAG, eventType + "::");
        switch (eventType) {
            //第一步：监听通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                checkContent(event);
                break;
            //第二步：监听是否进入微信红包消息界面
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                checkWindowContent(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                String className = event.getClassName().toString();
                Log.d(TAG, className + ":::::");
                if (watchList(event)) {
                    //开始抢红包
                    getPacket();
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void checkWindowContent(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        if (className.equals(Constant.PACKAGE_LAUNCHER)) {
            // 判断聊天界面 返回true 表示 不是在列表界面，
            if (watchList(event)) {
                //开始抢红包
                getPacket();
            }

        } else if (className.equals(Constant.PACKAGE_RECEIVE)) {
            //开始打开红包
            String string = SpUtils.getString(this, Constant.DELAY_TIME, "");
            int time = 0;
            if (null != string && string.length() > 0) {
                time = Integer.parseInt(string) * 100;
            }
            if (mHandler == null) {
                mHandler = new Handler();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        openPacket();
                    } catch (Exception e) {
                    }
                }
            }, time);

        } else if (className.equals(Constant.PACKET_DETAIL)) {
            close();
        }

    }

    /**
     * 在聊天列表的时候有红包提示，进入聊天页面
     * 包括在聊天的界面中，也会如果有红包出现，也会获取到红包信息，然后将布尔值改为true
     *
     * @param event
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean watchList(AccessibilityEvent event) {


        AccessibilityNodeInfo eventSource = event.getSource();
        // Not a message
        if (eventSource == null)
            return true;
        List<AccessibilityNodeInfo> nodes = eventSource.findAccessibilityNodeInfosByViewId(Constant.LIST_TEXT);
//        com.tencent.mm:id/apr
        //增加条件判断currentActivityName.contains(WECHAT_LUCKMONEY_GENERAL_ACTIVITY)
        //避免当订阅号中出现标题为“[微信红包]拜年红包”（其实并非红包）的信息时误判
        if (nodes != null && !nodes.isEmpty()) {
            AccessibilityNodeInfo nodeToClick = nodes.get(0);
            if (nodeToClick == null) return true;
            boolean equals = nodeToClick.getText().toString().contains(WIEXIN_TEXT);
            if (equals) {
                performClick(nodeToClick);
                return false;
            }
        } else {
            List<AccessibilityNodeInfo> list = eventSource.findAccessibilityNodeInfosByText(WIEXIN_TEXT);
            if (list != null && list.size() > 0) {
                performClick(eventSource);
                return false;
            }
        }

        return true;
    }

    /**
     * 点击事件
     */
    public void performClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }

    /**
     * 关闭红包详情界面,实现自动返回聊天窗口
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void close() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            //为了演示,直接查看了关闭按钮的id
            performGlobalAction(GLOBAL_ACTION_BACK);
        }
    }

    //2146553454
//        2147401056 可能对象没有被回收，所以就是这个
    @SuppressLint("NewApi")
    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;
        boolean aBoolean = SpUtils.getBoolean(this, Constant.IS_TEXT_INPUT, false);
        if (!aBoolean) {
            // 使用id的方式打开，如果不行使用文本匹配
//            List<AccessibilityNodeInfo> infoList = rootNode.findAccessibilityNodeInfosByViewId(Constant.LINEARID);
//            if (infoList != null && !infoList.isEmpty()) {
//                AccessibilityNodeInfo item = infoList.get(infoList.size() - 1);
//                Log.d(TAG, isReceivingHongbao+":::这个item的hashcode:"+item.hashCode());
//                // 我可以根据红包的状态来区分是否开启
//                if (isReceivingHongbao || !mList.contains(item.hashCode())) {
//                    Log.d(TAG, "有红包::" + item.hashCode());
//                    item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    isReceivingHongbao = false;
//                    mList.add(item.hashCode());
//                    return;
//                }
//            }
            List<AccessibilityNodeInfo> textList = rootNode.findAccessibilityNodeInfosByViewId(Constant.TEXTSTATUS);
            if (textList != null && !textList.isEmpty()) {
                AccessibilityNodeInfo item = textList.get(textList.size() - 1);
                // 我可以通过文字来判断是否有红包,对于自己的红包，状态来判断
                Log.d(TAG, isReceivingHongbao + ":::这个item的hashcode:" + item.hashCode());
                if (packetStatus(item)) {
                    item.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    isReceivingHongbao = false;
                    return;
                }
            }

            AccessibilityNodeInfo inofo = getTheLastNode(rootNode, Constant.PACKET_STATUS1, Constant.PACKET_STATUS2);
            if (null != inofo && packetStatus(inofo)) {
                inofo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                isReceivingHongbao = false;
            }
        } else {
            AccessibilityNodeInfo inofo = getTheLastNode(rootNode, Constant.PACKET_STATUS1, Constant.PACKET_STATUS2);
            if (null != inofo && packetStatus(inofo) && generateSignature(inofo)) {
                inofo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                isReceivingHongbao = false;
            }
        }
    }

    /**
     * 对于红包状态判断
     *
     * @param item
     * @return
     */
    public boolean packetStatus(AccessibilityNodeInfo item) {
        String packetText = item.getText().toString();
        Log.d(TAG, packetText);
        if (packetText.equals(Constant.PACKET_STATUS2)) {
            return true;
        } else if (packetText.equals(Constant.PACKET_STATUS1)) {
            //认为是查看红包，表示自己发的 ，当isreceiving 为false就表示已经领取过
            return isReceivingHongbao;
        }
        return false;
    }

    public boolean generateSignature(AccessibilityNodeInfo node, String excludeWords) {
        try {
            /* The hongbao container node. It should be a LinearLayout. By specifying that, we can avoid text messages. */
            AccessibilityNodeInfo hongbaoNode = node.getParent();
            if (!"android.widget.LinearLayout".equals(hongbaoNode.getClassName())) return false;

            /* The text in the hongbao. Should mean something. */
            String hongbaoContent = hongbaoNode.getChild(0).getText().toString();
            if (hongbaoContent == null || Constant.PACKET_STATUS1.equals(hongbaoContent))
                return false;

            /* Check the user's exclude words list. */
            String[] excludeWordsArray = excludeWords.split(" +");
            for (String word : excludeWordsArray) {
                if (word.length() > 0 && hongbaoContent.contains(word)) return false;
            }

            /* The container node for a piece of message. It should be inside the screen.
                Or sometimes it will get opened twice while scrolling. */
            AccessibilityNodeInfo messageNode = hongbaoNode.getParent();

            Rect bounds = new Rect();
            messageNode.getBoundsInScreen(bounds);
            if (bounds.top < 0) return false;

            /* The sender and possible timestamp. Should mean something too. */
            String[] hongbaoInfo = getSenderContentDescriptionFromNode(messageNode);
            if (this.getSignature(hongbaoInfo[0], hongbaoContent, hongbaoInfo[1]).equals(this.toString()))
                return false;

            /* So far we make sure it's a valid new coming hongbao. */
//            this.sender = hongbaoInfo[0];
//            this.time = hongbaoInfo[1];
//            this.content = hongbaoContent;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String[] getSenderContentDescriptionFromNode(AccessibilityNodeInfo node) {
        int count = node.getChildCount();
        String[] result = {"unknownSender", "unknownTime"};
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo thisNode = node.getChild(i);
            if ("android.widget.ImageView".equals(thisNode.getClassName()) && "unknownSender".equals(result[0])) {
                CharSequence contentDescription = thisNode.getContentDescription();
                if (contentDescription != null)
                    result[0] = contentDescription.toString().replaceAll("头像$", "");
            } else if ("android.widget.TextView".equals(thisNode.getClassName()) && "unknownTime".equals(result[1])) {
                CharSequence thisNodeText = thisNode.getText();
                if (thisNodeText != null) result[1] = thisNodeText.toString();
            }
        }
        return result;
    }

    private String getSignature(String... strings) {
        String signature = "";
        for (String str : strings) {
            if (str == null) return null;
            signature += str + "|";
        }

        return signature.substring(0, signature.length() - 1);
    }

    /**
     * 这是用来判断是否包含不想领的文字
     *
     * @param item
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean generateSignature(AccessibilityNodeInfo item) {
        //todo 这是屏蔽掉某些红包不想领的红包
        String string = SpUtils.getString(this, Constant.TEXT_INPUT, "");
        if (null != string && string.length() > 0) {
            generateSignature(item, string);
        } else {
            return false;
        }
        return false;
    }


    /**
     * 查找按钮，进行遍历递归查找
     *
     * @param node
     * @return
     */

    private AccessibilityNodeInfo findOpenButton(AccessibilityNodeInfo node) {
        if (node == null)
            return null;

        //非layout元素
        if (node.getChildCount() == 0) {
            if ("android.widget.Button".equals(node.getClassName()))
                return node;
            else
                return null;
        }

        //layout元素，遍历找button
        AccessibilityNodeInfo button;
        for (int i = 0; i < node.getChildCount(); i++) {
            button = findOpenButton(node.getChild(i));
            if (button != null)
                return button;
        }
        return null;
    }

    /**
     * 获取匹配的红包信息,获取页面的所有红包个数
     *
     * @param texts
     * @return
     */
    private AccessibilityNodeInfo getTheLastNode(AccessibilityNodeInfo nodeInfo, String... texts) {
        int bottom = 0;
        AccessibilityNodeInfo lastNode = null, tempNode;
        List<AccessibilityNodeInfo> nodes;

        for (String text : texts) {
            if (text == null) continue;

            nodes = nodeInfo.findAccessibilityNodeInfosByText(text);

            if (nodes != null && !nodes.isEmpty()) {
                tempNode = nodes.get(nodes.size() - 1);
                if (tempNode == null) return null;
                Rect bounds = new Rect();
                tempNode.getBoundsInScreen(bounds);
                if (bounds.bottom > bottom) {
                    bottom = bounds.bottom;
                    lastNode = tempNode;
                }
            }
        }
        return lastNode;
    }

    /**
     * 打印一个节点的结构
     *
     * @param info
     */
    @SuppressLint("NewApi")
    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if (info.getText() != null) {
                if ("领取红包".equals(info.getText().toString()) || "查看红包".equals(info.getText().toString())) {
                    //这里有一个问题需要注意，就是需要找到一个可以点击的View
                    Log.i("demo", "Click" + ",isClick:" + info.isClickable());
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    AccessibilityNodeInfo parent = info.getParent();
                    while (parent != null) {
                        Log.i("demo", "parent isClick:" + parent.isClickable());
                        if (parent.isClickable()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }

                }
            }

        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    /**
     * 查找到
     */
    @SuppressLint("NewApi")
    private void openPacket() {

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> idList = nodeInfo.findAccessibilityNodeInfosByViewId(Constant.BUTTON);
        if (null != idList && !idList.isEmpty()) {
            AccessibilityNodeInfo info = idList.get(0);
            Log.d(TAG, "open hash" + info.hashCode());
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
        AccessibilityNodeInfo info = findOpenButton(nodeInfo);
        if (info != null && "android.widget.Button".equals(info.getClassName())) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }

        // 需要判断点击红包后，打开的是手慢了
        boolean hasNode = hasOneOfThoseNodes(nodeInfo, Constant.PACKET_NOT, Constant.PACKET_TIMEOUT);
        if (hasNode) {
            performGlobalAction(GLOBAL_ACTION_BACK);
        }
    }

    /**
     * 匹配传入字符
     *
     * @param texts
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean hasOneOfThoseNodes(AccessibilityNodeInfo nodeInfo, String... texts) {
        AccessibilityNodeInfo child = nodeInfo.getChild(1);
        for (String text : texts) {
            if (text == null) continue;
            String str = child.getText().toString();
            if (null != str && str.contains(text)) {
                return true;
            }
            List<AccessibilityNodeInfo> textid = nodeInfo.findAccessibilityNodeInfosByViewId(Constant.TEXT_DEC);
            String s = textid.get(0).getText().toString();
            if (null != s && s.contains(text)) return true;

        }
        return false;
    }

    private void checkContent(AccessibilityEvent event) {
        String content = event.getText().toString();
        Log.d(TAG, content + "内容");
        if (content.contains(WIEXIN_TEXT)) {
            //模拟打开通知栏消息
            if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                Notification notification = (Notification) event.getParcelableData();
                PendingIntent pendingIntent = notification.contentIntent;
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        } else if (content.contains("[已发送]")) {
            // 判断你自己发了红包，允许抢了
            isReceivingHongbao = true;
        }


    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // 可以在这里做包名过滤

        mHandler = new Handler();

    }



}
