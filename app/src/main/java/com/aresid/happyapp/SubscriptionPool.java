package com.aresid.happyapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on: 13/11/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

class SubscriptionPool {

	private List<Subscription> mSubscriptions;
	private static final String TAG = "SubscriptionPool";

	SubscriptionPool() {

		Log.d(TAG, "SubscriptionPool:true");

		// Constructor

		mSubscriptions = new ArrayList<>();

	}

	void addOnSuccessListener(OnSuccessListener listener) {

		Log.d(TAG, "addOnSuccessListener:true");

	}

	/**
	 * Here I init my pool. I grab the subscriptions from the google play console and load them into here when it is done.
	 */
	void initialize() {

		Log.d(TAG, "initialize:true");

	}

	/**
	 * Getter for count of all subscriptions in the pool starting at 0.
	 *
	 * @return Int count.
	 */
	int getSubscriptionCount() {

		Log.d(TAG, "getSubscriptions:true");

		return mSubscriptions.size();

	}

	/**
	 * Getter for a specific subscription object.
	 *
	 * @param index Index of the desired subscription.
	 * @return Subscription in index.
	 */
	Subscription getSubscription(int index) {

		Log.d(TAG, "getSubscription:true");

		return mSubscriptions.get(index);

	}

}
