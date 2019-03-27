package com.theone.a_levelwallet.activity.lockPatten.locus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.theone.a_levelwallet.R;
import com.theone.a_levelwallet.activity.lockPatten.util.StringUtil;

public class SetPasswordActivity extends Activity {
	private LocusPassWordView lpwv;
	private String password;
	private boolean needverify = true;
	private Toast toast;

	private void showToast(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}

		toast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_locus_psd);
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new LocusPassWordView.OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				password = mPassword;
			}
		});

		OnClickListener mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tvSave:
						lpwv.resetPassWord(password);
						lpwv.clearPassword();
						Log.i("password", password);
						startActivity(new Intent(SetPasswordActivity.this,
								LocusMainActivity.class));
						showToast("确认密码");
						finish();
					break;
				case R.id.tvReset:
					lpwv.clearPassword();
					break;
				}
			}
		};
		Button buttonSave = (Button) this.findViewById(R.id.tvSave);
		buttonSave.setOnClickListener(mOnClickListener);
		Button tvReset = (Button) this.findViewById(R.id.tvReset);
		tvReset.setOnClickListener(mOnClickListener);
		if (lpwv.isPasswordEmpty()) {
			this.needverify = false;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
