package com.aresid.happyapp.database.tables.accountdata

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

@Entity(tableName = DatabaseKeys.Table.AccountData.NAME)
data class AccountData(
	
	@PrimaryKey @ColumnInfo(name = DatabaseKeys.Table.AccountData.Column.UID) val uid: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.AccountData.Column.TYPE) val type: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.AccountData.Column.PURCHASES_COUNT) val purchasesCount: Int = 0,
	
	@ColumnInfo(name = DatabaseKeys.Table.AccountData.Column.LOCATION) val location: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.AccountData.Column.SUBSCRIPTION_EXPIRY_DATE) val subscriptionExpiryDate: String, // TODO: 15.06.20 Check if it is better to save a Calendar object
	
	@ColumnInfo(name = DatabaseKeys.Table.AccountData.Column.SUBSCRIPTION_STATUS) val subscriptionStatus: String

)