package com.aresid.happyapp.utils

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aresid.happyapp.databinding.FragmentLoadingBinding
import timber.log.Timber

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

class LoadingFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentLoadingBinding
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentLoadingBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Return the inflated layout
		return binding.root
		
	}
	
	override fun onResume() {
		
		Timber.d("onResume: called")
		
		super.onResume()
		
		// Start the loading animation again
		startLoadingAnimation()
		
	}
	
	override fun onStop() {
		
		Timber.d("onStop: called")
		
		super.onStop()
		
		// Stop the animation when the fragment goes into background
		stopLoadingAnimation()
		
	}
	
	/**
	 * Starts the loading animation on the ImageView's drawable.
	 */
	private fun startLoadingAnimation() {
		
		Timber.d("startLoading: called")
		
		(binding.loadingImage.drawable as AnimatedVectorDrawable).start()
		
	}
	
	/**
	 * Stops the loading animation on the ImageView's drawable.
	 */
	private fun stopLoadingAnimation() {
		
		Timber.d("stopLoading: called")
		
		(binding.loadingImage.drawable as AnimatedVectorDrawable).stop()
		
	}
	
}