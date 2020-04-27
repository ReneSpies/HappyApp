package com.aresid.happyapp.fragments;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateView: called");
		
		// Inflate the layout
		return inflater.inflate(R.layout.fragment_loading, container, false);
		
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onViewCreated: called");
		super.onViewCreated(view, savedInstanceState);
		
		// Define a TextView from the layout
		TextView loadingTextView = view.findViewById(R.id.loading_text_view);
		
		// Start the loading animation on the TextViews compound drawable
		startLoadingAnimation(loadingTextView);
		
		// Define NavController
		mNavController = Navigation.findNavController(view);
		
	}
	
	/**
	 * Takes the TextViews compound drawable top and starts its loading animation.
	 *
	 * @param textView The TextView to take the compound drawable top from.
	 */
	private void startLoadingAnimation(TextView textView) {
		
		Log.d(TAG, "startLoadingAnimation: called");
		
		// Define a AnimatedVectorDrawable object from the TextViews compound drawable top
		AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) textView.getCompoundDrawablesRelative()[1];
		
		// Start the loading animation
		animatedVectorDrawable.start();
		
	}
	
}
