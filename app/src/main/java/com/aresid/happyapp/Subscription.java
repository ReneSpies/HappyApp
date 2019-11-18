package com.aresid.happyapp;

import android.util.Log;

import java.util.List;

/**
 * Created on: 13/11/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class Subscription {

	private String       mTitle;
	private String       mDescription;
	private String       mPrice;
	private boolean      mHasBulletpoints;
	private List<String> mBulletspoints;

	// TODO: subscription object. think rené, think!

	private static final String TAG = "Subscription";

	/**
	 * Constructor.
	 */
	Subscription() {

		Log.d(TAG, "Subscription:true");

		// constructor!

	}

	/**
	 * Sets the bool if bulletpoints are used.
	 *
	 * @param hasBulletpoints True if bulletpoints are used.
	 */
	private void setHasBulletpoints(boolean hasBulletpoints) {

		Log.d(TAG, "setHasBulletpoints:true");

		mHasBulletpoints = hasBulletpoints;

	}

	/**
	 * Returns the subscriptions title.
	 *
	 * @return Title.
	 */
	String getTitle() {

		Log.d(TAG, "getTitle:true");

		return mTitle;

	}

	/**
	 * Sets the subscriptions title to the given String.
	 *
	 * @param title Your desired title name.
	 */
	void setTitle(String title) {

		Log.d(TAG, "setTitle:true");

	}

	/**
	 * Returns the subscriptions description as single text if bulletpoints are not used.
	 *
	 * @return Description.
	 */
	String getDescription() {

		Log.d(TAG, "getDescription:true");

		return mDescription;

	}

	/**
	 * Sets the subscriptions description to the given String as a single text.
	 *
	 * @param description Your desired description as text.
	 */
	void setDescription(String description) {

		Log.d(TAG, "setDescription:true");

	}

	/**
	 * Sets the subscriptions description to the given String as bulletpoints.
	 *
	 * @param bulletpoints Your desired bulletpoints.
	 */
	void setDescription(String... bulletpoints) {

		Log.d(TAG, "setDescription:true");

	}

	/**
	 * Returns the subscriptions price as a formatted String.
	 *
	 * @return Price.
	 */
	String getPrice() {

		Log.d(TAG, "getPrice:true");

		return mPrice;

	}

	/**
	 * Sets the subscriptions price to the given float and converts it to a String.
	 *
	 * @param price Your desired price.
	 */
	void setPrice(float price) {

		Log.d(TAG, "setPrice:true");

	}

}
