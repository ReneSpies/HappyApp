package android.aresid.happyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity
		extends AppCompatActivity
		implements GoogleApiClient.OnConnectionFailedListener,
		           LoginFragment.OnFragmentInteractionListener,
		           SignUpFragment.OnFragmentInteractionListener,
		           EmailVerificationFragment.OnFragmentInteractionListener,
		           LegalFragment.OnFragmentInteractionListener
{
	private final String TAG = getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate:true");

		// Make sure this is before super.onCreate() and setContentView().
		// This is my startup theme that gets launched before the app is loaded.
		setTheme(R.style.Theme_HappyApp);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Instantly display the LoginFragment which deals with the further login process.
		displayLoginFragment();
	}

	@Override
	public void onStart()
	{
		Log.d(TAG, "onStart:true");
		super.onStart();
	}

	/**
	 * Loads the LoginFragment into the activities container.
	 * The Fragment handles the further login process.
	 */
	@Override
	public void displayLoginFragment()
	{
		Log.d(TAG, "displayLoginFragment:true");

		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.login_container, LoginFragment.newInstance())
		                           .commit();
	}

	/**
	 * Shows a Dialog with important legalities such as terms and conditions and privacy policy.
	 * Furthermore there is the confirmation that the user is older than 18 years.
	 */
	@Override
	public void displayPrivacyPolicyDialog()
	{

	}

	/**
	 * Loads the SignUpFragment into the activities container.
	 *
	 * @param firstName The user's first name which he stated.
	 * @param surname   The user's surname which he stated.
	 * @param email     The user's email which he stated.
	 */
	@Override
	public void displaySignUpFragment(String firstName, String surname, String email)
	{
		Log.d(TAG, "displaySignUpFragment:true");

		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.login_container, SignUpFragment.newInstance(firstName, surname, email))
		                           .addToBackStack(null)
		                           .commit();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

	/**
	 * Loads the EmailVerificationFragment into the activities container.
	 * The user is not allowed to step any further into the app without having his email verified.
	 *
	 * @param user Pass the FirebaseUser to the fragment because there is a button that allows him to kick off the email verification again.
	 */
	@Override
	public void displayEmailVerificationFragment(FirebaseUser user)
	{
		Log.d(TAG, "displayEmailVerificationFragment:true");

		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.login_container, EmailVerificationFragment.newInstance(user))
		                           .commit();
	}

	/**
	 * Loads the SignUpFragment into the activities container.
	 *
	 * @param email Passes the email to the fragment if the user has accidently typed one in.
	 */
	@Override
	public void displaySignUpFragment(String email)
	{
		Log.d(TAG, "displaySignUpFragment:true");
	}

	/**
	 * Loads the LegalFragment into the activities container.
	 * I plan to replace this with a simple dialog.
	 *
	 * @param user
	 * @param account
	 * @param userID
	 */
	@Override
	public void displayLegalFragment(FirebaseUser user, GoogleSignInAccount account, String userID)
	{
		Log.d(TAG, "displayLegalFragment:true");
		// TODO: Replace with dialog.

		ViewGroup logoAndFragmentContainer = findViewById(R.id.scroll_view);

		logoAndFragmentContainer.setVisibility(View.INVISIBLE);

		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.legal_container, LegalFragment.newInstance(user, account, userID))
		                           .commit();
	}

	/**
	 * Kicks off an Intent and starts the MainActivity via it.
	 *
	 * @param user    Pass the FirebaseUser over to the MainActivity so it's usable there.
	 * @param account Pass the GoogleAccount to the MainActivity so it's usable there.
	 * @param userID  I plan to replace this with SharedPrefs
	 */
	@Override
	public void startMainActivity(FirebaseUser user, GoogleSignInAccount account, String userID)
	{
		Log.d(TAG, "startMainActivity:true");
		// TODO: Replace String userID with SharedPrefs.

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("firebaseUser", user);
		intent.putExtra("userFirestoreID", userID);
		intent.putExtra("googleSignInAccount", account);

		startActivity(intent);
	}







}