package com.aresid.happyapp.subscribe.silver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.billing.billingrepository.localdatabase.AugmentedSkuDetails
import com.aresid.happyapp.databinding.FragmentSilverBinding
import com.aresid.happyapp.subscribe.SubscribeViewModel
import com.aresid.happyapp.utils.Util.underline
import timber.log.Timber

/**
 *    Created on: 16.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SilverFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentSilverBinding
	
	// Corresponding ViewModel
	private lateinit var silverViewModel: SilverViewModel
	
	// SubscribeViewModel
	private val subscribeViewModel: SubscribeViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentSilverBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		silverViewModel = ViewModelProvider(this).get(SilverViewModel::class.java)
		
		// Tell the binding about the ViewModel
		binding.viewModel = silverViewModel
		
		// Tell the binding about the activity
		binding.activity = requireActivity()
		
		// Observe the toggleLoadingScreen LiveData
		silverViewModel.toggleLoadingScreen.observe(viewLifecycleOwner,
		                                            Observer { status ->
			
			                                            subscribeViewModel.toggleLoading.value = status
			
		                                            })
		
		// Observe the subscriptionSkuDetailsListLiveData
		silverViewModel.subscriptionSkuDetailsListLiveData.observe(viewLifecycleOwner,
		                                                           Observer { list ->
			
			                                                           Timber.d("subscriptionSkuDetailsListLiveData observer called")
			
			                                                           if (list.isNotEmpty()) {
				
				                                                           // Take the SkuDetails and populate the View with its information
				                                                           populateContent(silverViewModel.getSubscriptionSkuDetails()!!)
				
				                                                           // Show the goldContent
				                                                           showContent()
				
			                                                           }
		                                                           })
		
		// Return the inflated layout
		return binding.root
		
	}
	
	/**
	 * Unpacks the information in the [augmentedSkuDetails] and puts it into the layout.
	 * Also underlines the priceText.
	 */
	private fun populateContent(augmentedSkuDetails: AugmentedSkuDetails) {
		
		Timber.d("populateSilverContent: called")
		
		// Title does not have to be set, since it is static
		
		// Set the description
		binding.descriptionText.text = augmentedSkuDetails.description
		
		// Underline the priceText
		binding.priceText.underline()
		
		// Set the price
		binding.priceText.text = augmentedSkuDetails.price
		
	}
	
	/**
	 * Enables the silverContent visibility and
	 * disables the loadingSpinner and errorFragment
	 * visibility.
	 */
	private fun showContent() {
		
		Timber.d("showSilverContent: called")
		
		// Disable the loadingSpinner visibility
		binding.loading.visibility = View.GONE
		
		// Enable the silverContent visibility
		binding.content.visibility = View.VISIBLE
		
	}
	
}