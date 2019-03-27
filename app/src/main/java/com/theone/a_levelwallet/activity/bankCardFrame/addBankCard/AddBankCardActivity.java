package com.theone.a_levelwallet.activity.bankCardFrame.addBankCard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.bankCardFrame.BankCardMainFrameActivity;

public class AddBankCardActivity extends Activity implements View.OnClickListener{

    private EditText bankCardNumber;//输入卡号
    private Button camera,btn_next;//摄像机进行扫描,进入下一页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        init();
    }

    public void init(){
        bankCardNumber = (EditText)findViewById(R.id.et_bankCardNumber);
        camera = (Button)findViewById(R.id.btn_camera);
        camera.setOnClickListener(this);
        btn_next = (Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera:
                //ocr识别
                break;
            case R.id.btn_next:
                if(validate()) {
                    Intent intent = new Intent(AddBankCardActivity.this, BankCardInfoActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(AddBankCardActivity.this, "抱歉，卡不正确", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public boolean validate(){
        String cardNum = bankCardNumber.getText().toString();

        //判断银行卡是否正确以及卡的类型名称
        return true;
    }
}
