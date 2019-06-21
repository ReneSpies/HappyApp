package android.aresid.happyapp;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EntryActivity
		extends AppCompatActivity
		implements GoogleApiClient.OnConnectionFailedListener,
		           ViewPagerAdapter.OnViewPagerInteractionListener,
		           View.OnClickListener,
		           PurchasesUpdatedListener,
		           BillingClientStateListener,
		           RetrieveInternetTime.OnInternetTimeInteractionListener {

	private final static String             TAG                    = "EntryActivity";
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
//		setContentView(R.layout.activity_entry);
		setContentView(R.layout.activity_google_email_verification);

		// Instantiate FirebaseAuth.
		mAuth = FirebaseAuth.getInstance();

		// Load waiting assistant into ImageViews.
		loadGifInto(findViewById(R.id.entry_activity_login_waiting_assistant));
		loadGifInto(findViewById(R.id.entry_activity_logging_in_waiting_assistant));
		loadGifInto(findViewById(R.id.entry_activity_subscription_waiting_assistant));

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
		Button btCheckOut = findViewById(R.id.entry_activity_subscription_check_out_button);

		btCheckOut.setOnClickListener(this);
		btLogin.setOnClickListener(this);
		sv.setSmoothScrollingEnabled(true);
		btGoogleLogin.setOnClickListener(this);
		etRegistrationDateOfBirthField.setOnClickListener(this);
		etRegistrationDateOfBirthField.setOnFocusChangeListener((v, hasFocus) -> {

			if (hasFocus) {

				new DatePickerFragment(this).show(getSupportFragmentManager(), "date picker");

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

	void startMainActivity(FirebaseUser user) {

		Log.d(TAG, "startMainActivity:true");
		// TODO

	}

	void startEmailVerificationActivity(FirebaseUser user) {

		Log.d(TAG, "startEmailVerificationActivity:true");
		// TODO

	}

	@Override
	public void createUserWithEmailAndPassword() {

		Log.d(TAG, "createUserWithEmailAndPassword:true");

		TextInputEditText etRegistrationEmailField = findViewById(R.id.entry_activity_registration_email_field);
		TextInputEditText etRegistrationPasswordField = findViewById(R.id.entry_activity_registration_password_field);
		TextInputEditText etRegistrationFirstNameField = findViewById(R.id.entry_activity_registration_first_name_field);
		TextInputEditText etRegistrationFamilyNameField = findViewById(R.id.entry_activity_registration_family_name_field);
		TextInputEditText etRegistrationDobField = findViewById(R.id.entry_activity_registration_date_of_birth_field);
		TextInputEditText etRegistrationUsernameField = findViewById(R.id.entry_activity_registration_username_field);
		TextInputLayout etRegistrationUsernameLayout = findViewById(R.id.entry_activity_registration_username_layout);
		TextInputLayout etRegistrationEmailLayout = findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout etRegistrationPasswordLayout = findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout etRegistrationFirstNameLayout = findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout etRegistrationFamilyNameLayout = findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout etRegistrationDobLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		CheckBox cbTermsConditionsPrivacyPolicy = findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);
		FirebaseFirestore db = FirebaseFirestore.getInstance();

		String email = etRegistrationEmailField.getText()
		                                       .toString();
		String password = etRegistrationPasswordField.getText()
		                                             .toString();
		String username = etRegistrationUsernameField.getText()
		                                             .toString();
		String firstName = etRegistrationFirstNameField.getText()
		                                               .toString();
		String familyName = etRegistrationFamilyNameField.getText()
		                                                 .toString();
		String dob = etRegistrationDobField.getText()
		                                   .toString();

		setRegistrationLayoutErrorsNull();

		if (firstName.length() == 0) {

			etRegistrationFirstNameLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationFirstNameLayout.getTop());

			return;

		} else if (firstName.startsWith(" ")) {

			etRegistrationFirstNameLayout.setError("First Name cannot start with whitespace");

			smoothScrollTo(etRegistrationFirstNameLayout.getTop());

			return;

		} else if (familyName.length() == 0) {

			etRegistrationFamilyNameLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationFirstNameLayout.getBottom());

			return;

		} else if (familyName.startsWith(" ")) {

			etRegistrationFamilyNameLayout.setError("Family Name cannot start with whitespace");

			smoothScrollTo(etRegistrationFirstNameLayout.getBottom());

			return;

		} else if (username.length() == 0) {

			etRegistrationUsernameLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());

			return;

		} else if (username.startsWith(" ")) {

			etRegistrationUsernameLayout.setError("Nickname cannot start with whitespace");

			smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());

			return;

		} else if (email.length() == 0) {

			etRegistrationEmailLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationUsernameLayout.getBottom());

			return;

		} else if (email.startsWith(" ")) {

			etRegistrationEmailLayout.setError("Email cannot start with whitespace");

			smoothScrollTo(etRegistrationUsernameLayout.getBottom());

			return;

		} else if (password.length() == 0) {

			etRegistrationPasswordLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return;

		} else if (password.startsWith(" ")) {

			etRegistrationPasswordLayout.setError("Password cannot start with whitespace");

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return;

		} else if (password.length() < 6) {

			etRegistrationPasswordLayout.setError("I need to be longer than 6 characters of your choice");

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return;

		} else if (dob.length() == 0) {

			etRegistrationDobLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationPasswordLayout.getBottom());

			return;

		} else if (!cbTermsConditionsPrivacyPolicy.isChecked()) {

			cbTermsConditionsPrivacyPolicy.setError("Required field");

			smoothScrollTo(etRegistrationDobLayout.getBottom());

			return;

		}

		etRegistrationFirstNameField.setEnabled(false);
		etRegistrationFamilyNameField.setEnabled(false);
		etRegistrationUsernameField.setEnabled(false);
		etRegistrationEmailField.setEnabled(false);
		etRegistrationPasswordField.setEnabled(false);
		etRegistrationDobField.setEnabled(false);

		Button btCheckOut = findViewById(R.id.entry_activity_subscription_check_out_button);
		btCheckOut.setEnabled(false);
		findViewById(R.id.entry_activity_subscription_waiting_assistant_layout).setVisibility(View.VISIBLE);

		db.collection(FirestoreNames.COLLECTION_USERS)
		  .whereEqualTo(FirestoreNames.COLUMN_USERNAME, username)
		  .get()
		  .addOnSuccessListener(command -> {

			  if (command.isEmpty()) {

				  FirebaseAuth.getInstance()
				              .createUserWithEmailAndPassword(email, password)
				              .addOnSuccessListener(result -> {

					              Log.d(TAG, "createUserWithEmailAndPassword: success");

					              result.getUser()
					                    .updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(etRegistrationUsernameField.getText()
					                                                                                                                    .toString())
					                                                                         .build());

					              saveUserInFirestore(result.getUser(), firstName, familyName, username, email, dob);

				              })
				              .addOnFailureListener(e -> {

					              Log.d(TAG, "createUserWithEmailAndPassword: failure");
					              Log.e(TAG, "createUserWithEmailAndPassword: ", e);

					              btCheckOut.setEnabled(true);
					              etRegistrationFirstNameField.setEnabled(true);
					              etRegistrationFamilyNameField.setEnabled(true);
					              etRegistrationUsernameField.setEnabled(true);
					              etRegistrationEmailField.setEnabled(true);
					              etRegistrationPasswordField.setEnabled(true);
					              etRegistrationDobField.setEnabled(true);

					              findViewById(R.id.entry_activity_subscription_waiting_assistant_layout).setVisibility(View.INVISIBLE);

					              // TODO

					              if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {

						              smoothScrollTo(etRegistrationUsernameLayout.getBottom());

						              etRegistrationEmailLayout.setError("This email is already in use");

					              } else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {

						              smoothScrollTo(etRegistrationUsernameLayout.getBottom());

						              etRegistrationEmailLayout.setError("This email is badly formatted");

					              } else {

						              Toast.makeText(this, "Oops. Something went wrong. Please try again", Toast.LENGTH_SHORT)
						                   .show();

					              }

				              });

			  } else {

				  findViewById(R.id.entry_activity_subscription_waiting_assistant_layout).setVisibility(View.INVISIBLE);

				  btCheckOut.setEnabled(true);
				  etRegistrationFirstNameField.setEnabled(true);
				  etRegistrationFamilyNameField.setEnabled(true);
				  etRegistrationUsernameField.setEnabled(true);
				  etRegistrationEmailField.setEnabled(true);
				  etRegistrationPasswordField.setEnabled(true);
				  etRegistrationDobField.setEnabled(true);

				  smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());

				  etRegistrationUsernameLayout.setError("Username is already taken");

			  }

		  })
		  .addOnFailureListener(e -> {

			  Log.d(TAG, "createUserWithEmailAndPassword: query failure");
			  Log.e(TAG, "createUserWithEmailAndPassword: ", e);

			  Toast.makeText(this, "Something went wrong. Please try again", Toast.LENGTH_SHORT)
			       .show();

			  btCheckOut.setEnabled(true);
			  etRegistrationFirstNameField.setEnabled(true);
			  etRegistrationFamilyNameField.setEnabled(true);
			  etRegistrationUsernameField.setEnabled(true);
			  etRegistrationEmailField.setEnabled(true);
			  etRegistrationPasswordField.setEnabled(true);
			  etRegistrationDobField.setEnabled(true);

			  findViewById(R.id.entry_activity_subscription_waiting_assistant_layout).setVisibility(View.INVISIBLE);

		  });

	}

	private void saveUserInFirestore(FirebaseUser user, String firstName, String familyName, String username, String email, String dob) {

		Log.d(TAG, "saveUserInFirestore:true");

		FirebaseFirestore db = FirebaseFirestore.getInstance();

		Toast toast = Toast.makeText(this, "Something went wrong. Please stand by", Toast.LENGTH_SHORT);

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

		Toast toast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT);

		if (mBackPressedHelper == 0) {

			Log.d(TAG, "onBackPressed: helper == 0");

			toast.show();

			mBackPressedHelper = 1;

			// A Timer that resets my onBackPressed helper to 0 after 6.13 seconds.
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {

					Log.d(TAG, "run:true");

					mBackPressedHelper = 0;

				}

			}, 6130);

		} else {

			if (mBackPressedHelper == 1) {

				Log.d(TAG, "onBackPressed: helper == 1");

				toast.cancel();

				super.onBackPressed();

			}

		}

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

		setLoginLayoutErrorsNull();

		if (email.length() == 0) {

			smoothScrollTo(etEmailFieldLayout.getTop());

			etEmailFieldLayout.setError("You forgot me");

			return;

		} else if (password.length() == 0) {

			smoothScrollTo(etEmailFieldLayout.getBottom());

			etPasswordFieldLayout.setError("You forgot me");

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

			     setLoginLayoutErrorsNull();

			     findViewById(R.id.entry_activity_login_waiting_assistant_layout).setVisibility(View.GONE);

			     // TODO

			     if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {

				     smoothScrollTo(etEmailFieldLayout.getTop());

				     etEmailFieldLayout.setError("Email or password incorrect");

			     } else if (e instanceof com.google.firebase.FirebaseNetworkException) {

				     smoothScrollTo(etEmailFieldLayout.getTop());

				     etEmailFieldLayout.setError("Check your internet connection and try again");

			     }

		     });

	}

	private void setRegistrationLayoutErrorsNull() {

		Log.d(TAG, "setRegistrationLayoutErrorsNull:true");

		TextInputLayout etRegistrationFirstNameLayout = findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout etRegistrationFamilyNameLayout = findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout etRegistrationEmailLayout = findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout etRegistrationPasswordLayout = findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout etRegistrationDobLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout etRegistrationNicknameLayout = findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox cbTermsConditionsPrivacyPolicy = findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);

		cbTermsConditionsPrivacyPolicy.clearFocus();

		etRegistrationFirstNameLayout.setError(null);
		etRegistrationFamilyNameLayout.setError(null);
		etRegistrationNicknameLayout.setError(null);
		etRegistrationEmailLayout.setError(null);
		etRegistrationPasswordLayout.setError(null);
		etRegistrationDobLayout.setError(null);
		cbTermsConditionsPrivacyPolicy.setError(null);

	}

	void updateUI(FirebaseUser user) {

		Log.d(TAG, "updateUI:true");

		if (user != null) {

			if (user.getProviderData()
			        .get(1)
			        .getProviderId()
			        .equals("google.com")) {

				Log.d(TAG, "updateUI: display name = " + user.getDisplayName());
				Log.d(TAG, "updateUI: email = " + user.getEmail());
				Log.d(TAG, "updateUI: " + user.getPhoneNumber());

				// TODO

			}

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

					    Toast.makeText(this, "There is no user record corresponding to this identifier. The user may have been deleted", Toast.LENGTH_LONG)
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

	private void smoothScrollTo(float y) {

		Log.d(TAG, "smoothScrollTo:true");

		ScrollView svParent = findViewById(R.id.entry_activity_scroll_view);

		svParent.smoothScrollTo(0, (int) y);

	}

	private void changeToLoadingScreen() {

		Log.d(TAG, "changeToLoadingScreen:true");

		findViewById(R.id.entry_activity_logging_in_waiting_layout).setVisibility(View.VISIBLE);
		findViewById(R.id.entry_activity_constraint_layout).setVisibility(View.GONE);

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

				new DatePickerFragment(this).show(getSupportFragmentManager(), "date picker");

				break;

			case R.id.entry_activity_login_login_button:
				Log.d(TAG, "onClick: id = login button");

				loginUser();

				break;

			case R.id.entry_activity_subscription_check_out_button:
				Log.d(TAG, "onClick: id = check out button");

				createUserWithEmailAndPassword();

				break;

		}

	}

	private void changeFromLoadingScreen() {

		Log.d(TAG, "changeFromLoadingScreen:true");

		findViewById(R.id.entry_activity_logging_in_waiting_layout).setVisibility(View.GONE);
		findViewById(R.id.entry_activity_constraint_layout).setVisibility(View.VISIBLE);

	}

	private void setLoginLayoutErrorsNull() {

		Log.d(TAG, "setLoginLayoutErrorsNull:true");

		TextInputLayout emailLayout = findViewById(R.id.entry_activity_login_email_layout);
		TextInputLayout passwordLayout = findViewById(R.id.entry_activity_login_password_layout);

		emailLayout.setError(null);
		passwordLayout.setError(null);

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

				Toast.makeText(this, "Oops. Something went wrong. Please try again", Toast.LENGTH_SHORT)
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

				     saveUserInFirestore(user, account.getGivenName(), account.getFamilyName(), account.getDisplayName(), account.getEmail(), null);

			     } else {

				     updateUI(user);

			     }

		     })
		     .addOnFailureListener(e -> {

			     Log.e(TAG, "firebaseAuthWithGoogleAccount: ", e);

			     Toast.makeText(this, "Oops. Something went wrong. Please try again", Toast.LENGTH_SHORT)
			          .show();

			     changeFromLoadingScreen();

		     });

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

				  Toast.makeText(this, "Something went wrong synchronizing with the server. Trying again", Toast.LENGTH_SHORT)
				       .show();

				  currentTime(time, uid);

				  mCurrentTimeSyncHelper++;

			  }

		  });

	}

}