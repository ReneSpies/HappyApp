package com.aresid.happyapp.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.happyapp.R
import com.aresid.happyapp.databases.billingdatabase.AugmentedSkuDetails
import com.aresid.happyapp.databinding.FragmentSubscribeBinding
import com.aresid.happyapp.subscribe.viewpager2.SubscriptionViewPagerAdapter
import com.aresid.happyapp.subscribe.viewpager2.ZoomOutPageTransformer
import com.aresid.happyapp.utils.Util
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class SubscribeFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var mBinding: FragmentSubscribeBinding
	
	// Corresponding ViewModel
	private lateinit var mSubscribeViewModel: SubscribeViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding
		mBinding = FragmentSubscribeBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		mSubscribeViewModel = ViewModelProvider(this).get(SubscribeViewModel::class.java)
		
		// Observe the subscriptionSkuDetailsList LiveData to populate the content
		mSubscribeViewModel.subscriptionSkuDetailsList.observe(viewLifecycleOwner,
		                                                       Observer { subscriptions ->
			
			                                                       if (subscriptions != null) {
				
				                                                       // Init ViewPager2 and it's TabLayout
				                                                       initViewPager2AndTabLayout(subscriptions.sortedBy {
					
					                                                       it.priceMicros
					
				                                                       })
				
			                                                       }
			
		                                                       })
		
		// Observe the toggleLoading LiveData to toggle the loading screen
		mSubscribeViewModel.toggleLoading.observe(viewLifecycleOwner,
		                                          Observer { status ->
			
			                                          when (status) {
				
				                                          SubscribeLoadingStatus.INIT -> {
				                                          }
				
				                                          SubscribeLoadingStatus.IDLE -> showContent()
				
				                                          SubscribeLoadingStatus.LOADING -> showLoading()
				
				                                          SubscribeLoadingStatus.SUCCESS -> TODO("Navigate to MainFragment")
				
				                                          SubscribeLoadingStatus.FAILURE -> showContent()
				
				                                          SubscribeLoadingStatus.ERROR_NO_INTERNET -> {
					
					                                          // Show an error snackbar
					                                          Util.showErrorSnackbar(
						                                          mBinding.content,
						                                          getString(R.string.error_no_internet_connection)
					                                          )
					
					                                          // Show the content
					                                          showContent()
					
				                                          }
				
				                                          else -> {
				                                          }
				
			                                          }
			
		                                          })
		
		// Return the inflated layout
		return mBinding.root
		
	}
	
	/**
	 * Hides the content Group and shows the loading Group.
	 */
	private fun showLoading() {
		
		Timber.d("showLoading: called")
		
		// Hide the content
		mBinding.content.visibility = View.GONE
		
		// Show the loading
		mBinding.loading.visibility = View.VISIBLE
		
	}
	
	/**
	 * Hides the loading Group and shows the content Group.
	 */
	private fun showContent() {
		
		Timber.d("showContent: called")
		
		// Hide the loading
		mBinding.loading.visibility = View.GONE
		
		// Show the content
		mBinding.content.visibility = View.VISIBLE
		
	}
	
	/**
	 * Initializes the ViewPager2 and bounds the TabLayout to it.
	 */
	private fun initViewPager2AndTabLayout(subscriptions: List<AugmentedSkuDetails>) {
		
		Timber.d("initViewPager2AndTabLayout: called")
		
		// Define the subscriptionStateAdapter object
		val subscriptionStateAdapter = SubscriptionViewPagerAdapter(subscriptions) { position ->
			
			// Pass the position through to the SubscribeViewModel
			mSubscribeViewModel.onCheckoutButtonClicked(
				position,
				subscriptions,
				requireActivity()
			)
			
		}
		
		// Define the ViewPager2 from the layout file
		val viewPager2 = mBinding.viewPager2
		
		// Set the adapter
		viewPager2.adapter = subscriptionStateAdapter
		
		// Define the TabLayout from the layout file
		val tabLayout = mBinding.tabLayout
		
		// Initialize the TabLayout and bind it to the ViewPager2
		TabLayoutMediator(
			tabLayout,
			viewPager2
		) { tab, position ->
			
			// Name the Tabs
			tab.text = when (position) {
				
				// Bronze
				0 -> getString(R.string.bronze)
				
				// Silver
				1 -> getString(R.string.silver)
				
				// Gold
				2 -> getString(R.string.gold)
				
				// Platinum
				3 -> getString(R.string.platinum)
				
				// Should not happen
				else -> "404"
				
			}
			
		}.attach()
		
		// Set the pager transformer
		viewPager2.setPageTransformer(ZoomOutPageTransformer())
		
	}
	
}