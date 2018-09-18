package com.sample.android_client

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface ServerAPI {
    @POST("message/{userId}/{roomId}/{content}")
    fun postNewMessage(@Path("userId") userId: Int, @Path("roomId") roomId: Int, @Path("content") content: String): Observable<Boolean>
}