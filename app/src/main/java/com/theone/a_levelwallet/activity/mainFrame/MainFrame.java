package com.theone.a_levelwallet.activity.mainFrame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.IdCardFrame.IDCardInfoActivity;
import com.theone.a_levelwallet.activity.accountSetting.AccountInfoActivity;
import com.theone.a_levelwallet.activity.bankCardFrame.BankCardMainFrameActivity;
import com.theone.a_levelwallet.activity.businessCardFrame.BusinessCardMainFrameActivity;
import com.theone.a_levelwallet.activity.loginAndRegister.LoginActivity;
import com.theone.a_levelwallet.activity.mainFrame.ZxingScan.CaptureActivity;
import com.theone.a_levelwallet.test.TVOffAnimation;
import com.theone.a_levelwallet.utils.TokenUtil;

import cy.rorate3d.view.CYRorateView;
import cy.rorate3d.view.CYRorateViewObserver;

/**
 * Created by lh on 2015/8/18.
 */
public class MainFrame extends Activity implements CYRorateViewObserver, View.OnClickListener {

    private Context context;
    ListView listView;
    CYRorateView myView;//3d旋转视图
    private SlidingLayout slidingLayout;
    private Button btn_enter_cardframe;//进入卡页面
    private Adapter adapter;//翻转效果list的适配器
    private int currentItem;//获得3d翻转效果当前显示的下标
    private ImageView img_scanner, img_anccount_manage, img_log_out, img_search;//二维码扫描,账户管理,注销，搜索
    private long mExitTime;//按两次返回键退出时隔
    private Button addPage, delPage, menuButton;//增加钱包页数,减少页数,右侧菜单按钮
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_frame_slide_groups);
        context = this;
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        final ProgressDialog myDialog = ProgressDialog.show(context, "", "正在获取信息,请稍后..", true, false);

        //检测token是否匹配，是否已经登录
        new Thread() {
            @Override
            public void run() {
                if (!haslogined()) {
                    myDialog.dismiss();
                    Intent intent = new Intent(MainFrame.this, LoginActivity.class);
                    startActivity(intent);
                    MainFrame.this.finish();
                } else {
                    myDialog.dismiss();
                }
            }
        }.start();
        initUI();
    }

    private void initUI() {

        addPage = (Button) findViewById(R.id.Ibtn_addWalletPage);
        addPage.setOnClickListener(this);
        delPage = (Button) findViewById(R.id.Ibtn_deletePage);
        delPage.setOnClickListener(this);
        btn_enter_cardframe = (Button) findViewById(R.id.btn_enter_cardframe);
        btn_enter_cardframe.setOnClickListener(this);
        slidingLayout = (SlidingLayout) findViewById(R.id.slidingLayout);
        img_scanner = (ImageView) findViewById(R.id.img_scanner);
        img_scanner.setOnClickListener(this);
        img_search = (ImageView) findViewById(R.id.img_search);
        img_search.setOnClickListener(this);
        img_log_out = (ImageView) findViewById(R.id.img_log_out);
        img_log_out.setOnClickListener(this);
        img_anccount_manage = (ImageView) findViewById(R.id.img_anccount_manage);
        img_anccount_manage.setOnClickListener(this);
        menuButton = (Button) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(this);
        myView = (CYRorateView) findViewById(R.id.myView);
        View view1 = LayoutInflater.from(this).inflate(R.layout.view1, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.view2, null);
        View view3 = LayoutInflater.from(this).inflate(R.layout.view3, null);
        View view4 = LayoutInflater.from(this).inflate(R.layout.view4, null);

        myView.addView(view1);
        myView.addView(view3);
        myView.addView(view4);
        //事件旋转，默认手势旋转
        myView.rorateToNext();
        myView.rorateToPre();
        //设置旋转角度，默认为90度
        myView.setRoateAngle(90);
        //设置旋转是否循环，默认循环
        myView.setIsNeedCirculate(true);
        listView = (ListView) view2.findViewById(R.id.list);
        adapter = new Adapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(context, arg2 + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //判断是否登录
    private boolean haslogined() {
        String name = sp.getString("USER_NAME", "");
        String tokeninlocal = sp.getString("TOKEN", "");
        TokenUtil tokenUtil = new TokenUtil();
        String token = tokenUtil.getToken(name);
        if (tokeninlocal.equals(token) && !tokeninlocal.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    //按两次返回键退出程序
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //如果侧边栏出来了先弹回侧边栏
            if (slidingLayout.isLeftLayoutVisible()) {
                slidingLayout.scrollToRightLayout();
            } else if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                //关闭效果
                View view = (View) findViewById(R.id.slidingLayout);
                view.startAnimation(new TVOffAnimation());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menuButton:
                if (slidingLayout.isLeftLayoutVisible()) {
                    slidingLayout.scrollToRightLayout();
                } else {
                    slidingLayout.scrollToLeftLayout();
                }
                break;
            case R.id.btn_enter_cardframe:
                listView.getLayoutParams();
                if (currentItem == 1) {
                    //进入银行卡主页面
                    Intent intent = new Intent(MainFrame.this, BankCardMainFrameActivity.class);
                    startActivity(intent);
                } else if (currentItem == 2) {
                    //进入名片主页面
                    Intent intent = new Intent(MainFrame.this, BusinessCardMainFrameActivity.class);
                    startActivity(intent);
                } else if (currentItem == 0) {
                    //进入身份证主页面
                    Intent intent = new Intent(MainFrame.this, IDCardInfoActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "尚未开发，敬请期待", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_scanner:
                Intent intent = new Intent(MainFrame.this, CaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.img_search:
                Intent intent1 = new Intent(MainFrame.this, SearchActivity.class);
                startActivity(intent1);
                break;
            case R.id.img_anccount_manage:
                Intent intent2 = new Intent(MainFrame.this, AccountInfoActivity.class);
                startActivity(intent2);
                break;
            case R.id.Ibtn_addWalletPage:
                // myView.addView(LayoutInflater.from(this).inflate(R.layout.view5, null));
                break;
            case R.id.Ibtn_deletePage:
                if (currentItem > 2) {
                    View view = myView.getChildAt(currentItem);
                    myView.removeView(view);
                } else {
                    Toast.makeText(this, "固有属性，无法删除", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_log_out:
                sp.edit().putBoolean("ISLOGIN", false).commit();
                Intent intent3 = new Intent(MainFrame.this, LoginActivity.class);
                startActivity(intent3);
                finish();
                break;
        }
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listView.getCount();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listView.getItemAtPosition(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return listView.getItemIdAtPosition(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

           /* convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
            TextView txt = (TextView) convertView.findViewById(R.id.txt);
           txt.setText(position+1+"");*/
            return convertView;
        }

    }

    /**
     * 里面有listview之类的好像高度会不对，继承下CYRorateViewMeasuredObserver重新设置高度，
     * 目前只能这样，还没找到什么更好的方法
     */
    @Override
    public void getRorateViewMeasured(int width, int height) {
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height;
        listView.setLayoutParams(layoutParams);

    }

    /**
     * 界面切换
     */
    @Override
    public void getRorateCurrentView(int item) {
        currentItem = item;
        //Toast.makeText(context, "CurrentView"+item , Toast.LENGTH_SHORT).show();
    }


}
