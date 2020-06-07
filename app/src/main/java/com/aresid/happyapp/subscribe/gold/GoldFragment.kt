package com.aresid.happyapp.subscribe.gold

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.billing.billingrepository.localdatabase.AugmentedSkuDetails
import com.aresid.happyapp.databinding.FragmentGoldBinding
import com.aresid.happyapp.subscribe.SubscribeViewModel
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
	
	// SubscribeViewModel
	private val subscribeViewModel: SubscribeViewModel by activityViewModels()
	
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
		
		// Tell the binding about the ViewModel
		binding.viewModel = goldViewModel
		
		// Tell the binding about the activity
		binding.activity = requireActivity()
		
		// Observe the toggleLoadingScreen LiveData
		goldViewModel.toggleLoadingScreen.observe(viewLifecycleOwner,
		                                          Observer { status ->
			
			                                          subscribeViewModel.toggleLoading.value = status
			
		                                          })
		
		// Observe the subscriptionSkuDetailsListLiveData
		goldViewModel.subscriptionSkuDetailsListLiveData.observe(viewLifecycleOwner,
		                                                         Observer { list ->
			
			                                                         Timber.d("subscriptionSkuDetailsListLiveData observer called")
			
			                                                         if (list.isNotEmpty()) {
				
				                                                         // Take the SkuDetails and populate the View with its information
				                                                         populateContent(goldViewModel.getSubscriptionSkuDetails()!!)
				
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
	 * disables the loadingSpinner and errorFragment
	 * visibility.
	 */
	private fun showContent() {
		
		Timber.d("showGoldContent: called")
		
		// Disable the loadingSpinner visibility
		binding.loading.visibility = View.GONE
		
		// Enable the goldContent visibility
		binding.content.visibility = View.VISIBLE
		
	}
	
}