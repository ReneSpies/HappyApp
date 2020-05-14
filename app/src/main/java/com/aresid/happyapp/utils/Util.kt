package com.aresid.happyapp.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.aresid.happyapp.R
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * Created on: 18/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
object Util {
	
	private const val TAG = "Utils"
	
	/**
	 * Checks whether the given Object is null and returns either an empty String or Object.toString().
	 *
	 * @param objectToTransform The Object which is needed as String.
	 * @return Empty String or Object#toString().
	 */
	fun getString(objectToTransform: Any?): String {
		Log.d(
			TAG,
			"getString: called"
		)
		
		// If the object is null, return empty String, else do .toString()
		return objectToTransform?.toString() ?: ""
	}
	
	/**
	 * Shows an error Snackbar with the length long and error color background.
	 *
	 * @param snackbarView The view to find a parent from.
	 * @param errorMessage The text to show. Can be formatted text.
	 * @param context      The Context to use ContextCompat#getColor.
	 */
	fun showErrorSnackbar(
		snackbarView: View?,
		errorMessage: String?,
		context: Context?
	) {
		Timber.d("showErrorSnackbar: called")
		
		Snackbar.make(
			snackbarView!!,
			errorMessage!!,
			Snackbar.LENGTH_LONG
		).setBackgroundTint(
			ContextCompat.getColor(
				context!!,
				R.color.design_default_color_error
			)
		).show()
	}
	
	/**
	 * Shows a standard Snackbar with the length long and accent color background.
	 *
	 * @param snackbarView The view to find a parent from.
	 * @param message      The text to show. Can be formatted text.
	 * @param context      The Context to use ContextCompat#getColor.
	 */
	fun showSnackbar(
		snackbarView: View?,
		message: String?,
		context: Context?
	) {
		Timber.d("showSnackbar: called")
		
		Snackbar.make(
			snackbarView!!,
			message!!,
			Snackbar.LENGTH_LONG
		).setBackgroundTint(
			ContextCompat.getColor(
				context!!,
				R.color.black
			)
		).show()
	}
	
	/**
	 * Uses the InputMethodManager to hide the soft keyboard.
	 */
	fun hideKeyboard(
		context: Context,
		view: View
	) {
		
		Timber.d("hideKeyboard: called")
		
		// Define an InputMethodManager object from the context
		val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		
		// Hide the soft keyboard using InputMethodManager
		inputMethodManager.hideSoftInputFromWindow(
			view.windowToken,
			0
		)
		
	}
	
}