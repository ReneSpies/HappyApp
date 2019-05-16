package android.aresid.happyapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;


/**
 * Created on: 29.04.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */


class StandardLoadingDialog
		extends DialogFragment
{
	private final String TAG = getClass().getSimpleName();




	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateDialog:true");

		// Get an AlertDialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		// Get the view object from the dialogs layout
		View dialogView = getActivity().getLayoutInflater()
		                               .inflate(R.layout.item_standard_loading_dialog, null);

		// Accessing my views in the layout
		ImageView gif = dialogView.findViewById(R.id.progress_spinner);

		Glide.with(this)
		     .load(R.drawable.progress_spinner_test)
		     .into(gif);

		// Apply the view to the builder
		builder.setView(dialogView);

		builder.setCancelable(false);

		// Finally show the dialog
		return builder.create();
	}







}
