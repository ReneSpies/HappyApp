package com.aresid.happyapp.database.tables.accountdata

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 *    Created on: 15.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Dao
interface AccountDataDao {
	
	@Query("SELECT * FROM account_data WHERE uid = :uid")
	fun get(uid: String): LiveData<AccountData>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(data: AccountData)
	
	@Delete
	suspend fun delete(data: AccountData)
	
}