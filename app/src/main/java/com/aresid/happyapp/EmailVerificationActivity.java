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
public class EmailVerificationActivity
		extends AppCompatActivity
		implements View.OnClickListener {
	private static final String TAG                = "GmailVerificationAct";
	private              int    mEmailErrorHelper  = 0;
	private              int    mBackPressedHelper = 0;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate:true");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_verification);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			FirebaseUser user = extras.getParcelable("user");
			if (user != null) {
				checkUserStatus(user);
			}
		} else {
			Toast.makeText(this, getString(R.string.errorStandardMessageTryAgain),
			               Toast.LENGTH_LONG)
			     .show();
		}
	}
	
	private void startMainActivity(FirebaseUser user) {
		Log.d(TAG, "startMainActivity:true");
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
	}
	
	private void checkUserStatus(FirebaseUser user) {
		Log.d(TAG, "checkUserStatus:true");
		new Handler().postDelayed(() -> {
			Log.d(TAG, "run:true");
			user.reload()
			    .addOnSuccessListener(command -> {
				    Log.d(TAG, "checkUserStatus: success");
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
		}, 1300);
	}
	
	private void sendEmailVerification(FirebaseUser user) {
		Log.d(TAG, "sendEmailVerification:true");
		Toast toast = Toast.makeText(this,
		                             getString(R.string.errorStandardMessageTryAgain),
		                             Toast.LENGTH_LONG);
		if (user != null) {
			user.sendEmailVerification()
			    .addOnSuccessListener(command -> {
				    Log.d(TAG, "sendEmailVerification: success");
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
		Log.d(TAG, "onBackPressed:true");
		Toast toast = Toast.makeText(this, getString(R.string.plainPressAgainToExit),
		                             Toast.LENGTH_SHORT);
		if (mBackPressedHelper == 0) {
			Log.d(TAG, "onBackPressed: helper == 0");
			toast.show();
			mBackPressedHelper = 1;
			new Handler().postDelayed(() -> {
				Log.d(TAG, "run:true");
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
		Log.d(TAG, "handleLogout:true");
		FirebaseAuth.getInstance()
		            .signOut();
		finish();
	}
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick:true");
		switch (v.getId()) {
			case R.id.buttong2:
				Log.d(TAG, "onClick: send again button");
				Bundle extras = getIntent().getExtras();
				if (extras != null && extras.getParcelable("user") != null) {
					sendEmailVerification(extras.getParcelable("user"));
				}
				break;
			case R.id.buttong1:
				Log.d(TAG, "onClick: logout button");
				handleLogout();
				break;
		}
	}
}
