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
import com.aresid.happyapp.utils.LoadingStatus
import com.aresid.happyapp.utils.Util
import timber.log.Timber

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

class LoginFragment: Fragment() {
	
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
		
		// Tell the layout about this fragment to use its methods
		binding.fragment = this
		
		// Define the NavController object
		mNavController = findNavController(this)
		
		// Observe the LiveData of emailOrPasswordIsEmpty and show an error to the user
		loginViewModel.emailOrPasswordIsEmpty.observe(
			viewLifecycleOwner,
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
		
		// Observe the toggleLoadingScreen LiveData to control the loading screen
		// and decide where to send the user next
		loginViewModel.toggleLoadingScreen.observe(
			viewLifecycleOwner,
			Observer { status ->
				
				// If status is IDLE, show the content and stop the animation
				when (status) {
					
					LoadingStatus.IDLE -> {
						
						// Show the content and stop the animation
						showContent()
						
					}
					
					// Else, if the status is LOADING, show the loading screen
					LoadingStatus.LOADING -> {
						
						// Show the loading screen
						showLoading()
						
					}
					
					// Else, if the status is SUCCESS, bring the user to the MainFragment
					// and reset the LiveData value
					LoadingStatus.SUCCESS -> {
						
						// Navigate to MainFragment
						navigateToMainFragment()
						
						// Reset the LiveData
						loginViewModel.navigated()
						
					}
					
					// Else, if the status is ERROR_NO_INTERNET,
					// show the content again and an error to the user
					LoadingStatus.ERROR_NO_INTERNET -> {
						
						// Show the content
						showContent()
						
						// Show an error
						Util.showErrorSnackbar(
							binding.progressBar,
							getString(R.string.error_no_internet_connection)
						)
						
					}
					
					// Else, if the status is ERROR_USER_DELETED,
					// show the content again and an error to the user
					LoadingStatus.ERROR_USER_DELETED -> {
						
						// Show content again
						showContent()
						
						// Show an error
						Util.showErrorSnackbar(
							binding.progressBar,
							getString(R.string.error_account_disabled_or_deleted)
						)
						
					}
					
					LoadingStatus.ERROR_NOT_SUBSCRIBED -> {
						
						// Navigate to the SubscribeFragment
						navigateToSubscribeFragment()
						
						// Reset the LiveData
						loginViewModel.navigated()
						
					}
					
					LoadingStatus.INIT -> {
					}
					
					else -> {
					}
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
	 * Hides the loading spinner, stops its animation and shows the content.
	 */
	private fun showContent() {
		
		Timber.d("showContent: called")
		
		// Hide the loading spinner
		binding.loading.visibility = View.GONE
		
		// Show the content
		binding.content.visibility = View.VISIBLE
		
	}
	
	/**
	 * Hides the content, starts the loading spinner animation and shows the loading spinner itself.
	 */
	private fun showLoading() {
		
		Timber.d("showLoading: called")
		
		// Hide the content
		binding.content.visibility = View.GONE
		
		// Show the loading spinner
		binding.loading.visibility = View.VISIBLE
		
	}
	
	/**
	 * Uses the NavController object to navigate to the ForgotLoginFragment using LoginFragmentDirections.
	 */
	fun navigateToForgotLoginFragment() {
		
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
	
	/**
	 * Uses the NavController to navigate to the SubscribeFragment.
	 */
	fun navigateToSubscribeFragment() {
		
		Timber.d("navigateToSubscribeFragment: called")
		
		mNavController.navigate(LoginFragmentDirections.toSubscribeFragment())
		
	}
	
	/**
	 * Uses the NavController object to navigate to the EmailSignupFragment using LoginFragmentDirections.
	 */
	fun navigateToEmailSignupFragment() {
		
		Timber.d("showEmailSignupFragment: called")
		
		// Navigate to the EmailSignupFragment
		mNavController.navigate(LoginFragmentDirections.toEmailSignupFragment())
		
	}
	
	/**
	 * Uses the NavController object to navigate to the SubscribeFragment using LoginFragmentDirections.
	 */
	fun navigateToGoogleSignupFragment() {
		
		Timber.d("onGoogleSignupButtonClicked: called")
		
		// Navigate to the SubscribeFragment
		mNavController.navigate(LoginFragmentDirections.toGoogleSignupFragment())
		
	}
	
}