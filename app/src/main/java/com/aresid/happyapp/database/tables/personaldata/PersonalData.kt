package com.aresid.happyapp.database.tables.personaldata

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

@Entity(tableName = DatabaseKeys.Table.PersonalData.NAME)
data class PersonalData(
	
	@PrimaryKey @ColumnInfo(name = DatabaseKeys.Table.PersonalData.Column.UID) val uid: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.PersonalData.Column.FAMILY_NAME) val familyName: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.PersonalData.Column.FIRST_NAME) val first_name: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.PersonalData.Column.EMAIL) val email: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.PersonalData.Column.USERNAME) val username: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.PersonalData.Column.DATE_OF_BIRTH) val dateOfBirth: String, // TODO: 15.06.20 Check if it is better to save a Calendar object
	
	@ColumnInfo(name = DatabaseKeys.Table.PersonalData.Column.DATE_OF_CREATION) val dateOfCreation: String, // TODO: 15.06.20 Check if it is better to save a Calendar object
	
	@ColumnInfo(name = DatabaseKeys.Table.PersonalData.Column.PROFILE_PICTURE) val profilePicture: String

)