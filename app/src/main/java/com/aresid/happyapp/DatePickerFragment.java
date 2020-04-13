package com.aresid.happyapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * Created on: 15.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class DatePickerFragment
		extends DialogFragment {
	private static final String                             TAG                = "DatePickerFragment";
	private static final long                               EIGHTEEN_IN_MILLIS = 568024668000l;
	private              EditText                           mEditText;
	private              AppCompatActivity                  mContext;
	private              DatePickerDialog.OnDateSetListener dateSetListener    =
			new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
			Log.d(TAG, "onDateSet:true");
			Log.d(TAG, "onDateSet: selected date = " + view.getDayOfMonth() + (view.getMonth() + 1) +
			           view.getYear());
			mEditText.setText(getResources().getString(R.string.placeholders_for_date_of_birth,
			                                           view.getDayOfMonth(),
			                                           view.getMonth() + 1, view.getYear()));
		}
	};
	
	DatePickerFragment(@NonNull AppCompatActivity context, @NonNull EditText field) {
		mContext = context;
		mEditText = field;
	}
	
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		final Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dialog = new DatePickerDialog(mContext, dateSetListener, year - 25, month, day);
		dialog.getDatePicker()
		      .setMaxDate(System.currentTimeMillis() - EIGHTEEN_IN_MILLIS);
		return dialog;
	}
}
