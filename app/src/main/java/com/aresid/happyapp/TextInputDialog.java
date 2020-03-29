package com.aresid.happyapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

interface OnTextInputDialogInteractionListener {
	void transferTextInputText(String text);
}

/**
 * Created on: 20/03/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */
public class TextInputDialog
		extends DialogFragment {
	private static final String                               TAG = "TextInputDialog";
	private              OnTextInputDialogInteractionListener mListener;
	
	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (context instanceof OnTextInputDialogInteractionListener) {
			mListener = (OnTextInputDialogInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString() +
			                           " must implement " +
			                           "OnTextInputDialogInteractionListener");
		}
	}
	
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreateDialog: called");
		AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
		View view = requireActivity().getLayoutInflater()
		                             .inflate(R.layout.view_text_input_dialog, null);
		builder.setView(view);
		builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
			if (mListener != null) {
				TextInputEditText emailField = view.findViewById(R.id.email_field);
				mListener.transferTextInputText(emailField.getText()
				                                          .toString());
			}
		});
		builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
		return builder.create();
	}
}
