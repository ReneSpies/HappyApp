package com.aresid.happyapp.utils

import android.app.Activity
import android.graphics.Paint
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aresid.happyapp.R
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*

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
		
		Timber.d("getString: called")
		
		// If the object is null, return empty String, else do .toString()
		return objectToTransform?.toString() ?: ""
	}
	
	/**
	 * Shows an error Snackbar with the length long and error color background.
	 *
	 * @param snackbarView The view to find a parent from.
	 * @param errorMessage The text to show. Can be formatted text.
	 */
	fun showErrorSnackbar(
		snackbarView: View,
		errorMessage: String
	) {
		
		Timber.d("showErrorSnackbar: called")
		
		Snackbar.make(
			snackbarView,
			errorMessage,
			Snackbar.LENGTH_LONG
		).setBackgroundTint(
			ContextCompat.getColor(
				snackbarView.context,
				R.color.design_default_color_error
			)
		).show()
	}
	
	/**
	 * Checks if the String is >= 6.
	 */
	fun String.isGreaterOrEqualToSix(): Boolean {
		
		Timber.d("isGreaterThanSix: called")
		
		return length >= 6
		
	}
	
	/**
	 * Checks if the String contains a capital letter.
	 */
	fun String.containsCapitalLetter(): Boolean {
		
		Timber.d("containsCapitalLetter: called")
		
		for (character in this) {
			
			// This comparison is necessary because otherwise, you would return false,
			// if the first character is not uppercase
			if (character.isUpperCase()) {
				
				return true
				
			}
			
		}
		
		return false
		
	}
	
	/**
	 * Checks if the String contains a number.
	 */
	fun String.containsNumber(): Boolean {
		
		Timber.d("containsNumber: called")
		
		for (character in this) {
			
			// This comparison is necessary because otherwise, you would return false,
			// if the first character is not a digit
			if (character.isDigit()) {
				
				return true
				
			}
			
		}
		
		return false
		
	}
	
	fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
		
		Timber.d("makeLinks: called")
		
		val spannableString = SpannableString(text)
		
		for (link in links) {
			
			val clickableSpan = object: ClickableSpan() {
				
				override fun onClick(widget: View) {
					
					Selection.setSelection(
						(widget as TextView).text as Spannable,
						0
					)
					
					widget.invalidate()
					
					link.second.onClick(widget)
					
				}
				
			}
			
			val startIndexOfLink = text.toString().indexOf(link.first)
			
			spannableString.setSpan(
				clickableSpan,
				startIndexOfLink,
				startIndexOfLink + link.first.length,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
			)
			
		}
		
		movementMethod = LinkMovementMethod.getInstance()
		
		setText(
			spannableString,
			TextView.BufferType.SPANNABLE
		)
		
	}
	
	/**
	 * Calendar extension function to calculate
	 * whether the Date in the Calendar is past
	 * 18 years from the time in milliseconds [now].
	 */
	fun Calendar.isOlderThan18(now: Long): Boolean {
		
		Timber.d("isOlderThan18: called")
		
		val eighteenYearsMillis = 568_025_136_000 / 1
		
		return (now - timeInMillis) >= eighteenYearsMillis
		
	}
	
	/**
	 * Shows a standard Snackbar with the length long and accent color background.
	 *
	 * @param snackbarView The view to find a parent from.
	 * @param message      The text to show. Can be formatted text.
	 * @param context      The Context to use ContextCompat#getColor.
	 */
	fun showSnackbar(
		snackbarView: View,
		message: String
	) {
		Timber.d("showSnackbar: called")
		
		Snackbar.make(
			snackbarView,
			message,
			Snackbar.LENGTH_LONG
		).setBackgroundTint(
			ContextCompat.getColor(
				snackbarView.context,
				android.R.color.black
			)
		).show()
	}
	
	/**
	 * Extension function for TextView to underline the TextView.
	 */
	fun TextView.underline() {
		
		Timber.d("underline: called")
		
		paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
		
	}
	
	/**
	 * Uses the InputMethodManager to hide the soft keyboard.
	 */
	fun hideKeyboard(
		view: View
	) {
		
		Timber.d("hideKeyboard: called")
		
		// Define an InputMethodManager object from the context
		val inputMethodManager = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		
		// Hide the soft keyboard using InputMethodManager
		inputMethodManager.hideSoftInputFromWindow(
			view.windowToken,
			0
		)
		
	}
	
}