package com.theone.a_levelwallet.activity.businessCardFrame.flip;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static com.theone.a_levelwallet.activity.businessCardFrame.flip.FlipRenderer.checkError;


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

 improved by MarcinKosztolowicz
 */
enum Position {
    up, down
};

public class FlipCards {

    private static final float ACCELERATION = 0.618f;
    private static final float MOVEMENT_RATE = 2f;

    private static final int STATE_TOUCH = 1;
    private static final int STATE_AUTO_ROTATE = 2;
    private static final int STATE_NO_ACTION = 3;
    private static final Object LOCKANGLE = new Object();
    private int tempCurrentViewId = -1;
    private Position startPosition = Position.up;
    private Position endPosition = Position.up;
    private boolean nextPage = false;
    private Position block = null;
    
    private View newView;
    private FlipViewGroup flipViewGroup;

    private Bitmap previousBitmap;
    private Bitmap currentBitmap;
    private Bitmap nextBitmap;
    
    private Texture frontTexture;
    private Bitmap upBitmap;

    private Texture backTexture;
    private Bitmap downBitmap;

    private Card frontTopCard;
    private Card frontBottomCard;

    private Card backTopCard;
    private Card backBottomCard;

    private float angle = 180f;
    private boolean forward = true;
    private int animatedFrame = 0;
    private int state = STATE_TOUCH;

    public FlipCards() {
	frontTopCard = new Card();
	frontBottomCard = new Card();

	backTopCard = new Card();
	backBottomCard = new Card();

	frontBottomCard.setAxis(Card.AXIS_TOP);
	backTopCard.setAxis(Card.AXIS_BOTTOM);
	backBottomCard.setAxis(Card.AXIS_TOP);
	frontTopCard.setAxis(Card.AXIS_BOTTOM);
    }

    public void rotateBy(float delta) {

	angle += delta;

	if (angle > 180)
	    angle = 180;
	else if (angle < 0)
	    angle = 0;

    }

    public void setState(int state) {
	if (this.state != state) {
	    this.state = state;
	    animatedFrame = 0;
	}
    }

    public void draw(GL10 gl) {
	synchronized (LOCKANGLE) {
	applyTexture(gl);

	if (frontTexture == null)
	    return;
//	synchronized (LOCKANGLE) {
	    switch (state) {
	    case STATE_TOUCH:
	    case STATE_NO_ACTION:
		break;
	    case STATE_AUTO_ROTATE: {
		animatedFrame++;
		rotateBy((forward ? ACCELERATION : -ACCELERATION)
			* animatedFrame);

		whenPageLies(gl);
		// reloadFirstTexture();
		
	    }
		break;
	    default:
		AphidLog.e("Invalid state: " + state);
		break;
	    }
	    
//	}
	
	    if (startPosition == Position.down) {
		drawDependenceOnAngleStartDown(gl);
	    } else if (startPosition == Position.up) {
		drawDependenceOnAngleStartUp(gl);
	    }
	}
    }

    private void whenPageLies(GL10 gl) {

	if (angle >= 180 || angle <= -0) {
	    setState(STATE_NO_ACTION);
	    setEndposition();
	    if (nextPage && startPosition != endPosition) {
		flipViewGroup.setCurrentViewID(tempCurrentViewId);
		onPageLiesDown(startPosition == Position.up ? true : false);
		Log.i("bitmap", angle + " bmp " + upBitmap + " " + downBitmap);

	    }

	}
    }

    private void setEndposition() {
	if (angle >= 180)
	    endPosition = Position.up;
	else if (angle <= 0)
	    endPosition = Position.down;
    }

    private void drawDependenceOnAngleStartDown(GL10 gl) {
	if (angle < 90) {
	    drawInStartDownLowerPart(gl);
	} else {
	    drawInStartDownUpperPart(gl);
	}
    }

    private void drawDependenceOnAngleStartUp(GL10 gl) {
	if (angle < 90) {
	    drawInStartUpLowerPart(gl);
	} else {
	    drawInStartUpUpperPart(gl);
	}
    }

    private void drawInStartDownLowerPart(GL10 gl) {
	frontTopCard.setAngle(0);
	frontTopCard.draw(gl);
	backBottomCard.setAngle(0);
	backBottomCard.draw(gl);
	frontBottomCard.setAngle(angle);
	frontBottomCard.draw(gl);
    }

    private void drawInStartDownUpperPart(GL10 gl) {
	backTopCard.setAngle(180 - angle);
	frontTopCard.setAngle(0f);
	frontTopCard.draw(gl);
	backTopCard.draw(gl);
	backBottomCard.setAngle(0f);
	backBottomCard.draw(gl);
    }

    private void drawInStartUpLowerPart(GL10 gl) {
	frontBottomCard.setAngle(0f);
	frontBottomCard.draw(gl);
	backTopCard.setAngle(0f);
	backTopCard.draw(gl);
	backBottomCard.setAngle(angle);
	backBottomCard.draw(gl);
    }

    private void drawInStartUpUpperPart(GL10 gl) {
	frontBottomCard.setAngle(0f);
	frontBottomCard.draw(gl);
	backTopCard.setAngle(0f);
	backTopCard.draw(gl);
	frontTopCard.setAngle(180 - angle);
	frontTopCard.draw(gl);
    }

    private void applyTexture(GL10 gl) {

	if (upBitmap != null) {
	    if (frontTexture != null)
		frontTexture.destroy(gl);

	    frontTexture = Texture.createTexture(upBitmap, gl);

	    frontTopCard.setTexture(frontTexture);
	    frontBottomCard.setTexture(frontTexture);

	    frontTopCard.setCardVertices(new float[] { 0f,
		    upBitmap.getHeight(), 0f, // top left
		    0f, upBitmap.getHeight() / 2.0f, 0f, // bottom left
		    upBitmap.getWidth(), upBitmap.getHeight() / 2f, 0f, // bottom
									// right
		    upBitmap.getWidth(), upBitmap.getHeight(), 0f // top
								  // right
		    });

	    frontTopCard
		    .setTextureCoordinates(new float[] {
			    0f,
			    0f,
			    0f,
			    upBitmap.getHeight() / 2f
				    / (float) frontTexture.getHeight(),
			    upBitmap.getWidth()
				    / (float) frontTexture.getWidth(),
			    upBitmap.getHeight() / 2f
				    / (float) frontTexture.getHeight(),
			    upBitmap.getWidth()
				    / (float) frontTexture.getWidth(), 0f });

	    frontBottomCard.setCardVertices(new float[] { 0f,
		    upBitmap.getHeight() / 2f, 0f, // top left
		    0f, 0f, 0f, // bottom left
		    upBitmap.getWidth(), 0f, 0f, // bottom right
		    upBitmap.getWidth(), upBitmap.getHeight() / 2f, 0f // top
								       // right
		    });

	    frontBottomCard.setTextureCoordinates(new float[] {
		    0f,
		    upBitmap.getHeight() / 2f
			    / (float) frontTexture.getHeight(),
		    0f,
		    upBitmap.getHeight() / (float) frontTexture.getHeight(),
		    upBitmap.getWidth() / (float) frontTexture.getWidth(),
		    upBitmap.getHeight() / (float) frontTexture.getHeight(),
		    upBitmap.getWidth() / (float) frontTexture.getWidth(),
		    upBitmap.getHeight() / 2f
			    / (float) frontTexture.getHeight() });

	    checkError(gl);

	    // upBitmap.recycle();
	    upBitmap = null;
	}

	if (downBitmap != null) {
	    if (backTexture != null)
		backTexture.destroy(gl);

	    backTexture = Texture.createTexture(downBitmap, gl);

	    backTopCard.setTexture(backTexture);
	    backBottomCard.setTexture(backTexture);

	    backTopCard.setCardVertices(new float[] { 0f,
		    downBitmap.getHeight(), 0f, // top left
		    0f, downBitmap.getHeight() / 2.0f, 0f, // bottom left
		    downBitmap.getWidth(), downBitmap.getHeight() / 2f, 0f, // bottom
									    // right
		    downBitmap.getWidth(), downBitmap.getHeight(), 0f // top
								      // right
		    });

	    backTopCard
		    .setTextureCoordinates(new float[] {
			    0f,
			    0f,
			    0f,
			    downBitmap.getHeight() / 2f
				    / (float) backTexture.getHeight(),
			    downBitmap.getWidth()
				    / (float) backTexture.getWidth(),
			    downBitmap.getHeight() / 2f
				    / (float) backTexture.getHeight(),
			    downBitmap.getWidth()
				    / (float) backTexture.getWidth(), 0f });

	    backBottomCard.setCardVertices(new float[] { 0f,
		    downBitmap.getHeight() / 2f, 0f, // top left
		    0f, 0f, 0f, // bottom left
		    downBitmap.getWidth(), 0f, 0f, // bottom right
		    downBitmap.getWidth(), downBitmap.getHeight() / 2f, 0f // top
									   // right
		    });

	    backBottomCard.setTextureCoordinates(new float[] {
		    0f,
		    downBitmap.getHeight() / 2f
			    / (float) backTexture.getHeight(),
		    0f,
		    downBitmap.getHeight() / (float) backTexture.getHeight(),
		    downBitmap.getWidth() / (float) backTexture.getWidth(),
		    downBitmap.getHeight() / (float) backTexture.getHeight(),
		    downBitmap.getWidth() / (float) backTexture.getWidth(),
		    downBitmap.getHeight() / 2f
			    / (float) backTexture.getHeight() });

	    checkError(gl);

	    // downBitmap.recycle();
	    downBitmap = null;
	}
    }

    public void invalidateTexture() {
	// Texture is vanished when the gl context is gone, no need to delete it
	// explicitly
	frontTexture = null;
	backTexture = null;
    }

    public void setupBitmaps(View nextView, View currentView, View previousView) {
	nextBitmap = GrabIt.takeScreenshot(nextView);
	currentBitmap = GrabIt.takeScreenshot(currentView);
	previousBitmap = GrabIt.takeScreenshot(previousView);
    }

    private void onPageLiesDown(boolean up) {
	// synchronized (LOCKBITMAP) {
	if (up) {
	    previousBitmap = currentBitmap;
	    currentBitmap = nextBitmap;
	    nextBitmap = GrabIt.takeScreenshot(newView);
	} else {
	    nextBitmap = currentBitmap;
	    currentBitmap = previousBitmap;
	    previousBitmap = GrabIt.takeScreenshot(newView);
	}
	// }
    }

    private void reloadTextures(boolean up) {
	// synchronized (LOCKBITMAP) {
	upBitmap = currentBitmap;
	if (up)
	    downBitmap = nextBitmap;
	else
	    downBitmap = previousBitmap;
	// }
    }

    public void reloadFirstTexture() {
	upBitmap = currentBitmap;
    }

    private void reloadEndPointTextures() {
	upBitmap = currentBitmap;
	downBitmap = currentBitmap;
    }

    public void handleTouchEventDown(boolean up, int tempCurrentViewId,
	    View newView) {
	if (angle < 180 && angle > 0)
	    return;

	this.newView = newView;
	this.tempCurrentViewId = tempCurrentViewId;

	setStartAngleAndtextures(up);

	startPosition = up ? Position.up : Position.down;
    }

    private void setStartAngleAndtextures(boolean up) {
	if (up && nextBitmap != null) {

	    synchronized (LOCKANGLE) {
		angle = 180f;
		reloadTextures(up);
		System.out.println("up touch");
	    }

	    nextPage = true;
	    block = null;
	} else if (!up && previousBitmap != null) {

	    synchronized (LOCKANGLE) {
		angle = 0f;
		reloadTextures(up);
		 System.out.println("down touch");
	    }

	    nextPage = true;
	    block = null;
	} else {
	    block = up ? Position.up : Position.down;
	    reloadEndPointTextures();
	    nextPage = false;
	}
    }

    private float lastY = -1;

    public boolean handleTouchEvent(MotionEvent event) {
	if(block !=null)
	    if(block==startPosition)
		return false;
	
	if (frontTexture == null)
	    return false;

	float delta;

	switch (event.getAction()) {
	case MotionEvent.ACTION_DOWN:
	    lastY = event.getY();

	    setState(STATE_TOUCH);
	    return true;
	case MotionEvent.ACTION_MOVE:
	    delta = lastY - event.getY();
	    rotateBy(180 * delta / frontTexture.getContentHeight()
		    * MOVEMENT_RATE);
	    lastY = event.getY();
	    return true;
	case MotionEvent.ACTION_UP:
	case MotionEvent.ACTION_CANCEL:
	    delta = lastY - event.getY();
	    rotateBy(180 * delta / frontTexture.getContentHeight()
		    * MOVEMENT_RATE);
	    if (angle < 90) {
		forward = false;

	    } else {
		forward = true;

	    }

	    setState(STATE_AUTO_ROTATE);
	    return true;
	}

	return false;
    }

    public void setViewvGroup(FlipViewGroup flipViewGroup) {
	this.flipViewGroup = flipViewGroup;

    }
}
