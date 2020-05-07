package com.aresid.happyapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import org.apache.commons.net.ntp.NTPUDPClient
import timber.log.Timber
import java.net.InetAddress
import java.util.*

/**
 * Created on: 20.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class RetrieveInternetTime internal constructor(
	context: AppCompatActivity,
	uid: String
): AsyncTask<String?, Void?, Date>() {
	
	private var mListener: OnInternetTimeInteractionListener? = null
	private val mUid: String
	
	override fun onPostExecute(time: Date) {
		Timber.d("onPostExecute: called")
		
		mListener!!.addTimeToFirestoreEntry(
			time,
			mUid
		)
	}
	
	interface OnInternetTimeInteractionListener {
		fun addTimeToFirestoreEntry(
			time: Date,
			uid: String?
		)
	}
	
	companion object {
		private const val TAG = "RetrieveInternetTime"
	}
	
	init {
		Timber.d("init: called")
		
		mListener = if (context is OnInternetTimeInteractionListener) {
			context
		}
		else {
			throw RuntimeException(
				"$context must implement OnInternetTimeInteractionListener"
			)
		}
		mUid = uid
	}
	
	override fun doInBackground(vararg params: String?): Date {
		Timber.d("doInBackground: called")
		val timeServer = params[0]
		return try {
			// Define NTPUDPClient
			val client = NTPUDPClient()
			// Define InetAddress Object from time server name received in the parameters
			val address = InetAddress.getByName(timeServer)
			// Define TimeInfo Object from clients time
			val timeInfo = client.getTime(address)
			// Get the time from the clients time info
			val returnTime = timeInfo.message.originateTimeStamp.time
			// Translate the return time long into a Date Object and return it
			Date(returnTime)
		}
		catch (e: Exception) {
			Timber.e(e)
			// If an error occurs, just return the devices local time
			Date()
		}
	}
}