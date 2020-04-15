package com.aresid.happyapp.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aresid.happyapp.R;

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
public class MainActivity2
		extends AppCompatActivity {
	
	private static final String TAG = "MainActivity2";
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreate: called");
		setTheme(R.style.Theme_HappyApp);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
}
