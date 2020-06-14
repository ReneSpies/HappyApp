package com.aresid.happyapp.databases.billingdatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import timber.log.Timber

/**
 *    Created on: 18.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

/**
 * No update methods necessary since for each table there is ever expecting one
 * row, hence the primary key is hardcoded.
 */
@Dao
interface EntitlementsDao {
	
	/* BronzeSubscription beginning */
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(bronzeSubscription: BronzeSubscription)
	
	@Update
	fun update(bronzeSubscription: BronzeSubscription)
	
	@Query("SELECT * FROM bronze_subscription_table LIMIT 1")
	fun getBronzeSubscription(): LiveData<BronzeSubscription>
	
	@Delete
	fun delete(bronzeSubscription: BronzeSubscription)
	
	/* SilverSubscription beginning */
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(silverSubscription: SilverSubscription)
	
	@Update
	fun update(silverSubscription: SilverSubscription)
	
	@Query("SELECT * FROM silver_subscription_table LIMIT 1")
	fun getSilverSubscription(): LiveData<SilverSubscription>
	
	@Delete
	fun delete(silverSubscription: SilverSubscription)
	
	/* GoldSubscription beginning */
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(goldSubscription: GoldSubscription)
	
	@Update
	fun update(goldSubscription: GoldSubscription)
	
	@Query("SELECT * FROM gold_subscription_table LIMIT 1")
	fun getGoldSubscription(): LiveData<GoldSubscription>
	
	@Delete
	fun delete(goldSubscription: GoldSubscription)
	
	/* PlatinumSubscription beginning */
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(platinumSubscription: PlatinumSubscription)
	
	@Update
	fun update(platinumSubscription: PlatinumSubscription)
	
	@Query("SELECT * FROM platinum_subscription_table LIMIT 1")
	fun getPlatinumSubscription(): LiveData<PlatinumSubscription>
	
	@Delete
	fun delete(platinumSubscription: PlatinumSubscription)
	
	/**
	 * This is purely for convenience. The clients of this DAO don't have to
	 * discriminate among [BronzeSubscription] vs [SilverSubscription] vs [GoldSubscription]
	 * vs [PlatinumSubscription] but can simply send in a list of [entitlements][Entitlement].
	 * The Transaction annotation makes the method do all its Dao method calls in one transaction
	 * with the database.
	 */
	@Transaction
	fun insert(vararg entitlements: Entitlement) {
		
		Timber.d("insert: called")
		
		entitlements.forEach { entitlement ->
			
			when (entitlement) {
				
				is BronzeSubscription -> insert(entitlement)
				
				is SilverSubscription -> insert(entitlement)
				
				is GoldSubscription -> insert(entitlement)
				
				is PlatinumSubscription -> insert(entitlement)
				
			}
			
		}
		
	}
	
	@Transaction
	fun update(vararg entitlements: Entitlement) {
		
		Timber.d("update: called")
		
		// Iterates through all elements of entitlements and updates the corresponding tables
		entitlements.forEach { entitlement ->
			
			when (entitlement) {
				
				is BronzeSubscription -> update(entitlement)
				
				is SilverSubscription -> update(entitlement)
				
				is GoldSubscription -> update(entitlement)
				
				is PlatinumSubscription -> update(entitlement)
				
			}
			
		}
		
	}
}
