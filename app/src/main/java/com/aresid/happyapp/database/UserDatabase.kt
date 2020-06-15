package com.aresid.happyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aresid.happyapp.database.tables.accountdata.AccountData
import com.aresid.happyapp.database.tables.accountdata.AccountDataDao
import com.aresid.happyapp.database.tables.orderdata.OrderData
import com.aresid.happyapp.database.tables.orderdata.OrderDataDao
import com.aresid.happyapp.database.tables.personaldata.PersonalData
import com.aresid.happyapp.database.tables.personaldata.PersonalDataDao
import com.aresid.happyapp.database.tables.subscriptiondata.SubscriptionData
import com.aresid.happyapp.database.tables.subscriptiondata.SubscriptionDataDao
import com.aresid.happyapp.database.tables.timedata.TimeData
import com.aresid.happyapp.database.tables.timedata.TimeDataDao
import com.aresid.happyapp.keys.DatabaseKeys

/**
 *    Created on: 15.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Database(
	entities = [AccountData::class, OrderData::class, PersonalData::class, SubscriptionData::class, TimeData::class],
	version = 1,
	exportSchema = true
)
abstract class UserDatabase: RoomDatabase() {
	
	abstract fun accountDataDao(): AccountDataDao
	abstract fun orderDataDao(): OrderDataDao
	abstract fun personalDataDao(): PersonalDataDao
	abstract fun subscriptionDataDao(): SubscriptionDataDao
	abstract fun timeDataDao(): TimeDataDao
	
	/**
	 * Make the Database a singleton.
	 */
	companion object {
		
		@Volatile
		private var INSTANCE: UserDatabase? = null
		
		fun getInstance(context: Context): UserDatabase = INSTANCE ?: synchronized(this) {
			
			INSTANCE ?: Room.databaseBuilder(
				context.applicationContext,
				UserDatabase::class.java,
				DatabaseKeys.Name.DATABASE_NAME
			).fallbackToDestructiveMigration() //remote sources more reliable
				.build().also { INSTANCE = it }
			
		}
		
	}
	
}
