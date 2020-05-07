package com.aresid.happyapp.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aresid.happyapp.R;
import com.aresid.happyapp.keys.Keys;
import com.aresid.happyapp.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

public class LoginFragment
		extends Fragment
		implements View.OnClickListener {
	
	// Declare NavController
	private              NavController     mNavController;
	// Declare fields and layouts for login flow
	private              TextInputLayout   mEmailFieldLayout;
	private              TextInputEditText mEmailField;
	private              TextInputLayout   mPasswordFieldLayout;
	private              TextInputEditText mPasswordField;
	private              FirebaseAuth      mAuth;
	
	// TODO: 23/04/2020: Implement reset password policy.
	
	/**
	 * Required public empty constructor
	 */
	public LoginFragment() {
		
		Timber.d("LoginFragment: called");
		
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		
		Timber.d("onCreate: called");
		
		super.onCreate(savedInstanceState);
		
		// Define FirebaseAuth object
		mAuth = FirebaseAuth.getInstance();
		
		// UpdateUI to send user through if exists
		updateUI(mAuth.getCurrentUser());
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Timber.d("onCreateView: called");
		
		// Inflate the layout
		return inflater.inflate(R.layout.fragment_login, container, false);
		
	}
	
	/**
	 * Use this method to reference views
	 *
	 * @param view               Parent
	 * @param savedInstanceState savedInstanceState
	 */
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		
		Timber.d("onViewCreated: called");
		
		super.onViewCreated(view, savedInstanceState);
		
		// Define the NavController
		mNavController = Navigation.findNavController(view);
		
		// Set onClickListeners
		view.findViewById(R.id.login_button).setOnClickListener(this);
		view.findViewById(R.id.email_signup_button).setOnClickListener(this);
		view.findViewById(R.id.google_signup_button).setOnClickListener(this);
		
		// Define fields and layouts for login flow
		mEmailFieldLayout = view.findViewById(R.id.email_field_layout);
		mEmailField = view.findViewById(R.id.email_field);
		mPasswordFieldLayout = view.findViewById(R.id.password_field_layout);
		mPasswordField = view.findViewById(R.id.password_field);
		
	}
	
	/**
	 * Sends user to the MainFragment if user exists.
	 *
	 * @param user FirebaseUser.
	 */
	private void updateUI(FirebaseUser user) {
		
		Timber.d("updateUI: called");
		
		if (user != null) {
			
			// Navigate to MainFragment
			mNavController.navigate(R.id.action_loginFragment_to_mainFragment);
			
		}
		
	}
	
	@Override
	public void onClick(View v) {
		
		Timber.d("onClick: called");
		
		switch (v.getId()) {
			
			case R.id.login_button:
				
				onLoginButtonClicked((Button) v);
				
				break;
			
			case R.id.email_signup_button:
				
				showEmailSignupFragment();
				
				break;
			
			case R.id.google_signup_button:
				
				onGoogleSignupButtonClicked();
				
				break;
			
		}
		
	}
	
	private void onLoginButtonClicked(Button button) {
		
		Timber.d("onLoginButtonClicked: called");
		
		// Define Strings carrying the fields text
		String email = Utils.getString(mEmailField.getText());
		String password = Utils.getString(mPasswordField.getText());
		
		// Reset old layout errors
		resetLoginLayoutErrors();
		
		// Define error string
		String youForgotMeError = getString(R.string.error_you_forgot_me);
		
		// If email length is 0, set appropriate error
		if (email.length() == 0) {
			
			mEmailFieldLayout.setError(youForgotMeError);
			
			return;
			
		}
		
		// If password length is 0, set appropriate error
		if (password.length() == 0) {
			
			mPasswordFieldLayout.setError(youForgotMeError);
			
			return;
			
		}
		
		// Start loading animation on the button and disable it
		Utils.setAndStartLoadingButtonAnimationWithDisable(button, true);
		
		// Sign in with email and password
		mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(result -> {
			
			Timber.d("onLoginButtonClicked: great success logging user in");
			
			// Send the user to MainFragment if exist
			updateUI(result.getUser());
			
			// Remove loading animation and enable button again
			Utils.removeLoadingButtonAnimationWithEnable(button, true);
			
		}).addOnFailureListener(e -> {
			
			Timber.d("onLoginButtonClicked: failure logging user in");
			Timber.e(e, "onLoginButtonClicked: ");
			
			// Set layout errors null so no multiple errors are shown
			resetLoginLayoutErrors();
			
			// Remove loading animation and enable button again
			Utils.removeLoadingButtonAnimationWithEnable(button, true);
			
			if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
				
				// Wrong credentials
				mEmailFieldLayout.setError(getString(R.string.error_email_or_password_incorrect));
				
			}
			
			// Account for email exists but has been deleted or disabled
			else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {
				
				// Set appropriate error
				mEmailFieldLayout.setError(getString(R.string.error_account_disabled_or_deleted));
				
			}
			
			// Network connection failed
			else if (e instanceof com.google.firebase.FirebaseNetworkException) {
				
				// Show appropriate error Snackbar
				Utils.showErrorSnackbar(mEmailFieldLayout, getString(R.string.error_network_connection_failed), requireContext());
				
			}
		});
	}
	
	/**
	 * Navigates to the EmailSignupFragment.
	 */
	private void showEmailSignupFragment() {
		
		Timber.d("showEmailSignupFragment: called");
		
		// Navigate to the EmailSignupFragment
		mNavController.navigate(R.id.action_loginFragment_to_emailSignupFragment);
		
	}
	
	/**
	 * Navigates to the SubscribeFragment and puts an ID in a Bundle to identify that the user want Google signup.
	 */
	private void onGoogleSignupButtonClicked() {
		
		Timber.d("onGoogleSignupButtonClicked: called");
		
		// Define a new Bundle object
		Bundle arguments = new Bundle();
		
		// Put a String into the Bundle to identify that the user wants a Google signup
		arguments.putString(Keys.BundleKeys.KEY_GOOGLE_SIGNUP_ID, Keys.BundleKeys.GOOGLE_SIGNUP_ID);
		
		// Navigate to the SubscribeFragment and pass the bundle
		mNavController.navigate(R.id.action_loginFragment_to_subscribeFragment, arguments);
		
	}
	
	/**
	 * Sets the email and password field layouts error null
	 */
	private void resetLoginLayoutErrors() {
		
		Timber.d("resetLayoutErrors: called");
		
		// Set layout error null
		mEmailFieldLayout.setError(null);
		mPasswordFieldLayout.setError(null);
		
	}
	
}
