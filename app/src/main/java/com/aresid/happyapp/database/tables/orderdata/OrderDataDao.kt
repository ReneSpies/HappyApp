package com.aresid.happyapp.database.tables.orderdata

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 *    Created on: 15.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Dao
interface OrderDataDao {
	
	@Query("SELECT * FROM order_data WHERE order_id = :orderId")
	fun get(orderId: Double): LiveData<OrderData>
	
	@Query("SELECT * FROM order_data")
	fun getAll(): LiveData<List<OrderData>>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(data: OrderData)
	
	@Delete
	suspend fun delete(data: OrderData)
	
}