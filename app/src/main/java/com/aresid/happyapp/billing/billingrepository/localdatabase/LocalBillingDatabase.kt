package com.aresid.happyapp.billing.billingrepository.localdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 *    Created on: 18.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Database(
	entities = [BronzeSubscription::class, SilverSubscription::class, GoldSubscription::class, PlatinumSubscription::class, CachedPurchase::class, AugmentedSkuDetails::class],
	version = 1,
	exportSchema = true
)
abstract class LocalBillingDatabase: RoomDatabase() {
	
	abstract fun purchaseDao(): PurchaseDao
	abstract fun entitlementsDao(): EntitlementsDao
	abstract fun augmentedSkuDetailDao(): AugmentedSkuDetailDao
	
	/**
	 * Make the LocalBillingDatabase a singleton.
	 */
	companion object {
		
		@Volatile
		private var INSTANCE: LocalBillingDatabase? = null
		
		fun getInstance(context: Context): LocalBillingDatabase = INSTANCE ?: synchronized(this) {
			INSTANCE ?: Room.databaseBuilder(
				context.applicationContext,
				LocalBillingDatabase::class.java,
				"purchase_db"
			).fallbackToDestructiveMigration() //remote sources more reliable
				.build().also { INSTANCE = it }
		}
	}
	
}