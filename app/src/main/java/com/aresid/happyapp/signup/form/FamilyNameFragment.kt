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
import com.aresid.happyapp.databinding.FragmentFamilyNameBinding
import timber.log.Timber

/**
 * Created on: 23/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

class FamilyNameFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentFamilyNameBinding
	
	// Corresponding ViewModel
	private lateinit var familyNameViewModel: FamilyNameViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentFamilyNameBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		familyNameViewModel = ViewModelProvider(this).get(FamilyNameViewModel::class.java)
		
		// Tell the binding about the ViewModel
		binding.viewModel = familyNameViewModel
		
		// Observe the familyNameEmpty LiveData and show feedback if true
		familyNameViewModel.familyNameEmpty.observe(viewLifecycleOwner,
		                                            Observer { isError ->
			
			                                            // If isError, show feedback to the user
			                                            if (isError) {
				
				                                            // Show an error message on the familyNameFieldLayout
				                                            binding.familyNameFieldLayout.error = getString(R.string.error_you_forgot_me)
				
			                                            }
			
			                                            // Else, reset the error
			                                            else {
				
				                                            binding.familyNameFieldLayout.error = null
				
			                                            }
			
		                                            })
		
		// Observe the familyNameOk LiveData, notify the SignupFormData singleton about the familyName and navigate to the UsernameFragment
		familyNameViewModel.familyNameOk.observe(viewLifecycleOwner,
		                                         Observer { isOk ->
			
			                                         // If isOk, notify and navigate
			                                         if (isOk) {
				
				                                         // Put the familyName into the SignupFormData singleton object
				                                         SignupFormData.getInstance().familyName = familyNameViewModel.familyName.value!!
				
				                                         // Navigate to the UsernameFragment
				                                         navigateToUsernameFragment()
				
				                                         // Reset the familyNameOk LiveData
				                                         familyNameViewModel.navigatedAndNotified()
				
			                                         }
			
		                                         })
		
		// Return the inflated layout
		return binding.root
		
	}
	
	/**
	 * Navigates to the UsernameFragment using the NavController.
	 */
	private fun navigateToUsernameFragment() {
		
		Timber.d("navigateToUsernameFragment: called")
		
		// Navigate to the FamilyNameFragment
		findNavController(this).navigate(FamilyNameFragmentDirections.toUsernameFragment())
		
	}
	
}