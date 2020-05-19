package com.aresid.happyapp.billing.billingrepository.localdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *    Created on: 18.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

abstract class Entitlement {
	
	@PrimaryKey
	var id: Int = 1
	
	/**
	 * This method tells clients whether a user __should__ buy a particular item at the moment. For
	 * example, if the silver subscription is bought, the user should not buy another one.
	 * This method is __not__ a reflection on whether Google Play Billing can make a purchase.
	 */
	abstract fun mayPurchase(): Boolean
	
}

@Entity(tableName = "bronze_subscription_table")
data class BronzeSubscription(val entitled: Boolean): Entitlement() {
	
	// Returns the opposite of entitled. Entitled means, the user has already bought the subscription
	override fun mayPurchase(): Boolean = !entitled
	
}

@Entity(tableName = "silver_subscription_table")
data class SilverSubscription(val entitled: Boolean): Entitlement() {
	
	// Returns the opposite of entitled. Entitled means, the user has already bought the subscription
	override fun mayPurchase(): Boolean = !entitled
	
}

@Entity(tableName = "gold_subscription_table")
data class GoldSubscription(val entitled: Boolean): Entitlement() {
	
	// Returns the opposite of entitled. Entitled means, the user has already bought the subscription
	override fun mayPurchase(): Boolean = !entitled
	
}

@Entity(tableName = "platinum_subscription_table")
data class PlatinumSubscription(val entitled: Boolean): Entitlement() {
	
	// Returns the opposite of entitled. Entitled means, the user has already bought the subscription
	override fun mayPurchase(): Boolean = !entitled
	
}

