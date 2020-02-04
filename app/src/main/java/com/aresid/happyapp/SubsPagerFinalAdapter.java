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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
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
public class SubsPagerFinalAdapter
		extends RecyclerView.Adapter<SubsPagerFinalAdapter.ViewHolder>
		implements BillingClientStateListener,
		           PurchasesUpdatedListener,
		           SkuDetailsResponseListener {
	private static final String                         TAG = "SubsPagerFinalAdapter";
	private              View                           mProcessingLayout;
	private              View                           mMainView;
	private              LayoutInflater                 mInflater;
	private              OnViewPagerInteractionListener mListener;
	private              Context                        mContext;
	private              SubscriptionPool               mSubscriptionPool;
	private              BillingClient                  mBillingClient;
	
	SubsPagerFinalAdapter(Context context) {
		Log.d(TAG, "SubsPagerFinalAdapter: called");
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
		Log.d(TAG, "onCreateViewHolder: called");
		View view = LayoutInflater.from(mContext)
		                          .inflate(R.layout.item_viewpager, parent, false);
		mProcessingLayout = view.findViewById(R.id.view_pager_waiting_assistant_layout);
		mMainView = view.findViewById(R.id.view_pager_main_view);
		// TODO: Using XML onClick attribute. The listener is called inside EntryActivity
//		viewPagerBtConfirm.setOnClickListener(v -> {
//			Log.d(TAG, "onCreateViewHolder: bt click");
//			ViewPager2 viewPager2 = (ViewPager2) view.getParent()
//			                                         .getParent();
//			if (mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
//			                  .getResponseCode() == BillingClient.BillingResponseCode.OK) {
//				SkuDetails details = mSubscriptionPool.getSubscription(viewPager2.getCurrentItem())
//				                                      .getSkuDetails();
//				Log.d(TAG, "onCreateViewHolder: sku details = " + details.getTitle());
//				BillingFlowParams params = BillingFlowParams.newBuilder()
//				                                            .setSkuDetails(details)
//				                                            .build();
//				mBillingClient.launchBillingFlow((Activity) mContext, params);
//			}
////			mListener.createUser(mSubscriptionPool.getSubscription(viewPager2.getCurrentItem()));
//		});
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
	
	private void loadGifInto(ImageView... gifHolders) {
		Log.d(TAG, "loadGifInto: called");
		for (ImageView gifHolder : gifHolders) {
			Glide.with(mContext)
			     .load(mContext.getDrawable(R.drawable.waiting_assistant_content))
			     .into(gifHolder);
		}
	}
	
	@Override
	public void onSkuDetailsResponse(BillingResult result, List<SkuDetails> list) {
		Log.d(TAG, "onSkuDetailsResponse: called");
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
	
	private void updateUI() {
		Log.d(TAG, "updateUI: called");
		synchronized (this) {
			Log.d(TAG, "updateUI: synchronized");
			notifyDataSetChanged();
			setProcessingLayoutVisibility(View.GONE);
		}
	}
	
	private void setProcessingLayoutVisibility(int visibility) {
		Log.d(TAG, "setProcessingLayoutVisibility: called");
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
	
	@Override
	public void onBillingSetupFinished(BillingResult result) {
		Log.d(TAG, "onBillingSetupFinished: called");
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
		Log.d(TAG, "onBillingServiceDisconnected: called");
		// TODO: Implement own connection failed policy!
	}
	
	@Override
	public void onPurchasesUpdated(BillingResult result, @Nullable List<Purchase> list) {
		Log.d(TAG, "onPurchasesUpdated: called");
		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
			Log.d(TAG, "onPurchasesUpdated: response code is ok");
		} else if (result.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
			Log.d(TAG, "onPurchasesUpdated: user cancelled");
		} else {
			Log.d(TAG, "onPurchasesUpdated: response code is " + result.getDebugMessage());
		}
	}
	
	public void onConfirmButtonClick(View view) {
		Log.d(TAG, "onConfirmButtonClick: called");
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
