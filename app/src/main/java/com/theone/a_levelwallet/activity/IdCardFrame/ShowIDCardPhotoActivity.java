package com.theone.a_levelwallet.activity.IdCardFrame;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.theone.a_levelwallet.R;


public class ShowIDCardPhotoActivity extends Activity implements View.OnClickListener {

    private Button idcardprintbt;
    private Button idcardprintbackbt;
    private ImageView idcardfront;//身份证正面照
    private ImageView idcardback;//身份证背面照
    private ImageView idcardfront_background;//身份证正面照背景
    private ImageView idcardback_background;//身份证背面照背景

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard_show_photo);
        idcardprintbt = (Button) findViewById(R.id.bt_idcard_print);
        idcardprintbackbt = (Button) findViewById(R.id.bt_idcard_print_back);
        idcardfront = (ImageView) findViewById(R.id.img_idcard_show);
        idcardback = (ImageView) findViewById(R.id.img_idcard_back_show);
        idcardfront_background = (ImageView) findViewById(R.id.imageView1);
        idcardback_background = (ImageView) findViewById(R.id.imageView2);
        idcardfront.setImageURI(Uri.parse(Environment.getExternalStorageDirectory() +
                "/AlevelWallet/temp/" + "temp_head_image.jpg"));
        idcardback.setImageURI(Uri.parse(Environment.getExternalStorageDirectory() +
                "/AlevelWallet/temp/" + "temp_back_image.jpg"));

        idcardprintbt.setOnClickListener(this);
        idcardprintbackbt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_idcard_print) {
            Intent intent = new Intent(ShowIDCardPhotoActivity.this, MyQRCordActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.bt_idcard_print_back) {
            this.finish();
        }
    }
}
