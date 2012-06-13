package org.funlearning.activities;

import org.funlearning.R;
import org.funlearning.view.WriteView;
import org.funlearning.view.WriteView.OnBitmapDrawnListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class WriteActivity extends Activity {

	private static final String[] LettersImage = { "A", "a", "B", "b", "C",
			"c", "D", "d", "E", "e", "F", "f", "G", "g", "H", "h", "I", "i",
			"J", "j", "K", "k", "L", "l", "M", "m", "N", "n", "O", "o", "P",
			"p", "Q", "q", "R", "r", "S", "s", "T", "t", "U", "u", "V", "v",
			"W", "w", "X", "x", "Y", "y", "Z", "z" };
	private DisplayMetrics metrics;
	private LinearLayout letterLayout;
	private LinearLayout writeLayout;
	private Bitmap originalLetterBitmap;
	private int currentPos = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.write);

		letterLayout = (LinearLayout) findViewById(R.id.layoutLetter);
		writeLayout = (LinearLayout) findViewById(R.id.layoutWrite);
		
		letterLayout.setBackgroundResource(this.getResources().getIdentifier(
				"drawable/" + LettersImage[currentPos], null, this.getPackageName()));
		
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		WriteView wv = new WriteView(this, metrics);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		wv.setLayoutParams(params);

		// listen for change events in the bitmap drawn
		wv.setOnBitmapDrawnListener(changeListener);
		writeLayout.addView(wv);

		setLetter();
		super.onCreate(savedInstanceState);
	}

	public void setLetter() {
		originalLetterBitmap = ((BitmapDrawable) letterLayout.getBackground())
				.getBitmap();
		originalLetterBitmap = scaleBitmap(originalLetterBitmap,
				metrics.heightPixels, metrics.widthPixels);
	}

	public Bitmap scaleBitmap(Bitmap bitmap, int newHeight, int newWidth) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		float scaleWidth = ((float) newHeight) / width;
		float scaleHeight = ((float) newWidth) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth,
				newHeight, true);

		return resizedBitmap;
	}

	private OnBitmapDrawnListener changeListener = new OnBitmapDrawnListener() {

		@Override
		public void onBitmapDrawn(View v, Bitmap bitmapDrawn) {
			BitmapDrawable originalLetter = new BitmapDrawable(
					originalLetterBitmap);
			BitmapDrawable drawnLetter = new BitmapDrawable(bitmapDrawn);

			float proportion = (float) originalLetter.getIntrinsicHeight()
					/ originalLetter.getIntrinsicWidth();

			int newHeight = Math.round(proportion * 32);

			Bitmap scaledOriginalLetter = scaleBitmap(
					originalLetter.getBitmap(), newHeight, 32);
			Bitmap scaledDrawnLetter = scaleBitmap(drawnLetter.getBitmap(),
					newHeight, 32);

			boolean ok = imagesAreEqual(scaledOriginalLetter, scaledDrawnLetter);

			if (ok) {
				if (currentPos < LettersImage.length - 1) {
					currentPos++;
				} else {
					currentPos = 0;
				}
				letterLayout.setBackgroundResource(getResources().getIdentifier(
						"drawable/" + LettersImage[currentPos], null, getPackageName()));
			}
			
			Log.i("WriteActivity", "same region: " + ok);
		}

	};

	public boolean imagesAreEqual(Bitmap image1, Bitmap image2) {
		if (image1.getHeight() != image2.getHeight())
			return false;
		if (image1.getWidth() != image2.getWidth())
			return false;

		int notEqual = 0;

		for (int y = 0; y < image1.getHeight(); ++y)
			for (int x = 0; x < image1.getWidth(); ++x) {
				// background is transparent
				// just check where there is a coloured pixel
				if ((image1.getPixel(x, y) != 0 && image2.getPixel(x, y) == 0)
						|| (image1.getPixel(x, y) == 0 && image2.getPixel(x, y) != 0))
					notEqual++;
			}

		Log.i("Fun Learning Write Activity", "not equal pixels" + notEqual);
		if (notEqual > 40)
			return false;
		else
			return true;
	}
}
