package android.aresid.happyapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created on: 21.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class GoogleEmailVerificationActivity
		extends AppCompatActivity {

	private static final String TAG = "GoogleEmailVerificationActivity";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_email_verification);

	}

}
