package com.theone.a_levelwallet.activity.welcome;

/**
 * Created by lh on 2015/8/18.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.lockPatten.locus.LocusMainActivity;

public class WelcomeActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    SharedPreferences preferences;//记录是否第一次登入app

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);

        final Intent intent = new Intent(WelcomeActivity.this, PicturesForWelcome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Intent intent1 = new Intent(WelcomeActivity.this, LocusMainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(2000);

                    //读取SharedPreferences中需要的数据
                    preferences = getSharedPreferences("count", MODE_WORLD_READABLE);
                    int count = preferences.getInt("count", 0);
                    //判断程序与第几次运行，如果是第一次运行则跳转到引导页面
                    if (count == 0) {
                        getApplicationContext().startActivity(intent);
                        finish();
                    } else {
                        getApplicationContext().startActivity(intent1);
                        finish();
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    //存入数据
                    editor.putInt("count", ++count);
                    //提交修改
                    editor.commit();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();

    }

}
