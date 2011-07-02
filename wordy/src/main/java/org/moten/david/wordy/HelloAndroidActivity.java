package org.moten.david.wordy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class HelloAndroidActivity extends Activity {

	private static String TAG = "wordy";
	private WordDbAdapter dbAdapter;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.main);
		dbAdapter = new WordDbAdapter(this);
		dbAdapter.open();
		dbAdapter.createWord("abaco");
		dbAdapter.close();
	}

}
