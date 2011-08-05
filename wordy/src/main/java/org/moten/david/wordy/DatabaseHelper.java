package org.moten.david.wordy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHelper.class.getSimpleName();

	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/org.moten.david.wordy/databases/";

	private static String DB_NAME = "word.db";

	private SQLiteDatabase db;

	private final Context context;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.context = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDatabase() {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
		} else {
			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			Log.w(TAG, e.getMessage());
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		Log.i(TAG, "creating db from asset: " + DB_NAME);
		InputStream myInput = context.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
		Log.i(TAG, "created database from asset");

	}

	public void openDatabase() {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		Log.i(TAG, "opening database: " + myPath);
		db = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);
		Log.i(TAG, "opened database");

	}

	@Override
	public synchronized void close() {
		if (db != null)
			db.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// Add your public helper methods to access and get content from the
	// database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd
	// be easy
	// to you to create adapters for your views.

	// Database fields
	public static final String WORD_COLUMN = "word";
	private static final String WORD_TABLE = "word";

	public Cursor getAnagrams(String word) {
		char[] chars = word.toCharArray();
		Arrays.sort(chars);
		String wordSorted = new String(chars);
		Map<String, String> projectionMap = new HashMap<String, String>();
		projectionMap.put("_id", "_id");
		projectionMap.put("word", "word");
		projectionMap.put("word_sorted", "word_sorted");
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables("word");
		builder.setProjectionMap(projectionMap);
		Cursor cursor = builder.query(db, new String[] { "_ID", "WORD" },
				"WORD_SORTED = ?", new String[] { wordSorted.toUpperCase() },
				null, null, null);
		return cursor;
	}

	public boolean isValidWord(String word) {
		// Cursor c = db.rawQuery("select word from word where word=?",
		// new String[] { word.toUpperCase() });
		Cursor c = db.query("WORD", new String[] { "WORD" }, "WORD=?",
				new String[] { word.toUpperCase() }, null, null, null);
		c.moveToFirst();
		boolean isValid = c.getCount() > 0;
		c.close();
		return isValid;
	}

}
