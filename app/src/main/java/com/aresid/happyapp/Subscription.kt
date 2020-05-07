package com.aresid.happyapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.android.billingclient.api.SkuDetails

/**
 * Created on: 13/11/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class Subscription(
	context: Context,
	skuDetails: SkuDetails
) {
	
	private var mTitle: String? = null
	private var mDescription: String? = null
	private var mPrice: String? = null
	private var mIcon: Drawable? = null
	
	/**
	 * Getter for subscriptions id.
	 *
	 * @return Guess.
	 */
	var id: String? = null
		private set
	private val mContext: Context
	private var mPriority = 0
	/**
	 * Getter for sku details.
	 *
	 * @return Guess.
	 */
	/**
	 * Setter for sku details.
	 *
	 * @param skuDetails Guess.
	 */
	var skuDetails: SkuDetails
	private fun init() {
		Log.d(
			TAG,
			"init: called"
		)
		setTitle(skuDetails.title)
		setId(skuDetails.sku)
		description = skuDetails.description
		price = skuDetails.price
	}
	
	/**
	 * Returns the subscriptions title.
	 *
	 * @return Title.
	 */
	val title: String?
		get() {
			Log.d(
				TAG,
				"getTitle:true"
			)
			return mTitle
		}
	
	/**
	 * Sets the subscriptions title to the given String.
	 *
	 * @param title Your desired title name_with_placeholder.
	 */
	fun setTitle(title: String) {
		Log.d(
			TAG,
			"setTitle:true"
		)
		mTitle = if (title.contains("(HappyApp)")) {
			title.replace(
				"(HappyApp)",
				""
			).trim { it <= ' ' }
		}
		else {
			title.trim { it <= ' ' }
		}
	}
	
	/**
	 * Returns the subscriptions description as single text if bulletpoints are not used.
	 *
	 * @return Description.
	 */
	/**
	 * Sets the subscriptions description to the given String as a single text.
	 * Throws IllegalStateException if subscription already has bulletpoints.
	 *
	 * @param description Your desired description as text.
	 */
	var description: String?
		get() {
			Log.d(
				TAG,
				"getDescription:true"
			)
			return mDescription
		}
		set(description) {
			Log.d(
				TAG,
				"setDescription:true"
			)
			mDescription = description
		}
	
	/**
	 * Returns the subscriptions icon.
	 *
	 * @return Icon.
	 */
	/**
	 * Sets the subscriptions icon to the given drawable.
	 *
	 * @param icon Your desired icon.
	 */
	var icon: Drawable?
		get() {
			Log.d(
				TAG,
				"getIcon:true"
			)
			return mIcon
		}
		set(icon) {
			Log.d(
				TAG,
				"setIcon:true"
			)
			mIcon = icon
		}
	
	/**
	 * Returns the subscriptions price as a formatted String.
	 *
	 * @return Price.
	 */
	/**
	 * Sets the subscriptions price to the given float and converts it to a String.
	 *
	 * @param price Your desired price.
	 */
	var price: String?
		get() {
			Log.d(
				TAG,
				"getPrice:true"
			)
			return mPrice
		}
		set(price) {
			Log.d(
				TAG,
				"setPrice:true"
			)
			mPrice = price
		}
	
	/**
	 * Setter for subscriptions id.
	 *
	 * @param id Guess.
	 */
	fun setId(id: String) {
		if (id.contains("bronze")) {
			icon = mContext.getDrawable(R.drawable.bronze_logo)
			priority = PRIORITY_BRONZE
		}
		else if (id.contains("silver")) {
			icon = mContext.getDrawable(R.drawable.silver_logo)
			priority = PRIORITY_SILVER
		}
		else if (id.contains("gold")) {
			icon = mContext.getDrawable(R.drawable.gold_logo)
			priority = PRIORITY_GOLD
		}
		else if (id.contains("platinum")) {
			icon = mContext.getDrawable(R.drawable.platinum_logo)
			priority = PRIORITY_PLATINUM
		}
		else {
			priority = PRIORITY_DEFAULT
		}
		this.id = id
	}
	
	/**
	 * Getter for subscriptions priority.
	 *
	 * @return Guess.
	 */
	/**
	 * Setter for subscriptions priority.
	 *
	 * @param priority Guess.
	 */
	var priority: Int
		get() = mPriority
		private set(priority) {
			Log.d(
				TAG,
				"setPriority:true"
			)
			mPriority = priority
		}
	
	companion object {
		private const val PRIORITY_BRONZE = 0
		private const val PRIORITY_SILVER = 1
		private const val PRIORITY_GOLD = 2
		private const val PRIORITY_PLATINUM = 3
		private const val PRIORITY_DEFAULT = -1
		private const val TAG = "Subscription"
	}
	
	/**
	 * Constructor.
	 */
	init {
		Log.d(
			TAG,
			"Subscription: empty constructor"
		)
		mContext = context
		this.skuDetails = skuDetails
		init()
	}
}