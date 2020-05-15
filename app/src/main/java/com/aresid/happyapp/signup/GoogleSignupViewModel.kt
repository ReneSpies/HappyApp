package com.aresid.happyapp.signup

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber

/**
 *    Created on: 15.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class GoogleSignupViewModel: ViewModel() {
	
	init {
		
		Timber.d("init: called")
		
	}
	
	fun processGoogleSignup(data: Intent?) {
		
		Timber.d("processGoogleSignup: called")
		
		val task = GoogleSignIn.getSignedInAccountFromIntent(data)
		
		try {
			
			val account = task.getResult(ApiException::class.java)!!
			
			val credential = GoogleAuthProvider.getCredential(
				account.idToken,
				null
			)
			
		}
		catch (e: ApiException) {
			
			Timber.e(e)
			
			// TODO:  onActivityResult: show feedback
			
		}
		
	}
	
}