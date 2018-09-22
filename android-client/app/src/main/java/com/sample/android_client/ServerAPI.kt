package com.sample.android_client

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.sql.Timestamp

interface ServerAPI {
    @GET("message")
    fun fetchAllMessages(): Observable<List<MessageReceiver>>

    @POST("message/{userId}/{roomId}/{content}")
    fun postNewMessage(@Path("userId") userId: Int, @Path("roomId") roomId: Int, @Path("content") content: String): Observable<Boolean>

    @GET("/user/NamedId")
    fun fetchUser(@Query("NamedId") namedId: String): Single<UserReceiver>
}

class MessageReceiver(val type: String,
                      val id: Int,
                      val roomId: Int,
                      val senderId: Int,
                      val content: String,
                      @SerializedName("SendTime") val sendTime: Timestamp) {
    fun toMessage(): Message {
        return Message(id, roomId, senderId, content, sendTime)
    }
}

class UserReceiver(val id: Int,
                   val name: String,
                   val uId: Int,
                   @SerializedName("named_id") val namedId: String) {
    fun toUser(): User {
        return User(id, name, namedId, 0, false)
    }
}