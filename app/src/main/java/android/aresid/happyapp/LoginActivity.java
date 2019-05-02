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

		// Make sure this is before super.onCreate() and setContentView()
		setTheme(R.style.Theme_HappyApp);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		StandardLoadingDialog sld = new StandardLoadingDialog();
		sld.show(getSupportFragmentManager(), "henlo");

		//displayLoginFragment();
	}

	@Override
	public void onStart()
	{
		Log.d(TAG, "onStart:true");
		super.onStart();
	}

	/**
	 * This method displays an AlertDialog that shows the privacy policy
	 */
	//	@Override
	//	public void displayPrivacyPolicyDialog()
	//	{
	//		Log.d(TAG, "displayPrivacyPolicyDialog:true");
	//
	//		// Get an AlertDialog builder
	//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	//
	//		// Get the view object from the dialogs layout
	//		View dialogView = getLayoutInflater().inflate(R.layout.item_privacy_policy_dialog_view, findViewById(R.id.sign_up_fragment));
	//
	//		// Apply the view to the builder
	//		builder.setView(dialogView);
	//
	//		// Get the button from within the view and create the dialog
	//		Button alertDialogAcceptButton = dialogView.findViewById(R.id.privacy_policy_dialog_accept_button);
	//		final Dialog dialog = builder.create();
	//
	//		// Register a listener to the accept button in the dialog and let it cancel the dialog
	//		alertDialogAcceptButton.setOnClickListener((view) ->
	//		                                           {
	//			                                           Log.d(TAG, "onClick:true");
	//
	//			                                           dialog.cancel();
	//		                                           });
	//
	//		// Finally show the dialog
	//		dialog.show();
	//	}

	/**
	 * This method displays the login fragment
	 * I don't need to transfer information to the fragment so I don't take any params
	 */
	@Override
	public void displayLoginFragment()
	{
		Log.d(TAG, "displayLoginFragment:true");

		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.login_container, LoginFragment.newInstance())
		                           .commit();
	}

	@Override
	public void displayPrivacyPolicyDialog()
	{

	}

	/**
	 * This method displays the sign up fragment with the given params
	 *
	 * @param firstName
	 * @param surname
	 * @param email
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
	 * This method displays the email verification fragment with the given param
	 *
	 * @param user
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
	 * This method displays the legal fragment with the given params
	 *
	 * @param user
	 * @param account
	 * @param userID
	 */
	@Override
	public void displayLegalFragment(FirebaseUser user, GoogleSignInAccount account, String userID)
	{
		Log.d(TAG, "displayLegalFragment:true");

		ViewGroup logoAndFragmentContainer = findViewById(R.id.scroll_view);

		logoAndFragmentContainer.setVisibility(View.INVISIBLE);

		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.legal_container, LegalFragment.newInstance(user, account, userID))
		                           .commit();
	}

	/**
	 * This method displays the sign up fragment with the given param
	 *
	 * @param email
	 */
	@Override
	public void displaySignUpFragment(String email)
	{
		Log.d(TAG, "displaySignUpFragment:true");
		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.login_container, SignUpFragment.newInstance(null, null, email))
		                           .addToBackStack(null)
		                           .commit();
	}

	/**
	 * This method starts the main activity with the given params
	 *
	 * @param user
	 * @param account
	 * @param userID
	 */
	@Override
	public void startMainActivity(FirebaseUser user, GoogleSignInAccount account, String userID)
	{
		Log.d(TAG, "startMainActivity:true");

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("firebaseUser", user);
		intent.putExtra("userFirestoreID", userID);
		intent.putExtra("googleSignInAccount", account);

		startActivity(intent);
	}

	@Override
	public void onBackPressed()
	{
		Log.d(TAG, "onBackPressed:true");


		// TODO: Handle back pressing in MainActivity!

		super.onBackPressed();
	}







}