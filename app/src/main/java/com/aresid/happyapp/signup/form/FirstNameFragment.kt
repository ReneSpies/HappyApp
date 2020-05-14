package com.aresid.happyapp.signup.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentFirstNameBinding
import com.aresid.happyapp.signup.EmailSignupViewModel
import timber.log.Timber

/**
 * Created on: 23/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class FirstNameFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentFirstNameBinding
	
	// Corresponding ViewModel
	private lateinit var firstNameViewModel: FirstNameViewModel
	
	// EmailSignupViewModel needed to pass the firstName
	private lateinit var emailSignupViewModel: EmailSignupViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding
		binding = FragmentFirstNameBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		firstNameViewModel = ViewModelProvider(this).get(FirstNameViewModel::class.java)
		
		// Tell the binding about the ViewModel
		binding.viewModel = firstNameViewModel
		
		// Observe the firstNameEmpty LiveData and show feedback if true
		firstNameViewModel.firstNameEmpty.observe(viewLifecycleOwner,
		                                          Observer { isError ->
			
			                                          // If isError, show feedback to the user
			                                          if (isError) {
				
				                                          // Show an error message on the firstNameFieldLayout
				                                          binding.firstNameFieldLayout.error = getString(R.string.error_you_forgot_me)
				
			                                          }
			
			                                          // Else, reset the error
			                                          else {
				
				                                          binding.firstNameFieldLayout.error = null
				
			                                          }
			
		                                          })
		
		// Observe the firstNameOk LiveData, notify the emailSignupViewModel about the firstName and navigate to the FamilyNameFragment
		firstNameViewModel.firstNameOk.observe(viewLifecycleOwner,
		                                       Observer { isOk ->
			
			                                       // If isOk, notify and navigate
			                                       if (isOk) {
				
				                                       // Put the firstName into the SignupFormData singleton object
				                                       SignupFormData.getInstance().firstName = firstNameViewModel.firstName.value!!
				
				                                       // Navigate to the FamilyNameFragment
				                                       navigateToFamilyNameFragment()
				
				                                       // Reset the firstNameOk LiveData
				                                       firstNameViewModel.navigatedAndNotified()
				
			                                       }
			
		                                       })
		
		// Return the inflated layout
		return binding.root
		
	}
	
	/**
	 * Navigates to the FamilyNameFragment using the NavController.
	 */
	private fun navigateToFamilyNameFragment() {
		
		Timber.d("navigateToFamilyNameFragment: called")
		
		// Navigate to the FamilyNameFragment
		findNavController(this).navigate(FirstNameFragmentDirections.toFamilyNameFragment())
		
	}
	
}