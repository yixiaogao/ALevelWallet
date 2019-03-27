package com.theone.a_levelwallet.activity.loginAndRegister;

/**
 * Created by lh on 2015/8/18.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.utils.MyTextUtils;
import com.theone.a_levelwallet.utils.SMSUtil;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static cn.smssdk.framework.utils.R.getStringRes;

public class RegisterConfirmActivity extends Activity implements OnClickListener {
    public static final int REGION_SELECT = 1;
    private TextView tv_AL_Server;  //服务条款显示
    private TextView tv_region_modify;  //手机地址修改
    private TextView tv_region_show;    //手机地址显示
    private Button btn_title_left, btn_send_code;   //返回键，验证码发送
    private CheckBox chk_agree; //勾选服务条款
    private Button confirm; //确认验证码
    private EditText confirmInput;  //输入验证码
    private EditText et_mobileNo;   //输入电话号码
    private MyCount mc;     //时间限制
    private String phone;   //手机号码
    private Context context;    //activity context
    private MyHandler myHandler;    //线程消息传递
    private ProgressDialog progressDialog;  //信息提示对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_confirm);
        initView();

    }

    private void initView() {

        btn_title_left = (Button) findViewById(R.id.btn_title_left);
        btn_title_left.setOnClickListener(this);
        confirm = (Button) findViewById(R.id.btn_erificationcode);
        confirm.setOnClickListener(this);
        confirmInput = (EditText) findViewById(R.id.et_codeInput);
        btn_send_code = (Button) findViewById(R.id.btn_send_code);
        btn_send_code.setOnClickListener(this);

        tv_AL_Server = (TextView) findViewById(R.id.tv_AL_Server);
        tv_AL_Server.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        tv_region_show = (TextView) findViewById(R.id.tv_region_show);

        tv_region_modify = (TextView) findViewById(R.id.tv_region_modify);
        tv_region_modify.setOnClickListener(this);

        chk_agree = (CheckBox) findViewById(R.id.chk_agree);
        et_mobileNo = (EditText) findViewById(R.id.et_mobileNo);

        context = getApplication();
        // System.out.print(temp);
        initSMS();
    }

    public void initSMS() {
        myHandler = new MyHandler();
        //初始化SDK
        SMSSDK.initSDK(context, SMSUtil.APPKEY, SMSUtil.APPSECRET);
        Log.i("TAG", "initSMSSDK");
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

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        // TODO Auto-generated method stub
        switch (id) {
            case REGION_SELECT:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请选择所在地");
                builder.setSingleChoiceItems(
                        new String[]{"+86中国大陆", "+853中国澳门", "+852中国香港", "+886中国台湾"},
                        0,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                switch (which) {
                                    case 0:
                                        tv_region_show.setText("+86中国大陆");
                                        break;
                                    case 1:
                                        tv_region_show.setText("+853中国澳门");
                                        break;
                                    case 2:
                                        tv_region_show.setText("+852中国香港");
                                        break;
                                    case 3:
                                        tv_region_show.setText("+886中国台湾");
                                        break;
                                }
                                dismissDialog(REGION_SELECT);
                            }
                        });
                return builder.create();
        }
        return null;
    }


    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_region_modify:
                showDialog(REGION_SELECT);
                break;

            case R.id.btn_send_code:
                String et_mobileNoStr = et_mobileNo.getText().toString().trim();
                if (TextUtils.isEmpty(et_mobileNoStr)) {
                    Toast.makeText(this, "电话不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!MyTextUtils.isInteger(et_mobileNoStr)) {
                    Toast.makeText(this, "电话号码输入有误", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.i("TAG", "SMSSDK.getVerificationCode");

                mc = new MyCount(30000, 1000);//计时三十秒，每秒刷新
                mc.start();
                btn_send_code.setClickable(false);//按钮设置为不可编辑

                //开启线程阻塞ui
                progressDialog = ProgressDialog.show(RegisterConfirmActivity.this, "", "正在提交请稍后......");

                //发送获得验证码请求
                new GetVerificationCodeThread(et_mobileNoStr).start();
                // SMSSDK.getVerificationCode("86", et_mobileNoStr);
                break;

            case R.id.btn_title_left:
                //返回登录界面
                RegisterConfirmActivity.this.finish();
                break;

            case R.id.btn_erificationcode:
                String confirmInputStr = confirmInput.getText().toString().trim();
                String et_mobileNoStr2 = et_mobileNo.getText().toString().trim();

                if (TextUtils.isEmpty(confirmInputStr)) {
                    Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!MyTextUtils.isInteger(et_mobileNo.getText().toString())
                        || !MyTextUtils.isInteger(confirmInput.getText().toString())) {
                    Toast.makeText(this, "电话号码或输入有误", Toast.LENGTH_LONG).show();
                    return;
                }

                //开启线程阻塞ui
                progressDialog = ProgressDialog.show(RegisterConfirmActivity.this, "", "正请稍后......");
                //提交验证码
                new SubmitVerificationCodeThread(et_mobileNoStr2, confirmInputStr).start();
                //SMSSDK.submitVerificationCode("86", et_mobileNoStr2, confirmInputStr);
                break;
        }

    }

    class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long second = millisUntilFinished / 1000;
            btn_send_code.setText(second + "秒后重新获取");
            Log.i("PDA", millisUntilFinished / 1000 + "");

        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            btn_send_code.setClickable(true);
            btn_send_code.setText("重新发送");
        }

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("result/event：", "result= " + result + " and event= " + event);
            progressDialog.dismiss();
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    Toast.makeText(context, "验证成功", Toast.LENGTH_SHORT).show();

                    //验证成功到下一个界面
                    Intent intent = new Intent(RegisterConfirmActivity.this, RegisterPsdConfirm.class);
                    Bundle bundle = new Bundle();
                    phone = et_mobileNo.getText().toString().trim();
                    bundle.putString("phone", phone);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    RegisterConfirmActivity.this.finish();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(context, "验证码正在发送", Toast.LENGTH_SHORT).show();
                }

            } else if (result == SMSSDK.RESULT_ERROR) {
                Toast.makeText(context, "电话号码或验证码发生错误", Toast.LENGTH_SHORT).show();
            } else {
                ((Throwable) data).printStackTrace();
                int resId = getStringRes(context, "smssdk_network_error");
                Toast.makeText(context, "发生错误，请检查网络", Toast.LENGTH_SHORT).show();
                if (resId > 0) {
                    Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    //提交验证码线程
    class SubmitVerificationCodeThread extends Thread {

        public String phoneNum;
        public String confirmNum;

        SubmitVerificationCodeThread() {
        }


        SubmitVerificationCodeThread(String phoneNum, String confirmNum) {
            this.confirmNum = confirmNum;
            this.phoneNum = phoneNum;
        }

        public void run() {
            Log.i("提交信息：", phoneNum + "+" + confirmNum);
            SMSSDK.submitVerificationCode("86", phoneNum, confirmNum);
        }
    }


    //发送验证码线程
    class GetVerificationCodeThread extends Thread {

        public String et_mobileNoStr;

        GetVerificationCodeThread() {
        }


        GetVerificationCodeThread(String et_mobileNoStr) {
            this.et_mobileNoStr = et_mobileNoStr;
        }

        public void run() {
            SMSSDK.getVerificationCode("86", et_mobileNoStr);
        }
    }


}
