package com.theone.a_levelwallet.activity.loginAndRegister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.lockPatten.locus.LocusMainActivity;
import com.theone.a_levelwallet.activity.mainFrame.MainFrame;

/**
 * Created by lh on 2015/8/18.
 */
public class RegisterFinish extends Activity implements View.OnClickListener{
    private Button bt_register_success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_finish);
        bt_register_success = (Button)findViewById(R.id.btn_registerOK);
        bt_register_success.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_registerOK:
                Intent intent = new Intent(RegisterFinish.this,LocusMainActivity.class);
                startActivity(intent);
                RegisterFinish.this.finish();
                break;
            case R.id.btn_title_left:
                Intent intent1 = new Intent(RegisterFinish.this,LoginActivity.class);
                startActivity(intent1);
                RegisterFinish.this.finish();
                break;
        }

    }
}
