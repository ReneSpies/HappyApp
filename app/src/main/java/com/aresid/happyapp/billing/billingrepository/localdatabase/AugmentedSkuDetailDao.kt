package com.aresid.happyapp.billing.billingrepository.localdatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import timber.log.Timber

/**
 *    Created on: 18.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Dao
interface AugmentedSkuDetailDao {
	
	/**
	 * Returns a LiveData with a List of all AugmentedSkuDetails in the augmented_sku_details_table.
	 */
	@Query("SELECT * FROM augmented_sku_details_table WHERE type = '${BillingClient.SkuType.SUBS}' ")
	fun getSubscriptionSkuDetails(): LiveData<List<AugmentedSkuDetails>>
	
	/**
	 * Inserts or updates the augmented_sku_details_table with the given SkuDetails object.
	 */
	@Transaction
	fun insertOrUpdate(skuDetails: SkuDetails) = skuDetails.apply {
		
		// Get the AugmentedSkuDetails by the SKU ID
		val resultSkuDetails = getById(sku)
		
		// Evaluate whether the Sku can be purchased or not
		val canPurchase = if (resultSkuDetails == null) true else resultSkuDetails.canPurchase
		
		// Gets the String from SkuDetails.toString beginning at the index "SkuDetails: ".length
		val originalJson = toString().substring("SkuDetails: ".length)
		
		// Defines a new AugmentedSkuDetails object
		val augmentedSkuDetails = AugmentedSkuDetails(
			canPurchase,
			sku,
			type,
			price,
			title,
			description,
			originalJson
		)
		
		// Inserts the new AugmentedSkuDetails object into the augmented_sku_details_table
		insert(augmentedSkuDetails)
		
	}
	
	/**
	 * Updates the augmented_sku_details_table with the given information,
	 * if the result from getById(sku) is null.
	 * If it is not null, it creates a new AugmentedSkuDetails object and inserts it into the table.
	 */
	@Transaction
	fun insertOrUpdate(
		sku: String,
		canPurchase: Boolean
	) {
		
		Timber.d("insertOrUpdate: called")
		
		// Get the AugmentedSkuDetails by the SKU ID
		val result = getById(sku)
		
		// If there is no AugmentedSkuDetail for the sku, update the table
		if (result != null) {
			
			// Update the table
			update(
				sku,
				canPurchase
			)
			
		}
		
		// Else, create a new AugmentedSkuDetails object and insert it into the table
		else {
			
			// Create a new AugmentedSkuDetails object and insert it into the table
			insert(
				AugmentedSkuDetails(
					canPurchase,
					sku,
					null,
					null,
					null,
					null,
					null
				)
			)
			
		}
		
	}
	
	/**
	 * Returns an AugmentedSkuDetails from the augmented_sku_details_table by the SKU ID.
	 */
	@Query("SELECT * FROM augmented_sku_details_table WHERE sku = :sku")
	fun getById(sku: String): AugmentedSkuDetails
	
	/**
	 * Inserts a AugmentedSkuDetails object into the augmented_sku_details_table.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(augmentedSkuDetails: AugmentedSkuDetails)
	
	/**
	 * Update the augmented_sku_details_table by the SKU ID and put the canPurchase Boolean in it.
	 */
	@Query("UPDATE augmented_sku_details_table SET canPurchase = :canPurchase WHERE sku = :sku")
	fun update(
		sku: String,
		canPurchase: Boolean
	)
	
}