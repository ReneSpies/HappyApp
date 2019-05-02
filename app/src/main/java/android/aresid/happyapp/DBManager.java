package android.aresid.happyapp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Created on: 02.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */


public class DBManager
		extends SQLiteOpenHelper
{
	public static final String TABLE_USER_ACCOUNT_SIGNATURE = "account signature";
	public static final String TABLE_USER_ACCOUNT_LEVEL = "account level";
	public static final String COLUMN_FIRESTORE_ID = "_firestore-id";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_BIRTHDATE = "birthdate";
	public static final String COLUMN_FIRST_NAME = "first-name";
	public static final String COLUMN_SURNAME = "surname";
	public static final String COLUMN_LEGALITIES = "legalities";
	public static final String COLUMN_LEGALITIES_VERSION = "legalities-version";
	public static final String COLUMN_ACCOUNT_LEVEL = "account-level";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "userdata.db";

	public DBManager(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version)
	{
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String query = "CREATE TABLE " + TABLE_USER_ACCOUNT_SIGNATURE + "(" + COLUMN_FIRESTORE_ID + " INTEGER PRIMARY KEY " + COLUMN_EMAIL + " TEXT "
				+ COLUMN_BIRTHDATE + " " + COLUMN_FIRST_NAME + " TEXT " + COLUMN_SURNAME + " TEXT " + COLUMN_LEGALITIES + " TEXT " + COLUMN_LEGALITIES_VERSION + " TEXT " + ");";

		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}







}
