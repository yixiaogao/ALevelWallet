package com.theone.a_levelwallet.activity.businessCardFrame;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.businessCardFrame.flip.FlipViewGroup;

/**
 * Created by lh on 2015/8/22.
 */
public class BusinessCardMainFrameActivity extends Activity {
    private FlipViewGroup contentView;
    private Button businesscardaddbt;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card_frame);
        contentView = (FlipViewGroup)findViewById(R.id.flipviewgroup);

        contentView.addFlipView(View.inflate(this, R.layout.business_card_frame_second_page, null));
        contentView.addFlipView(View.inflate(this, R.layout.business_card_frame_first_page, null));
        contentView.addFlipView(View.inflate(this, R.layout.business_card_frame_second_page, null));
        contentView.addFlipView(View.inflate(this, R.layout.business_card_frame_first_page, null));
        contentView.startFlipping(); //make the first_page view flipping

        businesscardaddbt=(Button)findViewById(R.id.businesscardaddbt);
        MyOnClickListener l=new MyOnClickListener();
        businesscardaddbt.setOnClickListener(l);
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.businesscardaddbt){
                Intent intent = new Intent(BusinessCardMainFrameActivity.this, BSCardRGActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        contentView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        contentView.onPause();
    }
}
