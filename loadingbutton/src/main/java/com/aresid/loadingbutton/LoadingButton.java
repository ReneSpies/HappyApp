package com.aresid.loadingbutton;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;

import com.google.android.material.button.MaterialButton;

/**
 * Created on: 29/12/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class LoadingButton
		extends MaterialButton {

	private final static String TAG = "LoadingButton";

	public LoadingButton(Context context) {

		super(context);

	}

	public LoadingButton(Context context, AttributeSet attrs) {

		super(context, attrs);

	}

	public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);

	}

	void finishLoading() {

		Log.d(TAG, "finishLoading:true");

	}

	void startLoading(Drawable gif) {

		Log.d(TAG, "startLoading: with gif");

	}

	void startLoading(Animation loadingAnimation) {

		Log.d(TAG, "startLoading: with animation");

	}

	void startLoading() {

		Log.d(TAG, "startLoading: with progressbar");

	}
	
}
