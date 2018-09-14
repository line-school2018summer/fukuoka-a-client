package com.sample.android_client

class Room(val id: Int, val serverId: Int, val iconId: Int, val name: String, val isGroup: Boolean) {
    fun toRoomItem(): RoomItem {
        return RoomItem(id, name, iconId)
    }
}