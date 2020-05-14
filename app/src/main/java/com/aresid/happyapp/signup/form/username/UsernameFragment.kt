package com.aresid.happyapp.signup.form.username

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentUsernameBinding
import com.aresid.happyapp.signup.form.SignupFormData
import timber.log.Timber

/**
 * Created on: 23/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

class UsernameFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentUsernameBinding
	
	// Corresponding ViewModel
	private lateinit var usernameViewModel: UsernameViewModel
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentUsernameBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		usernameViewModel = ViewModelProvider(this).get(UsernameViewModel::class.java)
		
		// Tell the binding about the ViewModel
		binding.viewModel = usernameViewModel
		
		// Observe the usernameEmpty LiveData and show feedback if true
		usernameViewModel.usernameEmpty.observe(viewLifecycleOwner,
		                                        Observer { isError ->
			
			                                        // If isError, show feedback to the user
			                                        if (isError) {
				
				                                        // Show an error message on the usernameFieldLayout
				                                        binding.usernameFieldLayout.error = getString(R.string.error_you_forgot_me)
				
			                                        }
			
			                                        // Else, reset the error
			                                        else {
				
				                                        binding.usernameFieldLayout.error = null
				
			                                        }
			
		                                        })
		
		// Observe the usernameOk LiveData, notify the SignupFormData singleton about the username and navigate to the EmailFragment
		usernameViewModel.usernameOk.observe(viewLifecycleOwner,
		                                     Observer { isOk ->
			
			                                     // If isOk, notify and navigate
			                                     if (isOk) {
				
				                                     // Put the username into the SignupFormData singleton object
				                                     SignupFormData.getInstance().username = usernameViewModel.username.value!!
				
				                                     // Navigate to the EmailFragment
				                                     navigateToEmailFragment()
				
				                                     // Reset the usernameOk LiveData
				                                     usernameViewModel.navigatedAndNotified()
				
			                                     }
			
		                                     })
		
		// Observe the usernameTaken LiveData and show feedback to the user
		usernameViewModel.usernameTaken.observe(viewLifecycleOwner,
		                                        Observer { isTaken ->
			
			                                        // If isTaken, show feedback to the user
			                                        if (isTaken) {
				
				                                        binding.usernameFieldLayout.error = getString(R.string.error_username_is_already_taken)
				
			                                        }
			
			                                        // Else, reset the error message
			                                        else {
				
				                                        binding.usernameFieldLayout.error = null
				
			                                        }
			
		                                        })
		
		// Return the inflated layout
		return binding.root
		
	}
	
	/**
	 * Navigates to the EmailFragment using the NavController.
	 */
	private fun navigateToEmailFragment() {
		
		Timber.d("navigateToEmailFragment: called")
		
		// Navigate to the EmailFragment
		NavHostFragment.findNavController(this).navigate(UsernameFragmentDirections.toEmailFragment())
		
	}
	
}