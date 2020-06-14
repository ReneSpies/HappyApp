package com.aresid.happyapp.databases.billingdatabase

import androidx.room.*
import com.android.billingclient.api.Purchase
import timber.log.Timber

/**
 *    Created on: 18.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Dao
interface PurchaseDao {
	
	/**
	 * Gets all CachedPurchase objects from the purchases_table.
	 */
	@Query("SELECT * FROM purchases_table")
	fun getPurchases(): List<CachedPurchase>
	
	/**
	 * Inserts one CachedPurchase into the purchases_table.
	 */
	@Insert
	fun insert(purchase: CachedPurchase)
	
	/**
	 * Allows clients to put in a list of Purchase at once.
	 * Converts the Purchase objects to a CachedPurchase and inserts them into the purchases_table.
	 */
	@Transaction
	fun insert(vararg purchases: Purchase) {
		
		Timber.d("insert: called")
		
		// Iterates through the purchases
		purchases.forEach {
			
			// Creates a new CachedPurchase object from the purchase and inserts it into the purchases_table
			insert(CachedPurchase(purchase = it))
			
		}
		
	}
	
	/**
	 * Deletes the given entries from purchases_table.
	 */
	@Delete
	fun delete(vararg purchases: CachedPurchase)
	
	/**
	 * Deletes all entries in purchases_table.
	 */
	@Query("DELETE FROM purchases_table")
	fun deleteAll()
	
}