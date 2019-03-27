package com.theone.a_levelwallet.activity.businessCardFrame;

import com.hanvon.HWCloudManager;
import com.hanvon.utils.BitmapUtil;
import com.theone.a_levelwallet.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BSCardRGActivity extends Activity {

    private Button button1;
    private Button button2;
    private ImageView iv_image;
    private TextView testView;
    private ProgressDialog pd;
    private DiscernHandler discernHandler;

    String picPath = null;
    String result = null;
    private HWCloudManager hwCloudManagerBcard; //名片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //remove title bar
        setContentView(R.layout.activity_bscard_rg);

        hwCloudManagerBcard = new HWCloudManager(this, "4f0836f5-529c-4e82-9be3-e623422560b6");

        discernHandler = new DiscernHandler();

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        testView = (TextView) findViewById(R.id.result);

        MyOnClickListener listener=new MyOnClickListener();
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
    }
    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button1:
                    // 激活系统图库，选择一张图片
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 0);
                    break;

                case R.id.button2:
                    //识别
                    testView.setText("");
                    pd = ProgressDialog.show(BSCardRGActivity.this, "", "正在识别请稍后......");
                    DiscernThread discernThread = new DiscernThread();
                    new Thread(discernThread).start();
                    break;
            }
        }
    }


    public class DiscernThread implements Runnable{

        @Override
        public void run() {
            try {
                /**
                 * 调用汉王云名片识别方法
                 */
                result = hwCloudManagerBcard.cardLanguage("chns", picPath);
//				result = hwCloudManagerBcard.cardLanguage4Https("chns", picPath);
            } catch (Exception e) {
                // TODO: handle exception
            }
            Bundle mBundle = new Bundle();
            mBundle.putString("responce", result);
            Message msg = new Message();
            msg.setData(mBundle);
            discernHandler.sendMessage(msg);
        }
    }

    public class DiscernHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pd.dismiss();
            Bundle bundle = msg.getData();
            String responce = bundle.getString("responce");
            System.out.println(responce);
            String str=new BSCardUtils(responce).toString();
            testView.setText(str);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            //通过uri获取图片路径
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            picPath = cursor.getString(column_index);
            System.out.println(picPath);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picPath, options);
            options.inSampleSize = BitmapUtil.calculateInSampleSize(options, 1280, 720);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);
            iv_image.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}