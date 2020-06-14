package com.aresid.happyapp.subscribe

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aresid.happyapp.billingrepository.BillingRepository
import com.aresid.happyapp.databases.billingdatabase.AugmentedSkuDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 *    Created on: 15.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SubscribeViewModel(private val mApplication: Application): AndroidViewModel(mApplication) {
	
	// BillingRepository
	private val mBillingRepository: BillingRepository
	
	// IO CoroutineScope
	private val mIOScope = CoroutineScope(Dispatchers.IO)
	
	// LiveData for toggling the loading screen in the fragment
	private val _toggleLoading = MutableLiveData<SubscribeLoadingStatus>()
	val toggleLoading: LiveData<SubscribeLoadingStatus>
		get() = _toggleLoading
	
	// LiveData representing the SkuDetails from Google Play
	val subscriptionSkuDetailsList: LiveData<List<AugmentedSkuDetails>>
	
	init {
		
		Timber.d("init: called")
		
		// Define the BillingRepository
		mBillingRepository = BillingRepository.getInstance(mApplication)
		
		// Start the connection in the BillingRepository
		startDataSourceConnection()
		
		// Define the subscriptionSkuDetailsList LiveData
		subscriptionSkuDetailsList = mBillingRepository.subscriptionSkuDetailsList
		
		// Init toggleLoadingScreen LiveData
		_toggleLoading.value = SubscribeLoadingStatus.INIT
		
	}
	
	/**
	 * Sets the toggleLoadingScreen LiveData's inside the Main context.
	 */
	private suspend fun setToggleLoadingScreenValue(status: SubscribeLoadingStatus) {
		
		Timber.d("setToggleLoadingScreenValue: called")
		Timber.d("toggleScreenValue = $status")
		
		withContext(Dispatchers.Main) {
			
			_toggleLoading.value = status
			
		}
		
	}
	
	/**
	 * Starts the connection to Google Play in the [BillingRepository].
	 */
	private fun startDataSourceConnection() = mIOScope.launch {
		
		Timber.d("startDataSourceConnection: called")
		
		try {
			
			setToggleLoadingScreenValue(SubscribeLoadingStatus.LOADING)
			
			withContext(Dispatchers.Main) {
				
				mBillingRepository.startDataSourceConnections()
				
			}
			
			setToggleLoadingScreenValue(SubscribeLoadingStatus.IDLE)
			
		}
		catch (e: Exception) {
			
			Timber.e(e)
			
			setToggleLoadingScreenValue(SubscribeLoadingStatus.ERROR_NO_INTERNET)
			
		}
		
	}
	
	fun onCheckoutButtonClicked(
		position: Int,
		subscriptions: List<AugmentedSkuDetails>,
		activity: Activity
	) {
		
		Timber.d("onCheckoutButtonClicked: called")
		
		mIOScope.launch {
			
			try {
				
				setToggleLoadingScreenValue(SubscribeLoadingStatus.LOADING)
				
				withContext(coroutineContext) {
					
					mBillingRepository.launchBillingFlow(
						activity,
						subscriptions[position]
					)
					
				}
				
				setToggleLoadingScreenValue(SubscribeLoadingStatus.SUCCESS)
				
			}
			catch (e: Exception) {
				
				Timber.e(e)
				
				setToggleLoadingScreenValue(SubscribeLoadingStatus.FAILURE)
				
			}
			
		}
		
	}
	
	override fun onCleared() {
		
		Timber.d("onCleared: called")
		
		super.onCleared()
		
		// End the connection in the BillingRepository
		mBillingRepository.endDataSourceConnections()
		
	}
	
}
