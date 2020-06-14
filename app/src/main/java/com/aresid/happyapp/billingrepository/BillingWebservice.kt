package com.aresid.happyapp.billingrepository

import com.android.billingclient.api.Purchase
import com.aresid.happyapp.keys.FirestoreKeys
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.coroutineContext

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
	suspend fun getPurchases(uid: String): DocumentSnapshot {
		
		Timber.d("getPurchases: called")
		
		return withContext(coroutineContext) {
			
			Firebase.firestore.collection(FirestoreKeys.Collection.USERS).document(uid).get().await()
			
		}
		
	}
	
	private suspend fun savePurchaseInFirestore(
		uid: String,
		purchase: Purchase
	) {
		
		Timber.d("savePurchaseInFirestore: called")
		
		// Create a map for the purchase to save it to the firestore
		val purchaseMap = withContext(
			Dispatchers.Main
		) {
			
			mapOf(
				FirestoreKeys.Collection.Column.SUBSCRIPTION_VARIANT to purchase
			)
			
		}
		
		// Save the subscription to the Firestore referencing the users uid
		Firebase.firestore.collection(FirestoreKeys.Collection.USERS).document(uid).set(
			purchaseMap,
			SetOptions.merge()
		).await()
		
	}
	
	/**
	 * Update the server with a new Purchase.
	 */
	suspend fun updateServer(
		uid: String,
		purchases: Set<Purchase>
	) {
		
		Timber.d("updateServer: called")
		
		purchases.forEach { purchase ->
			
			savePurchaseInFirestore(
				uid,
				purchase
			)
			
		}
		
	}
	
	companion object {
		
		private var INSTANCE: BillingWebservice? = null
		
		/**
		 * Creates a new BillingWebservice object.
		 */
		fun getInstance(): BillingWebservice = INSTANCE ?: synchronized(this) {
			
			Timber.d("getInstance: called")
			
			INSTANCE ?: BillingWebservice().also { INSTANCE = it }
			
		}
	}
}