package com.aresid.happyapp.signup

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aresid.happyapp.LoadingStatus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber

/**
 *    Created on: 15.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class GoogleSignupViewModel: ViewModel() {
	
	// LiveData to control the loadingScreen visibility
	private val _toggleLoadingScreen = MutableLiveData<LoadingStatus>()
	val toggleLoadingScreen: LiveData<LoadingStatus>
		get() = _toggleLoadingScreen
	
	init {
		
		Timber.d("init: called")
		
		// Init the toggleLoadingScreen LiveData
		_toggleLoadingScreen.value = LoadingStatus.INIT
		
	}
	
	/**
	 * Signs the User up.
	 */
	fun processGoogleSignup(data: Intent?) {
		
		Timber.d("processGoogleSignup: called")
		
		// Set the LiveData to LOADING
		_toggleLoadingScreen.value = LoadingStatus.LOADING
		
		// Define the task from GoogleSignIn
		val task = GoogleSignIn.getSignedInAccountFromIntent(data)
		
		try {
			
			// Define the account from the task result
			val account = task.getResult(ApiException::class.java)!!
			
			// Define the credential from GoogleAuthProvider
			val credential = GoogleAuthProvider.getCredential(
				account.idToken,
				null
			)
			
			// Signup with credential
			signUpWithCredential(credential)
			
		}
		catch (e: ApiException) {
			
			Timber.e(e)
			
			// TODO:  onActivityResult: show feedback (low priority)
			
		}
		
	}
	
	/**
	 * Called from the [GoogleSignupFragment] to reset the LiveData.
	 */
	fun navigated() {
		
		Timber.d("navigated: called")
		
		// Reset the LiveData
		_toggleLoadingScreen.value = LoadingStatus.IDLE
		
	}
	
	/**
	 * Signs up with the given credential.
	 */
	private fun signUpWithCredential(credential: AuthCredential) {
		
		Timber.d("signUpWithCredential: called")
		
		// Signup with credential
		FirebaseAuth.getInstance().signInWithCredential(credential).addOnSuccessListener {
			
			Timber.d("great success signing up with credential")
			
			if (it.user != null) {
				
				_toggleLoadingScreen.value = LoadingStatus.SUCCESS
				
				// If the email is not verified, send a verification email
				if (!it.user!!.isEmailVerified) {
					
					it.user!!.sendEmailVerification()
					
				}
				
			}
			
		}.addOnFailureListener { e ->
			
			Timber.d("failure signing up with credential")
			
			Timber.e(e)
			
		}
		
	}
	
}