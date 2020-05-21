package com.aresid.happyapp.subscribe.viewpager2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aresid.happyapp.subscribe.bronze.BronzeFragment
import com.aresid.happyapp.subscribe.gold.GoldFragment
import com.aresid.happyapp.subscribe.platinum.PlatinumFragment
import com.aresid.happyapp.subscribe.silver.SilverFragment

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
		BronzeFragment(),
		SilverFragment(),
		GoldFragment(),
		PlatinumFragment()
	)
	
	override fun getItemCount(): Int = fragments.size
	
	override fun createFragment(position: Int): Fragment {
		
		return fragments[position]
		
	}
	
}