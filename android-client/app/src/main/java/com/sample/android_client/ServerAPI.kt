package com.sample.android_client

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*
import java.sql.Timestamp

interface ServerAPI {
    @GET("messagestring")
    fun fetchAllMessages(@Header("Token") token: String): Observable<List<MessageReceiver>>

    @POST("message/{userId}/{roomId}/{content}")
    fun postNewMessage(@Path("userId") userId: Int,
                       @Path("roomId") roomId: Int,
                       @Path("content") content: String): Observable<Boolean>

    @POST("/user/{name}/{namedId}")
    fun postUser(@Header("Token") token: String,
                 @Path("name") name: String,
                 @Path("namedId") namedId: String): Observable<UserReceiver>

    @GET("/user/NamedId")
    fun fetchUser(@Query("NamedId") namedId: String): Single<List<UserReceiver>>
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


class UserReceiver(val id: Int,
                   val name: String,
                   @SerializedName("uid") val uId: String,
                   @SerializedName("named_id") val namedId: String) {
}