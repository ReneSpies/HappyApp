package com.aresid.happyapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.aresid.happyapp.keys.Keys;
import com.aresid.happyapp.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Created on: 18/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

public class RegistrationChecker {
	
	private static final String            TAG = "RegistrationChecker";
	private              View              mParentView;
	// Fields layouts
	private              TextInputLayout   mFirstNameFieldLayout;
	private              TextInputLayout   mFamilyNameFieldLayout;
	private              TextInputLayout   mUsernameFieldLayout;
	private              TextInputLayout   mEmailFieldLayout;
	private              TextInputLayout   mPasswordFieldLayout;
	private              TextInputLayout   mDateOfBirthFieldLayout;
	// Fields
	private              TextInputEditText mFirstNameField;
	private              TextInputEditText mFamilyNameField;
	private              TextInputEditText mUsernameField;
	private              TextInputEditText mEmailField;
	private              TextInputEditText mPasswordField;
	private              TextInputEditText mDateOfBirthField;
	// Checkbox
	private              CheckBox          mLegalitiesCheckbox;
	
	public RegistrationChecker(View parent) {
		
		Log.d(TAG, "RegistrationChecker: called");
		
		// Define the parent view
		mParentView = parent;
		
		// Define the fields
		defineFields(parent);
		
		// Define the fields layouts
		defineFieldLayouts(parent);
		
		// Define the checkbox
		mLegalitiesCheckbox = parent.findViewById(R.id.legalities_check_box);
		
	}
	
	/**
	 * Defines the fields from given parent.
	 *
	 * @param parent Parent carrying the fields.
	 */
	private void defineFields(View parent) {
		
		Log.d(TAG, "defineFields: called");
		
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
		
		Log.d(TAG, "defineFieldLayouts: called");
		
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
	public Bundle getInputBundle() {
		
		Log.d(TAG, "getInputBundle: called");
		
		// Define the Strings carrying the field texts
		String firstName = Utils.getString(mFirstNameField.getText());
		String familyName = Utils.getString(mFamilyNameField.getText());
		String username = Utils.getString(mUsernameField.getText());
		String email = Utils.getString(mEmailField.getText());
		String dateOfBirth = Utils.getString(mDateOfBirthField.getText());
		
		// Define the Bundle which will carry the information
		Bundle bundle = new Bundle();
		
		// Put the Strings into the Bundle using keys
		bundle.putString(Keys.BundleKeys.KEY_FIRST_NAME, firstName);
		bundle.putString(Keys.BundleKeys.KEY_FAMILY_NAME, familyName);
		bundle.putString(Keys.BundleKeys.KEY_USERNAME, username);
		bundle.putString(Keys.BundleKeys.KEY_EMAIL, email);
		bundle.putString(Keys.BundleKeys.KEY_DATE_OF_BIRTH, dateOfBirth);
		
		return bundle;
		
	}
	
	/**
	 * Checks whether the users input is usable and sets field layout errors if
	 * appropriate.
	 *
	 * @return Everything ok, false otherwise.
	 */
	public boolean isInputOkWithErrors() {
		
		Log.d(TAG, "isInputOkWithErrors: called");
		
		// TODO: 18/04/2020 isInputOkWithErrors: check credentials and throw errors if needed
		
		return false;
		
	}
	
	/**
	 * Sets the email fields text.
	 *
	 * @param emailText The email text.
	 */
	public void setEmailText(String emailText) {
		
		Log.d(TAG, "setEmailText: called");
		
		mEmailField.setText(emailText);
		
	}
	
}
