package com.aresid.happyapp.subscribe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

/**
 *    Created on: 15.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SubscribeViewModel: ViewModel() {
	
	// LiveData to navigate to the MainFragment
	val navigateToMainFragment = MutableLiveData<Boolean>()
	
	init {
		
		Timber.d("init: called")
		
		// Init navigateToMainFragment LiveData
		navigateToMainFragment.value = false
		
	}
	
	/**
	 * Resets the [navigateToMainFragment] LiveData.
	 */
	fun navigated() {
		
		Timber.d("navigated: called")
		
		navigateToMainFragment.value = false
		
	}
	
}
