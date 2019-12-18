package com.aresid.happyapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created on: 13/11/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

class SubscriptionPool {

	private static final String                        TAG = "SubscriptionPool";
	private              HashMap<String, Subscription> mSubscriptions;

	SubscriptionPool() {

		Log.d(TAG, "SubscriptionPool: empty constructor");

		mSubscriptions = new HashMap<>();

	}

	void addSubscription(Subscription subscription) {

		Log.d(TAG, "addSubscription:true");

		mSubscriptions.put(subscription.getId(), subscription);

		Log.d(TAG, "addSubscription: size = " + mSubscriptions.size());
		Log.d(TAG, "addSubscription: count = " + getSubscriptionCount());

	}

	/**
	 * Getter for count of all subscriptions in the pool starting at 0.
	 *
	 * @return Int count.
	 */
	int getSubscriptionCount() {

		Log.d(TAG, "getSubscriptionCount = " + mSubscriptions.size());

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

		List<Subscription> listOfSubscriptions = new ArrayList<>(mSubscriptions.values());

		return listOfSubscriptions.get(index);

	}

	Subscription getSubscription(String subscriptoinId) {

		Log.d(TAG, "getSubscription:true");

		return mSubscriptions.get(subscriptoinId);

	}

}
