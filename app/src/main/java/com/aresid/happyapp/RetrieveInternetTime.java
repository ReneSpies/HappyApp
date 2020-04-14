package com.aresid.happyapp;

import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created on: 20.06.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class RetrieveInternetTime
		extends AsyncTask<String, Void, Date> {
	private static final String                            TAG = "RetrieveInternetTime";
	private              OnInternetTimeInteractionListener mListener;
	private              String                            mUid;
	
	RetrieveInternetTime(AppCompatActivity context, String uid) {
		Log.d(TAG, "RetrieveInternetTime: called");
		if (context instanceof OnInternetTimeInteractionListener) {
			mListener = (OnInternetTimeInteractionListener) context;
		} else {
			throw new RuntimeException(
					context.toString() + " must implement OnInternetTimeInteractionListener");
		}
		mUid = uid;
	}
	
	@Override
	protected Date doInBackground(String... strings) {
		Log.d(TAG, "doInBackground: called");
		String timeServer = strings[0];
		try {
			// Define NTPUDPClient
			NTPUDPClient client = new NTPUDPClient();
			// Define InetAddress Object from time server name received in the parameters
			InetAddress address = InetAddress.getByName(timeServer);
			// Define TimeInfo Object from clients time
			TimeInfo timeInfo = client.getTime(address);
			// Get the time from the clients time info
			long returnTime = timeInfo.getMessage()
			                          .getOriginateTimeStamp()
			                          .getTime();
			// Translate the return time long into a Date Object and return it
			return new Date(returnTime);
		} catch (Exception e) {
			Log.e(TAG, "doInBackground: ", e);
			// If an error occurs, just return the devices local time
			return new Date();
		}
	}
	
	@Override
	protected void onPostExecute(Date time) {
		Log.d(TAG, "onPostExecute: called");
		mListener.addTimeToFirestoreEntry(time, mUid);
	}
	
	interface OnInternetTimeInteractionListener {
		void addTimeToFirestoreEntry(Date time, String uid);
	}
}
