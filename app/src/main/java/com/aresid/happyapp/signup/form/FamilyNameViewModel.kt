package com.aresid.happyapp.signup.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

/**
 *    Created on: 14.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class FamilyNameViewModel: ViewModel() {
	
	// LiveData for the familyNameField value
	val familyName = MutableLiveData<String>()
	
	// LiveData for the event that the familyNameField is blank or null
	private val _familyNameEmpty = MutableLiveData<Boolean>()
	val familyNameEmpty: LiveData<Boolean>
		get() = _familyNameEmpty
	
	// LiveData for when the nextButton is clicked and the familyName is OK
	private val _familyNameOk = MutableLiveData<Boolean>()
	val familyNameOk: LiveData<Boolean>
		get() = _familyNameOk
	
	init {
		
		Timber.d("init: called")
		
		// familyName LiveData
		familyName.value = ""
		
		// familyNameEmpty LiveData
		_familyNameEmpty.value = false
		
		// familyNameOk LiveData
		_familyNameOk.value = false
		
	}
	
	/**
	 * Checks if the familyName is empty and sets the familyNameEmpty LiveData if true.
	 * If false, sets the familyNameOk LiveData.
	 */
	fun onNextButtonClicked() {
		
		Timber.d("onNextButtonClicked: called")
		
		// Reset all exceptions
		resetAllExceptions()
		
		// Check if the familyName is either blank or null and set the familyNameEmpty LiveData to show feedback to the user
		if (familyName.value.isNullOrBlank()) {
			
			// Set the familyNameEmpty LiveData
			_familyNameEmpty.value = true
			
			return
			
		}
		
		// Set the familyNameOk LiveData to tell the fragment it can navigate and notify the SignupFormData about the familyName
		_familyNameOk.value = true
		
	}
	
	/**
	 * Resets the familyNameOk LiveData.
	 */
	fun navigatedAndNotified() {
		
		Timber.d("navigated: called")
		
		// Reset the familyNameOk LiveData
		_familyNameOk.value = false
		
	}
	
	/**
	 * Resets all Exception LiveData.
	 */
	private fun resetAllExceptions() {
		
		Timber.d("resetAllExceptions: called")
		
		// Reset familyNameEmpty LiveData
		_familyNameEmpty.value = false
		
	}
	
}