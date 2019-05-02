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

		HomeFragment home = HomeFragment.newInstance();
		mFragmentTransaction().replace(R.id.fragment_container, home)
		                      .commit();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setCheckedItem(R.id.nav_home);
		navigationView.setNavigationItemSelectedListener(this);
	}

	private FragmentTransaction mFragmentTransaction()
	{
		FragmentManager manager = getSupportFragmentManager();
		return manager.beginTransaction();
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

	public void onNavHeaderClick(View view)
	{
		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.getCheckedItem()
		              .setChecked(false);

		MyAccountFragment account = MyAccountFragment.newInstance();
		mFragmentTransaction().replace(R.id.fragment_container, account)
		                      .addToBackStack(null)
		                      .commit();

		closeDrawer();
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		switch (id)
		{
			case R.id.nav_home:
				HomeFragment home = HomeFragment.newInstance();
				mFragmentTransaction().replace(R.id.fragment_container, home)
				                      .addToBackStack(null)
				                      .commit();
				item.setChecked(true);
				break;
			case R.id.nav_search:
				SearchFragment search = SearchFragment.newInstance();
				mFragmentTransaction().replace(R.id.fragment_container, search)
				                      .addToBackStack(null)
				                      .commit();
				break;
			case R.id.nav_favorites:
				FavoritesFragment favorites = FavoritesFragment.newInstance();
				mFragmentTransaction().replace(R.id.fragment_container, favorites)
				                      .addToBackStack(null)
				                      .commit();
				break;
			case R.id.nav_place_ad:
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

	@Override
	public void onFragmentInteraction(Uri uri)
	{

	}

	public void onLookingClick(View view)
	{
		LookFragment look = LookFragment.newInstance();
		mFragmentTransaction().replace(R.id.fragment_container, look)
		                      .addToBackStack(null)
		                      .commit();
	}

	public void onOfferingClick(View view)
	{
		OfferFragment offer = OfferFragment.newInstance();
		mFragmentTransaction().replace(R.id.fragment_container, offer)
		                      .addToBackStack(null)
		                      .commit();
	}






}
