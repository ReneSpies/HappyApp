package com.aresid.happyapp

import android.util.Log
import java.util.*

/**
 * Created on: 13/11/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class SubscriptionPool {
	
	private val mSubscriptions: MutableList<Subscription>
	
	/**
	 * As the query returns the subs in some weird sequence I sort the subs here and add
	 * their icons.
	 */
	fun sort(): SubscriptionPool {
		Log.d(
			TAG,
			"sort:true"
		)
		Collections.sort(
			mSubscriptions
		) { o1: Subscription, o2: Subscription -> o1.priority - o2.priority }
		return this
	}
	
	fun addSubscription(subscription: Subscription) {
		Log.d(
			TAG,
			"addSubscription:true"
		)
		mSubscriptions.add(subscription)
	}
	
	/**
	 * Getter for count of all subscriptions in the pool starting at 0.
	 *
	 * @return Int count.
	 */
	val subscriptionCount: Int
		get() {
			Log.d(
				TAG,
				"getSubscriptionCount = " + mSubscriptions.size
			)
			return mSubscriptions.size
		}
	
	/**
	 * Getter for a specific subscription object.
	 *
	 * @param index Index of the desired subscription.
	 * @return Subscription in index.
	 */
	fun getSubscription(index: Int): Subscription {
		Log.d(
			TAG,
			"getSubscription:true"
		)
		return mSubscriptions[index]
	}
	
	companion object {
		private const val TAG = "SubscriptionPool"
	}
	
	init {
		Log.d(
			TAG,
			"SubscriptionPool: empty constructor"
		)
		mSubscriptions = ArrayList()
	}
}