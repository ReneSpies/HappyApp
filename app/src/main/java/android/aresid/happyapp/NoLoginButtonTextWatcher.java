package android.aresid.happyapp;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created on: 09.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */


public class NoLoginButtonTextWatcher
		implements TextWatcher
{
	private static final String TAG = "NoLoginButtonTextWatcher";
	private TextInputEditText mEmailField;
	private TextInputEditText mPasswordField;
	private Handler mHandler;
	private TextInputLayout mEmailLayout;
	private TextInputLayout mPasswordLayout;
	private ImageView mWaitingAssistant;
	private TextView mWaitingAssistantTextView;
	private AppCompatActivity mContext;
	private String mEditableHelper;
	private OnNoLoginButtonTextWatcherInteractionListener mListener;




	@SuppressLint ("LongLogTag")
	NoLoginButtonTextWatcher(AppCompatActivity context)
	{
		Log.d(TAG, "NoLoginButtonTextWatcher:true");

		mContext = context;

		if (mContext instanceof OnNoLoginButtonTextWatcherInteractionListener)
		{
			mListener = (OnNoLoginButtonTextWatcherInteractionListener) mContext;
		}
		else
		{
			throw new RuntimeException(context.toString() + " must implement OnNoLoginButtonTextWatcherListener");
		}

		mEditableHelper = "";
		mEmailField = mContext.findViewById(R.id.entry_activity_login_email_field);
		mPasswordField = mContext.findViewById(R.id.entry_activity_login_password_field);
		mEmailLayout = mContext.findViewById(R.id.entry_activity_login_email_layout);
		mPasswordLayout = mContext.findViewById(R.id.entry_activity_login_password_layout);
		mWaitingAssistant = mContext.findViewById(R.id.entry_activity_login_waiting_assistant);
		mWaitingAssistantTextView = mContext.findViewById(R.id.entry_activity_login_waiting_assistant_text_view);
		mHandler = new Handler();

		Glide.with(mContext)
		     .load(R.drawable.waiting_assistant_content)
		     .into(mWaitingAssistant);
	}




	@SuppressLint ("LongLogTag")
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
		Log.d(TAG, "beforeTextChanged:true");
	}




	@SuppressLint ("LongLogTag")
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		Log.d(TAG, "onTextChanged:true");
	}




	@SuppressLint ("LongLogTag")
	@Override
	public void afterTextChanged(Editable s)
	{
		Log.d(TAG, "afterTextChanged: Editable = " + s);

		mHandler.removeMessages(0);

		if (!mEditableHelper.equals(s.toString()))
		{
			if (mEmailField.length() != 0 && mPasswordField.length() != 0)
			{
				mHandler.postDelayed(() ->
				                     {
					                     Log.d(TAG, "afterTextChanged: run");

					                     String email = mEmailField.getText()
					                                               .toString();
					                     String password = mPasswordField.getText()
					                                                     .toString();

					                     mWaitingAssistant.setVisibility(View.VISIBLE);
					                     mWaitingAssistantTextView.setVisibility(View.VISIBLE);

					                     FirebaseAuth.getInstance()
					                                 .signInWithEmailAndPassword(email, password)
					                                 .addOnSuccessListener(command ->
					                                                       {
						                                                       Log.d(TAG, "afterTextChanged: success");

						                                                       mListener.loginWithUser(command.getUser());

						                                                       mEmailLayout.setError(null);
						                                                       mPasswordLayout.setError(null);
					                                                       })
					                                 .addOnFailureListener(e ->
					                                                       {
						                                                       Log.d(TAG, "afterTextChanged: failure");
						                                                       Log.e(TAG, "afterTextChanged: ", e);

						                                                       mWaitingAssistant.setVisibility(View.GONE);
						                                                       mWaitingAssistantTextView.setVisibility(View.GONE);

						                                                       // TODO: Exception handling!
						                                                       if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException |
						                                                           e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException)
						                                                       {
							                                                       mPasswordLayout.setError("Email or password incorrect");
						                                                       }
						                                                       else if (e instanceof com.google.firebase.FirebaseNetworkException)
						                                                       {
							                                                       Toast.makeText(
									                                                       mContext,
									                                                       "Check your internet connection and try again later",
									                                                       Toast.LENGTH_LONG
							                                                       )
							                                                            .show();
						                                                       }
					                                                       });
				                     }, 1013);
			}

			mEditableHelper = s.toString();
		}
	}




	interface OnNoLoginButtonTextWatcherInteractionListener
	{
		void loginWithUser(FirebaseUser user);







	}







}
