package com.theone.a_levelwallet.activity.bankCardFrame.addBankCard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.theone.a_levelwallet.R;

public class BankCardInfoActivity extends Activity {

    private TextView bankCardName,userName,cardType;//卡名，卡的开户者名，卡种类
    private Button next;//跳转下一个页面
    private EditText phone;//银行卡绑定的手机号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_info);
        init();
    }

    public void init(){
        bankCardName = (TextView)findViewById(R.id.tv_bankCardName);
        userName = (TextView)findViewById(R.id.tv_bankUserName);
        cardType = (TextView)findViewById(R.id.tv_bankCardType);
        phone = (EditText)findViewById(R.id.et_telNumber);
        next = (Button)findViewById(R.id.btn_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    Intent intent = new Intent(BankCardInfoActivity.this, BankCardConfirm.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(BankCardInfoActivity.this, "抱歉,手机号不正确", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean check() {
        String phoneNum = phone.getText().toString();
        //验证是否为绑定的银行卡，并发送验证码
        return true;
    }
}
