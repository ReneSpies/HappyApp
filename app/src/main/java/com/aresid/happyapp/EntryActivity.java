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

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
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
		           SubsPagerFinalAdapter.OnFinalAdapterInteractionListener {
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
		TextInputEditText registrationDateOfBirthField = findViewById(R.id.entry_activity_registration_date_of_birth_field);
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
	
	private void establishBillingClientConnection() {
		Log.d(TAG, "establishBillingClientConnection: called");
		mBillingClient = BillingClient.newBuilder(this)
		                              .setListener(this)
		                              .enablePendingPurchases()
		                              .build();
		mBillingClient.startConnection(this /* Continue with onBillingSetupFinished or onBillingServiceDisconnected */);
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
	protected void onDestroy() {
		Log.d(TAG, "onDestroy: called");
		// TODO
		super.onDestroy();
	}
	
	void updateUI(FirebaseUser user) {
		Log.d(TAG, "updateUI: called");
		if (user != null) {
			changeToLoadingScreen();
			Toast toast = Toast.makeText(this, getString(R.string.plainReloadingUserInformation), Toast.LENGTH_LONG);
			toast.show();
			user.reload()
			    .addOnSuccessListener(command -> {
				    Log.d(TAG, "updateUI: success");
				    // cancels the toast after user has been reloaded.
				    toast.cancel();
				    // checks if user already has a username and date of birth set.
				    Task<DocumentSnapshot> task = FirebaseFirestore.getInstance()
				                                                   .collection(FirestoreNames.COLLECTION_USERS)
				                                                   .document(user.getUid())
				                                                   .get();
				    task.addOnSuccessListener(document -> {
					    Log.d(TAG, "updateUI: success");
					    if (document.getString(FirestoreNames.COLUMN_USERNAME) != null && document.getString(FirestoreNames.COLUMN_DATE_OF_BIRTH) != null) {
						    if (user.isEmailVerified()) {
							    startMainActivity(user);
							    // changes from loading screen after user has been directed to the main activity.
							    changeFromLoadingScreen();
						    } else {
							    startEmailVerificationActivity(user);
							    // changes from loading screen after user has been directed to the email verification.
							    changeFromLoadingScreen();
						    }
					    } else {
						    changeFromLoadingScreen();
						    changeToGoogleRegistration();
					    }
				    })
				        .addOnFailureListener(e -> {
					        // if user is not found somehow.
					        Log.d(TAG, "updateUI: failure");
					        Log.e(TAG, "updateUI: ", e);
				        });
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
	 * Changes to loading screen.
	 */
	private void changeToLoadingScreen() {
		Log.d(TAG, "changeToLoadingScreen: called");
//		findViewById(R.id.entry_activity_logging_in_waiting_layout).setVisibility(View.VISIBLE);
		findViewById(R.id.entry_activity_constraint_layout).setVisibility(View.GONE);
	}
	
	/**
	 * Starts main activity.
	 *
	 * @param user Firebase user.
	 */
	void startMainActivity(FirebaseUser user) {
		Log.d(TAG, "startMainActivity: called");
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("firebase_user", user);
		startActivity(intent);
	}
	
	/**
	 * Starts new activity to tell the user to verify his email.
	 *
	 * @param user Firebase user.
	 */
	void startEmailVerificationActivity(FirebaseUser user) {
		Log.d(TAG, "startEmailVerificationActivity: called");
		Intent intent = new Intent(this, EmailVerificationActivity.class);
		intent.putExtra("firebase_user", user);
		startActivity(intent);
	}
	
	/**
	 * Changes EditTexts to Google registration input since Google account do not have a username or date of birth yet.
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
	 * Moves the screen to the desired position y.
	 *
	 * @param y Desired position to be at. Mostly view.getTop();
	 */
	private void smoothScrollTo(float y) {
		Log.d(TAG, "smoothScrollTo: called");
		ScrollView svParent = findViewById(R.id.entry_activity_scroll_view);
		svParent.smoothScrollTo(0, (int) y);
	}
	
	/**
	 * Changes from loading screen back to normal.
	 */
	private void changeFromLoadingScreen() {
		Log.d(TAG, "changeFromLoadingScreen: called");
//		findViewById(R.id.entry_activity_logging_in_waiting_layout).setVisibility(View.GONE);
		findViewById(R.id.entry_activity_constraint_layout).setVisibility(View.VISIBLE);
	}
	
	/**
	 * Loads drawable waiting assistant into @param view.
	 *
	 * @param gifHolders
	 */
	private void loadGifInto(ImageView... gifHolders) {
		Log.d(TAG, "loadGifInto: called");
		for (ImageView holder : gifHolders) {
			Glide.with(this)
			     .load(getResources().getDrawable(R.drawable.waiting_assistant_content))
			     .into(holder);
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
//			  SubsPagerFinalAdapter.setCheckoutProcessingLayoutVisibility(View.INVISIBLE);
		  });
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
//			  SubsPagerFinalAdapter.setCheckoutProcessingLayoutVisibility(View.INVISIBLE);
		  });
	}
	
	private void createUserWithEmailAndPassword(String email, String password) {
		Log.d(TAG, "createUserWithEmailAndPassword: called");
	}
	
	/**
	 * Now this does some fancy stuff. Checks if all the data is correct and then proceeds to Google billing flow.
	 *
	 * @param subscription The subscription.
	 */
	public void createUser(Subscription subscription) {
		Log.d(TAG, "createUser: called");
		Log.d(TAG, "createUser: subscription = " + subscription.getTitle());
		TextInputEditText etRegistrationEmailField = findViewById(R.id.entry_activity_registration_email_field);
		TextInputEditText etRegistrationPasswordField = findViewById(R.id.entry_activity_registration_password_field);
		TextInputEditText etRegistrationFirstNameField = findViewById(R.id.entry_activity_registration_first_name_field);
		TextInputEditText etRegistrationFamilyNameField = findViewById(R.id.entry_activity_registration_family_name_field);
		TextInputEditText etRegistrationDobField = findViewById(R.id.entry_activity_registration_date_of_birth_field);
		TextInputEditText etRegistrationUsernameField = findViewById(R.id.entry_activity_registration_username_field);
		TextInputLayout registrationEmailFieldLayout = findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout registrationPasswordFieldLayout = findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout registrationFirstNameFieldLayout = findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout registrationFamilyNameFieldLayout = findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout registrationDateOfBirthFieldLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout registrationUsernameFieldLayout = findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox termsAndConditionsCheckBox = findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);
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
		String dateOfBirth = etRegistrationDobField.getText()
		                                           .toString();
		FirebaseFirestore db = getFirestoreInstance();
		FirebaseUser user = mAuth.getCurrentUser();
		db.collection(FirestoreNames.COLLECTION_USERS)
		  .whereEqualTo(FirestoreNames.COLUMN_USERNAME, username)
		  .get()
		  .addOnSuccessListener(command -> {
			  Log.d(TAG, "createUser: success");
			  if (user != null) {
				  if (googleUserInfoIsOk(username, dateOfBirth)) {
					  disableViews(registrationUsernameFieldLayout, registrationDateOfBirthFieldLayout, termsAndConditionsCheckBox);
					  if (command.isEmpty()) {
						  // Username is not taken but user is not null
						  // TODO:
						  saveUserInFirestore(user, user.getDisplayName(), user.getDisplayName(), username, user.getEmail(), dateOfBirth, user.getPhotoUrl()
						                                                                                                                      .toString());
					  } else {
						  Log.d(TAG, "createUser: username is already taken");
						  // Username is already taken
						  registrationUsernameFieldLayout.setError(getString(R.string.errorUsernameIsAlreadyTaken));
						  enableViews(registrationUsernameFieldLayout, registrationDateOfBirthFieldLayout);
					  }
				  }
			  } else {
				  if (newUserInfoIsOk(firstName, username, familyName, email, password, dateOfBirth)) {
					  disableViews(registrationDateOfBirthFieldLayout, registrationFirstNameFieldLayout, registrationFamilyNameFieldLayout, registrationUsernameFieldLayout,
					               registrationEmailFieldLayout, registrationPasswordFieldLayout, termsAndConditionsCheckBox);
					  if (command.isEmpty()) {
						  // Username is not taken but user is null
						  FirebaseAuth.getInstance()
						              .createUserWithEmailAndPassword(email, password)
						              .addOnSuccessListener(result -> {
							              Log.d(TAG, "createUser: success");
							              result.getUser()
							                    .updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(username)
							                                                                         .build());
							              saveUserInFirestore(result.getUser(), firstName, familyName, username, email, dateOfBirth, null);
						              })
						              .addOnFailureListener(e -> {
							              Log.d(TAG, "createUser: failure");
							              Log.e(TAG, "createUser: ", e);
							              enableViews(registrationFirstNameFieldLayout, registrationFamilyNameFieldLayout, registrationUsernameFieldLayout, registrationEmailFieldLayout,
							                          registrationPasswordFieldLayout, registrationDateOfBirthFieldLayout);
							              // TODO
							              if (e instanceof FirebaseAuthUserCollisionException) {
								              smoothScrollTo(registrationUsernameFieldLayout.getBottom());
								              registrationEmailFieldLayout.setError(getString(R.string.errorThisEmailIsAlreadyInUse));
							              } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
								              smoothScrollTo(registrationUsernameFieldLayout.getBottom());
								              registrationEmailFieldLayout.setError(getString(R.string.errorThisEmailIsBadlyFormatted));
							              } else {
								              Toast.makeText(this, getString(R.string.errorStandardMessageTryAgain), Toast.LENGTH_SHORT)
								                   .show();
							              }
						              });
					  } else {
						  enableViews(registrationFirstNameFieldLayout, registrationFamilyNameFieldLayout, registrationUsernameFieldLayout, registrationEmailFieldLayout,
						              registrationPasswordFieldLayout, registrationDateOfBirthFieldLayout);
						  smoothScrollTo(registrationFamilyNameFieldLayout.getBottom());
						  registrationUsernameFieldLayout.setError(getString(R.string.errorUsernameIsAlreadyTaken));
					  }
				  }
			  }
		  })
		  .addOnFailureListener(e -> {
			  Log.d(TAG, "createUser: failure");
			  Log.e(TAG, "createUser: ", e);
			  enableViews(registrationFirstNameFieldLayout, registrationFamilyNameFieldLayout, registrationUsernameFieldLayout, registrationEmailFieldLayout, registrationPasswordFieldLayout,
			              registrationDateOfBirthFieldLayout);
		  });
	}
	
	/**
	 * This method checks if the given input is correct to create a Google user.
	 *
	 * @param username The username.
	 * @param dob      The date of birth.
	 * @return True if data is fitting.
	 */
	private boolean googleUserInfoIsOk(String username, String dob) {
		Log.d(TAG, "evaluateGoogleUserInfo: called");
		setLayoutErrorsNull();
		TextInputLayout etRegistrationDobLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout etRegistrationUsernameLayout = findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox cbTermsConditionsPrivacyPolicy = findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);
		if (username.length() == 0) {
			etRegistrationUsernameLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			smoothScrollTo(etRegistrationUsernameLayout.getTop());
			return false;
		} else if (username.startsWith(" ")) {
			etRegistrationUsernameLayout.setError(getString(R.string.errorFieldCannotStartWithWhitespace, R.string.plainUsername));
			smoothScrollTo(etRegistrationUsernameLayout.getTop());
			return false;
		} else if (dob.length() == 0) {
			etRegistrationDobLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			smoothScrollTo(etRegistrationDobLayout.getTop());
			return false;
		} else if (!cbTermsConditionsPrivacyPolicy.isChecked()) {
			cbTermsConditionsPrivacyPolicy.setError(getString(R.string.plainPleaseConfirm));
			smoothScrollTo(etRegistrationDobLayout.getBottom());
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
	
	private void enableViews(@NonNull View... views) {
		Log.d(TAG, "enableViews: called");
		for (View view : views) {
			view.setEnabled(true);
		}
	}
	
	/**
	 * This method checks if the given input is correct to create a new user.
	 *
	 * @param firstName  Users first name. Must be > 0 and cannot start with space.
	 * @param username   Users username. Must be > 0 and cannot start with space.
	 * @param familyName Users family name. Must be > 0 and cannot start with space.
	 * @param email      Users email. Must be > 0 and cannot start with space.
	 * @param password   Users password. Must be > 6 and cannot start with space.
	 * @param dob        Users date of birth. Must be > 0.
	 * @return True if everything is fitting.
	 */
	private boolean newUserInfoIsOk(String firstName, String username, String familyName, String email, String password, String dob) {
		Log.d(TAG, "evaluateNewUserInfo: called");
		setLayoutErrorsNull();
		TextInputLayout etRegistrationEmailLayout = findViewById(R.id.entry_activity_registration_email_layout);
		TextInputLayout etRegistrationPasswordLayout = findViewById(R.id.entry_activity_registration_password_layout);
		TextInputLayout etRegistrationFirstNameLayout = findViewById(R.id.entry_activity_registration_first_name_layout);
		TextInputLayout etRegistrationFamilyNameLayout = findViewById(R.id.entry_activity_registration_family_name_layout);
		TextInputLayout etRegistrationDobLayout = findViewById(R.id.entry_activity_registration_date_of_birth_layout);
		TextInputLayout etRegistrationUsernameLayout = findViewById(R.id.entry_activity_registration_username_layout);
		CheckBox cbTermsConditionsPrivacyPolicy = findViewById(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox);
		if (firstName.length() == 0) {
			etRegistrationFirstNameLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			smoothScrollTo(etRegistrationFirstNameLayout.getTop());
			return false;
		} else if (firstName.startsWith(" ")) {
			etRegistrationFirstNameLayout.setError(getString(R.string.errorFieldCannotStartWithWhitespace, R.string.plainFirstName));
			smoothScrollTo(etRegistrationFirstNameLayout.getTop());
			return false;
		} else if (familyName.length() == 0) {
			etRegistrationFamilyNameLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			smoothScrollTo(etRegistrationFirstNameLayout.getBottom());
			return false;
		} else if (familyName.startsWith(" ")) {
			etRegistrationFamilyNameLayout.setError(getString(R.string.errorFieldCannotStartWithWhitespace, R.string.plainFamilyName));
			smoothScrollTo(etRegistrationFirstNameLayout.getBottom());
			return false;
		} else if (username.length() == 0) {
			etRegistrationUsernameLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());
			return false;
		} else if (username.startsWith(" ")) {
			etRegistrationUsernameLayout.setError(getString(R.string.errorFieldCannotStartWithWhitespace, R.string.plainUsername));
			smoothScrollTo(etRegistrationFamilyNameLayout.getBottom());
			return false;
		} else if (email.length() == 0) {
			etRegistrationEmailLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			smoothScrollTo(etRegistrationUsernameLayout.getBottom());
			return false;
		} else if (email.startsWith(" ")) {
			etRegistrationEmailLayout.setError(getString(R.string.errorFieldCannotStartWithWhitespace, R.string.plainEmail));
			smoothScrollTo(etRegistrationUsernameLayout.getBottom());
			return false;
		} else if (password.length() == 0) {
			etRegistrationPasswordLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			smoothScrollTo(etRegistrationEmailLayout.getBottom());
			return false;
		} else if (password.startsWith(" ")) {
			etRegistrationPasswordLayout.setError(getString(R.string.errorFieldCannotStartWithWhitespace, R.string.plainPassword));
			smoothScrollTo(etRegistrationEmailLayout.getBottom());
			return false;
		} else if (password.length() < 6) {
			etRegistrationPasswordLayout.setError(getString(R.string.errorPasswordTooShort));
			smoothScrollTo(etRegistrationEmailLayout.getBottom());
			return false;
		} else if (dob.length() == 0) {
			etRegistrationDobLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			smoothScrollTo(etRegistrationPasswordLayout.getBottom());
			return false;
		} else if (!cbTermsConditionsPrivacyPolicy.isChecked()) {
			cbTermsConditionsPrivacyPolicy.setError(getString(R.string.plainPleaseConfirm));
			smoothScrollTo(etRegistrationDobLayout.getBottom());
			return false;
		}
		return true;
	}
	
	/**
	 * This method saves users info in firestore upon registration.
	 *
	 * @param user           The user to save the Uid.
	 * @param firstName      Users first name.
	 * @param familyName     Users family name.
	 * @param username       Users username.
	 * @param email          Users email.
	 * @param dob            Users date of birth.
	 * @param profilePicture Users profile picture.
	 */
	private void saveUserInFirestore(FirebaseUser user, String firstName, String familyName, String username, String email, String dob, String profilePicture) {
		Log.d(TAG, "saveUserInFirestore: called");
		Log.d(TAG, "saveUserInFirestore: pic = " + profilePicture);
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		Toast toast = Toast.makeText(this, getString(R.string.errorStandardMessageStandBy), Toast.LENGTH_SHORT);
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(FirestoreNames.COLUMN_FIRST_NAME, firstName);
		userInfo.put(FirestoreNames.COLUMN_FAMILY_NAME, familyName);
		userInfo.put(FirestoreNames.COLUMN_USERNAME, username);
		userInfo.put(FirestoreNames.COLUMN_EMAIL, email);
		userInfo.put(FirestoreNames.COLUMN_DATE_OF_BIRTH, dob);
		userInfo.put(FirestoreNames.COLUMN_PROFILE_PICTURE, profilePicture);
		db.collection(FirestoreNames.COLLECTION_USERS)
		  .document(user.getUid())
		  .set(userInfo)
		  .addOnSuccessListener(command -> {
			  Log.d(TAG, "saveUserInFirestore: success");
			  toast.cancel();
			  new RetrieveInternetTime(this, user.getUid()).execute("time.google.com");
			  updateUI(user);
		  })
		  .addOnFailureListener(e -> {
			  Log.d(TAG, "saveUserInFirestore: failure");
			  Log.e(TAG, "saveUserInFirestore: ", e);
			  toast.cancel();
		  });
	}
	
	/**
	 * This method resets all the layout errors in the registration form.
	 */
	private void setLayoutErrorsNull() {
		Log.d(TAG, "setLayoutErrorsNull: called");
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
	
	/**
	 * Starts email verification activity to tell the Google user to verify his email.
	 *
	 * @param user The corresponding user.
	 */
	private void startGoogleEmailVerificationActivity(FirebaseUser user) {
		Log.d(TAG, "startGoogleEmailVerificationActivity: called");
		Intent intent = new Intent(this, EmailVerificationActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
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
		EditText etEmailField = findViewById(R.id.entry_activity_login_email_field);
		EditText etPasswordField = findViewById(R.id.entry_activity_login_password_field);
		TextInputLayout etEmailFieldLayout = findViewById(R.id.entry_activity_login_email_layout);
		TextInputLayout etPasswordFieldLayout = findViewById(R.id.entry_activity_login_password_layout);
		ScrollView scrollView = findViewById(R.id.entry_activity_scroll_view);
		String email = etEmailField.getText()
		                           .toString();
		String password = etPasswordField.getText()
		                                 .toString();
		setLayoutErrorsNull();
		if (email.length() == 0) {
			if (scrollView.getY() > etEmailFieldLayout.getTop()) {
				smoothScrollTo(etEmailFieldLayout.getTop());
			}
			etEmailFieldLayout.setError(getString(R.string.contractionEmptyCredentialsField));
			return;
		} else if (password.length() == 0) {
			if (scrollView.getY() > etPasswordFieldLayout.getTop()) {
				smoothScrollTo(etPasswordFieldLayout.getTop());
			}
			etPasswordFieldLayout.setError(getString(R.string.contractionEmptyCredentialsField));
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
			     // TODO
			     if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
				     smoothScrollTo(etEmailFieldLayout.getTop());
				     etEmailFieldLayout.setError(getString(R.string.errorEmailOrPasswordIncorrect));
			     } else if (e instanceof com.google.firebase.FirebaseNetworkException) {
				     smoothScrollTo(etEmailFieldLayout.getTop());
				     etEmailFieldLayout.setError(getString(R.string.errorCheckInternetConnection));
			     }
		     });
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
				Log.d(TAG, "onActivityResult: success");
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
	 * Registers a new firebase user via Google sign in.
	 *
	 * @param account The Google account.
	 */
	private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
		Log.d(TAG, "firebaseAuthWithGoogleAccount: called");
		AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
		mAuth.signInWithCredential(authCredential)
		     .addOnSuccessListener(command -> {
			     Log.d(TAG, "firebaseAuthWithGoogleAccount: success");
			     // Login success.
			     FirebaseUser user = command.getUser();
			     if (command.getAdditionalUserInfo()
			                .isNewUser()) {
				     Log.d(TAG, "firebaseAuthWithGoogleAccount: user is new");
				     saveUserInFirestore(user, account.getGivenName(), account.getFamilyName(), null, account.getEmail(), null, user.getPhotoUrl()
				                                                                                                                    .toString());
			     } else {
				     updateUI(user);
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
	 * This method evaluates an online time and saves it to the users account in the database.
	 *
	 * @param time Timestamp to save.
	 * @param uid  Corresponding user.
	 */
	public void addTimeToFirestoreEntry(Date time, String uid) {
		Log.d(TAG, "addTimeToFirestoreEntry: called");
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		HashMap<String, Object> info = new HashMap<>();
		info.put(FirestoreNames.COLUMN_DATE_OF_CREATION, time.toString());
		db.collection(FirestoreNames.COLLECTION_USERS)
		  .document(uid)
		  .update(info)
		  .addOnSuccessListener(aVoid -> Log.d(TAG, "addTimeToFirestoreEntry: success"))
		  .addOnFailureListener(e -> {
			  Log.d(TAG, "addTimeToFirestoreEntry: failure");
			  Log.e(TAG, "addTimeToFirestoreEntry: ", e);
		  });
	}
	
	@Override
	public void onPurchasesUpdated(BillingResult result, @Nullable List<Purchase> list) {
		Log.d(TAG, "onPurchasesUpdated: called");
	}
	
	public void onLoginGoogleButtonClick(View view) {
		Log.d(TAG, "onLoginGoogleButtonClick: called");
		int loginRequestCode = getResources().getInteger(R.integer.loginRequestCode);
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("930375194703-ioc97apofqrtst7sf064h0tpi8qcgekv.apps.googleusercontent.com")
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
				Subscription sub = new Subscription(this);
				sub.setTitle(details.getTitle());
				sub.setId(details.getSku());
				sub.setPrice(details.getPrice());
				sub.setDescription(details.getDescription());
				sub.setSkuDetails(details);
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
			skus.add("happyapp.subscription.bronze");
			skus.add("happyapp.subscription.silver");
			skus.add("happyapp.subscription.gold");
			skus.add("happyapp.subscription.platinum");
			mBillingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder()
			                                                    .setSkusList(skus)
			                                                    .setType(BillingClient.SkuType.SUBS)
			                                                    .build(), this /*Continue with SkuDetailsResponseListener*/);
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
}