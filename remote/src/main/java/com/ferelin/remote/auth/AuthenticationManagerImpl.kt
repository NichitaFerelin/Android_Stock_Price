/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.remote.auth

import android.app.Activity
import com.ferelin.remote.RESPONSE_UNDEFINED
import com.ferelin.remote.VERIFICATION_CODE_SENT
import com.ferelin.remote.VERIFICATION_COMPLETED
import com.ferelin.remote.VERIFICATION_TOO_MANY_REQUESTS
import com.ferelin.remote.base.BaseResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationManagerImpl @Inject constructor() : AuthenticationManager {

    private val mFirebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance().apply {
            useAppLanguage()
        }
    }

    // User ID is used to complete verification
    private var mUserVerificationId: String? = null
    private var mAuthCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    override fun tryToLogIn(
        holderActivity: Activity,
        phone: String
    ) = callbackFlow<BaseResponse<Boolean>> {

        // Empty phone number causes exception
        if (phone.isEmpty()) {
            return@callbackFlow
        }

        mAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                mUserVerificationId = p0
                trySend(BaseResponse(responseCode = VERIFICATION_CODE_SENT))
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                mFirebaseAuth.signInWithCredential(p0).addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> trySend(
                            BaseResponse(responseCode = VERIFICATION_COMPLETED)
                        )
                        else -> trySend(BaseResponse(responseCode = RESPONSE_UNDEFINED))
                    }
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                when (p0) {
                    is FirebaseTooManyRequestsException -> {
                        trySend(
                            BaseResponse(
                                responseCode = VERIFICATION_TOO_MANY_REQUESTS
                            )
                        )
                    }
                    else -> trySend(BaseResponse(responseCode = RESPONSE_UNDEFINED))
                }
            }
        }

        mAuthCallbacks?.let { authCallbacks ->
            val options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setActivity(holderActivity)
                .setCallbacks(authCallbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        awaitClose()
    }

    override fun logInWithCode(code: String) {
        mUserVerificationId?.let { userVerificationId ->
            val credential = PhoneAuthProvider.getCredential(userVerificationId, code)
            mAuthCallbacks?.onVerificationCompleted(credential)
        }
    }

    override fun logOut() {
        mFirebaseAuth.signOut()
    }

    override fun provideUserId(): String? {
        return mFirebaseAuth.uid
    }

    override fun provideIsUserLogged(): Boolean {
        return mFirebaseAuth.currentUser != null
    }
}