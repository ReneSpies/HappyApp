package com.aresid.happyapp.subscribe.platinum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.databinding.FragmentPlatinumContainerBinding
import timber.log.Timber

/**
 *    Created on: 16.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class PlatinumContainerFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentPlatinumContainerBinding
	
	// Corresponding ViewModel
	private lateinit var platinumContainerViewModel: PlatinumContainerViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentPlatinumContainerBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		platinumContainerViewModel = ViewModelProvider(this).get(PlatinumContainerViewModel::class.java)
		
		// Return the inflated layout
		return binding.root
		
	}
	
}