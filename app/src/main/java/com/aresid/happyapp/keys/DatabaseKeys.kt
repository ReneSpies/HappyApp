package com.aresid.happyapp.keys

/**
 *    Created on: 14.06.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

object DatabaseKeys {
	
	object Name {
		
		/* Following the ARES ID naming convention for Database names, PascalCase is used */
		
		const val DATABASE_NAME = "UserData"
		
	}
	
	object Table {
		
		/* Following the ARES ID naming convention for Database tables, snake_case is used */
		
		object PersonalData {
			
			const val NAME = "personal_data"
			
			object Column {
				
				/* Following the ARES ID naming convention for Database columns, snake_case is used */
				
				const val UID = "uid" // Primary key
				const val FAMILY_NAME = "family_name"
				const val FIRST_NAME = "first_name"
				const val EMAIL = "email"
				const val USERNAME = "username"
				const val DATE_OF_BIRTH = "date_of_birth"
				const val PROFILE_PICTURE = "profile_picture"
				const val DATE_OF_CREATION = "date_of_creation"
				
			}
			
		}
		
		object SubscriptionData {
			
			const val NAME = "subscription_data"
			
			object Column {
				
				/* Following the ARES ID naming convention for Database columns, snake_case is used */
				
				const val SKU = "sku" // Primary key
				const val TITLE = "title"
				const val DESCRIPTION = "description"
				const val PRICE = "price"
				const val PRICE_MICROS = "price_micros"
				const val ORIGINAL_JSON = "original_json"
				
			}
			
		}
		
		object AccountData {
			
			const val NAME = "account_data"
			
			object Column {
				
				/* Following the ARES ID naming convention for Database columns, snake_case is used */
				
				const val UID = "uid" // Primary key
				const val TYPE = "type"
				const val PURCHASES_COUNT = "purchases_count"
				const val LOCATION = "location"
				const val SUBSCRIPTION_EXPIRY_DATE = "subscription_expiry_date"
				const val SUBSCRIPTION_STATUS = "subscription_status"
				
			}
			
		}
		
		object OrderData {
			
			const val NAME = "order_data"
			
			object Column {
				
				/* Following the ARES ID naming convention for Database columns, snake_case is used */
				
				const val ORDER_ID = "order_id" // Primary key
				const val SELLER_UID = "seller_uid"
				const val BUYER_UID = "buyer_uid"
				const val DATE = "date"
				const val PRICE = "price"
				const val LOCATION = "location"
				
			}
			
		}
		
		object TimeData {
			
			const val NAME = "time_data"
			
			object Column {
				
				/* Following the ARES ID naming convention for Database columns, snake_case is used */
				
				const val UID = "uid"
				const val START_DATE = "start_date"
				const val END_DATE = "end_date"
				
			}
			
		}
		
		// TODO: 14.06.20 merge these into the SUBSCRIPTION_DATA table
		const val BRONZE_SUBSCRIPTION = "bronze_subscription"
		const val SILVER_SUBSCRIPTION = "silver_subscription"
		const val GOLD_SUBSCRIPTION = "gold_subscription"
		const val PLATINUM_SUBSCRIPTION = "platinum_subscription"
		
	}
	
}