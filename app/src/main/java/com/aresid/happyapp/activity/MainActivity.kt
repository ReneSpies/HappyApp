package com.aresid.happyapp.activity

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.aresid.happyapp.R
import com.aresid.happyapp.RetrieveInternetTime.OnInternetTimeInteractionListener
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*

class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnInternetTimeInteractionListener {
	private var mFirebaseUser: FirebaseUser? = null
	private var mSignInAccount: GoogleSignInAccount? = null
	private var mUserFirestoreID: String? = null
	private var doubleOnBackPressedHelper = 0
	private val mTimesLoggedIn = 0
	override fun onCreate(savedInstanceState: Bundle?) {
		Log.d(
			TAG,
			"onCreate:true"
		)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
		//		// Check if we need to display our OnboardingFragment
		//		if (!sharedPreferences.getBoolean(OnboardingFragment
		//		.COMPLETED_ONBOARDING_PREF_NAME, false))
		//		{
		//			// The user hasn't seen the OnboardingFragment yet, so show it
		//			startActivity(new Intent(this, OnboardingActivity.class));
		//		}
		val extras = intent.extras
		if (extras != null) {
			mFirebaseUser = extras.getParcelable(FIREBASE_USER_INTENT_KEY)
			mSignInAccount = extras.getParcelable(GOOGLE_SIGNIN_ACCOUNT_INTENT_KEY)
			mUserFirestoreID = extras.getString(USER_FIRESTORE_ID_INTENT_KEY)
		}
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)
		val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
		val toggle = ActionBarDrawerToggle(
			this,
			drawer,
			toolbar,
			R.string.open_navigation_drawer,
			R.string.close_navigation_drawer
		)
		drawer.addDrawerListener(toggle)
		toggle.syncState()
		displayHomeFragment()
		val navigationView = findViewById<NavigationView>(R.id.nav_view)
		navigationView.setCheckedItem(R.id.nav_home)
		navigationView.setNavigationItemSelectedListener(this)
	}
	
	//private void displayOnboardingFragment()
	//{
	//	Log.d(TAG, "displayOnboardingFragment:true");
	//	getSupportFragmentManager().beginTransaction()
	//	                           .replace(R.id.fragment_container, OnboardingFragment
	//	                           .newInstance())
	//	                           .commit();
	//}
	private fun displayHomeFragment() {
		Log.d(
			TAG,
			"displayHomeFragment:true"
		)
	}
	
	/**
	 * Method gets called when the NavigationDrawer's header is clicked.
	 * Planned to be replaced by onClick method.
	 *
	 * @param view The view which called this method.
	 */
	fun onNavHeaderClick(view: View?) {
		Log.d(
			TAG,
			"onNavHeaderClick:true"
		)
		val navigationView = findViewById<NavigationView>(R.id.nav_view)
		if (navigationView.checkedItem != null) {
			navigationView.checkedItem!!.isChecked = false
		}
		displayMyAccountFragment()
		closeDrawer()
	}
	
	private fun displayMyAccountFragment() {
		Log.d(
			TAG,
			"displayMyAccountFragment:true"
		)
	}
	
	/**
	 * Method simply closes the drawer.
	 */
	private fun closeDrawer() {
		Log.d(
			TAG,
			"closeDrawer:true"
		)
		val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
		drawer.closeDrawer(GravityCompat.START)
	}
	
	override fun onBackPressed() {
		Log.d(
			TAG,
			"onBackPressed:true"
		)
		val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			closeDrawer()
		}
		else {
			if (doubleOnBackPressedHelper == 0) {
				Toast.makeText(
					this,
					"Press again to exit",
					Toast.LENGTH_SHORT
				).show()
				doubleOnBackPressedHelper++
				// A Timer that resets my onBackPressed helper to 0 after 6.13 seconds.
				Timer().schedule(
					object: TimerTask() {
						override fun run() {
							Log.d(
								TAG,
								"run:true"
							)
							doubleOnBackPressedHelper = 0
						}
					},
					6130
				)
				return
			}
			else {
				if (doubleOnBackPressedHelper == 1) {
					finishAffinity()
					return
				}
			}
			finishAffinity()
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		Log.d(
			TAG,
			"onCreateOptionsMenu:true"
		)
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(
			R.menu.main,
			menu
		)
		return true
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		Log.d(
			TAG,
			"onOptionsItemSelected:true"
		)
		when (item.itemId) {
			R.id.action_settings -> {
			}
			
			R.id.action_contact -> {
			}
			
			R.id.action_logout -> onToolbarMenuLogoutClicked()
		}
		return super.onOptionsItemSelected(item)
	}
	
	private fun onToolbarMenuLogoutClicked() {
		Log.d(
			TAG,
			"onToolbarMenuLogoutClicked: called"
		)
		FirebaseAuth.getInstance().signOut()
		FirebaseAuth.getInstance().addAuthStateListener { auth: FirebaseAuth ->
			if (auth.currentUser == null) {
				handleFinish()
			}
		}
	}
	
	private fun handleFinish() {
		Log.d(
			TAG,
			"handleFinish: called"
		)
		// Set a result so onActivityResult() is called in the activity from backstack
		// If necessary, I can do stuff in onActivityResult() like resetting views
		val resultIntent = Intent()
		//		int logoutResultCode = getResources().getInteger(R.integer.result_code_logout);
		//		setResult(logoutResultCode, resultIntent);
		finish()
	}
	
	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		Log.d(
			TAG,
			"onNavigationItemSelected:true"
		)
		// Handle navigation view item clicks here.
		val id = item.itemId
		when (id) {
			R.id.nav_home -> {
				displayHomeFragment()
				item.isChecked = true
			}
			
			R.id.nav_search -> displaySearchFragment()
			R.id.nav_favorites -> displayFavoritesFragment()
			R.id.nav_place_ad -> displayAdvertisementFragment()
			
			else -> {
			}
		}
		closeDrawer()
		return true
	}
	
	private fun displaySearchFragment() {
		Log.d(
			TAG,
			"displaySearchFragment:true"
		)
	}
	
	private fun displayFavoritesFragment() {
		Log.d(
			TAG,
			"displayFavoritesFragment:true"
		)
	}
	
	private fun displayAdvertisementFragment() {
		Log.d(
			TAG,
			"displayAdvertisementFragment:true"
		)
	}
	
	/**
	 * Method is called when the appropriate item in the NavigationDrawer was clicked.
	 *
	 * @param view View which called the method.
	 */
	fun onLookingClick(view: View?) {
		Log.d(
			TAG,
			"onLookingClick:true"
		)
		// TODO: Delete this.
		displayLookFragment()
	}
	
	private fun displayLookFragment() {
		Log.d(
			TAG,
			"displayLookFragment:true"
		)
	}
	
	/**
	 * Method is called when the appropriate item in the NavigationDrawer was clicked.
	 *
	 * @param view View which called the method.
	 */
	fun onOfferingClick(view: View?) {
		Log.d(
			TAG,
			"onOfferingClick:true"
		)
		displayOfferFragment()
	}
	
	private fun displayOfferFragment() {
		Log.d(
			TAG,
			"displayOfferFragment:true"
		)
	}
	
	override fun addTimeToFirestoreEntry(
		time: Date,
		uid: String?
	) {
		Log.d(
			TAG,
			"addTimeToFirestoreEntry: called"
		)
	}
	
	companion object {
		private const val FIREBASE_USER_INTENT_KEY = "firesbase_user"
		private const val GOOGLE_SIGNIN_ACCOUNT_INTENT_KEY = "google_sign_in_account"
		private const val USER_FIRESTORE_ID_INTENT_KEY = "user_firestore_id"
		private const val TAG = "MainActivity"
	}
}