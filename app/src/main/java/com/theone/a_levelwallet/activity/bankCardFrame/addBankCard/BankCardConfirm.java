package com.theone.a_levelwallet.activity.bankCardFrame.addBankCard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.bankCardFrame.BankCardMainFrameActivity;

public class BankCardConfirm extends Activity {

    private Button confirm;//确认添加
    private EditText code;//验证码
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_confirm);
        init();
    }

    public void init(){
        context = this;
        confirm = (Button)findViewById(R.id.btn_next);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "添加成功！", Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(BankCardConfirm.this,BankCardMainFrameActivity.class);
                     startActivity(intent);
                     finish();
            }
        });
    }
}
