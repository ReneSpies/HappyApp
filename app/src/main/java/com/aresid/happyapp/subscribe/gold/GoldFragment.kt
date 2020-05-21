package com.aresid.happyapp.subscribe.gold

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.billing.billingrepository.localdatabase.AugmentedSkuDetails
import com.aresid.happyapp.databinding.FragmentGoldBinding
import com.aresid.happyapp.utils.Util.underline
import timber.log.Timber

/**
 *    Created on: 16.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class GoldFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentGoldBinding
	
	// Corresponding ViewModel
	private lateinit var goldViewModel: GoldViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentGoldBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		goldViewModel = ViewModelProvider(this).get(GoldViewModel::class.java)
		
		// Observe the subscriptionSkuDetailsListLiveData
		goldViewModel.subscriptionSkuDetailsListLiveData.observe(viewLifecycleOwner,
		                                                         Observer { list ->
			
			                                                         Timber.d("subscriptionSkuDetailsListLiveData observer called")
			
			                                                         if (list.isNotEmpty()) {
				
				                                                         // Take the SkuDetails and populate the View with its information
				                                                         populateGoldContent(goldViewModel.getSubscriptionSkuDetails()!!)
				
				                                                         // Show the goldContent
				                                                         showGoldContent()
				
			                                                         }
		                                                         })
		
		// Return the inflated layout
		return binding.root
		
	}
	
	/**
	 * Unpacks the information in the [augmentedSkuDetails] and puts it into the layout.
	 * Also underlines the priceText.
	 */
	private fun populateGoldContent(augmentedSkuDetails: AugmentedSkuDetails) {
		
		Timber.d("populateGoldContent: called")
		
		// Title does not have to be set, since it is static
		
		// Set the description
		binding.descriptionText.text = augmentedSkuDetails.description
		
		// Underline the priceText
		binding.priceText.underline()
		
		// Set the price
		binding.priceText.text = augmentedSkuDetails.price
		
	}
	
	/**
	 * Enables the goldContent visibility and
	 * disables the loadingFragment and errorFragment
	 * visibility.
	 */
	private fun showGoldContent() {
		
		Timber.d("showGoldContent: called")
		
		// Disable the loadingFragment visibility
		binding.loadingFragment.visibility = View.GONE
		
		// Disable the errorFragment visibility
		binding.errorFragment.visibility = View.GONE
		
		// Enable the goldContent visibility
		binding.goldContent.visibility = View.VISIBLE
		
	}
	
	/**
	 * Enables the loadingFragment visibility and
	 * disables the errorFragment and goldContent
	 * visibility.
	 */
	private fun showLoadingFragment() {
		
		Timber.d("showLoadingFragment: called")
		
		// Disable the errorFragment visibility
		binding.errorFragment.visibility = View.GONE
		
		// Disable the goldContent visibility
		binding.goldContent.visibility = View.GONE
		
		// Enable the loadingFragment visibility
		binding.loadingFragment.visibility = View.VISIBLE
		
	}
	
	/**
	 * Enables the errorFragment visibility and
	 * disables the loadingFragment and goldContent
	 * visibility.
	 */
	private fun showErrorFragment() {
		
		Timber.d("showErrorFragment: called")
		
		// Disable the loadingFragment visibility
		binding.loadingFragment.visibility = View.GONE
		
		// Disable the goldContent visibility
		binding.goldContent.visibility = View.GONE
		
		// Enable the errorFragment visibility
		binding.errorFragment.visibility = View.VISIBLE
		
	}
	
}