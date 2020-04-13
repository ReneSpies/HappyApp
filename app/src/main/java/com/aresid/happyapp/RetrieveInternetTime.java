package com.aresid.happyapp;

import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.net.TimeUDPClient;

import java.io.IOException;
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
			TimeUDPClient client = new TimeUDPClient();
			client.open();
			InetAddress address = InetAddress.getByName(timeServer);
			return client.getDate(address);
		} catch (IOException e) {
			Log.e(TAG, "doInBackground: ", e);
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
