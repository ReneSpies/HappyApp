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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SignUpFragment
		extends Fragment
		implements View.OnClickListener
{
	// Static final String for key value pair for the Bundle
	private static final String ARG_EMAIL = "email";
	private static final String ARG_FIRST_NAME = "firstName";
	private static final String ARG_SURNAME = "surname";
	private static final String ARG_BIRTHDATE = "birthdate";

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




	public static SignUpFragment newInstance(String firstName, String surname, String birthdate, String email)
	{
		SignUpFragment signUpFragment = new SignUpFragment();
		Bundle args = new Bundle();
		args.putString(ARG_FIRST_NAME, firstName);
		args.putString(ARG_SURNAME, surname);
		args.putString(ARG_BIRTHDATE, birthdate);
		args.putString(ARG_EMAIL, email);
		signUpFragment.setArguments(args);
		return signUpFragment;
	}




	@Override
	public void onAttach(@NonNull Context context)
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
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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
		daysAdapter.setDropDownViewResource(R.layout.item_birthdate_spinner_dropdown);
		mBirthdateDaySpinner.setAdapter(daysAdapter);

		ArrayAdapter monthsAdapter = mFragmentInteractionListener.createMonthsAdapter();
		monthsAdapter.setDropDownViewResource(R.layout.item_birthdate_spinner_dropdown);
		mBirthdateMonthSpinner.setAdapter(monthsAdapter);

		ArrayAdapter yearsAdapter = mFragmentInteractionListener.createYearsAdapter();
		yearsAdapter.setDropDownViewResource(R.layout.item_birthdate_spinner_dropdown);
		mBirthdateYearSpinner.setAdapter(yearsAdapter);

		signUpSignUpButton.setOnClickListener(this);

		if (getArguments() != null)
		{
			mFirstNameField.setText(getArguments().getString(ARG_FIRST_NAME));
			mSurnameField.setText(getArguments().getString(ARG_SURNAME));
			mEmailField.setText(getArguments().getString(ARG_EMAIL));

			if (getArguments().getString(ARG_BIRTHDATE) != null)
			{
				List<Integer> listOfBirthdate = convertBirthdateFromString(getArguments().getString(ARG_BIRTHDATE));
				mBirthdateDaySpinner.setSelection(listOfBirthdate.get(0));
				mBirthdateMonthSpinner.setSelection(listOfBirthdate.get(1));
				mBirthdateYearSpinner.setSelection(listOfBirthdate.get(2));
			}
		}

		return rootView;
	}




	private List<Integer> convertBirthdateFromString(String birthdate)
	{
		Log.d(TAG, "convertBirthdateFromString:true");
		Log.d(TAG, "convertBirthdateFromString: birthdate = " + birthdate);
		List<Integer> listOfBirthdate = new ArrayList<>();
		String[] arrayOfMonthsInYear = new String[] {
				"January",
				"February",
				"March",
				"April",
				"May",
				"June",
				"July",
				"August",
				"September",
				"October",
				"November",
				"December"
		};
		List<Integer> listOfDaysInMonth = new ArrayList<>();
		List<Integer> listOfYears = new ArrayList<>();
		int yearsHelper = 0;

		for (int i = 0;
		     i <= 30;
		     i++)
		{
			listOfDaysInMonth.add(i);
		}

		for (int i = Calendar.getInstance()
		                     .get(Calendar.YEAR);
		     i >= 1903;
		     i--)
		{
			listOfYears.add(i);
		}

		for (int day : listOfDaysInMonth)
		{
			Log.d(TAG, "convertBirthdateFromString: day = " + day);
			Log.d(TAG, "convertBirthdateFromString: contains day = " + birthdate.contains(String.valueOf(day)));
			if (birthdate.contains(String.valueOf(day)))
			{
				listOfBirthdate.add(day);
				break;
			}
		}

		for (String month : arrayOfMonthsInYear)
		{
			Log.d(TAG, "convertBirthdateFromString: month = " + month);
			Log.d(TAG, "convertBirthdateFromString: contains month = " + birthdate.contains(month));
			if (birthdate.contains(month))
			{
				for (int i = 0;
				     i <= arrayOfMonthsInYear.length;
				     i++)
				{
					if (arrayOfMonthsInYear[i].equals(month))
					{
						listOfBirthdate.add(i);
						break;
					}
				}
				break;
			}
		}
		Log.d(TAG, "convertBirthdateFromString: listOfYears = " + listOfYears);
		for (int year : listOfYears)
		{
			Log.d(TAG, "convertBirthdateFromString: year = " + year);
			Log.d(TAG, "convertBirthdateFromString: birthdate contains " + year + " = " + birthdate.contains(String.valueOf(year)));
			if (birthdate.contains(String.valueOf(year)))
			{
				listOfBirthdate.add(yearsHelper);
				break;
			}
			yearsHelper++;
		}
		Log.d(TAG, "convertBirthdateFromString: listOfBirthdate = " + listOfBirthdate);
		return listOfBirthdate;
	}




	@Override
	public void onStart()
	{
		Log.d(TAG, "onStart:true");
		super.onStart();

		if (mFirebaseAuth.getCurrentUser() != null)
		{
			// TODO: Move all the hardcoded Strings into strings.xml.
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
		String age = convertBirthdateIntoString(mBirthdateDaySpinner.getSelectedItem()
		                                                            .toString(), mBirthdateMonthSpinner.getSelectedItem()
		                                                                                               .toString(),
		                                        mBirthdateYearSpinner.getSelectedItem()
		                                                             .toString()
		                                       );
		Log.d(TAG, "onClick: age = " + age);

		// Handles all the mButton clicks in this Fragment
		if (view.getId() == R.id.sign_up_sign_up_button)
		{
			// TODO: Move all the hardcoded Strings into strings.xml.
			Log.d(TAG, "onClick:sign_up_sign_up_button");

			if (firstName.length() == 0)
			{
				mFirstNameField.setError("You forgot me");
				return;
			}
			else if (surname.length() == 0)
			{
				mSurnameField.setError("You forgot me");
				return;
			}
			else if (!isEmailCorrect(email))
			{
				mEmailField.setError("You forgot me");
				return;
			}
			else if (!isPasswordCorrect(password))
			{
				mPasswordField.setError("You forgot me. I have to be longer than six characters");
				return;
			}
			else if (!isAgeCorrect(
					mBirthdateDaySpinner.getSelectedItem()
					                    .toString(), mBirthdateMonthSpinner.getSelectedItem()
					                                                       .toString(), mBirthdateYearSpinner.getSelectedItem()
					                                                                                         .toString()))
			{
				Snackbar.make(view, "You must be older than 18 years to register", Snackbar.LENGTH_LONG)
				        .show();
				return;
			}

			mFragmentInteractionListener.displayLegalitiesDialog(firstName, surname, email, password, age, "13.0.13");
		}
	}




	/**
	 * Method converts the birthdate from the spinners into a String.
	 *
	 * @param dayDD     The date in DD format.
	 * @param monthName The months name e. g. January.
	 * @param yearYYYY  The year in YYYY format.
	 * @return String that represents the birthdate in DD.MM.YYYY format.
	 */
	private String convertBirthdateIntoString(String dayDD, String monthName, String yearYYYY)
	{
		Log.d(TAG, "convertBirthdateIntoString:true");

		return dayDD + ". " + monthName + " " + yearYYYY;
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
	 * Method verifies if the user's date of birth > 6573 days (> 18 years).
	 *
	 * @param dayDD     The dob day in DD format.
	 * @param monthName The dob month in MM format.
	 * @param yearYYYY  The dob year in YYYY format.
	 * @return Boolean if user dob > 6573 days (> 18 years).
	 */
	private boolean isAgeCorrect(String dayDD, String monthName, String yearYYYY)
	{
		Log.d(TAG, "isAgeCorrect:true");

		int day = Integer.parseInt(dayDD);
		int month = getMonthFromName(monthName);
		int year = Integer.parseInt(yearYYYY);

		int today = Calendar.getInstance()
		                    .get(Calendar.DATE);
		int thisMonth = Calendar.getInstance()
		                        .get(Calendar.MONTH);
		int thisYear = Calendar.getInstance()
		                       .get(Calendar.YEAR);

		Calendar calendarNow = Calendar.getInstance();
		Calendar calendarDob = Calendar.getInstance();

		// Convert the two given dates into two different Calendar instances.
		calendarNow.set(thisYear, thisMonth, today);
		calendarDob.set(year, month, day);

		Log.d(TAG, "isAgeCorrect: calendarDob = " + calendarDob.toString());
		Log.d(TAG, "isAgeCorrect: calendarNow = " + calendarNow.toString());

		// Convert the two given Calendar instances into milliseconds.
		long millisecondsNow = calendarNow.getTimeInMillis();
		long millisecondsDob = calendarDob.getTimeInMillis();

		// Calculate the difference in milliseconds.
		long dateDifference = millisecondsNow - millisecondsDob;

		long secondInMillis = 1000;
		long minuteInMillis = 60 * secondInMillis;
		long hourInMillis = 60 * minuteInMillis;
		long dayInMillis = 24 * hourInMillis;

		// Calculate the difference in days.
		long differenceInDays = dateDifference / dayInMillis;

		Log.d(TAG, "isAgeCorrect: differenceInDays = " + differenceInDays);
		return differenceInDays > 6573;
	}




	/**
	 * Method converts a month name like January into its number like 01.
	 *
	 * @param monthName Name of month.
	 * @return Integer number of month in year.
	 */
	private int getMonthFromName(String monthName)
	{
		Log.d(TAG, "getMonthFromName:true");

		// A HashMap with the months as keys and their numbers as values.
		HashMap<String, Integer> months = new HashMap<>(11);

		// String array to iterate through the available month names.
		String[] monthNames = new String[] {
				"January",
				"February",
				"March",
				"April",
				"May",
				"June",
				"July",
				"August",
				"September",
				"October",
				"November",
				"December"
		};

		int i = 0;
		for (String month : monthNames)
		{
			months.put(month, i);
			i++;
		}

		// Return the value of the given month name.
		return months.get(monthName);
	}




	public interface OnFragmentInteractionListener
	{
		void displayEmailVerificationFragment(FirebaseUser user);


		void displayLoginFragment();


		void displayLegalitiesDialog(String firstName, String surname, String email, String password, String birthdate,
		                             String acceptedLegalitiesVersion);


		ArrayAdapter createDaysAdapter();


		ArrayAdapter createMonthsAdapter();


		ArrayAdapter createYearsAdapter();







	}







}
