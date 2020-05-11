package com.aresid.happyapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentLoginBinding
import com.aresid.happyapp.keys.Keys
import com.aresid.happyapp.utils.Util
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
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
		
		// TODO:  onCreate: move the firebase auth to the ViewModel
		// Define FirebaseAuth object
		mAuth = FirebaseAuth.getInstance()
		
		// UpdateUI to send user through if exists
		updateUI(mAuth!!.currentUser)
		
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
		
		// Observe the LiveData of the firebaseUser and if the user is not null, navigate to the MainFragment
		loginViewModel.firebaseUser.observe(
			viewLifecycleOwner,
			Observer { firebaseUser ->
				
				// Check if the User is not null and log the user in
				firebaseUser ?: navigateToMainFragment()
				
			})
		
		// Observe the LiveData for the FirebaseAuthInvalidUserException and show the appropriate error
		loginViewModel.firebaseAuthInvalidUserException.observe(
			viewLifecycleOwner,
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
		loginViewModel.firebaseAuthInvalidCredentialsException.observe(
			viewLifecycleOwner,
			Observer { isError ->
				
				// If the error is thrown, show the appropriate error message
				if (isError) {
					
					binding.emailFieldLayout.error = getString(R.string.error_email_or_password_incorrect)
					
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
	
	/**
	 * Sends user to the MainFragment if user exists.
	 *
	 * @param user FirebaseUser.
	 */
	private fun updateUI(user: FirebaseUser?) {
		Timber.d("updateUI: called")
		if (user != null) {
			
			// Navigate to MainFragment
			mNavController!!.navigate(LoginFragmentDirections.toMainFragment())
		}
	}
	
	override fun onClick(v: View) {
		Timber.d("onClick: called")
		when (v.id) {
			//			R.id.login_button -> onLoginButtonClicked(v as Button)
			R.id.email_signup_button -> showEmailSignupFragment()
			R.id.google_signup_button -> onGoogleSignupButtonClicked()
		}
	}
	
	private fun onLoginButtonClicked(button: Button) {
		Timber.d("onLoginButtonClicked: called")
		
		// Define Strings carrying the fields text
		val email = Util.getString(mEmailField!!.text)
		val password = Util.getString(mPasswordField!!.text)
		
		// Reset old layout errors
		resetLoginLayoutErrors()
		
		// Define error string
		val youForgotMeError = getString(R.string.error_you_forgot_me)
		
		// If email length is 0, set appropriate error
		if (email.isEmpty()) {
			mEmailFieldLayout!!.error = youForgotMeError
			return
		}
		
		// If password length is 0, set appropriate error
		if (password.isEmpty()) {
			mPasswordFieldLayout!!.error = youForgotMeError
			return
		}
		
		// Start loading animation on the button and disable it
		Util.setAndStartLoadingButtonAnimationWithDisable(
			button,
			true
		)
		
		// Sign in with email and password
		mAuth!!.signInWithEmailAndPassword(
			email,
			password
		).addOnSuccessListener { result: AuthResult ->
			Timber.d("onLoginButtonClicked: great success logging user in")
			
			// Send the user to MainFragment if exist
			updateUI(result.user)
			
			// Remove loading animation and enable button again
			Util.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
		}.addOnFailureListener { e: Exception? ->
			Timber.d("onLoginButtonClicked: failure logging user in")
			Timber.e(
				e,
				"onLoginButtonClicked: "
			)
			
			// Set layout errors null so no multiple errors are shown
			resetLoginLayoutErrors()
			
			// Remove loading animation and enable button again
			Util.removeLoadingButtonAnimationWithEnable(
				button,
				true
			)
			when (e) {
				is FirebaseAuthInvalidCredentialsException -> {
					
					// Wrong credentials
					mEmailFieldLayout!!.error = getString(R.string.error_email_or_password_incorrect)
				}
				
				is FirebaseAuthInvalidUserException -> {
					
					// Set appropriate error
					mEmailFieldLayout!!.error = getString(R.string.error_account_disabled_or_deleted)
				}
				
				is FirebaseNetworkException -> {
					
					// Show appropriate error Snackbar
					Util.showErrorSnackbar(
						mEmailFieldLayout,
						getString(R.string.error_network_connection_failed),
						requireContext()
					)
				}
			}
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