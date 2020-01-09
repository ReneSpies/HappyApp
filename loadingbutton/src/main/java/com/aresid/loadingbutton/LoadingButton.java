package com.aresid.loadingbutton;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.material.button.MaterialButton;

/**
 * Created on: 29/12/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class LoadingButton
		extends MaterialButton {

	private boolean isShadowEnabled  = true;
	private boolean isCircularButton = false;
	private int     mButtonColor;
	private int     mloaderColor;
	private int     mShadowColor;
	private int     mShadowHeight;
	private int     mCornerRadius;
	private boolean isLoading        = false;
	private String  text             = "";

	private final static String TAG = "LoadingButton";

	public LoadingButton(Context context) {

		super(context);

		init();

	}

	void init() {

		Log.d(TAG, "init:true");

		// Init default values
		isShadowEnabled = true;

		Resources resources = getResources();

		if (resources == null) { return; }

//		mButtonColor = resources.getColor(R.color.fbutton_default_color);
//		mShadowColor = resources.getColor(R.color.fbutton_default_shadow_color);
//		mShadowHeight = resources.getDimensionPixelSize(R.dimen.fbutton_default_shadow_height);
//		mCornerRadius = resources.getDimensionPixelSize(R.dimen.fbutton_default_corner_radius);

		text = getText().toString();

	}

	public LoadingButton(Context context, AttributeSet attrs) {

		super(context, attrs);

		init();

	}

	public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);

		init();

	}

}
