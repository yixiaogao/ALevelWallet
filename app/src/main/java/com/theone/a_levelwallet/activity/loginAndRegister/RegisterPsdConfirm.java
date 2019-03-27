package com.theone.a_levelwallet.activity.loginAndRegister;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.utils.HttpUtil;
import com.theone.a_levelwallet.utils.RSAUtils;

/**
 * Created by lh on 2015/8/18.
 */
public class RegisterPsdConfirm extends Activity implements OnClickListener{

    private EditText psdInput;
    private EditText psdConfirm;
    private Button btn_confirmPsd;
    private String phone;//前页面传来的手机信息
    private EditText et_registerName;
    private String name;//输入的姓名
    private  String tempPsd;//密码

    private MyHandler myHandler;//线程传递信息

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            showDialog("服务器响应超时，或用户名已存在");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_psd_input);
        psdInput = (EditText)findViewById(R.id.et_psdinput);
        psdConfirm = (EditText)findViewById(R.id.et_psdConfirm);
        btn_confirmPsd = (Button)findViewById(R.id.btn_confirmPsd);
        btn_confirmPsd.setOnClickListener(this);
        et_registerName = (EditText)findViewById(R.id.et_registerName);
        name = et_registerName.getText().toString();
        Bundle bundle = this.getIntent().getExtras();
        phone = bundle.getString("phone");
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
                tempPsd = psdInput.getText().toString();
        //验证
        if(!validate()) {
            return;
        }
        final ProgressDialog myDialog = ProgressDialog.show(RegisterPsdConfirm.this, "", "正在注册,请稍后..", true, false);
        //登录
        new Thread() {
            @Override
            public void run() {
                if(register())
                {
                    myDialog.dismiss();
                    Intent intent = new Intent(RegisterPsdConfirm.this, RegisterFinish.class);
                    startActivity(intent);
                    RegisterPsdConfirm.this.finish();
                }
                else
                {
                    myDialog.dismiss();
                    myHandler.sendEmptyMessage(1);
                }
            }
        }.start();

    }

    /* 信息提示框 */
    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog ad = builder.create();
        ad.show();
    }

    /* 发送查询请求 */
    private String query(String phoneNumber, String password) {
        // SQL字符串
        String queryString = "phoneNumber=" + phoneNumber + "&password=" + password;
        // 查询的URL
        String url = HttpUtil.URL + "register?" + queryString;
        Log.i("url", url);
        //查询并返回结果
        return HttpUtil.queryStringForPost(url);
    }

    /* 登录 */
    private boolean register() {
        //返回查询结果
        String rsaPsd= RSAUtils.encrypt(tempPsd);
        System.out.println(rsaPsd);
        String result = query(phone,rsaPsd);
        Log.i("result", result);
        //对查询的结果进行判断
        if(result != null && result.equals("1"))
        {
            return true;
        }
        return false;
    }


    /* 对用户名和密码进行非空验证 */
    private boolean validate() {
        String tempConfirm = psdConfirm.getText().toString();
        if (tempPsd.equals("") || !tempPsd.equals(tempConfirm)) {
            Toast.makeText(this, "输入不能为空且需要输入一致", Toast.LENGTH_LONG).show();
            psdInput.setText("");
            psdConfirm.setText("");
            return false;
        }
        return true;
    }

}
