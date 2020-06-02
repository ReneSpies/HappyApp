package com.aresid.happyapp.utils

/**
 *    Created on: 27.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

enum class LoadingStatus {
	
	INIT,
	
	IDLE,
	
	LOADING,
	
	SUCCESS,
	
	ERROR_USER_DELETED,
	
	ERROR_NO_INTERNET,
	
	ERROR_NOT_SUBSCRIBED,
	
	ERROR_CARD_DECLINED
	
}