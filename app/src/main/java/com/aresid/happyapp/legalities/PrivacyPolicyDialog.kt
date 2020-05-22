package com.aresid.happyapp.legalities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.aresid.happyapp.R
import timber.log.Timber

/**
 *    Created on: 22.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class PrivacyPolicyDialog: DialogFragment() {
	
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		
		Timber.d("onCreateDialog: called")
		
		return activity?.let {
			
			val builder = AlertDialog.Builder(it)
			
			val inflater = layoutInflater
			
			builder.setView(
				inflater.inflate(
					R.layout.dialog_privacy_policy,
					null
				)
			)
			
			builder.setPositiveButton(
				getString(R.string.ok)
			) { _, _ -> }
			
			builder.create()
			
		} ?: throw IllegalStateException("Activity cannot be null")
		
	}
}