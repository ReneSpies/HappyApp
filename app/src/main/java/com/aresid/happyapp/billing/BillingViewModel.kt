package com.aresid.happyapp.billing

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.aresid.happyapp.billing.billingrepository.BillingRepository
import com.aresid.happyapp.billing.billingrepository.localdatabase.*
import kotlinx.coroutines.*
import timber.log.Timber

/**
 *    Created on: 19.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

/**
 * Notice just how small and simple this BillingViewModel is!!
 *
 * This beautiful simplicity is the result of keeping all the hard work buried
 * inside the BillingRepository and only inside the BillingRepository. The
 * rest of your app is now free from BillingClient tentacles!! And this
 * [BillingViewModel] is the one and only object the rest of your Android team
 * need to know about billing.
 */
class BillingViewModel(application: Application): AndroidViewModel(application) {
	
	val bronzeSubscriptionLiveData: LiveData<BronzeSubscription>
	val silverSubscriptionLiveData: LiveData<SilverSubscription>
	val goldSubscriptionLiveData: LiveData<GoldSubscription>
	val platinumSubscriptionLiveData: LiveData<PlatinumSubscription>
	val subscriptionsSkuDetailsListLiveData: LiveData<List<AugmentedSkuDetails>>
	
	private val mIOScope = CoroutineScope(Dispatchers.IO)
	
	private val viewModelScope = CoroutineScope(Job() + Dispatchers.Main)
	private val repository: BillingRepository
	
	init {
		
		Timber.d("init: called")
		
		repository = BillingRepository.getInstance(application)
		
		mIOScope.launch {
			
			try {
				
				repository.startDataSourceConnections()
				
			}
			catch (e: Exception) {
				
				Timber.e(e)
				
			}
			
		}
		
		bronzeSubscriptionLiveData = repository.bronzeSubscription
		silverSubscriptionLiveData = repository.silverSubscriptionLiveData
		goldSubscriptionLiveData = repository.goldSubscriptionLiveData
		platinumSubscriptionLiveData = repository.platinumSubscriptionLiveData
		
		subscriptionsSkuDetailsListLiveData = repository.subscriptionSkuDetailsList
		
	}
	
	/**
	 * Force refresh.
	 */
	fun queryPurchases() = mIOScope.launch {
		
		Timber.d("queryPurchases: called")
		
		try {
			
			repository.queryPurchasesAsync()
			
		}
		catch (e: java.lang.Exception) {
			
			Timber.e(e)
			
		}
		
	}
	
	override fun onCleared() {
		Timber.d("onCleared: called")
		super.onCleared()
		
		repository.endDataSourceConnections()
		viewModelScope.coroutineContext.cancel()
	}
	
	fun makePurchase(
		activity: Activity,
		augmentedSkuDetails: AugmentedSkuDetails
	) = mIOScope.launch {
		
		Timber.d("makePurchase: called")
		
		repository.launchBillingFlow(
			activity,
			augmentedSkuDetails
		)
		
	}
	
}