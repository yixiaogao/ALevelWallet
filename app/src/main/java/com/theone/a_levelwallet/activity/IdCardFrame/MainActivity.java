package com.theone.a_levelwallet.activity.IdCardFrame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hanvon.HWCloudManager;
import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils.BitmapUtil;

import java.io.File;

import com.alibaba.fastjson.JSON;
import com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils.Client;
import com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils.Const;
import com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils.FaceDetectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final String HwCloudIdcardAppKey = "4f0836f5-529c-4e82-9be3-e623422560b6";
    private static final String TEMP_HEAD_IMAGE = "temp_head_image.jpg"; /* 身份证正面文件名 */
    private static final String TEMP_BACK_IMAGE = "temp_back_image.jpg"; /* 身份证背面文件名 */
    private static final String FILE_DERECTORY = "/AlevelWallet/temp"; /* 身份证背面文件名 */
    private static final int CODE_CAMERA_REQUEST = 0xa1;//相机拍摄图片的识别码
    private static final int CODE_CROP_REQUEST = 0xa2;//剪裁图片识别码
    private static final int CODE_DiscernThread_REQUEST = 0xa3;//身份证信息识别线程消息的识别码
    private static final int CODE_FaceDetectThread_REQUEST = 0xa4;//人脸识别线程消息的识别码

    private ProgressDialog progressDialog;          //信息提示对话框
    private MyHandler myHandler;//线程消息传递

    private Button nextbt;//下一步按钮
    private ImageView idcardfront;//身份证正面照
    private ImageView idcardback;//身份证背面照
    private ImageView idcardfront_background;//身份证正面照背景
    private ImageView idcardback_background;//身份证背面照背景
    private Uri idcardFrontUri;
    private Uri idcardBackUri;
    private int option = 0;           //身份证0为选择正面,否则为背面
    private String idcardFrontInfo;
    private String idcardBackInfo;
    private HWCloudManager hwCloudManagerIdcard;

    private boolean isCompareSuccess;//人脸比对成功的判断

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard_main);
        init();
    }

    protected void init() {

        hwCloudManagerIdcard = new HWCloudManager(getApplication(), HwCloudIdcardAppKey);
        idcardfront = (ImageView) findViewById(R.id.img_idcard);
        idcardback = (ImageView) findViewById(R.id.img_idcard_back);
        idcardfront_background = (ImageView) findViewById(R.id.image1);
        idcardback_background = (ImageView) findViewById(R.id.image2);
        nextbt = (Button) findViewById(R.id.nextbt);
        myHandler = new MyHandler();


        //初始化Uri
        String path;
        try {
            path = Environment.getExternalStorageDirectory() + FILE_DERECTORY;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), "没有存储!", Toast.LENGTH_SHORT).show();
            return;
        }
        //创建目录,有目录才能生成uri
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        idcardFrontUri = Uri.fromFile(new File(path, TEMP_HEAD_IMAGE));
        idcardBackUri = Uri.fromFile(new File(path, TEMP_BACK_IMAGE));

        Log.i("idcardFrontUri", idcardFrontUri.toString());
        Log.i("idcardBackUri", idcardBackUri.toString());

        //监听界面
        MyOnClickListener l = new MyOnClickListener();
        idcardfront_background.setOnClickListener(l);
        idcardback_background.setOnClickListener(l);
        nextbt.setOnClickListener(l);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // 用户操作取消，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "操作取消", Toast.LENGTH_SHORT).show();
            Log.i("***************", "操作取消");
            return;
        }

        switch (requestCode) {
            case CODE_CAMERA_REQUEST:
                if (option == 0) {
                    cropRawPhoto(idcardFrontUri);
                } else {
                    cropRawPhoto(idcardBackUri);
                }

                break;

            case CODE_CROP_REQUEST:
                if (intent != null) {
                    if (option == 0) {
                        setImageToHeadView(idcardFrontUri);
                    } else {
                        setImageToHeadView(idcardBackUri);
                    }
                }

                break;

            default:
                Log.e("***************", "未定义操作");
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image1) {
                option = 0;
                startCameraCapture(idcardFrontUri);
            }

            if (v.getId() == R.id.image2) {
                option = 1;
                startCameraCapture(idcardBackUri);
            }

            if (v.getId() == R.id.nextbt) {
                if (idcardFrontInfo == null || idcardBackInfo == null) {
                    Toast.makeText(getApplication(), "未检测到身份信息，请重新拍照", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("idcardFrontInfo", idcardFrontInfo);
                Log.i("idcardBackInfo", idcardBackInfo);

                //Intent传递uri
                Intent intent = new Intent(MainActivity.this, IDCardInfoActivity.class);
                intent.putExtra("idcardFrontUri", idcardFrontUri.toString());
                intent.putExtra("idcardFrontInfo", idcardFrontInfo);
                intent.putExtra("idcardBackInfo", idcardBackInfo);
                startActivity(intent);
            }
        }
    }

    // 启动相机拍摄照片,uri为相片保存路径
    private void startCameraCapture(Uri uri) {
        Log.i("***************", "打开相机");

        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    //裁剪图片
    public void cropRawPhoto(Uri uri) {
        Log.i("***************", "剪裁图片");

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true"); // 设置裁剪
        intent.putExtra("aspectX", 8);   // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectY", 5);
        intent.putExtra("scale", true); //保持比例
        intent.putExtra("return-data", false);//不返回“data”，不能通过data获得bitmap
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//输出方式为Uri
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//      intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CODE_CROP_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置View
     */
    private void setImageToHeadView(Uri uri) {
        if (uri != null) {
            progressDialog = ProgressDialog.show(MainActivity.this, "", "正在识别请稍后......");

            Bitmap bitmap = BitmapUtil.getSmallBitmap(uri.toString());
            if (option == 0) {
                new FaceDetectThread().start();
                Log.i("**************", "身份证人脸识别线程");


                DiscernThread discernThread = new DiscernThread();
                new Thread(discernThread).start();
                Log.i("**************", "身份证信息识别线程");

                idcardfront.setMaxHeight(idcardfront_background.getHeight());
                idcardfront.setImageBitmap(bitmap);
            } else {
                DiscernThread discernThread = new DiscernThread();
                new Thread(discernThread).start();
                Log.i("**************", "身份证信息识别线程");

                idcardback.setMaxHeight(idcardback_background.getHeight());
                idcardback.setImageBitmap(bitmap);
            }
        }

    }

    //人脸识别线程
    class FaceDetectThread extends Thread {
        public void run() {
            Uri uri;
            if (option == 0) {
                uri = idcardFrontUri;
            } else {
                uri = idcardBackUri;
            }

            Bitmap bitmap = BitmapUtil.getSmallBitmap(uri.toString());
            if (bitmap != null) {
                //人脸数
                int mNumberOfFaceDetected = 0;
                // 图片Base64编码
                String faceImageBase64Str = BitmapUtil.bitmaptoString(bitmap);
                // 创建人脸检测对象
                FaceDetectRequest faceDetectRequest = new FaceDetectRequest();
                faceDetectRequest.setFaceImage(faceImageBase64Str);
                // 序列化人脸检测对象
                String s = JSON.toJSONString(faceDetectRequest);

                try {
                    Client client = new Client();
                    // 请求数据
                    String str= client.PostMethod(Const.FaceDeteiveUrl, s);
                    JSONObject jsonObj = new JSONObject(str);
                    //System.out.println(jsonObj);
                    JSONArray facemodels = (JSONArray) jsonObj.get("facemodels");
                    mNumberOfFaceDetected = facemodels.length();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Bundle mBundle = new Bundle();
                mBundle.putInt("mNumberOfFaceDetected", mNumberOfFaceDetected);
                Message msg = new Message();
                msg.setData(mBundle);
                msg.what = CODE_FaceDetectThread_REQUEST;
                myHandler.sendMessage(msg);
            }

        }
    }

    public class DiscernThread implements Runnable {

        @Override
        public void run() {

            Uri uri;
            if (option == 0) {
                uri = idcardFrontUri;
            } else {
                uri = idcardBackUri;
            }
            String picPath = uri.toString().replace("file://", "");
            Log.i("picPath", picPath);
            String result = null;
            try {
                result = hwCloudManagerIdcard.idCardLanguage(picPath);
                //result = hwCloudManagerIdcard.idCardLanguage4Https(picPath);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("picPath", picPath);
            }

            Bundle mBundle = new Bundle();
            mBundle.putString("responce", result);
            Message msg = new Message();
            msg.setData(mBundle);
            msg.what = CODE_DiscernThread_REQUEST;
            myHandler.sendMessage(msg);
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case CODE_FaceDetectThread_REQUEST:
                    Bundle mbundle = msg.getData();
                    int mNumberOfFaceDetected = mbundle.getInt("mNumberOfFaceDetected");
                    Log.i("mNumberOfFaceDetected", mNumberOfFaceDetected + "");
                    if (mNumberOfFaceDetected == 0) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplication(), "未检测到身份证人脸，请重新拍照", Toast.LENGTH_LONG).show();
                    }
                    break;

                case CODE_DiscernThread_REQUEST:
                    Bundle bundle = msg.getData();
                    String responce = bundle.getString("responce");
                    Log.i("responce", responce);
                    if (option == 0) {
                        idcardFrontInfo = responce;
                    } else {
                        idcardBackInfo = responce;
                    }
                    progressDialog.dismiss();
                    break;
            }
        }
    }

}
