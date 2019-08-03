package com.aresid.happyapp;

import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

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

		Log.d(TAG, "RetrieveInternetTime:true");

		if (context instanceof OnInternetTimeInteractionListener) {

			mListener = (OnInternetTimeInteractionListener) context;

		} else {

			throw new RuntimeException(context.toString() + " must implement OnInternetTimeInteractionListener");

		}

		mUid = uid;

	}

	@Override
	protected Date doInBackground(String... strings) {

		Log.d(TAG, "doInBackground:true");

		String timeServer = strings[0];

		try {

			NTPUDPClient client = new NTPUDPClient();
			InetAddress address = InetAddress.getByName(timeServer);
			TimeInfo info = client.getTime(address);
			long returnTime = info.getMessage()
			                      .getTransmitTimeStamp()
			                      .getTime();

			return new Date(returnTime);

		} catch (IOException e) {

			Log.e(TAG, "doInBackground: ", e);

			return null;

		}

	}

	@Override
	protected void onPostExecute(Date time) {

		Log.d(TAG, "onPostExecute:true");

		mListener.addTimeToFirestoreEntry(time, mUid);

	}

	interface OnInternetTimeInteractionListener {

		void addTimeToFirestoreEntry(Date time, String uid);

	}
}
