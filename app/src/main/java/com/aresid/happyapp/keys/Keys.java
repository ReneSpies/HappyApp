package com.aresid.happyapp.keys;

/**
 * Created on: 18/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */

public class Keys {
	
	/**
	 * Carries the keys for the Bundles.
	 */
	public static class BundleKeys {
		
		// No key. This is the default value
		public static final String DEFAULT_VALUE = "404";
		
		// Keys
		public static final String KEY_FIRST_NAME       = "happyapp.happyapp.bundle.keys.first_name";
		public static final String KEY_FAMILY_NAME      = "happyapp.happyapp.bundle.keys.family_name";
		public static final String KEY_USERNAME         = "happyapp.happyapp.bundle.keys.username";
		public static final String KEY_EMAIL            = "happyapp.happyapp.bundle.keys.email";
		public static final String KEY_DATE_OF_BIRTH    = "happyapp.happyapp.bundle.keys.date_of_birth";
		public static final String KEY_GOOGLE_SIGNUP_ID = "happyapp.happyapp.bundle.keys.google_signup";
		public static final String KEY_EMAIL_SIGNUP_ID  = "happyapp.happyapp.bundle.keys.email_signup";
		
		// IDs
		public static final String GOOGLE_SIGNUP_ID = "happyapp.happyapp.bundle.ids.google_signup";
		public static final String EMAIL_SIGNUP_ID  = "happyapp.happyapp.bundle.ids.email_signup";
		
	}
	
	/**
	 * Carries the keys for the HashMaps.
	 */
	public static class HashmapKeys {
		
		// Keys
		public static final String KEY_FIRST_NAME    = "happyapp.happyapp.hashmap.keys.first_name";
		public static final String KEY_FAMILY_NAME   = "happyapp.happyapp.hashmap.keys.family_name";
		public static final String KEY_USERNAME      = "happyapp.happyapp.hashmap.keys.username";
		public static final String KEY_EMAIL         = "happyapp.happyapp.hashmap.keys.email";
		public static final String KEY_PASSWORD      = "happyapp.happyapp.hashmap.keys.password";
		public static final String KEY_DATE_OF_BIRTH = "happyapp.happyapp.hashmap.keys.date_of_birth";
		
	}
	
	/**
	 * Carries the keys for the Firestore fields.
	 */
	public static class FirestoreFieldKeys {
		
		// Keys
		// Collections
		public static final String KEY_COLLECTION_SUBSCRIPTION = "subscription";
		public static final String KEY_COLLECTION_USERS        = "users";
		
		// Columns
		public static final String KEY_COLUMN_DATE_OF_BIRTH        = "date of birth";
		public static final String KEY_COLUMN_DATE_OF_CREATION     = "date of creation";
		public static final String KEY_COLUMN_FAMILY_NAME          = "family name";
		public static final String KEY_COLUMN_FIRST_NAME           = "first name";
		public static final String KEY_COLUMN_EMAIL                = "email";
		public static final String KEY_COLUMN_PROFILE_PICTURE      = "profile picture";
		public static final String KEY_COLUMN_SUBSCRIPTION_VARIANT = "subscription variant";
		public static final String KEY_COLUMN_USERNAME             = "username";
		
	}
	
}
