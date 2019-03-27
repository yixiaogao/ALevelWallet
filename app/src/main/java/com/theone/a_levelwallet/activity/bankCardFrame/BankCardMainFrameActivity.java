package com.theone.a_levelwallet.activity.bankCardFrame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.bankCardFrame.addBankCard.AddBankCardActivity;
import com.theone.a_levelwallet.activity.mainFrame.ZxingScan.CaptureActivity;
import com.theone.a_levelwallet.test.AnimButtons;

import java.util.ArrayList;

/**
 * Created by lh on 2015/8/21.
 */
//这里添加按钮点击事件！！！！
public class BankCardMainFrameActivity extends Activity {
    private ListView asyncLoadImageListView; //这个是ListView组件
    private ImageView img_bankcard_show;
    private Button btn_take_money;
    private Button btn_qrcode;
    private long idNUmber[] = new long[]{
            R.drawable.bank3,R.drawable.bank2,R.drawable.bank1
    };

    private int resourse[] = new int[]{
         R.drawable.icbc,R.drawable.abc,R.drawable.boc
    };
    private String urlString[] = new String[]{
            "http://b.hiphotos.baidu.com/album/w%3D2048/sign=6499e88450da81cb4ee684cd665ed116/eac4b74543a98226060b2b578b82b9014b90ebfc.jpg"
            ,"http://f.hiphotos.baidu.com/album/w%3D2048/sign=1f649d5ea9d3fd1f3609a53a0476241f/ac6eddc451da81cba4348dbd5366d016082431eb.jpg"
            ,"http://e.hiphotos.baidu.com/album/w%3D2048/sign=62219409fcfaaf5184e386bfb86c94ee/fc1f4134970a304e89259cd5d0c8a786c9175c9d.jpg"
    };
    private String cardNUmber[] = new String[]{
            "工商银行   6222351234567899","农业银行   6228378888888888","中国银行   621785888888888888"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bankcard_frame_listview);
        listSetting();
        menuSetting();
    }

    public void listSetting(){
        img_bankcard_show = (ImageView)findViewById(R.id.img_bankcard_show);
        asyncLoadImageListView = (ListView)this.findViewById(R.id.listView);
        ArrayList<ImageUrl> urlList = new ArrayList<ImageUrl>(); //这个就是用于显示ListView的数据集合
        for(String temp :urlString){//使用增强的for循环遍历数组
            ImageUrl imag = new ImageUrl(); //这个是一个javabean
            imag.setImageUrl(temp);
            urlList.add(imag);//将这个javabean添加到集合中
        }
        //初始化适配器 （Context ,ArrayList<ImageUrl>,AsyncLoadImage）
        ListViewAsyncAdapter listViewAsyncAdapter = new ListViewAsyncAdapter(getApplicationContext(), urlList,new AsyncLoadImage(),cardNUmber,resourse);
        //给ListView添加适配器
        asyncLoadImageListView.setAdapter(listViewAsyncAdapter);
        asyncLoadImageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                img_bankcard_show.setImageResource((int) (idNUmber[position]));
            }
        });
    }

    public void menuSetting() {
        btn_take_money = (Button) findViewById(R.id.btn_take_money);
        AnimButtons animButtons = (AnimButtons) findViewById(R.id.animButtons);
        animButtons.setOnButtonClickListener(new AnimButtons.OnButtonClickListener() {

            @Override
            public void onButtonClick(View v, int id) {
                switch (id) {
                    case 3:
                        Intent intent3 = new Intent(BankCardMainFrameActivity.this, CaptureActivity.class);
                        startActivity(intent3);
                        break;
                    case 2:
                        Intent intent2 = new Intent(BankCardMainFrameActivity.this, CashTaking.class);
                        startActivity(intent2);
                        break;
                    case 0:
                        Intent intent0 = new Intent(BankCardMainFrameActivity.this, AddBankCardActivity.class);
                        startActivity(intent0);
                        break;
                }
            }
        });
    }
}