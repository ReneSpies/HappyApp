package android.aresid.happyapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


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
	private double legalitiesVersion;
	private OnPrivacyPolicyDialogInteractionListener mDialogInteractionListener;




	public static PrivacyPolicyDialog newInstance(String firstName, String surname, String birthdate, String email, boolean acceptedLegalities,
	                                              double legalitiesVersion, String password)
	{
		Bundle args = new Bundle();
		PrivacyPolicyDialog fragment = new PrivacyPolicyDialog();

		// Putting the parameters into the arguments bundle
		args.putString("firstName", firstName);
		args.putString("surname", surname);
		args.putString("birthdate", birthdate);
		args.putString("email", email);
		args.putBoolean("acceptedLegalities", acceptedLegalities);
		args.putDouble("legalitiesVersion", legalitiesVersion);
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

		// TODO: Move all hardcoded String into strings.xml.
		// TODO: Change layout and use ListView or RecyclerView.

		// Get an AlertDialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		// Get the view object from the dialogs layout
		View dialogView = mDialogInteractionListener.getLayoutInflaterForDialog()
		                                            .inflate(R.layout.item_privacy_policy_dialog_view, null);

		// Apply the view to the builder
		builder.setView(dialogView);

		// Access all view I'll need to perform this action.
		TextView title1 = dialogView.findViewById(R.id.privacy_policy_dialog_title1);
		TextView title2 = dialogView.findViewById(R.id.privacy_policy_dialog_title2);
		TextView content1 = dialogView.findViewById(R.id.privacy_policy_dialog_content1);
		TextView content2 = dialogView.findViewById(R.id.privacy_policy_dialog_content2);
		Button alertDialogAcceptButton = dialogView.findViewById(R.id.privacy_policy_dialog_accept_button);
		Dialog dialog = builder.create();

		// Get Firestore instance
		FirebaseFirestore db = FirebaseFirestore.getInstance();

		DocumentReference docRef = db.collection("legalities")
		                             .document("legalities v1.0");

		docRef.get()
		      .addOnCompleteListener(task ->
		                             {
			                             if (task.isSuccessful())
			                             {
				                             DocumentSnapshot document = task.getResult();
				                             if (document.exists())
				                             {
					                             title1.setText("AGB");
					                             content1.setText(document.getString("AGB"));
					                             title2.setText("Datenschutzerklärung");
					                             content2.setText(document.getString("Datenschutzerklärung"));
					                             setLegalitiesVersion(document.getDouble("version"));
					                             Log.d(TAG, "onCreateDialog: document = " + document.getData());
				                             }
				                             else
				                             {
					                             Log.d(TAG, "onCreateDialog: no such document");
				                             }
			                             }
			                             else
			                             {
				                             Log.d(TAG, "onCreateDialog: get failed with " + task.getException());
				                             setLegalitiesVersion(0.9);
			                             }
		                             });

		String firstName, surname, birthdate, email, password;
		double legalitiesVersion = getLegalitiesVersion();
		Log.d(TAG, "onCreateDialog: legalitiesVersion = " + legalitiesVersion);

		if (getArguments() != null)
		{
			firstName = getArguments().getString("firstName");
			surname = getArguments().getString("surname");
			birthdate = getArguments().getString("birthdate");
			email = getArguments().getString("email");
			password = getArguments().getString("password");
		}
		else
		{
			firstName = null;
			surname = null;
			birthdate = null;
			email = null;
			password = null;
		}

		// Register a listener to the accept button in the dialog and let it cancel the dialog
		alertDialogAcceptButton.setOnClickListener((view) ->
		                                           {
			                                           Log.d(TAG, "onClick:true");

			                                           mDialogInteractionListener.handlePrivacyPolicyAccept(firstName, surname, birthdate, email,
			                                                                                                true, legalitiesVersion, password);

			                                           dialog.cancel();
		                                           });

		// Finally show the dialog
		dialog.setCancelable(false);
		return dialog;
	}




	private double getLegalitiesVersion()
	{
		Log.d(TAG, "getLegalitiesVersion:true");
		return this.legalitiesVersion;
	}




	private void setLegalitiesVersion(double legalitiesVersion)
	{
		Log.d(TAG, "setLegalitiesVersion:true");
		this.legalitiesVersion = legalitiesVersion;
	}




	public interface OnPrivacyPolicyDialogInteractionListener
	{
		void handlePrivacyPolicyAccept(String firstName, String surname, String birthdate, String email, boolean acceptedLegalities,
		                               double legalitiesVersion, String password);


		LayoutInflater getLayoutInflaterForDialog();







	}







}
