package com.sample.android_client

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable

class FirebaseUtil {
    fun getIdToken(): Observable<String> =
            Observable.create<String> { emitter ->
                val loginUser = FirebaseAuth.getInstance().currentUser

                if (loginUser == null) {
                    emitter.onError(RuntimeException("Can't get current user."))
                    return@create
                }

                loginUser.getIdToken(true)
                        .addOnSuccessListener {
                            val idToken = it.token
                            if (idToken == null) {
                                emitter.onError(RuntimeException("idToken is null."))
                                return@addOnSuccessListener
                            }
                            emitter.onNext(idToken)
                            emitter.onComplete()
                        }
                        .addOnFailureListener {
                            emitter.onError(it)
                        }
            }
}