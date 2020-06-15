package com.aresid.happyapp.database.tables.subscriptiondata

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

@Entity(tableName = DatabaseKeys.Table.SubscriptionData.NAME)
data class SubscriptionData(
	
	@PrimaryKey @ColumnInfo(name = DatabaseKeys.Table.SubscriptionData.Column.SKU) val sku: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.SubscriptionData.Column.TITLE) val title: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.SubscriptionData.Column.DESCRIPTION) val description: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.SubscriptionData.Column.PRICE) val price: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.SubscriptionData.Column.PRICE_MICROS) val priceMicros: Double,
	
	@ColumnInfo(name = DatabaseKeys.Table.SubscriptionData.Column.ORIGINAL_JSON) val originalJson: String

)