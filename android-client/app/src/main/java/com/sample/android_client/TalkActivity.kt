package com.sample.android_client

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_talk.*
import org.jetbrains.anko.db.*
import java.sql.Timestamp
import java.text.SimpleDateFormat

class TalkActivity : Activity() {
    val Context.database: DBHelper
        get() = DBHelper.getInstance(applicationContext)

    // TODO Activity起動時に代入するように変更する
    private var roomId = 1

    private val newMessages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk)

        val messages = mutableListOf<Message>()
        talk_recycler_view.adapter = MessageRecyclerViewAdapter(messages)
        talk_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        send_button_talk.setOnClickListener {
            val message = input_message_box_talk.text.toString()
            Log.d("TalkActivity", "文字列${message}を送信する")
            // TODO: message送信処理
            // 入力ボックスを空にする
            newMessages.add(messages.last())
            input_message_box_talk.text.clear()
        }
    }

    override fun onResume() {
        super.onResume()

        newMessages.clear()
        val pastMessages = loadPastMessages()
        val recyclerViewAdapter = talk_recycler_view.adapter as MessageRecyclerViewAdapter

        recyclerViewAdapter.setMessages(pastMessages)
    }

    override fun onPause() {
        super.onPause()
        Completable.fromAction { insertNewMessages(newMessages) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun insertNewMessages(newMessages: MutableList<Message>) {
        database.use {
            this.transaction {
                newMessages.forEach {
                    this.insert(MESSAGES_TABLE_NAME,
                            "server_id" to it.serverId,
                            "room_id" to it.roomId,
                            "user_id" to it.userId,
                            "body" to it.body,
                            "posted_at" to it.postedAt.toString())

                }
            }
        }
    }

    private fun loadPastMessages(): List<Message> {
        var pastMessages = listOf<Message>()

        database.use {
            pastMessages = this.select(MESSAGES_TABLE_NAME, "server_id", "room_id", "user_id", "body", "posted_at")
                    .whereArgs("room_id = {roomId}", "roomId" to roomId)
                    .exec {
                        var parser = rowParser { serverId: Int, roomId: Int, userId: Int, body: String, postedAt: String ->
                            Message(serverId, roomId, userId, body, toTimeStamp(postedAt))
                        }
                        parseList(parser)
                    }
        }
        return pastMessages
    }

    private fun toTimeStamp(time: String): Timestamp {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        return Timestamp(dateFormat.parse(time).time)
    }
}
