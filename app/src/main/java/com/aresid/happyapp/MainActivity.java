package com.aresid.happyapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity
		extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener,
		           SearchFragment.OnFragmentInteractionListener,
		           FavoritesFragment.OnFragmentInteractionListener,
		           MyAccountFragment.OnFragmentInteractionListener,
		           AdvertisementFragment.OnFragmentInteractionListener,
		           HomeFragment.OnFragmentInteractionListener,
		           OfferFragment.OnFragmentInteractionListener,
		           LookFragment.OnFragmentInteractionListener {
	private static final String              FIREBASE_USER_INTENT_KEY         = "firesbase_user";
	private static final String              GOOGLE_SIGNIN_ACCOUNT_INTENT_KEY = "google_sign_in_account";
	private static final String              USER_FIRESTORE_ID_INTENT_KEY     = "user_firestore_id";
	private static final String              TAG                              = "MainActivity";
	private              FirebaseUser        mFirebaseUser;
	private              GoogleSignInAccount mSignInAccount;
	private              String              mUserFirestoreID;
	private              int                 doubleOnBackPressedHelper        = 0;
	private              int                 mTimesLoggedIn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate:true");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		//		// Check if we need to display our OnboardingFragment
		//		if (!sharedPreferences.getBoolean(OnboardingFragment.COMPLETED_ONBOARDING_PREF_NAME, false))
		//		{
		//			// The user hasn't seen the OnboardingFragment yet, so show it
		//			startActivity(new Intent(this, OnboardingActivity.class));
		//		}
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mFirebaseUser = extras.getParcelable(FIREBASE_USER_INTENT_KEY);
			mSignInAccount = extras.getParcelable(GOOGLE_SIGNIN_ACCOUNT_INTENT_KEY);
			mUserFirestoreID = extras.getString(USER_FIRESTORE_ID_INTENT_KEY);
		}
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		displayHomeFragment();
		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setCheckedItem(R.id.nav_home);
		navigationView.setNavigationItemSelectedListener(this);
	}
	//private void displayOnboardingFragment()
	//{
	//	Log.d(TAG, "displayOnboardingFragment:true");
	//	getSupportFragmentManager().beginTransaction()
	//	                           .replace(R.id.fragment_container, OnboardingFragment.newInstance())
	//	                           .commit();
	//}
	private void displayHomeFragment() {
		Log.d(TAG, "displayHomeFragment:true");
		new DisplayFragment(this).displayFragment(R.id.fragment_container, HomeFragment.newInstance());
	}
	/**
	 * Method gets called when the NavigationDrawer's header is clicked.
	 * Planned to be replaced by onClick method.
	 *
	 * @param view The view which called this method.
	 */
	public void onNavHeaderClick(View view) {
		Log.d(TAG, "onNavHeaderClick:true");
		NavigationView navigationView = findViewById(R.id.nav_view);
		if (navigationView.getCheckedItem() != null) {
			navigationView.getCheckedItem()
			              .setChecked(false);
		}
		displayMyAccountFragment();
		closeDrawer();
	}
	private void displayMyAccountFragment() {
		Log.d(TAG, "displayMyAccountFragment:true");
		new DisplayFragment(this).displayFragment(R.id.fragment_container, MyAccountFragment.newInstance());
	}
	/**
	 * Method simply closes the drawer.
	 */
	private void closeDrawer() {
		Log.d(TAG, "closeDrawer:true");
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
	}
	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed:true");
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			closeDrawer();
		} else {
			if (doubleOnBackPressedHelper == 0) {
				Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT)
				     .show();
				doubleOnBackPressedHelper++;
				// A Timer that resets my onBackPressed helper to 0 after 6.13 seconds.
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						Log.d(TAG, "run:true");
						doubleOnBackPressedHelper = 0;
					}
				}, 6130);
				return;
			} else {
				if (doubleOnBackPressedHelper == 1) {
					finishAffinity();
					return;
				}
			}
			finishAffinity();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu:true");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected:true");
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up mButton, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		// noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		Log.d(TAG, "onNavigationItemSelected:true");
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		switch (id) {
			case R.id.nav_home:
				displayHomeFragment();
				item.setChecked(true);
				break;
			case R.id.nav_search:
				displaySearchFragment();
				break;
			case R.id.nav_favorites:
				displayFavoritesFragment();
				break;
			case R.id.nav_place_ad:
				displayAdvertisementFragment();
				break;
			default:
				break;
		}
		closeDrawer();
		return true;
	}
	private void displaySearchFragment() {
		Log.d(TAG, "displaySearchFragment:true");
		new DisplayFragment(this).displayFragment(R.id.fragment_container, SearchFragment.newInstance());
	}
	private void displayFavoritesFragment() {
		Log.d(TAG, "displayFavoritesFragment:true");
		new DisplayFragment(this).displayFragment(R.id.fragment_container, FavoritesFragment.newInstance());
	}
	private void displayAdvertisementFragment() {
		Log.d(TAG, "displayAdvertisementFragment:true");
		new DisplayFragment(this).displayFragment(R.id.fragment_container, AdvertisementFragment.newInstance());
	}
	// TODO: Delete method in all fragment interfaces.
	@Override
	public void onFragmentInteraction(Uri uri) {
	}
	/**
	 * Method is called when the appropriate item in the NavigationDrawer was clicked.
	 *
	 * @param view View which called the method.
	 */
	public void onLookingClick(View view) {
		Log.d(TAG, "onLookingClick:true");
		// TODO: Delete this.
		displayLookFragment();
	}
	private void displayLookFragment() {
		Log.d(TAG, "displayLookFragment:true");
		new DisplayFragment(this).displayFragment(R.id.fragment_container, LookFragment.newInstance());
	}
	/**
	 * Method is called when the appropriate item in the NavigationDrawer was clicked.
	 *
	 * @param view View which called the method.
	 */
	public void onOfferingClick(View view) {
		Log.d(TAG, "onOfferingClick:true");
		displayOfferFragment();
	}
	private void displayOfferFragment() {
		Log.d(TAG, "displayOfferFragment:true");
		new DisplayFragment(this).displayFragment(R.id.fragment_container, OfferFragment.newInstance());
	}
}
