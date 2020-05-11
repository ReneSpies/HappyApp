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
class FamilyNameFragment: Fragment(), View.OnClickListener {
	
	private var mNavController: NavController? = null
	private var mFamilyNameField: TextInputEditText? = null
	private var mFamilyNameFieldLayout: TextInputLayout? = null
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
			R.layout.fragment_family_name,
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
		
		// Set OnClickListener
		view.findViewById<View>(R.id.next_button).setOnClickListener(this)
		
		// Define the FamilyNameField from the layout
		mFamilyNameField = view.findViewById(R.id.family_name_field)
		
		// Define the FamilyNameFieldLayout from the layout
		mFamilyNameFieldLayout = view.findViewById(R.id.family_name_field_layout)
	}
	
	override fun onClick(v: View) {
		Log.d(
			TAG,
			"onClick: called"
		)
		if (v.id == R.id.next_button) {
			showUsernameFragment()
		}
	}
	
	/**
	 * Bundles the family name into the bundle from getArguments() and navigates to the UsernameFragment.
	 * Returns if no family name was detected or getArguments() is null and shows appropriate errors.
	 */
	private fun showUsernameFragment() {
		Log.d(
			TAG,
			"showUsernameFragment: called"
		)
		
		// Show error if no family name was detected and return
		if (mFamilyNameField!!.length() == 0) {
			
			// Show error
			mFamilyNameFieldLayout!!.error = getString(R.string.error_you_forgot_me)
			return
		}
		
		// Reset the FamilyNameFieldLayouts error proactively
		mFamilyNameFieldLayout!!.error = null
		
		// Define a new Bundle object from getArguments()
		val arguments = arguments
		
		// Show error snackbar if arguments is null and return
		if (arguments == null) {
			
			// Show the error snackbar
			Util.showErrorSnackbar(
				mFamilyNameFieldLayout,
				getString(R.string.error_try_again),
				requireContext()
			)
			return
		}
		
		// Put the family name into the bundle
		arguments.putString(
			Keys.BundleKeys.KEY_FAMILY_NAME,
			Util.getString(mFamilyNameField!!.text)
		)
		
		// Navigate to the UsernameFragment and pass the bundle
		mNavController!!.navigate(R.id.to_usernameFragment)
	}
	
	companion object {
		private const val TAG = "FamilyNameFragment"
	}
}