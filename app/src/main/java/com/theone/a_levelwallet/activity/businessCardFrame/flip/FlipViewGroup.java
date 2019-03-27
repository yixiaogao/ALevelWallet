package com.theone.a_levelwallet.activity.businessCardFrame.flip;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/*
 Copyright 2012 Aphid Mobile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */

public class FlipViewGroup extends ViewGroup {

	private static final int MSG_SURFACE_CREATED = 1;

	private LinkedList<View> flipViews = new LinkedList<View>();
	private int currentViewId = 1;

	boolean first = true;

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == MSG_SURFACE_CREATED) {
				width = 0;
				height = 0;
				requestLayout();
				return true;
			}
			return false;
		}
	});

	private GLSurfaceView surfaceView;
	private FlipRenderer renderer;

	private int width;
	private int height;

	private boolean flipping = false;

	public FlipViewGroup(Context context) {
		super(context);
		setupSurfaceView();
	}

	
	public FlipViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupSurfaceView();
	}


	public FlipViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupSurfaceView();
	}


	private void setupSurfaceView() {
		surfaceView = new GLSurfaceView(getContext());

		renderer = new FlipRenderer(this);

		surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		surfaceView.setZOrderOnTop(true);
		surfaceView.setRenderer(renderer);
		surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		renderer.getCards().setViewvGroup(this);
		addView(surfaceView);
		

	}

	public GLSurfaceView getSurfaceView() {
		return surfaceView;
	}

	public FlipRenderer getRenderer() {
		return renderer;
	}

	public void addFlipView(View v) {
		flipViews.add(v);
		v.setVisibility(View.INVISIBLE);
		addView(v);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		AphidLog.i("onLayout: %d, %d, %d, %d; child %d", l, t, r, b,
				flipViews.size());

		for (View child : flipViews)
			child.layout(0, 0, r - l, b - t);

		if (changed || width == 0) {
			int w = r - l;
			int h = b - t;
			surfaceView.layout(0, 0, w, h);

			if (width != w || height != h) {
				width = w;
				height = h;

				if ( flipViews.size() >= 2) {
					if(first){
						first =renderer.setupBitmaps(getNextView(), flipViews.get(currentViewId), getPrevView());
						renderer.getCards().reloadFirstTexture();
					}
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Logger.i( String.format("onMeasure: %d, %d, ; child %d",
		// widthMeasureSpec, heightMeasureSpec, flipViews.size()));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		for (View child : flipViews)
			child.measure(widthMeasureSpec, heightMeasureSpec);
	}

	public void startFlipping() {
		flipping = true;
	}

	public void onResume() {
		surfaceView.onResume();
	}

	public void onPause() {
		surfaceView.onPause();
	}

	public void reloadTexture() {
		handler.sendMessage(Message.obtain(handler, MSG_SURFACE_CREATED));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int tempCurrentViewId = currentViewId;
			if (event.getY() < getHeight() / 2) {
				if (tempCurrentViewId > 0) {

					tempCurrentViewId--;
				}
				renderer.getCards().handleTouchEventDown(true, tempCurrentViewId,getNextNextView() );

			} else {
				if (tempCurrentViewId < flipViews.size() - 1) {

					tempCurrentViewId++;
				}

				renderer.getCards().handleTouchEventDown(false, tempCurrentViewId, getPrevPrevView());
			}
		}
		
		return renderer.getCards().handleTouchEvent(event);
	}

	private View getPrevView() {
		if (currentViewId >= flipViews.size() - 1)
			return null;
		return flipViews.get(currentViewId + 1);
	}

	private View getNextView() {
		if (currentViewId == 0)
			return null;
		return flipViews.get(currentViewId - 1);
	}
	
	private View getPrevPrevView() {
		
		if (currentViewId >= flipViews.size() - 2)
			return null;

		return flipViews.get(currentViewId + 2);
	}

	private View getNextNextView() {
		if (currentViewId <= 1)
			return null;

		return flipViews.get(currentViewId - 2);
	}

	public int getCurrentView() {
		return currentViewId;
	}

	public void setCurrentViewID(int currentView) {
		Log.i("current view Id",
				"id " + currentView + "size " + flipViews.size());
		this.currentViewId = currentView;
	}
}
