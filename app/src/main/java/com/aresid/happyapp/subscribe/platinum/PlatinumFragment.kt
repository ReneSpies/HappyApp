package com.aresid.happyapp.subscribe.platinum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.billing.billingrepository.localdatabase.AugmentedSkuDetails
import com.aresid.happyapp.databinding.FragmentPlatinumBinding
import com.aresid.happyapp.utils.Util.underline
import timber.log.Timber

/**
 *    Created on: 16.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class PlatinumFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentPlatinumBinding
	
	// Corresponding ViewModel
	private lateinit var platinumViewModel: PlatinumViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentPlatinumBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		platinumViewModel = ViewModelProvider(this).get(PlatinumViewModel::class.java)
		
		// Observe the subscriptionSkuDetailsListLiveData
		platinumViewModel.subscriptionSkuDetailsListLiveData.observe(viewLifecycleOwner,
		                                                             Observer { list ->
			
			                                                             Timber.d("subscriptionSkuDetailsListLiveData observer called")
			
			                                                             if (list.isNotEmpty()) {
				
				                                                             // Take the SkuDetails and populate the View with its information
				                                                             populatePlatinumContent(platinumViewModel.getSubscriptionSkuDetails()!!)
				
				                                                             // Show the goldContent
				                                                             showPlatinumContent()
				
			                                                             }
		                                                             })
		
		// Return the inflated layout
		return binding.root
		
	}
	
	/**
	 * Unpacks the information in the [augmentedSkuDetails] and puts it into the layout.
	 * Also underlines the priceText.
	 */
	private fun populatePlatinumContent(augmentedSkuDetails: AugmentedSkuDetails) {
		
		Timber.d("populatePlatinumContent: called")
		
		// Title does not have to be set, since it is static
		
		// Set the description
		binding.descriptionText.text = augmentedSkuDetails.description
		
		// Underline the priceText
		binding.priceText.underline()
		
		// Set the price
		binding.priceText.text = augmentedSkuDetails.price
		
	}
	
	/**
	 * Enables the platinumContent visibility and
	 * disables the loadingFragment and errorFragment
	 * visibility.
	 */
	private fun showPlatinumContent() {
		
		Timber.d("showPlatinumContent: called")
		
		// Disable the loadingFragment visibility
		binding.loadingFragment.visibility = View.GONE
		
		// Disable the errorFragment visibility
		binding.errorFragment.visibility = View.GONE
		
		// Enable the platinumContent visibility
		binding.platinumContent.visibility = View.VISIBLE
		
	}
	
	/**
	 * Enables the loadingFragment visibility and
	 * disables the errorFragment and platinumContent
	 * visibility.
	 */
	private fun showLoadingFragment() {
		
		Timber.d("showLoadingFragment: called")
		
		// Disable the errorFragment visibility
		binding.errorFragment.visibility = View.GONE
		
		// Disable the platinumContent visibility
		binding.platinumContent.visibility = View.GONE
		
		// Enable the loadingFragment visibility
		binding.loadingFragment.visibility = View.VISIBLE
		
	}
	
	/**
	 * Enables the errorFragment visibility and
	 * disables the loadingFragment and platinumContent
	 * visibility.
	 */
	private fun showErrorFragment() {
		
		Timber.d("showErrorFragment: called")
		
		// Disable the loadingFragment visibility
		binding.loadingFragment.visibility = View.GONE
		
		// Disable the platinumContent visibility
		binding.platinumContent.visibility = View.GONE
		
		// Enable the errorFragment visibility
		binding.errorFragment.visibility = View.VISIBLE
		
	}
	
}