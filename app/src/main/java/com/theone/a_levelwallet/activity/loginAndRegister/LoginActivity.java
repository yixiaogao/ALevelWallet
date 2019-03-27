package com.theone.a_levelwallet.activity.loginAndRegister;

/**
 * Created by lh on 2015/8/18.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.lockPatten.locus.LocusMainActivity;
import com.theone.a_levelwallet.utils.HttpUtil;
import com.theone.a_levelwallet.utils.RSAUtils;
import com.theone.a_levelwallet.utils.TokenUtil;

public class LoginActivity extends Activity implements OnClickListener {

    private EditText username;//用户姓名
    private Button btn_login_regist;//注册
    private Button btn_confirmReg;//登陆
    private EditText password;//登陆密码
    private MyHandler myHandler;//线程传递信息
    private CheckBox checkbox;
    private SharedPreferences sp;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            showDialog("用户名或密码错误，请重新输入");

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        initView();

        //判断记住密码多选框的状态
        if (!sp.getBoolean("ISCHECK", false)) {
            //设置默认是记录密码状态
            checkbox.setChecked(true);
            username.setText(sp.getString("USER_NAME", ""));
            password.setText(sp.getString("PASSWORD", ""));
        }
    }

    private void initView() {
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        myHandler = new MyHandler();
        btn_confirmReg = (Button) findViewById(R.id.btn_confirmReg);
        btn_confirmReg.setOnClickListener(this);
        btn_login_regist = (Button) findViewById(R.id.btn_login_regist);
        btn_login_regist.setOnClickListener(this);
        username = (EditText) findViewById(R.id.et_userName);
        password = (EditText) findViewById(R.id.et_alW_Pwd);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkbox.isChecked()) {
                    sp.edit().putBoolean("ISCHECK", true).commit();

                } else {
                    sp.edit().putBoolean("ISCHECK", false).commit();

                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_regist:
                Intent intent = new Intent(LoginActivity.this, RegisterConfirmActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_confirmReg:
                if (!validate()) {
                    return;
                }

                final ProgressDialog myDialog = ProgressDialog.show(LoginActivity.this, "", "正在登录,请稍后..", true, false);
                //登录
                new Thread() {
                    @Override
                    public void run() {
                        if (login()) {
                            myDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, LocusMainActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        } else {
                            myDialog.dismiss();
                            myHandler.sendEmptyMessage(1);
                        }
                    }
                }.start();
        }
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

    /* 对用户名和密码进行非空验证 */
    private boolean validate() {
        String name = username.getText().toString();
        if (name.equals("")) {
            showDialog("用户名不能为空");
            return false;
        }
        String pwd = password.getText().toString();
        if (pwd.equals("")) {
            showDialog("密码不能为空");
            return false;
        }
        return true;
    }

    /* 发送查询请求 */
    private String query(String username, String password) {
        // SQL字符串
        String queryString = "phoneNumber=" + username + "&password=" + password;
        // 查询的URL
        String url = HttpUtil.URL + "login?" + queryString;
        Log.i("url", url);
        //查询并返回结果
        return HttpUtil.queryStringForPost(url);
    }

    /* 登录 */
    private boolean login() {
        String name = username.getText().toString().trim();
        String pwd = password.getText().toString().trim();
        //返回查询结果
        String rsaPsd = RSAUtils.encrypt(pwd);
        System.out.println(rsaPsd);
        String result = query(name, rsaPsd);
        Log.i("result", result);
        //对查询的结果进行判断
        if (result != null && result.equals("1")) {
            SharedPreferences.Editor editor = sp.edit();
            if (checkbox.isChecked()) {
                editor.putBoolean("ISLOGIN", true);
            }

            //记住用户名、密码、
            editor.putString("USER_NAME", name);
            editor.putString("PASSWORD", pwd);
            editor.commit();
            TokenUtil tokenUtil = new TokenUtil();
            tokenUtil.writetoken(this, tokenUtil.getToken(name));
            System.out.println("token result" + tokenUtil.token);
            return true;
        }
        return false;
    }
}

