package android.aresid.happyapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

/**
 * Created on: 15.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class DatePickerFragment
		extends DialogFragment {

	private static final String                             TAG             = "DatePickerFragment";
	private              TextInputEditText                  mEtRegistrationDateOfBirthField;
	private              DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

			Log.d(TAG, "onDateSet:true");
			Log.d(TAG, "onDateSet: selected date = " + view.getDayOfMonth() + (view.getMonth() + 1) + view.getYear());
			mEtRegistrationDateOfBirthField.setText(getResources().getString(R.string.placeholder_date_of_birth_content, view.getDayOfMonth(), view.getMonth() + 1, view.getYear()));

		}

	};

	DatePickerFragment(AppCompatActivity context) {

		mEtRegistrationDateOfBirthField = context.findViewById(R.id.entry_activity_registration_date_of_birth_field);

	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

		final Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);

	}

}
