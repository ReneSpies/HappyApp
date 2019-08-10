package com.aresid.happyapp;

import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Created on: 19.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class Legalities {

	private static final String TAG = "Legalities";
	private              String mTitle, mContent;

	/**
	 * Constructor.
	 *
	 * @param title   Title for the specific legality.
	 * @param content Content for the specific legality.
	 */
	Legalities(String title, String content) {

		Log.d(TAG, "Legalities:true");
		mTitle = title;
		mContent = content;
	}

	/**
	 * Getter for the title of the specific legality.
	 *
	 * @return Title of the specific legality.
	 */
	public String getTitle() {

		Log.d(TAG, "getTitle:true");
		return mTitle;
	}

	/**
	 * Setter for the title for the specific legality.
	 *
	 * @param title Title for the specific legality.
	 */
	public void setTitle(@NonNull String title) {

		Log.d(TAG, "setTitle:true");
		mTitle = title;
	}

	/**
	 * Getter for the content of the specific legality.
	 *
	 * @return Content of the specific legality.
	 */
	public String getContent() {

		Log.d(TAG, "getContent:true");
		return mContent;
	}

	/**
	 * Setter for the content for the specific legality.
	 *
	 * @param content Content for the specific legality.
	 */
	public void setContent(@NonNull String content) {

		Log.d(TAG, "setContent:true");
		mContent = content;
	}
}
