package com.aresid.happyapp.signup.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.aresid.happyapp.R
import com.aresid.happyapp.keys.Keys
import com.aresid.happyapp.utils.Util
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Created on: 23/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class FirstNameFragment: Fragment(), View.OnClickListener {
	
	private var mNavController: NavController? = null
	private var mFirstNameField: TextInputEditText? = null
	private var mFirstNameFieldLayout: TextInputLayout? = null
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		Log.d(
			TAG,
			"onCreateView: called"
		)
		
		// Inflate the layout
		return inflater.inflate(
			R.layout.fragment_first_name,
			container,
			false
		)
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		Log.d(
			TAG,
			"onViewCreated: called"
		)
		super.onViewCreated(
			view,
			savedInstanceState
		)
		
		// Define the NavController object
		mNavController = Navigation.findNavController(view)
		
		// Set onClickListener
		view.findViewById<View>(R.id.next_button).setOnClickListener(this)
		
		// Define first name field TextInputEditText from layout
		mFirstNameField = view.findViewById(R.id.first_name_field)
		
		// Define first name field layout TextInputLayout from layout
		mFirstNameFieldLayout = view.findViewById(R.id.first_name_field_layout)
	}
	
	override fun onClick(v: View) {
		Log.d(
			TAG,
			"onClick: called"
		)
		if (v.id == R.id.next_button) {
			showFamilyNameFragment()
		}
	}
	
	/**
	 * Bundles the first name and navigates to the FamilyNameFragment passing the Bundle.
	 */
	private fun showFamilyNameFragment() {
		Log.d(
			TAG,
			"showFamilyNameFragment: called"
		)
		
		// Show error if no first name has been typed in
		if (mFirstNameField!!.length() == 0) {
			
			// No first name detected
			
			// Show error
			mFirstNameFieldLayout!!.error = getString(R.string.error_you_forgot_me)
			return
		}
		
		// Set the first name fields layout error to null proactively
		mFirstNameFieldLayout!!.error = null
		
		// Define a new Bundle object
		val arguments = Bundle()
		
		// Put the first name into the bundle
		arguments.putString(
			Keys.BundleKeys.KEY_FIRST_NAME,
			Util.getString(mFirstNameField!!.text)
		)
		
		// Navigate to the FamilyNameFragment and pass the bundle
		mNavController!!.navigate(
			R.id.to_familyNameFragment,
			arguments
		)
	}
	
	companion object {
		private const val TAG = "FirstNameFragment"
	}
	
	init {
		Log.d(
			TAG,
			"FirstNameFragment: called"
		)
		
		// Required empty constructor
	}
}