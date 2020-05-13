package com.aresid.happyapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentForgotLoginBinding
import com.google.android.material.snackbar.Snackbar
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
		
		// Observe the passwordResetEmailSent LiveData and show a success Snackbar if true
		forgotLoginViewModel.passwordResetEmailSent.observe(viewLifecycleOwner,
		                                                    Observer { isSent ->
			
			                                                    // If email has been sent, show success Snackbar
			                                                    if (isSent) {
				
				                                                    // Show the success Snackbar
				                                                    showPasswordResetEmailSentSuccessSnackbar()
				
				                                                    // Reset the passwordResetEmailSent LiveData
				                                                    forgotLoginViewModel.showedSuccessSnackbar()
				
			                                                    }
			
		                                                    })
		
		// Observe the emailBlank LiveData and show an error message if true
		forgotLoginViewModel.emailBlank.observe(viewLifecycleOwner,
		                                        Observer { isBlank ->
			
			                                        // If isBlank, show the error message on the emailFieldLayout
			                                        if (isBlank) {
				
				                                        // Show the error message on the emailFieldLayout
				                                        binding.emailFieldLayout.error = getString(R.string.error_you_forgot_me)
				
			                                        }
			
			                                        // Else, reset the error
			                                        else {
				
				                                        binding.emailFieldLayout.error = null
				
			                                        }
			
		                                        })
		
		// Observe the FirebaseAuthInvalidCredentialException LiveData and show an error message if true
		forgotLoginViewModel.firebaseAuthInvalidCredentialsException.observe(viewLifecycleOwner,
		                                                                     Observer { isError ->
			
			                                                                     // If isError, show error message on emailFieldLayout
			                                                                     if (isError) {
				
				                                                                     // Show error message
				                                                                     binding.emailFieldLayout.error = getString(R.string.error_email_is_badly_formatted)
				
			                                                                     }
			
			                                                                     // Else, reset the error message
			                                                                     else {
				
				                                                                     binding.emailFieldLayout.error = null
				
			                                                                     }
			
		                                                                     })
		
		// Observe the FirebaseNetworkException LiveData and show an error message if true
		forgotLoginViewModel.firebaseNetworkException.observe(viewLifecycleOwner,
		                                                      Observer { isError ->
			
			                                                      // If isError, show error message on emailFieldLayout
			                                                      if (isError) {
				
				                                                      binding.emailFieldLayout.error = getString(R.string.error_network_connection_failed)
				
			                                                      }
			
			                                                      // Else, reset the error message
			                                                      else {
				
				                                                      binding.emailFieldLayout.error = null
				
			                                                      }
			
		                                                      })
		
		// Observe the FirebaseAuthInvalidUserException LiveData and show an error message if true
		forgotLoginViewModel.firebaseAuthInvalidUserException.observe(
			viewLifecycleOwner,
			Observer { isError ->
				
				// If isError, show error message on emailFieldLayout
				if (isError) {
					
					binding.emailFieldLayout.error = getString(R.string.error_email_not_recognized)
					
				}
				
				// Else, reset the error message
				else {
					
					binding.emailFieldLayout.error = null
					
				}
				
			})
		
		// Tell the binding about the ViewModel
		binding.viewModel = forgotLoginViewModel
		
		// Return the inflated layout for creation
		return binding.root
		
	}
	
	/**
	 * Shows a Snackbar with message "Email sent" and black background tint.
	 */
	private fun showPasswordResetEmailSentSuccessSnackbar() {
		
		Timber.d("showPasswordResetEmailSentSuccessSnackbar: called")
		
		Snackbar.make(
			binding.sendButton,
			getString(R.string.email_sent),
			Snackbar.LENGTH_LONG
		).setBackgroundTint(
			ContextCompat.getColor(
				requireContext(),
				android.R.color.black
			)
		).show()
		
	}
	
}