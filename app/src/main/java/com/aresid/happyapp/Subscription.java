package com.aresid.happyapp;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created on: 13/11/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

class Subscription {

	private String       mTitle;
	private String       mDescription;
	private String       mPrice;
	private boolean      mHasBulletpoints;
	private List<String> mBulletspoints;
	private Drawable     mIcon;

	// TODO: subscription object. think rené, think!

	private static final String TAG = "Subscription";

	/**
	 * Constructor.
	 */
	Subscription() {

		Log.d(TAG, "Subscription: empty constructor");

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

	Subscription(String title, String description, String price, Drawable icon) {

		Log.d(TAG, "Subscription: constructor without bulletpoints");

		setTitle(title);
		setDescription(description);
		setPrice(price);
		setIcon(icon);

	}

	/**
	 * Sets the subscriptions title to the given String.
	 *
	 * @param title Your desired title name.
	 */
	void setTitle(String title) {

		Log.d(TAG, "setTitle:true");

		mTitle = title;

	}

	/**
	 * Sets the subscriptions price to the given float and converts it to a String.
	 *
	 * @param price Your desired price.
	 */
	void setPrice(String price) {

		Log.d(TAG, "setPrice:true");

		mPrice = price;

	}

	/**
	 * Sets the subscriptions description to the given String as bulletpoints.
	 * Throws IllegalStateException if subscription already has a description.
	 *
	 * @param bulletpoints Your desired bulletpoints.
	 */
	void setBulletpoints(String... bulletpoints) {

		Log.d(TAG, "setBulletpoints:true");

		if (getDescription() == null) {

			mBulletspoints.addAll(Arrays.asList(bulletpoints));

			setHasBulletpoints(true);

		} else {

			throw new IllegalStateException("Subscription already has bulletpoints");

		}

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
	 * Throws IllegalStateException if subscription already has bulletpoints.
	 *
	 * @param description Your desired description as text.
	 */
	void setDescription(String description) {

		Log.d(TAG, "setDescription:true");

		if (mBulletspoints == null || !mHasBulletpoints) {

			mDescription = description;

			setHasBulletpoints(false);

		} else {

			throw new IllegalStateException("Subscription already has bulletpoints");

		}

	}

	/**
	 * Returns the subscriptions icon.
	 *
	 * @return Icon.
	 */
	Drawable getIcon() {

		Log.d(TAG, "getIcon:true");

		return mIcon;

	}

	/**
	 * Sets the subscriptions icon to the given drawable.
	 *
	 * @param icon Your desired icon.
	 */
	void setIcon(Drawable icon) {

		Log.d(TAG, "setIcon:true");

		mIcon = icon;

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

	Subscription(String title, String price, Drawable icon, String... bulletpoints) {

		Log.d(TAG, "Subscription: constructor with bulletpoints");

		setTitle(title);
		setPrice(price);
		setIcon(icon);
		setBulletpoints(bulletpoints);

	}

}
