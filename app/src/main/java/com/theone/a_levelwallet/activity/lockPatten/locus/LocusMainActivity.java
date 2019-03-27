package com.theone.a_levelwallet.activity.lockPatten.locus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.mainFrame.MainFrame;

/**
 * Created by lh on 2015/8/26.
 */
public class LocusMainActivity extends Activity{

    private LocusPassWordView lpwv;
    private Toast toast;

    private void showToast(CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }

        toast.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_locus_psd);
        lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
        lpwv.setOnCompleteListener(new LocusPassWordView.OnCompleteListener() {
            @Override
            public void onComplete(String mPassword) {
                if (lpwv.verifyPassword(mPassword)) {
                    showToast("成功解锁");
                    Intent intent = new Intent(LocusMainActivity.this,
                            MainFrame.class);
                    startActivity(intent);
                    finish();
                } else {
                    showToast("输入有误，请重新输入");
                    lpwv.clearPassword();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        View noSetPassword = (View) this.findViewById(R.id.tvNoSetPassword);
        if (lpwv.isPasswordEmpty()) {
           lpwv.setVisibility(View.GONE);
            noSetPassword.setVisibility(View.VISIBLE);
            noSetPassword.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LocusMainActivity.this,
                            SetPasswordActivity.class);
                    startActivity(intent);
                    finish();
                }

            });
        } else {
            lpwv.setVisibility(View.VISIBLE);
            noSetPassword.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
