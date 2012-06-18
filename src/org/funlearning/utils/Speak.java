package org.funlearning.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;

public class Speak implements TextToSpeech.OnInitListener,
		OnUtteranceCompletedListener {

	private static final String[] Congratulations = { "well done", "good job",
			"you did well" };
	private static final String[] TryAgain = { "guess again", "try again" };

	private static final Random RANDOM = new Random();
	
	private TextToSpeech mTts;
	private boolean isPlaying;
	private HashMap<String, String> params;

	/**
	 * Called when the activity is first created.
	 */

	public Speak(Context context) {
		mTts = new TextToSpeech(context, this);

		params = new HashMap<String, String>();
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
	}

	/**
	 * Executed when a new TTS is instantiated. Some static text is spoken via
	 * TTS here.
	 * 
	 * @param i
	 */
	public void onInit(int status) {
		mTts.setOnUtteranceCompletedListener(this);
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			int result = mTts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA) {
				// Lanuage data is missing, install it
				// Intent installIntent = new Intent();
				// installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				// startActivity(installIntent);
				Log.e("FunLearningAvtivity", "Language is not available.");
			} else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Language is not supported.
				Log.e("FunLearningAvtivity", "Language is not supported.");
			} else {
				// Check the documentation for other possible result codes.
				// For example, the language may be available for the locale,
				// but not for the specified country and variant.

			}
		} else {
			// Initialization failed.
			Log.e("FunLearningAvtivity", "Could not initialize TextToSpeech.");
		}
	}

	/**
	 * Be kind, once you've finished with the TTS engine, shut it down so other
	 * applications can use it without us interfering with it :)
	 */

	public void destroy() {
		// Don't forget to shutdown!
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}
	}

	@Override
	public void onUtteranceCompleted(String utteranceId) {
		isPlaying = false;
	}

	public void speak(String toSay, int queueMode) {
		isPlaying = true;
		mTts.speak(toSay, queueMode, null);
	}

	public void playSilence() {
		mTts.playSilence((long) 700, TextToSpeech.QUEUE_ADD, params);
	}

	public boolean isPlaing() {
		return isPlaying;
	}

	public void setRare() {
		mTts.setSpeechRate((float) 0.6);
	}

	public void setNormal() {
		mTts.setSpeechRate(1);
	}

	public void sayRandomCongratulation() {
		String toSay = "   "
				+ Congratulations[RANDOM.nextInt(Congratulations.length)];
		mTts.speak(toSay, TextToSpeech.QUEUE_ADD, null);
	}
	
	public void sayRandomTryAgain() {
		String toSay = "   "
			+ TryAgain[RANDOM.nextInt(TryAgain.length)];
	mTts.speak(toSay, TextToSpeech.QUEUE_ADD, null);		
	}
}