package com.aresid.happyapp.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aresid.happyapp.R

/**
 * Created on: 23/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class DateOfBirthFragment: Fragment() {
	
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
			R.layout.fragment_date_of_birth,
			container,
			false
		)
	}
	
	companion object {
		private const val TAG = "DateOfBirthFragment"
	}
}