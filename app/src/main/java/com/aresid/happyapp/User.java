package com.aresid.happyapp;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.HashMap;

/**
 * Created on: 01/12/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class User {

	private static final String TAG = "User";

	private Drawable                 mProfilePicture;
	private String                   mFirstName;
	private String                   mFamilyName;
	private String                   mDateOfBirth;
	private String                   mUsername;
	private int                      mSubscriptionVariant = 0;
	private HashMap<Integer, String> mSubscriptionVariants;
	private String                   mDateOfCreation;

	/**
	 * Empty Constructor.
	 */
	User() {

		Log.d(TAG, "User: empty constructor");

		init();

	}

	@SuppressLint ("UseSparseArrays")
	private void init() {

		Log.d(TAG, "init:true");

		mSubscriptionVariants = new HashMap<>();
		mSubscriptionVariants.put(1, "HappyApp Bronze");
		mSubscriptionVariants.put(2, "HappyApp Silver");
		mSubscriptionVariants.put(3, "HappyApp Gold");
		mSubscriptionVariants.put(4, "HappyApp Platinum");
		mSubscriptionVariants.put(13, "HappyApp Diamond");

	}

	/**
	 * Constructor 1.
	 *
	 * @param firstName           Users first name.
	 * @param familyName          Users family name.
	 * @param dateOfBirth         Users date of birth.
	 * @param username            Users username.
	 * @param subscriptionVariant Users subscription variant.
	 * @param profilePicture      Users profile picture.
	 */
	User(String firstName, String familyName, String dateOfBirth, String username, int subscriptionVariant, Drawable profilePicture) {

		Log.d(TAG, "User: constructor 1");

		init();

		mFirstName = firstName;
		mFamilyName = familyName;
		mDateOfBirth = dateOfBirth;
		mUsername = username;
		mSubscriptionVariant = subscriptionVariant;
		mProfilePicture = profilePicture;

	}

	/**
	 * Getter for users first name.
	 *
	 * @return Guess.
	 */
	public String getFirstName() {

		return mFirstName;
	}

	/**
	 * Setter for users first name.
	 *
	 * @param firstName Guess.
	 */
	void setFirstName(String firstName) {

		Log.d(TAG, "setFirstName:true");

	}

	/**
	 * Getter for users profile picture.
	 *
	 * @return Guess.
	 */
	public Drawable getProfilePicture() {

		return mProfilePicture;
	}

	/**
	 * Setter for profile picture.
	 *
	 * @param profilePicture Guess.
	 */
	public void setProfilePicture(Drawable profilePicture) {

		mProfilePicture = profilePicture;
	}

	/**
	 * Getter for users family name.
	 *
	 * @return Guess.
	 */
	public String getFamilyName() {

		return mFamilyName;
	}

	/**
	 * Setter for users family name.
	 *
	 * @param familyName Guess.
	 */
	public void setFamilyName(String familyName) {

		mFamilyName = familyName;
	}

	/**
	 * Getter for users date of birth.
	 *
	 * @return Guess.
	 */
	public String getDateOfBirth() {

		return mDateOfBirth;
	}

	/**
	 * Setter for users date of birth.
	 *
	 * @param dateOfBirth Guess.
	 */
	public void setDateOfBirth(String dateOfBirth) {

		mDateOfBirth = dateOfBirth;
	}

	/**
	 * Getter for users username.
	 *
	 * @return Guess.
	 */
	public String getUsername() {

		return mUsername;
	}

	/**
	 * Setter for users username.
	 *
	 * @param username Guess.
	 */
	public void setUsername(String username) {

		mUsername = username;
	}

	/**
	 * Getter for users subscription variant.
	 *
	 * @return Guess.
	 */
	public int getSubscriptionVariant() {

		return mSubscriptionVariant;
	}

	/**
	 * Setter for users subscription variant.
	 *
	 * @param subscriptionVariant Guess.
	 */
	public void setSubscriptionVariant(int subscriptionVariant) {

		mSubscriptionVariant = subscriptionVariant;
	}

	/**
	 * Getter for users date of creation.
	 *
	 * @return Guess.
	 */
	public String getDateOfCreation() {

		return mDateOfCreation;
	}

	/**
	 * Setter for users date of creation.
	 *
	 * @param dateOfCreation Guess.
	 */
	public void setDateOfCreation(String dateOfCreation) {

		mDateOfCreation = dateOfCreation;
	}

}
