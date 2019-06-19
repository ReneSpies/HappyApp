package android.aresid.happyapp;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class EntryActivity
		extends AppCompatActivity
		implements GoogleApiClient.OnConnectionFailedListener,
		           ViewPagerAdapter.OnViewPagerInteractionListener,
		           View.OnClickListener,
		           PurchasesUpdatedListener,
		           BillingClientStateListener {

	private final static String             TAG                = "EntryActivity";
	private static final int                REQUEST_CODE_LOGIN = 13;
	private              DBHelper           mDBHelper;
	private              GoogleSignInClient mGoogleSignInClient;
	private              FirebaseAuth       mAuth;
	private              BillingClient      mBillingClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);

		// Instantiate FirebaseAuth.
		mAuth = FirebaseAuth.getInstance();

		// Load waiting assistant into ImageViews.
		Glide.with(this)
		     .load(getResources().getDrawable(R.drawable.waiting_assistant_content))
		     .into((ImageView) findViewById(R.id.entry_activity_login_waiting_assistant));
		Glide.with(this)
		     .load(getResources().getDrawable(R.drawable.waiting_assistant_content))
		     .into((ImageView) findViewById(R.id.entry_activity_logging_in_waiting_assistant));
		Glide.with(this)
		     .load(getResources().getDrawable(R.drawable.waiting_assistant_content))
		     .into((ImageView) findViewById(R.id.entry_activity_subscription_waiting_assistant));

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

	/**
	 * Method populates the Subscriptions table in the db with the server data.
	 */
	private void populateSubscriptionsTable() {

		Log.d(TAG, "populateSubscriptionsTable:true");
		// TODO

	}

	void updateUI(FirebaseUser user) {

		Log.d(TAG, "updateUI:true");

		Log.d(TAG, "updateUI: display name = " + user.getDisplayName());

		if (user != null) {

			if (user.getProviderData()
			        .get(1)
			        .getProviderId()
			        .equals("google.com")) {

				// TODO

			}

			findViewById(R.id.entry_activity_constraint_layout).setVisibility(View.GONE);
			findViewById(R.id.entry_activity_logging_in_waiting_layout).setVisibility(View.VISIBLE);

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

			    });

		} else {

			Log.d(TAG, "updateUI: user == null");

			// TODO

		}

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
		TextInputEditText etRegistrationNicknameField = findViewById(R.id.entry_activity_registration_nickname_field);
		TextInputLayout etRegistrationNicknameLayout = findViewById(R.id.entry_activity_registration_nickname_layout);
		TextInputLayout etRegistrationEmailLayout = findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout etRegistrationPasswordLayout = findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout etRegistrationFirstNameLayout = findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout etRegistrationFamilyNameLayout = findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout etRegistrationDobLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		CheckBox cbTermsConditionsPrivacyPolicy = findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);
		Query query = FirebaseFirestore.getInstance()
		                               .collection("users")
		                               .whereEqualTo("nickname", etRegistrationNicknameField.getText()
		                                                                                    .toString());

		String email = etRegistrationEmailField.getText()
		                                       .toString();
		String password = etRegistrationPasswordField.getText()
		                                             .toString();

		setRegistrationLayoutErrorsNull();

		if (etRegistrationFirstNameField.length() == 0) {

			etRegistrationFirstNameLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationFirstNameLayout.getTop());

			return;

		} else if (etRegistrationFirstNameField.getText()
		                                       .toString()
		                                       .startsWith(" ")) {

			etRegistrationFirstNameLayout.setError("First Name cannot start with whitespace");

			smoothScrollTo(etRegistrationFirstNameLayout.getTop());

			return;

		} else if (etRegistrationFamilyNameField.length() == 0) {

			etRegistrationFamilyNameLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationFirstNameLayout.getBottom());

			return;

		} else if (etRegistrationFamilyNameField.getText()
		                                        .toString()
		                                        .startsWith(" ")) {

			etRegistrationFamilyNameLayout.setError("Family Name cannot start with whitespace");

			smoothScrollTo(etRegistrationFirstNameLayout.getBottom());

			return;

		} else if (etRegistrationNicknameField.length() == 0) {

			etRegistrationNicknameLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());

			return;

		} else if (etRegistrationNicknameField.getText()
		                                      .toString()
		                                      .startsWith(" ")) {

			etRegistrationNicknameLayout.setError("Nickname cannot start with whitespace");

			smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());

			return;

		} else if (email.length() == 0) {

			etRegistrationEmailLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationNicknameLayout.getBottom());

			return;

		} else if (email.startsWith(" ")) {

			etRegistrationEmailLayout.setError("Email cannot start with whitespace");

			smoothScrollTo(etRegistrationNicknameLayout.getBottom());

			return;

		} else if (password.length() == 0) {

			etRegistrationPasswordLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return;

		} else if (etRegistrationPasswordField.getText()
		                                      .toString()
		                                      .startsWith(" ")) {

			etRegistrationPasswordLayout.setError("Password cannot start with whitespace");

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return;

		} else if (password.length() < 6) {

			etRegistrationPasswordLayout.setError("I need to be longer than 6 characters of your choice");

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return;

		} else if (etRegistrationDobField.length() == 0) {

			etRegistrationDobLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationPasswordLayout.getBottom());

			return;

		} else if (!cbTermsConditionsPrivacyPolicy.isChecked()) {

			cbTermsConditionsPrivacyPolicy.setError("Required field");

			smoothScrollTo(etRegistrationDobLayout.getBottom());

			return;

		}

		Button btCheckOut = findViewById(R.id.entry_activity_subscription_check_out_button);
		btCheckOut.setEnabled(false);
		findViewById(R.id.entry_activity_subscription_waiting_assistant_layout).setVisibility(View.VISIBLE);

		FirebaseAuth.getInstance()
		            .createUserWithEmailAndPassword(email, password)
		            .addOnSuccessListener(command -> {

			            Log.d(TAG, "createUserWithEmailAndPassword: success");
			            Log.d(TAG, "createUserWithEmailAndPassword: user = " + command.getUser());

			            command.getUser()
			                   .updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(etRegistrationNicknameField.getText()
			                                                                                                                   .toString())
			                                                                        .build());

			            updateUI(command.getUser());

		            })
		            .addOnFailureListener(e -> {

			            Log.d(TAG, "createUserWithEmailAndPassword: failure");
			            Log.e(TAG, "createUserWithEmailAndPassword: ", e);

			            btCheckOut.setEnabled(true);

			            findViewById(R.id.entry_activity_subscription_waiting_assistant_layout).setVisibility(View.INVISIBLE);

			            // TODO

			            if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {

				            smoothScrollTo(etRegistrationNicknameLayout.getBottom());

				            etRegistrationEmailLayout.setError("This email is already in use");

			            } else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {

				            smoothScrollTo(etRegistrationNicknameLayout.getBottom());

				            etRegistrationEmailLayout.setError("This email is badly formatted");

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
		TextInputLayout etRegistrationNicknameLayout = findViewById(R.id.entry_activity_registration_nickname_layout);
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

	private void smoothScrollTo(float y) {

		Log.d(TAG, "smoothScrollTo:true");

		ScrollView svParent = findViewById(R.id.entry_activity_scroll_view);

		svParent.smoothScrollTo(0, (int) y);

	}

	@Override
	public void onBackPressed() {

		Log.d(TAG, "onBackPressed:true");
		super.onBackPressed();
		// TODO

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

		if (email.length() == 0) {

			setLoginLayoutErrorsNull();

			etEmailFieldLayout.setError("You forgot me");

			return;

		} else if (password.length() == 0) {

			setLoginLayoutErrorsNull();

			etPasswordFieldLayout.setError("You forgot me");

			return;

		} else {

			setLoginLayoutErrorsNull();

		}

		// Show loading assistant.
		findViewById(R.id.entry_activity_login_waiting_assistant).setVisibility(View.VISIBLE);
		findViewById(R.id.entry_activity_login_waiting_assistant_text_view).setVisibility(View.VISIBLE);

		mAuth.signInWithEmailAndPassword(email, password)
		     .addOnSuccessListener(command -> {

			     Log.d(TAG, "loginUser: success");

			     updateUI(command.getUser());

		     })
		     .addOnFailureListener(e -> {

			     Log.d(TAG, "loginUser: failure");
			     Log.e(TAG, "loginUser: ", e);

			     setLoginLayoutErrorsNull();

			     findViewById(R.id.entry_activity_login_waiting_assistant).setVisibility(View.GONE);
			     findViewById(R.id.entry_activity_login_waiting_assistant_text_view).setVisibility(View.GONE);

			     // TODO

			     if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {

				     etEmailFieldLayout.setError("Email or password incorrect");

			     } else if (e instanceof com.google.firebase.FirebaseNetworkException) {

				     etEmailFieldLayout.setError("Check your internet connection and try again");

			     }

		     });

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

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

		Log.d(TAG, "onConnectionFailed:true");

	}

	private void setRegistrationLayoutErrorsEnabled(boolean enabled) {

		Log.d(TAG, "setRegistrationLayoutErrorsEnabled:true");

		TextInputLayout etRegistrationFirstNameLayout = findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout etRegistrationFamilyNameLayout = findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout etRegistrationEmailLayout = findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout etRegistrationPasswordLayout = findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout etRegistrationDobLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout etRegistrationNicknameLayout = findViewById(R.id.entry_activity_registration_nickname_layout);

		etRegistrationFirstNameLayout.setErrorEnabled(enabled);
		etRegistrationFamilyNameLayout.setErrorEnabled(enabled);
		etRegistrationEmailLayout.setErrorEnabled(enabled);
		etRegistrationPasswordLayout.setErrorEnabled(enabled);
		etRegistrationDobLayout.setErrorEnabled(enabled);
		etRegistrationNicknameLayout.setErrorEnabled(enabled);

		Log.d(TAG, "setRegistrationLayoutErrorsEnabled:end");

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

	class BackgroundTransitionTransformer
			extends ViewPager2.OnPageChangeCallback {

		private static final String TAG = "BackgroundTransitionTransformer";
		int[] mArrayOfColors;
		private float mPositionOffset = 0;
		private int   mPosition;

		@SuppressLint ("LongLogTag")
		BackgroundTransitionTransformer(ViewPager2 viewPager2) {

			super();

			Log.d(TAG, "BackgroundTransitionTransformer:true");

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