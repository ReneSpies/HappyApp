package android.aresid.happyapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;


public class AccountLevelFragment
		extends Fragment
{
	private static final String TAG = "AccountLevelFragment";
	private OnFragmentInteractionListener mListener;
	private Context mContext;
	private ViewPager2 mViewPager2;




	/**
	 * Required empty constructor.
	 */
	private AccountLevelFragment()
	{
		Log.d(TAG, "AccountLevelFragment:true");
	}




	/**
	 * Use this factory method to create a new object of this instance.
	 *
	 * @return New instance of object.
	 */
	public static AccountLevelFragment newInstance()
	{
		Log.d(TAG, "newInstance:true");

		AccountLevelFragment fragment = new AccountLevelFragment();
		return fragment;
	}




	@Override
	public void onAttach(@NonNull Context context)
	{
		Log.d(TAG, "onAttach:true");

		super.onAttach(context);

		mContext = context;

		if (context instanceof OnFragmentInteractionListener)
		{
			mListener = (OnFragmentInteractionListener) context;
		}
		else
		{
			throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
		}
	}




	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);
	}




	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView:true");

		// Inflate the layout for this fragment.
		View rootView = inflater.inflate(R.layout.fragment_account_level, container, false);

		// Access all necessary views.
		mViewPager2 = rootView.findViewById(R.id.account_level_variants_holder);

		List<String> listOfStrings = new ArrayList<>();
		listOfStrings.add("first page");
		listOfStrings.add("second page");
		listOfStrings.add("third page");
		listOfStrings.add("fourth page");
		listOfStrings.add("fifth page");

		mViewPager2.setAdapter(new ViewPagerAdapter(mContext, listOfStrings, mViewPager2));

		return rootView;
	}




	@Override
	public void onDetach()
	{
		Log.d(TAG, "onDetach:true");

		super.onDetach();
		mListener = null;
	}




	public interface OnFragmentInteractionListener
	{}







}
