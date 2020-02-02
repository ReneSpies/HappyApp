package com.aresid.happyapp;

import android.content.Context;
import android.util.Log;

/**
 * Created on: 06/12/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class Cacher {
	private static final String  TAG = "Cacher";
	private              Context mContext;
	Cacher(Context context) {
		Log.d(TAG, "Cacher:true");
		mContext = context;
	}
	void cache() {
		Log.d(TAG, "cache:true");
	}
}
