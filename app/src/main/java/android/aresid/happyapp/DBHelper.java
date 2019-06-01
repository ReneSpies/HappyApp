package android.aresid.happyapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;


/**
 * Created on: 31.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */


public class DBHelper
		extends SQLiteOpenHelper
{
	private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "HappyApp_Database";
	private static final int DATABASE_VERSION = 1;




	/**
	 * Constructor matching super.
	 *
	 * @param context Context from the calling activity.
	 */
	public DBHelper(@Nullable Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}




	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.d(TAG, "onCreate:true");

		// Creating the Database.
		db.execSQL("PRAGMA foreign_keys = ON;");

		// Create Subscriptions table.
		db.execSQL(
				"CREATE TABLE IF NOT EXISTS Subscriptions(icon BLOB NOT NULL," + " title TEXT PRIMARY KEY NOT NULL," + " description TEXT NOT NULL," + " " + "price TEXT NOT NULL);");

		// Create Userdata table.
		db.execSQL(
				"CREATE TABLE IF NOT EXISTS Userdata(firestore_id TEXT PRIMARY KEY NOT NULL," + " first_name TEXT NOT NULL," + " surname TEXT NOT " + "NULL," + " email TEXT NOT NULL," + " password TEXT NOT NULL," + " birthdate TEXT NOT NULL," + " accepted_legalities_version" + " REAL NOT NULL);");

		Log.d(TAG, "onCreate: Database created in: " + db.getPath());
	}




	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.d(TAG, "onUpgrade:true");
	}







}
