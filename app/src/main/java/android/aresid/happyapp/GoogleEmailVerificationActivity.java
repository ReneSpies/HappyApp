package android.aresid.happyapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on: 21.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class GoogleEmailVerificationActivity
		extends AppCompatActivity
		implements View.OnClickListener,
		           ViewPagerAdapter.OnViewPagerInteractionListener {

	private static final String TAG                = "GmailVerificationAct";
	private              int    mEmailErrorHelper  = 0;
	private              int    mBackPressedHelper = 0;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_email_verification);

		List<String> listOfTitles = new ArrayList<>();
		List<String> listOfDescriptions = new ArrayList<>();
		List<String> listOfPriceTags = new ArrayList<>();

		// List population for HappyApp Free
		listOfTitles.add(EntryActivity.VP_TITLE_HAPPYAPP_FREE);
		listOfDescriptions.add(EntryActivity.VP_DESC_HAPPYAPP_FREE);
		listOfPriceTags.add(EntryActivity.VP_PRICE_HAPPYAPP_FREE);

		// List population for HappyApp Gold
		listOfTitles.add(EntryActivity.VP_TITLE_HAPPYAPP_GOLD);
		listOfDescriptions.add(EntryActivity.VP_DESC_HAPPYAPP_GOLD);
		listOfPriceTags.add(EntryActivity.VP_PRICE_HAPPYAPP_GOLD);

		ViewPager2 vpSubscriptions = findViewById(R.id.google_email_verification_activity_subscription_view_pager);
		Button btSendAgain = findViewById(R.id.google_email_verification_activity_send_again_button);
		TextInputEditText etGmailVerificationDobField = findViewById(R.id.google_email_verification_activity_dob_field);
		Button btLogout = findViewById(R.id.google_email_verification_activity_logout_button);

		etGmailVerificationDobField.setOnClickListener(this);
		etGmailVerificationDobField.setOnFocusChangeListener((v, hasFocus) -> {

			if (hasFocus) {

				Log.d(TAG, "onCreate: has focus");

				new DatePickerFragment(this, (EditText) v).show(getSupportFragmentManager(), "date picker");

			}

		});
		etGmailVerificationDobField.setKeyListener(null);
		vpSubscriptions.setAdapter(new ViewPagerAdapter(this, listOfTitles, listOfDescriptions, listOfPriceTags, vpSubscriptions));
		btSendAgain.setOnClickListener(this);
		btLogout.setOnClickListener(this);

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

					    if (e instanceof com.google.firebase.FirebaseTooManyRequestsException) {

						    toast.cancel();

						    Toast.makeText(this, "I never expected it would take so long to get donuts. Better be patient", Toast.LENGTH_SHORT)
						         .show();

						    return;

					    }

					    if (mEmailErrorHelper <= 3) {

						    toast.show();

						    sendEmailVerification();

						    mEmailErrorHelper++;

					    }

				    });

			} else {

				// TODO

			}

		} else {

			// TODO

		}

	}

	@Override
	public void onBackPressed() {

		Log.d(TAG, "onBackPressed:true");

		Toast toast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT);

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

//		Intent intent = new Intent(this, EntryActivity.class);
//
//		startActivity(intent);
//
	}

	@Override
	public void onClick(View v) {

		Log.d(TAG, "onClick:true");

		switch (v.getId()) {

			case R.id.google_email_verification_activity_send_again_button:

				Log.d(TAG, "onClick: send again button");

				sendEmailVerification();

				break;

			case R.id.google_email_verification_activity_logout_button:

				Log.d(TAG, "onClick: logout button");

				handleLogout();

				break;

			case R.id.google_email_verification_activity_dob_field:

				Log.d(TAG, "onClick: dob field");

				new DatePickerFragment(this, (EditText) v).show(getSupportFragmentManager(), "date picker");

				break;
		}

	}

	@Override
	public void createUser(double requestCode) {

		Log.d(TAG, "createUser:true");

	}
}
