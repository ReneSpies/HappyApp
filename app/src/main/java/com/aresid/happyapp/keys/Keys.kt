package com.aresid.happyapp.keys

/**
 * Created on: 18/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class Keys {
	
	/**
	 * Carries the keys for the Bundles.
	 */
	object BundleKeys {
		
		// No key. This is the default value
		const val DEFAULT_VALUE = "404"
		
		// Keys
		const val KEY_FIRST_NAME = "happyapp.happyapp.bundle.keys.first_name"
		const val KEY_FAMILY_NAME = "happyapp.happyapp.bundle.keys.family_name"
		const val KEY_USERNAME = "happyapp.happyapp.bundle.keys.username"
		const val KEY_EMAIL = "happyapp.happyapp.bundle.keys.email"
		const val KEY_DATE_OF_BIRTH = "happyapp.happyapp.bundle.keys.date_of_birth"
		const val KEY_GOOGLE_SIGNUP_ID = "happyapp.happyapp.bundle.keys.google_signup"
		const val KEY_EMAIL_SIGNUP_ID = "happyapp.happyapp.bundle.keys.email_signup"
		
		// IDs
		const val GOOGLE_SIGNUP_ID = "happyapp.happyapp.bundle.ids.google_signup"
		const val EMAIL_SIGNUP_ID = "happyapp.happyapp.bundle.ids.email_signup"
	}
	
	/**
	 * Carries the keys for the HashMaps.
	 */
	object HashmapKeys {
		
		// Keys
		const val KEY_FIRST_NAME = "happyapp.happyapp.hashmap.keys.first_name"
		const val KEY_FAMILY_NAME = "happyapp.happyapp.hashmap.keys.family_name"
		const val KEY_USERNAME = "happyapp.happyapp.hashmap.keys.username"
		const val KEY_EMAIL = "happyapp.happyapp.hashmap.keys.email"
		const val KEY_PASSWORD = "happyapp.happyapp.hashmap.keys.password"
		const val KEY_DATE_OF_BIRTH = "happyapp.happyapp.hashmap.keys.date_of_birth"
	}
	
	/**
	 * Carries the keys for the Firestore fields.
	 */
	object FirestoreFieldKeys {
		
		// Keys
		// Collections
		const val KEY_COLLECTION_SUBSCRIPTION = "subscription"
		const val KEY_COLLECTION_USERS = "users"
		
		// Columns
		const val KEY_COLUMN_DATE_OF_BIRTH = "date of birth"
		const val KEY_COLUMN_DATE_OF_CREATION = "date of creation"
		const val KEY_COLUMN_FAMILY_NAME = "family name"
		const val KEY_COLUMN_FIRST_NAME = "first name"
		const val KEY_COLUMN_EMAIL = "email"
		const val KEY_COLUMN_PROFILE_PICTURE = "profile picture"
		const val KEY_COLUMN_SUBSCRIPTION_VARIANT = "subscription variant"
		const val KEY_COLUMN_USERNAME = "username"
	}
	
	/**
	 * Carries the RequestCodes.
	 * RequestCode creation policy is only odd numbers!
	 */
	object RequestCodes {
		
		const val REQUEST_CODE_GOOGLE_SIGN_IN = 1
		
	}
	
	/**
	 * Carries the IDs of the SKUs from the Google Play Console.
	 */
	object HappyAppSkus {
		
		// Bronze subscription
		const val BRONZE_SUBSCRIPTION = "happyapp.subscription.bronze"
		
		// Silver subscription
		const val SILVER_SUBSCRIPTION = "happyapp.subscription.silver"
		
		// Gold subscription
		const val GOLD_SUBSCRIPTION = "happyapp.subscription.gold"
		
		// Platinum subscription
		const val PLATINUM_SUBSCRIPTION = "happyapp.subscription.platinum"
		
		// All SKU IDs as a list
		val SUBSCRIPTION_SKUS = listOf(
			BRONZE_SUBSCRIPTION,
			SILVER_SUBSCRIPTION,
			GOLD_SUBSCRIPTION,
			PLATINUM_SUBSCRIPTION
		)
		
	}
	
}