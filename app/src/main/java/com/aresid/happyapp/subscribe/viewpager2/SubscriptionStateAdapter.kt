package com.aresid.happyapp.subscribe.viewpager2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aresid.happyapp.subscribe.bronze.BronzeContainerFragment
import com.aresid.happyapp.subscribe.gold.GoldContainerFragment
import com.aresid.happyapp.subscribe.platinum.PlatinumContainerFragment
import com.aresid.happyapp.subscribe.silver.SilverContainerFragment

/**
 *    Created on: 16.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SubscriptionStateAdapter(
	fragment: Fragment
): FragmentStateAdapter(fragment) {
	
	private val fragments = arrayListOf(
		BronzeContainerFragment(),
		SilverContainerFragment(),
		GoldContainerFragment(),
		PlatinumContainerFragment()
	)
	
	override fun getItemCount(): Int = fragments.size
	
	override fun createFragment(position: Int): Fragment {
		
		return fragments[position]
		
	}
	
}