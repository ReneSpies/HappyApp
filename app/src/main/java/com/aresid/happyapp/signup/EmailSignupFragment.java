package com.aresid.happyapp.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aresid.happyapp.R;

import timber.log.Timber;

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

public class EmailSignupFragment
		extends Fragment
		implements EmailSignupHelper.SignupCheckerListener {
	
	private              NavController     mNavController;
	private              EmailSignupHelper mEmailSignupHelper;
	
	public EmailSignupFragment() {
		
		Timber.d("EmailSignupFragment: called");
		// Required public empty constructor
		
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		
		Timber.d("onCreateView: called");
		
		// Inflate the layout
		return inflater.inflate(R.layout.fragment_email_signup, container, false);
		
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		
		Timber.d("onViewCreated: called");
		super.onViewCreated(view, savedInstanceState);
		
		/* Use this class to reference the views */
		
		// Define NavController
		mNavController = Navigation.findNavController(view);
		
		// Define the EmailSignupHelper
		mEmailSignupHelper = new EmailSignupHelper(this, view);
		
	}
	
	/**
	 * Is called when everything is ok with the signup forms input.
	 */
	@Override
	public void inputIsOk() {
		
		Timber.d("inputIsOk: called");
		
		// Get signup forms input as bundle and pass it to the subscribe fragment
		
		// Define the Bundle
		Bundle arguments = mEmailSignupHelper.getInputBundle();
		
		// Navigate to the subscription fragment and pass the bundle
		mNavController.navigate(R.id.action_emailSignupFragment_to_subscribeFragment, arguments);
		
	}
	
}
