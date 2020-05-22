package com.aresid.happyapp.signup.form.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentPasswordBinding
import com.aresid.happyapp.signup.form.SignupFormData
import timber.log.Timber

/**
 * Created on: 23/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class PasswordFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentPasswordBinding
	
	// Corresponding ViewModel
	private lateinit var passwordViewModel: PasswordViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentPasswordBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		passwordViewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)
		
		// Tell the binding about the ViewModel
		binding.viewModel = passwordViewModel
		
		// Observe the passwordEmpty LiveData and show feedback if true
		passwordViewModel.passwordEmpty.observe(
			viewLifecycleOwner,
			Observer { isEmpty ->
				
				// If isEmpty, show feedback on the passwordFieldLayout
				if (isEmpty) {
					
					binding.passwordFieldLayout.error = getString(R.string.error_you_forgot_me)
					
				}
				
				// Else, reset the error
				else {
					
					binding.passwordFieldLayout.error = null
					
				}
				
			})
		
		// Observe the passwordWeak LiveData and show feedback if true
		passwordViewModel.passwordWeak.observe(
			viewLifecycleOwner,
			Observer { isWeak ->
				
				// If isWeak, show feedback on the passwordFieldLayout
				if (isWeak) {
					
					binding.passwordFieldLayout.error = getString(R.string.error_password_rules)
					
				}
				
				// Else, reset the error
				else {
					
					binding.passwordFieldLayout.error = null
					
				}
				
			})
		
		// Observe the passwordOk LiveData, notify the SignupFormData singleton
		// and navigate to the DateOfBirthFragment if true
		passwordViewModel.passwordOk.observe(
			viewLifecycleOwner,
			Observer { isOk ->
				
				// If isOk, notify and navigate
				if (isOk) {
					
					// Notify the singleton
					SignupFormData.getInstance().password = passwordViewModel.password.value!!
					
					// Navigate
					navigateToDateOfBirthFragment()
					
					// Reset the LiveData
					passwordViewModel.navigatedAndNotified()
					
				}
				
			})
		
		// Observe the password LiveData to reset the errors when the user types
		passwordViewModel.password.observe(
			viewLifecycleOwner,
			Observer {
				
				// Reset the errors
				binding.passwordFieldLayout.error = null
				
			})
		
		// Return the inflated layout
		return binding.root
		
	}
	
	/**
	 * Navigates to the DateOfBirthFragment using the NavController.
	 */
	private fun navigateToDateOfBirthFragment() {
		
		Timber.d("navigateToDateOfBirthFragment: called")
		
		// Navigate to the DateOfBirthFragment
		findNavController(this).navigate(PasswordFragmentDirections.toDateOfBirthFragment())
		
	}
	
}