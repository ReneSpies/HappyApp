package com.aresid.happyapp.fragments

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.aresid.happyapp.R

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class LoadingFragment: Fragment() {
	
	private var mNavController: NavController? = null
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		Log.d(
			TAG,
			"onCreateView: called"
		)
		
		// Inflate the layout
		return inflater.inflate(
			R.layout.fragment_loading,
			container,
			false
		)
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		Log.d(
			TAG,
			"onViewCreated: called"
		)
		super.onViewCreated(
			view,
			savedInstanceState
		)
		
		// Define a TextView from the layout
		val loadingTextView = view.findViewById<TextView>(R.id.loading_text_view)
		
		// Start the loading animation on the TextViews compound drawable
		startLoadingAnimation(loadingTextView)
		
		// Define NavController
		mNavController = Navigation.findNavController(view)
	}
	
	/**
	 * Takes the TextViews compound drawable top and starts its loading animation.
	 *
	 * @param textView The TextView to take the compound drawable top from.
	 */
	private fun startLoadingAnimation(textView: TextView) {
		Log.d(
			TAG,
			"startLoadingAnimation: called"
		)
		
		// Define a AnimatedVectorDrawable object from the TextViews compound drawable top
		val animatedVectorDrawable = textView.compoundDrawablesRelative[1] as AnimatedVectorDrawable
		
		// Start the loading animation
		animatedVectorDrawable.start()
	}
	
	companion object {
		private const val TAG = "LoadingFragment"
	}
	
	init {
		Log.d(
			TAG,
			"LoadingFragment: called"
		)
		// Required public empty constructor
	}
}