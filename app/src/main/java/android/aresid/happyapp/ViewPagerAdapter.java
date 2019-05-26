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
		extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>
{
	private static final String TAG = "ViewPagerAdapter";
	private List<String> mData;
	private LayoutInflater mInflater;
	private ViewPager2 mViewPager2;
	private int[] colorArray = new int[] {R.color.white, R.color.silver, R.color.gold, R.color.platinum};




	ViewPagerAdapter(Context context, List<String> data, ViewPager2 viewPager2)
	{
		Log.d(TAG, "ViewPagerAdapter:true");
		this.mInflater = LayoutInflater.from(context);
		this.mData = data;
		this.mViewPager2 = viewPager2;
	}




	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		Log.d(TAG, "onCreateViewHolder:true");
		View view = mInflater.inflate(R.layout.item_viewpager, parent, false);
		return new ViewHolder(view);
	}




	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position)
	{
		Log.d(TAG, "onBindViewHolder:true");

		String animal = mData.get(position);
		holder.mTextView.setText(animal);
		//		holder.mConstraintLayout.setBackgroundResource(colorArray[position]);
	}




	@Override
	public int getItemCount()
	{
		Log.d(TAG, "getItemCount:true");
		return mData.size();
	}




	class ViewHolder
			extends RecyclerView.ViewHolder
	{
		TextView mTextView;
		ConstraintLayout mConstraintLayout;
		Button mButton;




		ViewHolder(View itemView)
		{
			super(itemView);
			mTextView = itemView.findViewById(R.id.tvTitle);
			mConstraintLayout = itemView.findViewById(R.id.container2);
			mButton = itemView.findViewById(R.id.btnToggle);
		}







	}







}
