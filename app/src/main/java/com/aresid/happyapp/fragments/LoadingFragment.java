package com.aresid.happyapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aresid.happyapp.R;
import com.bumptech.glide.Glide;

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
public class LoadingFragment
		extends Fragment {
	
	private static final String        TAG = "LoadingFragment";
	private              NavController mNavController;
	
	public LoadingFragment() {
		
		Log.d(TAG, "LoadingFragment: called");
		// Required public empty constructor
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateView: called");
		// Inflate the layout
		return inflater.inflate(R.layout.fragment_loading, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onViewCreated: called");
		super.onViewCreated(view, savedInstanceState);
		
		// Define NavController
		mNavController = Navigation.findNavController(view);
		
		// Load gif into loading animation
		Glide.with(view).load(getResources().getDrawable(R.drawable.loading_animation)).into((ImageView) view.findViewById(R.id.loading_view_container));
	}
	
}
