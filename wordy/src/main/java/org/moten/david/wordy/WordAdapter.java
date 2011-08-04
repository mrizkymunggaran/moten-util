package org.moten.david.wordy;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class WordAdapter {

	private final Context context;
	private DatabaseHelper dbHelper;

	public WordAdapter(Context context) {
		this.context = context;
	}

	public WordAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		dbHelper.createDatabase();
		dbHelper.openDatabase();
		return this;
	}

	public Cursor getAnagrams() {
		return dbHelper.getAnagrams("abacus");
	}

	public void close() {
		dbHelper.close();
	}

	public boolean isValidWord(String word) {
		return dbHelper.isValidWord(word);
	}

}
