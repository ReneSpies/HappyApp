package com.aresid.happyapp.signup.form.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentEmailBinding
import com.aresid.happyapp.signup.form.SignupFormData
import timber.log.Timber

/**
 * Created on: 23/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class EmailFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentEmailBinding
	
	// Corresponding ViewModel
	private lateinit var emailViewModel: EmailViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentEmailBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		emailViewModel = ViewModelProvider(this).get(EmailViewModel::class.java)
		
		// Tell the binding about the ViewModel
		binding.viewModel = emailViewModel
		
		// Observe the emailOk LiveData, notify the SignupFormData singleton
		// and navigate to the password fragment
		emailViewModel.emailOk.observe(
			viewLifecycleOwner,
			Observer { isOk ->
				
				// If isOk, notify the SignupFormData and navigate
				if (isOk) {
					
					// Notify the SignupFormData
					SignupFormData.getInstance().email = emailViewModel.email.value!!
					
					// Navigate to PasswordFragment
					navigateToPasswordFragment()
					
					// Reset the emailOk LiveData
					emailViewModel.navigatedAndNotified()
					
				}
				
			})
		
		// Observe the emailEmpty LiveData and show feedback if true
		emailViewModel.emailEmpty.observe(
			viewLifecycleOwner,
			Observer { isEmpty ->
				
				// If isEmpty, show feedback
				if (isEmpty) {
					
					// Show an error on the emailFieldLayout
					binding.emailFieldLayout.error = getString(R.string.error_you_forgot_me)
					
				}
				
				// Else, reset the error
				else {
					
					// Reset the error on the emailFieldLayout
					binding.emailFieldLayout.error = null
					
				}
				
			})
		
		// Return the inflated layout
		return binding.root
	}
	
	/**
	 * Navigates to the PasswordFragment using the NavController.
	 */
	private fun navigateToPasswordFragment() {
		
		Timber.d("navigateToPasswordFragment: called")
		
		findNavController(this).navigate(EmailFragmentDirections.toPasswordFragment())
		
	}
	
}