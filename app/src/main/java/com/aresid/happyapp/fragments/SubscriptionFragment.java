package com.aresid.happyapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
public class SubscriptionFragment
		extends Fragment {
	
	private static final String TAG = "SubscriptionFragment";
	
	public SubscriptionFragment() {
		
		Log.d(TAG, "SubscriptionFragment: called");
		// Required public empty constructor
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateView: called");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
}
