package com.theone.a_levelwallet.activity.bankCardFrame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.loginAndRegister.RegisterPsdConfirm;
import com.theone.a_levelwallet.utils.MyTextUtils;
import com.theone.a_levelwallet.utils.SMSUtil;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static cn.smssdk.framework.utils.R.getStringRes;

public class CashTaking extends Activity implements View.OnClickListener {

    private String cardNumber[];//卡号数组
    private String time[];//有效时间选择
    private Spinner s_cardNo, s_timeLimit;//银行卡号选择,有效时间选择
    private Button btn_confirmInfo, btn_address_search;//确认取现信息,搜索地址
    private EditText cashNumber;//取现金额输入
    private TextView tv_dyPsd, tv_address;//银行回复的动态密码显示，取现地址显示
    private Context context;    //activity context
    private MyHandler myHandler;    //线程消息传递
    private MyCount mc;     //时间限制
    private ProgressDialog progressDialog;  //信息提示对话框
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_taking);
        init();

    }

    public void init() {
        context = getApplication();
        cardNumber = new String[]{"6222020003412536781", "6222020003412536781"};
        time = new String[]{"30分钟", "一个小时", "2个小时", "5个小时"};
        s_cardNo = (Spinner) findViewById(R.id.s_cardNo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cardNumber);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_cardNo.setAdapter(adapter);
        s_cardNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        s_timeLimit = (Spinner) findViewById(R.id.s_timeLimit);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, time);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_timeLimit.setAdapter(adapter1);
        s_timeLimit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cashNumber = (EditText) findViewById(R.id.et_cashNum);
        tv_dyPsd = (TextView) findViewById(R.id.tv_dyPsd);
        tv_address = (TextView) findViewById(R.id.tv_address);
        btn_confirmInfo = (Button) findViewById(R.id.btn_confirmInfo);
        btn_confirmInfo.setOnClickListener(this);
        btn_address_search = (Button) findViewById(R.id.btn_address_search);
        btn_address_search.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_address_search:
                startActivityForResult(new Intent(CashTaking.this, PoiSearchActivity.class), 1);
                break;
            case R.id.btn_confirmInfo:
                sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                String et_mobileNoStr = sp.getString("USER_NAME", "");
                System.out.println(et_mobileNoStr);
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
                btn_confirmInfo.setClickable(false);//按钮设置为不可编辑

                //开启线程阻塞ui
                //progressDialog = ProgressDialog.show(context, "", "正在提交请稍后......");

                //发送获得验证码请求
                new GetVerificationCodeThread(et_mobileNoStr).start();
                // SMSSDK.getVerificationCode("86", et_mobileNoStr);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = data.getExtras().getString("result");//得到新Activity关闭后返回的数据
        Log.e("result", "result:" + result);
        tv_address.setText(result);
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
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
            //progressDialog.dismiss();
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    Toast.makeText(context, "验证成功", Toast.LENGTH_SHORT).show();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(context, "验证码正在发送", Toast.LENGTH_SHORT).show();
                    tv_dyPsd.setText("查收手机短信，请不要将信息泄露信息给别人，取款有限时间为一个小时，银行会随意隔时间给你发最新动态码，" +
                            "请关注并在取款时输入最终动态码，谢谢合作！");
                }

            } else if (result == SMSSDK.RESULT_ERROR) {
                Toast.makeText(context, "电话号码发生错误", Toast.LENGTH_SHORT).show();
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

    class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long second = millisUntilFinished / 1000;
            btn_confirmInfo.setText(second + "秒后重新获取");
            Log.i("PDA", millisUntilFinished / 1000 + "");

        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            btn_confirmInfo.setClickable(true);
            btn_confirmInfo.setText("重新发送");
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
