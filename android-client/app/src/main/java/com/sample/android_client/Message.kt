package com.sample.android_client

import java.time.LocalDateTime

data class Message<T>(val ID:Int,val senderID:Int,val body:T,val sendTime:LocalDateTime)