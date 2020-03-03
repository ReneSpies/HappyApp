package com.aresid.happyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created on: 21.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class ConfirmEmailActivity
		extends AppCompatActivity
		implements View.OnClickListener {
	private static final String TAG                = "ConfirmEmailActivity";
	private              int    mEmailErrorHelper  = 0;
	private              int    mBackPressedHelper = 0;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		setTheme(R.style.Theme_HappyApp);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_email);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			FirebaseUser user =
					extras.getParcelable(getString(R.string.firebaseUserKey));
			if (user != null) {
				sendEmailVerification(user);
				checkUserStatus(user);
			}
		} else {
			Toast.makeText(this, getString(R.string.errorStandardMessageTryAgain),
			               Toast.LENGTH_LONG)
			     .show();
		}
	}
	
	private void startMainActivity(FirebaseUser user) {
		Log.d(TAG, "startMainActivity: called");
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(getString(R.string.firebaseUserKey), user);
		startActivity(intent);
	}
	
	private void checkUserStatus(FirebaseUser user) {
		Log.d(TAG, "checkUserStatus: called");
		new Handler().postDelayed(() -> {
			Log.d(TAG, "run: called");
			user.reload()
			    .addOnSuccessListener(command -> {
				    Log.d(TAG, "checkUserStatus: great success");
				    if (user.isEmailVerified()) {
					    startMainActivity(user);
				    } else {
					    checkUserStatus(user);
				    }
			    })
			    .addOnFailureListener(e -> {
				    Log.d(TAG, "checkUserStatus: failure");
				    Log.e(TAG, "checkUserStatus: ", e);
				    checkUserStatus(user);
			    });
		}, 5000);
	}
	
	private void sendEmailVerification(FirebaseUser user) {
		Log.d(TAG, "sendEmailVerification: called");
		Toast toast = Toast.makeText(this,
		                             getString(R.string.errorStandardMessageTryAgain),
		                             Toast.LENGTH_LONG);
		if (user != null) {
			user.sendEmailVerification()
			    .addOnSuccessListener(command -> {
				    Log.d(TAG, "sendEmailVerification: great success");
				    toast.cancel();
				    Toast.makeText(this, getString(R.string.plainEmailSentTo,
				                                   user.getEmail()), Toast.LENGTH_LONG)
				         .show();
			    })
			    .addOnFailureListener(e -> {
				    Log.d(TAG, "sendEmailVerification: failure");
				    Log.e(TAG, "sendEmailVerification: ", e);
				    toast.cancel();
				    if (e instanceof com.google.firebase.FirebaseTooManyRequestsException) {
					    Toast.makeText(this, getString(R.string.errorGettingDonuts),
					                   Toast.LENGTH_SHORT)
					         .show();
					    return;
				    }
				    if (mEmailErrorHelper <= 3) {
					    toast.show();
					    sendEmailVerification(user);
					    mEmailErrorHelper++;
				    }
			    });
		}
	}
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed: called");
		Toast toast = Toast.makeText(this, getString(R.string.plainPressAgainToExit),
		                             Toast.LENGTH_SHORT);
		if (mBackPressedHelper == 0) {
			Log.d(TAG, "onBackPressed: helper == 0");
			toast.show();
			mBackPressedHelper = 1;
			new Handler().postDelayed(() -> {
				Log.d(TAG, "run: called");
				mBackPressedHelper = 0;
			}, 6130);
		} else {
			if (mBackPressedHelper == 1) {
				Log.d(TAG, "onBackPressed: helper == 1");
				toast.cancel();
				finishAffinity();
			}
		}
	}
	
	private void handleLogout() {
		Log.d(TAG, "handleLogout: called");
		FirebaseAuth.getInstance()
		            .signOut();
		finish();
	}
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick: called");
		switch (v.getId()) {
			case R.id.buttong2:
				Log.d(TAG, "onClick: send again button");
				Bundle extras = getIntent().getExtras();
				if (extras != null &&
				    extras.getParcelable(getString(R.string.firebaseUserKey)) != null) {
					sendEmailVerification(extras.getParcelable(getString(R.string.firebaseUserKey)));
				}
				break;
			case R.id.buttong1:
				Log.d(TAG, "onClick: logout button");
				handleLogout();
				break;
		}
	}
}
