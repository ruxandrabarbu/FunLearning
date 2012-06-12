package org.funlearning.activities;

import org.funlearning.R;
import org.funlearning.view.WriteView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class WriteActivity extends Activity {
	private DisplayMetrics metrics;
	private LinearLayout letterLayout;
	private LinearLayout writeLayout;
	private Bitmap originalLetterBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.write);	

		letterLayout = (LinearLayout) findViewById(R.id.layoutLetter);
		writeLayout = (LinearLayout) findViewById(R.id.layoutWrite);
		letterLayout.setBackgroundResource(R.drawable.a);
		
		
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		WriteView wv = new WriteView(this, metrics);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		wv.setLayoutParams(params);
		writeLayout.addView(wv);

		setLetter();
		super.onCreate(savedInstanceState);
	}

	public void setLetter() {
		originalLetterBitmap = ((BitmapDrawable)letterLayout.getBackground()).getBitmap();
		originalLetterBitmap = scaleToScreen(originalLetterBitmap);		
	}

	public Bitmap scaleToScreen(Bitmap bitmap) {
//		float scaleFactor = (float) metrics.widthPixels
//				/ (float) bitmap.getWidth();
//		int newWidth = (int) (bitmap.getWidth() * scaleFactor);
//		int newHeight = (int) (bitmap.getHeight() * scaleFactor);
//		Bitmap img = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight,
//				true);
//		return img;

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrig, 0, 0, bitmapOrig.getWidth(), bitmapOrig.getHeight(), matrix, true);
        BitmapDrawable bitmapDrawableResized = new BitmapDrawable(resizedBitmap);

		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		float scaleWidth = ((float) metrics.widthPixels) / width;
		float scaleHeight = ((float) metrics.heightPixels) / height;
		
		int newWidth = (int) (bitmap.getWidth() * scaleWidth);
		int newHeight = (int) (bitmap.getHeight() * scaleHeight);
		
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

		Bitmap img = Bitmap.createBitmap(bitmap, newWidth, newHeight, true);
		
		return img;

	}


	public boolean imagesAreEqual(Bitmap i1) {
		if (i1.getHeight() != originalLetterBitmap.getHeight())
			return false;
		if (i1.getWidth() != originalLetterBitmap.getWidth())
			return false;

		int notEqual = 0;

		for (int y = 0; y < i1.getHeight(); ++y)
			for (int x = 0; x < i1.getWidth(); ++x)
				if (i1.getPixel(x, y) != originalLetterBitmap.getPixel(x, y))
					notEqual++;
		
		Log.i("Fun Learning Write Activity", "not equal pixels" + notEqual);
		if (notEqual > 10000)
			return false;
		else
			return true;
	}
}
