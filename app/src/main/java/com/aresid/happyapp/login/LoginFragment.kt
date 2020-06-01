package com.aresid.happyapp.login

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.aresid.happyapp.LoadingStatus
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
		
		// Observe the toggleLoadingScreen LiveData to control the loading screen
		// and decide where to send the user next
		loginViewModel.toggleLoadingScreen.observe(
			viewLifecycleOwner,
			Observer { status ->
				
				// If status is IDLE, show the content and stop the animation
				when (status) {
					
					LoadingStatus.IDLE -> {
						
						// Show the content and stop the animation
						showContentStopLoadingAnimation()
						
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
						showContentStopLoadingAnimation()
						
						// Show an error
						Util.showErrorSnackbar(
							binding.loadingSpinner,
							getString(R.string.error_no_internet_connection)
						)
						
					}
					
					// Else, if the status is ERROR_USER_DELETED,
					// show the content again and an error to the user
					LoadingStatus.ERROR_USER_DELETED -> {
						
						// Show content again
						showContentStopLoadingAnimation()
						
						// Show an error
						Util.showErrorSnackbar(
							binding.loadingSpinner,
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
	
	override fun onResume() {
		
		Timber.d("onResume: called")
		
		super.onResume()
		
		// If the loadingSpinner is visible, start its loading animation
		if (binding.loadingSpinner.visibility == View.VISIBLE) {
			
			startLoadingAnimation()
			
		}
		
	}
	
	override fun onStop() {
		
		Timber.d("onStop: called")
		
		super.onStop()
		
		// Stop the loading animation to save resources
		stopLoadingAnimation()
		
	}
	
	/**
	 * Stops the loadingSpinner's animation.
	 */
	private fun stopLoadingAnimation() {
		
		Timber.d("stopLoadingAnimation: called")
		
		// Casts the loadingSpinner's drawable to AnimatedVectorDrawable and stops its animation
		(binding.loadingSpinner.drawable as AnimatedVectorDrawable).stop()
		
	}
	
	/**
	 * Starts the loadingSpinner's animation.
	 */
	private fun startLoadingAnimation() {
		
		Timber.d("startLoadingAnimation: called")
		
		// Casts the loadingSpinner's drawable to AnimatedVectorDrawable and starts its animation
		(binding.loadingSpinner.drawable as AnimatedVectorDrawable).start()
		
	}
	
	/**
	 * Hides the loading spinner, stops its animation and shows the content.
	 */
	private fun showContentStopLoadingAnimation() {
		
		Timber.d("showContent: called")
		
		// Hide the loading spinner
		binding.loadingSpinner.visibility = View.GONE
		
		// Stop the animation
		stopLoadingAnimation()
		
		// Show the content
		binding.loginContent.visibility = View.VISIBLE
		
	}
	
	/**
	 * Hides the content, starts the loading spinner animation and shows the loading spinner itself.
	 */
	private fun showLoading() {
		
		Timber.d("showLoading: called")
		
		// Hide the content
		binding.loginContent.visibility = View.GONE
		
		// Start the loading animation
		startLoadingAnimation()
		
		// Show the loading spinner
		binding.loadingSpinner.visibility = View.VISIBLE
		
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
		
	}
	
	override fun onClick(v: View) {
		
		Timber.d("onClick: called")
		
		when (v.id) {
			
			R.id.email_signup_button -> navigateToEmailSignupFragment()
			R.id.google_signup_button -> onGoogleSignupButtonClicked()
			R.id.forgot_login_button -> navigateToForgotLoginFragment()
			
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