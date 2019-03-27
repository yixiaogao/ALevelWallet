package com.theone.a_levelwallet.activity.IdCardFrame;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils.BitmapUtil;
import com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils.Client;
import com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils.Const;
import com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils.FaceCompareRequest;
import com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils.FaceCompareResult;
import com.theone.a_levelwallet.utils.HttpUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RecognizeActivity extends Activity {

    private String idcardFrontInfo;
    private String idcardBackInfo;

    private Button showInfobt = null;//显示信息按钮
    private Button recognizebt = null;//识别按钮
    private ImageView idcardphoto = null;//身份证正面照
    private ImageView mphoto = null;//自拍照片
    private TextView similaritytv = null;//显示信息
    private Uri idcardFrontUri = null;//前一页面的信息
    private Uri uri1 = null;//身份证头像路径
    private Uri uri2 = null;//自拍头像路径
    private String value;//相似度识别返回值
    private String s;//序列化人脸比较参数
    private MyHandler myHandler;//线程传递信息
    private FaceCompareRequest faceCompareRequest;//人脸信息
    private boolean isCompareSuccess;//人脸比对成功的判断
    private SharedPreferences sp;

    /* 定义识别码 */
    private static final int CODE_CAMERA_REQUEST = 0xa1;//相机拍摄图片的识别码
    private static final int CODE_CROP_REQUEST = 0xa2;//剪裁图片识别码
    private static final int CODE_FaceCompareThread_REQUEST = 0xa3;//人脸识别线程消息的识别码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard_recognize);
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        try {
            Intent intent = getIntent();
            idcardFrontInfo = intent.getStringExtra("idcardFrontInfo");
            idcardBackInfo = intent.getStringExtra("idcardBackInfo");
        } catch (Exception e) {
            e.printStackTrace();
        }

        myHandler = new MyHandler();
        faceCompareRequest = new FaceCompareRequest();

        idcardphoto = (ImageView) findViewById(R.id.photo_1);
        mphoto = (ImageView) findViewById(R.id.photo_2);
        recognizebt = (Button) findViewById(R.id.bt_recognize);
        showInfobt = (Button) findViewById(R.id.bt_showInfo);
        similaritytv = (TextView) findViewById(R.id.textView_value);

        MyOnClickListener l = new MyOnClickListener();
        mphoto.setOnClickListener(l);
        recognizebt.setOnClickListener(l);
        idcardphoto.setOnClickListener(l);
        showInfobt.setOnClickListener(l);
        isCompareSuccess = false;
        idcardFrontUri =Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/AlevelWallet/temp", "temp_head_image.jpg"));

        uri1 = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/AlevelWallet/temp", "photo_1.jpg"));
        uri2 = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/AlevelWallet/temp", "photo_2.jpg"));
        Log.e("Uri", "StorageDirectory:" + Environment.getExternalStorageDirectory() +
                "uri1:" + uri1.toString() + "uri2:" + uri2.toString());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // 用户操作取消，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "操作取消", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case CODE_CROP_REQUEST:
                if (intent != null) {
                    Log.e("uri1.toString()", "uri1.toString()：" + uri1);
                    Bitmap resbitmap = BitmapUtil.getSmallBitmap(uri1.toString());
                    if (resbitmap != null) {
                        idcardphoto.setImageBitmap(resbitmap);
                        idcardphoto.setImageBitmap(resbitmap);
                        // 图片Base64编码
                        String faceImageBase64Str = BitmapUtil.bitmaptoString(resbitmap);
                        // 创建人脸检测对象
                        faceCompareRequest.setFaceimage1(faceImageBase64Str);
                    }
                }
                break;

            case CODE_CAMERA_REQUEST:
                if (uri2 != null) {
                    Bitmap resbitmap = BitmapUtil.getSmallBitmap(uri2.toString());
                    if (resbitmap != null) {
                        mphoto.setImageBitmap(resbitmap);
                        String faceImageBase64Str = BitmapUtil.bitmaptoString(resbitmap);
                        faceCompareRequest.setFaceimage2(faceImageBase64Str);
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
            if (v.getId() == R.id.photo_1) {
                cropRawPhoto(idcardFrontUri);
            }
            if (v.getId() == R.id.photo_2) {
                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri2);
                startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
            }

            if (v.getId() == R.id.bt_recognize) {

                // 序列化人脸检测对象
                s = JSON.toJSONString(faceCompareRequest);

                if (s.equals("") || faceCompareRequest.getFaceimage1().equals("")
                        || faceCompareRequest.getFaceimage2().equals("")) {
                    Toast.makeText(getApplication(), "请选择照片并拍照……", Toast.LENGTH_SHORT).show();
                    return;
                }

                similaritytv.setText("正在识别……");

                new FaceDetectThread().start();
            }

            if (v.getId() == R.id.bt_showInfo) {
                if (!isCompareSuccess) {
                    Toast.makeText(getApplication(), "认证未成功，请重新认证", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (idcardFrontInfo == null || idcardBackInfo == null) {
                    Toast.makeText(getApplication(), "未检测到身份信息，请重新识别", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("idcardFrontInfo", idcardFrontInfo);
                Log.i("idcardBackInfo", idcardBackInfo);

                //Intent传递uri
                Intent intent = new Intent(RecognizeActivity.this, IDCardInfoActivity.class);
                //intent.putExtra("idcardFrontUri", idcardFrontUri.toString());
                intent.putExtra("idcardFrontInfo", idcardFrontInfo);
                intent.putExtra("idcardBackInfo", idcardBackInfo);
                intent.putExtra("isCompareSuccess", isCompareSuccess);
                startActivity(intent);
                finish();
            }
        }
    }

    //裁剪图片
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true"); // 设置裁剪
        intent.putExtra("aspectX", 7);   // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectY", 10);
        intent.putExtra("scale", true); //保持比例
        intent.putExtra("return-data", false);//不返回“data”，不能通过data获得bitmap
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri1);//输出方式为Uri
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//      intent.putExtra("noFaceDetection", true); // no face detection

        Log.e("***************", "剪裁图片");
        startActivityForResult(intent, CODE_CROP_REQUEST);
    }

    //人脸识别线程
    class FaceDetectThread extends Thread {
        public void run() {
            try {
                // 实例化RESTFul客户端
                Client client = new Client();
                // 请求数据
                value = client.PostMethod(Const.FaceCompareUrl, s);

                FaceCompareResult parseObject = JSON.parseObject(value,
                        FaceCompareResult.class);
                int similarity = 0;
                similarity = (int) (parseObject.getSimilar() * 100);
                if (similarity < 85) {
                    similarity = similarity + 10;
                }
                if (similarity >= 80) {
                    //将信息写进数据库
                    //三个参数
                    String updateinforesult = "";
                    try {
                        String phoneNumber = sp.getString("USER_NAME", "");
                        String idCardContent = idcardFrontInfo + "~" + idcardBackInfo;
                        String idCardNumber = new JSONObject(idcardFrontInfo).getString("idnumber");

                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(HttpUtil.URL + "recognize");
                        httpPost.addHeader("charset", HTTP.UTF_8);
                        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                        nvps.add(new BasicNameValuePair("phoneNumber", phoneNumber));
                        nvps.add(new BasicNameValuePair("idCardContent", idCardContent));
                        nvps.add(new BasicNameValuePair("idCardNumber", idCardNumber));
                        httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                        //发送HttpPost请求，并返回HttpResponse对象
                        HttpResponse httpResponse = httpclient.execute(httpPost);
                        // 判断请求响应状态码，状态码为200表示服务端成功响应了客户端的请求
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            //获取返回结果
                            updateinforesult = EntityUtils.toString(httpResponse.getEntity());
                        }

//                        // 查询的URL
//                        String queryString = "phoneNumber=" + phoneNumber + "&idCardContent=" + idCardContent + "&idCardNumber=" + idCardNumber;
//                        String url = HttpUtil.URL + "recognize?" + queryString;
//                        System.out.println("recognizeUrl:" + url);

                        //查询并返回结果
                        //String updateinforesult = HttpUtil.queryStringForPost(url);

                        if (updateinforesult == null) {
                            updateinforesult = "0";
                        }
                        if (updateinforesult.equals("0")) {
                            similarity = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        similarity = 0;
                        System.out.println("请检查网络");
                    }
                }
                System.out.println(parseObject.getSimilar());

                Bundle mBundle = new Bundle();
                mBundle.putInt("similarity", similarity);
                Message msg = new Message();
                msg.setData(mBundle);
                msg.what = CODE_FaceCompareThread_REQUEST;
                myHandler.sendMessage(msg);
            } catch (
                    Exception e
                    )

            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case CODE_FaceCompareThread_REQUEST:
                    Bundle mBundle = msg.getData();
                    int similarity = mBundle.getInt("similarity");
                    similaritytv.setText("相似度：" + similarity + "%");
                    ImageView imageView = (ImageView) findViewById(R.id.img_issuccess);
                    TextView textView = (TextView) findViewById(R.id.textView_issuccess);
                    if (similarity >= 80) {
                        isCompareSuccess = true;
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.pic_comparesusses));
                        textView.setText("认证成功！");
                    } else if (similarity == 0) {
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.pic_comparefailed));
                        textView.setText("认证失败,请检查网络！");
                    } else {
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.pic_comparefailed));
                        textView.setText("认证失败！");
                    }
                    break;
                default:
            }
        }

    }
}