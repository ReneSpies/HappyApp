package com.aresid.happyapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aresid.happyapp.R;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
public class LoginFragment
		extends Fragment
		implements View.OnClickListener {
	
	private static final String            TAG = "LoginFragment";
	// Declare NavController
	private              NavController     mNavController;
	// Declare fields and layouts for login flow
	private              TextInputLayout   mEmailFieldLayout;
	private              TextInputEditText mEmailField;
	private              TextInputLayout   mPasswordFieldLayout;
	private              TextInputEditText mPasswordField;
	private              FirebaseAuth      mAuth;
	private              LinearLayout      mLoadingViewLayout;
	
	/**
	 * Required public empty constructor
	 */
	public LoginFragment() {
		
		Log.d(TAG, "LoginFragment: called");
		
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreate: called");
		
		super.onCreate(savedInstanceState);
		
		// Define FirebaseAuth object
		mAuth = FirebaseAuth.getInstance();
		
		// UpdateUI to send user through if exists
		updateUI(mAuth.getCurrentUser());
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateView: called");
		
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
		
		Log.d(TAG, "onViewCreated: called");
		
		super.onViewCreated(view, savedInstanceState);
		
		// Define the NavController
		mNavController = Navigation.findNavController(view);
		
		// Set onClickListeners
		view.findViewById(R.id.login_button).setOnClickListener(this);
		view.findViewById(R.id.register_button).setOnClickListener(this);
		
		// Define fields and layouts for login flow
		mEmailFieldLayout = view.findViewById(R.id.email_field_layout);
		mEmailField = view.findViewById(R.id.email_field);
		mPasswordFieldLayout = view.findViewById(R.id.password_field_layout);
		mPasswordField = view.findViewById(R.id.password_field);
		
		// Define the loading view layout
		mLoadingViewLayout = view.findViewById(R.id.loading_view_layout);
		
		// Load gif into loading view container
		Glide.with(view).load(getResources().getDrawable(R.drawable.loading_animation)).into((ImageView) view.findViewById(R.id.loading_view_container));
	}
	
	/**
	 * Sends user to the MainFragment if exists
	 *
	 * @param user FirebaseUser
	 */
	private void updateUI(FirebaseUser user) {
		
		Log.d(TAG, "updateUI: called");
		
		if (user != null) {
			
			// Navigate to MainFragment
			mNavController.navigate(R.id.action_loginFragment_to_mainFragment);
			
		}
		
	}
	
	@Override
	public void onClick(View v) {
		
		Log.d(TAG, "onClick: called");
		switch (v.getId()) {
			case R.id.login_button:
				onLoginButtonClicked((Button) v);
				break;
			case R.id.register_button:
				onRegisterButtonClicked(v);
				break;
		}
	}
	
	private void onLoginButtonClicked(Button view) {
		
		Log.d(TAG, "onLoginButtonClicked: called");
		
		// Define Strings carrying the fields text
		String email = mEmailField.getText().toString();
		String password = mPasswordField.getText().toString();
		
		resetLoginLayoutErrors();
		
		// Define error string
		String youForgotMeError = getString(R.string.error_you_forgot_me);
		
		if (email.length() == 0) {
			
			mEmailFieldLayout.setError(youForgotMeError);
			
			return;
		}
		if (password.length() == 0) {
			
			mPasswordFieldLayout.setError(youForgotMeError);
			
			return;
		}
		
		// Set loading view layout visible to show loading animation
		mLoadingViewLayout.setVisibility(View.VISIBLE);
		
		// Disable login button
		view.setEnabled(false);
		
		// Sign in with email and password
		mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(result -> {
			
			Log.d(TAG, "onLoginButtonClicked: great success logging user in");
			
			updateUI(result.getUser());
			
		}).addOnFailureListener(e -> {
			
			Log.d(TAG, "onLoginButtonClicked: failure logging user in");
			Log.e(TAG, "onLoginButtonClicked: ", e);
			
			// Set layout errors null so no multiple errors are shown
			resetLoginLayoutErrors();
			
			mLoadingViewLayout.setVisibility(View.INVISIBLE);
			
			// Enable login button again
			view.setEnabled(true);
			
			if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
				
				// Wrong credentials
				mEmailFieldLayout.setError(getString(R.string.error_email_or_password_incorrect));
				
			} else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {
				
				// Account for email exists but has been deleted or disabled
				mEmailFieldLayout.setError(getString(R.string.error_account_disabled_or_deleted));
				
			} else if (e instanceof com.google.firebase.FirebaseNetworkException) {
				
				// Network connection failed
				showErrorSnackbar(mEmailFieldLayout,
						getString(R.string.error_network_connection_failed));
				
			}
		});
	}
	
	private void onRegisterButtonClicked(View view) {
		
		Log.d(TAG, "onRegisterButtonClicked: called");
		
		// Define a new Bundle
		Bundle bundle = new Bundle();
		
		// Access the email field and save its text into a new String
		String email = mEmailField.getText().toString();
		
		// Define String from getString() method
		String emailKey = getString(R.string.arguments_key_email);
		
		// Put the email into the bundle using the key
		bundle.putString(emailKey, email);
		
		// Finally navigate to the new fragment and pass the bundle
		mNavController.navigate(R.id.action_loginFragment_to_registrationFragment, bundle);
		
	}
	
	/**
	 * Sets the email and password field layouts error null
	 */
	private void resetLoginLayoutErrors() {
		
		Log.d(TAG, "resetLayoutErrors: called");
		
		// Set layout error null
		mEmailFieldLayout.setError(null);
		mPasswordFieldLayout.setError(null);
		
	}
	
	private void showErrorSnackbar(View snackbarView, String message) {
		
		Log.d(TAG, "showErrorSnackbar: called");
		Snackbar.make(snackbarView, message, Snackbar.LENGTH_LONG).setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.design_default_color_error)).show();
		
	}
	
}
