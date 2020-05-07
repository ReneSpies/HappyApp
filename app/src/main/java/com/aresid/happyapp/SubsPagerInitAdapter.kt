package com.aresid.happyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

/**
 * Created on: 04/02/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */
class SubsPagerInitAdapter internal constructor(context: Context): RecyclerView.Adapter<SubsPagerInitAdapter.ViewHolder>() {
	
	private val mContext: Context
	private var mRetryScreen: View? = null
	private var mLoadingScreen: View? = null
	private var mRetryScreenIsNeeded = false
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ViewHolder {
		Timber.d("onCreateViewHolder: called")
		
		val view = LayoutInflater.from(mContext).inflate(
			R.layout.content_subspagerinitadapter,
			parent,
			false
		)
		mLoadingScreen = view.findViewById(R.id.init_adapter_loading_screen)
		mRetryScreen = view.findViewById(R.id.init_adapter_retry_screen)
		if (mRetryScreenIsNeeded) {
			changeScreen(mRetryScreen)
		}
		else {
			changeScreen(mLoadingScreen)
		}
		return ViewHolder(view)
	}
	
	private fun changeScreen(neededScreen: View?) {
		Timber.d("changeScreen: called")
		
		if (neededScreen === mRetryScreen) {
			mLoadingScreen!!.visibility = View.INVISIBLE
			mRetryScreen!!.visibility = View.VISIBLE
		}
		else {
			mLoadingScreen!!.visibility = View.VISIBLE
			mRetryScreen!!.visibility = View.INVISIBLE
		}
	}
	
	override fun onBindViewHolder(
		holder: ViewHolder,
		position: Int
	) {
		Timber.d("onBindViewHolder: called")
		
	}
	
	override fun getItemCount(): Int {
		Timber.d("getItemCount: called")
		
		return 1
	}
	
	fun retry() {
		Timber.d("retry: called")
		
		changeScreen(mRetryScreen)
	}
	
	private fun toggleIsRetryScreenNeeded(isNeeded: Boolean) {
		Timber.d("toggleIsRetryScreenNeeded: called")
		
		mRetryScreenIsNeeded = isNeeded
	}
	
	inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
	companion object {
		private const val TAG = "SubsPagerInitAdapter"
	}
	
	init {
		Timber.d("init: called")
		
		mContext = context
	}
}