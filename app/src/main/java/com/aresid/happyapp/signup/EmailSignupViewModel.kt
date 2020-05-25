package com.aresid.happyapp.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

/**
 *    Created on: 08.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class EmailSignupViewModel: ViewModel() {
	
	// LiveData for navigating to the SubscribeFragment
	private val _navigateToSubscribeFragment = MutableLiveData<Boolean>()
	val navigateToSubscribeFragment: LiveData<Boolean>
		get() = _navigateToSubscribeFragment
	
	init {
		
		Timber.d("init: called")
		
		// navigateToSubscribeFragment
		_navigateToSubscribeFragment.value = false
		
	}
	
	/**
	 * Sets the navigateToSubscribeFragment LiveData.
	 */
	fun navigateToSubscribeFragment() {
		
		Timber.d("navigateToSubscribeFragment: called")
		
		Timber.d("navigateToSubscribeFragment = ${_navigateToSubscribeFragment.value}")
		
		// Set the navigateToSubscribeFragment LiveData
		_navigateToSubscribeFragment.value = true
		
		Timber.d("navigateToSubscribeFragment = ${_navigateToSubscribeFragment.value}")
		
	}
	
	/**
	 * Called by the EmailSignupFragment to reset the [navigateToSubscribeFragment] LiveData.
	 */
	fun navigated() {
		
		Timber.d("navigated: called")
		
		_navigateToSubscribeFragment.value = false
		
	}
	
}