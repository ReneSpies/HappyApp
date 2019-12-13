package com.aresid.happyapp;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

/**
 * Created on: 13/12/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class ButtonlessLogin
		implements TextWatcher {

	private static final String                               TAG = "ButtonlessLogin";
	private              OnButtonlessLoginInteractionListener mListener;

	ButtonlessLogin(Context context) {

		Log.d(TAG, "ButtonlessLogin: empty constructor");

		if (context instanceof OnButtonlessLoginInteractionListener) {

			mListener = (OnButtonlessLoginInteractionListener) context;

		} else {

			throw new RuntimeException(context.toString() + " must implement OnButtonlessInteractionListener");

		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		Log.d(TAG, "beforeTextChanged:true");

		if (start < after) {

			new Handler().postDelayed(() -> mListener.onFinishedTyping(), 1300);

		}

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		Log.d(TAG, "onTextChanged:true");

	}

	@Override
	public void afterTextChanged(Editable s) {

		Log.d(TAG, "afterTextChanged:true");

	}

	interface OnButtonlessLoginInteractionListener {

		void onFinishedTyping();

	}

}
