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
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
public class SubscribeFragment
		extends Fragment {
	
	private static final String TAG = "SubscribeFragment";
	
	public SubscribeFragment() {
		
		Log.d(TAG, "SubscribeFragment: called");
		// Required public empty constructor
		
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreate: called");
		
		super.onCreate(savedInstanceState);
		
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateView: called");
		
		// Inflate layout
		return inflater.inflate(R.layout.fragment_subscribe, container, false);
		
	}
	
}
