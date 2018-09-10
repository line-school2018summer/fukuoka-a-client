package com.sample.android_client

data class User(val id: Int,
                val serverId: Int,
                val userId: Int,
                val name: String,
                val iconId: Int,
                val isFriend: Boolean)