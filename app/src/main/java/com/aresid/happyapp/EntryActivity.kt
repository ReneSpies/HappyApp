package com.aresid.happyapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.android.billingclient.api.*
import com.aresid.happyapp.RetrieveInternetTime.OnInternetTimeInteractionListener
import com.aresid.happyapp.SubsPagerFinalAdapter.OnFinalAdapterInteractionListener
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

class EntryActivity: AppCompatActivity(), View.OnClickListener, OnInternetTimeInteractionListener, PurchasesUpdatedListener, SkuDetailsResponseListener, BillingClientStateListener, OnFinalAdapterInteractionListener, AcknowledgePurchaseResponseListener {
	private var mAuth: FirebaseAuth? = null
	private val mBackPressCounter = 0
	private var mBillingClient: BillingClient? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		Log.d(
			TAG,
			"onCreate: called"
		)
		setTheme(R.style.Theme_HappyApp)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_entry)
		handleAllNeededViewsOnCreate()
		defineFirebaseAuth()
		registerFirebaseAuthStateListener()
		loadGifInto(findViewById(R.id.entry_activity_logging_in_waiting_assistant))
		establishBillingClientConnection()
	}
	
	private fun handleAllNeededViewsOnCreate() {
		Log.d(
			TAG,
			"handleAllNeededViewsOnCreate: called"
		)
		val scrollView = findViewById<ScrollView>(R.id.entry_activity_scroll_view)
		val registrationDateOfBirthField = findViewById<TextInputEditText>(R.id.entry_activity_registration_date_of_birth_field)
		val subscriptionsViewPager2 = findViewById<ViewPager2>(R.id.entry_activity_subscription_view_pager)
		scrollView.isSmoothScrollingEnabled = true
		subscriptionsViewPager2.adapter = SubsPagerInitAdapter(this)
		setUpDatePickerForRegistration(registrationDateOfBirthField)
	}
	
	fun defineFirebaseAuth() {
		Log.d(
			TAG,
			"defineFirebaseAuth: called"
		)
		mAuth = FirebaseAuth.getInstance()
	}
	
	/**
	 * Loads drawable waiting assistant into @param view.
	 *
	 * @param gifHolders The ImageViews which will show the gif
	 */
	private fun loadGifInto(vararg gifHolders: ImageView) {
		Log.d(
			TAG,
			"loadGifInto: called"
		)
		for (holder in gifHolders) {
			Glide.with(this).load(resources.getDrawable(R.drawable.loading_animation)).into(holder)
		}
	}
	
	private fun establishBillingClientConnection() {
		Log.d(
			TAG,
			"establishBillingClientConnection: called"
		)
		mBillingClient = BillingClient.newBuilder(this).setListener(this).enablePendingPurchases().build()
		mBillingClient!!.startConnection(
			this /* onBillingSetupFinished or
		onBillingServiceDisconnected */
		)
	}
	
	private fun setUpDatePickerForRegistration(editText: TextInputEditText) {
		Log.d(
			TAG,
			"setUpDatePickerForRegistration: called"
		)
		// ClickListener required to show the DatePicker when clicked.
		editText.setOnClickListener(this)
		// FocusListener required to show the DatePicker when focused.
		editText.onFocusChangeListener = View.OnFocusChangeListener { v: View, hasFocus: Boolean ->
			if (hasFocus) {
				DatePickerFragment(
					this,
					v as EditText
				).show(
					supportFragmentManager,
					"date picker"
				)
			}
		}
		// KeyListener required so the keyboard does not show up.
		editText.keyListener = null
	}
	
	fun registerFirebaseAuthStateListener() {
		Log.d(
			TAG,
			"registerFirebaseAuthStateListener: called"
		)
		// Resets loading screen when user is logged out!
		mAuth!!.addAuthStateListener { auth: FirebaseAuth ->
			Log.d(
				TAG,
				"registerFirebaseAuthStateListener: auth state changed"
			)
			if (auth.currentUser == null) {
				changeFromLoadingScreen()
			}
		}
	}
	
	public override fun onStart() {
		Log.d(
			TAG,
			"onStart: called"
		)
		super.onStart()
		// checks if user has signed in before.
		val user = mAuth!!.currentUser
		updateUI(user)
	}
	
	override fun onStop() {
		Log.d(
			TAG,
			"onStop: called"
		)
		super.onStop()
		changeFromLoadingScreen()
	}
	
	override fun onDestroy() {
		Log.d(
			TAG,
			"onDestroy: called"
		)
		super.onDestroy()
	}
	
	/**
	 * Now this does some fancy stuff. Checks if all the data is correct and then
	 * proceeds to Google billing flow.
	 *
	 * @param subscription The subscription.
	 */
	override fun startRegistrationFlow(subscription: Subscription?) {
		Timber.d("startRegistrationFlow: called")
		
		// All these views are needed for the process
		val registrationEmailField = findViewById<TextInputEditText>(R.id.entry_activity_registration_email_field)
		val registrationPasswordField = findViewById<TextInputEditText>(R.id.entry_activity_registration_password_field)
		val registrationFirstNameField = findViewById<TextInputEditText>(R.id.entry_activity_registration_first_name_field)
		val registrationFamilyNameField = findViewById<TextInputEditText>(R.id.entry_activity_registration_family_name_field)
		val registrationDateOfBirthField = findViewById<TextInputEditText>(R.id.entry_activity_registration_date_of_birth_field)
		val registrationUsernameField = findViewById<TextInputEditText>(R.id.entry_activity_registration_username_field)
		val registrationEmailFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_email_layout)
		val registrationPasswordFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_password_layout)
		val registrationFirstNameFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_first_name_layout)
		val registrationFamilyNameFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_family_name_layout)
		val registrationDateOfBirthFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_date_of_birth_layout)
		val registrationUsernameFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_username_layout)
		val termsAndConditionsCheckBox = findViewById<CheckBox>(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox)
		// Extracting the content from the fields
		val email = getStringFromField(registrationEmailField)
		val password = getStringFromField(registrationPasswordField)
		val firstName = getStringFromField(registrationFirstNameField)
		val username = getStringFromField(registrationUsernameField)
		val familyName = getStringFromField(registrationFamilyNameField)
		val dateOfBirth = getStringFromField(registrationDateOfBirthField)
		val details = subscription?.skuDetails
		val user = mAuth!!.currentUser
		val db = firestoreInstance
		if (user != null) {
			// User != null means first time google login
			if (googleUserInfoIsOk(
					username,
					dateOfBirth
				)) {
				// Provided information is ok
				disableViews(
					registrationUsernameFieldLayout,
					registrationDateOfBirthFieldLayout,
					termsAndConditionsCheckBox
				)
				// Checks whether the username is available
				db.collection(getString(R.string.firestore_key_collection_users)).whereEqualTo(
					getString(R.string.firestore_key_column_username),
					username
				).get().addOnSuccessListener { command: QuerySnapshot ->
					Log.d(
						TAG,
						"startRegistrationFlow: great success"
					)
					if (command.isEmpty) {
						// Username is available
						launchBillingFlow(details) /* Continue with
						  onPurchasesUpdated() */
					}
					else {
						// Username is already taken
						enableViews(
							registrationUsernameFieldLayout,
							registrationDateOfBirthFieldLayout,
							termsAndConditionsCheckBox
						)
						registrationUsernameFieldLayout.error = getString(R.string.error_username_is_already_taken)
						smoothScrollTo(registrationFamilyNameFieldLayout.bottom.toFloat())
					}
				}.addOnFailureListener { e: Exception? ->
					Log.d(
						TAG,
						"startRegistrationFlow: failure"
					)
					Log.e(
						TAG,
						"startRegistrationFlow: ",
						e
					)
					enableViews(
						registrationUsernameFieldLayout,
						registrationDateOfBirthFieldLayout,
						termsAndConditionsCheckBox
					)
					registrationUsernameFieldLayout.error = getString(R.string.error_username_is_already_taken)
					smoothScrollTo(registrationFamilyNameFieldLayout.bottom.toFloat())
				}
			}
		}
		else {
			// User is null means no google login and we can create a new firebase user
			if (newUserInfoIsOk(
					firstName,
					username,
					familyName,
					email,
					password,
					dateOfBirth
				)) {
				// Provided info is ok
				//				disableViews(registrationDateOfBirthFieldLayout,
				//				             registrationFirstNameFieldLayout,
				//				             registrationFamilyNameFieldLayout,
				//				             registrationUsernameFieldLayout,
				//				             registrationEmailFieldLayout,
				//				             registrationPasswordFieldLayout,
				//				             termsAndConditionsCheckBox);
				// Checks whether username is available
				db.collection(getString(R.string.firestore_key_collection_users)).whereEqualTo(
					getString(R.string.firestore_key_column_username),
					username
				).get().addOnSuccessListener { command: QuerySnapshot ->
					Log.d(
						TAG,
						"startRegistrationFlow: great success querying username"
					)
					if (command.isEmpty) {
						// Username is available
						mAuth!!.fetchSignInMethodsForEmail(email).addOnSuccessListener { result: SignInMethodQueryResult ->
							Log.d(
								TAG,
								"startRegistrationFlow:  great success querying email"
							)
							// Result sign in methods must be null or else the email is already in use
							if (result.signInMethods!!.size == 0) {
								launchBillingFlow(details) /* onPurchasesUpdated() */
							}
							else {
								// Email is already used
								smoothScrollTo(registrationUsernameFieldLayout.bottom.toFloat())
								registrationEmailFieldLayout.error = getString(R.string.error_email_is_already_in_use)
							}
						}.addOnFailureListener { e: Exception? ->
							Log.d(
								TAG,
								"startRegistrationFlow: failure querying email"
							)
							Log.e(
								TAG,
								"startRegistrationFlow: ",
								e
							)
							if (e is FirebaseNetworkException) {
								// Timeout
								val connectionFailed = getString(R.string.error_network_connection_failed)
								showErrorSnackbar(connectionFailed)
							}
						}
					}
					else {
						// Username is already taken
						enableViews(
							registrationFirstNameFieldLayout,
							registrationFamilyNameFieldLayout,
							registrationUsernameFieldLayout,
							registrationEmailFieldLayout,
							registrationPasswordFieldLayout,
							registrationDateOfBirthFieldLayout,
							termsAndConditionsCheckBox
						)
						smoothScrollTo(registrationFamilyNameFieldLayout.bottom.toFloat())
						registrationUsernameFieldLayout.error = getString(R.string.error_username_is_already_taken)
					}
				}.addOnFailureListener { e: Exception? ->
					Log.d(
						TAG,
						"startRegistrationFlow: failure querying username"
					)
					Log.e(
						TAG,
						"startRegistrationFlow: ",
						e
					)
					enableViews(
						registrationFirstNameFieldLayout,
						registrationFamilyNameFieldLayout,
						registrationUsernameFieldLayout,
						registrationEmailFieldLayout,
						registrationPasswordFieldLayout,
						registrationDateOfBirthFieldLayout,
						termsAndConditionsCheckBox
					)
					smoothScrollTo(registrationFamilyNameFieldLayout.bottom.toFloat())
					registrationUsernameFieldLayout.error = getString(R.string.error_username_is_already_taken)
				}
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
	private fun newUserInfoIsOk(
		firstName: String,
		username: String,
		familyName: String,
		email: String,
		password: String,
		dateOfBirth: String
	): Boolean {
		Log.d(
			TAG,
			"newUserInfoIsOk: called"
		)
		setLayoutErrorsNull()
		val registrationEmailFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_email_layout)
		val registrationPasswordFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_password_layout)
		val registrationFirstNameFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_first_name_layout)
		val registrationFamilyNameFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_family_name_layout)
		val registrationDateOfBirthFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_date_of_birth_layout)
		val registrationUsernameFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_username_layout)
		val termsAndConditionsCheckBox = findViewById<CheckBox>(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox)
		val youForgotMe = getString(R.string.error_you_forgot_me)
		if (firstName.length == 0) {
			setFieldLayoutErrorAndScroll(
				registrationFirstNameFieldLayout,
				getString(R.string.error_you_forgot_me)
			)
			return false
		}
		else if (firstName.startsWith(" ")) {
			setFieldLayoutErrorAndScroll(
				registrationFirstNameFieldLayout,
				getString(
					R.string.error_field_starts_with_whitespace,
					R.string.first_name
				)
			)
			return false
		}
		else if (familyName.length == 0) {
			setFieldLayoutErrorAndScroll(
				registrationFamilyNameFieldLayout,
				youForgotMe
			)
			return false
		}
		else if (familyName.startsWith(" ")) {
			setFieldLayoutErrorAndScroll(
				registrationFamilyNameFieldLayout,
				getString(
					R.string.error_field_starts_with_whitespace,
					R.string.family_name
				)
			)
			return false
		}
		else if (username.length == 0) {
			setFieldLayoutErrorAndScroll(
				registrationUsernameFieldLayout,
				youForgotMe
			)
			return false
		}
		else if (username.startsWith(" ")) {
			setFieldLayoutErrorAndScroll(
				registrationUsernameFieldLayout,
				getString(
					R.string.error_field_starts_with_whitespace,
					R.string.username
				)
			)
			return false
		}
		else if (email.length == 0) {
			setFieldLayoutErrorAndScroll(
				registrationEmailFieldLayout,
				youForgotMe
			)
			return false
		}
		else if (email.startsWith(" ")) {
			setFieldLayoutErrorAndScroll(
				registrationEmailFieldLayout,
				getString(
					R.string.error_field_starts_with_whitespace,
					R.string.email
				)
			)
			return false
		}
		else if (password.length == 0) {
			setFieldLayoutErrorAndScroll(
				registrationPasswordFieldLayout,
				youForgotMe
			)
			return false
		}
		else if (password.startsWith(" ")) {
			setFieldLayoutErrorAndScroll(
				registrationPasswordFieldLayout,
				getString(
					R.string.error_field_starts_with_whitespace,
					R.string.password
				)
			)
			return false
		}
		else if (password.length < 6) {
			setFieldLayoutErrorAndScroll(
				registrationPasswordFieldLayout,
				getString(R.string.error_password_must_be_6_long)
			)
			return false
		}
		else if (dateOfBirth.length == 0) {
			setFieldLayoutErrorAndScroll(
				registrationDateOfBirthFieldLayout,
				youForgotMe
			)
			return false
		}
		else if (!termsAndConditionsCheckBox.isChecked) {
			termsAndConditionsCheckBox.error = getString(R.string.error_please_confirm)
			smoothScrollTo(registrationDateOfBirthFieldLayout.bottom.toFloat())
			return false
		}
		return true
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
	private fun changeToLoadingScreen() {
		Log.d(
			TAG,
			"changeToLoadingScreen: called"
		)
		findViewById<View>(R.id.entry_activity_loading_screen).visibility = View.VISIBLE
		findViewById<View>(R.id.entry_activity_scroll_view).visibility = View.GONE
	}
	
	/**
	 * This method checks if the given input is correct to create a Google user.
	 *
	 * @param username The username.
	 * @param dob      The date of birth.
	 * @return True if data is fitting.
	 */
	private fun googleUserInfoIsOk(
		username: String,
		dob: String
	): Boolean {
		Log.d(
			TAG,
			"googleUserInfoIsOk: called"
		)
		setLayoutErrorsNull()
		val registrationDateOfBirthFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_date_of_birth_layout)
		val registrationUsernameFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_registration_username_layout)
		val termsAndConditionsCheckBox = findViewById<CheckBox>(R.id.entry_activity_registration_confirm_tc_privacy_policy_checkbox)
		if (username.length == 0) {
			registrationUsernameFieldLayout.error = getString(R.string.error_you_forgot_me)
			smoothScrollTo(registrationUsernameFieldLayout.top.toFloat())
			return false
		}
		else if (username.startsWith(" ")) {
			registrationUsernameFieldLayout.error = getString(
				R.string.error_field_starts_with_whitespace,
				R.string.username
			)
			smoothScrollTo(registrationUsernameFieldLayout.top.toFloat())
			return false
		}
		else if (dob.length == 0) {
			registrationDateOfBirthFieldLayout.error = getString(R.string.error_you_forgot_me)
			smoothScrollTo(registrationDateOfBirthFieldLayout.top.toFloat())
			return false
		}
		else if (!termsAndConditionsCheckBox.isChecked) {
			termsAndConditionsCheckBox.error = getString(R.string.error_please_confirm)
			smoothScrollTo(registrationDateOfBirthFieldLayout.bottom.toFloat())
			return false
		}
		return true
	}
	
	private fun showErrorSnackbar(message: String) {
		Log.d(
			TAG,
			"showErrorSnackbar: called"
		)
		val snackbarView = findViewById<CoordinatorLayout>(R.id.coordinator_layout)
		Snackbar.make(
			snackbarView,
			message,
			Snackbar.LENGTH_LONG
		).setBackgroundTint(
			ContextCompat.getColor(
				this,
				R.color.design_default_color_error
			)
		).show()
	}
	
	/**
	 * Changes from loading screen back to normal.
	 */
	private fun changeFromLoadingScreen() {
		Log.d(
			TAG,
			"changeFromLoadingScreen: called"
		)
		findViewById<View>(R.id.entry_activity_loading_screen).visibility = View.GONE
		findViewById<View>(R.id.entry_activity_scroll_view).visibility = View.VISIBLE
	}
	
	private fun launchBillingFlow(details: SkuDetails?) {
		Log.d(
			TAG,
			"launchBillingFlow: called"
		)
		val params = BillingFlowParams.newBuilder().setSkuDetails(details).build()
		mBillingClient!!.launchBillingFlow(
			this,
			params
		) /* onPurchasesUpdated() */
	}
	
	/**
	 * Returns fresh new firestore instance.
	 *
	 * @return Fresh new firestore instance.
	 */
	private val firestoreInstance: FirebaseFirestore
		private get() {
			Log.d(
				TAG,
				"getFirestoreInstance: called"
			)
			return FirebaseFirestore.getInstance()
		}
	
	private fun getStringFromField(field: EditText): String {
		Log.d(
			TAG,
			"getStringFromField: called"
		)
		return if (field.text != null) {
			Log.d(
				TAG,
				"getStringFromField: text = " + field.text.toString()
			)
			field.text.toString()
		}
		else {
			Log.d(
				TAG,
				"getStringFromField: text = "
			)
			""
		}
	}
	
	/**
	 * This method resets all the layout errors
	 */
	private fun setLayoutErrorsNull() {
		Log.d(
			TAG,
			"setLayoutErrorsNull: called"
		)
		val constraintLayout = findViewById<ConstraintLayout>(R.id.entry_activity_constraint_layout)
		val childCount = constraintLayout.childCount
		for (i in 0 until childCount) {
			val child = constraintLayout.getChildAt(i)
			if (child is TextInputLayout) {
				child.error = null
			}
			(child as? CheckBox)?.clearFocus()
		}
	}
	
	private fun disableViews(vararg views: View) {
		Log.d(
			TAG,
			"disableViews: called"
		)
		for (view in views) {
			view.isEnabled = false
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
	private fun enableViews(vararg views: View) {
		Log.d(
			TAG,
			"enableViews: called"
		)
		for (view in views) {
			view.isEnabled = true
		}
	}
	
	/**
	 * Moves the screen to the desired position y.
	 *
	 * @param y Desired position to be at. Mostly view.getTop();
	 */
	private fun smoothScrollTo(y: Float) {
		Log.d(
			TAG,
			"smoothScrollTo: called"
		)
		val scrollView = findViewById<ScrollView>(R.id.entry_activity_scroll_view)
		scrollView.smoothScrollTo(
			0,
			y.toInt()
		)
	}
	
	private fun setFieldLayoutErrorAndScroll(
		errorLayout: TextInputLayout,
		message: String
	) {
		Log.d(
			TAG,
			"setFieldLayoutErrorAndScroll: called"
		)
		errorLayout.error = message
		smoothScrollTo(errorLayout.top.toFloat())
	}
	
	override fun onActivityResult(
		requestCode: Int,
		resultCode: Int,
		data: Intent?
	) {
		Log.d(
			TAG,
			"onActivityResult: called"
		)
		super.onActivityResult(
			requestCode,
			resultCode,
			data
		)
		//		int googleLoginRequestCode = getResources().getInteger(R.integer.request_code_google_login);
		//		// Normal login request code when user is guided from entry activity to main activity
		//		int loginRequestCode = getResources().getInteger(R.integer.request_code_login);
		//		// Result code when user logs out of main activity
		//		int logoutResultCode = getResources().getInteger(R.integer.result_code_logout);
		//		if (requestCode == googleLoginRequestCode) {
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
	
	private fun resetAllViews() {
		Log.d(
			TAG,
			"resetAllViews: called"
		)
		// Iterates through all children of the scroll view enables them
		val constraintLayout = findViewById<ConstraintLayout>(R.id.entry_activity_constraint_layout)
		val childCount = constraintLayout.childCount
		for (i in 0 until childCount) {
			val child = constraintLayout.getChildAt(i)
			child.isEnabled = true
			if (child is TextInputLayout) {
				// If it is an TextInputLayout it resets the errors
				val textInputLayout = child
				textInputLayout.error = null
				// If the TextInputLayout has an TextInputEditText set its text to null
				textInputLayout.editText!!.text = null
			}
			else if (child is CheckBox) {
				child.isChecked = false
			}
		}
	}
	
	fun onLoginGoogleButtonClick(view: View?) {
		Log.d(
			TAG,
			"onLoginGoogleButtonClick: called"
		)
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
	private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount) {
		Log.d(
			TAG,
			"firebaseAuthWithGoogleAccount: called"
		)
		val authCredential = GoogleAuthProvider.getCredential(
			account.idToken,
			null
		)
		mAuth!!.signInWithCredential(authCredential).addOnSuccessListener { command: AuthResult ->
			Log.d(
				TAG,
				"firebaseAuthWithGoogleAccount: great success signing in"
			)
			// Login success.
			val user = command.user
			if (command.additionalUserInfo != null && user != null) {
				if (command.additionalUserInfo!!.isNewUser) {
					Log.d(
						TAG,
						"firebaseAuthWithGoogleAccount: user is new"
					)
					saveUserInFirestore(
						user,
						null
					)
				}
				else {
					updateUI(user)
				}
			}
			else {
				// Something went wrong
				Toast.makeText(
					this,
					getString(R.string.error_try_again),
					Toast.LENGTH_SHORT
				).show()
				changeFromLoadingScreen()
			}
		}.addOnFailureListener { e: Exception? ->
			Log.e(
				TAG,
				"firebaseAuthWithGoogleAccount: ",
				e
			)
			Toast.makeText(
				this,
				getString(R.string.error_try_again),
				Toast.LENGTH_SHORT
			).show()
			changeFromLoadingScreen()
		}
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
	private fun createNewUser(purchase: Purchase) {
		Log.d(
			TAG,
			"createNewUser: called"
		)
		// Change to loading screen
		changeToLoadingScreen()
		// Define the HashMap carrying the signup information
		val registrationInformation: HashMap<String, String> = registrationFieldsContent
		val email = registrationInformation[getString(R.string.hashmap_key_email)]
		val password = registrationInformation[getString(R.string.hashmap_key_password)]
		// Create user with email and password
		mAuth!!.createUserWithEmailAndPassword(
			email!!,
			password!!
		).addOnSuccessListener { result: AuthResult ->
			Log.d(
				TAG,
				"createNewUser: great success creating user"
			)
			if (result.user != null) {
				saveUserInFirestore(
					result.user,
					purchase
				)
			}
		}.addOnFailureListener { e: Exception? ->
			Log.d(
				TAG,
				"createNewUser: failure creating user"
			)
			Log.e(
				TAG,
				"createNewUser: ",
				e
			)
			changeFromLoadingScreen()
			if (e is FirebaseNetworkException) {
				val connectionFail = getString(R.string.error_network_connection_failed)
				showErrorSnackbar(connectionFail)
			}
		}
	}
	
	/**
	 * Handles all the on click methods.
	 *
	 * @param v The view that called this.
	 */
	override fun onClick(v: View) {
		Log.d(
			TAG,
			"onClick: called"
		)
		if (v.id == R.id.entry_activity_registration_date_of_birth_field) {
			Log.d(
				TAG,
				"onClick: id = dob field"
			)
			DatePickerFragment(
				this,
				v as EditText
			).show(
				supportFragmentManager,
				"date picker"
			)
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
	override fun addTimeToFirestoreEntry(
		time: Date,
		uid: String?
	) {
		Log.d(
			TAG,
			"addTimeToFirestoreEntry: called"
		)
		// The time cannot be null. If network connection fails, it returns the offline date
		// Define Firestore
		val db = firestoreInstance
		// Define HashMap carrying the field and information for it
		val info = HashMap<String, Any>()
		info[getString(R.string.firestore_key_column_date_of_creation)] = time.toString()
		// Put the new information in the firestore
		db.collection(getString(R.string.firestore_key_collection_users)).document(uid!!).update(info).addOnSuccessListener { aVoid: Void? ->
			Log.d(
				TAG,
				"addTimeToFirestoreEntry: great success"
			)
		}.addOnFailureListener { e: Exception? ->
			Log.d(
				TAG,
				"addTimeToFirestoreEntry: failure"
			)
			Log.e(
				TAG,
				"addTimeToFirestoreEntry: ",
				e
			)
		}
	}
	
	override fun onSkuDetailsResponse(
		result: BillingResult,
		list: List<SkuDetails>
	) {
		Log.d(
			TAG,
			"onSkuDetailsResponse: called"
		)
		if (result.responseCode == BillingClient.BillingResponseCode.OK) {
			// Define a new SubscriptionPool
			val pool = SubscriptionPool()
			// Iterate through the SkuDetails list
			for (details in list) {
				// Define a new Subscription based on the SkuDetails
				val sub = Subscription(
					this,
					details
				)
				// Add the new Subscription to the pool
				pool.addSubscription(sub)
			}
			val viewPager2 = findViewById<ViewPager2>(R.id.entry_activity_subscription_view_pager)
			// Set the new Adapter to the ViewPager2 as we have the Skus from the server
			viewPager2.adapter = SubsPagerFinalAdapter(
				this,
				pool.sort()
			)
		}
		else {
			Log.d(
				TAG,
				"onSkuDetailsResponse: " + result.debugMessage
			)
			Log.d(
				TAG,
				"onSkuDetailsResponse: " + result.responseCode
			)
			if (result.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE or BillingClient.BillingResponseCode.ERROR) {
				SubsPagerInitAdapter(this).retry()
			}
		}
	}
	
	override fun onPurchasesUpdated(
		result: BillingResult,
		list: List<Purchase>?
	) {
		Log.d(
			TAG,
			"onPurchasesUpdated: called"
		)
		if (result.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
			for (purchase in list) {
				Log.d(
					TAG,
					"onPurchasesUpdated: got a purchase = $purchase"
				)
				handlePurchase(purchase)
			}
		}
		else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
			// Handle user cancellation
		}
		else {
			// Handle any other error
		}
	}
	
	private fun handlePurchase(purchase: Purchase) {
		Log.d(
			TAG,
			"handlePurchase: called"
		)
		if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
			// Grant entitlement to user
			createNewUser(purchase)
		}
		else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
			// Here you can confirm to the user that they've started the pending
			// purchase, and to complete it, they should follow instructions that
			// are given to them. You can also choose to remind the user in the
			// future to complete the purchase if you detect that it is still
			// pending.
		}
	}
	
	// The HashMap the information is saved to
	private val registrationFieldsContent: HashMap<String, String>
		// Access all the fields
		// Save all the fields content into strings
		// Save the content into HashMap.util.HashMap<kotlin.String, kotlin.String>
		private get() {
			Log.d(
				TAG,
				"getRegistrationFieldsContent: called"
			)
			// The HashMap the information is saved to
			val registrationFieldsContent = HashMap<String, String>()
			// Access all the fields
			val firstNameField: TextInputEditText
			val familyNameField: TextInputEditText
			val usernameField: TextInputEditText
			val emailField: TextInputEditText
			val passwordField: TextInputEditText
			val dateOfBirthField: TextInputEditText
			firstNameField = findViewById(R.id.entry_activity_registration_first_name_field)
			familyNameField = findViewById(R.id.entry_activity_registration_family_name_field)
			usernameField = findViewById(R.id.entry_activity_registration_username_field)
			emailField = findViewById(R.id.entry_activity_registration_email_field)
			passwordField = findViewById(R.id.entry_activity_registration_password_field)
			dateOfBirthField = findViewById(R.id.entry_activity_registration_date_of_birth_field)
			// Save all the fields content into strings
			val firstName: String
			val familyName: String
			val username: String
			val email: String
			val password: String
			val dateOfBirth: String
			firstName = getStringFromField(firstNameField)
			familyName = getStringFromField(familyNameField)
			username = getStringFromField(usernameField)
			email = getStringFromField(emailField)
			password = getStringFromField(passwordField)
			dateOfBirth = getStringFromField(dateOfBirthField)
			// Save the content into HashMap
			registrationFieldsContent[getString(R.string.hashmap_key_first_name)] = firstName
			registrationFieldsContent[getString(R.string.hashmap_key_family_name)] = familyName
			registrationFieldsContent[getString(R.string.hashmap_key_username)] = username
			registrationFieldsContent[getString(R.string.hashmap_key_email)] = email
			registrationFieldsContent[getString(R.string.hashmap_key_password)] = password
			registrationFieldsContent[getString(R.string.hashmap_key_date_of_birth)] = dateOfBirth
			return registrationFieldsContent
		}
	
	fun onConfirmButtonClick(view: View?) {
		Log.d(
			TAG,
			"onConfirmButtonClick: called"
		)
	}
	
	/**
	 * This method saves users info in firestore upon signup.
	 *
	 * @param user     The user to save the Uid.
	 * @param purchase
	 */
	private fun saveUserInFirestore(
		user: FirebaseUser?,
		purchase: Purchase?
	) {
		Log.d(
			TAG,
			"saveUserInFirestore: called"
		)
		// Define the HashMap carrying the signup information
		val registrationInformation: HashMap<String, String> = registrationFieldsContent
		// Save the signup information in strings
		val firstName = registrationInformation[getString(R.string.hashmap_key_first_name)]
		val familyName = registrationInformation[getString(R.string.hashmap_key_family_name)]
		val username = registrationInformation[getString(R.string.hashmap_key_username)]
		val email = registrationInformation[getString(R.string.hashmap_key_email)]
		val dateOfBirth = registrationInformation[getString(R.string.hashmap_key_date_of_birth)]
		val profilePicture = if (user!!.photoUrl != null) user.photoUrl.toString() else ""
		val db = firestoreInstance
		val toast = Toast.makeText(
			this,
			getString(R.string.error_stand_by),
			Toast.LENGTH_SHORT
		)
		// Define the HashMap carrying the user information from the fields
		val userInfo = HashMap<String, Any?>()
		userInfo[getString(R.string.firestore_key_column_first_name)] = firstName
		userInfo[getString(R.string.firestore_key_column_family_name)] = familyName
		userInfo[getString(R.string.firestore_key_column_username)] = username
		userInfo[getString(R.string.firestore_key_column_email)] = email
		userInfo[getString(R.string.firestore_key_column_date_of_birth)] = dateOfBirth
		userInfo[getString(R.string.firestore_key_column_profile_picture)] = profilePicture
		// Store the user information in the firestore
		db.collection(getString(R.string.firestore_key_collection_users)).document(user.uid).set(userInfo).addOnSuccessListener { command: Void? ->
			Log.d(
				TAG,
				"saveUserInFirestore: great success"
			)
			toast.cancel()
			// Retrieve the creation date from the internet
			// It calls the callback addTimeToFirestoreEntry
			RetrieveInternetTime(
			).execute(getString(R.string.google_time_server_url))
			// Acknowledge the purchase if it hasn't already been acknowledged
			if (!purchase!!.isAcknowledged) {
				val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
				mBillingClient!!.acknowledgePurchase(
					params,
					this /* onAcknowledgePurchaseResponse */
				)
			}
		}.addOnFailureListener { e: Exception? ->
			Log.d(
				TAG,
				"saveUserInFirestore: failure"
			)
			Log.e(
				TAG,
				"saveUserInFirestore: ",
				e
			)
			toast.show()
		}
	}
	
	fun updateUI(user: FirebaseUser?) {
		Log.d(
			TAG,
			"updateUI: called"
		)
		if (user != null) {
			changeToLoadingScreen()
			// Starts the MainActivity an passes the FirebaseUser through
			// It starts for result which is called when the user logs out in MainActivity
			//			startMainActivity(user);
		}
		else {
			Log.d(
				TAG,
				"updateUI: user == null"
			)
			// no user logged in.
			changeFromLoadingScreen()
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
	fun onRetryClick(view: View?) {
		Log.d(
			TAG,
			"onRetryClick: called"
		)
	}
	
	override fun onAcknowledgePurchaseResponse(result: BillingResult) {
		Log.d(
			TAG,
			"onAcknowledgePurchaseResponse: called"
		)
		updateUI(mAuth!!.currentUser)
	}
	
	override fun onBillingSetupFinished(result: BillingResult) {
		Log.d(
			TAG,
			"onBillingSetupFinished: called"
		)
		// The billing client is ready. Query SKUs here.
		if (result.responseCode == BillingClient.BillingResponseCode.OK) {
			// Queries the server and calls back
			mBillingClient!!.querySkuDetailsAsync(
				SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.SUBS).build(),
				this /* onSkuDetailsResponse */
			)
		}
		else {
			Log.w(
				TAG,
				"onBillingSetupFinished: result = " + result.debugMessage
			)
			Log.d(
				TAG,
				"onBillingSetupFinished: result code = " + result.responseCode
			)
		}
	}
	
	// Define the list carrying the subscription IDs
	private val skuList: List<String>
		private get() {
			Log.d(
				TAG,
				"getSkuList: called"
			)
			// Define the list carrying the subscription IDs
			val skuList: MutableList<String> = ArrayList()
			skuList.add(getString(R.string.subscription_bronze))
			skuList.add(getString(R.string.subscription_silver))
			skuList.add(getString(R.string.subscription_gold))
			skuList.add(getString(R.string.subscription_platinum))
			return skuList
		}
	
	override fun onBillingServiceDisconnected() {
		Log.d(
			TAG,
			"onBillingServiceDisconnected: called"
		)
	}
	
	fun onLoginButtonClicked(view: View?) {
		Log.d(
			TAG,
			"onLoginButtonClicked: called"
		)
		loginUser()
	}
	
	/**
	 * This method logs the user in.
	 */
	fun loginUser() {
		Log.d(
			TAG,
			"loginWithUser: called"
		)
		// Define all needed Views
		val loginEmailField = findViewById<EditText>(R.id.entry_activity_login_email_field)
		val loginPasswordField = findViewById<EditText>(R.id.entry_activity_login_password_field)
		val loginEmailFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_login_email_layout)
		val loginPasswordFieldLayout = findViewById<TextInputLayout>(R.id.entry_activity_login_password_layout)
		val scrollView = findViewById<ScrollView>(R.id.entry_activity_scroll_view)
		// Get the Strings from the fields
		val email = getStringFromField(loginEmailField)
		val password = getStringFromField(loginPasswordField)
		setLayoutErrorsNull()
		if (email.length == 0) {
			// If the ScrollView is below the scroll point, set error and scroll, else just error
			if (scrollView.y > loginEmailFieldLayout.top) {
				setFieldLayoutErrorAndScroll(
					loginEmailFieldLayout,
					getString(R.string.error_you_forgot_me)
				)
			}
			else {
				loginEmailFieldLayout.error = getString(R.string.error_you_forgot_me)
			}
			return
		}
		else if (password.length == 0) {
			// If the ScrollView is below the scroll point, set error and scroll, else just error
			if (scrollView.y > loginPasswordFieldLayout.top) {
				setFieldLayoutErrorAndScroll(
					loginPasswordFieldLayout,
					getString(R.string.error_you_forgot_me)
				)
			}
			else {
				loginPasswordFieldLayout.error = getString(R.string.error_you_forgot_me)
			}
			return
		}
		// Show loading assistant.
		findViewById<View>(R.id.entry_activity_logging_in_waiting_assistant).visibility = View.VISIBLE
		// Disable the email and password field.
		findViewById<View>(R.id.entry_activity_login_email_layout).isEnabled = false
		findViewById<View>(R.id.entry_activity_login_password_layout).isEnabled = false
		mAuth!!.signInWithEmailAndPassword(
			email,
			password
		).addOnSuccessListener { command: AuthResult ->
			Log.d(
				TAG,
				"loginUser: great success"
			)
			updateUI(command.user)
		}.addOnFailureListener { e: Exception? ->
			Log.d(
				TAG,
				"loginUser: failure"
			)
			Log.e(
				TAG,
				"loginUser: ",
				e
			)
			setLayoutErrorsNull()
			// Remove the loading assistant
			findViewById<View>(R.id.entry_activity_logging_in_waiting_assistant).visibility = View.GONE
			// Enable the views again
			findViewById<View>(R.id.entry_activity_login_email_layout).isEnabled = true
			findViewById<View>(R.id.entry_activity_login_password_layout).isEnabled = true
			if (e is FirebaseAuthInvalidCredentialsException) {
				smoothScrollTo(loginEmailFieldLayout.top.toFloat())
				loginEmailFieldLayout.error = getString(R.string.error_email_or_password_incorrect)
			}
			else if (e is FirebaseNetworkException) {
				smoothScrollTo(loginEmailFieldLayout.top.toFloat())
				loginEmailFieldLayout.error = getString(R.string.error_network_connection_failed)
			}
		}
	}
	
	companion object {
		private const val TAG = "EntryActivity"
	}
}