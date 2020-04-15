package com.aresid.happyapp;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
public class SubscriptionsDialog
		extends DialogFragment {
	private static final String TAG = "SubscriptionsDialog";
	
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreateDialog: called");
		return super.onCreateDialog(savedInstanceState);
	}
}
