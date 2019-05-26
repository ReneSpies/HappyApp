package android.aresid.happyapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity
		extends AppCompatActivity
		implements AccountLevelFragment.OnFragmentInteractionListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onboarding);

		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.onboarding_fragment_container, AccountLevelFragment.newInstance())
		                           .commit();
	}







}
