package com.aresid.happyapp

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.util.*

/**
 * Created on: 15.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class DatePickerFragment internal constructor(
	private val mContext: AppCompatActivity,
	private val mEditText: EditText
): DialogFragment() {
	
	private val dateSetListener = OnDateSetListener { view, year, month, dayOfMonth ->
		Log.d(
			TAG,
			"onDateSet:true"
		)
		Log.d(
			TAG,
			"onDateSet: selected date = " + view.dayOfMonth + (view.month + 1) + view.year
		)
		mEditText.setText(
			resources.getString(
				R.string.placeholders_for_date_of_birth,
				view.dayOfMonth,
				view.month + 1,
				view.year
			)
		)
	}
	
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val calendar = Calendar.getInstance()
		val year = calendar[Calendar.YEAR]
		val month = calendar[Calendar.MONTH]
		val day = calendar[Calendar.DAY_OF_MONTH]
		val dialog = DatePickerDialog(
			mContext,
			dateSetListener,
			year - 25,
			month,
			day
		)
		dialog.datePicker.maxDate = System.currentTimeMillis() - EIGHTEEN_IN_MILLIS
		return dialog
	}
	
	companion object {
		private const val TAG = "DatePickerFragment"
		private const val EIGHTEEN_IN_MILLIS = 568024668000L
	}
	
}