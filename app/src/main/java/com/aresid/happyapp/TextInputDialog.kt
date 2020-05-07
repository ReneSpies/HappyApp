package com.aresid.happyapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText

internal interface OnTextInputDialogInteractionListener {
	fun onTextInputDialogPositiveButtonClicked(text: String?)
}

/**
 * Created on: 20/03/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */

class TextInputDialog: DialogFragment() {
	
	private var mListener: OnTextInputDialogInteractionListener? = null
	override fun onAttach(context: Context) {
		super.onAttach(context)
		mListener = if (context is OnTextInputDialogInteractionListener) {
			context
		}
		else {
			throw RuntimeException(
				"$context must implement OnTextInputDialogInteractionListener"
			)
		}
	}
	
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		Log.d(
			TAG,
			"onCreateDialog: called"
		)
		val builder = AlertDialog.Builder(requireContext())
		val view = requireActivity().layoutInflater.inflate(
			R.layout.view_text_input_dialog,
			null
		)
		builder.setView(view)
		builder.setPositiveButton(
			getString(R.string.ok)
		) { dialog: DialogInterface?, which: Int ->
			if (mListener != null) {
				val emailField: TextInputEditText = view.findViewById(R.id.email_field)
				mListener!!.onTextInputDialogPositiveButtonClicked(
					emailField.text.toString()
				)
			}
		}
		builder.setNegativeButton(
			getString(R.string.cancel)
		) { dialog: DialogInterface?, which: Int -> }
		return builder.create()
	}
	
	companion object {
		private const val TAG = "TextInputDialog"
	}
}