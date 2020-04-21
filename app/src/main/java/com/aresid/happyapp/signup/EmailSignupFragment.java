package com.aresid.happyapp.signup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aresid.happyapp.R;

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
public class EmailSignupFragment
		extends Fragment
		implements EmailSignupChecker.SignupCheckerListener,
		           View.OnClickListener {
	
	private static final String             TAG = "EmailSignupFragment";
	private              NavController      mNavController;
	private              String             mEmailText;
	private              EmailSignupChecker mRegistrationChecker;
	
	public EmailSignupFragment() {
		
		Log.d(TAG, "EmailSignupFragment: called");
		// Required public empty constructor
		
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreate: called");
		super.onCreate(savedInstanceState);
		
		// Define a Bundle from the arguments
		Bundle arguments = getArguments();
		
		if (arguments != null) {
			
			String emailKey = getString(R.string.arguments_key_email);
			mEmailText = arguments.getString(emailKey);
			
		}
		
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateView: called");
		
		// Inflate the layout
		return inflater.inflate(R.layout.fragment_email_signup, container, false);
		
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onViewCreated: called");
		super.onViewCreated(view, savedInstanceState);
		
		/* Use this class to reference the views */
		
		// Define NavController
		mNavController = Navigation.findNavController(view);
		
		// Define the EmailSignupChecker
		mRegistrationChecker = new EmailSignupChecker(this, view);
		
		// Send the email text to the signup checker which sets it to the field
		mRegistrationChecker.setEmailText(mEmailText);
		
		// Set the OnClickListener for the subscribe button
		view.findViewById(R.id.subscribe_button).setOnClickListener(this);
		
	}
	
	/**
	 * Is called when everything is ok with the signup forms input.
	 */
	@Override
	public void inputIsOk() {
		
		Log.d(TAG, "inputIsOk: called");
		
		// Get signup forms input as bundle and pass it to the subscribe fragment
		
		// Define the Bundle
		Bundle arguments = mRegistrationChecker.getInputBundle();
		
		// Navigate to the subscription fragment and pass the bundle
		mNavController.navigate(R.id.action_emailSignupFragment_to_subscribeFragment, arguments);
		
	}
	
	@Override
	public void onClick(View v) {
		
		Log.d(TAG, "onClick: called");
		
		if (v.getId() == R.id.subscribe_button) {
			
			onSubscribeButtonClicked();
			
		}
		
	}
	
	/**
	 * Checks signup form input and continues with inputIsOk callback if appropriate.
	 */
	private void onSubscribeButtonClicked() {
		
		Log.d(TAG, "onSubscribeButtonClicked: called");
		
		// Check signup form input
		mRegistrationChecker.checkInput(); // Calls inputIsOk()
		
	}
	
}
