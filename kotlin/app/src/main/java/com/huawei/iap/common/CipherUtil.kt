/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.iap.common

import android.text.TextUtils
import android.util.Base64
import android.util.Log
import java.io.UnsupportedEncodingException
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec

object CipherUtil {
    
    private val TAG = "HMS_LOG_CipherUtil"
    private val SIGN_ALGORITHMS = "SHA256WithRSA"

    /**
     * To check the signature for the data returned from the interface.
     *
     * @param content Unsigned data.
     * @param sign the signature for content.
     * @param publicKey the public key of the application.
     *
     * @return boolean
     */
    fun doCheck(content: String,sign: String?, publicKey: String?): Boolean {
        if (TextUtils.isEmpty(publicKey)) {
            Log.e(TAG, "publicKey is null")
            return false
        }
        try {
            val keyFactory = KeyFactory.getInstance("RSA")
            val encodedKey = Base64.decode(publicKey, Base64.DEFAULT)
            val pubKey = keyFactory.generatePublic(X509EncodedKeySpec(encodedKey))
            val signature = Signature.getInstance(SIGN_ALGORITHMS)
            signature.initVerify(pubKey)
            signature.update(content.toByteArray(charset("utf-8")))
            return signature.verify(Base64.decode(sign, Base64.DEFAULT))
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "doCheck NoSuchAlgorithmException$e")
        } catch (e: InvalidKeySpecException) {
            Log.e(TAG, "doCheck InvalidKeySpecException$e")
        } catch (e: InvalidKeyException) {
            Log.e(TAG, "doCheck InvalidKeyException$e")
        } catch (e: SignatureException) {
            Log.e(TAG, "doCheck SignatureException$e")
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "doCheck UnsupportedEncodingException$e")
        }
        return false
    }
}