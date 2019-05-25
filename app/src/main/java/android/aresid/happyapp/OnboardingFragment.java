package android.aresid.happyapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;


public class OnboardingFragment
		extends Fragment
{
	private static final String TAG = "OnboardingFragment";
	private ViewPager mSlideViewPager;
	private LinearLayout mPageIndicatorLayout;
	private OnFragmentInteractionListener mListener;




	/**
	 * Required empty constructor
	 */
	private OnboardingFragment()
	{
		Log.d(TAG, "OnboardingFragmentConstructor:true");
	}




	/**
	 * Use this factory method to create a new instance of OnboardingFragment.
	 * @return New instance of OnboardingFragment.
	 */
	public static OnboardingFragment newInstance()
	{
		Log.d(TAG, "newInstance:true");
		return new OnboardingFragment();
	}




	@Override
	public void onAttach(Context context)
	{
		Log.d(TAG, "onAttach:true");
		super.onAttach(context);
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
		View rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);

		// Init all views.
		mSlideViewPager = rootView.findViewById(R.id.onboarding_slide_view_pager);
		mPageIndicatorLayout = rootView.findViewById(R.id.onboarding_page_indicator_layout);

		mSlideViewPager.setAdapter(new SlideViewPagerAdapter(getActivity()));

		return rootView;
	}




	@Override
	public void onDetach()
	{
		super.onDetach();
		mListener = null;
	}




	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener
	{


	}







}
