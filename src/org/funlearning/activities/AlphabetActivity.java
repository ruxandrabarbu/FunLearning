package org.funlearning.activities;

import org.funlearning.R;
import org.funlearning.utils.ImagesJSONParser;
import org.funlearning.utils.Speak;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AlphabetActivity extends Activity implements OnClickListener {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static final int SMALL_A_CODE = 97; //small a
	private static final int NO_LETTERS = 24; //no of letters in the alphabet
	
	private ImagesJSONParser allImages; 
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private Speak mTts;
	private int currentLetter = 0;
	private ImageView ivImageLetter;
	private ImageView ivBigLetter;
	private ImageView ivSmallLetter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.alphabet);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		mTts = new Speak(this);
		allImages = new ImagesJSONParser(this);
		
		ivImageLetter = (ImageView) findViewById(R.id.ivImageLetter);
		ivBigLetter = (ImageView) findViewById(R.id.ivBigLetter);
		ivSmallLetter = (ImageView) findViewById(R.id.ivSmallLetter);

		ivImageLetter.setImageResource(R.drawable.apple);
		ivBigLetter.setImageResource(R.drawable.a_big);
		ivSmallLetter.setImageResource(R.drawable.a_small);

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
	protected void onStart() {
		sayText(currentLetter);
		super.onStart();
	}

	@Override
	public void onDestroy() {
		mTts.destroy();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// nothing
	}

	private void sayText(int currentSaying) {
		String toSay = String.valueOf((char)(currentLetter + SMALL_A_CODE)) + " is for "
				+ allImages.getAt(currentSaying);
		mTts.speak(toSay, TextToSpeech.QUEUE_FLUSH);
	}

	private void changeImages(int currentPos) {

		String a_big = (char)(currentLetter + SMALL_A_CODE) + "_big";
		String a_small = (char)(currentLetter + SMALL_A_CODE) + "_small";

		ivImageLetter.setImageResource(this.getResources().getIdentifier(
				"drawable/" + allImages.getAt(currentPos), null,
				this.getPackageName()));
		ivBigLetter.setImageResource(this.getResources().getIdentifier(
				"drawable/" + a_big, null, this.getPackageName()));
		ivSmallLetter.setImageResource(this.getResources().getIdentifier(
				"drawable/" + a_small, null, this.getPackageName()));
	}

	public void goBackward() {
		if (currentLetter > 0) {
			currentLetter--;
		} else {
			currentLetter = NO_LETTERS - 1;
		}
		changeImages(currentLetter);
		sayText(currentLetter);
	}

	public void goForward() {
		if (currentLetter < NO_LETTERS - 1) {
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
