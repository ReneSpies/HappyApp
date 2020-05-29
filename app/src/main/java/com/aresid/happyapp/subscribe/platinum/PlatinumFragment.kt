package com.aresid.happyapp.subscribe.platinum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.LoadingStatus
import com.aresid.happyapp.billing.billingrepository.localdatabase.AugmentedSkuDetails
import com.aresid.happyapp.databinding.FragmentPlatinumBinding
import com.aresid.happyapp.subscribe.SubscribeViewModel
import com.aresid.happyapp.utils.Util.disableLoading
import com.aresid.happyapp.utils.Util.enableLoading
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
	
	// SubscribeViewModel
	private val subscribeViewModel: SubscribeViewModel by activityViewModels()
	
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
		
		// Tell the binding about the ViewModel
		binding.viewModel = platinumViewModel
		
		// Tell the binding about the activity
		binding.activity = requireActivity()
		
		// Observe the subscriptionSkuDetailsListLiveData
		platinumViewModel.subscriptionSkuDetailsListLiveData.observe(viewLifecycleOwner,
		                                                             Observer { list ->
			
			                                                             Timber.d("subscriptionSkuDetailsListLiveData observer called")
			
			                                                             if (list.isNotEmpty()) {
				
				                                                             // Take the SkuDetails and populate the View with its information
				                                                             populatePlatinumContent(platinumViewModel.getSubscriptionSkuDetails()!!)
				
				                                                             // Show the goldContent
				                                                             showContent()
				
			                                                             }
		                                                             })
		
		// Observe the toggleLoadingScreen LiveData
		platinumViewModel.toggleLoadingScreen.observe(viewLifecycleOwner,
		                                              Observer { status ->
			
			                                              when (status) {
				
				                                              LoadingStatus.INIT -> {
				                                              }
				
				                                              LoadingStatus.IDLE -> {
					
					                                              populatePlatinumContent(platinumViewModel.getSubscriptionSkuDetails()!!)
					
					                                              showContent()
					
				                                              }
				
				                                              LoadingStatus.LOADING -> showLoadingSpinner()
				
				                                              LoadingStatus.SUCCESS -> {
					
					                                              subscribeViewModel.navigateToMainFragment.value = true
					
				                                              }
				
				                                              LoadingStatus.ERROR_USER_DELETED -> {
				                                              }
				
				                                              LoadingStatus.ERROR_NO_INTERNET -> {
				                                              }
				
				                                              LoadingStatus.ERROR_NOT_SUBSCRIBED -> {
				                                              }
				
				                                              LoadingStatus.ERROR_CARD_DECLINED -> {
					
					                                              // Google already shows an error saying the card has been declined
					
					                                              // Show the content again
					                                              showContent()
					
				                                              }
				
			                                              }
			
		                                              })
		
		// Return the inflated layout
		return binding.root
		
	}
	
	override fun onResume() {
		
		Timber.d("onResume: called")
		
		super.onResume()
		
		// If the loadingSpinner is visible, start the loading animation again
		if (binding.loadingSpinner.visibility == View.VISIBLE) {
			
			// Start loading animation again
			binding.loadingSpinner.enableLoading()
			
		}
		
	}
	
	override fun onStop() {
		
		Timber.d("onStop: called")
		
		super.onStop()
		
		// Stop the loading animation
		binding.loadingSpinner.enableLoading()
		
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
	 * disables the loadingSpinner and errorFragment
	 * visibility.
	 */
	private fun showContent() {
		
		Timber.d("showPlatinumContent: called")
		
		// Disable the loadingFragment visibility
		binding.loadingSpinner.visibility = View.GONE
		
		// Disable the loading animation
		binding.loadingSpinner.disableLoading()
		
		// Enable the platinumContent visibility
		binding.platinumContent.visibility = View.VISIBLE
		
	}
	
	/**
	 * Enables the loadingSpinner visibility and
	 * disables the errorFragment and platinumContent
	 * visibility.
	 */
	private fun showLoadingSpinner() {
		
		Timber.d("showLoadingSpinner: called")
		
		// Disable the platinumContent visibility
		binding.platinumContent.visibility = View.GONE
		
		// Enable the loading animation
		binding.loadingSpinner.enableLoading()
		
		// Enable the loadingSpinner visibility
		binding.loadingSpinner.visibility = View.VISIBLE
		
	}
	
}