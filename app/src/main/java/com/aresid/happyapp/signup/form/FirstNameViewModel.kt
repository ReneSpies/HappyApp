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

class FirstNameViewModel: ViewModel() {
	
	// LiveData for the firstNameField value
	val firstName = MutableLiveData<String>()
	
	// LiveData for the event that the firstNameField is blank or null
	private val _firstNameEmpty = MutableLiveData<Boolean>()
	val firstNameEmpty: LiveData<Boolean>
		get() = _firstNameEmpty
	
	// LiveData for when the nextButton is clicked and the firstName is OK
	private val _firstNameOk = MutableLiveData<Boolean>()
	val firstNameOk: LiveData<Boolean>
		get() = _firstNameOk
	
	init {
		
		Timber.d("init: called")
		
		// firstName LiveData
		firstName.value = ""
		
		// firstNameEmpty LiveData
		_firstNameEmpty.value = false
		
		// firstNameOk LiveData
		_firstNameOk.value = false
		
	}
	
	/**
	 * Checks if the firstName is empty and sets the firstNameEmpty LiveData if true.
	 * If false, sets the firstNameOk LiveData.
	 */
	fun onNextButtonClicked() {
		
		Timber.d("onNextButtonClicked: called")
		
		// Reset all exceptions
		resetAllExceptions()
		
		// Check if the firstName is either blank or null and set the firstNameEmpty LiveData to show feedback to the user
		if (firstName.value.isNullOrBlank()) {
			
			// Set the firstNameEmpty LiveData
			_firstNameEmpty.value = true
			
			return
			
		}
		
		// Set the firstNameOk LiveData to tell the fragment it can navigate and notify the emailSignupViewModel about the firstName
		_firstNameOk.value = true
		
	}
	
	/**
	 * Resets the firstNameOk LiveData.
	 */
	fun navigatedAndNotified() {
		
		Timber.d("navigated: called")
		
		// Reset the firstNameOk LiveData
		_firstNameOk.value = false
		
	}
	
	/**
	 * Resets all Exception LiveData.
	 */
	private fun resetAllExceptions() {
		
		Timber.d("resetAllExceptions: called")
		
		// Reset firstNameEmpty LiveData
		_firstNameEmpty.value = false
		
	}
	
}