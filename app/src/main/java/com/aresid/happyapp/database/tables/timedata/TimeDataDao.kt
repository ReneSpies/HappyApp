package com.aresid.happyapp.database.tables.timedata

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 *    Created on: 15.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Dao
interface TimeDataDao {
	
	@Query("SELECT * FROM time_data WHERE uid = :uid")
	fun get(uid: String): LiveData<TimeData>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(data: TimeData)
	
	@Delete
	suspend fun delete(data: TimeData)
	
}