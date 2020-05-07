package com.aresid.happyapp.signup;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.aresid.happyapp.R;
import com.aresid.happyapp.keys.Keys;
import com.aresid.happyapp.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import timber.log.Timber;

/**
 * Created on: 18/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

public class EmailSignupHelper {
	
	private final FirebaseAuth          mAuth;
	private final Fragment              mContext;
	private final SignupCheckerListener mListener;
	private       View                  mParentView;
	// Fields layouts
	private       TextInputLayout       mFirstNameFieldLayout;
	private       TextInputLayout       mFamilyNameFieldLayout;
	private       TextInputLayout       mUsernameFieldLayout;
	private       TextInputLayout       mEmailFieldLayout;
	private       TextInputLayout       mPasswordFieldLayout;
	private       TextInputLayout       mDateOfBirthFieldLayout;
	// Fields
	private       TextInputEditText     mFirstNameField;
	private       TextInputEditText     mFamilyNameField;
	private       TextInputEditText     mUsernameField;
	private       TextInputEditText     mEmailField;
	private       TextInputEditText     mPasswordField;
	private       TextInputEditText     mDateOfBirthField;
	// Field text Strings
	private       String                mFirstName;
	private       String                mFamilyName;
	private       String                mUsername;
	private       String                mEmail;
	private       String                mPassword;
	private       String                mDateOfBirth;
	private       CheckBox              mLegalitiesCheckbox;
	
	EmailSignupHelper(Fragment context, View parent) {
		
		Timber.d("EmailSignupHelper: called");
		
		// Check if associated context implements the SignupCheckerListener interface
		if (context instanceof SignupCheckerListener) {
			
			mListener = (SignupCheckerListener) context;
			
		}
		
		// Else throw RuntimeException
		else {
			
			throw new RuntimeException(context.toString() + " must implement SignupCheckerListener");
			
		}
		
		// Define the parent view
		mParentView = parent;
		
		// Define the activity object
		mContext = context;
		
		// Define the fields
		defineFields(parent);
		
		// Define the field layouts
		defineFieldLayouts(parent);
		
		// Define the checkbox
		// Check box not in the view hierarchy anymore
		//				mLegalitiesCheckbox = parent.findViewById(R.id.legalities_check_box);
		
		// Define FirebaseAuth instance
		mAuth = FirebaseAuth.getInstance();
		
	}
	
	/**
	 * Defines the fields from given parent.
	 *
	 * @param parent Parent carrying the fields.
	 */
	private void defineFields(View parent) {
		
		Timber.d("defineFields: called");
		
		mFirstNameField = parent.findViewById(R.id.first_name_field);
		mFamilyNameField = parent.findViewById(R.id.family_name_field);
		mUsernameField = parent.findViewById(R.id.username_field);
		mEmailField = parent.findViewById(R.id.email_field);
		mPasswordField = parent.findViewById(R.id.password_field);
		mDateOfBirthField = parent.findViewById(R.id.date_of_birth_field);
		
	}
	
	/**
	 * Defines the field layouts from given parent.
	 *
	 * @param parent Parent carrying the field layouts.
	 */
	private void defineFieldLayouts(View parent) {
		
		Timber.d("defineFieldLayouts: called");
		
		mFirstNameFieldLayout = parent.findViewById(R.id.first_name_field_layout);
		mFamilyNameFieldLayout = parent.findViewById(R.id.family_name_field_layout);
		mUsernameFieldLayout = parent.findViewById(R.id.username_field_layout);
		mEmailFieldLayout = parent.findViewById(R.id.email_field_layout);
		mPasswordFieldLayout = parent.findViewById(R.id.password_field_layout);
		mDateOfBirthFieldLayout = parent.findViewById(R.id.date_of_birth_field_layout);
		
	}
	
	/**
	 * Bundles all the field texts into a bundle.
	 *
	 * @return The bundled field texts.
	 */
	Bundle getInputBundle() {
		
		Timber.d("getInputBundle: called");
		
		// Define the field text strings
		defineFieldStrings();
		
		// Define the Bundle which will carry the information
		Bundle bundle = new Bundle();
		
		// Put the Strings into the Bundle using keys
		bundle.putString(Keys.BundleKeys.KEY_FIRST_NAME, mFirstName);
		bundle.putString(Keys.BundleKeys.KEY_FAMILY_NAME, mFamilyName);
		bundle.putString(Keys.BundleKeys.KEY_USERNAME, mUsername);
		bundle.putString(Keys.BundleKeys.KEY_EMAIL, mEmail);
		bundle.putString(Keys.BundleKeys.KEY_DATE_OF_BIRTH, mDateOfBirth);
		
		return bundle;
		
	}
	
	/**
	 * Defines the Strings carrying the fields texts.
	 */
	private void defineFieldStrings() {
		
		Timber.d("defineFieldStrings: called");
		
		mFirstName = Utils.getString(mFirstNameField.getText());
		mFamilyName = Utils.getString(mFamilyNameField.getText());
		mUsername = Utils.getString(mUsernameField.getText());
		mEmail = Utils.getString(mEmailField.getText());
		mPassword = Utils.getString(mPasswordField.getText());
		mDateOfBirth = Utils.getString(mDateOfBirthField.getText());
		
	}
	
	/**
	 * Checks if the signup forms input is ok and calls SignupCheckerListener#inputIsOk() if so.
	 * Shows appropriate error messages otherwise.
	 */
	public void checkInput() {
		
		Timber.d("checkInput: called");
		
		// Reset layout errors
		resetLayoutErrors();
		
		// Define the field text strings
		defineFieldStrings();
		
		// Define empty field error message String
		String errorEmptyField = mContext.getString(R.string.error_you_forgot_me);
		
		// If first name empty, set error on mFirstNameFieldLayout
		if (mFirstName.length() == 0) {
			
			mFirstNameFieldLayout.setError(errorEmptyField);
			
			return;
			
		}
		
		// If family name empty, set error on mFamilyNameFieldLayout
		if (mFamilyName.length() == 0) {
			
			mFamilyNameFieldLayout.setError(errorEmptyField);
			
			return;
			
		}
		
		// If username empty, set error on mUsernameFieldLayout
		if (mUsername.length() == 0) {
			
			mUsernameFieldLayout.setError(errorEmptyField);
			
			return;
			
		}
		
		// If email empty, set error on mEmailFieldLayout
		if (mEmail.length() == 0) {
			
			mEmailFieldLayout.setError(errorEmptyField);
			
			return;
			
		}
		
		// If password length < 6, set error on mPasswordFieldLayout
		if (mPassword.length() < 6) {
			
			mPasswordFieldLayout.setError(mContext.getString(R.string.error_password_must_be_6_long));
			
			return;
			
		}
		
		// If date of birth empty, set error on mDateOfBirthFieldLayout
		if (mDateOfBirth.length() == 0) {
			
			mDateOfBirthFieldLayout.setError(errorEmptyField);
			
			return;
			
		}
		
		// If CheckBox not checked, set error on mLegalitiesCheckbox
		if (!mLegalitiesCheckbox.isChecked()) {
			
			mLegalitiesCheckbox.setError(mContext.getString(R.string.error_please_confirm));
			
			return;
			
		}
		
		/*
		Query FirebaseAuth if email is already in use.
		If not, query FirebaseFirestore if username is already taken.
		If not, call SignupCheckerListener#inputIsOk() callback.
		 */
		mAuth.fetchSignInMethodsForEmail(mEmail).addOnSuccessListener(result -> {
			
			Timber.d("checkInput: great success checking email availability");
			
			// If signInMethods() size == 0, the email is available
			if (result.getSignInMethods() != null && result.getSignInMethods().size() == 0) {
				
				// Carry on and check the username availability
				
				// Define Firestore object
				FirebaseFirestore firestore = FirebaseFirestore.getInstance();
				
				/*
				Query FirebaseFirestore's users collection and compares the usernames.
				If no double username is found, call callback, else show errors.
				 */
				firestore.collection(Keys.FirestoreFieldKeys.KEY_COLLECTION_USERS).whereEqualTo(Keys.FirestoreFieldKeys.KEY_COLUMN_USERNAME, mUsername).get().addOnSuccessListener(snapshots -> {
					
					Timber.d("checkInput: great success comparing usernames");
					
					// If username is available, call callback
					if (snapshots.isEmpty()) {
						
						// Username is available
						
						// Call callback
						mListener.inputIsOk();
						
					}
					
					// Else show error on mUsernameFieldLayout
					else {
						
						// Username is unavailable
						
						// Show username is already taken error
						mUsernameFieldLayout.setError(mContext.getString(R.string.error_username_already_is_taken));
						
					}
					
				}).addOnFailureListener(e -> {
					
					Timber.d("checkInput: failure comparing usernames");
					Timber.e(e, "checkInput: ");
					
					// TODO: 18/04/2020 checkInput: test exceptions. Docs do not tell which exceptions can be thrown
					
				});
				
			}
			
			// Email is unavailable, show error on mEmailFieldLayout
			else {
				
				mEmailFieldLayout.setError(mContext.getString(R.string.error_email_is_already_in_use));
				
			}
			
		}).addOnFailureListener(e -> {
			
			Timber.d("checkInput: failure checking email availability");
			Timber.e(e, "checkInput: ");
			
			// TODO: 18/04/2020 checkInput: test exceptions
			
			// The email is malformed, show error on mEmailFieldLayout
			if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
				
				// The email is malformed
				
				// Show error on field layout
				mEmailFieldLayout.setError(mContext.getString(R.string.error_email_is_badly_formatted));
				
			}
			
		});
		
	}
	
	/**
	 * Iterates through all children of the parent view from the constructor.
	 * When the child is a TextInputLayout, reset its error.
	 */
	private void resetLayoutErrors() {
		
		Timber.d("resetLayoutErrors: called");
		
		// mParentView is the first view in the EmailSignupFragment's layout
		// Define a ViewGroup object from the first child of mParentView which is the ConstraintLayout
		ConstraintLayout viewGroup = (ConstraintLayout) ((ScrollView) mParentView).getChildAt(0);
		
		// Count how many children the ViewGroup has
		int childCount = viewGroup.getChildCount();
		
		// Iterate through the children
		for (int i = 0; i < childCount; i++) {
			
			// Define a child from the child at index in the ViewGroup
			View child = viewGroup.getChildAt(i);
			
			// If the child is a TextInputLayout, set its error to null
			if (child instanceof TextInputLayout) {
				
				((TextInputLayout) child).setError(null);
				
			}
			
			// Else if the child is the CheckBox, set its error to null too
			else if (child instanceof CheckBox) {
				
				((CheckBox) child).setError(null);
				
			}
			
		}
		
	}
	
	/**
	 * Sets the email fields text.
	 *
	 * @param emailText The email text.
	 */
	public void setEmailText(String emailText) {
		
		Timber.d("setEmailText: called");
		
		mEmailField.setText(emailText);
		
	}
	
	public interface SignupCheckerListener {
		
		void inputIsOk();
		
	}
	
}