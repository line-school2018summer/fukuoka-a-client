package com.sample.android_client

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class FirebaseUtil {
    fun getIdToken(): String? {
        val loginUser = FirebaseAuth.getInstance().currentUser ?: return null
        var idToken: String? = null

        loginUser.getIdToken(true)
                .addOnSuccessListener {
                    Log.d("FirebaseUtil", it.token)
                    idToken = it.token
                }
                .addOnFailureListener {
                    Log.d("FirebaseUtil", it.toString())
                }

        return idToken
    }
}