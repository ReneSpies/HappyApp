package com.aresid.happyapp.signup

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber

/**
 *    Created on: 15.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class GoogleSignupViewModel: ViewModel() {
	
	// LiveData for the FirebaseUser
	private val _firebaseUser = MutableLiveData<FirebaseUser>()
	val firebaseUser: LiveData<FirebaseUser>
		get() = _firebaseUser
	
	init {
		
		Timber.d("init: called")
		
		// Init the FirebaseUser LiveData
		_firebaseUser.value = null
		
	}
	
	/**
	 * Signs the User up.
	 */
	fun processGoogleSignup(data: Intent?) {
		
		Timber.d("processGoogleSignup: called")
		
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
			
			// TODO:  onActivityResult: show feedback
			
		}
		
	}
	
	/**
	 * Signs up with the given credential.
	 */
	private fun signUpWithCredential(credential: AuthCredential) {
		
		Timber.d("signUpWithCredential: called")
		
		// Signup with credential
		FirebaseAuth.getInstance().signInWithCredential(credential).addOnSuccessListener {
			
			Timber.d("great success signing up with credential")
			
			// Update the LiveData with the new FirebaseUser
			_firebaseUser.value = it.user
			
		}.addOnFailureListener { e ->
			
			Timber.d("failure signing up with credential")
			
			Timber.e(e)
			
		}
		
	}
	
}