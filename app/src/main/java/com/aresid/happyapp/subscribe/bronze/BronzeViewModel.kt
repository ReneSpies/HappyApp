package com.aresid.happyapp.subscribe.bronze

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

class BronzeViewModel(application: Application): AndroidViewModel(application) {
	
	// SkuDetails LiveData
	val subscriptionSkuDetailsList: LiveData<List<AugmentedSkuDetails>>
	
	// BillingRepository
	private val billingRepository: BillingRepository
	
	// Application
	private val mApplication: Application
	
	// IO CoroutineScope
	private val mIOScope = CoroutineScope(Dispatchers.IO)
	
	// LiveData to toggle the loading screen
	private val _toggleLoadingScreen = MutableLiveData<LoadingStatus>()
	val toggleLoadingScreen: LiveData<LoadingStatus>
		get() = _toggleLoadingScreen
	
	init {
		
		Timber.d("init: called")
		
		// Define the application
		mApplication = application
		
		// Define the BillingRepository
		billingRepository = BillingRepository.getInstance(application)
		
		// Start the connection in the BillingRepository
		startDataSourceConnection()
		
		// Define the SkuDetails LiveData
		subscriptionSkuDetailsList = billingRepository.subscriptionSkuDetailsList
		
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
	
	private fun startDataSourceConnection() = mIOScope.launch {
		
		Timber.d("startDataSourceConnection: called")
		
		try {
			
			setToggleLoadingScreenValue(LoadingStatus.LOADING)
			
			withContext(coroutineContext) {
				
				billingRepository.startDataSourceConnections()
				
			}
			
			setToggleLoadingScreenValue(LoadingStatus.IDLE)
			
		}
		catch (e: Exception) {
			
			Timber.e(e)
			
			setToggleLoadingScreenValue(LoadingStatus.ERROR_NO_INTERNET)
			
		}
		
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
		
		val list = subscriptionSkuDetailsList.value
		
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