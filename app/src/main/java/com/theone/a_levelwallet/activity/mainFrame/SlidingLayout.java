package com.theone.a_levelwallet.activity.mainFrame;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class SlidingLayout extends RelativeLayout {


	/**
	 * 屏幕宽度值。
	 */
	private int screenWidth;

	/**
	 * 右侧布局最多可以滑动到的左边缘。
	 */
	private int leftEdge = 0;

	/**
	 * 右侧布局最多可以滑动到的右边缘。
	 */
	private int rightEdge = 0;

	/**
	 * 左侧布局当前是显示还是隐藏。只有完全显示或隐藏时才会更改此值，滑动过程中此值无效。
	 */
	private boolean isLeftLayoutVisible;


	/**
	 * 左侧布局对象。
	 */
	private View leftLayout;

	/**
	 * 右侧布局对象。
	 */
	private View rightLayout;

	/**
	 * 用于监听侧滑事件的View。
	 */
	private View mBindView;

	/**
	 * 左侧布局的参数，通过此参数来重新确定左侧布局的宽度，以及更改leftMargin的值。
	 */
	private MarginLayoutParams leftLayoutParams;

	/**
	 * 右侧布局的参数，通过此参数来重新确定右侧布局的宽度。
	 */
	private MarginLayoutParams rightLayoutParams;

	/**
	 * 用于计算手指滑动的速度。
	 */
	private VelocityTracker mVelocityTracker;

	/**
	 * 重写SlidingLayout的构造函数，其中获取了屏幕的宽度。
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlidingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();
	}

	/**
	 * 绑定监听侧滑事件的View，即在绑定的View进行滑动才可以显示和隐藏左侧布局。
	 * 
	 * @param bindView
	 *            需要绑定的View对象。
	 */
	public void setScrollEvent(View bindView) {
		mBindView = bindView;
	}

	/**
	 * 将屏幕滚动到左侧布局界面，滚动速度设定为30.
	 */
	public void scrollToLeftLayout() {
		if (leftLayout.getVisibility() != View.VISIBLE) {
			leftLayout.setVisibility(View.VISIBLE);
		}
		new ScrollTask().execute(-60);
	}

	/**
	 * 将屏幕滚动到右侧布局界面，滚动速度设定为-30.
	 */
	public void scrollToRightLayout() {
		if (leftLayout.getVisibility() != View.VISIBLE) {
			leftLayout.setVisibility(View.VISIBLE);
		}
		new ScrollTask().execute(60);
	}

	/**
	 * 左侧布局是否完全显示出来，或完全隐藏，滑动过程中此值无效。
	 * 
	 * @return 左侧布局完全显示返回true，完全隐藏返回false。
	 */
	public boolean isLeftLayoutVisible() {
		return isLeftLayoutVisible;
	}

	/**
	 * 在onLayout中重新设定左侧布局和右侧布局的参数。
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			// 获取左侧布局对象
			leftLayout = getChildAt(0);
			leftLayoutParams = (MarginLayoutParams) leftLayout
					.getLayoutParams();
			rightEdge = -leftLayoutParams.width;
			// 获取右侧布局对象
			rightLayout = getChildAt(1);
			rightLayoutParams = (MarginLayoutParams) rightLayout
					.getLayoutParams();
			rightLayoutParams.width = screenWidth;
			rightLayout.setLayoutParams(rightLayoutParams);
		}
	}



	/**
	 * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中。
	 * 
	 * @param event
	 *            右侧布局监听控件的滑动事件
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	/**
	 * 获取手指在右侧布局的监听View上的滑动速度。
	 * 
	 * @return 滑动速度，以每秒钟移动了多少像素值为单位。
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}

	/**
	 * 回收VelocityTracker对象。
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}

	/**
	 * 使用可以获得焦点的控件在滑动的时候失去焦点。
	 */
	private void unFocusBindView() {
		if (mBindView != null) {
			mBindView.setPressed(false);
			mBindView.setFocusable(false);
			mBindView.setFocusableInTouchMode(false);
		}
	}

	class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... speed) {
			int rightMargin = rightLayoutParams.rightMargin;
			// 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环。
			while (true) {
				rightMargin = rightMargin + speed[0];
				if (rightMargin < rightEdge) {
					rightMargin = rightEdge;
					break;
				}
				if (rightMargin > leftEdge) {
					rightMargin = leftEdge;
					break;
				}
				publishProgress(rightMargin);
				// 为了要有滚动效果产生，每次循环使线程睡眠20毫秒，这样肉眼才能够看到滚动动画。
				sleep(5);
			}
			if (speed[0] > 0) {
				isLeftLayoutVisible = false;
			} else {
				isLeftLayoutVisible = true;
			}
			return rightMargin;
		}

		@Override
		protected void onProgressUpdate(Integer... rightMargin) {
			rightLayoutParams.rightMargin = rightMargin[0];
			rightLayout.setLayoutParams(rightLayoutParams);
			unFocusBindView();
		}

		@Override
		protected void onPostExecute(Integer rightMargin) {
			rightLayoutParams.rightMargin = rightMargin;
			rightLayout.setLayoutParams(rightLayoutParams);
		}
	}

	/**
	 * 使当前线程睡眠指定的毫秒数。
	 * 
	 * @param millis
	 *            指定当前线程睡眠多久，以毫秒为单位
	 */
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
