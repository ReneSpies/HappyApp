package com.aresid.happyapp.signup.form.username

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aresid.happyapp.keys.Keys
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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
		
	}
	
	/**
	 * Checks if the username is empty and sets the usernameEmpty LiveData if true.
	 * Checks if the username is already taken on the server and sets the usernameTaken LiveData if true.
	 * If false, sets the usernameOk LiveData.
	 */
	fun onNextButtonClicked() {
		
		Timber.d("onNextButtonClicked: called")
		
		// Reset all exceptions
		resetAllExceptions()
		
		// Check if the username is either blank or null and set the usernameEmpty LiveData to show feedback to the user
		if (username.value.isNullOrBlank()) {
			
			// Set the familyNameEmpty LiveData
			_usernameEmpty.value = true
			
			return
			
		}
		
		// Check if username is already registered in the Firestore
		checkIfUsernameIsTaken()
		
	}
	
	private fun checkIfUsernameIsTaken() {
		
		Timber.d("checkIfUsernameIsTaken: called")
		
		// Check if the username is already registered in the Firestore
		firestore.collection(Keys.FirestoreFieldKeys.KEY_COLLECTION_USERS).whereEqualTo(
			Keys.FirestoreFieldKeys.KEY_COLUMN_USERNAME,
			username.value
		).get().addOnSuccessListener { snapshot: QuerySnapshot ->
			
			Timber.d("great success checking if the username is already registered in the Firestore")
			
			Timber.d("snapshot is empty = ${snapshot.isEmpty}")
			
			Timber.d("snapshot size = ${snapshot.size()}")
			
			Timber.d("snapshot documents = ${snapshot.documents}")
			
			Timber.d("snapshot query = ${snapshot.query}")
			
			Timber.d("username = ${username.value}")
			
			// If snapshot is empty, the username is available
			if (snapshot.isEmpty) {
				
				// Set the usernameOk LiveData to tell the fragment it can navigate and notify the SignupFormData about the username
				_usernameOk.value = true
				
			}
			
			// Else, the username is already taken
			else {
				
				// Set the usernameTaken LiveData
				_usernameTaken.value = true
				
			}
			
		}.addOnFailureListener { e ->
			
			Timber.d("failure checking if the username is already registered in the Firestore")
			
			Timber.e(e)
			
			// TODO:  checkIfUsernameIsTaken: exception test
			
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
		
	}
	
}