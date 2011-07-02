package org.moten.david.wordy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "wordy";

	private static final int DATABASE_VERSION = 8;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table word (word text primary key);";
	public static final String KEY_WORD = "word";
	private static final String DATABASE_TABLE = "word";

	private final Context context;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		InputStream is = context.getResources().openRawResource(R.raw.words);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			while ((line = br.readLine()) != null)
				createWord(database, line.trim());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a new todo If the todo is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 * 
	 * @param database
	 */
	public static long createWord(SQLiteDatabase database, String word) {
		ContentValues initialValues = createContentValues(word);
		return database.insert(DATABASE_TABLE, null, initialValues);
	}

	private static ContentValues createContentValues(String word) {
		ContentValues values = new ContentValues();
		values.put(KEY_WORD, word);
		return values;
	}

	// Method is called during an upgrade of the database, e.g. if you increase
	// the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(DatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS word");
		onCreate(database);
	}
}