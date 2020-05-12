package com.aresid.happyapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentLoginBinding
import com.aresid.happyapp.keys.Keys
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
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
	private var mNavController: NavController? = null
	
	// Declare fields and layouts for login flow
	private var mEmailFieldLayout: TextInputLayout? = null
	private var mEmailField: TextInputEditText? = null
	private var mPasswordFieldLayout: TextInputLayout? = null
	private var mPasswordField: TextInputEditText? = null
	private var mAuth: FirebaseAuth? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		
		Timber.d("onCreate: called")
		
		super.onCreate(savedInstanceState)
		
	}
	
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
		
		// TODO:  onCreateView: code goes here
		
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
				
				                                    navigateToMainFragment()
				
			                                    }
			
		                                    })
		
		// Observe the LiveData for the FirebaseAuthInvalidUserException and show the appropriate error
		loginViewModel.firebaseAuthInvalidUserException.observe(viewLifecycleOwner,
		                                                        Observer { isError ->
			
			                                                        // If the error is thrown, show the appropriate error message
			                                                        if (isError) {
				
				                                                        binding.emailFieldLayout.error = getString(R.string.error_account_disabled_or_deleted)
				
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
	 * Finds the NavController and navigates to the MainFragment using LoginFragmentDirections.
	 */
	private fun navigateToMainFragment() {
		
		Timber.d("navigateToMainFragment: called")
		
		// Find the NavController and navigate to the MainFragment using LoginFragmentDirections
		findNavController(this).navigate(LoginFragmentDirections.toMainFragment())
		
	}
	
	/**
	 * Use this method to reference views
	 *
	 * @param view               Parent
	 * @param savedInstanceState savedInstanceState
	 */
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		Timber.d("onViewCreated: called")
		super.onViewCreated(
			view,
			savedInstanceState
		)
		
		// Define the NavController
		mNavController = Navigation.findNavController(view)
		
		// Set onClickListeners
		//		view.findViewById<View>(R.id.login_button).setOnClickListener(this)
		view.findViewById<View>(R.id.email_signup_button).setOnClickListener(this)
		view.findViewById<View>(R.id.google_signup_button).setOnClickListener(this)
		
		// Define fields and layouts for login flow
		mEmailFieldLayout = view.findViewById(R.id.email_field_layout)
		mEmailField = view.findViewById(R.id.email_field)
		mPasswordFieldLayout = view.findViewById(R.id.password_field_layout)
		mPasswordField = view.findViewById(R.id.password_field)
	}
	
	override fun onClick(v: View) {
		Timber.d("onClick: called")
		when (v.id) {
			//			R.id.login_button -> onLoginButtonClicked(v as Button)
			R.id.email_signup_button -> showEmailSignupFragment()
			R.id.google_signup_button -> onGoogleSignupButtonClicked()
		}
	}
	
	/**
	 * Navigates to the EmailSignupFragment.
	 */
	private fun showEmailSignupFragment() {
		Timber.d("showEmailSignupFragment: called")
		
		// Navigate to the EmailSignupFragment
		mNavController!!.navigate(LoginFragmentDirections.toEmailSignupFragment())
	}
	
	/**
	 * Navigates to the SubscribeFragment and puts an ID in a Bundle to identify that the user want Google signup.
	 */
	private fun onGoogleSignupButtonClicked() {
		Timber.d("onGoogleSignupButtonClicked: called")
		
		// Define a new Bundle object
		val arguments = Bundle()
		
		// Put a String into the Bundle to identify that the user wants a Google signup
		arguments.putString(
			Keys.BundleKeys.KEY_GOOGLE_SIGNUP_ID,
			Keys.BundleKeys.GOOGLE_SIGNUP_ID
		)
		
		// Navigate to the SubscribeFragment and pass the bundle
		mNavController!!.navigate(
			LoginFragmentDirections.toSubscribeFragment()
		)
	}
	
	/**
	 * Sets the email and password field layouts error null
	 */
	private fun resetLoginLayoutErrors() {
		Timber.d("resetLayoutErrors: called")
		
		// Set layout error null
		mEmailFieldLayout!!.error = null
		mPasswordFieldLayout!!.error = null
	}
	// TODO: 23/04/2020: Implement reset password policy.
	/**
	 * Required public empty constructor
	 */
	init {
		Timber.d("LoginFragment: called")
	}
}