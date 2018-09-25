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
    fun postNewMessage(@Path("userId") userId: Int, @Path("roomId") roomId: Int, @Path("content") content: String): Observable<Boolean>

    @GET("/user/NamedId")
    fun fetchUser(@Query("NamedId") namedId: String): Single<List<UserReceiver>>

    @POST("room/{Name}/{isGroup}/{IconURL}")
    fun postRoom(@Path("Name") name: String,
                 @Path("isGroup") isGroup: Boolean,
                 @Path("IconURL") iconURL: String = " "): Observable<RoomReceiver>

    @POST("roomInfo/{userId}/{roomId}")
    fun postRoomInfo(@Path("userId") userId: Int,
                     @Path("roomId") roomId: Int): Observable<Boolean>

    @GET("user/MyId")
    fun fetchMyId(@Header("Token") token: String): Single<Int>

    @GET("roominfo/{userId}/roomid")
    fun fetchRoomIdInclude(@Path("userId") userId: Int): Observable<List<Int>>

    @GET("roominfo/{roomId}/userid")
    fun fetchUserIdBelong(@Path("roomId") roomId: Int): Observable<List<UserReceiver>>
}

class MessageReceiver(val type: String,
                      val id: Int,
                      val senderId: Int,
                      val roomId: Int,
                      val content: String,
                      @SerializedName("CreatedAt") val sendTime: Timestamp) {
    fun toMessage(): Message {
        return Message(id, roomId, senderId, content, sendTime)
    }
}

class UserReceiver(val id: Int,
                   val name: String,
                   @SerializedName("uid") val uId: String,
                   @SerializedName("named_id") val namedId: String) {
    fun toUser(): User {
        return User(id, name, namedId, 0, false)
    }
}

data class RoomReceiver(val id: Int,
                        val name: String,
                        val iconURL: String,
                        val group: Boolean)