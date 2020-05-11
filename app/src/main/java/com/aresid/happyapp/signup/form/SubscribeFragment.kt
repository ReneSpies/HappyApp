package com.aresid.happyapp.signup.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aresid.happyapp.R

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class SubscribeFragment: Fragment() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		Log.d(
			TAG,
			"onCreate: called"
		)
		super.onCreate(savedInstanceState)
	}
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		Log.d(
			TAG,
			"onCreateView: called"
		)
		
		// Inflate layout
		return inflater.inflate(
			R.layout.fragment_subscribe,
			container,
			false
		)
	}
	
	companion object {
		private const val TAG = "SubscribeFragment"
	}
	
	init {
		Log.d(
			TAG,
			"SubscribeFragment: called"
		)
		// Required public empty constructor
	}
}