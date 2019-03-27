package com.theone.a_levelwallet.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.theone.a_levelwallet.R;

public class AnimButtonsActivity extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        AnimButtons animButtons=(AnimButtons)findViewById(R.id.animButtons);
        animButtons.setOnButtonClickListener(new AnimButtons.OnButtonClickListener() {
			
			@Override
			public void onButtonClick(View v, int id) {
				// TODO Auto-generated method stub
			}
		});
        
    }
	
}