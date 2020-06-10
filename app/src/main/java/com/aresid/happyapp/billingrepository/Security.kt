package com.aresid.happyapp.billingrepository

import android.text.TextUtils
import android.util.Base64
import timber.log.Timber
import java.io.IOException
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec

/**
 *    Created on: 19.05.20
 *    For Project: HappyApp
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

object Security {
	
	private val TAG = "IABUtil/Security"
	private val KEY_FACTORY_ALGORITHM = "RSA"
	private val SIGNATURE_ALGORITHM = "SHA1withRSA"
	
	val BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkN5jlLvHn7HyQHhnHeuBJjash++bGa2z+d+cL6+H9Luy5Nr9l9kYvjkoB/Hh/fJPQ5KVZN1K0w/MPa2VldWQ5FeHSYeFCbmetGeTzrxmys78WazzTj80WI6kA0CrJUy2Ijaw7jKZ4oT5QQjI0JnWqzmFh8XT79dKsPTuDx2GdO6x7QUeCs8VKJEF5SnjLGfq01MH/YxYRkKPQgkjCrlvg67+K7JFE98GvYlq+2M6/m4aTFlMFnUyCe97i41BKJpYq9SXiEK2UWYPV5+JCW3JqmnchLfnBUGD0EQe7rXJDoRRbboGCWL2otTqQIiQFOOU6/6llbyZDJham+2Yhf27gwIDAQAB"
	
	@Throws(IOException::class)
	fun verifyPurchase(
		base64PublicKey: String,
		signedData: String,
		signature: String
	): Boolean {
		
		Timber.d("verifyPurchase: called")
		
		if ((TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64PublicKey) || TextUtils.isEmpty(signature))) {
			
			Timber.w("Purchase verification failed, missing data")
			
			return false
			
		}
		
		val key = generatePublicKey(base64PublicKey)
		
		return verify(
			key,
			signedData,
			signature
		)
		
	}
	
	@Throws(IOException::class)
	private fun generatePublicKey(encodedPublicKey: String): PublicKey {
		
		Timber.d("generatePublicKey: called")
		
		try {
			
			val decodedKey = Base64.decode(
				encodedPublicKey,
				Base64.DEFAULT
			)
			
			val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
			
			return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
			
		}
		catch (e: NoSuchAlgorithmException) {
			
			Timber.e(e)
			
			throw RuntimeException(e)
			
		}
		catch (e: InvalidKeySpecException) {
			
			Timber.e(e)
			
			throw IOException(e)
			
		}
		
	}
	
	private fun verify(
		publicKey: PublicKey,
		signedData: String,
		signature: String
	): Boolean {
		
		Timber.d("verify: called")
		
		val signatureBytes: ByteArray
		
		try {
			
			signatureBytes = Base64.decode(
				signature,
				Base64.DEFAULT
			)
			
		}
		catch (e: IllegalArgumentException) {
			
			Timber.e(e)
			
			return false
			
		}
		
		try {
			
			val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
			
			signatureAlgorithm.initVerify(publicKey)
			
			signatureAlgorithm.update(signedData.toByteArray())
			
			if (!signatureAlgorithm.verify(signatureBytes)) {
				
				Timber.w("Signature verification failed")
				
				return false
				
			}
			
			return true
			
		}
		catch (e: NoSuchAlgorithmException) {
			
			Timber.e(e)
			
			throw RuntimeException(e)
			
		}
		catch (e: InvalidKeySpecException) {
			
			Timber.e(e)
			
		}
		catch (e: SignatureException) {
			
			Timber.e(e)
			
		}
		
		return false
		
	}
	
}
