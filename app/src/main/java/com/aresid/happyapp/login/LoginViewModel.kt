package com.aresid.happyapp.login

import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aresid.happyapp.billingrepository.BillingWebservice
import com.aresid.happyapp.keys.FirestoreKeys
import com.aresid.happyapp.utils.ButtonUtil
import com.aresid.happyapp.utils.LoadingStatus
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
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
	
	// LiveData for the FirebaseAuthInvalidUserException, meaning the is not recognized
	private val _firebaseAuthInvalidUserException = MutableLiveData<Boolean>()
	val firebaseAuthInvalidUserException: LiveData<Boolean>
		get() = _firebaseAuthInvalidUserException
	
	// LiveData for the FirebaseAuthInvalidCredentialException, meaning the password or email is wrong
	private val _firebaseAuthInvalidCredentialsException = MutableLiveData<Boolean>()
	val firebaseAuthInvalidCredentialsException: LiveData<Boolean>
		get() = _firebaseAuthInvalidCredentialsException
	
	// LiveData for the FirebaseNetworkException, meaning the user is not connected to the internet
	private val _firebaseNetworkException = MutableLiveData<Boolean>()
	val firebaseNetworkException: LiveData<Boolean>
		get() = _firebaseNetworkException
	
	// LiveData to toggle the loading screen in the LoginFragment
	private val _toggleLoadingScreen = MutableLiveData<LoadingStatus>()
	val toggleLoadingScreen: LiveData<LoadingStatus>
		get() = _toggleLoadingScreen
	
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
		
		// Init the FirebaseAuthInvalidUserException LiveData
		_firebaseAuthInvalidUserException.value = false
		
		// Init the FirebaseAuthInvalidCredentialException LiveData
		_firebaseAuthInvalidCredentialsException.value = false
		
		// Init the FirebaseNetworkException LiveData
		_firebaseNetworkException.value = false
		
		// toggleLoadingScreen LiveData
		_toggleLoadingScreen.value = LoadingStatus.IDLE
		
		// Verify the FirebaseUser
		verifyFirebaseUser()
		
	}
	
	/**
	 * Checks if the FirebaseUser is not null and if so,
	 * reloads the user, checks for errors and sets the
	 * appropriate value to [_toggleLoadingScreen].
	 */
	private fun verifyFirebaseUser() {
		
		Timber.d("verifyFirebaseUser: called")
		
		val firebaseUser = mFirebaseAuth.currentUser
		
		// If firebaseUser not null, set the toggleLoadingScreen value to LOADING
		// and reload the firebase user
		if (firebaseUser != null) {
			
			_toggleLoadingScreen.value = LoadingStatus.LOADING
			
			CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
				
				try {
					
					// Reload the FirebaseUser to check if the account got deleted
					firebaseUser.reload().await()
					
					// FirebaseUser reloading successful, now check the Firestore
					val userDocument = BillingWebservice.getInstance().getPurchases(firebaseUser.uid)
					
					// FirebaseUser is ok, send him through
					withContext(Dispatchers.Main) {
						
						// If the userDocument is from cache, show a no internet error
						if (userDocument.metadata.isFromCache) {
							
							_toggleLoadingScreen.value = LoadingStatus.ERROR_NO_INTERNET
							
						}
						
						val subscription = userDocument.get(FirestoreKeys.Collection.Column.SUBSCRIPTION_VARIANT)
						
						// If the subscription is not null, send the user to MainFragment
						if (subscription != null) {
							
							_toggleLoadingScreen.value = LoadingStatus.SUCCESS
							
						}
						
						// Else, send the user to SubscribeFragment
						else {
							
							_toggleLoadingScreen.value = LoadingStatus.ERROR_NOT_SUBSCRIBED
							
						}
						
					}
					
				}
				catch (e: Exception) {
					
					Timber.w("failure reloading firebase user")
					
					Timber.e(e)
					
					withContext(Dispatchers.Main) {
						
						when (e) {
							
							is FirebaseNetworkException -> _toggleLoadingScreen.value = LoadingStatus.ERROR_NO_INTERNET
							
							is FirebaseAuthInvalidUserException -> _toggleLoadingScreen.value = LoadingStatus.ERROR_USER_DELETED
							
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Resets all Firebase exception LiveData used to give feedback to the user.
	 */
	private fun resetAllFirebaseExceptions() {
		
		Timber.d("resetAllFirebaseExceptions: called")
		
		// Reset FirebaseInvalidUserException
		_firebaseAuthInvalidUserException.value = false
		
		// Reset FirebaseInvalidCredentialsException
		_firebaseAuthInvalidCredentialsException.value = false
		
		// Reset the FirebaseNetworkException
		_firebaseNetworkException.value = false
		
	}
	
	/**
	 * Resets all exceptions.
	 * Checks if the emailField and passwordField are neither null nor blank.
	 * Calls loginUserWithEmailAndPassword if everything fits.
	 */
	fun onLoginButtonClicked(view: View) {
		
		Timber.d("onLoginButtonClicked: called")
		
		// Cast View to Button. XML expression do not allow a cast to Button in XML, so I do it here
		val button = view as Button
		
		// Reset all FirebaseExceptions
		resetAllFirebaseExceptions()
		
		// Check if input is empty and show an error, else reset the error
		if (email.value.isNullOrBlank() || password.value.isNullOrBlank()) {
			
			// Set the LiveData to show an error in the fragment class
			_emailOrPasswordIsEmpty.value = true
			return
			
		}
		
		// Else, set the LiveData to false to reset the error
		else {
			
			_emailOrPasswordIsEmpty.value = false
			
		}
		
		// Login user with email and password
		loginUserWithEmailAndPassword(button)
		
	}
	
	/**
	 * Uses the mFirebaseAuth object to log the user in with email and password.
	 * Sets and resets a loading animation on the button.
	 * And sets the firebaseUser LiveData.
	 */
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
		).addOnSuccessListener {
			
			Timber.d("great success authenticating user with email and password")
			
			verifyFirebaseUser()
			
			// Reset the buttons loading animation and enable it again
			ButtonUtil.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			
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
				is FirebaseAuthInvalidCredentialsException -> _firebaseAuthInvalidCredentialsException.value = true
				
				// No or bad internet
				is FirebaseNetworkException -> _firebaseNetworkException.value = true
				
			}
			
		}
		
	}
	
	/**
	 * Called from the [LoginFragment] to reset the LiveData.
	 */
	fun navigated() {
		
		Timber.d("navigatedToMainFragment: called")
		
		// Reset the LiveData
		_toggleLoadingScreen.value = LoadingStatus.IDLE
		
	}
	
}
