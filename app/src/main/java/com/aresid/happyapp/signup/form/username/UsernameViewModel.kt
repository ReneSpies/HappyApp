package com.aresid.happyapp.signup.form.username

import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aresid.happyapp.keys.FirestoreKeys
import com.aresid.happyapp.utils.ButtonUtil
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

/**
 *    Created on: 14.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class UsernameViewModel: ViewModel() {
	
	// LiveData for the usernameField value
	val username = MutableLiveData<String>()
	
	// LiveData for the event that the usernameField is blank or null
	private val _usernameEmpty = MutableLiveData<Boolean>()
	val usernameEmpty: LiveData<Boolean>
		get() = _usernameEmpty
	
	// LiveData for when the nextButton is clicked and the username is OK
	private val _usernameOk = MutableLiveData<Boolean>()
	val usernameOk: LiveData<Boolean>
		get() = _usernameOk
	
	// LiveData for when the username is already taken
	private val _usernameTaken = MutableLiveData<Boolean>()
	val usernameTaken: LiveData<Boolean>
		get() = _usernameTaken
	
	// LiveData for when there is no internet connection
	private val _noInternet = MutableLiveData<Boolean>()
	val noInternet: LiveData<Boolean>
		get() = _noInternet
	
	private val firestore: FirebaseFirestore
		get() = FirebaseFirestore.getInstance()
	
	init {
		
		Timber.d("init: called")
		
		// username LiveData
		username.value = ""
		
		// usernameEmpty LiveData
		_usernameEmpty.value = false
		
		// usernameOk LiveData
		_usernameOk.value = false
		
		// usernameTaken LiveData
		_usernameTaken.value = false
		
		// noInternet LiveData
		_noInternet.value = false
		
	}
	
	/**
	 * Checks if the username is empty and sets the usernameEmpty LiveData if true.
	 * Checks if the username is already taken on the server and sets the usernameTaken LiveData if true.
	 * If false, sets the usernameOk LiveData.
	 */
	fun onNextButtonClicked(view: View) {
		
		Timber.d("onNextButtonClicked: called")
		
		// Cast the view to Button, since XML expression do not allow casting themselves
		val button = view as Button
		
		// Reset all exceptions
		resetAllExceptions()
		
		// Check if the username is either blank or null and set the usernameEmpty LiveData to show feedback to the user
		if (username.value.isNullOrBlank()) {
			
			// Set the familyNameEmpty LiveData
			_usernameEmpty.value = true
			
			return
			
		}
		
		// Check if username is already registered in the Firestore
		checkIfUsernameIsTaken(button)
		
	}
	
	private fun checkIfUsernameIsTaken(button: Button) {
		
		Timber.d("checkIfUsernameIsTaken: called")
		
		// Start the loading animation and disable the button
		ButtonUtil.setAndStartLoadingButtonAnimationWithDisable(
			button,
			true
		)
		
		// Check if the username is already registered in the Firestore
		firestore.collection(FirestoreKeys.Collection.USERS).whereEqualTo(
			FirestoreKeys.Collection.Column.USERNAME,
			username.value
		).get().addOnSuccessListener { document ->
			
			Timber.d("great success checking if the username is already registered in the Firestore")
			
			// Remove the loading animation and enable the button again
			ButtonUtil.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			
			// If snapshot is empty and not from cache, the username is available
			if (document.isEmpty && !document.metadata.isFromCache) {

				// Set the usernameOk LiveData to tell the fragment it can navigate and notify the SignupFormData about the username
				_usernameOk.value = true

			}
			
			// Else, if the metadata isFromCache, there is no internet connection available
			else if (document.metadata.isFromCache) {
				
				// Set the noInternet LiveData
				_noInternet.value = true
				
			}
			
			// Else, the username is already taken
			else {
				
				// Set the usernameTaken LiveData
				_usernameTaken.value = true
				
			}

		}.addOnFailureListener { e ->
			
			Timber.d("failure checking if the username is already registered in the Firestore")
			
			Timber.e(e)
			
			// Remove the loading animation and enable the button again
			ButtonUtil.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			
			// TODO:  checkIfUsernameIsTaken: Check what
			//  exceptions can happen here,
			//  no internet connection is not triggered,
			//  it simply loads the data from the cache (low priority)
			
		}
		
	}
	
	/**
	 * Resets the usernameOk LiveData.
	 */
	fun navigatedAndNotified() {
		
		Timber.d("navigatedAndNotified: called")
		
		// Reset the usernameOk LiveData
		_usernameOk.value = false
		
	}
	
	/**
	 * Resets all Exception LiveData.
	 */
	private fun resetAllExceptions() {
		
		Timber.d("resetAllExceptions: called")
		
		// Reset familyNameEmpty LiveData
		_usernameEmpty.value = false
		
		// Reset the usernameTaken LiveData
		_usernameTaken.value = false
		
		// Reset the noInternet LiveData
		_noInternet.value = false
		
	}
	
}