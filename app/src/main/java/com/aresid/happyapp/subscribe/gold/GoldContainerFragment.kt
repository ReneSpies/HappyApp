package com.aresid.happyapp.subscribe.gold

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.databinding.FragmentGoldContainerBinding
import timber.log.Timber

/**
 *    Created on: 16.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class GoldContainerFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentGoldContainerBinding
	
	// Corresponding ViewModel
	private lateinit var goldContainerViewModel: GoldContainerViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentGoldContainerBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		goldContainerViewModel = ViewModelProvider(this).get(GoldContainerViewModel::class.java)
		
		// Return the inflated layout
		return binding.root
		
	}
	
}