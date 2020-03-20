package com.aresid.happyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
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
		implements OnTextInputDialogInteractionListener {
	private static final String       TAG                = "ConfirmEmailActivity";
	private              int          mBackPressedHelper = 0;
	private              FirebaseUser mFirebaseUser;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		setTheme(R.style.Theme_HappyApp);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_email);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mFirebaseUser = extras.getParcelable(getString(R.string.firebaseUserKey));
			if (mFirebaseUser != null) {
				setEmailTextViewText(mFirebaseUser.getEmail());
				sendEmailVerification(mFirebaseUser);
				checkEmailVerificationStatus(mFirebaseUser);
			}
		} else {
			showErrorSnackbar(findViewById(R.id.confirm_email_activity_constraint_layout), getString(R.string.user_should_not_be_here));
		}
	}
	
	private void setEmailTextViewText(String email) {
		Log.d(TAG, "setEmailTextViewText: called");
		TextView emailTextView =
				findViewById(R.id.confirm_email_activity_email_textview);
		emailTextView.setText(email);
	}
	
	private void startMainActivity(FirebaseUser user) {
		Log.d(TAG, "startMainActivity: called");
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(getString(R.string.firebaseUserKey), user);
		startActivity(intent);
	}
	
	private void checkEmailVerificationStatus(FirebaseUser user) {
		Log.d(TAG, "checkEmailVerificationStatus: called");
		new Handler().postDelayed(() -> {
			Log.d(TAG, "run: called");
			user.reload()
			    .addOnSuccessListener(command -> {
				    Log.d(TAG, "checkEmailVerificationStatus: great success");
				    if (user.isEmailVerified()) {
					    startMainActivity(user);
				    } else {
					    checkEmailVerificationStatus(user);
				    }
			    })
			    .addOnFailureListener(e -> {
				    Log.d(TAG, "checkEmailVerificationStatus: failure");
				    Log.e(TAG, "checkEmailVerificationStatus: ", e);
				    checkEmailVerificationStatus(user);
			    });
		}, 5000);
	}
	
	private void showStandardSnackbar(View view, String message) {
		Log.d(TAG, "showStandardSnackbar: called");
		Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
		        .setBackgroundTint(ContextCompat.getColor(this, R.color.white))
		        .setTextColor(ContextCompat.getColor(this, R.color.black))
		        .show();
	}
	
	private FirebaseUser getCurrentUser() {
		Log.d(TAG, "getCurrentUser: called");
		return mFirebaseUser;
	}
	
	public void onLogoutButtonClick(View view) {
		Log.d(TAG, "onLogoutButtonClick: called");
		handleLogout();
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
	
	public void onChangeAndResendButtonClick(View view) {
		Log.d(TAG, "onChangeAndResendButtonClick: called");
		// TODO: Show TextInputDialog and receive data from it and then change the email
		//  and resend the confirmation
		TextInputDialog inputDialog = new TextInputDialog();
		inputDialog.show(getSupportFragmentManager(), "Change email dialog");
	}
	
	private void handleLogout() {
		Log.d(TAG, "handleLogout: called");
		FirebaseAuth.getInstance()
		            .signOut();
		finish();
	}
	
	public void onResendButtonClick(View view) {
		Log.d(TAG, "onResendButtonClick: called");
		sendEmailVerification(mFirebaseUser);
	}
	
	private void sendEmailVerification(FirebaseUser user) {
		Log.d(TAG, "sendEmailVerification: called");
		ConstraintLayout snackbarView =
				findViewById(R.id.confirm_email_activity_constraint_layout);
		if (user != null) {
			user.sendEmailVerification()
			    .addOnSuccessListener(command -> {
				    Log.d(TAG, "sendEmailVerification: great success");
			    })
			    .addOnFailureListener(e -> {
				    Log.d(TAG, "sendEmailVerification: failure");
				    Log.e(TAG, "sendEmailVerification: ", e);
				    if (e instanceof com.google.firebase.FirebaseTooManyRequestsException) {
					    showErrorSnackbar(snackbarView,
					                      getString(R.string.too_many_attempts));
					    return;
				    }
			    });
		} else {
			showErrorSnackbar(snackbarView, getString(R.string.user_should_not_be_here));
		}
	}
	
	private void showErrorSnackbar(View view, String message) {
		Log.d(TAG, "showErrorSnackbar: called");
		Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
		        .setBackgroundTint(ContextCompat.getColor(this,
		                                                  R.color.design_default_color_error))
		        .show();
	}
	
	public void onOpenEmailButtonClicked(View view) {
		Log.d(TAG, "onOpenEmailButtonClicked: called");
		// Starts default email app on users phone or shows error message
		Intent emailIntent = new Intent(Intent.ACTION_MAIN);
		emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
		if (emailIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(emailIntent);
		} else {
			showErrorSnackbar(view, getString(R.string.no_suitable_app_found));
		}
	}
	
	@Override
	public void transferTextInputText(String text) {
		Log.d(TAG, "transferTextInputText: called");
		changeEmail(text);
	}
	
	private void changeEmail(String email) {
		Log.d(TAG, "changeEmail: called");
		FirebaseUser user = mFirebaseUser;
		if (user == null) {
			View snackbarView =
					findViewById(R.id.confirm_email_activity_constraint_layout);
			showErrorSnackbar(snackbarView, getString(R.string.user_should_not_be_here));
			return;
		}
		changeToLoadingScreen();
		user.updateEmail(email)
		    .addOnSuccessListener(aVoid -> {
			    Log.d(TAG, "changeEmail: great success");
			    changeFromLoadingScreen();
			    updateEmailView(user.getEmail());
			    sendEmailVerification(user);
		    })
		    .addOnFailureListener(e -> {
			    Log.d(TAG, "changeEmail: failure");
			    Log.e(TAG, "changeEmail: ", e);
			    changeFromLoadingScreen();
			    // TODO: Error handling
		    });
	}
	
	private void changeToLoadingScreen() {
		Log.d(TAG, "changeToLoadingScreen: called");
		View loadingScreen = findViewById(R.id.loading_screen);
		View mainView = findViewById(R.id.main_view);
		loadingScreen.setVisibility(View.VISIBLE);
		mainView.setVisibility(View.GONE);
	}
	
	private void changeFromLoadingScreen() {
		Log.d(TAG, "changeFromLoadingScreen: called");
		View loadingScreen = findViewById(R.id.loading_screen);
		View mainView = findViewById(R.id.main_view);
		loadingScreen.setVisibility(View.GONE);
		mainView.setVisibility(View.VISIBLE);
	}
	
	private void updateEmailView(String email) {
		Log.d(TAG, "updateEmailView: called");
		TextView emailText = findViewById(R.id.confirm_email_activity_email_textview);
		emailText.setText(email);
	}
}
