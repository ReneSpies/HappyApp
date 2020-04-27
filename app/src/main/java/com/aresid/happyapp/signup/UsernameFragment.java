package com.aresid.happyapp.signup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aresid.happyapp.R;

/**
 * Created on: 23/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

public class UsernameFragment
		extends Fragment {
	
	private static final String TAG = "UsernameFragment";
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateView: called");
		
		// Inflate the layout
		return inflater.inflate(R.layout.fragment_username, container, false);
		
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onViewCreated: called");
		
		super.onViewCreated(view, savedInstanceState);
		
		String firstName = UsernameFragmentArgs.fromBundle(getArguments()).getFirstName();
		Log.d(TAG, "onViewCreated: first name = " + firstName);
		
	}
	
}
