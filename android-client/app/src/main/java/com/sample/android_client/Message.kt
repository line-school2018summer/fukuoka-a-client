package com.sample.android_client

import java.time.LocalDateTime

data class Message(val ID: Int, val senderID: Int, val body: String, val sendTime: LocalDateTime)