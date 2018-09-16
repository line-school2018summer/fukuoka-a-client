package com.sample.android_client

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.GET
import java.sql.Timestamp

interface ServerAPI {
    @GET("message")
    fun fetchAllMessages(): Observable<List<MessageReceiver>>
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
