package com.aresid.happyapp.signup.form.dateofbirth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentDateOfBirthBinding
import com.aresid.happyapp.legalities.PrivacyPolicyDialog
import com.aresid.happyapp.legalities.TermsAndConditionsDialog
import com.aresid.happyapp.signup.EmailSignupViewModel
import com.aresid.happyapp.utils.Util
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
	
	// emailSignupViewModel
	private val emailSignupViewModel: EmailSignupViewModel by activityViewModels()
	
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
		
		// Observe the noInternetConnection LiveData and show an error, if true
		dateOfBirthViewModel.noInternetConnection.observe(viewLifecycleOwner,
		                                                  Observer { noInternet ->
			
			                                                  // If noInternet, show an error
			                                                  if (noInternet) {
				
				                                                  Util.showErrorSnackbar(
					                                                  binding.subscribeButton,
					                                                  getString(R.string.error_no_internet_connection)
				                                                  )
				
			                                                  }
			
		                                                  })
		
		// Observe the youngerThan18 LiveData and show an error, if true
		dateOfBirthViewModel.youngerThan18.observe(viewLifecycleOwner,
		                                           Observer { isYounger ->
			
			                                           // If isYounger, show an error snackbar
			                                           if (isYounger) {
				
				                                           Util.showErrorSnackbar(
					                                           binding.subscribeButton,
					                                           getString(R.string.error_not_18)
				                                           )
				
			                                           }
			
		                                           })
		
		// Observe the emailIncorrect LiveData and show an error, if true
		dateOfBirthViewModel.emailIncorrect.observe(viewLifecycleOwner,
		                                            Observer { isIncorrect ->
			
			                                            // If isIncorrect, show an error snackbar
			                                            if (isIncorrect) {
				
				                                            Util.showErrorSnackbar(
					                                            binding.subscribeButton,
					                                            getString(R.string.error_email_is_badly_formatted)
				                                            )
				
			                                            }
			
		                                            })
		
		// Observe the registerSuccessful LiveData, tell the EmailSignupViewModel to navigate
		// and reset the LiveData
		dateOfBirthViewModel.registerSuccessful.observe(
			viewLifecycleOwner,
			Observer { isSuccessful ->
				
				// If isSuccessful, tell the EmailSignupViewModel and reset the LiveData
				if (isSuccessful) {
					
					emailSignupViewModel.navigateToSubscribeFragment()
					
					dateOfBirthViewModel.notified()
					
				}
				
			})
		
		// Observe the emailAlreadyInUse LiveData and show an error, if true
		dateOfBirthViewModel.emailAlreadyInUse.observe(
			viewLifecycleOwner,
			Observer { isUsed ->
				
				// If isUsed, show an error snackbar
				if (isUsed) {
					
					Util.showErrorSnackbar(
						binding.subscribeButton,
						getString(R.string.error_email_is_already_in_use)
					)
					
				}
				
			})
		
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
			DATE_PICKER_YEAR_INIT,
			DATE_PICKER_MONTH_INIT,
			DATE_PICKER_DAY_INIT,
			null
		)
		
	}
	
	/**
	 * Holds the initial values for the DatePicker.
	 * This is need so the init values are centralized,
	 * because they are also needed inside the [DateOfBirthViewModel],
	 * because the init values are null.
	 */
	companion object {
		
		const val DATE_PICKER_YEAR_INIT = 1980
		
		const val DATE_PICKER_MONTH_INIT = 0
		
		const val DATE_PICKER_DAY_INIT = 1
		
	}
}

