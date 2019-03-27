package com.theone.a_levelwallet.activity.IdCardFrame;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class IDCardInfoActivity extends Activity {

    private String idcardFrontInfo;
    private String idcardBackInfo;
    private EditText nametx;
    private EditText gendertx;
    private EditText peopletx;
    private EditText birthtx;
    private EditText addresstx;
    private EditText idnumbertx;
    private EditText authoritytx;
    private EditText validdatetx;
    private Button nextbt;
    private Button showphotobt;
    private Button rerecognizebt;
    private Button getticketbt;
    private boolean isCompareSuccess;//人脸比对成功的判断
    private Context context;
    private SharedPreferences sp;
    private ProgressDialog myDialog;
    MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard_info);
        context = this;
        //获取全局信息
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);

        try {
            Intent intent = getIntent();
            idcardFrontInfo = intent.getStringExtra("idcardFrontInfo");
            idcardBackInfo = intent.getStringExtra("idcardBackInfo");
            isCompareSuccess = intent.getBooleanExtra("isCompareSuccess", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //检测是否已识别,否则从数据库获取信息，若无重新识别
        myHandler = new MyHandler();
        if (!isCompareSuccess && idcardFrontInfo == null && idcardBackInfo == null) {
            myDialog = ProgressDialog.show(context, "", "正在获取身份证信息,请稍后..", true, false);
            new GetIdcardInfoThread().start();
        }

        initView();
    }

    protected void initView() {
        nametx = (EditText) findViewById(R.id.id_name);
        gendertx = (EditText) findViewById(R.id.id_gender);
        peopletx = (EditText) findViewById(R.id.id_people);
        birthtx = (EditText) findViewById(R.id.id_birth);
        addresstx = (EditText) findViewById(R.id.id_address);
        idnumbertx = (EditText) findViewById(R.id.id_idnumber);
        authoritytx = (EditText) findViewById(R.id.id_authority);
        validdatetx = (EditText) findViewById(R.id.id_validdate);

        showphotobt = (Button) findViewById(R.id.bt_showphoto);
        rerecognizebt = (Button) findViewById(R.id.bt_rerecognize);
        nextbt = (Button) findViewById(R.id.infonextbt);
        getticketbt = (Button) findViewById(R.id.bt_getticket);

        MyOnClickListener l = new MyOnClickListener();
        nextbt.setOnClickListener(l);
        rerecognizebt.setOnClickListener(l);
        showphotobt.setOnClickListener(l);
        getticketbt.setOnClickListener(l);

        freshButton();
        freshIdcardInfo();
    }

    private void freshIdcardInfo() {
        if (!(idcardFrontInfo == null || idcardBackInfo == null)) {
            try {
                //正面
                JSONObject jsonObj = new JSONObject(idcardFrontInfo);
                nametx.setText(jsonObj.getString("name"));
                gendertx.setText(jsonObj.getString("gender") + " ");
                peopletx.setText(jsonObj.getString("people") + " ");
                birthtx.setText(jsonObj.getString("byear") + "年"
                        + jsonObj.getString("bmonth") + "月" + jsonObj.getString("bday") + "日");
                addresstx.setText(jsonObj.getString("address"));
                idnumbertx.setText(jsonObj.getString("idnumber"));

                //背面
                jsonObj = new JSONObject(idcardBackInfo);
                authoritytx.setText(jsonObj.getString("authority"));
                validdatetx.setText(jsonObj.getString("validdate"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("无身份信息");
        }
    }

    private void freshButton() {
        if (isCompareSuccess) {
            showphotobt.setVisibility(View.VISIBLE);
            getticketbt.setVisibility(View.VISIBLE);
            nextbt.setVisibility(View.INVISIBLE);
        } else {
            nextbt.setVisibility(View.VISIBLE);
            showphotobt.setVisibility(View.INVISIBLE);
            getticketbt.setVisibility(View.INVISIBLE);
        }
    }

    //判断是否
    private boolean hasCompared() {
        String phoneNumber = sp.getString("USER_NAME", "");
        String queryString = "phoneNumber=" + phoneNumber;
        // 查询的URL
        String url = HttpUtil.URL + "getIDCardInfo?" + queryString;
        System.out.println("getIDCardInfoUrl:" + url);
        //查询并返回结果
        String idCardContent = HttpUtil.queryStringForPost(url);
        if (idCardContent == null) {
            idCardContent = "";
        }
        if (!idCardContent.equals("")) {
            //将正面背面信息分开
            String[] strarray = idCardContent.split("~");
            System.out.println("idCardContent:" + idCardContent);
            if (strarray.length != 2) {
                return false;
            }
            idcardFrontInfo = strarray[0];
            idcardBackInfo = strarray[1];
            return true;
        } else {
            return false;
        }
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.infonextbt) {
                Intent intent = new Intent(IDCardInfoActivity.this, RecognizeActivity.class);
                intent.putExtra("idcardFrontInfo", idcardFrontInfo);
                intent.putExtra("idcardBackInfo", idcardBackInfo);
                startActivity(intent);
            }
            if (v.getId() == R.id.bt_rerecognize) {
                Intent intent = new Intent(IDCardInfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            if (v.getId() == R.id.bt_showphoto) {
                Intent intent = new Intent(IDCardInfoActivity.this, ShowIDCardPhotoActivity.class);
                //intent.putExtra("isCompareSuccess", isCompareSuccess);
                startActivity(intent);
            }
            if (v.getId() == R.id.bt_getticket) {
                Intent intent = new Intent(IDCardInfoActivity.this, MyQRCordActivity.class);
                startActivity(intent);
            }

        }

    }

    //人脸识别线程
    class GetIdcardInfoThread extends Thread {
        public void run() {
            if (!hasCompared()) {
                myDialog.dismiss();
                Intent intent = new Intent(IDCardInfoActivity.this, MainActivity.class);
                startActivity(intent);
                IDCardInfoActivity.this.finish();
            } else {
                myDialog.dismiss();
                Message msg = new Message();
                msg.what = 10;
                myHandler.sendMessage(msg);
            }
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    isCompareSuccess = true;
                    freshButton();
                    freshIdcardInfo();
                    break;
                default:
            }
        }

    }

}
