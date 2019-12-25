package com.aresid.happyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity
		extends AppCompatActivity {

	private final static String TAG = "SplashActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		setTheme(R.style.Theme_HappyApp);

		super.onCreate(savedInstanceState);

		startActivity(new Intent(this, EntryActivity.class));

		finish();

	}
}
