package android.aresid.happyapp;


import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Created on: 19.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */


public class PrivacyPolicy
		extends Legalities
{
	private static final String TAG = "PrivacyPolicy";
	private String mTitle, mContent;




	/**
	 * Constructor.
	 *
	 * @param title   Title for privacy policy.
	 * @param content Content for privacy policy.
	 */
	PrivacyPolicy(String title, String content)
	{
		super(title, content);
		Log.d(TAG, "PrivacyPolicy:true");
		mTitle = title;
		mContent = content;
	}




	/**
	 * Getter for title of privacy policy.
	 *
	 * @return Title of privacy policy.
	 */
	@Override
	public String getTitle()
	{
		Log.d(TAG, "getTitle:true");
		return mTitle;
	}




	/**
	 * Setter for title for privacy policy.
	 *
	 * @param title Title for privacy policy.
	 */
	@Override
	public void setTitle(@NonNull String title)
	{
		Log.d(TAG, "setTitle:true");
		mTitle = title;
	}




	/**
	 * Getter for content of privacy policy.
	 *
	 * @return Content of privacy policy.
	 */
	@Override
	public String getContent()
	{
		Log.d(TAG, "getContent:true");
		return mContent;
	}




	/**
	 * Setter for content for privacy policy.
	 *
	 * @param content Content for privacy policy.
	 */
	@Override
	public void setContent(@NonNull String content)
	{
		Log.d(TAG, "setContent:true");
		mContent = content;
	}







}
