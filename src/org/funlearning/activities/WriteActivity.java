package org.funlearning.activities;

import org.funlearning.R;
import org.funlearning.view.WriteView;
import org.funlearning.view.WriteView.OnBitmapDrawnListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class WriteActivity extends Activity implements OnClickListener {

	private static final int BUTOON_REDO_ID = 666;
	private static final int BUTTON_SKIP_ID = 999;
	private static final int SMALL_A_CODE = 97; //small a
	private static final int NO_LETTERS = 48; //no of letters in the alphabet(big and small)

	private DisplayMetrics metrics;
	private LinearLayout letterLayout;
	private RelativeLayout writeLayout;
	private Bitmap originalLetterBitmap;
	private int currentPos = 0;
	private int pLetter;
	private int pEqual;
	private int pNotEqual;
	private WriteView writeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.write);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		letterLayout = (LinearLayout) findViewById(R.id.layoutLetter);
		writeLayout = (RelativeLayout) findViewById(R.id.layoutWrite);
		String name = getImageName();

		letterLayout
				.setBackgroundResource(getResources().getIdentifier(
						"drawable/" + name, null,
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case BUTOON_REDO_ID:
			resetLayers();
			break;

		case BUTTON_SKIP_ID:
			if (currentPos < NO_LETTERS - 1) {
				currentPos++;
			} else {
				currentPos = 0;
			}
			resetLayers();
			break;

		default:
		}
	}

	private Bitmap scaleBitmap(Bitmap bitmap) {

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

			if ((float) pNotEqual / pEqual >= 0.5
					|| (float) pNotEqual / pLetter >= 0.35) {
				whatToDoNext();
			}

			if ((float) pEqual / pLetter > 0.73
					&& (float) pNotEqual / pLetter <= 0.35
					&& (float) pEqual / pNotEqual > 1) {
				if (currentPos < NO_LETTERS - 1) {
					currentPos++;
				} else {
					currentPos = 0;
				}
				resetLayers();
			}

		}

	};

	private void resetLayers() {
		String name = getImageName();
		letterLayout
				.setBackgroundResource(getResources().getIdentifier(
						"drawable/" + name, null,
						getPackageName()));

		// add empty view
		writeLayout.removeAllViews();
		writeView = new WriteView(this, metrics);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		writeView.setLayoutParams(params);

		// listen for change events in the bitmap drawn
		writeView.setOnBitmapDrawnListener(changeListener);
		writeLayout.addView(writeView);

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

	private void whatToDoNext() {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);

		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rp.addRule(RelativeLayout.CENTER_IN_PARENT);

		Button btnRedo = new Button(this);
		Button btnSkip = new Button(this);

		btnRedo.setPadding(10, 10, 10, 10);
		btnSkip.setPadding(10, 10, 10, 10);

		btnRedo.setText("Redo");
		btnSkip.setText("Skip");

		btnSkip.setId(BUTTON_SKIP_ID);
		btnRedo.setId(BUTOON_REDO_ID);

		btnRedo.setOnClickListener(this);
		btnSkip.setOnClickListener(this);

		writeLayout.removeAllViews();
		writeLayout.addView(btnRedo, lp);
		writeLayout.addView(btnSkip, rp);
	}

	private void setPixelParams(Bitmap image1, Bitmap image2) {
		pEqual = 0;
		pNotEqual = 0;
		pLetter = 0;
		for (int y = 0; y < image1.getHeight(); ++y)
			for (int x = 0; x < image1.getWidth(); ++x) {
				// background is transparent
				// just check where there is a coloured pixel
				if ((image1.getPixel(x, y) == 0 && image2.getPixel(x, y) != 0)) {
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
	
	private String getImageName() {
		String name = "" + (char)(currentPos + SMALL_A_CODE);
		if (currentPos % 2 == 0) {
			name = name + "_small";
		} else {
			name = name + "_big";
		}
		return name;
	}
}
