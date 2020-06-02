package com.aresid.happyapp.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aresid.happyapp.databinding.FragmentContactBinding
import timber.log.Timber

/**
 *    Created on: 02.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class ContactFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentContactBinding
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentContactBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Return the inflated layout
		return binding.root
		
	}
	
}