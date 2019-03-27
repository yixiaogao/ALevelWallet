package com.theone.a_levelwallet.activity.mainFrame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.theone.a_levelwallet.R;

public class SearchActivity extends Activity implements View.OnClickListener{
    private Button search;//查询按钮
    private TextView tv_result,search_card;//结果展示和卡号输入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    public void init(){
        search = (Button)findViewById(R.id.btn_search);
        search.setOnClickListener(this);
        tv_result = (TextView)findViewById(R.id.tv_result);
        search_card =  (TextView)findViewById(R.id.search_card);
    }

    @Override
    public void onClick(View v) {
        String cardNumer = search_card.getText().toString();
        int result = query(cardNumer);
        if(result > 0){
            show();
        }
        else
        {
            Toast.makeText(this, "此卡号未存在！", Toast.LENGTH_SHORT).show();
        }
    }

    public int query(String number){
      return 0;
    }

    public void show(){

    }
}
