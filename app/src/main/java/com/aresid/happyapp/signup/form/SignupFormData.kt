package com.aresid.happyapp.signup.form

import timber.log.Timber

/**
 *    Created on: 14.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SignupFormData {
	
	// FirstName
	var firstName = ""
	
	// FirstName
	var familyName = ""
	
	// FirstName
	var username = ""
	
	// FirstName
	var email = ""
	
	// FirstName
	var dateOfBirth = ""
	
	// FirstName
	var password = ""
	
	companion object {
		
		private lateinit var INSTANCE: SignupFormData
		
		/**
		 * Returns a singleton for SignupFormData.
		 */
		fun getInstance(): SignupFormData {
			
			Timber.d("getInstance: called")
			
			// Make sure it is thread save
			synchronized(SignupFormData::class.java) {
				
				// If it ain't initialized yet, init
				if (!::INSTANCE.isInitialized) {
					
					INSTANCE = SignupFormData()
					
				}
				
			}
			
			return INSTANCE
			
		}
		
	}
	
}
