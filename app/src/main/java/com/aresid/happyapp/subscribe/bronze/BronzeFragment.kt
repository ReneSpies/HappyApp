package com.aresid.happyapp.subscribe.bronze

import android.graphics.drawable.AnimatedVectorDrawable
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
		
		// Tell the binding about the ViewModel
		binding.viewModel = bronzeViewModel
		
		// Tell the binding about the Activity
		binding.activity = requireActivity()
		
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
	
	override fun onResume() {
		
		Timber.d("onResume: called")
		
		super.onResume()
		
		// If the loadingSpinner is visible, start the loading animation again
		if (binding.loadingSpinner.visibility == View.VISIBLE) {
			
			// Start loading animation again
			startLoadingAnimation()
			
		}
		
	}
	
	override fun onStop() {
		
		Timber.d("onStop: called")
		
		super.onStop()
		
		// Stop the loading animation
		stopLoadingAnimation()
		
	}
	
	/**
	 * Starts the loading animation on the ImageView's drawable.
	 */
	private fun startLoadingAnimation() {
		
		Timber.d("startLoadingAnimation: called")
		
		(binding.loadingSpinner.drawable as AnimatedVectorDrawable).start()
		
	}
	
	/**
	 * Stops the loading animation on the ImageView's drawable.
	 */
	private fun stopLoadingAnimation() {
		
		Timber.d("stopLoadingAnimation: called")
		
		(binding.loadingSpinner.drawable as AnimatedVectorDrawable).stop()
		
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
	private fun showBronzeContent() {
		
		Timber.d("showBronzeContent: called")
		
		// Disable the loadingSpinner visibility
		binding.loadingSpinner.visibility = View.GONE
		
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
		
		// Enable the loadingSpinner visibility
		binding.loadingSpinner.visibility = View.VISIBLE
		
	}
	
	/**
	 * Enables the errorFragment visibility and
	 * disables the loadingSpinner and bronzeContent
	 * visibility.
	 */
	private fun showErrorFragment() {
		
		Timber.d("showErrorFragment: called")
		
		// Disable the loadingSpinner visibility
		binding.loadingSpinner.visibility = View.GONE
		
		// Disable the bronzeContent visibility
		binding.bronzeContent.visibility = View.GONE
		
	}
	
}