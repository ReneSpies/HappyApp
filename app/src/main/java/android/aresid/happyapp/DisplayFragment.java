package android.aresid.happyapp;


import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Created on: 01.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */


public class DisplayFragment
{
	private static final String TAG = "DisplayFragment";

	private FragmentActivity mFragmentActivity;




	DisplayFragment(FragmentActivity fragmentActivity)
	{
		Log.d(TAG, "DisplayFragment:true");
		mFragmentActivity = fragmentActivity;
	}




	void displayFragment(int containerResource, Fragment fragment)
	{
		Log.d(TAG, "displayFragment:true");

		mFragmentActivity.getSupportFragmentManager()
		                 .beginTransaction()
		                 .replace(containerResource, fragment)
		                 .commit();
	}







}
