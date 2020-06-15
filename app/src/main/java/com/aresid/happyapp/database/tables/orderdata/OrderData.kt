package com.aresid.happyapp.database.tables.orderdata

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

@Entity(tableName = DatabaseKeys.Table.OrderData.NAME)
data class OrderData(
	
	@PrimaryKey @ColumnInfo(name = DatabaseKeys.Table.OrderData.Column.ORDER_ID) val orderId: Double,
	
	@ColumnInfo(name = DatabaseKeys.Table.OrderData.Column.SELLER_UID) val sellerUid: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.OrderData.Column.BUYER_UID) val buyerUid: String,
	
	@ColumnInfo(name = DatabaseKeys.Table.OrderData.Column.DATE) val date: String, // TODO: 15.06.20 Check if it is better to save a Calendar object
	
	@ColumnInfo(name = DatabaseKeys.Table.OrderData.Column.PRICE) val price: Double,
	
	@ColumnInfo(name = DatabaseKeys.Table.OrderData.Column.LOCATION) val location: String

)