package co.joyatwork.droidz;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

public class DroidzActivity extends Activity {

	private static final String TAG = DroidzActivity.class.getSimpleName();

	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MainGamePanel.Game game;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String value = extras.getString("game");
		    Log.d(TAG, "extras " + value);
		    game = MainGamePanel.Game.valueOf(value);
		}
		else {
			game = MainGamePanel.Game.DROID;
		}
		// requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// making it full screen
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// set our MainGamePanel as the View
		setContentView(new MainGamePanel(this, game));
		Log.d(TAG, "onCreate");
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}


	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}


	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
	}


	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.droidz, menu);
        return true;
    }
    
}
