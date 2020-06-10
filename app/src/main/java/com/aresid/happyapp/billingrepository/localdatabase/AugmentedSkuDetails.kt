package com.aresid.happyapp.billingrepository.localdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *    Created on: 18.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

/**
 * AugmentedSkuDetails is just a more client-friendly version of SkuDetails,
 * that gives the clients the additional info on whether to buy the relevant product at this moment.
 */
@Entity(tableName = "augmented_sku_details_table")
data class AugmentedSkuDetails(
	val canPurchase: Boolean, /* Not in SkuDetails; it's the augmentation */
	@PrimaryKey val sku: String,
	val type: String?,
	val price: String?,
	val title: String?,
	val description: String?,
	val originalJson: String?,
	val priceMicros: Long
)
