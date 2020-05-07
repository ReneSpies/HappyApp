package com.aresid.happyapp

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import java.util.*

/**
 * Created on: 01/12/2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class HappyAppUser {
	
	/**
	 * Getter for users profile picture.
	 *
	 * @return Guess.
	 */
	/**
	 * Setter for profile picture.
	 *
	 * @param profilePictureUrl Guess.
	 */
	var profilePictureUrl: Uri? = null
	private var mFirstName: String? = null
	/**
	 * Getter for users family name_with_placeholder.
	 *
	 * @return Guess.
	 */
	/**
	 * Setter for users family name_with_placeholder.
	 *
	 * @param familyName Guess.
	 */
	var familyName: String? = null
	/**
	 * Getter for users date of birth.
	 *
	 * @return Guess.
	 */
	/**
	 * Setter for users date of birth.
	 *
	 * @param dateOfBirth Guess.
	 */
	var dateOfBirth: String? = null
	/**
	 * Getter for users username.
	 *
	 * @return Guess.
	 */
	/**
	 * Setter for users username.
	 *
	 * @param username Guess.
	 */
	var username: String? = null
	/**
	 * Getter for users subscription variant.
	 *
	 * @return Guess.
	 */
	/**
	 * Setter for users subscription variant.
	 *
	 * @param subscriptionVariant Guess.
	 */
	var subscriptionVariant: Subscription? = null
	private var mSubscriptionVariants: HashMap<Subscription, String>? = null
	/**
	 * Getter for users date of creation.
	 *
	 * @return Guess.
	 */
	/**
	 * Setter for users date of creation.
	 *
	 * @param dateOfCreation Guess.
	 */
	var dateOfCreation: String? = null
	/**
	 * Getter for users unique id.
	 *
	 * @return Guess.
	 */
	/**
	 * Setter for users unique id.
	 *
	 * @param uid Guess.
	 */
	var uid: String? = null
	
	/**
	 * Empty Constructor.
	 */
	internal constructor() {
		Log.d(
			TAG,
			"HappyAppUser: empty constructor"
		)
		init()
	}
	
	@SuppressLint("UseSparseArrays")
	private fun init() {
		Log.d(
			TAG,
			"init:true"
		)
		mSubscriptionVariants = HashMap()
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
	internal constructor(
		uid: String?,
		firstName: String?,
		familyName: String?,
		dateOfBirth: String?,
		username: String?,
		subscriptionVariant: Subscription?,
		profilePictureUrl: Uri?
	) {
		Log.d(
			TAG,
			"HappyAppUser: constructor 1"
		)
		this.uid = uid
		init()
		mFirstName = firstName
		this.familyName = familyName
		this.dateOfBirth = dateOfBirth
		this.username = username
		this.subscriptionVariant = subscriptionVariant
		this.profilePictureUrl = profilePictureUrl
	}
	
	/**
	 * Saves the current user information to the firestore.
	 */
	fun saveToFirestore() {
		Log.d(
			TAG,
			"saveToFirestore:true"
		)
	}
	
	/**
	 * Getter for users first name_with_placeholder.
	 *
	 * @return Guess.
	 */
	/**
	 * Setter for users first name_with_placeholder.
	 *
	 * @param firstName Guess.
	 */
	var firstName: String?
		get() = mFirstName
		set(firstName) {
			Log.d(
				TAG,
				"setFirstName:true"
			)
		}
	
	companion object {
		private const val TAG = "HappyAppUser"
	}
}