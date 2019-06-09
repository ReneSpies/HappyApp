package android.aresid.happyapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


/**
 * Created on: 25.04.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */


public class LegalitiesDialog
		extends DialogFragment
{
	// TODO: Move to strings.xml for translation.
	private static final String TERMS_AND_CONDITIONS_TITLE = "Terms and conditions";
	private static final String TERMS_AND_CONDITIONS_CONTENT_KEY = "terms_and_conditions";
	private static final String PRIVACY_POLICY_TITLE = "Privacy policy";
	private static final String PRIVACY_POLICY_CONTENT_KEY = "privacy_policy";
	private static final String TAG = "LegalitiesDialog";
	private String mAcceptedLegalitiesVersion;
	private OnPrivacyPolicyDialogInteractionListener mDialogInteractionListener;




	public static LegalitiesDialog newInstance(String firstName, String surname, String email, String password, String birthdate,
	                                           String acceptedLegalitiesVersion)
	{
		Bundle args = new Bundle();
		LegalitiesDialog fragment = new LegalitiesDialog();

		// Putting the parameters into the arguments bundle
		args.putString("firstName", firstName);
		args.putString("surname", surname);
		args.putString("password", password);
		args.putString("email", email);
		args.putString("birthdate", birthdate);
		args.putString("acceptedLegalitiesVersion", acceptedLegalitiesVersion);

		Log.d(TAG, "newInstance: firstName = " + firstName);
		Log.d(TAG, "newInstance: surname = " + surname);
		Log.d(TAG, "newInstance: email = " + email);
		Log.d(TAG, "newInstance: password = " + password);
		Log.d(TAG, "newInstance: birthdate = " + birthdate);
		Log.d(TAG, "newInstance: acceptedLegalitiesVersion = " + acceptedLegalitiesVersion);

		fragment.setArguments(args);
		return fragment;
	}




	@Override
	public void onAttach(@NonNull Context context)
	{
		Log.d(TAG, "onAttach:true");

		super.onAttach(context);

		if (context instanceof LegalitiesDialog.OnPrivacyPolicyDialogInteractionListener)
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

		// Get an AlertDialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		// Get the view object from the dialogs layout
		View dialogView = mDialogInteractionListener.getLayoutInflaterForDialog()
		                                            .inflate(R.layout.legalities_dialog_view, null);

		// Apply the view to the builder
		builder.setView(dialogView);

		// Access all view I'll need to perform this action.
		ListView legalitiesContent = dialogView.findViewById(R.id.legalities_list_view);
		ImageView waitingAssistant = dialogView.findViewById(R.id.privacy_policy_image_view_waiting_assistant);
		Button alertDialogAcceptButton = dialogView.findViewById(R.id.privacy_policy_dialog_accept_button);
		TextView waitingAssistantText = dialogView.findViewById(R.id.privacy_policy_text_view_waiting_assistant_text);
		Dialog dialog = builder.create();

		Glide.with(this)
		     .load(R.drawable.waiting_assistant_content)
		     .into(waitingAssistant);

		// Setting the visibilty of the views.
		alertDialogAcceptButton.setVisibility(View.INVISIBLE);

		// Get Firestore instance
		FirebaseFirestore db = FirebaseFirestore.getInstance();

		DocumentReference docRef = db.collection("legalities")
		                             .document("legalities v1.0");

		docRef.get()
		      .addOnSuccessListener(command ->
		                            {
			                            TermsAndConditions termsAndConditions = new TermsAndConditions(
					                            TERMS_AND_CONDITIONS_TITLE,
					                            command.getString(
							                            TERMS_AND_CONDITIONS_CONTENT_KEY)
			                            );
			                            PrivacyPolicy privacyPolicy = new PrivacyPolicy(
					                            PRIVACY_POLICY_TITLE,
					                            command.getString(PRIVACY_POLICY_CONTENT_KEY)
			                            );
			                            List<Legalities> listOfLegalities = new ArrayList<>();
			                            listOfLegalities.add(termsAndConditions);
			                            listOfLegalities.add(privacyPolicy);

			                            // Create an Adapter and load the List from above in it.
			                            LegalitiesAdapter adapterOfList = new LegalitiesAdapter(
					                            mDialogInteractionListener.getActivitiesContext(), R.layout.legalities_list_view_content,
					                            listOfLegalities
			                            );

			                            legalitiesContent.setAdapter(adapterOfList);

			                            // Set the waiting feedback invisible and show the actual content
			                            waitingAssistant.setVisibility(View.INVISIBLE);
			                            alertDialogAcceptButton.setVisibility(View.VISIBLE);
			                            waitingAssistantText.setVisibility(View.INVISIBLE);

			                            Log.d(TAG, "onCreateDialog: version code = " + command.getString("version"));
			                            Log.d(TAG, "onCreateDialog: document = " + command.getData());

			                            String firstName, surname, birthdate, email, password;
			                            String acceptedLegalitiesVersion = command.getString("version");

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

			                            // Register a listener to the accept mButton in the dialog and let it cancel the dialog
			                            alertDialogAcceptButton.setOnClickListener((view) ->
			                                                                       {
				                                                                       Log.d(TAG, "onClick:true");

				                                                                       mDialogInteractionListener.handleLegalitiesAccept(
						                                                                       firstName, surname, email, password, birthdate,
						                                                                       acceptedLegalitiesVersion
				                                                                                                                        );

				                                                                       dialog.cancel();
			                                                                       });
		                            })
		      .addOnFailureListener(command -> Log.e(TAG, "onCreateDialog: failure", command));

		// Finally show the dialog
		dialog.setCancelable(false);
		return dialog;
	}




	/**
	 * Getter for legalities version. This is needed because I have to set the legalities version inside a lambda.
	 *
	 * @return The current legalities version.
	 */
	private String getAcceptedLegalitiesVersion()
	{
		Log.d(TAG, "getAcceptedLegalitiesVersion:true");
		return mAcceptedLegalitiesVersion;
	}




	/**
	 * Setter for the legalities version. This is needed because I have to set the legalities verison inside a lambda.
	 *
	 * @param acceptedLegalitiesVersion The legalities version that needs to be set as double.
	 */
	private void setAcceptedLegalitiesVersion(String acceptedLegalitiesVersion)
	{
		Log.d(TAG, "setAcceptedLegalitiesVersion:true");
		mAcceptedLegalitiesVersion = acceptedLegalitiesVersion;
	}




	public interface OnPrivacyPolicyDialogInteractionListener
	{
		void handleLegalitiesAccept(String firstName, String surname, String email, String password, String birthdate,
		                            String acceptedLegalitiesVersion);


		LayoutInflater getLayoutInflaterForDialog();


		Activity getActivitiesContext();







	}







}
