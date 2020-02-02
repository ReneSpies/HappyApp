package com.aresid.happyapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on: 13/11/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class SubscriptionPool {
	private static final String             TAG = "SubscriptionPool";
	private              List<Subscription> mSubscriptions;
	SubscriptionPool() {
		Log.d(TAG, "SubscriptionPool: empty constructor");
		mSubscriptions = new ArrayList<>();
	}
	/**
	 * As the query returns the subs in some weird sequence I sort the subs here and add their icons.
	 */
	void sort() {
		Log.d(TAG, "sort:true");
		Collections.sort(mSubscriptions, (o1, o2) -> o1.getPriority() - (o2.getPriority()));
	}
	void addSubscription(Subscription subscription) {
		Log.d(TAG, "addSubscription:true");
		mSubscriptions.add(subscription);
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
		return mSubscriptions.get(index);
	}
}
