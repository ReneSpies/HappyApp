package com.aresid.happyapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on: 26.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class SubsPagerFinalAdapter
		extends RecyclerView.Adapter<SubsPagerFinalAdapter.ViewHolder> {
	private static final String                         TAG = "SubsPagerFinalAdapter";
	private              Context                        mContext;
	private              SubscriptionPool               mSubscriptionPool;
	
	SubsPagerFinalAdapter(Context context, SubscriptionPool pool) {
		Log.d(TAG, "SubsPagerFinalAdapter: called");
		mSubscriptionPool = pool;
		mContext = context;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		Log.d(TAG, "onCreateViewHolder: called");
		View view = LayoutInflater.from(mContext)
		                          .inflate(R.layout.content_subspagerfinaladapter, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Log.d(TAG, "onBindViewHolder: called");
		String title = mSubscriptionPool.getSubscription(position)
		                                .getTitle();
		String description = mSubscriptionPool.getSubscription(position)
		                                      .getDescription();
		String price = mSubscriptionPool.getSubscription(position)
		                                .getPrice();
		Drawable icon = mSubscriptionPool.getSubscription(position)
		                                 .getIcon();
		holder.mTVTitle.setText(title);
		holder.mTVDescription.setText(description);
		holder.mTVPrice.setText(price);
		holder.mSubIcon.setImageDrawable(icon);
	}
	
	@Override
	public int getItemCount() {
		Log.d(TAG, "getItemCount: called");
		return mSubscriptionPool.getSubscriptionCount();
	}
	
	class ViewHolder
			extends RecyclerView.ViewHolder {
		TextView  mTVTitle;
		TextView  mTVDescription;
		TextView  mTVPrice;
		ImageView mSubIcon;
		
		ViewHolder(View itemView) {
			super(itemView);
			mTVTitle = itemView.findViewById(R.id.view_pager_tv_title);
			mTVDescription = itemView.findViewById(R.id.view_pager_tv_description);
			mTVPrice = itemView.findViewById(R.id.view_pager_tv_price);
			mSubIcon = itemView.findViewById(R.id.view_pager_subscription_icon);
		}
	}
}
