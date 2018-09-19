package com.sample.android_client

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.sql.Timestamp

interface ServerAPI {
    @GET("messagestring")
    fun fetchAllMessages(@Header("Token") token: String): Observable<List<MessageReceiver>>

    @POST("message/{userId}/{roomId}/{content}")
    fun postNewMessage(@Path("userId") userId: Int, @Path("roomId") roomId: Int, @Path("content") content: String): Observable<Boolean>
}

class MessageReceiver(val type: String,
                      val id: Int,
                      val senderId: Int,
                      val roomId: Int,
                      val content: String,
                      @SerializedName("SendTime") val sendTime: Timestamp) {
    fun toMessage(): Message {
        return Message(id, roomId, senderId, content, sendTime)
    }
}