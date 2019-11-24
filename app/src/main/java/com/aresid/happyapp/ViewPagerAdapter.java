package com.aresid.happyapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
	private static       View                           mMainView;
	private              LayoutInflater                 mInflater;
	private              List<String>                   mTitles;
	private              List<String>                   mDescriptions;
	private              List<String>                   mPrices;
	private              OnViewPagerInteractionListener mListener;
	private              int                            mVariant;
	private              Context                        mContext;
	private              ViewPager2                     mViewPager2;
	private              SubscriptionPool               mSubscriptionPool;

	ViewPagerAdapter(Context context, ViewPager2 viewPager2) {

		Log.d(TAG, "ViewPagerAdapter:true");

		Drawable bronzeIcon, silverIcon, goldIcon, platinumIcon;

		mInflater = LayoutInflater.from(context);
		mViewPager2 = viewPager2;

		bronzeIcon = context.getDrawable(R.drawable.bronze_icon);
		silverIcon = context.getDrawable(R.drawable.silver_icon);
		goldIcon = context.getDrawable(R.drawable.gold_icon);
		platinumIcon = context.getDrawable(R.drawable.platinum_icon);

		mSubscriptionPool = new SubscriptionPool();
		mSubscriptionPool.addSubscription("HappyApp Bronze", "dis shit bronze", "$ 50", bronzeIcon);
		mSubscriptionPool.addSubscription("HappyApp Silver", "dis shit silver", "$ 100", silverIcon);
		mSubscriptionPool.addSubscription("HappyApp Gold", "dis shit gold", "$ 200", goldIcon);
		mSubscriptionPool.addSubscription("HappyApp Platinum", "dis shit platinum", "$ 1000", platinumIcon);

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

	static void setCheckoutProcessingLayoutVisibility(int visibility) {

		Log.d(TAG, "setCheckoutProcessingLayoutVisibility:true");

		mCheckoutProcessingLayout.setVisibility(visibility);

	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		Log.d(TAG, "onCreateViewHolder:true");

		View view = mInflater.inflate(R.layout.item_viewpager, parent, false);
		Button viewPagerBtConfirm = view.findViewById(R.id.view_pager_bt_confirm);
		mCheckoutProcessingLayout = view.findViewById(R.id.view_pager_checkout_waiting_assistant_layout);
		mProcessingLayout = view.findViewById(R.id.view_pager_waiting_assistant_layout);
		mMainView = view.findViewById(R.id.view_pager_main_view);

		Log.d(TAG, "onCreateViewHolder: parent = " + parent.toString());

		Log.d(TAG, "onCreateViewHolder: checkout layout = " + mCheckoutProcessingLayout);

		fetchSubscriptionInfoFromServer();

		Glide.with(mContext)
		     .load(mContext.getDrawable(R.drawable.waiting_assistant_content))
		     .into((ImageView) view.findViewById(R.id.view_pager_checkout_waiting_assistant));

		Glide.with(mContext)
		     .load(mContext.getDrawable(R.drawable.waiting_assistant_content))
		     .into((ImageView) view.findViewById(R.id.view_pager_waiting_assistant));

		viewPagerBtConfirm.setOnClickListener(v -> {

			Log.d(TAG, "onCreateViewHolder: bt click");

			Log.d(TAG, "onCreateViewHolder: variant is " + (mViewPager2.getCurrentItem() == 1 ? 13 : 26));

			mListener.createUser((mViewPager2.getCurrentItem() == 1 ? 13 : 26));

		});

		return new ViewHolder(view);

	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

		Log.d(TAG, "onBindViewHolder:true");

		String title = mSubscriptionPool.getSubscription(position)
		                                .getTitle();
		String description = mSubscriptionPool.getSubscription(position)
		                                      .getDescription();
		String price = mSubscriptionPool.getSubscription(position)
		                                .getPrice();
		Drawable icon = mSubscriptionPool.getSubscription(position)
		                                 .getIcon();

		Log.d(TAG, "onBindViewHolder: title = " + title);
		Log.d(TAG, "onBindViewHolder: description = " + description);
		Log.d(TAG, "onBindViewHolder: price = " + price);

		holder.mTVTitle.setText(title);
		holder.mTVDescription.setText(description);
		holder.mTVPrice.setText(price);
		holder.mSubIcon.setImageDrawable(icon);

	}

	@Override
	public int getItemCount() {

		Log.d(TAG, "getItemCount:true");

		Log.d(TAG, "getItemCount: " + mSubscriptionPool.getSubscriptionCount());
		return mSubscriptionPool.getSubscriptionCount();

	}

	private void fetchSubscriptionInfoFromServer() {

		Log.d(TAG, "fetchSubscriptionInfoFromServer:true");

		FirebaseFirestore db = FirebaseFirestore.getInstance();

		List<String> listOfTitles = new ArrayList<>();
		List<String> listOfDescs = new ArrayList<>();
		List<String> listOfPrices = new ArrayList<>();

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

				  setProcessingLayoutVisibility(View.GONE);

			  }

		  })
		  .addOnFailureListener(e -> {

			  Log.d(TAG, "getSubscriptionsInfoArray: failure");
			  Log.e(TAG, "getSubscriptionsInfoArray: ", e);

			  setProcessingLayoutVisibility(View.GONE);

		  });

	}

	private static void setProcessingLayoutVisibility(int visibility) {

		Log.d(TAG, "setProcessingLayoutVisibility:true");

		switch (visibility) {

			case View.INVISIBLE:

				Log.d(TAG, "setProcessingLayoutVisibility: invisible");

				mProcessingLayout.setVisibility(visibility);

				mMainView.setVisibility(View.VISIBLE);

				break;

			case View.VISIBLE:

				Log.d(TAG, "setProcessingLayoutVisibility: visible");

				mProcessingLayout.setVisibility(visibility);

				mMainView.setVisibility(View.INVISIBLE);

				break;

			case View.GONE:

				Log.d(TAG, "setProcessingLayoutVisibility: gone");

				mProcessingLayout.setVisibility(visibility);

				mMainView.setVisibility(View.VISIBLE);

				break;

		}

	}

	public interface OnViewPagerInteractionListener {

		void createUser(int variantCode);

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
