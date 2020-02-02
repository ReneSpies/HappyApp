package com.aresid.happyapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class OnboardingActivity
		extends AppCompatActivity
		implements AccountLevelFragment.OnFragmentInteractionListener {
	private static final String TAG                       = "OnboardingActivity";
	private              double doubleOnBackPressedHelper = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate:true");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onboarding);
		new DisplayFragment(this).displayFragment(R.id.onboarding_fragment_container, AccountLevelFragment.newInstance());
	}
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed:true");
		if (doubleOnBackPressedHelper == 0) {
			Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT)
			     .show();
			doubleOnBackPressedHelper = 1;
			// A Timer that resets my onBackPressed helper to 0 after 6.13 seconds.
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					Log.d(TAG, "run:true");
					doubleOnBackPressedHelper = 0;
				}
			}, 6130);
		} else {
			if (doubleOnBackPressedHelper == 1) {
				finishAffinity();
			}
		}
	}
}
