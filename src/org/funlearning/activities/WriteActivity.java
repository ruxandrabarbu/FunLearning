package org.funlearning.activities;

import org.funlearning.R;
import org.funlearning.view.WriteView;
import org.funlearning.view.WriteView.OnBitmapDrawnListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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

		// listen for change events in the bitmap drawn
		wv.setOnBitmapDrawnListener(changeListener);
		writeLayout.addView(wv);

		setLetter();
		super.onCreate(savedInstanceState);
	}

	public void setLetter() {
		originalLetterBitmap = ((BitmapDrawable) letterLayout.getBackground())
				.getBitmap();
		originalLetterBitmap = scaleToScreen(originalLetterBitmap);
	}

	public Bitmap scaleToScreen(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		float scaleWidth = ((float) metrics.widthPixels) / width;
		float scaleHeight = ((float) metrics.heightPixels) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap,
				metrics.widthPixels, metrics.heightPixels, true);

		return resizedBitmap;
	}

	private OnBitmapDrawnListener changeListener = new OnBitmapDrawnListener() {

		@Override
		public void onBitmapDrawn(View v, Bitmap bitmapDrawn) {
			BitmapDrawable originalLetter = new BitmapDrawable(originalLetterBitmap);
			BitmapDrawable drawnLetter = new BitmapDrawable(bitmapDrawn);

			boolean ok = imagesAreEqual(originalLetter.getBitmap(), drawnLetter.getBitmap());
			
			Log.i("WriteActivity", "same region: " + ok);
		}

	};

	public boolean imagesAreEqual(Bitmap image1, Bitmap image2) {
		if (image1.getHeight() != image2.getHeight())
			return false;
		if (image1.getWidth() != image2.getWidth())
			return false;

		// transform red into black
		int [] allpixels = new int [ image2.getHeight()*image2.getWidth()];
		image2.getPixels(allpixels, 0, image2.getWidth(), 0, 0, image2.getWidth(),image2.getHeight());
		for(int i =0; i<image2.getHeight()*image2.getWidth();i++){
		 if( allpixels[i] == Color.RED)
		             allpixels[i] = Color.BLACK;
		 }
		image2.setPixels(allpixels, 0, image2.getWidth(), 0, 0, image2.getWidth(), image2.getHeight());
		
		int notEqual = 0;

		for (int y = 0; y < image1.getHeight(); ++y)
			for (int x = 0; x < image1.getWidth(); ++x)
				if (image1.getPixel(x, y) != image2.getPixel(x, y))
					notEqual++;

		Log.i("Fun Learning Write Activity", "not equal pixels" + notEqual);
		if (notEqual > 12000)
			return false;
		else
			return true;
	}
}
