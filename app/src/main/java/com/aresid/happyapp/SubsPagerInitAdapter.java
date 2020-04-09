package com.aresid.happyapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on: 04/02/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */
public class SubsPagerInitAdapter
		extends RecyclerView.Adapter<SubsPagerInitAdapter.ViewHolder> {
	private static final String  TAG                  = "SubsPagerInitAdapter";
	private final        Context mContext;
	private              View    mRetryScreen;
	private              View    mLoadingScreen;
	private              boolean mRetryScreenIsNeeded = false;
	
	SubsPagerInitAdapter(Context context) {
		Log.d(TAG, "SubsPagerInitAdapter: called");
		mContext = context;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		Log.d(TAG, "onCreateViewHolder: called");
		View view = LayoutInflater.from(mContext)
		                          .inflate(R.layout.content_subspagerinitadapter, parent, false);
		mLoadingScreen = view.findViewById(R.id.init_adapter_loading_screen);
		mRetryScreen = view.findViewById(R.id.init_adapter_retry_screen);
		if (mRetryScreenIsNeeded) {
			changeScreen(mRetryScreen);
		} else {
			changeScreen(mLoadingScreen);
		}
		return new ViewHolder(view);
	}
	
	private void changeScreen(View neededScreen) {
		Log.d(TAG, "changeScreen: called");
		if (neededScreen == mRetryScreen) {
			mLoadingScreen.setVisibility(View.INVISIBLE);
			mRetryScreen.setVisibility(View.VISIBLE);
		} else {
			mLoadingScreen.setVisibility(View.VISIBLE);
			mRetryScreen.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Log.d(TAG, "onBindViewHolder: called");
	}
	
	@Override
	public int getItemCount() {
		Log.d(TAG, "getItemCount: called");
		return 1;
	}
	
	void retry() {
		Log.d(TAG, "retry: called");
		if (mRetryScreen == null | mLoadingScreen == null) {
			toggleIsRetryScreenNeeded(true);
		} else {
			changeScreen(mRetryScreen);
		}
	}
	
	private void toggleIsRetryScreenNeeded(boolean isNeeded) {
		Log.d(TAG, "toggleNeededScreens: called");
		mRetryScreenIsNeeded = isNeeded;
	}
	
	class ViewHolder
			extends RecyclerView.ViewHolder {
		ViewHolder(@NonNull View itemView) {
			super(itemView);
		}
	}
}
