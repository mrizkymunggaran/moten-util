package org.moten.david.wordy;

import java.util.List;

import android.content.Context;
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

	public List<String> getAnagrams(String word) {
		return dbHelper.getAnagrams(word);
	}

	public List<String> getAnagramsExtra(String word) {
		return dbHelper.getAnagramsExtra(word);
	}

	public void close() {
		dbHelper.close();
	}

	public boolean isValidWord(String word) {
		return dbHelper.isValidWord(word);
	}

}
