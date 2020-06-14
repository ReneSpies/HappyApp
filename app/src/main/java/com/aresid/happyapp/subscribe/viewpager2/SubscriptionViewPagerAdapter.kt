package com.aresid.happyapp.subscribe.viewpager2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aresid.happyapp.R
import com.aresid.happyapp.databases.billingdatabase.AugmentedSkuDetails
import com.aresid.happyapp.databinding.SubscribeItemViewPagerBinding
import com.aresid.happyapp.utils.Util.underline
import timber.log.Timber

/**
 *    Created on: 10.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SubscriptionViewPagerAdapter(
	private val mSubscriptions: List<AugmentedSkuDetails>,
	private val itemClickListener: (Int) -> (Unit)
): RecyclerView.Adapter<SubscriptionViewPagerAdapter.SubscriptionViewPagerViewHolder>() {
	
	// Binding for the layout
	private lateinit var binding: SubscribeItemViewPagerBinding
	
	inner class SubscriptionViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
	
	// A static list holding the logos from drawable resources
	private val mLogos = listOf(
		
		R.drawable.bronze_logo,
		
		R.drawable.silver_logo,
		
		R.drawable.gold_logo,
		
		R.drawable.platinum_logo
	
	)
	
	// A static list holding the titles from string resources
	private val mTitles = listOf(
		
		R.string.happyapp_bronze,
		
		R.string.happyapp_silver,
		
		R.string.happyapp_gold,
		
		R.string.happyapp_platinum
	
	)
	
	// A static list holding the colors from color resources
	private val mColors = listOf(
		
		R.color.bronzeColor,
		
		R.color.silverColor,
		
		R.color.goldColor,
		
		R.color.platinumColor
	
	)
	
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): SubscriptionViewPagerViewHolder {
		
		Timber.d("onCreateViewHolder: called")
		
		// Inflate the layout and define the binding
		binding = SubscribeItemViewPagerBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		
		// Return the ViewHolder
		return SubscriptionViewPagerViewHolder(binding.root)
		
	}
	
	override fun getItemCount(): Int = mSubscriptions.size
	
	override fun onBindViewHolder(
		holder: SubscriptionViewPagerViewHolder,
		position: Int
	) {
		
		Timber.d("onBindViewHolder: called")
		
		// Get the current subscription
		val subscription = mSubscriptions[position]
		
		// Set the buttons OnClickListener
		binding.checkoutButton.setOnClickListener {
			
			// Call the lambda passed in the constructor
			itemClickListener(position)
			
		}
		
		// Set the logo according to the current position
		binding.logo.setImageResource(mLogos[position])
		
		// Set the titleText according to the current position
		binding.titleText.text = holder.itemView.context.getString(mTitles[position])
		
		// Set the descriptionText according to the current position
		binding.descriptionText.text = subscription.description
		
		// Underline the priceText
		binding.priceText.underline()
		
		// Colorize the priceText
		binding.priceText.setTextColor(
			ContextCompat.getColor(
				holder.itemView.context,
				mColors[position]
			)
		)
		
		// Set the priceText according to the current position
		binding.priceText.text = subscription.price
		
	}
	
}