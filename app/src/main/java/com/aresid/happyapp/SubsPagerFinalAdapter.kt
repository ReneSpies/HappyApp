package com.aresid.happyapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton

/**
 * Created on: 26.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class SubsPagerFinalAdapter internal constructor(
	context: Context,
	pool: SubscriptionPool
): RecyclerView.Adapter<SubsPagerFinalAdapter.ViewHolder>(), View.OnClickListener {
	
	private val mContext: Context
	private val mSubscriptionPool: SubscriptionPool
	private var mListener: OnFinalAdapterInteractionListener? = null
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ViewHolder {
		Log.d(
			TAG,
			"onCreateViewHolder: called"
		)
		val view = LayoutInflater.from(mContext).inflate(
			R.layout.content_subspagerfinaladapter,
			parent,
			false
		)
		val confirmButton: MaterialButton = view.findViewById(R.id.view_pager_bt_confirm)
		confirmButton.setOnClickListener(this)
		return ViewHolder(view)
	}
	
	override fun onBindViewHolder(
		holder: ViewHolder,
		position: Int
	) {
		Log.d(
			TAG,
			"onBindViewHolder: called"
		)
		val title = mSubscriptionPool.getSubscription(position).title
		val description = mSubscriptionPool.getSubscription(position).description
		val price = mSubscriptionPool.getSubscription(position).price
		val icon = mSubscriptionPool.getSubscription(position).icon
		holder.mTVTitle.text = title
		holder.mTVDescription.text = description
		holder.mTVPrice.text = price
		holder.mSubIcon.setImageDrawable(icon)
	}
	
	override fun getItemCount(): Int {
		Log.d(
			TAG,
			"getItemCount: called"
		)
		return mSubscriptionPool.subscriptionCount
	}
	
	override fun onClick(v: View) {
		Log.d(
			TAG,
			"onClick: called"
		)
		if (v.id == R.id.view_pager_bt_confirm) {
			Log.d(
				TAG,
				"onClick: confirm button click"
			)
			val viewPager2 = v.parent.parent.parent as ViewPager2
			mListener!!.startRegistrationFlow(mSubscriptionPool.getSubscription(viewPager2.currentItem))
		}
	}
	
	val subscriptionPool: SubscriptionPool
		get() {
			Log.d(
				TAG,
				"getSubscriptionPool: called"
			)
			return mSubscriptionPool
		}
	
	interface OnFinalAdapterInteractionListener {
		fun startRegistrationFlow(subscription: Subscription?)
	}
	
	inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
		var mTVTitle: TextView
		var mTVDescription: TextView
		var mTVPrice: TextView
		var mSubIcon: ImageView
		
		init {
			mTVTitle = itemView.findViewById(R.id.view_pager_tv_title)
			mTVDescription = itemView.findViewById(R.id.view_pager_tv_description)
			mTVPrice = itemView.findViewById(R.id.view_pager_tv_price)
			mSubIcon = itemView.findViewById(R.id.view_pager_subscription_icon)
		}
	}
	
	companion object {
		private const val TAG = "SubsPagerFinalAdapter"
	}
	
	init {
		Log.d(
			TAG,
			"SubsPagerFinalAdapter: called"
		)
		mSubscriptionPool = pool
		mContext = context
		mListener = if (mContext is OnFinalAdapterInteractionListener) {
			mContext
		}
		else {
			throw RuntimeException(
				"$mContext must implement OnFinalAdapterInteractionListener"
			)
		}
	}
}