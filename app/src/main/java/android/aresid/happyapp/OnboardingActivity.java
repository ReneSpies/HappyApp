package android.aresid.happyapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.leanback.app.OnboardingSupportFragment;


/**
 * Created on: 24.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */


public class OnboardingActivity
		extends OnboardingSupportFragment
{
	static final String COMPLETED_ONBOARDING_PREF_NAME = "completed_onboarding";
	private static final String TAG = "OnboardingActivity";




	@Override
	protected int getPageCount()
	{
		return 0;
	}




	@Override
	protected CharSequence getPageTitle(int pageIndex)
	{
		return null;
	}




	@Override
	protected CharSequence getPageDescription(int pageIndex)
	{
		return null;
	}




	@Nullable
	@Override
	protected View onCreateBackgroundView(LayoutInflater inflater, ViewGroup container)
	{
		return null;
	}




	@Nullable
	@Override
	protected View onCreateContentView(LayoutInflater inflater, ViewGroup container)
	{
		return null;
	}




	@Nullable
	@Override
	protected View onCreateForegroundView(LayoutInflater inflater, ViewGroup container)
	{
		return null;
	}




	@Override
	protected void onFinishFragment()
	{
		Log.d(TAG, "onFinishFragment:true");
		super.onFinishFragment();

		// User has seen OnboardingActivity, so mark our SharedPreferences
		// flag as completed so that we don't show our OnboardingActivity
		// the next time the user launches the app.
		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(getContext())
		                                                                    .edit();

		sharedPreferencesEditor.putBoolean(COMPLETED_ONBOARDING_PREF_NAME, true);
		sharedPreferencesEditor.apply();
	}







}
