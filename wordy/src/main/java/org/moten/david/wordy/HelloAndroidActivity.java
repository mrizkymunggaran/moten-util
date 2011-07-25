package org.moten.david.wordy;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class HelloAndroidActivity extends Activity {

	private static String TAG = "wordy";
	private WordAdapter dbAdapter;

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
		dbAdapter = new WordAdapter(this);

		ListView listView = (ListView) this.findViewById(R.id.list);
		dbAdapter.open();
		Cursor cursor = dbAdapter.getAnagrams();
		startManagingCursor(cursor);

		// the desired columns to be bound
		String[] columns = new String[] { "word" };
		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.list_item };

		// create the adapter using the cursor pointing to the desired data as
		// well as the layout information
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.main, cursor, columns, to);

		listView.setAdapter(adapter);
	}
}
