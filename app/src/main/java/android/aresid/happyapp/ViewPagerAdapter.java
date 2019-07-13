package android.aresid.happyapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
	private              List<String>                   mTitles;
	private              LayoutInflater                 mInflater;
	private              List<String>                   mDescriptions;
	private              List<String>                   mPrices;
	private              OnViewPagerInteractionListener mListener;
	private              int                            mRequestCode;

	ViewPagerAdapter(Context context, List<String> titles, List<String> descriptions, List<String> prices, ViewPager2 viewPager2) {

		Log.d(TAG, "ViewPagerAdapter:true");

		mInflater = LayoutInflater.from(context);
		mTitles = titles;
		mDescriptions = descriptions;
		mPrices = prices;

		if (context instanceof OnViewPagerInteractionListener) {

			mListener = (OnViewPagerInteractionListener) context;

		} else {

			throw new RuntimeException(context.toString() + " must implement OnViewPagerInteractionListener");

		}

	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		Log.d(TAG, "onCreateViewHolder:true");

		View view = mInflater.inflate(R.layout.item_viewpager, parent, false);
		Button viewPagerBtConfirm = view.findViewById(R.id.view_pager_bt_confirm);

		viewPagerBtConfirm.setOnClickListener(v -> {

			Log.d(TAG, "onCreateViewHolder: bt click");

			mListener.createUser();

		});

		return new ViewHolder(view);

	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

		Log.d(TAG, "onBindViewHolder:true");

		String title = mTitles.get(position);
		String description = mDescriptions.get(position);
		String price = mPrices.get(position);

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

		void createUser();

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
