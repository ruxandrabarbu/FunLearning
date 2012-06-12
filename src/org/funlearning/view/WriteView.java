package org.funlearning.view;

import org.funlearning.R;
import org.funlearning.activities.WriteActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class WriteView extends View {

	private Paint mPaint;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private Paint innerPaint;
	private DisplayMetrics metrics;
	
	private static final int FINGER_SIZE = 30;

	public WriteView(Context aContext, DisplayMetrics aMetrics) {
		super(aContext);

		metrics = aMetrics;

		mPath = new Path();
		innerPaint = new Paint();
		// innerPaint.setARGB(225, 75, 75, 75); //gray
		innerPaint.setColor(Color.TRANSPARENT);
		innerPaint.setAntiAlias(true);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFAAFF);// AT changed
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(FINGER_SIZE);

		// set the paint in erase mode
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
		mPaint.setColor(Color.RED);

		BitmapFactory.Options bounds = new BitmapFactory.Options();
		// bounds.inScaled = false;
		bounds.outHeight = 100;
		bounds.outWidth = 100;

		mBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.background, bounds);

		mBitmap = scaleToScreen(mBitmap);	

		mCanvas = new Canvas(mBitmap);

	}

	public Bitmap scaleToScreen(Bitmap bitmap) {
		float scaleFactor = (float) metrics.widthPixels
				/ (float) bitmap.getWidth();
		int newWidth = (int) (bitmap.getWidth() * scaleFactor);
		int newHeight = (int) (bitmap.getHeight() * scaleFactor);
		Bitmap img = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight,
				true);
		return img;
	}

	public void setInnerPaint(Paint innerPaint) {
		this.innerPaint = innerPaint;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		RectF drawRect = new RectF();
		drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

		canvas.drawRoundRect(drawRect, 0, 0, innerPaint);
		// canvas.drawRoundRect(drawRect, 5, 5, borderPaint);

		super.dispatchDraw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath(mPath, mPaint);
	}

	private float mX, mY, oldX, oldY;
	private static final float TOUCH_TOLERANCE = 4;
	private int pathSize = 1;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		oldX = x;
		oldY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
		pathSize++;
		if (pathSize > 3) {

			pathSize = 1;
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			mPath.reset();
			oldX = mX;
			oldY = mY;

			mPath.moveTo(oldX, oldY);
		}
	}

	private void touch_up() {
		mPath.moveTo(oldX, oldY);
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			// touch_up();
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			new WriteActivity().imagesAreEqual(mBitmap);
			break;
		}
		return true;
	}
}
