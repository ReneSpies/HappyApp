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
import com.aresid.happyapp.utils.LoadingStatus
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
			
			                                            when (status) {
				
				                                            LoadingStatus.INIT -> {
				                                            }
				
				                                            LoadingStatus.IDLE -> {
					
					                                            populateSilverContent(silverViewModel.getSubscriptionSkuDetails()!!)
					
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
		
		// Observe the subscriptionSkuDetailsListLiveData
		silverViewModel.subscriptionSkuDetailsListLiveData.observe(
			viewLifecycleOwner,
			Observer { list ->
				
				Timber.d("subscriptionSkuDetailsListLiveData observer called")
				
				if (list.isNotEmpty()) {
					
					// Take the SkuDetails and populate the View with its information
					populateSilverContent(silverViewModel.getSubscriptionSkuDetails()!!)
					
					// Show the goldContent
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
		binding.loadingSpinner.enableLoading()
		
	}
	
	/**
	 * Unpacks the information in the [augmentedSkuDetails] and puts it into the layout.
	 * Also underlines the priceText.
	 */
	private fun populateSilverContent(augmentedSkuDetails: AugmentedSkuDetails) {
		
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
		binding.loadingSpinner.visibility = View.GONE
		
		// Disable the loading animation
		binding.loadingSpinner.disableLoading()
		
		// Enable the silverContent visibility
		binding.silverContent.visibility = View.VISIBLE
		
	}
	
	/**
	 * Enables the loadingSpinner visibility and
	 * disables the errorFragment and silverContent
	 * visibility.
	 */
	private fun showLoadingSpinner() {
		
		Timber.d("showLoadingSpinner: called")
		
		// Disable the silverContent visibility
		binding.silverContent.visibility = View.GONE
		
		// Enable the loading animation
		binding.loadingSpinner.enableLoading()
		
		// Enable the loadingSpinner visibility
		binding.loadingSpinner.visibility = View.VISIBLE
		
	}
	
}