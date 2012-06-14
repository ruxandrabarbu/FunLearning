package org.funlearning.activities;

import java.util.Random;

import org.funlearning.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class GameActivity extends Activity implements OnClickListener {

	private static final String[] LettersSmall = { "a", "b", "c", "d", "e",
			"f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
			"s", "t", "u", "v", "w", "x", "y", "z" };
	private static final String[] LettersImage = { "apple", "bubbles",
			"cupcake", "door", "eraser", "fire", "grapes", "hydrant",
			"icecream", "jelly", "key", "lightbulb", "mountain", "nest",
			"onion", "pencil", "questionmark", "ring", "snail", "tulip",
			"umbrella", "vase", "watermelon", "xylophone", "yoyo", "zipper" };

	private static final Random RANDOM = new Random();

	private ImageView ivLetter;
	private ImageView ivImage1;
	private ImageView ivImage2;
	private ImageView ivImage3;
	private ImageView ivImage4;

	private int[] images;

	private int correctImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.game);

		ivLetter = (ImageView) findViewById(R.id.ivLetter);
		ivImage1 = (ImageView) findViewById(R.id.ivImage1);
		ivImage2 = (ImageView) findViewById(R.id.ivImage2);
		ivImage3 = (ImageView) findViewById(R.id.ivImage3);
		ivImage4 = (ImageView) findViewById(R.id.ivImage4);
		
		images = new int[5];

		generateGame();

		findViewById(R.id.ivLetter).setOnClickListener(this);
		findViewById(R.id.ivImage1).setOnClickListener(this);
		findViewById(R.id.ivImage2).setOnClickListener(this);
		findViewById(R.id.ivImage3).setOnClickListener(this);
		findViewById(R.id.ivImage4).setOnClickListener(this);

		super.onCreate(savedInstanceState);
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
					ok = (randomImage == correctImage) ? false : true;
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
