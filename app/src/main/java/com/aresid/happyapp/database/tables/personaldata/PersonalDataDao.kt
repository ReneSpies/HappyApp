package com.aresid.happyapp.database.tables.personaldata

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 *    Created on: 15.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Dao
interface PersonalDataDao {
	
	@Query("SELECT * FROM personal_data WHERE uid = :uid")
	fun get(uid: String): LiveData<PersonalDataDao>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(data: PersonalData)
	
	@Delete
	suspend fun delete(data: PersonalData)
	
}