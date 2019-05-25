package android.aresid.happyapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity
		extends AppCompatActivity
	implements OnboardingFragment.OnFragmentInteractionListener
{
	private static final String TAG = "OnboardingAcitivty";




	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate:true");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_onboarding);

		displayOnboardingFragment();
	}




	private void displayOnboardingFragment()
	{
		Log.d(TAG, "displayOnboardingFragment:true");
		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.onboarding_container, OnboardingFragment.newInstance())
		                           .commit();
	}







}
