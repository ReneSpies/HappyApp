package com.aresid.happyapp.signup

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.aresid.happyapp.R
import com.aresid.happyapp.keys.Keys
import com.aresid.happyapp.utils.Util
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import timber.log.Timber

/**
 * Created on: 18/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class EmailSignupHelper internal constructor(
	context: Fragment,
	parent: View
) {
	
	private val mAuth: FirebaseAuth
	private val mContext: Fragment
	private var mListener: SignupCheckerListener? = null
	private val mParentView: View
	
	// Fields layouts
	private var mFirstNameFieldLayout: TextInputLayout? = null
	private var mFamilyNameFieldLayout: TextInputLayout? = null
	private var mUsernameFieldLayout: TextInputLayout? = null
	private var mEmailFieldLayout: TextInputLayout? = null
	private var mPasswordFieldLayout: TextInputLayout? = null
	private var mDateOfBirthFieldLayout: TextInputLayout? = null
	
	// Fields
	private var mFirstNameField: TextInputEditText? = null
	private var mFamilyNameField: TextInputEditText? = null
	private var mUsernameField: TextInputEditText? = null
	private var mEmailField: TextInputEditText? = null
	private var mPasswordField: TextInputEditText? = null
	private var mDateOfBirthField: TextInputEditText? = null
	
	// Field text Strings
	private var mFirstName: String? = null
	private var mFamilyName: String? = null
	private var mUsername: String? = null
	private var mEmail: String? = null
	private var mPassword: String? = null
	private var mDateOfBirth: String? = null
	private val mLegalitiesCheckbox: CheckBox? = null
	
	/**
	 * Defines the fields from given parent.
	 *
	 * @param parent Parent carrying the fields.
	 */
	private fun defineFields(parent: View) {
		Timber.d("defineFields: called")
		mFirstNameField = parent.findViewById(R.id.first_name_field)
		mFamilyNameField = parent.findViewById(R.id.family_name_field)
		mUsernameField = parent.findViewById(R.id.username_field)
		mEmailField = parent.findViewById(R.id.email_field)
		mPasswordField = parent.findViewById(R.id.password_field)
		mDateOfBirthField = parent.findViewById(R.id.date_of_birth_field)
	}
	
	/**
	 * Defines the field layouts from given parent.
	 *
	 * @param parent Parent carrying the field layouts.
	 */
	private fun defineFieldLayouts(parent: View) {
		Timber.d("defineFieldLayouts: called")
		mFirstNameFieldLayout = parent.findViewById(R.id.first_name_field_layout)
		mFamilyNameFieldLayout = parent.findViewById(R.id.family_name_field_layout)
		mUsernameFieldLayout = parent.findViewById(R.id.username_field_layout)
		mEmailFieldLayout = parent.findViewById(R.id.email_field_layout)
		mPasswordFieldLayout = parent.findViewById(R.id.password_field_layout)
		mDateOfBirthFieldLayout = parent.findViewById(R.id.date_of_birth_field_layout)
	} // Define the field text strings
	
	// Define the Bundle which will carry the information
	
	// Put the Strings into the Bundle using keys
	
	/**
	 * Bundles all the field texts into a bundle.
	 *
	 * @return The bundled field texts.
	 */
	val inputBundle: Bundle
		get() {
			Timber.d("getInputBundle: called")
			
			// Define the field text strings
			defineFieldStrings()
			
			// Define the Bundle which will carry the information
			val bundle = Bundle()
			
			// Put the Strings into the Bundle using keys
			bundle.putString(
				Keys.BundleKeys.KEY_FIRST_NAME,
				mFirstName
			)
			bundle.putString(
				Keys.BundleKeys.KEY_FAMILY_NAME,
				mFamilyName
			)
			bundle.putString(
				Keys.BundleKeys.KEY_USERNAME,
				mUsername
			)
			bundle.putString(
				Keys.BundleKeys.KEY_EMAIL,
				mEmail
			)
			bundle.putString(
				Keys.BundleKeys.KEY_DATE_OF_BIRTH,
				mDateOfBirth
			)
			return bundle
		}
	
	/**
	 * Defines the Strings carrying the fields texts.
	 */
	private fun defineFieldStrings() {
		Timber.d("defineFieldStrings: called")
		mFirstName = Util.getString(mFirstNameField!!.text)
		mFamilyName = Util.getString(mFamilyNameField!!.text)
		mUsername = Util.getString(mUsernameField!!.text)
		mEmail = Util.getString(mEmailField!!.text)
		mPassword = Util.getString(mPasswordField!!.text)
		mDateOfBirth = Util.getString(mDateOfBirthField!!.text)
	}
	
	/**
	 * Checks if the signup forms input is ok and calls SignupCheckerListener#inputIsOk() if so.
	 * Shows appropriate error messages otherwise.
	 */
	fun checkInput() {
		Timber.d("checkInput: called")
		
		// Reset layout errors
		resetLayoutErrors()
		
		// Define the field text strings
		defineFieldStrings()
		
		// Define empty field error message String
		val errorEmptyField = mContext.getString(R.string.error_you_forgot_me)
		
		// If first name empty, set error on mFirstNameFieldLayout
		if (mFirstName!!.length == 0) {
			mFirstNameFieldLayout!!.error = errorEmptyField
			return
		}
		
		// If family name empty, set error on mFamilyNameFieldLayout
		if (mFamilyName!!.length == 0) {
			mFamilyNameFieldLayout!!.error = errorEmptyField
			return
		}
		
		// If username empty, set error on mUsernameFieldLayout
		if (mUsername!!.length == 0) {
			mUsernameFieldLayout!!.error = errorEmptyField
			return
		}
		
		// If email empty, set error on mEmailFieldLayout
		if (mEmail!!.length == 0) {
			mEmailFieldLayout!!.error = errorEmptyField
			return
		}
		
		// If password length < 6, set error on mPasswordFieldLayout
		if (mPassword!!.length < 6) {
			mPasswordFieldLayout!!.error = mContext.getString(R.string.error_password_must_be_6_long)
			return
		}
		
		// If date of birth empty, set error on mDateOfBirthFieldLayout
		if (mDateOfBirth!!.length == 0) {
			mDateOfBirthFieldLayout!!.error = errorEmptyField
			return
		}
		
		// If CheckBox not checked, set error on mLegalitiesCheckbox
		if (!mLegalitiesCheckbox!!.isChecked) {
			mLegalitiesCheckbox.error = mContext.getString(R.string.error_please_confirm)
			return
		}
		
		/*
		Query FirebaseAuth if email is already in use.
		If not, query FirebaseFirestore if username is already taken.
		If not, call SignupCheckerListener#inputIsOk() callback.
		 */mAuth.fetchSignInMethodsForEmail(mEmail!!).addOnSuccessListener { result: SignInMethodQueryResult ->
			Timber.d("checkInput: great success checking email availability")
			
			// If signInMethods() size == 0, the email is available
			if (result.signInMethods != null && result.signInMethods!!.size == 0) {
				
				// Carry on and check the username availability
				
				// Define Firestore object
				val firestore = FirebaseFirestore.getInstance()
				
				/*
				Query FirebaseFirestore's users collection and compares the usernames.
				If no double username is found, call callback, else show errors.
				 */firestore.collection(Keys.FirestoreFieldKeys.KEY_COLLECTION_USERS).whereEqualTo(
					Keys.FirestoreFieldKeys.KEY_COLUMN_USERNAME,
					mUsername
				).get().addOnSuccessListener { snapshots: QuerySnapshot ->
					Timber.d("checkInput: great success comparing usernames")
					
					// If username is available, call callback
					if (snapshots.isEmpty) {
						
						// Username is available
						
						// Call callback
						mListener!!.inputIsOk()
					}
					else {
						
						// Username is unavailable
						
						// Show username is already taken error
						mUsernameFieldLayout!!.error = mContext.getString(R.string.error_username_already_is_taken)
					}
				}.addOnFailureListener { e: Exception? ->
					Timber.d("checkInput: failure comparing usernames")
					Timber.e(
						e,
						"checkInput: "
					)
				}
			}
			else {
				mEmailFieldLayout!!.error = mContext.getString(R.string.error_email_is_already_in_use)
			}
		}.addOnFailureListener { e: Exception? ->
			Timber.d("checkInput: failure checking email availability")
			Timber.e(
				e,
				"checkInput: "
			)
			
			// TODO: 18/04/2020 checkInput: test exceptions
			
			// The email is malformed, show error on mEmailFieldLayout
			if (e is FirebaseAuthInvalidCredentialsException) {
				
				// The email is malformed
				
				// Show error on field layout
				mEmailFieldLayout!!.error = mContext.getString(R.string.error_email_is_badly_formatted)
			}
		}
	}
	
	/**
	 * Iterates through all children of the parent view from the constructor.
	 * When the child is a TextInputLayout, reset its error.
	 */
	private fun resetLayoutErrors() {
		Timber.d("resetLayoutErrors: called")
		
		// mParentView is the first view in the EmailSignupFragment's layout
		// Define a ViewGroup object from the first child of mParentView which is the ConstraintLayout
		val viewGroup = (mParentView as ScrollView).getChildAt(0) as ConstraintLayout
		
		// Count how many children the ViewGroup has
		val childCount = viewGroup.childCount
		
		// Iterate through the children
		for (i in 0 until childCount) {
			
			// Define a child from the child at index in the ViewGroup
			val child = viewGroup.getChildAt(i)
			
			// If the child is a TextInputLayout, set its error to null
			if (child is TextInputLayout) {
				child.error = null
			}
			else if (child is CheckBox) {
				child.error = null
			}
		}
	}
	
	/**
	 * Sets the email fields text.
	 *
	 * @param emailText The email text.
	 */
	fun setEmailText(emailText: String?) {
		Timber.d("setEmailText: called")
		mEmailField!!.setText(emailText)
	}
	
	interface SignupCheckerListener {
		fun inputIsOk()
	}
	
	init {
		Timber.d("EmailSignupHelper: called")
		
		// Check if associated context implements the SignupCheckerListener interface
		mListener = if (context is SignupCheckerListener) {
			context
		}
		else {
			throw RuntimeException("$context must implement SignupCheckerListener")
		}
		
		// Define the parent view
		mParentView = parent
		
		// Define the activity object
		mContext = context
		
		// Define the fields
		defineFields(parent)
		
		// Define the field layouts
		defineFieldLayouts(parent)
		
		// Define the checkbox
		// Check box not in the view hierarchy anymore
		//				mLegalitiesCheckbox = parent.findViewById(R.id.legalities_check_box);
		
		// Define FirebaseAuth instance
		mAuth = FirebaseAuth.getInstance()
	}
}