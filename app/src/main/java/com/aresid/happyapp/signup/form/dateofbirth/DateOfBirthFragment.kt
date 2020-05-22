package com.aresid.happyapp.signup.form.dateofbirth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentDateOfBirthBinding
import com.aresid.happyapp.legalities.PrivacyPolicyDialog
import com.aresid.happyapp.legalities.TermsAndConditionsDialog
import com.aresid.happyapp.utils.Util.makeLinks
import timber.log.Timber

/**
 * Created on: 23/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class DateOfBirthFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentDateOfBirthBinding
	
	// Corresponding ViewModel
	private lateinit var dateOfBirthViewModel: DateOfBirthViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentDateOfBirthBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		dateOfBirthViewModel = ViewModelProvider(this).get(DateOfBirthViewModel::class.java)
		
		// Tell the binding about the ViewModel
		binding.viewModel = dateOfBirthViewModel
		
		// Initialize the datePicker
		initializeDatePicker()
		
		// Initialize the legalitiesInfo text and make it clickable
		initializeLegalitiesInfoText()
		
		// Return the inflated layout
		return binding.root
	}
	
	/**
	 * Sets the proper text on the legalitiesInfoText,
	 * makes the "Terms and Conditions" and "Privacy Policy" clickable, and
	 * shows the corresponding Dialog when clicked.
	 */
	private fun initializeLegalitiesInfoText() {
		
		Timber.d("initializeLegalitiesInfoText: called")
		
		// Set the proper text on the legalitiesInfo
		binding.legalitiesInfoText.text = getString(
			R.string.legalities_info,
			getString(R.string.subscribe),
			getString(R.string.terms_and_conditions),
			getString(R.string.privacy_policy)
		)
		
		// Make the Terms of conditions and Privacy policy clickable
		binding.legalitiesInfoText.makeLinks(
			Pair(getString(R.string.terms_and_conditions),
			     View.OnClickListener {
				
				     // Show the TermsAndConditionsDialog when clicked
				     TermsAndConditionsDialog().show(
					     parentFragmentManager,
					     "Terms and Conditions"
				     )
				
			     }),
			Pair(getString(R.string.privacy_policy),
			     View.OnClickListener {
				
				     // Show the PrivacyPolicyDialog when clicked
				     PrivacyPolicyDialog().show(
					     parentFragmentManager,
					     "Privacy Policy"
				     )
				
			     })
		)
		
	}
	
	/**
	 * Sets the datePicker's init value.
	 */
	private fun initializeDatePicker() {
		
		Timber.d("initializeDatePicker: called")
		
		// Set the init value of the datePicker to 01/01/1980
		binding.datePicker.init(
			1980,
			0,
			1,
			null
		)
		
	}
	
}