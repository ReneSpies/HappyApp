package android.aresid.happyapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignUpFragment
		extends Fragment
		implements View.OnClickListener,
		           PrivacyPolicyDialog.OnPrivacyPolicyDialogInteractionListener
{
	// Static final String for key value pair for the Bundle
	private static final String ARG_EMAIL = "email";
	private static final String ARG_FIRST_NAME = "firstName";
	private static final String ARG_SURNAME = "surname";

	private final String TAG = getClass().getSimpleName();
	private OnFragmentInteractionListener mFragmentInteractionListener;

	private String mUserFirestoreID;

	private FirebaseAuth mFirebaseAuth;
	private EditText mEmailField;
	private EditText mFirstNameField;
	private EditText mSurnameField;
	private EditText mPasswordField;
	private Spinner mBirthdateDaySpinner;
	private Spinner mBirthdateMonthSpinner;
	private Spinner mBirthdateYearSpinner;

	public SignUpFragment()
	{
		Log.d(TAG, "SignUpFragment:emptyConstructor");
		// Required empty public constructor
	}

	public static SignUpFragment newInstance(String firstName, String surname, String email)
	{
		SignUpFragment signUpFragment = new SignUpFragment();
		Bundle args = new Bundle();
		args.putString(ARG_FIRST_NAME, firstName);
		args.putString(ARG_SURNAME, surname);
		args.putString(ARG_EMAIL, email);
		signUpFragment.setArguments(args);
		return signUpFragment;
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);

		Log.d(TAG, "onAttach:true");

		if (context instanceof OnFragmentInteractionListener)
		{
			mFragmentInteractionListener = (OnFragmentInteractionListener) context;
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

		// Get FirebaseAuth instance
		mFirebaseAuth = FirebaseAuth.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView:true");
		View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

		// Init all views.
		mEmailField = rootView.findViewById(R.id.sign_up_email_field);
		mFirstNameField = rootView.findViewById(R.id.sign_up_first_name_field);
		mSurnameField = rootView.findViewById(R.id.sign_up_surname_field);
		mPasswordField = rootView.findViewById(R.id.sign_up_password_field);
		mBirthdateDaySpinner = rootView.findViewById(R.id.birthdate_day);
		mBirthdateMonthSpinner = rootView.findViewById(R.id.birthdate_month);
		mBirthdateYearSpinner = rootView.findViewById(R.id.birthdate_year);
		Button signUpSignUpButton = rootView.findViewById(R.id.sign_up_sign_up_button);

		// Create the adapters on acitivity level and apply them to the spinners.
		ArrayAdapter daysAdapter = mFragmentInteractionListener.createDaysAdapter();
		daysAdapter.setDropDownViewResource(R.layout.birthdate_spinner_dropdown_item);
		mBirthdateDaySpinner.setAdapter(daysAdapter);

		ArrayAdapter monthsAdapter = mFragmentInteractionListener.createMonthsAdapter();
		monthsAdapter.setDropDownViewResource(R.layout.birthdate_spinner_dropdown_item);
		mBirthdateMonthSpinner.setAdapter(monthsAdapter);

		ArrayAdapter yearsAdapter = mFragmentInteractionListener.createYearsAdapter();
		yearsAdapter.setDropDownViewResource(R.layout.birthdate_spinner_dropdown_item);
		mBirthdateYearSpinner.setAdapter(yearsAdapter);

		signUpSignUpButton.setOnClickListener(this);

		if (getArguments() != null)
		{
			mFirstNameField.setText(getArguments().getString(ARG_FIRST_NAME));
			mSurnameField.setText(getArguments().getString(ARG_SURNAME));
			mEmailField.setText(getArguments().getString(ARG_EMAIL));
		}

		return rootView;
	}

	@Override
	public void onStart()
	{
		Log.d(TAG, "onStart:true");
		super.onStart();

		if (mFirebaseAuth.getCurrentUser() != null)
		{
			Toast.makeText(getContext(), "Reloading user information...", Toast.LENGTH_LONG)
			     .show();

			mFirebaseAuth.getCurrentUser()
			             .reload()
			             .addOnSuccessListener(aVoid ->
			                                   {
				                                   if (mFirebaseAuth.getCurrentUser()
				                                                    .isEmailVerified())
				                                   {
					                                   mFragmentInteractionListener.displayLoginFragment();
				                                   }
			                                   });
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		Log.d(TAG, "onDetach:true");
		mFragmentInteractionListener = null;
	}

	/**
	 * Creates a list of 1903 - today for the years the customers could've been born in.
	 *
	 * @return List of 1903 - today.
	 */
	private List<String> createYearsList()
	{
		Log.d(TAG, "createYearsList:true");
		List<String> listOfYears = new ArrayList<>();

		for (int year = Calendar.getInstance()
		                        .get(Calendar.YEAR);
		     year >= 1903;
		     year--)
		{
			listOfYears.add(String.valueOf(year));
		}

		return listOfYears;
	}

	/**
	 * Method converts the birthdate from the spinners into a String.
	 *
	 * @param dayDD    The date in DD format.
	 * @param monthMM  The month in MM format.
	 * @param yearYYYY The year in YYYY format.
	 * @return String that represents the birthdate in DD.MM.YYYY format.
	 */
	private String convertBirthdateIntoString(int dayDD, int monthMM, int yearYYYY)
	{
		Log.d(TAG, "convertBirthdateIntoString:true");

		return dayDD + "." + monthMM + "." + yearYYYY;
	}

	@Override
	public void onClick(View view)
	{
		Log.d(TAG, "onClick:SignUpFragment");

		String firstName = mFirstNameField.getText()
		                                  .toString();
		String surname = mSurnameField.getText()
		                              .toString();
		String email = mEmailField.getText()
		                          .toString();
		String password = mPasswordField.getText()
		                                .toString();

		// Handles all the button clicks in this Fragment
		if (view.getId() == R.id.sign_up_sign_up_button)
		{
			Log.d(TAG, "onClick:sign_up_sign_up_button");

			if (!isEmailCorrect(email))
			{
				Snackbar.make(view, "Check Email", Snackbar.LENGTH_LONG)
				        .show();
				return;
			}
			else if (!isPasswordCorrect(password))
			{
				Snackbar.make(view, "Password must be larger than 6 characters", Snackbar.LENGTH_LONG)
				        .show();
				return;
			}

			displayPrivacyPolicyDialog();
		}
	}

	/**
	 * Method verifies that the email is valid.
	 *
	 * @param email The email to check.
	 * @return Boolean if email is valid or not.
	 */
	private boolean isEmailCorrect(String email)
	{
		return (email.length() != 0 && email.indexOf('@') != -1);
	}

	/**
	 * Method varifies that the password is valid.
	 *
	 * @param password The password to check.
	 * @return Boolean if password is valid or not.
	 */
	private boolean isPasswordCorrect(String password)
	{
		return password.length() > 6;
	}

	/**
	 * Method loads the privacy policy & legalities dialog and shows it to the user.
	 */
	private void displayPrivacyPolicyDialog()
	{
		// TODO: Move to activity level.
		Log.d(TAG, "displayPrivacyPolicyDialog:true");

		PrivacyPolicyDialog dialog = new PrivacyPolicyDialog();
		dialog.show(getFragmentManager(), "PrivacyPolicyDialog");
	}

	/**
	 * Method checks if birthdate is valid (30.02 == invalid) and if the user's age > 18.
	 *
	 * @param dayDD    Day in DD format.
	 * @param monthMM  Month in MM format.
	 * @param yearYYYY Year in YYYY format.
	 * @return Boolean if birthdate is valid.
	 */
	private boolean isBirthdateValid(int dayDD, int monthMM, int yearYYYY)
	{
		Log.d(TAG, "isBirthdateValid:true");

		if (dayDD > 29 && monthMM == 02) return false;

		String currentDate = new SimpleDateFormat("ddmmyyyy", Locale.GERMANY).format(new Date());
		Log.d(TAG, "isBirthdateValid: current date " + currentDate);
		return true;
	}

	/**
	 * Method handles the event when user accepts the legalities.
	 *
	 * @param firstName User's first name to save to Firestore.
	 * @param surname   User's surname to save to Firestore.
	 * @param email     User's email to save to Firestore.
	 * @param password  User's password to handle sign up.
	 */
	@Override
	public void handlePrivacyPolicyAccept(String firstName, String surname, String birthdate, String email, boolean acceptedLegalities,
	                                      float legalitiesVersion, String password)
	{
		Log.d(TAG, "handlePrivacyPolicyAccept:true");

		mFragmentInteractionListener.saveUserInfoInSharedPreferences(firstName, surname, birthdate, email, acceptedLegalities, legalitiesVersion);

		//		saveUserInFirestore(email, password);
	}

	/**
	 * Method to save userdata to firestore.
	 * I plan to refactor this in coop with SharedPrefs.
	 *
	 * @param firstName User's first name to save.
	 * @param surname   User's surname name to save.
	 * @param email     User's email name to save.
	 * @param password  I don't know why this is here.
	 */
	private void saveUserInFirestore(String firstName, String surname, String email, String password)
	{
		Log.d(TAG, "saveUserInFirestore:true");

		final String PASSWORD = password;
		final String EMAIL = email;

		// Get Firestore instance
		FirebaseFirestore db = FirebaseFirestore.getInstance();

		// Save the data in a HashMap
		final Map<String, String> user = new HashMap<>();
		user.put("firstName", firstName);
		user.put("surname", surname);
		if (email != null)
		{
			user.put("email", email);
		}
		else
		{
			user.put("email", "no email");
		}

		// Add a new document with a generated ID
		db.collection("users")
		  .add(user)
		  .addOnSuccessListener(documentReference ->
		                        {
			                        Log.d(TAG, "New user added with ID: " + documentReference.getId());

			                        // Save the users ID in a member variable so I can easily access it in this fragment
			                        mUserFirestoreID = documentReference.getId();

			                        // TODO: Request brithdate and move this method to the dialog.
			                        // Saving the users ID in the SharedPreferences so I can access the document from other activities as well
			                        mFragmentInteractionListener.saveUserInfoInSharedPreferences(user.get("firstName"), user.get("surname"), null,
			                                                                                     user.get("email"), false, 0);

			                        handleSignUp(PASSWORD, EMAIL);
		                        })
		  .addOnFailureListener(e ->
		                        {
			                        Log.d(TAG, "onFailure:true");
			                        Log.e(TAG, "saveUserInFirestore:failure", e);
		                        });
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
		mFirebaseAuth.createUserWithEmailAndPassword(email, password)
		             .addOnSuccessListener(authResult ->
		                                   {
			                                   Log.d(TAG, "onSuccess:true");

			                                   updateUI(authResult.getUser());
		                                   })
		             .addOnFailureListener(e ->
		                                   {
			                                   // TODO: exception handling!
			                                   Log.d(TAG, "onFailure:true");
			                                   Log.e(TAG, "onFailure: ", e);
			                                   if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException)
			                                   {
				                                   Toast.makeText(getContext(), "The email address is already in use by another account.",
				                                                  Toast.LENGTH_LONG)
				                                        .show();
			                                   }
		                                   });
	}

	/**
	 * Method updates the UI after sign up got handled with.
	 *
	 * @param user Passes this to the next fragment.
	 */
	private void updateUI(FirebaseUser user)
	{
		Log.d(TAG, "updateUI:true");

		mFragmentInteractionListener.displayEmailVerificationFragment(user);
	}

	public interface OnFragmentInteractionListener
	{
		void displayEmailVerificationFragment(FirebaseUser user);

		void displayLoginFragment();

		void saveUserInfoInSharedPreferences(String firstName, String surname, String birthdate, String email, boolean acceptedLegalities,
		                                     float legalitiesVersion);

		void saveFirestoreUserIDInSharedPreferences(String firestoreID);

		ArrayAdapter createDaysAdapter();

		ArrayAdapter createMonthsAdapter();

		ArrayAdapter createYearsAdapter();







	}







}
