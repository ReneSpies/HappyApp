package com.aresid.happyapp.billing.billingrepository

import com.android.billingclient.api.Purchase
import timber.log.Timber

/**
 *    Created on: 18.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

/**
 * If you have a server, you can use it everywhere you see a reference to BillingWebservice.
 */
class BillingWebservice {
	
	/**
	 * Get purchases from the server.
	 */
	fun getPurchases(): Any {
		
		Timber.d("getPurchases: called")
		
		return Any() //TODO("not implemented")
		
	}
	
	/**
	 * Update the server with a new Purchase.
	 */
	fun updateServer(purchases: Set<Purchase>) {
		
		Timber.d("updateServer: called")
		
		//TODO("not implemented")
		
	}
	
	fun onConsumeResponse(
		purchaseToken: String?,
		responseCode: Int
	) {
		
		Timber.d("onConsumeResponse: called")
		
		//TODO("not implemented")
		
	}
	
	companion object {
		
		/**
		 * Creates a new BillingWebservice object.
		 */
		fun create(): BillingWebservice {
			
			Timber.d("create: called")
			
			//TODO("not implemented")
			return BillingWebservice()
			
		}
	}
}