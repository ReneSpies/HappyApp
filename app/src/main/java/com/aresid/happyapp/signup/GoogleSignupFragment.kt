package com.aresid.happyapp.signup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentGoogleSignupBinding
import com.aresid.happyapp.keys.Keys
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import timber.log.Timber

/**
 *    Created on: 15.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class GoogleSignupFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentGoogleSignupBinding
	
	// Corresponding ViewModel
	private lateinit var googleSignupViewModel: GoogleSignupViewModel
	
	private lateinit var mGoogleSignInClient: GoogleSignInClient
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding object and inflate the layout
		binding = FragmentGoogleSignupBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel object
		googleSignupViewModel = ViewModelProvider(this).get(GoogleSignupViewModel::class.java)
		
		// Observe the firebaseUser LiveData and navigate to the SubscribeFragment, if not null
		googleSignupViewModel.firebaseUser.observe(
			viewLifecycleOwner,
			Observer { firebaseUser ->
				
				// If the firebaseUser is not null, navigate to the SubscribeFragment
				if (firebaseUser != null) {
					
					navigateToSubscribeFragment()
					
				}
				
			})
		
		// Configure GoogleSignInClient
		configureGoogleSignInClient()
		
		// Tell the binding about the ViewModel
		binding.viewModel = googleSignupViewModel
		
		// Listener for the Google button which simple starts the signup flow
		binding.googleSignupButton.setOnClickListener { startSignupFlow() }
		
		// Return the inflated layout to create it
		return binding.root
		
	}
	
	private fun navigateToSubscribeFragment() {
		
		Timber.d("navigateToSubscribeFragment: called")
		
		findNavController(this).navigate(GoogleSignupFragmentDirections.toSubscribeFragment())
		
	}
	
	private fun configureGoogleSignInClient() {
		
		Timber.d("configureGoogleSignInClient: called")
		
		// Define a GoogleSignInOptions object using a builder
		val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
		
		// Define the mGoogleSignInClient
		mGoogleSignInClient = GoogleSignIn.getClient(
			requireContext(),
			googleSignInOptions
		)
		
	}
	
	override fun onStart() {
		
		Timber.d("onStart: called")
		
		super.onStart()
		
		// Start the signIn flow
		startSignupFlow()
		
	}
	
	private fun startSignupFlow() {
		
		Timber.d("signIn: called")
		
		// Define a Intent object from the GoogleSignInClient object
		val signInIntent = mGoogleSignInClient.signInIntent
		
		// Start the activity for result
		startActivityForResult(
			signInIntent,
			Keys.RequestCodes.REQUEST_CODE_GOOGLE_SIGN_IN
		)
		
	}
	
	override fun onActivityResult(
		requestCode: Int,
		resultCode: Int,
		data: Intent?
	) {
		
		Timber.d("onActivityResult: called")
		
		super.onActivityResult(
			requestCode,
			resultCode,
			data
		)
		
		Timber.d("result code = $resultCode")
		Timber.d("request code = $requestCode")
		
		// Result is OK and the Google Signup
		if (requestCode == Keys.RequestCodes.REQUEST_CODE_GOOGLE_SIGN_IN && resultCode == Activity.RESULT_OK) {
			
			Timber.d("google sign in & result ok")
			
			// Processes the Intent and checks if everything is ok and if so, leads the user further to the SubscribeFragment
			googleSignupViewModel.processGoogleSignup(data)
			
		}
		
	}
	
}