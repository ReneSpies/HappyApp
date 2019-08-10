package com.aresid.happyapp;

import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Created on: 19.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class TermsAndConditions
		extends Legalities {

	private static final String TAG = "TermsAndConditions";
	private              String mTitle, mContent;

	/**
	 * Constructor.
	 *
	 * @param title   Title for t&c.
	 * @param content Content for t&c.
	 */
	TermsAndConditions(String title, String content) {

		super(title, content);
		Log.d(TAG, "TermsAndConditions:true");
		mTitle = title;
		mContent = content;
	}

	/**
	 * Getter for title of t&c.
	 *
	 * @return Title of t&c.
	 */
	@Override
	public String getTitle() {

		Log.d(TAG, "getTitle:true");
		return mTitle;
	}

	/**
	 * Setter for title for t&c.
	 *
	 * @param title Title for t&c.
	 */
	@Override
	public void setTitle(@NonNull String title) {

		Log.d(TAG, "setTitle:true");
		mTitle = title;
	}

	/**
	 * Getter for content of t&c.
	 *
	 * @return Content of t&c.
	 */
	@Override
	public String getContent() {

		Log.d(TAG, "getContent:true");
		return mContent;
	}

	/**
	 * Setter for content for t&c.
	 *
	 * @param content Content for t&c.
	 */
	@Override
	public void setContent(@NonNull String content) {

		Log.d(TAG, "setContent:true");
		mContent = content;
	}
}
