package com.aresid.happyapp;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AccountLevelFragment
		extends Fragment {
	private static final String                        TAG                                = "AccountLevelFragment";
	private static final String                        FIRESTORE_PACKAGES_COLLECTION_NAME = "subscription packages";
	private static final String                        FIRESTORE_FREE_PACKAGE             = "subscription packages";
	private static final String                        FIRESTORE_SILVER_PACKAGE           = "subscription packages";
	private static final String                        FIRESTORE_GOLD_PACKAGE             = "subscription packages";
	private static final String                        FIRESTORE_PLATINUM_PACKAGE         = "subscription packages";
	private static final String                        FIRESTORE_DIAMOND_PACKAGE          = "subscription packages";
	private static final String                        FIRESTORE_DESCRIPTION              = "description";
	private static final String                        FIRESTORE_TITLE                    = "title";
	private static final String                        FIRESTORE_PRICE                    = "price";
	private              OnFragmentInteractionListener mListener;
	private              Context                       mContext;
	
	/**
	 * Required empty constructor.
	 */
	private AccountLevelFragment() {
		Log.d(TAG, "AccountLevelFragment:true");
	}
	
	/**
	 * Use this factory method to create a new object of this instance.
	 *
	 * @return New instance of object.
	 */
	public static AccountLevelFragment newInstance() {
		Log.d(TAG, "newInstance:true");
		return new AccountLevelFragment();
	}
	
	@Override
	public void onAttach(@NonNull Context context) {
		Log.d(TAG, "onAttach:true");
		super.onAttach(context);
		mContext = context;
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString() + " must implement " +
			                           "OnFragmentInteractionListener");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate:true");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView:true");
		// Inflate the layout for this fragment.
		View rootView = inflater.inflate(R.layout.fragment_account_level, container,
		                                 false);
		// Access all necessary views.
		ViewPager2 viewPager2 = rootView.findViewById(R.id.account_level_variants_holder);
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference freePackage = db.collection(FIRESTORE_PACKAGES_COLLECTION_NAME)
		                                  .document(FIRESTORE_FREE_PACKAGE);
		DocumentReference silverPackage = db.collection(FIRESTORE_PACKAGES_COLLECTION_NAME)
		                                    .document(FIRESTORE_SILVER_PACKAGE);
		DocumentReference goldPackage = db.collection(FIRESTORE_PACKAGES_COLLECTION_NAME)
		                                  .document(FIRESTORE_GOLD_PACKAGE);
		DocumentReference platinumPackage = db.collection(FIRESTORE_PACKAGES_COLLECTION_NAME)
		                                      .document(FIRESTORE_PLATINUM_PACKAGE);
		DocumentReference diamondPackage = db.collection(FIRESTORE_PACKAGES_COLLECTION_NAME)
		                                     .document(FIRESTORE_DIAMOND_PACKAGE);
		List<String> listOfTitles = new ArrayList<>();
		List<String> listOfDescriptions = new ArrayList<>();
		List<String> listOfPrices = new ArrayList<>();
		//		freePackage.get(Source.SERVER)
		//		           .addOnSuccessListener(command -> listOfTitles.add(command
		//		           .getString(FIRESTORE_TITLE)))
		//		           .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ", e));
		//		silverPackage.get(Source.SERVER)
		//		             .addOnSuccessListener(command -> listOfTitles.add(command
		//		             .getString(FIRESTORE_TITLE)))
		//		             .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ", e));
		//		goldPackage.get(Source.SERVER)
		//		           .addOnSuccessListener(command -> listOfTitles.add(command
		//		           .getString(FIRESTORE_TITLE)))
		//		           .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ", e));
		//		platinumPackage.get(Source.SERVER)
		//		               .addOnSuccessListener(command -> listOfTitles.add(command
		//		               .getString(FIRESTORE_TITLE)))
		//		               .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ",
		//		               e));
		//		diamondPackage.get(Source.SERVER)
		//		              .addOnSuccessListener(command -> listOfTitles.add(command
		//		              .getString(FIRESTORE_TITLE)))
		//		              .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ",
		//		              e));
		//
		//		freePackage.get(Source.SERVER)
		//		           .addOnSuccessListener(command -> listOfTitles.add(command
		//		           .getString(FIRESTORE_DESCRIPTION)))
		//		           .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ", e));
		//		silverPackage.get(Source.SERVER)
		//		             .addOnSuccessListener(command -> listOfTitles.add(command
		//		             .getString(FIRESTORE_DESCRIPTION)))
		//		             .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ", e));
		//		goldPackage.get(Source.SERVER)
		//		           .addOnSuccessListener(command -> listOfTitles.add(command
		//		           .getString(FIRESTORE_DESCRIPTION)))
		//		           .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ", e));
		//		platinumPackage.get(Source.SERVER)
		//		               .addOnSuccessListener(command -> listOfTitles.add(command
		//		               .getString(FIRESTORE_DESCRIPTION)))
		//		               .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ",
		//		               e));
		//		diamondPackage.get(Source.SERVER)
		//		              .addOnSuccessListener(command -> listOfTitles.add(command
		//		              .getString(FIRESTORE_DESCRIPTION)))
		//		              .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ",
		//		              e));
		//
		//		freePackage.get(Source.SERVER)
		//		           .addOnSuccessListener(command -> listOfTitles.add(command
		//		           .getString(FIRESTORE_PRICE)))
		//		           .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ", e));
		//		silverPackage.get(Source.SERVER)
		//		             .addOnSuccessListener(command -> listOfTitles.add(command
		//		             .getString(FIRESTORE_PRICE)))
		//		             .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ", e));
		//		goldPackage.get(Source.SERVER)
		//		           .addOnSuccessListener(command -> listOfTitles.add(command
		//		           .getString(FIRESTORE_PRICE)))
		//		           .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ", e));
		//		platinumPackage.get(Source.SERVER)
		//		               .addOnSuccessListener(command -> listOfTitles.add(command
		//		               .getString(FIRESTORE_PRICE)))
		//		               .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ",
		//		               e));
		//		diamondPackage.get(Source.SERVER)
		//		              .addOnSuccessListener(command -> listOfTitles.add(command
		//		              .getString(FIRESTORE_PRICE)))
		//		              .addOnFailureListener(e -> Log.e(TAG, "onCreateView: ",
		//		              e));
//		listOfTitles.add("Free package");
//		listOfTitles.add("Silver package");
//		listOfTitles.add("Gold package");
//		listOfTitles.add("Platinum package");
//		listOfTitles.add("Diamond package");
//		listOfDescriptions.add("This is the description for the free package");
//		listOfDescriptions.add("This is the description for the silver package");
//		listOfDescriptions.add("This is the description for the gold package");
//		listOfDescriptions.add("This is the description for the platinum package");
//		listOfDescriptions.add("This is the description for the diamond package");
//		listOfPrices.add("Free/Month");
//		listOfPrices.add("4.99 $/Month");
//		listOfPrices.add("9.99 $/Month");
//		listOfPrices.add("14.99 $/Month");
//		listOfPrices.add("Prestige");
//		SubsPagerFinalAdapter mViewPagerAdapter = new SubsPagerFinalAdapter(mContext,
//		listOfTitles, listOfDescriptions, listOfPrices, viewPager2);
//		viewPager2.setAdapter(mViewPagerAdapter);
//		viewPager2.registerOnPageChangeCallback(new BackgroundTransitionTransformer
//		(viewPager2));
		return rootView;
	}
	
	@Override
	public void onDetach() {
		Log.d(TAG, "onDetach:true");
		super.onDetach();
		mListener = null;
	}
	
	public interface OnFragmentInteractionListener {}
	
	class BackgroundTransitionTransformer
			extends OnPageChangeCallback {
		private final String TAG = getClass().getSimpleName();
		int[] arrayOfColors = new int[] {
				getResources().getColor(R.color.white),
				getResources().getColor(R.color.silver),
				getResources().getColor(R.color.gold),
				getResources().getColor(R.color.platinum),
				getResources().getColor(R.color.diamond)
		};
		private ViewPager2 mViewPager2;
		
		BackgroundTransitionTransformer(ViewPager2 viewPager2) {
			Log.d(TAG, "BackgroundTransitionTransformer:true");
			this.mViewPager2 = viewPager2;
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset,
		                           int positionOffsetPixels) {
			Log.d(TAG, "onPageScrolled:true");
			Log.d(TAG, "onPageScrolled: position = " + position + "\npositionOffset = " +
			           positionOffset + "\npositionOffsetPixels = " +
			           positionOffsetPixels);
			Log.d(TAG, "onPageScrolled: array length = " + arrayOfColors.length);
			if (position < arrayOfColors.length - 1) {
				Log.d(TAG, "onPageScrolled: position < array length");
				mViewPager2.setBackgroundColor((int) new ArgbEvaluator().evaluate(positionOffset, arrayOfColors[position], arrayOfColors[
						position + 1]));
			}
		}
		
		@Override
		public void onPageSelected(int position) {
			Log.d(TAG, "onPageSelected:true");
			Log.d(TAG, "onPageSelected: position = " + position);
			super.onPageSelected(position);
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
			Log.d(TAG, "onPageScrollStateChanged:true");
			Log.d(TAG, "onPageScrollStateChanged: state = " + state);
			super.onPageScrollStateChanged(state);
		}
	}
}
