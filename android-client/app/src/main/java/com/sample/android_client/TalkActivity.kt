package com.sample.android_client

import android.os.Bundle
import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log

import kotlinx.android.synthetic.main.activity_talk.*
import java.time.LocalDateTime

class TalkActivity : Activity() {

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
            input_message_box_talk.text.clear()
        }
    }

}
