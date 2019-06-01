package android.aresid.happyapp;

import android.annotation.SuppressLint;
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


public class AvailableSubscriptionsDBelper
		extends SQLiteOpenHelper
{
	private static final String TAG = "AvailableSubscriptionsDBelper";
	private static final String DATABASE_NAME = "AvailableSubscriptions";
	private static final int DATABASE_VERSION = 1;




	/**
	 * Constructor matching super.
	 *
	 * @param context Context from the calling activity.
	 */
	public AvailableSubscriptionsDBelper(@Nullable Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}




	@SuppressLint ("LongLogTag")
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.d(TAG, "onCreate:true");

		// Creating the Database.
		db.execSQL("PRAGMA foreign_keys = ON;");

		db.execSQL(
				"CREATE TABLE FreePackage(" + "kurzbezeichnung TEXT PRIMARY KEY," + " titel TEXT NOT NULL," + " autoren TEXT NOT NULL," + " " +
						"verlag_ort_url TEXT NOT NULL," + " publikationen TEXT);");
		db.execSQL(
				"CREATE TABLE Stichworte(" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + " stichwort TEXT NOT NULL," + " quelle TEXT NOT NULL," + " " + "fundstelle TEXT NOT NULL," + " text TEXT NOT NULL," + " CONSTRAINT QuellenFK FOREIGN KEY(quelle)" + " REFERENCES " + "Quellen(kurzbezeichnung)" + " ON DELETE CASCADE ON UPDATE CASCADE);");


		Log.d(TAG, "onCreate: Database created in: " + db.getPath());
	}




	@SuppressLint ("LongLogTag")
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.d(TAG, "onUpgrade:true");
	}







}
