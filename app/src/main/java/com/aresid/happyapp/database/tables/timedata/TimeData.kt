package com.aresid.happyapp.database.tables.timedata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aresid.happyapp.keys.DatabaseKeys

/**
 *    Created on: 15.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Entity(tableName = DatabaseKeys.Table.TimeData.NAME)
data class TimeData(
	
	@PrimaryKey @ColumnInfo(name = DatabaseKeys.Table.TimeData.Column.UID) val uid: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.TimeData.Column.START_DATE) val startDate: String, // TODO: 15.06.20 Check if it is better to save a Calendar object
	
	@ColumnInfo(name = DatabaseKeys.Table.TimeData.Column.END_DATE) val endDate: String // TODO: 15.06.20 Check if it is better to save a Calendar object

)