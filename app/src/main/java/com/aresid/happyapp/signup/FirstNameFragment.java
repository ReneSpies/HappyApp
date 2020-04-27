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
import com.aresid.happyapp.keys.Keys;
import com.aresid.happyapp.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Created on: 23/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

public class FirstNameFragment
		extends Fragment
		implements View.OnClickListener {
	
	private static final String            TAG = "FirstNameFragment";
	private              NavController     mNavController;
	private              TextInputEditText mFirstNameField;
	private              TextInputLayout   mFirstNameFieldLayout;
	
	public FirstNameFragment() {
		
		Log.d(TAG, "FirstNameFragment: called");
		
		// Required empty constructor
		
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateView: called");
		
		// Inflate the layout
		return inflater.inflate(R.layout.fragment_first_name, container, false);
		
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onViewCreated: called");
		
		super.onViewCreated(view, savedInstanceState);
		
		// Define the NavController object
		mNavController = Navigation.findNavController(view);
		
		// Set onClickListener
		view.findViewById(R.id.next_button).setOnClickListener(this);
		
		// Define first name field TextInputEditText from layout
		mFirstNameField = view.findViewById(R.id.first_name_field);
		
		// Define first name field layout TextInputLayout from layout
		mFirstNameFieldLayout = view.findViewById(R.id.first_name_field_layout);
		
	}
	
	@Override
	public void onClick(View v) {
		
		Log.d(TAG, "onClick: called");
		
		if (v.getId() == R.id.next_button) {
			
			showFamilyNameFragment();
			
		}
		
	}
	
	/**
	 * Bundles the first name and navigates to the FamilyNameFragment passing the Bundle.
	 */
	private void showFamilyNameFragment() {
		
		Log.d(TAG, "showFamilyNameFragment: called");
		
		// Show error if no first name has been typed in
		if (mFirstNameField.length() == 0) {
			
			// No first name detected
			
			// Show error
			mFirstNameFieldLayout.setError(getString(R.string.error_you_forgot_me));
			
			return;
			
		}
		
		// Set the first name fields layout error to null proactively
		mFirstNameFieldLayout.setError(null);
		
		// Define a new Bundle object
		Bundle arguments = new Bundle();
		
		// Put the first name into the bundle
		arguments.putString(Keys.BundleKeys.KEY_FIRST_NAME, Utils.getString(mFirstNameField.getText()));
		
		// Navigate to the FamilyNameFragment and pass the bundle
		mNavController.navigate(R.id.to_familyNameFragment, arguments);
		
	}
	
}
