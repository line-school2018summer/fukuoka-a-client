package com.sample.android_client

import android.os.Bundle
import android.app.Activity
import android.support.v7.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_talk.*
import java.time.LocalDateTime

class TalkActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk)

        val messages = mutableListOf<Message<String>>()
        messages.add(Message(1, 1, "Test1", LocalDateTime.now()))
        messages.add(Message(2, 2, "Test1", LocalDateTime.now()))

        talk_recycler_view.adapter = MessageRecyclerViewAdaper(messages)
        talk_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

}
