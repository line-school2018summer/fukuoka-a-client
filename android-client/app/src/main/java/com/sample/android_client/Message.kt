package com.sample.android_client

import java.sql.Timestamp

data class Message(val serverId: Int, val roomId: Int, val userId: Int, val body: String, val postedAt: Timestamp)