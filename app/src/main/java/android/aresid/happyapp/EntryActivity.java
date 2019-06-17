package android.aresid.happyapp;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
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

import java.util.ArrayList;
import java.util.List;

public class EntryActivity
		extends AppCompatActivity
		implements GoogleApiClient.OnConnectionFailedListener,
		           NoLoginButtonTextWatcher.OnNoLoginButtonTextWatcherInteractionListener,
		           ViewPagerAdapter.OnViewPagerInteractionListener,
		           View.OnClickListener {

	private final static String             TAG                = "EntryActivity";
	private static final int                REQUEST_CODE_LOGIN = 13;
	private              DBHelper           mDBHelper;
	private              GoogleSignInClient mGoogleSignInClient;
	private              FirebaseAuth       mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);

		// Instantiate FirebaseAuth.
		mAuth = FirebaseAuth.getInstance();

		// Configure Google Sign In.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getResources().getString(R.string.default_web_client_id))
		                                                                                              .requestEmail()
		                                                                                              .build();

		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		// Access all views that are needed.
		TextInputEditText etLoginPasswordField = findViewById(R.id.entry_activity_login_password_field);
		TextInputEditText etLoginEmailField = findViewById(R.id.entry_activity_login_email_field);
		ScrollView sv = findViewById(R.id.entry_activity_scroll_view);
		Button btGoogleLogin = findViewById(R.id.entry_activity_login_google_button);
		TextInputEditText etRegistrationDateOfBirthField = findViewById(R.id.entry_activity_registration_date_of_birth_field);
		ViewPager2 vpSubscriptionsView = findViewById(R.id.entry_activity_subscription_view_pager);

		etLoginPasswordField.addTextChangedListener(new NoLoginButtonTextWatcher(this));
		etLoginEmailField.addTextChangedListener(new NoLoginButtonTextWatcher(this));
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

		BackgroundTransitionTransformer btt = new BackgroundTransitionTransformer(vpSubscriptionsView);
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

		String email = etRegistrationEmailField.getText()
		                                       .toString();
		String password = etRegistrationPasswordField.getText()
		                                             .toString();

		if (etRegistrationFirstNameField.length() == 0) {

			setRegistrationLayoutErrorsNull();

			etRegistrationFirstNameLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationFirstNameLayout.getTop());

			return;

		} else if (etRegistrationFirstNameField.getText()
		                                       .toString()
		                                       .startsWith(" ")) {

			setRegistrationLayoutErrorsNull();

			etRegistrationFirstNameLayout.setError("First Name cannot start with whitespace");

			smoothScrollTo(etRegistrationFirstNameLayout.getTop());

			return;

		} else if (etRegistrationFamilyNameField.length() == 0) {

			setRegistrationLayoutErrorsNull();

			etRegistrationFamilyNameLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationFirstNameLayout.getBottom());

			return;

		} else if (etRegistrationFamilyNameField.getText()
		                                        .toString()
		                                        .startsWith(" ")) {

			setRegistrationLayoutErrorsNull();

			etRegistrationFamilyNameLayout.setError("Family Name cannot start with whitespace");

			smoothScrollTo(etRegistrationFirstNameLayout.getBottom());

			return;

		} else if (etRegistrationNicknameField.length() == 0) {

			setRegistrationLayoutErrorsNull();

			etRegistrationNicknameLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());

			return;

		} else if (etRegistrationNicknameField.getText()
		                                      .toString()
		                                      .startsWith(" ")) {

			setRegistrationLayoutErrorsNull();

			etRegistrationNicknameLayout.setError("Nickname cannot start with whitespace");

			smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());

			return;

		} else if (email.length() == 0) {

			setRegistrationLayoutErrorsNull();

			etRegistrationEmailLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationNicknameLayout.getBottom());

			return;

		} else if (email.startsWith(" ")) {

			setRegistrationLayoutErrorsNull();

			etRegistrationEmailLayout.setError("Email cannot start with whitespace");

			smoothScrollTo(etRegistrationNicknameLayout.getBottom());

			return;

		} else if (password.length() == 0) {

			setRegistrationLayoutErrorsNull();

			etRegistrationPasswordLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return;

		} else if (etRegistrationPasswordField.getText()
		                                      .toString()
		                                      .startsWith(" ")) {

			setRegistrationLayoutErrorsNull();

			etRegistrationPasswordLayout.setError("Password cannot start with whitespace");

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return;

		} else if (password.length() < 6) {

			setRegistrationLayoutErrorsNull();

			etRegistrationPasswordLayout.setError("I need to be longer than 6 characters of your choice");

			smoothScrollTo(etRegistrationEmailLayout.getBottom());

			return;

		} else if (etRegistrationDobField.length() == 0) {

			setRegistrationLayoutErrorsNull();

			etRegistrationDobLayout.setError("You forgot me");

			smoothScrollTo(etRegistrationPasswordLayout.getBottom());

			return;

		} else if (!cbTermsConditionsPrivacyPolicy.isChecked()) {

			setRegistrationLayoutErrorsNull();

			cbTermsConditionsPrivacyPolicy.setError("Required field");

			smoothScrollTo(etRegistrationDobLayout.getBottom());

			return;

		} else {

			setRegistrationLayoutErrorsNull();

		}

		FirebaseAuth.getInstance()
		            .createUserWithEmailAndPassword(email, password)
		            .addOnSuccessListener(command -> {

			            Log.d(TAG, "createUserWithEmailAndPassword: success");
			            Log.d(TAG, "createUserWithEmailAndPassword: user = " + command.getUser());

			            updateUI(command.getUser());

		            })
		            .addOnFailureListener(e -> {

			            Log.d(TAG, "createUserWithEmailAndPassword: failure");
			            Log.e(TAG, "createUserWithEmailAndPassword: ", e);

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

	/**
	 * Method populates the Subscriptions table in the db with the server data.
	 */
	private void populateSubscriptionsTable() {

		Log.d(TAG, "populateSubscriptionsTable:true");
		// TODO

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
	public void loginWithUser(FirebaseUser user) {

		Log.d(TAG, "loginWithUser:true");

		updateUI(user);

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

		Log.d(TAG, "onConnectionFailed:true");

	}

	void updateUI(FirebaseUser user) {

		Log.d(TAG, "updateUI:true");

		ImageView waitingAssistant = findViewById(R.id.entry_activity_logging_in_waiting_assistant);

		if (user != null) {

			if (user.getProviderData()
			        .get(1)
			        .getProviderId()
			        .equals("google.com")) {

				// TODO

			}

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