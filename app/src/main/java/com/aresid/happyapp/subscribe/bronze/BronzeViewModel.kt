package com.aresid.happyapp.subscribe.bronze

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.aresid.happyapp.billing.billingrepository.BillingRepository
import com.aresid.happyapp.billing.billingrepository.localdatabase.AugmentedSkuDetails
import com.aresid.happyapp.keys.Keys
import timber.log.Timber

/**
 *    Created on: 16.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class BronzeViewModel(application: Application): AndroidViewModel(application) {
	
	// SkuDetails LiveData
	val subscriptionSkuDetailsListLiveData: LiveData<List<AugmentedSkuDetails>>
	
	// BillingRepository
	private val billingRepository: BillingRepository
	
	// Application
	private val mApplication: Application
	
	init {
		
		Timber.d("init: called")
		
		// Define the application
		mApplication = application
		
		// Define the BillingRepository
		billingRepository = BillingRepository.getInstance(application)
		
		// Start the connection in the BillingRepository
		billingRepository.startDataSourceConnections()
		
		// Define the SkuDetails LiveData
		subscriptionSkuDetailsListLiveData = billingRepository.subscriptionSkuDetailsListLiveData
		
	}
	
	override fun onCleared() {
		
		Timber.d("onCleared: called")
		
		super.onCleared()
		
		// End the connection in the BillingRepository
		billingRepository.endDataSourceConnections()
		
	}
	
	/**
	 * Iterates through the list of SkuDetails and returns the BronzeSubscriptionSkuDetails,
	 * or null, if the list is empty.
	 */
	fun getSubscriptionSkuDetails(): AugmentedSkuDetails? {
		
		Timber.d("getSubscriptionSkuDetails: called")
		
		val list = subscriptionSkuDetailsListLiveData.value
		
		list?.forEach { skuDetails ->
			
			if (skuDetails.sku == Keys.HappyAppSkus.BRONZE_SUBSCRIPTION) {
				
				return skuDetails
				
			}
			
		}
		
		return null
		
	}
	
	fun onCheckoutButtonClicked(activity: Activity) {
		
		Timber.d("onCheckoutButtonClicked: called")
		
		// Launch the billing flow
		billingRepository.launchBillingFlow(
			activity,
			getSubscriptionSkuDetails()!!
		)
		
	}
	
}
