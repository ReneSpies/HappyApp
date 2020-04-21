package com.aresid.happyapp.utils;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.aresid.happyapp.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * Created on: 18/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

public class Utils {
	
	private static final String TAG = "Utils";
	
	/**
	 * Checks whether the given Object is null and returns either an empty String or Object.toString().
	 *
	 * @param objectToTransform The Object which is needed as String.
	 * @return Empty String or Object#toString().
	 */
	public static String getString(Object objectToTransform) {
		
		Log.d(TAG, "getString: called");
		
		// If the object is null, return empty String, else do .toString()
		return ((objectToTransform == null) ? "" : objectToTransform.toString());
		
	}
	
	/**
	 * Shows an error Snackbar with the length long and error color background.
	 *
	 * @param snackbarView The view to find a parent from.
	 * @param errorMessage The text to show. Can be formatted text.
	 * @param context      The Context to use ContextCompat#getColor.
	 */
	public static void showErrorSnackbar(View snackbarView, String errorMessage, Context context) {
		
		Log.d(TAG, "showErrorSnackbar: called");
		
		Snackbar.make(snackbarView, errorMessage, Snackbar.LENGTH_LONG).setBackgroundTint(ContextCompat.getColor(context, R.color.design_default_color_error)).show();
		
	}
	
	/**
	 * Shows a standard Snackbar with the length long and accent color background.
	 *
	 * @param snackbarView The view to find a parent from.
	 * @param message      The text to show. Can be formatted text.
	 * @param context      The Context to use ContextCompat#getColor.
	 */
	public static void showSnackbar(View snackbarView, String message, Context context) {
		
		Log.d(TAG, "showSnackbar: called");
		
		Snackbar.make(snackbarView, message, Snackbar.LENGTH_LONG).setBackgroundTint(ContextCompat.getColor(context, R.color.colorAccent)).show();
		
	}
	
	/**
	 * Sets a animated vector drawable to the buttons end and then starts its rotation animation.
	 *
	 * @param button The button to set the drawable on.
	 */
	public static void setAndStartLoadingButtonAnimationWithDisable(Button button, boolean disable) {
		
		Log.d(TAG, "setAndStartLoadingButtonAnimationWithDisable: called");
		
		// Disable the button or not
		button.setEnabled(!disable);
		
		// Define an AnimatedVectorDrawable object from the drawable file
		AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) button.getContext().getResources().getDrawable(R.drawable.animated_circle, null);
		
		// Sets the button drawable to its end
		button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, animatedVectorDrawable, null);
		
		// Start the animation
		animatedVectorDrawable.start();
		
	}
	
	public static void removeLoadingButtonAnimationWithEnable(Button button, boolean enable) {
		
		Log.d(TAG, "removeLoadingButtonAnimationWithEnable: called");
		
		// Enable the button or not
		button.setEnabled(enable);
		
		// Set all button drawables to null
		button.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
		
	}
	
}
