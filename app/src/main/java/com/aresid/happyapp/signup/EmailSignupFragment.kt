package com.aresid.happyapp.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.aresid.happyapp.databinding.FragmentEmailSignupBinding
import timber.log.Timber

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class EmailSignupFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentEmailSignupBinding
	
	// Corresponding ViewModel
	private val emailSignupViewModel: EmailSignupViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentEmailSignupBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Observe the navigateToSubscribeFragment LiveData and navigate, if true
		emailSignupViewModel.navigateToSubscribeFragment.observe(
			viewLifecycleOwner,
			Observer { navigate ->
				
				Timber.d("navigate = $navigate")
				
				// If navigate, navigate and reset the LiveData
				if (navigate) {
					
					// Navigate
					navigateToSubscribeFragment()
					
					// Reset the LiveData
					emailSignupViewModel.navigated()
					
				}
				
			})
		
		// Return the inflated layout
		return binding.root
		
	}
	
	private fun navigateToSubscribeFragment() {
		
		Timber.d("navigateToSubscribeFragment: called")
		
		// Navigate to the SubscribeFragment
		findNavController(this).navigate(EmailSignupFragmentDirections.toSubscribeFragment())
		
	}
}