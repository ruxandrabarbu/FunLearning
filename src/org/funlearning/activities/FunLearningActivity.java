package org.funlearning.activities;

import org.funlearning.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class FunLearningActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.btnExit).setOnClickListener(this);
        findViewById(R.id.btnWrite).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnExit:
			finish();
			break;
			
		case R.id.btnWrite:
            Intent intent = new Intent();
            intent.setClass(this, WriteActivity.class);
            startActivity(intent);
			break;

		default:
		}		
	}
}