package org.funlearning.activities;

import org.funlearning.R;
import org.funlearning.view.WriteView;
import org.funlearning.view.WriteView.OnBitmapDrawnListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class WriteActivity extends Activity {

	private static final String[] LettersImage = { "a_big", "a_small", "b_big",
			"b_small", "c_big", "c_small", "d_big", "d_small", "e_big",
			"e_small", "f_big", "f_small", "g_big", "g_small", "h_big",
			"h_small", "i_big", "i_small", "j_big", "j_small", "k_big",
			"k_small", "l_big", "l_small", "m_big", "m_small", "n_big",
			"n_small", "o_big", "o_small", "p_big", "p_small", "q_big",
			"q_small", "r_big", "r_small", "s_big", "s_small", "t_big",
			"t_small", "u_big", "u_small", "v_big", "v_small", "w_big",
			"w_small", "x_big", "x_small", "y_big", "y_small", "z_big",
			"z_small" };
	private DisplayMetrics metrics;
	private LinearLayout letterLayout;
	private LinearLayout writeLayout;
	private Bitmap originalLetterBitmap;
	private int currentPos = 0;
	private int pLetter;
	private int pEqual;
	private int pNotEqual;
	private WriteView writeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.write);

		letterLayout = (LinearLayout) findViewById(R.id.layoutLetter);
		writeLayout = (LinearLayout) findViewById(R.id.layoutWrite);

		letterLayout
				.setBackgroundResource(getResources().getIdentifier(
						"drawable/" + LettersImage[currentPos], null,
						getPackageName()));

		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		writeView = new WriteView(this, metrics);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		writeView.setLayoutParams(params);

		// listen for change events in the bitmap drawn
		writeView.setOnBitmapDrawnListener(changeListener);
		writeLayout.addView(writeView);

		resetLayers();
		super.onCreate(savedInstanceState);
	}

	public Bitmap scaleBitmap(Bitmap bitmap) {

		float proportion = (float) bitmap.getHeight() / bitmap.getWidth();
		int newHeight = Math.round(proportion * 32);

		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 32, newHeight,
				true);

		return resizedBitmap;
	}

	private OnBitmapDrawnListener changeListener = new OnBitmapDrawnListener() {

		@Override
		public void onBitmapDrawn(View v, Bitmap bitmapDrawn) {
			BitmapDrawable originalLetter = new BitmapDrawable(
					originalLetterBitmap);
			BitmapDrawable drawnLetter = new BitmapDrawable(bitmapDrawn);

			Bitmap scaledOriginalLetter = scaleBitmap(originalLetter
					.getBitmap());
			Bitmap scaledDrawnLetter = scaleBitmap(drawnLetter.getBitmap());

			setPixelParams(scaledOriginalLetter, scaledDrawnLetter);

			if ((float) pNotEqual / pEqual >= 1 || pLetter < pNotEqual) {
				// fail, try again
			}

			if ((float) pEqual / pLetter > 0.73
					&& (float) pEqual / pNotEqual > 1.5) {
				if (currentPos < LettersImage.length - 1) {
					currentPos++;
				} else {
					currentPos = 0;
				}
				resetLayers();
			}

		}

	};

	public void resetLayers() {
		letterLayout
				.setBackgroundResource(getResources().getIdentifier(
						"drawable/" + LettersImage[currentPos], null,
						getPackageName()));

		writeView.resetBitmap();

		originalLetterBitmap = ((BitmapDrawable) letterLayout.getBackground())
				.getBitmap();
		originalLetterBitmap = Bitmap.createScaledBitmap(originalLetterBitmap,
				metrics.widthPixels, metrics.heightPixels, true);

		BitmapDrawable originalLetter = new BitmapDrawable(originalLetterBitmap);
		Bitmap scaledOriginalLetter = scaleBitmap(originalLetter.getBitmap());

		setPixelParams(scaledOriginalLetter, scaledOriginalLetter);

		pEqual = 0;
		pNotEqual = 0;
	}

	public void setPixelParams(Bitmap image1, Bitmap image2) {
		pEqual = 0;
		pNotEqual = 0;
		pLetter = 0;
		for (int y = 0; y < image1.getHeight(); ++y)
			for (int x = 0; x < image1.getWidth(); ++x) {
				// background is transparent
				// just check where there is a coloured pixel
				if ((image1.getPixel(x, y) != 0 && image2.getPixel(x, y) == 0)
						|| (image1.getPixel(x, y) == 0 && image2.getPixel(x, y) != 0)) {
					pNotEqual++;
				} else if (image1.getPixel(x, y) != 0
						&& image2.getPixel(x, y) != 0) {
					pEqual++;
				}
				if (image1.getPixel(x, y) != 0) {
					pLetter++;
				}

			}
	}
}
