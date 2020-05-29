package com.aresid.happyapp.subscribe.bronze

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
import com.aresid.happyapp.databinding.FragmentBronzeBinding
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
		
		// Tell the binding about the Activity
		binding.activity = requireActivity()
		
		// Observe the toggleLoadingScreen LiveData
		bronzeViewModel.toggleLoadingScreen.observe(viewLifecycleOwner,
		                                            Observer { status ->
			
			                                            when (status) {
				
				                                            LoadingStatus.INIT -> {
				                                            }
				
				                                            LoadingStatus.IDLE -> {
					
					                                            populateBronzeContent(bronzeViewModel.getSubscriptionSkuDetails()!!)
					
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
		
		// Observe the subscriptionSkuDetailsList LiveData
		bronzeViewModel.subscriptionSkuDetailsList.observe(viewLifecycleOwner,
		                                                   Observer { list ->
			
			                                                   if (list.isNotEmpty()) {
				
				                                                   Timber.d("list is not empty")
				
				                                                   // Take the SkuDetails and populate the View with its information
				                                                   populateBronzeContent(bronzeViewModel.getSubscriptionSkuDetails()!!)
				
				                                                   // Show the bronzeContent in the layout
				                                                   showContent()
				
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
		binding.loadingSpinner.disableLoading()
		
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
	 * disables the loadingSpinner and errorFragment
	 * visibility.
	 */
	private fun showContent() {
		
		Timber.d("showBronzeContent: called")
		
		// Disable the loadingSpinner visibility
		binding.loadingSpinner.visibility = View.GONE
		
		// Stop the loading animation
		binding.loadingSpinner.disableLoading()
		
		// Enable the bronzeContent visibility
		binding.bronzeContent.visibility = View.VISIBLE
		
	}
	
	/**
	 * Enables the loadingSpinner visibility and
	 * disables the errorFragment and bronzeContent
	 * visibility.
	 */
	private fun showLoadingSpinner() {
		
		Timber.d("showLoadingSpinner: called")
		
		// Disable the bronzeContent visibility
		binding.bronzeContent.visibility = View.GONE
		
		// Start the loading animation
		binding.loadingSpinner.enableLoading()
		
		// Enable the loadingSpinner visibility
		binding.loadingSpinner.visibility = View.VISIBLE
		
	}
	
}