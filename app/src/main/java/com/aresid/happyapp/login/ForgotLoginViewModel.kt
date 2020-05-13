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
import timber.log.Timber

/**
 *    Created on: 12.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class ForgotLoginViewModel: ViewModel() {
	
	// LiveData for the email
	val email = MutableLiveData<String>()
	
	// LiveData for the event of password reset email sent
	// True if email has been sent, false default
	private val _passwordResetEmailSent = MutableLiveData<Boolean>()
	val passwordResetEmailSent: LiveData<Boolean>
		get() = _passwordResetEmailSent
	
	// LiveData for when the emailField is blank
	private val _emailBlank = MutableLiveData<Boolean>()
	val emailBlank: LiveData<Boolean>
		get() = _emailBlank
	
	// LiveData for the FirebaseAuthInvalidCredentialException, meaning the email is wrong
	private val _firebaseAuthInvalidCredentialsException = MutableLiveData<Boolean>()
	val firebaseAuthInvalidCredentialsException: LiveData<Boolean>
		get() = _firebaseAuthInvalidCredentialsException
	
	// LiveData for the FirebaseNetworkException, meaning the user is not connected to the internet
	private val _firebaseNetworkException = MutableLiveData<Boolean>()
	val firebaseNetworkException: LiveData<Boolean>
		get() = _firebaseNetworkException
	
	// LiveData for the FirebaseAuthInvalidUserException, meaning this email is not recognized
	private val _firebaseAuthInvalidUserException = MutableLiveData<Boolean>()
	val firebaseAuthInvalidUserException: LiveData<Boolean>
		get() = _firebaseAuthInvalidUserException
	
	// FirebaseAuth
	private val mFirebaseAuth = FirebaseAuth.getInstance()
	
	init {
		
		Timber.d("init: called")
		
		// Init email value
		email.value = ""
		
		// Init passwordResetEmailSent LiveData
		// True if email has been sent, false default
		_passwordResetEmailSent.value = false
		
		// Init emailBlank LiveData
		_emailBlank.value = false
		
		// Init FirebaseAuthInvalidCredentialException LiveData
		_firebaseAuthInvalidCredentialsException.value = false
		
		// Init FirebaseNetworkException LiveData
		_firebaseNetworkException.value = false
		
		// Init FirebaseAuthInvalidUserException LiveData
		_firebaseAuthInvalidUserException.value = false
		
	}
	
	fun onSendButtonClicked(view: View) {
		
		Timber.d("onSendButtonClicked: called")
		
		// Cast the View parameter to button since XML expression do not allow casting
		val button = view as Button
		
		// Reset all exceptions
		resetAllExceptions()
		
		// Check if emailField is empty and if so, set the appropriate LiveData
		if (email.value.isNullOrBlank()) {
			
			// Set the emailBlank LiveData to true to show an error message in the Fragment
			_emailBlank.value = true
			return
			
		}
		
		// Send the password reset email
		sendPasswordResetEmail(button)
		
	}
	
	private fun resetAllExceptions() {
		
		Timber.d("resetAllExceptions: called")
		
		// EmailBlank error
		_emailBlank.value = false
		
		// FirebaseAuthInvalidCredentialException
		_firebaseAuthInvalidCredentialsException.value = false
		
		// FirebaseNetworkException
		_firebaseNetworkException.value = false
		
		// FirebaseAuthInvalidUserException
		_firebaseAuthInvalidUserException.value = false
		
	}
	
	private fun sendPasswordResetEmail(button: Button) {
		
		Timber.d("sendPasswordResetEmail: called")
		
		// Start the loading animation on the button
		ButtonUtil.setAndStartLoadingButtonAnimationWithDisable(
			button,
			true
		)
		
		mFirebaseAuth.sendPasswordResetEmail(email.value!!).addOnSuccessListener {
			
			Timber.d("great success sending password reset email to user")
			
			_passwordResetEmailSent.value = true
			
			// Remove the button loading animation
			ButtonUtil.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			
		}.addOnFailureListener { e ->
			
			Timber.d("failure sending password reset email to user")
			
			Timber.e(e)
			
			// Remove the button loading animation
			ButtonUtil.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			
			when (e) {
				
				// Set FirebaseAuthInvalidCredentialException LiveData to true
				is FirebaseAuthInvalidCredentialsException -> _firebaseAuthInvalidCredentialsException.value = true
				
				// Set FirebaseNetworkException LiveData to true
				is FirebaseNetworkException -> _firebaseNetworkException.value = true
				
				// Set FirebaseAuthInvalidUserException LiveData to true
				is FirebaseAuthInvalidUserException -> _firebaseAuthInvalidUserException.value = true
				
			}
			
		}
		
	}
	
	fun showedSuccessSnackbar() {
		
		Timber.d("showedSuccessSnackbar: called")
		
		// Reset the passwordResetEmailSent LiveData
		_passwordResetEmailSent.value = false
		
	}
	
}