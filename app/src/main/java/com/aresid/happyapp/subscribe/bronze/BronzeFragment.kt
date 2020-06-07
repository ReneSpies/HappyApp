package com.aresid.happyapp.subscribe.bronze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.billing.billingrepository.localdatabase.AugmentedSkuDetails
import com.aresid.happyapp.databinding.FragmentBronzeBinding
import com.aresid.happyapp.subscribe.SubscribeViewModel
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
	
	// SubscribeViewModel
	private val subscribeViewModel: SubscribeViewModel by activityViewModels()
	
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
		
		// Tell the binding about the ViewModel
		binding.viewModel = bronzeViewModel
		
		// Tell the binding about the activity
		binding.activity = requireActivity()
		
		// Observe the toggleLoadingScreen LiveData
		bronzeViewModel.toggleLoadingScreen.observe(viewLifecycleOwner,
		                                            Observer { status ->
			
			                                            subscribeViewModel.toggleLoading.value = status
			
		                                            })
		
		// Observe the subscriptionSkuDetailsList LiveData
		bronzeViewModel.subscriptionSkuDetailsList.observe(viewLifecycleOwner,
		                                                   Observer { list ->
			
			                                                   if (list.isNotEmpty()) {
				
				                                                   Timber.d("list is not empty")
				
				                                                   // Take the SkuDetails and populate the View with its information
				                                                   populateContent(bronzeViewModel.getSubscriptionSkuDetails()!!)
				
				                                                   // Show the content
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
	 * Hides the loading and shows the content.
	 */
	private fun showContent() {
		
		Timber.d("showBronzeContent: called")
		
		// Hide the loading
		binding.loading.visibility = View.GONE
		
		// Show the content
		binding.content.visibility = View.VISIBLE
		
	}
	
}