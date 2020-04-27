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

public class FamilyNameFragment
		extends Fragment
		implements View.OnClickListener {
	
	private static final String            TAG = "FamilyNameFragment";
	private              NavController     mNavController;
	private              TextInputEditText mFamilyNameField;
	private              TextInputLayout   mFamilyNameFieldLayout;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateView: called");
		
		// Inflate the layout
		return inflater.inflate(R.layout.fragment_family_name, container, false);
		
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onViewCreated: called");
		
		super.onViewCreated(view, savedInstanceState);
		
		// Define the NavController object
		mNavController = Navigation.findNavController(view);
		
		// Set OnClickListener
		view.findViewById(R.id.next_button).setOnClickListener(this);
		
		// Define the FamilyNameField from the layout
		mFamilyNameField = view.findViewById(R.id.family_name_field);
		
		// Define the FamilyNameFieldLayout from the layout
		mFamilyNameFieldLayout = view.findViewById(R.id.family_name_field_layout);
		
	}
	
	@Override
	public void onClick(View v) {
		
		Log.d(TAG, "onClick: called");
		
		if (v.getId() == R.id.next_button) {
			
			showUsernameFragment();
			
		}
		
	}
	
	/**
	 * Bundles the family name into the bundle from getArguments() and navigates to the UsernameFragment.
	 * Returns if no family name was detected or getArguments() is null and shows appropriate errors.
	 */
	private void showUsernameFragment() {
		
		Log.d(TAG, "showUsernameFragment: called");
		
		// Show error if no family name was detected and return
		if (mFamilyNameField.length() == 0) {
			
			// Show error
			mFamilyNameFieldLayout.setError(getString(R.string.error_you_forgot_me));
			
			return;
			
		}
		
		// Reset the FamilyNameFieldLayouts error proactively
		mFamilyNameFieldLayout.setError(null);
		
		// Define a new Bundle object from getArguments()
		Bundle arguments = getArguments();
		
		// Show error snackbar if arguments is null and return
		if (arguments == null) {
			
			// Show the error snackbar
			Utils.showErrorSnackbar(mFamilyNameFieldLayout, getString(R.string.error_try_again), requireContext());
			
			return;
			
		}
		
		// Put the family name into the bundle
		arguments.putString(Keys.BundleKeys.KEY_FAMILY_NAME, Utils.getString(mFamilyNameField.getText()));
		
		// Navigate to the UsernameFragment and pass the bundle
		mNavController.navigate(R.id.to_usernameFragment);
		
	}
	
}
