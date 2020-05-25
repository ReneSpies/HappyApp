package com.aresid.happyapp.application

import android.app.Application
import timber.log.Timber

/**
 *    Created on: 01.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class HappyApplication: Application() {
	
	/**
	 * ONLY EDIT IF REALLY NECESSARY!
	 */
	
	override fun onCreate() {
		
		super.onCreate()
		
		// Initialize Timber for logging
		Timber.plant(Timber.DebugTree())
		
	}
	
}