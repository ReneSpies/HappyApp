package com.aresid.happyapp.billingrepository

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.android.billingclient.api.*
import com.aresid.happyapp.databases.billingdatabase.*
import com.aresid.happyapp.exceptions.CardDeclinedException
import com.aresid.happyapp.keys.HappyAppSkus
import com.aresid.happyapp.utils.Util.isSuccess
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.*
import kotlin.math.pow

/**
 *    Created on: 18.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

/**
 * The BillingRepository will handle absolutely everything billing-related,
 * so that the rest of the app doesn't have to.
 */
class BillingRepository private constructor(private val application: Application) {
	
	// BillingClient
	private lateinit var playStoreBillingClient: BillingClient
	
	// Secure server client
	private lateinit var secureServerBillingClient: BillingWebservice
	
	// LocalBillingDatabase
	private lateinit var localCacheBillingClient: LocalBillingDatabase
	
	val subscriptionSkuDetailsList: LiveData<List<AugmentedSkuDetails>> by lazy {
		
		if (!::localCacheBillingClient.isInitialized) {
			
			localCacheBillingClient = LocalBillingDatabase.getInstance(application)
			
		}
		
		localCacheBillingClient.augmentedSkuDetailDao().getSubscriptionSkuDetails()
		
	}
	
	val bronzeSubscription: LiveData<BronzeSubscription> by lazy {
		
		if (!::localCacheBillingClient.isInitialized) {
			
			localCacheBillingClient = LocalBillingDatabase.getInstance(application)
			
		}
		
		localCacheBillingClient.entitlementsDao().getBronzeSubscription()
		
	}
	val silverSubscriptionLiveData: LiveData<SilverSubscription> by lazy {
		
		if (!::localCacheBillingClient.isInitialized) {
			
			localCacheBillingClient = LocalBillingDatabase.getInstance(application)
			
		}
		
		localCacheBillingClient.entitlementsDao().getSilverSubscription()
		
	}
	val goldSubscriptionLiveData: LiveData<GoldSubscription> by lazy {
		
		if (!::localCacheBillingClient.isInitialized) {
			
			localCacheBillingClient = LocalBillingDatabase.getInstance(application)
			
		}
		
		localCacheBillingClient.entitlementsDao().getGoldSubscription()
		
	}
	val platinumSubscriptionLiveData: LiveData<PlatinumSubscription> by lazy {
		
		if (!::localCacheBillingClient.isInitialized) {
			
			localCacheBillingClient = LocalBillingDatabase.getInstance(application)
			
		}
		
		localCacheBillingClient.entitlementsDao().getPlatinumSubscription()
		
	}
	
	// A channel to make the launchBillingFlow fit into coroutines
	private val mPurchaseChannel: Channel<Purchase.PurchasesResult> = Channel(Channel.UNLIMITED)
	
	/**
	 * Calls [defineAndConnectToGooglePlayBillingService],
	 * defines the [secureServerBillingClient] and [localCacheBillingClient].
	 */
	suspend fun startDataSourceConnections() {
		
		Timber.d("startDataSourceConnections: called")
		
		defineAndConnectToGooglePlayBillingService()
		
		secureServerBillingClient = BillingWebservice.getInstance()
		
		localCacheBillingClient = LocalBillingDatabase.getInstance(application)
		
	}
	
	/**
	 * Ends the connection on the [playStoreBillingClient].
	 */
	fun endDataSourceConnections() {
		
		Timber.d("endDataSourceConnections: called")
		
		playStoreBillingClient.endConnection()
		
		// normally you don't worry about closing a DB connection unless you have
		// more than one open. so no need to call 'localCacheBillingClient.close()'
		
	}
	
	/**
	 * Defines the [playStoreBillingClient], puts the result
	 * from [PurchasesUpdatedListener] into the [mPurchaseChannel]
	 * and calls [connectToGooglePlayBillingService].
	 */
	private suspend fun defineAndConnectToGooglePlayBillingService() {
		
		Timber.d("defineAndConnectToGooglePlayBillingService: called")
		
		playStoreBillingClient = BillingClient.newBuilder(application.applicationContext).enablePendingPurchases().setListener { responseCode, purchases ->
			
			// Put the result into the mPurchaseChannel
			mPurchaseChannel.offer(
				Purchase.PurchasesResult(
					responseCode,
					purchases
				)
			)
			
		}.build()
		
		connectToGooglePlayBillingService()
		
	}
	
	/**
	 * When the responseCode is OK, calls [RetryPolicies.resetConnectionRetryPolicyCounter],
	 * [querySubscriptionSkuDetailsAsync] and [queryPurchasesAsync].
	 * When the responseCode is BILLING_UNAVAILABLE
	 * or anything else, throws and Exception.
	 */
	private suspend fun processBillingResult(billingResult: BillingResult?) {
		
		Timber.d("processBillingResult: called")
		
		when (billingResult?.responseCode) {
			
			BillingClient.BillingResponseCode.OK -> {
				
				Timber.d("onBillingSetupFinished success")
				
				// Reset the retry counter in the RetryPolicies
				RetryPolicies.resetConnectionRetryPolicyCounter()
				
				// Query for subscription SkuDetails
				querySubscriptionSkuDetailsAsync(HappyAppSkus.SUBSCRIPTION_SKUS)
				
				//				queryPurchasesAsync()
				
			}
			
			BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
				
				Timber.d("onBillingSetupFinished but subscriptions are not available on this device")
				
				// Throw an exception to show feedback
				throw Exception("Subscriptions are not available on this device")
				
			}
			
			else -> {
				
				Timber.d("onBillingSetupFinished with failure response code: ${billingResult?.responseCode}")
				
				throw Exception("Response code = ${billingResult?.responseCode}")
				
			}
			
		}
		
	}
	
	/**
	 * Suspends the call to [BillingClientStateListener], uses
	 * the [RetryPolicies.connectionRetryPolicy] when the
	 * onBillingServiceDisconnected listener callback is called,
	 * and calls [processBillingResult] in onBillingSetupFinished.
	 */
	suspend fun connectToGooglePlayBillingService() {
		
		Timber.d("connectToGooglePlayBillingService: called")
		
		val billingResult = suspendCoroutine<BillingResult?> { continuation ->
			
			// If the billingClient is not already ready, start the connection
			if (!playStoreBillingClient.isReady) {
				
				// Start the connection and wait for its result in the listener
				playStoreBillingClient.startConnection(object: BillingClientStateListener {
					
					override fun onBillingServiceDisconnected() {
						
						Timber.d("onBillingServiceDisconnected: called")
						
						// Retry to connect using the RetryPolicies
						RetryPolicies.connectionRetryPolicy {
							
							connectToGooglePlayBillingService()
							
						}
						
					}
					
					override fun onBillingSetupFinished(billingResult: BillingResult?) {
						
						Timber.d("onBillingSetupFinished: called")
						
						// Process the result
						continuation.resume(billingResult)
						
					}
					
				})
				
			}
			
		}
		
		processBillingResult(billingResult)
		
	}
	
	/**
	 * Make [BillingRepository] a singleton.
	 */
	companion object {
		
		@Volatile
		private var INSTANCE: BillingRepository? = null
		
		fun getInstance(application: Application): BillingRepository = INSTANCE ?: synchronized(this) {
			INSTANCE ?: BillingRepository(application).also { INSTANCE = it }
		}
		
	}
	
	private suspend fun querySubscriptionSkuDetailsAsync(
		skuList: List<String>
	) {
		
		Timber.d("querySubscriptionSkuDetailsAsync: called")
		
		val parameterBuilder = SkuDetailsParams.newBuilder()
		
		parameterBuilder.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
		
		RetryPolicies.taskExecutionRetryPolicy(
			this,
			playStoreBillingClient,
			coroutineContext
		) {
			
			Timber.d("querySkuDetailsAsync for ${BillingClient.SkuType.SUBS}")
			
			val skuDetails = suspendCoroutine<List<SkuDetails>> { continuation ->
				
				playStoreBillingClient.querySkuDetailsAsync(
					parameterBuilder.build()
				) { billingResult, skuDetails ->
					
					if (billingResult.responseCode.isSuccess()) {
						
						continuation.resume(
							skuDetails
						)
						
					}
					else {
						
						continuation.resumeWithException(Exception("Response code = ${billingResult.responseCode}"))
						
					}
					
				}
				
			}
			
			processSkuDetails(skuDetails)
			
		}
		
	}
	
	private fun processSkuDetails(
		skuDetails: List<SkuDetails>
	) {
		
		Timber.d("processSkuDetails: called")
		
		if (skuDetails.isNotEmpty()) {
			
			val scope = CoroutineScope(Job() + Dispatchers.IO)
			scope.launch {
				
				skuDetails.forEach {
					
					localCacheBillingClient.augmentedSkuDetailDao().insertOrUpdate(it)
					
				}
				
			}
			
		}
		
	}
	
	suspend fun queryPurchasesAsync() {
		
		Timber.d("queryPurchasesAsync: called")
		
		suspend fun task() {
			
			Timber.d("task: called")
			
			val purchasesResult = HashSet<Purchase>()
			
			if (isSubscriptionSupported()) {
				
				val result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.SUBS)
				
				result.purchasesList?.apply {
					
					purchasesResult.addAll(this)
					
				}
				
				Timber.d("queryPurchasesAsync subs result = ${result.purchasesList}")
				
			}
			
			// Get the uid from the current user to save it in the Firestore
			val uid = FirebaseAuth.getInstance().currentUser!!.uid
			
			processPurchases(
				uid,
				purchasesResult
			)
			
		}
		
		RetryPolicies.taskExecutionRetryPolicy(
			this,
			playStoreBillingClient,
			coroutineContext
		) {
			
			task()
			
		}
		
	}
	
	/**
	 * Acknowledges the purchase using its [purchaseToken].
	 */
	private suspend fun acknowledgePurchase(purchaseToken: String) {
		
		Timber.d("acknowledgePurchase: called")
		
		// Define a AcknowledgePurchaseParamsBuilder
		val acknowledgePurchaseParamsBuilder = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchaseToken)
		
		// Acknowledge the purchase
		playStoreBillingClient.acknowledgePurchase(acknowledgePurchaseParamsBuilder.build())
		
	}
	
	/**
	 * Processes all new purchases which are not yet cached.
	 */
	private suspend fun processPurchases(
		uid: String,
		purchaseResult: Set<Purchase>
	) {
		
		Timber.d("processPurchases: called")
		
		// Get all cached purchases
		val cachedPurchases = withContext(Dispatchers.IO) {
			localCacheBillingClient.purchaseDao().getPurchases()
		}
		
		// Define a new HashSet for all valid and not cached purchases
		val newBatch = HashSet<Purchase>(purchaseResult.size)
		
		// Iterate through all purchases
		purchaseResult.forEach { purchase ->
			
			// If the signature is valid and the purchase is not already cached, add it to the newBatch
			if (isSignatureValid(purchase) && !cachedPurchases.any { it.purchase == purchase }) {
				
				// Add it to newBatch
				newBatch.add(purchase)
				
				// If the purchase is purchased, acknowledge it
				if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
					
					// Acknowledge the purchase
					acknowledgePurchase(purchase.purchaseToken)
					
				}
				
			}
			
		}
		
		// If the newBatch is not empty, send the purchase to the server and the cache
		if (newBatch.isNotEmpty()) {
			
			// Send to server
			sendPurchasesToServer(
				uid,
				newBatch
			)
			
			// We still care about purchaseResult in case an old purchase
			// has not been consumed yet
			saveToLocalDatabase(
				newBatch,
				purchaseResult
			)
			
		}
		
		// Else, if the newBatch is empty, query purchases from server if possible
		else if (Throttle.isLastInvocationTimeStale(application)) {
			
			queryPurchasesFromSecureServer(uid)
			
		}
		
	}
	
	private fun isSubscriptionSupported(): Boolean {
		
		Timber.d("isSubscriptionSupported: called")
		
		val responseCode = playStoreBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).responseCode
		
		if (responseCode != BillingClient.BillingResponseCode.OK) {
			
			Timber.d("isSubscriptionSupport got an error response = $responseCode")
			
		}
		
		return responseCode == BillingClient.BillingResponseCode.OK
		
	}
	
	private fun isSignatureValid(purchase: Purchase): Boolean {
		
		Timber.d("isSignatureValid: called")
		
		return Security.verifyPurchase(
			Security.BASE_64_ENCODED_PUBLIC_KEY,
			purchase.originalJson,
			purchase.signature
		)
		
	}
	
	private suspend fun sendPurchasesToServer(
		uid: String,
		purchases: Set<Purchase>
	) {
		
		Timber.d("sendPurchasesToServer: called")
		
		BillingWebservice.getInstance().updateServer(
			uid,
			purchases
		)
		
	}
	
	/**
	 * Queries the purchases from the secure server, saves them
	 * to the cache and refreshes the [Throttle].
	 */
	private fun queryPurchasesFromSecureServer(uid: String) {
		
		Timber.d("queryPurchasesFromSecureServer: called")
		
		fun getPurchasesFromSecureServerToLocalDatabase() {
			
			Timber.d("getPurchasesFromSecureServerToLocalDatabase: called")
			
		}
		
		getPurchasesFromSecureServerToLocalDatabase()
		
		Throttle.refreshLastInvocationTime(application)
		
	}
	
	private fun saveToLocalDatabase(
		newBatch: Set<Purchase>,
		allPurchases: Set<Purchase>
	) {
		
		Timber.d("saveToLocalDatabase: called")
		
		val scope = CoroutineScope(Job() + Dispatchers.IO)
		scope.launch {
			
			newBatch.forEach { purchase ->
				
				when (purchase.sku) {
					
					HappyAppSkus.BRONZE_SUBSCRIPTION -> {
						
						val bronzeSubscription = BronzeSubscription(true)
						
						insert(bronzeSubscription)
						
						localCacheBillingClient.augmentedSkuDetailDao().insertOrUpdate(
							purchase.sku,
							bronzeSubscription.mayPurchase()
						)
						
						HappyAppSkus.SUBSCRIPTION_SKUS.forEach { otherSku ->
							
							if (otherSku != purchase.sku) {
								
								localCacheBillingClient.augmentedSkuDetailDao().insertOrUpdate(
									otherSku,
									!bronzeSubscription.mayPurchase()
								)
								
							}
							
						}
						
					}
					
				}
				
			}
			
			localCacheBillingClient.purchaseDao().insert(*newBatch.toTypedArray())
			
		}
		
	}
	
	@WorkerThread
	private suspend fun insert(entitlement: Entitlement) = withContext(Dispatchers.IO) {
		
		localCacheBillingClient.entitlementsDao().insert(entitlement)
		
	}
	
	suspend fun launchBillingFlow(
		activity: Activity,
		augmentedSkuDetails: AugmentedSkuDetails
	) = launchBillingFlow(
		activity,
		SkuDetails(augmentedSkuDetails.originalJson)
	)
	
	private suspend fun launchBillingFlow(
		activity: Activity,
		skuDetails: SkuDetails
	) {
		
		Timber.d("launchBillingFlow: called")
		
		// TODO:  launchBillingFlow: setOldSku() needs a >purchaseToken<!!!
		
		val purchaseParameters = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
		
		RetryPolicies.taskExecutionRetryPolicy(
			this,
			playStoreBillingClient,
			coroutineContext
		) {
			
			playStoreBillingClient.launchBillingFlow(
				activity,
				purchaseParameters
			)
			
			// This is the result from onPurchasesUpdated
			val purchasesResult = mPurchaseChannel.receive()
			
			// Process the purchases the user made
			processPurchasesResult(purchasesResult)
			
		}
		
	}
	
	/**
	 * Splits the [purchasesResult] into two values, billingResult
	 * and a List of purchases. It checks if the responseCode is OK
	 * and if so, calls [processPurchases] and if not, throws and
	 * Exception.
	 */
	private suspend fun processPurchasesResult(purchasesResult: Purchase.PurchasesResult) {
		
		Timber.d("processPurchasesResult: called")
		
		// Split the purchasesResult into a billingResult and a List of purchases
		val billingResult = purchasesResult.billingResult
		val purchases = purchasesResult.purchasesList
		
		// When the responseCode is OK, process the purchases,
		// when ITEM_ALREADY_OWNED, query the purchases again,
		// else, throw an Exception.
		when (billingResult.responseCode) {
			
			BillingClient.BillingResponseCode.OK -> {
				
				val uid = FirebaseAuth.getInstance().currentUser!!.uid
				
				purchases.apply {
					
					processPurchases(
						uid,
						toSet()
					)
					
				}
				
			}
			
			BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
				
				Timber.d("Item already owned")
				
				queryPurchasesAsync()
				
			}
			
			BillingClient.BillingResponseCode.ERROR -> throw CardDeclinedException()
			
			else -> throw Exception("Response code = ${billingResult.responseCode}")
			
		}
		
	}
	
	private fun getOldSku(sku: String?): String? {
		
		Timber.d("getOldSku: called")
		
		var result: String? = null
		
		when (sku) {
			
			HappyAppSkus.BRONZE_SUBSCRIPTION -> {
				
				bronzeSubscription.value.apply {
					
					result = HappyAppSkus.BRONZE_SUBSCRIPTION
					
				}
				
			}
			
			HappyAppSkus.SILVER_SUBSCRIPTION -> {
				
				silverSubscriptionLiveData.value.apply {
					
					result = HappyAppSkus.SILVER_SUBSCRIPTION
					
				}
				
			}
			
			HappyAppSkus.GOLD_SUBSCRIPTION -> {
				
				goldSubscriptionLiveData.value.apply {
					
					result = HappyAppSkus.GOLD_SUBSCRIPTION
					
				}
				
			}
			
			HappyAppSkus.PLATINUM_SUBSCRIPTION -> {
				
				platinumSubscriptionLiveData.value.apply {
					
					result = HappyAppSkus.PLATINUM_SUBSCRIPTION
					
				}
				
			}
			
		}
		
		return result
		
	}
	
	/**
	 * This private object class shows an example retry policies. You may choose to replace it with
	 * your own policies.
	 */
	private object RetryPolicies {
		
		private const val maxRetry = 3
		private var retryCounter = AtomicInteger(1)
		private const val baseDelayMillis = 500
		private const val taskDelay = 2000L
		
		fun resetConnectionRetryPolicyCounter() {
			
			Timber.d("resetConnectionRetryPolicyCounter: called")
			
			// Reset the retry counter
			retryCounter.set(1)
			
		}
		
		/**
		 * This works because it actually only makes one call. Then it waits for success or failure.
		 * onSuccess it makes no more calls and resets the retryCounter to 1. onFailure another
		 * call is made, until too many failures cause retryCounter to reach maxRetry and the
		 * policy stops trying. This is a safe algorithm: the initial calls to
		 * connectToPlayBillingService from instantiateAndConnectToPlayBillingService is always
		 * independent of the RetryPolicies. And so the Retry Policy exists only to help and never
		 * to hurt.
		 */
		fun connectionRetryPolicy(block: suspend () -> Unit) {
			
			Timber.d("connectionRetryPolicy: called")
			
			// Launch the coroutine to wait for a specific delay
			val scope = CoroutineScope(Job() + Dispatchers.Main)
			scope.launch {
				
				// Get and increment the current retry counter
				val counter = retryCounter.getAndIncrement()
				
				// Check if the counter is smaller than the maximum retry count and if so, wait a bit and execute the given function
				if (counter < maxRetry) {
					
					// Calculate the time to be waited
					val waitTime: Long = (2f.pow(counter) * baseDelayMillis).toLong()
					
					// Delay the scope for the calculated time
					delay(waitTime)
					
					// Execute the given function
					block()
					
				}
				
				// Else, throw an exception
				else {
					
					throw FirebaseNetworkException("Retry count reached")
					
				}
				
			}
			
		}
		
		/**
		 * All this is doing is check that billingClient is connected and if it's
		 * not, request connection, wait x number of seconds and then proceed with
		 * the actual task.
		 */
		fun taskExecutionRetryPolicy(
			billingRepository: BillingRepository,
			billingClient: BillingClient,
			coroutineContext: CoroutineContext,
			task: suspend () -> Unit
		) {
			
			Timber.d("taskExecutionRetryPolicy: called")
			
			// Launch the coroutine
			val scope = CoroutineScope(coroutineContext)
			scope.launch {
				
				// If the billingClient is not ready, wait some seconds and restart the connection
				if (!billingClient.isReady) {
					
					Timber.d("billing not ready")
					
					// Restart the connection
					billingRepository.connectToGooglePlayBillingService()
					
					// Delay the scope
					delay(taskDelay)
					
				}
				
				// Proceed with the given task
				task()
				
			}
		}
	}
	
	/**
	 * This is the throttling valve. It is used to modulate how often calls are
	 * made to the secure server in order to save money.
	 */
	private object Throttle {
		
		private const val DEAD_BAND = 7200000 //2*60*60*1000: two hours wait
		private const val PREFS_NAME = "BillingRepository.Throttle"
		private const val KEY = "lastInvocationTime"
		
		/**
		 * Checks if the lastInvocationTime is longer than 2 hours ago.
		 */
		fun isLastInvocationTimeStale(context: Context): Boolean {
			
			// Define a SharedPreferences object from the context
			val sharedPreferences = context.getSharedPreferences(
				PREFS_NAME,
				Context.MODE_PRIVATE
			)
			
			// Get the lastInvocationTime from the SharedPreferences
			val lastInvocationTime = sharedPreferences.getLong(
				KEY,
				0
			)
			
			return lastInvocationTime + DEAD_BAND < Date().time
			
		}
		
		/**
		 * Updates the lastInvocationTime in the SharedPreferences.
		 */
		fun refreshLastInvocationTime(context: Context) {
			
			// Define a SharedPreferences object from the context
			val sharedPreferences = context.getSharedPreferences(
				PREFS_NAME,
				Context.MODE_PRIVATE
			)
			
			// Put NOW into the SharedPreferences as the lastInvocationTime
			with(sharedPreferences.edit()) {
				putLong(
					KEY,
					Date().time
				)
				apply()
			}
		}
	}
	
}
