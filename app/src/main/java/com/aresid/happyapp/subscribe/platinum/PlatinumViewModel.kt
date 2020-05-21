package com.aresid.happyapp.subscribe.platinum

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

class PlatinumViewModel(application: Application): AndroidViewModel(application) {
	
	// SkuDetails LiveData
	val subscriptionSkuDetailsListLiveData: LiveData<List<AugmentedSkuDetails>>
	
	// BillingRepository
	private val billingRepository: BillingRepository
	
	init {
		
		Timber.d("init: called")
		
		// Define the BillingRepository
		billingRepository = BillingRepository.getInstance(application)
		
		/*
		No need to call BillingRepository#startDataSourceConnection here,
		because it is called in the BronzeContainerViewModel already
		and LiveData takes care of updating all other fragments,
		after connection could successfully be established
		*/
		
		// Define the SkuDetails LiveData
		subscriptionSkuDetailsListLiveData = billingRepository.subscriptionSkuDetailsListLiveData
		
	}
	
	/**
	 * Iterates through the list of SkuDetails and returns the PlatinumSubscriptionSkuDetails,
	 * or null, if the list is empty.
	 */
	fun getSubscriptionSkuDetails(): AugmentedSkuDetails? {
		
		Timber.d("getSubscriptionSkuDetails: called")
		
		val list = subscriptionSkuDetailsListLiveData.value
		
		list?.forEach { skuDetails ->
			
			if (skuDetails.sku == Keys.HappyAppSkus.PLATINUM_SUBSCRIPTION) {
				
				return skuDetails
				
			}
			
		}
		
		return null
		
	}
	
}
