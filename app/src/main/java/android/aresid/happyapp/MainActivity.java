package android.aresid.happyapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity
		extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener,
		           SearchFragment.OnFragmentInteractionListener,
		           FavoritesFragment.OnFragmentInteractionListener,
		           MyAccountFragment.OnFragmentInteractionListener,
		           AdvertisementFragment.OnFragmentInteractionListener,
		           HomeFragment.OnFragmentInteractionListener,
		           OfferFragment.OnFragmentInteractionListener,
		           LookFragment.OnFragmentInteractionListener

{
	private FirebaseUser mFirebaseUser;




	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Bundle extras = getIntent().getExtras();

		if (extras != null)
		{
			mFirebaseUser = extras.getParcelable("firebaseUser");
		}

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
		                                                         R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		// TODO: Refactor this and move it to own methods.
		HomeFragment home = HomeFragment.newInstance();
		mFragmentTransaction().replace(R.id.fragment_container, home)
		                      .commit();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setCheckedItem(R.id.nav_home);
		navigationView.setNavigationItemSelectedListener(this);
	}




	/**
	 * Method gets called when the NavigationDrawer's header is clicked.
	 * Planned to be replaced by onClick method.
	 *
	 * @param view The view which called this method.
	 */
	public void onNavHeaderClick(View view)
	{
		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.getCheckedItem()
		              .setChecked(false);

		// TODO: Replace by displayXXX method.
		MyAccountFragment account = MyAccountFragment.newInstance();
		mFragmentTransaction().replace(R.id.fragment_container, account)
		                      .addToBackStack(null)
		                      .commit();

		closeDrawer();
	}




	@Override
	public void onBackPressed()
	{
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			closeDrawer();
		}
		else
		{
			super.onBackPressed();
		}
	}




	/**
	 * Method simply closes the drawer.
	 */
	private void closeDrawer()
	{
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}




	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}




	/**
	 * Planned to be replaced by displayXXX() methods.
	 *
	 * @return FragmentTransaction object.
	 */
	private FragmentTransaction mFragmentTransaction()
	{
		FragmentManager manager = getSupportFragmentManager();
		return manager.beginTransaction();
	}




	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		switch (id)
		{
			case R.id.nav_home:
				// TODO: Replace by displayHomeFragment method.
				HomeFragment home = HomeFragment.newInstance();
				mFragmentTransaction().replace(R.id.fragment_container, home)
				                      .addToBackStack(null)
				                      .commit();
				item.setChecked(true);
				break;
			case R.id.nav_search:
				// TODO: Replace by displaySearchFragment method.
				SearchFragment search = SearchFragment.newInstance();
				mFragmentTransaction().replace(R.id.fragment_container, search)
				                      .addToBackStack(null)
				                      .commit();
				break;
			case R.id.nav_favorites:
				// TODO: Replace by displayFavoritesFragment method.
				FavoritesFragment favorites = FavoritesFragment.newInstance();
				mFragmentTransaction().replace(R.id.fragment_container, favorites)
				                      .addToBackStack(null)
				                      .commit();
				break;
			case R.id.nav_place_ad:
				// TODO: Replace by displayAdvertisementFragment method.
				AdvertisementFragment ad = AdvertisementFragment.newInstance();
				mFragmentTransaction().replace(R.id.fragment_container, ad)
				                      .addToBackStack(null)
				                      .commit();
				break;
			default:
				break;
		}

		closeDrawer();
		return true;
	}




	// TODO: Delete method in all fragment interfaces.
	@Override
	public void onFragmentInteraction(Uri uri)
	{

	}




	/**
	 * Method is called when the appropriate item in the NavigationDrawer was clicked.
	 *
	 * @param view View which called the method.
	 */
	public void onLookingClick(View view)
	{
		// TODO: Replace with displayLookFragment method.
		LookFragment look = LookFragment.newInstance();
		mFragmentTransaction().replace(R.id.fragment_container, look)
		                      .addToBackStack(null)
		                      .commit();
	}




	/**
	 * Method is called when the appropriate item in the NavigationDrawer was clicked.
	 *
	 * @param view View which called the method.
	 */

	public void onOfferingClick(View view)
	{
		// TODO: Replace with displayOfferFragment method.
		OfferFragment offer = OfferFragment.newInstance();
		mFragmentTransaction().replace(R.id.fragment_container, offer)
		                      .addToBackStack(null)
		                      .commit();
	}







}
