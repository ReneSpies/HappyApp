package com.aresid.happyapp

import android.util.Log

/**
 * Created on: 13.10.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class SkuRowData(
	sku: String,
	title: String,
	description: String,
	price: String,
	billingType: String
) {
	
	private val mSku: String
	private val mTitle: String
	private val mDescription: String
	private val mPrice: String
	private val mBillingType: String
	val sku: String
		get() {
			Log.d(
				TAG,
				"getSku:true"
			)
			return mSku
		}
	
	val title: String
		get() {
			Log.d(
				TAG,
				"getSku:true"
			)
			return mTitle
		}
	
	val description: String
		get() {
			Log.d(
				TAG,
				"getSku:true"
			)
			return mDescription
		}
	
	val price: String
		get() {
			Log.d(
				TAG,
				"getSku:true"
			)
			return mPrice
		}
	
	val billingType: String
		get() {
			Log.d(
				TAG,
				"getSku:true"
			)
			return mBillingType
		}
	
	companion object {
		private const val TAG = "SkuRowData"
	}
	
	init {
		Log.d(
			TAG,
			"SkuRowData:true"
		)
		mSku = sku
		mTitle = title
		mDescription = description
		mPrice = price
		mBillingType = billingType
	}
}