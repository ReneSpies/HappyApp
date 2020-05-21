package com.aresid.happyapp.billing.billingrepository

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.android.billingclient.api.*
import com.aresid.happyapp.billing.billingrepository.localdatabase.*
import com.aresid.happyapp.keys.Keys
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
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
class BillingRepository private constructor(private val application: Application): PurchasesUpdatedListener, BillingClientStateListener, ConsumeResponseListener, SkuDetailsResponseListener {
	
	// BillingClient
	private lateinit var playStoreBillingClient: BillingClient
	
	// Secure server client
	private lateinit var secureServerBillingClient: BillingWebservice
	
	// LocalBillingDatabase
	private lateinit var localCacheBillingClient: LocalBillingDatabase
	
	val subscriptionSkuDetailsListLiveData: LiveData<List<AugmentedSkuDetails>> by lazy {
		
		if (!::localCacheBillingClient.isInitialized) {
			
			localCacheBillingClient = LocalBillingDatabase.getInstance(application)
			
		}
		
		localCacheBillingClient.augmentedSkuDetailDao().getSubscriptionSkuDetails()
		
	}
	
	val bronzeSubscriptionLiveData: LiveData<BronzeSubscription> by lazy {
		
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
	
	fun startDataSourceConnections() {
		
		Timber.d("startDataSourceConnections: called")
		
		defineAndConnectToGooglePlayBillingService()
		
		secureServerBillingClient = BillingWebservice.create()
		
		localCacheBillingClient = LocalBillingDatabase.getInstance(application)
		
	}
	
	fun endDataSourceConnections() {
		
		Timber.d("endDataSourceConnections: called")
		
		playStoreBillingClient.endConnection()
		
		// normally you don't worry about closing a DB connection unless you have
		// more than one open. so no need to call 'localCacheBillingClient.close()'
		
	}
	
	private fun defineAndConnectToGooglePlayBillingService() {
		
		Timber.d("defineAndConnectToGooglePlayBillingService: called")
		
		playStoreBillingClient = BillingClient.newBuilder(application.applicationContext).enablePendingPurchases().setListener(this).build()
		
		connectToGooglePlayBillingService()
		
	}
	
	private fun connectToGooglePlayBillingService(): Boolean {
		
		Timber.d("connectToGooglePlayBillingService: called")
		
		if (!playStoreBillingClient.isReady) {
			
			playStoreBillingClient.startConnection(this)
			
			return true
			
		}
		
		return false
		
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
	
	/**
	 * Implement this method to get notifications for purchases updates. Both
	 * purchases initiated by your app and the ones initiated by Play Store
	 * will be reported here.
	 *
	 * @param billingResult The result of the update.
	 * @param purchases List of updated purchases if present.
	 */
	override fun onPurchasesUpdated(
		billingResult: BillingResult?,
		purchases: MutableList<Purchase>?
	) {
		
		Timber.d("onPurchasesUpdated: called")
		
		when (billingResult?.responseCode) {
			
			BillingClient.BillingResponseCode.OK -> {
				
				purchases?.apply {
					
					processPurchases(toSet())
					
				}
				
			}
			
			BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
				
				Timber.d("Item already owned")
				
				queryPurchasesAsync()
				
			}
			
			BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
				
				Timber.e(
					"Your app's configuration is incorrect. Review in the Google PlayConsole. Possible causes of this error include: APK is not signed with release key; SKU productId mismatch."
				)
				
			}
			
			else -> Timber.w("BillingResponseCode = ${billingResult?.responseCode}")
			
		}
		
	}
	
	/**
	 * Called to notify that connection to billing service was lost.
	 * Note: This does not remove billing service connection itself - this
	 * binding to the service will remain active, and you will receive a call to
	 * [onBillingSetupFinished] when billing service is running again and setup is complete.
	 */
	override fun onBillingServiceDisconnected() {
		
		Timber.d("onBillingServiceDisconnected: called")
		
		RetryPolicies.connectionRetryPolicy { connectToGooglePlayBillingService() }
		
	}
	
	/**
	 * Called to notify that setup is complete.
	 *
	 * @param billingResult The response code from [BillingResult] which returns the status of
	 * the setup process.
	 */
	override fun onBillingSetupFinished(billingResult: BillingResult?) {
		
		Timber.d("onBillingSetupFinished: called")
		
		when (billingResult?.responseCode) {
			
			BillingClient.BillingResponseCode.OK -> {
				
				Timber.d("onBillingSetupFinished success")
				
				RetryPolicies.resetConnectionRetryPolicyCounter()
				
				querySubscriptionSkuDetailsAsync(Keys.HappyAppSkus.SUBSCRIPTION_SKUS)
				
				queryPurchasesAsync()
				
			}
			
			BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
				
				Timber.d("onBillingSetupFinished but billing is not available on this device")
				
			}
			
			else -> {
				
				Timber.d("onBillingSetupFinished with failure response code: ${billingResult?.responseCode}")
				
			}
			
		}
		
	}
	
	private fun querySubscriptionSkuDetailsAsync(
		skuList: List<String>
	) {
		
		Timber.d("querySubscriptionSkuDetailsAsync: called")
		
		val parameter = SkuDetailsParams.newBuilder()
		
		parameter.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
		
		RetryPolicies.taskExecutionRetryPolicy(
			playStoreBillingClient,
			this
		) {
			
			Timber.d("querySkuDetailsAsync for ${BillingClient.SkuType.SUBS}")
			
			playStoreBillingClient.querySkuDetailsAsync(
				parameter.build(),
				this
			)
			
		}
		
	}
	
	/**
	 * Called to notify that a consume operation has finished.
	 *
	 * @param billingResult The response code from [BillingResult] set to report the result of
	 * consume operation.
	 * @param purchaseToken The purchase token that was consumed.
	 */
	override fun onConsumeResponse(
		billingResult: BillingResult?,
		purchaseToken: String?
	) {
		
		Timber.d("onConsumeResponse: called")
		
		when (billingResult?.responseCode) {
			
			BillingClient.BillingResponseCode.OK -> {
				
				purchaseToken?.apply {
					
					//					saveToLocalDatabase(this)
					
				}
				
				secureServerBillingClient.onConsumeResponse(
					purchaseToken,
					billingResult.responseCode
				)
				
			}
			
			else -> Timber.w("Error consuming purchase with token $purchaseToken. Response code = ${billingResult?.responseCode}.")
			
		}
		
	}
	
	/**
	 * Called to notify that a fetch SKU details operation has finished.
	 *
	 * @param billingResult Response code of the update.
	 * @param skuDetailsList List of SKU details.
	 */
	override fun onSkuDetailsResponse(
		billingResult: BillingResult?,
		skuDetailsList: MutableList<SkuDetails>?
	) {
		
		Timber.d("onSkuDetailsResponse: called")
		
		if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
			
			Timber.d("SkuDetails queried with success, list = $skuDetailsList")
			
		}
		else {
			
			Timber.d("SkuDetails queried with failure, code = ${billingResult?.responseCode}")
			
		}
		
		if (skuDetailsList.orEmpty().isNotEmpty()) {
			
			val scope = CoroutineScope(Job() + Dispatchers.IO)
			scope.launch {
				
				skuDetailsList?.forEach {
					
					localCacheBillingClient.augmentedSkuDetailDao().insertOrUpdate(it)
					
				}
				
			}
			
		}
		
	}
	
	fun queryPurchasesAsync() {
		
		Timber.d("queryPurchasesAsync: called")
		
		fun task() {
			
			Timber.d("task: called")
			
			val purchasesResult = HashSet<Purchase>()
			
			if (isSubscriptionSupported()) {
				
				val result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.SUBS)
				
				result.purchasesList?.apply {
					
					purchasesResult.addAll(this)
					
				}
				
				Timber.d("queryPurchasesAsync subs result = ${result.purchasesList}")
				
			}
			
			processPurchases(purchasesResult)
			
		}
		
		RetryPolicies.taskExecutionRetryPolicy(
			playStoreBillingClient,
			this
		) {
			
			task()
			
		}
		
	}
	
	private fun processPurchases(purchaseResult: Set<Purchase>) = CoroutineScope(Job() + Dispatchers.IO).launch {
		
		val cachedPurchases = localCacheBillingClient.purchaseDao().getPurchases()
		
		val newBatch = HashSet<Purchase>(purchaseResult.size)
		
		purchaseResult.forEach { purchase ->
			
			if (isSignatureValid(purchase) && !cachedPurchases.any { it.purchase == purchase }) {
				
				newBatch.add(purchase)
				
			}
			
		}
		
		if (newBatch.isNotEmpty()) {
			
			sendPurchasesToServer(newBatch)
			
			// We still care about purchaseResult in case an old purchase
			// has not been consumed yet
			saveToLocalDatabase(
				newBatch,
				purchaseResult
			)
			
		}
		else if (Throttle.isLastInvocationTimeStale(application)) {
			
			queryPurchasesFromSecureServer()
			
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
	
	private fun sendPurchasesToServer(purchases: Set<Purchase>) {
		
		Timber.d("sendPurchasesToServer: called")
		
		// TODO:  sendPurchasesToServer:
		
	}
	
	private fun queryPurchasesFromSecureServer() {
		
		Timber.d("queryPurchasesFromSecureServer: called")
		
		fun getPurchasesFromSecureServerToLocalDatabase() {
			
			Timber.d("getPurchasesFromSecureServerToLocalDatabase: called")
			
			// TODO:  getPurchasesFromSecureServerToLocalDatabase:
			
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
					
					Keys.HappyAppSkus.BRONZE_SUBSCRIPTION -> {
						
						val bronzeSubscription = BronzeSubscription(true)
						
						insert(bronzeSubscription)
						
						localCacheBillingClient.augmentedSkuDetailDao().insertOrUpdate(
							purchase.sku,
							bronzeSubscription.mayPurchase()
						)
						
						Keys.HappyAppSkus.SUBSCRIPTION_SKUS.forEach { otherSku ->
							
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
	
	fun launchBillingFlow(
		activity: Activity,
		augmentedSkuDetails: AugmentedSkuDetails
	) = launchBillingFlow(
		activity,
		com.android.billingclient.api.SkuDetails(augmentedSkuDetails.originalJson)
	)
	
	fun launchBillingFlow(
		activity: Activity,
		skuDetails: SkuDetails
	) {
		
		Timber.d("launchBillingFlow: called")
		
		val oldSku: String? = getOldSku(skuDetails.sku)
		
		// TODO:  launchBillingFlow: setOldSku() needs a >purchaseToken<!!!
		
		val purchaseParameters = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).setOldSku(oldSku).build()
		
		RetryPolicies.taskExecutionRetryPolicy(
			playStoreBillingClient,
			this
		) {
			playStoreBillingClient.launchBillingFlow(
				activity,
				purchaseParameters
			)
		}
		
	}
	
	private fun getOldSku(sku: String?): String? {
		
		Timber.d("getOldSku: called")
		
		var result: String? = null
		
		when (sku) {
			
			Keys.HappyAppSkus.BRONZE_SUBSCRIPTION -> {
				
				bronzeSubscriptionLiveData.value.apply {
					
					result = Keys.HappyAppSkus.BRONZE_SUBSCRIPTION
					
				}
				
			}
			
			Keys.HappyAppSkus.SILVER_SUBSCRIPTION -> {
				
				silverSubscriptionLiveData.value.apply {
					
					result = Keys.HappyAppSkus.SILVER_SUBSCRIPTION
					
				}
				
			}
			
			Keys.HappyAppSkus.GOLD_SUBSCRIPTION -> {
				
				goldSubscriptionLiveData.value.apply {
					
					result = Keys.HappyAppSkus.GOLD_SUBSCRIPTION
					
				}
				
			}
			
			Keys.HappyAppSkus.PLATINUM_SUBSCRIPTION -> {
				
				platinumSubscriptionLiveData.value.apply {
					
					result = Keys.HappyAppSkus.PLATINUM_SUBSCRIPTION
					
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
		fun connectionRetryPolicy(block: () -> Unit) {
			
			Timber.d("connectionRetryPolicy: called")
			
			// Launch the coroutine
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
			}
		}
		
		/**
		 * All this is doing is check that billingClient is connected and if it's
		 * not, request connection, wait x number of seconds and then proceed with
		 * the actual task.
		 */
		fun taskExecutionRetryPolicy(
			billingClient: BillingClient,
			listener: BillingRepository,
			task: () -> Unit
		) {
			
			Timber.d("taskExecutionRetryPolicy: called")
			
			// Launch the coroutine
			val scope = CoroutineScope(Job() + Dispatchers.Main)
			scope.launch {
				
				// If the billingClient is not ready, wait some seconds and restart the connection
				if (!billingClient.isReady) {
					
					Timber.d("billing not ready")
					
					// Restart the connection
					billingClient.startConnection(listener)
					
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
