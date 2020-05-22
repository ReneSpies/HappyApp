package com.aresid.happyapp.signup.form.dateofbirth

import android.view.View
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber
import java.util.*

/**
 *    Created on: 22.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class DateOfBirthViewModel: ViewModel() {
	
	// LiveData for the selected date from the DatePicker
	val date = MutableLiveData<Date>()
	
	init {
		
		Timber.d(": called")
		
	}
	
	fun onSubscribeButtonClicked(view: View) {
		
		Timber.d("onSubscribeButtonClicked: called")
		
		// Cast the view to Button, since XML expression do not allow casting
		val button = view as Button
		
	}
	
}