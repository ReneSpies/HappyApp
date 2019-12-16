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

	private static final String             TAG = "SubscriptionPool";
	private              List<Subscription> mSubscriptions;

	private SubscriptionPool(List<Subscription> subscriptions) {

		mSubscriptions = subscriptions;

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

	static class Builder {

		private static final String                                TAG = "Builder";
		private              OnSubscriptionPopulateSuccessListener mSuccessListener;

		/**
		 * Here I build the pool. Retrieve the available subscriptions from the console and save it in the pool.
		 *
		 * @return Built pool.
		 */
		SubscriptionPool build() {

			Log.d(TAG, "build:true");

			return new SubscriptionPool(new ArrayList<>());

		}

		/**
		 * Adds a new listener that is called when the build was successful!
		 *
		 * @param listener Listener that's called upon success.
		 * @return This.
		 */
		Builder addOnSuccessListener(OnSubscriptionPopulateSuccessListener listener) {

			Log.d(TAG, "addOnSuccessListener:true");

			mSuccessListener = listener;

			return this;

		}

	}

}
