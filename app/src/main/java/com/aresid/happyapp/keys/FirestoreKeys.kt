package com.aresid.happyapp.keys

/**
 *    Created on: 14.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

object FirestoreKeys {
	
	object Collection {
		
		/* Following the ARES ID naming convention for Firestore collections, PascalCase is used */
		
		const val USERS = "Users"
		
		object Column {
			
			/* Following the ARES ID naming convention for Firestore columns, snake_case is used */
			
			const val DATE_OF_BIRTH = "date_of_birth"
			const val DATE_OF_CREATION = "date_of_creation"
			const val FAMILY_NAME = "family_name"
			const val FIRST_NAME = "first_name"
			const val EMAIL = "email"
			const val PROFILE_PICTURE = "profile_picture"
			const val SUBSCRIPTION_VARIANT = "subscription_variant"
			const val USERNAME = "username"
			
		}
		
	}
	
}