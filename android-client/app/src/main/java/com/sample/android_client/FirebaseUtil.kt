package com.sample.android_client

import com.google.firebase.auth.FirebaseAuth

class FirebaseUtil {
    fun getIdToken(): String? {
        val loginUser = FirebaseAuth.getInstance().currentUser ?: return null
        var idToken: String? = null

        loginUser.getIdToken(true)
                ?.addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    idToken = it.result.token
                }

        return idToken
    }
}