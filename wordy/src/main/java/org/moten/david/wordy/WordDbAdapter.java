package org.moten.david.wordy;

import android.content.Context;
import android.database.Cursor;
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

	public void createWord(String word) {
		DatabaseHelper.createWord(database, word);
	}

	public Cursor getCursor() {
		return dbHelper.getReadableDatabase().query(DATABASE_TABLE,
				new String[] { "_id", "word" }, null, null, null, null,
				"word asc");
	}

	public void close() {
		dbHelper.close();
	}

}
