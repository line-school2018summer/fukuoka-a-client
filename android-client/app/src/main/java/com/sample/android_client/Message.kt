package com.sample.android_client

import java.time.LocalDateTime

data class Message(val messageID: Int, val senderID: Int, val body: String, val sendTime: LocalDateTime)