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
	private static final String  TAG = "SubsPagerInitAdapter";
	private final        Context mContext;
	
	SubsPagerInitAdapter(Context context) {
		mContext = context;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		Log.d(TAG, "onCreateViewHolder: called");
		View view = LayoutInflater.from(mContext)
		                          .inflate(R.layout.content_subspagerinitadapter, parent, false);
		return new ViewHolder(view);
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
	
	class ViewHolder
			extends RecyclerView.ViewHolder {
		ViewHolder(@NonNull View itemView) {
			super(itemView);
		}
	}
}
