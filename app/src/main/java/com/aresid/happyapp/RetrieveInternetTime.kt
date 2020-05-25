package com.aresid.happyapp

import android.os.AsyncTask
import org.apache.commons.net.time.TimeTCPClient
import timber.log.Timber
import java.util.*

/**
 * Created on: 20.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class RetrieveInternetTime: AsyncTask<String?, Void?, Date>() {
	
	override fun onPostExecute(date: Date) {
		Timber.d("onPostExecute: called")
		Timber.d("date = $date")
	}
	
	interface OnInternetTimeInteractionListener {
		fun addTimeToFirestoreEntry(
			time: Date,
			uid: String?
		)
	}
	
	init {
		Timber.d("init: called")
	}
	
	override fun doInBackground(vararg params: String?): Date {
		Timber.d("doInBackground: called")
		val timeServer = params[0]
		return try {
			val client = TimeTCPClient()
			client.connect(timeServer)
			client.date
		}
		catch (e: Exception) {
			Timber.e(e)
			Date()
		}
	}
}