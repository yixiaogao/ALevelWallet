package com.theone.a_levelwallet.activity.IdCardFrame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.theone.a_levelwallet.R;

public class MyQRCordActivity extends AppCompatActivity implements View.OnClickListener{
    private Button qrbackbt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcord);
        qrbackbt= (Button) findViewById(R.id.bt_back_qrcode);
        qrbackbt.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bt_back_qrcode){
            this.finish();
        }
    }
}
