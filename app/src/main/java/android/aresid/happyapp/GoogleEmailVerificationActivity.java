package android.aresid.happyapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created on: 21.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class GoogleEmailVerificationActivity
		extends AppCompatActivity
		implements View.OnClickListener {

	private static final String TAG               = "GoogleEmailVerificationActivity";
	private              int    mEmailErrorHelper = 0;

	@SuppressLint ("LongLogTag")
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_email_verification);

		Button btSendAgain = findViewById(R.id.google_email_verification_activity_send_again_button);
		Button btBack = findViewById(R.id.google_email_verification_activity_back_button);

		btSendAgain.setOnClickListener(this);
		btBack.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {

			FirebaseUser user = extras.getParcelable("user");

			if (user != null) {

				checkUserStatus(user);

			} else {

				// TODO

			}

		} else {

			// TODO

		}

		sendEmailVerification();

	}

	@SuppressLint ("LongLogTag")
	private void checkUserStatus(FirebaseUser user) {

		Log.d(TAG, "checkUserStatus:true");

		new Handler().postDelayed(() -> {

			Log.d(TAG, "run:true");

			user.reload()
			    .addOnSuccessListener(command -> {

				    Log.d(TAG, "checkUserStatus: success");

				    if (user.isEmailVerified()) {

					    // TODO

				    } else {

					    checkUserStatus(user);

				    }

			    })
			    .addOnFailureListener(e -> {

				    Log.d(TAG, "checkUserStatus: failure");
				    Log.e(TAG, "checkUserStatus: ", e);

				    checkUserStatus(user);

			    });

		}, 1130);

	}

	@SuppressLint ("LongLogTag")
	private void sendEmailVerification() {

		Log.d(TAG, "sendEmailVerification:true");

		Toast toast = Toast.makeText(this, "Something went wrong sending the email. Retrying", Toast.LENGTH_LONG);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {

			FirebaseUser user = extras.getParcelable("user");

			if (user != null) {

				user.sendEmailVerification()
				    .addOnSuccessListener(command -> {

					    Log.d(TAG, "sendEmailVerification: success");

					    toast.cancel();

					    Toast.makeText(this, "Email verification sent to " + user.getEmail(), Toast.LENGTH_LONG)
					         .show();

				    })
				    .addOnFailureListener(e -> {

					    Log.d(TAG, "sendEmailVerification: failure");
					    Log.e(TAG, "sendEmailVerification: ", e);

					    toast.cancel();

					    if (mEmailErrorHelper <= 3) {

						    toast.show();

						    sendEmailVerification();

					    }

				    });

			} else {

				// TODO

			}

		} else {

			// TODO

		}

	}

	@SuppressLint ("LongLogTag")
	@Override
	public void onClick(View v) {

		Log.d(TAG, "onClick:true");

		Log.d(TAG, "onClick: initiator = " + v.getTransitionName());

		switch (v.getId()) {

			case R.id.google_email_verification_activity_send_again_button:

				sendEmailVerification();

				break;

			case R.id.google_email_verification_activity_back_button:

				super.onBackPressed();

				break;

		}

	}
}
