package com.aresid.happyapp.database.tables.subscriptiondata

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 *    Created on: 15.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Dao
interface SubscriptionDataDao {
	
	@Query("SELECT * FROM subscription_data WHERE sku = :sku")
	fun get(sku: String): LiveData<SubscriptionData>
	
	@Query("SELECT * FROM subscription_data")
	fun getAll(): LiveData<List<SubscriptionData>>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(data: SubscriptionData)
	
	@Delete
	suspend fun delete(data: SubscriptionData)
	
}