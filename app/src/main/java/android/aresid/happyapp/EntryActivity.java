package android.aresid.happyapp;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryActivity
		extends AppCompatActivity
		implements GoogleApiClient.OnConnectionFailedListener,
		           LoginFragment.OnFragmentInteractionListener,
		           SignUpFragment.OnFragmentInteractionListener,
		           EmailVerificationFragment.OnFragmentInteractionListener,
		           LegalitiesDialog.OnPrivacyPolicyDialogInteractionListener,
		           NoLoginButtonTextWatcher.OnNoLoginButtonTextWatcherInteractionListener,
		           View.OnClickListener {

	private final static String             FIRST_NAME_KEY                      = "first_name";
	private final static String             SURNAME_KEY                         = "surname";
	private final static String             BIRTHDATE_KEY                       = "birthdate";
	private final static String             EMAIL_KEY                           = "email";
	private final static String             ACCEPTED_LEGALITIES_VERSION_KEY     = "accepted_legalities";
	private final static String             LEGALITIES_VERSION_KEY              = "legalities_version";
	private final static String             FIRESTORE_ID_KEY                    = "firestore_id";
	private final static String             NAME_PREFS_FIRESTORE_ID             = "user_firestore_id";
	private final static String             NAME_PREFS_FIRESTORE_USER_DATA      = "user_data";
	private final static String             TAG                                 = "EntryActivity";
	private static final int                REQUEST_CODE_LOGIN                  = 13;
	private static       boolean            mComesFromEmailVerificationFragment = false;
	private              DBHelper           mDBHelper;
	private              TextInputEditText  mEtLoginPasswordField;
	private              TextInputEditText  mEtLoginEmailField;
	private              TextInputLayout    mEtLoginPasswordLayout;
	private              TextInputLayout    mEtLoginEmailLayout;
	private              FirebaseUser       mFirebaseUser;
	private              GoogleSignInClient mGoogleSignInClient;
	private              FirebaseAuth       mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		// Make sure this is before super.onCreate() and setContentView().
		// This is my startup theme that gets launched before the app is loaded.
		//		setTheme(R.style.Theme_HappyApp);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);

		// Instantiate FirebaseAuth.
		mAuth = FirebaseAuth.getInstance();

		// Configure Google Sign In.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getResources().getString(R.string.default_web_client_id))
		                                                                                              .requestEmail()
		                                                                                              .build();

		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		if (mDBHelper == null) {

			// Create new database if not exists.
			mDBHelper = new DBHelper(this);

			Log.d(TAG, "onCreate: db = " + mDBHelper);

		}

		// Access all views that are needed.
		mEtLoginPasswordField = findViewById(R.id.entry_activity_login_password_field);
		mEtLoginPasswordLayout = findViewById(R.id.entry_activity_login_password_layout);
		mEtLoginEmailField = findViewById(R.id.entry_activity_login_email_field);
		mEtLoginEmailLayout = findViewById(R.id.entry_activity_login_email_layout);
		mEtLoginPasswordField.addTextChangedListener(new NoLoginButtonTextWatcher(this));
		mEtLoginEmailField.addTextChangedListener(new NoLoginButtonTextWatcher(this));
		ScrollView sv = findViewById(R.id.entry_activity_scroll_view);
		sv.setSmoothScrollingEnabled(true);
		Button btGoogleLogin = findViewById(R.id.entry_activity_login_google_button);
		btGoogleLogin.setOnClickListener(this);
		TextInputEditText etRegistrationDateOfBirthField = findViewById(R.id.entry_activity_registration_date_of_birth_field);
		etRegistrationDateOfBirthField.setOnClickListener(v -> {

			Log.d(TAG, "onCreate: dob field click");

			new DatePickerFragment(this).show(getSupportFragmentManager(), "date picker");

		});
		etRegistrationDateOfBirthField.setOnFocusChangeListener((v, hasFocus) -> {

			if (v.hasFocus()) {

				new DatePickerFragment(this).show(getSupportFragmentManager(), "date picker");

			}

		});
		etRegistrationDateOfBirthField.setKeyListener(null);

		// Set up the ViewPager for the subscriptions.
		ViewPager2 vpSubscriptionsView = findViewById(R.id.entry_activity_subscription_view_pager);
		List<String> listOfTitles = new ArrayList<>();
		List<String> listOfDescriptions = new ArrayList<>();
		List<String> listOfPrices = new ArrayList<>();

		// Silver package
		listOfTitles.add("Silver package");
		listOfDescriptions.add("Description for silver package");
		listOfPrices.add("4.99 $/Month");

		// Gold package
		listOfTitles.add("Gold package");
		listOfDescriptions.add("Description for gold package");
		listOfPrices.add("9.99 $/Month");

		// Platinum package
		listOfTitles.add("Platinum package");
		listOfDescriptions.add("Description for platinum package");
		listOfPrices.add("14.99 $/Month");

		ViewPagerAdapter vpAdapter = new ViewPagerAdapter(this, listOfTitles, listOfDescriptions, listOfPrices, vpSubscriptionsView);
		vpSubscriptionsView.setAdapter(vpAdapter);

		BackgroundTransitionTransformer btt = new BackgroundTransitionTransformer(this, vpSubscriptionsView);
		vpSubscriptionsView.registerOnPageChangeCallback(btt);

		sv.getViewTreeObserver()
		  .addOnScrollChangedListener(() -> {

			  int scrollY = sv.getScrollY() + sv.getHeight() - vpSubscriptionsView.getTop();
			  if (scrollY >= 0.0 && scrollY <= vpSubscriptionsView.getHeight()) {

				  if (btt.getPosition() < btt.mArrayOfColors.length - 1) {

					  sv.setBackgroundColor((int) new ArgbEvaluator().evaluate((float) scrollY /
					                                                           vpSubscriptionsView.getHeight(), getResources().getColor(R.color.white),
					                                                           new ArgbEvaluator().evaluate(btt.getPositionOffset(), btt.mArrayOfColors[btt.getPosition()], btt.mArrayOfColors[
							  btt.getPosition() + 1])));

				  } else {

					  sv.setBackgroundColor((int) new ArgbEvaluator().evaluate((float) scrollY /
					                                                           vpSubscriptionsView.getHeight(), getResources().getColor(R.color.white),
					                                                           new ArgbEvaluator().evaluate(btt.getPositionOffset(), btt.mArrayOfColors[btt.getPosition()], btt.mArrayOfColors[2])));

				  }
			  }
		  });

		populateSubscriptionsTable();

		// Instantly display the LoginFragment which deals with the further login process.
		//		displayLoginFragment();
	}

	@Override
	public void onStart() {

		Log.d(TAG, "onStart:true");

		super.onStart();

		FirebaseUser user = FirebaseAuth.getInstance()
		                                .getCurrentUser();

		updateUI(user);
	}

	void updateUI(FirebaseUser user) {

		Log.d(TAG, "updateUI:true");

		ImageView waitingAssistant = findViewById(R.id.entry_activity_logging_in_waiting_assistant);

		if (user != null) {

			Glide.with(this)
			     .load(R.drawable.waiting_assistant_content)
			     .into(waitingAssistant);

			findViewById(R.id.entry_activity_constraint_layout).setVisibility(View.GONE);
			findViewById(R.id.entry_activity_logging_in_waiting_layout).setVisibility(View.VISIBLE);

			Toast toast = Toast.makeText(this, "Reloading user information", Toast.LENGTH_LONG);
			toast.show();

			user.reload()
			    .addOnSuccessListener(command -> {

				    Log.d(TAG, "updateUI: success");
				    toast.cancel();
				    if (user.isEmailVerified()) {

					    startMainActivityWithUser(user);

				    } else {

					    startEmailVerificationActivity(user);

				    }
			    })
			    .addOnFailureListener(e -> {

				    Log.d(TAG, "updateUI: failure");
				    Log.e(TAG, "updateUI: ", e);

			    });

		} else {

			Log.d(TAG, "updateUI: user == null");

			// TODO

		}
	}

	void startMainActivityWithUser(FirebaseUser user) {

		Log.d(TAG, "startMainActivityWithUser:true");
		// TODO
	}

	void startEmailVerificationActivity(FirebaseUser user) {

		Log.d(TAG, "startEmailVerificationActivity:true");
		// TODO
	}

	/**
	 * Method populates the Subscriptions table in the db with the server data.
	 */
	private void populateSubscriptionsTable() {

		Log.d(TAG, "populateSubscriptionsTable:true");
	}

	void loginWithEmailAndPassword(String email, String password) {

		Log.d(TAG, "loginWithEmailAndPassword:true");

		FirebaseAuth auth = FirebaseAuth.getInstance();

		auth.signInWithEmailAndPassword(email, password)
		    .addOnSuccessListener(command -> {

			    Log.d(TAG, "loginWithEmailAndPassword: success");
			    mEtLoginPasswordLayout.setError(null);

		    })
		    .addOnFailureListener(command -> {

			    Log.d(TAG, "loginWithEmailAndPassword: failure");
			    mEtLoginPasswordLayout.setError("You got me wrong");
			    Log.e(TAG, "loginWithEmailAndPassword: ", command.getCause());

		    });
	}

	/**
	 * Loads the LoginFragment into the activities container.
	 * The Fragment handles the further login process.
	 */
	@Override
	public void displayLoginFragment() {

		Log.d(TAG, "displayLoginFragment:true");
		//		new DisplayFragment(this).displayFragment(R.id.login_container, LoginFragment.newInstance());
	}

	/**
	 * Shows a Dialog with important legalities such as terms and conditions and privacy policy.
	 * Furthermore there is the confirmation that the user is older than 18 years.
	 */
	@Override
	public void displayLegalitiesDialog(String firstName, String surname, String email, String password, String birthdate, String acceptedLegalitiesVersion) {

		Log.d(TAG, "displayLegalitiesDialog:true");
		Log.d(TAG, "displayLegalitiesDialog: firstName = " + firstName);
		Log.d(TAG, "displayLegalitiesDialog: surname = " + surname);
		Log.d(TAG, "displayLegalitiesDialog: email = " + email);
		Log.d(TAG, "displayLegalitiesDialog: password = " + password);
		Log.d(TAG, "displayLegalitiesDialog: birthdate = " + birthdate);
		Log.d(TAG, "displayLegalitiesDialog: acceptedLegalitiesVersion = " + acceptedLegalitiesVersion);

		LegalitiesDialog dialog = LegalitiesDialog.newInstance(firstName, surname, email, password, birthdate, acceptedLegalitiesVersion);
		dialog.show(getSupportFragmentManager(), "LegalitiesDialog");
	}

	/**
	 * Method creates and populates an adapter for the spinner that's handling the days.
	 * Note: The case of e. g. 30.02.2019 is handled in the SignUpFragment class.
	 *
	 * @return ArrayAdapter with content from 1 to 31.
	 */
	@Override
	public ArrayAdapter createDaysAdapter() {

		Log.d(TAG, "createDaysAdapter:true");
		return ArrayAdapter.createFromResource(this, R.array.days_of_month_array, R.layout.item_birthdate_spinner);
	}

	/**
	 * Method creates and populates an adapter for the spinner that's handling the months.
	 * Note: The case of e. g. 30.02.2019 is handled in the SignUpFragment class.
	 *
	 * @return ArrayAdapter with content January to December as Strings.
	 */
	@Override
	public ArrayAdapter createMonthsAdapter() {

		Log.d(TAG, "createMonthsAdapter:true");
		return ArrayAdapter.createFromResource(this, R.array.months_of_year_array, R.layout.item_birthdate_spinner);
	}

	/**
	 * Method creates and populates an adapter for the spinner that's handling the years.
	 * Note: The case of e. g. 30.02.2019 is handled in the SignUpFragment class.
	 *
	 * @return ArrayAdapter with content now to 1903 as Strings.
	 */
	@Override
	public ArrayAdapter createYearsAdapter() {

		Log.d(TAG, "createYearsAdapter:true");
		List<String> listOfYearsSince1903 = new ArrayList<>();
		// Iterate through the years from now to 1903 and add them to the list.
		for (int year = Calendar.getInstance()
		                        .get(Calendar.YEAR); year >= 1903; year--) {

			listOfYearsSince1903.add(String.valueOf(year));

		}

		return new ArrayAdapter<>(this, R.layout.item_birthdate_spinner, listOfYearsSince1903);
	}

	@Override
	public void onBackPressed() {

		Log.d(TAG, "onBackPressed:true");
		super.onBackPressed();
	}

	@Override
	public void handleLegalitiesAccept(String firstName, String surname, String email, String password, String birthdate, String acceptedLegalitiesVersion) {

		Log.d(TAG, "handleLegalitiesAccept:true");

		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

		firebaseAuth.createUserWithEmailAndPassword(email, password)
		            .addOnSuccessListener(authResult -> {

			            Log.d(TAG, "onSuccess:true");
			            createUserInFirestore(firstName, surname, email, password, birthdate, acceptedLegalitiesVersion);
			            displayEmailVerificationFragment(authResult.getUser());

		            })
		            .addOnFailureListener(e -> {

			            // TODO: exception handling!
			            // TODO: Move hardcoded strings to strings.xml.
			            Log.d(TAG, "onFailure:true");
			            Log.e(TAG, "onFailure: ", e);

			            if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {

				            Toast.makeText(this, "The email address is already in use by another account.", Toast.LENGTH_LONG)
				                 .show();

			            }

			            if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {

				            Toast.makeText(this, "Your email adress does not seem to fit. Please check it", Toast.LENGTH_LONG)
				                 .show();

			            }
		            });
	}

	/**
	 * Method uploads data from the SharedPreferences onto the Firestore cloud and creates a new user.
	 */
	private void createUserInFirestore(String firstName, String surname, String email, String password, String birthdate, String acceptedLegalitiesVersion) {

		Log.d(TAG, "createUserInFirestore:true");

		// Get Firestore instance.
		FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();

		// Get and save the data from shared preferences into a HashMap.
		Map<String, Object> userData = new HashMap<>();
		userData.put("first_name", firstName);
		userData.put("surname", surname);
		userData.put("email", email);
		userData.put("password", password);
		userData.put("birthdate", birthdate);
		userData.put("accepted_legalities_version", acceptedLegalitiesVersion);
		userData.put("when", new Date());

		// Add a new document with a generated ID.
		firestoreDB.collection("users")
		           .add(userData)
		           .addOnSuccessListener(documentReference -> {

			           Log.d(TAG, "createUserInFirestore: success");
			           Log.d(TAG, "createUserInFirestore: new user added with id = " + documentReference.getId());
			           // Insert User into database.
			           mDBHelper.insertUser(documentReference.getId(), firstName, surname, email, password, birthdate, acceptedLegalitiesVersion);

		           })
		           .addOnFailureListener(e -> {

			           Log.d(TAG, "createUserInFirestore: failure");
			           Log.e(TAG, "createUserInFirestore: ", e);

		           });
	}

	/**
	 * Loads the EmailVerificationFragment into the activities container.
	 * The user is not allowed to step any further into the app without having his email verified.
	 *
	 * @param user Pass the FirebaseUser to the fragment because there is a mButton that allows him to kick off the email verification again.
	 */
	@Override
	public void displayEmailVerificationFragment(FirebaseUser user) {

		Log.d(TAG, "displayEmailVerificationFragment:true");
		Log.d(TAG, "displayEmailVerificationFragment: mComesFromEmailVerificatoinFragment = " + mComesFromEmailVerificationFragment);

		if (!mComesFromEmailVerificationFragment) {

			Log.d(TAG, "displayEmailVerificationFragment: umm hello");
			//			new DisplayFragment(this).displayFragmentBackstack(R.id.login_container, EmailVerificationFragment.newInstance(user));
		}

		mComesFromEmailVerificationFragment = false;
	}

	/**
	 * Loads the SignUpFragment into the activities container.
	 *
	 * @param email Passes the email to the fragment if the user has accidently typed one in.
	 */
	@Override
	public void displaySignUpFragment(String email) {

		Log.d(TAG, "displaySignUpFragment:true");
		//		new DisplayFragment(this).displayFragmentBackstack(R.id.login_container, SignUpFragment.newInstance(null, null, null, email));
	}

	/**
	 * Kicks off an Intent and starts the MainActivity via it.
	 *
	 * @param user    Pass the FirebaseUser over to the MainActivity so it's usable there.
	 * @param account Pass the GoogleAccount to the MainActivity so it's usable there.
	 */
	@Override
	public void startMainActivity(FirebaseUser user, GoogleSignInAccount account) {

		Log.d(TAG, "startMainActivity:true");

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("firebase_user", user);
		intent.putExtra("user_firestore_id", getSharedPreferences(NAME_PREFS_FIRESTORE_ID, Context.MODE_PRIVATE).getString(FIRESTORE_ID_KEY, null));
		intent.putExtra("google_sign_in_account", account);

		startActivity(intent);
	}

	@Override
	public void startOnboardingActivity(FirebaseUser user, GoogleSignInAccount account) {

		Log.d(TAG, "startOnboardingActivity:true");

		Intent intent = new Intent(this, OnboardingActivity.class);
		intent.putExtra("firebase_user", user);
		intent.putExtra("sql_user", (String) null);
		intent.putExtra("google_account", account);

		startActivity(intent);
	}

	@Override
	public Activity getActivitiesContext() {

		return this;
	}

	@Override
	public void displayProgressBar() {

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
	public LayoutInflater getLayoutInflaterForDialog() {

		Log.d(TAG, "getLayoutInflater:true");
		return getLayoutInflater();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void loginWithUser(FirebaseUser user) {

		Log.d(TAG, "loginWithUser:true");
		updateUI(user);
	}

	@Override
	public void onClick(View v) {

		Log.d(TAG, "onClick:true");

		switch (v.getId()) {

			case R.id.entry_activity_login_google_button:
				Log.d(TAG, "onClick: id = google login button");

				// Google login.
				Intent googleLoginIntent = mGoogleSignInClient.getSignInIntent();
				startActivityForResult(googleLoginIntent, REQUEST_CODE_LOGIN);

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

		Log.d(TAG, "onActivityResult:true");

		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent.
		if (requestCode == REQUEST_CODE_LOGIN) {

			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

			try {

				// Google login was successful, login with Firebase.
				GoogleSignInAccount gsa = task.getResult(ApiException.class);
				firebaseAuthWithGoogleAccount(gsa);

			} catch (ApiException e) {

				// Google login failed. Update UI appropriately.
				Log.e(TAG, "onActivityResult: ", e);

				Toast.makeText(this, "Oops. Something went wrong", Toast.LENGTH_LONG)
				     .show();

			}
		}
	}

	private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {

		Log.d(TAG, "firebaseAuthWithGoogleAccount:true");
		Log.d(TAG, "firebaseAuthWithGoogleAccount: id = " + account.getId());

		AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

		mAuth.signInWithCredential(authCredential)
		     .addOnSuccessListener(command -> {

			     Log.d(TAG, "firebaseAuthWithGoogleAccount: success");

			     // Login success.
			     FirebaseUser user = mAuth.getCurrentUser();
			     updateUI(user);

		     })
		     .addOnFailureListener(e -> {

			     Log.e(TAG, "firebaseAuthWithGoogleAccount: ", e);

			     // TODO

		     });
	}

	class BackgroundTransitionTransformer
			extends ViewPager2.OnPageChangeCallback {

		private static final String TAG = "BackgroundTransitionTransformer";
		int[] mArrayOfColors;
		private ViewPager2 mViewPager2;
		private ScrollView mScrollView;
		private float      mPositionOffset = 0;
		private int        mPosition;

		@SuppressLint ("LongLogTag")
		BackgroundTransitionTransformer(AppCompatActivity context, ViewPager2 viewPager2) {

			super();

			Log.d(TAG, "BackgroundTransitionTransformer:true");

			mViewPager2 = viewPager2;

			// Populate the int[] for the colors.
			if (viewPager2.getAdapter() != null) {
				mArrayOfColors = new int[viewPager2.getAdapter()
				                                   .getItemCount()];

				for (int i = 0; i <= viewPager2.getAdapter()
				                               .getItemCount(); i++) {

					Log.d(TAG, "i = " + i);

					switch (i) {

						case 0:
							mArrayOfColors[i] = getResources().getColor(R.color.silver);
							break;
						case 1:
							mArrayOfColors[i] = getResources().getColor(R.color.gold);
							break;
						case 2:
							mArrayOfColors[i] = getResources().getColor(R.color.platinum);
							break;

					}
				}

			} else {

				mArrayOfColors[0] = getResources().getColor(R.color.silver);
				mArrayOfColors[1] = getResources().getColor(R.color.gold);
				mArrayOfColors[2] = getResources().getColor(R.color.platinum);

			}

			mScrollView = context.findViewById(R.id.entry_activity_scroll_view);
		}

		@SuppressLint ("LongLogTag")
		float getPositionOffset() {

			Log.d(TAG, "getPositionOffset:true");
			return mPositionOffset;
		}

		@SuppressLint ("LongLogTag")
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			Log.d(TAG, "onPageScrolled:true");
			mPosition = position;
			mPositionOffset = positionOffset;
		}

		@Override
		public void onPageSelected(int position) {

			super.onPageSelected(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {

			super.onPageScrollStateChanged(state);
		}

		@SuppressLint ("LongLogTag")
		int getPosition() {

			Log.d(TAG, "getPosition:true");
			return mPosition;
		}
	}
}