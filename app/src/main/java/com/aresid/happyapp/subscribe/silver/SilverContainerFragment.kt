package com.aresid.happyapp.subscribe.silver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.databinding.FragmentSilverContainerBinding
import timber.log.Timber

/**
 *    Created on: 16.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SilverContainerFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentSilverContainerBinding
	
	// Corresponding ViewModel
	private lateinit var silverContainerViewModel: SilverContainerViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentSilverContainerBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		silverContainerViewModel = ViewModelProvider(this).get(SilverContainerViewModel::class.java)
		
		// Return the inflated layout
		return binding.root
		
	}
	
}
