package com.aresid.happyapp;

import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on: 26.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class ViewPagerAdapter
		extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>
		implements BillingClientStateListener,
		           PurchasesUpdatedListener,
		           SkuDetailsResponseListener {

	private static final String                         TAG = "ViewPagerAdapter";
	private              View                           mProcessingLayout;
	private              View                           mMainView;
	private              LayoutInflater                 mInflater;
	private              OnViewPagerInteractionListener mListener;
	private              Context                        mContext;
	private              SubscriptionPool               mSubscriptionPool;
	private              BillingClient                  mBillingClient;

	ViewPagerAdapter(Context context) {

		Log.d(TAG, "ViewPagerAdapter:true");

		mInflater = LayoutInflater.from(context);

		mSubscriptionPool = new SubscriptionPool();

		mContext = context;

		mBillingClient = BillingClient.newBuilder(context)
		                              .enablePendingPurchases()
		                              .setListener(this)
		                              .build();

		mBillingClient.startConnection(this);

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
		mProcessingLayout = view.findViewById(R.id.view_pager_waiting_assistant_layout);
		mMainView = view.findViewById(R.id.view_pager_main_view);

//		loadGifInto(view.findViewById(R.id.view_pager_checkout_waiting_assistant), view.findViewById(R.id.view_pager_waiting_assistant));

		viewPagerBtConfirm.setOnClickListener(v -> {

			Log.d(TAG, "onCreateViewHolder: bt click");

			ViewPager2 viewPager2 = (ViewPager2) view.getParent()
			                                         .getParent();

			if (mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
			                  .getResponseCode() == BillingClient.BillingResponseCode.OK) {

				SkuDetails details = mSubscriptionPool.getSubscription(viewPager2.getCurrentItem())
				                                      .getSkuDetails();

				Log.d(TAG, "onCreateViewHolder: sku details = " + details.getTitle());

				BillingFlowParams params = BillingFlowParams.newBuilder()
				                                            .setSkuDetails(details)
				                                            .build();

				mBillingClient.launchBillingFlow((Activity) mContext, params);

			}

//			mListener.createUser(mSubscriptionPool.getSubscription(viewPager2.getCurrentItem()));

		});

		return new ViewHolder(view);

	}

	private void loadGifInto(ImageView... gifHolders) {

		Log.d(TAG, "loadGifInto:true");

		for (ImageView gifHolder : gifHolders) {

			Glide.with(mContext)
			     .load(mContext.getDrawable(R.drawable.waiting_assistant_content))
			     .into(gifHolder);

		}

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

		holder.mTVTitle.setText(title);
		holder.mTVDescription.setText(description);
		holder.mTVPrice.setText(price);
		holder.mSubIcon.setImageDrawable(icon);

	}

	@Override
	public int getItemCount() {

		Log.d(TAG, "getItemCount:true");

		return mSubscriptionPool.getSubscriptionCount();

	}

	private void updateUI() {

		Log.d(TAG, "updateUI:true");

		synchronized (this) {

			Log.d(TAG, "updateUI: synchronized");

			notifyDataSetChanged();

			setProcessingLayoutVisibility(View.GONE);

		}

	}

	@Override
	public void onSkuDetailsResponse(BillingResult result, List<SkuDetails> list) {

		Log.d(TAG, "onSkuDetailsResponse:true");

		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {

			for (SkuDetails sku : list) {

				Log.d(TAG, "onSkuDetailsResponse: got a sku: " + sku.getTitle());

				Subscription sub = new Subscription(mContext);
				sub.setId(sku.getSku());
				sub.setTitle(sku.getTitle());
				sub.setPrice(sku.getPrice());
				sub.setDescription(sku.getDescription());
				sub.setSkuDetails(sku);

				mSubscriptionPool.addSubscription(sub);

			}

			mSubscriptionPool.sort();

			updateUI();

		} else {

			Log.d(TAG, "onSkuDetailsResponse: response code = " + result.getResponseCode());
			Log.w(TAG, "onSkuDetailsResponse: response message = " + result.getDebugMessage());

		}

	}

	@Override
	public void onBillingSetupFinished(BillingResult result) {

		Log.d(TAG, "onBillingSetupFinished:true");

		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {

			// The client is ready. Query purchases here!

			List<String> skus = new ArrayList<>();
			skus.add("happyapp.subscription.bronze");
			skus.add("happyapp.subscription.silver");
			skus.add("happyapp.subscription.gold");
			skus.add("happyapp.subscription.platinum");

			mBillingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder()
			                                                    .setSkusList(skus)
			                                                    .setType(BillingClient.SkuType.SUBS)
			                                                    .build(), this);

		} else {

			Log.d(TAG, "onBillingSetupFinished: response code = " + result.getResponseCode());
			Log.w(TAG, "onBillingSetupFinished: response message = " + result.getDebugMessage());

		}

	}

	@Override
	public void onBillingServiceDisconnected() {

		Log.d(TAG, "onBillingServiceDisconnected:true");

		// TODO: Implement own connection failed policy!

	}

	@Override
	public void onPurchasesUpdated(BillingResult result, @Nullable List<Purchase> list) {

		Log.d(TAG, "onPurchasesUpdated:true");

		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {

			Log.d(TAG, "onPurchasesUpdated: response code is ok");

		} else if (result.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

			Log.d(TAG, "onPurchasesUpdated: user cancelled");

		} else {

			Log.d(TAG, "onPurchasesUpdated: response code is " + result.getDebugMessage());

		}

	}

	private void setProcessingLayoutVisibility(int visibility) {

		Log.d(TAG, "setProcessingLayoutVisibility:true");

		if (mProcessingLayout == null) {

			return;

		}

		if (visibility == View.VISIBLE) {

			Log.d(TAG, "setProcessingLayoutVisibility: visible");

			mProcessingLayout.setVisibility(visibility);

			mMainView.setVisibility(View.INVISIBLE);

		} else {

			mProcessingLayout.setVisibility(visibility);

			mMainView.setVisibility(View.VISIBLE);

		}

	}

	public interface OnViewPagerInteractionListener {

		void createUser(Subscription subscription);

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
