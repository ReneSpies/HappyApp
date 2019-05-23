package android.aresid.happyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity
		extends AppCompatActivity
		implements GoogleApiClient.OnConnectionFailedListener,
		           LoginFragment.OnFragmentInteractionListener,
		           SignUpFragment.OnFragmentInteractionListener,
		           EmailVerificationFragment.OnFragmentInteractionListener,
		           LegalitiesDialog.OnPrivacyPolicyDialogInteractionListener
{
	private final static String FIRST_NAME_KEY = "first_name";
	private final static String SURNAME_KEY = "surname";
	private final static String BIRTHDATE_KEY = "birthdate";
	private final static String EMAIL_KEY = "email";
	private final static String ACCEPTED_LEGALITIES_KEY = "accepted_legalities";
	private final static String LEGALITIES_VERSION_KEY = "legalities_version";
	private final static String FIRESTORE_ID_KEY = "firestore_id";
	private final static String NAME_PREFS_FIRESTORE_ID = "user_firestore_id";
	private final static String NAME_PREFS_FIRESTORE_USER_DATA = "user_data";
	private final static String TAG = "LoginActivity";




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




	@Override
	public void saveUserInfoInSharedPreferences(String firstName, String surname, String birthdate, String email, boolean acceptedLegalities,
	                                            double legalitiesVersion)
	{
		Log.d(TAG, "saveUserInfoInSharedPreferences:true");
		// Creating the SharedPref's file and initializing a SharedPref object.
		SharedPreferences preferences = getSharedPreferences(NAME_PREFS_FIRESTORE_USER_DATA, Context.MODE_PRIVATE);

		try
		{
			Log.d(TAG,
			      "saveUserInfoInSharedPreferences:\nfirstName " + firstName + "\nsurname " + surname + "\nbirthdate" + " " + birthdate + "\nemail " + email + "\nacceptedLegalities " + acceptedLegalities + "\nlegalitiesVersion " + legalitiesVersion);
			preferences.edit()
			           .putString(FIRST_NAME_KEY, firstName)
			           .putString(SURNAME_KEY, surname)
			           .putString(BIRTHDATE_KEY, birthdate)
			           .putString(EMAIL_KEY, email)
			           .putBoolean(ACCEPTED_LEGALITIES_KEY, acceptedLegalities)
			           .putFloat(LEGALITIES_VERSION_KEY, (float) legalitiesVersion)
			           .apply();

			createUserInFirestore();
		}
		catch (Exception ex)
		{
			Log.e(TAG, "saveUserInfoInSharedPreferences: ", ex);
		}

	}




	@Override
	public void saveFirestoreUserIDInSharedPreferences(String firestoreID)
	{
		Log.d(TAG, "saveFirestoreUserIDInSharedPreferences:true");
		Log.d(TAG, "saveFirestoreUserIDInSharedPreferences: firestore id = " + firestoreID);
		SharedPreferences preferences = getSharedPreferences(NAME_PREFS_FIRESTORE_ID, Context.MODE_PRIVATE);

		try
		{
			preferences.edit()
			           .putString(FIRESTORE_ID_KEY, firestoreID)
			           .apply();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}




	/**
	 * Shows a Dialog with important legalities such as terms and conditions and privacy policy.
	 * Furthermore there is the confirmation that the user is older than 18 years.
	 */
	@Override
	public void displayLegalitiesDialog(String firstName, String surname, String birthdate, String email, boolean acceptedLegalities,
	                                    double legalitiesVersion, String password)
	{
		Log.d(TAG, "displayLegalitiesDialog:true");

		LegalitiesDialog dialog = LegalitiesDialog.newInstance(firstName, surname, birthdate, email, acceptedLegalities, legalitiesVersion,
		                                                       password);
		dialog.show(getSupportFragmentManager(), "LegalitiesDialog");
	}




	/**
	 * Method creates and populates an adapter for the spinner that's handling the days.
	 * Note: The case of e. g. 30.02.2019 is handled in the SignUpFragment class.
	 *
	 * @return ArrayAdapter with content from 1 to 31.
	 */
	@Override
	public ArrayAdapter createDaysAdapter()
	{
		Log.d(TAG, "createDaysAdapter:true");
		return ArrayAdapter.createFromResource(this, R.array.days_of_month_array, R.layout.birthdate_spinner_item);
	}




	/**
	 * Method creates and populates an adapter for the spinner that's handling the months.
	 * Note: The case of e. g. 30.02.2019 is handled in the SignUpFragment class.
	 *
	 * @return ArrayAdapter with content January to December as Strings.
	 */
	@Override
	public ArrayAdapter createMonthsAdapter()
	{
		Log.d(TAG, "createMonthsAdapter:true");
		return ArrayAdapter.createFromResource(this, R.array.months_of_year_array, R.layout.birthdate_spinner_item);
	}




	/**
	 * Method creates and populates an adapter for the spinner that's handling the years.
	 * Note: The case of e. g. 30.02.2019 is handled in the SignUpFragment class.
	 *
	 * @return ArrayAdapter with content now to 1903 as Strings.
	 */
	@Override
	public ArrayAdapter createYearsAdapter()
	{
		Log.d(TAG, "createYearsAdapter:true");

		List<String> listOfYearsSince1903 = new ArrayList<>();

		// Iterate through the years from now to 1903 and add them to the list.
		for (int year = Calendar.getInstance()
		                        .get(Calendar.YEAR);
		     year >= 1903;
		     year--)
		{
			listOfYearsSince1903.add(String.valueOf(year));
		}

		return new ArrayAdapter<>(this, R.layout.birthdate_spinner_item, listOfYearsSince1903);
	}




	@Override
	public void handleLegalitiesAccept(String firstName, String surname, String birthdate, String email, boolean acceptedLegalities,
	                                   double legalitiesVersion, String password)
	{
		Log.d(TAG, "handleLegalitiesAccept:true");

		handleSignUp(email, password);
		saveUserInfoInSharedPreferences(firstName, surname, birthdate, email, acceptedLegalities, legalitiesVersion);
	}




	/**
	 * Method handles the sign up and registers a new user in my Firebase console.
	 *
	 * @param email    The email to create a new user with.
	 * @param password The password to create a new user with.
	 */
	private void handleSignUp(String email, String password)
	{
		Log.d(TAG, "handleSignUp:true");
		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		firebaseAuth.createUserWithEmailAndPassword(email, password)
		            .addOnSuccessListener(authResult ->
		                                  {
			                                  Log.d(TAG, "onSuccess:true");

			                                  displayEmailVerificationFragment(authResult.getUser());
		                                  })
		            .addOnFailureListener(e ->
		                                  {
			                                  // TODO: exception handling!
			                                  // TODO: Move hardcoded strings to strings.xml.
			                                  Log.d(TAG, "onFailure:true");
			                                  Log.e(TAG, "onFailure: ", e);
			                                  if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException)
			                                  {
				                                  Toast.makeText(this, "The email address is already in use by another account.", Toast.LENGTH_LONG)
				                                       .show();
			                                  }
			                                  if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException)
			                                  {
				                                  Toast.makeText(this, "Your email adress does not seem to fit. Please check it", Toast.LENGTH_LONG)
				                                       .show();
			                                  }
		                                  });
	}




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
	 * Method uploads data from the SharedPreferences onto the Firestore cloud and creates a new user.
	 */
	private void createUserInFirestore()
	{
		Log.d(TAG, "createUserInFirestore:true");

		// Get Firestore instance.
		FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();

		// Get and save the data from shared preferences into a HashMap.
		SharedPreferences prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE);
		Map<String, Object> userData = new HashMap<>();
		userData.put(FIRST_NAME_KEY, prefs.getString(FIRST_NAME_KEY, null));
		userData.put(SURNAME_KEY, prefs.getString(SURNAME_KEY, null));
		userData.put(BIRTHDATE_KEY, prefs.getString(BIRTHDATE_KEY, null));
		userData.put(EMAIL_KEY, prefs.getString(EMAIL_KEY, null));
		userData.put(ACCEPTED_LEGALITIES_KEY, prefs.getBoolean(ACCEPTED_LEGALITIES_KEY, false));
		userData.put(LEGALITIES_VERSION_KEY, prefs.getFloat(LEGALITIES_VERSION_KEY, 0.9f));

		// Add a new document with a generated ID.
		firestoreDB.collection("users")
		           .add(userData)
		           .addOnSuccessListener(documentReference ->
		                                 {
			                                 Log.d(TAG, "createUserInFirestore: success");
			                                 Log.d(TAG, "createUserInFirestore: new user added with id = " + documentReference.getId());

			                                 saveFirestoreUserIDInSharedPreferences(documentReference.getId());
		                                 })
		           .addOnFailureListener(e ->
		                                 {
			                                 Log.d(TAG, "createUserInFirestore: failure");
			                                 Log.e(TAG, "createUserInFirestore: ", e);
		                                 });
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

		getSupportFragmentManager().beginTransaction()
		                           .addToBackStack(null)
		                           .replace(R.id.login_container, SignUpFragment.newInstance(null, null, null, email))
		                           .commit();
	}




	/**
	 * Kicks off an Intent and starts the MainActivity via it.
	 *
	 * @param user    Pass the FirebaseUser over to the MainActivity so it's usable there.
	 * @param account Pass the GoogleAccount to the MainActivity so it's usable there.
	 */
	@Override
	public void startMainActivity(FirebaseUser user, GoogleSignInAccount account)
	{
		Log.d(TAG, "startMainActivity:true");

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("firebase_user", user);
		intent.putExtra("user_firestore_id", getSharedPreferences(NAME_PREFS_FIRESTORE_ID, Context.MODE_PRIVATE).getString(FIRESTORE_ID_KEY, null));
		intent.putExtra("google_sign_in_account", account);

		startActivity(intent);
	}




	@Override
	public Activity getActivitiesContext()
	{
		return this;
	}




	@Override
	public void displayProgressBar()
	{
		Log.d(TAG, "displayProgressBar:true");

		// Getting instances.
		LinearLayout loginLayout, googleLayout, signUpLayout;
		loginLayout = findViewById(R.id.login_linear_layout);
		googleLayout = findViewById(R.id.google_linear_layout);
		signUpLayout = findViewById(R.id.sign_up_linear_layout);
		ImageView progressBar = findViewById(R.id.progress_bar);
		TextView statusInfoTextView = findViewById(R.id.login_text_view);
		Button loginButton = findViewById(R.id.login_login_button);
		Glide.with(this)
		     .load(R.drawable.waiting_assistant_content)
		     .into(progressBar);

		// Setting invisible first so in case of long loading times there are no views overlapping.
		// Setting invisible.
		loginLayout.setVisibility(View.INVISIBLE);
		googleLayout.setVisibility(View.INVISIBLE);
		signUpLayout.setVisibility(View.INVISIBLE);
		loginButton.setVisibility(View.INVISIBLE);

		// Setting visible.
		progressBar.setVisibility(View.VISIBLE);
		statusInfoTextView.setVisibility(View.VISIBLE);

	}




	@Override
	public LayoutInflater getLayoutInflaterForDialog()
	{
		Log.d(TAG, "getLayoutInflater:true");

		return getLayoutInflater();
	}




	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}




	/**
	 * Loads the SignUpFragment into the activities container.
	 *
	 * @param firstName The user's first name which he stated.
	 * @param surname   The user's surname which he stated.
	 * @param email     The user's email which he stated.
	 */
	@Override
	public void displaySignUpFragment(String firstName, String surname, String birthdate, String email)
	{
		Log.d(TAG, "displaySignUpFragment:true");

		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.login_container, SignUpFragment.newInstance(firstName, surname, birthdate, email))
		                           .addToBackStack(null)
		                           .commit();
	}




	@Override
	public String getUserIDFromSharedPreferences()
	{
		return getSharedPreferences(NAME_PREFS_FIRESTORE_ID, Context.MODE_PRIVATE).getString(FIRESTORE_ID_KEY, null);
	}




	@Override
	public void fetchUserIncredentialsFromSharedPreferences()
	{
		Log.d(TAG, "fetchUserIncredentialsFromSharedPreferences:true");

		SharedPreferences preferences = getSharedPreferences(NAME_PREFS_FIRESTORE_USER_DATA, Context.MODE_PRIVATE);

		String firstName = preferences.getString(FIRST_NAME_KEY, null);
		String surname = preferences.getString(SURNAME_KEY, null);
		String birthdate = preferences.getString(BIRTHDATE_KEY, null);
		String email = preferences.getString(EMAIL_KEY, null);

		displaySignUpFragment(firstName, surname, birthdate, email);
	}







}