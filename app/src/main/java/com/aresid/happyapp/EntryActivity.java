package com.aresid.happyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EntryActivity
		extends AppCompatActivity
		implements View.OnClickListener,
		           RetrieveInternetTime.OnInternetTimeInteractionListener,
		           ButtonlessLogin.OnButtonlessLoginInteractionListener,
		           PurchasesUpdatedListener,
		           SkuDetailsResponseListener,
		           BillingClientStateListener,
		           SubsPagerFinalAdapter.OnFinalAdapterInteractionListener,
		           AcknowledgePurchaseResponseListener {
	private static final String        TAG               = "EntryActivity";
	private              FirebaseAuth  mAuth;
	private              int           mBackPressCounter = 0;
	private              BillingClient mBillingClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		setTheme(R.style.Theme_HappyApp);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		handleAllNeededViewsOnCreate();
		instantiateFirebaseAuth();
		loadGifInto(findViewById(R.id.entry_activity_logging_in_waiting_assistant));
		establishBillingClientConnection();
	}
	
	private void handleAllNeededViewsOnCreate() {
		Log.d(TAG, "handleAllNeededViewsOnCreate: called");
		ScrollView scrollView = findViewById(R.id.entry_activity_scroll_view);
		TextInputEditText registrationDateOfBirthField =
				findViewById(R.id.entry_activity_registration_date_of_birth_field);
		TextInputEditText loginPasswordField = findViewById(R.id.entry_activity_login_password_field);
		ViewPager2 subscriptionsViewPager2 = findViewById(R.id.entry_activity_subscription_view_pager);
		scrollView.setSmoothScrollingEnabled(true);
		subscriptionsViewPager2.setAdapter(new SubsPagerInitAdapter(this));
		loginPasswordField.addTextChangedListener(new ButtonlessLogin(this));
		setUpDatePickerForRegistration(registrationDateOfBirthField);
	}
	
	void instantiateFirebaseAuth() {
		Log.d(TAG, "instantiateFirebaseAuth: called");
		mAuth = FirebaseAuth.getInstance();
		registerFirebaseAuthStateListener();
	}
	
	/**
	 * Loads drawable waiting assistant into @param view.
	 *
	 * @param gifHolders The ImageViews which will show the gif
	 */
	private void loadGifInto(ImageView... gifHolders) {
		Log.d(TAG, "loadGifInto: called");
		for (ImageView holder : gifHolders) {
			Glide.with(this)
			     .load(getResources().getDrawable(R.drawable.waiting_assistant_content))
			     .into(holder);
		}
	}
	
	private void establishBillingClientConnection() {
		Log.d(TAG, "establishBillingClientConnection: called");
		mBillingClient = BillingClient.newBuilder(this)
		                              .setListener(this)
		                              .enablePendingPurchases()
		                              .build();
		mBillingClient.startConnection(this /* onBillingSetupFinished or
		onBillingServiceDisconnected */);
	}
	
	private void setUpDatePickerForRegistration(TextInputEditText editText) {
		Log.d(TAG, "setUpDatePickerForRegistration: called");
		// ClickListener required to show the DatePicker when clicked.
		editText.setOnClickListener(this);
		// FocusListener required to show the DatePicker when focused.
		editText.setOnFocusChangeListener((v, hasFocus) -> {
			if (hasFocus) {
				new DatePickerFragment(this, (EditText) v).show(getSupportFragmentManager(), "date picker");
			}
		});
		// KeyListener required so the keyboard does not show up.
		editText.setKeyListener(null);
	}
	
	void registerFirebaseAuthStateListener() {
		Log.d(TAG, "registerFirebaseAuthStateListener: called");
		// Resets loading screen when user is logged out!
		mAuth.addAuthStateListener(auth -> {
			Log.d(TAG, "registerFirebaseAuthStateListener: auth state changed");
			if (auth.getCurrentUser() == null) {
				changeFromLoadingScreen();
			}
		});
	}
	
	@Override
	public void onStart() {
		Log.d(TAG, "onStart: called");
		super.onStart();
		// checks if user has signed in before.
		FirebaseUser user = mAuth.getCurrentUser();
		updateUI(user);
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "onStop: called");
		super.onStop();
		changeFromLoadingScreen();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy: called");
		// TODO
		super.onDestroy();
	}
	
	void updateUI(FirebaseUser user) {
		Log.d(TAG, "updateUI: called");
		if (user != null) {
			changeToLoadingScreen();
			Toast toast = Toast.makeText(this, getString(R.string.plainReloadingUserInformation),
			                             Toast.LENGTH_LONG);
			toast.show();
			user.reload()
			    .addOnSuccessListener(command -> {
				    Log.d(TAG, "updateUI: great success");
				    // cancels the toast after user has been reloaded.
				    toast.cancel();
				    if (user.isEmailVerified()) {
					    startMainActivity(user);
				    } else {
					    startConfirmEmailActivity(user);
				    }
			    })
			    .addOnFailureListener(e -> {
				    // if user info cannot be reloaded somehow.
				    Log.d(TAG, "updateUI: failure");
				    Log.e(TAG, "updateUI: ", e);
				    // TODO
				    if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {
					    // if user has been deleted in the meantime.
					    Toast.makeText(this, getString(R.string.errorUserNotFound), Toast.LENGTH_LONG)
					         .show();
					    // log non-existing user out.
					    mAuth.signOut();
					    // change to normal screen after logout.
					    changeFromLoadingScreen();
				    }
			    });
		} else {
			Log.d(TAG, "updateUI: user == null");
			// no user logged in.
			// TODO
			changeFromLoadingScreen();
		}
	}
	
	/**
	 * Updates Google's user account in the Firestore.
	 *
	 * @param user     Used to load the user.getUid() into the database.
	 * @param username The username to load into the database.
	 * @param dob      The Date of birth to load into the database.
	 */
	private void updateGoogleUser(FirebaseUser user, String username, String dob) {
		Log.d(TAG, "updateGoogleUser: called");
		HashMap<String, Object> info = new HashMap<>();
		info.put(FirestoreNames.COLUMN_USERNAME, username);
		info.put(FirestoreNames.COLUMN_DATE_OF_BIRTH, dob);
		FirebaseFirestore db = getFirestoreInstance();
		db.collection(FirestoreNames.COLLECTION_USERS)
		  .document(user.getUid())
		  .update(info)
		  .addOnSuccessListener(aVoid -> {
			  Log.d(TAG, "updateGoogleUser: success");
			  updateUI(user);
		  })
		  .addOnFailureListener(e -> {
			  Log.d(TAG, "updateGoogleUser: failure");
			  Log.e(TAG, "updateGoogleUser: ", e);
//			  SubsPagerFinalAdapter.setCheckoutProcessingLayoutVisibility(View
//			  .INVISIBLE);
		  });
	}
	
	/**
	 * Changes to loading screen.
	 */
	private void changeToLoadingScreen() {
		Log.d(TAG, "changeToLoadingScreen: called");
		findViewById(R.id.entry_activity_loading_screen).setVisibility(View.VISIBLE);
		findViewById(R.id.entry_activity_scroll_view).setVisibility(View.GONE);
	}
	
	/**
	 * Starts main activity.
	 *
	 * @param user Firebase user.
	 */
	void startMainActivity(FirebaseUser user) {
		Log.d(TAG, "startMainActivity: called");
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(getString(R.string.firebaseUserKey), user);
		startActivity(intent);
	}
	
	/**
	 * Starts new activity to tell the user to verify his email.
	 *
	 * @param user Firebase user.
	 */
	void startConfirmEmailActivity(FirebaseUser user) {
		Log.d(TAG, "startConfirmEmailActivity: called");
		Intent intent = new Intent(this, ConfirmEmailActivity.class);
		intent.putExtra(getString(R.string.firebaseUserKey), user);
		startActivity(intent);
	}
	
	/**
	 * Changes from loading screen back to normal.
	 */
	private void changeFromLoadingScreen() {
		Log.d(TAG, "changeFromLoadingScreen: called");
		findViewById(R.id.entry_activity_loading_screen).setVisibility(View.GONE);
		findViewById(R.id.entry_activity_scroll_view).setVisibility(View.VISIBLE);
	}
	
	/**
	 * Now this does some fancy stuff. Checks if all the data is correct and then
	 * proceeds to Google billing flow.
	 *
	 * @param subscription The subscription.
	 */
	public void startRegistrationFlow(Subscription subscription) {
		Log.d(TAG, "startRegistrationFlow: called");
		Log.d(TAG, "startRegistrationFlow: subscription = " + subscription.getTitle());
		// All these views are needed for the process
		TextInputEditText registrationEmailField =
				findViewById(R.id.entry_activity_registration_email_field);
		TextInputEditText registrationPasswordField =
				findViewById(R.id.entry_activity_registration_password_field);
		TextInputEditText registrationFirstNameField =
				findViewById(R.id.entry_activity_registration_first_name_field);
		TextInputEditText registrationFamilyNameField =
				findViewById(R.id.entry_activity_registration_family_name_field);
		TextInputEditText registrationDateOfBirthField =
				findViewById(R.id.entry_activity_registration_date_of_birth_field);
		TextInputEditText registrationUsernameField =
				findViewById(R.id.entry_activity_registration_username_field);
		TextInputLayout registrationEmailFieldLayout =
				findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout registrationPasswordFieldLayout =
				findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout registrationFirstNameFieldLayout =
				findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout registrationFamilyNameFieldLayout =
				findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout registrationDateOfBirthFieldLayout =
				findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout registrationUsernameFieldLayout =
				findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox termsAndConditionsCheckBox =
				findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);
		// Extracting the content from the fields
		String email = getStringFromField(registrationEmailField);
		String password = getStringFromField(registrationPasswordField);
		String firstName = getStringFromField(registrationFirstNameField);
		String username = getStringFromField(registrationUsernameField);
		String familyName = getStringFromField(registrationFamilyNameField);
		String dateOfBirth = getStringFromField(registrationDateOfBirthField);
		SkuDetails details = subscription.getSkuDetails();
		FirebaseUser user = mAuth.getCurrentUser();
		FirebaseFirestore db = getFirestoreInstance();
		if (user != null) {
			// User != null means first time google login
			if (googleUserInfoIsOk(username, dateOfBirth)) {
				// Provided information is ok
				disableViews(registrationUsernameFieldLayout, registrationDateOfBirthFieldLayout,
				             termsAndConditionsCheckBox);
				// Checks whether the username is available
				db.collection(getString(R.string.collectionUsers))
				  .whereEqualTo(getString(R.string.columnUsername), username)
				  .get()
				  .addOnSuccessListener(command -> {
					  Log.d(TAG, "startRegistrationFlow: great success");
					  if (command.isEmpty()) {
						  // Username is available
						  // TODO: Subscribe and when successful, create new user and
						  //  continue
						  launchBillingFlow(details); /* Continue with
						  onPurchasesUpdated() */
					  } else {
						  // Username is already taken
						  enableViews(registrationUsernameFieldLayout, registrationDateOfBirthFieldLayout,
						              termsAndConditionsCheckBox);
						  registrationUsernameFieldLayout.setError(getString(R.string.errorUsernameIsAlreadyTaken));
						  smoothScrollTo(registrationFamilyNameFieldLayout.getBottom());
					  }
				  })
				  .addOnFailureListener(e -> {
					  Log.d(TAG, "startRegistrationFlow: failure");
					  Log.e(TAG, "startRegistrationFlow: ", e);
					  enableViews(registrationUsernameFieldLayout, registrationDateOfBirthFieldLayout,
					              termsAndConditionsCheckBox);
					  registrationUsernameFieldLayout.setError(getString(R.string.errorUsernameIsAlreadyTaken));
					  smoothScrollTo(registrationFamilyNameFieldLayout.getBottom());
				  });
			}
		} else {
			// User is null means no google login and we can create a new firebase user
			if (newUserInfoIsOk(firstName, username, familyName, email, password, dateOfBirth)) {
				// Provided info is ok
//				disableViews(registrationDateOfBirthFieldLayout,
//				             registrationFirstNameFieldLayout,
//				             registrationFamilyNameFieldLayout,
//				             registrationUsernameFieldLayout,
//				             registrationEmailFieldLayout,
//				             registrationPasswordFieldLayout,
//				             termsAndConditionsCheckBox);
				// Checks whether username is available
				db.collection(getString(R.string.collectionUsers))
				  .whereEqualTo(getString(R.string.columnUsername), username)
				  .get()
				  .addOnSuccessListener(command -> {
					  Log.d(TAG, "startRegistrationFlow: great success");
					  if (command.isEmpty()) {
						  // Username is available
						  // TODO: Subscribe and when successful, create new user and
						  //  continue
						  launchBillingFlow(details); /* Continue with
						  onPurchasesUpdated() */
					  } else {
						  // Username is already taken
						  enableViews(registrationFirstNameFieldLayout, registrationFamilyNameFieldLayout,
						              registrationUsernameFieldLayout, registrationEmailFieldLayout,
						              registrationPasswordFieldLayout, registrationDateOfBirthFieldLayout,
						              termsAndConditionsCheckBox);
						  smoothScrollTo(registrationFamilyNameFieldLayout.getBottom());
						  registrationUsernameFieldLayout.setError(getString(R.string.errorUsernameIsAlreadyTaken));
					  }
				  })
				  .addOnFailureListener(e -> {
					  Log.d(TAG, "startRegistrationFlow: failure");
					  Log.e(TAG, "startRegistrationFlow: ", e);
					  enableViews(registrationFirstNameFieldLayout, registrationFamilyNameFieldLayout,
					              registrationUsernameFieldLayout, registrationEmailFieldLayout,
					              registrationPasswordFieldLayout, registrationDateOfBirthFieldLayout,
					              termsAndConditionsCheckBox);
					  smoothScrollTo(registrationFamilyNameFieldLayout.getBottom());
					  registrationUsernameFieldLayout.setError(getString(R.string.errorUsernameIsAlreadyTaken));
				  });
			}
		}
	}
	
	/**
	 * Returns fresh new firestore instance.
	 *
	 * @return Fresh new firestore instance.
	 */
	private FirebaseFirestore getFirestoreInstance() {
		Log.d(TAG, "getFirestoreInstance: called");
		return FirebaseFirestore.getInstance();
	}
	
	private String getStringFromField(EditText field) {
		Log.d(TAG, "getStringFromField: called");
		if (field.getText() != null) {
			Log.d(TAG, "getStringFromField: text = " + field.getText()
			                                                .toString());
			return field.getText()
			            .toString();
		} else {
			Log.d(TAG, "getStringFromField: text = ");
			return "";
		}
	}
	
	/**
	 * This method checks if the given input is correct to create a Google user.
	 *
	 * @param username The username.
	 * @param dob      The date of birth.
	 * @return True if data is fitting.
	 */
	private boolean googleUserInfoIsOk(String username, String dob) {
		Log.d(TAG, "googleUserInfoIsOk: called");
		setLayoutErrorsNull();
		TextInputLayout registrationDateOfBirthFieldLayout =
				findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout registrationUsernameFieldLayout =
				findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox termsAndConditionsCheckBox =
				findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);
		if (username.length() == 0) {
			registrationUsernameFieldLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			smoothScrollTo(registrationUsernameFieldLayout.getTop());
			return false;
		} else if (username.startsWith(" ")) {
			registrationUsernameFieldLayout.setError(getString(R.string.errorFieldCannotStartWithWhitespace,
			                                                   R.string.plainUsername));
			smoothScrollTo(registrationUsernameFieldLayout.getTop());
			return false;
		} else if (dob.length() == 0) {
			registrationDateOfBirthFieldLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			smoothScrollTo(registrationDateOfBirthFieldLayout.getTop());
			return false;
		} else if (!termsAndConditionsCheckBox.isChecked()) {
			termsAndConditionsCheckBox.setError(getString(R.string.plainPleaseConfirm));
			smoothScrollTo(registrationDateOfBirthFieldLayout.getBottom());
			return false;
		}
		return true;
	}
	
	private void disableViews(@NonNull View... views) {
		Log.d(TAG, "disableViews: called");
		for (View view : views) {
			view.setEnabled(false);
		}
	}
	
	private void launchBillingFlow(SkuDetails details) {
		Log.d(TAG, "launchBillingFlow: called");
		BillingFlowParams params = BillingFlowParams.newBuilder()
		                                            .setSkuDetails(details)
		                                            .build();
		mBillingClient.launchBillingFlow(this, params);
	}
	
	private void enableViews(@NonNull View... views) {
		Log.d(TAG, "enableViews: called");
		for (View view : views) {
			view.setEnabled(true);
		}
	}
	
	/**
	 * Moves the screen to the desired position y.
	 *
	 * @param y Desired position to be at. Mostly view.getTop();
	 */
	private void smoothScrollTo(float y) {
		Log.d(TAG, "smoothScrollTo: called");
		ScrollView scrollView = findViewById(R.id.entry_activity_scroll_view);
		scrollView.smoothScrollTo(0, (int) y);
	}
	
	/**
	 * This method checks if the given input is correct to create a new user.
	 *
	 * @param firstName   Users first name. Must be > 0 and cannot start with space.
	 * @param username    Users username. Must be > 0 and cannot start with space.
	 * @param familyName  Users family name. Must be > 0 and cannot start with space.
	 * @param email       Users email. Must be > 0 and cannot start with space.
	 * @param password    Users password. Must be > 6 and cannot start with space.
	 * @param dateOfBirth Users date of birth. Must be > 0.
	 * @return True if everything is fitting.
	 */
	private boolean newUserInfoIsOk(String firstName, String username, String familyName, String email,
	                                String password, String dateOfBirth) {
		Log.d(TAG, "newUserInfoIsOk: called");
		setLayoutErrorsNull();
		TextInputLayout registrationEmailFieldLayout =
				findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout registrationPasswordFieldLayout =
				findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout registrationFirstNameFieldLayout =
				findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout registrationFamilyNameFieldLayout =
				findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout registrationDateOfBirthFieldLayout =
				findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout registrationUsernameFieldLayout =
				findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox termsAndConditionsCheckBox =
				findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);
		String youForgotMe = getString(R.string.contractionEmptyCredentialsField);
		if (firstName.length() == 0) {
			setFieldLayoutError(registrationFirstNameFieldLayout,
			                    getString(R.string.contractionEmptyCredentialsField));
			return false;
		} else if (firstName.startsWith(" ")) {
			setFieldLayoutError(registrationFirstNameFieldLayout,
			                    getString(R.string.errorFieldCannotStartWithWhitespace,
			                              R.string.plainFirstName));
			return false;
		} else if (familyName.length() == 0) {
			setFieldLayoutError(registrationFamilyNameFieldLayout, youForgotMe);
			return false;
		} else if (familyName.startsWith(" ")) {
			setFieldLayoutError(registrationFamilyNameFieldLayout,
			                    getString(R.string.errorFieldCannotStartWithWhitespace,
			                              R.string.plainFamilyName));
			return false;
		} else if (username.length() == 0) {
			setFieldLayoutError(registrationUsernameFieldLayout, youForgotMe);
			return false;
		} else if (username.startsWith(" ")) {
			setFieldLayoutError(registrationUsernameFieldLayout,
			                    getString(R.string.errorFieldCannotStartWithWhitespace,
			                              R.string.plainUsername));
			return false;
		} else if (email.length() == 0) {
			setFieldLayoutError(registrationEmailFieldLayout, youForgotMe);
			return false;
		} else if (email.startsWith(" ")) {
			setFieldLayoutError(registrationEmailFieldLayout,
			                    getString(R.string.errorFieldCannotStartWithWhitespace,
			                              R.string.plainEmail));
			return false;
		} else if (password.length() == 0) {
			setFieldLayoutError(registrationPasswordFieldLayout, youForgotMe);
			return false;
		} else if (password.startsWith(" ")) {
			setFieldLayoutError(registrationPasswordFieldLayout,
			                    getString(R.string.errorFieldCannotStartWithWhitespace,
			                              R.string.plainPassword));
			return false;
		} else if (password.length() < 6) {
			setFieldLayoutError(registrationPasswordFieldLayout, getString(R.string.errorPasswordTooShort));
			return false;
		} else if (dateOfBirth.length() == 0) {
			setFieldLayoutError(registrationDateOfBirthFieldLayout, youForgotMe);
			return false;
		} else if (!termsAndConditionsCheckBox.isChecked()) {
			termsAndConditionsCheckBox.setError(getString(R.string.plainPleaseConfirm));
			smoothScrollTo(registrationDateOfBirthFieldLayout.getBottom());
			return false;
		}
		return true;
	}
	
	/**
	 * This method resets all the layout errors in the registration form.
	 */
	private void setLayoutErrorsNull() {
		Log.d(TAG, "setLayoutErrorsNull: called");
		TextInputLayout etRegistrationFirstNameLayout =
				findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout etRegistrationFamilyNameLayout =
				findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout etRegistrationEmailLayout =
				findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout etRegistrationPasswordLayout =
				findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout etRegistrationDobLayout =
				findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout etRegistrationNicknameLayout =
				findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox cbTermsConditionsPrivacyPolicy =
				findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);
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
	
	private void setFieldLayoutError(TextInputLayout errorLayout, String message) {
		Log.d(TAG, "setFieldLayoutError: called");
		errorLayout.setError(message);
		smoothScrollTo(errorLayout.getTop());
	}
	
	/**
	 * Starts email verification activity to tell the Google user to verify his email.
	 *
	 * @param user The corresponding user.
	 */
	private void startGoogleEmailVerificationActivity(FirebaseUser user) {
		Log.d(TAG, "startGoogleEmailVerificationActivity: called");
		Intent intent = new Intent(this, ConfirmEmailActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		Log.d(TAG, "onActivityResult: called");
		super.onActivityResult(requestCode, resultCode, data);
		int loginRequestCode = getResources().getInteger(R.integer.loginRequestCode);
		// Result returned from launching the Intent.
		if (requestCode == loginRequestCode) {
			changeToLoadingScreen();
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			task.addOnSuccessListener(gsa -> {
				Log.d(TAG, "onActivityResult: great success");
				if (gsa != null) { firebaseAuthWithGoogleAccount(gsa); }
			})
			    .addOnFailureListener(e -> {
				    Log.d(TAG, "onActivityResult: failure");
				    Log.e(TAG, "onActivityResult: ", e);
				    changeFromLoadingScreen();
				    Toast.makeText(this, getString(R.string.errorStandardMessageTryAgain), Toast.LENGTH_LONG)
				         .show();
			    });
		}
	}
	
	/**
	 * Changes EditTexts to Google registration input since Google account do not have a
	 * username or date of birth yet.
	 */
	private void changeToGoogleRegistration() {
		Log.d(TAG, "changeToGoogleRegistration: called");
		smoothScrollTo(findViewById(R.id.entry_activity_registration_seperator_view).getBottom());
		findViewById(R.id.entry_activity_registration_first_name_layout).setEnabled(false);
		findViewById(R.id.entry_activity_registration_family_name_layout).setEnabled(false);
		findViewById(R.id.entry_activity_registration_email_layout).setEnabled(false);
		findViewById(R.id.entry_activity_registration_password_layout).setEnabled(false);
	}
	
	/**
	 * Adds users subscription variant to his account info in the database.
	 *
	 * @param user    The corresponding user.
	 * @param variant The variant he subscribed.
	 */
	private void addUsersSubscriptionVariantToFirestore(FirebaseUser user, int variant) {
		Log.d(TAG, "addUsersSubscriptionVariantToFirestore: called");
		FirebaseFirestore db = getFirestoreInstance();
		db.collection(FirestoreNames.COLLECTION_USERS)
		  .document(user.getUid())
		  .update(FirestoreNames.COLUMN_SUBSCRIPTION_VARIANT, variant == 13 ? "gold" : "no gold")
		  .addOnFailureListener(e -> {
			  Log.e(TAG, "addUsersSubscriptionVariantToFirestore: ", e);
//			  SubsPagerFinalAdapter.setCheckoutProcessingLayoutVisibility(View
//			  .INVISIBLE);
		  });
	}
	
	private String getGoogleUserFirstName(String displayName) {
		Log.d(TAG, "getGoogleUserFirstName: called");
		Log.d(TAG, "getGoogleUserFirstName: display name = " + displayName);
		if (displayName == null) {
			return "Error 404";
		}
		String[] nameParts = displayName.split("\\s+");
		Log.d(TAG, "getGoogleUserFirstName: nameParts = " + nameParts.toString());
		return nameParts[0];
	}
	
	private String getGoogleUserFamilyName(String displayName) {
		Log.d(TAG, "getGoogleUserFamilyName: called");
		Log.d(TAG, "getGoogleUserFamilyName: display name = " + displayName);
		if (displayName == null) {
			return "Error 404";
		}
		String[] nameParts = displayName.split("\\s+");
		Log.d(TAG, "getGoogleUserFamilyName: nameParts = " + nameParts.toString());
		return nameParts[nameParts.length - 1];
	}
	
	/**
	 * Registers a new firebase user via Google sign in.
	 *
	 * @param account The Google account.
	 */
	private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
		Log.d(TAG, "firebaseAuthWithGoogleAccount: called");
		AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
		mAuth.signInWithCredential(authCredential)
		     .addOnSuccessListener(command -> {
			     Log.d(TAG, "firebaseAuthWithGoogleAccount: great success");
			     // Login success.
			     FirebaseUser user = command.getUser();
			     if (command.getAdditionalUserInfo() != null && user != null) {
				     if (command.getAdditionalUserInfo()
				                .isNewUser()) {
					     Log.d(TAG, "firebaseAuthWithGoogleAccount: user is new");
					     saveUserInFirestore(user, account.getGivenName(), account.getFamilyName(), null,
					                         account.getEmail(), null, user.getPhotoUrl()
					                                                                                                                    .toString());
				     } else {
					     updateUI(user);
				     }
			     } else {
				     // Something went wrong
				     Toast.makeText(this, getString(R.string.errorStandardMessageTryAgain),
				                    Toast.LENGTH_SHORT)
				          .show();
				     changeFromLoadingScreen();
			     }
		     })
		     .addOnFailureListener(e -> {
			     Log.e(TAG, "firebaseAuthWithGoogleAccount: ", e);
			     Toast.makeText(this, getString(R.string.errorStandardMessageTryAgain), Toast.LENGTH_SHORT)
			          .show();
			     changeFromLoadingScreen();
		     });
	}
	
	/**
	 * This method handles what happens on back pressed.
	 */
	@Override
	public void onBackPressed() {
		// I want to have the user click 2 times back when he wants to leave the app.
		Log.d(TAG, "onBackPressed: called");
		int time = 5130;
		Toast toast = Toast.makeText(this, getString(R.string.plainPressAgainToExit), Toast.LENGTH_LONG);
		if (mBackPressCounter == 0) {
			Log.d(TAG, "onBackPressed: counter == 0");
			toast.show();
			mBackPressCounter = 1;
			new Handler().postDelayed(() -> {
				Log.d(TAG, "run:true");
				mBackPressCounter = 0;
			}, time);
			return;
		} else {
			if (mBackPressCounter == 1) {
				Log.d(TAG, "onBackPressed: counter == 1");
				toast.cancel();
				finishAffinity();
			}
		}
		finishAffinity();
	}
	
	/**
	 * Handles all the on click methods.
	 *
	 * @param v The view that called this.
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick: called");
		if (v.getId() == R.id.entry_activity_registration_date_of_birth_field) {
			Log.d(TAG, "onClick: id = dob field");
			new DatePickerFragment(this, (EditText) v).show(getSupportFragmentManager(), "date picker");
		}
	}
	
	@Override
	public void onFinishedTyping() {
		Log.d(TAG, "onFinishedTyping: called");
		loginUser();
	}
	
	/**
	 * This method logs the user in.
	 */
	public void loginUser() {
		Log.d(TAG, "loginWithUser: called");
		EditText loginEmailField = findViewById(R.id.entry_activity_login_email_field);
		EditText loginPasswordField = findViewById(R.id.entry_activity_login_password_field);
		TextInputLayout loginEmailFieldLayout = findViewById(R.id.entry_activity_login_email_layout);
		TextInputLayout loginPasswordFieldLayout = findViewById(R.id.entry_activity_login_password_layout);
		ScrollView scrollView = findViewById(R.id.entry_activity_scroll_view);
		String email = getStringFromField(loginEmailField);
		String password = getStringFromField(loginPasswordField);
		setLayoutErrorsNull();
		if (email.length() == 0) {
			if (scrollView.getY() > loginEmailFieldLayout.getTop()) {
				smoothScrollTo(loginEmailFieldLayout.getTop());
			}
			loginEmailFieldLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			return;
		} else if (password.length() == 0) {
			if (scrollView.getY() > loginPasswordFieldLayout.getTop()) {
				smoothScrollTo(loginPasswordFieldLayout.getTop());
			}
			loginPasswordFieldLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			return;
		}
		// Show loading assistant.
		findViewById(R.id.entry_activity_logging_in_waiting_assistant).setVisibility(View.VISIBLE);
		// Disable the email and password field.
		findViewById(R.id.entry_activity_login_email_layout).setEnabled(false);
		findViewById(R.id.entry_activity_login_password_layout).setEnabled(false);
		mAuth.signInWithEmailAndPassword(email, password)
		     .addOnSuccessListener(command -> {
			     Log.d(TAG, "loginUser: success");
			     updateUI(command.getUser());
		     })
		     .addOnFailureListener(e -> {
			     Log.d(TAG, "loginUser: failure");
			     Log.e(TAG, "loginUser: ", e);
			     setLayoutErrorsNull();
			     findViewById(R.id.entry_activity_logging_in_waiting_assistant).setVisibility(View.GONE);
			     findViewById(R.id.entry_activity_login_email_layout).setEnabled(true);
			     findViewById(R.id.entry_activity_login_password_layout).setEnabled(true);
			     // TODO
			     if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
				     smoothScrollTo(loginEmailFieldLayout.getTop());
				     loginEmailFieldLayout.setError(getString(R.string.errorEmailOrPasswordIncorrect));
			     } else if (e instanceof com.google.firebase.FirebaseNetworkException) {
				     smoothScrollTo(loginEmailFieldLayout.getTop());
				     loginEmailFieldLayout.setError(getString(R.string.errorCheckInternetConnection));
			     }
		     });
	}
	
	/**
	 * Changes the view from Google registration restriction.
	 */
	private void changeFromGoogleRegistration() {
		Log.d(TAG, "changeFromGoogleRegistration: called");
		findViewById(R.id.entry_activity_registration_first_name_layout).setEnabled(true);
		findViewById(R.id.entry_activity_registration_family_name_layout).setEnabled(true);
		findViewById(R.id.entry_activity_registration_email_layout).setEnabled(true);
		findViewById(R.id.entry_activity_registration_password_layout).setEnabled(true);
	}
	
	/**
	 * This method evaluates an online time and saves it to the users account in the
	 * database.
	 *
	 * @param time Timestamp to save.
	 * @param uid  Corresponding user.
	 */
	public void addTimeToFirestoreEntry(Date time, String uid) {
		Log.d(TAG, "addTimeToFirestoreEntry: called");
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		HashMap<String, Object> info = new HashMap<>();
		info.put(getString(R.string.columnDateOfCreation), time.toString());
		db.collection(getString(R.string.collectionUsers))
		  .document(uid)
		  .update(info)
		  .addOnSuccessListener(aVoid -> Log.d(TAG, "addTimeToFirestoreEntry: great " + "success"))
		  .addOnFailureListener(e -> {
			  Log.d(TAG, "addTimeToFirestoreEntry: failure");
			  Log.e(TAG, "addTimeToFirestoreEntry: ", e);
		  });
	}
	
	@Override
	public void onPurchasesUpdated(BillingResult result, @Nullable List<Purchase> list) {
		Log.d(TAG, "onPurchasesUpdated: called");
		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
			for (Purchase purchase : list) {
				Log.d(TAG, "onPurchasesUpdated: got a purchase = " + purchase.toString());
				handlePurchase(purchase);
//				createNewUser();
			}
		} else if (result.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
			// Handle user cancellation
		} else {
			// Handle any other error
		}
	}
	
	private void handlePurchase(Purchase purchase) {
		Log.d(TAG, "handlePurchase: called");
		if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
			// Grant entitlement to user
			// Acknowledge the purchase if it hasn't already been acknowledged
			if (!purchase.isAcknowledged()) {
				AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
				                                                            .setPurchaseToken(purchase.getPurchaseToken())
				                                                            .build();
				mBillingClient.acknowledgePurchase(params, this /* onAcknowledgePurchaseResponse */);
			}
		} else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
			// Here you can confirm to the user that they've started the pending
			// purchase, and to complete it, they should follow instructions that
			// are given to them. You can also choose to remind the user in the
			// future to complete the purchase if you detect that it is still
			// pending.
		}
	}
	
	public void onLoginGoogleButtonClick(View view) {
		Log.d(TAG, "onLoginGoogleButtonClick: called");
		int loginRequestCode = getResources().getInteger(R.integer.loginRequestCode);
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
		                                                                                              .requestProfile()
		                                                                                              .requestEmail()
		                                                                                              .build();
		Intent googleLoginIntent = GoogleSignIn.getClient(this, gso)
		                                       .getSignInIntent();
		startActivityForResult(googleLoginIntent, loginRequestCode);
	}
	
	public void onConfirmButtonClick(View view) {
		Log.d(TAG, "onConfirmButtonClick: called");
	}
	
	@Override
	public void onSkuDetailsResponse(BillingResult result, List<SkuDetails> list) {
		Log.d(TAG, "onSkuDetailsResponse: called");
		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
			SubscriptionPool pool = new SubscriptionPool();
			for (SkuDetails details : list) {
				Log.d(TAG, "onSkuDetailsResponse: got a sku: " + details.getTitle());
				Subscription sub = new Subscription(this, details);
				pool.addSubscription(sub);
			}
			ViewPager2 viewPager2 = findViewById(R.id.entry_activity_subscription_view_pager);
			viewPager2.setAdapter(new SubsPagerFinalAdapter(this, pool.sort()));
		} else {
			Log.d(TAG, "onSkuDetailsResponse: " + result.getDebugMessage());
			Log.d(TAG, "onSkuDetailsResponse: " + result.getResponseCode());
			if (result.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE) {
				new SubsPagerInitAdapter(this).retry();
			}
		}
	}
	
	@Override
	public void onBillingSetupFinished(BillingResult result) {
		Log.d(TAG, "onBillingSetupFinished: called");
		// The billing client is ready. Query SKUs here.
		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
			List<String> skus = new ArrayList<>();
			skus.add(getString(R.string.subscriptionBronze));
			skus.add(getString(R.string.subscriptionSilver));
			skus.add(getString(R.string.subscriptionGold));
			skus.add(getString(R.string.subscriptionPlatinum));
			mBillingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder()
			                                                    .setSkusList(skus)
			                                                    .setType(BillingClient.SkuType.SUBS)
			                                                    .build(), this
					/*Continue with onSkuDetailsResponse*/);
		} else {
			Log.w(TAG, "onBillingSetupFinished: result = " + result.getDebugMessage());
			Log.d(TAG, "onBillingSetupFinished: result code = " + result.getResponseCode());
		}
	}
	
	@Override
	public void onBillingServiceDisconnected() {
		Log.d(TAG, "onBillingServiceDisconnected: called");
		// TODO: Implement own connection failed policy!
	}
	
	public void onRetryClick(View view) {
		Log.d(TAG, "onRetryClick: called");
		// TODO: subscription view pager retry policy
	}
	
	@Override
	public void onAcknowledgePurchaseResponse(BillingResult result) {
		Log.d(TAG, "onAcknowledgePurchaseResponse: called");
		createNewUser();
	}
	
	private void createNewUser() {
		Log.d(TAG, "createNewUser: called");
		// Change to loading screen
		changeToLoadingScreen();
		// The HashMap carrying the registration information
		HashMap<String, String> registrationInformation = getRegistrationFieldsContent();
		// Save the registration information in strings
		String firstName, familyName, username, email, password, dateOfBirth;
		firstName = registrationInformation.get(getString(R.string.firstNameKey));
		familyName = registrationInformation.get(getString(R.string.familyNameKey));
		username = registrationInformation.get(getString(R.string.usernameKey));
		email = registrationInformation.get(getString(R.string.emailKey));
		password = registrationInformation.get(getString(R.string.passwordKey));
		dateOfBirth = registrationInformation.get(getString(R.string.dateOfBirthKey));
		// Create user with email and password
		mAuth.createUserWithEmailAndPassword(email, password)
		     .addOnSuccessListener(result -> {
			     Log.d(TAG, "createNewUser: great success");
			     if (result.getUser() != null) {
				     saveUserInFirestore(result.getUser(), firstName, familyName, username, email,
				                         dateOfBirth, null);
			     }
		     })
		     .addOnFailureListener(e -> {
			     Log.d(TAG, "createNewUser: failure");
			     Log.e(TAG, "createNewUser: ", e);
		     });
	}
	
	private HashMap<String, String> getRegistrationFieldsContent() {
		Log.d(TAG, "getUserInformation: called");
		// The HashMap the information is saved to
		HashMap<String, String> registrationFieldsContent = new HashMap<>();
		// Access all the fields
		TextInputEditText firstNameField, familyNameField, usernameField, emailField, passwordField,
				dateOfBirthField;
		firstNameField = findViewById(R.id.entry_activity_registration_first_name_field);
		familyNameField = findViewById(R.id.entry_activity_registration_family_name_field);
		usernameField = findViewById(R.id.entry_activity_registration_username_field);
		emailField = findViewById(R.id.entry_activity_registration_email_field);
		passwordField = findViewById(R.id.entry_activity_registration_password_field);
		dateOfBirthField = findViewById(R.id.entry_activity_registration_date_of_birth_field);
		// Save all the fields content into strings
		String firstName, familyName, username, email, password, dateOfBirth;
		firstName = getStringFromField(firstNameField);
		familyName = getStringFromField(familyNameField);
		username = getStringFromField(usernameField);
		email = getStringFromField(emailField);
		password = getStringFromField(passwordField);
		dateOfBirth = getStringFromField(dateOfBirthField);
		// Save the content into HashMap
		registrationFieldsContent.put(getString(R.string.firstNameKey), firstName);
		registrationFieldsContent.put(getString(R.string.familyNameKey), familyName);
		registrationFieldsContent.put(getString(R.string.usernameKey), username);
		registrationFieldsContent.put(getString(R.string.emailKey), email);
		registrationFieldsContent.put(getString(R.string.passwordKey), password);
		registrationFieldsContent.put(getString(R.string.dateOfBirthKey), dateOfBirth);
		return registrationFieldsContent;
	}
	
	/**
	 * This method saves users info in firestore upon registration.
	 *
	 * @param user           The user to save the Uid.
	 * @param firstName      Users first name.
	 * @param familyName     Users family name.
	 * @param username       Users username.
	 * @param email          Users email.
	 * @param dateOfBirth    Users date of birth.
	 * @param profilePicture Users profile picture.
	 */
	private void saveUserInFirestore(FirebaseUser user, String firstName, String familyName, String username
			, String email, String dateOfBirth, String profilePicture) {
		Log.d(TAG, "saveUserInFirestore: called");
		FirebaseFirestore db = getFirestoreInstance();
		Toast toast = Toast.makeText(this, getString(R.string.errorStandardMessageStandBy),
		                             Toast.LENGTH_SHORT);
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(getString(R.string.columnFirstName), firstName);
		userInfo.put(getString(R.string.columnFamilyName), familyName);
		userInfo.put(getString(R.string.columnUsername), username);
		userInfo.put(getString(R.string.columnEmail), email);
		userInfo.put(getString(R.string.columnDateOfBirth), dateOfBirth);
		userInfo.put(getString(R.string.columnProfilePicture), profilePicture);
		db.collection(getString(R.string.collectionUsers))
		  .document(user.getUid())
		  .set(userInfo)
		  .addOnSuccessListener(command -> {
			  Log.d(TAG, "saveUserInFirestore: great success");
			  toast.cancel();
			  new RetrieveInternetTime(this, user.getUid()).execute(getString(R.string.googleTimeServerURL));
			  updateUI(user);
		  })
		  .addOnFailureListener(e -> {
			  Log.d(TAG, "saveUserInFirestore: failure");
			  Log.e(TAG, "saveUserInFirestore: ", e);
			  toast.show();
		  });
	}
}