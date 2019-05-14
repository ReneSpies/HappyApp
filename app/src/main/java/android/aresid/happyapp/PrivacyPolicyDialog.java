package android.aresid.happyapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


/**
 * Created on: 25.04.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */


public class PrivacyPolicyDialog
		extends DialogFragment
{
	private final String TAG = getClass().getSimpleName();
	private OnPrivacyPolicyDialogInteractionListener mDialogInteractionListener;
	private final static float LEGALITIES_VERSION = 1.0f;

	public static PrivacyPolicyDialog newInstance(String firstName, String surname, String email, String password)
	{
		Bundle args = new Bundle();
		PrivacyPolicyDialog fragment = new PrivacyPolicyDialog();

		// Putting the parameters into the arguments bundle
		args.putString("firstName", firstName);
		args.putString("surname", surname);
		args.putString("email", email);
		args.putString("password", password);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(@NonNull Context context)
	{
		Log.d(TAG, "onAttach:true");

		super.onAttach(context);

		if (context instanceof PrivacyPolicyDialog.OnPrivacyPolicyDialogInteractionListener)
		{
			mDialogInteractionListener = (OnPrivacyPolicyDialogInteractionListener) context;
		}
		else
		{
			throw new RuntimeException(context.toString() + " must implement OnPrivacyPolicyDialogInteractionListener");
		}
	}

	@Override
	public void onDetach()
	{
		Log.d(TAG, "onDetach:true");

		super.onDetach();

		mDialogInteractionListener = null;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateDialog:true");

		// Get an AlertDialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		// Get the view object from the dialogs layout
		View dialogView = getActivity().getLayoutInflater()
		                               .inflate(R.layout.item_privacy_policy_dialog_view, null);

		// Apply the view to the builder
		builder.setView(dialogView);

		// Get the button from within the view and create the dialog
		Button alertDialogAcceptButton = dialogView.findViewById(R.id.privacy_policy_dialog_accept_button);
		Dialog dialog = builder.create();

		String firstName, surname, email, password;

		if (getArguments() != null)
		{
			firstName = getArguments().getString("firstName");
			surname = getArguments().getString("surname");
			email = getArguments().getString("email");
			password = getArguments().getString("password");
		}
		else
		{
			firstName = null;
			surname = null;
			email = null;
			password = null;
		}

		String birthdate = "01.10.1999";

		// Register a listener to the accept button in the dialog and let it cancel the dialog
		alertDialogAcceptButton.setOnClickListener((view) ->
		                                           {
			                                           Log.d(TAG, "onClick:true");

			                                           mDialogInteractionListener.handlePrivacyPolicyAccept(firstName, surname, birthdate, email,
			                                                                                                true, LEGALITIES_VERSION, password);

			                                           dialog.cancel();
		                                           });

		// Finally show the dialog
		return dialog;
	}

	public interface OnPrivacyPolicyDialogInteractionListener
	{
		void handlePrivacyPolicyAccept(String firstName, String surname, String birthdate, String email, boolean acceptedLegalities,
		                               float legalitiesVersion, String password);







	}







}
