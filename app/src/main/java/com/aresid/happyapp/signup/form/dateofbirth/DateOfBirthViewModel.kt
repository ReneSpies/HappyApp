package com.aresid.happyapp.signup.form.dateofbirth

import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aresid.happyapp.keys.FirestoreKeys
import com.aresid.happyapp.signup.form.SignupFormData
import com.aresid.happyapp.utils.ButtonUtil
import com.aresid.happyapp.utils.NTPDate
import com.aresid.happyapp.utils.Util.isOlderThan18
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.*

/**
 *    Created on: 22.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class DateOfBirthViewModel: ViewModel() {
	
	// LiveData for the selected date from the DatePicker
	val date = MutableLiveData<Date>()
	
	// LiveData for the current state of the DatePicker
	val year = MutableLiveData<Int>()
	val month = MutableLiveData<Int>()
	val day = MutableLiveData<Int>()
	
	// LiveData for when there is no internet connection
	private val _noInternetConnection = MutableLiveData<Boolean>()
	val noInternetConnection: LiveData<Boolean>
		get() = _noInternetConnection
	
	// LiveData for when the user is younger than 18
	private val _youngerThan18 = MutableLiveData<Boolean>()
	val youngerThan18: LiveData<Boolean>
		get() = _youngerThan18
	
	// LiveData for when the email is wrong
	private val _emailIncorrect = MutableLiveData<Boolean>()
	val emailIncorrect: LiveData<Boolean>
		get() = _emailIncorrect
	
	// LiveData for when the user is successfully registered
	private val _registerSuccessful = MutableLiveData<Boolean>()
	val registerSuccessful: LiveData<Boolean>
		get() = _registerSuccessful
	
	// LiveData for when the email is already in use
	private val _emailAlreadyInUse = MutableLiveData<Boolean>()
	val emailAlreadyInUse: LiveData<Boolean>
		get() = _emailAlreadyInUse
	
	// Member object representing the date of birth
	private lateinit var mDateOfBirth: Calendar
	
	init {
		
		Timber.d(": called")
		
		// year LiveData
		year.value = null
		
		// noInternetConnection LiveData
		_noInternetConnection.value = false
		
		// youngerThan18 LiveData
		_youngerThan18.value = false
		
		// emailIncorrect LiveData
		_emailIncorrect.value = false
		
		// registerSuccessful LiveData
		_registerSuccessful.value = false
		
		// emailAlreadyInUse LiveData
		_emailAlreadyInUse.value = false
		
	}
	
	/**
	 * This function is called by the DateOfBirthFragment
	 * to reset the registerSuccessful LiveData.
	 */
	fun notified() {
		
		Timber.d("notified: called")
		
		// Reset the registerSuccessful LiveData
		_registerSuccessful.value = false
		
	}
	
	/**
	 * Takes the current LiveData state and converts it into a Date object.
	 */
	private fun getSelectedDateAsCalendar(): Calendar {
		
		Timber.d("getSelectedDate: called")
		
		val calendar = Calendar.getInstance()
		
		// The initial values for each LiveData is null, until the user scrolls the DatePicker.
		// I assume that, when the values are null, the user has not scrolled yet,
		// so I pass in the initialized values
		calendar.set(
			year.value ?: DateOfBirthFragment.DATE_PICKER_YEAR_INIT,
			month.value ?: DateOfBirthFragment.DATE_PICKER_MONTH_INIT,
			day.value ?: DateOfBirthFragment.DATE_PICKER_DAY_INIT
		)
		
		return calendar
		
	}
	
	fun onSubscribeButtonClicked(view: View) {
		
		Timber.d("onSubscribeButtonClicked: called")
		
		// Reset all errors
		resetAllErrors()
		
		// Cast the view to Button, since XML expression do not allow casting
		val button = view as Button
		
		// Start the loading animation on the Button and disable it
		ButtonUtil.setAndStartLoadingButtonAnimationWithDisable(
			button,
			true
		)
		
		var now = 0L
		
		try {
			
			runBlocking {
				
				withContext(Dispatchers.IO) {
					
					// Get the current time in milliseconds from NTP server
					now = NTPDate.getTimeInMillis()
					
				}
				
			}
			
		}
		catch (e: IOException) {
			
			Timber.e(e)
			
			// Disable the loading animation on the Button and enable it
			ButtonUtil.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			
			_noInternetConnection.value = true
			
			return
			
		}
		
		// Save the getSelectedDateAsCalendar in the member variable
		mDateOfBirth = getSelectedDateAsCalendar()
		
		// User is older than 18, register him
		if (mDateOfBirth.isOlderThan18(now)) {
			
			Timber.d("is older than 18")
			
			// Register the user in Firebase
			registerUser(button)
			
		}
		
		// Else, trigger LiveData to show an error
		else {
			
			Timber.d("is younger than 18")
			
			_youngerThan18.value = true
			
			// Disable the loading animation on the Button and enable it
			ButtonUtil.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			
		}
		
	}
	
	/**
	 * Creates a new user with email and password in Firebase.
	 * Uses the [button] to disable its loading animation and enable it again.
	 */
	private fun registerUser(button: Button) {
		
		Timber.d("registerUser: called")
		
		// Get the credentials from the singleton
		val email = SignupFormData.getInstance().email
		val password = SignupFormData.getInstance().password
		
		FirebaseAuth.getInstance().createUserWithEmailAndPassword(
			email,
			password
		).addOnSuccessListener {
			
			Timber.d("great success creating new user with email and password")
			
			// Disable the loading animation on the Button and enable it
			ButtonUtil.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			
			// Send the user a verification email
			it.user?.sendEmailVerification()
			
			// Save the user's data in the Firestore
			saveUserDataInFirestore(it.user!!.uid)
			
		}.addOnFailureListener { e ->
			
			Timber.w("failure creating new user with email and password")
			
			Timber.e(e)
			
			// Disable the loading animation on the Button and enable it
			ButtonUtil.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			
			when (e) {
				
				is FirebaseAuthInvalidCredentialsException -> _emailIncorrect.value = true
				
				is FirebaseNetworkException -> _noInternetConnection.value = true
				
				is FirebaseAuthUserCollisionException -> _emailAlreadyInUse.value = true
				
			}
			
		}
		
	}
	
	private fun saveUserDataInFirestore(uid: String) {
		
		Timber.d("saveUserDataInFirestore: called")
		
		// Get the date of creation in milliseconds from the NTP server
		val dateOfCreationInMillis = runBlocking {
			
			withContext(Dispatchers.IO) {
				
				NTPDate.getTimeInMillis()
				
			}
			
		}
		
		// Define a Calendar object
		val calendar = Calendar.getInstance()
		
		// Set the timeInMillis in the calendar to the dateOfCreationInMillis
		calendar.timeInMillis = dateOfCreationInMillis
		
		// Define a HashMap containing the information to put into the Firestore
		val data = hashMapOf(
			FirestoreKeys.Collection.Column.DATE_OF_CREATION to calendar.time.toString(),
			FirestoreKeys.Collection.Column.DATE_OF_BIRTH to mDateOfBirth.time.toString(),
			FirestoreKeys.Collection.Column.USERNAME to SignupFormData.getInstance().username,
			FirestoreKeys.Collection.Column.EMAIL to SignupFormData.getInstance().email,
			FirestoreKeys.Collection.Column.FIRST_NAME to SignupFormData.getInstance().firstName,
			FirestoreKeys.Collection.Column.FAMILY_NAME to SignupFormData.getInstance().familyName
		)
		
		FirebaseFirestore.getInstance().collection(FirestoreKeys.Collection.USERS).document(uid).set(data).addOnSuccessListener {
			
			Timber.d("great success adding the user's data to the firestore")
			
			// Set the registerSuccessful LiveData
			_registerSuccessful.value = true
			
		}.addOnFailureListener { e ->
			
			Timber.w("failure adding user's data to the firestore")
			
			Timber.e(e)
			
			when (e) {
				
				is FirebaseNetworkException -> _noInternetConnection.value = true
				
			}
			
		}
		
	}
	
	/**
	 * Resets all error LiveData.
	 */
	private fun resetAllErrors() {
		
		Timber.d("resetAllErrors: called")
		
		// noInternetConnection LiveData
		_noInternetConnection.value = false
		
		// youngerThan18 LiveData
		_youngerThan18.value = false
		
		// emailIncorrect LiveData
		_emailIncorrect.value = false
		
		// emailAlreadyInUse LiveData
		_emailAlreadyInUse.value = false
		
	}
	
}