package com.nextonedaygg.hongbao;

/**
 * Created by nextonedaygg on 2018/1/1.
 */

public class Constant {

    //这是红包的整个id
    public static final String LINEARID ="com.tencent.mm:id/ada";

    // 这是 text的id
    public static final String TEXTID = "com.tencent.mm:id/aea";

    // 这是查看红包，和红包已领取
    public static  final String TEXTSTATUS="com.tencent.mm:id/aeb" ;

    // 这是最下面的微信红包
    public static  final String FLAG_TEXT="com.tencent.mm:id/aec" ;


    public static final String LIST_LINEAR="com.tencent.mm:id/apr";
    // 这是列表界面的文字id
    public static  final String LIST_TEXT="com.tencent.mm:id/apv" ;



    // 这是button 按钮
   public static final String BUTTON="com.tencent.mm:id/c2i";

    // 这是看大家的手气
   public static final String BOTTOM_BUTTON="com.tencent.mm:id/c2j";

   // 这是打开红包的文字
    public static final  String TEXT_DEC="com.tencent.mm:id/c2h";


    public static final String SERVICE_STATUS = "server_status";
    public static final String DELAY_TIME = "delay_time";

    public static final String TEXT_INPUT = "text_input";
    public static final String IS_TEXT_INPUT = "is_text_input";


    // 红包领取状态
    public static  final  String RECEIVE_STATUS1="红包已过期";
    public static  final  String RECEIVE_STATUS2="红包已被领完";
    public static  final  String RECEIVE_STATUS3="红包已领取";

    //红包状态
    public static  final  String PACKET_STATUS1="查看红包";
    public static  final  String PACKET_STATUS2="领取红包";

    // 红包通知
    public static final String PACKET_NOTIFY="[微信红包]";


    // package name
    public static final String PACKAGE_LAUNCHER="com.tencent.mm.ui.LauncherUI";
    public static final String PACKAGE_RECEIVE="com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    public static final String PACKET_DETAIL="com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";


    public static final String PACKET_NOT="手慢了，红包派完了";
    public static final String PACKET_TIMEOUT="该红包已超过24小时";
}


