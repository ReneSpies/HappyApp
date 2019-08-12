package com.aresid.happyapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created on: 26.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class ViewPagerAdapter
		extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

	private static final String                         TAG = "ViewPagerAdapter";
	private static       View                           mLoadingLayout;
	private              List<String>                   mTitles;
	private              LayoutInflater                 mInflater;
	private              List<String>                   mDescriptions;
	private              List<String>                   mPrices;
	private              OnViewPagerInteractionListener mListener;
	private              int                            mVariant;
	private              Context                        mContext;

	ViewPagerAdapter(Context context, List<String> titles, List<String> descriptions, List<String> prices, ViewPager2 viewPager2) {

		Log.d(TAG, "ViewPagerAdapter:true");

		mInflater = LayoutInflater.from(context);
		mTitles = titles;
		mDescriptions = descriptions;
		mPrices = prices;
		mVariant = 0;
		mContext = context;

		if (context instanceof OnViewPagerInteractionListener) {

			mListener = (OnViewPagerInteractionListener) context;

		} else {

			throw new RuntimeException(context.toString() + " must implement OnViewPagerInteractionListener");

		}

	}

	static void setLoadingLayoutVisibility(int visibility) {

		Log.d(TAG, "setLoadingLayoutVisibility:true");

		mLoadingLayout.setVisibility(visibility);

	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		Log.d(TAG, "onCreateViewHolder:true");

		View view = mInflater.inflate(R.layout.item_viewpager, parent, false);
		Button viewPagerBtConfirm = view.findViewById(R.id.view_pager_bt_confirm);
		mLoadingLayout = parent.findViewById(R.id.view_pager_checkout_waiting_assistant_layout);

		Glide.with(mContext)
		     .load(mContext.getDrawable(R.drawable.waiting_assistant_content))
		     .into((ImageView) view.findViewById(R.id.view_pager_checkout_waiting_assistant));

		viewPagerBtConfirm.setOnClickListener(v -> {

			Log.d(TAG, "onCreateViewHolder: bt click");

			Log.d(TAG, "onCreateViewHolder: variant is " + mVariant);

			mListener.createUser(mVariant);

		});

		return new ViewHolder(view);

	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

		Log.d(TAG, "onBindViewHolder:true");

		String title = mTitles.get(position);
		String description = mDescriptions.get(position);
		String price = mPrices.get(position);

		if (position == 0) {

			mVariant = 26;

		} else if (position == 1) {

			mVariant = 13;

		}

		holder.mTVTitle.setText(title);
		holder.mTVDescription.setText(description);
		holder.mTVPrice.setText(price);

	}

	@Override
	public int getItemCount() {

		Log.d(TAG, "getItemCount:true");

		return mTitles.size();

	}

	public interface OnViewPagerInteractionListener {

		void createUser(int variantCode);

	}

	class ViewHolder
			extends RecyclerView.ViewHolder {

		TextView         mTVTitle;
		TextView         mTVDescription;
		TextView         mTVPrice;
		ConstraintLayout mConstraintLayout;

		ViewHolder(View itemView) {

			super(itemView);

			mTVTitle = itemView.findViewById(R.id.view_pager_tv_title);
			mTVDescription = itemView.findViewById(R.id.view_pager_tv_description);
			mTVPrice = itemView.findViewById(R.id.view_pager_tv_price);
			mConstraintLayout = itemView.findViewById(R.id.container2);

		}
	}
}
