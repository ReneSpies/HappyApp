package com.aresid.happyapp.utils

import org.apache.commons.net.ntp.NTPUDPClient
import org.apache.commons.net.ntp.TimeStamp
import timber.log.Timber
import java.io.IOException
import java.net.InetAddress

/**
 *    Created on: 24.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class NTPDate {
	
	companion object {
		
		/**
		 * Connects to the internet using Apache Commons-Net and uses the
		 * Google Time Server to retrieve the time and returns it.
		 */
		@Throws(IOException::class)
		fun getTimeInMillis(): Long {
			
			// A list holding all servers to get the internet time from
			val serverList = listOf(
				"time1.google.com",
				"time2.google.com",
				"time3.google.com",
				"time4.google.com"
			)
			
			Timber.d("getDate: called")
			
			// Define NPTUDPClient object
			val client = NTPUDPClient()
			
			// Set the default timeout to 1 second,
			// we don't want the user to wait for too long,
			// just to get the time from the internet
			client.defaultTimeout = 1000
			
			// Open the client
			client.open()
			
			// Iterate through the list of Google Time Server
			for (server in serverList) {
				
				try {
					
					// Define a InetAddress object from the servers address
					val inetAddress = InetAddress.getByName(server)
					
					// Define a TimeInfo object from the clients time it got from the InetAddress
					val timeInfo = client.getTime(inetAddress)
					
					// Define a TimeStamp object from the TimeInfo's time
					val ntpTime = TimeStamp.getNtpTime(timeInfo.returnTime)
					
					// Return the time
					return ntpTime.time
					
				}
				catch (e: IOException) {
					
					Timber.e(e)
					
					Timber.w("Could not connect to server $server")
					
				}
				
			}
			
			// Close the client
			client.close()
			
			// Throw an exception, none of the servers could be reached
			throw IOException("No internet")
			
		}
		
	}
	
}