package com.theone.a_levelwallet.activity.accountSetting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.lockPatten.locus.SetPasswordActivity;

/**
 * Created by lh on 2015/8/26.
 */
public class AccountInfoActivity extends Activity implements View.OnClickListener{

    final Context context = this;
    private TextView tv_account_name;//显示姓名
    private TextView tv_account_mobilNo;//显示手机号
    private TextView tv_account_IDNo;//显示身份证号
    private Button btn_loginpsd_change;//登陆密码修改
    private Button btn_locuspsd_change;//手势密码修改
    private Button btn_new_mobilNo;//手机号码修改
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_account_setting_frame);
        init();
    }

    public void init(){
        tv_account_name = (TextView)findViewById(R.id.tv_account_name);
        tv_account_mobilNo = (TextView)findViewById(R.id.tv_account_mobilNo);
        tv_account_IDNo = (TextView)findViewById(R.id.tv_account_IDNo);
        btn_loginpsd_change = (Button)findViewById(R.id.btn_loginpsd_change);
        btn_loginpsd_change.setOnClickListener(this);
        btn_locuspsd_change = (Button)findViewById(R.id.btn_locuspsd_change);
        btn_locuspsd_change.setOnClickListener(this);
        btn_new_mobilNo = (Button)findViewById(R.id.btn_new_mobilNo);
        btn_new_mobilNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_loginpsd_change:
                dialog_loginpsdChange();
                break;
            case R.id.btn_locuspsd_change:
                Intent intent = new Intent(AccountInfoActivity.this, SetPasswordActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_new_mobilNo:
                dialog_newMobilNo();
                break;
        }

    }

    public void dialog_loginpsdChange(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setView(inflater.inflate(R.layout.dialog_loginpsd_change,null)).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        builder.show();
    }

    public void dialog_newMobilNo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setView(inflater.inflate(R.layout.dialog_mobilno_change,null)).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        builder.show();

    }
}
