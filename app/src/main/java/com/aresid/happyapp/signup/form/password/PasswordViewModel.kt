package com.aresid.happyapp.signup.form.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aresid.happyapp.utils.Util.containsCapitalLetter
import com.aresid.happyapp.utils.Util.containsNumber
import com.aresid.happyapp.utils.Util.isGreaterOrEqualToSix
import timber.log.Timber

/**
 *    Created on: 22.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class PasswordViewModel: ViewModel() {
	
	// LiveData for the password
	val password = MutableLiveData<String>()
	
	// LiveData for when the passwordField is empty
	private val _passwordEmpty = MutableLiveData<Boolean>()
	val passwordEmpty: LiveData<Boolean>
		get() = _passwordEmpty
	
	// LiveData for when the password does not meet the requirements
	private val _passwordWeak = MutableLiveData<Boolean>()
	val passwordWeak: LiveData<Boolean>
		get() = _passwordWeak
	
	// LiveData for when the password is OK
	private val _passwordOk = MutableLiveData<Boolean>()
	val passwordOk: LiveData<Boolean>
		get() = _passwordOk
	
	init {
		
		Timber.d("init: called")
		
		// password LiveData
		password.value = ""
		
		// passwordEmpty LiveData
		_passwordEmpty.value = false
		
		// passwordWeak LiveData
		_passwordWeak.value = false
		
		// passwordOk LiveData
		_passwordOk.value = false
		
	}
	
	fun onNextButtonClicked() {
		
		Timber.d("onNextButtonClicked: called")
		
		// Reset all errors
		resetAllErrors()
		
		// Else, if the password is nullOrBlank and if true, set the passwordEmpty LiveData
		if (password.value.isNullOrBlank()) {
			
			// Set the passwordEmpty LiveData
			_passwordEmpty.value = true
			
			return
			
		}
		
		// Check if the password meets the requirements and if true, set the passwordOk LiveData
		else if (password.value!!.isGreaterOrEqualToSix() && password.value!!.containsCapitalLetter() && password.value!!.containsNumber()) {
			
			// Set the passwordOk LiveData
			_passwordOk.value = true
			
			return
			
		}
		
		// Else, set the passwordWeak LiveData
		else {
			
			// Set the passwordWeak LiveData
			_passwordWeak.value = true
			
			return
			
		}
		
	}
	
	/**
	 * Resets all errors.
	 */
	private fun resetAllErrors() {
		
		Timber.d("resetAllErrors: called")
		
		// Reset the passwordEmpty LiveData
		_passwordEmpty.value = false
		
		// Reset the passwordWeak LiveData
		_passwordWeak.value = false
		
	}
	
	/**
	 * Resets the passwordOk LiveData.
	 */
	fun navigatedAndNotified() {
		
		Timber.d("navigatedAndNotified: called")
		
		// Reset the passwordOk LiveData
		_passwordOk.value = false
		
	}
	
}