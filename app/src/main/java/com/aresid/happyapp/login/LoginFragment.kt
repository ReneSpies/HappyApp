package com.aresid.happyapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentLoginBinding
import com.aresid.happyapp.utils.Util
import timber.log.Timber

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

class LoginFragment: Fragment(), View.OnClickListener {
	
	// Declare LoginViewModel
	private lateinit var loginViewModel: LoginViewModel
	
	// Declare FragmentLoginBinding
	private lateinit var binding: FragmentLoginBinding
	
	// Declare NavController
	private lateinit var mNavController: NavController
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define LoginViewModel
		loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
		
		// Define FragmentLoginBinding and inflate the layout
		binding = FragmentLoginBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the NavController object
		mNavController = findNavController(this)
		
		// Observe the LiveData of emailOrPasswordIsEmpty and show an error to the user
		loginViewModel.emailOrPasswordIsEmpty.observe(viewLifecycleOwner,
		                                              Observer { isEmpty ->
			
			                                              // If the LiveData is true, show an error to the user
			                                              if (isEmpty) {
				
				                                              binding.emailFieldLayout.error = getString(R.string.error_email_or_password_empty)
				
			                                              }
			
			                                              // Else, reset the error
			                                              else {
				
				                                              binding.emailFieldLayout.error = null
				
			                                              }
			
		                                              })
		
		// Observe the LiveData of the firebaseUser and if the user is not null, navigate to the MainFragment
		loginViewModel.firebaseUser.observe(viewLifecycleOwner,
		                                    Observer { firebaseUser ->
			
			                                    Timber.d("firebaseUser = $firebaseUser")
			
			                                    // Check if the User is not null and log the user in
			                                    if (firebaseUser != null) {
				
				                                    // Hide the soft keyboard
				                                    Util.hideKeyboard(
					                                    requireContext(),
					                                    binding.loginButton
				                                    )
				
				                                    // Navigate to MainFragment
				                                    navigateToMainFragment()
				
			                                    }
			
		                                    })
		
		// Observe the LiveData for the FirebaseAuthInvalidUserException and show the appropriate error
		loginViewModel.firebaseAuthInvalidUserException.observe(viewLifecycleOwner,
		                                                        Observer { isError ->
			
			                                                        // If the error is thrown, show the appropriate error message
			                                                        if (isError) {
				
				                                                        binding.emailFieldLayout.error = getString(R.string.error_email_not_recognized)
				
			                                                        }
			
			                                                        // Else, reset the error
			                                                        else {
				
				                                                        binding.emailFieldLayout.error = null
				
			                                                        }
			
		                                                        })
		
		// Observe the LiveData for the FirebaseAuthInvalidCredentialException and show the appropriate error
		loginViewModel.firebaseAuthInvalidCredentialsException.observe(viewLifecycleOwner,
		                                                               Observer { isError ->
			
			                                                               // If the error is thrown, show the appropriate error message
			                                                               if (isError) {
				
				                                                               // Show the error
				                                                               binding.emailFieldLayout.error = getString(R.string.error_email_or_password_incorrect)
				
			                                                               }
			
			                                                               // Else, reset the error
			                                                               else {
				
				                                                               binding.emailFieldLayout.error = null
				
			                                                               }
			
		                                                               })
		
		// Observe the LiveData for the FirebaseNetworkException and show the appropriate error
		loginViewModel.firebaseNetworkException.observe(viewLifecycleOwner,
		                                                Observer { isError ->
			
			                                                // If the error happens, show the message
			                                                if (isError) {
				
				                                                // Show a network connection failed error message
				                                                binding.emailFieldLayout.error = getString(R.string.error_network_connection_failed)
				
			                                                }
			
			                                                // Else, reset the error
			                                                else {
				
				                                                binding.emailFieldLayout.error = null
				
			                                                }
			
		                                                })
		
		// Let the data binder know about the LoginViewModel
		binding.loginViewModel = loginViewModel
		
		// Return the inflated layout
		return binding.root
		
	}
	
	/**
	 * Uses the NavController object to navigate to the ForgotLoginFragment using LoginFragmentDirections.
	 */
	private fun navigateToForgotLoginFragment() {
		
		Timber.d("navigateToForgotLoginFragment: called")
		
		// Navigate to the ForgotLoginFragment
		mNavController.navigate(LoginFragmentDirections.toForgotLoginFragment())
		
	}
	
	/**
	 * Uses the NavController object to navigate to the MainFragment using LoginFragmentDirections.
	 */
	private fun navigateToMainFragment() {
		
		Timber.d("navigateToMainFragment: called")
		
		// Find the NavController and navigate to the MainFragment using LoginFragmentDirections
		mNavController.navigate(LoginFragmentDirections.toMainFragment())
		
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		Timber.d("onViewCreated: called")
		super.onViewCreated(
			view,
			savedInstanceState
		)
		
		// Set onClickListeners
		binding.emailSignupButton.setOnClickListener(this)
		binding.googleSignupButton.setOnClickListener(this)
		binding.forgotLoginButton.setOnClickListener(this)
		
		// Subscribe Button click listener. temporarily
		binding.subscribeButton.setOnClickListener(this)
		
	}
	
	override fun onClick(v: View) {
		
		Timber.d("onClick: called")
		
		when (v.id) {
			
			R.id.email_signup_button -> navigateToEmailSignupFragment()
			R.id.google_signup_button -> onGoogleSignupButtonClicked()
			R.id.forgot_login_button -> navigateToForgotLoginFragment()
			R.id.subscribe_button -> navigateToSubscribeFragment()
			
		}
		
	}
	
	private fun navigateToSubscribeFragment() {
		
		Timber.d("navigateToSubscribeFragment: called")
		
		mNavController.navigate(LoginFragmentDirections.toSubscribeFragment())
		
	}
	
	/**
	 * Uses the NavController object to navigate to the EmailSignupFragment using LoginFragmentDirections.
	 */
	private fun navigateToEmailSignupFragment() {
		
		Timber.d("showEmailSignupFragment: called")
		
		// Navigate to the EmailSignupFragment
		mNavController.navigate(LoginFragmentDirections.toEmailSignupFragment())
		
	}
	
	/**
	 * Uses the NavController object to navigate to the SubscribeFragment using LoginFragmentDirections.
	 */
	private fun onGoogleSignupButtonClicked() {
		
		Timber.d("onGoogleSignupButtonClicked: called")
		
		// Navigate to the SubscribeFragment
		mNavController.navigate(LoginFragmentDirections.toGoogleSignupFragment())
		
	}
	
}