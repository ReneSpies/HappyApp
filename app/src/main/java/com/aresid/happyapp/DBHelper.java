package com.aresid.happyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created on: 31.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class DBHelper
		extends SQLiteOpenHelper {
	static final         String   TABLE_SUBSCRIPTIONS              = "Subscriptions";
	static final         String   TABLE_USERDATA                   = "Userdata";
	static final         String[] TABLE_USERDATA_COLUMN_NAMES      = new String[] {
			"firestore_id", "first_name", "surname", "email", "password", "birthdate", "accepted_legalities_version"
	};
	static final         String[] TABLE_SUBSCRIPTIONS_COLUMN_NAMES = new String[] {
			"icon", "title", "description", "price"
	};
	private static final String   TAG                              = "DBHelper";
	private static final String   DATABASE_NAME                    = "HappyApp_Database";
	private static final int      DATABASE_VERSION                 = 1;
	/**
	 * Constructor matching super.
	 *
	 * @param context Context from the calling activity.
	 */
	DBHelper(@Nullable Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate:true");
		try {
			// Creating the Database.
			db.execSQL("PRAGMA foreign_keys = ON;");
			// Create Userdata table.
			// This one is first because it gets referenced.
			db.execSQL(
					"CREATE TABLE IF NOT EXISTS " + TABLE_USERDATA + "(" + TABLE_USERDATA_COLUMN_NAMES[0] + " TEXT PRIMARY KEY NOT NULL UNIQUE," + TABLE_USERDATA_COLUMN_NAMES[1] + " TEXT NOT NULL," +
					" " + TABLE_USERDATA_COLUMN_NAMES[2] + " TEXT NOT NULL, " + TABLE_USERDATA_COLUMN_NAMES[3] + "TEXT NOT NULL, " + TABLE_USERDATA_COLUMN_NAMES[4] + " TEXT NOT NULL, " +
					TABLE_USERDATA_COLUMN_NAMES[5] + " TEXT NOT NULL, " + TABLE_USERDATA_COLUMN_NAMES[6] + " REAL NOT NULL);");
			// Create Subscriptions table.
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SUBSCRIPTIONS + "(" + TABLE_SUBSCRIPTIONS_COLUMN_NAMES[0] + " BLOB NOT NULL, " + TABLE_SUBSCRIPTIONS_COLUMN_NAMES[1] +
			           "TEXT PRIMARY KEY NOT NULL, " + TABLE_SUBSCRIPTIONS_COLUMN_NAMES[2] + " TEXT NOT NULL, " + TABLE_SUBSCRIPTIONS_COLUMN_NAMES[3] + " TEXT NOT NULL," +
			           "CONSTRAINT SubscriptionFK FOREIGN KEY(" + TABLE_SUBSCRIPTIONS_COLUMN_NAMES[1] + ")" + "REFERENCES Userdata(" + TABLE_USERDATA_COLUMN_NAMES[0] + ")" +
			           "ON DELETE RESTRICT ON UPDATE CASCADE);");
			Log.d(TAG, "onCreate: Database created in: " + db.getPath());
		} catch (SQLException e) {
			Log.e(TAG, "onCreate: ", e);
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade:true");
	}
	/**
	 * Get userdata from user with specified ID.
	 *
	 * @return HashMap of userdata from the database.
	 */
	HashMap<String, Object> getUserdata(String firestoreID) {
		Log.d(TAG, "getUserdata:true");
		// Get a readable DB object.
		SQLiteDatabase db = getReadableDatabase();
		// The Cursor that holds the data I retrieve from the DB via query method.
		Cursor cursor = db.query(TABLE_USERDATA, // The table to get info from.
		                         null, // The column names to get info from. null = all.
		                         TABLE_USERDATA_COLUMN_NAMES[0] + " = '" + firestoreID + "'", // The row to get info from.
		                         null, // Needed if ?s is included in selection argument.
		                         null, // A filter declared how to group rows.
		                         null, // A filter declared which row groups to include in the cursor.
		                         null // How to order rows.
		);
		HashMap<String, Object> mapOfUserInfo = new HashMap<>();
		if (cursor.moveToNext()) {
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				mapOfUserInfo.put(cursor.getColumnName(i), cursor.getString(i));
				Log.d(TAG, "getUserdata: mapOfUserInfo updated = " + mapOfUserInfo.get(cursor.getColumnName(i)));
			}
		}
		cursor.close();
		return mapOfUserInfo;
	}
	/**
	 * Insert new userdata into the database.
	 */
	void insertUser(String firestoreID, String firstName, String surname, String email, String password, String birthdate, String acceptedLegalitiesVersion) {
		Log.d(TAG, "insertUser:true");
		Log.d(TAG, "insertUser: firestoreID = " + firestoreID);
		Log.d(TAG, "insertUser: firstName = " + firstName);
		Log.d(TAG, "insertUser: surname = " + surname);
		Log.d(TAG, "insertUser: email = " + email);
		Log.d(TAG, "insertUser: password = " + password);
		Log.d(TAG, "insertUser: birthdate = " + birthdate);
		Log.d(TAG, "insertUser: acceptedLegalitiesVersion = " + acceptedLegalitiesVersion);
		ContentValues values = new ContentValues();
		values.put(TABLE_USERDATA_COLUMN_NAMES[0], firestoreID);
		values.put(TABLE_USERDATA_COLUMN_NAMES[1], firstName);
		values.put(TABLE_USERDATA_COLUMN_NAMES[2], surname);
		values.put(TABLE_USERDATA_COLUMN_NAMES[3], email);
		values.put(TABLE_USERDATA_COLUMN_NAMES[4], password);
		values.put(TABLE_USERDATA_COLUMN_NAMES[5], birthdate);
		values.put(TABLE_USERDATA_COLUMN_NAMES[6], acceptedLegalitiesVersion);
		SQLiteDatabase db = getWritableDatabase();
		Log.d(TAG, "insertUser: db path = " + db.getPath());
		try {
			db.insertOrThrow(TABLE_USERDATA, null, values);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}
	/**
	 * Insert a new subscription into the database.
	 * This is called from EntryActivity onCreate and checks the version code in SharedPrefs to see if it has changed.
	 * If it has changed it gets updated from there only.
	 */
	void insertSubscription(Bitmap icon, String title, String description, String price) {
		Log.d(TAG, "insertSubscription:true");
		Log.d(TAG, "insertSubscription: icon = " + icon);
		Log.d(TAG, "insertSubscription: title = " + title);
		Log.d(TAG, "insertSubscription: description = " + description);
		Log.d(TAG, "insertSubscription: price = " + price);
		// Convert the bitmap into a byte[] so it can be saved in database.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		icon.compress(Bitmap.CompressFormat.PNG, 100, bos);
		ContentValues values = new ContentValues();
		values.put(TABLE_SUBSCRIPTIONS_COLUMN_NAMES[0], bos.toByteArray() /*the icon value is now compressed to byte[]*/);
		values.put(TABLE_SUBSCRIPTIONS_COLUMN_NAMES[1], title);
		values.put(TABLE_SUBSCRIPTIONS_COLUMN_NAMES[2], description);
		values.put(TABLE_SUBSCRIPTIONS_COLUMN_NAMES[3], price);
	}
	/**
	 * Get subscriptions from the database.
	 *
	 * @return HashMap of subscriptions from the database.
	 */
	HashMap<String, String> getSubscriptions(String subscriptionName) {
		Log.d(TAG, "getSubscriptions:true");
		// String[] for columns to return.
		String[] arrayOfColumns = new String[] {
				TABLE_SUBSCRIPTIONS_COLUMN_NAMES[1], TABLE_SUBSCRIPTIONS_COLUMN_NAMES[2], TABLE_SUBSCRIPTIONS_COLUMN_NAMES[3]
		};
		// Get a readable DB object.
		SQLiteDatabase db = getReadableDatabase();
		// The Cursor that holds the data I retrieve from the DB via query method.
		Cursor cursor = db.query(TABLE_SUBSCRIPTIONS, arrayOfColumns, TABLE_SUBSCRIPTIONS_COLUMN_NAMES[1] + " = '" + subscriptionName + "'", null, null, null, null);
		HashMap<String, String> mapOfSubscriptionData = new HashMap<>();
		if (cursor.moveToNext()) {
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				mapOfSubscriptionData.put(cursor.getColumnName(i), cursor.getString(i));
			}
		}
		cursor.close();
		return mapOfSubscriptionData;
	}
	/**
	 * Get subscription icon for specified subscription.
	 *
	 * @return Subscription icon as bitmap.
	 */
	Bitmap getSubscriptionIcon(String subscriptionName) {
		Log.d(TAG, "getSubscriptionIcon:true");
		// TODO: TODO!
		return null;
	}
}
