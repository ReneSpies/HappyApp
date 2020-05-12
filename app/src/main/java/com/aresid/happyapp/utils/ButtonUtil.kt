package com.aresid.happyapp.utils

import android.graphics.drawable.AnimatedVectorDrawable
import android.widget.Button
import com.aresid.happyapp.R
import timber.log.Timber

/**
 *    Created on: 12.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */
object ButtonUtil {
	
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
		
		Timber.d("removeLoadingButtonAnimationWithEnable: called")
		
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
	 * Sets an animated vector drawable to the buttons end and then starts its rotation animation.
	 *
	 * @param button  The button to set the animation on.
	 * @param disable Disable the button?
	 */
	fun setAndStartLoadingButtonAnimationWithDisable(
		button: Button,
		disable: Boolean
	) {
		
		Timber.d("setAndStartLoadingButtonAnimationWithDisable: called")
		
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
	
}
