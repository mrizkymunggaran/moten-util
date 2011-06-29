package org.moten.david.wordy;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class WordDbAdapter {

	// Database fields
	public static final String KEY_WORD = "word";
	private static final String DATABASE_TABLE = "word";
	private final Context context;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	public WordDbAdapter(Context context) {
		this.context = context;
	}

	public WordDbAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new todo If the todo is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */
	public long createWord(String word) {
		ContentValues initialValues = createContentValues(word);
		return database.insert(DATABASE_TABLE, null, initialValues);
	}

	private ContentValues createContentValues(String word) {
		ContentValues values = new ContentValues();
		values.put(KEY_WORD, word);
		return values;
	}
}
