package com.aresid.happyapp;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
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
		defineFirebaseAuth();
		registerFirebaseAuthStateListener();
		loadGifInto(findViewById(R.id.entry_activity_logging_in_waiting_assistant));
		establishBillingClientConnection();
	}
	
	private void handleAllNeededViewsOnCreate() {
		Log.d(TAG, "handleAllNeededViewsOnCreate: called");
		ScrollView scrollView = findViewById(R.id.entry_activity_scroll_view);
		TextInputEditText registrationDateOfBirthField =
				findViewById(R.id.entry_activity_registration_date_of_birth_field);
		ViewPager2 subscriptionsViewPager2 = findViewById(R.id.entry_activity_subscription_view_pager);
		scrollView.setSmoothScrollingEnabled(true);
		subscriptionsViewPager2.setAdapter(new SubsPagerInitAdapter(this));
		setUpDatePickerForRegistration(registrationDateOfBirthField);
	}
	
	void defineFirebaseAuth() {
		Log.d(TAG, "defineFirebaseAuth: called");
		mAuth = FirebaseAuth.getInstance();
	}
	
	/**
	 * Loads drawable waiting assistant into @param view.
	 *
	 * @param gifHolders The ImageViews which will show the gif
	 */
	private void loadGifInto(ImageView... gifHolders) {
		Log.d(TAG, "loadGifInto: called");
		for (ImageView holder : gifHolders) {
			Glide.with(this).load(getResources().getDrawable(R.drawable.loading_animation))
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
	
	/**
	 * Now this does some fancy stuff. Checks if all the data is correct and then
	 * proceeds to Google billing flow.
	 *
	 * @param subscription The subscription.
	 */
	public void startRegistrationFlow(Subscription subscription) {
		Log.d(TAG, "startRegistrationFlow: called");
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
				db.collection(getString(R.string.firestore_key_collection_users))
				  .whereEqualTo(getString(R.string.firestore_key_column_username), username)
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
						  registrationUsernameFieldLayout.setError(getString(R.string.error_username_already_is_taken));
						  smoothScrollTo(registrationFamilyNameFieldLayout.getBottom());
					  }
				  })
				  .addOnFailureListener(e -> {
					  Log.d(TAG, "startRegistrationFlow: failure");
					  Log.e(TAG, "startRegistrationFlow: ", e);
					  enableViews(registrationUsernameFieldLayout, registrationDateOfBirthFieldLayout,
					              termsAndConditionsCheckBox);
					  registrationUsernameFieldLayout.setError(getString(R.string.error_username_already_is_taken));
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
				db.collection(getString(R.string.firestore_key_collection_users))
				  .whereEqualTo(getString(R.string.firestore_key_column_username), username)
				  .get()
				  .addOnSuccessListener(command -> {
					  Log.d(TAG, "startRegistrationFlow: great success querying username");
					  if (command.isEmpty()) {
						  // Username is available
						  // TODO: Subscribe and when successful, create new user and
						  //  continue
						  mAuth.fetchSignInMethodsForEmail(email)
						       .addOnSuccessListener(result -> {
							       Log.d(TAG, "startRegistrationFlow:  great success querying email");
							       // Result sign in methods must be null or else the email is already in use
							       if (result.getSignInMethods()
							                 .size() == 0) {
								       launchBillingFlow(details); /* onPurchasesUpdated() */
							       } else {
								       // Email is already used
								       smoothScrollTo(registrationUsernameFieldLayout.getBottom());
								       registrationEmailFieldLayout.setError(getString(R.string.error_email_is_already_in_use));
							       }
						       })
						       .addOnFailureListener(e -> {
							       Log.d(TAG, "startRegistrationFlow: failure querying email");
							       Log.e(TAG, "startRegistrationFlow: ", e);
							       if (e instanceof FirebaseNetworkException) {
								       // Timeout
								       String connectionFailed =
										       getString(R.string.error_network_connection_failed);
								       showErrorSnackbar(connectionFailed);
							       }
						       });
					  } else {
						  // Username is already taken
						  enableViews(registrationFirstNameFieldLayout, registrationFamilyNameFieldLayout,
						              registrationUsernameFieldLayout, registrationEmailFieldLayout,
						              registrationPasswordFieldLayout, registrationDateOfBirthFieldLayout,
						              termsAndConditionsCheckBox);
						  smoothScrollTo(registrationFamilyNameFieldLayout.getBottom());
						  registrationUsernameFieldLayout.setError(getString(R.string.error_username_already_is_taken));
					  }
				  })
				  .addOnFailureListener(e -> {
					  Log.d(TAG, "startRegistrationFlow: failure querying username");
					  Log.e(TAG, "startRegistrationFlow: ", e);
					  enableViews(registrationFirstNameFieldLayout, registrationFamilyNameFieldLayout,
					              registrationUsernameFieldLayout, registrationEmailFieldLayout,
					              registrationPasswordFieldLayout, registrationDateOfBirthFieldLayout,
					              termsAndConditionsCheckBox);
					  smoothScrollTo(registrationFamilyNameFieldLayout.getBottom());
					  registrationUsernameFieldLayout.setError(getString(R.string.error_username_already_is_taken));
				  });
			}
		}
	}
	
	/**
	 * This method checks if the given input is correct to create a new user.
	 *
	 * @param firstName   Users first name_with_placeholder. Must be > 0 and cannot start with space.
	 * @param username    Users username. Must be > 0 and cannot start with space.
	 * @param familyName  Users family name_with_placeholder. Must be > 0 and cannot start with space.
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
		String youForgotMe = getString(R.string.error_you_forgot_me);
		if (firstName.length() == 0) {
			setFieldLayoutErrorAndScroll(registrationFirstNameFieldLayout,
			                             getString(R.string.error_you_forgot_me));
			return false;
		} else if (firstName.startsWith(" ")) {
			setFieldLayoutErrorAndScroll(registrationFirstNameFieldLayout,
			                             getString(R.string.error_field_starts_with_whitespace,
			                                       R.string.first_name));
			return false;
		} else if (familyName.length() == 0) {
			setFieldLayoutErrorAndScroll(registrationFamilyNameFieldLayout, youForgotMe);
			return false;
		} else if (familyName.startsWith(" ")) {
			setFieldLayoutErrorAndScroll(registrationFamilyNameFieldLayout,
			                             getString(R.string.error_field_starts_with_whitespace,
			                                       R.string.family_name));
			return false;
		} else if (username.length() == 0) {
			setFieldLayoutErrorAndScroll(registrationUsernameFieldLayout, youForgotMe);
			return false;
		} else if (username.startsWith(" ")) {
			setFieldLayoutErrorAndScroll(registrationUsernameFieldLayout,
			                             getString(R.string.error_field_starts_with_whitespace,
			                                       R.string.username));
			return false;
		} else if (email.length() == 0) {
			setFieldLayoutErrorAndScroll(registrationEmailFieldLayout, youForgotMe);
			return false;
		} else if (email.startsWith(" ")) {
			setFieldLayoutErrorAndScroll(registrationEmailFieldLayout, getString(R.string.error_field_starts_with_whitespace, R.string.email));
			return false;
		} else if (password.length() == 0) {
			setFieldLayoutErrorAndScroll(registrationPasswordFieldLayout, youForgotMe);
			return false;
		} else if (password.startsWith(" ")) {
			setFieldLayoutErrorAndScroll(registrationPasswordFieldLayout, getString(R.string.error_field_starts_with_whitespace, R.string.password));
			return false;
		} else if (password.length() < 6) {
			setFieldLayoutErrorAndScroll(registrationPasswordFieldLayout, getString(R.string.error_password_must_be_6_long));
			return false;
		} else if (dateOfBirth.length() == 0) {
			setFieldLayoutErrorAndScroll(registrationDateOfBirthFieldLayout, youForgotMe);
			return false;
		} else if (!termsAndConditionsCheckBox.isChecked()) {
			termsAndConditionsCheckBox.setError(getString(R.string.error_please_confirm));
			smoothScrollTo(registrationDateOfBirthFieldLayout.getBottom());
			return false;
		}
		return true;
	}
	
	/**
	 * Updates Google's user account in the Firestore.
	 *
	 * @param user     Used to load the user.getUid() into the database.
	 * @param username The username to load into the database.
	 * @param dob      The Date of birth to load into the database.
	 */
	//	private void updateGoogleUser(FirebaseUser user, String username, String dob) {
	//		Log.d(TAG, "updateGoogleUser: called");
	//		HashMap<String, Object> info = new HashMap<>();
	//		info.put(FirestoreNames.COLUMN_USERNAME, username);
	//		info.put(FirestoreNames.COLUMN_DATE_OF_BIRTH, dob);
	//		FirebaseFirestore db = getFirestoreInstance();
	//		db.collection(FirestoreNames.COLLECTION_USERS)
	//		  .document(user.getUid())
	//		  .update(info)
	//		  .addOnSuccessListener(aVoid -> {
	//			  Log.d(TAG, "updateGoogleUser: success");
	//			  updateUI(user);
	//		  })
	//		  .addOnFailureListener(e -> {
	//			  Log.d(TAG, "updateGoogleUser: failure");
	//			  Log.e(TAG, "updateGoogleUser: ", e);
	////			  SubsPagerFinalAdapter.setCheckoutProcessingLayoutVisibility(View
	////			  .INVISIBLE);
	//		  });
	//	}
	
	/**
	 * Changes to loading screen.
	 */
	private void changeToLoadingScreen() {
		Log.d(TAG, "changeToLoadingScreen: called");
		findViewById(R.id.entry_activity_loading_screen).setVisibility(View.VISIBLE);
		findViewById(R.id.entry_activity_scroll_view).setVisibility(View.GONE);
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
			registrationUsernameFieldLayout.setError(getString(R.string.error_you_forgot_me));
			smoothScrollTo(registrationUsernameFieldLayout.getTop());
			return false;
		} else if (username.startsWith(" ")) {
			registrationUsernameFieldLayout.setError(getString(R.string.error_field_starts_with_whitespace,
			                                                   R.string.username));
			smoothScrollTo(registrationUsernameFieldLayout.getTop());
			return false;
		} else if (dob.length() == 0) {
			registrationDateOfBirthFieldLayout.setError(getString(R.string.error_you_forgot_me));
			smoothScrollTo(registrationDateOfBirthFieldLayout.getTop());
			return false;
		} else if (!termsAndConditionsCheckBox.isChecked()) {
			termsAndConditionsCheckBox.setError(getString(R.string.error_please_confirm));
			smoothScrollTo(registrationDateOfBirthFieldLayout.getBottom());
			return false;
		}
		return true;
	}
	
	private void showErrorSnackbar(String message) {
		Log.d(TAG, "showErrorSnackbar: called");
		CoordinatorLayout snackbarView = findViewById(R.id.coordinator_layout);
		Snackbar.make(snackbarView, message, Snackbar.LENGTH_LONG)
		        .setBackgroundTint(ContextCompat.getColor(this, R.color.design_default_color_error))
		        .show();
	}
	
	/**
	 * Changes from loading screen back to normal.
	 */
	private void changeFromLoadingScreen() {
		Log.d(TAG, "changeFromLoadingScreen: called");
		findViewById(R.id.entry_activity_loading_screen).setVisibility(View.GONE);
		findViewById(R.id.entry_activity_scroll_view).setVisibility(View.VISIBLE);
	}
	
	private void launchBillingFlow(SkuDetails details) {
		Log.d(TAG, "launchBillingFlow: called");
		BillingFlowParams params = BillingFlowParams.newBuilder()
		                                            .setSkuDetails(details)
		                                            .build();
		mBillingClient.launchBillingFlow(this, params); /* onPurchasesUpdated() */
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
	 * This method resets all the layout errors
	 */
	private void setLayoutErrorsNull() {
		Log.d(TAG, "setLayoutErrorsNull: called");
		ConstraintLayout constraintLayout = findViewById(R.id.entry_activity_constraint_layout);
		int childCount = constraintLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = constraintLayout.getChildAt(i);
			if (child instanceof TextInputLayout) {
				((TextInputLayout) child).setError(null);
			}
			if (child instanceof CheckBox) {
				child.clearFocus();
			}
		}
	}
	
	private void disableViews(@NonNull View... views) {
		Log.d(TAG, "disableViews: called");
		for (View view : views) {
			view.setEnabled(false);
		}
	}
	
	/**
	 * Starts new activity to tell the user to verify his email.
	 *
	 * @param user Firebase user.
	 */
	//	void startConfirmEmailActivity(FirebaseUser user) {
	//		Log.d(TAG, "startConfirmEmailActivity: called");
	//		Intent intent = new Intent(this, ConfirmEmailActivity.class);
	//		intent.putExtra(getString(R.string.intent_key_firebase_user), user);
	//		startActivity(intent);
	//	}
	
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
	
	private void setFieldLayoutErrorAndScroll(TextInputLayout errorLayout, String message) {
		Log.d(TAG, "setFieldLayoutErrorAndScroll: called");
		errorLayout.setError(message);
		smoothScrollTo(errorLayout.getTop());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		Log.d(TAG, "onActivityResult: called");
		super.onActivityResult(requestCode, resultCode, data);
		//		int googleLoginRequestCode = getResources().getInteger(R.integer.request_code_google_login);
		//		// Normal login request code when user is guided from entry activity to main activity
		//		int loginRequestCode = getResources().getInteger(R.integer.request_code_login);
		//		// Result code when user logs out of main activity
		//		int logoutResultCode = getResources().getInteger(R.integer.result_code_logout);
		//		if (requestCode == googleLoginRequestCode) {
		//			// TODO: 15/04/2020 onActivityResult: subscribe
		//			// Result received from Google login activity
		//			changeToLoadingScreen();
		//			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
		//			task.addOnSuccessListener(gsa -> {
		//				Log.d(TAG, "onActivityResult: great success");
		//				if (gsa != null) { firebaseAuthWithGoogleAccount(gsa); }
		//			})
		//			    .addOnFailureListener(e -> {
		//				    Log.d(TAG, "onActivityResult: failure");
		//				    Log.e(TAG, "onActivityResult: ", e);
		//				    changeFromLoadingScreen();
		//				    Toast.makeText(this, getString(R.string.error_try_again), Toast.LENGTH_LONG)
		//				         .show();
		//			    });
		//		}
		//		if (requestCode == loginRequestCode && resultCode == logoutResultCode) {
		//			// Logout from another Activity
		//			// Reset all views here
		//			resetAllViews();
		////		}
	}
	
	private void resetAllViews() {
		Log.d(TAG, "resetAllViews: called");
		// Iterates through all children of the scroll view enables them
		ConstraintLayout constraintLayout = findViewById(R.id.entry_activity_constraint_layout);
		int childCount = constraintLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = constraintLayout.getChildAt(i);
			child.setEnabled(true);
			if (child instanceof TextInputLayout) {
				// If it is an TextInputLayout it resets the errors
				TextInputLayout textInputLayout = (TextInputLayout) child;
				textInputLayout.setError(null);
				// If the TextInputLayout has an TextInputEditText set its text to null
				if (textInputLayout.getEditText() != null) {
					textInputLayout.getEditText()
					               .setText(null);
				}
			} else if (child instanceof CheckBox) {
				((CheckBox) child).setChecked(false);
			}
		}
	}
	
	public void onLoginGoogleButtonClick(View view) {
		Log.d(TAG, "onLoginGoogleButtonClick: called");
		//		int googleLoginRequestCode = getResources().getInteger(R.integer.request_code_google_login);
		//		GoogleSignInOptions gso =
		//				new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
		//		                                                                                              .requestProfile()
		//		                                                                                              .requestEmail()
		//		                                                                                              .build();
		//		Intent googleLoginIntent = GoogleSignIn.getClient(this, gso)
		//		                                       .getSignInIntent();
		//		startActivityForResult(googleLoginIntent, googleLoginRequestCode);
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
			     Log.d(TAG, "firebaseAuthWithGoogleAccount: great success signing in");
			     // Login success.
			     FirebaseUser user = command.getUser();
			     if (command.getAdditionalUserInfo() != null && user != null) {
				     if (command.getAdditionalUserInfo()
				                .isNewUser()) {
					     Log.d(TAG, "firebaseAuthWithGoogleAccount: user is new");
					     // TODO:
					     saveUserInFirestore(user, null);
				     } else {
					     updateUI(user);
				     }
			     } else {
				     // Something went wrong
				     Toast.makeText(this, getString(R.string.error_try_again), Toast.LENGTH_SHORT)
				          .show();
				     changeFromLoadingScreen();
			     }
		     })
		     .addOnFailureListener(e -> {
			     Log.e(TAG, "firebaseAuthWithGoogleAccount: ", e);
			     Toast.makeText(this, getString(R.string.error_try_again), Toast.LENGTH_SHORT)
			          .show();
			     changeFromLoadingScreen();
		     });
	}
	
	/**
	 * This method handles what happens on back pressed.
	 */
	//	@Override
	//	public void onBackPressed() {
	//		// I want to have the user click 2 times back when he wants to leave the app.
	//		Log.d(TAG, "onBackPressed: called");
	//		int time = getResources().getInteger(R.integer.double_back_press_time);
	//		Toast toast = Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_LONG);
	//		if (mBackPressCounter == 0) {
	//			Log.d(TAG, "onBackPressed: counter == 0");
	//			toast.show();
	//			mBackPressCounter = 1;
	//			new Handler().postDelayed(() -> {
	//				Log.d(TAG, "run:true");
	//				mBackPressCounter = 0;
	//			}, time);
	//			return;
	//		} else {
	//			if (mBackPressCounter == 1) {
	//				Log.d(TAG, "onBackPressed: counter == 1");
	//				toast.cancel();
	//				finishAffinity();
	//			}
	//		}
	//		finishAffinity();
	//	}
	
	private void createNewUser(Purchase purchase) {
		
		Log.d(TAG, "createNewUser: called");
		// Change to loading screen
		changeToLoadingScreen();
		// Define the HashMap carrying the signup information
		HashMap<String, String> registrationInformation = getRegistrationFieldsContent();
		String email = registrationInformation.get(getString(R.string.hashmap_key_email));
		String password = registrationInformation.get(getString(R.string.hashmap_key_password));
		// Create user with email and password
		mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(result -> {
			Log.d(TAG, "createNewUser: great success creating user");
			if (result.getUser() != null) {
				saveUserInFirestore(result.getUser(), purchase);
			}
		}).addOnFailureListener(e -> {
			Log.d(TAG, "createNewUser: failure creating user");
			Log.e(TAG, "createNewUser: ", e);
			changeFromLoadingScreen();
			// TODO: 15/04/2020 createNewUser: error handling
			if (e instanceof com.google.firebase.FirebaseNetworkException) {
				String connectionFail = getString(R.string.error_network_connection_failed);
				showErrorSnackbar(connectionFail);
			}
		});
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
	
	//	@Override
	//	public void onFinishedTyping() {
	//		Log.d(TAG, "onFinishedTyping: called");
	//		loginUser();
	//	}
	
	/**
	 * This method evaluates an online time and saves it to the users account in the
	 * database.
	 *
	 * @param time Timestamp to save.
	 * @param uid  Corresponding user.
	 */
	public void addTimeToFirestoreEntry(Date time, String uid) {
		Log.d(TAG, "addTimeToFirestoreEntry: called");
		// The time cannot be null. If network connection fails, it returns the offline date
		// Define Firestore
		FirebaseFirestore db = getFirestoreInstance();
		// Define HashMap carrying the field and information for it
		HashMap<String, Object> info = new HashMap<>();
		info.put(getString(R.string.firestore_key_column_date_of_creation), time.toString());
		// Put the new information in the firestore
		db.collection(getString(R.string.firestore_key_collection_users))
		  .document(uid)
		  .update(info)
		  .addOnSuccessListener(aVoid -> Log.d(TAG, "addTimeToFirestoreEntry: great success"))
		  .addOnFailureListener(e -> {
			  Log.d(TAG, "addTimeToFirestoreEntry: failure");
			  Log.e(TAG, "addTimeToFirestoreEntry: ", e);
		  });
	}
	
	@Override
	public void onSkuDetailsResponse(BillingResult result, List<SkuDetails> list) {
		Log.d(TAG, "onSkuDetailsResponse: called");
		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
			// Define a new SubscriptionPool
			SubscriptionPool pool = new SubscriptionPool();
			// Iterate through the SkuDetails list
			for (SkuDetails details : list) {
				// Define a new Subscription based on the SkuDetails
				Subscription sub = new Subscription(this, details);
				// Add the new Subscription to the pool
				pool.addSubscription(sub);
			}
			ViewPager2 viewPager2 = findViewById(R.id.entry_activity_subscription_view_pager);
			// Set the new Adapter to the ViewPager2 as we have the Skus from the server
			viewPager2.setAdapter(new SubsPagerFinalAdapter(this, pool.sort()));
		} else {
			Log.d(TAG, "onSkuDetailsResponse: " + result.getDebugMessage());
			Log.d(TAG, "onSkuDetailsResponse: " + result.getResponseCode());
			if (result.getResponseCode() == (BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE |
			                                 BillingClient.BillingResponseCode.ERROR)) {
				new SubsPagerInitAdapter(this).retry();
			}
		}
	}
	
	@Override
	public void onPurchasesUpdated(BillingResult result, @Nullable List<Purchase> list) {
		Log.d(TAG, "onPurchasesUpdated: called");
		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
			for (Purchase purchase : list) {
				Log.d(TAG, "onPurchasesUpdated: got a purchase = " + purchase.toString());
				handlePurchase(purchase);
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
			createNewUser(purchase);
		} else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
			// Here you can confirm to the user that they've started the pending
			// purchase, and to complete it, they should follow instructions that
			// are given to them. You can also choose to remind the user in the
			// future to complete the purchase if you detect that it is still
			// pending.
		}
	}
	
	private HashMap<String, String> getRegistrationFieldsContent() {
		Log.d(TAG, "getRegistrationFieldsContent: called");
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
		registrationFieldsContent.put(getString(R.string.hashmap_key_first_name), firstName);
		registrationFieldsContent.put(getString(R.string.hashmap_key_family_name), familyName);
		registrationFieldsContent.put(getString(R.string.hashmap_key_username), username);
		registrationFieldsContent.put(getString(R.string.hashmap_key_email), email);
		registrationFieldsContent.put(getString(R.string.hashmap_key_password), password);
		registrationFieldsContent.put(getString(R.string.hashmap_key_date_of_birth), dateOfBirth);
		return registrationFieldsContent;
	}
	
	public void onConfirmButtonClick(View view) {
		Log.d(TAG, "onConfirmButtonClick: called");
	}
	
	/**
	 * This method saves users info in firestore upon signup.
	 *
	 * @param user     The user to save the Uid.
	 * @param purchase
	 */
	private void saveUserInFirestore(FirebaseUser user, Purchase purchase) {
		
		Log.d(TAG, "saveUserInFirestore: called");
		// Define the HashMap carrying the signup information
		HashMap<String, String> registrationInformation = getRegistrationFieldsContent();
		// Save the signup information in strings
		String firstName = registrationInformation.get(getString(R.string.hashmap_key_first_name));
		String familyName = registrationInformation.get(getString(R.string.hashmap_key_family_name));
		String username = registrationInformation.get(getString(R.string.hashmap_key_username));
		String email = registrationInformation.get(getString(R.string.hashmap_key_email));
		String dateOfBirth = registrationInformation.get(getString(R.string.hashmap_key_date_of_birth));
		String profilePicture = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
		FirebaseFirestore db = getFirestoreInstance();
		Toast toast = Toast.makeText(this, getString(R.string.error_stand_by), Toast.LENGTH_SHORT);
		// Define the HashMap carrying the user information from the fields
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(getString(R.string.firestore_key_column_first_name), firstName);
		userInfo.put(getString(R.string.firestore_key_column_family_name), familyName);
		userInfo.put(getString(R.string.firestore_key_column_username), username);
		userInfo.put(getString(R.string.firestore_key_column_email), email);
		userInfo.put(getString(R.string.firestore_key_column_date_of_birth), dateOfBirth);
		userInfo.put(getString(R.string.firestore_key_column_profile_picture), profilePicture);
		// Store the user information in the firestore
		db.collection(getString(R.string.firestore_key_collection_users)).document(user.getUid()).set(userInfo).addOnSuccessListener(command -> {
			Log.d(TAG, "saveUserInFirestore: great success");
			toast.cancel();
			// Retrieve the creation date from the internet
			// It calls the callback addTimeToFirestoreEntry
			new RetrieveInternetTime(this, user.getUid()).execute(getString(R.string.google_time_server_url));
			// Acknowledge the purchase if it hasn't already been acknowledged
			if (!purchase.isAcknowledged()) {
				AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
				mBillingClient.acknowledgePurchase(params, this /* onAcknowledgePurchaseResponse */);
			}
		}).addOnFailureListener(e -> {
			Log.d(TAG, "saveUserInFirestore: failure");
			Log.e(TAG, "saveUserInFirestore: ", e);
			toast.show();
		  });
	}
	
	void updateUI(FirebaseUser user) {
		Log.d(TAG, "updateUI: called");
		if (user != null) {
			changeToLoadingScreen();
			// Starts the MainActivity an passes the FirebaseUser through
			// It starts for result which is called when the user logs out in MainActivity
			//			startMainActivity(user);
		} else {
			Log.d(TAG, "updateUI: user == null");
			// no user logged in.
			// TODO
			changeFromLoadingScreen();
		}
	}
	
	/**
	 * Starts main activity.
	 *
	 * @param user Firebase user.
	 */
	//	void startMainActivity(FirebaseUser user) {
	//		Log.d(TAG, "startMainActivity: called");
	//		Intent intent = new Intent(this, MainActivity.class);
	//		intent.putExtra(getString(R.string.intent_key_firebase_user), user);
	//		int requestCode = getResources().getInteger(R.integer.request_code_login);
	//		startActivityForResult(intent, requestCode);
	//	}
	
	public void onRetryClick(View view) {
		Log.d(TAG, "onRetryClick: called");
		// TODO: subscription view pager retry policy
	}
	
	@Override
	public void onAcknowledgePurchaseResponse(BillingResult result) {
		Log.d(TAG, "onAcknowledgePurchaseResponse: called");
		// TODO: Verify on backend server
		updateUI(mAuth.getCurrentUser());
	}
	
	@Override
	public void onBillingSetupFinished(BillingResult result) {
		Log.d(TAG, "onBillingSetupFinished: called");
		// The billing client is ready. Query SKUs here.
		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
			// Queries the server and calls back
			mBillingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder()
			                                                    .setSkusList(getSkuList())
			                                                    .setType(BillingClient.SkuType.SUBS)
			                                                    .build(), this /* onSkuDetailsResponse */);
		} else {
			Log.w(TAG, "onBillingSetupFinished: result = " + result.getDebugMessage());
			Log.d(TAG, "onBillingSetupFinished: result code = " + result.getResponseCode());
		}
	}
	
	private List<String> getSkuList() {
		Log.d(TAG, "getSkuList: called");
		// Define the list carrying the subscription IDs
		List<String> skuList = new ArrayList<>();
		skuList.add(getString(R.string.subscription_bronze));
		skuList.add(getString(R.string.subscription_silver));
		skuList.add(getString(R.string.subscription_gold));
		skuList.add(getString(R.string.subscription_platinum));
		return skuList;
	}
	
	@Override
	public void onBillingServiceDisconnected() {
		Log.d(TAG, "onBillingServiceDisconnected: called");
		// TODO: Implement own connection failed policy
	}
	
	public void onLoginButtonClicked(View view) {
		Log.d(TAG, "onLoginButtonClicked: called");
		loginUser();
	}
	
	/**
	 * This method logs the user in.
	 */
	public void loginUser() {
		Log.d(TAG, "loginWithUser: called");
		// Define all needed Views
		EditText loginEmailField = findViewById(R.id.entry_activity_login_email_field);
		EditText loginPasswordField = findViewById(R.id.entry_activity_login_password_field);
		TextInputLayout loginEmailFieldLayout = findViewById(R.id.entry_activity_login_email_layout);
		TextInputLayout loginPasswordFieldLayout = findViewById(R.id.entry_activity_login_password_layout);
		ScrollView scrollView = findViewById(R.id.entry_activity_scroll_view);
		// Get the Strings from the fields
		String email = getStringFromField(loginEmailField);
		String password = getStringFromField(loginPasswordField);
		setLayoutErrorsNull();
		if (email.length() == 0) {
			// If the ScrollView is below the scroll point, set error and scroll, else just error
			if (scrollView.getY() > loginEmailFieldLayout.getTop()) {
				setFieldLayoutErrorAndScroll(loginEmailFieldLayout, getString(R.string.error_you_forgot_me));
			} else {
				loginEmailFieldLayout.setError(getString(R.string.error_you_forgot_me));
			}
			return;
		} else if (password.length() == 0) {
			// If the ScrollView is below the scroll point, set error and scroll, else just error
			if (scrollView.getY() > loginPasswordFieldLayout.getTop()) {
				setFieldLayoutErrorAndScroll(loginPasswordFieldLayout,
				                             getString(R.string.error_you_forgot_me));
			} else {
				loginPasswordFieldLayout.setError(getString(R.string.error_you_forgot_me));
			}
			return;
		}
		// Show loading assistant.
		findViewById(R.id.entry_activity_logging_in_waiting_assistant).setVisibility(View.VISIBLE);
		// Disable the email and password field.
		findViewById(R.id.entry_activity_login_email_layout).setEnabled(false);
		findViewById(R.id.entry_activity_login_password_layout).setEnabled(false);
		mAuth.signInWithEmailAndPassword(email, password)
		     .addOnSuccessListener(command -> {
			     Log.d(TAG, "loginUser: great success");
			     updateUI(command.getUser());
		     })
		     .addOnFailureListener(e -> {
			     Log.d(TAG, "loginUser: failure");
			     Log.e(TAG, "loginUser: ", e);
			     setLayoutErrorsNull();
			     // Remove the loading assistant
			     findViewById(R.id.entry_activity_logging_in_waiting_assistant).setVisibility(View.GONE);
			     // Enable the views again
			     findViewById(R.id.entry_activity_login_email_layout).setEnabled(true);
			     findViewById(R.id.entry_activity_login_password_layout).setEnabled(true);
			     // TODO: More error handling
			     if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
				     smoothScrollTo(loginEmailFieldLayout.getTop());
				     loginEmailFieldLayout.setError(getString(R.string.error_email_or_password_incorrect));
			     } else if (e instanceof com.google.firebase.FirebaseNetworkException) {
				     smoothScrollTo(loginEmailFieldLayout.getTop());
				     loginEmailFieldLayout.setError(getString(R.string.error_network_connection_failed));
			     }
		     });
	}
}