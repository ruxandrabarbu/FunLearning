package org.funlearning.activities;

import java.util.ArrayList;
import java.util.Random;

import org.funlearning.R;
import org.funlearning.utils.ImagesJSONParser;
import org.funlearning.utils.Speak;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class GameActivity extends Activity implements OnClickListener {
	private static final int SMALL_A_CODE = 97; //small a
	private static final int NO_LETTERS = 24; //no of letters in the alphabet

	private static final Random RANDOM = new Random();

	private ArrayList<Integer> lettersSpoken = new ArrayList<Integer>();
	private ImagesJSONParser allImages;

	private ImageView ivLetter;
	private ImageView ivImage1;
	private ImageView ivImage2;
	private ImageView ivImage3;
	private ImageView ivImage4;

	private int[] currentImages;

	private int correctImage;
	private Speak mTts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.game);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		mTts = new Speak(this);
		allImages = new ImagesJSONParser(this);

		ivLetter = (ImageView) findViewById(R.id.ivLetter);
		ivImage1 = (ImageView) findViewById(R.id.ivImage1);
		ivImage2 = (ImageView) findViewById(R.id.ivImage2);
		ivImage3 = (ImageView) findViewById(R.id.ivImage3);
		ivImage4 = (ImageView) findViewById(R.id.ivImage4);

		currentImages = new int[5];

		generateGame();		

		ivLetter.setOnClickListener(this);
		ivImage1.setOnClickListener(this);
		ivImage2.setOnClickListener(this);
		ivImage3.setOnClickListener(this);
		ivImage4.setOnClickListener(this);

		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		readLetter();
		super.onStart();
	}

	private void readLetter() {
		String toSay = String.valueOf((char)(currentImages[correctImage] + SMALL_A_CODE)) + " is for ";
		mTts.speak(toSay, TextToSpeech.QUEUE_FLUSH);
	}

	private void readImage(int imageNo) {
		String toSay = allImages.getAt(imageNo);
		mTts.speak(toSay, TextToSpeech.QUEUE_FLUSH);
	}

	@Override
	protected void onDestroy() {
		mTts.destroy();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO make it more baby proof
		removeListeners();
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
		addListeners();
	}

	private void imageSelected(int imageSelected) {
		readImage(currentImages[imageSelected]);
		if (correctImage == imageSelected) {
			mTts.sayRandomCongratulation();
			mTts.playSilence();
			boolean go = mTts.isPlaing();
			while (go) {
				go = mTts.isPlaing();
			}
			generateGame();
			readLetter();
		} else {
			mTts.sayRandomTryAgain();
		}
	}

	private void addListeners() {
		ivImage1.setClickable(true);
		ivImage2.setClickable(true);
		ivImage3.setClickable(true);
		ivImage4.setClickable(true);
	}

	private void removeListeners() {
		ivImage1.setClickable(false);
		ivImage2.setClickable(false);
		ivImage3.setClickable(false);
		ivImage4.setClickable(false);
	}

	private void generateGame() {
		int letterLenght = NO_LETTERS;
		int letterPos = RANDOM.nextInt(letterLenght);
		while (lettersSpoken.contains(letterPos)) {
			letterPos = RANDOM.nextInt(letterLenght);
		}
		lettersSpoken.add(letterPos);

		if (lettersSpoken.size() > 15) {
			lettersSpoken.remove(0);
		}

		correctImage = RANDOM.nextInt(4) + 1;

		String a_big = String.valueOf((char)(letterPos + SMALL_A_CODE)) + "_big";

		ivLetter.setImageResource(this.getResources().getIdentifier(
				"drawable/" + a_big, null, this.getPackageName()));

		for (int i = 1; i < 5; i++) {
			String ivName = "ivImage" + Integer.toString(i);
			String a_image;

			ImageView iv = (ImageView) findViewById(this.getResources()
					.getIdentifier("id/" + ivName, null, this.getPackageName()));

			if (i == correctImage) {
				currentImages[i] = letterPos;
				a_image = allImages.getAt(letterPos);
				iv.setImageResource(this.getResources().getIdentifier(
						"drawable/" + a_image, null, this.getPackageName()));
			} else {
				boolean ok = false;
				int randomImage = 0;
				while (!ok) {
					randomImage = allImages.getRandomWordPos();
					ok = true;
					if (letterPos == randomImage)
						ok = false;
					for (int j = 1; j < i; j++) {
						if (randomImage == currentImages[j])
							ok = false;
					}
				}
				currentImages[i] = randomImage;
				a_image = allImages.getAt(randomImage);
				iv.setImageResource(this.getResources().getIdentifier(
						"drawable/" + a_image, null, this.getPackageName()));
			}
		}
	}
}
