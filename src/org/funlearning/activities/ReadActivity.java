package org.funlearning.activities;

import java.util.ArrayList;
import java.util.List;

import org.funlearning.R;
import org.funlearning.utils.Speak;
import org.funlearning.utils.WordsJSONParser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;

public class ReadActivity extends Activity implements OnClickListener,
		OnLongClickListener {

	private static final int REQUEST_CODE = 1234;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
	private ArrayList<String> wordsSpoken = new ArrayList<String>();

	private WordsJSONParser words;
	private ImageView ivImage1;
	private ImageView ivImage2;
	private ImageView ivImage3;
	private String currentWord;
	private Speak mTts;
	private String found;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.read);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		ivImage1 = (ImageView) findViewById(R.id.ivReadLetter1);
		ivImage2 = (ImageView) findViewById(R.id.ivReadLetter2);
		ivImage3 = (ImageView) findViewById(R.id.ivReadLetter3);

		// Gesture detection
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		ivImage1.setOnClickListener(this);
		ivImage2.setOnClickListener(this);
		ivImage3.setOnClickListener(this);

		ivImage1.setOnLongClickListener(this);
		ivImage2.setOnLongClickListener(this);
		ivImage3.setOnLongClickListener(this);

		ivImage1.setOnTouchListener(gestureListener);
		ivImage1.setOnTouchListener(gestureListener);
		ivImage1.setOnTouchListener(gestureListener);

		mTts = new Speak(this);
		words = new WordsJSONParser(this);

		// Disable button if no recognition service is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			// speach recognition not present
		}

		setWord();

		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		spellWord(currentWord);
		readWord(currentWord);
		super.onStart();
	}

	private void setWord() {
		currentWord = words.getRandomWord();
		
		//not to repeat the last 10 words
		while (wordsSpoken.contains(currentWord)) {
			currentWord = words.getRandomWord();
		}
		wordsSpoken.add(currentWord);

		if (wordsSpoken.size() > 10) {
			wordsSpoken.remove(0);
		}
		ivImage1.setImageResource(this.getResources().getIdentifier(
				"drawable/" + currentWord.charAt(0) + "_big", null,
				this.getPackageName()));
		ivImage2.setImageResource(this.getResources().getIdentifier(
				"drawable/" + currentWord.charAt(1) + "_small", null,
				this.getPackageName()));
		ivImage3.setImageResource(this.getResources().getIdentifier(
				"drawable/" + currentWord.charAt(2) + "_small", null,
				this.getPackageName()));
	}

	@Override
	public void onDestroy() {
		mTts.destroy();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO make it more baby proof
		switch (v.getId()) {
		case R.id.ivReadLetter1:
			readLetter(0);
			break;

		case R.id.ivReadLetter2:
			readLetter(1);
			break;

		case R.id.ivReadLetter3:
			readLetter(2);
			break;

		default:
		}
	}

	@Override
	public boolean onLongClick(View v) {
		startVoiceRecognitionActivity();
		return false;
	}

	/**
	 * Fire an intent to start the voice recognition activity.
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening");
		startActivityForResult(intent, REQUEST_CODE);
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			// Populate the wordsList with the String values the recognition
			// engine thought it heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			found = matches.get(0);
		}
		checkWordFound();
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void checkWordFound() {
		if (currentWord.equals(found)) {
			setWord();
			spellWord(currentWord);
			readWord(currentWord);
		} else {
			//String toSay = "Try again";
			readWord(found);
		}
	}

	private void readLetter(int index) {
		mTts.setNormal();
		String toSay = "" + currentWord.charAt(index);
		mTts.speak(toSay, TextToSpeech.QUEUE_FLUSH);
	}

	private void readWord(String word) {
		mTts.setNormal();
		mTts.speak(word, TextToSpeech.QUEUE_ADD);
	}

	private void spellWord(String word) {
		for (int i = 0; i < word.length(); i++) {
			String toSay = "" + currentWord.charAt(i);
			mTts.speak(toSay, TextToSpeech.QUEUE_ADD);
		}
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// swipe to the right of the screen
					readWord(currentWord);
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

	}
}
