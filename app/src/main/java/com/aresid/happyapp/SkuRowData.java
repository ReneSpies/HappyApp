package com.aresid.happyapp;

import android.util.Log;

/**
 * Created on: 13.10.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class SkuRowData {

	private static final String TAG = "SkuRowData";

	private final String mSku, mTitle, mDescription, mPrice, mBillingType;

	public SkuRowData(String sku, String title, String description, String price, String billingType) {

		Log.d(TAG, "SkuRowData:true");

		mSku = sku;
		mTitle = title;
		mDescription = description;
		mPrice = price;
		mBillingType = billingType;

	}

	public String getSku() {

		Log.d(TAG, "getSku:true");

		return mSku;

	}

	public String getTitle() {

		Log.d(TAG, "getSku:true");

		return mTitle;

	}

	public String getDescription() {

		Log.d(TAG, "getSku:true");

		return mDescription;

	}

	public String getPrice() {

		Log.d(TAG, "getSku:true");

		return mPrice;

	}

	public String getBillingType() {

		Log.d(TAG, "getSku:true");

		return mBillingType;

	}

}
