package android.aresid.happyapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity
		extends AppCompatActivity
		implements AccountLevelFragment.OnFragmentInteractionListener
{
	private static final String TAG = "OnboardingActivity";




	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onboarding);

		new DisplayFragment(this).displayFragment(R.id.onboarding_fragment_container, AccountLevelFragment.newInstance());
	}







}
