package com.aresid.happyapp.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentSubscribeBinding
import com.aresid.happyapp.subscribe.viewpager2.SubscriptionStateAdapter
import com.aresid.happyapp.subscribe.viewpager2.ZoomOutPageTransformer
import com.aresid.happyapp.utils.LoadingStatus
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
	private lateinit var binding: FragmentSubscribeBinding
	
	// ViewModel
	private val subscribeFragmentViewModel: SubscribeViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding
		binding = FragmentSubscribeBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Init ViewPager2 and it's TabLayout
		initViewPager2AndTabLayout()
		
		// Observe the navigateToMainFragment LiveData
		subscribeFragmentViewModel.navigateToMainFragment.observe(viewLifecycleOwner,
		                                                          Observer { navigate ->
			
			                                                          // If navigate, navigate
			                                                          if (navigate) {
				
				                                                          // Navigate
				                                                          navigateToMainFragment()
				
				                                                          // Reset the LiveData
				                                                          subscribeFragmentViewModel.navigated()
				
			                                                          }
			
		                                                          })
		
		// Observe the toggleLoading LiveData
		subscribeFragmentViewModel.toggleLoading.observe(
			viewLifecycleOwner,
			Observer { status ->
				
				when (status) {
					
					LoadingStatus.IDLE -> showContent()
					
					LoadingStatus.LOADING -> showLoading()
					
					LoadingStatus.SUCCESS -> {
						
						navigateToMainFragment()
						
						subscribeFragmentViewModel.navigated()
						
					}
					
					LoadingStatus.FAILURE -> showContent()
					
				}
				
			})
		
		// Return the inflated layout
		return binding.root
		
	}
	
	private fun showLoading() {
		
		Timber.d("showLoading: called")
		
		// Disable the content
		binding.content.visibility = View.GONE
		
		// Show the loading screen
		binding.loading.visibility = View.VISIBLE
		
	}
	
	private fun showContent() {
		
		Timber.d("showContent: called")
		
		// Hide the loading screen
		binding.loading.visibility = View.GONE
		
		// Show the content
		binding.content.visibility = View.VISIBLE
		
		Timber.d("loading visibility = ${binding.loading.visibility}") // Prints 8 for View.GONE
		Timber.d("content visibility = ${binding.content.visibility}") // Prints 0 for View.VISIBLE
		
	}
	
	private fun navigateToMainFragment() {
		
		Timber.d("navigateToMainFragment: called")
		
		// Navigate to MainFragment
		findNavController(this).navigate(SubscribeFragmentDirections.toMainFragment())
		
	}
	
	/**
	 * Initializes the ViewPager2 and bounds the TabLayout to it.
	 */
	private fun initViewPager2AndTabLayout() {
		
		Timber.d("initViewPager2AndTabLayout: called")
		
		// Define the subscriptionStateAdapter object
		val subscriptionStateAdapter = SubscriptionStateAdapter(this)
		
		// Define the ViewPager2 from the layout file
		val viewPager2 = binding.viewPager2
		
		// Set the adapter
		viewPager2.adapter = subscriptionStateAdapter
		
		// Define the TabLayout from the layout file
		val tabLayout = binding.tabLayout
		
		// Initialize the TabLayout and bind it to the ViewPager2
		TabLayoutMediator(
			tabLayout,
			viewPager2
		) { tab, position ->
			
			Timber.d("TabLayoutMediator lambda")
			
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