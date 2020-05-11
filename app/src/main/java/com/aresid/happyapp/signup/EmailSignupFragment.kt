package com.aresid.happyapp.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.aresid.happyapp.R
import com.aresid.happyapp.signup.EmailSignupHelper.SignupCheckerListener
import timber.log.Timber

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class EmailSignupFragment: Fragment(), SignupCheckerListener {
	
	private var mNavController: NavController? = null
	private var mEmailSignupHelper: EmailSignupHelper? = null
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		Timber.d("onCreateView: called")
		
		// Inflate the layout
		return inflater.inflate(
			R.layout.fragment_email_signup,
			container,
			false
		)
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
		
		/* Use this class to reference the views */
		
		// Define NavController
		mNavController = Navigation.findNavController(view)
		
		// Define the EmailSignupHelper
		mEmailSignupHelper = EmailSignupHelper(
			this,
			view
		)
	}
	
	/**
	 * Is called when everything is ok with the signup forms input.
	 */
	override fun inputIsOk() {
		Timber.d("inputIsOk: called")
		
		// Get signup forms input as bundle and pass it to the subscribe fragment
		
		// Define the Bundle
		val arguments = mEmailSignupHelper?.inputBundle
		
		// Navigate to the subscription fragment and pass the bundle
		mNavController!!.navigate(
			EmailSignupFragmentDirections.toSubscribeFragment()
		)
	}
	
	init {
		Timber.d("EmailSignupFragment: called")
		// Required public empty constructor
	}
}