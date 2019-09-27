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

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Collection;
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
	private static       View                           mCheckoutProcessingLayout;
	private static       View                           mProcessingLayout;
	private              LayoutInflater                 mInflater;
	private              List<String>                   mTitles;
	private              List<String>                   mDescriptions;
	private              List<String>                   mPrices;
	private              OnViewPagerInteractionListener mListener;
	private              int                            mVariant;
	private              Context                        mContext;

	ViewPagerAdapter(Context context) {

		Log.d(TAG, "ViewPagerAdapter:true");

		mInflater = LayoutInflater.from(context);

		Collection<String> collection = new ArrayList<>();
		collection.add(context.getString(R.string.plain_processing));

		mTitles = new ArrayList<>(collection);
		mDescriptions = new ArrayList<>(collection);
		mPrices = new ArrayList<>(collection);

		mVariant = 0;
		mContext = context;

		if (context instanceof OnViewPagerInteractionListener) {

			mListener = (OnViewPagerInteractionListener) context;

		} else {

			throw new RuntimeException(context.toString() + " must implement OnViewPagerInteractionListener");

		}

	}

	static void setProcessingLayoutVisibility(int visibility) {

		Log.d(TAG, "setProcessingLayoutVisibility:true");

		mProcessingLayout.setVisibility(visibility);

	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		Log.d(TAG, "onCreateViewHolder:true");

		View view = mInflater.inflate(R.layout.item_viewpager, parent, false);
		Button viewPagerBtConfirm = view.findViewById(R.id.view_pager_bt_confirm);
		mCheckoutProcessingLayout = parent.findViewById(R.id.view_pager_checkout_waiting_assistant_layout);
		mProcessingLayout = parent.findViewById(R.id.view_pager_waiting_assistant_layout);

		Glide.with(mContext)
		     .load(mContext.getDrawable(R.drawable.waiting_assistant_content))
		     .into((ImageView) view.findViewById(R.id.view_pager_checkout_waiting_assistant));

		Glide.with(mContext)
		     .load(mContext.getDrawable(R.drawable.waiting_assistant_content))
		     .into((ImageView) view.findViewById(R.id.view_pager_waiting_assistant));

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

	@Override
	public void onViewAttachedToWindow(@NonNull ViewHolder holder) {

		Log.d(TAG, "onViewAttachedToWindow:true");

		fetchSubscriptionInfoFromServer();

	}

	private void fetchSubscriptionInfoFromServer() {

		Log.d(TAG, "fetchSubscriptionInfoFromServer:true");

		FirebaseFirestore db = FirebaseFirestore.getInstance();

		List<String> listOfTitles = new ArrayList<>();
		List<String> listOfDescs = new ArrayList<>();
		List<String> listOfPrices = new ArrayList<>();

//		setCheckoutProcessingLayoutVisibility(View.VISIBLE);

		setProcessingLayoutVisibility(View.VISIBLE);

		db.collection(FirestoreNames.COLLECTION_SUBSCRIPTIONS)
		  .document(FirestoreNames.DOCUMENT_INFO)
		  .get(Source.SERVER)
		  .addOnSuccessListener(command -> {

			  Log.d(TAG, "getSubscriptionsInfoArray: success");

			  listOfTitles.add(command.getString(FirestoreNames.COLUMN_TITLE_FREE));
			  listOfTitles.add(command.getString(FirestoreNames.COLUMN_TITLE_GOLD));

			  listOfDescs.add(command.getString(FirestoreNames.COLUMN_DESC_FREE));
			  listOfDescs.add(command.getString(FirestoreNames.COLUMN_DESC_GOLD));

			  listOfPrices.add(command.getString(FirestoreNames.COLUMN_PRICE_FREE));
			  listOfPrices.add(command.getString(FirestoreNames.COLUMN_PRICE_GOLD));

			  mTitles = listOfTitles;
			  mDescriptions = listOfDescs;
			  mPrices = listOfPrices;

			  synchronized (this) {

				  Log.d(TAG, "fetchSubscriptionInfoFromServer: notifying");

				  notifyDataSetChanged();

			  }

		  })
		  .addOnFailureListener(e -> {

			  Log.d(TAG, "getSubscriptionsInfoArray: failure");
			  Log.e(TAG, "getSubscriptionsInfoArray: ", e);

		  });

	}

	static void setCheckoutProcessingLayoutVisibility(int visibility) {

		Log.d(TAG, "setCheckoutProcessingLayoutVisibility:true");

		mCheckoutProcessingLayout.setVisibility(visibility);

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
