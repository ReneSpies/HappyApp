package com.aresid.happyapp;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created on: 13/11/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

class SubscriptionPool {

	private List<Subscription> mSubscriptions;

	// TODO: think what i wanna do here lol

	private static final String TAG = "SubscriptionPool";

	SubscriptionPool() {

		Log.d(TAG, "SubscriptionPool:true");

		// Constructor

		mSubscriptions = new ArrayList<>();

	}

	/**
	 * This method lets you add a new subscription object to the subscription pool with a text description.
	 *
	 * @param title       How you call the subscription.
	 * @param description What does your subscription do?
	 * @param price       How much will your subscription cost?
	 * @param icon        A beautiful picture for this subscription.
	 */
	void addSubscription(String title, String description, String price, Drawable icon) {

		Log.d(TAG, "addSubscription:true");

		mSubscriptions.add(new Subscription(title, description, price, icon));

	}

	void addSubscriptions(Collection<? extends Subscription> subscriptions) {

		Log.d(TAG, "addSubscriptions:true");

		mSubscriptions.addAll(subscriptions);

	}

	/**
	 * This method lets you add a new subscription object to the subscription pool with bulletpoints.
	 *
	 * @param title        How you call your subscription.
	 * @param price        How much will your subscription cost?
	 * @param icon         A beautiful picture for this subscription.
	 * @param bulletpoints What does your subscription do in bulletpoints.
	 */
	void addSubscription(String title, String price, Drawable icon, String... bulletpoints) {

		Log.d(TAG, "addSubscriptoin: with bulletpoints");

		mSubscriptions.add(new Subscription(title, price, icon, bulletpoints));

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
