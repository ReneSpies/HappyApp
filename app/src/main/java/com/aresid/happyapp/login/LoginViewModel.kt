package com.aresid.happyapp.login

import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aresid.happyapp.utils.ButtonUtil
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber

/**
 * Created on: 27/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

class LoginViewModel: ViewModel() {
	
	// LiveData for the login email
	// It's value is changed in the fragment_login.xml file using two-way data binding
	val email = MutableLiveData<String>()
	
	// LiveData for the login password
	// It's value is changed in the fragment_login.xml file using two-way data binding
	val password = MutableLiveData<String>()
	
	// LiveData for the check if email or password input is empty
	private val _emailOrPasswordIsEmpty = MutableLiveData<Boolean>()
	val emailOrPasswordIsEmpty: LiveData<Boolean>
		get() = _emailOrPasswordIsEmpty
	
	// LiveData for the FirebaseUser object
	// Used to navigate to the MainFragment in the LoginFragment if the user is not null
	private val _firebaseUser = MutableLiveData<FirebaseUser>()
	val firebaseUser: LiveData<FirebaseUser>
		get() = _firebaseUser
	
	// LiveData for the FirebaseAuthInvalidUserException, meaning the users account has been disabled or deleted
	private val _firebaseAuthInvalidUserException = MutableLiveData<Boolean>()
	val firebaseAuthInvalidUserException: LiveData<Boolean>
		get() = _firebaseAuthInvalidUserException
	
	// LiveData for the FirebaseAuthInvalidCredentialException, meaning the password or email is wrong
	private val _firebaseAuthInvalidCredentialException = MutableLiveData<Boolean>()
	val firebaseAuthInvalidCredentialsException: LiveData<Boolean>
		get() = _firebaseAuthInvalidCredentialException
	
	// LiveData for the FirebaseNetworkException, meaning the user is not connected to the internet
	private val _firebaseNetworkException = MutableLiveData<Boolean>()
	val firebaseNetworkException: LiveData<Boolean>
		get() = _firebaseNetworkException
	
	// FirebaseAuth object initiated in init
	private var mFirebaseAuth: FirebaseAuth
	
	init {
		
		Timber.d("init: called")
		
		// Init email LiveData
		email.value = ""
		
		// Init password LiveData
		password.value = ""
		
		// Init empty email or password LiveData
		_emailOrPasswordIsEmpty.value = false
		
		// Init the FirebaseAuth object
		mFirebaseAuth = FirebaseAuth.getInstance()
		
		// Init the FirebaseUser LiveData
		_firebaseUser.value = mFirebaseAuth.currentUser
		
		// Init the FirebaseAuthInvalidUserException LiveData
		_firebaseAuthInvalidUserException.value = false
		
		// Init the FirebaseAuthInvalidCredentialException LiveData
		_firebaseAuthInvalidCredentialException.value = false
		
		// Init the FirebaseNetworkException LiveData
		_firebaseNetworkException.value = false
		
	}
	
	private fun resetAllFirebaseExceptions() {
		
		Timber.d("resetAllFirebaseExceptions: called")
		
		// Reset FirebaseInvalidUserException
		_firebaseAuthInvalidUserException.value = false
		
		// Reset FirebaseInvalidCredentialsException
		_firebaseAuthInvalidCredentialException.value = false
		
		// Reset the FirebaseNetworkException
		_firebaseNetworkException.value = false
		
	}
	
	fun onLoginButtonClicked(view: View) {
		
		Timber.d("onLoginButtonClicked: called")
		
		// Cast View to Button. XML expression do not allow a cast to Button in XML, so I do it here
		val button = view as Button
		
		// Reset all FirebaseExceptions
		resetAllFirebaseExceptions()
		
		// Check if input is empty and show an error, else reset the error
		if (email.value!!.isEmpty() || password.value!!.isEmpty()) {
			
			// Set the LiveData to show an error in the fragment class
			_emailOrPasswordIsEmpty.value = true
			return
			
		}
		
		// Else, set the LiveData to false to reset the error
		else {
			
			_emailOrPasswordIsEmpty.value = false
			
		}
		
		loginUserWithEmailAndPassword(button)
		
	}
	
	private fun loginUserWithEmailAndPassword(button: Button) {
		
		Timber.d("loginUser: called")
		
		// Set and start the loading animation on the button and disable it
		ButtonUtil.setAndStartLoadingButtonAnimationWithDisable(
			button,
			true
		)
		
		mFirebaseAuth.signInWithEmailAndPassword(
			email.value!!,
			password.value!!
		).addOnSuccessListener { authResult ->
			
			Timber.d("great success authenticating user with email and password")
			
			// Set the new FirebaseUser. If the
			_firebaseUser.value = authResult.user
			
		}.addOnFailureListener { e ->
			
			Timber.e(e)
			
			// Reset the buttons loading animation and enable it again
			ButtonUtil.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			
			// Check which error e is and do appropriate things
			when (e) {

				// The users account has been disabled or deleted
				is FirebaseAuthInvalidUserException -> _firebaseAuthInvalidUserException.value = true
				
				// The password is wrong
				is FirebaseAuthInvalidCredentialsException -> _firebaseAuthInvalidCredentialException.value = true
				
				// No or bad internet
				is FirebaseNetworkException -> _firebaseNetworkException.value = true

			}
			
		}
		
	}
	
	fun onForgotLoginButtonClicked(view: View) {
		
		Timber.d("onForgotLoginButtonClicked: called")
		
		// Cast View to Button. XML expression do not allow a cast to Button in XML, so I do it here
		val button = view as Button
		
		// TODO:  onForgotLoginButtonClicked: implement logic
		
	}
	
}