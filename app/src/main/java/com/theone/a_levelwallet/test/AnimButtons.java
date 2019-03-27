package com.theone.a_levelwallet.test;

import android.R.anim;
import android.content.Context;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.theone.a_levelwallet.R;



public class AnimButtons extends RelativeLayout{

	private Context context;
	private int leftMargin=0,bottomMargin=0;
	private final int buttonWidth=100;
	private final int r=400;
	private final int maxTimeSpent=200;
	private final int minTimeSpent=80;
	private int intervalTimeSpent;
	private Button[] btns;
	private Button btn_menu;
	private RelativeLayout.LayoutParams params;
	private boolean isOpen = false;
	private float angle;
	private boolean isClick = false;
	public AnimButtons(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	public AnimButtons(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context=context;
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		View view=LayoutInflater.from(context).inflate(R.layout.anim_buttons, this);
		initButtons(view);

	}

	private void initButtons(View view){
		// TODO Auto-generated method stub
		btns=new Button[4];
		btns[0] = (Button) view.findViewById(R.id.btn_add_bankcard);
		btns[1] = (Button) view.findViewById(R.id.btn_del_bankcard);
		btns[2] = (Button) view.findViewById(R.id.btn_take_money);
		btns[3] = (Button) view.findViewById(R.id.btn_qrcode);
		btn_menu = (Button) view.findViewById(R.id.btn_menu);

		leftMargin=((RelativeLayout.LayoutParams)(btn_menu.getLayoutParams())).leftMargin;
		bottomMargin=((RelativeLayout.LayoutParams)(btn_menu.getLayoutParams())).bottomMargin;

		for(int i=0;i<btns.length;i++){
			btns[i].setLayoutParams(btn_menu.getLayoutParams());
			btns[i].setTag(String.valueOf(i));
			btns[i].setOnClickListener(clickListener);
		}

		intervalTimeSpent=(maxTimeSpent-minTimeSpent)/btns.length;
		angle=(float)Math.PI/(2*(btns.length-1));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		final int bottomMargins=this.getMeasuredHeight()-buttonWidth-bottomMargin;
		btn_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isOpen){
					isOpen = true;

					for(int i=0;i<btns.length;i++){
						float xLenth=(float)(r*Math.sin(i*angle));
						float yLenth=(float)(r*Math.cos(i*angle));
						btns[i].startAnimation(animTranslate(xLenth, -yLenth, leftMargin+(int)xLenth, bottomMargins - (int)yLenth, btns[i], minTimeSpent+i*intervalTimeSpent));
					}

				}
				else{
					isOpen = false;
					for(int i=0;i<btns.length;i++){
						float xLenth=(float)(r*Math.sin(i*angle));
						float yLenth=(float)(r*Math.cos(i*angle));
						btns[i].startAnimation(animTranslate(-xLenth, yLenth, leftMargin, bottomMargins, btns[i], maxTimeSpent-i*intervalTimeSpent));
					}
				}

			}
		});

	}
	private Animation animScale(float toX, float toY){
		Animation animation = new ScaleAnimation(1.0f, toX, 1.0f, toY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(context, anim.accelerate_decelerate_interpolator);
		animation.setDuration(400);
		animation.setFillAfter(false);
		return animation;

	}

	private Animation animTranslate(float toX, float toY, final int lastX, final int lastY,
									final Button button, long durationMillis){
		// TODO Auto-generated method stub
		Animation animation = new TranslateAnimation(0, toX, 0, toY);
		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationStart(Animation animation){
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation){
				// TODO Auto-generated method stub
				params = new RelativeLayout.LayoutParams(0, 0);
				params.height = buttonWidth;
				params.width = buttonWidth;
				params.setMargins(lastX, lastY, 0, 0);
				button.setLayoutParams(params);
				button.clearAnimation();

			}
		});
		animation.setDuration(durationMillis);
		return animation;
	}

	View.OnClickListener clickListener=new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int selectedItem=Integer.parseInt((String)v.getTag());
			for(int i=0;i<btns.length;i++){
				if(i==selectedItem){
					btns[i].startAnimation(animScale(2.0f, 2.0f));
				}else{
					btns[i].startAnimation(animScale(0.0f, 0.0f));
				}
			}
			if(onButtonClickListener!=null){
				onButtonClickListener.onButtonClick(v, selectedItem);
			}
		}

	};

	public boolean isOpen(){
		return isOpen;
	}

	public boolean isClick(){
		return isClick;
	}

	private OnButtonClickListener onButtonClickListener;
	public interface OnButtonClickListener{
		void onButtonClick(View v,int id);
	}
	public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener){
		this.onButtonClickListener=onButtonClickListener;
	}
}
