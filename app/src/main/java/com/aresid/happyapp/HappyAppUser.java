package com.aresid.happyapp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

/**
 * Created on: 01/12/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class HappyAppUser {
	private static final String                        TAG = "HappyAppUser";
	private              Uri                           mProfilePictureUrl;
	private              String                        mFirstName;
	private              String                        mFamilyName;
	private              String                        mDateOfBirth;
	private              String                        mUsername;
	private              Subscription                  mSubscriptionVariant;
	private              HashMap<Subscription, String> mSubscriptionVariants;
	private              String                        mDateOfCreation;
	private              String                        mUid;
	
	/**
	 * Empty Constructor.
	 */
	HappyAppUser() {
		Log.d(TAG, "HappyAppUser: empty constructor");
		init();
	}
	
	@SuppressLint ("UseSparseArrays")
	private void init() {
		Log.d(TAG, "init:true");
		mSubscriptionVariants = new HashMap<>();
	}
	
	/**
	 * Constructor 1.
	 *
	 * @param uid                 Users unique id.
	 * @param firstName           Users first name_with_placeholder.
	 * @param familyName          Users family name_with_placeholder.
	 * @param dateOfBirth         Users date of birth.
	 * @param username            Users username.
	 * @param subscriptionVariant Users subscription variant.
	 * @param profilePictureUrl   Users profile picture.
	 */
	HappyAppUser(String uid, String firstName, String familyName, String dateOfBirth, String username,
	             Subscription subscriptionVariant, Uri profilePictureUrl) {
		Log.d(TAG, "HappyAppUser: constructor 1");
		mUid = uid;
		init();
		mFirstName = firstName;
		mFamilyName = familyName;
		mDateOfBirth = dateOfBirth;
		mUsername = username;
		mSubscriptionVariant = subscriptionVariant;
		mProfilePictureUrl = profilePictureUrl;
	}
	
	/**
	 * Saves the current user information to the firestore.
	 */
	void saveToFirestore() {
		Log.d(TAG, "saveToFirestore:true");
	}
	
	/**
	 * Getter for users first name_with_placeholder.
	 *
	 * @return Guess.
	 */
	public String getFirstName() {
		return mFirstName;
	}
	
	/**
	 * Setter for users first name_with_placeholder.
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
	public Uri getProfilePictureUrl() {
		return mProfilePictureUrl;
	}
	
	/**
	 * Setter for profile picture.
	 *
	 * @param profilePictureUrl Guess.
	 */
	public void setProfilePictureUrl(Uri profilePictureUrl) {
		mProfilePictureUrl = profilePictureUrl;
	}
	
	/**
	 * Getter for users family name_with_placeholder.
	 *
	 * @return Guess.
	 */
	public String getFamilyName() {
		return mFamilyName;
	}
	
	/**
	 * Setter for users family name_with_placeholder.
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
	public Subscription getSubscriptionVariant() {
		return mSubscriptionVariant;
	}
	
	/**
	 * Setter for users subscription variant.
	 *
	 * @param subscriptionVariant Guess.
	 */
	public void setSubscriptionVariant(Subscription subscriptionVariant) {
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
	
	/**
	 * Getter for users unique id.
	 *
	 * @return Guess.
	 */
	public String getUid() {
		return mUid;
	}
	
	/**
	 * Setter for users unique id.
	 *
	 * @param uid Guess.
	 */
	public void setUid(String uid) {
		mUid = uid;
	}
}
