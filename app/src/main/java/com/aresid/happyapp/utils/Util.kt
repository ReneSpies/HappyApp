package com.aresid.happyapp.utils

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.aresid.happyapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

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
		Log.d(
			TAG,
			"showErrorSnackbar: called"
		)
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
		Log.d(
			TAG,
			"showSnackbar: called"
		)
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
	 * Sets an animated vector drawable to the buttons end and then starts its rotation animation.
	 *
	 * @param button  The button to set the animation on.
	 * @param disable Disable the button?
	 */
	fun setAndStartLoadingButtonAnimationWithDisable(
		button: Button,
		disable: Boolean
	) {
		Log.d(
			TAG,
			"setAndStartLoadingButtonAnimationWithDisable: called"
		)
		
		// Define a new AnimatedVectorDrawable object from the drawable file
		val animatedVectorDrawable = button.context.resources.getDrawable(
			R.drawable.animated_loading_circle_red_black,
			null
		) as AnimatedVectorDrawable
		
		// Disable the button or not
		button.isEnabled = !disable
		
		// Sets the button drawable to its end
		button.setCompoundDrawablesRelativeWithIntrinsicBounds(
			null,
			null,
			animatedVectorDrawable,
			null
		)
		
		// Start the animation
		animatedVectorDrawable.start()
	}
	
	/**
	 * Removes the loading animation from the button and enables it if wished.
	 *
	 * @param button The button to remove the animation from.
	 * @param enable Enable the button?
	 */
	fun removeLoadingButtonAnimationWithEnable(
		button: Button,
		enable: Boolean
	) {
		Log.d(
			TAG,
			"removeLoadingButtonAnimationWithEnable: called"
		)
		
		// Enable the button or not
		button.isEnabled = enable
		
		// Set all button drawables to null
		button.setCompoundDrawablesRelativeWithIntrinsicBounds(
			0,
			0,
			0,
			0
		)
	}
	
	/**
	 * Takes all TextInputLayouts available in parentView and set its errors to null.
	 *
	 * @param parentView The View containing the TextInputLayouts
	 */
	fun resetLayoutErrors(parentView: ViewGroup) {
		Log.d(
			TAG,
			"resetLayoutErrors: called"
		)
		
		// Abstract the parentViews child count to an int
		val childCount = parentView.childCount
		
		// Iterate through all members of parentView and set every TextInputLayout error to null
		for (i in 0 .. childCount) {
			
			// Define a View object from the parentViews child at index
			val child = parentView.getChildAt(i)
			
			// If child is a TextInputLayout, set its error to null
			if (child is TextInputLayout) {
				
				// Set error to null
				child.error = null
			}
		}
	}
}