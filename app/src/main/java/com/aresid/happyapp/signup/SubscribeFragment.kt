package com.aresid.happyapp.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.databinding.FragmentSubscribeBinding
import timber.log.Timber

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class SubscribeFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentSubscribeBinding
	
	// ViewModel
	private lateinit var subscribeFragmentViewModel: SubscribeViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding
		binding = FragmentSubscribeBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		subscribeFragmentViewModel = ViewModelProvider(this).get(SubscribeViewModel::class.java)
		
		// Return the inflated layout
		return binding.root
		
	}
	
}