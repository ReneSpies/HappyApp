package com.aresid.happyapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.databinding.FragmentForgotLoginBinding
import timber.log.Timber

/**
 *    Created on: 12.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class ForgotLoginFragment: Fragment() {
	
	private lateinit var forgotLoginViewModel: ForgotLoginViewModel
	
	private lateinit var binding: FragmentForgotLoginBinding
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the ViewModel
		forgotLoginViewModel = ViewModelProvider(this).get(ForgotLoginViewModel::class.java)
		
		// Inflate the layout and define the binding object
		binding = FragmentForgotLoginBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Return the inflated layout for creation
		return binding.root
		
	}
	
}