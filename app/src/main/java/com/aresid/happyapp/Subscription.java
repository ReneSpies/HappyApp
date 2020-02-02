package com.aresid.happyapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.android.billingclient.api.SkuDetails;

/**
 * Created on: 13/11/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class Subscription {
	private static final int        PRIO_BRONZE   = 0;
	private static final int        PRIO_SILVER   = 1;
	private static final int        PRIO_GOLD     = 2;
	private static final int        PRIO_PLATINUM = 3;
	private static final int        PRIO_DEFAULT  = -1;
	private static final String     TAG           = "Subscription";
	private              String     mTitle;
	private              String     mDescription;
	private              String     mPrice;
	private              Drawable   mIcon;
	private              String     mId;
	private              Context    mContext;
	private              int        mPriority;
	private              SkuDetails mSkuDetails;
	/**
	 * Constructor.
	 */
	Subscription(Context context) {
		Log.d(TAG, "Subscription: empty constructor");
		mContext = context;
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
		if (title.contains("(HappyApp)")) {
			mTitle = title.replace("(HappyApp)", "")
			              .trim();
		} else {
			mTitle = title.trim();
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
		mDescription = description;
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
	 * Getter for subscriptions id.
	 *
	 * @return Guess.
	 */
	String getId() {
		return mId;
	}
	/**
	 * Setter for subscriptions id.
	 *
	 * @param id Guess.
	 */
	void setId(String id) {
		if ((id.contains("bronze"))) {
			setIcon(mContext.getDrawable(R.drawable.bronze_icon));
			setPriority(PRIO_BRONZE);
		} else if ((id.contains("silver"))) {
			setIcon(mContext.getDrawable(R.drawable.silver_icon));
			setPriority(PRIO_SILVER);
		} else if ((id.contains("gold"))) {
			setIcon(mContext.getDrawable(R.drawable.gold_icon));
			setPriority(PRIO_GOLD);
		} else if ((id.contains("platinum"))) {
			setIcon(mContext.getDrawable(R.drawable.platinum_icon));
			setPriority(PRIO_PLATINUM);
		} else {
			setPriority(PRIO_DEFAULT);
		}
		mId = id;
	}
	/**
	 * Getter for subscriptions priority.
	 *
	 * @return Guess.
	 */
	int getPriority() {
		return mPriority;
	}
	/**
	 * Setter for subscriptions priority.
	 *
	 * @param priority Guess.
	 */
	private void setPriority(int priority) {
		Log.d(TAG, "setPriority:true");
		mPriority = priority;
	}
	/**
	 * Getter for sku details.
	 *
	 * @return Guess.
	 */
	SkuDetails getSkuDetails() {
		return mSkuDetails;
	}
	/**
	 * Setter for sku details.
	 *
	 * @param skuDetails Guess.
	 */
	void setSkuDetails(SkuDetails skuDetails) {
		mSkuDetails = skuDetails;
	}
}
