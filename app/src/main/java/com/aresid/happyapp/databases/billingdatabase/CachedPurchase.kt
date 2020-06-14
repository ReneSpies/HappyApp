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

@Entity(tableName = "purchases_table")
@TypeConverters(PurchaseTypeConverter::class)
class CachedPurchase(val purchase: Purchase) {
	
	@PrimaryKey(autoGenerate = true)
	var id: Int = 0
	
	// Represents the purchases token
	@Ignore
	val purchaseToken = purchase.purchaseToken
	
	// Represents the purchases SKU
	@Ignore
	val sku = purchase.sku
	
	/**
	 * This method is overridden to cover the case of CachedPurchase.equals(CachedPurchase),
	 * which would not check the CachedPurchase.purchase member but the CachedPurchase objects itself.
	 * And you can call CachedPurchase.equals(Purchase) which would check if the CachedPurchases
	 * purchase member is equal to Purchase.
	 */
	override fun equals(other: Any?): Boolean {
		
		Timber.d("equals: called")
		
		return when (other) {
			
			// CachedPurchase
			is CachedPurchase -> purchase == other.purchase
			
			// Purchase
			is Purchase -> purchase == other
			
			else -> false
			
		}
		
	}
	
	/**
	 * Overridden to return the CachedPurchase.purchase members hashCode, not the CachedPurchases hashCode.
	 */
	override fun hashCode(): Int = purchase.hashCode()
	
}

class PurchaseTypeConverter {
	
	// Merges the purchases originalJson and signature into a String
	@TypeConverter
	fun toString(purchase: Purchase): String = purchase.originalJson + '|' + purchase.signature
	
	// Splits the originalJson and signature from the String data and creates a new Purchase object from it
	@TypeConverter
	fun toPurchase(data: String): Purchase = data.split('|').let {
		
		// Return a freshly created Purchase object from the originalJson and signature
		Purchase(
			it[0],
			it[1]
		)
		
	}
	
}
