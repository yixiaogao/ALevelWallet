package com.theone.a_levelwallet.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static cn.smssdk.framework.utils.R.getStringRes;

/**
 * Created by liuyuan on 2015/10/30.
 * 用getApplication（）初始化这个类
 * 为了防止重复提交，建议设置获得验证码的按钮点击后三十秒不可用
 */
public class SMSUtil {
    private boolean isChecked;//是否验证成功
    private String infostr;//验证过程中的信息
    private Context context;//activity context
    // 填写从短信SDK应用后台注册得到的APPKEY
    public static final String APPKEY = "b486a843d2e1";
    // 填写从短信SDK应用后台注册得到的APPSECRET
    public static final String APPSECRET = "9ba24fe97c8198ac548811276082bee3";
    private MyHandler myHandler;

    public SMSUtil() {
    }

    /**
     * 在Activity里面用getApplication（）初始化这个类
     * @param context
     */

    public SMSUtil(Context context) {
        this.context = context;
        initSMS();
    }

    //初始化
    public void initSMS() {
        isChecked = false;
        myHandler = new MyHandler();
        //初始化SDK
        SMSSDK.initSDK(context, APPKEY, APPSECRET);
        //注册消息事件
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                myHandler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);
    }

    /**
     * phoneNum 是接受短信的电话号码
     *
     * @param phoneNum
     */
    public void getVerificationCode(String phoneNum) {
        Log.e("phoneNum", phoneNum);
        SMSSDK.getVerificationCode("86", phoneNum);
    }

    /**
     * phoneNum是接受短信的电话号码
     * verificationCode是验证码
     *
     * @param phoneNum
     * @param verificationCode
     */
    public void submitVerificationCode(String phoneNum, String verificationCode) {
        SMSSDK.submitVerificationCode("86", phoneNum, verificationCode);
    }

    //注销SMSSDK，记得在Activity的onDestroy里面注销
    public void unregisterAllEventHandler() {
        SMSSDK.unregisterAllEventHandler();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("result/event", "result=" + result + "\nevent=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    Toast.makeText(context, "验证成功", Toast.LENGTH_SHORT).show();
                    setInfostr("验证成功");
                    setIsChecked(true);

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(context, "验证码正在发送", Toast.LENGTH_SHORT).show();
                    setInfostr("验证码正在发送");
                }
            } else {
                ((Throwable) data).printStackTrace();
                int resId = getStringRes(context, "smssdk_network_error");
                Toast.makeText(context, "发生错误，请检查网络，验证码或电话号码", Toast.LENGTH_SHORT).show();
                setInfostr("发生错误，请检查网络是否通畅，验证码或电话号码是否错误");
                setIsChecked(false);
                if (resId > 0) {
                    Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getInfostr() {
        return infostr;
    }

    public void setInfostr(String infostr) {
        this.infostr = infostr;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
