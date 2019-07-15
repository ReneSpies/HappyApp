package android.aresid.happyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EntryActivity
		extends AppCompatActivity
		implements GoogleApiClient.OnConnectionFailedListener,
		           ViewPagerAdapter.OnViewPagerInteractionListener,
		           View.OnClickListener,
		           PurchasesUpdatedListener,
		           BillingClientStateListener,
		           RetrieveInternetTime.OnInternetTimeInteractionListener {

	static final         String             VP_TITLE_HAPPYAPP_FREE = "HappyApp Free";
	static final         String             VP_TITLE_HAPPYAPP_GOLD = "HappyApp Gold";
	static final         String             VP_DESC_HAPPYAPP_FREE  = "Desc for HappyApp Free";
	static final         String             VP_DESC_HAPPYAPP_GOLD  = "Desc for HappyApp Gold";
	static final         String             VP_PRICE_HAPPYAPP_FREE = "Free/Month";
	static final         String             VP_PRICE_HAPPYAPP_GOLD = "$6.99/Month";
	private static final String             TAG                    = "EntryActivity";
	private static final String             GOOGLE_COM             = "google.com";
	private static final int                REQUEST_CODE_LOGIN     = 13;
	private              DBHelper           mDBHelper;
	private              GoogleSignInClient mGoogleSignInClient;
	private              FirebaseAuth       mAuth;
	private              BillingClient      mBillingClient;
	private              int                mCurrentTimeSyncHelper = 0;
	private              int                mBackPressedHelper     = 0;
	private              int                mCreateUserHelper      = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);

		// Instantiate FirebaseAuth.
		mAuth = FirebaseAuth.getInstance();
		mAuth.addAuthStateListener(firebaseAuth -> {

			Log.d(TAG, "onCreate: auth state changed");

			if (firebaseAuth.getCurrentUser() == null) {

				Log.d(TAG, "onCreate: user = " + firebaseAuth.getCurrentUser());

				changeFromLoadingScreen();

			}

		});

		// Load waiting assistant into ImageViews.
		loadGifInto(findViewById(R.id.entry_activity_login_waiting_assistant));
		loadGifInto(findViewById(R.id.entry_activity_logging_in_waiting_assistant));
//		loadGifInto(findViewById(R.id.entry_activity_subscription_waiting_assistant));

//		mBillingClient = BillingClient.newBuilder(this)
//		                              .setListener(this)
//		                              .build();
//		mBillingClient.startConnection(this);

		// Configure Google Sign In.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getResources().getString(R.string.default_web_client_id))
		                                                                                              .requestEmail()
		                                                                                              .build();

		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		// Access all views that are needed.
		Button btLogin = findViewById(R.id.entry_activity_login_login_button);
		ScrollView sv = findViewById(R.id.entry_activity_scroll_view);
		Button btGoogleLogin = findViewById(R.id.entry_activity_login_google_button);
		TextInputEditText etRegistrationDateOfBirthField = findViewById(R.id.entry_activity_registration_date_of_birth_field);
//		Button btCheckOut = findViewById(R.id.entry_activity_subscription_check_out_button);
		ViewPager2 vpSubscriptions = findViewById(R.id.entry_activity_subscription_view_pager);

		List<String> listOfTitles = new ArrayList<>();
		List<String> listOfDescriptions = new ArrayList<>();
		List<String> listOfPriceTags = new ArrayList<>();

		// List population for HappyApp Free
		listOfTitles.add(VP_TITLE_HAPPYAPP_FREE);
		listOfDescriptions.add(VP_DESC_HAPPYAPP_FREE);
		listOfPriceTags.add(VP_PRICE_HAPPYAPP_FREE);

		// List population for HappyApp Gold
		listOfTitles.add(VP_TITLE_HAPPYAPP_GOLD);
		listOfDescriptions.add(VP_DESC_HAPPYAPP_GOLD);
		listOfPriceTags.add(VP_PRICE_HAPPYAPP_GOLD);

		vpSubscriptions.setAdapter(new ViewPagerAdapter(this, listOfTitles, listOfDescriptions, listOfPriceTags, vpSubscriptions));
//		btCheckOut.setOnClickListener(this);
		btLogin.setOnClickListener(this);
		sv.setSmoothScrollingEnabled(true);
		btGoogleLogin.setOnClickListener(this);
		etRegistrationDateOfBirthField.setOnClickListener(this);
		etRegistrationDateOfBirthField.setOnFocusChangeListener((v, hasFocus) -> {

			if (hasFocus) {

				new DatePickerFragment(this, (EditText) v).show(getSupportFragmentManager(), "date picker");

			}

		});
		etRegistrationDateOfBirthField.setKeyListener(null);

		if (mDBHelper == null) {

			// Create new database if not exists.
			mDBHelper = new DBHelper(this);

			Log.d(TAG, "onCreate: db = " + mDBHelper);

		}

		populateSubscriptionsTable();

	}

	@Override
	public void onStart() {

		Log.d(TAG, "onStart:true");

		super.onStart();

		FirebaseUser user = FirebaseAuth.getInstance()
		                                .getCurrentUser();

		updateUI(user);
	}

	@Override
	public void createUser() {

		Log.d(TAG, "createUser:true");

		TextInputEditText etRegistrationEmailField = findViewById(R.id.entry_activity_registration_email_field);
		TextInputEditText etRegistrationPasswordField = findViewById(R.id.entry_activity_registration_password_field);
		TextInputEditText etRegistrationFirstNameField = findViewById(R.id.entry_activity_registration_first_name_field);
		TextInputEditText etRegistrationFamilyNameField = findViewById(R.id.entry_activity_registration_family_name_field);
		TextInputEditText etRegistrationDobField = findViewById(R.id.entry_activity_registration_date_of_birth_field);
		TextInputEditText etRegistrationUsernameField = findViewById(R.id.entry_activity_registration_username_field);
		TextInputLayout etRegistrationEmailLayout = findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout etRegistrationPasswordLayout = findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout etRegistrationFirstNameLayout = findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout etRegistrationFamilyNameLayout = findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout etRegistrationDobLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout etRegistrationUsernameLayout = findViewById(R.id.entry_activity_registration_username_layout);

		String email = etRegistrationEmailField.getText()
		                                       .toString();
		String password = etRegistrationPasswordField.getText()
		                                             .toString();
		String firstName = etRegistrationFirstNameField.getText()
		                                               .toString();
		String username = etRegistrationUsernameField.getText()
		                                             .toString();
		String familyName = etRegistrationFamilyNameField.getText()
		                                                 .toString();
		String dob = etRegistrationDobField.getText()
		                                   .toString();

		FirebaseFirestore db = FirebaseFirestore.getInstance();

		FirebaseUser user = mAuth.getCurrentUser();

		if (user != null) {

			if (user.getProviderData()
			        .get(1)
			        .getProviderId()
			        .equals(GOOGLE_COM)) {

				Log.d(TAG, "createUser: google user");

				if (evaluateGoogleUserInfo(firstName, familyName, username, dob)) {

					etRegistrationFirstNameLayout.setEnabled(false);
					etRegistrationFamilyNameLayout.setEnabled(false);
					etRegistrationUsernameLayout.setEnabled(false);
					etRegistrationDobLayout.setEnabled(false);

					db.collection(FirestoreNames.COLLECTION_USERS)
					  .whereEqualTo(FirestoreNames.COLUMN_USERNAME, username)
					  .get()
					  .addOnSuccessListener(command -> {

						  Log.d(TAG, "createUser:success");

						  if (command.isEmpty()) {

							  // NO MATCH

							  saveUserInFirestore(user, firstName, familyName, username, user.getEmail(), dob);

						  } else {

							  Log.d(TAG, "createUser: username already taken");

							  etRegistrationUsernameLayout.setError(getString(R.string.plain_username_is_already_taken));

							  etRegistrationFirstNameLayout.setEnabled(true);
							  etRegistrationFamilyNameLayout.setEnabled(true);
							  etRegistrationUsernameLayout.setEnabled(true);
							  etRegistrationDobLayout.setEnabled(true);

						  }

					  })
					  .addOnFailureListener(e -> {

						  Log.e(TAG, "createUser: ", e);

						  Toast.makeText(this, getString(R.string.contraction_server_connection_failed), Toast.LENGTH_LONG)
						       .show();

						  etRegistrationFirstNameLayout.setEnabled(true);
						  etRegistrationFamilyNameLayout.setEnabled(true);
						  etRegistrationUsernameLayout.setEnabled(true);
						  etRegistrationDobLayout.setEnabled(true);

					  });
				}

			}

		} else {

			Log.d(TAG, "createUser: new user");

			if (evaluateNewUserInfo(firstName, username, familyName, email, password, dob)) {

				etRegistrationFirstNameField.setEnabled(false);
				etRegistrationFamilyNameField.setEnabled(false);
				etRegistrationUsernameField.setEnabled(false);
				etRegistrationEmailField.setEnabled(false);
				etRegistrationPasswordField.setEnabled(false);
				etRegistrationDobField.setEnabled(false);

				db.collection(FirestoreNames.COLLECTION_USERS)
				  .whereEqualTo(FirestoreNames.COLUMN_USERNAME, username)
				  .get()
				  .addOnSuccessListener(command -> {

					  if (command.isEmpty()) {

						  FirebaseAuth.getInstance()
						              .createUserWithEmailAndPassword(email, password)
						              .addOnSuccessListener(result -> {

							              Log.d(TAG, "createUser: success");

							              result.getUser()
							                    .updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(etRegistrationUsernameField.getText()
							                                                                                                                    .toString())
							                                                                         .build());

							              saveUserInFirestore(result.getUser(), firstName, familyName, username, email, dob);

						              })
						              .addOnFailureListener(e -> {

							              Log.d(TAG, "createUser: failure");
							              Log.e(TAG, "createUser: ", e);

							              etRegistrationFirstNameField.setEnabled(true);
							              etRegistrationFamilyNameField.setEnabled(true);
							              etRegistrationUsernameField.setEnabled(true);
							              etRegistrationEmailField.setEnabled(true);
							              etRegistrationPasswordField.setEnabled(true);
							              etRegistrationDobField.setEnabled(true);

							              // TODO

							              if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {

								              smoothScrollTo(etRegistrationUsernameLayout.getBottom());

								              etRegistrationEmailLayout.setError(getString(R.string.plain_this_email_is_already_in_use));

							              } else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {

								              smoothScrollTo(etRegistrationUsernameLayout.getBottom());

								              etRegistrationEmailLayout.setError(getString(R.string.plain_this_email_is_badly_formatted));

							              } else {

								              Toast.makeText(this, getString(R.string.contraction_standard_error_message), Toast.LENGTH_SHORT)
								                   .show();

							              }

						              });

					  } else {

						  etRegistrationFirstNameField.setEnabled(true);
						  etRegistrationFamilyNameField.setEnabled(true);
						  etRegistrationUsernameField.setEnabled(true);
						  etRegistrationEmailField.setEnabled(true);
						  etRegistrationPasswordField.setEnabled(true);
						  etRegistrationDobField.setEnabled(true);

						  smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());

						  etRegistrationUsernameLayout.setError(getString(R.string.plain_username_is_already_taken));

					  }

				  })
				  .addOnFailureListener(e -> {

					  Log.d(TAG, "createUser: query failure");
					  Log.e(TAG, "createUser: ", e);

					  Toast.makeText(this, getString(R.string.contraction_standard_error_message), Toast.LENGTH_SHORT)
					       .show();

					  etRegistrationFirstNameField.setEnabled(true);
					  etRegistrationFamilyNameField.setEnabled(true);
					  etRegistrationUsernameField.setEnabled(true);
					  etRegistrationEmailField.setEnabled(true);
					  etRegistrationPasswordField.setEnabled(true);
					  etRegistrationDobField.setEnabled(true);

				  });

			}

		}

	}

	private void changeToLoadingScreen() {

		Log.d(TAG, "changeToLoadingScreen:true");

		findViewById(R.id.entry_activity_logging_in_waiting_layout).setVisibility(View.VISIBLE);
		findViewById(R.id.entry_activity_constraint_layout).setVisibility(View.GONE);

	}

	private void startGoogleEmailVerificationActivity(FirebaseUser user) {

		Log.d(TAG, "startGoogleEmailVerificationActivity:true");

		Intent intent = new Intent(this, EmailVerificationActivity.class);
		intent.putExtra("user", user);

		startActivity(intent);

	}

	void startMainActivity(FirebaseUser user) {

		Log.d(TAG, "startMainActivity:true");

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("firebase_user", user);

		startActivity(intent);

	}

	void startEmailVerificationActivity(FirebaseUser user) {

		Log.d(TAG, "startEmailVerificationActivity:true");

		Intent intent = new Intent(this, EmailVerificationActivity.class);
		intent.putExtra("firebase_user", user);

		startActivity(intent);

	}

	private void changeFromLoadingScreen() {

		Log.d(TAG, "changeFromLoadingScreen:true");

		findViewById(R.id.entry_activity_logging_in_waiting_layout).setVisibility(View.GONE);
		findViewById(R.id.entry_activity_constraint_layout).setVisibility(View.VISIBLE);

	}

	private void loadGifInto(ImageView view) {

		Log.d(TAG, "loadGifInto:true");

		Glide.with(this)
		     .load(getResources().getDrawable(R.drawable.waiting_assistant_content))
		     .into(view);

	}

	/**
	 * Method populates the Subscriptions table in the db with the server data.
	 */
	private void populateSubscriptionsTable() {

		Log.d(TAG, "populateSubscriptionsTable:true");
		// TODO

	}

	private boolean evaluateGoogleUserInfo(String firstName, String familyName, String username, String dob) {

		Log.d(TAG, "evaluateGoogleUserInfo:true");

		setLayoutErrorsNull();

		TextInputLayout etRegistrationFirstNameLayout = findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout etRegistrationFamilyNameLayout = findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout etRegistrationDobLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout etRegistrationUsernameLayout = findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox cbTermsConditionsPrivacyPolicy = findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);

		if (firstName.length() == 0) {

			etRegistrationFirstNameLayout.setError(getString(R.string.contraction_empty_credentials_field));

			smoothScrollTo(etRegistrationFirstNameLayout.getTop());

			return false;

		} else if (firstName.startsWith(" ")) {

			etRegistrationFirstNameLayout.setError(getString(R.string.contraction_field_whitespace, R.string.plain_first_name));

			smoothScrollTo(etRegistrationFirstNameLayout.getTop());

			return false;

		} else if (familyName.length() == 0) {

			etRegistrationFamilyNameLayout.setError(getString(R.string.contraction_empty_credentials_field));

			smoothScrollTo(etRegistrationFamilyNameLayout.getTop());

			return false;

		} else if (familyName.startsWith(" ")) {

			etRegistrationFamilyNameLayout.setError(getString(R.string.contraction_field_whitespace, R.string.plain_family_name));

			smoothScrollTo(etRegistrationFamilyNameLayout.getTop());

			return false;

		} else if (username.length() == 0) {

			etRegistrationUsernameLayout.setError(getString(R.string.contraction_empty_credentials_field));

			smoothScrollTo(etRegistrationUsernameLayout.getTop());

			return false;

		} else if (username.startsWith(" ")) {

			etRegistrationUsernameLayout.setError(getString(R.string.contraction_field_whitespace, R.string.plain_username));

			smoothScrollTo(etRegistrationUsernameLayout.getTop());

			return false;

		} else if (dob.length() == 0) {

			etRegistrationDobLayout.setError(getString(R.string.contraction_empty_credentials_field));

			smoothScrollTo(etRegistrationDobLayout.getTop());

			return false;

		} else if (!cbTermsConditionsPrivacyPolicy.isChecked()) {

			cbTermsConditionsPrivacyPolicy.setError(getString(R.string.plain_please_confirm));

			smoothScrollTo(etRegistrationDobLayout.getBottom());

			return false;

		}

		return true;

	}

	private boolean evaluateNewUserInfo(String firstName, String username, String familyName, String email, String password, String dob) {

		Log.d(TAG, "evaluateNewUserInfo:true");

		setLayoutErrorsNull();

		TextInputLayout etRegistrationEmailLayout = findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout etRegistrationPasswordLayout = findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout etRegistrationFirstNameLayout = findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout etRegistrationFamilyNameLayout = findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout etRegistrationDobLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout etRegistrationUsernameLayout = findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox cbTermsConditionsPrivacyPolicy = findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);

		if (firstName.length() == 0) {

			etRegistrationFirstNameLayout.setError(getString(R.string.contraction_empty_credentials_field));

			smoothScrollTo(etRegistrationFirstNameLayout.getTop());

			return false;

		} else if (firstName.startsWith(" ")) {

			etRegistrationFirstNameLayout.setError(getString(R.string.contraction_field_whitespace, R.string.plain_first_name));

			smoothScrollTo(etRegistrationFirstNameLayout.getTop());

			return false;

		} else if (familyName.length() == 0) {

			etRegistrationFamilyNameLayout.setError(getString(R.string.contraction_empty_credentials_field));

			smoothScrollTo(etRegistrationFirstNameLayout.getBottom());

			return false;

		} else if (familyName.startsWith(" ")) {

			etRegistrationFamilyNameLayout.setError(getString(R.string.contraction_field_whitespace, R.string.plain_family_name));

			smoothScrollTo(etRegistrationFirstNameLayout.getBottom());

			return false;

		} else if (username.length() == 0) {

			etRegistrationUsernameLayout.setError(getString(R.string.contraction_empty_credentials_field));

			smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());

			return false;

		} else if (username.startsWith(" ")) {

			etRegistrationUsernameLayout.setError(getString(R.string.contraction_field_whitespace, R.string.plain_username));

			smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());

			return false;

		} else if (email.length() == 0) {

			etRegistrationEmailLayout.setError(getString(R.string.contraction_empty_credentials_field));

			smoothScrollTo(etRegistrationUsernameLayout.getBottom());

			return false;

		} else if (email.startsWith(" ")) {

			etRegistrationEmailLayout.setError(getString(R.string.contraction_field_whitespace, R.string.plain_email));

			smoothScrollTo(etRegistrationUsernameLayout.getBottom());

			return false;

		} else if (password.length() == 0) {

			etRegistrationPasswordLayout.setError(getString(R.string.contraction_empty_credentials_field));

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return false;

		} else if (password.startsWith(" ")) {

			etRegistrationPasswordLayout.setError(getString(R.string.contraction_field_whitespace, R.string.plain_password));

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return false;

		} else if (password.length() < 6) {

			etRegistrationPasswordLayout.setError(getString(R.string.contraction_password_length));

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return false;

		} else if (dob.length() == 0) {

			etRegistrationDobLayout.setError(getString(R.string.contraction_empty_credentials_field));

			smoothScrollTo(etRegistrationPasswordLayout.getBottom());

			return false;

		} else if (!cbTermsConditionsPrivacyPolicy.isChecked()) {

			cbTermsConditionsPrivacyPolicy.setError(getString(R.string.plain_please_confirm));

			smoothScrollTo(etRegistrationDobLayout.getBottom());

			return false;

		}

		return true;

	}

	private void saveUserInFirestore(FirebaseUser user, String firstName, String familyName, String username, String email, String dob) {

		Log.d(TAG, "saveUserInFirestore:true");

		FirebaseFirestore db = FirebaseFirestore.getInstance();

		Toast toast = Toast.makeText(this, getString(R.string.contraction_standard_error_message_standby), Toast.LENGTH_SHORT);

		HashMap<String, String> userInfo = new HashMap<>();
		userInfo.put(FirestoreNames.COLUMN_FIRST_NAME, firstName);
		userInfo.put(FirestoreNames.COLUMN_FAMILY_NAME, familyName);
		userInfo.put(FirestoreNames.COLUMN_USERNAME, username);
		userInfo.put(FirestoreNames.COLUMN_EMAIL, email);
		userInfo.put(FirestoreNames.COLUMN_DATE_OF_BIRTH, dob);

		db.collection(FirestoreNames.COLLECTION_USERS)
		  .document(user.getUid())
		  .set(userInfo)
		  .addOnSuccessListener(command -> {

			  Log.d(TAG, "saveUserInFirestore: success");

			  toast.cancel();

			  mCreateUserHelper = 0;

			  new RetrieveInternetTime(this, user.getUid()).execute("time.google.com");

			  updateUI(user);

		  })
		  .addOnFailureListener(e -> {

			  Log.d(TAG, "saveUserInFirestore: failure");
			  Log.e(TAG, "saveUserInFirestore: ", e);

			  toast.cancel();

			  if (mCreateUserHelper <= 3) {

				  toast.show();

				  mCreateUserHelper++;

				  saveUserInFirestore(user, firstName, familyName, username, email, dob);

			  }

		  });

	}

	@Override
	public void onBackPressed() {

		Log.d(TAG, "onBackPressed:true");

		Toast toast = Toast.makeText(this, getString(R.string.plain_press_again_to_exit), Toast.LENGTH_SHORT);

		if (mBackPressedHelper == 0) {

			Log.d(TAG, "onBackPressed: helper == 0");

			toast.show();

			mBackPressedHelper = 1;

			new Handler().postDelayed(() -> {

				Log.d(TAG, "run:true");

				mBackPressedHelper = 0;

			}, 6130);

		} else {

			if (mBackPressedHelper == 1) {

				Log.d(TAG, "onBackPressed: helper == 1");

				toast.cancel();

				finishAffinity();

			}

		}

		finishAffinity();

	}

	public void loginUser() {

		Log.d(TAG, "loginWithUser:true");

		EditText etEmailField = findViewById(R.id.entry_activity_login_email_field);
		EditText etPasswordField = findViewById(R.id.entry_activity_login_password_field);
		TextInputLayout etEmailFieldLayout = findViewById(R.id.entry_activity_login_email_layout);
		TextInputLayout etPasswordFieldLayout = findViewById(R.id.entry_activity_login_password_layout);

		String email = etEmailField.getText()
		                           .toString();
		String password = etPasswordField.getText()
		                                 .toString();

		setLayoutErrorsNull();

		if (email.length() == 0) {

			smoothScrollTo(etEmailFieldLayout.getTop());

			etEmailFieldLayout.setError(getString(R.string.contraction_empty_credentials_field));

			return;

		} else if (password.length() == 0) {

			smoothScrollTo(etEmailFieldLayout.getBottom());

			etPasswordFieldLayout.setError(getString(R.string.contraction_empty_credentials_field));

			return;

		}

		// Show loading assistant.
		findViewById(R.id.entry_activity_login_waiting_assistant_layout).setVisibility(View.VISIBLE);

		mAuth.signInWithEmailAndPassword(email, password)
		     .addOnSuccessListener(command -> {

			     Log.d(TAG, "loginUser: success");

			     updateUI(command.getUser());

		     })
		     .addOnFailureListener(e -> {

			     Log.d(TAG, "loginUser: failure");
			     Log.e(TAG, "loginUser: ", e);

			     setLayoutErrorsNull();

			     findViewById(R.id.entry_activity_login_waiting_assistant_layout).setVisibility(View.GONE);

			     // TODO

			     if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {

				     smoothScrollTo(etEmailFieldLayout.getTop());

				     etEmailFieldLayout.setError(getString(R.string.plain_email_or_password_incorrect));

			     } else if (e instanceof com.google.firebase.FirebaseNetworkException) {

				     smoothScrollTo(etEmailFieldLayout.getTop());

				     etEmailFieldLayout.setError(getString(R.string.contraction_check_internet_connection));

			     }

		     });

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

				break;

			case R.id.entry_activity_registration_date_of_birth_field:
				Log.d(TAG, "onClick: id = dob field");

				new DatePickerFragment(this, (EditText) v).show(getSupportFragmentManager(), "date picker");

				break;

			case R.id.entry_activity_login_login_button:
				Log.d(TAG, "onClick: id = login button");

				loginUser();

				break;

		}

	}

	void updateUI(FirebaseUser user) {

		Log.d(TAG, "updateUI:true");

		if (user != null) {

			changeToLoadingScreen();

			Toast toast = Toast.makeText(this, "Reloading user information", Toast.LENGTH_LONG);
			toast.show();

			user.reload()
			    .addOnSuccessListener(command -> {

				    Log.d(TAG, "updateUI: success");

				    toast.cancel();

				    if (user.isEmailVerified()) {

					    startMainActivity(user);

				    } else {

					    startEmailVerificationActivity(user);

				    }

			    })
			    .addOnFailureListener(e -> {

				    Log.d(TAG, "updateUI: failure");
				    Log.e(TAG, "updateUI: ", e);

				    // TODO

				    if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {

					    Toast.makeText(this, getString(R.string.contraction_user_not_found), Toast.LENGTH_LONG)
					         .show();

					    mAuth.signOut();

					    changeFromLoadingScreen();

				    }

			    });

		} else {

			Log.d(TAG, "updateUI: user == null");

			// TODO

		}

	}

	private void setLayoutErrorsNull() {

		Log.d(TAG, "setLayoutErrorsNull:true");

		TextInputLayout etRegistrationFirstNameLayout = findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout etRegistrationFamilyNameLayout = findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout etRegistrationEmailLayout = findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout etRegistrationPasswordLayout = findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout etRegistrationDobLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout etRegistrationNicknameLayout = findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox cbTermsConditionsPrivacyPolicy = findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);
		TextInputLayout emailLayout = findViewById(R.id.entry_activity_login_email_layout);
		TextInputLayout passwordLayout = findViewById(R.id.entry_activity_login_password_layout);

		cbTermsConditionsPrivacyPolicy.clearFocus();

		emailLayout.setError(null);
		passwordLayout.setError(null);
		etRegistrationFirstNameLayout.setError(null);
		etRegistrationFamilyNameLayout.setError(null);
		etRegistrationNicknameLayout.setError(null);
		etRegistrationEmailLayout.setError(null);
		etRegistrationPasswordLayout.setError(null);
		etRegistrationDobLayout.setError(null);
		cbTermsConditionsPrivacyPolicy.setError(null);

	}

	private void smoothScrollTo(float y) {

		Log.d(TAG, "smoothScrollTo:true");

		ScrollView svParent = findViewById(R.id.entry_activity_scroll_view);

		svParent.smoothScrollTo(0, (int) y);

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

				if (gsa != null) {

					firebaseAuthWithGoogleAccount(gsa);

				}

			} catch (ApiException e) {

				// Google login failed. Update UI appropriately.
				Log.e(TAG, "onActivityResult: ", e);

				Toast.makeText(this, getString(R.string.contraction_standard_error_message), Toast.LENGTH_SHORT)
				     .show();

			}

		}

	}

	private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {

		Log.d(TAG, "firebaseAuthWithGoogleAccount:true");
		Log.d(TAG, "firebaseAuthWithGoogleAccount: id = " + account.getId());
		Log.d(TAG, "firebaseAuthWithGoogleAccount: email = " + account.getEmail());
		Log.d(TAG, "firebaseAuthWithGoogleAccount: given name = " + account.getGivenName());
		Log.d(TAG, "firebaseAuthWithGoogleAccount: family name = " + account.getFamilyName());
		Log.d(TAG, "firebaseAuthWithGoogleAccount: photo url = " + account.getPhotoUrl());
		Log.d(TAG, "firebaseAuthWithGoogleAccount: display name = " + account.getDisplayName());
		Log.d(TAG, "firebaseAuthWithGoogleAccount: id token = " + account.getIdToken());
		Log.d(TAG, "firebaseAuthWithGoogleAccount: auth code = " + account.getServerAuthCode());

		for (Scope scope : account.getGrantedScopes()) {

			Log.d(TAG, "firebaseAuthWithGoogleAccount: scope = " + scope.toString());
			Log.d(TAG, "firebaseAuthWithGoogleAccount: scope uri = " + scope.getScopeUri());
			Log.d(TAG, "firebaseAuthWithGoogleAccount: scope hashcode = " + scope.hashCode());

		}

		changeToLoadingScreen();

		AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

		mAuth.signInWithCredential(authCredential)
		     .addOnSuccessListener(command -> {

			     Log.d(TAG, "firebaseAuthWithGoogleAccount: success");

			     // Login success.
			     FirebaseUser user = mAuth.getCurrentUser();

			     if (command.getAdditionalUserInfo()
			                .isNewUser()) {

				     Log.d(TAG, "firebaseAuthWithGoogleAccount: user is new");

				     changeToGoogleRegistration();

//				     saveUserInFirestore(user, account.getGivenName(), account.getFamilyName(), account.getDisplayName(), account.getEmail(), null);

			     } else {

				     updateUI(user);

			     }

		     })
		     .addOnFailureListener(e -> {

			     Log.e(TAG, "firebaseAuthWithGoogleAccount: ", e);

			     Toast.makeText(this, getString(R.string.contraction_standard_error_message), Toast.LENGTH_SHORT)
			          .show();

			     changeFromLoadingScreen();

		     });

	}

	private void changeToGoogleRegistration() {

		Log.d(TAG, "changeToGoogleRegistration:true");

		findViewById(R.id.entry_activity_registration_first_name_layout).setEnabled(false);
		findViewById(R.id.entry_activity_registration_family_name_layout).setEnabled(false);
		findViewById(R.id.entry_activity_registration_email_layout).setEnabled(false);
		findViewById(R.id.entry_activity_registration_password_layout).setEnabled(false);

	}

	private void changeFromGoogleRegistration() {

		Log.d(TAG, "changeFromGoogleRegistration:true");

		findViewById(R.id.entry_activity_registration_first_name_layout).setEnabled(true);
		findViewById(R.id.entry_activity_registration_family_name_layout).setEnabled(true);
		findViewById(R.id.entry_activity_registration_email_layout).setEnabled(true);
		findViewById(R.id.entry_activity_registration_password_layout).setEnabled(true);

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

		Log.d(TAG, "onConnectionFailed:true");

	}

	@Override
	public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

		Log.d(TAG, "onPurchasesUpdated:true");

	}

	@Override
	public void onBillingSetupFinished(BillingResult billingResult) {

		Log.d(TAG, "onBillingSetupFinished:true");

		if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

			// The BillingClient is ready. You can query purchases here.

		}

	}

	@Override
	public void onBillingServiceDisconnected() {

		Log.d(TAG, "onBillingServiceDisconnected:true");

		// Try to restart the connection on the next request to
		// Google Play by calling the startConnection() method.

	}

	@Override
	public void currentTime(Date time, String uid) {

		FirebaseFirestore db = FirebaseFirestore.getInstance();

		HashMap<String, Object> info = new HashMap<>();
		info.put(FirestoreNames.COLUMN_DATE_OF_CREATION, time.toString());

		db.collection(FirestoreNames.COLLECTION_USERS)
		  .document(uid)
		  .update(info)
		  .addOnSuccessListener(aVoid -> Log.d(TAG, "currentTime: success"))
		  .addOnFailureListener(e -> {

			  Log.d(TAG, "currentTime: failure");
			  Log.e(TAG, "currentTime: ", e);

			  if (mCurrentTimeSyncHelper <= 3) {

				  Toast.makeText(this, getString(R.string.contraction_standard_error_message), Toast.LENGTH_SHORT)
				       .show();

				  currentTime(time, uid);

				  mCurrentTimeSyncHelper++;

			  }

		  });

	}

}