package org.funlearning.activities;

import java.util.Locale;
import java.util.Random;

import org.funlearning.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class GameActivity extends Activity implements OnClickListener, TextToSpeech.OnInitListener {

	private static final String[] LettersSmall = { "a", "b", "c", "d", "e",
			"f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
			"s", "t", "u", "v", "w", "x", "y", "z" };
	private static final String[] LettersImage = { "apple", "bubbles",
			"cupcake", "door", "eraser", "fire", "grapes", "hydrant",
			"icecream", "jelly", "key", "lightbulb", "mountain", "nest",
			"onion", "pencil", "questionmark", "ring", "snail", "tulip",
			"umbrella", "vase", "watermelon", "xylophone", "yoyo", "zipper" };
    private static final String[] Congratulations = {
        "well done",
        "good job",
        "yey"
      };
    private static final String[] TryAgain = {
        "guess again",
        "try again"
      };

	private static final Random RANDOM = new Random();

	private ImageView ivLetter;
	private ImageView ivImage1;
	private ImageView ivImage2;
	private ImageView ivImage3;
	private ImageView ivImage4;

	private int[] images;

	private int correctImage;
	private TextToSpeech mTts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.game);
		
		mTts = new TextToSpeech(this, this);

		ivLetter = (ImageView) findViewById(R.id.ivLetter);
		ivImage1 = (ImageView) findViewById(R.id.ivImage1);
		ivImage2 = (ImageView) findViewById(R.id.ivImage2);
		ivImage3 = (ImageView) findViewById(R.id.ivImage3);
		ivImage4 = (ImageView) findViewById(R.id.ivImage4);
		
		images = new int[5];

		generateGame();
		
		ivLetter.setOnClickListener(this);
		ivImage1.setOnClickListener(this);
		ivImage2.setOnClickListener(this);
		ivImage3.setOnClickListener(this);
		ivImage4.setOnClickListener(this);

		super.onCreate(savedInstanceState);
	}
	
	private void readLetter() {
		String toSay = LettersSmall[images[correctImage]] + " is for ";
		mTts.speak(toSay, TextToSpeech.QUEUE_FLUSH, null);	
	}
	
	private void readImage(int imageNo) {
		String toSay = LettersImage[imageNo];
		mTts.speak(toSay, TextToSpeech.QUEUE_FLUSH, null);	
	}
	
	private void readCongratulations() {
		String toSay = "   " + Congratulations[RANDOM.nextInt(Congratulations.length)];
		mTts.speak(toSay, TextToSpeech.QUEUE_ADD, null);
	}
	
	private void readTryAgain() {
		String toSay = "   " + TryAgain[RANDOM.nextInt(TryAgain.length)];
		mTts.speak(toSay, TextToSpeech.QUEUE_ADD, null);	
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
		switch (v.getId()) {
		case R.id.ivImage1:
			imageSelected(1);
			break;
			
		case R.id.ivImage2:            
			imageSelected(2);
			break;
			
		case R.id.ivImage3:
			imageSelected(3);
			break;
			
		case R.id.ivImage4:
			imageSelected(4);
			break;
			
		case R.id.ivLetter:     
			readLetter();
			break;
			
		default:
		}		
	}
	
	private void imageSelected(int imageSelected) {
		readImage(images[imageSelected]);
		if (correctImage == imageSelected) {
			readCongratulations();
			generateGame();
			readLetter();
		} else {
			readTryAgain();
		}	
	}

	private void generateGame() {
		int letterLenght = LettersSmall.length;
		int letterPos = RANDOM.nextInt(letterLenght);

		correctImage = RANDOM.nextInt(4) + 1;

		String a_big = LettersSmall[letterPos] + "_big";

		ivLetter.setImageResource(this.getResources().getIdentifier(
				"drawable/" + a_big, null, this.getPackageName()));

		for (int i = 1; i < 5; i++) {			
			String ivName = "ivImage" + Integer.toString(i);
			String a_image;
			
			ImageView iv = (ImageView) findViewById(this.getResources()
					.getIdentifier("id/" + ivName, null, this.getPackageName()));			
			
			if (i == correctImage) {
				images[i] = letterPos;
				a_image = LettersImage[letterPos];
				iv.setImageResource(this.getResources().getIdentifier(
						"drawable/" + a_image, null, this.getPackageName()));
			} else {
				boolean ok = false;
				int randomImage = 0;
				while (!ok) {
					randomImage = RANDOM.nextInt(letterLenght);
					ok = true;
					if (letterPos == randomImage)
						ok = false;
					for (int j = 1; j < i; j++) {
						if (randomImage == images[j]) 
							ok = false;
					}
				}
				images[i] = randomImage;
				a_image = LettersImage[randomImage];
				iv.setImageResource(this.getResources().getIdentifier(
						"drawable/" + a_image, null, this.getPackageName()));
			}
		}
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
				readLetter();	
			}
		} else {
			// Initialization failed.
			Log.e("AlphabetAvtivity", "Could not initialize TextToSpeech.");
		}
	}	
}
