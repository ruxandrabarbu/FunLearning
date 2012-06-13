package org.funlearning.activities;

import java.util.Locale;
import java.util.Random;

import org.funlearning.R;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class AlphabetActivity extends Activity implements
		TextToSpeech.OnInitListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
	private TextToSpeech mTts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.alphabet);

		ImageView ivLetterObject = (ImageView) findViewById(R.id.ivLetterObject);
		ivLetterObject.setImageResource(R.drawable.apple);

		// Initialize text-to-speech. This is an asynchronous operation.
		// The OnInitListener (second argument) is called after initialization
		// completes.

		mTts = new TextToSpeech(this, this);

        // Gesture detection
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };		

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

	// Implements TextToSpeech.OnInitListener.
	public void onInit(int status) {
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will
			// indicate this.
			// int result = mTts.setLanguage(Locale.US);
			// Try this someday for some interesting results.
			int result = mTts.setLanguage(Locale.US);		
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Lanuage data is missing or the language is not supported.
				// missing data, install it
//				Intent installIntent = new Intent();
//				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//				startActivity(installIntent);
				Log.e("AlphabetAvtivity", "Language is not available.");
			} else {
				// Check the documentation for other possible result codes.
				// For example, the language may be available for the locale,
				// but not for the specified country and variant.
				// Greet the user.
				sayHello();
			}
		} else {
			// Initialization failed.
			Log.e("AlphabetAvtivity", "Could not initialize TextToSpeech.");
		}
	}

	private static final Random RANDOM = new Random();
	private static final String[] HELLOS = { "Hello", "Salutations",
			"Greetings", "Howdy", "What's crack-a-lackin?",
			"That explains the stench!" };

	private void sayHello() {
		// Select a random hello.
		int helloLength = HELLOS.length;
		String hello = HELLOS[RANDOM.nextInt(helloLength)];
		mTts.speak(hello, TextToSpeech.QUEUE_FLUSH, // Drop all pending entries
													// in the playback queue.
				null);
	}
	
    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // left Swipe
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    //  right swipe
                	sayHello();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }

}
