package com.aresid.happyapp.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.aresid.happyapp.R
import com.aresid.happyapp.databinding.FragmentMainBinding
import timber.log.Timber

/**
 * Created on: 15/04/2020
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class MainFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentMainBinding
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentMainBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Setup navigation
		setupNavigation()
		
		// Return the inflated layout
		return binding.root
		
	}
	
	private fun setupNavigation() {
		
		Timber.d("setupNavigation: called")
		
		// Get the NavController
		val navController = (childFragmentManager.findFragmentById(R.id.navigation_host) as NavHostFragment).navController
		
		// Build and define an AppBarConfiguration object
		val appBarConfiguration = AppBarConfiguration.Builder(
			R.id.nav_lobby,
			R.id.nav_search,
			R.id.nav_favorites,
			R.id.nav_ad,
			R.id.nav_settings,
			R.id.nav_account,
			R.id.nav_contact
		).setOpenableLayout(binding.drawerLayout).build()
		
		// Setup the toolbar with NavController
		NavigationUI.setupWithNavController(
			binding.toolbar,
			navController,
			appBarConfiguration
		)
		
		// Setup the navigationView with NavController
		NavigationUI.setupWithNavController(
			binding.navigationView,
			navController
		)
		
	}
	
}