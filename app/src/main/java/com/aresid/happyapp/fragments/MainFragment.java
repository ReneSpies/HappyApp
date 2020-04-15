package com.aresid.happyapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.aresid.happyapp.R;

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
public class MainFragment
		extends Fragment {
	
	private static final String TAG = "MainFragment";
	
	public MainFragment() {
		
		Log.d(TAG, "MainFragment: called");
		// Required public empty constructor
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateView: called");
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_main, container, false);
	}
	
}
