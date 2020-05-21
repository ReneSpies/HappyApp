package com.aresid.happyapp.subscribe.bronze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.billing.billingrepository.localdatabase.AugmentedSkuDetails
import com.aresid.happyapp.databinding.FragmentBronzeBinding
import com.aresid.happyapp.utils.Util.underline
import timber.log.Timber

/**
 *    Created on: 16.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class BronzeFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentBronzeBinding
	
	// Corresponding ViewModel
	private lateinit var bronzeViewModel: BronzeViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentBronzeBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		bronzeViewModel = ViewModelProvider(this).get(BronzeViewModel::class.java)
		
		// Observe the subscriptionSkuDetailsListLiveData
		bronzeViewModel.subscriptionSkuDetailsListLiveData.observe(
			viewLifecycleOwner,
			Observer { list ->
				
				if (list.isNotEmpty()) {
					
					// Take the SkuDetails and populate the View with its information
					populateBronzeContent(bronzeViewModel.getSubscriptionSkuDetails()!!)
					
					// Show the bronzeContent in the layout
					showBronzeContent()
					
				}
				
			})
		
		// Return the inflated layout
		return binding.root
		
	}
	
	/**
	 * Unpacks the information in the [augmentedSkuDetails] and puts it into the layout.
	 * Also underlines the priceText.
	 */
	private fun populateBronzeContent(augmentedSkuDetails: AugmentedSkuDetails) {
		
		Timber.d("populateBronzeContent: called")
		
		// Title does not have to be set, since it is static
		
		// Set the description
		binding.descriptionText.text = augmentedSkuDetails.description
		
		// Underline the priceText
		binding.priceText.underline()
		
		// Set the price
		binding.priceText.text = augmentedSkuDetails.price
		
	}
	
	/**
	 * Enables the bronzeContent visibility and
	 * disables the loadingFragment and errorFragment
	 * visibility.
	 */
	private fun showBronzeContent() {
		
		Timber.d("showBronzeContent: called")
		
		// Disable the loadingFragment visibility
		binding.loadingFragment.visibility = View.GONE
		
		// Disable the errorFragment visibility
		binding.errorFragment.visibility = View.GONE
		
		// Enable the bronzeContent visibility
		binding.bronzeContent.visibility = View.VISIBLE
		
	}
	
	/**
	 * Enables the loadingFragment visibility and
	 * disables the errorFragment and bronzeContent
	 * visibility.
	 */
	private fun showLoadingFragment() {
		
		Timber.d("showLoadingFragment: called")
		
		// Disable the errorFragment visibility
		binding.errorFragment.visibility = View.GONE
		
		// Disable the bronzeContent visibility
		binding.bronzeContent.visibility = View.GONE
		
		// Enable the loadingFragment visibility
		binding.loadingFragment.visibility = View.VISIBLE
		
	}
	
	/**
	 * Enables the errorFragment visibility and
	 * disables the loadingFragment and bronzeContent
	 * visibility.
	 */
	private fun showErrorFragment() {
		
		Timber.d("showErrorFragment: called")
		
		// Disable the loadingFragment visibility
		binding.loadingFragment.visibility = View.GONE
		
		// Disable the bronzeContent visibility
		binding.bronzeContent.visibility = View.GONE
		
		// Enable the errorFragment visibility
		binding.errorFragment.visibility = View.VISIBLE
		
	}
	
}