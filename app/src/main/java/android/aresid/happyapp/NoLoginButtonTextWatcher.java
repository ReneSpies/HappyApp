package android.aresid.happyapp;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;


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




	@SuppressLint ("LongLogTag")
	NoLoginButtonTextWatcher(TextInputEditText emailField, TextInputEditText passwordField, TextInputLayout emailLayout,
	                         TextInputLayout passwordLayout)
	{
		Log.d(TAG, "NoLoginButtonTextWatcher:true");

		mEmailField = emailField;
		mPasswordField = passwordField;
		mHandler = new Handler();
		mEmailLayout = emailLayout;
		mPasswordLayout = passwordLayout;
	}




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

		if ((mEmailField.length() & mPasswordField.length()) != 0)
		{
			mHandler.postDelayed(() ->
			                     {
				                     Log.d(TAG, "afterTextChanged: run");

				                     FirebaseAuth.getInstance()
				                                 .signInWithEmailAndPassword(mEmailField.getText()
				                                                                        .toString(), mPasswordField.getText()
				                                                                                                   .toString())
				                                 .addOnSuccessListener(command ->
				                                                       {
					                                                       Log.d(TAG, "afterTextChanged: success");
					                                                       mEmailLayout.setError(null);
					                                                       mPasswordLayout.setError(null);
				                                                       })
				                                 .addOnFailureListener(command ->
				                                                       {
					                                                       Log.d(TAG, "afterTextChanged: failure");
					                                                       mPasswordLayout.setError("Email or password incorrect");
				                                                       });
			                     }, 1013);
		}
	}







}
