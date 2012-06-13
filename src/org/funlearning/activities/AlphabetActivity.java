package org.funlearning.activities;

import java.util.Locale;

import org.funlearning.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AlphabetActivity extends Activity implements
		TextToSpeech.OnInitListener, OnClickListener {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static final String[] LettersSmall = { "a", "b", "c", "d", "e",
			"f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
			"s", "t", "u", "v", "w", "x", "y", "z" };
	private static final String[] LettersBig = { "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
			"T", "U", "V", "W", "X", "Y", "Z" };
	private static final String[] LettersImage = { "apple", "bubbles",
			"cup-cake", "door", "eraser", "fire", "grapes", "hydrant",
			"ice-cream", "jelly", "key", "lightbulb", "mountain", "nest",
			"onion", "pencil", "question mark", "ring", "snail", "tulip",
			"umbrella", "v ", "w", "X", "Y", "Z" };

	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private TextToSpeech mTts;
	private int currentLetter = 0;
	private ImageView ivImageLetter;
	private ImageView ivBigLetter;
	private ImageView ivSmallLetter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.alphabet);

		mTts = new TextToSpeech(this, this);

		ivImageLetter = (ImageView) findViewById(R.id.ivImageLetter);
		ivBigLetter = (ImageView) findViewById(R.id.ivBigLetter);
		ivSmallLetter = (ImageView) findViewById(R.id.ivSmallLetter);

		ivImageLetter.setImageResource(R.drawable.apple);
		ivBigLetter.setImageResource(R.drawable.apple);
		ivSmallLetter.setImageResource(R.drawable.apple);

		// Gesture detection
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		ivImageLetter.setOnClickListener(AlphabetActivity.this);
		ivBigLetter.setOnClickListener(AlphabetActivity.this);
		ivSmallLetter.setOnClickListener(AlphabetActivity.this);
		ivImageLetter.setOnTouchListener(gestureListener);
		ivBigLetter.setOnTouchListener(gestureListener);
		ivSmallLetter.setOnTouchListener(gestureListener);

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// nothing
	}

	public void onInit(int status) {
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			int result = mTts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA) {
				// Lanuage data is missing, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
				Log.e("AlphabetAvtivity", "Language is not available.");
			} else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Language is not supported.
				Log.e("AlphabetAvtivity", "Language is not supported.");
			} else {
				// Check the documentation for other possible result codes.
				// For example, the language may be available for the locale,
				// but not for the specified country and variant.
				sayText(currentLetter);		
			}
		} else {
			// Initialization failed.
			Log.e("AlphabetAvtivity", "Could not initialize TextToSpeech.");
		}
	}

	private void sayText(int currentSaying) {
		String toSay = LettersBig[currentSaying] + " is for "
				+ LettersImage[currentSaying];
		mTts.speak(toSay, TextToSpeech.QUEUE_FLUSH, null);
	}

	private void changeImages(int currentPos) {

		//the code works but i need to add all the resources once i get them and resize the images
		//cause they are way too big
		
//		ivImageLetter.setImageResource(this.getResources().getIdentifier(
//				"drawable/" + LettersImage[currentPos], null, this.getPackageName()));
//		ivBigLetter.setImageResource(this.getResources().getIdentifier(
//				"drawable/" + LettersBig[currentPos], null, this.getPackageName()));
//		ivSmallLetter.setImageResource(this.getResources().getIdentifier(
//				"drawable/" + LettersSmall[currentPos], null, this.getPackageName()));
	}

	public void goBackward() {
		if (currentLetter > 0) {
			currentLetter--;
		} else {
			currentLetter = LettersBig.length - 1;
		}
		changeImages(currentLetter);
		sayText(currentLetter);
	}

	public void goForward() {
		if (currentLetter < LettersBig.length - 1) {
			currentLetter++;
		} else {
			currentLetter = 0;
		}
		changeImages(currentLetter);
		sayText(currentLetter);		
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// swipe to the left of the screen
					goForward();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// swipe to the right of the screen
					goBackward();
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

	}

}
