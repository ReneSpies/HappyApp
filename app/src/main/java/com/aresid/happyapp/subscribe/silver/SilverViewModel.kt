package com.aresid.happyapp.subscribe.silver

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aresid.happyapp.billing.billingrepository.BillingRepository
import com.aresid.happyapp.billing.billingrepository.localdatabase.AugmentedSkuDetails
import com.aresid.happyapp.exceptions.CardDeclinedException
import com.aresid.happyapp.keys.Keys
import com.aresid.happyapp.utils.LoadingStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 *    Created on: 16.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SilverViewModel(application: Application): AndroidViewModel(application) {
	
	// SkuDetails LiveData
	val subscriptionSkuDetailsListLiveData: LiveData<List<AugmentedSkuDetails>>
	
	// BillingRepository
	private val billingRepository: BillingRepository
	
	// IO CoroutineScope
	private val mIOScope = CoroutineScope(Dispatchers.IO)
	
	// LiveData to toggle the loading screen
	private val _toggleLoadingScreen = MutableLiveData<LoadingStatus>()
	val toggleLoadingScreen: LiveData<LoadingStatus>
		get() = _toggleLoadingScreen
	
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
		subscriptionSkuDetailsListLiveData = billingRepository.subscriptionSkuDetailsList
		
		// Init toggleLoadingScreen LiveData
		_toggleLoadingScreen.value = LoadingStatus.INIT
		
	}
	
	/**
	 * Sets the toggleLoadingScreen LiveData's inside the Main context.
	 */
	private suspend fun setToggleLoadingScreenValue(status: LoadingStatus) {
		
		Timber.d("setToggleLoadingScreenValue: called")
		
		withContext(Dispatchers.Main) {
			
			_toggleLoadingScreen.value = status
			
		}
		
	}
	
	/**
	 * Iterates through the list of SkuDetails and returns the PlatinumSubscriptionSkuDetails,
	 * or null, if the list is empty.
	 */
	fun getSubscriptionSkuDetails(): AugmentedSkuDetails? {
		
		Timber.d("getSubscriptionSkuDetails: called")
		
		val list = subscriptionSkuDetailsListLiveData.value
		
		list?.forEach { skuDetails ->
			
			if (skuDetails.sku == Keys.HappyAppSkus.SILVER_SUBSCRIPTION) {
				
				return skuDetails
				
			}
			
		}
		
		return null
		
	}
	
	fun onCheckoutButtonClicked(activity: Activity) {
		
		Timber.d("onCheckoutButtonClicked: called")
		
		mIOScope.launch {
			
			try {
				
				setToggleLoadingScreenValue(LoadingStatus.LOADING)
				
				// Wrap this call inside the withContext to wait for its value
				withContext(coroutineContext) {
					
					billingRepository.launchBillingFlow(
						activity,
						getSubscriptionSkuDetails()!!
					)
					
				}
				
				setToggleLoadingScreenValue(LoadingStatus.SUCCESS)
				
			}
			catch (e: Exception) {
				
				Timber.e(e)
				
				when (e) {
					
					is CardDeclinedException -> setToggleLoadingScreenValue(LoadingStatus.ERROR_CARD_DECLINED)
					
				}
				
			}
			
		}
		
	}
	
}
