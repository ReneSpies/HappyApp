package com.aresid.happyapp.signup.form.email

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

/**
 *    Created on: 21.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class EmailViewModel: ViewModel() {
	
	// LiveData for the emailField value
	val email = MutableLiveData<String>()
	
	// LiveData for when the emailField is empty
	private val _emailEmpty = MutableLiveData<Boolean>()
	val emailEmpty: LiveData<Boolean>
		get() = _emailEmpty
	
	// LiveData for when the email is OK
	private val _emailOk = MutableLiveData<Boolean>()
	val emailOk: LiveData<Boolean>
		get() = _emailOk
	
	init {
		
		Timber.d("init: called")
		
		// Init the email LiveData
		email.value = ""
		
		// Init the emailEmpty LiveData
		_emailEmpty.value = false
		
		// Init the emailOk LiveData
		_emailOk.value = false
		
	}
	
	fun onNextButtonClicked() {
		
		Timber.d("onNextButtonClicked: called")
		
		resetAllExceptions()
		
		// Check if email is either null or blank and
		// set the emailEmpty LiveData to show feedback to the user
		if (email.value.isNullOrBlank()) {
			
			// Set the emailEmpty LiveData
			_emailEmpty.value = true
			
			return
			
		}
		
		_emailOk.value = true
		
	}
	
	/**
	 * Resets all Exception LiveData.
	 */
	private fun resetAllExceptions() {
		
		Timber.d("resetAllExceptions: called")
		
		// emailEmpty LiveData
		_emailEmpty.value = false
		
	}
	
	/**
	 * Resets the emailOk LiveData.
	 */
	fun navigatedAndNotified() {
		
		Timber.d("navigatedAndNotified: called")
		
		// Reset the emailOk LiveData
		_emailOk.value = false
		
	}
	
}