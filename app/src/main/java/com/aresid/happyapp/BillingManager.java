package com.aresid.happyapp;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created on: 13.10.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class BillingManager
		implements PurchasesUpdatedListener {

	private static final String                        TAG = "BillingManager";
	private static final HashMap<String, List<String>> SKUS;

	static {

		Log.d(TAG, "static initializer:true");

		SKUS = new HashMap<>();
		SKUS.put(BillingClient.SkuType.SUBS, Collections.singletonList("happyapp.subscription.bronze"));
		SKUS.put(BillingClient.SkuType.SUBS, Collections.singletonList("happyapp.subscription.silver"));
		SKUS.put(BillingClient.SkuType.SUBS, Collections.singletonList("happyapp.subscription.gold"));
		SKUS.put(BillingClient.SkuType.SUBS, Collections.singletonList("happyapp.subscription.platinum"));

	}

	private Activity      mActivity;
	private BillingClient mBillingClient;

	BillingManager(Activity activity) {

		Log.d(TAG, "BillingManager:true");

		mActivity = activity;

		mBillingClient = BillingClient.newBuilder(activity)
		                              .enablePendingPurchases()
		                              .setListener(this)
		                              .build();

		mBillingClient.startConnection(new BillingClientStateListener() {

			@Override
			public void onBillingSetupFinished(BillingResult billingResult) {

				Log.d(TAG, "onBillingSetupFinished:true");

				if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

					Log.i(TAG, "onBillingSetupFinished: result = " + billingResult.getResponseCode());

				} else {

					Log.w(TAG, "onBillingSetupFinished: result = " + billingResult.getResponseCode());

				}

			}

			@Override
			public void onBillingServiceDisconnected() {

				Log.d(TAG, "onBillingServiceDisconnected:true");

			}

		});

	}

	void startPurchaseFlow(SkuDetails skuDetails) {

		Log.d(TAG, "startPurchaseFlow:true");

		BillingFlowParams params = BillingFlowParams.newBuilder()
		                                            .setSkuDetails(skuDetails)
		                                            .build();

		int responseCode = mBillingClient.launchBillingFlow(mActivity, params)
		                                 .getResponseCode();

	}

	List<String> getSkus(@BillingClient.SkuType String type) {

		Log.d(TAG, "getSkus:true");

		Log.d(TAG, "getSkus: " + SKUS.get(type));

		return SKUS.get(type);

	}

	void querySkuDetailsAsync(@BillingClient.SkuType final String skuType, final List<String> skuList, final SkuDetailsResponseListener listener) {

		Log.d(TAG, "querySkuDetailsAsync:true");

		SkuDetailsParams params = SkuDetailsParams.newBuilder()
		                                          .setSkusList(skuList)
		                                          .setType(skuType)
		                                          .build();

		Log.d(TAG, "querySkuDetailsAsync: is ready " + mBillingClient.isReady());

		if (mBillingClient.isReady()) {

			mBillingClient.querySkuDetailsAsync(params, listener);

		}

	}

	@Override
	public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

		Log.d(TAG, "onPurchasesUpdated:true");

	}

}
